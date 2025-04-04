package tech.intellispaces.jaquarius.annotationprocessor.domain;

import tech.intellispaces.commons.annotation.processor.ArtifactGenerator;
import tech.intellispaces.commons.annotation.processor.ArtifactGeneratorContext;
import tech.intellispaces.commons.annotation.processor.ArtifactProcessor;
import tech.intellispaces.commons.annotation.processor.ArtifactValidator;
import tech.intellispaces.commons.object.Objects;
import tech.intellispaces.commons.reflection.customtype.CustomType;
import tech.intellispaces.commons.reflection.method.MethodStatement;
import tech.intellispaces.commons.reflection.reference.CustomTypeReference;
import tech.intellispaces.jaquarius.Jaquarius;
import tech.intellispaces.jaquarius.annotation.AnnotationProcessor;
import tech.intellispaces.jaquarius.annotation.Channel;
import tech.intellispaces.jaquarius.annotation.Ignore;
import tech.intellispaces.jaquarius.annotationprocessor.AnnotationFunctions;
import tech.intellispaces.jaquarius.artifact.ArtifactTypes;
import tech.intellispaces.jaquarius.naming.NameConventionFunctions;

import javax.annotation.processing.RoundEnvironment;
import java.util.ArrayList;
import java.util.List;

import static tech.intellispaces.commons.reflection.customtype.AnnotationFunctions.allAnnotationsOf;

public interface DomainProcessorFunctions {

  static List<ArtifactGenerator> makeDomainArtifactGenerators(
      CustomType domainType, ArtifactGeneratorContext context
  ) {
    if (domainType.selectAnnotation(Ignore.class).isPresent()) {
      return List.of();
    }

    var generators = new ArrayList<ArtifactGenerator>();
    for (MethodStatement method : domainType.declaredMethods()) {
      if (method.hasAnnotation(Channel.class)) {
        if (AnnotationFunctions.isAutoGenerationEnabled(
            domainType, ArtifactTypes.Channel, context.initialRoundEnvironment())
        ) {
          generators.add(new DomainChannelGenerator(domainType, method));
        }
      }
    }
    addSimpleObjectGenerators(domainType, generators, context.initialRoundEnvironment());
    addObjectHandleGenerators(domainType, generators, context.initialRoundEnvironment());
    addDownwardObjectHandleGenerators(domainType, generators);
    addAttachedAnnotationGenerators(domainType, generators, context);
    addObjectAssistantGenerators(domainType, generators);
    return generators;
  }

  private static void addAttachedAnnotationGenerators(
      CustomType annotatedType, List<ArtifactGenerator> generators, ArtifactGeneratorContext context
  ) {
    List<ArtifactProcessor> processors = allAnnotationsOf(annotatedType, AnnotationProcessor.class).stream()
        .map(AnnotationFunctions::getAnnotationProcessorClass)
        .distinct()
        .map(c -> (ArtifactProcessor) Objects.get(c))
        .toList();
    for (ArtifactProcessor processor : processors) {
      if (processor.isApplicable(annotatedType)) {
        ArtifactValidator validator = processor.validator();
        if (validator != null) {
          validator.validate(annotatedType);
        }
        generators.addAll(processor.makeGenerators(annotatedType, context));
      }
    }
  }

  private static void addSimpleObjectGenerators(
      CustomType domainType, List<ArtifactGenerator> generators, RoundEnvironment roundEnv
  ) {
    if (AnnotationFunctions.isAutoGenerationEnabled(domainType, ArtifactTypes.UndefinedPlainObject, roundEnv)) {
      generators.add(new UndefinedPlainObjectGenerator(domainType));
    }
    if (AnnotationFunctions.isAutoGenerationEnabled(domainType, ArtifactTypes.MovablePlainObject, roundEnv)) {
      generators.add(new MovablePlainObjectGenerator(domainType));
    }
    if (AnnotationFunctions.isAutoGenerationEnabled(domainType, ArtifactTypes.UnmovablePlainObject, roundEnv)) {
      generators.add(new UnmovablePlainObjectGenerator(domainType));
    }
  }

  private static void addObjectHandleGenerators(
      CustomType domainType, List<ArtifactGenerator> generators, RoundEnvironment roundEnv
  ) {
    if (
        AnnotationFunctions.isAutoGenerationEnabled(domainType, ArtifactTypes.UndefinedObjectHandle, roundEnv)
    ) {
      generators.add(new UndefinedObjectHandleGenerator(domainType));
    }
    if (
        AnnotationFunctions.isAutoGenerationEnabled(domainType, ArtifactTypes.MovableObjectHandle, roundEnv)
    ) {
      generators.add(new MovableObjectHandleGenerator(domainType));
    }
    if (
        AnnotationFunctions.isAutoGenerationEnabled(domainType, ArtifactTypes.UnmovableObjectHandle, roundEnv)
    ) {
      generators.add(new UnmovableObjectHandleGenerator(domainType));
    }
  }

  private static void addDownwardObjectHandleGenerators(
      CustomType domainType, List<ArtifactGenerator> generators
  ) {
    if (Jaquarius.settings().getKeyDomainByName(NameConventionFunctions.convertToDomainName(domainType.canonicalName())) != null) {
      return;
    }

    List<CustomTypeReference> superDomains = domainType.parentTypes();
    for (CustomTypeReference superDomain : superDomains) {
      generators.add(new UnmovableDownwardObjectGenerator(domainType, superDomain));
      generators.add(new MovableDownwardObjectGenerator(domainType, superDomain));
    }
  }

  private static void addObjectAssistantGenerators(
      CustomType domainType, List<ArtifactGenerator> generators
  ) {
    generators.add(new ObjectAssistantBrokerGenerator(domainType));
    generators.add(new ObjectAssistantGenerator(domainType));
  }
}
