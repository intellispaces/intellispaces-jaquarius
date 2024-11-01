package intellispaces.jaquarius.action;

import intellispaces.common.action.AbstractAction1;
import intellispaces.common.base.type.Type;
import intellispaces.jaquarius.channel.Channel0;
import intellispaces.jaquarius.guide.GuideForm;
import intellispaces.jaquarius.guide.n0.MapperOfMoving0;
import intellispaces.jaquarius.system.Modules;

class MapOfMovingThruChannel0Action<T, S> extends AbstractAction1<T, S> {
  private final Type<S> sourceType;
  private final Class<? extends Channel0> channelClass;
  private final GuideForm guideForm;
  private final MapperOfMoving0<S, T> autoMapper;

  MapOfMovingThruChannel0Action(
      Type<S> sourceType,
      Class<? extends Channel0> channelClass,
      GuideForm guideForm
  ) {
    this.sourceType = sourceType;
    this.channelClass = channelClass;
    this.guideForm = guideForm;
    this.autoMapper = Modules.current().autoMapperOfMovingThruChannel0(sourceType, channelClass, guideForm);
  }

  @Override
  public T execute(S source) {
    return autoMapper.map(source);
  }
}
