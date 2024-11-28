package tech.intellispaces.jaquarius.validation;

import tech.intellispaces.jaquarius.annotation.Channel;
import tech.intellispaces.jaquarius.annotation.Domain;
import tech.intellispaces.jaquarius.annotation.Ontology;
import tech.intellispaces.jaquarius.common.NameConventionFunctions;
import tech.intellispaces.jaquarius.exception.JaquariusExceptions;
import tech.intellispaces.jaquarius.space.domain.DomainFunctions;
import tech.intellispaces.entity.text.StringFunctions;
import tech.intellispaces.entity.type.ClassFunctions;
import tech.intellispaces.java.annotation.validator.AnnotatedTypeValidator;
import tech.intellispaces.java.reflection.customtype.CustomType;
import tech.intellispaces.java.reflection.method.MethodParam;
import tech.intellispaces.java.reflection.method.MethodStatement;
import tech.intellispaces.java.reflection.reference.CustomTypeReference;
import tech.intellispaces.java.reflection.reference.NotPrimitiveReference;
import tech.intellispaces.java.reflection.reference.ReferenceBound;
import tech.intellispaces.java.reflection.reference.TypeReference;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

/**
 * Domain type validator.
 */
public class DomainValidator implements AnnotatedTypeValidator {

  @Override
  public void validate(CustomType domainType) {
    validateName(domainType);
    validateEnclosingType(domainType);
    validateParentDomains(domainType);
    validateMethods(domainType);
  }

  private void validateName(CustomType domainType) {
    if (!domainType.simpleName().endsWith("Domain")) {
      throw JaquariusExceptions.withMessage("Domain interface name must end with 'Domain'. Check class {0}\"",
          domainType.canonicalName());
    }
  }

  private void validateEnclosingType(CustomType domainType) {
    Optional<CustomType> enclosingType = domainType.enclosingType();
    if (enclosingType.isPresent() && !enclosingType.get().hasAnnotation(Ontology.class)) {
      throw JaquariusExceptions.withMessage("Domain interface can only be nested to ontology interface. " +
          "Check class {0}", domainType.canonicalName());
    }
  }

  private void validateParentDomains(CustomType domainType) {
    boolean isAlias = false;
    for (CustomTypeReference parentDomain : domainType.parentTypes()) {
      if (isAlias) {
        throw JaquariusExceptions.withMessage("Alias domain must have a single base domain. Check class {0}",
            domainType.canonicalName());
      }
      if (isAliasDomain(parentDomain)) {
        isAlias = true;
        if (domainType.declaredMethod(NameConventionFunctions.getConversionMethodName(parentDomain), List.of()).isPresent()) {
          throw JaquariusExceptions.withMessage("Alias domain could not contain channel to base domain. See domain {0}",
              domainType.canonicalName());
        }
      } else {
        if (domainType.declaredMethod(NameConventionFunctions.getConversionMethodName(parentDomain), List.of()).isEmpty()) {
          throw JaquariusExceptions.withMessage("Could not find conversion channel to domain {0}. See domain {1}",
              parentDomain.targetType().canonicalName(), domainType.canonicalName());
        }
      }
      validateParentDomain(parentDomain, domainType);
    }
  }

  private boolean isAliasDomain(CustomTypeReference baseDomain) {
    for (NotPrimitiveReference typeArgument : baseDomain.typeArguments()) {
      if (typeArgument.isCustomTypeReference()) {
        CustomType targetType = typeArgument.asCustomTypeReferenceOrElseThrow().targetType();
        return targetType.isFinal();
      }
      if (typeArgument.isNamedReference()) {
        List<ReferenceBound> extendedBounds = typeArgument.asNamedReferenceOrElseThrow().extendedBounds();
        if (!extendedBounds.isEmpty()) {
          return true;
        }
      }
    }
    return false;
  }

  private void validateParentDomain(CustomTypeReference baseDomain, CustomType domainType) {
    for (NotPrimitiveReference typeArgument : baseDomain.typeArguments()) {
      if (typeArgument.isCustomTypeReference()) {
        CustomType argumentType = typeArgument.asCustomTypeReferenceOrElseThrow().targetType();
        if (!argumentType.isFinal()) {
          throw JaquariusExceptions.withMessage("The value of a base domain type variable should be " +
              "a non-final class. Check domain {0}. Base domain {1} has type argument {2}",
              domainType.canonicalName(), baseDomain.targetType().simpleName(), argumentType.simpleName());
        }
      }
    }
  }

  private void validateMethods(CustomType domainType) {
    domainType.declaredMethods().forEach(method -> validateMethod(method, domainType));
  }

