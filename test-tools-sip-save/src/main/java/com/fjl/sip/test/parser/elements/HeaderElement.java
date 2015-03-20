package com.fjl.sip.test.parser.elements;

import fj_levee.test.parser.SipClientParserException;
import fj_levee.test.parser.SipTestCallFlowUtils;

public class HeaderElement
{
  public static final String HEADER_ELEMENT = "header";

  /**
   * Header Element name
   */
  private String name;

  /**
   * CDATA Section :<![CDATA[ SOME_DATA ]]>
   */
  private String cData;

  /**
   * 
   * Constructor of HttpRequestElement.
   */
  public HeaderElement ()
  {
    name = null;
    cData = null;
  }

  /**
   * 
   * @param messageElement
   * @param headerElement
   * @throws SipClientParserException
   */
  public void parseElement ( MessageElement messageElement, org.w3c.dom.Node headerElement)
      throws SipClientParserException
  {

    // Get the xmlCallFlowElement attributes
    org.w3c.dom.NamedNodeMap xmlCallFlowNodesAttributes = headerElement.getAttributes();

    // Param name.
    try
    {
      String name = xmlCallFlowNodesAttributes.getNamedItem( SipTestCallFlowUtils.NAME_ATTRIBUTE).getNodeValue();
      this.name = name;
    }
    catch (NullPointerException e)
    {
      throw new SipClientParserException( "HeaderElement.parseXmlCallFlowElement: name attribute can not be null");
    }

    // Retrieve the Http request Element Children
    org.w3c.dom.NodeList children = headerElement.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      org.w3c.dom.Node child = children.item( i);
      if (child.getNodeType() == org.w3c.dom.Node.CDATA_SECTION_NODE)
      {

        cData = child.getNodeValue();

      }
    }
    // Check if the Header is a Content Header
    if (SipTestCallFlowUtils.CONTENT_HEADER.equals( getName()))
    {
      // Don't add this header in the list of headers
      
      //char test ="dfdfdfdf";
      
      cData  = cData.replace( "\n", "\r\n");
      ContentElement content = new ContentElement(cData);
      messageElement.setContent(content);
      
    }else{
      messageElement.addHeader( this);
    }

    

  }

  /**
   * Get the param name
   * 
   * @return the param name
   */
  public String getName ()
  {
    return name;
  }

  /**
   * Set the param name
   * 
   * @param name specifies the new name
   */
  public void setName ( String name)
  {
    this.name = name;
  }

  /**
   * Get the CDATA content.
   * 
   * @return the CDATA content
   */
  public String getCData ()
  {
    return cData;
  }

  /**
   * Set the CDATA content
   * 
   * @param cData specifies the new CDATA
   */
  public void setCData ( String cData)
  {
    this.cData = cData;
  }

}