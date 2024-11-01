package intellispaces.jaquarius.system.projection;

import intellispaces.jaquarius.system.ProjectionTargetSupplier;

import java.lang.reflect.Method;

public abstract class AbstractProjectionTargetSupplier implements ProjectionTargetSupplier {
  protected final Method projectionMethod;

  AbstractProjectionTargetSupplier(Method projectionMethod) {
    this.projectionMethod = projectionMethod;
  }
}
