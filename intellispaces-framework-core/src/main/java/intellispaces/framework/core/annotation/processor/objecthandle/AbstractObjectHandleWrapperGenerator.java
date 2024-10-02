package intellispaces.framework.core.annotation.processor.objecthandle;

import intellispaces.common.action.Actions;
import intellispaces.common.action.runner.Runner;
import intellispaces.common.base.text.TextActions;
import intellispaces.common.base.type.TypeFunctions;
import intellispaces.common.javastatement.customtype.CustomType;
import intellispaces.common.javastatement.method.MethodParam;
import intellispaces.common.javastatement.method.MethodStatement;
import intellispaces.common.javastatement.reference.CustomTypeReference;
import intellispaces.common.javastatement.reference.NamedReference;
import intellispaces.common.javastatement.reference.TypeReference;
import intellispaces.common.javastatement.type.Types;
import intellispaces.framework.core.action.TraverseActions;
import intellispaces.framework.core.annotation.AutoGuide;
import intellispaces.framework.core.annotation.Inject;
import intellispaces.framework.core.annotation.processor.AbstractObjectHandleGenerator;
import intellispaces.framework.core.common.NameConventionFunctions;
import intellispaces.framework.core.exception.ConfigurationException;
import intellispaces.framework.core.guide.GuideFunctions;
import intellispaces.framework.core.object.ObjectFunctions;
import intellispaces.framework.core.object.ObjectHandleTypes;
import intellispaces.framework.core.space.domain.DomainFunctions;
import intellispaces.framework.core.space.transition.TransitionFunctions;
import intellispaces.framework.core.system.Modules;
import intellispaces.framework.core.system.ProjectionInjection;

import javax.annotation.processing.RoundEnvironment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

abstract class AbstractObjectHandleWrapperGenerator extends AbstractObjectHandleGenerator {
  protected String domainSimpleClassName;
  protected String typeParamsFull;
  protected String typeParamsBrief;
  protected boolean isAlias;
  protected String primaryDomainSimpleName;
  protected String primaryDomainTypeArguments;
  private final List<MethodStatement> domainMethods;
  protected List<Object> constructors;
  protected List<String> guideActions;
  protected List<String> transitionActions;
  protected final List<Map<String, Object>> injections = new ArrayList<>();
  protected final List<Map<String, Object>> injectionMethods = new ArrayList<>();

  AbstractObjectHandleWrapperGenerator(CustomType initiatorType, CustomType objectHandleType) {
    super(initiatorType, objectHandleType);

    CustomType domainType = ObjectFunctions.getDomainTypeOfObjectHandle(objectHandleType);
    this.domainMethods = domainType.actualMethods().stream()
        .filter(m -> !m.isDefault())
        .filter(this::isNotGetDomainMethod)
        .toList();
  }

  @Override
  protected Stream<MethodStatement> getObjectHandleMethods(
      CustomType objectHandleType, RoundEnvironment roundEnv
  ) {
    return domainMethods.stream();
  }

  protected void analyzeDomain() {
    CustomType domainType = ObjectFunctions.getDomainTypeOfObjectHandle(annotatedType);
    context.addImport(domainType.canonicalName());
    domainSimpleClassName = context.simpleNameOf(domainType.canonicalName());

    Optional<CustomTypeReference> primaryDomain = DomainFunctions.getPrimaryDomainOfAlias(domainType);
    isAlias = primaryDomain.isPresent();
    if (isAlias) {
      Optional<CustomTypeReference> mainPrimaryDomain = DomainFunctions.getMainPrimaryDomainOfAlias(domainType);
      primaryDomainSimpleName = context.addToImportAndGetSimpleName(mainPrimaryDomain.orElseThrow().targetType().canonicalName());
      primaryDomainTypeArguments = primaryDomain.get().typeArgumentsDeclaration(context::addToImportAndGetSimpleName);
    }
  }

  protected void analyzeTransitionActions() {
    this.transitionActions = domainMethods.stream()
        .map(this::buildTransitionAction)
        .toList();
  }

  private String buildTransitionAction(MethodStatement domainMethod) {
    var sb = new StringBuilder();
    sb.append(context.addToImportAndGetSimpleName(Actions.class));
    sb.append(".delegate");
    sb.append(domainMethod.params().size() + 1);
    sb.append("(");
    sb.append(context.addToImportAndGetSimpleName(Actions.class));
    sb.append(".cachedLazyGetter(");
    sb.append(context.addToImportAndGetSimpleName(TraverseActions.class));
    sb.append("::");
    switch (TransitionFunctions.getTraverseType(domainMethod)) {
      case Mapping:
        sb.append("map");
        break;
      case Moving:
        sb.append("move");
        break;
      case MappingOfMoving:
        sb.append("mapOfMoving");
        break;
    }
    sb.append("ThruTransition");
    sb.append(domainMethod.params().size());
    sb.append(",\n");
    sb.append("  ");
    sb.append(context.addToImportAndGetSimpleName(Types.class));
    sb.append(".<");
    sb.append(annotatedType.simpleName());
    sb.append(annotatedType.typeParametersFullDeclaration());
    sb.append(", ");
    sb.append(annotatedType.simpleName());
    sb.append("> of(");
    sb.append(annotatedType.simpleName());
    sb.append(".class),\n  ");
    sb.append(context.addToImportAndGetSimpleName(NameConventionFunctions.getTransitionClassCanonicalName(domainMethod)));
    sb.append(".class))");
    return sb.toString();
  }

