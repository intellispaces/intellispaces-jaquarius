package tech.intellispaces.jaquarius.guide.n0;

import tech.intellispaces.jaquarius.guide.GuideForm;
import tech.intellispaces.jaquarius.system.UnitWrapper;
import tech.intellispaces.java.reflection.method.MethodStatement;

/**
 * Non-parameterized unit method mapper.
 *
 * @param <S> source object handle type.
 */
public class UnitMapper0<S, T> extends UnitGuide0<S, T> implements AbstractMapper0<S, T> {

  public UnitMapper0(String cid, UnitWrapper unitInstance, MethodStatement guideMethod, int guideOrdinal, GuideForm guideForm) {
    super(cid, unitInstance, guideMethod, guideOrdinal, guideForm);
  }
}