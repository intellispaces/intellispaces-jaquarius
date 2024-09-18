package intellispaces.framework.core.system;

import intellispaces.common.javastatement.customtype.CustomType;
import intellispaces.common.javastatement.instance.AnnotationInstance;
import intellispaces.common.javastatement.instance.Instance;
import intellispaces.framework.core.annotation.Module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ModuleFunctions {

  static Iterable<CustomType> getIncludedUnits(CustomType moduleType) {
    Map<String, CustomType> unitTypes = new HashMap<>();
    AnnotationInstance moduleAnnotation = moduleType.selectAnnotation(Module.class.getCanonicalName()).orElseThrow();
    Optional<Instance> unitsAttr = moduleAnnotation.elementValue("units");
    if (unitsAttr.isPresent()) {
      List<Instance> units = unitsAttr.get().asArray().orElseThrow().elements();
      for (Instance unit : units) {
        CustomType unitType = unit.asClass().orElseThrow().type();
        if (!unitTypes.containsKey(unitType.className())) {
          unitTypes.put(unitType.className(), unitType);
        }
      }
    }
    return unitTypes.values();
  }
}