  protected void analyzeGuideActions(CustomType objectHandleType) {
    this.guideActions = new ArrayList<>();

    List<MethodStatement> objectHandleMethods = objectHandleType.actualMethods();
    for (MethodStatement domainMethod : domainMethods) {
      if (domainMethod.isDefault()) {
        continue;
      }
      MethodStatement objectHandleMethod = findObjectHandleMethods(domainMethod, objectHandleMethods);
      if (objectHandleMethod == null) {
        this.guideActions.add("null");
      } else {
        this.guideActions.add(buildGuideAction(objectHandleMethod));
      }
    }
  }

  protected void analyzeInjectedGuides(CustomType objectHandleType) {
    for (MethodStatement method : annotatedType.declaredMethods()) {
      if (method.isAbstract()) {
        if (isInjectionMethod(method)) {
          if (!isReturnGuide(method)) {
            throw ConfigurationException.withMessage("Guide injection method '{}' in class {} must return guide",
                method.name(), annotatedType.className()
            );
          }
          if (isAutoGuideMethod(method)) {
            addAutoGuideInjectionAndImplementationMethod(method);
          } else {
            addGuideInjectionAndImplementationMethod(method);
          }
        } else {
          throw ConfigurationException.withMessage("Undefined abstract method '{}' in class {}",
              method.name(), annotatedType.className()
          );
        }
      }
    }
  }

  private void addAutoGuideInjectionAndImplementationMethod(MethodStatement method) {
    context.addImport(Modules.class);
    context.addImport(ProjectionInjection.class);

    String injectionName = method.name();
    String injectionType = method.returnType().orElseThrow().actualDeclaration();

    Map<String, Object> injection = new HashMap<>();
    injection.put("kind", "autoguide");
    injection.put("name", injectionName);
    injection.put("type", injectionType);
    injections.add(injection);

    Map<String, Object> methodProperties = new HashMap<>();
    methodProperties.put("javadoc", "");
    methodProperties.put("annotations", List.of(Override.class.getSimpleName()));
    methodProperties.put("signature", buildMethodSignature(method));
    methodProperties.put("body", buildInjectionMethodBody(injectionType, injections.size() - 1));
    injectionMethods.add(methodProperties);
  }

  private void addGuideInjectionAndImplementationMethod(MethodStatement method) {
    context.addImport(Modules.class);
    context.addImport(ProjectionInjection.class);

    String injectionName = method.name();
    String injectionType = method.returnType().orElseThrow().actualDeclaration();

    Map<String, Object> injection = new HashMap<>();
    injection.put("kind", "guide");
    injection.put("name", injectionName);
    injection.put("type", injectionType);
    injections.add(injection);

    Map<String, Object> methodProperties = new HashMap<>();
    methodProperties.put("javadoc", "");
    methodProperties.put("annotations", List.of(Override.class.getSimpleName()));
    methodProperties.put("signature", buildMethodSignature(method));
    methodProperties.put("body", buildInjectionMethodBody(injectionType, injections.size() - 1));
    injectionMethods.add(methodProperties);
  }

  private String buildInjectionMethodBody(String injectionType, int injectionIndex) {
    return "return (" + injectionType + ") this.$handle.injection(" + injectionIndex + ").value();";
  }

  private MethodStatement findObjectHandleMethods(
      MethodStatement domainMethod, List<MethodStatement> objectHandleMethods
  ) {
    for (MethodStatement objectHandleMethod : objectHandleMethods) {
      if (!GuideFunctions.isGuideMethod(objectHandleMethod)) {
        continue;
      }
      if (
          domainMethod.name().equals(objectHandleMethod.name())
              && equalParams(domainMethod.params(), objectHandleMethod.params())
      ) {
        return objectHandleMethod;
      }
    }
    return null;
  }

  private boolean equalParams(List<MethodParam> domainMethodParams, List<MethodParam> guideMethodParams) {
    if (domainMethodParams.size() != guideMethodParams.size()) {
      return false;
    }

    boolean paramMatches = true;
    for (int i = 0; i < domainMethodParams.size(); i++) {
      MethodParam domainMethodParam = domainMethodParams.get(i);
      String domainMethodParamDeclaration = ObjectFunctions.getObjectHandleDeclaration(
          domainMethodParam.type(), ObjectHandleTypes.Common, Function.identity()
      );

      MethodParam guideMethodParam = guideMethodParams.get(i);
      String guideMethodParamDeclaration = guideMethodParam.type().actualDeclaration(TypeFunctions::shortenName);
      if (!domainMethodParamDeclaration.equals(guideMethodParamDeclaration)) {
        paramMatches = false;
        break;
      }
    }
    return paramMatches;
  }

