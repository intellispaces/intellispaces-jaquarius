package tech.intellispaces.jaquarius.engine.descriptor;

import tech.intellispaces.action.Action;
import tech.intellispaces.action.functional.FunctionActions;
import tech.intellispaces.jaquarius.traverse.TraverseType;

import java.util.List;
import java.util.function.Function;

public class ObjectHandleMethodBuilder1<H> {
  private final String name;

  private Action action;
  private String purpose;
  private int ordinal;
  private Class<?> channelClass;
  private TraverseType traverseType;

  public ObjectHandleMethodBuilder1(Class<H> objectHandleClass, String name) {
    this.name = name;
  }

  public ObjectHandleMethodBuilder1<H> purpose(String purpose) {
    this.purpose = purpose;
    return this;
  }

  public ObjectHandleMethodBuilder1<H> ordinal(int ordinal) {
    this.ordinal = ordinal;
    return this;
  }

  public ObjectHandleMethodBuilder1<H> channelClass(Class<?> channelClass) {
    this.channelClass = channelClass;
    return this;
  }

  public ObjectHandleMethodBuilder1<H> traverseType(TraverseType traverseType) {
    this.traverseType = traverseType;
    return this;
  }

  public <R> ObjectHandleMethodBuilder1<H> function(Function<H, R> function) {
    this.action = FunctionActions.ofFunction(function);
    return this;
  }

  public ObjectHandleMethod get() {
    return new ObjectHandleMethodImpl(
        name,
        List.of(),
        purpose,
        ordinal,
        action,
        channelClass,
        traverseType
    );
  }
}
