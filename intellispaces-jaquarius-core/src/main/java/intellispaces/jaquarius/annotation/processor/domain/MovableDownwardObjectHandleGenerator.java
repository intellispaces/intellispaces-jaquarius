package intellispaces.jaquarius.annotation.processor.domain;

import intellispaces.common.annotationprocessor.context.AnnotationProcessingContext;
import intellispaces.common.base.text.TextFunctions;
import intellispaces.common.base.type.Type;
import intellispaces.common.javastatement.customtype.CustomType;
import intellispaces.common.javastatement.method.MethodStatement;
import intellispaces.common.javastatement.reference.CustomTypeReference;
import intellispaces.common.javastatement.reference.CustomTypeReferences;
import intellispaces.common.javastatement.type.Types;
import intellispaces.jaquarius.annotation.Channel;
import intellispaces.jaquarius.annotation.ObjectHandle;
import intellispaces.jaquarius.channel.Channel0;
import intellispaces.jaquarius.channel.Channel1;
import intellispaces.jaquarius.channel.ChannelMethod0;
import intellispaces.jaquarius.channel.ChannelMethod1;
import intellispaces.jaquarius.channel.MappingChannel;
import intellispaces.jaquarius.channel.MappingOfMovingChannel;
import intellispaces.jaquarius.common.NameConventionFunctions;
import intellispaces.jaquarius.exception.TraverseException;
import intellispaces.jaquarius.guide.GuideForm;
import intellispaces.jaquarius.object.ObjectFunctions;
import intellispaces.jaquarius.object.ObjectHandleTypes;
import intellispaces.jaquarius.space.domain.DomainFunctions;

