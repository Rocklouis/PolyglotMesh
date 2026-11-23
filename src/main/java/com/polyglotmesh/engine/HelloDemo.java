package com.polyglotmesh.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class HelloDemo implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(HelloDemo.class);

    private final PolyglotEngine engine;

    public HelloDemo(PolyglotEngine engine) {
        this.engine = engine;
    }

    @Override
    public void run(String... args) {

        // JavaScript execution
        PolyglotEngine.EvalResult js =
                engine.evalJavaScript("console.log('Hello from JS')");
        log.info("js stdout=[{}]", js.stdout().trim());

        // Python execution
        try {
            PolyglotEngine.EvalResult py =
                    engine.evalPython("print('Hello from Python')");

            log.info("python stdout=[{}]", py.stdout().trim());

            if (py.error() != null) {
                log.error("Python error: {}", py.error());
            }

        } catch (Exception e) {
            log.error("Python execution failed", e);
        }
    }
}
