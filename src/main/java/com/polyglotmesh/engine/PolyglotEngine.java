package com.polyglotmesh.engine;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.springframework.stereotype.Component;

import com.polyglotmesh.security.sandbox.SandboxConfig;

@Component
public class PolyglotEngine {

    private final SandboxConfig sandboxConfig;

    public PolyglotEngine(SandboxConfig sandboxConfig) {
        this.sandboxConfig = sandboxConfig;
    }

    public boolean isReady() {
        return true;
    }

    public record EvalResult(
            String stdout,
            String stderr,
            String value,
            String error
    ) {}

    public EvalResult evalPython(String code) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        try (Context context = sandboxConfig.createPythonContext(out,err)) {

            Value result = context.eval("python", code);

            return new EvalResult(
                    out.toString(StandardCharsets.UTF_8),
                    err.toString(StandardCharsets.UTF_8),
                    result == null ? null : result.toString(),
                    null
            );

        } catch (Exception e) {
            return new EvalResult(
                    out.toString(StandardCharsets.UTF_8),
                    err.toString(StandardCharsets.UTF_8),
                    null,
                    e.toString()
            );
        }
    }

    public EvalResult evalJavaScript(String code) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        try (Context context = sandboxConfig.createJavaScriptContext(out,err)) {

            Value result = context.eval("js", code);

            return new EvalResult(
                    out.toString(StandardCharsets.UTF_8),
                    err.toString(StandardCharsets.UTF_8),
                    result == null ? null : result.toString(),
                    null
            );

        } catch (Exception e) {
            return new EvalResult(
                    out.toString(StandardCharsets.UTF_8),
                    err.toString(StandardCharsets.UTF_8),
                    null,
                    e.toString()
            );
        }
    }

    public EvalResult eval(String language, String code) {

        if ("python".equalsIgnoreCase(language)
                || "py".equalsIgnoreCase(language)) {
            return evalPython(code);
        }

        if ("js".equalsIgnoreCase(language)
                || "javascript".equalsIgnoreCase(language)) {
            return evalJavaScript(code);
        }

        return new EvalResult(
                "", "", null,
                "Unsupported language: " + language
        );
    }
}