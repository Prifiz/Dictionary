package exceptions;

/**
 * Created by Prifiz on 03.01.2017.
 */
public class RecordHasNotSingleThemeException extends IllegalArgumentException {
    public RecordHasNotSingleThemeException() {
    }

    public RecordHasNotSingleThemeException(String message) {
        super(message);
    }
}
