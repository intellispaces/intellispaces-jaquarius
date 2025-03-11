package tech.intellispaces.jaquarius.guide.n3;

import tech.intellispaces.commons.java.reflection.method.MethodStatement;
import tech.intellispaces.jaquarius.object.reference.ObjectForm;
import tech.intellispaces.jaquarius.system.UnitWrapper;

/**
 * Unit method mapper with three qualifier.
 *
 * @param <S> source object handle type.
 * @param <T> target object handle type.
 * @param <Q1> first qualifier object handle type.
 * @param <Q2> second qualifier object handle type.
 * @param <Q3> third  qualifier object handle type.
 */
public class UnitMapper3<S, T, Q1, Q2, Q3>
    extends UnitGuide3<S, T, Q1, Q2, Q3>
    implements AbstractMapper3<S, T, Q1, Q2, Q3>
{
  public UnitMapper3(
      String cid,
      UnitWrapper unitInstance,
      MethodStatement guideMethod,
      int guideOrdinal,
      ObjectForm targetForm
  ) {
    super(cid, unitInstance, guideMethod, guideOrdinal, targetForm);
  }
}
