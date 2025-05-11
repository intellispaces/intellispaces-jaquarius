package tech.intellispaces.reflections.framework.traverse.plan;

import tech.intellispaces.reflections.framework.exception.TraverseException;

/**
 * The plan to traverse source object in semantic space.
 */
public interface TraversePlan {

  /**
   * Traverse plan type.
   */
  TraversePlanType type();

  boolean isDeclarative();

  /**
   * Executes traverse plan to source object.
   *
   * @param source source object handle.
   * @param executor traverse executor.
   * @return result object handle.
   * @throws TraverseException throws if source object can't be traversed.
   */
  Object execute(Object source, TraverseExecutor executor) throws TraverseException;

  int executeReturnInt(Object source, TraverseExecutor executor) throws TraverseException;

  double executeReturnDouble(Object source, TraverseExecutor executor) throws TraverseException;

  /**
   * Executes traverse plan to source object and qualifier.
   *
   * @param source source object handle.
   * @param qualifier qualifier.
   * @param executor traverse executor.
   * @return result object handle.
   * @throws TraverseException throws if source object can't be traversed.
   */
  Object execute(Object source, Object qualifier, TraverseExecutor executor) throws TraverseException;

  /**
   * Executes traverse plan to source object and two qualifiers.
   *
   * @param source source object handle.
   * @param qualifier1 first qualifier.
   * @param qualifier2 second qualifier.
   * @param executor traverse executor.
   * @return result object handle.
   * @throws TraverseException throws if source object can't be traversed.
   */
  Object execute(Object source, Object qualifier1, Object qualifier2, TraverseExecutor executor) throws TraverseException;

  /**
   * Executes traverse plan to source object and three qualifiers.
   *
   * @param source source object handle.
   * @param qualifier1 first qualifier.
   * @param qualifier2 second qualifier.
   * @param qualifier3 third qualifier.
   * @param executor traverse executor.
   * @return result object handle.
   * @throws TraverseException throws if source object can't be traversed.
   */
  Object execute(Object source, Object qualifier1, Object qualifier2, Object qualifier3, TraverseExecutor executor) throws TraverseException;

  /**
   * Executes traverse plan to source object and three qualifiers.
   *
   * @param source source object handle.
   * @param qualifier1 first qualifier.
   * @param qualifier2 second qualifier.
   * @param qualifier3 third qualifier.
   * @param qualifier4 four qualifier.
   * @param executor traverse executor.
   * @return result object handle.
   * @throws TraverseException throws if source object can't be traversed.
   */
  Object execute(Object source, Object qualifier1, Object qualifier2, Object qualifier3, Object qualifier4, TraverseExecutor executor) throws TraverseException;
}
