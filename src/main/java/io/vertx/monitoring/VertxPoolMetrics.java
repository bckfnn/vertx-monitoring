/*
 * Copyright (c) 2011-2017 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *     The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import io.vertx.core.spi.metrics.PoolMetrics;
import io.vertx.monitoring.match.LabelMatchers;
import io.vertx.monitoring.meters.Counters;
import io.vertx.monitoring.meters.Gauges;
import io.vertx.monitoring.meters.Timers;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author Joel Takvorian
 */
class VertxPoolMetrics {
  private final LabelMatchers labelMatchers;
  private final Timers queueDelay;
  private final Gauges<LongAdder> queueSize;
  private final Timers usage;
  private final Gauges<LongAdder> inUse;
  private final Gauges<AtomicReference<Double>> usageRatio;
  private final Counters completed;

  VertxPoolMetrics(LabelMatchers labelMatchers, MeterRegistry registry) {
    this.labelMatchers = labelMatchers;
    queueDelay = new Timers(MetricsCategory.NAMED_POOLS, "vertx.pool.queue.delay",
      "Queue time for a resource", registry, "pool.type", "pool.name");
    queueSize = Gauges.longGauges(MetricsCategory.NAMED_POOLS, "vertx.pool.queue.size",
      "Number of elements waiting for a resource", registry, "pool.type", "pool.name");
    usage = new Timers(MetricsCategory.NAMED_POOLS, "vertx.pool.usage",
      "Time using a resource", registry, "pool.type", "pool.name");
    inUse = Gauges.longGauges(MetricsCategory.NAMED_POOLS, "vertx.pool.inUse",
      "Number of resources used", registry, "pool.type", "pool.name");
    usageRatio = Gauges.doubleGauges(MetricsCategory.NAMED_POOLS, "vertx.pool.ratio",
      "Pool usage ratio, only present if maximum pool size could be determined", registry, "pool.type", "pool.name");
    completed = new Counters(MetricsCategory.NAMED_POOLS, "vertx.pool.completed",
      "Number of elements done with the resource", registry, "pool.type", "pool.name");
  }

  PoolMetrics forInstance(String poolType, String poolName, int maxPoolSize) {
    return new Instance(poolType, poolName, maxPoolSize);
  }

  class Instance implements PoolMetrics<Timers.EventTiming> {
    private final String poolType;
    private final String poolName;
    private final int maxPoolSize;

    Instance(String poolType, String poolName, int maxPoolSize) {
      this.poolType = poolType;
      this.poolName = poolName;
      this.maxPoolSize = maxPoolSize;
    }

    @Override
    public Timers.EventTiming submitted() {
      queueSize.get(labelMatchers, poolType, poolName).increment();
      return queueDelay.start(labelMatchers, poolType, poolName);
    }

    @Override
    public void rejected(Timers.EventTiming submitted) {
      queueSize.get(labelMatchers, poolType, poolName).decrement();
      submitted.end();
    }

    @Override
    public Timers.EventTiming begin(Timers.EventTiming submitted) {
      queueSize.get(labelMatchers, poolType, poolName).decrement();
      submitted.end();
      LongAdder l = inUse.get(labelMatchers, poolType, poolName);
      l.increment();
      checkRatio(l.longValue());
      return usage.start(labelMatchers, poolType, poolName);
    }

    @Override
    public void end(Timers.EventTiming begin, boolean succeeded) {
      LongAdder l = inUse.get(labelMatchers, poolType, poolName);
      l.decrement();
      checkRatio(l.longValue());
      begin.end();
      completed.get(labelMatchers, poolType, poolName).increment();
    }

    @Override
    public boolean isEnabled() {
      return true;
    }

    @Override
    public void close() {
    }

    private void checkRatio(long inUse) {
      if (maxPoolSize > 0) {
        usageRatio.get(labelMatchers, poolType, poolName)
          .set((double)inUse / maxPoolSize);
      }
    }
  }
}
