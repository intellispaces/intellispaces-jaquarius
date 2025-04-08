package tech.intellispaces.jaquarius.action;

import tech.intellispaces.actions.AbstractAction4;
import tech.intellispaces.commons.type.Type;
import tech.intellispaces.jaquarius.channel.Channel3;
import tech.intellispaces.jaquarius.guide.n3.Mover3;
import tech.intellispaces.jaquarius.object.reference.ObjectReferenceForm;
import tech.intellispaces.jaquarius.system.Modules;

class MoveThruChannel3Action<S, Q1, Q2, Q3> extends AbstractAction4<S, S, Q1, Q2, Q3> {
  private final Type<S> sourceType;
  private final Class<? extends Channel3> channelClass;
  private final ObjectReferenceForm targetForm;
  private final Mover3<S, Q1, Q2, Q3> autoMover;

  MoveThruChannel3Action(
      Type<S> sourceType,
      Class<? extends Channel3> channelClass,
      ObjectReferenceForm targetForm
  ) {
    this.sourceType = sourceType;
    this.channelClass = channelClass;
    this.targetForm = targetForm;
    this.autoMover = Modules.current().autoMoverThruChannel3(sourceType, channelClass, targetForm);
  }

  @Override
  public S execute(S source, Q1 qualifier1, Q2 qualifier2, Q3 qualifier3) {
    return autoMover.move(source, qualifier1, qualifier2, qualifier3);
  }
}
