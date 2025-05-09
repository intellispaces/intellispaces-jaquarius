package tech.intellispaces.reflections.framework.object.reference;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.intellispaces.actions.runnable.RunnableAction;
import tech.intellispaces.actions.text.StringActions;
import tech.intellispaces.commons.exception.NotImplementedExceptions;
import tech.intellispaces.commons.exception.UnexpectedException;
import tech.intellispaces.commons.exception.UnexpectedExceptions;
import tech.intellispaces.commons.text.StringFunctions;
import tech.intellispaces.commons.type.ClassFunctions;
import tech.intellispaces.commons.type.Classes;
import tech.intellispaces.commons.type.Type;
import tech.intellispaces.reflections.framework.Jaquarius;
import tech.intellispaces.reflections.framework.annotation.Movable;
import tech.intellispaces.reflections.framework.annotation.ObjectHandle;
import tech.intellispaces.reflections.framework.annotation.Unmovable;
import tech.intellispaces.reflections.framework.annotation.Wrapper;
import tech.intellispaces.reflections.framework.naming.NameConventionFunctions;
import tech.intellispaces.reflections.framework.settings.DomainReference;
import tech.intellispaces.reflections.framework.settings.DomainTypes;
import tech.intellispaces.reflections.framework.space.domain.DomainFunctions;
import tech.intellispaces.jstatements.JavaStatements;
import tech.intellispaces.jstatements.customtype.AnnotationFunctions;
import tech.intellispaces.jstatements.customtype.CustomType;
import tech.intellispaces.jstatements.customtype.CustomTypes;
import tech.intellispaces.jstatements.instance.AnnotationInstance;
import tech.intellispaces.jstatements.method.MethodParam;
import tech.intellispaces.jstatements.method.MethodStatement;
import tech.intellispaces.jstatements.reference.CustomTypeReference;
import tech.intellispaces.jstatements.reference.NamedReference;
import tech.intellispaces.jstatements.reference.NotPrimitiveReference;
import tech.intellispaces.jstatements.reference.ReferenceBound;
import tech.intellispaces.jstatements.reference.TypeReference;
import tech.intellispaces.jstatements.reference.WildcardReference;

public class ObjectReferenceFunctions {
  private static final Logger LOG = LoggerFactory.getLogger(ObjectReferenceFunctions.class);

  public static boolean isObjectFormType(TypeReference type) {
    if (type.isPrimitiveReference()) {
      return true;
    }
    if (type.isCustomTypeReference()) {
      CustomType targetType = type.asCustomTypeReferenceOrElseThrow().targetType();
      if (targetType.hasParent(tech.intellispaces.reflections.framework.object.reference.ObjectHandle.class)) {
        return true;
      }
    }
    return getDomainOfObjectForm(type).isPresent();
  }

  public static boolean isObjectFormClass(Class<?> aClass) {
    Wrapper wrapper = aClass.getAnnotation(Wrapper.class);
    if (wrapper != null) {
      aClass = wrapper.value();
    }
    if (isDefaultObjectHandleType(aClass.getCanonicalName())) {
      return true;
    }
    if (aClass.isAnnotationPresent(ObjectHandle.class)) {
      return true;
    }
    if (tech.intellispaces.reflections.framework.object.reference.ObjectHandle.class.isAssignableFrom(aClass)) {
      return true;
    }
    Optional<Class<?>> domainClass = Classes.get(
        NameConventionFunctions.getDomainNameOfRegularObjectForm(aClass.getCanonicalName())
    );
    return domainClass.isPresent();
  }

  public static boolean isObjectHandleType(TypeReference type) {
    return isDefaultObjectHandleType(type) || isCustomObjectHandleType(type);
  }

  public static boolean isObjectHandleClass(Class<?> aClass) {
    return isDefaultObjectHandleClass(aClass) || isCustomObjectFormClass(aClass);
  }

  public static boolean isCustomObjectHandleType(TypeReference type) {
    if (!type.isCustomTypeReference()) {
      return false;
    }
    CustomType customType = type.asCustomTypeReferenceOrElseThrow().targetType();
    return customType.hasParent(tech.intellispaces.reflections.framework.object.reference.ObjectHandle.class);
  }

