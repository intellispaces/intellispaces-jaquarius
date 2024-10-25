package intellispaces.framework.core.annotation.processor.unit;

import intellispaces.common.action.Action;
import intellispaces.common.action.Actions;
import intellispaces.common.action.functional.FunctionActions;
import intellispaces.common.action.getter.ResettableGetter;
import intellispaces.common.action.runner.Runner;
import intellispaces.common.annotationprocessor.context.AnnotationProcessingContext;
import intellispaces.common.base.exception.UnexpectedViolationException;
import intellispaces.common.base.math.MathFunctions;
import intellispaces.common.base.text.TextActions;
import intellispaces.common.base.type.TypeFunctions;
import intellispaces.common.javastatement.customtype.CustomType;
import intellispaces.common.javastatement.instance.AnnotationInstance;
import intellispaces.common.javastatement.instance.ClassInstance;
import intellispaces.common.javastatement.instance.Instance;
import intellispaces.common.javastatement.method.MethodParam;
import intellispaces.common.javastatement.method.MethodStatement;
import intellispaces.common.javastatement.reference.NamedReference;
import intellispaces.common.javastatement.reference.TypeReference;
import intellispaces.framework.core.annotation.AutoGuide;
import intellispaces.framework.core.annotation.Inject;
import intellispaces.framework.core.annotation.Ordinal;
import intellispaces.framework.core.annotation.Projection;
import intellispaces.framework.core.annotation.ProjectionDefinition;
import intellispaces.framework.core.annotation.Wrapper;
import intellispaces.framework.core.annotation.processor.AbstractGenerator;
import intellispaces.framework.core.annotation.processor.GuideProcessorFunctions;
import intellispaces.framework.core.common.NameConventionFunctions;
import intellispaces.framework.core.exception.ConfigurationException;
import intellispaces.framework.core.guide.GuideFunctions;
import intellispaces.framework.core.object.ObjectFunctions;
import intellispaces.framework.core.system.Injection;
import intellispaces.framework.core.system.Modules;
import intellispaces.framework.core.system.ProjectionInjection;
import intellispaces.framework.core.system.ProjectionTargetSupplier;
import intellispaces.framework.core.system.UnitWrapper;
import intellispaces.framework.core.system.injection.AutoGuideInjections;
import intellispaces.framework.core.system.injection.GuideInjections;
import intellispaces.framework.core.system.injection.ProjectionInjections;
import intellispaces.framework.core.system.kernel.KernelUnit;
import intellispaces.framework.core.system.kernel.ProjectionRegistry;
import intellispaces.framework.core.system.projection.ProjectionDefinitionBasedOnMethodActions;
import intellispaces.framework.core.system.projection.ProjectionDefinitionBasedOnProviderClasses;
import intellispaces.framework.core.system.projection.ProjectionFunctions;
import intellispaces.framework.core.system.projection.ProjectionReferences;

