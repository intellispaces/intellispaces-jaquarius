package tech.intellispaces.jaquarius.object.reference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.intellispaces.commons.action.runnable.RunnableAction;
import tech.intellispaces.commons.action.text.StringActions;
import tech.intellispaces.commons.exception.NotImplementedExceptions;
import tech.intellispaces.commons.exception.UnexpectedException;
import tech.intellispaces.commons.exception.UnexpectedExceptions;
import tech.intellispaces.commons.text.StringFunctions;
import tech.intellispaces.commons.type.ClassFunctions;
import tech.intellispaces.commons.type.Classes;
import tech.intellispaces.commons.type.Type;
import tech.intellispaces.commons.java.reflection.JavaStatements;
import tech.intellispaces.commons.java.reflection.customtype.AnnotationFunctions;
import tech.intellispaces.commons.java.reflection.customtype.CustomType;
import tech.intellispaces.commons.java.reflection.customtype.CustomTypes;
import tech.intellispaces.commons.java.reflection.instance.AnnotationInstance;
import tech.intellispaces.commons.java.reflection.method.MethodParam;
import tech.intellispaces.commons.java.reflection.method.MethodStatement;
import tech.intellispaces.commons.java.reflection.reference.CustomTypeReference;
import tech.intellispaces.commons.java.reflection.reference.NamedReference;
import tech.intellispaces.commons.java.reflection.reference.NotPrimitiveReference;
import tech.intellispaces.commons.java.reflection.reference.ReferenceBound;
import tech.intellispaces.commons.java.reflection.reference.TypeReference;
import tech.intellispaces.commons.java.reflection.reference.WildcardReference;
import tech.intellispaces.jaquarius.Jaquarius;
import tech.intellispaces.jaquarius.annotation.Movable;
import tech.intellispaces.jaquarius.annotation.ObjectHandle;
import tech.intellispaces.jaquarius.annotation.Unmovable;
import tech.intellispaces.jaquarius.annotation.Wrapper;
import tech.intellispaces.jaquarius.naming.NameConventionFunctions;
import tech.intellispaces.jaquarius.settings.KeyDomain;
import tech.intellispaces.jaquarius.settings.KeyDomainPurposes;
import tech.intellispaces.jaquarius.space.domain.DomainFunctions;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class ObjectHandleFunctions {
  private static final Logger LOG = LoggerFactory.getLogger(ObjectHandleFunctions.class);

  public static Class<?> getObjectHandleClass(ObjectHandleTypes objectHandleType) {
    return switch (objectHandleType) {
      case UndefinedHandle, UndefinedClearObject -> tech.intellispaces.jaquarius.object.reference.ObjectHandle.class;
      case UnmovableHandle, UnmovableClearObject -> UnmovableObjectHandle.class;
      case MovableHandle, MovableClearObject -> MovableObjectHandle.class;
    };
  }

  public static boolean isObjectHandleClass(Class<?> aClass) {
    return isDefaultObjectHandleClass(aClass) || isCustomObjectHandleClass(aClass);
  }

  public static boolean isObjectType(TypeReference type) {
    if (type.isCustomTypeReference()) {
      if (type.asCustomTypeReferenceOrElseThrow().targetType().hasParent(tech.intellispaces.jaquarius.object.reference.ObjectHandle.class)) {
        return true;
      }
    }
    try {
      CustomType domainType = getDomainOfObjectHandle(type);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static boolean isObjectHandleType(TypeReference type) {
    return isDefaultObjectHandleType(type) || isCustomObjectHandleType(type);
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

  public static String getUndefinedObjectHandleTypename(TypeReference domainType) {
    return getUndefinedObjectHandleTypename(domainType, Function.identity());
  }

  public static String getUndefinedObjectHandleTypename(
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
      return "? extends " + getUndefinedObjectHandleTypename(type.asWildcardOrElseThrow().extendedBound().get(), typeReplacer);
    }
    return getUndefinedObjectHandleTypename(type.asCustomTypeReferenceOrElseThrow().targetType());
  }

  public static String getUndefinedPureObjectTypename(
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
      return "? extends " + getUndefinedPureObjectTypename(type.asWildcardOrElseThrow().extendedBound().get(), typeReplacer);
    }
    return getUndefinedPureObjectTypename(type.asCustomTypeReferenceOrElseThrow().targetType());
  }

  public static String getUndefinedPureObjectTypename(CustomType domainType) {
    if (isDefaultObjectHandleType(domainType)) {
      return domainType.canonicalName();
    }
    return NameConventionFunctions.getUndefinedPureObjectTypename(domainType.className());
  }

  public static String getUndefinedObjectHandleTypename(CustomType domainType) {
    if (isDefaultObjectHandleType(domainType)) {
      return domainType.canonicalName();
    }
    return NameConventionFunctions.getUndefinedObjectHandleTypename(domainType.className());
  }

  public static String getObjectHandleTypename(
      CustomType customType, ObjectHandleType type, boolean replaceKeyDomain
  ) {
    if (isDefaultObjectHandleType(customType)) {
      return customType.canonicalName();
    }
    if (!DomainFunctions.isDomainType(customType)) {
      return customType.canonicalName();
    }
    return NameConventionFunctions.getObjectHandleTypename(customType.className(), type, replaceKeyDomain);
  }

  public static String getObjectHandleTypename(String canonicalName, ObjectHandleType type) {
    if (isDefaultObjectHandleType(canonicalName)) {
      return canonicalName;
    }
    return NameConventionFunctions.getObjectHandleTypename(canonicalName, type, true);
  }

  public static boolean isCustomObjectHandleClass(Class<?> aClass) {
    Wrapper wrapper = aClass.getAnnotation(Wrapper.class);
    if (wrapper != null) {
      aClass = wrapper.value();
    }
    if (aClass.isAnnotationPresent(ObjectHandle.class)) {
      return true;
    }

    Optional<Class<?>> domainClass = Classes.get(
        NameConventionFunctions.getDomainNameOfPureObject(aClass.getCanonicalName())
    );
    return domainClass.isPresent();

  }

  public static boolean isCustomObjectHandleType(TypeReference type) {
    if (!type.isCustomTypeReference()) {
      return false;
    }
    CustomType customType = type.asCustomTypeReferenceOrElseThrow().targetType();
    return AnnotationFunctions.isAssignableAnnotatedType(customType, ObjectHandle.class);
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

  public static CustomType getDomainOfObjectHandle(TypeReference objectHandleType) {
    if (objectHandleType.isPrimitiveReference()) {
      Class<?> wrapperClass = ClassFunctions.wrapperClassOfPrimitive(
        objectHandleType.asPrimitiveReferenceOrElseThrow().typename()
      );
      return JavaStatements.customTypeStatement(wrapperClass);
    } else if (objectHandleType.isCustomTypeReference()) {
      return getDomainOfObjectHandle(objectHandleType.asCustomTypeReferenceOrElseThrow().targetType());
    } else {
      throw NotImplementedExceptions.withCode("yZJg8A");
    }
  }

  public static CustomType getDomainOfObjectHandle(CustomType objectHandleType) {
    if (isDefaultObjectHandleType(objectHandleType)) {
      return objectHandleType;
    }

    Optional<AnnotationInstance> wrapper = objectHandleType.selectAnnotation(Wrapper.class.getCanonicalName());
    if (wrapper.isPresent()) {
      objectHandleType = wrapper.get()
          .value().orElseThrow()
          .asClass().orElseThrow()
          .type();
    }

    Optional<AnnotationInstance> objectHandle = objectHandleType.selectAnnotation(
      ObjectHandle.class.getCanonicalName()
    );
    if (objectHandle.isPresent()) {
      return objectHandle.get()
          .value().orElseThrow()
          .asClass().orElseThrow()
          .type();
    }

    Optional<Class<?>> domainClass = Classes.get(
        NameConventionFunctions.getDomainNameOfPureObject(objectHandleType.canonicalName())
    );
    if (domainClass.isPresent()) {
      return CustomTypes.of(domainClass.get());
    }

    throw UnexpectedExceptions.withMessage("Object handle class {0} must be annotated with annotation {1}",
        objectHandleType.canonicalName(), ObjectHandle.class.getSimpleName());
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
        NameConventionFunctions.getDomainNameOfPureObject(objectHandleClass.getCanonicalName())
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
    if (isCustomObjectHandleClass(sourceObjectHandleClass) && isCustomObjectHandleClass(targetObjectHandleClass)) {
      CustomType sourceObjectHandleDomain = getDomainOfObjectHandle(CustomTypes.of(sourceObjectHandleClass));
      CustomType targetObjectHandleDomain = getDomainOfObjectHandle(CustomTypes.of(targetObjectHandleClass));
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
    String downgradeObjectHandleCanonicalName = NameConventionFunctions.getMovableDownwardObjectHandleTypename(
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

  public static String geUndefinedPureObjectDeclaration(
      TypeReference domainType, boolean replaceKeyDomain, Function<String, String> simpleNameMapping
  ) {
    return getObjectHandleDeclaration(domainType, ObjectHandleTypes.UndefinedClearObject, replaceKeyDomain, simpleNameMapping);
  }

  public static String getObjectHandleDeclaration(
      TypeReference domainType,
      ObjectHandleType handleType,
      boolean replaceKeyDomain,
      Function<String, String> simpleNameMapping
  ) {
    return getObjectHandleDeclaration(domainType, handleType, ObjectReferenceForms.Default, replaceKeyDomain, simpleNameMapping);
  }

  public static String getObjectHandleDeclaration(
      TypeReference domainType,
      ObjectHandleType handleType,
      ObjectReferenceForm form,
      boolean replaceKeyDomain,
      Function<String, String> simpleNameMapping
  ) {
    return getObjectHandleDeclaration(domainType, handleType, form, true, replaceKeyDomain, simpleNameMapping);
  }

  public static String getObjectHandleDeclaration(
      TypeReference domainType,
      ObjectHandleType handleType,
      ObjectReferenceForm form,
      boolean includeTypeParams,
      boolean replaceKeyDomain,
      Function<String, String> simpleNameMapping
  ) {
    if (domainType.isPrimitiveReference()) {
      return domainType.asPrimitiveReferenceOrElseThrow().typename();
    } else if (domainType.asNamedReference().isPresent()) {
      return domainType.asNamedReference().get().name();
    } else if (domainType.isCustomTypeReference()) {
      CustomTypeReference customTypeReference = domainType.asCustomTypeReferenceOrElseThrow();
      CustomType targetType = customTypeReference.targetType();
      if (ObjectReferenceForms.Primitive.is(form)) {
        return ClassFunctions.primitiveTypenameOfWrapper(targetType.canonicalName());
      } else if (targetType.canonicalName().equals(Class.class.getCanonicalName())) {
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
        String canonicalName = getObjectHandleTypename(targetType, handleType, replaceKeyDomain);
        String simpleName = simpleNameMapping.apply(canonicalName);
        sb.append(simpleName);
        if (includeTypeParams && !customTypeReference.typeArguments().isEmpty()) {
          sb.append("<");
          RunnableAction commaAppender = StringActions.skipFirstTimeCommaAppender(sb);
          for (NotPrimitiveReference argType : customTypeReference.typeArguments()) {
            commaAppender.run();
            sb.append(getObjectHandleDeclaration(
                argType, ObjectHandleTypes.UndefinedClearObject, ObjectReferenceForms.Default, true, replaceKeyDomain, simpleNameMapping
            ));
          }
          sb.append(">");
        }
        return sb.toString();
      }
    } else if (domainType.isWildcard()) {
      WildcardReference wildcardTypeReference = domainType.asWildcardOrElseThrow();
      if (wildcardTypeReference.extendedBound().isPresent()) {
        return getObjectHandleDeclaration(wildcardTypeReference.extendedBound().get(), handleType, replaceKeyDomain, simpleNameMapping);
      } else {
        return Object.class.getCanonicalName();
      }
    } else if (domainType.isArrayReference()) {
      TypeReference elementType = domainType.asArrayReferenceOrElseThrow().elementType();
      return getObjectHandleDeclaration(elementType, handleType, replaceKeyDomain, simpleNameMapping) + "[]";
    } else {
      throw new UnsupportedOperationException("Unsupported type - " + domainType.actualDeclaration());
    }
  }

  public static String getObjectHandleTypeParams(
      CustomType domainType,
      ObjectHandleType handleType,
      ObjectReferenceForm form,
      Function<String, String> simpleNameMapping,
      boolean pureHandle,
      boolean full
  ) {
    if (domainType.typeParameters().isEmpty()) {
      return "";
    }

    var sb = new StringBuilder();
    RunnableAction commaAppender = StringActions.skipFirstTimeCommaAppender(sb);
    sb.append("<");
    for (NamedReference namedReference : domainType.typeParameters()) {
      commaAppender.run();
      if (!full || namedReference.extendedBounds().isEmpty()) {
        sb.append(namedReference.name());
      } else {
        sb.append(namedReference.name());
        sb.append(" extends ");
        RunnableAction boundCommaAppender = StringActions.skipFirstTimeCommaAppender(sb);
        for (ReferenceBound bound : namedReference.extendedBounds()) {
          boundCommaAppender.run();
          sb.append(getObjectHandleDeclaration(bound, handleType, form, true, pureHandle, simpleNameMapping));
        }
      }
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
      KeyDomain keyDomain = Jaquarius.settings().getKeyDomainByPurpose(KeyDomainPurposes.Properties);
      String domainClassName = NameConventionFunctions.convertToDomainClassName(keyDomain.domainName());
      String handleClassName = NameConventionFunctions.getUndefinedPureObjectTypename(domainClassName);
      propertiesHandleClass = ClassFunctions.getClass(handleClassName).orElseThrow(() ->
          UnexpectedExceptions.withMessage("Could not get class {0}", handleClassName)
      );
    }
    return propertiesHandleClass;
  }

  public static void releaseSilently(ObjectReference<?> objectReference) {
    if (objectReference == null) {
      return;
    }
    try {
      objectReference.release();
    } catch (Exception e) {
      LOG.error("Could not release object reference", e);
    }
  }

  public static void releaseEach(List<ObjectReference<?>> objectReferences) {
    List<Exception> exceptions = null;
    for (var objectReference : objectReferences) {
      try {
        objectReference.release();
      } catch (Exception e) {
        if (exceptions == null) {
          exceptions = new ArrayList<>();
          exceptions.add(e);
        }
      }
    }
    if (exceptions != null) {
      UnexpectedException ue = UnexpectedExceptions.withMessage("Could not release object handles");
      exceptions.forEach(ue::addSuppressed);
      throw ue;
    }
  }

  private ObjectHandleFunctions() {}

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
