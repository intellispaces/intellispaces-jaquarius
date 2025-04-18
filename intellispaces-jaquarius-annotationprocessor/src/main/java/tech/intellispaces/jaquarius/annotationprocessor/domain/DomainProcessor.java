package tech.intellispaces.jaquarius.annotationprocessor.domain;

import com.google.auto.service.AutoService;
import tech.intellispaces.annotationprocessor.ArtifactGenerator;
import tech.intellispaces.annotationprocessor.ArtifactGeneratorContext;
import tech.intellispaces.annotationprocessor.ArtifactProcessor;
import tech.intellispaces.annotationprocessor.ArtifactValidator;
import tech.intellispaces.jaquarius.annotation.Domain;
import tech.intellispaces.jaquarius.annotationprocessor.AnnotationFunctions;
import tech.intellispaces.jaquarius.annotationprocessor.JaquariusArtifactProcessor;
import tech.intellispaces.reflection.customtype.CustomType;

import javax.annotation.processing.Processor;
import javax.lang.model.element.ElementKind;
import java.util.List;
import java.util.Optional;

@AutoService(Processor.class)
public class DomainProcessor extends ArtifactProcessor {
  private int index;

  public DomainProcessor() {
    super(ElementKind.INTERFACE, Domain.class, JaquariusArtifactProcessor.SOURCE_VERSION);
  }

  @Override
  public boolean isApplicable(CustomType domainType) {
    return AnnotationFunctions.isAutoGenerationEnabled(domainType);
  }

  @Override
  public ArtifactValidator validator() {
    return new DomainValidator();
  }

  @Override
  public List<ArtifactGenerator> makeGenerators(CustomType domainType, ArtifactGeneratorContext context) {
    return DomainProcessorFunctions.makeDomainArtifactGenerators(domainType, context);
  }

  @Override
  protected Optional<ArtifactGenerator> penaltyRoundArtifactGenerator() {
    return Optional.of(new PenaltyRoundDomainGenerator(index++));
  }
}
