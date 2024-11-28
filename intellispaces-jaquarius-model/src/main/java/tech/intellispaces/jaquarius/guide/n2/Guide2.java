package tech.intellispaces.jaquarius.guide.n2;

import tech.intellispaces.jaquarius.exception.TraverseException;
import tech.intellispaces.jaquarius.guide.Guide;

/**
 * Guide with two qualifiers.
 *
 * @param <S> source handle type.
 * @param <R> result handle type.
 * @param <Q1> first qualifier handle type.
 * @param <Q2> second qualifier handle type.
 */
public interface Guide2<S, R, Q1, Q2> extends Guide<S, R> {

  R traverse(S source, Q1 qualifier1, Q2 qualifier2) throws TraverseException;
}