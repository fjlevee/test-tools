package com.fjl.test.tools.sip.call;



public class SipClientStackException extends Exception{
  
  

  /**
   * Attribute serialVersionUID is ...
   */
  private static final long serialVersionUID = 9189067813932957906L;

  /**
   * Constructor
   * @param message Description of the exception
   * @param e initial exception catched  
   */
  public SipClientStackException(String message, Throwable e) {
    super("SipClientStack exception: " + message, e);
  }
  
  /**
   * Constructor
   * @param message Description of the exception
   */
  public SipClientStackException(String message) {
    super("SipClientStack exception: " + message);
  }
  
    /**
     * Return toString representation of the exception.
     */
  public String toString() {
    String result = getMessage()+"\n"+getStackTrace();
    if (getCause()==null) return result;
    else return result+"\n caused by "+getCause();
  }
}
