package com.algoprep.judge;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
public class JudgeService {
    private static final int MAX_SOURCE_LENGTH = 50_000;
    private static final Duration COMPILE_TIMEOUT = Duration.ofSeconds(6);
    private static final Duration RUN_TIMEOUT = Duration.ofSeconds(2);
    private static final Duration DOCKER_TIMEOUT = Duration.ofSeconds(12);
    private static final List<String> FORBIDDEN = List.of(
            "Runtime.getRuntime", "ProcessBuilder", "FileWriter", "FileOutputStream",
            "java.nio.file", "java.net.", "Socket", "ServerSocket", "System.load", "System.exit",
            "Thread", "Process", "URLClassLoader", "javax.script", "ObjectInputStream",
            "Files.", "Paths.get", "Class.forName", "Method.invoke", "Compiler", "Unsafe");
    private final ObjectMapper yaml = new ObjectMapper(new YAMLFactory());
    private final ObjectMapper json = new ObjectMapper();
    private final JudgeMainGenerator generator = new JudgeMainGenerator();
    private final Semaphore concurrency = new Semaphore(2);
    private final boolean dockerEnabled;
    private final String dockerImage;

    @Autowired
    public JudgeService(
            @Value("${algoprep.judge.docker:false}") boolean dockerEnabled,
            @Value("${algoprep.judge.dockerImage:eclipse-temurin:21-jdk-jammy}") String dockerImage) {
        this.dockerEnabled = dockerEnabled;
        this.dockerImage = dockerImage;
    }

    /** Test-friendly constructor. */
    public JudgeService() {
        this(false, "eclipse-temurin:21-jdk-jammy");
    }

    public JudgeResult judge(JudgeRequest request) {
        long started = System.nanoTime();
        validateSource(request.source());
        JudgeSpec spec = loadSpec(request.problemId());
        try {
            concurrency.acquire();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return result(false, 0, spec.cases().size(), List.of(), "", "Judge interrupted", started);
        }
        Path directory = null;
        try {
            directory = Files.createTempDirectory("algoprep-judge-");
            String extraImports = safeList(spec.imports()).stream()
                    .map(importName -> "import " + importName + ";\n")
                    .reduce("", String::concat);
            Files.writeString(directory.resolve(spec.className() + ".java"), extraImports + request.source(), StandardCharsets.UTF_8);
            Files.writeString(directory.resolve("JudgeMain.java"), generator.generate(spec), StandardCharsets.UTF_8);
            List<String> helpers = new ArrayList<>(safeList(spec.helpers()));
            // JudgeMain normalizes linked-list / tree / interval values, so helpers are always compiled.
            for (String required : List.of("ListNode", "TreeNode", "Interval")) {
                if (!helpers.contains(required)) helpers.add(required);
            }
            for (String helper : helpers) {
                Files.writeString(directory.resolve(helper + ".java"), generator.helperSource(helper), StandardCharsets.UTF_8);
            }
            Path projectClasses = projectClasses();
            if (dockerEnabled && dockerAvailable()) {
                ProcessResult docker = processDocker(directory, projectClasses);
                if (docker.timedOut()) {
                    return result(false, 0, spec.cases().size(), List.of(), "", "Docker judge timed out", started);
                }
                if (docker.exitCode() != 0 && !looksLikeJudgeNdjson(docker.output())) {
                    return result(false, 0, spec.cases().size(), List.of(), docker.output(), "", started);
                }
                return parseExecution(docker, spec.cases().size(), started);
            }
            String compileClasspath = projectClasses.toString();
            ProcessResult compile = process(
                    List.of(javac(), "-encoding", "UTF-8", "-cp", compileClasspath, "-d", ".", "*.java"),
                    directory, COMPILE_TIMEOUT);
            if (compile.timedOut()) return result(false, 0, spec.cases().size(), List.of(), "Compilation timed out", "", started);
            if (compile.exitCode() != 0) return result(false, 0, spec.cases().size(), List.of(), compile.output(), "", started);

            String runtimeClasspath = "." + File.pathSeparator + projectClasses;
            ProcessResult execution = process(
                    List.of(java(), "-Xmx64m", "-XX:MaxMetaspaceSize=32m", "-cp", runtimeClasspath, "JudgeMain"),
                    directory, RUN_TIMEOUT);
            if (execution.timedOut()) return result(false, 0, spec.cases().size(), List.of(), "", "Execution timed out", started);
            return parseExecution(execution, spec.cases().size(), started);
        } catch (IOException exception) {
            return result(false, 0, spec.cases().size(), List.of(), "", exception.getMessage(), started);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return result(false, 0, spec.cases().size(), List.of(), "", "Judge interrupted", started);
        } finally {
            concurrency.release();
            deleteDirectory(directory);
        }
    }

