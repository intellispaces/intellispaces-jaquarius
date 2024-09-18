package intellispaces.framework.core.system.kernel;

import intellispaces.common.base.exception.UnexpectedViolationException;
import intellispaces.framework.core.guide.Guide;
import intellispaces.framework.core.guide.GuideKind;
import intellispaces.framework.core.guide.GuideKinds;
import intellispaces.framework.core.guide.n0.Guide0;
import intellispaces.framework.core.guide.n1.Guide1;
import intellispaces.framework.core.guide.n2.Guide2;
import intellispaces.framework.core.guide.n3.Guide3;
import intellispaces.framework.core.object.ObjectFunctions;
import intellispaces.framework.core.traverse.CallGuide0PlanImpl;
import intellispaces.framework.core.traverse.CallGuide1PlanImpl;
import intellispaces.framework.core.traverse.CallGuide2PlanImpl;
import intellispaces.framework.core.traverse.CallGuide3PlanImpl;
import intellispaces.framework.core.traverse.ExecutionPlan;
import intellispaces.framework.core.traverse.MapObjectHandleThruTransition0Plan;
import intellispaces.framework.core.traverse.MapObjectHandleThruTransition0PlanImpl;
import intellispaces.framework.core.traverse.MapObjectHandleThruTransition1Plan;
import intellispaces.framework.core.traverse.MapObjectHandleThruTransition1PlanImpl;
import intellispaces.framework.core.traverse.MapObjectHandleThruTransition2Plan;
import intellispaces.framework.core.traverse.MapObjectHandleThruTransition2PlanImpl;
import intellispaces.framework.core.traverse.MapObjectHandleThruTransition3Plan;
import intellispaces.framework.core.traverse.MapObjectHandleThruTransition3PlanImpl;
import intellispaces.framework.core.traverse.MoveObjectHandleThruTransition0Plan;
import intellispaces.framework.core.traverse.MoveObjectHandleThruTransition0PlanImpl;
import intellispaces.framework.core.traverse.MoveObjectHandleThruTransition1Plan;
import intellispaces.framework.core.traverse.MoveObjectHandleThruTransition1PlanImpl;
import intellispaces.framework.core.traverse.MoveObjectHandleThruTransition2Plan;
import intellispaces.framework.core.traverse.MoveObjectHandleThruTransition2PlanImpl;
import intellispaces.framework.core.traverse.MoveObjectHandleThruTransition3Plan;
import intellispaces.framework.core.traverse.MoveObjectHandleThruTransition3PlanImpl;
import intellispaces.framework.core.traverse.ObjectHandleTraversePlan;
import intellispaces.framework.core.traverse.TraverseAnalyzer;
import intellispaces.framework.core.traverse.TraversePlanTypes;

import java.util.List;

class TraverseAnalyzerImpl implements TraverseAnalyzer {
  private final GuideRegistry guideRegistry;

  public TraverseAnalyzerImpl(GuideRegistry guideRegistry) {
    this.guideRegistry = guideRegistry;
  }

  @Override
  public MapObjectHandleThruTransition0Plan buildTraversePlanMapObjectHandleThruTransition0(
      Class<?> objectHandleClass, String tid
  ) {
    MapObjectHandleThruTransition0Plan declarativePlan = new MapObjectHandleThruTransition0PlanImpl(
        objectHandleClass, tid);
    setExecutionPlan(declarativePlan, objectHandleClass);
    return declarativePlan;
  }

  @Override
  public MapObjectHandleThruTransition1Plan buildTraversePlanMapObjectHandleThruTransition1(
      Class<?> objectHandleClass, String tid
  ) {
    MapObjectHandleThruTransition1Plan declarativePlan = new MapObjectHandleThruTransition1PlanImpl(
        objectHandleClass, tid);
    setExecutionPlan(declarativePlan, objectHandleClass);
    return declarativePlan;
  }

  @Override
  public MapObjectHandleThruTransition2Plan buildTraversePlanMapObjectHandleThruTransition2(
      Class<?> objectHandleClass, String tid
  ) {
    MapObjectHandleThruTransition2Plan declarativePlan = new MapObjectHandleThruTransition2PlanImpl(
        objectHandleClass, tid);
    setExecutionPlan(declarativePlan, objectHandleClass);
    return declarativePlan;
  }

  @Override
  public MapObjectHandleThruTransition3Plan buildTraversePlanMapObjectHandleThruTransition3(
    Class<?> objectHandleClass, String tid
  ) {
    MapObjectHandleThruTransition3Plan declarativePlan = new MapObjectHandleThruTransition3PlanImpl(
      objectHandleClass, tid);
    setExecutionPlan(declarativePlan, objectHandleClass);
    return declarativePlan;
  }

  @Override
  public MoveObjectHandleThruTransition0Plan buildTraversePlanMoveObjectHandleThruTransition0(
      Class<?> objectHandleClass, String tid
  ) {
    MoveObjectHandleThruTransition0Plan declarativePlan = new MoveObjectHandleThruTransition0PlanImpl(
        objectHandleClass, tid);
    setExecutionPlan(declarativePlan, objectHandleClass);
    return declarativePlan;
  }

