package tech.intellispaces.jaquarius.naming;

import tech.intellispaces.commons.exception.NotImplementedExceptions;
import tech.intellispaces.commons.exception.UnexpectedExceptions;
import tech.intellispaces.commons.text.StringFunctions;
import tech.intellispaces.commons.type.ClassNameFunctions;
import tech.intellispaces.jaquarius.ArtifactType;
import tech.intellispaces.jaquarius.Jaquarius;
import tech.intellispaces.jaquarius.annotation.Channel;
import tech.intellispaces.jaquarius.annotation.Domain;
import tech.intellispaces.jaquarius.annotation.ObjectHandle;
import tech.intellispaces.jaquarius.annotation.Ontology;
import tech.intellispaces.jaquarius.artifact.ArtifactTypes;
import tech.intellispaces.jaquarius.object.reference.MovabilityType;
import tech.intellispaces.jaquarius.object.reference.MovabilityTypes;
import tech.intellispaces.jaquarius.object.reference.ObjectReferenceForm;
import tech.intellispaces.jaquarius.object.reference.ObjectReferenceForms;
import tech.intellispaces.jaquarius.settings.KeyDomain;
import tech.intellispaces.jaquarius.settings.KeyDomainPurposes;
import tech.intellispaces.jaquarius.space.channel.ChannelFunctions;
import tech.intellispaces.reflection.customtype.CustomType;
import tech.intellispaces.reflection.method.MethodParam;
import tech.intellispaces.reflection.method.MethodStatement;
import tech.intellispaces.reflection.reference.CustomTypeReference;

import java.util.Optional;

public interface NameConventionFunctions {

  static boolean isDomainName(String canonicalName) {
    return canonicalName.endsWith(DOMAIN);
  }

  static String convertToDomainClassName(String domainName) {
    return domainName + DOMAIN;
  }

  static String convertToDomainName(String domainClassName) {
    return StringFunctions.removeTailIfPresent(domainClassName, DOMAIN);
  }

  static String convertToChannelClassName(String channelName) {
    return channelName + CHANNEL;
  }

  static String getDomainNameOfRegularObjectForm(String objectCanonicalName) {
    String simpleName = ClassNameFunctions.getSimpleName(objectCanonicalName);
    simpleName = StringFunctions.removeHeadIfPresent(simpleName, UNMOVABLE);
    simpleName = StringFunctions.removeHeadIfPresent(simpleName, MOVABLE);
    simpleName = simpleName + DOMAIN;
    return ClassNameFunctions.replaceSimpleName(objectCanonicalName, simpleName);
  }

  static String getObjectTypename(
      String domainClassName, ObjectReferenceForm objectForm, MovabilityType movabilityType, boolean replaceKeyDomain
  ) {
    return switch (ObjectReferenceForms.of(objectForm)) {
      case Regular -> switch (MovabilityTypes.of(movabilityType)) {
        case General -> getGeneralRegularObjectTypename(domainClassName);
        case Movable -> getMovableRegularObjectTypename(domainClassName, replaceKeyDomain);
        case Unmovable -> getUnmovableRegularObjectTypename(domainClassName, replaceKeyDomain);
      };
      case ObjectHandle -> switch (MovabilityTypes.of(movabilityType)) {
        case General -> getGeneralObjectHandleTypename(domainClassName);
        case Movable -> getMovableObjectHandleTypename(domainClassName, replaceKeyDomain);
        case Unmovable -> getUnmovableObjectHandleTypename(domainClassName, replaceKeyDomain);
      };
      case Primitive -> throw NotImplementedExceptions.withCode("LXc75Q");
      case PrimitiveWrapper -> throw NotImplementedExceptions.withCode("XGDuuQ");
    };
  }

  static String getGeneralRegularObjectTypename(String domainClassName) {
    return StringFunctions.removeTailOrElseThrow(transformClassName(domainClassName), DOMAIN);
  }

  static String getGeneralObjectHandleTypename(String domainClassName) {
    return StringFunctions.replaceTailOrElseThrow(transformClassName(domainClassName), DOMAIN, HANDLE);
  }

  static String getUnmovableObjectHandleTypename(String domainClassName) {
    return ClassNameFunctions.addPrefixToSimpleName(UNMOVABLE,
        StringFunctions.replaceTailOrElseThrow(transformClassName(domainClassName), DOMAIN, HANDLE));
  }

