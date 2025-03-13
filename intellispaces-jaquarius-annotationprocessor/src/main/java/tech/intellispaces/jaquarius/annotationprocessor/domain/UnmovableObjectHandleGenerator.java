package tech.intellispaces.jaquarius.annotationprocessor.domain;

import tech.intellispaces.commons.annotation.processor.ArtifactGeneratorContext;
import tech.intellispaces.commons.exception.UnexpectedExceptions;
import tech.intellispaces.commons.java.reflection.customtype.CustomType;
import tech.intellispaces.commons.java.reflection.method.MethodSignatureDeclarations;
import tech.intellispaces.commons.java.reflection.method.MethodStatement;
import tech.intellispaces.commons.java.reflection.reference.CustomTypeReference;
import tech.intellispaces.jaquarius.annotation.ObjectHandle;
import tech.intellispaces.jaquarius.annotation.Unmovable;
import tech.intellispaces.jaquarius.naming.NameConventionFunctions;
import tech.intellispaces.jaquarius.object.reference.MovabilityType;
import tech.intellispaces.jaquarius.object.reference.MovabilityTypes;
import tech.intellispaces.jaquarius.object.reference.ObjectForm;
import tech.intellispaces.jaquarius.object.reference.ObjectForms;
import tech.intellispaces.jaquarius.object.reference.UnmovableObjectHandle;
import tech.intellispaces.jaquarius.space.channel.ChannelFunctions;
import tech.intellispaces.jaquarius.space.domain.DomainFunctions;
import tech.intellispaces.jaquarius.traverse.TraverseType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class UnmovableObjectHandleGenerator extends AbstractObjectGenerator {

  private final List<Map<String, String>> movableMethods = new ArrayList<>();

  public UnmovableObjectHandleGenerator(CustomType domainType) {
    super(domainType);
  }

  @Override
  public boolean isRelevant(ArtifactGeneratorContext context) {
    return true;
  }

  @Override
  protected ObjectForm getObjectForm() {
    return ObjectForms.ObjectHandle;
  }

  @Override
  protected MovabilityType getMovabilityType() {
    return MovabilityTypes.Unmovable;
  }

  @Override
  public String generatedArtifactName() {
    return NameConventionFunctions.getUnmovableObjectHandleTypename(sourceArtifact().className(), false);
  }

  @Override
  protected String templateName() {
    return "/unmovable_object_handle.template";
  }

  @Override
  protected boolean analyzeSourceArtifact(ArtifactGeneratorContext context) {
    addImports(
        ObjectHandle.class,
        Unmovable.class,
        UnmovableObjectHandle.class,
        UnexpectedExceptions.class
    );

    analyzeDomain();
    analyzeAlias();
    analyzeObjectFormMethods(sourceArtifact(), context);
    analyzeConversionMethods(sourceArtifact());
    analyzeMovableMethods(context);

    addVariable("movableClassSimpleName", movableClassSimpleName());
    addVariable("handleTypeParamsBrief", typeParamsBrief);
    addVariable("handleTypeParamsFull", typeParamsFull);
    addVariable("domainTypeParamsBrief", domainTypeParamsBrief);
    addVariable("generalObjectHandle", generalObjectHandle);
    addVariable("conversionMethods", conversionMethods);
    addVariable("isAlias", isAlias);
    addVariable("primaryObjectHandle", baseObjectHandle);
    addVariable("movableMethods", movableMethods);
    addVariable("simpleObject", getSimpleObjectClassName());
    return true;
  }

  private String getSimpleObjectClassName() {
    return addImportAndGetSimpleName(NameConventionFunctions.getUnmovableSimpleObjectTypename(sourceArtifact().className(), false));
  }

  private void analyzeAlias() {
    Optional<CustomTypeReference> equivalentDomain = DomainFunctions.getAliasNearNeighbourDomain(sourceArtifact());
    isAlias = equivalentDomain.isPresent();
    if (isAlias) {
      baseObjectHandle = buildObjectFormDeclaration(equivalentDomain.get(), ObjectForms.ObjectHandle, MovabilityTypes.Unmovable, true);
    }
  }

  @Override
  protected Stream<MethodStatement> getObjectFormMethods(CustomType customType, ArtifactGeneratorContext context) {
    return super.getObjectFormMethods(customType, context)
        .filter(m -> ChannelFunctions.getTraverseTypes(m).stream().noneMatch(TraverseType::isMovingBased));
  }

  private void analyzeMovableMethods(ArtifactGeneratorContext context) {
    super.getObjectFormMethods(sourceArtifact(), context)
        .filter(m -> ChannelFunctions.getTraverseTypes(m).stream().anyMatch(TraverseType::isMovingBased))
        .map(this::generateMethod)
        .forEach(movableMethods::add);
  }

  private Map<String, String> generateMethod(MethodStatement method) {
    var returnTypeHandle = new StringBuilder();
    appendObjectFormMethodReturnType(returnTypeHandle, method);

    String declaration = MethodSignatureDeclarations.build(method)
        .returnType(returnTypeHandle.toString())
        .includeMethodTypeParams(true)
        .includeOwnerTypeParams(false)
        .get(this::addImport, this::addImportAndGetSimpleName);
    return Map.of(
        "javadoc", buildGeneratedMethodJavadoc(method.owner().canonicalName(), method),
        "declaration", declaration
    );
  }
}