  public static boolean isCustomObjectFormClass(Class<?> aClass) {
    Wrapper wrapper = aClass.getAnnotation(Wrapper.class);
    if (wrapper != null) {
      aClass = wrapper.value();
    }
    if (aClass.isAnnotationPresent(ObjectHandle.class)) {
      return true;
    }
    if (tech.intellispaces.reflections.framework.object.reference.ObjectHandle.class.isAssignableFrom(aClass)) {
      return true;
    };
    Optional<Class<?>> domainClass = Classes.get(
        NameConventionFunctions.getDomainNameOfRegularObjectForm(aClass.getCanonicalName())
    );
    return domainClass.isPresent();
  }

  public static boolean isDefaultObjectHandleClass(Class<?> aClass) {
    return DEFAULT_OBJECT_HANDLE_CLASSES.contains(aClass.getCanonicalName());
  }

  public static boolean isDefaultObjectHandleClass(CustomType aClass) {
    return DEFAULT_OBJECT_HANDLE_CLASSES.contains(aClass.canonicalName());
  }

  public static boolean isDefaultObjectHandleType(TypeReference type) {
    return type.isPrimitiveReference() ||
        (type.isCustomTypeReference() && isDefaultObjectHandleType(type.asCustomTypeReferenceOrElseThrow().targetType()));
  }

  public static boolean isDefaultObjectHandleType(CustomType type) {
    return isDefaultObjectHandleType(type.canonicalName());
  }

  public static boolean isDefaultObjectHandleType(String canonicalName) {
    return DEFAULT_OBJECT_HANDLE_CLASSES.contains(canonicalName);
  }

  public static boolean isMovableObjectHandle(Object objectHandle) {
    return isMovableObjectHandle(objectHandle.getClass());
  }

  public static boolean isMovableObjectHandle(Class<?> objectHandleClass) {
    return isMovableObjectHandle(CustomTypes.of(objectHandleClass));
  }

  public static boolean isMovableObjectHandle(CustomType objectHandleType) {
    return AnnotationFunctions.isAssignableAnnotatedType(objectHandleType, Movable.class);
  }

  public static boolean isUnmovableObjectHandle(CustomType objectHandleType) {
    return AnnotationFunctions.isAssignableAnnotatedType(objectHandleType, Unmovable.class);
  }

  public static Class<?> getObjectHandleClass(MovabilityType movabilityType) {
    return switch (MovabilityTypes.of(movabilityType)) {
      case General -> tech.intellispaces.reflections.framework.object.reference.ObjectHandle.class;
      case Unmovable -> UnmovableObjectHandle.class;
      case Movable -> MovableObjectHandle.class;
    };
  }

  public static String getObjectFormTypename(
      ObjectReferenceForm form, TypeReference type, Function<TypeReference, TypeReference> typeReplacer
  ) {
    return switch (ObjectReferenceForms.of(form)) {
      case Regular -> getGeneralRegularObjectTypename(type, typeReplacer);
      case ObjectHandle -> getGeneralObjectHandleTypename(type, typeReplacer);
      default -> throw NotImplementedExceptions.withCode("UoXguA");
    };
  }

  public static String getGeneralObjectHandleTypename(TypeReference domainType) {
    return getGeneralObjectHandleTypename(domainType, Function.identity());
  }

  public static String getGeneralObjectHandleTypename(
      TypeReference type, Function<TypeReference, TypeReference> typeReplacer
  ) {
    type = typeReplacer.apply(type);
    if (type.isPrimitiveReference()) {
      return ClassFunctions.wrapperClassOfPrimitive(type.asPrimitiveReferenceOrElseThrow().typename()).getCanonicalName();
    } else if (type.isNamedReference()) {
      return type.asNamedReferenceOrElseThrow().name();
    } else if (type.isWildcard()) {
      if (type.asWildcardOrElseThrow().extendedBound().isEmpty()) {
        return "?";
      }
      return "? extends " + getGeneralObjectHandleTypename(type.asWildcardOrElseThrow().extendedBound().get(), typeReplacer);
    }
    return getGeneralObjectHandleTypename(type.asCustomTypeReferenceOrElseThrow().targetType());
  }

