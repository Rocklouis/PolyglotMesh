package com.polyglotmesh.security.sandbox;

import java.io.OutputStream;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.io.IOAccess;
import org.springframework.stereotype.Component;

@Component
public class SandboxConfig {

    public Context createPythonContext(OutputStream out, OutputStream err) {
        return createContext("python", out, err);
    }

    public Context createJavaScriptContext(OutputStream out, OutputStream err) {
        return createContext("js", out, err);
    }

    private Context createContext(
            String language,
            OutputStream out,
            OutputStream err
    ) {
        return Context.newBuilder(language)
                .allowAllAccess(false)
                .allowIO(IOAccess.NONE)
                .out(out)
                .err(err)
                .build();
    }
}