  static String getUnmovableRegularObjectTypename(String domainClassName, boolean replaceKeyDomain) {
    if (replaceKeyDomain) {
      KeyDomain keyDomain = Jaquarius.settings().getKeyDomainByName(convertToDomainName(domainClassName));
      if (keyDomain != null && keyDomain.delegateClassName() != null && (
          KeyDomainPurposes.Number.is(keyDomain.purpose()) ||
              KeyDomainPurposes.Short.is(keyDomain.purpose()) ||
              KeyDomainPurposes.Integer.is(keyDomain.purpose()) ||
              KeyDomainPurposes.Float.is(keyDomain.purpose()) ||
              KeyDomainPurposes.Double.is(keyDomain.purpose())
      )) {
        return keyDomain.delegateClassName();
      }
    }
    return ClassNameFunctions.addPrefixToSimpleName(UNMOVABLE,
        StringFunctions.removeTailOrElseThrow(transformClassName(domainClassName), DOMAIN));
  }

  static String getMovableRegularObjectTypename(String domainClassName, boolean replaceKeyDomain) {
    if (replaceKeyDomain) {
      KeyDomain keyDomain = Jaquarius.settings().getKeyDomainByName(convertToDomainName(domainClassName));
      if (keyDomain != null && keyDomain.delegateClassName() != null && (
          KeyDomainPurposes.Number.is(keyDomain.purpose()) ||
              KeyDomainPurposes.Short.is(keyDomain.purpose()) ||
              KeyDomainPurposes.Integer.is(keyDomain.purpose()) ||
              KeyDomainPurposes.Float.is(keyDomain.purpose()) ||
              KeyDomainPurposes.Double.is(keyDomain.purpose())
      )) {
        return keyDomain.delegateClassName();
      }
    }
    return ClassNameFunctions.addPrefixToSimpleName(MOVABLE,
        StringFunctions.removeTailOrElseThrow(transformClassName(domainClassName), DOMAIN));
  }

  static String getUnmovableObjectHandleTypename(String domainClassName, boolean replaceKeyDomain) {
    if (replaceKeyDomain) {
      KeyDomain keyDomain = Jaquarius.settings().getKeyDomainByName(convertToDomainName(domainClassName));
      if (keyDomain != null && keyDomain.delegateClassName() != null && (
          KeyDomainPurposes.Number.is(keyDomain.purpose()) ||
              KeyDomainPurposes.Short.is(keyDomain.purpose()) ||
              KeyDomainPurposes.Integer.is(keyDomain.purpose()) ||
              KeyDomainPurposes.Float.is(keyDomain.purpose()) ||
              KeyDomainPurposes.Double.is(keyDomain.purpose())
      )) {
        return keyDomain.delegateClassName();
      }
    }
    return ClassNameFunctions.addPrefixToSimpleName(UNMOVABLE, getGeneralObjectHandleTypename(domainClassName));
  }

  static String getMovableObjectHandleTypename(String domainClassName, boolean replaceKeyDomain) {
    if (replaceKeyDomain) {
      KeyDomain keyDomain = Jaquarius.settings().getKeyDomainByName(convertToDomainName(domainClassName));
      if (keyDomain != null && keyDomain.delegateClassName() != null && (
          KeyDomainPurposes.Number.is(keyDomain.purpose()) ||
            KeyDomainPurposes.Short.is(keyDomain.purpose()) ||
            KeyDomainPurposes.Integer.is(keyDomain.purpose()) ||
            KeyDomainPurposes.Float.is(keyDomain.purpose()) ||
            KeyDomainPurposes.Double.is(keyDomain.purpose())
      )) {
        return keyDomain.delegateClassName();
      }
    }
    return ClassNameFunctions.addPrefixToSimpleName(MOVABLE, getGeneralObjectHandleTypename(domainClassName));
  }

  static String getObjectHandleWrapperCanonicalName(CustomType objectHandleType) {
    Optional<ObjectHandle> oha = objectHandleType.selectAnnotation(ObjectHandle.class);
    if (oha.isPresent() && StringFunctions.isNotBlank(oha.get().name())) {
      return ClassNameFunctions.replaceSimpleName(objectHandleType.canonicalName(), oha.get().name());
    }
    return objectHandleType.canonicalName() + WRAPPER;
  }

