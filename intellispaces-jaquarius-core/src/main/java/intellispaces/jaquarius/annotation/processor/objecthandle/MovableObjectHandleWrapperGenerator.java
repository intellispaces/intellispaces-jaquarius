package intellispaces.jaquarius.annotation.processor.objecthandle;

import intellispaces.common.action.Actions;
import intellispaces.common.action.functional.FunctionActions;
import intellispaces.common.annotationprocessor.context.AnnotationProcessingContext;
import intellispaces.common.base.math.MathFunctions;
import intellispaces.common.base.type.Type;
import intellispaces.common.javastatement.customtype.CustomType;
import intellispaces.common.javastatement.method.MethodStatement;
import intellispaces.common.javastatement.reference.TypeReference;
import intellispaces.jaquarius.annotation.Movable;
import intellispaces.jaquarius.annotation.Ordinal;
import intellispaces.jaquarius.annotation.Unmovable;
import intellispaces.jaquarius.annotation.Wrapper;
import intellispaces.jaquarius.channel.Channel0;
import intellispaces.jaquarius.channel.Channel1;
import intellispaces.jaquarius.channel.ChannelMethod0;
import intellispaces.jaquarius.channel.ChannelMethod1;
import intellispaces.jaquarius.channel.MappingChannel;
import intellispaces.jaquarius.channel.MappingOfMovingChannel;
import intellispaces.jaquarius.common.NameConventionFunctions;
import intellispaces.jaquarius.exception.TraverseException;
import intellispaces.jaquarius.guide.GuideForms;
import intellispaces.jaquarius.guide.n0.Mover0;
import intellispaces.jaquarius.guide.n1.Mover1;
import intellispaces.jaquarius.object.ObjectHandleTypes;
import intellispaces.jaquarius.space.channel.ChannelFunctions;
import intellispaces.jaquarius.system.Modules;
import intellispaces.jaquarius.system.ObjectHandleWrapper;
import intellispaces.jaquarius.system.injection.AutoGuideInjections;
import intellispaces.jaquarius.system.injection.GuideInjections;
import intellispaces.jaquarius.system.kernel.InnerObjectHandle;
import intellispaces.jaquarius.system.kernel.KernelFunctions;
import intellispaces.jaquarius.traverse.TraverseType;

import javax.annotation.processing.RoundEnvironment;
import java.util.HashMap;
import java.util.Map;

public class MovableObjectHandleWrapperGenerator extends AbstractObjectHandleWrapperGenerator {

  public MovableObjectHandleWrapperGenerator(CustomType initiatorType, CustomType objectHandleType) {
    super(initiatorType, objectHandleType);
  }

  @Override
  public boolean isRelevant(AnnotationProcessingContext context) {
    return true;
  }

  @Override
  public String artifactName() {
    return getGeneratedClassCanonicalName();
  }

  @Override
  protected String templateName() {
    return "/movable_object_handle_wrapper.template";
  }

  protected Map<String, Object> templateVariables() {
    Map<String, Object> vars = new HashMap<>();
    vars.put("generatedAnnotation", makeGeneratedAnnotation());
    vars.put("packageName", context.packageName());
    vars.put("sourceClassName", sourceClassCanonicalName());
    vars.put("sourceClassSimpleName", sourceClassSimpleName());
    vars.put("classSimpleName", context.generatedClassSimpleName());
    vars.put("typeParamsFull", typeParamsFull);
    vars.put("typeParamsBrief", typeParamsBrief);
    vars.put("domainClassSimpleName", domainSimpleClassName);
    vars.put("isAlias", isAlias);
    vars.put("primaryDomainSimpleName", primaryDomainSimpleName);
    vars.put("primaryDomainTypeArguments", primaryDomainTypeArguments);
    vars.put("constructors", constructors);
    vars.put("importedClasses", context.getImports());
    vars.put("methodActions", methodActions);
    vars.put("guideActions", guideActions);
    vars.put("guideActionMethods", guideMethods);
    vars.put("domainMethods", methods);
    vars.put("injections", injections);
    vars.put("injectionMethods", injectionMethods);
    vars.put("conversionMethods", conversionMethods);
    vars.put("notImplRelease", !implRelease);
    return vars;
  }

  @Override
  protected ObjectHandleTypes getObjectHandleType() {
    return ObjectHandleTypes.Movable;
  }

  @Override
  protected boolean analyzeAnnotatedType(RoundEnvironment roundEnv) {
    context.generatedClassCanonicalName(artifactName());

    context.addImport(Modules.class);
    context.addImport(KernelFunctions.class);
    context.addImport(TraverseException.class);
    context.addImport(Actions.class);
    context.addImport(FunctionActions.class);
    context.addImport(Type.class);
    context.addImport(Ordinal.class);
    context.addImport(Wrapper.class);
    context.addImport(ObjectHandleWrapper.class);
    context.addImport(InnerObjectHandle.class);
    context.addImport(MathFunctions.class);
    context.addImport(GuideForms.class);

    context.addImport(Mover0.class);
    context.addImport(Mover1.class);
    context.addImport(Channel0.class);
    context.addImport(Channel1.class);
    context.addImport(ChannelMethod0.class);
    context.addImport(ChannelMethod1.class);
    context.addImport(ChannelFunctions.class);
    context.addImport(GuideInjections.class);
    context.addImport(AutoGuideInjections.class);
    context.addImport(MappingChannel.class);
    context.addImport(MappingOfMovingChannel.class);

    analyzeDomain();
    analyzeTypeParams(annotatedType);
    analyzeConstructors(annotatedType);
    analyzeInjectedGuides(annotatedType);
    analyzeObjectHandleMethods(annotatedType, roundEnv);
    analyzeConversionMethods(domainType, roundEnv);
    analyzeReleaseMethod(annotatedType);
    return true;
  }

  protected void appendMethodReturnHandleType(StringBuilder sb, MethodStatement method) {
    TypeReference domainReturnType = method.returnType().orElseThrow();
    if (
        ChannelFunctions.getTraverseTypes(method).stream().anyMatch(TraverseType::isMoving)
          || method.hasAnnotation(Movable.class)
          || NameConventionFunctions.isConversionMethod(method)
    ) {
      sb.append(getObjectHandleDeclaration(domainReturnType, ObjectHandleTypes.Movable));
    } else if (method.hasAnnotation(Unmovable.class)) {
      sb.append(getObjectHandleDeclaration(domainReturnType, ObjectHandleTypes.Unmovable));
    } else {
      sb.append(getObjectHandleDeclaration(domainReturnType, ObjectHandleTypes.Common));
    }
  }
}
