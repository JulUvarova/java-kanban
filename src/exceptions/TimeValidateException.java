package exceptions;

public class TimeValidateException extends RuntimeException {

    public TimeValidateException() {
    }

    public TimeValidateException(String message) {
        super(message);
    }
}
