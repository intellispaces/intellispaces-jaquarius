package tech.intellispaces.jaquarius.system.injection;

import tech.intellispaces.commons.base.entity.Enumeration;
import tech.intellispaces.jaquarius.system.InjectionKind;

public enum InjectionKinds implements InjectionKind, Enumeration<InjectionKind> {

  Projection,

  SpecificGuide,

  AutoGuide
}
