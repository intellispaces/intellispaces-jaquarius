package tech.intellispaces.framework.core.guide.n1;

import tech.intellispaces.framework.commons.exception.UnexpectedViolationException;
import tech.intellispaces.framework.core.exception.TraverseException;
import tech.intellispaces.framework.core.guide.GuideLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Attached to object handle mover guide.<p/>
 *
 * Attached guide can be used exclusively with this object handle only.
 *
 * @param <S> source object handle type.
 * @param <B> backward object handle type.
 * @param <Q> qualified object handle type.
 */
public class AttachedMover1<S, B, Q> implements BasicMover1<S, B, Q> {
  private final Class<S> objectHandleClass;
  private final String tid;
  private final Method moverMethod;
  private final Method actualMoverMethod;

  public AttachedMover1(String tid, Class<S> objectHandleClass, Method moverMethod, Method actualMoverMethod) {
    if (moverMethod.getParameterCount() != 1) {
      throw UnexpectedViolationException.withMessage("Attached guide should have 1 parameter");
    }
    this.tid = tid;
    this.objectHandleClass = objectHandleClass;
    this.moverMethod = moverMethod;
    this.actualMoverMethod = actualMoverMethod;
  }

  @Override
  public String tid() {
    return tid;
  }

  @Override
  @SuppressWarnings("unchecked")
  public B move(S source, Q qualifier) throws TraverseException {
    try {
      GuideLogger.logCallGuide(moverMethod);
      return (B) actualMoverMethod.invoke(source, qualifier);
    } catch (InvocationTargetException e) {
      Throwable t = e.getTargetException();
      if (t instanceof TraverseException) {
        throw (TraverseException) t;
      } else {
        throw TraverseException.withCauseAndMessage(e, "Failed to invoke attached mover method {} of object handle {}",
            actualMoverMethod.getName(), objectHandleClass.getCanonicalName());
      }
    } catch (IllegalAccessException | IllegalArgumentException e) {
      throw TraverseException.withCauseAndMessage(e, "Failed to invoke attached mover method {} of object handle {}",
          actualMoverMethod.getName(), objectHandleClass.getCanonicalName());
    }
  }
}