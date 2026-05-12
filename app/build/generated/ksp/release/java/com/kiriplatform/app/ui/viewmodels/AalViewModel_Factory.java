package com.kiriplatform.app.ui.viewmodels;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class AalViewModel_Factory implements Factory<AalViewModel> {
  @Override
  public AalViewModel get() {
    return newInstance();
  }

  public static AalViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AalViewModel newInstance() {
    return new AalViewModel();
  }

  private static final class InstanceHolder {
    static final AalViewModel_Factory INSTANCE = new AalViewModel_Factory();
  }
}