  private String buildGuideAction(MethodStatement objectHandleMethod) {
    var sb = new StringBuilder();
    sb.append("Actions.of(super::");sb.append(objectHandleMethod.name());
    sb.append(", ");
    sb.append(buildProjectionActionTypeParameter(objectHandleMethod.returnType().orElseThrow()));
    sb.append(".class");
    for (MethodParam param : objectHandleMethod.params()) {
      sb.append(", ");
      sb.append(buildProjectionActionTypeParameter(param.type()));
      sb.append(".class");
    }
    sb.append(")");
    return sb.toString();
  }

  private String buildProjectionActionTypeParameter(TypeReference type) {
    if (type.isNamedReference()) {
      NamedReference namedType = type.asNamedReferenceOrElseThrow();
      if (namedType.extendedBounds().isEmpty()) {
        return "Object";
      }
    } else if (type.isCustomTypeReference()) {
      return context.addToImportAndGetSimpleName(type.asCustomTypeReferenceOrElseThrow().targetType().canonicalName());
    }
    return type.actualDeclaration(context::addToImportAndGetSimpleName);
  }

  protected String getGeneratedClassCanonicalName() {
    return NameConventionFunctions.getObjectHandleWrapperCanonicalName(annotatedType);
  }

  protected void analyzeTypeParams(CustomType objectHandleType) {
    if (objectHandleType.typeParameters().isEmpty()) {
      typeParamsFull = typeParamsBrief = "";
      return;
    }

    var typeParamsFullBuilder = new StringBuilder();
    var typeParamsBriefBuilder = new StringBuilder();
    Runner commaAppender = TextActions.skippingFirstTimeCommaAppender(typeParamsFullBuilder, typeParamsBriefBuilder);

    typeParamsFullBuilder.append("<");
    typeParamsBriefBuilder.append("<");
    for (NamedReference typeParam : objectHandleType.typeParameters()) {
      commaAppender.run();
      typeParamsFullBuilder.append(typeParam.formalFullDeclaration());
      typeParamsBriefBuilder.append(typeParam.formalBriefDeclaration());
    }
    typeParamsFullBuilder.append(">");
    typeParamsBriefBuilder.append(">");
    typeParamsFull = typeParamsFullBuilder.toString();
    typeParamsBrief = typeParamsBriefBuilder.toString();
  }

  @SuppressWarnings("unchecked, rawtypes")
  protected void analyzeConstructors(CustomType objectHandleType) {
    List<MethodStatement> constructors;
    if (objectHandleType.asClass().isPresent()) {
      constructors = objectHandleType.asClass().get().constructors();
      List<Map<String, Object>> constructorDescriptors = new ArrayList<>();
      for (MethodStatement constructor : constructors) {
        constructorDescriptors.add(analyzeConstructor(constructor, context.getImportConsumer()));
      }
      this.constructors = (List) constructorDescriptors;
    } else {
      this.constructors = List.of();
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
          "type", type.actualDeclaration(context::simpleNameOf))
      );
      type.asCustomTypeReference().ifPresent(t -> imports.accept(t.targetType().canonicalName()));
    }
    constructorDescriptor.put("params", paramDescriptors);
    return constructorDescriptor;
  }

  @Override
  protected void analyzeObjectHandleMethods(CustomType customType, RoundEnvironment roundEnv) {
    int index = 0;
    this.methods = new ArrayList<>();
    for (MethodStatement domainMethod : domainMethods) {
      this.methods.add(buildTransitionMethod(index++, domainMethod));
    }
  }

  protected Map<String, String> buildTransitionMethod(int methodIndex, MethodStatement domainMethod) {
    if (isDisableMoving(domainMethod)) {
      return Map.of();
    }

    var sb = new StringBuilder();
    sb.append("@Ordinal(").append(methodIndex).append(")\n");
    sb.append("public ");
    appendMethodTypeParameters(sb, domainMethod);
    appendMethodReturnHandleType(sb, domainMethod);
    sb.append(" ");
    sb.append(domainMethod.name());
    sb.append("(");
    appendMethodParameters(sb, domainMethod);
    sb.append(")");
    appendMethodExceptions(sb, domainMethod);
    sb.append(" {\n");
    sb.append("  return (");
    appendMethodReturnHandleType(sb, domainMethod);
    sb.append(") $handle.getTransitionAction(");
    sb.append(methodIndex);
    sb.append(").asAction");
    sb.append(domainMethod.params().size() + 1);
    sb.append("().execute(this");
    for (MethodParam param : domainMethod.params()) {
      sb.append(", ");
      sb.append(param.name());
    }
    sb.append(");\n}");
    return Map.of("declaration", sb.toString());
  }

  private boolean isInjectionMethod(MethodStatement method) {
    return method.hasAnnotation(Inject.class);
  }

  private boolean isAutoGuideMethod(MethodStatement method) {
    return method.hasAnnotation(AutoGuide.class);
  }

  private boolean isReturnGuide(MethodStatement method) {
    return GuideFunctions.isGuideType(method.returnType().orElseThrow());
  }
}
