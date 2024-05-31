package tech.intellispacesframework.core;

import tech.intellispacesframework.core.system.Module;
import tech.intellispacesframework.core.system.ModuleLoader;

public interface IntellispacesFramework {

  /**
   * Loads system module to current application.
   *
   * @param moduleClass the module class.
   * @return system module.
   */
  static Module loadModule(Class<?> moduleClass) {
    return ModuleLoader.loadDefaultModule(moduleClass);
  }
}
