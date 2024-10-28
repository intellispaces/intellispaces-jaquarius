package intellispaces.jaquarius.guide.n3;

import intellispaces.common.base.exception.UnexpectedViolationException;
import intellispaces.jaquarius.exception.TraverseException;
import intellispaces.jaquarius.guide.GuideForm;
import intellispaces.jaquarius.guide.GuideLogger;
import intellispaces.jaquarius.system.UnitWrapper;
import intellispaces.jaquarius.system.kernel.KernelUnitGuide;

import java.lang.reflect.Method;

abstract class UnitGuide3<S, R, Q1, Q2, Q3> implements Guide3<S, R, Q1, Q2, Q3>, KernelUnitGuide<S, R> {
  private final String cid;
  private final UnitWrapper unitInstance;
  private final Method guideMethod;
  private final int guideOrdinal;
  private final GuideForm guideForm;

  UnitGuide3(String cid, UnitWrapper unitInstance, Method guideMethod, int guideOrdinal, GuideForm guideForm) {
    if (guideMethod.getParameterCount() != 4) {
      throw UnexpectedViolationException.withMessage("Guide method should have four parameters: source and three qualifiers");
    }
    this.cid = cid;
    this.unitInstance = unitInstance;
    this.guideMethod = guideMethod;
    this.guideOrdinal = guideOrdinal;
    this.guideForm = guideForm;
  }

  @Override
  public Method guideMethod() {
    return guideMethod;
  }

  @Override
  public int guideOrdinal() {
    return guideOrdinal;
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
  public R traverse(S source, Q1 qualifier1, Q2 qualifier2, Q3 qualifier3) throws TraverseException {
    try {
      GuideLogger.logCallGuide(guideMethod);
      return (R) unitInstance.$unit().getGuideAction(guideOrdinal).asAction5().execute(
          unitInstance, source, qualifier1, qualifier2, qualifier3
      );
    } catch (TraverseException e) {
      throw e;
    } catch (Exception e) {
      throw TraverseException.withCauseAndMessage(e, "Failed to invoke unit guide {0} in unit {1}",
          guideMethod.getName(), guideMethod.getDeclaringClass().getCanonicalName());
    }
  }
}
