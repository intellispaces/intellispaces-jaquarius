package intellispaces.framework.core.aop;

import intellispaces.common.action.Action;
import intellispaces.common.action.wrapper.Wrapper;

public interface Advice extends Wrapper {

  Action joinAction();
}