package tech.intellispaces.reflectionsj.system;

import java.lang.reflect.Method;

/**
 * Projection definition declared in unit method annotated with annotation @Projection.
 */
public interface UnitProjectionDefinition extends ProjectionDefinition {

  Class<?> unitClass();

  Method projectionMethod();
}
