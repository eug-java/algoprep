package com.algoprep.patterns.spring;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SqlInjectionSafeQueryTest {
  private static final Set<String> USERS = Set.of("users", "id", "status");

  @Test
  void buildsParameterizedSelect() {
    Map<String, Object> where = new LinkedHashMap<>();
    where.put("id", 42);
    where.put("status", "ACTIVE");

    SqlInjectionSafeQuery.BuiltQuery query = SqlInjectionSafeQuery.buildSelect("users", where, USERS);

    assertEquals("SELECT * FROM users WHERE id = ? AND status = ?", query.sql());
    assertEquals(List.of(42, "ACTIVE"), query.parameters());
  }

  @Test
  void buildsSelectWithNoConditions() {
    SqlInjectionSafeQuery.BuiltQuery query = SqlInjectionSafeQuery.buildSelect("users", Map.of(), USERS);
    assertEquals("SELECT * FROM users", query.sql());
    assertTrue(query.parameters().isEmpty());
  }

  @Test
  void buildsParameterizedUpdate() {
    Map<String, Object> set = new LinkedHashMap<>();
    set.put("status", "INACTIVE");
    Map<String, Object> where = new LinkedHashMap<>();
    where.put("id", 7);

    SqlInjectionSafeQuery.BuiltQuery query = SqlInjectionSafeQuery.buildUpdate("users", set, where, USERS);

    assertEquals("UPDATE users SET status = ? WHERE id = ?", query.sql());
    assertEquals(List.of("INACTIVE", 7), query.parameters());
  }

  @Test
  void rejectsMaliciousTableName() {
    assertThrows(IllegalArgumentException.class, () ->
        SqlInjectionSafeQuery.buildSelect("users; DROP TABLE users;--", Map.of("id", 1), USERS));
  }

  @Test
  void rejectsMaliciousColumnName() {
    Map<String, Object> where = new LinkedHashMap<>();
    where.put("id = 1 OR 1=1; --", "x");
    assertThrows(IllegalArgumentException.class, () -> SqlInjectionSafeQuery.buildSelect("users", where, USERS));
  }

  @Test
  void rejectsIdentifierThatLooksValidButIsNotAllowed() {
    Map<String, Object> where = new LinkedHashMap<>();
    where.put("email", "a@b.c");
    IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () ->
        SqlInjectionSafeQuery.buildSelect("users", where, USERS));
    assertTrue(error.getMessage().contains("allow-list"));
  }

  @Test
  void rejectsMissingAllowList() {
    assertThrows(IllegalArgumentException.class, () ->
        SqlInjectionSafeQuery.buildSelect("users", Map.of(), null));
    assertThrows(IllegalArgumentException.class, () ->
        SqlInjectionSafeQuery.buildSelect("users", Map.of(), Set.of()));
  }

  @Test
  void rejectsUnconditionalUpdate() {
    assertThrows(IllegalArgumentException.class, () ->
        SqlInjectionSafeQuery.buildUpdate("users", Map.of("status", "X"), Map.of(), USERS));
  }
}
