package com.algoprep.patterns.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Builds parameterized SQL text and a matching bind-parameter list from column/value maps -
 * deliberately NOT a real JDBC integration, just the string-building discipline an interviewer is
 * checking for: table and column names (which JDBC can't parameterize) are validated against an
 * allow-list pattern, while every value is emitted as a {@code ?} placeholder and returned
 * separately so a caller would bind it with a {@link java.sql.PreparedStatement}, never
 * concatenate it into the SQL text.
 */
public final class SqlInjectionSafeQuery {
  private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");

  private SqlInjectionSafeQuery() {}

  public record BuiltQuery(String sql, List<Object> parameters) {}

  /** Builds {@code SELECT * FROM table WHERE col1 = ? AND col2 = ? ...} in map iteration order. */
  public static BuiltQuery buildSelect(String table, Map<String, Object> whereEquals) {
    validateIdentifier(table);
    if (whereEquals == null || whereEquals.isEmpty()) {
      return new BuiltQuery("SELECT * FROM " + table, List.of());
    }
    List<Object> params = new ArrayList<>();
    StringBuilder sql = new StringBuilder("SELECT * FROM ").append(table).append(" WHERE ");
    appendConditions(sql, whereEquals, params);
    return new BuiltQuery(sql.toString(), params);
  }

  /** Builds {@code UPDATE table SET col = ? ... WHERE col = ? ...} in map iteration order. */
  public static BuiltQuery buildUpdate(String table, Map<String, Object> setColumns, Map<String, Object> whereEquals) {
    validateIdentifier(table);
    if (setColumns == null || setColumns.isEmpty()) {
      throw new IllegalArgumentException("setColumns must not be empty");
    }
    if (whereEquals == null || whereEquals.isEmpty()) {
      throw new IllegalArgumentException("whereEquals must not be empty; refusing an unconditional UPDATE");
    }
    List<Object> params = new ArrayList<>();
    StringBuilder sql = new StringBuilder("UPDATE ").append(table).append(" SET ");
    boolean first = true;
    for (Map.Entry<String, Object> entry : setColumns.entrySet()) {
      validateIdentifier(entry.getKey());
      if (!first) sql.append(", ");
      sql.append(entry.getKey()).append(" = ?");
      params.add(entry.getValue());
      first = false;
    }
    sql.append(" WHERE ");
    appendConditions(sql, whereEquals, params);
    return new BuiltQuery(sql.toString(), params);
  }

  private static void appendConditions(StringBuilder sql, Map<String, Object> whereEquals, List<Object> params) {
    boolean first = true;
    for (Map.Entry<String, Object> entry : whereEquals.entrySet()) {
      validateIdentifier(entry.getKey());
      if (!first) sql.append(" AND ");
      sql.append(entry.getKey()).append(" = ?");
      params.add(entry.getValue());
      first = false;
    }
  }

  private static void validateIdentifier(String identifier) {
    if (identifier == null || !IDENTIFIER.matcher(identifier).matches()) {
      throw new IllegalArgumentException("invalid SQL identifier: " + identifier);
    }
  }
}
