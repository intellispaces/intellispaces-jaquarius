package tech.intellispaces.jaquarius.guide.n2;

import tech.intellispaces.jaquarius.guide.GuideKind;
import tech.intellispaces.jaquarius.guide.GuideKinds;
import tech.intellispaces.entity.function.QuadriFunction;
import tech.intellispaces.entity.function.TriFunction;

public interface AbstractMapper2<S, T, Q1, Q2> extends Mapper2<S, T, Q1, Q2> {

  @Override
  default GuideKind kind() {
    return GuideKinds.Mapper2;
  }

  @Override
  default TriFunction<S, Q1, Q2, T> asTriFunction() {
    return this::map;
  }

  @Override
  default QuadriFunction<S, Q1, Q2, Void, T> asQuadFunction() {
    return this::map;
  }
}