package intellispaces.jaquarius.guide.n0;

import intellispaces.common.javastatement.method.MethodStatement;
import intellispaces.jaquarius.guide.GuideForm;
import intellispaces.jaquarius.system.ObjectHandleWrapper;

/**
 * Attached to object handle mapper guide.<p/>
 *
 * Attached guide can be used exclusively with this object handle only.
 *
 * @param <S> mapper source object type.
 */
public class ObjectMapper0<S extends ObjectHandleWrapper, T>
    extends ObjectGuide0<S, T>
    implements AbstractMapper0<S, T>
{
  public ObjectMapper0(
      String cid,
      Class<S> objectHandleClass,
      MethodStatement guideMethod,
      int channelIndex,
      GuideForm guideForm
  ) {
    super(cid, objectHandleClass, guideForm, guideMethod, channelIndex);
  }
}
