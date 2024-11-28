package tech.intellispaces.jaquarius.system.injection;

import tech.intellispaces.jaquarius.system.GuideInjection;
import tech.intellispaces.jaquarius.system.InjectionKind;
import tech.intellispaces.jaquarius.system.kernel.KernelFunctions;
import tech.intellispaces.entity.exception.UnexpectedExceptions;

class GuideInjectionImpl implements GuideInjection {
  private final Class<?> unitClass;
  private final String name;
  private final Class<?> guideClass;
  private Object guide;

  GuideInjectionImpl(Class<?> unitClass, String name, Class<?> guideClass) {
    this.unitClass = unitClass;
    this.name = name;
    this.guideClass = guideClass;
  }

  @Override
  public InjectionKind kind() {
    return InjectionKinds.GuideInjection;
  }

  @Override
  public Class<?> unitClass() {
    return unitClass;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Class<?> guideClass() {
    return guideClass;
  }

  @Override
  public Object value() {
    if (guide == null) {
      guide = KernelFunctions.currentModuleSilently().guideRegistry().getGuide(name, guideClass);
      if (guide == null) {
        throw UnexpectedExceptions.withMessage("Value of guide injection '{0}' in unit {1} is not defined",
            name(), unitClass.getCanonicalName());
      }
    }
    return guide;
  }
}