package tech.intellispaces.jaquarius.annotation.processor.preprocessing;

import com.google.auto.service.AutoService;
import tech.intellispaces.jaquarius.annotation.Preprocessing;
import tech.intellispaces.jaquarius.annotation.processor.AnnotationProcessorFunctions;
import tech.intellispaces.java.annotation.AnnotatedTypeProcessor;
import tech.intellispaces.java.annotation.generator.Generator;
import tech.intellispaces.java.annotation.validator.AnnotatedTypeValidator;
import tech.intellispaces.java.reflection.customtype.CustomType;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ElementKind;
import java.util.List;
import java.util.Set;

@AutoService(Processor.class)
public class PreprocessingAnnotationProcessor extends AnnotatedTypeProcessor {

  public PreprocessingAnnotationProcessor() {
    super(Preprocessing.class, Set.of(ElementKind.CLASS, ElementKind.INTERFACE));
  }

  @Override
  public boolean isApplicable(CustomType customType) {
    return AnnotationProcessorFunctions.isAutoGenerationEnabled(customType);
  }

  @Override
  public AnnotatedTypeValidator getValidator() {
    return null;
  }

  @Override
  public List<Generator> makeGenerators(CustomType initiatorType, CustomType customType, RoundEnvironment roundEnv) {
    return AnnotationProcessorFunctions.makePreprocessingArtifactGenerators(initiatorType, customType, roundEnv);
  }
}
