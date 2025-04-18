package tech.intellispaces.jaquarius.annotationprocessor.channel;

import com.google.auto.service.AutoService;
import tech.intellispaces.annotationprocessor.ArtifactGenerator;
import tech.intellispaces.annotationprocessor.ArtifactGeneratorContext;
import tech.intellispaces.annotationprocessor.ArtifactProcessor;
import tech.intellispaces.annotationprocessor.ArtifactValidator;
import tech.intellispaces.jaquarius.annotation.Channel;
import tech.intellispaces.jaquarius.annotationprocessor.AnnotationFunctions;
import tech.intellispaces.jaquarius.annotationprocessor.JaquariusArtifactProcessor;
import tech.intellispaces.reflection.customtype.CustomType;

import javax.annotation.processing.Processor;
import javax.lang.model.element.ElementKind;
import java.util.List;

@AutoService(Processor.class)
public class ChannelProcessor extends ArtifactProcessor {

  public ChannelProcessor() {
    super(ElementKind.INTERFACE, Channel.class, JaquariusArtifactProcessor.SOURCE_VERSION);
  }

  @Override
  public boolean isApplicable(CustomType channelType) {
    return AnnotationFunctions.isAutoGenerationEnabled(channelType);
  }

  @Override
  public ArtifactValidator validator() {
    return new ChannelValidator();
  }

  @Override
  public List<ArtifactGenerator> makeGenerators(CustomType channelType, ArtifactGeneratorContext context) {
    return ChannelProcessorFunctions.makeGenerators(channelType);
  }
}