  static String getObjectHandleWrapperCanonicalName(Class<?> objectHandleClass) {
    ObjectHandle oha = objectHandleClass.getAnnotation(ObjectHandle.class);
    if (oha != null && StringFunctions.isNotBlank(oha.name())) {
      return ClassNameFunctions.replaceSimpleName(objectHandleClass.getCanonicalName(), oha.name());
    }
    return objectHandleClass.getCanonicalName() + WRAPPER;
  }

  static String getUnitWrapperCanonicalName(String unitClassName) {
    return transformClassName(unitClassName) + WRAPPER;
  }

  static String getAutoGuiderCanonicalName(String guideClassName) {
    return StringFunctions.replaceSingleOrElseThrow(transformClassName(guideClassName), GUIDE, AUTO_GUIDE);
  }

  static String getUnmovableDatasetClassName(String domainClassName) {
    return ClassNameFunctions.addPrefixToSimpleName(UNMOVABLE,
      StringFunctions.replaceTailOrElseThrow(transformClassName(domainClassName), DOMAIN, DATASET));
  }

  static String getDatasetBuilderCanonicalName(String domainClassName) {
    return StringFunctions.replaceTailOrElseThrow(transformClassName(domainClassName), DOMAIN, BUILDER);
  }

  static String getChannelClassCanonicalName(MethodStatement channelMethod) {
    String spaceName = channelMethod.owner().packageName();
    CustomType owner = channelMethod.owner();
    if (!owner.hasAnnotation(Domain.class) && !owner.hasAnnotation(Ontology.class)) {
      throw UnexpectedExceptions.withMessage("Channel method {0} should be declared " +
              "in domain or ontology class. But actual class {1} is not marked with annotation",
          channelMethod.name(), owner.canonicalName()
      );
    }
    return getChannelClassCanonicalName(spaceName, owner, channelMethod);
  }

  static String getChannelClassCanonicalName(
      String spaceName, CustomType domain, MethodStatement channelMethod
  ) {
    String channelSimpleName = channelMethod.selectAnnotation(Channel.class).orElseThrow().name();
    if (!channelSimpleName.isBlank()) {
      return ClassNameFunctions.joinPackageAndSimpleName(spaceName, channelSimpleName);
    }
    return assignChannelClassCanonicalName(spaceName, domain, channelMethod);
  }

  static String getObjectFactoryWrapperClassName(String objectFactoryClassName) {
    return transformClassName(objectFactoryClassName) + WRAPPER;
  }

  static String getObjectFactoriesResourceName() {
    return "META-INF/jaquarius/object_factories";
  }

  static String getUnmovableUpwardObjectTypename(CustomType domainType, CustomType baseDomainType) {
    String packageName = ClassNameFunctions.getPackageName(domainType.canonicalName());
    String simpleName = StringFunctions.removeTailOrElseThrow(domainType.simpleName(), DOMAIN) +
        "BasedOn" +
        StringFunctions.capitalizeFirstLetter(StringFunctions.removeTailOrElseThrow(baseDomainType.simpleName(), DOMAIN));
    return ClassNameFunctions.joinPackageAndSimpleName(packageName, simpleName);
  }

  static String getDownwardObjectHandleTypename(
      CustomType domainType, CustomType baseDomainType, MovabilityType movabilityType
  ) {
    return switch (MovabilityTypes.of(movabilityType)) {
      case General -> getGeneralDownwardObjectTypename(domainType, baseDomainType);
      case Unmovable -> getUnmovableDownwardObjectTypename(domainType, baseDomainType);
      case Movable -> getMovableDownwardObjectTypename(domainType, baseDomainType);
    };
  }

  static String getGeneralDownwardObjectTypename(CustomType domainType, CustomType baseDomainType) {
    String packageName = ClassNameFunctions.getPackageName(domainType.canonicalName());
    String simpleName = StringFunctions.capitalizeFirstLetter(StringFunctions.removeTailOrElseThrow(baseDomainType.simpleName(), DOMAIN)) +
        "BasedOn" + StringFunctions.capitalizeFirstLetter(domainType.simpleName());
    return ClassNameFunctions.joinPackageAndSimpleName(packageName, simpleName);
  }

  static String getMovableDownwardObjectTypename(CustomType domainType, CustomType baseDomainType) {
    String packageName = ClassNameFunctions.getPackageName(domainType.canonicalName());
    String simpleName = MOVABLE +
        StringFunctions.capitalizeFirstLetter(StringFunctions.removeTailOrElseThrow(baseDomainType.simpleName(), DOMAIN)) +
        "BasedOn" + StringFunctions.capitalizeFirstLetter(StringFunctions.removeTailOrElseThrow(domainType.simpleName(), DOMAIN));
    return ClassNameFunctions.joinPackageAndSimpleName(packageName, simpleName);
  }

