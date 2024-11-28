package tech.intellispaces.jaquarius.object.reference;

/**
 * The handle of the unmovable object.<p/>
 *
 * The unmovable object being moved cannot move in space.
 *
 * @param <D> the object domain type.
 */
public interface UnmovableObjectHandle<D> extends ObjectHandle<D> {

  @Override
  default boolean isMovable() {
    return false;
  }
}