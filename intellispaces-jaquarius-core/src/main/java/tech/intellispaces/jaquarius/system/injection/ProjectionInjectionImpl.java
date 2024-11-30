package tech.intellispaces.jaquarius.system.injection;

import tech.intellispaces.entity.exception.UnexpectedExceptions;
import tech.intellispaces.jaquarius.system.InjectionKind;
import tech.intellispaces.jaquarius.system.ProjectionInjection;
import tech.intellispaces.jaquarius.system.kernel.KernelFunctions;

class ProjectionInjectionImpl implements ProjectionInjection {
  private final Class<?> unitClass;
  private final String name;
  private final Class<?> targetClass;
  private Object projectionTarget;

  ProjectionInjectionImpl(Class<?> unitClass, String name, Class<?> targetClass) {
    this.unitClass = unitClass;
    this.name = name;
    this.targetClass = targetClass;
  }

  @Override
  public InjectionKind kind() {
    return InjectionKinds.ProjectionInjection;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Class<?> unitClass() {
    return unitClass;
  }

  @Override
  public Class<?> targetClass() {
    return targetClass;
  }

  @Override
  public boolean isDefined() {
    return projectionTarget != null;
  }

  @Override
  public Object value() {
    if (projectionTarget == null) {
      projectionTarget = KernelFunctions.currentModule().projectionRegistry().getProjection(name, targetClass);
      if (projectionTarget == null) {
        throw UnexpectedExceptions.withMessage("Target of projection injection '{0}' in unit {1} " +
                "is not defined", name(), unitClass.getCanonicalName());
      }
    }
    return projectionTarget;
  }
}
