package intellispaces.jaquarius.annotation;

import intellispaces.jaquarius.system.projection.ModulePropertiesTargetSupplier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ProjectionDefinition(provider = ModulePropertiesTargetSupplier.class)
public @interface Properties {

  /**
   * Property path.
   */
  String value() default "";
}
