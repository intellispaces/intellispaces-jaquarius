package tech.intellispaces.jaquarius.annotationprocessor.ontology;

import tech.intellispaces.annotationprocessor.ArtifactGenerator;
import tech.intellispaces.annotationprocessor.ArtifactGeneratorContext;
import tech.intellispaces.jaquarius.annotation.Channel;
import tech.intellispaces.jaquarius.annotationprocessor.AnnotationFunctions;
import tech.intellispaces.jaquarius.artifact.ArtifactTypes;
import tech.intellispaces.reflection.customtype.CustomType;
import tech.intellispaces.reflection.method.MethodStatement;

import java.util.ArrayList;
import java.util.List;

public interface OntologyProcessorFunctions {

  static List<ArtifactGenerator> makeOntologyArtifactGenerators(
      CustomType ontologyType, ArtifactGeneratorContext context
  ) {
    List<ArtifactGenerator> generators = new ArrayList<>();
    for (MethodStatement method : ontologyType.declaredMethods()) {
      if (!method.hasAnnotation(Channel.class)) {
        continue;
      }
      if (AnnotationFunctions.isAutoGenerationEnabled(
          ontologyType, ArtifactTypes.Channel, context.initialRoundEnvironment()
      )) {
        generators.add(new OntologyChannelGenerator(
            ontologyType, method
        ));
      }
    }
    return generators;
  }
}
