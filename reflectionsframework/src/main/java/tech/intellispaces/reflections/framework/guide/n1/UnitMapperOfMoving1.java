package tech.intellispaces.reflections.framework.guide.n1;

import tech.intellispaces.reflections.framework.object.reference.ObjectReferenceForm;
import tech.intellispaces.reflections.framework.system.UnitWrapper;
import tech.intellispaces.jstatements.method.MethodStatement;

public class UnitMapperOfMoving1<S, T, Q> extends UnitGuide1<S, T, Q> implements AbstractMapperOfMoving1<S, T, Q> {

  public UnitMapperOfMoving1(
      String cid,
      UnitWrapper unitInstance,
      MethodStatement guideMethod,
      int guideOrdinal,
      ObjectReferenceForm targetForm
  ) {
    super(cid, unitInstance, guideMethod, guideOrdinal, targetForm);
  }
}
