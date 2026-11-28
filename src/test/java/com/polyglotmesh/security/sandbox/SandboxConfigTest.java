package com.polyglotmesh.security.sandbox;

import java.io.ByteArrayOutputStream;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
@Test
void pythonShouldNotAccessNetwork() throws Exception {

    try (ServerSocket server = new ServerSocket(0)) {
        server.setSoTimeout(1000);

        String pythonCode =
                "import socket\n" +
                "s = socket.socket()\n" +
                "s.connect(('127.0.0.1', " + server.getLocalPort() + "))";

        assertThrows(PolyglotException.class, () -> {
            try (Context context = sandboxConfig.createPythonContext(
                    new ByteArrayOutputStream(),
                    new ByteArrayOutputStream())) {

                context.eval("python", pythonCode);
            }
        });

        assertThrows(SocketTimeoutException.class, server::accept);
    }
}
@Test
void pythonShouldExecuteCodeWithinSandbox() {

    try (Context context = sandboxConfig.createPythonContext(
            new ByteArrayOutputStream(),
            new ByteArrayOutputStream())) {

        int result = context.eval("python", "1 + 1").asInt();

        assertEquals(2, result);
    }
}

@Test
void javaScriptShouldExecuteCodeWithinSandbox() {

    try (Context context = sandboxConfig.createJavaScriptContext(
            new ByteArrayOutputStream(),
            new ByteArrayOutputStream())) {

        int result = context.eval("js", "1 + 1").asInt();

        assertEquals(2, result);
    }
}
@Test
void javaScriptShouldNotAccessHostJavaSocket() throws Exception {

    try (ServerSocket server = new ServerSocket(0)) {
        server.setSoTimeout(1000);

        String javaScriptCode =
                "const Socket = Java.type('java.net.Socket');" +
                "const socket = new Socket('127.0.0.1', "
                + server.getLocalPort() + ");";

        assertThrows(PolyglotException.class, () -> {
            try (Context context = sandboxConfig.createJavaScriptContext(
                    new ByteArrayOutputStream(),
                    new ByteArrayOutputStream())) {

                context.eval("js", javaScriptCode);
            }
        });

        assertThrows(SocketTimeoutException.class, server::accept);
    }
}
}