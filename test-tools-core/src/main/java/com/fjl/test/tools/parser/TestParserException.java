package com.fjl.test.tools.parser;

public class TestParserException extends Exception {

    /**
     * Attribute serialVersionUID is ...
     */
    private static final long serialVersionUID = 8169204267233541357L;

    /**
     * Constructor
     * 
     * @param message
     *            Description of the exception
     * @param e
     *            initial exception catched
     */
    public TestParserException(String message, Throwable e) {
        super("TestException exception: " + message, e);
    }

    /**
     * Constructor
     * 
     * @param message
     *            Description of the exception
     */
    public TestParserException(String message) {
        super("TestException exception: " + message);
    }

    /**
     * Return toString representation of the exception.
     */
    public String toString() {
        String result = getMessage() + "\n" + getStackTrace();
        if (getCause() == null)
            return result;
        else
            return result + "\n caused by " + getCause();
    }
}
