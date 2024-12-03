package tech.intellispaces.jaquarius.guide.n1;

import tech.intellispaces.general.exception.UnexpectedExceptions;
import tech.intellispaces.jaquarius.exception.TraverseException;
import tech.intellispaces.jaquarius.exception.TraverseExceptions;
import tech.intellispaces.jaquarius.guide.GuideLogger;
import tech.intellispaces.jaquarius.object.reference.ObjectReferenceForm;
import tech.intellispaces.jaquarius.system.UnitWrapper;
import tech.intellispaces.jaquarius.system.kernel.KernelUnitGuide;
import tech.intellispaces.java.reflection.method.MethodStatement;

abstract class UnitGuide1<S, R, Q> implements Guide1<S, R, Q>, KernelUnitGuide<S, R> {
  private final String cid;
  private final UnitWrapper unitInstance;
  private final MethodStatement guideMethod;
  private final int guideOrdinal;
  private final ObjectReferenceForm targetForm;

  UnitGuide1(String cid, UnitWrapper unitInstance, MethodStatement guideMethod, int guideOrdinal, ObjectReferenceForm targetForm) {
    if (guideMethod.params().size() != 2) {
      throw UnexpectedExceptions.withMessage("Guide method should have two parameters: source and qualifier");
    }
    this.cid = cid;
    this.unitInstance = unitInstance;
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
  public ObjectReferenceForm targetForm() {
    return targetForm;
  }

  @Override
  @SuppressWarnings("unchecked")
  public R traverse(S source, Q qualifier) throws TraverseException {
    try {
      GuideLogger.logCallGuide(guideMethod);
      return (R) unitInstance.$unit().getGuideAction(guideOrdinal).castToAction3().execute(unitInstance, source, qualifier);
    } catch (TraverseException e) {
      throw e;
    } catch (Exception e) {
      throw TraverseExceptions.withCauseAndMessage(e, "Failed to invoke unit guide {0} in unit {1}",
          guideMethod.name(), guideMethod.owner().canonicalName());
    }
  }
}
