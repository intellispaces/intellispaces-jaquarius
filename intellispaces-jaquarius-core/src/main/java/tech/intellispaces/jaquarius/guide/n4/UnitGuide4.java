package tech.intellispaces.jaquarius.guide.n4;

import tech.intellispaces.jaquarius.exception.TraverseException;
import tech.intellispaces.jaquarius.exception.TraverseExceptions;
import tech.intellispaces.jaquarius.guide.GuideForm;
import tech.intellispaces.jaquarius.guide.GuideLogger;
import tech.intellispaces.jaquarius.system.UnitWrapper;
import tech.intellispaces.jaquarius.system.kernel.KernelUnitGuide;
import tech.intellispaces.entity.exception.UnexpectedExceptions;
import tech.intellispaces.java.reflection.method.MethodStatement;

abstract class UnitGuide4<S, R, Q1, Q2, Q3, Q4> implements Guide4<S, R, Q1, Q2, Q3, Q4>, KernelUnitGuide<S, R> {
  private final String cid;
  private final UnitWrapper unitInstance;
  private final MethodStatement guideMethod;
  private final int guideOrdinal;
  private final GuideForm guideForm;

  UnitGuide4(String cid, UnitWrapper unitInstance, MethodStatement guideMethod, int guideOrdinal, GuideForm guideForm) {
    if (guideMethod.params().size() != 5) {
      throw UnexpectedExceptions.withMessage("Guide method should have four parameters: source and four qualifiers");
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
  public R traverse(S source, Q1 qualifier1, Q2 qualifier2, Q3 qualifier3, Q4 qualifier4) throws TraverseException {
    try {
      GuideLogger.logCallGuide(guideMethod);
      return (R) unitInstance.$unit().getGuideAction(guideOrdinal).castToAction6().execute(
          unitInstance, source, qualifier1, qualifier2, qualifier3, qualifier4
      );
    } catch (TraverseException e) {
      throw e;
    } catch (Exception e) {
      throw TraverseExceptions.withCauseAndMessage(e, "Failed to invoke unit guide {0} in unit {1}",
          guideMethod.name(), guideMethod.owner().canonicalName());
    }
  }
}