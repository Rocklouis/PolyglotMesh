package com.polyglotmesh.security.sandbox;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.io.IOAccess;
import org.springframework.stereotype.Component;

@Component
public class SandboxConfig {

    public Context createPythonContext() {
        return createContext("python");
    }

    public Context createJavaScriptContext() {
        return createContext("js");
    }

    private Context createContext(String language) {
    return Context.newBuilder(language)
            .allowAllAccess(false)
            .allowIO(IOAccess.NONE)
            .build();
}
}