package tech.intellispaces.jaquarius.annotationprocessor.object.factory;

import tech.intellispaces.commons.annotation.processor.Artifact;
import tech.intellispaces.commons.annotation.processor.ArtifactGenerator;
import tech.intellispaces.commons.annotation.processor.ArtifactGeneratorContext;
import tech.intellispaces.commons.annotation.processor.ArtifactImpl;
import tech.intellispaces.commons.annotation.processor.ArtifactKinds;
import tech.intellispaces.jaquarius.naming.NameConventionFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ObjectFactoryMetaInfGenerator implements ArtifactGenerator {
  private final List<String> objectFactories = new ArrayList<>();

  public ObjectFactoryMetaInfGenerator() {
  }

  @Override
  public boolean isRelevant(ArtifactGeneratorContext context) {
    return context.isOverRound();
  }

  @Override
  public String generatedArtifactName() {
    return NameConventionFunctions.getObjectFactoriesResourceName();
  }

  public void addObjectFactory(String objectFactory) {
    objectFactories.add(objectFactory);
  }

  @Override
  public Optional<Artifact> generate(ArtifactGeneratorContext context) {
    if (objectFactories.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(new ArtifactImpl(
        ArtifactKinds.ResourceFile,
        generatedArtifactName(),
        String.join("\n", objectFactories).toCharArray()
    ));
  }
}
