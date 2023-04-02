/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby.internal.quartz;

import java.lang.reflect.Method;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobBuilder;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.impl.JobDetailImpl;

@SuppressWarnings("serial")
public class JobMethodDetail extends JobDetailImpl {

  private Method jobMethod;

  public JobMethodDetail(final Method method) {
    this.jobMethod = method;
  }

  public Method getJobMethod() {
    return jobMethod;
  }

  @Override
  public boolean isConcurrentExectionDisallowed() {
    return jobMethod.getDeclaringClass().getAnnotation(DisallowConcurrentExecution.class) != null;
  }

  @Override
  public boolean isPersistJobDataAfterExecution() {
    return jobMethod.getDeclaringClass().getAnnotation(PersistJobDataAfterExecution.class) != null;
  }

  @Override
  public JobBuilder getJobBuilder() {
    // See: https://github.com/jooby-project/jooby/issues/2852
    return new JobMethodBuilder(this);
  }
}
