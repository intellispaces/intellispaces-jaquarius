package tech.intellispaces.jaquarius.engine.descriptor;

import tech.intellispaces.general.entity.Enumeration;

public enum UnitMethodPurposes implements UnitMethodPurpose, Enumeration<UnitMethodPurpose> {

  StartupMethod,

  ShutdownMethod,

  InjectionMethod,

  ProjectionDefinition,

  Guide
}
