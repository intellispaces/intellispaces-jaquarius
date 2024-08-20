package tech.intellispaces.core.guide.n1;

import tech.intellispaces.commons.exception.UnexpectedViolationException;
import tech.intellispaces.core.exception.TraverseException;
import tech.intellispaces.core.guide.GuideLogger;
import tech.intellispaces.core.system.UnitWrapper;

import java.lang.reflect.Method;

/**
 * Unit method mapper with one qualifier.
 *
 * @param <S> source object handle type.
 * @param <T> target object handle type.
 * @param <Q> qualifier object handle type.
 */
public class UnitMapper1<S, T, Q> implements AbstractMapper1<S, T, Q> {
  private final String tid;
  private final UnitWrapper unitInstance;
  private final Method guideMethod;
  private final int guideActionIndex;

  public UnitMapper1(String tid, UnitWrapper unitInstance, Method guideMethod, int guideActionIndex) {
    if (guideMethod.getParameterCount() != 2) {
      throw UnexpectedViolationException.withMessage("Guide method should have two parameters: source and qualifier");
    }
    this.tid = tid;
    this.unitInstance = unitInstance;
    this.guideMethod = guideMethod;
    this.guideActionIndex = guideActionIndex;
  }

  @Override
  public String tid() {
    return tid;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T map(S source, Q qualifier) throws TraverseException {
    try {
      GuideLogger.logCallGuide(guideMethod);
      return (T) unitInstance.$shadowUnit().getGuideAction(guideActionIndex).asAction2().execute(source, qualifier);
    } catch (TraverseException e) {
      throw e;
    } catch (Exception e) {
      throw TraverseException.withCauseAndMessage(e, "Failed to invoke unit guide {} in unit {}",
          guideMethod.getName(), guideMethod.getDeclaringClass().getCanonicalName());
    }
  }
}