package intellispaces.framework.core.traverse.plan;

import intellispaces.common.base.exception.UnexpectedViolationException;
import intellispaces.framework.core.exception.TraverseException;

public class MapOfMovingObjectHandleThruChannel4PlanImpl extends AbstractObjectHandleTraversePlan
    implements MapOfMovingObjectHandleThruChannel4Plan
{
  public MapOfMovingObjectHandleThruChannel4PlanImpl(Class<?> objectHandleClass, String cid) {
    super(objectHandleClass, cid);
  }

  @Override
  public TraversePlanType type() {
    return TraversePlanTypes.MapOfMovingObjectHandleThruChannel4;
  }

  @Override
  public Object execute(Object source, TraverseExecutor executor) {
    throw UnexpectedViolationException.withMessage("Expected traverse with four qualifiers");
  }

  @Override
  public int executeReturnInt(Object source, TraverseExecutor executor) throws TraverseException {
    throw UnexpectedViolationException.withMessage("Expected traverse with four qualifiers");
  }

  @Override
  public double executeReturnDouble(Object source, TraverseExecutor executor) throws TraverseException {
    throw UnexpectedViolationException.withMessage("Expected traverse with four qualifiers");
  }

  @Override
  public Object execute(Object source, Object qualifier, TraverseExecutor executor) throws TraverseException {
    throw UnexpectedViolationException.withMessage("Expected traverse with four qualifiers");
  }

  @Override
  public Object execute(
    Object source, Object qualifier1, Object qualifier2, TraverseExecutor executor
  ) throws TraverseException {
    throw UnexpectedViolationException.withMessage("Expected traverse with four qualifiers");
  }

  @Override
  public Object execute(
      Object source, Object qualifier1, Object qualifier2, Object qualifier3, TraverseExecutor executor
  ) throws TraverseException {
    throw UnexpectedViolationException.withMessage("Expected traverse with four qualifiers");
  }

  @Override
  public Object execute(
      Object source, Object qualifier1, Object qualifier2, Object qualifier3, Object qualifier4, TraverseExecutor executor
  ) throws TraverseException {
    return executor.execute(this, source, qualifier1, qualifier2, qualifier3, qualifier4);
  }
}