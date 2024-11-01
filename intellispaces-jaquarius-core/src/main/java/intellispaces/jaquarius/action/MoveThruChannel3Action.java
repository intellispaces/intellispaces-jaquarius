package intellispaces.jaquarius.action;

import intellispaces.common.action.AbstractAction4;
import intellispaces.common.base.type.Type;
import intellispaces.jaquarius.channel.Channel3;
import intellispaces.jaquarius.guide.GuideForm;
import intellispaces.jaquarius.guide.n3.Mover3;
import intellispaces.jaquarius.system.Modules;

class MoveThruChannel3Action<S, Q1, Q2, Q3> extends AbstractAction4<S, S, Q1, Q2, Q3> {
  private final Type<S> sourceType;
  private final Class<? extends Channel3> channelClass;
  private final GuideForm guideForm;
  private final Mover3<S, Q1, Q2, Q3> autoMover;

  MoveThruChannel3Action(
      Type<S> sourceType,
      Class<? extends Channel3> channelClass,
      GuideForm guideForm
  ) {
    this.sourceType = sourceType;
    this.channelClass = channelClass;
    this.guideForm = guideForm;
    this.autoMover = Modules.current().autoMoverThruChannel3(sourceType, channelClass, guideForm);
  }

  @Override
  public S execute(S source, Q1 qualifier1, Q2 qualifier2, Q3 qualifier3) {
    return autoMover.move(source, qualifier1, qualifier2, qualifier3);
  }
}
