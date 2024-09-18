package intellispaces.framework.core.traverse;

import intellispaces.common.base.exception.UnexpectedViolationException;
import intellispaces.framework.core.exception.TraverseException;

public class MoveObjectHandleThruTransition3PlanImpl extends AbstractObjectHandleTraversePlan
    implements MoveObjectHandleThruTransition3Plan
{
  public MoveObjectHandleThruTransition3PlanImpl(Class<?> objectHandleClass, String tid) {
    super(objectHandleClass, tid);
  }

  @Override
  public TraversePlanType type() {
    return TraversePlanTypes.MoveObjectHandleThruTransition3;
  }

  @Override
  public Object execute(Object source, TraverseExecutor traverseExecutor) {
    throw UnexpectedViolationException.withMessage("Expected traverse with three qualifiers");
  }

  @Override
  public Object execute(Object source, Object qualifier, TraverseExecutor traverseExecutor) throws TraverseException {
    throw UnexpectedViolationException.withMessage("Expected traverse with three qualifiers");
  }

  @Override
  public Object execute(
    Object source, Object qualifier1, Object qualifier2, TraverseExecutor executor
  ) throws TraverseException {
    throw UnexpectedViolationException.withMessage("Expected traverse with three qualifiers");
  }

  @Override
  public Object execute(
      Object source, Object qualifier1, Object qualifier2, Object qualifier3, TraverseExecutor traverseExecutor
  ) throws TraverseException {
    return traverseExecutor.execute(this, source, qualifier1, qualifier2, qualifier3);
  }
}
