package tech.intellispaces.jaquarius.object;

import tech.intellispaces.jaquarius.channel.Channel0;
import tech.intellispaces.jaquarius.channel.Channel1;
import tech.intellispaces.jaquarius.channel.ChannelMethod0;
import tech.intellispaces.jaquarius.channel.ChannelMethod1;
import tech.intellispaces.jaquarius.channel.MappingOfMovingChannel;
import tech.intellispaces.jaquarius.exception.TraverseException;

/**
 * The handle of the movable object.<p/>
 *
 * Movable object being moved can move in space.
 *
 * @param <D> object domain type.
 */
public interface MovableObjectHandle<D> extends ObjectHandle<D> {

  @Override
  default boolean isMovable() {
    return true;
  }

  <Q> MovableObjectHandle<D> moveThru(String cid, Q qualifier) throws TraverseException;

  MovableObjectHandle<D> moveThru(Class<? extends Channel0> channelClass) throws TraverseException;

  MovableObjectHandle<D> moveThru(ChannelMethod0<? super D, ? super D> channelMethod) throws TraverseException;

  <Q> MovableObjectHandle<D> moveThru(Class<? extends Channel1> channelClass, Q qualifier) throws TraverseException;

  <Q> MovableObjectHandle<D> moveThru(ChannelMethod1<? super D, ? super D, Q> channelMethod, Q qualifier) throws TraverseException;

  <R, Q> R mapOfMovingThru(String cid, Q qualifier) throws TraverseException;

  <R> R mapOfMovingThru(Class<? extends Channel0> channelClass) throws TraverseException;

  <R> R mapOfMovingThru(ChannelMethod0<? super D, R> channelMethod) throws TraverseException;

  <R, Q> R mapOfMovingThru(ChannelMethod1<? super D, R, Q> channelMethod, Q qualifier) throws TraverseException;

  <R, Q, C extends Channel1 & MappingOfMovingChannel> R mapOfMovingThru(Class<C> channelClass, Q qualifier) throws TraverseException;
}
