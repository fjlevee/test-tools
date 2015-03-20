package com.fjl.sip.test.actions;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.fjl.sip.SipConstants;
import com.fjl.sip.test.SipClientActor;
import com.fjl.sip.test.SipClientTestManagerConfiguration;
import com.fjl.sip.test.parser.elements.HeaderElement;
import com.fjl.sip.test.parser.elements.HttpRequestElement;
import com.fjl.sip.test.parser.elements.MessageElement;
import com.fjl.sip.test.parser.elements.SipRequestElement;
import com.fjl.sip.test.parser.elements.SipResponseElement;
import com.fjl.sip.utils.SipHeader;

import fj_levee.test.parser.SipTestCallFlowAction;
import fj_levee.test.parser.SipTestCallFlowAction.ActionDirection;

public class SipClientActionFactory
{
  /**
   * SipClientActionFactory instance
   */
  private static SipClientActionFactory instance;

  /**
   * Log4J Logger
   */
  Logger logger;

  /**
   * DEFAULT_MINIMUM_WAIT_TIME_BEFORE_SENDING_MESSAGE
   */
  public static long DEFAULT_MINIMUM_WAIT_TIME_BEFORE_SENDING_MESSAGE = 1000;
  /**
   * DEFAULT_MINIMUM_WAIT_TIME_BEFORE_SENDING_ACK_MESSAGE
   */
  public static long DEFAULT_MINIMUM_WAIT_TIME_BEFORE_SENDING_ACK_MESSAGE = 100;
  
  /**
   * DEFAULT_MINIMUM_WAIT_TIME_BEFORE_RECEIVING_MESSAGE
   */
  public static long DEFAULT_MINIMUM_WAIT_TIME_BEFORE_RECEIVING_MESSAGE = 5000;
  /**
   * DEFAULT_MAXIMUM_WAIT_TIME
   */
  public static long DEFAULT_MAXIMUM_WAIT_TIME = 60000;
  
  /**
   * SECURITY_TIME_TO_WAIT_ADDED
   */
  //public static long SECURITY_TIME_TO_WAIT_ADDED = 5000;

  /**
   * Minimum Wait Time Before Sending Message
   */
  //private long minimumWaitTimeBeforeSendingMessage;

  /**
   * Minimum Wait Time Before Sending Ack Message
   */
 // private long minimumWaitTimeBeforeSendingAckMessage;
  /**
   * Minimum Wait Time
   */
  //private long minimumWaitTimeBeforeReceivingMessage;
  

  /**
   * Maximum Wait Time
   */
  private long maximumWaitTime;
  /**
   * List of Sip Headers To Be Copied from Sip Message if prensent in the Sip Test Call Flow
   */
  private Vector<String> sipHeadersToCopy;

  /**
   * Constructor
   * 
   */
  private SipClientActionFactory ()
  {
    logger = Logger.getLogger( com.fjl.sip.test.actions.SipClientActionFactory.class);
    maximumWaitTime = SipClientTestManagerConfiguration.getLongParameter(
        SipClientTestManagerConfiguration.ACTION_MAX_WAIT_TIME, DEFAULT_MAXIMUM_WAIT_TIME);
    /*
    minimumWaitTimeBeforeSendingMessage = SipClientTestLauncherConfiguration.getLongParameter(
        SipClientTestLauncherConfiguration.ACTION_MIN_WAIT_TIME_SEND_MESSAGE, DEFAULT_MINIMUM_WAIT_TIME_BEFORE_SENDING_MESSAGE);
    minimumWaitTimeBeforeReceivingMessage = SipClientTestLauncherConfiguration.getLongParameter(
        SipClientTestLauncherConfiguration.ACTION_MIN_WAIT_TIME_RECEIVE_MESSAGE, DEFAULT_MINIMUM_WAIT_TIME_BEFORE_RECEIVING_MESSAGE);
    minimumWaitTimeBeforeSendingAckMessage = SipClientTestLauncherConfiguration.getLongParameter(
        SipClientTestLauncherConfiguration.ACTION_MIN_WAIT_TIME_SEND_ACK_MESSAGE, DEFAULT_MINIMUM_WAIT_TIME_BEFORE_SENDING_ACK_MESSAGE);
    */
    List list = SipClientTestManagerConfiguration.getListParameter(SipClientTestManagerConfiguration.HEADERS_LIST_COPIED);
    sipHeadersToCopy = new Vector<String>();
    Iterator iter =list.iterator();
    for(;iter.hasNext();){
      String header = (String)iter.next();
      if(logger.isTraceEnabled()) logger.trace( "SipClientActionFactory.constructor: Header copied in Sip Message: "+header);
      sipHeadersToCopy.add(header);
    }
    
    
  }

  /**
   * Create a SipClientAction
   * 
   * @param sipClientActor
   * @param sipTestCallFlowAction
   * @param previousActionTime
   * @param lastSipClientActionCreated
   * @return the SipClientAction created
   */
  public static SipClientAction createSipClientAction ( SipClientActor sipClientActor,
      SipTestCallFlowAction sipTestCallFlowAction, ClientAction lastSipClientActionCreated)
  {
    if (instance == null)
    {
      instance = new SipClientActionFactory();
    }
    return instance.buildSipClientAction( sipClientActor, sipTestCallFlowAction, lastSipClientActionCreated);
  }
  
