package com.fjl.sip.test.parser.elements;

import com.fjl.sip.test.tools.SipTestTools;

import fj_levee.callflow.xml.configuration.XmlElementsConfiguration;
import fj_levee.test.parser.SipClientParserException;
import fj_levee.test.parser.SipTestCallFlow;
import fj_levee.test.parser.SipTestCallFlowAction;
import fj_levee.test.parser.SipTestCallFlowActor;
import fj_levee.test.parser.SipTestCallFlowUtils;

public class SipRequestElement extends SipTestCallFlowAction
{
  public static final String SIP_REQUEST_ELEMENT = "request";

  /**
   * Sip Request Direction
   */
  private String directionString;

  /**
   * Sip Request method
   */
  private String method;

  /**
   * Sip Request ifc type
   */
  private String ifctype;

  /**
   * Sip Request CallID
   */
  private String callid;

  /**
   * Sip Request From Sip Uri
   */
  private String fromSipUri;

  /**
   * Sip Request To Sip Uri
   */
  private String toSipUri;
  

  /**
   * This parameter is set to true if the request final destination is a server. The request uri is a server request uri. This parameter is set to true if the last contact uri received in a Initial INVITE request or in a Initial INVITE final response has to be used to send this request.
   * Today we use it only to make a convergence between an INVITE session and a Subscribe session.
   */
  private boolean finalDestinationIsServer;

  /**
   * Attribute auto generated
   */
  private boolean auto = false;

  /**
   * Message Element
   */
  private MessageElement message;

  /**
   * Sip Call Flow Actor Receiver
   */
  private SipTestCallFlowActor sipTestCallFlowActorReceiver;
  
  /**
   * Authenticated Request
   */
  private boolean authenticated;


  /**
   * 
   * Constructor of SipRequestElement.
   */
  public SipRequestElement ()
  {
    super( SIP_REQUEST_ELEMENT);
    sipTestCallFlowActorReceiver = null;
    finalDestinationIsServer = false;
    authenticated = false;
  }

