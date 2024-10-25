package intellispaces.framework.core.annotation.processor;

import intellispaces.common.javastatement.customtype.CustomType;
import intellispaces.common.javastatement.method.MethodStatement;
import intellispaces.common.javastatement.reference.TypeReference;
import intellispaces.framework.core.annotation.Channel;
import intellispaces.framework.core.space.channel.ChannelFunctions;
import intellispaces.framework.core.space.channel.MappingChannel;
import intellispaces.framework.core.space.channel.MappingOfMovingChannel;
import intellispaces.framework.core.space.channel.MovingChannel;
import intellispaces.framework.core.traverse.TraverseType;

import javax.annotation.processing.RoundEnvironment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractChannelGenerator extends AbstractGenerator {
  protected final MethodStatement channelMethod;
  private String channelMethodSignature;
  private String channelClasses;

  public AbstractChannelGenerator(
      CustomType initiatorType, CustomType annotatedType, MethodStatement channelMethod
  ) {
    super(initiatorType, annotatedType);
    this.channelMethod = channelMethod;
  }

  protected abstract String getChannelClassCanonicalName();

  protected abstract String getChannelMethodSignature();

  protected abstract List<TypeReference> getQualifierTypes();

  @Override
  public String artifactName() {
    return getChannelClassCanonicalName();
  }

  @Override
  protected String templateName() {
    return "/channel.template";
  }

  @Override
  protected Map<String, Object> templateVariables() {
    Map<String, Object> vars = new HashMap<>();
    vars.put("generatedAnnotation", makeGeneratedAnnotation());
    vars.put("packageName", context.packageName());
    vars.put("sourceClassName", sourceClassCanonicalName());
    vars.put("sourceClassSimpleName", sourceClassSimpleName());
    vars.put("targetClassLink", AnnotationProcessorFunctions.getDomainClassLink(channelMethod.returnType().orElseThrow()));
    vars.put("classSimpleName", context.generatedClassSimpleName());
    vars.put("importedClasses", context.getImports());
    vars.put("channelMethod", channelMethodSignature);
    vars.put("channelClasses", channelClasses);
    vars.put("channelMethodName", channelMethod.name());
    vars.put("cid", getCid());
    return vars;
  }

  @Override
  protected boolean analyzeAnnotatedType(RoundEnvironment roundEnv) {
    context.generatedClassCanonicalName(artifactName());
    if (annotatedType.isNested()) {
      context.addImport(sourceClassCanonicalName());
    }
    context.addImport(Channel.class);

    channelClasses = defineChannelClass();
    channelMethodSignature = getChannelMethodSignature();
    return true;
  }

  private String defineChannelClass() {
    var sb = new StringBuilder();
    sb.append(context.addToImportAndGetSimpleName(
        ChannelFunctions.getChannelClass(getQualifierTypes().size())
    ));
    for (TraverseType traverseType : getTraverseTypes()) {
      if (traverseType.isMapping()) {
        sb.append(", ");
        sb.append(context.addToImportAndGetSimpleName(MappingChannel.class));
      } else if (traverseType.isMoving()) {
          sb.append(", ");
          sb.append(context.addToImportAndGetSimpleName(MovingChannel.class));
      } else if (traverseType.isMappingOfMoving()) {
        sb.append(", ");
        sb.append(context.addToImportAndGetSimpleName(MappingOfMovingChannel.class));
      }
    }
    return sb.toString();
  }

  private String getCid() {
    return channelMethod.selectAnnotation(Channel.class).orElseThrow().value();
  }

  private List<TraverseType> getTraverseTypes() {
    return ChannelFunctions.getTraverseTypes(channelMethod);
  }
}