  public static String getGeneralRegularObjectTypename(
      TypeReference type, Function<TypeReference, TypeReference> typeReplacer
  ) {
    type = typeReplacer.apply(type);
    if (type.isPrimitiveReference()) {
      return ClassFunctions.wrapperClassOfPrimitive(type.asPrimitiveReferenceOrElseThrow().typename()).getCanonicalName();
    } else if (type.isNamedReference()) {
      return type.asNamedReferenceOrElseThrow().name();
    } else if (type.isWildcard()) {
      if (type.asWildcardOrElseThrow().extendedBound().isEmpty()) {
        return "?";
      }
      return "? extends " + getGeneralRegularObjectTypename(type.asWildcardOrElseThrow().extendedBound().get(), typeReplacer);
    }
    return getGeneralRegularObjectTypename(type.asCustomTypeReferenceOrElseThrow().targetType());
  }

  public static String getGeneralRegularObjectTypename(CustomType domainType) {
    if (isDefaultObjectHandleType(domainType)) {
      return domainType.canonicalName();
    }
    return NameConventionFunctions.getGeneralRegularObjectTypename(domainType.className(), false);
  }

  public static String getGeneralObjectHandleTypename(CustomType domainType) {
    if (isDefaultObjectHandleType(domainType)) {
      return domainType.canonicalName();
    }
    return NameConventionFunctions.getGeneralObjectHandleTypename(domainType.className(), false);
  }

  public static String getUnmovableObjectHandleTypename(CustomType domainType) {
    if (isDefaultObjectHandleType(domainType)) {
      return domainType.canonicalName();
    }
    return NameConventionFunctions.getUnmovableObjectHandleTypename(domainType.className());
  }

  public static String getObjectTypename(
      CustomType customType, ObjectReferenceForm objectForm, MovabilityType movabilityType, boolean replaceDomainWithDelegate
  ) {
    if (isDefaultObjectHandleType(customType)) {
      return customType.canonicalName();
    }
    if (!DomainFunctions.isDomainType(customType)) {
      return customType.canonicalName();
    }
    return NameConventionFunctions.getObjectTypename(customType.className(), objectForm, movabilityType, replaceDomainWithDelegate);
  }

  public static String getObjectTypename(String canonicalName, ObjectReferenceForm objectForm, MovabilityType movabilityType) {
    if (isDefaultObjectHandleType(canonicalName)) {
      return canonicalName;
    }
    return NameConventionFunctions.getObjectTypename(canonicalName, objectForm, movabilityType, true);
  }

  public static Class<?> getObjectHandleClass(Class<?> aClass) {
    return defineObjectHandleClassInternal(aClass);
  }

  private static Class<?> defineObjectHandleClassInternal(Class<?> aClass) {
    if (aClass.isAnnotationPresent(ObjectHandle.class) ||
        isDefaultObjectHandleClass(aClass)
    ) {
      return aClass;
    }
    if (aClass.getSuperclass() != null) {
      Class<?> result = defineObjectHandleClassInternal(aClass.getSuperclass());
      if (result != null) {
        return result;
      }
    }
    for (Class<?> anInterface : aClass.getInterfaces()) {
      Class<?> result = defineObjectHandleClassInternal(anInterface);
      if (result != null) {
        return result;
      }
    }
    return null;
  }

  public static boolean isCompatibleObjectType(Class<?> type1, Class<?> type2) {
    Class<?> actualType1 = ClassFunctions.getObjectClass(type1);
    Class<?> actualType2 = ClassFunctions.getObjectClass(type2);
    return actualType2 == actualType1 || actualType1.isAssignableFrom(actualType2);
  }

  public static Optional<CustomType> getDomainOfObjectForm(TypeReference objectFormType) {
    if (objectFormType.isPrimitiveReference()) {
      Class<?> wrapperClass = ClassFunctions.wrapperClassOfPrimitive(
        objectFormType.asPrimitiveReferenceOrElseThrow().typename()
      );
      return Optional.of(JavaStatements.customTypeStatement(wrapperClass));
    } else if (objectFormType.isCustomTypeReference()) {
      return getDomainOfObjectForm(objectFormType.asCustomTypeReferenceOrElseThrow().targetType());
    } else {
      throw NotImplementedExceptions.withCode("yZJg8A");
    }
  }

