package tech.intellispaces.reflections.framework.guide.n2;

import tech.intellispaces.reflections.framework.object.reference.ObjectReferenceForm;
import tech.intellispaces.reflections.framework.system.ObjectHandleWrapper;
import tech.intellispaces.jstatements.method.MethodStatement;

/**
 * Attached to object handle mapper guide.<p/>
 *
 * Attached guide can be used exclusively with this object handle only.
 *
 * @param <S> source object type.
 * @param <Q1> first qualifier type.
 * @param <Q2> second qualifier type.
 */
public class ObjectMapper2<S extends ObjectHandleWrapper, T, Q1, Q2>
    extends ObjectGuide2<S, T, Q1, Q2>
    implements AbstractMapper2<S, T, Q1, Q2>
{
  public ObjectMapper2(
      String cid,
      Class<S> objectHandleClass,
      MethodStatement guideMethod,
      int traverseOrdinal,
      ObjectReferenceForm targetForm
  ) {
    super(cid, objectHandleClass, guideMethod, traverseOrdinal, targetForm);
  }
}
