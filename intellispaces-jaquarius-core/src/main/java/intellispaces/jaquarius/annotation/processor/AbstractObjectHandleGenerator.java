package intellispaces.jaquarius.annotation.processor;

import intellispaces.common.action.runner.Runner;
import intellispaces.common.base.exception.UnexpectedViolationException;
import intellispaces.common.base.text.TextActions;
import intellispaces.common.base.type.TypeFunctions;
import intellispaces.common.javastatement.customtype.CustomType;
import intellispaces.common.javastatement.method.MethodParam;
import intellispaces.common.javastatement.method.MethodStatement;
import intellispaces.common.javastatement.reference.CustomTypeReference;
import intellispaces.common.javastatement.reference.CustomTypeReferences;
import intellispaces.common.javastatement.reference.NamedReference;
import intellispaces.common.javastatement.reference.TypeReference;
import intellispaces.jaquarius.annotation.Channel;
import intellispaces.jaquarius.annotation.Movable;
import intellispaces.jaquarius.annotation.Unmovable;
import intellispaces.jaquarius.common.NameConventionFunctions;
import intellispaces.jaquarius.exception.TraverseException;
import intellispaces.jaquarius.guide.GuideForm;
import intellispaces.jaquarius.guide.GuideForms;
import intellispaces.jaquarius.object.ObjectHandleTypes;
import intellispaces.jaquarius.space.SpaceConstants;
import intellispaces.jaquarius.space.channel.ChannelFunctions;
import intellispaces.jaquarius.space.domain.DomainFunctions;

