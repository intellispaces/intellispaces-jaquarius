package tech.intellispaces.reflections.framework.engine.impl;

import java.util.List;

import tech.intellispaces.actions.Action;
import tech.intellispaces.reflections.framework.engine.description.ObjectHandleMethodDescription;
import tech.intellispaces.reflections.framework.system.Injection;

public class ObjectHandleTypeDescription implements tech.intellispaces.reflections.framework.engine.description.ObjectHandleTypeDescription {
  private final Class<?> objctHandleClass;
  private final Class<?> objctHandleWrapperClass;
  private final List<ObjectHandleMethodDescription> methods;
  private final Action[] methodActions;
  private final Action[] guideActions;
  private final Injection[] injections;

  public ObjectHandleTypeDescription(
      Class<?> objctHandleClass,
      Class<?> objctHandleWrapperClass,
      List<ObjectHandleMethodDescription> methods,
      Action[] methodActions,
      Action[] guideActions,
      Injection[] injections
  ) {
    this.objctHandleClass = objctHandleClass;
    this.objctHandleWrapperClass = objctHandleWrapperClass;
    this.methods = methods;
    this.methodActions = methodActions;
    this.guideActions = guideActions;
    this.injections = injections;
  }

  @Override
  public Class<?> objctHandleClass() {
    return objctHandleClass;
  }

  @Override
  public Class<?> objctHandleWrapperClass() {
    return objctHandleWrapperClass;
  }

  @Override
  public List<ObjectHandleMethodDescription> methods() {
    return methods;
  }

  public Action[] methodActions() {
    return methodActions;
  }

  public Action[] guideActions() {
    return guideActions;
  }

  public Injection[] injections() {
    return injections;
  }
}
