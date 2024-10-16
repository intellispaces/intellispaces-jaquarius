package intellispaces.framework.core.aop;

import intellispaces.common.action.Action;
import intellispaces.common.javastatement.method.MethodStatement;
import intellispaces.framework.core.system.ProjectionProvider;

public abstract class Interceptor extends AbstractMethodAdvice {

  public Interceptor(MethodStatement joinPoint, Action nextAction, ProjectionProvider projectionProvider) {
    super(joinPoint, nextAction, projectionProvider);
  }
}