  public static Optional<CustomType> getDomainOfObjectForm(CustomType objectFormType) {
    if (isDefaultObjectHandleType(objectFormType)) {
      return Optional.of(objectFormType);
    }

    Optional<AnnotationInstance> wrapper = objectFormType.selectAnnotation(Wrapper.class.getCanonicalName());
    if (wrapper.isPresent()) {
      objectFormType = wrapper.get()
          .value().orElseThrow()
          .asClass().orElseThrow()
          .type();
    }

    Optional<AnnotationInstance> objectHandle = objectFormType.selectAnnotation(
        ObjectHandle.class.getCanonicalName()
    );
    if (objectHandle.isPresent()) {
      return Optional.of(objectHandle.get()
          .value().orElseThrow()
          .asClass().orElseThrow()
          .type());
    }

    Optional<Class<?>> domainClass = Classes.get(
        NameConventionFunctions.getDomainNameOfRegularObjectForm(objectFormType.canonicalName())
    );
    return domainClass.map(CustomTypes::of);
  }

  public static CustomType getDomainOfObjectFormOrElseThrow(CustomType objectFormType) {
    Optional<CustomType> domainType = getDomainOfObjectForm(objectFormType);
    if (domainType.isEmpty()) {
      throw UnexpectedExceptions.withMessage("Object handle class {0} must be annotated with annotation {1}",
          objectFormType.canonicalName(), ObjectHandle.class.getSimpleName());
    }
    return domainType.get();
  }

  public static Class<?> getDomainClassOfObjectHandle(Class<?> objectHandleClass) {
    if (isDefaultObjectHandleClass(objectHandleClass)) {
      return objectHandleClass;
    }
    Wrapper wrapper = objectHandleClass.getAnnotation(Wrapper.class);
    if (wrapper != null) {
      objectHandleClass = wrapper.value();
    }

    ObjectHandle objectHandle = objectHandleClass.getAnnotation(ObjectHandle.class);
    if (objectHandle != null) {
      return objectHandle.value();
    }

    Optional<Class<?>> domainClass = Classes.get(
        NameConventionFunctions.getDomainNameOfRegularObjectForm(objectHandleClass.getCanonicalName())
    );
    if (domainClass.isPresent()) {
      return domainClass.get();
    }

    throw UnexpectedExceptions.withMessage("Object handle class {0} must be annotated with annotation {1}",
        objectHandleClass.getCanonicalName(), ObjectHandle.class.getSimpleName());
  }

  @SuppressWarnings("unchecked")
  public static <T> T tryDowngrade(Object sourceObjectHandle, Class<T> targetObjectHandleClass) {
    Class<?> sourceObjectHandleClass = sourceObjectHandle.getClass();
    if (isCustomObjectFormClass(sourceObjectHandleClass) && isCustomObjectFormClass(targetObjectHandleClass)) {
      CustomType sourceObjectHandleDomain = getDomainOfObjectFormOrElseThrow(CustomTypes.of(sourceObjectHandleClass));
      CustomType targetObjectHandleDomain = getDomainOfObjectFormOrElseThrow(CustomTypes.of(targetObjectHandleClass));
      if (sourceObjectHandleDomain.hasParent(targetObjectHandleDomain)) {
        if (isMovableObjectHandle(targetObjectHandleClass)) {
          return (T) tryCreateDowngradeObjectHandle(sourceObjectHandle, sourceObjectHandleDomain, targetObjectHandleDomain);
        }
      }
    }
    return null;
  }

  private static Object tryCreateDowngradeObjectHandle(
      Object sourceObjectHandle, CustomType sourceObjectHandleDomain, CustomType targetObjectHandleDomain
  ) {
    String downgradeObjectHandleCanonicalName = NameConventionFunctions.getMovableDownwardObjectTypename(
        sourceObjectHandleDomain, targetObjectHandleDomain);
    Optional<Class<?>> downgradeObjectHandleClass = ClassFunctions.getClass(downgradeObjectHandleCanonicalName);
    if (downgradeObjectHandleClass.isPresent()) {
      try {
        Constructor<?> constructor = downgradeObjectHandleClass.get().getConstructors()[0];
        return constructor.newInstance(sourceObjectHandle);
      } catch (Exception e) {
        throw UnexpectedExceptions.withCauseAndMessage(e, "Could not create downgrade object handle");
      }
    }
    return null;
  }

  public static String geGeneralRegularObjectDeclaration(
      TypeReference domainType, boolean replaceDomainWithDelegate, Function<String, String> simpleNameMapping
  ) {
    return getObjectFormDeclaration(domainType, ObjectReferenceForms.Regular, MovabilityTypes.General, replaceDomainWithDelegate, simpleNameMapping);
  }

