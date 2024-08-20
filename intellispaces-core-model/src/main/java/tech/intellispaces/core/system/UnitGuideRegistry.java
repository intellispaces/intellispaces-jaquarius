package tech.intellispaces.core.system;

import tech.intellispaces.core.guide.Guide;
import tech.intellispaces.core.guide.GuideKind;

import java.util.List;

/**
 * Unit guide register.
 */
public interface UnitGuideRegistry {

  List<Guide<?, ?>> findGuides(GuideKind kind, String tid);
}