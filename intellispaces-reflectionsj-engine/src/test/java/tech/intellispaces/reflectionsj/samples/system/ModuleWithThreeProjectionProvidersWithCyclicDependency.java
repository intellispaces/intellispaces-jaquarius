package tech.intellispaces.reflectionsj.samples.system;

import tech.intellispaces.reflectionsj.annotation.Module;
import tech.intellispaces.reflectionsj.annotation.Projection;

@Module
public class ModuleWithThreeProjectionProvidersWithCyclicDependency {

  @Projection(lazy = false)
  public String projection1(String projection2) {
    return "projection1";
  }

  @Projection
  public String projection2(String projection3) {
    return "projection2";
  }

  @Projection
  public String projection3(String projection1) {
    return "projection3";
  }
}
