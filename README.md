# AlgoPrep

Pattern-first coding interview course for **Java 21**, packaged as a single **Spring Boot 3.4** app with a local SPA, in-browser judge, quizzes, mock interviews, and EN/RU/ES i18n.

Inspired by Educative’s *Grokking the Coding Interview Patterns*, with fuller explanations, more problems per pattern, Challenge Yourself, and a Spring Boot interview lab.

## Quick start

```bash
./start.sh
```

Open [http://localhost:8080](http://localhost:8080).

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

Same UI at [http://localhost:8080](http://localhost:8080). Sync files persist in the `algoprep-sync` volume.

Optional harder judge isolation on the **host** (Docker available):

```bash
ALGOPREP_JUDGE_DOCKER=true ./start.sh
# or in application.yml: algoprep.judge.docker: true
```

Runs compile+execute inside `eclipse-temurin:21-jdk` with `--network none`, memory/CPU/pids limits.

### Playwright e2e

With the app already on `:8080`:

```bash
cd e2e && npm i && npx playwright install chromium
ALGOPREP_E2E_NO_SERVER=1 npm test
```

## What’s included

| Area | Content |
|------|---------|
| Patterns | **29** modules (28 classic + Spring Boot Interview Lab) |
| Problems | **155** catalog + **15** challenges (**170** total) |
| By difficulty | Catalog **38** Easy · **82** Medium · **35** Hard (every pattern has all three); challenges count as Hard |
| Judge | **156** runnable specs (incl. ops-mode for Trie/LRU/MinStack/…) + Monaco playground |
| Challenges | **15** unlabeled hard drills (some with `alternatePatterns`) |
| Quiz | Guess-the-pattern recognition set |
| Locales | EN · RU · ES |
| Themes | Light / Dark |

Difficulty counts (Easy / Medium / Hard) show on the home hero, pattern list, pattern detail, tracks, and challenges.

### Study modes in the UI

- **Patterns** — intuition, walkthrough, animated diagrams, company/frequency filters, difficulty counts, solution reveal, complexity self-check
- **Plans / Report** — week checklists and printable weekly progress
- **Playground** — edit Java (Monaco), run hidden tests via `/api/v1/judge`
- **Daily** — three deterministic problems per day + streak
- **Review** — spaced repetition (SM-2 lite) for due cards
- **Mock** — timed hard session, timeline + speak checklist
- **Skills / Metrics** — heatmap and abandonment stats
- **Tracks** — company presets (Amazon, Google, Meta, …)
- **Challenge / Quiz / Cheatsheet / Spring / Settings**
- **Sync** — create a sync key, push/pull progress between browsers (`/api/v1/sync`)

Progress is stored in `localStorage` by default; export/import and optional server sync live under Settings.

## How to study

1. Open a pattern → read intuition, signs, and the diagram.
2. Solve Easy → Medium → Hard (or use Daily / Tracks).
3. Use the playground when a judge spec exists; otherwise reveal the reference solution.
4. Mark done, answer the complexity check, schedule SRS grades in Review.
5. Drill recognition with Quiz and unlabeled Challenges; run Mock under a timer.

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
| GET | `/api/v1/course/challenges…` | Challenges + reveal |
| POST | `/api/v1/judge` | Run submitted Java |
| GET | `/api/v1/judge/template/{pattern}/{problem}` | Starter source |
| POST/PUT/GET | `/api/v1/sync…` | Progress sync key |
| POST/GET | `/api/v1/metrics…` | Events + summary |

Full tables and judge details: [docs/API.md](docs/API.md). Architecture notes: [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## Configuration

`src/main/resources/application.yml`:

- `server.port` — default `8080`
- `algoprep.sync.dir` — sync payload directory (`./data/sync`, gitignored)
- Actuator: `health`, `info` under `/actuator`

## CI

GitHub Actions (`.github/workflows/ci.yml`) runs `./mvnw test`, packages the jar, boots the app, and smokes course/judge/sync/metrics endpoints.

## License / intent

Personal interview-prep project. Not affiliated with Educative.
