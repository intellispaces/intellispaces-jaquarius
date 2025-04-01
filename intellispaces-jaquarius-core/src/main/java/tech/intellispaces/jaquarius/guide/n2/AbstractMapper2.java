package tech.intellispaces.jaquarius.guide.n2;

import tech.intellispaces.commons.function.Function3;
import tech.intellispaces.commons.function.Function4;
import tech.intellispaces.jaquarius.guide.GuideKind;
import tech.intellispaces.jaquarius.guide.GuideKinds;

public interface AbstractMapper2<S, T, Q1, Q2> extends Mapper2<S, T, Q1, Q2> {

  @Override
  default GuideKind kind() {
    return GuideKinds.Mapper2;
  }

  @Override
  default Function3<S, Q1, Q2, T> asFunction3() {
    return this::map;
  }

  @Override
  default Function4<S, Q1, Q2, Void, T> asFunction4() {
    return this::map;
  }
}
