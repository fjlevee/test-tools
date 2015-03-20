package com.fjl.sip.test;

import java.util.EventObject;
import java.util.Vector;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;

import org.apache.log4j.Logger;

import com.fjl.sip.CallControl;
import com.fjl.sip.SipConstants;
import com.fjl.sip.SipEventListener;
import com.fjl.sip.SipPhone;
import com.fjl.sip.call.SipClientStackException;
import com.fjl.sip.call.content.Content;
import com.fjl.sip.test.parser.elements.SipRequestElement;
import com.fjl.sip.utils.SipHeader;

public class SipClientUA implements SipEventListener
{
  /**
   * Log4j Logger
   */
  private Logger logger = Logger.getLogger( com.fjl.sip.test.SipClientUA.class);

  /**
   * Sip Phone
   */
  private SipPhone sipPhone;

  /**
   * CallControl
   */
  private CallControl callControl;

  /**
   * hashMap between Sip Client-Call-Id and Sip-Dialog-ID (call header)
   */
  private CallMapList callsMap;

  /**
   * SipClientUAListener
   */
  private SipClientUAListener listener;

  /**
   * Current CallId on Invite
   */
  private String currentCallID;

  /**
   * Last contact uri received on an Initial INVITE request or in a Initial INVITE final response. Today we use it only
   * to make a convergence between an INVITE session and a Subscribe session.
   */
  private String lastContactUriReceived;

  /**
   * This parameter check if the Client UA is used.
   */
  private boolean used = false;

  /**
   * Concurent HashMap to Stor Event that have not be notified to the SipClientUAListener
   */
  private Vector<SipClientUAEvent> sipUAEvents;

  /**
   * This parameter is helpful to get the Last Sip Call Id removed in this SipClient UA. This is helpful to get the Last
   * Sip Event received before a Sip Dialog has been closed. Example: a reception of an INVITE Error while the Sip Test
   * Client is not listening on event. When this Sip Test Client
   * 
   */
  private String lastSipCallIdRemoved;

  /**
   * Constructor
   * 
   * @param sipPhone
   */
  public SipClientUA ()
  {
    sipUAEvents = new Vector<SipClientUAEvent>();
    callsMap = new CallMapList();
  }

  /**
   * 
   * @param callId Specifies the Call ID on which the Event Has occured
   * @param method Specifes the Method. Can be a Sip Method (for Request or response method) or a TIME_OUT_EVENT
   * @param event Specifies the real Event.
   * 
   */
  public void notifyListener ( String callId, String method, EventObject event)
  {

    SipClientUAEvent sipUAEvent = new SipClientUAEvent( callId, method, event);
    if (logger.isTraceEnabled())
      logger.trace( "SipClientUA.notifyListener of Sip UA Event: " + sipUAEvent.getEventAsString());

    boolean handled = false;
    if (listener != null)
    {
      logger.debug( "FJL TEST 1.0");
      handled = listener.notifySipClientUAEvent( sipUAEvent);
      logger.debug( "FJL TEST 1.1 Handled?: " + handled);
      // now the listener is set to null if handled
      if (handled)
      {
        listener = null;
      }
    }
    if (!handled)
    {
      logger.debug( "FJL TEST 1.2 !Handled");

      addSipUAEvent( sipUAEvent);

      if (logger.isTraceEnabled()) logger.trace( "SipClientUA: Size of sipUAEvents List: " + sipUAEvents.size());
    }

  }

  public void addSipUAEvent ( SipClientUAEvent sipUAEvent)
  {
    synchronized (sipUAEvents)
    {
      String codeString = "";
      if (sipUAEvent.getEvent().getClass() == ResponseEvent.class)
      {
        codeString = " (" + ((ResponseEvent) sipUAEvent.getEvent()).getResponse().getStatusCode() + ")";
      }
      logger.warn( "SipClientUA.notifyListener: an Event: " + sipUAEvent.getMethod() + codeString + " on call id: "
          + sipUAEvent.getCallId() + " has not be handled by a SipClientUAListener => Store it in sipUAEvents List");
      sipUAEvents.add( sipUAEvent);
    }

  }

  /**
   * Allows the SipClientUAListener to check if the Event he waits has already been received.
   * 
   * @param clientCallId Specifies the Client Call ID
   * @param method Specifies the method of the Event awaited
   * @param eventClass Specifies the Event Class awaited (can be ResponseEvent.class or RequestEvent.class)
   * @param statusCode Specifies the Status Code in case of a Response awaited
   * @return the SipClientUAEvent
   */
  public SipClientUAEvent checkIfEventAlreadyReceived ( String scenarioSipCallId, String method, Class eventClass,
      int statusCode)
  {
    SipClientUAEvent event = null;
    // First determine the SIP Call ID
    String sipCallId = null;
    try
    {
      sipCallId = getSipCallIdWithScenarioSipCallId( scenarioSipCallId);
    }
    catch (SipClientStackException e)
    {
      // Nothing to do
      logger.warn( "SipClientUA.checkIfEventAlreadyReceived can not find a Sip Call Id with scenario Sip Call Id: "
          + scenarioSipCallId + ", the Sip Client UA will search with the Last Sip Call Id removed: "
          + lastSipCallIdRemoved);

      sipCallId = lastSipCallIdRemoved;
    }
    if (sipCallId != null)
    {
      if (logger.isTraceEnabled())
        logger.trace( "SipClientUA.checkIfEventAlreadyReceived: on sip call Id: " + sipCallId + ", method awaited: "
            + method + ", Event Class: " + eventClass.getName());

      synchronized (sipUAEvents)
      {

        for (int i = 0; i < sipUAEvents.size(); i++)
        {
          if (logger.isTraceEnabled()) logger.trace( "Check With Event: " + sipUAEvents.get( i).getEventAsString());
          if (sipUAEvents.get( i).checkEventParameters( sipCallId, method, eventClass, statusCode))
          {

            event = sipUAEvents.get( i);
            deleteEventsReceivedBeforeEventIndex( i);
            if (logger.isTraceEnabled()) logger.trace( "SipClientUA: Size of sipUAEvents List: " + sipUAEvents.size());
            return event;
          }
        }
      }
    }
    return event;
  }

