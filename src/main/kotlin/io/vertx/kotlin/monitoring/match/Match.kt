package io.vertx.kotlin.monitoring.match

import io.vertx.monitoring.match.Match
import io.vertx.monitoring.MetricsCategory
import io.vertx.monitoring.match.MatchType

/**
 * A function providing a DSL for building [io.vertx.monitoring.match.Match] objects.
 *
 * A match for a value.
 *
 * @param alias  Set the alias the human readable name that will be used as a part of registry entry name when the value matches.
 * @param domain  Set the label domain.
 * @param label  Set the label to match.
 * @param type  Set the type of matching to apply.
 * @param value  Set the matched value.
 *
 * <p/>
 * NOTE: This function has been automatically generated from the [io.vertx.monitoring.match.Match original] using Vert.x codegen.
 */
fun Match(
  alias: String? = null,
  domain: MetricsCategory? = null,
  label: String? = null,
  type: MatchType? = null,
  value: String? = null): Match = io.vertx.monitoring.match.Match().apply {

  if (alias != null) {
    this.setAlias(alias)
  }
  if (domain != null) {
    this.setDomain(domain)
  }
  if (label != null) {
    this.setLabel(label)
  }
  if (type != null) {
    this.setType(type)
  }
  if (value != null) {
    this.setValue(value)
  }
}

