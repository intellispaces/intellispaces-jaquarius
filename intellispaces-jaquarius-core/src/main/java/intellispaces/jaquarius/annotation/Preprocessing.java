package intellispaces.jaquarius.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Preprocessing {

  Class<?>[] value() default {};

  boolean enable() default true;

  String artifact() default "";

  Class<?>[] addOnFor() default {};
}
