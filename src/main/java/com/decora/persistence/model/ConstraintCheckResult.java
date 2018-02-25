package com.decora.persistence.model;

public class ConstraintCheckResult {

    public static class Builder {

        private ConstraintCheckResult result;

        public Builder() {
            result = new ConstraintCheckResult();
        }

        public final ConstraintCheckResult build() {
            return result;
        }
        public final Builder setErrorMessage(final String errorMessage) {
            result = new ConstraintCheckResult(errorMessage);
            return this;
        }

    }

    private final boolean valid;

    private final String message;

    private ConstraintCheckResult() {
        message = null;
        valid = true;
    }

    public ConstraintCheckResult(final String errorMessage) {
        message = errorMessage;
        valid = false;
    }

    public final boolean isValid() {
        return valid;
    }

    public final String getMessage() {
        return message;
    }
}
