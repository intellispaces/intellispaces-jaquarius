package tech.intellispaces.jaquarius.engine;

import tech.intellispaces.action.Action;
import tech.intellispaces.jaquarius.system.Injection;

/**
 * The system unit broker.
 */
public interface UnitAgent {

  Action guideAction(int guideOrdinal);

  Injection injection(int ordinal);
}
