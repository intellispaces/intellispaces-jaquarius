package tech.intellispaces.jaquarius.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ProjectionSupplier {

  /**
   * The projection target supplier class.
   */
  Class<? extends tech.intellispaces.jaquarius.system.ProjectionSupplier> supplier()
      default tech.intellispaces.jaquarius.system.ProjectionSupplier.class;
}
