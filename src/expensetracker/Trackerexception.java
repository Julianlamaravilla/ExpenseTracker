package expensetracker;

/**
 * An error caused by invalid user input or a store problem
 * Its message is shown to the user as-is, without a stack trace.
 */

public class Trackerexception extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public Trackerexception(String message){
        super(message);
    }

    public Trackerexception(String message, Throwable cause){
        super(message, cause);
    }
}
