package tech.intellispaces.jaquarius.annotationprocessor;

import tech.intellispaces.action.runnable.RunnableAction;
import tech.intellispaces.action.text.StringActions;
import tech.intellispaces.annotationprocessor.TemplatedJavaArtifactGenerator;
import tech.intellispaces.general.exception.UnexpectedExceptions;
import tech.intellispaces.general.object.ObjectFunctions;
import tech.intellispaces.general.type.ClassFunctions;
import tech.intellispaces.general.type.PrimitiveType;
import tech.intellispaces.general.type.PrimitiveTypes;
import tech.intellispaces.jaquarius.annotation.AutoGuide;
import tech.intellispaces.jaquarius.annotation.Inject;
import tech.intellispaces.jaquarius.annotation.Projection;
import tech.intellispaces.jaquarius.annotation.Shutdown;
import tech.intellispaces.jaquarius.annotation.Startup;
import tech.intellispaces.jaquarius.guide.GuideFunctions;
import tech.intellispaces.jaquarius.object.handle.ObjectHandleFunctions;
import tech.intellispaces.java.reflection.customtype.CustomType;
import tech.intellispaces.java.reflection.method.MethodParam;
import tech.intellispaces.java.reflection.method.MethodStatement;
import tech.intellispaces.java.reflection.reference.NamedReference;
import tech.intellispaces.java.reflection.reference.PrimitiveReference;
import tech.intellispaces.java.reflection.reference.PrimitiveReferences;
import tech.intellispaces.java.reflection.reference.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Common annotation generator functions.
 */
public interface AnnotationGeneratorFunctions {

  static boolean isInjectionMethod(MethodStatement method) {
    return method.hasAnnotation(Inject.class) || method.hasAnnotation(AutoGuide.class);
  }

  static boolean isAutoGuideMethod(MethodStatement method) {
    return method.hasAnnotation(AutoGuide.class);
  }

  static boolean isStartupMethod(MethodStatement method) {
    return method.hasAnnotation(Startup.class);
  }

  static boolean isShutdownMethod(MethodStatement method) {
    return method.hasAnnotation(Shutdown.class);
  }

  static boolean isProjectionMethod(MethodStatement method) {
    return method.hasAnnotation(Projection.class);
  }

  static boolean isReturnObjectHandle(MethodStatement method) {
    return ObjectHandleFunctions.isObjectHandleType(method.returnType().orElseThrow());
  }

  static boolean isReturnGuide(MethodStatement method) {
    return GuideFunctions.isGuideType(method.returnType().orElseThrow());
  }

  static TypeReference normalizeType(TypeReference type) {
    if (type.isPrimitiveReference()) {
      String typename = type.asPrimitiveReferenceOrElseThrow().typename();
      if (ObjectFunctions.equalsAnyOf(typename,
          PrimitiveTypes.Boolean.typename(),
          PrimitiveTypes.Char.typename(),
          PrimitiveTypes.Byte.typename(),
          PrimitiveTypes.Short.typename(),
          PrimitiveTypes.Int.typename()
      )) {
        return PrimitiveReferences.Int;
      }
      if (PrimitiveTypes.Float.typename().equals(typename)) {
        return PrimitiveReferences.Double;
      }
    }
    return type;
  }

