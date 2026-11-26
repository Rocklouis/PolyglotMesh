package com.polyglotmesh.security.sandbox;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class SandboxConfigTest {

    private final SandboxConfig sandboxConfig = new SandboxConfig();

    @Test
    void pythonShouldNotAccessHostFileSystem() throws Exception {

        Path directory = Files.createTempDirectory("sandbox-test");
        Path targetFile = directory.resolve("blocked.txt");

        String filePath = targetFile.toString()
                .replace("\\", "\\\\");

        String pythonCode = "open('" + filePath + "', 'w')";

        try {
            assertThrows(PolyglotException.class, () -> {
                try (Context context = sandboxConfig.createPythonContext(
                        new ByteArrayOutputStream(),
                        new ByteArrayOutputStream())) {

                    context.eval("python", pythonCode);
                }
            });

            assertFalse(Files.exists(targetFile));

        } finally {
            Files.deleteIfExists(targetFile);
            Files.deleteIfExists(directory);
        }
    }
    @Test
void javaScriptShouldNotAccessHostFileSystem() throws Exception {

    Path directory = Files.createTempDirectory("sandbox-js-test");
    Path targetFile = directory.resolve("blocked.txt");

    String filePath = targetFile.toString()
            .replace("\\", "\\\\");

    String javaScriptCode =
            "const Files = Java.type('java.nio.file.Files');" +
            "const Paths = Java.type('java.nio.file.Paths');" +
            "Files.writeString(Paths.get('" + filePath + "'), 'test');";

    try {
        assertThrows(PolyglotException.class, () -> {
            try (Context context = sandboxConfig.createJavaScriptContext(
                    new ByteArrayOutputStream(),
                    new ByteArrayOutputStream())) {

                context.eval("js", javaScriptCode);
            }
        });

        assertFalse(Files.exists(targetFile));

    } finally {
        Files.deleteIfExists(targetFile);
        Files.deleteIfExists(directory);
    }
}
}