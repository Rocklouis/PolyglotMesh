package com.polyglotmesh.api;

import com.polyglotmesh.engine.PolyglotEngine;
import com.polyglotmesh.security.validation.InputValidator;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class ExecutionController {

    private final PolyglotEngine engine;
    private final InputValidator validator;

    public ExecutionController(
            PolyglotEngine engine,
            InputValidator validator) {

        this.engine = engine;
        this.validator = validator;
    }

    public record ExecutionRequest(
            String language,
            String code
    ) {}

    public record ExecutionResponse(
            boolean success,
            String stdout,
            String stderr,
            String value,
            String error
    ) {}

    @PostMapping("/execute")
    public ResponseEntity<ExecutionResponse> execute(
            @RequestBody ExecutionRequest request) {

        if (request == null) {
            return ResponseEntity.badRequest().body(
                    new ExecutionResponse(
                            false,
                            "",
                            "",
                            null,
                            "Request cannot be empty"
                    )
            );
        }

        String validationError = validator.validate(
                request.language(),
                request.code()
        );

        if (validationError != null) {
            return ResponseEntity.badRequest().body(
                    new ExecutionResponse(
                            false,
                            "",
                            "",
                            null,
                            validationError
                    )
            );
        }

        PolyglotEngine.EvalResult result =
                engine.eval(
                        request.language(),
                        request.code()
                );

        boolean success = result.error() == null;

        return ResponseEntity.ok(
                new ExecutionResponse(
                        success,
                        result.stdout() == null ? "" : result.stdout(),
                        result.stderr() == null ? "" : result.stderr(),
                        result.value(),
                        result.error()
                )
        );
    }
}