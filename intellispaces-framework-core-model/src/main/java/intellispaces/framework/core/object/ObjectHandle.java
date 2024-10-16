package intellispaces.framework.core.object;

import intellispaces.common.base.type.Type;
import intellispaces.framework.core.exception.TraverseException;
import intellispaces.framework.core.space.channel.Channel1;
import intellispaces.framework.core.space.channel.MappingChannel;

/**
 * Handle of object.<p/>
 *
 * The handle implements interaction with the object.<p/>
 *
 * The interaction of the system with the object is performed through the object handle.<p/>
 *
 * @param <D> object domain type.
 */
public interface ObjectHandle<D> {

  /**
   * Returns the type of the primary domain declaration.
   */
  Type<D> domain();

  /**
   * Returns the class declaring the domain.
   */
  Class<?> domainClass();

  boolean isMovable();

  MovableObjectHandle<D> asMovableOrElseThrow();

  <T, Q, C extends Channel1 & MappingChannel> T mapThru(Class<C> channelClass, Q qualifier) throws TraverseException;
}
