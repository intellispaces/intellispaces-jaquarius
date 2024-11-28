package tech.intellispaces.jaquarius.guide.n2;

import tech.intellispaces.jaquarius.guide.GuideForm;
import tech.intellispaces.jaquarius.system.UnitWrapper;
import tech.intellispaces.java.reflection.method.MethodStatement;

public class UnitMapperOfMoving2<S, T, Q1, Q2> extends UnitGuide2<S, T, Q1, Q2> implements AbstractMapperOfMoving2<S, T, Q1, Q2> {

  public UnitMapperOfMoving2(String cid, UnitWrapper unitInstance, MethodStatement guideMethod, int guideOrdinal, GuideForm guideForm) {
    super(cid, unitInstance, guideMethod, guideOrdinal, guideForm);
  }
}