package exceptions;

public class RecordHasNotSingleThemeException extends IllegalArgumentException {
    public RecordHasNotSingleThemeException() {
    }

    public RecordHasNotSingleThemeException(String message) {
        super(message);
    }
}
