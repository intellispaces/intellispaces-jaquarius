package tech.intellispaces.jaquarius.guide.n3;

import tech.intellispaces.commons.function.Function4;
import tech.intellispaces.jaquarius.guide.GuideKind;
import tech.intellispaces.jaquarius.guide.GuideKinds;

public interface AbstractMapperOfMoving3<S, T, Q1, Q2, Q3> extends MapperOfMoving3<S, T, Q1, Q2, Q3> {

  @Override
  default GuideKind kind() {
    return GuideKinds.MapperOfMoving3;
  }

  @Override
  default Function4<S, Q1, Q2, Q3, T> asQuadFunction() {
    return this::traverse;
  }
}
