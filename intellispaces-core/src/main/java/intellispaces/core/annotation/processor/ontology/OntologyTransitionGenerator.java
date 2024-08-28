package intellispaces.core.annotation.processor.ontology;

import intellispaces.core.annotation.processor.AbstractTransitionGenerator;
import intellispaces.core.common.NameConventionFunctions;
import intellispaces.javastatements.Statement;
import intellispaces.javastatements.customtype.CustomType;
import intellispaces.javastatements.method.MethodParam;
import intellispaces.javastatements.method.MethodStatement;
import intellispaces.javastatements.reference.TypeReference;

import java.util.List;
import java.util.stream.Collectors;

public class OntologyTransitionGenerator extends AbstractTransitionGenerator {

  public OntologyTransitionGenerator(CustomType ontologyType, MethodStatement transitionMethod) {
    super(ontologyType, transitionMethod);
  }

  @Override
  protected String getTransitionClassTypeParams() {
    if (transitionMethod.typeParameters().isEmpty()) {
      return "";
    }
    return "<" + transitionMethod.typeParameters().stream()
        .map(p -> p.actualDeclaration(context::addToImportAndGetSimpleName))
        .collect(Collectors.joining(", ")) + ">";
  }

  @Override
  protected String getTransitionMethodSignature() {
    return buildMethodSignature(
        transitionMethod,
        transitionMethod.name(),
        false,
        false,
        List.of()
    );
  }

  @Override
  protected String getTransitionClassCanonicalName() {
    return NameConventionFunctions.getTransitionClassCanonicalName(transitionMethod);
  }

  @Override
  protected Statement getSourceType() {
    return transitionMethod.params().get(0).type();
  }

  @Override
  protected TypeReference getTargetType() {
    return transitionMethod.returnType().orElseThrow();
  }

  @Override
  protected List<TypeReference> getQualifierTypes() {
    return transitionMethod.params().subList(1, transitionMethod.params().size()).stream()
        .map(MethodParam::type)
        .toList();
  }
}