  @Override
  public MoveObjectHandleThruTransition1Plan buildTraversePlanMoveObjectHandleThruTransition1(
      Class<?> objectHandleClass, String tid
  ) {
    MoveObjectHandleThruTransition1Plan declarativePlan = new MoveObjectHandleThruTransition1PlanImpl(
        objectHandleClass, tid);
    setExecutionPlan(declarativePlan, objectHandleClass);
    return declarativePlan;
  }

  @Override
  public MoveObjectHandleThruTransition2Plan buildTraversePlanMoveObjectHandleThruTransition2(
      Class<?> objectHandleClass, String tid
  ) {
    MoveObjectHandleThruTransition2Plan declarativePlan = new MoveObjectHandleThruTransition2PlanImpl(
        objectHandleClass, tid);
    setExecutionPlan(declarativePlan, objectHandleClass);
    return declarativePlan;
  }

  @Override
  public MoveObjectHandleThruTransition3Plan buildTraversePlanMoveObjectHandleThruTransition3(
    Class<?> objectHandleClass, String tid
  ) {
    MoveObjectHandleThruTransition3Plan declarativePlan = new MoveObjectHandleThruTransition3PlanImpl(
      objectHandleClass, tid);
    setExecutionPlan(declarativePlan, objectHandleClass);
    return declarativePlan;
  }

  private void setExecutionPlan(ObjectHandleTraversePlan plan, Class<?> objectHandleClass) {
    ExecutionPlan executionPlan = getExecutionTraversePlan(plan, objectHandleClass);
    if (executionPlan != null) {
      plan.addExecutionPlan(objectHandleClass, executionPlan);
    }
  }

  @Override
  public ExecutionPlan getExecutionTraversePlan(ObjectHandleTraversePlan plan, Class<?> objectHandleClass) {
    if (!ObjectFunctions.isObjectHandleClass(objectHandleClass)) {
      throw UnexpectedViolationException.withMessage("Traverse plan of type {0} expected object handle to input",
          plan.type());
    }
    if (objectHandleClass != plan.objectHandleClass() && !plan.objectHandleClass().isAssignableFrom(objectHandleClass)) {
      throw UnexpectedViolationException.withMessage("Traverse plan of type {0} expected object handle of class {1} " +
          "or it subclass", plan.objectHandleClass());
    }

    ExecutionPlan executionPlan = plan.getExecutionPlan(plan.objectHandleClass());
    if (executionPlan == null) {
      executionPlan = plan.getExecutionPlan(objectHandleClass);
    }
    if (executionPlan == null) {
      executionPlan = buildExecutionTraversePlan(plan, plan.objectHandleClass());
      if (executionPlan != null) {
        plan.addExecutionPlan(plan.objectHandleClass(), executionPlan);
      } else {
        executionPlan = buildExecutionTraversePlan(plan, objectHandleClass);
        if (executionPlan != null) {
          plan.addExecutionPlan(objectHandleClass, executionPlan);
        }
      }
    }
    return executionPlan;
  }

  private ExecutionPlan buildExecutionTraversePlan(
      ObjectHandleTraversePlan plan, Class<?> objectHandleClass
  ) {
    GuideKinds guideKind = getGuideKind((TraversePlanTypes) plan.type());
    List<Guide<?, ?>> guides = findGuides(guideKind, objectHandleClass, plan.tid());
    if (guides.isEmpty()) {
      return null;
    }
    if (guides.size() > 1) {
      throw new UnsupportedOperationException("Not implemented");
    }
    return switch (guideKind) {
      case Mapper0, Mover0 -> new CallGuide0PlanImpl((Guide0<?, ?>) guides.get(0));
      case Mapper1, Mover1 -> new CallGuide1PlanImpl((Guide1<?, ?, ?>) guides.get(0));
      case Mapper2, Mover2 -> new CallGuide2PlanImpl((Guide2<?, ?, ?, ?>) guides.get(0));
      case Mapper3, Mover3 -> new CallGuide3PlanImpl((Guide3<?, ?, ?, ?, ?>) guides.get(0));
      default -> throw new UnsupportedOperationException("Not implemented");
    };
  }

  private List<Guide<?, ?>> findGuides(GuideKind kind, Class<?> objectHandleClass, String tid) {
    return guideRegistry.findGuides(kind, objectHandleClass, tid);
  }

  private GuideKinds getGuideKind(TraversePlanTypes planType) {
    return switch (planType) {
      case MapObjectHandleThruTransition0 -> GuideKinds.Mapper0;
      case MapObjectHandleThruTransition1 -> GuideKinds.Mapper1;
      case MapObjectHandleThruTransition2 -> GuideKinds.Mapper2;
      case MapObjectHandleThruTransition3 -> GuideKinds.Mapper3;
      case MoveObjectHandleThruTransition0 -> GuideKinds.Mover0;
      case MoveObjectHandleThruTransition1 -> GuideKinds.Mover1;
      case MoveObjectHandleThruTransition2 -> GuideKinds.Mover2;
      case MoveObjectHandleThruTransition3 -> GuideKinds.Mover3;
      default -> throw new UnsupportedOperationException("Not implemented");
    };
  }
}
