package tech.intellispaces.framework.core.annotation.processor.ontology;

import com.google.auto.service.AutoService;
import tech.intellispaces.framework.annotationprocessor.generator.ArtifactGenerator;
import tech.intellispaces.framework.annotationprocessor.validator.AnnotatedTypeValidator;
import tech.intellispaces.framework.core.annotation.Ontology;
import tech.intellispaces.framework.core.annotation.Transition;
import tech.intellispaces.framework.core.annotation.processor.AbstractAnnotationProcessor;
import tech.intellispaces.framework.javastatements.statement.custom.CustomType;
import tech.intellispaces.framework.javastatements.statement.method.MethodStatement;

import javax.annotation.processing.Processor;
import javax.lang.model.element.ElementKind;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AutoService(Processor.class)
public class OntologyAnnotationProcessor extends AbstractAnnotationProcessor {

  public OntologyAnnotationProcessor() {
    super(Ontology.class, Set.of(ElementKind.INTERFACE));
  }

  @Override
  protected boolean isApplicable(CustomType ontologyType) {
    return isAutoGenerationEnabled(ontologyType);
  }

  @Override
  protected AnnotatedTypeValidator getValidator() {
    return null;
  }

  @Override
  protected List<ArtifactGenerator> makeArtifactGenerators(CustomType ontologyType) {
    List<ArtifactGenerator> generators = new ArrayList<>();
    for (MethodStatement method : ontologyType.declaredMethods()) {
      if (method.hasAnnotation(Transition.class)) {
        if (isAutoGenerationEnabled(ontologyType, "Transition")) {
          generators.add(new OntologyTransitionGenerator(ontologyType, method));
        }
        if (isAutoGenerationEnabled(ontologyType, "Guide")) {
          generators.add(new OntologyGuideGenerator(ontologyType, method));
        }
      }
    }
    return generators;
  }
}
