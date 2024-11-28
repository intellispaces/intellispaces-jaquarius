package tech.intellispaces.jaquarius.system.kernel;

import tech.intellispaces.jaquarius.system.ObjectHandleWrapper;

class ObjectRegistryImpl implements ObjectRegistry {

  @Override
  public void add(ObjectHandleWrapper handle) {
    InnerObjectHandle innerHandle = new InnerObjectHandleImpl();
    handle.$init(innerHandle);
  }
}