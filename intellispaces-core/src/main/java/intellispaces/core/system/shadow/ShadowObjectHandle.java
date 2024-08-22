package intellispaces.core.system.shadow;

import intellispaces.actions.Action;

/**
 * Internal representation of the object handle.
 */
public interface ShadowObjectHandle {

  int numberTransitions();

  Action getTransitionAction(int index);

  Action getGuideAction(int index);

  void setTransitionActions(Action... actions);

  void setGuideActions(Action... actions);
}