package intellispaces.jaquarius.validation;

import intellispaces.common.annotationprocessor.validator.AnnotatedTypeValidator;
import intellispaces.common.javastatement.customtype.CustomType;
import intellispaces.jaquarius.annotation.Ontology;
import intellispaces.jaquarius.exception.IntelliSpacesException;

import java.util.Optional;

/**
 * Channel type validator.
 */
public class ChannelValidator implements AnnotatedTypeValidator {

  @Override
  public void validate(CustomType channelType) {
    validateName(channelType);
    validateEnclosingType(channelType);
    validateMethods(channelType);
  }

  private void validateName(CustomType channelType) {
    if (!channelType.simpleName().endsWith("Channel")) {
      throw IntelliSpacesException.withMessage("Channel interface name must end with ''Channel''. Check class {0}\"",
          channelType.canonicalName());
    }
  }

  private void validateEnclosingType(CustomType channelType) {
    Optional<CustomType> enclosingType = channelType.enclosingType();
    if (enclosingType.isPresent() && !enclosingType.get().hasAnnotation(Ontology.class)) {
      throw IntelliSpacesException.withMessage("Channel interface can only be nested to ontology interface. " +
          "Check class {0}", channelType.canonicalName());
    }
  }

  private void validateMethods(CustomType channelType) {
    int numDeclaredMethods = channelType.declaredMethods().size();
    if (numDeclaredMethods == 0) {
      throw IntelliSpacesException.withMessage("Channel interface should contain one declared method. Check class {0}\"",
          channelType.canonicalName());
    } else if (numDeclaredMethods > 1) {
      throw IntelliSpacesException.withMessage("Channel interface should contain one declared method only. Check class {0}\"",
          channelType.canonicalName());
    }
  }
}
