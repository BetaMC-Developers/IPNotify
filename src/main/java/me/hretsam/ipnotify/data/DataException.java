package me.hretsam.ipnotify.data;

/**
 *
 * @author Hretsam
 * 
 * This is thrown when the datahandler cannot save or load the data
 */
public class DataException extends Exception {

    /**
     * Creates a new instance of <code>DataException</code> without detail message.
     */
    public DataException() {
    }

    /**
     * Constructs an instance of <code>DataException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public DataException(String msg) {
        super(msg);
    }
}
