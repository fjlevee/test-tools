package com.fjl.sip.test.actions;


public class HttpParameter
{
  /**
   * Http Parameter name
   */
  private String name;

  /**
   * Http Parameter value
   */
  private String value;

  /**
   * 
   * Constructor of HttpParameter.
   */
  public HttpParameter ()
  {
    name = null;
    value = null;
  }
  /**
   * 
   * Constructor of HttpParameter.

   * @param name Specifies the Http Parameter name
   * @param value Specifies the Http Parameter value
   */
  public HttpParameter (String name,String value)
  {
    this.name = name;
    this.value = value;
  }
 
  /**
   * Get the Http Parameter name
   * 
   * @return the Http Parameter name
   */
  public String getName ()
  {
    return name;
  }

  /**
   * Set the Http Parameter name
   * 
   * @param name specifies the new name
   */
  public void setName ( String name)
  {
    this.name = name;
  }

  /**
   * Get the Http Parameter value
   * 
   * @return the Http Parameter value
   */
  public String getValue ()
  {
    return value;
  }

  /**
   * Set the Http Parameter value
   * 
   * @param value specifies the new value
   */
  public void setValue ( String value)
  {
    this.value = value;
  }
}
