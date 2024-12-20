package tech.intellispaces.jaquarius.object.reference;

import tech.intellispaces.general.entity.Enumeration;

public enum ObjectReferenceForms implements ObjectReferenceForm, Enumeration<ObjectReferenceForm> {

  /**
   * The object reference represented as the instance of the {@link Boolean}, {@link Character}, {@link Byte},
   * {@link Short}, {@link Integer}, {@link Long}, {@link Float} {@link Double}, {@link String} or
   * {@link ObjectReference} classes.
   */
  Common,

  /**
   * The object reference represented as the primitive instance.
   */
  Primitive
}
