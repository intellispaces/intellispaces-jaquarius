package tech.intellispaces.jaquarius.guide.n0;

import tech.intellispaces.jaquarius.exception.TraverseException;
import tech.intellispaces.jaquarius.object.reference.ObjectForm;
import tech.intellispaces.jaquarius.traverse.plan.TraverseExecutor;
import tech.intellispaces.jaquarius.traverse.plan.TraversePlan;

/**
 * Not-parametrized automatic mapper of moving.
 *
 * <p>Automatic guide builds the traverse plan itself.
 *
 * @param <S> source object handle type.
 * @param <T> target object handle type.
 */
public class AutoMapperOfMoving0<S, T> implements AbstractMapperOfMoving0<S, T> {
  private final String cid;
  private final TraversePlan traversePlan;
  private final ObjectForm targetForm;
  private final TraverseExecutor traverseExecutor;

  public AutoMapperOfMoving0(
      String cid, TraversePlan traversePlan, ObjectForm targetForm, TraverseExecutor traverseExecutor
  ) {
    this.cid = cid;
    this.traversePlan = traversePlan;
    this.targetForm = targetForm;
    this.traverseExecutor = traverseExecutor;
  }

  @Override
  public String cid() {
    return cid;
  }

  @Override
  public ObjectForm targetForm() {
    return targetForm;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T traverse(S source) throws TraverseException {
    return (T) traversePlan.execute(source, traverseExecutor);
  }

  @Override
  public int traverseToInt(S source) throws TraverseException {
    return traversePlan.executeReturnInt(source, traverseExecutor);
  }

  @Override
  public double traverseToDouble(S source) throws TraverseException {
    return traversePlan.executeReturnDouble(source, traverseExecutor);
  }
}
