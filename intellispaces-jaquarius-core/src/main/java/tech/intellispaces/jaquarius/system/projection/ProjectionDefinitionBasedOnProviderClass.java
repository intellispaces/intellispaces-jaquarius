package tech.intellispaces.jaquarius.system.projection;

import tech.intellispaces.commons.base.exception.NotImplementedExceptions;
import tech.intellispaces.jaquarius.system.ProjectionDefinitionKind;
import tech.intellispaces.jaquarius.system.UnitProjectionDefinition;

import java.lang.reflect.Method;

public class ProjectionDefinitionBasedOnProviderClass implements UnitProjectionDefinition {
  private final Class<?> unitClass;
  private final String name;
  private final Class<?> type;
  private final boolean lazy;
  private final String providerClassCanonicalName;

  ProjectionDefinitionBasedOnProviderClass(
      Class<?> unitClass,
      String name,
      Class<?> type,
      boolean lazy,
      String providerClassCanonicalName
  ) {
    this.unitClass = unitClass;
    this.name = name;
    this.type = type;
    this.lazy = lazy;
    this.providerClassCanonicalName = providerClassCanonicalName;
  }

  @Override
  public ProjectionDefinitionKind kind() {
    return ProjectionDefinitionKinds.ProjectionDefinitionBasedOnProviderClass;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Class<?> type() {
    return type;
  }

  @Override
  public boolean isLazy() {
    return lazy;
  }

  @Override
  public Class<?> unitClass() {
    return unitClass;
  }

  @Override
  public Method projectionMethod() {
    throw NotImplementedExceptions.withCode("5Xui0Q");
  }

  public String providerClassCanonicalName() {
    return providerClassCanonicalName;
  }
}