  /**
   * 
   * @see com.fjl.sip.test.parser.elements.SipTestCallFlowAction#parseSipTestCallFlowAction(com.fjl.sip.test.parser.SipTestCallFlow,
   *      org.w3c.dom.Node, int)
   */
  @Override
  public boolean parseSipTestCallFlowAction ( SipTestCallFlow sipTestCallFlow, org.w3c.dom.Node SipTestCallFlowElement,
      int sipTestActionId) throws SipClientParserException
  {
    if (logger.isTraceEnabled()) logger.trace( "SipRequestElement.parseSipTestCallFlowAction Start");

    this.sipTestActionId = sipTestActionId;
    // Get the SipTestCallFlowElement attributes
    org.w3c.dom.NamedNodeMap SipTestCallFlowNodesAttributes = SipTestCallFlowElement.getAttributes();

    // Auto attribute
    try
    {
      String autoString = SipTestCallFlowNodesAttributes.getNamedItem( SipTestCallFlowUtils.AUTO_ATTRIBUTE)
          .getNodeValue();
      if (SipTestCallFlowUtils.TRUE.equals( autoString))
      {
        auto = true;
      }
    }
    catch (NullPointerException e)
    {
      // auto is false
    }
    
    // Authenticated Attribute
    try
    {
      String authenticatedString = SipTestCallFlowNodesAttributes.getNamedItem( SipTestCallFlowUtils.AUTHENTICATED_ATTRIBUTE)
          .getNodeValue();
      if (SipTestCallFlowUtils.TRUE.equals( authenticatedString))
      {
        authenticated = true;
      }
    }
    catch (NullPointerException e)
    {
      // authenticated is false
    }

    // Sip request direction
    try
    {
      directionString = SipTestCallFlowNodesAttributes.getNamedItem( SipTestCallFlowUtils.DIRECTION_ATTRIBUTE)
          .getNodeValue();
    }
    catch (NullPointerException e)
    {
      throw new SipClientParserException(
          "SipRequestElement.parseSipTestCallFlowElement: direction attribute can not be null");
    }
    // Sip request method
    try
    {
      method = SipTestCallFlowNodesAttributes.getNamedItem( SipTestCallFlowUtils.METHOD_ATTRIBUTE).getNodeValue();
    }
    catch (NullPointerException e)
    {
      throw new SipClientParserException(
          "SipRequestElement.parseSipTestCallFlowElement: method attribute can not be null");
    }
    // Sip request ifctype
    try
    {
      String ifctype = SipTestCallFlowNodesAttributes.getNamedItem( SipTestCallFlowUtils.IFC_TYPE_ATTRIBUTE)
          .getNodeValue();
      this.ifctype = ifctype;
    }
    catch (NullPointerException e)
    {
      // not present
    }

    // Sip request time
    try
    {
      String timeAsString = SipTestCallFlowNodesAttributes.getNamedItem( SipTestCallFlowUtils.TIME_ATTRIBUTE)
          .getNodeValue();
      if (timeAsString != null)
      {
        this.time = Long.parseLong( timeAsString);
      }
    }
    catch (NullPointerException e)
    {
      // not present
    }

    // Sip Request Call ID
    try
    {
      String callid = SipTestCallFlowNodesAttributes.getNamedItem( SipTestCallFlowUtils.CALL_ID_ATTRIBUTE)
          .getNodeValue();
      this.callid = callid;
    }
    catch (NullPointerException e)
    {
      throw new SipClientParserException(
          "SipRequestElement.parseSipTestCallFlowElement: callid attribute can not be null");
    }
    // Sip request From
    try
    {
      fromSipUri = SipTestCallFlowNodesAttributes.getNamedItem( SipTestCallFlowUtils.FROM_ATTRIBUTE).getNodeValue();

    }
    catch (NullPointerException e)
    {
      throw new SipClientParserException(
          "SipRequestElement.parseSipTestCallFlowElement: fromSipUri attribute can not be null");
    }
    // Sip request To
    try
    {
      toSipUri = SipTestCallFlowNodesAttributes.getNamedItem( SipTestCallFlowUtils.TO_ATTRIBUTE).getNodeValue();
    }
    catch (NullPointerException e)
    {
      throw new SipClientParserException(
          "SipRequestElement.parseSipTestCallFlowElement: toSipUri attribute can not be null");
    }
    // Is server is request uri
    finalDestinationIsServer = false;
    try
    {
      String value = SipTestCallFlowNodesAttributes.getNamedItem( SipTestCallFlowUtils.FINAL_DESTINATION_IS_SERVER).getNodeValue();
      if(SipTestCallFlowUtils.TRUE.equals( value)){
        finalDestinationIsServer = true;
      }
    }
    catch (NullPointerException e)
    {
      
    }
    try
    {
      toSipUri = SipTestCallFlowNodesAttributes.getNamedItem( SipTestCallFlowUtils.TO_ATTRIBUTE).getNodeValue();
    }
    catch (NullPointerException e)
    {
      throw new SipClientParserException(
          "SipRequestElement.parseSipTestCallFlowElement: toSipUri attribute can not be null");
    }

    // Parse Sip request Element Children
    // Retrieve the Http request Element Children
    org.w3c.dom.NodeList children = SipTestCallFlowElement.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      org.w3c.dom.Node child = children.item( i);
      if (child.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE)
      {
        if (SipTestCallFlowUtils.MESSAGE_ELEMENT == child.getNodeName())
        {
          MessageElement element = new MessageElement();
          element.parseElement( this, child);
        }

      }
    }

    // Check now the direction to add an actor if necessary
    SipTestCallFlowActor callFlowActor = null;

    boolean isRealSipTestRequest = true;

    this.direction = SipTestTools.getMessageDirection( directionString);

