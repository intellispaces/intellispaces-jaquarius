package intellispaces.framework.core.system.kernel;

import intellispaces.common.action.Action;
import intellispaces.framework.core.system.Injection;
import intellispaces.framework.core.system.Unit;
import intellispaces.framework.core.system.UnitProjectionDefinition;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * Internal kernel representation of the module unit.
 */
public interface KernelUnit extends Unit {

  Object instance();

  Optional<Method> startupMethod();

  Optional<Method> shutdownMethod();

  Optional<Action> startupAction();

  Optional<Action> shutdownAction();

  void setStartupAction(Action action);

  void setShutdownAction(Action action);

  void setProjectionDefinitions(UnitProjectionDefinition... projectionDefinitions);

  Injection injection(int ordinal);

  List<Injection> injections();

  void setInjections(Injection... injections);

  int numberGuides();

  Action getGuideAction(int index);

  void setGuideActions(Action... actions);

  void setGuideAction(int index, Action action);
}