package tech.intellispaces.jaquarius.guide.n2;

import tech.intellispaces.jaquarius.exception.TraverseException;
import tech.intellispaces.jaquarius.guide.Mapper;
import tech.intellispaces.jaquarius.guide.n3.Mapper3;
import tech.intellispaces.jaquarius.guide.n4.Mapper4;
import tech.intellispaces.jaquarius.guide.n5.Mapper5;
import tech.intellispaces.entity.function.TriFunction;

/**
 * Mapper guide with two qualifiers.
 *
 * @param <S> source handle type.
 * @param <T> target handle type.
 * @param <Q1> first qualifier handle type.
 * @param <Q2> second qualifier handle type.
 */
public interface Mapper2<S, T, Q1, Q2> extends
    Guide2<S, T, Q1, Q2>,
    Mapper<S, T>,
    Mapper3<S, T, Q1, Q2, Void>,
    Mapper4<S, T, Q1, Q2, Void, Void>,
    Mapper5<S, T, Q1, Q2, Void, Void, Void>
{
  TriFunction<S, Q1, Q2, T> asTriFunction();

  default T map(S source, Q1 qualifier1, Q2 qualifier2) throws TraverseException {
    return traverse(source, qualifier1, qualifier2);
  }

  @Override
  default T map(S source, Q1 qualifier1, Q2 qualifier2, Void qualifier3) throws TraverseException {
    return traverse(source, qualifier1, qualifier2);
  }

  @Override
  default T map(S source, Q1 qualifier1, Q2 qualifier2, Void qualifier3, Void qualifier4) throws TraverseException {
    return traverse(source, qualifier1, qualifier2);
  }

  @Override
  default T map(S source, Q1 qualifier1, Q2 qualifier2, Void qualifier3, Void qualifier4, Void qualifier5) throws TraverseException {
    return traverse(source, qualifier1, qualifier2);
  }

  @Override
  @SuppressWarnings("unchecked")
  default T traverse(S source, Object... qualifiers) throws TraverseException {
    return traverse(source, (Q1) qualifiers[0], (Q2) qualifiers[1]);
  }

  @Override
  default T traverse(S source, Q1 qualifier1, Q2 qualifier2, Void qualifier3) throws TraverseException {
    return traverse(source, qualifier1, qualifier2);
  }

  @Override
  default T traverse(S source, Q1 qualifier1, Q2 qualifier2, Void qualifier3, Void qualifier4) throws TraverseException {
    return traverse(source, qualifier1, qualifier2);
  }

  @Override
  default T traverse(S source, Q1 qualifier1, Q2 qualifier2, Void qualifier3, Void qualifier4, Void qualifier5) throws TraverseException {
    return traverse(source, qualifier1, qualifier2);
  }
}