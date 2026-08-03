package com.algoprep.patterns.spring;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RequestIdFilterTest {
  @Test
  void reusesIncomingHeaderWhenPresent() {
    RequestIdFilter filter = new RequestIdFilter();
    String id = filter.resolve("existing-request-id");
    assertEquals("existing-request-id", id);
    assertEquals("existing-request-id", filter.current());
  }

  @Test
  void generatesIdWhenHeaderMissingOrBlank() {
    RequestIdFilter filter = new RequestIdFilter(() -> "generated-id");
    assertEquals("generated-id", filter.resolve(null));
    assertEquals("generated-id", filter.resolve("   "));
  }

  @Test
  void clearRemovesCurrentId() {
    RequestIdFilter filter = new RequestIdFilter();
    filter.resolve("abc");
    filter.clear();
    assertNull(filter.current());
  }

  @Test
  void headerNameConstantIsStandard() {
    assertEquals("X-Request-Id", RequestIdFilter.HEADER_NAME);
  }
}
