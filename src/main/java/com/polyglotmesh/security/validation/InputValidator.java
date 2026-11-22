package com.polyglotmesh.security.validation;

import org.springframework.stereotype.Component;

@Component
public class InputValidator {

    public String validate(String language, String code) {

        if (language == null || language.isBlank()) {
            return "Language cannot be empty";
        }

        if (code == null || code.isBlank()) {
            return "Code cannot be empty";
        }

        String lang = language.toLowerCase().trim();

        if (!lang.equals("python") &&
            !lang.equals("py") &&
            !lang.equals("javascript") &&
            !lang.equals("js")) {

            return "Unsupported language: " + language;
        }

        return null;
    }
}