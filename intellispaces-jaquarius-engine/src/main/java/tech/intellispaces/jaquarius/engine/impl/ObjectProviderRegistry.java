package tech.intellispaces.jaquarius.engine.impl;

import tech.intellispaces.commons.action.Action;
import tech.intellispaces.commons.action.Action0;
import tech.intellispaces.commons.action.Action1;
import tech.intellispaces.commons.action.Action2;
import tech.intellispaces.commons.action.Action3;
import tech.intellispaces.commons.action.Action4;
import tech.intellispaces.commons.collection.CollectionFunctions;
import tech.intellispaces.commons.exception.NotImplementedExceptions;
import tech.intellispaces.commons.exception.UnexpectedExceptions;
import tech.intellispaces.commons.object.Objects;
import tech.intellispaces.commons.resource.ResourceFunctions;
import tech.intellispaces.commons.type.Classes;
import tech.intellispaces.commons.type.Type;
import tech.intellispaces.jaquarius.engine.ObjectProviderWrapper;
import tech.intellispaces.jaquarius.engine.description.ObjectProviderMethodDescription;
import tech.intellispaces.jaquarius.exception.ConfigurationExceptions;
import tech.intellispaces.jaquarius.object.provider.ObjectProviderFunctions;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class ObjectProviderRegistry {
  private boolean isLoaded;
  private Map<Class<?>, List<ObjectProviderMethodDescription>> domainToDescriptions = Map.of();

  @SuppressWarnings("unchecked")
  public <H> Action0<H> objectProviderAction(
      Class<?> targetDomainClass,
      String contractType,
      Type<H> targetObjectHandleType
  ) {
    loadObjectProviders();
    return (Action0<H>) makeAction(targetDomainClass, contractType, List.of());
  }

  @SuppressWarnings("unchecked")
  public <H, Q> Action1<H, Q> objectProviderAction(
      Class<?> targetDomainClass,
      String contractType,
      Type<Q> contractQualifierType,
      Type<H> targetObjectHandleType
  ) {
    loadObjectProviders();
    return (Action1<H, Q>) makeAction(targetDomainClass, contractType, List.of(contractQualifierType));
  }

  @SuppressWarnings("unchecked")
  public <H, Q1, Q2> Action2<H, Q1, Q2> objectProviderAction(
      Class<?> targetDomainClass,
      String contractType,
      Type<Q1> contractQualifierType1,
      Type<Q2> contractQualifierType2,
      Type<H> targetObjectHandleType
  ) {
    loadObjectProviders();
    return (Action2<H, Q1, Q2>) makeAction(
        targetDomainClass, contractType, List.of(contractQualifierType1, contractQualifierType2
    ));
  }

  @SuppressWarnings("unchecked")
  public <H, Q1, Q2, Q3> Action3<H, Q1, Q2, Q3> objectProviderAction(
      Class<?> targetDomainClass,
      String contractType,
      Type<Q1> contractQualifierType1,
      Type<Q2> contractQualifierType2,
      Type<Q3> contractQualifierType3,
      Type<H> targetObjectHandleType
  ) {
    loadObjectProviders();
    return (Action3<H, Q1, Q2, Q3>) makeAction(
        targetDomainClass, contractType, List.of(contractQualifierType1, contractQualifierType2, contractQualifierType3
      ));
  }

  void loadObjectProviders() {
    if (isLoaded) {
      return;
    }

    List<ObjectProviderMethodDescription> descriptions = new ArrayList<>();
    try {
      Enumeration<URL> enumeration = ObjectProviderRegistry.class.getClassLoader().getResources(
          "META-INF/jaquarius/object.providers"
      );
      List<URL> urls = CollectionFunctions.toList(enumeration);
      for (URL url : urls) {
        String content = ResourceFunctions.readResourceAsString(url);
        for (String providerClassName : content.split("\n")) {
          providerClassName = providerClassName.trim();
          if (providerClassName.isEmpty()) {
            continue;
          }
          Optional<Class<?>> providerClass = Classes.get(providerClassName);
          if (providerClass.isEmpty()) {
            throw UnexpectedExceptions.withMessage("Unable to load object providers class {0}", providerClassName);
          }
          var wrapper = (ObjectProviderWrapper) Objects.get(providerClass.get());
          descriptions.addAll(wrapper.methods());
        }
      }
    } catch (IOException e) {
      throw UnexpectedExceptions.withCauseAndMessage(e, "Unable to load object providers");
    }

    domainToDescriptions = descriptions.stream()
        .collect(Collectors.groupingBy(ObjectProviderMethodDescription::returnedDomainClass));
    isLoaded = true;
  }

  boolean isMatchContractQualifiers(
      ObjectProviderMethodDescription description, List<Type<?>> requiredQualifierTypes
  ) {
    List<Type<?>> providerQualifierTypes = description.paramTypes();
    if (providerQualifierTypes.size() != requiredQualifierTypes.size()) {
      return false;
    }
    for (int index = 0; index < providerQualifierTypes.size(); index++) {
      Type<?> providerQualifierType = providerQualifierTypes.get(index);
      Type<?> requiredQualifierType = requiredQualifierTypes.get(index);
      if (!providerQualifierType.equals(requiredQualifierType)) {
        return false;
      }
    }
    return true;
  }

  Action makeAction(
      Class<?> targetDomainClass,
      String contractType,
      List<Type<?>> contractQualifierTypes
  ) {
    List<ObjectProviderMethodDescription> descriptions = domainToDescriptions.get(targetDomainClass);
    if (descriptions == null) {
      throw ConfigurationExceptions.withMessage("No object providers of domain {0} were found",
          targetDomainClass.getCanonicalName());
    }
    for (ObjectProviderMethodDescription description : descriptions) {
      if (
          ObjectProviderFunctions.getContractType(description.name()).equals(contractType) &&
              isMatchContractQualifiers(description, contractQualifierTypes)
      ) {
        return makeAction(description);
      }
    }
    throw ConfigurationExceptions.withMessage("No object providers of domain {0} and contract type {1} were found",
        targetDomainClass.getCanonicalName(), contractType);
  }

  Action makeAction(ObjectProviderMethodDescription description) {
    return switch (description.paramTypes().size()) {
      case 0 -> makeAction0(description);
      case 1 -> makeAction1(description);
      case 2 -> makeAction2(description);
      case 3 -> makeAction3(description);
      default -> throw NotImplementedExceptions.withCode("h3he6A");
    };
  }

  @SuppressWarnings("unchecked")
  private Action makeAction0(ObjectProviderMethodDescription description) {
    var originAction = (Action1<Object, Object>) description.action();
    return originAction.convertToAction0(description.objectProvider());
  }

  @SuppressWarnings("unchecked")
  private Action makeAction1(ObjectProviderMethodDescription description) {
    var originAction = (Action2<Object, Object, Object>) description.action();
    return originAction.convertToAction1(description.objectProvider());
  }

  @SuppressWarnings("unchecked")
  private Action makeAction2(ObjectProviderMethodDescription description) {
    var originAction = (Action3<Object, Object, Object, Object>) description.action();
    return originAction.convertToAction2(description.objectProvider());
  }

  @SuppressWarnings("unchecked")
  private Action makeAction3(ObjectProviderMethodDescription description) {
    var originAction = (Action4<Object, Object, Object, Object, Object>) description.action();
    return originAction.convertToAction3(description.objectProvider());
  }
}
