# AlgoPrep

Self-contained **Java 21** interview training: reusable problem patterns, runnable solutions, JUnit tests, an in-browser judge, quizzes, timed mocks, and EN/RU/ES UI — all in one **Spring Boot 3.4** app.

## Quick start

```bash
./start.sh
```

Open [http://localhost:18080](http://localhost:18080).

The app binds to **127.0.0.1** unless `SERVER_ADDRESS` is set. That is deliberate: the judge compiles and runs submitted Java in this process unless `algoprep.judge.docker` is on. Do not publish port 18080 to a network you do not control. Docker Compose sets `SERVER_ADDRESS=0.0.0.0` only inside the container, so the published port is still the host's localhost mapping.

Requirements: **JDK 21+**. `start.sh` auto-detects common OpenJDK 21 installs and sets `JAVA_HOME`.

```bash
./mvnw test          # full suite
./mvnw -DskipTests package
java -jar target/algoprep-1.0.0-SNAPSHOT.jar
```

### Docker (no local JDK required)

```bash
docker compose up --build
```

Same UI at [http://localhost:18080](http://localhost:18080). Sync files persist in the `algoprep-sync` volume.

Optional harder judge isolation on the **host** (Docker available):

```bash
ALGOPREP_JUDGE_DOCKER=true ./start.sh
# or in application.yml: algoprep.judge.docker: true
```

Runs compile+execute inside `eclipse-temurin:21-jdk` with `--network none`, memory/CPU/pids limits.

### Playwright e2e

With the app already on `:18080`:

```bash
cd e2e && npm i && npx playwright install chromium
ALGOPREP_E2E_NO_SERVER=1 npm test
```

## What’s included

| Area | Content |
|------|---------|
| Patterns | **29** modules (classic DSA patterns + Spring Boot Interview Lab) |
| Problems | **156** catalog + **15** Blind Spot drills (**171** total) |
| By difficulty | Catalog **37** Easy · **85** Medium · **34** Hard; Blind Spot counts as Hard |
| Judge | Runnable YAML specs (including ops-mode for Trie/LRU/MinStack) + Monaco playground. Spring design drills without a hidden test are marked discussion-only. |
| Blind Spot | **15** unlabeled hard drills (some with alternate valid patterns) |
| Quiz | Guess-the-pattern recognition set |
| Locales | EN · RU · ES |
| Themes | Light / Dark |

Difficulty counts (Easy / Medium / Hard) show on the home hero, pattern list, pattern detail, tracks, and Blind Spot.

### Study modes in the UI

- **Patterns** — intuition, walkthrough, animated diagrams, company/frequency filters, difficulty counts, solution reveal, complexity self-check
- **Plans / Report** — week checklists and printable weekly progress
- **Playground** — edit Java (Monaco), run hidden tests via `/api/v1/judge`
- **Daily** — three deterministic problems per day + streak
- **Review** — spaced repetition (SM-2 lite) for due cards
- **Mock** — timed hard session, timeline + speak checklist
- **Skills / Metrics** — heatmap and abandonment stats
- **Tracks** — company presets (Amazon, Google, Meta, …)
- **Blind Spot / Quiz / Cheatsheet / Spring / Settings**
- **Sync** — create a sync key, push/pull progress between browsers (`/api/v1/sync`)

Progress is stored in `localStorage` by default; export/import and optional server sync live under Settings.

## How to study

1. Open a pattern → read intuition, signs, and the diagram.
2. Solve Easy → Medium → Hard (or use Daily / Tracks).
3. Use the playground when a judge spec exists; otherwise reveal the reference solution.
4. Mark done, answer the complexity check, schedule SRS grades in Review.
5. Drill recognition with Quiz and Blind Spot; run Mock under a timer.

Reference solutions and JUnit tests live under `src/main/java/com/algoprep/patterns/**` and `src/test/java/...`.

## Project layout

```
start.sh
pom.xml
.github/workflows/ci.yml      # tests + API smoke
scripts/                      # catalog/judge helpers
src/main/java/com/algoprep/
  course/                     # catalog API, SPA routes
  judge/                      # sandboxed compile+run judge
  sync/                       # progress sync store
  metrics/                    # event aggregation
  patterns/                   # solutions by pattern + challenge/
  common/                     # ListNode, TreeNode, Interval
src/main/resources/
  course/                     # catalog.yml, challenges, i18n, walkthroughs
  judge/specs/                # YAML test specs
  static/                     # SPA (HTML/CSS/JS), PWA, i18n
docs/                         # deeper documentation
```

## API (summary)

| Method | Path | Purpose |
|--------|------|---------|
| GET | `/api/v1/course?lang=` | Overview |
| GET | `/api/v1/course/patterns` | All patterns |
| GET | `/api/v1/course/patterns/{id}/problems/{pid}` | Problem detail |
| GET | `/api/v1/course/patterns/{id}/problems/{pid}/source` | Java source |
| GET | `/api/v1/course/walkthroughs/{pid}` | ASCII walkthrough |
| GET/POST | `/api/v1/course/quiz…` | Guess-the-pattern |
| GET | `/api/v1/course/challenges…` | Blind Spot drills + reveal |
| POST | `/api/v1/judge` | Run submitted Java |
| GET | `/api/v1/judge/template/{pattern}/{problem}` | Starter source |
| POST/PUT/GET | `/api/v1/sync…` | Progress sync key |
| POST/GET | `/api/v1/metrics…` | Events + summary |

Full tables and judge details: [docs/API.md](docs/API.md). Architecture notes: [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## Configuration

`src/main/resources/application.yml`:

- `server.port` — default `18080`
- `algoprep.sync.dir` — sync payload directory (`./data/sync`, gitignored)
- `algoprep.judge.docker` — optional containerized judge
- Actuator: `health`, `info` under `/actuator`

## CI

GitHub Actions (`.github/workflows/ci.yml`) runs `./mvnw test`, packages the jar, boots the app, smokes APIs, and runs Playwright e2e.

## License / intent

Personal interview-prep project for local study.
