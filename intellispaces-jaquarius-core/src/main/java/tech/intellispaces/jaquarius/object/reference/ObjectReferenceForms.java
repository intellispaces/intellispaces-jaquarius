package tech.intellispaces.jaquarius.object.reference;

import tech.intellispaces.commons.base.entity.Enumeration;

public enum ObjectReferenceForms implements ObjectReferenceForm, Enumeration<ObjectReferenceForm> {

  /**
   * The object reference represented as instance of the class {@link ObjectHandle} or primitive wrapper class.
   */
  Object,

  /**
   * The object reference represented as the primitive type.
   */
  Primitive
}