  static List<MethodParam> rearrangementParams(List<MethodParam> params) {
    List<MethodParam> result = new ArrayList<>(params.size());

    // 1. Objects
    for (MethodParam param : params) {
      if (param.type().isNamedReference()) {
        result.add(param);
      }
      if (param.type().isCustomTypeReference()) {
        CustomType type = param.type().asCustomTypeReferenceOrElseThrow().targetType();
        Optional<PrimitiveType> primitive = ClassFunctions.primitiveByWrapperClassName(type.canonicalName());
        if (primitive.isEmpty()) {
          result.add(param);
        }
      }
    }

    // 2. Long primitives
    for (MethodParam param : params) {
      if (param.type().isCustomTypeReference()) {
        CustomType type = param.type().asCustomTypeReferenceOrElseThrow().targetType();
        Optional<PrimitiveType> primitive = ClassFunctions.primitiveByWrapperClassName(type.canonicalName());
        if (primitive.isPresent() && primitive.get().isLong()) {
          result.add(param);
        }
      } else if (param.type().isPrimitiveReference()) {
        if (param.type().asPrimitiveReferenceOrElseThrow().primitiveType().isLong()) {
          result.add(param);
        }
      }
    }

    // 3. Integer related primitives
    for (MethodParam param : params) {
      if (param.type().isCustomTypeReference()) {
        CustomType type = param.type().asCustomTypeReferenceOrElseThrow().targetType();
        Optional<PrimitiveType> primitive = ClassFunctions.primitiveByWrapperClassName(type.canonicalName());
        if (primitive.isPresent()
            && (primitive.get().isBoolean()
                || primitive.get().isChar()
                || primitive.get().isByte()
                || primitive.get().isShort()
                || primitive.get().isInt()
        )) {
          result.add(param);
        }
      } else if (param.type().isPrimitiveReference()) {
        PrimitiveReference primitiveReference = param.type().asPrimitiveReferenceOrElseThrow();
        PrimitiveType primitiveType = primitiveReference.primitiveType();
        if (primitiveType.isBoolean()
            || primitiveType.isChar()
            || primitiveType.isByte()
            || primitiveType.isShort()
            || primitiveType.isInt()
        ) {
          result.add(param);
        }
      }
    }

    // 4. Float related primitives
    for (MethodParam param : params) {
      if (param.type().isCustomTypeReference()) {
        CustomType type = param.type().asCustomTypeReferenceOrElseThrow().targetType();
        Optional<PrimitiveType> primitive = ClassFunctions.primitiveByWrapperClassName(type.canonicalName());
        if (primitive.isPresent() && (primitive.get().isFloat() || primitive.get().isDouble())) {
          result.add(param);
        }
      } else if (param.type().isPrimitiveReference()) {
        PrimitiveReference primitiveReference = param.type().asPrimitiveReferenceOrElseThrow();
        PrimitiveType primitiveType = primitiveReference.primitiveType();
        if (primitiveType.isFloat() || primitiveType.isDouble()) {
          result.add(param);
        }
      }
    }

    if (result.size() != params.size()) {
      throw UnexpectedExceptions.withMessage("Invalid state");
    }
    return result;
  }

  static Map<String, String> buildGuideActionMethod(
      MethodStatement guideMethod, TemplatedJavaArtifactGenerator generator
  ) {
    var sb = new StringBuilder();
    RunnableAction commaAppender = StringActions.skipFirstTimeCommaAppender(sb);
    sb.append("private ");
    if (!guideMethod.typeParameters().isEmpty()) {
      sb.append("<");
      for (NamedReference param : guideMethod.typeParameters()) {
        commaAppender.run();
        sb.append(param.formalFullDeclaration());
      }
      sb.append("> ");
    }
    String returnType = buildGuideTypeDeclaration(guideMethod.returnType().orElseThrow(), generator);
    sb.append(returnType);
    sb.append(" ");
    sb.append(ObjectHandleFunctions.buildObjectHandleGuideMethodName(guideMethod));
    sb.append("(");
    commaAppender = StringActions.skipFirstTimeCommaAppender(sb);
    for (MethodParam param : rearrangementParams(guideMethod.params())) {
      commaAppender.run();
      sb.append(buildGuideTypeDeclaration(param.type(), generator));
      sb.append(" ");
      sb.append(param.name());
    }
    sb.append(") {\n");
    sb.append("  return ");
    String actualReturnType = guideMethod.returnType().orElseThrow().actualDeclaration(generator::addImportAndGetSimpleName);
    if (!actualReturnType.equals(returnType)) {
      if (PrimitiveTypes.Boolean.typename().equals(actualReturnType)) {
        sb.append("PrimitiveFunctions.booleanToInt(");
        buildInvokeSuperMethod(guideMethod, sb, generator);
        sb.append(")");
      } else {
        buildInvokeSuperMethod(guideMethod, sb, generator);
      }
    } else {
      buildInvokeSuperMethod(guideMethod, sb, generator);
    }
    sb.append(";\n");
    sb.append("}\n");
    return Map.of("declaration", sb.toString());
  }

  static void buildInvokeSuperMethod(
      MethodStatement objectHandleMethod, StringBuilder sb, TemplatedJavaArtifactGenerator generator
  ) {
    RunnableAction commaAppender;
    sb.append("super.");
    sb.append(objectHandleMethod.name());
    sb.append("(");
    commaAppender = StringActions.skipFirstTimeCommaAppender(sb);
    for (MethodParam param : rearrangementParams(objectHandleMethod.params())) {
      commaAppender.run();
      String actualType = param.type().actualDeclaration(generator::addImportAndGetSimpleName);
      if (!buildGuideTypeDeclaration(param.type(), generator).equals(actualType)) {
        if (PrimitiveTypes.Boolean.typename().equals(actualType)) {
          sb.append("PrimitiveFunctions.booleanToInt(").append(param.name());
        } else {
          sb.append("(");
          sb.append(actualType);
          sb.append(") ");
          sb.append(param.name());
        }
      } else {
        sb.append(param.name());
      }
    }
    sb.append(")");
  }

  static String buildGuideTypeDeclaration(TypeReference type, TemplatedJavaArtifactGenerator generator) {
    return normalizeType(type).actualDeclaration(generator::addImportAndGetSimpleName);
  }
}