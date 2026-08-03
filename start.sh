#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT"

pick_java() {
  if [[ -n "${JAVA_HOME:-}" && -x "${JAVA_HOME}/bin/java" ]]; then
    return
  fi
  for candidate in \
    /usr/lib/jvm/java-21-openjdk-amd64 \
    /usr/lib/jvm/java-21-openjdk \
    /opt/homebrew/opt/openjdk@21 \
    /usr/local/opt/openjdk@21; do
    if [[ -x "${candidate}/bin/java" ]]; then
      export JAVA_HOME="$candidate"
      return
    fi
  done
}

pick_java
export PATH="${JAVA_HOME:+$JAVA_HOME/bin:}$PATH"

if ! command -v java >/dev/null 2>&1; then
  echo "JDK 21+ is required. Install OpenJDK 21 and retry." >&2
  exit 1
fi

JAVA_VER="$(java -version 2>&1 | head -n1)"
echo "→ Java: $JAVA_VER"
echo "→ Starting AlgoPrep on http://localhost:8080"
echo "→ UI:     http://localhost:8080"
echo "→ API:    http://localhost:8080/api/v1/course"
echo

# Open browser shortly after boot (best-effort, non-blocking)
(
  for _ in $(seq 1 60); do
    if curl -sf "http://localhost:8080/actuator/health" >/dev/null 2>&1; then
      if command -v xdg-open >/dev/null 2>&1; then
        xdg-open "http://localhost:8080" >/dev/null 2>&1 || true
      elif command -v open >/dev/null 2>&1; then
        open "http://localhost:8080" >/dev/null 2>&1 || true
      fi
      break
    fi
    sleep 1
  done
) &

exec ./mvnw -q spring-boot:run
