package za.ac.salt.pipt.common.dataExchange;


/** A runtime exception thrown by PIPT classes if they encounter an invalid value. */
public class InvalidValueException extends RuntimeException
{
    /** Creates the runtime exception with the given error message.
     * @param message the error message */
    public InvalidValueException(String message)
    {
	super(message);
    }
}
