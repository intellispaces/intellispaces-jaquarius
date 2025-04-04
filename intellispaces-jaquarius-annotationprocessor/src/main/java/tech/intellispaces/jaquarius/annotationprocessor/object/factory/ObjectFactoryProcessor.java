package tech.intellispaces.jaquarius.annotationprocessor.object.factory;

import com.google.auto.service.AutoService;
import tech.intellispaces.commons.annotation.processor.ArtifactGenerator;
import tech.intellispaces.commons.annotation.processor.ArtifactGeneratorContext;
import tech.intellispaces.commons.annotation.processor.ArtifactProcessor;
import tech.intellispaces.commons.annotation.processor.ArtifactValidator;
import tech.intellispaces.commons.reflection.customtype.CustomType;
import tech.intellispaces.jaquarius.annotation.ObjectFactory;
import tech.intellispaces.jaquarius.annotationprocessor.AnnotationFunctions;
import tech.intellispaces.jaquarius.annotationprocessor.JaquariusArtifactProcessor;

import javax.annotation.processing.Processor;
import javax.lang.model.element.ElementKind;
import java.util.List;

@AutoService(Processor.class)
public class ObjectFactoryProcessor extends ArtifactProcessor {
  private final ObjectFactoryMetaInfGenerator metaInfGenerator = new ObjectFactoryMetaInfGenerator();

  public ObjectFactoryProcessor() {
    super(ElementKind.CLASS, ObjectFactory.class, JaquariusArtifactProcessor.SOURCE_VERSION);
  }

  @Override
  public boolean isApplicable(CustomType objectFactoryType) {
    return AnnotationFunctions.isAutoGenerationEnabled(objectFactoryType);
  }

  @Override
  public ArtifactValidator validator() {
    return null;
  }

  @Override
  public List<ArtifactGenerator> makeGenerators(CustomType objectFactoryType, ArtifactGeneratorContext context) {
    return List.of(
        metaInfGenerator,
        new ObjectFactoryWrapperGenerator(objectFactoryType, metaInfGenerator)
    );
  }
}
