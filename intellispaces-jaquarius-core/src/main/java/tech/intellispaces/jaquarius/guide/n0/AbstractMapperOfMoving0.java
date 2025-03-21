package tech.intellispaces.jaquarius.guide.n0;

import tech.intellispaces.jaquarius.guide.GuideKind;
import tech.intellispaces.jaquarius.guide.GuideKinds;

import java.util.function.Function;

public interface AbstractMapperOfMoving0<S, T> extends MapperOfMoving0<S, T> {

  @Override
  default GuideKind kind() {
    return GuideKinds.MapperOfMoving0;
  }

  @Override
  default Function<S, T> asFunction() {
    return this::traverse;
  }
}
