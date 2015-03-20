package com.fjl.sip.test.parser.elements;

import java.util.Vector;

import fj_levee.test.parser.SipClientParserException;
import fj_levee.test.parser.SipTestCallFlow;
import fj_levee.test.parser.SipTestCallFlowAction;
import fj_levee.test.parser.SipTestCallFlowActor;
import fj_levee.test.parser.SipTestCallFlowUtils;

public class HttpRequestElement extends SipTestCallFlowAction
{
  public static final String HTTP_REQUEST_ELEMENT = "http";

  /**
   * Http Request method
   */
  private String method;

 
  /**
   * 
   * Constructor of HttpRequestElement.
   */
  public HttpRequestElement ()
  {
    super( HTTP_REQUEST_ELEMENT);
    method = "";
    parametersList = new Vector<ParamElement>();
  }

  /**
   * Parameters List
   */
  public Vector<ParamElement> parametersList;

  /**
   * 
   * @see com.orange_ft.xml.call.flow.viewer.elements.SipTestCallFlowAction#parseSipTestCallFlowElement(com.orange_ft.xml.call.flow.viewer.SipTestCallFlow,
   *      org.w3c.dom.Node)
   */
  @Override
  public boolean parseSipTestCallFlowAction ( SipTestCallFlow sipTestCallFlow, org.w3c.dom.Node SipTestCallFlowElement,int sipTestActionId)
      throws SipClientParserException
  {
    if(logger.isTraceEnabled()) logger.trace( "HttpRequestAction.parseSipTestCallFlowAction Start");
    this.sipTestActionId = sipTestActionId;
    // Get the SipTestCallFlowElement attributes
    org.w3c.dom.NamedNodeMap SipTestCallFlowNodesAttributes = SipTestCallFlowElement.getAttributes();

    // Http request method
    try
    {
      String method = SipTestCallFlowNodesAttributes.getNamedItem( SipTestCallFlowUtils.METHOD_ATTRIBUTE)
          .getNodeValue();
      this.method = method;
    }
    catch (NullPointerException e)
    {
      throw new SipClientParserException(
          "HttpRequestAction.parseSipTestCallFlowElement: method attribute can not be null");
    }
    // Http request time
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
    // Parse Http request Element Children
    // Retrieve the Http request Element Children
    org.w3c.dom.NodeList children = SipTestCallFlowElement.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      org.w3c.dom.Node child = children.item( i);
      if (child.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE)
      {
        if (SipTestCallFlowUtils.PARAM_ELEMENT == child.getNodeName())
        {
          ParamElement element = new ParamElement();
          element.parseElement( this, child);
        }
      }
    }

    this.direction = ActionDirection.TO_CLIENT;
    SipTestCallFlowActor httpClientActor = sipTestCallFlow.getHttpClientSipTestCallFlowActor();
    // Checks if the Http Client has been created
    if (httpClientActor == null)
    {
      // not created => create it
      httpClientActor = new SipTestCallFlowActor( SipTestCallFlow.HTTP_CLIENT_ACTOR_NAME,
          SipTestCallFlow.HTTP_CLIENT_ACTOR_NAME,false);
      sipTestCallFlow.addSipTestCallFlowActor( httpClientActor);
    }

    // The SipTestCallFlowActor is the Http Client Actor
    setSipTestCallFlowActor( sipTestCallFlow.getHttpClientSipTestCallFlowActor());
    // Add this HttpRequest Action to the HttpClient Actor
    httpClientActor.addSipTestCallFlowAction( this);
    if(logger.isTraceEnabled()) logger.trace( "HttpRequestAction.parseSipTestCallFlowAction End");
    return true;
  }

  /**
   * Get the http request method
   * 
   * @return the http request method
   */
  public String getMethod ()
  {
    return method;
  }

  /**
   * Set the http request method
   * 
   * @param method specifies the new Method
   */
  public void setMethod ( String method)
  {
    this.method = method;
  }

  /**
   * get Http Parameters Number
   * 
   * @param index of the Http Parameter
   * @return the Http Parameter
   */
  public int getHttpParametersNumber ()
  {
    return parametersList.size();
  }

  /**
   * get an Http Parameter
   * 
   * @param index of the Http Parameter
   * @return the Http Parameter
   */
  public ParamElement getHttpParameter ( int index)
  {
    return parametersList.get( index);
  }

  /**
   * get an Http Parameter
   * 
   * @param index of the Http Parameter
   * @return the Http Parameter
   */
  public ParamElement getHttpActionParameter ()
  {
    ParamElement element = null;
    for (int i = 0; i < parametersList.size(); i++)
    {
      element = parametersList.get( i);
      if (element.getName().equals( SipTestCallFlowUtils.ACTION_ATTRIBUTE))
      {
        return element;
      }
    }
    return null;
  }

  /**
   * Addd Http Parameter
   * 
   * @param parameter the Http Parameter
   */
  public void addHttpParameter ( ParamElement parameter)
  {
    parametersList.add( parameter);
  }
  /**
   * 
   * @see com.fjl.sip.test.parser.elements.SipTestCallFlowAction#getActionAsString()
   */
  @Override
  public String getActionAsString(){
    ParamElement actionParameter = getHttpActionParameter();
    String actionString = "UNKNOWN";
    if(actionParameter!=null){
      actionString = actionParameter.getValue();
    }
    
    return "HttpRequest, Method: "+method+", Action: "+actionString;
    
  }
  /**
   * 
   * @return
   */
  public String getAction(){
    ParamElement actionParameter = getHttpActionParameter();
    String actionString = "UNKNOWN";
    if(actionParameter!=null){
      actionString = actionParameter.getValue();
    }
    
    return actionString;
    
  }

}