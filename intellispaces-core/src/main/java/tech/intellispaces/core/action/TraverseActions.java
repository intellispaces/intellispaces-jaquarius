package tech.intellispaces.core.action;

import tech.intellispaces.actions.Action1;
import tech.intellispaces.actions.Action2;
import tech.intellispaces.actions.Action3;
import tech.intellispaces.core.space.transition.Transition0;
import tech.intellispaces.core.space.transition.Transition1;
import tech.intellispaces.core.space.transition.Transition2;
import tech.intellispaces.javastatements.type.Type;

public interface TraverseActions {

  static <S, T> Action1<T, S> mapThruTransition0(
      Type<S> sourceType, Class<? extends Transition0> transitionClass
  ) {
    return new MapThruTransition0Action<>(sourceType, transitionClass);
  }

  static <S, T, Q> Action2<T, S, Q> mapThruTransition1(
      Type<S> sourceType, Class<? extends Transition1> transitionClass
  ) {
    return new MapThruTransition1Action<>(sourceType, transitionClass);
  }

  static <S, T, Q1, Q2> Action3<T, S, Q1, Q2> mapThruTransition2(
      Type<S> sourceType, Class<? extends Transition2> transitionClass
  ) {
    return new MapThruTransition2Action<>(sourceType, transitionClass);
  }

  static <S, B> Action1<B, S> moveThruTransition0(
      Type<S> sourceType, Class<? extends Transition0> transitionClass
  ) {
    return new MoveThruTransition0Action<>(sourceType, transitionClass);
  }

  static <S, B, Q> Action2<B, S, Q> moveThruTransition1(
      Type<S> sourceType, Class<? extends Transition1> transitionClass
  ) {
    return new MoveThruTransition1Action<>(sourceType, transitionClass);
  }
  static <S, B, Q1, Q2> Action3<B, S, Q1, Q2> moveThruTransition2(
      Type<S> sourceType, Class<? extends Transition2> transitionClass
  ) {
    return new MoveThruTransition2Action<>(sourceType, transitionClass);
  }
}
