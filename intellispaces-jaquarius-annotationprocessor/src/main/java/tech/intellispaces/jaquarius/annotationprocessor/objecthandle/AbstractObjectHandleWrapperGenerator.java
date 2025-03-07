package tech.intellispaces.jaquarius.annotationprocessor.objecthandle;

import tech.intellispaces.commons.action.runnable.RunnableAction;
import tech.intellispaces.commons.action.text.StringActions;
import tech.intellispaces.commons.exception.UnexpectedExceptions;
import tech.intellispaces.commons.text.StringFunctions;
import tech.intellispaces.commons.type.ClassFunctions;
import tech.intellispaces.commons.type.ClassNameFunctions;
import tech.intellispaces.commons.type.PrimitiveTypes;
import tech.intellispaces.commons.java.reflection.customtype.CustomType;
import tech.intellispaces.commons.java.reflection.method.MethodParam;
import tech.intellispaces.commons.java.reflection.method.MethodStatement;
import tech.intellispaces.commons.java.reflection.reference.CustomTypeReference;
import tech.intellispaces.commons.java.reflection.reference.NamedReference;
import tech.intellispaces.commons.java.reflection.reference.TypeReference;
import tech.intellispaces.jaquarius.annotationprocessor.domain.AbstractObjectHandleGenerator;
import tech.intellispaces.jaquarius.annotationprocessor.AnnotationGeneratorFunctions;
import tech.intellispaces.jaquarius.engine.description.ObjectHandleMethodPurposes;
import tech.intellispaces.jaquarius.exception.ConfigurationExceptions;
import tech.intellispaces.jaquarius.guide.GuideFunctions;
import tech.intellispaces.jaquarius.naming.NameConventionFunctions;
import tech.intellispaces.jaquarius.object.reference.ObjectHandleFunctions;
import tech.intellispaces.jaquarius.object.reference.ObjectHandleTypes;
import tech.intellispaces.jaquarius.object.reference.ObjectReferenceForm;
import tech.intellispaces.jaquarius.object.reference.ObjectReferenceForms;
import tech.intellispaces.jaquarius.space.channel.ChannelFunctions;
import tech.intellispaces.jaquarius.space.domain.DomainFunctions;
import tech.intellispaces.jaquarius.traverse.TraverseQualifierSetForm;
import tech.intellispaces.jaquarius.traverse.TraverseQualifierSetForms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class AbstractObjectHandleWrapperGenerator extends AbstractObjectHandleGenerator {
  protected String domainSimpleClassName;
  protected String typeParamsFull;
  protected String typeParamsBrief;
  protected String domainTypeParamsBrief;
  protected boolean isAlias;
  protected String primaryDomainSimpleName;
  protected String primaryDomainTypeArguments;
  protected boolean implRelease;
  protected CustomType domainType;
  protected String domainTypeDeclaration;
  protected String primaryDomainTypeDeclaration;
  private List<MethodStatement> domainMethods;
  protected final List<Map<String, Object>> constructors = new ArrayList<>();
  protected final List<Map<String, String>> traverseMethods = new ArrayList<>();
  protected final List<Map<String, String>> guideMethods = new ArrayList<>();
  protected final List<Map<String, Object>> injectionMethods = new ArrayList<>();
  protected final List<Map<String, String>> conversionMethods = new ArrayList<>();
  protected final List<Map<String, Object>> methodDescriptions = new ArrayList<>();

  AbstractObjectHandleWrapperGenerator(CustomType objectHandleType) {
    super(objectHandleType);
  }

  protected void analyzeDomain() {
    domainType = ObjectHandleFunctions.getDomainOfObjectHandle(sourceArtifact());
    addImport(domainType.canonicalName());
    domainSimpleClassName = simpleNameOf(domainType.canonicalName());
    domainTypeParamsBrief = sourceArtifact().typeParametersBriefDeclaration();

    domainMethods = domainType.actualMethods().stream()
        .filter(m -> !m.isDefault())
        .filter(m -> excludeDeepConversionMethods(m, domainType))
        .filter(DomainFunctions::isNotDomainClassGetter)
        .toList();
  }

  @SuppressWarnings("unchecked,rawtypes")
  protected void analyzeAlias() {
    domainTypeDeclaration = buildDomainType(domainType, (List) domainType.typeParameters());

    List<CustomTypeReference> equivalentDomains = DomainFunctions.getEquivalentDomains(domainType);
    isAlias = !equivalentDomains.isEmpty();
    if (isAlias) {
      CustomTypeReference nearEquivalentDomain = equivalentDomains.get(0);
      CustomTypeReference mainEquivalentDomain = equivalentDomains.get(equivalentDomains.size() - 1);

      primaryDomainTypeArguments = getDomainTypeParamsBrief(nearEquivalentDomain);
      primaryDomainSimpleName = addImportAndGetSimpleName(mainEquivalentDomain.targetType().canonicalName());
      primaryDomainTypeDeclaration = buildDomainType(mainEquivalentDomain.targetType(), mainEquivalentDomain.typeArguments());
    } else {
      primaryDomainTypeArguments = domainTypeParamsBrief;
      primaryDomainSimpleName = addImportAndGetSimpleName(domainType.canonicalName());
      primaryDomainTypeDeclaration = domainTypeDeclaration;
    }
  }

  protected String getObjectHandleSimpleName() {
    Class<?> handleClass;
    if (ObjectHandleFunctions.isUnmovableObjectHandle(sourceArtifact())) {
      handleClass = ObjectHandleFunctions.getObjectHandleClass(ObjectHandleTypes.UnmovablePureObject);
    } else if (ObjectHandleFunctions.isMovableObjectHandle(sourceArtifact())) {
      handleClass = ObjectHandleFunctions.getObjectHandleClass(ObjectHandleTypes.MovablePureObject);
    } else {
      throw UnexpectedExceptions.withMessage("Could not define movable type of the object handle {0}",
          sourceArtifact().canonicalName());
    }
    return addImportAndGetSimpleName(handleClass);
  }

  protected void analyzeTypeParams() {
    if (sourceArtifact().typeParameters().isEmpty()) {
      typeParamsFull = typeParamsBrief = "";
      return;
    }

    var typeParamsFullBuilder = new StringBuilder();
    var typeParamsBriefBuilder = new StringBuilder();
    RunnableAction commaAppender = StringActions.skipFirstTimeCommaAppender(typeParamsFullBuilder, typeParamsBriefBuilder);

    typeParamsFullBuilder.append("<");
    typeParamsBriefBuilder.append("<");
    for (NamedReference typeParam : sourceArtifact().typeParameters()) {
      commaAppender.run();
      typeParamsFullBuilder.append(typeParam.formalFullDeclaration());
      typeParamsBriefBuilder.append(typeParam.formalBriefDeclaration());
    }
    typeParamsFullBuilder.append(">");
    typeParamsBriefBuilder.append(">");
    typeParamsFull = typeParamsFullBuilder.toString();
    typeParamsBrief = typeParamsBriefBuilder.toString();
  }

  protected void analyzeConstructors() {
    List<MethodStatement> constructors;
    if (sourceArtifact().asClass().isPresent()) {
      constructors = sourceArtifact().asClass().get().constructors();
      for (MethodStatement constructor : constructors) {
        this.constructors.add(analyzeConstructor(constructor, getImportConsumer()));
      }
    }
  }

  private Map<String, Object> analyzeConstructor(MethodStatement constructor, Consumer<String> imports) {
    Map<String, Object> constructorDescriptor = new HashMap<>();
    List<Map<String, String>> paramDescriptors = new ArrayList<>();
    for (MethodParam param : constructor.params()) {
      TypeReference type = param.type();
      type.dependencyTypenames().forEach(imports);
      paramDescriptors.add(Map.of(
          "name", param.name(),
          "type", type.actualDeclaration(this::simpleNameOf))
      );
      type.asCustomTypeReference().ifPresent(t -> imports.accept(t.targetType().canonicalName()));
    }
    constructorDescriptor.put("params", paramDescriptors);
    return constructorDescriptor;
  }

  protected void analyzeObjectHandleMethods() {
    int methodOrdinal = 0;
    for (MethodStatement method : domainMethods) {
      analyzeRawDomainMethod(method);
      analyzeMethod(method, TraverseQualifierSetForms.Object, ObjectReferenceForms.Default, methodOrdinal++);
      if (hasMethodNormalForm(method)) {
        if (includeMethodForm(method, TraverseQualifierSetForms.Normal, ObjectReferenceForms.Default)) {
          analyzeMethod(method, TraverseQualifierSetForms.Normal, ObjectReferenceForms.Default, methodOrdinal++);
        }
      }
      if (includeMethodForm(method, TraverseQualifierSetForms.Normal, ObjectReferenceForms.Primitive)) {
        analyzeMethod(method, TraverseQualifierSetForms.Normal, ObjectReferenceForms.Primitive, methodOrdinal++);
      }
    }
  }

  private void analyzeMethod(
      MethodStatement method, TraverseQualifierSetForm methodForm, ObjectReferenceForm targetForm, int methodOrdinal
  ) {
    traverseMethods.add(generateTraverseMethod(method, methodForm, targetForm, methodOrdinal));
    analyzeGuideMethod(method, methodForm, targetForm, methodOrdinal);
  }

  private void analyzeGuideMethod(
      MethodStatement domainMethod, TraverseQualifierSetForm methodForm, ObjectReferenceForm targetForm, int methodOrdinal
  ) {
    if (domainMethod.isDefault()) {
      return;
    }

    List<MethodStatement> objectHandleMethods = sourceArtifact().actualMethods();
    if (NameConventionFunctions.isConversionMethod(domainMethod)) {
      this.methodDescriptions.add(buildTraverseMethodDescriptions(domainMethod, methodForm, targetForm, methodOrdinal));
      this.methodDescriptions.add(buildConversionGuideMethodDescriptions(domainMethod, methodOrdinal));
      return;
    }

    this.methodDescriptions.add(buildTraverseMethodDescriptions(domainMethod, methodForm, targetForm, methodOrdinal));
    MethodStatement guideMethod = findGuideMethod(domainMethod, objectHandleMethods, methodForm, targetForm);
    if (guideMethod != null && !guideMethod.isAbstract()) {
      this.guideMethods.add(AnnotationGeneratorFunctions.buildGuideActionMethod(guideMethod, this));
      this.methodDescriptions.add(buildGuideMethodDescriptions(guideMethod, methodForm, methodOrdinal));
    }
  }

  protected void analyzeInjectedGuides() {
    for (MethodStatement method : sourceArtifact().declaredMethods()) {
      if (method.isAbstract()) {
        if (AnnotationGeneratorFunctions.isInjectionMethod(method)) {
          if (!AnnotationGeneratorFunctions.isReturnGuide(method)) {
            throw ConfigurationExceptions.withMessage("Guide injection method '{0}' in class {1} must return guide",
                method.name(), sourceArtifact().className()
            );
          }
          if (AnnotationGeneratorFunctions.isAutoGuideMethod(method)) {
            addAutoGuideInjectionMethod(method);
          } else {
            addSpecGuideInjectionMethod(method);
          }
        } else {
          throw ConfigurationExceptions.withMessage("Undefined abstract method '{0}' in class {1}",
              method.name(), sourceArtifact().className()
          );
        }
      }
    }
  }

  protected void analyzeReleaseMethod() {
    Optional<MethodStatement> releaseMethod = sourceArtifact().declaredMethod("release", List.of());
    implRelease = releaseMethod.isPresent() && !releaseMethod.get().isAbstract();
  }

  private void addAutoGuideInjectionMethod(MethodStatement method) {
    int injectionOrdinal = injectionMethods.size();
    String injectionType = method.returnType().orElseThrow().actualDeclaration();

    Map<String, Object> methodProperties = new HashMap<>();
    methodProperties.put("javadoc", "");
    methodProperties.put("annotations", List.of(Override.class.getSimpleName()));
    methodProperties.put("signature", buildMethodSignature(method));
    methodProperties.put("body", buildInjectionMethodBody(injectionType, injectionOrdinal));
    injectionMethods.add(methodProperties);
    this.methodDescriptions.add(buildAutoGuideInjectionMethodDescriptions(method, injectionOrdinal));
  }

  private void addSpecGuideInjectionMethod(MethodStatement method) {
    int injectionOrdinal = injectionMethods.size();
    String injectionType = method.returnType().orElseThrow().actualDeclaration();

    Map<String, Object> methodProperties = new HashMap<>();
    methodProperties.put("javadoc", "");
    methodProperties.put("annotations", List.of(Override.class.getSimpleName()));
    methodProperties.put("signature", buildMethodSignature(method));
    methodProperties.put("body", buildInjectionMethodBody(injectionType, injectionOrdinal));
    injectionMethods.add(methodProperties);
    this.methodDescriptions.add(buildSpecGuideInjectionMethodDescriptions(method, injectionOrdinal));
  }

  private String buildInjectionMethodBody(String injectionType, int injectionIndex) {
    return "return (" + injectionType + ") this.$broker.injection(" + injectionIndex + ").value();";
  }

  private MethodStatement findGuideMethod(
      MethodStatement domainMethod,
      List<MethodStatement> objectHandleMethods,
      TraverseQualifierSetForm methodForm,
      ObjectReferenceForm targetForm
  ) {
    for (MethodStatement objectHandleMethod : objectHandleMethods) {
      if (!GuideFunctions.isGuideMethod(objectHandleMethod)) {
        continue;
      }
      if (getObjectHandleMethodName(domainMethod, targetForm).equals(objectHandleMethod.name()) &&
          equalParams(domainMethod.params(), objectHandleMethod.params(), methodForm)
      ) {
        return objectHandleMethod;
      }
    }
    return null;
  }

  private boolean equalParams(
      List<MethodParam> domainMethodParams, List<MethodParam> guideMethodParams, TraverseQualifierSetForm methodForm
  ) {
    if (domainMethodParams.size() != guideMethodParams.size()) {
      return false;
    }

    boolean paramMatches = true;
    for (int i = 0; i < domainMethodParams.size(); i++) {
      MethodParam domainParam = domainMethodParams.get(i);
      ObjectReferenceForm referenceForm = ObjectHandleFunctions.getReferenceForm(domainParam.type(), methodForm);
      String domainMethodParamDeclaration = ObjectHandleFunctions.getObjectHandleDeclaration(
          domainParam.type(), ObjectHandleTypes.UndefinedPureObject, referenceForm, true, false, Function.identity()
      );

      MethodParam guideParam = guideMethodParams.get(i);
      String guideMethodParamDeclaration = guideParam.type().actualDeclaration(ClassNameFunctions::getShortenName);
      if (!domainMethodParamDeclaration.equals(guideMethodParamDeclaration)) {
        paramMatches = false;
        break;
      }
    }
    return paramMatches;
  }

  protected String getGeneratedClassCanonicalName() {
    return NameConventionFunctions.getObjectHandleWrapperCanonicalName(sourceArtifact());
  }

  private Map<String, Object> buildTraverseMethodDescriptions(
      MethodStatement domainMethod, TraverseQualifierSetForm methodForm, ObjectReferenceForm targetForm, int ordinal
  ) {
    var map = new HashMap<String, Object>();
    map.put("name", getObjectHandleMethodName(domainMethod, targetForm));

    List<String> paramClasses = new ArrayList<>();
    for (MethodParam param : domainMethod.params()) {
      paramClasses.add(buildGuideParamClassName(param.type(), methodForm));
    }
    map.put("params", paramClasses);
    map.put("purpose", ObjectHandleMethodPurposes.TraverseMethod.name());
    map.put("traverseOrdinal", ordinal);
    map.put("channelClass", addImportAndGetSimpleName(NameConventionFunctions.getChannelClassCanonicalName(
        domainMethod)));
    map.put("traverseType", ChannelFunctions.getTraverseType(domainMethod).name());
    return map;
  }

  private Map<String, Object> buildGuideMethodDescriptions(
      MethodStatement guidMethod, TraverseQualifierSetForm methodForm, int ordinal
  ) {
    var map = new HashMap<String, Object>();
    map.put("name", ObjectHandleFunctions.buildObjectHandleGuideMethodName(guidMethod));

    List<String> paramClasses = new ArrayList<>();
    for (MethodParam param : guidMethod.params()) {
      paramClasses.add(buildGuideParamClassName(param.type(), methodForm));
    }
    map.put("params", paramClasses);
    map.put("purpose", ObjectHandleMethodPurposes.GuideMethod.name());
    map.put("traverseOrdinal", ordinal);
    return map;
  }

  private Map<String, Object> buildConversionGuideMethodDescriptions(
      MethodStatement domainMethod, int ordinal
  ) {
    var map = new HashMap<String, Object>();

    CustomTypeReference parentType = domainMethod.returnType().orElseThrow().asCustomTypeReferenceOrElseThrow();
    String methodName = "$as" +
        StringFunctions.capitalizeFirstLetter(StringFunctions.removeTailOrElseThrow(parentType.targetType().simpleName(), "Domain")) +
        "Guide";
    map.put("name", methodName);

    map.put("params", List.of());
    map.put("purpose", ObjectHandleMethodPurposes.GuideMethod.name());
    map.put("traverseOrdinal", ordinal);
    return map;
  }

  private Map<String, Object> buildAutoGuideInjectionMethodDescriptions(
      MethodStatement injectionMethod, int ordinal
  ) {
    var map = new HashMap<String, Object>();
    map.put("name", injectionMethod.name());
    map.put("params", List.of());
    map.put("purpose", ObjectHandleMethodPurposes.InjectionMethod.name());

    map.put("injectionKind", "autoguide");
    map.put("injectionOrdinal", ordinal);
    map.put("injectionName", injectionMethod.name());
    map.put("injectionType", injectionMethod.returnType().orElseThrow().actualDeclaration(
        this::addImportAndGetSimpleName)
    );
    return map;
  }

  private Map<String, Object> buildSpecGuideInjectionMethodDescriptions(MethodStatement injectionMethod, int ordinal) {
    var map = new HashMap<String, Object>();
    map.put("name", injectionMethod.name());
    map.put("params", List.of());
    map.put("purpose", ObjectHandleMethodPurposes.InjectionMethod.name());

    map.put("injectionKind", "specguide");
    map.put("injectionOrdinal", ordinal);
    map.put("injectionName", injectionMethod.name());
    map.put("injectionType", injectionMethod.returnType().orElseThrow().actualDeclaration(
        this::addImportAndGetSimpleName)
    );
    return map;
  }

  private String buildGuideParamClassName(TypeReference type, TraverseQualifierSetForm methodForm) {
    return ObjectHandleFunctions.getObjectHandleDeclaration(
        AnnotationGeneratorFunctions.normalizeType(type),
        ObjectHandleTypes.UndefinedPureObject,
        ObjectHandleFunctions.getReferenceForm(type, methodForm),
        false,
        true,
        this::addImportAndGetSimpleName
    );
  }

  protected Map<String, String> generateTraverseMethod(
      MethodStatement domainMethod, TraverseQualifierSetForm methodForm, ObjectReferenceForm targetForm, int methodOrdinal
  ) {
    var sb = new StringBuilder();
    sb.append("@Ordinal(").append(methodOrdinal).append(")\n");
    sb.append("public ");
    appendMethodTypeParameters(sb, domainMethod);
    appendMethodReturnHandleType(sb, domainMethod, targetForm);
    sb.append(" ");
    sb.append(getObjectHandleMethodName(domainMethod, targetForm));
    sb.append("(");
    appendMethodParams(sb, domainMethod, methodForm);
    sb.append(")");
    appendMethodExceptions(sb, domainMethod);
    sb.append(" {\n");
    sb.append("  return ");

    TypeReference returnType = domainMethod.returnType().orElseThrow();
    if (returnType.isPrimitiveReference()) {
      if (PrimitiveTypes.Boolean.is(returnType.asPrimitiveReferenceOrElseThrow().primitiveType())) {
        sb.append("PrimitiveFunctions.longToBoolean((int) ");
        appendInvokeMethodAction(sb, domainMethod, methodForm, targetForm, methodOrdinal);
        sb.append(")");
      } else {
        sb.append("(");
        appendMethodReturnHandleType(sb, domainMethod, targetForm);
        sb.append(") ");
        appendInvokeMethodAction(sb, domainMethod, methodForm, targetForm, methodOrdinal);
      }
    } else {
      sb.append("(");
      appendMethodCastHandleType(sb, domainMethod, targetForm);
      sb.append(") ");
      appendInvokeMethodAction(sb, domainMethod, methodForm, targetForm, methodOrdinal);
    }
    sb.append(";\n}");
    return Map.of("declaration", sb.toString());
  }

  private void appendMethodCastHandleType(
      StringBuilder sb, MethodStatement method, ObjectReferenceForm targetForm
  ) {
    if (ObjectReferenceForms.Default.is(targetForm)) {
      appendObjectFormMethodReturnType(sb, method);
    } else if (ObjectReferenceForms.Primitive.is(targetForm)) {
      CustomType returnType = method.returnType().orElseThrow().asCustomTypeReferenceOrElseThrow().targetType();
      sb.append(ClassFunctions.primitiveTypenameOfWrapper(returnType.canonicalName()));
    } else {
      throw UnexpectedExceptions.withMessage("Unsupported guide form - {0}", targetForm);
    }
  }

  private void appendInvokeMethodAction(
      StringBuilder sb,
      MethodStatement domainMethod,
      TraverseQualifierSetForm methodForm,
      ObjectReferenceForm targetForm,
      int methodOrdinal
  ) {
    var additionalCloseBracket = false;
    if (ObjectReferenceForms.Primitive.is(targetForm) && isPrimitiveWrapper(domainMethod.returnType().orElseThrow())) {
      CustomType returnType = domainMethod.returnType().orElseThrow().asCustomTypeReferenceOrElseThrow().targetType();
      String typename = ClassFunctions.primitiveTypenameOfWrapper(returnType.canonicalName());
      if (PrimitiveTypes.Byte.typename().equals(typename)) {
        sb.append("PrimitiveFunctions.intToByte(");
        additionalCloseBracket = true;
      }
    }
    sb.append("$broker.methodAction(");
    sb.append(methodOrdinal);
    sb.append(").castToAction");
    sb.append(domainMethod.params().size() + 1);
    sb.append("().");
    sb.append(buildExecuteMethod(domainMethod, targetForm));
    sb.append("(this");
    for (MethodParam param : domainMethod.params()) {
      sb.append(", ");
      appendMethodParam(sb, param);
    }
    sb.append(")");
    if (additionalCloseBracket) {
      sb.append(")");
    }
  }

  private void appendMethodParam(StringBuilder sb, MethodParam param) {
    if (param.type().isPrimitiveReference()) {
      String typename = param.type().asPrimitiveReferenceOrElseThrow().typename();
      if (PrimitiveTypes.Boolean.typename().equals(typename)) {
        sb.append("PrimitiveFunctions.booleanToInt(").append(param.name()).append(")");
      } else if (PrimitiveTypes.Char.typename().equals(typename)) {
        sb.append("(int) ").append(param.name());
      } else if (PrimitiveTypes.Byte.typename().equals(typename)) {
        sb.append("(int) ").append(param.name());
      } else if (PrimitiveTypes.Short.typename().equals(typename)) {
        sb.append("(int) ").append(param.name());
      } else {
        sb.append(param.name());
      }
    } else {
      sb.append(param.name());
    }
  }

  private String buildExecuteMethod(MethodStatement domainMethod, ObjectReferenceForm targetForm) {
    if (ObjectReferenceForms.Primitive.is(targetForm)) {
      if (isPrimitiveWrapper(domainMethod.returnType().orElseThrow())) {
        CustomType returnType = domainMethod.returnType().orElseThrow().asCustomTypeReferenceOrElseThrow().targetType();
        String typename = ClassFunctions.primitiveTypenameOfWrapper(returnType.canonicalName());
        if (tech.intellispaces.commons.object.ObjectFunctions.equalsAnyOf(
            typename,
            PrimitiveTypes.Boolean.typename(),
            PrimitiveTypes.Char.typename(),
            PrimitiveTypes.Byte.typename(),
            PrimitiveTypes.Short.typename(),
            PrimitiveTypes.Int.typename()
        )) {
          return "executeReturnInt";
        } else if (PrimitiveTypes.Float.typename().equals(typename) || PrimitiveTypes.Double.typename().equals(typename)) {
          return "executeReturnDouble";
        } else {
          return "execute";
        }
      } else {
        return "execute";
      }
    } else if (ObjectReferenceForms.Default.is(targetForm)) {
        return "execute";
    } else {
      throw UnexpectedExceptions.withMessage("Unsupported guide form - {0}", targetForm);
    }
  }

  protected void analyzeConversionMethods(CustomType domain) {
    Collection<CustomTypeReference> superDomains = DomainFunctions.getEffectiveSuperDomains(domain);
    superDomains.stream()
        .map(this::buildConversionMethod)
        .forEach(conversionMethods::add);
  }

  private Map<String, String> buildConversionMethod(CustomTypeReference superDomain) {
    var sb = new StringBuilder();
    sb.append("private ");
    sb.append(buildObjectHandleDeclaration(superDomain, getObjectHandleType(), true));
    sb.append(" $");
    sb.append(NameConventionFunctions.getConversionMethodName(superDomain));
    sb.append("Guide() {\n");
    sb.append("  return ");

    if (hasSuperDomain(superDomain)) {
      sb.append("new ");
      Optional<CustomTypeReference> aliasBaseDomain = DomainFunctions.getAliasBaseDomain(domainType);
      CustomType actualDomain = aliasBaseDomain.isPresent() ? aliasBaseDomain.get().targetType() : domainType;
      sb.append(addImportAndGetSimpleName(
          NameConventionFunctions.getDownwardObjectHandleTypename(actualDomain, superDomain.targetType(), getObjectHandleType())
      ));
      sb.append("(this);");
    } else {
      String chain = DomainFunctions.buildConversionMethodsChain(domainType, superDomain.targetType());
      sb.append("this");
      sb.append(chain);
      sb.append(";");
    }
    sb.append("\n}");
    return Map.of("declaration", sb.toString());
  }

  private boolean hasSuperDomain(CustomTypeReference domain) {
    for (CustomTypeReference superDomain : domainType.parentTypes()) {
      if (superDomain.targetType().canonicalName().equals(domain.targetType().canonicalName())) {
        return true;
      }
    }
    return false;
  }
}
