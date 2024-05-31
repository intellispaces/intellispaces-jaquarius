package tech.intellispacesframework.core.annotation.processor.objecthandle;

import tech.intellispacesframework.commons.action.Action;
import tech.intellispacesframework.core.annotation.processor.AbstractGenerator;
import tech.intellispacesframework.core.object.MovableObjectHandle;
import tech.intellispacesframework.javastatements.statement.custom.CustomType;
import tech.intellispacesframework.javastatements.statement.custom.MethodParam;
import tech.intellispacesframework.javastatements.statement.custom.MethodStatement;
import tech.intellispacesframework.javastatements.statement.reference.NamedTypeReference;
import tech.intellispacesframework.javastatements.statement.reference.TypeReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static tech.intellispacesframework.core.util.Actions.addSeparatorAction;

abstract class AbstractObjectHandleImplGenerator extends AbstractGenerator {
  protected String domainSimpleClassName;
  protected String typeParamsFull;
  protected String typeParamsBrief;
  protected List<Object> constructors;

  AbstractObjectHandleImplGenerator(CustomType objectHandleType) {
    super(objectHandleType);
  }

  protected boolean isMovableObjectHandle(CustomType objectHandleType) {
    return objectHandleType.hasParent(MovableObjectHandle.class.getCanonicalName());
  }

  protected void analyzeTypeParams(CustomType objectHandleType) {
    if (objectHandleType.typeParameters().isEmpty()) {
      typeParamsFull = typeParamsBrief = "";
      return;
    }

    var typeParamsFullBuilder = new StringBuilder();
    var typeParamsBriefBuilder = new StringBuilder();
    Action addCommaAction = addSeparatorAction(typeParamsFullBuilder, ", ")
        .join(addSeparatorAction(typeParamsBriefBuilder, ", "));

    typeParamsFullBuilder.append("<");
    typeParamsBriefBuilder.append("<");
    for (NamedTypeReference typeParam : objectHandleType.typeParameters()) {
      addCommaAction.execute();
      typeParamsFullBuilder.append(typeParam.formalFullDeclaration());
      typeParamsBriefBuilder.append(typeParam.formalBriefDeclaration());
    }
    typeParamsFullBuilder.append(">");
    typeParamsBriefBuilder.append(">");
    typeParamsFull = typeParamsFullBuilder.toString();
    typeParamsBrief = typeParamsBriefBuilder.toString();
  }

  @SuppressWarnings("unchecked, rawtypes")
  protected void analyzeConstructors(CustomType objectHandleType) {
    List<MethodStatement> constructors;
    if (objectHandleType.asClass().isPresent()) {
      constructors = objectHandleType.asClass().get().constructors();
      List<Map<String, Object>> constructorDescriptors = new ArrayList<>();
      for (MethodStatement constructor : constructors) {
        constructorDescriptors.add(analyzeConstructor(constructor, context.getImportConsumer()));
      }
      this.constructors = (List) constructorDescriptors;
    } else {
      this.constructors = List.of();
    }
  }

  private Map<String, Object> analyzeConstructor(MethodStatement constructor, Consumer<String> imports) {
    Map<String, Object> constructorDescriptor = new HashMap<>();
    List<Map<String, String>> paramDescriptors = new ArrayList<>();
    for (MethodParam param : constructor.params()) {
      TypeReference type = param.type();
      paramDescriptors.add(Map.of(
              "name", param.name(),
              "type", type.actualDeclaration()
          )
      );
      type.asCustomTypeReference().ifPresent(t -> imports.accept(t.targetType().canonicalName()));
    }
    constructorDescriptor.put("params", paramDescriptors);
    return constructorDescriptor;
  }
}