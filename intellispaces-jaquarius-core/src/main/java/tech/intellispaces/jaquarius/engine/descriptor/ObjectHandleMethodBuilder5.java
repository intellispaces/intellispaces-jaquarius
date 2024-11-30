package tech.intellispaces.jaquarius.engine.descriptor;

import tech.intellispaces.action.Action;
import tech.intellispaces.action.functional.FunctionActions;
import tech.intellispaces.entity.function.QuintiFunction;
import tech.intellispaces.jaquarius.traverse.TraverseType;

import java.util.List;

public class ObjectHandleMethodBuilder5<H, P1, P2, P3, P4> {
  private final String name;
  private final Class<P1> paramClass1;
  private final Class<P2> paramClass2;
  private final Class<P3> paramClass3;
  private final Class<P4> paramClass4;

  private Action action;
  private String purpose;
  private int traverseOrdinal;
  private Class<?> channelClass;
  private TraverseType traverseType;

  public ObjectHandleMethodBuilder5(
      Class<H> objectHandleClass,
      String name,
      Class<P1> paramClass1,
      Class<P2> paramClass2,
      Class<P3> paramClass3,
      Class<P4> paramClass4
  ) {
    this.name = name;
    this.paramClass1 = paramClass1;
    this.paramClass2 = paramClass2;
    this.paramClass3 = paramClass3;
    this.paramClass4 = paramClass4;
  }

  public ObjectHandleMethodBuilder5<H, P1, P2, P3, P4> purpose(String purpose) {
    this.purpose = purpose;
    return this;
  }

  public ObjectHandleMethodBuilder5<H, P1, P2, P3, P4> traverseOrdinal(int ordinal) {
    this.traverseOrdinal = ordinal;
    return this;
  }

  public ObjectHandleMethodBuilder5<H, P1, P2, P3, P4> channelClass(Class<?> channelClass) {
    this.channelClass = channelClass;
    return this;
  }

  public ObjectHandleMethodBuilder5<H, P1, P2, P3, P4> traverseType(TraverseType traverseType) {
    this.traverseType = traverseType;
    return this;
  }

  public <R> ObjectHandleMethodBuilder5<H, P1, P2, P3, P4> function(QuintiFunction<H, P1, P2, P3, P4, R> function) {
    this.action = FunctionActions.ofQuintiFunction(function);
    return this;
  }

  public ObjectHandleMethod get() {
    return new ObjectHandleMethodImpl(
        name,
        List.of(paramClass1, paramClass2, paramClass3, paramClass4),
        purpose,
        traverseOrdinal,
        action,
        channelClass,
        traverseType
    );
  }
}
