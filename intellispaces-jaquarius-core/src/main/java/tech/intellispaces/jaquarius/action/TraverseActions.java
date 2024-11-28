package tech.intellispaces.jaquarius.action;

import tech.intellispaces.jaquarius.channel.Channel0;
import tech.intellispaces.jaquarius.channel.Channel1;
import tech.intellispaces.jaquarius.channel.Channel2;
import tech.intellispaces.jaquarius.channel.Channel3;
import tech.intellispaces.jaquarius.channel.Channel4;
import tech.intellispaces.jaquarius.guide.GuideForm;
import tech.intellispaces.jaquarius.guide.GuideForms;
import tech.intellispaces.action.Action1;
import tech.intellispaces.action.Action2;
import tech.intellispaces.action.Action3;
import tech.intellispaces.action.Action4;
import tech.intellispaces.action.Action5;
import tech.intellispaces.entity.type.Type;
import tech.intellispaces.entity.type.Types;

public interface TraverseActions {

  static <S, T> Action1<T, S> mapThruChannel0(
    Type<S> sourceType, Class<? extends Channel0> channelClass, GuideForm guideForm
  ) {
    return new MapThruChannel0Action<>(sourceType, channelClass, guideForm);
  }

  static <S, T> Action1<T, S> mapThruChannel0(
      Class<S> sourceClass, Class<? extends Channel0> channelClass
  ) {
    return new MapThruChannel0Action<>(Types.get(sourceClass), channelClass, GuideForms.Main);
  }

  static <S, T, Q> Action2<T, S, Q> mapThruChannel1(
    Type<S> sourceType, Class<? extends Channel1> channelClass, GuideForm guideForm
  ) {
    return new MapThruChannel1Action<>(sourceType, channelClass, guideForm);
  }

  static <S, T, Q> Action2<T, S, Q> mapThruChannel1(
      Class<S> sourceClass, Class<? extends Channel1> channelClass
  ) {
    return new MapThruChannel1Action<>(Types.get(sourceClass), channelClass, GuideForms.Main);
  }

  static <S, T, Q1, Q2> Action3<T, S, Q1, Q2> mapThruChannel2(
    Type<S> sourceType, Class<? extends Channel2> channelClass, GuideForm guideForm
  ) {
    return new MapThruChannel2Action<>(sourceType, channelClass, guideForm);
  }

  static <S, T, Q1, Q2, Q3> Action4<T, S, Q1, Q2, Q3> mapThruChannel3(
    Type<S> sourceType, Class<? extends Channel3> channelClass, GuideForm guideForm
  ) {
    return new MapThruChannel3Action<>(sourceType, channelClass, guideForm);
  }

  static <S> Action1<S, S> moveThruChannel0(
    Type<S> sourceType, Class<? extends Channel0> channelClass, GuideForm guideForm
  ) {
    return new MoveThruChannel0Action<>(sourceType, channelClass, guideForm);
  }

  static <S, Q> Action2<S, S, Q> moveThruChannel1(
    Type<S> sourceType, Class<? extends Channel1> channelClass, GuideForm guideForm
  ) {
    return new MoveThruChannel1Action<>(sourceType, channelClass, guideForm);
  }

  static <S, Q1, Q2> Action3<S, S, Q1, Q2> moveThruChannel2(
    Type<S> sourceType, Class<? extends Channel2> channelClass, GuideForm guideForm
  ) {
    return new MoveThruChannel2Action<>(sourceType, channelClass, guideForm);
  }

  static <S, Q1, Q2, Q3> Action4<S, S, Q1, Q2, Q3> moveThruChannel3(
    Type<S> sourceType, Class<? extends Channel3> channelClass, GuideForm guideForm
  ) {
    return new MoveThruChannel3Action<>(sourceType, channelClass, guideForm);
  }

  static <S, T> Action1<T, S> mapOfMovingThruChannel0(
      Type<S> sourceType, Class<? extends Channel0> channelClass, GuideForm guideForm
  ) {
    return new MapOfMovingThruChannel0Action<>(sourceType, channelClass, guideForm);
  }

  static <S, T, Q> Action2<T, S, Q> mapOfMovingThruChannel1(
      Type<S> sourceType, Class<? extends Channel1> channelClass, GuideForm guideForm
  ) {
    return new MapOfMovingThruChannel1Action<>(sourceType, channelClass, guideForm);
  }

  static <S, T, Q> Action2<T, S, Q> mapOfMovingThruChannel1(
      Class<S> sourceClass, Class<? extends Channel1> channelClass
  ) {
    return new MapOfMovingThruChannel1Action<>(Types.get(sourceClass), channelClass, GuideForms.Main);
  }

  static <S, T, Q1, Q2> Action3<T, S, Q1, Q2> mapOfMovingThruChannel2(
      Type<S> sourceType, Class<? extends Channel2> channelClass, GuideForm guideForm
  ) {
    return new MapOfMovingThruChannel2Action<>(sourceType, channelClass, guideForm);
  }

  static <S, T, Q1, Q2, Q3> Action4<T, S, Q1, Q2, Q3> mapOfMovingThruChannel3(
      Type<S> sourceType, Class<? extends Channel3> channelClass, GuideForm guideForm
  ) {
    return new MapOfMovingThruChannel3Action<>(sourceType, channelClass, guideForm);
  }

  static <S, T, Q1, Q2, Q3, Q4> Action5<T, S, Q1, Q2, Q3, Q4> mapOfMovingThruChannel4(
      Type<S> sourceType, Class<? extends Channel4> channelClass, GuideForm guideForm
  ) {
    return new MapOfMovingThruChannel4Action<>(sourceType, channelClass, guideForm);
  }
}