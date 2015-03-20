package com.fjl.test.tools.sip.utils;

/**
 * 
 * This class
 *
 * @author FJ LEVEE
 *
 * @version G0R0C1
 * @since G0R0C1
 */
public class SipHeader
{
  /**
   * Header Name
   */
  String headerName;
  /**
   * Header Value
   */
  String headerValue;
  /**
   * 
   * Constructor of SipHeader.   *
   * @param headerName
   * @param headerValue
   */
  public SipHeader(String headerName){
    this.headerName = headerName;
    this.headerValue = "";
  }
  
  /**
   * 
   * Constructor of SipHeader.   *
   * @param headerName
   * @param headerValue
   */
  public SipHeader(String headerName,String headerValue){
    this.headerName = headerName;
    this.headerValue = headerValue;
  }
  /**
   * Get the Header Name
   * @return the Header Name
   */
  public String getHeaderName ()
  {
    return headerName;
  }
  /**
   * Set the Header Name
   * @param headerName Specifies the Header Name
   */
  public void setHeaderName ( String headerName)
  {
    this.headerName = headerName;
  }
  /**
   * Get the Header Value
   * @return the Header Value
   */
  public String getHeaderValue ()
  {
    return headerValue;
  }
  /**
   * Set the Header Name
   * @param headerValue Specifies the Header Value
   */
  public void setHeaderValue ( String headerValue)
  {
    this.headerValue = headerValue;
  }
  
}
