# Architecture

AlgoPrep is a monolith: Spring Boot serves the REST API **and** the static SPA. No separate frontend build step — ES modules load from `/js/*.js`.

```
Browser SPA (static/)
    │  fetch
    ▼
Spring Boot
 ├─ course/     catalog YAML → DTOs → JSON
 ├─ judge/      temp dir → javac → java → NDJSON results
 ├─ sync/       key → JSON file under data/sync
 └─ metrics/    in-memory counters
```

## Content pipeline

1. **Canonical English catalog** — `course/catalog.yml`, `challenges.yml`, `pattern-quiz.yml`
2. **Locale overlays** — `course/i18n/{en,ru,es}/*.yml` merge titles/prompts/hints
3. **UI chrome** — `static/i18n/{en,ru,es}.json`
4. **Walkthroughs** — `course/walkthroughs.yml` keyed by problem id. Every catalog problem has one: the worked example, the invariant already written as a hint, and a prompt to check that example by hand. Locale files override the English text.
5. **Solutions** — Java classes referenced by `className` in the catalog
6. **Judge specs** — optional YAML per problem id

`CourseCatalogLoader` / `ChallengeCatalogLoader` deserialize YAML; `CourseService` applies overlays and attaches walkthroughs.

## Judge flow

1. Validate source length and forbidden substrings
2. Load `judge/specs/{problemId}.yml`
3. Write `Solution.java`, `JudgeMain.java`, helpers into a temp directory
4. `javac`, then `java -Xmx64m -cp .:target/classes JudgeMain`
5. Parse one JSON object per line (`pass`, `expected`, `actual`)
6. Delete the temp directory

When `referenceClass` is set and a case omits `expected`, `JudgeMain` calls the reference method to compute the oracle. Tree/list problems usually ship explicit `expected` values because catalog node types differ from judge helpers.

Stateful problems use `mode: ops` with LeetCode-style `ops` / `expected` arrays (MinStack, Trie, LRU, …).

Optional Docker isolation (`algoprep.judge.docker=true`): compile+run in `docker run --network none --memory 128m` using `algoprep.judge.dockerImage`. Falls back to local `javac`/`java` if Docker is unavailable.

## Frontend modules

| File | Role |
|------|------|
| `app.js` | Router + all pages |
| `api.js` | Course / judge / sync / metrics clients |
| `progress.js` | Done set, local-day streak, recall grades, abandon stats, export/import |
| `complexity.js` | Canonical big-O classes for the self-check |
| `daily.js` | One Easy, one Medium, and one Hard for the local day |
| `editor.js` | Monaco loader (textarea fallback) |
| `diagrams.js` | SVG pattern animations |
| `diff.js` | Line diff vs official source |
| `speak.js` | Fallback speak tips (prefer i18n keys) |
| `theme.js` | Light/dark |

PWA: `manifest.webmanifest` + `sw.js` (static asset cache; API traffic is network-only).

## Persistence

| Data | Where |
|------|--------|
| Progress, theme, lang, recall cards, streak | Browser `localStorage` |
| Sync payloads | `./data/sync/{key}.json` |
| Metrics aggregates | Process memory |

Streak, Daily, and the weekly report use the browser's local date. A recall card is created only by an explicit grade (`again`, `hard`, `good`, `easy`) or a missed quiz pattern. `again` is due at the next local midnight. Later `good` and `easy` grades multiply the previous interval by the ease factor (starting at 2.5, clamped to 1.3–3.0). Marking a problem done records completion and does not invent a grade.

The complexity self-check compares a chosen class such as `O(n^2)` or `O(V+E)` with the catalog bound. `O(n)` does not match `O(n^2)`. A bound that states two classes, such as `O(log n) insert, O(1) median`, accepts either class.

## Extending

- **New pattern problem** — add YAML entry + Java class + JUnit test; optionally add `judge/specs/{id}.yml` (or run `scripts/generate_judge_specs.py`).
- **New challenge** — `challenges.yml` + class under `patterns/challenge/` + i18n overlays.
- **New UI string** — add key to all three `static/i18n/*.json` files.
- **New diagram** — extend `diagrams.js` `diagramFor` switch, plus `diagram.title.*` and `diagram.caption.*` in all three `static/i18n/*.json` files.

## Design constraints

- Brand-first home hero; avoid purple/cream AI-default themes
- Prefer CSS variables already defined in `app.css`
- Keep judge untrusted-code surface small; never expand forbidden-API allowlists casually
