package tech.intellispaces.core.annotation.processor.objecthandle;

import tech.intellispaces.actions.Actions;
import tech.intellispaces.core.annotation.Ordinal;
import tech.intellispaces.core.exception.TraverseException;
import tech.intellispaces.core.guide.n0.Mover0;
import tech.intellispaces.core.guide.n1.Mover1;
import tech.intellispaces.core.object.ObjectFunctions;
import tech.intellispaces.core.object.ObjectHandleTypes;
import tech.intellispaces.core.space.transition.Transition0;
import tech.intellispaces.core.space.transition.Transition1;
import tech.intellispaces.core.space.transition.TransitionFunctions;
import tech.intellispaces.core.system.Modules;
import tech.intellispaces.core.system.ObjectHandleWrapper;
import tech.intellispaces.core.system.shadow.ShadowModules;
import tech.intellispaces.core.system.shadow.ShadowObjectHandle;
import tech.intellispaces.core.transition.TransitionMethod0;
import tech.intellispaces.core.transition.TransitionMethod1;
import tech.intellispaces.javastatements.customtype.CustomType;
import tech.intellispaces.javastatements.type.Type;

import javax.annotation.processing.RoundEnvironment;
import java.util.HashMap;
import java.util.Map;

public class MovableObjectHandleImplGenerator extends AbstractObjectHandleImplGenerator {

  public MovableObjectHandleImplGenerator(CustomType objectHandleType) {
    super(objectHandleType);
  }

  @Override
  public String getArtifactName() {
    return getGeneratedClassCanonicalName();
  }

  @Override
  protected String templateName() {
    return "/movable_object_handle_impl.template";
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
    vars.put("constructors", constructors);
    vars.put("importedClasses", context.getImports());
    vars.put("transitionActions", transitionActions);
    vars.put("guideActions", guideActions);
    vars.put("domainMethods", methods);
    return vars;
  }

  @Override
  protected ObjectHandleTypes getObjectHandleType() {
    return ObjectHandleTypes.Movable;
  }

  @Override
  protected boolean analyzeAnnotatedType(RoundEnvironment roundEnv) {
    context.generatedClassCanonicalName(getGeneratedClassCanonicalName());

    context.addImport(Modules.class);
    context.addImport(ShadowModules.class);
    context.addImport(TraverseException.class);
    context.addImport(Actions.class);
    context.addImport(Type.class);
    context.addImport(Ordinal.class);
    context.addImport(ObjectHandleWrapper.class);
    context.addImport(ShadowObjectHandle.class);

    context.addImport(Mover0.class);
    context.addImport(Mover1.class);
    context.addImport(Transition0.class);
    context.addImport(Transition1.class);
    context.addImport(TransitionMethod0.class);
    context.addImport(TransitionMethod1.class);
    context.addImport(TransitionFunctions.class);

    CustomType domainType = ObjectFunctions.getDomainTypeOfObjectHandle(annotatedType);
    context.addImport(domainType.canonicalName());

    domainSimpleClassName = context.simpleNameOf(domainType.canonicalName());

    analyzeTypeParams(annotatedType);
    analyzeConstructors(annotatedType);
    analyzeGuideActions(annotatedType);
    analyzeTransitionActions();
    analyzeObjectHandleMethods(annotatedType, roundEnv);
    return true;
  }
}