    public Map<String, String> template(String patternId, String problemId) {
        JudgeSpec spec = loadSpec(problemId);
        String source;
        if (spec.opsMode()) {
            source = opsTemplate(spec, patternId);
        } else {
            StringBuilder parameters = new StringBuilder();
            List<String> params = safeList(spec.params());
            for (int i = 0; i < params.size(); i++) {
                if (i > 0) parameters.append(", ");
                parameters.append(params.get(i)).append(" arg").append(i);
            }
            source = """
                    import java.util.*;

                    public class Solution {
                        public %s %s(%s) {
                            // TODO: implement the %s approach.
                            throw new UnsupportedOperationException("Not implemented");
                        }
                    }
                    """.formatted(spec.returns(), spec.method(), parameters, patternId);
        }
        return Map.of("className", spec.className(), "source", source, "language", "java");
    }

    private String opsTemplate(JudgeSpec spec, String patternId) {
        StringBuilder body = new StringBuilder();
        body.append("import java.util.*;\n\n");
        body.append("public class ").append(spec.className()).append(" {\n");
        List<String> ctorParams = safeList(spec.constructorParams());
        if (!ctorParams.isEmpty()) {
            body.append("    public ").append(spec.className()).append("(");
            for (int i = 0; i < ctorParams.size(); i++) {
                if (i > 0) body.append(", ");
                body.append(ctorParams.get(i)).append(" arg").append(i);
            }
            body.append(") {\n");
            body.append("        // TODO: initialize for ").append(patternId).append(".\n");
            body.append("    }\n\n");
        }
        if (spec.methods() != null) {
            for (JudgeSpec.MethodSig method : spec.methods()) {
                body.append("    public ").append(method.returns() == null ? "void" : method.returns())
                        .append(' ').append(method.name()).append('(');
                List<String> params = safeList(method.params());
                for (int i = 0; i < params.size(); i++) {
                    if (i > 0) body.append(", ");
                    body.append(params.get(i)).append(" arg").append(i);
                }
                body.append(") {\n");
                body.append("        // TODO: implement ").append(method.name()).append(".\n");
                body.append("        throw new UnsupportedOperationException(\"Not implemented\");\n");
                body.append("    }\n\n");
            }
        }
        body.append("}\n");
        return body.toString();
    }

    private JudgeSpec loadSpec(String problemId) {
        if (!problemId.matches("[a-z0-9-]+")) throw new IllegalArgumentException("Invalid problem id");
        ClassPathResource resource = new ClassPathResource("judge/specs/" + problemId + ".yml");
        if (!resource.exists()) throw new IllegalArgumentException("No judge spec for problem: " + problemId);
        try {
            // YAML flow sequences require array-type tokens to be quoted. Accept the concise
            // documented `params: [int[], int]` form as well as explicitly quoted forms.
            String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            content = quoteArrayTokens(content);
            JudgeSpec spec = yaml.readValue(content, JudgeSpec.class);
            if (spec.className() == null || spec.cases() == null) {
                throw new IllegalArgumentException("Invalid judge spec: " + problemId);
            }
            if (!spec.opsMode() && (spec.method() == null || spec.params() == null)) {
                throw new IllegalArgumentException("Invalid judge spec: " + problemId);
            }
            return spec;
        } catch (IOException exception) {
            throw new IllegalArgumentException("Unable to load judge spec: " + problemId, exception);
        }
    }