  public static String getObjectFormDeclaration(
      TypeReference domainType,
      ObjectReferenceForm objectForm,
      MovabilityType movabilityType,
      boolean replaceDomainWithDelegate,
      Function<String, String> simpleNameMapping
  ) {
    return getObjectFormDeclaration(domainType, objectForm, movabilityType, true, replaceDomainWithDelegate, simpleNameMapping);
  }

  public static String getObjectFormDeclaration(
      TypeReference domainType,
      ObjectReferenceForm objectForm,
      MovabilityType movabilityType,
      boolean includeTypeParams,
      boolean replaceDomainWithDelegate,
      Function<String, String> simpleNameMapping
  ) {
    if (domainType.isPrimitiveReference()) {
      return domainType.asPrimitiveReferenceOrElseThrow().typename();
    } else if (domainType.asNamedReference().isPresent()) {
      return domainType.asNamedReference().get().name();
    } else if (domainType.isCustomTypeReference()) {
      CustomTypeReference customTypeReference = domainType.asCustomTypeReferenceOrElseThrow();
      CustomType targetType = customTypeReference.targetType();
      if (targetType.canonicalName().equals(Class.class.getCanonicalName())) {
        var sb = new StringBuilder();
        sb.append(Class.class.getSimpleName());
        if (includeTypeParams && !customTypeReference.typeArguments().isEmpty()) {
          sb.append("<");
          RunnableAction commaAppender = StringActions.skipFirstTimeCommaAppender(sb);
          for (NotPrimitiveReference argType : customTypeReference.typeArguments()) {
            commaAppender.run();
            sb.append(argType.actualDeclaration());
          }
          sb.append(">");
        }
        return sb.toString();
      } else if (ClassFunctions.isLanguageClass(targetType.canonicalName())) {
        return targetType.simpleName();
      } else {
        var sb = new StringBuilder();
        String canonicalName = getObjectTypename(targetType, objectForm, movabilityType, replaceDomainWithDelegate);
        String simpleName = simpleNameMapping.apply(canonicalName);
        sb.append(simpleName);
        if (includeTypeParams && !customTypeReference.typeArguments().isEmpty()) {
          sb.append("<");
          RunnableAction commaAppender = StringActions.skipFirstTimeCommaAppender(sb);
          for (NotPrimitiveReference argType : customTypeReference.typeArguments()) {
            commaAppender.run();
            sb.append(getObjectFormDeclaration(
                argType, ObjectReferenceForms.Regular, MovabilityTypes.General, true, replaceDomainWithDelegate, simpleNameMapping
            ));
          }
          sb.append(">");
        }
        return sb.toString();
      }
    } else if (domainType.isWildcard()) {
      WildcardReference wildcardTypeReference = domainType.asWildcardOrElseThrow();
      if (wildcardTypeReference.extendedBound().isPresent()) {
        return getObjectFormDeclaration(wildcardTypeReference.extendedBound().get(), objectForm, movabilityType, replaceDomainWithDelegate, simpleNameMapping);
      } else {
        return Object.class.getCanonicalName();
      }
    } else if (domainType.isArrayReference()) {
      TypeReference elementType = domainType.asArrayReferenceOrElseThrow().elementType();
      return getObjectFormDeclaration(elementType, objectForm, movabilityType, replaceDomainWithDelegate, simpleNameMapping) + "[]";
    } else {
      throw new UnsupportedOperationException("Unsupported type - " + domainType.actualDeclaration());
    }
  }

  public static List<String> getObjectFormTypeParamDeclarations(
      CustomType domainType,
      ObjectReferenceForm objectForm,
      MovabilityType movabilityType,
      Function<String, String> simpleNameMapping,
      boolean replaceDomainWithDelegate,
      boolean full
  ) {
    if (domainType.typeParameters().isEmpty()) {
      return List.of();
    }

    var params = new ArrayList<String>();
    for (NamedReference namedReference : domainType.typeParameters()) {
      if (!full || namedReference.extendedBounds().isEmpty()) {
        params.add(namedReference.name());
      } else {
        var sb = new StringBuilder();
        sb.append(namedReference.name());
        sb.append(" extends ");
        RunnableAction boundCommaAppender = StringActions.skipFirstTimeCommaAppender(sb);
        for (ReferenceBound bound : namedReference.extendedBounds()) {
          boundCommaAppender.run();
          sb.append(getObjectFormDeclaration(bound, objectForm, movabilityType, true, replaceDomainWithDelegate, simpleNameMapping));
        }
        params.add(sb.toString());
      }
    }
    return params;
  }

