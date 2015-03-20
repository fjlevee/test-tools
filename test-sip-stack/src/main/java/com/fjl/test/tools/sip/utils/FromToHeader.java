package com.fjl.test.tools.sip.utils;

import java.util.Iterator;
import java.util.Vector;

import javax.sip.address.Address;
import javax.sip.header.FromHeader;
import javax.sip.header.ToHeader;

public class FromToHeader
{
  /**
   * Address contained in this Header
   */
  Address address;
  /**
   * tag contained in this Header
   */
  String tag;
  /**
   * Parameters contained in this Header
   */
  private Vector<HeaderParameter> parameters;
  
  /**
   * 
   * Constructor of FromToHeader.
   * @param fromHeader
   */
  public FromToHeader(FromHeader fromHeader){
    this.address = fromHeader.getAddress();
    this.tag = fromHeader.getTag();
    this.parameters = new Vector<HeaderParameter>();
    Iterator iter=  fromHeader.getParameterNames();
    if(iter!=null){
      for(;iter.hasNext();){
        String paramName = (String)iter.next();
        String paramValue = fromHeader.getParameter( paramName);
        parameters.add( new HeaderParameter(paramName,paramValue));
      }
      
    }
    
  }
  
  /**
   * 
   * Constructor of FromToHeader.
   * @param toHeader
   */
  public FromToHeader(ToHeader toHeader){
    this.address = toHeader.getAddress();
    this.tag = toHeader.getTag();
    this.parameters = new Vector<HeaderParameter>();
    Iterator iter=  toHeader.getParameterNames();
    if(iter!=null){
      for(;iter.hasNext();){
        String paramName = (String)iter.next();
        String paramValue = toHeader.getParameter( paramName);
        parameters.add( new HeaderParameter(paramName,paramValue));
      }
    }

  }
  /**
   * Get the header Address.
   * @return the header Address.
   */
  public Address getAddress ()
  {
    return address;
  }
  /**
   * Set the header Address.
   * @param address Specifies the header Address.
   */
  public void setAddress ( Address address)
  {
    this.address = address;
  }
  /**
   * Get the list of Header parameters
   * @return the list of Header parameters
   */
  public Vector<HeaderParameter> getParameters ()
  {
    return parameters;
  }
  
  /**
   * Get the Header Tag
   * @return the Header Tag
   */
  public String getTag ()
  {
    return tag;
  }
  /**
   * Set the Header Tag
   * @param tag Specifies the Header Tag
   */
  public void setTag ( String tag)
  {
    this.tag = tag;
  }
}
