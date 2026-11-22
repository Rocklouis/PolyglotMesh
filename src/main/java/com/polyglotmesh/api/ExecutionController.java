
package com.polyglotmesh.api;

import com.polyglotmesh.engine.PolyglotEngine;
import com.polyglotmesh.security.validation.InputValidator;

import org.springframework.http.HttpStatus;
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
            String output,
            String error
    ) {}

    @PostMapping("/execute")
    public ResponseEntity<ExecutionResponse> execute(
            @RequestBody ExecutionRequest request) {

        if (request == null) {
            return ResponseEntity.badRequest().body(
                    new ExecutionResponse(
                            false, "", "Request cannot be empty"
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
                            false, "", validationError
                    )
            );
        }

        PolyglotEngine.EvalResult result =
                engine.eval(
                        request.language(),
                        request.code()
                );

        String output = result.stdout();

        if (output == null || output.isBlank()) {
            output = result.value() == null
                    ? ""
                    : result.value();
        }

        if (result.stderr() != null
                && !result.stderr().isBlank()) {

            output += (output.isBlank() ? "" : "\n")
                    + result.stderr();
        }

        if (result.error() != null) {
            return ResponseEntity.ok(
                    new ExecutionResponse(
                            false,
                            output,
                            result.error()
                    )
            );
        }

        return ResponseEntity.ok(
                new ExecutionResponse(
                        true,
                        output,
                        null
                )
        );
    }
}