  public static String getObjectFormTypeParamDeclaration(
      CustomType domainType,
      ObjectReferenceForm objectForm,
      MovabilityType movabilityType,
      Function<String, String> simpleNameMapping,
      boolean replaceDomainWithDelegate,
      boolean full
  ) {
    List<String> params = getObjectFormTypeParamDeclarations(
        domainType, objectForm, movabilityType, simpleNameMapping, replaceDomainWithDelegate, full
    );
    if (params.isEmpty()) {
      return "";
    }

    var sb = new StringBuilder();
    sb.append("<");
    RunnableAction commaAppender = StringActions.skipFirstTimeCommaAppender(sb);
    for (String param : params) {
      commaAppender.run();
      sb.append(param);
    }
    sb.append(">");
    return sb.toString();
  }

  public static String buildObjectHandleGuideMethodName(MethodStatement method) {
    var sb = new StringBuilder();
    sb.append("$");
    sb.append(method.name());
    if (!method.params().isEmpty()) {
      sb.append("With");
    }
    RunnableAction andAppender = StringActions.skipFirstTimeSeparatorAppender(sb, "And");
    for (MethodParam param : method.params()) {
      andAppender.run();
      sb.append(StringFunctions.removeTailIfPresent(StringFunctions.capitalizeFirstLetter(param.type().simpleDeclaration()), "Handle"));
    }
    sb.append("Guide");
    return sb.toString();
  }

  public static Class<?> propertiesHandleClass() {
    if (propertiesHandleClass == null) {
      DomainReference domainReference = Jaquarius.ontologyReference().getDomainByType(DomainTypes.PropertiesSet);
      String domainClassName = NameConventionFunctions.convertToDomainClassName(domainReference.domainName());
      String handleClassName = NameConventionFunctions.getGeneralRegularObjectTypename(domainClassName, false);
      propertiesHandleClass = ClassFunctions.getClass(handleClassName).orElseThrow(() ->
          UnexpectedExceptions.withMessage("Could not get class {0}", handleClassName)
      );
    }
    return propertiesHandleClass;
  }

  public static void unbindSilently(Object objectReference) {
    unbindSilently(ObjectHandles.handle(objectReference));
  }

  public static void unbindSilently(ObjectReference<?> objectReference) {
    if (objectReference == null) {
      return;
    }
    try {
      objectReference.unbind();
    } catch (Exception e) {
      LOG.error("Could not unbind object reference", e);
    }
  }

  public static void unbindEach(List<ObjectReference<?>> objectReferences) {
    List<Exception> exceptions = null;
    for (var objectReference : objectReferences) {
      try {
        objectReference.unbind();
      } catch (Exception e) {
        if (exceptions == null) {
          exceptions = new ArrayList<>();
          exceptions.add(e);
        }
      }
    }
    if (exceptions != null) {
      UnexpectedException ue = UnexpectedExceptions.withMessage("Could not unbind object handles");
      exceptions.forEach(ue::addSuppressed);
      throw ue;
    }
  }

  private ObjectReferenceFunctions() {}

  private final static Set<String> DEFAULT_OBJECT_HANDLE_CLASSES = Set.of(
      boolean.class.getCanonicalName(),
      byte.class.getCanonicalName(),
      short.class.getCanonicalName(),
      int.class.getCanonicalName(),
      long.class.getCanonicalName(),
      float.class.getCanonicalName(),
      double.class.getCanonicalName(),
      char.class.getCanonicalName(),
      Object.class.getCanonicalName(),
      Boolean.class.getCanonicalName(),
      Number.class.getCanonicalName(),
      Byte.class.getCanonicalName(),
      Short.class.getCanonicalName(),
      Integer.class.getCanonicalName(),
      Long.class.getCanonicalName(),
      Float.class.getCanonicalName(),
      Double.class.getCanonicalName(),
      Character.class.getCanonicalName(),
      String.class.getCanonicalName(),
      Class.class.getCanonicalName(),
      Type.class.getCanonicalName(),
      Void.class.getCanonicalName()
  );

  private static Class<?> propertiesHandleClass;
}
