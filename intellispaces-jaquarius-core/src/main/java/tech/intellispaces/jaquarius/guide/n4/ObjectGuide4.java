package tech.intellispaces.jaquarius.guide.n4;

import tech.intellispaces.jaquarius.exception.TraverseException;
import tech.intellispaces.jaquarius.exception.TraverseExceptions;
import tech.intellispaces.jaquarius.guide.GuideForm;
import tech.intellispaces.jaquarius.guide.GuideLogger;
import tech.intellispaces.jaquarius.system.ObjectHandleWrapper;
import tech.intellispaces.entity.exception.UnexpectedExceptions;
import tech.intellispaces.java.reflection.method.MethodStatement;
import tech.intellispaces.java.reflection.method.MethodParam;

import java.util.stream.Collectors;

abstract class ObjectGuide4<S extends ObjectHandleWrapper, R, Q1, Q2, Q3, Q4> implements Guide4<S, R, Q1, Q2, Q3, Q4> {
  private final Class<S> objectHandleClass;
  private final String cid;
  private final MethodStatement guideMethod;
  private final int channelIndex;
  private final GuideForm guideForm;

  ObjectGuide4(
      String cid,
      Class<S> objectHandleClass,
      MethodStatement guideMethod,
      int channelIndex,
      GuideForm guideForm
  ) {
    if (guideMethod.params().size() != 4) {
      throw UnexpectedExceptions.withMessage("Guide should have four qualifiers");
    }
    this.cid = cid;
    this.objectHandleClass = objectHandleClass;
    this.guideMethod = guideMethod;
    this.channelIndex = channelIndex;
    this.guideForm = guideForm;
  }

  @Override
  public String cid() {
    return cid;
  }

  @Override
  public GuideForm guideForm() {
    return guideForm;
  }

  @Override
  @SuppressWarnings("unchecked")
  public R traverse(S source, Q1 qualifier1, Q2 qualifier2, Q3 qualifier3, Q4 qualifier4) throws TraverseException {
    try {
      GuideLogger.logCallGuide(guideMethod);
      return (R) source.$innerHandle().getGuideAction(channelIndex).castToAction5().execute(
          source, qualifier1, qualifier2, qualifier3, qualifier4
      );
    } catch (TraverseException e) {
      throw e;
    } catch (Exception e) {
      throw TraverseExceptions.withCauseAndMessage(e, "Failed to invoke guide method '{0}' of object handle {1}",
          guideMethod.name(), objectHandleClass.getCanonicalName());
    }
  }

  @Override
  public String toString() {
    return "ObjectGuide4{" +
        "objectHandleClass=" + objectHandleClass +
        ", cid='" + cid + '\'' +
        ", guideMethod=" + guideMethod.name() + "(" + guideMethod.params().stream().map(MethodParam::name).collect(Collectors.joining(", ")) + ")" +
        ", guideForm=" + guideForm +
        '}';
  }
}