import javax.annotation.processing.RoundEnvironment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class MovableDownwardObjectHandleGenerator extends AbstractConversionDomainObjectHandleGenerator {
  private String classTypeParams;
  private String classTypeParamsBrief;
  private String movableObjectHandleName;
  private String childDomainClassSimpleName;
  private String childObjectHandleType;
  private String parentDomainClassSimpleName;
  private String domainClassSimpleName;
  protected String domainTypeParams;
  protected String domainTypeParamsBrief;
  protected String domainTypeArguments;
  private boolean isAlias;
  private String primaryDomainSimpleName;
  private String primaryDomainTypeArguments;
  private String domainType;

  public MovableDownwardObjectHandleGenerator(
      CustomType initiatorType, CustomType annotatedType, CustomTypeReference parentDomainType
  ) {
    super(initiatorType, annotatedType, parentDomainType);
  }

  @Override
  public boolean isRelevant(AnnotationProcessingContext context) {
    return true;
  }

  @Override
  protected ObjectHandleTypes getObjectHandleType() {
    return ObjectHandleTypes.Movable;
  }

  @Override
  public String artifactName() {
    return NameConventionFunctions.getMovableDownwardObjectHandleTypename(annotatedType, parentDomainType.targetType());
  }

  @Override
  protected String templateName() {
    return "/movable_downward_object_handle.template";
  }

  @Override
  protected Map<String, Object> templateVariables() {
    Map<String, Object> vars = new HashMap<>();
    vars.put("generatedAnnotation", makeGeneratedAnnotation());
    vars.put("packageName", context.packageName());
    vars.put("sourceClassName", sourceClassCanonicalName());
    vars.put("sourceClassSimpleName", sourceClassSimpleName());
    vars.put("classSimpleName", context.generatedClassSimpleName());
    vars.put("classTypeParams", classTypeParams);
    vars.put("classTypeParamsBrief", classTypeParamsBrief);
    vars.put("domainTypeParams", domainTypeParams);
    vars.put("domainTypeParamsBrief", domainTypeParamsBrief);
    vars.put("domainTypeArguments", domainTypeArguments);
    vars.put("childDomainClassSimpleName", childDomainClassSimpleName);
    vars.put("parentDomainClassSimpleName", parentDomainClassSimpleName);
    vars.put("childObjectHandleType", childObjectHandleType);
    vars.put("childField", childFieldName);
    vars.put("methods", methods);
    vars.put("importedClasses", context.getImports());
    vars.put("movableObjectHandleName", movableObjectHandleName);
    vars.put("domainClassSimpleName", domainClassSimpleName);
    vars.put("isAlias", isAlias);
    vars.put("primaryDomainSimpleName", primaryDomainSimpleName);
    vars.put("primaryDomainTypeArguments", primaryDomainTypeArguments);
    vars.put("domainType", domainType);
    return vars;
  }

  @Override
  protected boolean analyzeAnnotatedType(RoundEnvironment roundEnv) {
    context.generatedClassCanonicalName(artifactName());
    context.addImport(Type.class);
    context.addImport(Types.class);
    context.addImport(ObjectHandle.class);
    context.addImport(ObjectFunctions.class);
    context.addImport(TraverseException.class);
    context.addImport(Channel0.class);
    context.addImport(Channel1.class);
    context.addImport(ChannelMethod0.class);
    context.addImport(ChannelMethod1.class);
    context.addImport(MappingChannel.class);
    context.addImport(MappingOfMovingChannel.class);

    movableObjectHandleName = context.addToImportAndGetSimpleName(
        NameConventionFunctions.getMovableObjectHandleTypename(parentDomainType.targetType().className()));
    domainClassSimpleName = context.addToImportAndGetSimpleName(parentDomainType.targetType().canonicalName());

    domainTypeParams = parentDomainType.targetType().typeParametersFullDeclaration();
    domainTypeParamsBrief = parentDomainType.targetType().typeParametersBriefDeclaration();

    classTypeParams = annotatedType.typeParametersFullDeclaration();
    classTypeParamsBrief = annotatedType.typeParametersBriefDeclaration();
    domainTypeArguments = parentDomainType.typeArgumentsDeclaration(context::addToImportAndGetSimpleName);
    childFieldName = TextFunctions.lowercaseFirstLetter(TextFunctions.removeTailOrElseThrow(annotatedType.simpleName(), "Domain"));
    childObjectHandleType = getChildObjectHandleType();
    childDomainClassSimpleName = annotatedType.simpleName();
    parentDomainClassSimpleName = context.addToImportAndGetSimpleName(parentDomainType.targetType().canonicalName());

    CustomType actualParentDomainType = buildActualType(parentDomainType.targetType(), roundEnv);
    CustomTypeReference actualParentDomainTypeReference = CustomTypeReferences.get(actualParentDomainType, parentDomainType.typeArguments());
    CustomType effectiveActualParentDomainType = actualParentDomainTypeReference.effectiveTargetType();

    analyzeObjectHandleMethods(effectiveActualParentDomainType, roundEnv);

    Optional<CustomTypeReference> mainEquivalentDomain = DomainFunctions.getAliasBaseDomain(annotatedType);
    isAlias = mainEquivalentDomain.isPresent();
    if (isAlias) {
      primaryDomainSimpleName = context.addToImportAndGetSimpleName(
          mainEquivalentDomain.get().targetType().canonicalName()
      );
      primaryDomainTypeArguments = mainEquivalentDomain.get().typeArgumentsDeclaration(
          context::addToImportAndGetSimpleName
      );
      domainType = buildDomainType(mainEquivalentDomain.get().targetType(), mainEquivalentDomain.get().typeArguments());
    } else {
      domainType = buildDomainType(parentDomainType.targetType(), (List) annotatedType.typeParameters());
    }
    return true;
  }

  @Override
  protected Stream<MethodStatement> getObjectHandleMethods(
      CustomType customType, RoundEnvironment roundEnv
  ) {
    return buildActualType(parentDomainType.targetType(), roundEnv).actualMethods().stream()
        .filter(m -> excludeDeepConversionMethods(m, customType))
        .filter(this::isNotDomainClassGetter);
  }

  @Override
  protected Map<String, String> generateMethod(MethodStatement method, GuideForm guideForm, int methodIndex) {
    if (method.hasAnnotation(Channel.class)) {
      return generateMethodNormal(method, guideForm, methodIndex);
    } else {
      return generatePrototypeMethod(convertMethodBeforeGenerate(method));
    }
  }

  protected Map<String, String> generateMethodNormal(MethodStatement method, GuideForm guideForm, int methodIndex) {
    var sb = new StringBuilder();
    sb.append("public ");
    appendMethodTypeParameters(sb, method);
    appendMethodReturnHandleType(sb, method, guideForm);
    sb.append(" ");
    sb.append(getMethodName(method, guideForm));
    sb.append("(");
    appendMethodParameters(sb, method);
    sb.append(")");
    appendMethodExceptions(sb, method);
    sb.append(" {\n");
    sb.append("  ");
    buildReturnStatement(sb, method, guideForm);
    sb.append("\n}\n");
    return Map.of(
        "javadoc", buildGeneratedMethodJavadoc(method.owner().canonicalName(), method),
        "declaration", sb.toString()
    );
  }

  private Map<String, String> generatePrototypeMethod(MethodStatement method) {
    var sb = new StringBuilder();
    sb.append("public ");
    appendMethodTypeParameters(sb, method);
    appendMethodReturnType(sb, method);
    sb.append(" ");
    sb.append(method.name());
    sb.append("(");
    appendMethodParameters(sb, method);
    sb.append(")");
    appendMethodExceptions(sb, method);
    sb.append(" {\n");
    sb.append("  return this.")
        .append(childFieldName)
        .append(".")
        .append(method.name())
        .append("();\n");
    sb.append("}");
    return Map.of(
        "javadoc", buildGeneratedMethodJavadoc(method.owner().canonicalName(), method),
        "declaration", sb.toString()
    );
  }

  private String getChildObjectHandleType() {
    return context.addToImportAndGetSimpleName(
        NameConventionFunctions.getMovableObjectHandleTypename(annotatedType.className())
    );
  }
}
