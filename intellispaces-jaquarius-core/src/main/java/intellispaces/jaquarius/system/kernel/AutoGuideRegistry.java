package intellispaces.jaquarius.system.kernel;

import intellispaces.common.base.exception.UnexpectedExceptions;
import intellispaces.common.base.object.ObjectFunctions;
import intellispaces.common.base.type.ClassFunctions;
import intellispaces.jaquarius.common.NameConventionFunctions;

import java.util.Map;
import java.util.WeakHashMap;

class AutoGuideRegistry {
  private final Map<Class<?>, Object> autoGuide = new WeakHashMap<>();

  @SuppressWarnings("unchecked")
  public <G> G getAutoGuide(Class<G> guideClass) {
    Object guide = autoGuide.get(guideClass);
    if (guide == null) {
      guide = createAutoGuide(guideClass);
      autoGuide.put(guideClass, guide);
    }
    return (G) guide;
  }

  @SuppressWarnings("unchecked")
  private <G> G createAutoGuide(Class<G> guideClass) {
    String autoGuideCanonicalName = NameConventionFunctions.getAutoGuiderCanonicalName(guideClass.getName());
    Class<G> autoGuideClass = (Class<G>) ClassFunctions.getClass(autoGuideCanonicalName)
        .orElseThrow(() -> UnexpectedExceptions.withMessage("Could not load auto guide class by name {0}",
            autoGuideCanonicalName)
        );
    return ObjectFunctions.newInstance(autoGuideClass);
  }
}
