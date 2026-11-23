package com.polyglotmesh.api;

import com.polyglotmesh.engine.PolyglotEngine;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    private final PolyglotEngine engine;

    public HealthController(PolyglotEngine engine) {
        this.engine = engine;
    }

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "polyglot-engine",
                "engineReady", engine.isReady()
        );
    }
}