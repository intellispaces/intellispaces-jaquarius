package tech.intellispaces.jaquarius.guide;

import tech.intellispaces.commons.base.entity.Enumerable;

public interface GuideKind extends Enumerable<GuideKind> {

  int order();

  boolean isMapper();

  boolean isMover();

  boolean isMapperOfMoving();

  boolean isMovingBased();
}