    if (direction == ActionDirection.TO_SERVER)
    {

      // request received by the B2B Component search if the from Uri actor has been created yet;
      String actorId = SipTestCallFlowUtils.getUserIdentifier( fromSipUri);

      callFlowActor = sipTestCallFlow.getSipTestCallFlowActor( actorId);
      if (callFlowActor == null)
      {

        // Check if the request is not destinated to a network component
        if (XmlElementsConfiguration.getActorForId( actorId) != null)
        {
          if (logger.isDebugEnabled())
            logger
                .debug( "SipRequestElement.parseSipTestCallFlowElement: this "+method+" request is an internal network request => Don't add it to the list of actions");
          return false;
        }

        callFlowActor = new SipTestCallFlowActor( actorId, sipTestCallFlow.getNextCurrentUEActorId(), fromSipUri, true);
        sipTestCallFlow.addSipTestCallFlowActor( callFlowActor);

      }
      // The SipTestCallFlowActor Actor is the UE with fromSipUri
      setSipTestCallFlowActor( callFlowActor);

      String actorReceiverId = SipTestCallFlowUtils.getUserIdentifier( toSipUri);
      SipTestCallFlowActor callFlowActorReceiver = sipTestCallFlow.getSipTestCallFlowActor( actorReceiverId);
      
      
      if (callFlowActorReceiver == null)
      {
        if(finalDestinationIsServer){
          
          if(logger.isDebugEnabled()) logger.debug( "SipRequestElement.parseSipTestCallFlowElement: this  "+method+" request is destinated to a server: check if the sender is a client");
          
          if(callFlowActor.isSipClient()){
            
            if(logger.isDebugEnabled()) logger.debug( "SipRequestElement.parseSipTestCallFlowElement: this  "+method+" request is destinated to a server and sent by a client");
          }else{
            if(logger.isDebugEnabled()) logger.debug( "SipRequestElement.parseSipTestCallFlowElement: this  "+method+" request is an internal network request => Don't add it to the list of actions");
            return false;
          }
          
        }else{

          // Check if the request is not destinated to a network component
          if (XmlElementsConfiguration.getActorForId( actorId) != null)
          {
            
            
            if (logger.isDebugEnabled())
              logger
                  .debug( "SipRequestElement.parseSipTestCallFlowElement: this request is an internal network request => Don't add it to the list of actions");
            return false;
          
           
          }
  
          callFlowActorReceiver = new SipTestCallFlowActor( actorReceiverId, sipTestCallFlow.getNextCurrentUEActorId(),
              toSipUri, true);
          sipTestCallFlow.addSipTestCallFlowActor( callFlowActorReceiver);
  
        }
        
      }
      setSipTestCallFlowActorReceiver( callFlowActorReceiver);
    }
    else
    {
      // request sent by the B2B Component search if the to Uri actor has been created yet;
      String actorId = SipTestCallFlowUtils.getUserIdentifier( toSipUri);
      callFlowActor = sipTestCallFlow.getSipTestCallFlowActor( actorId);
      if (callFlowActor == null)
      {
        // Check if the request is not destinated to a network component
        if (XmlElementsConfiguration.getActorForId( actorId) != null)
        {
          if (logger.isDebugEnabled())
            logger
                .debug( "SipRequestElement.parseSipTestCallFlowElement: this request is an internal network request => Don't add it to the list of actions");
          return false;
        }
        callFlowActor = new SipTestCallFlowActor( actorId, sipTestCallFlow.getNextCurrentUEActorId(), toSipUri, true);
        sipTestCallFlow.addSipTestCallFlowActor( callFlowActor);
      }
      // The SipTestCallFlowActor Actor is the UE with toSipUri
      setSipTestCallFlowActor( callFlowActor);
    }

