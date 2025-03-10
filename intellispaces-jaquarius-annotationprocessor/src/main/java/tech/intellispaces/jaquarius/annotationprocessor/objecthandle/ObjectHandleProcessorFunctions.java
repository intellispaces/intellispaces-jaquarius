package tech.intellispaces.jaquarius.annotationprocessor.objecthandle;

import tech.intellispaces.commons.annotation.processor.ArtifactGenerator;
import tech.intellispaces.commons.exception.UnexpectedExceptions;
import tech.intellispaces.commons.java.reflection.customtype.CustomType;
import tech.intellispaces.jaquarius.object.reference.ObjectHandleFunctions;

import java.util.List;

public interface ObjectHandleProcessorFunctions {

  static List<ArtifactGenerator> makeObjectHandleArtifactGenerators(
      CustomType objectHandleType
  ) {
    if (ObjectHandleFunctions.isUnmovableObjectHandle(objectHandleType)) {
      return List.of(new UnmovableObjectWrapperGenerator(objectHandleType));
    } else if (ObjectHandleFunctions.isMovableObjectHandle(objectHandleType)) {
      return List.of(new MovableObjectWrapperGenerator(objectHandleType));
    } else {
      throw UnexpectedExceptions.withMessage("Could not define movable type of the object handle {0}",
          objectHandleType.canonicalName());
    }
  }
}
