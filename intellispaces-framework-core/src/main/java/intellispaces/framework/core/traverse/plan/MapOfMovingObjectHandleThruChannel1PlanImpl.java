package intellispaces.framework.core.traverse.plan;

import intellispaces.common.base.exception.UnexpectedViolationException;
import intellispaces.framework.core.exception.TraverseException;

public class MapOfMovingObjectHandleThruChannel1PlanImpl extends AbstractObjectHandleTraversePlan
    implements MapOfMovingObjectHandleThruChannel1Plan
{
  public MapOfMovingObjectHandleThruChannel1PlanImpl(Class<?> objectHandleClass, String cid) {
    super(objectHandleClass, cid);
  }

  @Override
  public TraversePlanType type() {
    return TraversePlanTypes.MapOfMovingObjectHandleThruChannel1;
  }

  @Override
  public Object execute(Object source, TraverseExecutor traverseExecutor) {
    throw UnexpectedViolationException.withMessage("Expected traverse with one qualifier");
  }

  @Override
  public Object execute(Object source, Object qualifier, TraverseExecutor traverseExecutor) throws TraverseException {
    return traverseExecutor.execute(this, source, qualifier);
  }

  @Override
  public Object execute(
      Object source, Object qualifier1, Object qualifier2, TraverseExecutor executor
  ) throws TraverseException {
    throw UnexpectedViolationException.withMessage("Expected traverse with one qualifier");
  }

  @Override
  public Object execute(
    Object source, Object qualifier1, Object qualifier2, Object qualifier3, TraverseExecutor executor
  ) throws TraverseException {
    throw UnexpectedViolationException.withMessage("Expected traverse with one qualifier");
  }
}