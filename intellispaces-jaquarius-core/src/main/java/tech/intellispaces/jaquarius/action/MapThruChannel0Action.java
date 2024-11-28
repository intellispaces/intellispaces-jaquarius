package tech.intellispaces.jaquarius.action;

import tech.intellispaces.jaquarius.channel.Channel0;
import tech.intellispaces.jaquarius.guide.GuideForm;
import tech.intellispaces.jaquarius.guide.n0.Mapper0;
import tech.intellispaces.jaquarius.system.Modules;
import tech.intellispaces.action.AbstractAction1;
import tech.intellispaces.entity.type.Type;

class MapThruChannel0Action<T, S> extends AbstractAction1<T, S> {
  private final Type<S> sourceType;
  private final Class<? extends Channel0> channelClass;
  private final GuideForm guideForm;
  private final Mapper0<S, T> autoMapper;

  MapThruChannel0Action(
      Type<S> sourceType,
      Class<? extends Channel0> channelClass,
      GuideForm guideForm
  ) {
    this.sourceType = sourceType;
    this.channelClass = channelClass;
    this.guideForm = guideForm;
    this.autoMapper = Modules.current().autoMapperThruChannel0(sourceType, channelClass, guideForm);
  }

  @Override
  public T execute(S source) {
    return autoMapper.map(source);
  }

  @Override
  public int executeReturnInt(S source) {
    return autoMapper.mapToInt(source);
  }

  @Override
  public double executeReturnDouble(S source) {
    return autoMapper.mapToDouble(source);
  }
}