package tech.intellispaces.reflections.annotationprocessor.domain;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import tech.intellispaces.annotationprocessor.ArtifactGeneratorContext;
import tech.intellispaces.commons.exception.UnexpectedExceptions;
import tech.intellispaces.reflections.framework.ArtifactType;
import tech.intellispaces.reflections.framework.annotation.Movable;
import tech.intellispaces.reflections.framework.annotation.ObjectHandle;
import tech.intellispaces.reflections.framework.annotation.Unmovable;
import tech.intellispaces.reflections.framework.artifact.ArtifactTypes;
import tech.intellispaces.reflections.framework.naming.NameConventionFunctions;
import tech.intellispaces.reflections.framework.object.reference.MovabilityType;
import tech.intellispaces.reflections.framework.object.reference.MovabilityTypes;
import tech.intellispaces.reflections.framework.object.reference.ObjectReferenceForm;
import tech.intellispaces.reflections.framework.object.reference.ObjectReferenceForms;
import tech.intellispaces.reflections.framework.object.reference.UnmovableObjectHandle;
import tech.intellispaces.reflections.framework.space.channel.ChannelFunctions;
import tech.intellispaces.reflections.framework.space.domain.DomainFunctions;
import tech.intellispaces.reflections.framework.traverse.TraverseType;
import tech.intellispaces.jstatements.customtype.CustomType;
import tech.intellispaces.jstatements.method.MethodStatement;
import tech.intellispaces.jstatements.reference.CustomTypeReference;
import tech.intellispaces.jstatements.reference.TypeReference;

public class UnmovableRegularObjectGenerator extends AbstractRegularObjectGenerator {

  public UnmovableRegularObjectGenerator(CustomType domainType) {
    super(domainType);
  }

  @Override
  public boolean isRelevant(ArtifactGeneratorContext context) {
    return true;
  }

  @Override
  protected ObjectReferenceForm getForm() {
    return ObjectReferenceForms.Regular;
  }

  @Override
  protected MovabilityType getMovabilityType() {
    return MovabilityTypes.Unmovable;
  }

  @Override
  protected List<ArtifactType> relatedArtifactTypes() {
    return List.of(ArtifactTypes.UnmovableRegularObject);
  }

  @Override
  public String generatedArtifactName() {
    return NameConventionFunctions.getUnmovableRegularObjectTypename(sourceArtifact().className(), false);
  }

  @Override
  protected String templateName() {
    return "/unmovable_regular_object.template";
  }

  @Override
  protected boolean analyzeSourceArtifact(ArtifactGeneratorContext context) {
    addImports(
        ObjectHandle.class,
        Unmovable.class,
        UnmovableObjectHandle.class,
        UnexpectedExceptions.class
    );

    analyzeAlias();
    analyzeDomain();
    analyzeObjectFormMethods(sourceArtifact(), context);
    analyzeConversionMethods(sourceArtifact());

    addVariable("handleTypeParamsBrief", typeParamsBrief);
    addVariable("handleTypeParamsFull", typeParamsFull);
    addVariable("generalPureObjectHandle", getGeneralOriginHandleClassName());
    addVariable("isAlias", isAlias);
    addVariable("primaryObject", baseObjectHandle);
    return true;
  }

  protected void analyzeAlias() {
    Optional<CustomTypeReference> equivalentDomain = DomainFunctions.getAliasNearNeighbourDomain(sourceArtifact());
    isAlias = equivalentDomain.isPresent();
    if (isAlias) {
      baseObjectHandle = buildObjectFormDeclaration(equivalentDomain.get(), ObjectReferenceForms.Regular, MovabilityTypes.Unmovable, true);
    }
  }

  @Override
  protected Stream<MethodStatement> getObjectFormMethods(CustomType customType, ArtifactGeneratorContext context) {
    return super.getObjectFormMethods(customType, context)
        .filter(m -> ChannelFunctions.getTraverseTypes(m).stream().noneMatch(TraverseType::isMovingBased));
  }

  @Override
  protected void appendObjectFormMethodReturnType(StringBuilder sb, MethodStatement method) {
    TypeReference domainReturnType = method.returnType().orElseThrow();
    if (
        ChannelFunctions.getTraverseTypes(method).stream().anyMatch(TraverseType::isMoving)
            || method.hasAnnotation(Movable.class)
    ) {
      sb.append(buildObjectFormDeclaration(domainReturnType, ObjectReferenceForms.Regular, MovabilityTypes.Movable, true));
    } else if (method.hasAnnotation(Unmovable.class)) {
      sb.append(buildObjectFormDeclaration(domainReturnType, ObjectReferenceForms.Regular, MovabilityTypes.Unmovable, true));
    } else {
      sb.append(buildObjectFormDeclaration(domainReturnType, ObjectReferenceForms.Regular, MovabilityTypes.General, true));
    }
  }
}
