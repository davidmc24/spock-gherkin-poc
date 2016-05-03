package spockgherkin.gradle

class FeatureMismatchException extends RuntimeException {
    FeatureMismatchException(String message) {
        super(message)
    }
}
