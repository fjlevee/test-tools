package com.fjl.test.tools.sip.utils;

public class HeaderParameter
{
  /**
   * Header parameter name
   */
  private String name;
  /**
   * Header parameter value
   */
  private String value;
    
  /**
   * 
   * Constructor of HeaderParameter.
   *
   * @param name
   * @param value
   */
  public HeaderParameter(String name,String value){
    this.name = name;
    this.value = value;
  }
  /**
   * Get the Header parameter name
   * @return the Header parameter name
   */
  public String getName ()
  {
    return name;
  }
  /**
   * Set the Header parameter name
   * @param name Specifies the Header parameter name
   */
  public void setName ( String name)
  {
    this.name = name;
  }
  /**
   * Get the Header parameter value
   * @return the Header parameter value
   */
  public String getValue ()
  {
    return value;
  }
  /**
   * Set the Header parameter value
   * @param value Specifies the Header parameter value
   */
  public void setValue ( String value)
  {
    this.value = value;
  }

}
