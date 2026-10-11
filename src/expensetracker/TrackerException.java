package expensetracker;

/**
 * An error caused by invalid user input or a store problem
 * Its message is shown to the user as-is, without a stack trace.
 */

public class TrackerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TrackerException(String message){
        super(message);
    }

    public TrackerException(String message, Throwable cause){
        super(message, cause);
    }
}
