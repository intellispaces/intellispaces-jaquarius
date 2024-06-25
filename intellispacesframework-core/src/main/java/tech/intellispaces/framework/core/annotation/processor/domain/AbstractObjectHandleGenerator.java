package tech.intellispaces.framework.core.annotation.processor.domain;

import tech.intellispaces.framework.core.annotation.Transition;
import tech.intellispaces.framework.core.annotation.processor.AbstractGenerator;
import tech.intellispaces.framework.core.common.ActionFunctions;
import tech.intellispaces.framework.commons.action.Action;
import tech.intellispaces.framework.core.exception.TraverseException;
import tech.intellispaces.framework.core.object.ObjectHandleTypes;
import tech.intellispaces.framework.core.space.transition.TransitionFunctions;
import tech.intellispaces.framework.core.traverse.TraverseTypes;
import tech.intellispaces.framework.javastatements.statement.custom.CustomType;
import tech.intellispaces.framework.javastatements.statement.custom.MethodParam;
import tech.intellispaces.framework.javastatements.statement.custom.MethodStatement;
import tech.intellispaces.framework.javastatements.statement.reference.TypeReference;

import java.util.List;
import java.util.stream.Collectors;

abstract class AbstractObjectHandleGenerator extends AbstractGenerator {
  protected String domainTypeParamsFull;
  protected String domainTypeParamsBrief;
  protected List<String> methods;

  AbstractObjectHandleGenerator(CustomType domainType) {
    super(domainType);
  }

  abstract protected ObjectHandleTypes getObjectHandleType();

  protected void analyzeObjectHandleMethods(CustomType domainType) {
    this.methods = domainType.declaredMethods().stream()
        .map(this::buildMethod)
        .toList();
  }

  private String buildMethod(MethodStatement method) {
    var sb = new StringBuilder();
    if (method.returnType().isEmpty()) {
      sb.append("void");
    } else {
      Transition transition = method.selectAnnotation(Transition.class).orElseThrow();
      boolean disableMoving = transition.allowedTraverse() != TraverseTypes.Mapping &&
          ObjectHandleTypes.Unmovable == getObjectHandleType();
      if (disableMoving) {
        sb.append("default ");
      }

      TypeReference domainReturnType = method.returnType().get();
      sb.append(getObjectHandleCanonicalName(domainReturnType, getObjectHandleType()));
      sb.append(" ");
      sb.append(method.name());
      sb.append("(");

      Action addCommaAction = ActionFunctions.buildAppendSeparatorAction(sb, ", ");
      for (MethodParam param : method.params()) {
        addCommaAction.execute();
        sb.append(getObjectHandleCanonicalName(param.type(), getObjectHandleType()));
        sb.append(" ");
        sb.append(param.name());
      }
      sb.append(")");

      String exceptions = method.exceptions().stream()
          .map(e -> e.asCustomTypeReference().orElseThrow().targetType())
          .peek(e -> context.addImport(e.canonicalName()))
          .map(e -> context.simpleNameOf(e.canonicalName()))
          .collect(Collectors.joining(", "));
      if (!exceptions.isEmpty()) {
        sb.append(" throws ").append(exceptions);
      }

      if (disableMoving) {
        context.addImport(TraverseException.class);
        String exceptionSimpleName = context.simpleNameOf(TraverseException.class);
        sb.append(" {\n");
        sb.append("    throw ").append(exceptionSimpleName).append(".withMessage(\"Unmovable object handle cannot be moved\");\n");
        sb.append("  }");
      }
    }
    return sb.toString();
  }
}
