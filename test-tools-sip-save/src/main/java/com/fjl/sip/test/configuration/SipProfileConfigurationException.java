package com.fjl.sip.test.configuration;

public class SipProfileConfigurationException extends Exception{


  /**
   * Attribute serialVersionUID is ...
   */
  private static final long serialVersionUID = 1376889752954844063L;

  /**
   * Constructor
   * @param message Description of the exception
   * @param e initial exception catched  
   */
  public SipProfileConfigurationException(String message, Throwable e) {
    super("SipProfileConfigurationException exception: " + message, e);
  }
  
  /**
   * Constructor
   * @param message Description of the exception
   */
  public SipProfileConfigurationException(String message) {
    super("SipProfileConfigurationException exception: " + message);
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
