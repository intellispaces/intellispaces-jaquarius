package intellispaces.jaquarius.guide.n4;

import intellispaces.common.javastatement.method.MethodStatement;
import intellispaces.jaquarius.guide.GuideForm;
import intellispaces.jaquarius.system.UnitWrapper;

public class UnitMapperOfMoving4<S, T, Q1, Q2, Q3, Q4>
    extends UnitGuide4<S, T, Q1, Q2, Q3, Q4>
    implements AbstractMapperOfMoving4<S, T, Q1, Q2, Q3, Q4>
{
  public UnitMapperOfMoving4(String cid, UnitWrapper unitInstance, MethodStatement guideMethod, int guideOrdinal, GuideForm guideForm) {
    super(cid, unitInstance, guideMethod, guideOrdinal, guideForm);
  }
}
