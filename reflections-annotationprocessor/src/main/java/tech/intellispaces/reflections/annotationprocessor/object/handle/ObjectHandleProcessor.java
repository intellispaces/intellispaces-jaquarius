package tech.intellispaces.reflections.annotationprocessor.object.handle;

import java.util.List;
import javax.annotation.processing.Processor;
import javax.lang.model.element.ElementKind;

import com.google.auto.service.AutoService;

import tech.intellispaces.annotationprocessor.ArtifactGenerator;
import tech.intellispaces.annotationprocessor.ArtifactGeneratorContext;
import tech.intellispaces.annotationprocessor.ArtifactProcessor;
import tech.intellispaces.annotationprocessor.ArtifactValidator;
import tech.intellispaces.reflections.framework.annotation.ObjectHandle;
import tech.intellispaces.reflections.annotationprocessor.AnnotationFunctions;
import tech.intellispaces.reflections.annotationprocessor.JaquariusArtifactProcessor;
import tech.intellispaces.jstatements.customtype.CustomType;

@AutoService(Processor.class)
public class ObjectHandleProcessor extends ArtifactProcessor {

  public ObjectHandleProcessor() {
    super(ElementKind.CLASS, ObjectHandle.class, JaquariusArtifactProcessor.SOURCE_VERSION);
  }

  @Override
  public boolean isApplicable(CustomType objectHandleType) {
    return objectHandleType.isAbstract() && AnnotationFunctions.isAutoGenerationEnabled(objectHandleType);
  }

  @Override
  public ArtifactValidator validator() {
    return null;
  }

  @Override
  public List<ArtifactGenerator> makeGenerators(CustomType objectHandleType, ArtifactGeneratorContext context) {
    return ObjectHandleProcessorFunctions.makeObjectHandleArtifactGenerators(objectHandleType);
  }
}
