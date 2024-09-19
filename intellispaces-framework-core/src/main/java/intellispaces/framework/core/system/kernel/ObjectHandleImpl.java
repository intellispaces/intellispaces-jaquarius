package intellispaces.framework.core.system.kernel;

import intellispaces.common.action.Action;
import intellispaces.common.action.Actions;
import intellispaces.common.action.getter.ResettableGetter;
import intellispaces.common.base.exception.UnexpectedViolationException;

import java.util.Arrays;
import java.util.List;

class ObjectHandleImpl implements SystemObjectHandle {
  private List<ResettableGetter<Action>> transitionActions = List.of();
  private List<Action> guideActions = List.of();

  @Override
  public int numberTransitions() {
    return transitionActions.size();
  }

  @Override
  public Action getTransitionAction(int index) {
    return transitionActions.get(index).get();
  }

  @Override
  public Action getGuideAction(int index) {
    Action action = guideActions.get(index);
    if (action == null) {
      throw UnexpectedViolationException.withMessage("Guide action os not defined");
    }
    return action;
  }

  @Override
  public void setTransitionActions(Action... actions) {
    if (actions == null) {
      transitionActions = List.of();
      return;
    }
    transitionActions = Arrays.stream(actions)
        .map(Actions::resettableGetter)
        .toList();
  }

  @Override
  public void setGuideActions(Action... actions) {
    if (actions == null) {
      guideActions = List.of();
      return;
    }
    guideActions = Arrays.stream(actions).toList();
  }
}