    // Add this action in the actor actions' list
    actor.addSipTestCallFlowAction( this);
    if (logger.isTraceEnabled()) logger.trace( "SipRequestElement.parseSipTestCallFlowAction End");
    return true;
  }

  /**
   * Get the sip request method
   * 
   * @return the sip request method
   */
  public String getMethod ()
  {
    return method;
  }

  /**
   * Set the sip request method
   * 
   * @param method specifies the new sip request method
   */
  public void setMethod ( String method)
  {
    this.method = method;
  }

  /**
   * Get the sip request ifc type
   * 
   * @return the sip request ifc type
   */
  public String getIfctype ()
  {
    return ifctype;
  }

  /**
   * Set the sip request ifc type
   * 
   * @param ifctype specifies the new sip request ifc type
   */
  public void setIfctype ( String ifctype)
  {
    this.ifctype = ifctype;
  }

  /**
   * Get the sip request callid
   * 
   * @return the sip request callid
   */
  public String getCallid ()
  {
    return callid;
  }

  /**
   * Set the sip request callid
   * 
   * @param callid specifies the new sip request callid
   */
  public void setCallid ( String callid)
  {
    this.callid = callid;
  }

  /**
   * Get the sip request From sip uri
   * 
   * @return the sip request From sip uri
   */
  public String getFromSipUri ()
  {
    return fromSipUri;
  }

  /**
   * Set the sip request From sip uri
   * 
   * @param fromSipUri specifies the new sip request From sip uri
   */
  public void setFromSipUri ( String fromSipUri)
  {
    this.fromSipUri = fromSipUri;
  }

  /**
   * Get the sip request To sip uri
   * 
   * @return the sip request To sip uri
   */
  public String getToSipUri ()
  {
    return toSipUri;
  }

  /**
   * Set the sip request To sip uri
   * 
   * @param toSipUri specifies the new sip request To sip uri
   */
  public void setToSipUri ( String toSipUri)
  {
    this.toSipUri = toSipUri;
  }

  /**
   * Get the Sip Message contained in this Element
   * 
   * @return the Sip Message contained in this Element
   */
  public String getSipMessageAsString ()
  {
    StringBuffer sipMessageBuffer = new StringBuffer();
    if (message != null)
    {

      sipMessageBuffer.append( message.getSipMessage( method));
    }
    return sipMessageBuffer.toString();
  }

  public boolean isAuto ()
  {
    return auto;
  }

  /**
   * 
   * @param message
   */
  public void setMessageElement ( MessageElement message)
  {
    this.message = message;
  }

  /**
   * Get the Message
   * 
   * @return the message
   */
  public MessageElement getMessageElement ()
  {
    return message;
  }

  /**
   * 
   * @see com.fjl.sip.test.parser.elements.SipTestCallFlowAction#getActionAsString()
   */
  @Override
  public String getActionAsString ()
  {
    return "SipRequest, Method: " + method + ", Direction: " + direction;
  }

  /**
   * Get the message actor receiver in case of this Request is an initial SIP Request
   * 
   * @return the message actor receiver in case of this Request is an initial SIP Request
   */
  public SipTestCallFlowActor getSipTestCallFlowActorReceiver ()
  {
    return sipTestCallFlowActorReceiver;
  }

  /**
   * Set the message actor receiver in case of this Request is an initial SIP Request
   * 
   * @param sipTestCallFlowActorReceiver
   */
  public void setSipTestCallFlowActorReceiver ( SipTestCallFlowActor sipTestCallFlowActorReceiver)
  {
    this.sipTestCallFlowActorReceiver = sipTestCallFlowActorReceiver;
  }
  /**
   * Get if the request final destination is a server. TheGet if the request uri is a server request uri. This parameter is set to true if the last contact uri received in a Initial INVITE request or in a Initial INVITE final response has to be used to send this request.
   * Today we use it only to make a convergence between an INVITE session and a Subscribe session.
   * @return true if the request uri is a server request uri.
   */
  public boolean isFinalDestinationIsServer ()
  {
    return finalDestinationIsServer;
  }

  public boolean isAuthenticated ()
  {
    return authenticated;
  }

}