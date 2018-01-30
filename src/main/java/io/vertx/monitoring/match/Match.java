package io.vertx.monitoring.match;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import io.vertx.monitoring.MetricsCategory;

/**
 * A match for a value.
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@DataObject
public class Match {

  /**
   * The default value : {@link MatchType#EQUALS}
   */
  public static final MatchType DEFAULT_TYPE = MatchType.EQUALS;

  private MetricsCategory domain;
  private String label;
  private String value;
  private MatchType type;
  private String alias;

  /**
   * Default constructor
   */
  public Match() {
    type = DEFAULT_TYPE;
  }

  /**
   * Copy constructor
   *
   * @param other The other {@link Match} to copy when creating this
   */
  public Match(Match other) {
    domain = other.domain;
    label = other.label;
    value = other.value;
    type = other.type;
  }

  /**
   * Create an instance from a {@link JsonObject}
   *
   * @param json the JsonObject to create it from
   */
  public Match(JsonObject json) {
    if (json.containsKey("domain")) {
      domain = MetricsCategory.valueOf(json.getString("domain"));
    }
    label = json.getString("label");
    value = json.getString("value");
    type = MatchType.valueOf(json.getString("type", DEFAULT_TYPE.name()));
    alias = json.getString("alias");
  }

  /**
   * @return the label domain
   */
  public MetricsCategory getDomain() {
    return domain;
  }

  /**
   * Set the label domain.
   *
   * @param domain the label domain
   * @return a reference to this, so the API can be used fluently
   */
  public Match setDomain(MetricsCategory domain) {
    this.domain = domain;
    return this;
  }

  /**
   * @return the label to match
   */
  public String getLabel() {
    return label;
  }

  /**
   * Set the label to match.
   *
   * @param label the label to match
   * @return a reference to this, so the API can be used fluently
   */
  public Match setLabel(String label) {
    this.label = label;
    return this;
  }

  /**
   * @return the matched value
   */
  public String getValue() {
    return value;
  }

  /**
   * Set the matched value.
   *
   * @param value the value to match
   * @return a reference to this, so the API can be used fluently
   */
  public Match setValue(String value) {
    this.value = value;
    return this;
  }

  /**
   * @return the matcher type
   */
  public MatchType getType() {
    return type;
  }

  /**
   * Set the type of matching to apply.
   *
   * @param type the matcher type
   * @return a reference to this, so the API can be used fluently
   */
  public Match setType(MatchType type) {
    this.type = type;
    return this;
  }

  /**
   * @return the matcher alias
   */
  public String getAlias() {
    return alias;
  }

  /**
   * Set the alias the human readable name that will be used as a part of
   * registry entry name when the value matches.
   *
   * @param alias the matcher alias
   * @return a reference to this, so the API can be used fluently
   */
  public Match setAlias(String alias) {
    this.alias = alias;
    return this;
  }
}
