package intellispaces.framework.core.traverse;

import intellispaces.common.base.exception.UnexpectedViolationException;
import intellispaces.framework.core.exception.TraverseException;

public class MoveObjectHandleThruTransition0PlanImpl extends AbstractObjectHandleTraversePlan
    implements MoveObjectHandleThruTransition0Plan
{
  public MoveObjectHandleThruTransition0PlanImpl(Class<?> objectHandleClass, String tid) {
    super(objectHandleClass, tid);
  }

  @Override
  public TraversePlanType type() {
    return TraversePlanTypes.MoveObjectHandleThruTransition0;
  }

  @Override
  public Object execute(Object source, TraverseExecutor traverseExecutor) throws TraverseException {
    return traverseExecutor.execute(this, source);
  }

  @Override
  public Object execute(Object source, Object qualifier, TraverseExecutor traverseExecutor) {
    throw UnexpectedViolationException.withMessage("Expected traverse with no qualifier");
  }

  @Override
  public Object execute(
      Object source, Object qualifier1, Object qualifier2, TraverseExecutor executor
  ) throws TraverseException {
    throw UnexpectedViolationException.withMessage("Expected traverse with no qualifier");
  }

  @Override
  public Object execute(
    Object source, Object qualifier1, Object qualifier2, Object qualifier3, TraverseExecutor executor
  ) throws TraverseException {
    throw UnexpectedViolationException.withMessage("Expected traverse with no qualifier");
  }
}