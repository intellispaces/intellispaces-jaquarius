package intellispaces.jaquarius.guide.n4;

import intellispaces.jaquarius.exception.TraverseException;
import intellispaces.jaquarius.guide.GuideForm;
import intellispaces.jaquarius.traverse.plan.TraverseExecutor;
import intellispaces.jaquarius.traverse.plan.TraversePlan;

/**
 * Four times parametrized automatic mapper of moving.
 *
 * <p>Automatic guide builds the traverse plan itself.
 *
 * @param <S> source object handle type.
 * @param <T> target object handle type.
 * @param <Q1> first qualifier handle type.
 * @param <Q2> second qualifier handle type.
 * @param <Q3> third qualifier handle type.
 * @param <Q4> third qualifier handle type.
 */
public class AutoMapperOfMoving4<S, T, Q1, Q2,  Q3, Q4> implements AbstractMapperOfMoving4<S, T, Q1, Q2, Q3, Q4> {
  private final String cid;
  private final TraversePlan traversePlan;
  private final GuideForm guideForm;
  private final TraverseExecutor traverseExecutor;

  public AutoMapperOfMoving4(
      String cid, TraversePlan traversePlan, GuideForm guideForm, TraverseExecutor traverseExecutor
  ) {
    this.cid = cid;
    this.traversePlan = traversePlan;
    this.guideForm = guideForm;
    this.traverseExecutor = traverseExecutor;
  }

  @Override
  public String cid() {
    return cid;
  }

  @Override
  public GuideForm guideForm() {
    return guideForm;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T traverse(S source, Q1 qualifier1, Q2 qualifier2, Q3 qualifier3, Q4 qualifier4) throws TraverseException {
    return (T) traversePlan.execute(source, qualifier1, qualifier2, qualifier3, qualifier4, traverseExecutor);
  }
}
