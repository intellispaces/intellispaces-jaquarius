package tech.intellispaces.jaquarius.system.projection;

import tech.intellispaces.jaquarius.system.ProjectionReference;

class ProjectionReferenceImpl implements ProjectionReference {
  private final String name;
  private final Class<?> targetClass;

  ProjectionReferenceImpl(String name, Class<?> targetClass) {
    this.name = name;
    this.targetClass = targetClass;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Class<?> targetClass() {
    return targetClass;
  }
}