  private void validateMethod(MethodStatement method, CustomType domainType) {
    if (!method.isPublic()) {
      throw JaquariusExceptions.withMessage("Domain class could not contain private methods. " +
          "Check method '{0}' in class {1}", method.name(), domainType.canonicalName());
    }
    if (!method.hasAnnotation(Channel.class)) {
      throw JaquariusExceptions.withMessage("Domain class methods should be marked with annotation @{0}. " +
              "Check method '{1}' in class {2}",
          Channel.class.getSimpleName(), method.name(), domainType.canonicalName());
    }
    validateMethodName(method, domainType);
    validateMethodReturnType(method, domainType);
    validateMethodParameters(method, domainType);
    if (NameConventionFunctions.isConversionMethod(method)) {
      validateConversionMethod(method, domainType);
    }
  }

  private void validateMethodName(MethodStatement method, CustomType domainType) {
    String name = method.selectAnnotation(Channel.class).orElseThrow().name();
    if (StringFunctions.isNotBlank(name) && !name.endsWith("Channel")) {
      throw JaquariusExceptions.withMessage("Channel method name must end with 'Channel'. " +
              "Check method {0} in class {1}",  method.name(), domainType.canonicalName());
    }
  }

  private void validateMethodReturnType(MethodStatement method, CustomType domainType) {
    Optional<TypeReference> returnType = method.returnType();
    if (returnType.isEmpty()) {
      throw JaquariusExceptions.withMessage("Domain methods must return a value. " +
          "Check method '{0}' in class 12}", method.name(), domainType.canonicalName());
    }
    if (returnType.get().isPrimitiveReference()) {
      throw JaquariusExceptions.withMessage("Domain method cannot return a primitive value. " +
          "Check method '{0}' in class {1}", method.name(), domainType.canonicalName());
    }
    if (returnType.get().isArrayReference()) {
      throw JaquariusExceptions.withMessage("Domain methods cannot return an array. " +
          "Check method '{0}' in class {1}", method.name(), domainType.canonicalName());
    }
    if (returnType.get().isCustomTypeReference()) {
      CustomTypeReference customTypeReference = returnType.get().asCustomTypeReferenceOrElseThrow();
      CustomType customType = customTypeReference.targetType();
      if (UNACCEPTABLE_TYPES.contains(customType.canonicalName())) {
        throw JaquariusExceptions.withMessage("Domain method returns unacceptable type. " +
            "Check method '{0}' in class {1}", method.name(), domainType.canonicalName());
      }
    }
  }

  private static void validateConversionMethod(MethodStatement method, CustomType domainType) {
    CustomTypeReference returnType = method.returnType().orElseThrow().asCustomTypeReferenceOrElseThrow();
    if (DomainFunctions.isAliasOf(returnType, domainType)) {
      throw JaquariusExceptions.withMessage("Domain conversion method should not return equivalent domain. " +
          "Check method '{0}' in class {1}", method.name(), domainType.canonicalName());
    }

    if (returnType.typeArguments().isEmpty()) {
      String expectedMethodName = NameConventionFunctions.getConversionMethodName(returnType);
      if (!method.name().equals(expectedMethodName)) {
        throw JaquariusExceptions.withMessage("Invalid name of the domain conversion method. " +
            "Check method '{0}' in class {1}. Expected name '{2}'",
            method.name(), domainType.canonicalName(), expectedMethodName);
      }
    }
  }

  private void validateMethodParameters(MethodStatement method, CustomType domainType) {
    method.params().forEach(p -> validateMethodParameter(p, method, domainType));
  }

  private void validateMethodParameter(MethodParam param, MethodStatement method, CustomType domainType) {
    if (param.type().isArrayReference()) {
      throw JaquariusExceptions.withMessage("It is not allowed to use array type in channel qualifiers. " +
          "Check method '{0}' in class {1}", method.name(), domainType.canonicalName());
    }
    if (param.type().isCustomTypeReference()) {
      CustomTypeReference customTypeReference = param.type().asCustomTypeReferenceOrElseThrow();
      if (ClassFunctions.isPrimitiveWrapperClass(customTypeReference.targetType().canonicalName())) {
        throw JaquariusExceptions.withMessage("It is not allowed to use primitive wrapper type in channel qualifiers. " +
            "Check parameter '{0}' in method '{1}' in class {2}",
            param.name(), method.name(), domainType.canonicalName());
      }
      CustomType customType = customTypeReference.targetType();
      if (UNACCEPTABLE_TYPES.contains(customType.canonicalName())) {
        throw JaquariusExceptions.withMessage("Channel method qualifier has unacceptable type. " +
            "Check parameter '{0}' in method '{1}' in class {2}",
            param.name(), method.name(), domainType.canonicalName());
      }
      if (customType.hasAnnotation(Domain.class) && DomainFunctions.hasEquivalentDomains(customType)) {
        throw JaquariusExceptions.withMessage("It is not allowed to use domain alias in channel method qualifier. " +
                "Check parameter '{0}' in method '{1}' in class {2}",
            param.name(), method.name(), domainType.canonicalName());
      }
    }
  }

  private static final List<String> UNACCEPTABLE_TYPES = List.of(
      Optional.class.getCanonicalName(),
      Future.class.getCanonicalName()
  );
}