  static String getUnmovableDownwardObjectTypename(CustomType domainType, CustomType baseDomainType) {
    String packageName = ClassNameFunctions.getPackageName(domainType.canonicalName());
    String simpleName = UNMOVABLE +
        StringFunctions.capitalizeFirstLetter(StringFunctions.removeTailOrElseThrow(baseDomainType.simpleName(), DOMAIN)) +
        "BasedOn" + StringFunctions.capitalizeFirstLetter(StringFunctions.removeTailOrElseThrow(domainType.simpleName(), DOMAIN));
    return ClassNameFunctions.joinPackageAndSimpleName(packageName, simpleName);
  }

  static String getMovableDownwardObjectTypename(Class<?> domainClass, Class<?> baseDomainClass) {
    String packageName = ClassNameFunctions.getPackageName(domainClass.getCanonicalName());
    String simpleName = MOVABLE +
        StringFunctions.capitalizeFirstLetter(StringFunctions.removeTailOrElseThrow(baseDomainClass.getSimpleName(), DOMAIN)) +
        "BasedOn" + StringFunctions.capitalizeFirstLetter(StringFunctions.removeTailOrElseThrow(domainClass.getSimpleName(), DOMAIN));
    return ClassNameFunctions.joinPackageAndSimpleName(packageName, simpleName);
  }

  static String getConversionMethodName(CustomTypeReference reference) {
    return getConversionMethodName(reference.targetType());
  }

  static String getConversionMethodName(CustomType targetType) {
    KeyDomain keyDomain = Jaquarius.settings().getKeyDomainByDelegateClass(targetType.canonicalName());
    if (keyDomain != null) {
      return "as" + StringFunctions.capitalizeFirstLetter(targetType.simpleName());
    }
    return "as" + StringFunctions.capitalizeFirstLetter(
        StringFunctions.removeTailOrElseThrow(targetType.simpleName(), DOMAIN));
  }

  static boolean isConversionMethod(MethodStatement method) {
    return method.name().length() > 2
        && method.name().startsWith("as")
        && Character.isUpperCase(method.name().charAt(2));
  }

  private static String assignChannelClassCanonicalName(
      String spaceName, CustomType domainType, MethodStatement channelMethod
  ) {
    final String simpleName;
    if (channelMethod.owner().hasAnnotation(Ontology.class)) {
      simpleName = getDefaultChannelClassSimpleName(channelMethod);
    } else {
      simpleName = assumeChannelClassSimpleNameWhenDomainClass(domainType, channelMethod);
    }
    return ClassNameFunctions.joinPackageAndSimpleName(spaceName, simpleName);
  }

  private static String assumeChannelClassSimpleNameWhenDomainClass(
      CustomType domainType, MethodStatement channelMethod
  ) {
    String simpleName = StringFunctions.removeTailOrElseThrow(
        ClassNameFunctions.getSimpleName(domainType.canonicalName()), DOMAIN
    );
    if (isMappingTraverseType(channelMethod)) {
      if (isConversionChannel(channelMethod)) {
        simpleName = StringFunctions.capitalizeFirstLetter(simpleName) + "To" +
            channelMethod.name().substring(2) + CHANNEL;
      } else {
        simpleName = StringFunctions.capitalizeFirstLetter(simpleName) + "To" +
            StringFunctions.capitalizeFirstLetter(channelMethod.name()) + CHANNEL;
      }
    } else {
      simpleName = StringFunctions.capitalizeFirstLetter(simpleName) +
          StringFunctions.capitalizeFirstLetter(joinMethodNameAndParameterTypes(channelMethod)) + CHANNEL;
    }
    return simpleName;
  }

  static String joinMethodNameAndParameterTypes(MethodStatement method) {
    if (method.params().isEmpty()) {
      return method.name();
    }
    var sb = new StringBuilder();
    sb.append(method.name());
    for (MethodParam param : method.params()) {
      if (param.type().isPrimitiveReference()) {
        sb.append(StringFunctions.capitalizeFirstLetter(param.type().asPrimitiveReferenceOrElseThrow().primitiveType().typename()));
      } else if (param.type().isCustomTypeReference()) {
        sb.append(param.type().asCustomTypeReferenceOrElseThrow().targetType().simpleName());
      } else if (param.type().isNamedReference()) {
        sb.append(param.type().asNamedReferenceOrElseThrow().name());
      }
    }
    return sb.toString();
  }

