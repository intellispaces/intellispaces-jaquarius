package tech.intellispaces.reflections.framework.guide.n4;

import tech.intellispaces.commons.exception.UnexpectedExceptions;
import tech.intellispaces.reflections.framework.exception.TraverseException;
import tech.intellispaces.reflections.framework.exception.TraverseExceptions;
import tech.intellispaces.reflections.framework.guide.GuideLogger;
import tech.intellispaces.reflections.framework.object.reference.ObjectReferenceForm;
import tech.intellispaces.reflections.framework.system.UnitGuide;
import tech.intellispaces.reflections.framework.system.UnitWrapper;
import tech.intellispaces.jstatements.method.MethodStatement;

abstract class UnitGuide4<S, R, Q1, Q2, Q3, Q4> implements Guide4<S, R, Q1, Q2, Q3, Q4>, UnitGuide<S, R> {
  private final String cid;
  private final UnitWrapper unit;
  private final MethodStatement guideMethod;
  private final int guideOrdinal;
  private final ObjectReferenceForm targetForm;

  UnitGuide4(
      String cid,
      UnitWrapper unit,
      MethodStatement guideMethod,
      int guideOrdinal,
      ObjectReferenceForm targetForm
  ) {
    if (guideMethod.params().size() != 5) {
      throw UnexpectedExceptions.withMessage("Guide method should have four parameters: source and four qualifiers");
    }
    this.cid = cid;
    this.unit = unit;
    this.guideMethod = guideMethod;
    this.guideOrdinal = guideOrdinal;
    this.targetForm = targetForm;
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
  public String channelId() {
    return cid;
  }

  @Override
  public ObjectReferenceForm targetForm() {
    return targetForm;
  }

  @Override
  @SuppressWarnings("unchecked")
  public R traverse(S source, Q1 qualifier1, Q2 qualifier2, Q3 qualifier3, Q4 qualifier4) throws TraverseException {
    try {
      GuideLogger.logCallGuide(guideMethod);
      return (R) unit.$broker().guideAction(guideOrdinal).castToAction5().execute(
          source, qualifier1, qualifier2, qualifier3, qualifier4
      );
    } catch (TraverseException e) {
      throw e;
    } catch (Exception e) {
      throw TraverseExceptions.withCauseAndMessage(e, "Failed to invoke unit guide {0} in unit {1}",
          guideMethod.name(), guideMethod.owner().canonicalName());
    }
  }
}
