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
import io.vertx.core.net.SocketAddress;
import io.vertx.core.net.impl.SocketAddressImpl;
import io.vertx.core.spi.metrics.TCPMetrics;
import io.vertx.monitoring.match.LabelMatchers;
import io.vertx.monitoring.meters.Counters;
import io.vertx.monitoring.meters.Gauges;
import io.vertx.monitoring.meters.Summaries;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author Joel Takvorian
 */
class VertxNetServerMetrics {
  protected final LabelMatchers labelMatchers;
  private final Gauges<LongAdder> connections;
  private final Summaries bytesReceived;
  private final Summaries bytesSent;
  private final Counters errorCount;

  VertxNetServerMetrics(LabelMatchers labelMatchers, MeterRegistry registry) {
    this(labelMatchers, registry, MetricsCategory.NET_SERVER, "vertx.net");
  }

  VertxNetServerMetrics(LabelMatchers labelMatchers, MeterRegistry registry, MetricsCategory domain, String prefix) {
    this.labelMatchers = labelMatchers;
    connections = Gauges.longGauges(domain, prefix + ".server.connections", "Number of opened connections to the server",
      registry, Labels.LOCAL, Labels.REMOTE);
    bytesReceived = new Summaries(domain, prefix + ".server.bytesReceived", "Number of bytes received by the server",
      registry, Labels.LOCAL, Labels.REMOTE);
    bytesSent = new Summaries(domain, prefix + ".server.bytesSent", "Number of bytes sent by the server",
      registry, Labels.LOCAL, Labels.REMOTE);
    errorCount = new Counters(domain, prefix + ".server.errors", "Number of errors",
      registry, Labels.LOCAL, Labels.REMOTE, Labels.CLASS);
  }

  TCPMetrics forAddress(SocketAddress localAddress) {
    String local = Labels.fromAddress(localAddress);
    return new Instance(local);
  }

  class Instance implements TCPMetrics<String> {
    final String local;

    Instance(String local) {
      this.local = local;
    }

    @Override
    public String connected(SocketAddress remoteAddress, String remoteName) {
      String remote = Labels.fromAddress(new SocketAddressImpl(remoteAddress.port(), remoteName));
      connections.get(labelMatchers, local, remote).increment();
      return remote;
    }

    @Override
    public void disconnected(String remote, SocketAddress remoteAddress) {
      connections.get(labelMatchers, local, remote).decrement();
    }

    @Override
    public void bytesRead(String remote, SocketAddress remoteAddress, long numberOfBytes) {
      bytesReceived.get(labelMatchers, local, remote).record(numberOfBytes);
    }

    @Override
    public void bytesWritten(String remote, SocketAddress remoteAddress, long numberOfBytes) {
      bytesSent.get(labelMatchers, local, remote).record(numberOfBytes);
    }

    @Override
    public void exceptionOccurred(String remote, SocketAddress remoteAddress, Throwable t) {
      errorCount.get(labelMatchers, local, remote, t.getClass().getSimpleName()).increment();
    }

    @Override
    public boolean isEnabled() {
      return true;
    }

    @Override
    public void close() {
    }
  }
}