  private static boolean isConversionChannel(MethodStatement channelMethod) {
    String name = channelMethod.name();
    return (name.length() > 3 && name.startsWith("as") && Character.isUpperCase(name.charAt(2)));
  }

  static String getDefaultChannelClassSimpleName(MethodStatement channelMethod) {
    return StringFunctions.capitalizeFirstLetter(channelMethod.name()) + CHANNEL;
  }

  static String getGuideClassCanonicalName(
      ObjectReferenceForm targetForm, String spaceName, CustomType channelType, MethodStatement channelMethod
  ) {
    String name = StringFunctions.replaceTailIfPresent(channelType.canonicalName(), CHANNEL, GUIDE);
    if (ObjectReferenceForms.Primitive.is(targetForm)) {
      name = name + "AsPrimitive";
    } else if (ObjectReferenceForms.PrimitiveWrapper.is(targetForm)) {
      name = name + "AsObject";
    }
    return name;
  }

  static String getObjectAssistantCanonicalName(CustomType domainType) {
    String name = StringFunctions.removeTailOrElseThrow(domainType.canonicalName(), DOMAIN);
    if (name.endsWith("ies")) {
      return name + "s";
    } else if (name.endsWith("es")) {
      return name + "s";
    } else if (name.endsWith("s") || name.endsWith("x") || name.endsWith("z") || name.endsWith("ch") || name.endsWith("sh")) {
      return name + "es";
    } else if (name.endsWith("y")) {
      return name.substring(0, name.length() - 1) + "ies";
    }
    return name + "s";
  }

  static String getObjectAssistantBrokerCanonicalName(CustomType domainType) {
    String name = StringFunctions.removeTailOrElseThrow(domainType.canonicalName(), DOMAIN);
    return name + "sBroker";
  }

  static boolean isPrimitiveTargetForm(MethodStatement method) {
    return method.name().endsWith("AsPrimitive");
  }

  private static boolean isMappingTraverseType(MethodStatement method) {
    return !ChannelFunctions.getTraverseType(method).isMovingBased();
  }

  private static String transformClassName(String className) {
    return className.replace("$", "");
  }

  static String getCustomizerCanonicalName(CustomType originArtifact, ArtifactType targetArtifactType) {
    final String name = StringFunctions.removeTailOrElseThrow(originArtifact.canonicalName(), DOMAIN);
    if (isDomainName(originArtifact.canonicalName())) {
      return switch (ArtifactTypes.of(targetArtifactType)) {
        case RegularObject -> name + CUSTOMIZER;
        case MovableRegularObject -> ClassNameFunctions.addPrefixToSimpleName(MOVABLE, name) + CUSTOMIZER;
        case UnmovableRegularObject -> ClassNameFunctions.addPrefixToSimpleName(UNMOVABLE, name) + CUSTOMIZER;
        case ObjectHandle -> name + HANDLE + CUSTOMIZER;
        case MovableObjectHandle -> name + MOVABLE_HANDLE + CUSTOMIZER;
        case UnmovableObjectHandle -> name + UNMOVABLE_HANDLE + CUSTOMIZER;
        case ObjectAssistant -> name + ASSISTANT + CUSTOMIZER;
        default -> name;
      };
    }
    throw NotImplementedExceptions.withCodeAndMessage("9kDR9g", "Origin artifact {0}, target artifact type {}",
        originArtifact.canonicalName(), targetArtifactType.name());
  }

  String DOMAIN = "Domain";
  String CHANNEL = "Channel";
  String UNMOVABLE = "Unmovable";
  String MOVABLE = "Movable";
  String HANDLE = "Handle";
  String MOVABLE_HANDLE = MOVABLE + HANDLE;
  String UNMOVABLE_HANDLE = UNMOVABLE + HANDLE;
  String WRAPPER = "Wrapper";
  String GUIDE = "Guide";
  String AUTO_GUIDE = "AutoGuide";
  String DATASET = "Dataset";
  String BUILDER = "Builder";
  String CUSTOMIZER = "Customizer";
  String ASSISTANT = "Assistant";
}
