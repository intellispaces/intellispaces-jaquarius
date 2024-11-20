package intellispaces.jaquarius.system.projection;

import intellispaces.common.javastatement.method.MethodStatement;
import intellispaces.jaquarius.annotation.Projection;
import tech.intellispaces.entity.exception.UnexpectedExceptions;

import java.lang.reflect.Method;

public interface ProjectionFunctions {

  static String getProjectionName(MethodStatement projectionMethod) {
    Projection annotation = projectionMethod.selectAnnotation(Projection.class).orElseThrow(() ->
        UnexpectedExceptions.withMessage("Projection method should be marked with annotation {0}",
        Projection.class.getSimpleName()));
    return getProjectionName(projectionMethod.name(), annotation);
  }

  static String getProjectionName(Method projectionMethod) {
    Projection annotation = projectionMethod.getAnnotation(Projection.class);
    if (annotation == null) {
      throw UnexpectedExceptions.withMessage("Projection method should be marked with annotation {0}",
          Projection.class.getSimpleName());
    }
    return getProjectionName(projectionMethod.getName(), annotation);
  }

  private static String getProjectionName(String methodName, Projection annotation) {
    String name = annotation.value();
    if ("".equals(name)) {
      name = methodName;
    }
    return name;
  }
}