  /**
   * build a SipClientAction
   * 
   * @param sipClientActor
   * @param sipTestCallFlowAction
   * @param previousActionTime
   * @param lastSipClientActionCreated
   * @return the SipClientAction created
   */
  private SipClientAction buildSipClientAction ( SipClientActor sipClientActor,
      SipTestCallFlowAction sipTestCallFlowAction, ClientAction lastSipClientActionCreated)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientActionFactory.buildSipClientAction Start");
    SipClientAction sipClientAction = null;
    
   
    String actionType = sipTestCallFlowAction.getType();
    if (SipRequestElement.SIP_REQUEST_ELEMENT.equals( actionType))
    {

      // The direction is set regarding the B2B Application.
      // It means that a direction received must be sent from the Sip Client side.
      // A direction sent must be received from the Sip Client side.
      if (ActionDirection.TO_CLIENT == sipTestCallFlowAction.getDirection())
      {
        if (logger.isDebugEnabled())
          logger
              .debug( "SipClientActionFactory.buildSipClientAction: Action to be created is a ReceiveSipRequestAction");
        // Calculate the Time To Wait for Request
        
        SipRequestElement requestElement = (SipRequestElement)sipTestCallFlowAction;
        sipClientAction = new  ReceiveSipRequestAction(sipClientActor,(SipRequestElement)sipTestCallFlowAction,maximumWaitTime);
        
        // In case of ACK to be received:
        // Check if the last Action Created is an error => In this case the Receive ACK must not be done (done by the SIP layer)
        if(SipConstants.ACK.equals( requestElement.getMethod()))
          try{
            SendSipResponseAction sendSipAction = (SendSipResponseAction) lastSipClientActionCreated;
            if(sendSipAction.getStatusCode()>=300){
              sipClientAction.setPreviousMessageWasAnError(true);
            }
          }catch (ClassCastException e) {
            // TODO: handle exception
          } 
        
      }
      else
      {
        if (logger.isDebugEnabled())
          logger.debug( "SipClientActionFactory.buildSipClientAction: Action to be created is a SendSipRequestAction");
        // Calculate the Time To Wait for Request
        SipRequestElement requestElement = (SipRequestElement)sipTestCallFlowAction;
        
        if(SipConstants.ACK.equals( requestElement.getMethod())){
          //timeToWait = minimumWaitTimeBeforeSendingAckMessage;
          sipClientAction = new  SendSipRequestAction(sipClientActor,(SipRequestElement)sipTestCallFlowAction);
          //sipClientActor.getSipClientTest().addClientActionListener( ""+lastSipClientActionCreated.getActionId(),sipClientActor);
          // Check if the last Action Created is an error => In this case the Send ACK must not be done
          try{
            ReceiveSipResponseAction receiveSipAction = (ReceiveSipResponseAction) lastSipClientActionCreated;
            if(receiveSipAction.getStatusCode()>=300){
              sipClientAction.setPreviousMessageWasAnError(true);
            }
          }catch (ClassCastException e) {
            // TODO: handle exception
          } 
          
        }
        else{
          sipClientAction = new  SendSipRequestAction(sipClientActor,(SipRequestElement)sipTestCallFlowAction);
        }
      }

    }
    else if (SipResponseElement.SIP_RESPONSE_ELEMENT.equals( actionType))
    {

      if (ActionDirection.TO_CLIENT == sipTestCallFlowAction.getDirection())
      {
        if (logger.isDebugEnabled())
          logger.debug( "SipClientActionFactory.buildSipClientAction: Action to be created is a ReceiveSipResponse");

        sipClientAction = new  ReceiveSipResponseAction(sipClientActor,(SipResponseElement)sipTestCallFlowAction,maximumWaitTime);
        
      }
      else
      {
        if (logger.isDebugEnabled())
          logger.debug( "SipClientActionFactory.buildSipClientAction: Action to be created is a SendSipResponse");
        sipClientAction = new  SendSipResponseAction(sipClientActor,(SipResponseElement)sipTestCallFlowAction);
        
      }
    }
    else if (HttpRequestElement.HTTP_REQUEST_ELEMENT.equals( actionType))
    {
      if (logger.isDebugEnabled())
        logger.debug( "SipClientActionFactory.buildSipClientAction: Action to be created is a HttpRequest");
      
    }

    if (logger.isTraceEnabled()) logger.trace( "SipClientActionFactory.buildSipClientAction End");
    return sipClientAction;
  }


  
  /**
   * get the Headers to be copied from the Message
   * @param message specifies the Message Element.
   * @return the List of copied Headers.
   */
  public static Vector<SipHeader> getHeaders(MessageElement message){
    if(message!=null){
      if (instance == null)
      {
        instance = new SipClientActionFactory();
      }
      Vector<SipHeader> headers = new Vector<SipHeader>();
      for (int i=0; i<instance.sipHeadersToCopy.size();i++){
        String headerName = instance.sipHeadersToCopy.get(i);
        HeaderElement headerElement = message.getHeader( headerName);
        if(headerElement!=null){
          SipHeader newHeader = new SipHeader(headerElement.getName(),headerElement.getCData());
          headers.add( newHeader);
        }
      }
      return headers;
    }
    return null;
  }
}