import javax.annotation.processing.RoundEnvironment;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractObjectHandleGenerator extends AbstractGenerator {
  protected String domainTypeParamsFull;
  protected String domainTypeParamsBrief;
  protected final List<Map<String, String>> conversionMethods = new ArrayList<>();
  protected final List<Map<String, String>> methods = new ArrayList<>();

  public AbstractObjectHandleGenerator(CustomType initiatorType, CustomType customType) {
    super(initiatorType, customType);
  }

  abstract protected ObjectHandleTypes getObjectHandleType();

  protected String movableClassSimpleName() {
    return context.addToImportAndGetSimpleName(
        NameConventionFunctions.getMovableObjectHandleTypename(annotatedType.className())
    );
  }

  protected boolean isNotDomainClassGetter(MethodStatement method) {
    return !method.name().equals("domainClass") &&
        !method.name().equals(SpaceConstants.POINT_TO_DOMAIN_CHANNEL_SIMPLE_NAME);
  }

  protected void analyzeObjectHandleMethods(CustomType customType, RoundEnvironment roundEnv) {
    List<MethodStatement> methods = getObjectHandleMethods(customType, roundEnv).toList();
    int methodIndex = 0;
    for (MethodStatement method : methods) {
      MethodStatement effectiveMethod = convertMethodBeforeGenerate(method);
      analyzeMethod(effectiveMethod, GuideForms.Main, methodIndex++);
      if (method.returnType().orElseThrow().isCustomTypeReference()) {
        CustomType returnType = method.returnType().orElseThrow().asCustomTypeReferenceOrElseThrow().targetType();
        if (TypeFunctions.isPrimitiveWrapperClass(returnType.canonicalName())) {
          analyzeMethod(effectiveMethod, GuideForms.Primitive, methodIndex++);
        }
      }
    }
  }

  protected void analyzeConversionMethods(CustomType customType, RoundEnvironment roundEnv) {
    analyzeConversionMethods(customType, customType, roundEnv);
  }

  private void analyzeConversionMethods(
      CustomType customType, CustomType effectiveCustomType, RoundEnvironment roundEnv
  ) {
    Iterator<CustomTypeReference> parents = customType.parentTypes().iterator();
    Iterator<CustomTypeReference> effectiveParents = effectiveCustomType.parentTypes().iterator();
    while (parents.hasNext() && effectiveParents.hasNext()) {
      CustomTypeReference parent = parents.next();
      CustomTypeReference effectiveParent = effectiveParents.next();
      if (DomainFunctions.isAliasOf(parent, customType)) {
        analyzeConversionMethods(parent.targetType(), effectiveParent.effectiveTargetType(), roundEnv);
      } else {
        conversionMethods.add(buildConversionMethod(CustomTypeReferences.get(effectiveParent.effectiveTargetType())));
      }
    }
  }

  protected Map<String, String> buildConversionMethod(CustomTypeReference parent) {
    var sb = new StringBuilder();
    sb.append(getObjectHandleDeclaration(parent, getObjectHandleType()));
    sb.append(" ");
    sb.append(NameConventionFunctions.getConversionMethodName(parent));
    sb.append("()");
    return Map.of(
        "declaration", sb.toString()
    );
  }

  abstract protected Stream<MethodStatement> getObjectHandleMethods(
      CustomType customType, RoundEnvironment roundEnv
  );

  protected MethodStatement convertMethodBeforeGenerate(MethodStatement method) {
    return method;
  }

  protected void analyzeMethod(MethodStatement method, GuideForm guideForm, int methodIndex) {
    methods.add(generateMethod(method, guideForm, methodIndex));
  }

  protected Map<String, String> generateMethod(MethodStatement method, GuideForm guideForm, int methodIndex) {
    var sb = new StringBuilder();
    appendMethodTypeParameters(sb, method);
    boolean disableMoving = isDisableMoving(method);
    if (disableMoving) {
      sb.append("default ");
    }
    appendMethodReturnHandleType(sb, method, guideForm);
    sb.append(" ");
    sb.append(getMethodName(method, guideForm));
    sb.append("(");
    appendMethodParameters(sb, method);
    sb.append(")");
    appendMethodExceptions(sb, method);

    if (disableMoving) {
      context.addImport(TraverseException.class);
      String exceptionSimpleName = context.simpleNameOf(TraverseException.class);
      sb.append(" {\n");
      sb.append("    throw ").append(exceptionSimpleName).append(
          ".withMessage(\"Unmovable object cannot be moved\");\n");
      sb.append("  }");
    }
    return Map.of(
        "javadoc", buildGeneratedMethodJavadoc(method.owner().canonicalName(), method),
        "declaration", sb.toString()
    );
  }

  protected String getMethodName(MethodStatement method, GuideForm guideForm) {
    if (guideForm == GuideForms.Main) {
      return method.name();
    } else if (guideForm == GuideForms.Primitive) {
      return method.name() + "Primitive";
    } else {
      throw UnexpectedViolationException.withMessage("Unsupported guide form - {0}", guideForm);
    }
  }

  protected boolean isDisableMoving(MethodStatement method) {
    Channel channel = ChannelFunctions.getDomainMainChannelAnnotation(method);
    return ChannelFunctions.getTraverseType(channel).isMovingBased() &&
        getObjectHandleType() == ObjectHandleTypes.Unmovable;
  }

  protected void appendMethodReturnType(StringBuilder sb, MethodStatement method) {
    if (method.returnType().isEmpty()) {
      sb.append("void");
    }
    TypeReference returnType = method.returnType().orElseThrow();
    sb.append(returnType.actualDeclaration(context::addToImportAndGetSimpleName));
  }

  protected void appendMethodReturnHandleType(StringBuilder sb, MethodStatement method, GuideForm guideForm) {
    if (guideForm == GuideForms.Main) {
      appendMethodReturnHandleType(sb, method);
    } else if (guideForm == GuideForms.Primitive) {
      CustomType ct = method.returnType().orElseThrow().asCustomTypeReferenceOrElseThrow().targetType();
      sb.append(TypeFunctions.getPrimitiveTypeOfWrapper(ct.canonicalName()));
    } else {
      throw UnexpectedViolationException.withMessage("Unsupported guide form - {0}", guideForm);
    }
  }

  protected void appendMethodReturnHandleType(StringBuilder sb, MethodStatement method) {
    TypeReference domainReturnType = method.returnType().orElseThrow();
    if (NameConventionFunctions.isConversionMethod(method)) {
      sb.append(getObjectHandleDeclaration(domainReturnType, getObjectHandleType()));
    } else {
      if (method.hasAnnotation(Movable.class)) {
        sb.append(getObjectHandleDeclaration(domainReturnType, ObjectHandleTypes.Movable));
      } else if (method.hasAnnotation(Unmovable.class)) {
        sb.append(getObjectHandleDeclaration(domainReturnType, ObjectHandleTypes.Unmovable));
      } else {
        sb.append(getObjectHandleDeclaration(domainReturnType, ObjectHandleTypes.Common));
      }
    }
  }

  protected void appendMethodTypeParameters(StringBuilder sb, MethodStatement method) {
    if (!method.typeParameters().isEmpty()) {
      sb.append("<");
      Runner commaAppender = TextActions.skippingFirstTimeCommaAppender(sb);
      for (NamedReference namedTypeReference : method.typeParameters()) {
        commaAppender.run();
        sb.append(namedTypeReference.actualDeclaration());
      }
      sb.append("> ");
    }
  }

  protected void appendMethodParameters(StringBuilder sb, MethodStatement method) {
    Runner commaAppender = TextActions.skippingFirstTimeCommaAppender(sb);
    for (MethodParam param : GuideProcessorFunctions.rearrangementParams(method.params())) {
      commaAppender.run();
      sb.append(getObjectHandleDeclaration(GuideProcessorFunctions.normalizeType(param.type()), ObjectHandleTypes.Common));
      sb.append(" ");
      sb.append(param.name());
    }
  }

  protected void appendMethodExceptions(StringBuilder sb, MethodStatement method) {
    String exceptions = method.exceptions().stream()
        .map(e -> e.asCustomTypeReference().orElseThrow().targetType())
        .peek(e -> context.addImport(e.canonicalName()))
        .map(e -> context.simpleNameOf(e.canonicalName()))
        .collect(Collectors.joining(", "));
    if (!exceptions.isEmpty()) {
      sb.append(" throws ").append(exceptions);
    }
  }

  protected boolean excludeDeepConversionMethods(MethodStatement method, CustomType customType) {
    if (!NameConventionFunctions.isConversionMethod(method)) {
      return true;
    }
    for (CustomTypeReference parent : customType.parentTypes()) {
      if (DomainFunctions.isAliasOf(parent, customType)) {
        if (excludeDeepConversionMethods(method, parent.targetType())) {
          return true;
        }
      }
      if (NameConventionFunctions.getConversionMethodName(parent).equals(method.name())) {
        return true;
      }
    }
    return false;
  }
}