  /**
   * Remove the Event received atr the given index all the event received before the given index, from the List of Event
   * received. <br>
   * Example:<br>
   * in a List of event: event1,event2,event3,event4,event5 => the call of method
   * deleteEventsReceivedBeforeEventIndex(event3) will result to the list: event4,event5
   * 
   * @param eventIndex Specifies the event index
   */
  private void deleteEventsReceivedBeforeEventIndex ( int eventIndex)
  {
    for (int i = eventIndex; i >= 0; i--)
    {
      sipUAEvents.remove( i);
    }
  }

  // SipEventListener interface implementation

  /**
   * SipDialog closed
   * 
   * @param sipCallId specifies the Call ID
   */
  public void sipDialogClosed ( String sipCallId)
  {
    this.lastSipCallIdRemoved = sipCallId;
    callsMap.removeWithSipCallID( sipCallId);
    if (logger.isTraceEnabled())
      logger.trace( "SipClientUA: Sip Dialog Closed: " + sipCallId + ", Call Map Size: " + callsMap.size());
  }

  /**
   * Handle an INVTE message
   * 
   * @param sipCallId specifies the Call ID
   * @param requestEvent specifies the INVITE request event
   */
  public void handleInvite ( String sipCallId, RequestEvent requestEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleInvite: Call ID: " + sipCallId);
    checkIfNewSipDialog( sipCallId,true);

    notifyListener( sipCallId, SipConstants.INVITE, requestEvent);

  }

