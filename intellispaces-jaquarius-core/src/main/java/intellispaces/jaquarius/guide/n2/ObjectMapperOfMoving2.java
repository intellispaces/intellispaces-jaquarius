package intellispaces.jaquarius.guide.n2;

import intellispaces.common.javastatement.method.MethodStatement;
import intellispaces.jaquarius.guide.GuideForm;
import intellispaces.jaquarius.system.ObjectHandleWrapper;

/**
 * Attached to object handle mapper related to moving.<p/>
 *
 * Attached guide can be used exclusively with this object handle only.
 *
 * @param <S> source object type.
 * @param <T> target object type.
 * @param <Q1> first qualifier object type.
 * @param <Q2> second qualifier object type.
 */
public class ObjectMapperOfMoving2<S extends ObjectHandleWrapper, T, Q1, Q2>
    extends ObjectGuide2<S, T, Q1, Q2>
    implements AbstractMapperOfMoving2<S, T, Q1, Q2>
{
  public ObjectMapperOfMoving2(
      String cid,
      Class<S> objectHandleClass,
      MethodStatement guideMethod,
      int channelIndex,
      GuideForm guideForm
  ) {
    super(cid, objectHandleClass, guideMethod, channelIndex, guideForm);
  }
}