import javax.annotation.processing.RoundEnvironment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UnitWrapperGenerator extends AbstractGenerator {
  private final List<Map<String, Object>> injections = new ArrayList<>();
  private final List<String> projectionDefinitions = new ArrayList<>();
  private final List<Map<String, Object>> injectionMethods = new ArrayList<>();
  private List<MethodStatement> declaredMethods;
  private List<Map<String, Object>> overrideGuideMethods;
  private final List<Map<String, String>> guideActionMethods = new ArrayList<>();
  private final List<String> guideActions = new ArrayList<>();
  private String typeParamsFullDeclaration;
  private String typeParamsBriefDeclaration;

  public UnitWrapperGenerator(CustomType initiatorType, CustomType annotatedType) {
    super(initiatorType, annotatedType);
  }

  @Override
  public boolean isRelevant(AnnotationProcessingContext context) {
    return true;
  }

  @Override
  public String artifactName() {
    return NameConventionFunctions.getUnitWrapperCanonicalName(annotatedType.className());
  }

  @Override
  protected String templateName() {
    return "/unit_wrapper.template";
  }

  @Override
  protected Map<String, Object> templateVariables() {
    Map<String, Object> vars = new HashMap<>();
    vars.put("generatedAnnotation", makeGeneratedAnnotation());
    vars.put("packageName", context.packageName());
    vars.put("sourceClassName", sourceClassCanonicalName());
    vars.put("sourceClassSimpleName", sourceClassSimpleName());
    vars.put("classSimpleName", context.generatedClassSimpleName());
    vars.put("typeParamsFullDeclaration", typeParamsFullDeclaration);
    vars.put("typeParamsBriefDeclaration", typeParamsBriefDeclaration);
    vars.put("importedClasses", context.getImports());
    vars.put("projectionDefinitions", projectionDefinitions);
    vars.put("injections", injections);
    vars.put("injectionMethods", injectionMethods);
    vars.put("guideActions", guideActions);
    vars.put("overrideGuideMethods", overrideGuideMethods);
    vars.put("guideActionMethods", guideActionMethods);
    return vars;
  }

  @Override
  protected boolean analyzeAnnotatedType(RoundEnvironment roundEnv) {
    context.generatedClassCanonicalName(artifactName());
    if (annotatedType.isNested()) {
      context.addImport(sourceClassCanonicalName());
    }

    context.addImport(List.class);
    context.addImport(Collections.class);
    context.addImport(ArrayList.class);
    context.addImport(Action.class);
    context.addImport(Actions.class);
    context.addImport(FunctionActions.class);
    context.addImport(ResettableGetter.class);
    context.addImport(KernelUnit.class);
    context.addImport(Wrapper.class);
    context.addImport(Ordinal.class);
    context.addImport(Modules.class);
    context.addImport(Injection.class);
    context.addImport(ProjectionRegistry.class);
    context.addImport(TypeFunctions.class);
    context.addImport(UnitWrapper.class);
    context.addImport(ProjectionReferences.class);
    context.addImport(ProjectionInjections.class);
    context.addImport(GuideInjections.class);
    context.addImport(AutoGuideInjections.class);
    context.addImport(MathFunctions.class);

    declaredMethods = annotatedType.declaredMethods();
    analyzeTypeParams();
    analyzeMethods();
    analyzeGuideMethods();
    analyzeGuideActions();
    return true;
  }

  private void analyzeTypeParams() {
    typeParamsFullDeclaration = annotatedType.typeParametersFullDeclaration();
    typeParamsBriefDeclaration = annotatedType.typeParametersBriefDeclaration();
  }

  private void analyzeGuideMethods() {
    this.overrideGuideMethods = declaredMethods.stream()
        .filter(GuideFunctions::isGuideMethod)
        .map(this::buildGuideMethod)
        .toList();
  }

  private Map<String, Object> buildGuideMethod(MethodStatement guideMethod) {
    var sb = new StringBuilder();
    sb.append("public ");
    sb.append(buildMethodSignature(guideMethod));
    sb.append(" {\n");
    sb.append("  return super.");
    sb.append(guideMethod.name());
    sb.append("(");
    Runner commaAppender = TextActions.skippingFirstTimeCommaAppender(sb);
    for (MethodParam param : guideMethod.params()) {
      commaAppender.run();
      sb.append(param.name());
    }
    sb.append(");\n}");
    return Map.of("declaration", sb.toString());
  }

  private void analyzeGuideActions() {
    declaredMethods.stream()
        .filter(GuideFunctions::isGuideMethod)
        .forEach(method -> {
          guideActions.add(GuideProcessorFunctions.buildGuideAction(artifactName(), method, context));
          guideActionMethods.add((GuideProcessorFunctions.buildGuideActionMethod(method, context)));
      });
  }

  private String buildTypeDeclaration(TypeReference type) {
    if (type.isPrimitiveReference()) {
      return type.asPrimitiveReferenceOrElseThrow().typename();
    } else if (type.isNamedReference()) {
      NamedReference namedType = type.asNamedReferenceOrElseThrow();
      if (namedType.extendedBounds().isEmpty()) {
        return "Object";
      } else {
        return buildTypeDeclaration(namedType.extendedBounds().get(0));
      }
    }
    return context.addToImportAndGetSimpleName(
        type.asCustomTypeReferenceOrElseThrow().targetType().canonicalName()
    );
  }

  private void analyzeMethods() {
    for (MethodStatement method : declaredMethods) {
      if (method.isAbstract()) {
        if (isProjectionMethod(method)) {
          if (!isReturnObjectHandle(method)) {
            throw ConfigurationException.withMessage("Projection method ''{0}'' in class {1} must return object handle",
                method.name(), annotatedType.className()
            );
          }
          addProjectionInjectionAndImplementationMethod(method);
          addProjectionDefinitionForAbstractMethod(method);
        } else if (isInjectionMethod(method)) {
          if (isAutoGuideMethod(method)) {
            if (!isReturnGuide(method)) {
              throw ConfigurationException.withMessage("Guide injection method ''{0}'' in class {1} must return guide",
                  method.name(), annotatedType.className()
              );
            }
            addAutoGuideInjectionAndImplementationMethod(method);
          } else if (isReturnGuide(method)) {
            addGuideInjectionAndImplementationMethod(method);
          } else {
            addProjectionInjectionAndImplementationMethod(method);
          }
        } else {
          throw ConfigurationException.withMessage("Undefined abstract method ''{0}'' in class {1}",
              method.name(), annotatedType.className()
          );
        }
      } else {
        if (isProjectionMethod(method)) {
          addProjectionInjectionAndImplementationMethod(method);
          addProjectionDefinitionBasedOnMethodAction(method);
        }
      }
    }
  }

  private void addProjectionDefinitionForAbstractMethod(MethodStatement method) {
    for (AnnotationInstance annotation : method.annotations()) {
      Optional<AnnotationInstance> projectionDefinitionAnnotation = annotation.annotationStatement().selectAnnotation(
          ProjectionDefinition.class.getCanonicalName());
      if (projectionDefinitionAnnotation.isPresent()) {
        AnnotationInstance projectionDefinition = projectionDefinitionAnnotation.get();
        Optional<Instance> providerClass = projectionDefinition.elementValue("provider");
        if (providerClass.isPresent() && isProjectionProviderDefined(providerClass.get())) {
          addProjectionDefinitionBasedOnProviderClass(method, providerClass.get().asClass().orElseThrow());
          return;
        }
      }
    }
    throw UnexpectedViolationException.withMessage("Projection definition is not found. See method {0} in unit {1}",
        method.name(), method.owner().canonicalName());
  }

  private static boolean isProjectionProviderDefined(Instance providerClass) {
    return !ProjectionTargetSupplier.class.getCanonicalName().equals(
        providerClass.asClass().orElseThrow().type().canonicalName()
    );
  }

  private void addProjectionDefinitionBasedOnProviderClass(
      MethodStatement method, ClassInstance projectionProvider
  ) {
    var sb = new StringBuilder();
    sb.append(context.addToImportAndGetSimpleName(ProjectionDefinitionBasedOnProviderClasses.class)).append(".get(\n");
    sb.append("  ").append(annotatedType.simpleName()).append(".class,\n");
    sb.append("  \"").append(ProjectionFunctions.getProjectionName(method)).append("\",\n");
    sb.append("  ").append(buildTypeDeclaration(method.returnType().orElseThrow())).append(".class,\n");
    sb.append("  ").append(method.selectAnnotation(Projection.class).orElseThrow().lazy()).append(",\n");
    sb.append("  \"").append(projectionProvider.type().canonicalName()).append("\"");
    sb.append(")");
    projectionDefinitions.add(sb.toString());
  }

  private void addProjectionDefinitionBasedOnMethodAction(MethodStatement method) {
    var sb = new StringBuilder();
    sb.append(context.addToImportAndGetSimpleName(ProjectionDefinitionBasedOnMethodActions.class)).append(".get(\n");
    sb.append("  ").append(annotatedType.simpleName()).append(".class,\n");
    sb.append("  \"").append(ProjectionFunctions.getProjectionName(method)).append("\",\n");
    sb.append("  ").append(buildTypeDeclaration(method.returnType().orElseThrow())).append(".class,\n");
    sb.append("  ").append(method.selectAnnotation(Projection.class).orElseThrow().lazy()).append(",\n");
    sb.append("  ").append("Actions.of(super::").append(method.name()).append(", ");
    sb.append(buildTypeDeclaration(method.returnType().orElseThrow())).append(".class");

    for (MethodParam param : method.params()) {
      sb.append(", ");
      sb.append(buildTypeDeclaration(param.type())).append(".class");
    }
    sb.append(")");

    for (MethodParam param : method.params()) {
      sb.append(",\n  \"");
      sb.append(param.name());
      sb.append("\", ");
      sb.append(buildTypeDeclaration(param.type())).append(".class");
    }
    sb.append("\n)");
    projectionDefinitions.add(sb.toString());
  }

  private void addProjectionInjectionAndImplementationMethod(MethodStatement method) {
    context.addImport(Modules.class);
    context.addImport(ProjectionInjection.class);

    String injectionName = method.name();
    String injectionType = method.returnType().orElseThrow().actualDeclaration();

    Map<String, Object> injection = new HashMap<>();
    injection.put("kind", "projection");
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
    return "return (" + injectionType + ") this.$unit.injection(" + injectionIndex + ").value();";
  }

  private boolean isProjectionMethod(MethodStatement method) {
    return method.hasAnnotation(Projection.class);
  }

  private boolean isInjectionMethod(MethodStatement method) {
    return method.hasAnnotation(Inject.class);
  }

  private boolean isAutoGuideMethod(MethodStatement method) {
    return method.hasAnnotation(AutoGuide.class);
  }

  private boolean isReturnObjectHandle(MethodStatement method) {
    return ObjectFunctions.isObjectHandleType(method.returnType().orElseThrow());
  }

  private boolean isReturnGuide(MethodStatement method) {
    return GuideFunctions.isGuideType(method.returnType().orElseThrow());
  }
}
