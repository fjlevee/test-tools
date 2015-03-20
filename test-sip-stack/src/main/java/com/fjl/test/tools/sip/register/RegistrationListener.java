package com.fjl.test.tools.sip.register;

public interface RegistrationListener
{
  
  /**
   * Notifies a Registration Success
   */
  public void registrationSuccess();
  
 /**
  * Notifies a Registration failure
  * @param statusCode Specifies the Status Code received
  * @param reason Specifies the reason of the failure
  */
  public void registrationFailure(int statusCode,String reason);
}
