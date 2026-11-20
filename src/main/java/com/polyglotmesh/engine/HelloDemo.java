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
        PolyglotEngine.EvalResult js = engine.evalJavaScript("console.log('Hello from JS')");
        log.info("js stdout=[{}]", js.stdout().trim());
    }
}