    private String quoteArrayTokens(String content) {
        // Quote bare array tokens without corrupting longer types (int[] vs int[][])
        // or values that are already quoted in the YAML.
        List<String> types = List.of(
                "char[][]", "int[][]", "ListNode[]", "Interval[]", "String[]", "double[]", "long[]", "int[]");
        Map<String, String> placeholders = new java.util.LinkedHashMap<>();
        int index = 0;
        for (String type : types) {
            String quoted = "\"" + type + "\"";
            String placeholder = "__JUDGE_TYPE_" + index++ + "__";
            placeholders.put(placeholder, quoted);
            content = content.replace(quoted, placeholder);
        }
        for (String type : types) {
            content = content.replace(type, "\"" + type + "\"");
        }
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            content = content.replace(entry.getKey(), entry.getValue());
        }
        return content;
    }

    private Path projectClasses() {
        Path fromUserDir = Path.of(System.getProperty("user.dir"), "target", "classes").toAbsolutePath().normalize();
        if (Files.isDirectory(fromUserDir)) return fromUserDir;
        try {
            URL location = JudgeService.class.getProtectionDomain().getCodeSource().getLocation();
            if (location != null) {
                Path codeSource = Path.of(location.toURI()).toAbsolutePath().normalize();
                if (Files.isDirectory(codeSource) && codeSource.getFileName().toString().equals("classes")) {
                    return codeSource;
                }
                Path sibling = codeSource.resolve("classes");
                if (Files.isDirectory(sibling)) return sibling;
            }
        } catch (URISyntaxException ignored) {
        }
        try {
            ClassPathResource resource = new ClassPathResource("judge/specs");
            if (resource.exists()) {
                Path specsDir = Path.of(resource.getURI()).toAbsolutePath().normalize();
                Path cursor = specsDir;
                for (int i = 0; i < 8 && cursor != null; i++, cursor = cursor.getParent()) {
                    Path candidate = cursor.resolve("target/classes");
                    if (Files.isDirectory(candidate)) return candidate;
                    if (cursor.getFileName() != null && cursor.getFileName().toString().equals("classes")
                            && Files.isDirectory(cursor)) {
                        return cursor;
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return fromUserDir;
    }

    private boolean dockerAvailable() {
        try {
            Process process = new ProcessBuilder("docker", "version", "--format", "{{.Server.Version}}")
                    .redirectErrorStream(true)
                    .start();
            boolean done = process.waitFor(2, TimeUnit.SECONDS);
            return done && process.exitValue() == 0;
        } catch (Exception exception) {
            return false;
        }
    }

    private boolean looksLikeJudgeNdjson(String output) {
        return output != null && output.lines().anyMatch(line -> line.contains("\"pass\""));
    }

    /**
     * Compile and run inside a disposable JDK container: no network, 128MB RAM, 1 CPU.
     * Mounts only the temp work directory and project classes (read-only).
     */
    private ProcessResult processDocker(Path directory, Path projectClasses)
            throws IOException, InterruptedException {
        String work = directory.toAbsolutePath().toString();
        String classes = projectClasses.toAbsolutePath().toString();
        String script = "set -e; "
                + "javac -encoding UTF-8 -cp /classes -d /work /work/*.java; "
                + "java -Xmx64m -XX:MaxMetaspaceSize=32m -cp /work:/classes JudgeMain";
        List<String> command = List.of(
                "docker", "run", "--rm",
                "--network", "none",
                "--memory", "128m",
                "--cpus", "1.0",
                "--pids-limit", "64",
                "--read-only",
                "--tmpfs", "/tmp:rw,noexec,nosuid,size=16m",
                "-v", work + ":/work",
                "-v", classes + ":/classes:ro",
                "-w", "/work",
                dockerImage,
                "bash", "-lc", script
        );
        return process(command, directory, DOCKER_TIMEOUT);
    }

    private ProcessResult process(List<String> command, Path directory, Duration timeout)
            throws IOException, InterruptedException {
        List<String> finalCommand = new ArrayList<>(command);
        if (finalCommand.contains("*.java")) {
            // ProcessBuilder does not expand globs; use explicit source names from the request-only directory.
            finalCommand.remove("*.java");
            try (Stream<Path> files = Files.list(directory)) {
                finalCommand.addAll(files.filter(path -> path.getFileName().toString().endsWith(".java"))
                        .map(path -> path.getFileName().toString())
                        .toList());
            }
        }
        ProcessBuilder builder = new ProcessBuilder(finalCommand).directory(directory.toFile()).redirectErrorStream(true);
        builder.redirectInput(ProcessBuilder.Redirect.from(new File("/dev/null")));
        Map<String, String> environment = builder.environment();
        String path = environment.getOrDefault("PATH", "/usr/bin:/bin");
        String javaHome = environment.get("JAVA_HOME");
        environment.clear();
        environment.put("PATH", path);
        environment.put("LANG", "C.UTF-8");
        if (javaHome != null && !javaHome.isBlank()) environment.put("JAVA_HOME", javaHome);
        Process process = builder.start();
        boolean completed = process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
        if (!completed) {
            process.destroyForcibly();
            process.waitFor(1, TimeUnit.SECONDS);
            return new ProcessResult(-1, read(process), true);
        }
        return new ProcessResult(process.exitValue(), read(process), false);
    }

    private JudgeResult parseExecution(ProcessResult execution, int total, long started) {
        List<JudgeResult.JudgeFailure> failures = new ArrayList<>();
        int passed = 0;
        try {
            for (String line : execution.output().lines().filter(line -> !line.isBlank()).toList()) {
                Map<String, Object> event = json.readValue(line, new TypeReference<>() {});
                if (Boolean.TRUE.equals(event.get("pass"))) passed++;
                else failures.add(new JudgeResult.JudgeFailure(
                        String.valueOf(event.get("name")), String.valueOf(event.get("expected")),
                        String.valueOf(event.get("actual")), event.get("message") == null ? "Wrong answer" : String.valueOf(event.get("message"))));
            }
        } catch (IOException exception) {
            return result(false, passed, total, failures, "", "Judge emitted invalid output: " + execution.output(), started);
        }
        String runtimeError = execution.exitCode() == 0 ? "" : execution.output();
        return result(runtimeError.isEmpty() && passed == total, passed, total, failures, "", runtimeError, started);
    }

    private void validateSource(String source) {
        if (source == null || source.length() > MAX_SOURCE_LENGTH) throw new IllegalArgumentException("Source exceeds 50,000 characters");
        if (source.contains("package ")) throw new IllegalArgumentException("Submitted code must use the default package");
        for (String forbidden : FORBIDDEN) if (source.contains(forbidden)) throw new IllegalArgumentException("Source contains forbidden API: " + forbidden);
    }

    private List<String> safeList(List<String> values) {
        return values == null ? List.of() : values;
    }

    private String javac() {
        return javaTool("javac");
    }

    private String java() {
        return javaTool("java");
    }

    private String javaTool(String name) {
        String home = System.getenv("JAVA_HOME");
        if (home == null || home.isBlank()) return name;
        Path tool = Path.of(home, "bin", name);
        return Files.isExecutable(tool) ? tool.toString() : name;
    }

    private String read(Process process) throws IOException {
        return new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private JudgeResult result(boolean ok, int passed, int total, List<JudgeResult.JudgeFailure> failures,
                               String compileErrors, String runtimeErrors, long started) {
        return new JudgeResult(ok, passed, total, failures, compileErrors, runtimeErrors,
                TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started));
    }

    private void deleteDirectory(Path directory) {
        if (directory == null) return;
        try (Stream<Path> paths = Files.walk(directory)) {
            paths.sorted((left, right) -> right.compareTo(left)).forEach(path -> {
                try { Files.deleteIfExists(path); } catch (IOException ignored) { }
            });
        } catch (IOException ignored) { }
    }

    private record ProcessResult(int exitCode, String output, boolean timedOut) {
    }
}