  /**
   * handle a REINVITE message
   * 
   * @param sipCallId specifies the Call ID
   * @param requestEvent specifies the INVITE request event
   */
  public void handleReInvite ( String sipCallId, RequestEvent requestEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleReInvite: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.INVITE, requestEvent);
  }

  /**
   * Handles a CANCEL message
   * 
   * @param sipCallId specifies the Call ID
   * @param requestEvent specifies the CANCEL request event
   */
  public void handleCancel ( String sipCallId, RequestEvent requestEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleCancel: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.CANCEL, requestEvent);
  }

  /**
   * Handles a BYE message
   * 
   * @param sipCallId specifies the Call ID
   * @param requestEvent specifies the BYE request event
   */
  public void handleBye ( String sipCallId, RequestEvent requestEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleBye: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.BYE, requestEvent);
  }

  /**
   * Handles a Prack message
   * 
   * @param sipCallId specifies the Call ID
   * @param requestEvent specifies the PRACK request event
   */
  public void handlePrack ( String sipCallId, RequestEvent requestEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handlePrack: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.PRACK, requestEvent);
  }

  /**
   * Handles a ACK message
   * 
   * @param sipCallId specifies the Call ID
   * @param requestEvent specifies the ACK request event
   */
  public void handleAck ( String sipCallId, RequestEvent requestEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleAck: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.ACK, requestEvent);
  }

  /**
   * Handles a MESSAGE message
   * 
   * @param sipCallId specifies the Call ID
   * @param requestEvent specifies the MESSAGE request event
   */
  public void handleMessage ( String sipCallId, RequestEvent requestEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleMessage: Call ID: " + sipCallId);
    // Check if this message request creates a new Sip Dialog
    checkIfNewSipDialog( sipCallId,false);

    notifyListener( sipCallId, SipConstants.MESSAGE, requestEvent);
  }

  /**
   * Handles a OPTIONS message
   * 
   * @param sipCallId specifies the Call ID
   * @param requestEvent specifies the OPTION request event
   */
  public void handleOptions ( String sipCallId, RequestEvent requestEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleOptions: Call ID: " + sipCallId);
    // Check if this options request creates a new Sip Dialog
    checkIfNewSipDialog( sipCallId,false);
    notifyListener( sipCallId, SipConstants.OPTIONS, requestEvent);
  }

  /**
   * Handles a UPDATE message
   * 
   * @param sipCallId specifies the Call ID
   * @param requestEvent specifies the UPDATE request event
   */
  public void handleUpdate ( String sipCallId, RequestEvent requestEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleUpdate: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.UPDATE, requestEvent);
  }

  /**
   * Handles a NOTIFY message
   * 
   * @param sipCallId specifies the Call ID
   * @param requestEvent specifies the NOTIFY request event
   */
  public void handleNotify ( String sipCallId, RequestEvent requestEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleNotify: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.NOTIFY, requestEvent);
  }

  /**
   * Handles a INFO message
   * 
   * @param sipCallId specifies the Call ID
   * @param requestEvent specifies the INFO request event
   */
  public void handleInfo ( String sipCallId, RequestEvent requestEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleInfo: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.INFO, requestEvent);
  }

  /**
   * Handles a REFER message
   * 
   * @param sipCallId specifies the Call ID
   * @param requestEvent specifies the REFER request event
   */
  public void handleRefer ( String sipCallId, RequestEvent requestEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleRefer: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.REFER, requestEvent);
  }
  
  /**
   * Handles a REGISTER message
   * 
   * @param sipCallId specifies the Call ID
   * @param requestEvent specifies the REGISTER request event
   */
  public void handleRegister ( String sipCallId, RequestEvent requestEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleRegister: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.REGISTER, requestEvent);
  }

  /**
   * handle a INVITE success response
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Success Response Evente
   */
  public void handleInviteSuccessResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleInviteSuccessResponse: Call ID: " + sipCallId);
    try
    {
      String lastContactUriReceived = callControl.getContactHeaderReceived( sipCallId).getHeaderValue();
      if (lastContactUriReceived != null)
      {
        setLastContactUriReceived( callControl.getContactHeaderReceived( sipCallId).getHeaderValue());
      }
    }
    catch (SipClientStackException e)
    {
      logger
          .warn( "SipClientUA.handleInviteSuccessResponse SipClientStackException when calling callControl.getContactHeaderReceived method\n"
              + e.getMessage());
    }

    notifyListener( sipCallId, SipConstants.INVITE, responseEvent);
  }

  /**
   * handle a INVITE success response
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Success Response Event
   */
  public void handleInviteErrorResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleInviteErrorResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.INVITE, responseEvent);
  }

  /**
   * handle a INVITE provisional response
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Provisional Response Event
   */
  public void handleInviteProvisionalResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleInviteProvisionalResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.INVITE, responseEvent);
  }

  /**
   * handle a INVITE redirect response
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Redirect Response Event
   */
  public void handleInviteRedirectResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleInviteRedirectResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.INVITE, responseEvent);
  }

  /**
   * Handles a CANCEL message
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Success Response Event
   */
  public void handleCancelSuccessResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleCancelSuccessResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.CANCEL, responseEvent);
  }

  /**
   * Handles a CANCEL message
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Error Response Event
   */
  public void handleCancelErrorResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleCancelErrorResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.CANCEL, responseEvent);
  }

  /**
   * Handles a BYE message
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Success Response Event
   */
  public void handleByeSuccessResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleByeSuccessResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.BYE, responseEvent);
  }

  /**
   * Handles a BYE message
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Success Response Event
   */
  public void handleByeErrorResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleByeErrorResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.BYE, responseEvent);
  }

  /**
   * Handles a Prack message
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Success Response Event
   */
  public void handlePrackSuccessResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handlePrackSuccessResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.PRACK, responseEvent);
  }

  /**
   * Handles a Prack message
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Success Response Event
   */
  public void handlePrackErrorResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handlePrackErrorResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.PRACK, responseEvent);
  }

  /**
   * Handles a MESSAGE message
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Success Response Event
   */
  public void handleMessageSuccessResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleMessageSuccessResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.MESSAGE, responseEvent);
  }

  /**
   * Handles a MESSAGE message
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Success Response Event
   */
  public void handleMessageErrorResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleMessageErrorResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.MESSAGE, responseEvent);
  }

  /**
   * Handles a OPTIONS message
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Success Response Event
   */
  public void handleOptionsSuccessResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleOptionsSuccessResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.OPTIONS, responseEvent);
  }

  /**
   * Handles a OPTIONS message
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Error Response Event
   */
  public void handleOptionsErrorResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleOptionsErrorResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.OPTIONS, responseEvent);
  }

  /**
   * Handles a UPDATE message
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Success Response Event
   */
  public void handleUpdateSuccessResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleUpdateSuccessResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.UPDATE, responseEvent);
  }

  /**
   * Handles a UPDATE message
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Errot Response Event
   */
  public void handleUpdateErrorResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleUpdateErrorResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.UPDATE, responseEvent);
  }

  /**
   * Handles a NOTIFY message
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Success Response Event
   */
  public void handleNotifySuccessResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleNotifySuccessResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.NOTIFY, responseEvent);
  }

  /**
   * Handles a NOTIFY message
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Error Response Event
   */
  public void handleNotifyErrorResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleNotifyErrorResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.NOTIFY, responseEvent);
  }

  /**
   * Handles a INFO message
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Success Response Event
   */
  public void handleInfoSuccessResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleInfoSuccessResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.INFO, responseEvent);
  }

  /**
   * Handles a INFO message
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Error Response Event
   */
  public void handleInfoErrorResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleInfoErrorResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.INFO, responseEvent);
  }

  /**
   * Handles a SUBSCRIBE message success response
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Success Response Event
   */
  public void handleSubscribeSuccessResponse ( String sipCallId, ResponseEvent responseEvent)
  {

    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleSubscribeSuccessResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.SUBSCRIBE, responseEvent);
  }

  /**
   * Handles a SUBSCRIBE message error response
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Error Response Event
   */
  public void handleSubscribeErrorResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleSubscribeErrorResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.SUBSCRIBE, responseEvent);

  }

  /**
   * Handles a REFER message
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Success Response Event
   */
  public void handleReferSuccessResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleReferSuccessResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.REFER, responseEvent);
  }

  /**
   * Handles a REFER message
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Error Response Event
   */
  public void handleReferErrorResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleReferErrorResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.REFER, responseEvent);
  }
  
  /**
   * Handles a REGISTER success response message
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Success Response Event
   */
  public void handleRegisterSuccessResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleRegisterSuccessResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.REGISTER, responseEvent);
  }

  /**
   * Handles a REGISTER error response message
   * 
   * @param sipCallId specifies the Call ID
   * @param responseEvent specifies the Error Response Event
   */
  public void handleRegisterErrorResponse ( String sipCallId, ResponseEvent responseEvent)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.handleRegisterErrorResponse: Call ID: " + sipCallId);
    notifyListener( sipCallId, SipConstants.REGISTER, responseEvent);
  }

  /**
   * Get the SipPhone used by this SipCLientUA
   * 
   * @return the SipPhone used by this SipCLientUA
   */
  public SipPhone getSipPhone ()
  {
    return sipPhone;
  }

  /**
   * set the SipPhone used by this SipClient UA
   * 
   * @param sipPhone specifies the SipPhone used by this SipClient UA
   */
  public void setSipPhone ( SipPhone sipPhone)
  {
    this.sipPhone = sipPhone;
    this.callControl = sipPhone.getCallControl();
  }

  // Call Control methods calls
  /**
   * Create a New Call by Sending a new INVITE mesage
   * 
   * @param scenarioSipCallId specifies the Client Call ID
   * @param toSipUri specifies the To Sip URI
   * @param content specifies the Content to add in the INVITE.
   * @param headers specifies the headers to be added in the INVITE.
   * @throws SipClientStackException In case of error.
   */
  public void sendInvite ( String scenarioSipCallId, String toSipUri, Content content, Vector<SipHeader> headers,boolean authenticated)
      throws SipClientStackException
  {
    try
    {
      if (logger.isTraceEnabled()) logger.trace( "SipClientUA.sendInvite to :" + toSipUri);
      // Check if the INVITE is a RE-INVITE => It means that the scenarioSipCallId has already been mapped

      if (callsMap.get( scenarioSipCallId) != null)
      {
        if (logger.isTraceEnabled()) logger.trace( "SipClientUA.sendInvite is in fact a RE-INVITE");

        callControl.sendReInvite( callsMap.get( scenarioSipCallId), content, headers);

      }
      else
      {
        String callId = callControl.sendInvite( toSipUri, content, headers,authenticated);
        callsMap.put( scenarioSipCallId, callId);
      }

    }
    catch (SipClientStackException e)
    {
      logger.error( "SipClientUA.sendInvite SipClientStackException: " + e.getMessage());
      logger.error( e);
      throw new SipClientStackException( "SipClientUA.sendInvite error: " + e.getMessage());

    }
    catch (Throwable e)
    {
      logger.error( "SipClientUA.sendInvite Throwable: " + e.getMessage());
      logger.error( e);
      throw new SipClientStackException( "SipClientUA.sendInvite error: " + e.getMessage());
    }
  }

  /**
   * Create a New Call by Sending a new INVITE mesage
   * 
   * @param scenarioSipCallId specifies the Client Call ID
   * @param headers specifies the headers to be added in the INVITE.
   * @throws SipClientStackException In case of error.
   */
  public void sendBye ( String scenarioSipCallId, Vector<SipHeader> headers) throws SipClientStackException
  {
    try
    {
      if (logger.isTraceEnabled()) logger.trace( "SipClientUA.sendBye");
      String callId = callsMap.get( scenarioSipCallId);
      if (callId == null)
      {
        throw new SipClientStackException(
            "SipClientUA.sendBye error: Can not find a SipClient Call with client Call Id: " + scenarioSipCallId);
      }
      callControl.sendBye( callId, headers);
    }
    catch (SipClientStackException e)
    {
      logger.error( "SipClientUA.sendBye SipClientStackException: " + e.getMessage());
      throw new SipClientStackException( "SipClientUA.sendBye error: " + e.getMessage());

    }
    catch (Throwable e)
    {
      logger.error( "SipClientUA.sendBye Throwable: " + e.getMessage());
      throw new SipClientStackException( "SipClientUA.sendBye error: " + e.getMessage());
    }
  }

  /**
   * Send a Message message
   * 
   * @param scenarioSipCallId specifies the Client Call ID
   * @param toSipUri specifies the To Sip URI. Helpful only when creating a intial Message Request
   * @param content specifies the Content to add in the MESSAGE.
   * @param headers specifies the headers to be added in the MESSAGE.
   * @return the Call-ID created
   * @throws SipClientStackException In case of error.
   */
  public void sendMessage ( String scenarioSipCallId, String toSipUri, Content content, Vector<SipHeader> headers,boolean authenticated)
      throws SipClientStackException
  {
    try
    {
      if (logger.isTraceEnabled()) logger.trace( "SipClientUA.sendMessage");

      if (callsMap.get( scenarioSipCallId) != null)
      {
        if (logger.isTraceEnabled()) logger.trace( "SipClientUA.sendMessage in a SIP INVITE DIALOG");

        callControl.sendMessage( callsMap.get( scenarioSipCallId), content, headers);

      }
      else
      {
        String callId = callControl.sendInitialMessage( toSipUri, content, headers,authenticated);
        callsMap.put( scenarioSipCallId, callId);
      }

    }
    catch (SipClientStackException e)
    {
      logger.error( "SipClientUA.sendMessage SipClientStackException: " + e.getMessage());
      throw new SipClientStackException( "SipClientUA.sendMessage error: " + e.getMessage());

    }
    catch (Throwable e)
    {
      logger.error( "SipClientUA.sendMessage Throwable: " + e.getMessage());
      throw new SipClientStackException( "SipClientUA.sendMessage error: " + e.getMessage());
    }
  }

  /**
   * Send a Options message
   * 
   * @param scenarioSipCallId specifies the Client Call ID
   * @param toSipUri specifies the To Sip URI. Helpful only when creating a intial Message Request
   * @param content specifies the Content to add in the OPTIONS.
   * @param headers specifies the headers to be added in the OPTIONS.
   * @throws SipClientStackException In case of error.
   */
  public void sendOptions ( String scenarioSipCallId, String toSipUri, Content content, Vector<SipHeader> headers,boolean authenticated)
      throws SipClientStackException
  {
    try
    {
      if (logger.isTraceEnabled()) logger.trace( "SipClientUA.sendOptions");

      if (callsMap.get( scenarioSipCallId) != null)
      {
        if (logger.isTraceEnabled()) logger.trace( "SipClientUA.sendOptions in a SIP INVITE DIALOG");

        callControl.sendOptions( callsMap.get( scenarioSipCallId), content, headers);

      }
      else
      {
        String callId = callControl.sendInitialOptions( toSipUri, content, headers,authenticated);
        callsMap.put( scenarioSipCallId, callId);
      }

    }
    catch (SipClientStackException e)
    {
      logger.error( "SipClientUA.sendOptions SipClientStackException: " + e.getMessage());
      throw new SipClientStackException( "SipClientUA.sendOptions error: " + e.getMessage());

    }
    catch (Throwable e)
    {
      logger.error( "SipClientUA.sendOptions Throwable: " + e.getMessage());
      throw new SipClientStackException( "SipClientUA.sendOptions error: " + e.getMessage());
    }
  }

  /**
   * Send a Update message
   * 
   * @param scenarioSipCallId specifies the Client Call ID
   * @param content specifies the Content to add in the UPDATE.
   * @param headers specifies the headers to be added in the UPDATE.
   * @throws SipClientStackException In case of error.
   */
  public void sendUpdate ( String scenarioSipCallId, Content content, Vector<SipHeader> headers)
      throws SipClientStackException
  {
    try
    {
      if (logger.isTraceEnabled()) logger.trace( "SipClientUA.sendUpdate");
      String callId = callsMap.get( scenarioSipCallId);
      if (callId == null)
      {
        throw new SipClientStackException(
            "SipClientUA.sendUpdate error: Can not find a SipClient Call with client Call Id: " + scenarioSipCallId);
      }
      callControl.sendUpdate( callId, content, headers);
    }
    catch (SipClientStackException e)
    {
      logger.error( "SipClientUA.sendUpdate SipClientStackException: " + e.getMessage());
      throw new SipClientStackException( "SipClientUA.sendUpdate error: " + e.getMessage());

    }
    catch (Throwable e)
    {
      logger.error( "SipClientUA.sendUpdate Throwable: " + e.getMessage());
      throw new SipClientStackException( "SipClientUA.sendUpdate error: " + e.getMessage());
    }
  }

  /**
   * Send a Notify message
   * 
   * @param scenarioSipCallId specifies the Client Call ID
   * @param content specifies the Content to add in the NOTIFY.
   * @param headers specifies the headers to be added in the NOTIFY.
   * @throws SipClientStackException In case of error.
   */
  public void sendNotify ( String scenarioSipCallId, Content content, Vector<SipHeader> headers)
      throws SipClientStackException
  {
    try
    {
      if (logger.isTraceEnabled()) logger.trace( "SipClientUA.sendNotify");
      String callId = callsMap.get( scenarioSipCallId);
      if (callId == null)
      {
        throw new SipClientStackException(
            "SipClientUA.sendNotify error: Can not find a SipClient Call with client Call Id: " + scenarioSipCallId);
      }
      callControl.sendNotify( callId, content, headers);
    }
    catch (SipClientStackException e)
    {
      logger.error( "SipClientUA.sendNotify SipClientStackException: " + e.getMessage());
      throw new SipClientStackException( "sendNotify.sendNotify error: " + e.getMessage());

    }
    catch (Throwable e)
    {
      logger.error( "SipClientUA.sendNotify Throwable: " + e.getMessage());
      throw new SipClientStackException( "SipClientUA.sendNotify error: " + e.getMessage());
    }
  }

  /**
   * Send a Subscribe message
   * 
   * @param scenarioSipCallId specifies the Client Call ID
   * @param toSipUri specifies the To Sip URI. Helpful only when creating a initial Message Request
   * @param content specifies the Content to add in the SUBSCRIBE.
   * @param headers specifies the headers to be added in the SUBSCRIBE.
   * @throws SipClientStackException In case of error.
   */
  public void sendSubscribe ( String scenarioSipCallId, String toSipUri, Content content, Vector<SipHeader> headers,boolean authenticated)
      throws SipClientStackException
  {
    try
    {
      if (logger.isTraceEnabled()) logger.trace( "SipClientUA.sendSubscribe");

      if (callsMap.get( scenarioSipCallId) != null)
      {
        if (logger.isTraceEnabled()) logger.trace( "SipClientUA.sendSubscribe in a SIP INVITE DIALOG");

        callControl.sendSubscribe( callsMap.get( scenarioSipCallId), content, headers);

      }
      else
      {
        if (logger.isTraceEnabled()) logger.trace( "SipClientUA.sendSubscribe => sendInitialSubscribe");
        String callId = callControl.sendInitialSubscribe( toSipUri, content, headers,authenticated);
        callsMap.put( scenarioSipCallId, callId);
      }

    }
    catch (SipClientStackException e)
    {
      logger.error( "SipClientUA.sendSubscribe SipClientStackException: " + e.getMessage());
      throw new SipClientStackException( "SipClientUA.sendSubscribe error: " + e.getMessage());

    }
    catch (Throwable e)
    {
      logger.error( "SipClientUA.sendSubscribe Throwable: " + e.getMessage());
      throw new SipClientStackException( "SipClientUA.sendSubscribe error: " + e.getMessage());
    }
  }

  /**
   * Send a Prack message
   * 
   * @param scenarioSipCallId specifies the Client Call ID
   * @param content specifies the Content to add in the PRACK.
   * @param headers specifies the headers to be added in the PRACK.
   * @throws SipClientStackException In case of error.
   */
  public void sendPrack ( String scenarioSipCallId, Content content, Vector<SipHeader> headers)
      throws SipClientStackException
  {
    try
    {
      if (logger.isTraceEnabled()) logger.trace( "SipClientUA.sendPrack");
      String callId = callsMap.get( scenarioSipCallId);
      if (callId == null)
      {
        throw new SipClientStackException(
            "SipClientUA.sendPrack error: Can not find a SipClient Call with client Call Id: " + scenarioSipCallId);
      }
      callControl.sendPrack( callId, content, headers);
    }
    catch (SipClientStackException e)
    {
      logger.error( "SipClientUA.sendPrack SipClientStackException: " + e.getMessage());
      throw new SipClientStackException( "sendNotify.sendPrack error: " + e.getMessage());

    }
    catch (Throwable e)
    {
      logger.error( "SipClientUA.sendPrack Throwable: " + e.getMessage());
      throw new SipClientStackException( "SipClientUA.sendPrack error: " + e.getMessage());
    }
  }

  /**
   * Send a Ack message
   * 
   * @param scenarioSipCallId specifies the Client Call ID
   * @param content specifies the Content to add in the ACK.
   * @param headers specifies the headers to be added in the ACK.
   * @throws SipClientStackException In case of error.
   */
  public void sendAck ( String scenarioSipCallId, Content content, Vector<SipHeader> headers)
      throws SipClientStackException
  {
    try
    {
      if (logger.isTraceEnabled()) logger.trace( "SipClientUA.sendAck");
      String callId = callsMap.get( scenarioSipCallId);
      if (callId == null)
      {
        throw new SipClientStackException(
            "SipClientUA.sendAck error: Can not find a SipClient Call with client Call Id: " + scenarioSipCallId);
      }
      callControl.sendAck( callId, content, headers);
    }
    catch (SipClientStackException e)
    {
      logger.error( "SipClientUA.sendAck SipClientStackException: " + e.getMessage());
      throw new SipClientStackException( "SipClientUA.sendAck error: " + e.getMessage());

    }
    catch (Throwable e)
    {
      logger.error( "SipClientUA.sendAck Throwable: " + e.getMessage());
      throw new SipClientStackException( "SipClientUA.sendAck error: " + e.getMessage());
    }
  }

  /**
   * Send a Cancel message
   * 
   * @param scenarioSipCallId specifies the Client Call ID
   * @param headers specifies the headers to be added in the CANCEL.
   * @throws SipClientStackException In case of error.
   */
  public void sendCancel ( String scenarioSipCallId, Vector<SipHeader> headers) throws SipClientStackException
  {
    try
    {
      if (logger.isTraceEnabled()) logger.trace( "SipClientUA.sendCancel");
      String callId = callsMap.get( scenarioSipCallId);
      if (callId == null)
      {
        throw new SipClientStackException(
            "SipClientUA.sendCancel error: Can not find a SipClient Call with client Call Id: " + scenarioSipCallId);
      }
      callControl.sendCancel( callId, headers);
    }
    catch (SipClientStackException e)
    {
      logger.error( "SipClientUA.sendCancel SipClientStackException: " + e.getMessage());
      throw new SipClientStackException( "SipClientUA.sendCancel error: " + e.getMessage());

    }
    catch (Throwable e)
    {
      logger.error( "SipClientUA.sendCancel Throwable: " + e.getMessage());
      throw new SipClientStackException( "SipClientUA.sendCancel error: " + e.getMessage());
    }
  }

  /**
   * Send a Info message
   * 
   * @param scenarioSipCallId specifies the Client Call ID
   * @param content specifies the Content to add in the INFO.
   * @param headers specifies the headers to be added in the INFO.
   * @throws SipClientStackException In case of error.
   */
  public void sendInfo ( String scenarioSipCallId, Content content, Vector<SipHeader> headers)
      throws SipClientStackException
  {
    try
    {
      if (logger.isTraceEnabled()) logger.trace( "SipClientUA.sendInfo");
      String callId = callsMap.get( scenarioSipCallId);
      if (callId == null)
      {
        throw new SipClientStackException(
            "SipClientUA.sendInfo error: Can not find a SipClient Call with client Call Id: " + scenarioSipCallId);
      }
      callControl.sendInfo( callId, content, headers);
    }
    catch (SipClientStackException e)
    {
      logger.error( "SipClientUA.sendInfo SipClientStackException: " + e.getMessage());
      throw new SipClientStackException( "SipClientUA.sendInfo error: " + e.getMessage());

    }
    catch (Throwable e)
    {
      logger.error( "SipClientUA.sendInfo Throwable: " + e.getMessage());
      throw new SipClientStackException( "SipClientUA.sendInfo error: " + e.getMessage());
    }
  }

  /**
   * Send a Refer message
   * 
   * @param scenarioSipCallId specifies the Client Call ID
   * @param content specifies the Content to add in the REFER.
   * @param headers specifies the headers to be added in the REFER.
   * @throws SipClientStackException In case of error.
   */
  public void sendRefer ( String scenarioSipCallId, Content content, Vector<SipHeader> headers)
      throws SipClientStackException
  {
    try
    {
      if (logger.isTraceEnabled()) logger.trace( "SipClientUA.sendRefer");
      String callId = callsMap.get( scenarioSipCallId);
      if (callId == null)
      {
        throw new SipClientStackException(
            "SipClientUA.sendRefer error: Can not find a SipClient Call with client Call Id: " + scenarioSipCallId);
      }
      callControl.sendRefer( callId, content, headers);
    }
    catch (SipClientStackException e)
    {
      logger.error( "SipClientUA.sendRefer SipClientStackException: " + e.getMessage());
      throw new SipClientStackException( "SipClientUA.sendRefer error: " + e.getMessage());

    }
    catch (Throwable e)
    {
      logger.error( "SipClientUA.sendRefer Throwable: " + e.getMessage());
      throw new SipClientStackException( "SipClientUA.sendRefer error: " + e.getMessage());
    }
  }

  /**
   * Send a Response
   * 
   * @param scenarioSipCallId Specifies the Client Call ID
   * @param method Specifies the SIP Method of the response
   * @param statusCode Specifies the status code of the response
   * @param content Specifies the Content to add in the INVITE response.
   * @param headers Specifies the headers to be added in the INVITE response.
   * @throws SipClientStackException In case of error.
   */
  public void sendResponse ( String scenarioSipCallId, String method, int statusCode, Content content,
      Vector<SipHeader> headers) throws SipClientStackException
  {
    try
    {
      if (logger.isTraceEnabled())
        logger.trace( "SipClientUA.sendResponse: method: " + method + ", Status code:" + statusCode);
      String callId = callsMap.get( scenarioSipCallId);
      if (callId == null)
      {

        if (currentCallID != null)
        {
          // Check if the Response to send is on an Incoming Initial Invite

          callId = callsMap.get( currentCallID);
          if (callId != null)
          {
            // Change the key to the scenarioSipCallId
            callsMap.remove( currentCallID);
            callsMap.put( scenarioSipCallId, callId);
          }
          else
          {
            throw new SipClientStackException(
                "SipClientUA.sendResponse error: Can not find a SipClient Call with client Call Id: "
                    + scenarioSipCallId);
          }
        }
        else
        {
          throw new SipClientStackException(
              "SipClientUA.sendResponse error: Can not find a SipClient Call with client Call Id: " + scenarioSipCallId);
        }
      }

      if ((statusCode > 100) && (statusCode < 200))
      {

        if (method.equals( SipConstants.INVITE))
        {
          callControl.sendInviteProvisionalResponse( callId, statusCode, content, headers);
        }
        else
        {
          throw new SipClientStackException(
              "SipClientUA.sendResponse: error can not sent provisional response for method: " + method);
        }
      }
      else if ((statusCode >= 200) && (statusCode < 300))
      {

        if (method.equals( SipConstants.INVITE))
        {
          callControl.sendInviteSuccessResponse( callId, statusCode, content, headers);
        }
        else if (method.equals( SipConstants.BYE))
        {
          callControl.sendByeSuccessResponse( callId, statusCode, headers);
        }
        else if (method.equals( SipConstants.MESSAGE))
        {
          callControl.sendMessageSuccessResponse( callId, statusCode, content, headers);
        }
        else if (method.equals( SipConstants.OPTIONS))
        {
          callControl.sendOptionsSuccessResponse( callId, statusCode, content, headers);
        }
        else if (method.equals( SipConstants.UPDATE))
        {
          callControl.sendUpdateSuccessResponse( callId, statusCode, content, headers);
        }
        else if (method.equals( SipConstants.NOTIFY))
        {
          callControl.sendNotifySuccessResponse( callId, statusCode, content, headers);
        }
        else if (method.equals( SipConstants.PRACK))
        {
          callControl.sendPrackSuccessResponse( callId, statusCode, content, headers);
        }
        else if (method.equals( SipConstants.CANCEL))
        {
          callControl.sendCancelSuccessResponse( callId, statusCode, headers);
        }
        else if (method.equals( SipConstants.INFO))
        {
          callControl.sendInfoSuccessResponse( callId, statusCode, content, headers);
        }
        else if (method.equals( SipConstants.REFER))
        {
          callControl.sendReferSuccessResponse( callId, statusCode, content, headers);
        }
        else
        {
          throw new SipClientStackException( "SipClientUA.sendResponset : error undefined method: " + method);
        }
      }
      else if ((statusCode >= 300) && (statusCode < 400))
      {

        if (!method.equals( SipConstants.INVITE))
        {
          throw new SipClientStackException(
              "SipClientUA.sendResponse : error can not sent redirect response for method: " + method);
        }
        callControl.sendInviteRedirectedResponse( callId, statusCode, content, headers);
      }
      else if (statusCode > 400)
      {

        if (method.equals( SipConstants.INVITE))
        {
          callControl.sendInviteErrorResponse( callId, statusCode, headers);
        }
        else if (method.equals( SipConstants.BYE))
        {
          callControl.sendByeErrorResponse( callId, statusCode, headers);
        }
        else if (method.equals( SipConstants.MESSAGE))
        {
          callControl.sendMessageErrorResponse( callId, statusCode, headers);
        }
        else if (method.equals( SipConstants.OPTIONS))
        {
          callControl.sendOptionsErrorResponse( callId, statusCode, headers);
        }
        else if (method.equals( SipConstants.UPDATE))
        {
          callControl.sendUpdateErrorResponse( callId, statusCode, headers);
        }
        else if (method.equals( SipConstants.NOTIFY))
        {
          callControl.sendNotifyErrorResponse( callId, statusCode, headers);
        }
        else if (method.equals( SipConstants.PRACK))
        {
          callControl.sendPrackErrorResponse( callId, statusCode, headers);
        }
        else if (method.equals( SipConstants.CANCEL))
        {
          callControl.sendCancelErrorResponse( callId, statusCode, headers);
        }
        else if (method.equals( SipConstants.INFO))
        {
          callControl.sendInfoErrorResponse( callId, statusCode, headers);
        }
        else if (method.equals( SipConstants.REFER))
        {
          callControl.sendReferErrorResponse( callId, statusCode, headers);
        }
        else
        {
          throw new SipClientStackException( "SipClientUA.sendResponse : error undefined method: " + method);
        }
      }

    }
    catch (SipClientStackException e)
    {
      logger.error( "SipClientUA.sendResponse SipClientStackException: " + e.getMessage());
      throw new SipClientStackException( "SipClientUA.sendResponse error: " + e.getMessage());

    }
    catch (Throwable e)
    {
      logger.error( "SipClientUA.sendResponse Throwable: " + e.getMessage());
      throw new SipClientStackException( "SipClientUA.sendResponse error: " + e.getMessage());
    }
  }

  /**
   * Get the CallControl
   * 
   * @return
   */
  public CallControl getCallControl ()
  {
    return callControl;
  }

  public SipClientUAListener getListener ()
  {
    return listener;
  }

  /**
   * Set the Sip Client UA Listener
   * 
   * @param listener Specifies the Sip Client UA Listener
   */
  public void setListener ( SipClientUAListener listener)
  {
    this.listener = listener;
  }

  /**
   * Get the Current call ID. Helpful for incoming Invite => the Current call ID will be the Key To get the Call-Id on
   * Incoming Invites message.
   * 
   * @return the Current call ID.
   */
  public String getCurrentCallID ()
  {
    return currentCallID;
  }

  /**
   * Get the Sip Call Id (Header Call-Id value) giving Scenario Sip Call Id of the Sip Client
   * 
   * @param scenarioSipCallId Specifies the scenario Sip Call Id.
   * @return the Sip Call Id (Header Call-Id value) giving Call If of the Sip Client
   * @throws SipClientStackException
   */
  public String getSipCallIdWithScenarioSipCallId ( String scenarioSipCallId) throws SipClientStackException
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.getSipCallIdWithScenarioSipCallId: " + scenarioSipCallId);
    String callId = null;
    if (scenarioSipCallId != null)
    {

      callId = callsMap.get( scenarioSipCallId);

      if (callId == null)
      {
        if (currentCallID != null)
        {
          // Check if the Response to send is on an Incoming Initial Invite

          callId = callsMap.get( currentCallID);
          if (callId != null)
          {
            // Change the key to the scenarioSipCallId
            callsMap.remove( currentCallID);
            callsMap.put( scenarioSipCallId, callId);
          }
          else
          {
            throw new SipClientStackException(
                "SipClientUA.getSipCallIdWithScenarioSipCallId error: Can not find a SipClient Call with scenario sip Call Id: "
                    + scenarioSipCallId);
          }
        }
        else
        {
          throw new SipClientStackException(
              "SipClientUA.getSipCallIdWithScenarioSipCallId error: Can not find a SipClient Call with scenario sip Call Id: "
                  + scenarioSipCallId);
        }
      }

    }
    return callId;
  }

  /**
   * Check if the Sip Call-ID has been already mapped or not
   * 
   * @param sipCallId Specifies the Sip Call ID
   */
  private void checkIfNewSipDialog ( String sipCallId, boolean isInviteMessage)
  {
    if (logger.isTraceEnabled()) logger.trace( "SipClientUA.checkIfNewSipDialog: Call ID: " + sipCallId);
    if (callsMap.get( sipCallId) == null)
    {
      if (logger.isTraceEnabled())
        logger.trace( "SipClientUA.checkIfNewSipDialog: Call ID: " + sipCallId + " is a new Sip Dialog");
      this.currentCallID = sipCallId;
      if (isInviteMessage)
      {
        try
        {
          String lastContactUriReceived = callControl.getContactHeaderReceived( sipCallId).getHeaderValue();
          if (lastContactUriReceived != null)
          {
            setLastContactUriReceived( callControl.getContactHeaderReceived( sipCallId).getHeaderValue());
          }
        }
        catch (SipClientStackException e)
        {
          logger
              .warn( "SipClientUA.checkIfNewSipDialog SipClientStackException when calling callControl.getContactHeaderReceived method\n"
                  + e.getMessage());
        }
      }
      callsMap.put( this.currentCallID, sipCallId);
      logger.debug( "FJL TEST 100.0");
    }

  }

  /**
   * Set the Sip Client UA Listener
   * 
   * @param listener Specifies the Sip Client UA Listener
   */
  public void removeListener ()
  {
    this.listener = null;
  }

  /**
   * Checks if this client UA is Used
   * 
   * @return true f this client UA is Used
   */
  public boolean isUsed ()
  {
    return used;
  }

  /**
   * Set if this client UA is Used
   * 
   * @param used true if used
   */
  public void setUsed ( boolean used)
  {
    this.used = used;
    if (used == false)
    {
      if (logger.isTraceEnabled())
        logger.trace( "SipClientUA.setUsed:SipClientUA: " + sipPhone.getSipProfile().getProfileName()
            + " has been released by a SipClientTest");
      // Clear all
      removeListener();
      callsMap.clear();
      currentCallID = null;
      sipUAEvents.clear();
      lastContactUriReceived = null;
    }
    else
    {
      if (logger.isTraceEnabled())
        logger.trace( "SipClientUA.setUsed:SipClientUA: " + sipPhone.getSipProfile().getProfileName()
            + " is now used by a SipClientTest");
    }
  }

  /**
   * Get the Last contact uri received on an Initial INVITE request or in a Initial INVITE final response. Today we use
   * it only to make a convergence between an INVITE session and a Subscribe session.
   * 
   * @return the Last contact uri received on an Initial INVITE request or in a Initial INVITE final response.
   */
  public String getLastContactUriReceived ()
  {
    return lastContactUriReceived;
  }

  /**
   * Set the Last contact uri received on an Initial INVITE request or in a Initial INVITE final response. Today we use
   * it only to make a convergence between an INVITE session and a Subscribe session.
   * 
   * @param lastContactUriReceived the Last contact uri received on an Initial INVITE request or in a Initial INVITE
   *          final response.
   */
  public void setLastContactUriReceived ( String lastContactUriReceived)
  {
    this.lastContactUriReceived = lastContactUriReceived;
  }
}
