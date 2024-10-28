package intellispaces.jaquarius.processor.data;

import intellispaces.common.annotationprocessor.AnnotatedTypeProcessor;
import intellispaces.common.annotationprocessor.generator.Generator;
import intellispaces.common.annotationprocessor.validator.AnnotatedTypeValidator;
import intellispaces.common.javastatement.customtype.CustomType;
import intellispaces.jaquarius.annotation.Data;
import intellispaces.jaquarius.processor.AnnotationProcessorFunctions;
import intellispaces.jaquarius.validation.DataValidator;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ElementKind;
import java.util.List;
import java.util.Set;

public class DataAnnotationProcessor extends AnnotatedTypeProcessor {

  public DataAnnotationProcessor() {
    super(Data.class, Set.of(ElementKind.INTERFACE));
  }

  @Override
  public boolean isApplicable(CustomType dataType) {
    return AnnotationProcessorFunctions.isAutoGenerationEnabled(dataType);
  }

  @Override
  public AnnotatedTypeValidator getValidator() {
    return new DataValidator();
  }

  @Override
  public List<Generator> makeGenerators(CustomType initiatorType, CustomType dataType, RoundEnvironment roundEnv) {
    return AnnotationProcessorFunctions.makeDataArtifactGenerators(initiatorType, dataType);
  }
}
