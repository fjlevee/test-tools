package com.fjl.test.tools.sip.authentication;

import java.io.Serializable;


/**
 * 
 * This class Nonce ...
 *
 * @author FJ. LEVEE (ofli8276)
 *
 * @version G0R0C0
 * @since G0R0C0
 */
public class Nonce implements Serializable
{
  /**
   * Attribute serialVersionUID is ...
   */
  private static final long serialVersionUID = -4019871856172877761L;
  /**
   * Nonce Value received for authentication
   */
  private String nonceValue;
  /**
   * Number of time the Nonce Value has been used
   */
  private int nonceCount ;
  /**
   * 
   * Constructor of Nonce.
   *
   * @param nonce Nonce Value received for authentication
   */
  public Nonce( String nonceValue ){
    
    this.nonceValue= nonceValue;
    this.nonceCount = 0;
  }
  /**
   * Get the Nonce Value
   * @return
   */
  public String getNonceValue ()
  {
    return nonceValue;
  }
  /**
   * Get the Nonce Value
   * @return
   */
  public void incrementNonceCount ()
  {
    nonceCount++;
  }
  /**
   * Get the Nounce Count
   * @return
   */
  public int getNonceCount ()
  {
    return nonceCount;
  }
  
  
}
