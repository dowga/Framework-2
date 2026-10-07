package model;

import java.util.Map;

public class ValidationErrorResponse {

    private int status;
    private String message;
    private Map<String, String> errors;

    public ValidationErrorResponse() {}

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
