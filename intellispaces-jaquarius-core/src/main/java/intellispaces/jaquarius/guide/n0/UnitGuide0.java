package intellispaces.jaquarius.guide.n0;

import intellispaces.common.base.exception.UnexpectedViolationException;
import intellispaces.common.javastatement.method.MethodStatement;
import intellispaces.jaquarius.exception.TraverseException;
import intellispaces.jaquarius.guide.GuideForm;
import intellispaces.jaquarius.guide.GuideLogger;
import intellispaces.jaquarius.system.UnitWrapper;
import intellispaces.jaquarius.system.kernel.KernelUnitGuide;

abstract class UnitGuide0<S, R> implements Guide0<S, R>, KernelUnitGuide<S, R> {
  private final String cid;
  private final UnitWrapper unitInstance;
  private final MethodStatement guideMethod;
  private final int guideOrdinal;
  private final GuideForm guideForm;

  UnitGuide0(String cid, UnitWrapper unitInstance, MethodStatement guideMethod, int guideOrdinal, GuideForm guideForm) {
    if (guideMethod.params().size() != 1) {
      throw UnexpectedViolationException.withMessage("Guide method should have one parameter: source");
    }
    this.cid = cid;
    this.unitInstance = unitInstance;
    this.guideMethod = guideMethod;
    this.guideOrdinal = guideOrdinal;
    this.guideForm = guideForm;
  }

  @Override
  public MethodStatement guideMethod() {
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
  public R traverse(S source) throws TraverseException {
    try {
      GuideLogger.logCallGuide(guideMethod);
      return (R) unitInstance.$unit().getGuideAction(guideOrdinal).asAction2().execute(unitInstance, source);
    } catch (TraverseException e) {
      throw e;
    } catch (Exception e) {
      throw TraverseException.withCauseAndMessage(e, "Failed to invoke unit guide {0} in unit {1}",
        guideMethod.name(), guideMethod.owner().canonicalName());
    }
  }

  @Override
  public int traverseToInt(S source) throws TraverseException {
    try {
      GuideLogger.logCallGuide(guideMethod);
      return unitInstance.$unit().getGuideAction(guideOrdinal).asAction2().executeReturnInt(unitInstance, source);
    } catch (TraverseException e) {
      throw e;
    } catch (Exception e) {
      throw TraverseException.withCauseAndMessage(e, "Failed to invoke unit guide {0} in unit {1}",
          guideMethod.name(), guideMethod.owner().canonicalName());
    }
  }

  @Override
  public double traverseToDouble(S source) throws TraverseException {
    try {
      GuideLogger.logCallGuide(guideMethod);
      return unitInstance.$unit().getGuideAction(guideOrdinal).asAction2().executeReturnDouble(unitInstance, source);
    } catch (TraverseException e) {
      throw e;
    } catch (Exception e) {
      throw TraverseException.withCauseAndMessage(e, "Failed to invoke unit guide {0} in unit {1}",
          guideMethod.name(), guideMethod.owner().canonicalName());
    }
  }
}
