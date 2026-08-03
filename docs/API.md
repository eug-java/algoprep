# AlgoPrep API

Base URL: `http://localhost:8080`

Most course GETs accept `?lang=en|ru|es` (default from server config / client).

## Course — `/api/v1/course`

| Method | Path | Description |
|--------|------|-------------|
| GET | `/` | Overview: title, pattern/problem counts, **easy/medium/hard counts**, pattern summaries |
| GET | `/ui` | UI bundle (cheatsheet rows, chrome strings from course overlays) |
| GET | `/patterns` | Full pattern list with nested problems |
| GET | `/patterns/{patternId}` | Pattern detail: intuition, walkthrough, mistakes, problems |
| GET | `/patterns/{patternId}/problems/{problemId}` | Problem meta (+ `walkthroughAscii` when available) |
| GET | `/patterns/{patternId}/problems/{problemId}/source` | Official Java source text |
| GET | `/walkthroughs/{problemId}` | `{ problemId, walkthroughAscii }` |
| GET | `/challenges` | Challenge list (pattern hidden) |
| GET | `/challenges/{id}` | Challenge prompt + hints (pattern still hidden) |
| GET | `/challenges/{id}/reveal` | Reveals `hiddenPattern`, optional `alternatePatterns`, complexities |
| GET | `/challenges/{id}/source` | Challenge solution source |
| GET | `/quiz` | Guess-the-pattern questions |
| POST | `/quiz/{questionId}/check` | Body: `{ "selectedPattern": "TWO_POINTERS" }` |

### Overview difficulty fields

```json
{
  "patternCount": 29,
  "problemCount": 170,
  "easyCount": 38,
  "mediumCount": 82,
  "hardCount": 50,
  "patterns": [
    {
      "id": "TWO_POINTERS",
      "problemCount": 6,
      "easyCount": 2,
      "mediumCount": 3,
      "hardCount": 1
    }
  ]
}
```

`hardCount` on the overview includes Challenge Yourself items (all HARD). Per-pattern counts cover catalog problems only.

### Problem fields (selected)

`id`, `title`, `difficulty`, `summary`, `whenToUse`, `timeComplexity`, `spaceComplexity`, `hints`, `tags`, `companies`, `frequency` (`HIGH`\|`MEDIUM`\|`LOW`), `className`, `walkthroughAscii`.

## Judge — `/api/v1/judge`

| Method | Path | Description |
|--------|------|-------------|
| GET | `/template/{patternId}/{problemId}` | Starter `Solution` source (`404` if no spec) |
| POST | `/` | Judge submission |

### Judge request

```json
{
  "patternId": "TWO_POINTERS",
  "problemId": "pair-with-target-sum",
  "source": "public class Solution { ... }",
  "lang": "en"
}
```

### Judge response

```json
{
  "ok": true,
  "passed": 2,
  "total": 2,
  "failures": [],
  "compileErrors": "",
  "runtimeErrors": "",
  "durationMs": 120
}
```

### Specs

YAML under `src/main/resources/judge/specs/{problemId}.yml`:

```yaml
className: Solution
method: search
params: ["int[]", int]
returns: int[]
referenceClass: com.algoprep.patterns.twopointers.PairWithTargetSum  # optional
referenceMethod: search
referenceStatic: true
helpers: [TreeNode]   # optional: ListNode, TreeNode, Interval, GraphNode
cases:
  - name: example1
    args: [[1, 2, 3, 4, 6], 6]
    # expected optional when referenceClass is set
```

Sandbox highlights:

- No `package` in user source; blocked APIs (process/file/net/reflection/…)
- Compile ~6s, run ~2s, `-Xmx64m`, concurrency semaphore (2)
- Runtime classpath includes `target/classes` for reference comparison

Regenerate missing specs (dev helper):

```bash
python3 scripts/generate_judge_specs.py
```

## Sync — `/api/v1/sync`

Anonymous key-based progress sync (files under `algoprep.sync.dir`).

| Method | Path | Description |
|--------|------|-------------|
| POST | `/keys` | `{ "syncKey": "<32 hex>" }` |
| GET | `/{syncKey}` | Stored `{ version, progress, meta, updatedAt }` |
| PUT | `/{syncKey}` | Body: `{ version, progress, meta, clientId }` |

## Metrics — `/api/v1/metrics`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/events` | `{ "type", "patternId?", "problemId?", "durationMs?", "props?" }` |
| GET | `/summary` | Aggregates: `totalEvents`, `byType`, `topAbandoned` |

In-memory process-local aggregation (resets on restart). The SPA also keeps local abandonment stats in `localStorage`.

## SPA routes

Hash routes are client-side; server forwards pretty paths to `index.html` via `SpaController`:

`/patterns`, `/challenge`, `/quiz`, `/cheatsheet`, `/skills`, `/daily`, `/mock`, `/spring`, `/settings`, `/review`, `/tracks`, `/metrics`.

## Health

- `GET /actuator/health`
- `GET /actuator/info`
