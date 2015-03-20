package com.fjl.sip.test.parser.elements;

import java.util.Vector;

import org.apache.log4j.Logger;

import com.fjl.sip.SipConstants;
import com.fjl.sip.call.content.Content;

import fj_levee.test.parser.SipClientParserException;
import fj_levee.test.parser.SipTestCallFlowUtils;

public class MessageElement
{
  private static final String MESSAGE_ELEMENT = "message";

  /**
   * Request Uri Element
   */
  RequestUriElement requestUri;

  /**
   * Response Uri Element
   */
  ResponseUriElement responseUri;

  /**
   * List of Headers
   */
  Vector<HeaderElement> headers;

  /**
   * Content
   */
  ContentElement content;

  /**
   * Log4J logger.
   */
  private Logger logger = Logger.getLogger( com.fjl.sip.test.parser.elements.MessageElement.class);

  /**
   * 
   * Constructor of MessageElement.
   */
  public MessageElement ()
  {
    requestUri = null;
    responseUri = null;
    headers = new Vector<HeaderElement>();
    content = null;
  }

  public void parseElement ( SipRequestElement requestElement, org.w3c.dom.Node messageElement)
      throws SipClientParserException
  {
    parse( messageElement);
    requestElement.setMessageElement( this);
  }

  public void parseElement ( SipResponseElement responseElement, org.w3c.dom.Node messageElement)
      throws SipClientParserException
  {
    parse( messageElement);
    responseElement.setMessageElement( this);
  }

  /**
   * Parse this Message element
   * 
   * @param messageElement
   * @throws SipClientParserException
   */
  private void parse ( org.w3c.dom.Node messageElement) throws SipClientParserException
  {
    // Get the xmlCallFlowElement attributes
    // org.w3c.dom.NamedNodeMap xmlCallFlowNodesAttributes = xmlCallFlowSubElement.getAttributes();
    // nothing to do with xmlCallFlowNodesAttributes

    // Parse Message Element Children
    // Retrieve the Http request Element Children
    org.w3c.dom.NodeList children = messageElement.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      org.w3c.dom.Node child = children.item( i);
      if (child.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE)
      {
        if (SipTestCallFlowUtils.REQUEST_URI_ELEMENT == child.getNodeName())
        {
          RequestUriElement element = new RequestUriElement();
          element.parseElement( this, child);
        }
        else if (SipTestCallFlowUtils.RESPONSE_URI_ELEMENT == child.getNodeName())
        {
          ResponseUriElement element = new ResponseUriElement();
          element.parseElement( this, child);
        }
        else if (SipTestCallFlowUtils.HEADER_ELEMENT == child.getNodeName())
        {
          HeaderElement element = new HeaderElement();
          element.parseElement( this, child);
          // Check if the Header was a Content Header
          if (SipTestCallFlowUtils.CONTENT_HEADER.equals( element.getName()))
          {
            // Remove the "Content header" from the list of headers
            headers.remove( element);

          }
        }

      }
    }
  }

  /**
   * Get the Sip Message as String
   * 
   * @param method specifies the method of this Sip Message
   * @return the Sip Message as String
   */
  public String getSipMessage ( String method)
  {

    StringBuffer sipMessageBuffer = new StringBuffer();

    // Check if the request URI is not null
    if (requestUri != null)
    {
      // Add the method and the SIP/2.0
      sipMessageBuffer.append( method + " ");
      sipMessageBuffer.append( requestUri.getValue());
      sipMessageBuffer.append( " SIP/2.0");
    }
    // Check if the response URI is not null
    else
    {
      sipMessageBuffer.append( requestUri.getValue());
    }
    // Add the Sip Headers
    for (int i = 0; i < headers.size(); i++)
    {
      HeaderElement header = headers.get( i);
      sipMessageBuffer.append( header.getName() + ": " + header.getCData() + "\n");
    }
    // Add the Content if not null
    if (content != null)
    {
      sipMessageBuffer.append( "\n" + content.getCData());
      sipMessageBuffer.append( "\n");
    }
    return sipMessageBuffer.toString();
  }

  /**
   * Set the Request Uri
   * 
   * @param requestUri
   */
  public void setRequestUri ( RequestUriElement requestUri)
  {
    this.requestUri = requestUri;
  }

  /**
   * Set the response URI
   * 
   * @param responseUri
   */
  public void setResponseUri ( ResponseUriElement responseUri)
  {
    this.responseUri = responseUri;
  }

  /**
   * Set the Content
   * 
   * @param content
   */
  public void setContent ( ContentElement content)
  {
    this.content = content;
  }

  /**
   * Add a Header in this Message
   * 
   * @param header specifies the header to be Added
   */
  public void addHeader ( HeaderElement header)
  {
    headers.add( header);
  }

  /**
   * get a Header contained in this Message
   * 
   * @param headerName specifies the header name
   * @return the header if found else null
   */
  public HeaderElement getHeader ( String headerName)
  {
    if (headerName != null)
    {
      for (int i = 0; i < headers.size(); i++)
      {
        if (headerName.equals( headers.get( i).getName()))
        {
          return headers.get( i);
        }
      }
    }
    return null;
  }
  /**
   * get the list of Headers in the message
   * 
   * @param headerName specifies the header name
   * @return the header if found else null
   */
  public Vector<HeaderElement> getHeaders ()
  {
    
    return headers;
  }
  /**
   * Get the Content Element
   * 
   * @return the Content
   */
  public ContentElement getContentElement ()
  {
    return content;
  }

  /**
   * Get the Content contained in this message element
   * 
   * @return the Content contained in this message element
   */
  public Content getContent(){
    Content contentReturned = null;
    if(content!=null){
      HeaderElement contentTypeHeader = getHeader( SipConstants.CONTENT_TYPE_HEADER);
      
      if(contentTypeHeader!=null){
        String contentType = contentTypeHeader.getCData();
        int slashIndex = contentType.indexOf( "/");
        String mainContentType = contentType;
        String contentSubType = "";
        String contentString = content.getCData();
              
        
        if(slashIndex!=-1){
          mainContentType = contentType.substring( 0,slashIndex);
          contentSubType = contentType.substring( slashIndex+1);
        }
        if(SipConstants.DTMF_RELAY_SUNCONTENT_TYPE.equals( contentSubType)){
          contentString=contentString.replace( "\n",System.getProperty( "line.separator"));
        }

        contentReturned = new Content(mainContentType,contentSubType,contentString);

      }
      
    }
    return contentReturned;
  }
}