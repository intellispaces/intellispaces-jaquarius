package intellispaces.framework.core.guide.n1;

import intellispaces.framework.core.exception.TraverseException;
import intellispaces.framework.core.guide.Mover;
import intellispaces.framework.core.guide.n2.Mover2;
import intellispaces.framework.core.guide.n3.Mover3;
import intellispaces.framework.core.guide.n4.Mover4;
import intellispaces.framework.core.guide.n5.Mover5;

import java.util.function.BiConsumer;

/**
 * Mover guide with one qualifier.
 *
 * @param <S> source object type.
 * @param <Q> qualifier type.
 */
public interface Mover1<S, Q> extends
    Guide1<S, S, Q>,
    Mover<S>,
    Mover2<S, Q, Void>,
    Mover3<S, Q, Void, Void>,
    Mover4<S, Q, Void, Void, Void>,
    Mover5<S, Q, Void, Void, Void, Void>
{
  BiConsumer<S, Q> asBiConsumer();

  default S move(S source, Q qualifier) throws TraverseException {
    return traverse(source, qualifier);
  }

  @Override
  default S move(S source, Q qualifier1, Void qualifier2) throws TraverseException {
    return traverse(source, qualifier1);
  }

  @Override
  default S move(S source, Q qualifier1, Void qualifier2, Void qualifier3) throws TraverseException {
    return traverse(source, qualifier1);
  }

  @Override
  default S move(S source, Q qualifier1, Void qualifier2, Void qualifier3, Void qualifier4) throws TraverseException {
    return traverse(source, qualifier1);
  }

  @Override
  default S move(S source, Q qualifier1, Void qualifier2, Void qualifier3, Void qualifier4, Void qualifier5) throws TraverseException {
    return traverse(source, qualifier1);
  }

  @SuppressWarnings("unchecked")
  default S traverse(S source, Object... qualifiers) throws TraverseException {
    return traverse(source, (Q) qualifiers[0]);
  }

  @Override
  default S traverse(S source, Q qualifier1, Void qualifier2) throws TraverseException {
    return traverse(source, qualifier1);
  }

  @Override
  default S traverse(S source, Q qualifier1, Void qualifier2, Void qualifier3) throws TraverseException {
    return traverse(source, qualifier1);
  }

  @Override
  default S traverse(S source, Q qualifier1, Void qualifier2, Void qualifier3, Void qualifier4) throws TraverseException {
    return traverse(source, qualifier1);
  }

  @Override
  default S traverse(S source, Q qualifier1, Void qualifier2, Void qualifier3, Void qualifier4, Void qualifier5) throws TraverseException {
    return traverse(source, qualifier1);
  }
}