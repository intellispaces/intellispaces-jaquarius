package tech.intellispaces.jaquarius.guide.n1;

import tech.intellispaces.commons.exception.UnexpectedExceptions;
import tech.intellispaces.commons.reflection.method.MethodStatement;
import tech.intellispaces.jaquarius.exception.TraverseException;
import tech.intellispaces.jaquarius.exception.TraverseExceptions;
import tech.intellispaces.jaquarius.guide.GuideLogger;
import tech.intellispaces.jaquarius.object.reference.ObjectForm;
import tech.intellispaces.jaquarius.system.UnitGuide;
import tech.intellispaces.jaquarius.system.UnitWrapper;

abstract class UnitGuide1<S, R, Q> implements Guide1<S, R, Q>, UnitGuide<S, R> {
  private final String cid;
  private final UnitWrapper unit;
  private final MethodStatement guideMethod;
  private final int guideOrdinal;
  private final ObjectForm targetForm;

  UnitGuide1(String cid, UnitWrapper unit, MethodStatement guideMethod, int guideOrdinal, ObjectForm targetForm) {
    if (guideMethod.params().size() != 2) {
      throw UnexpectedExceptions.withMessage("Guide method should have two parameters: source and qualifier");
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
  public String cid() {
    return cid;
  }

  @Override
  public ObjectForm targetForm() {
    return targetForm;
  }

  @Override
  @SuppressWarnings("unchecked")
  public R traverse(S source, Q qualifier) throws TraverseException {
    try {
      GuideLogger.logCallGuide(guideMethod);
      return (R) unit.$broker().guideAction(guideOrdinal).castToAction2().execute(source, qualifier);
    } catch (TraverseException e) {
      throw e;
    } catch (Exception e) {
      throw TraverseExceptions.withCauseAndMessage(e, "Failed to invoke unit guide {0} in unit {1}",
          guideMethod.name(), guideMethod.owner().canonicalName());
    }
  }
}
