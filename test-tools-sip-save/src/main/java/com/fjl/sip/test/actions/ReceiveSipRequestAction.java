package com.fjl.sip.test.actions;

import javax.sip.RequestEvent;

import com.fjl.sip.SipConstants;
import com.fjl.sip.test.SipClientActor;
import com.fjl.sip.test.SipClientUA;
import com.fjl.sip.test.SipClientUAEvent;
import com.fjl.sip.test.parser.elements.SipRequestElement;

import fj_levee.test.report.SipClientTestReportEvent;
import fj_levee.test.report.SipClientTestReportEvent.ReportEventType;


public class ReceiveSipRequestAction extends SipClientAction
{
  /**
   * Method Awaited
   */
  private String method;

  /**
   * 
   * Constructor of ReceiveSipRequestAction.
   * 
   * @param sipClientActor
   * @param requestElement
   * @param timeToWait
   */
  public ReceiveSipRequestAction ( SipClientActor sipClientActor, SipRequestElement requestElement, long timeToWait)
  {
    super( sipClientActor, requestElement.getCallid(), requestElement.getSipTestActionId(), timeToWait);
    this.method = requestElement.getMethod();

  }

  /**
   * 
   * @see com.fjl.sip.test.actions.SipClientAction#executeAction()
   */
  @Override
  public void executeAction ()
  {

    try
    {
      if (logger.isTraceEnabled())
        logger.debug( "ReceiveSipRequestAction.executeAction: Sip ClientActor: " + sipClientActor.getClientActorId()
            + ", Action Id: " + actionId + ",Max Time To Wait: " + timeToWait+", SIP Request Method: "+method);
      // Check if the request awaited is an ACK on an error message
      if ((SipConstants.ACK.equals( method)) && (previousMessageWasAnError))
      {
        // Yes the Sip Layer will never notify the Sip UA of the ACK => No need to wait for.
        notifySipClientTest( new SipClientTestReportEvent( ReportEventType.ACTION_SUCCESS, actionId, sipClientActor
            .getClientActorId(), isLastAction(), "ReceiveSipRequestAction " + method,"OK"));
        return;
      }
      SipClientUAEvent eventReceived = null;
      WaitForEventAction waitAction = new WaitForEventAction(sipClientActor.getSipClientUA());
      
      if (logger.isTraceEnabled())
        logger.debug( "ReceiveSipRequestAction.executeAction: Sip ClientActor: " + sipClientActor.getClientActorId()
            + ", Action Id: " + actionId + ", Start...");

      waitAction.waitForEvent( timeToWait, RequestEvent.class, method, scenarioSipCallId,-1);

      if (logger.isTraceEnabled())
        logger.debug( "ReceiveSipRequestAction.executeAction: Sip ClientActor: " + sipClientActor.getClientActorId()
            + ", Action Id: " + actionId + ", waitAction ended.");
      eventReceived = waitAction.getSipClientUAEvent();

      if (eventReceived != null)
      {
        if (logger.isTraceEnabled())
          logger.debug( "ReceiveSipRequestAction.executeAction: Sip ClientActor: " + sipClientActor.getClientActorId()
              + ", Action Id: " + actionId + ", Event Received: " + eventReceived.getMethod());
        if (method.equals( eventReceived.getMethod()))
        {
          if (SipConstants.INVITE.equals( eventReceived.getMethod()))
          {
            sipClientActor.getSipClientTest().setSipCallKey( eventReceived.getCallId());
          }

          // Action Success

          notifySipClientTest( new SipClientTestReportEvent( ReportEventType.ACTION_SUCCESS, actionId, sipClientActor
              .getClientActorId(), isLastAction(), "ReceiveSipRequestAction " + method ,"OK"));

        }
        else
        {
          notifySipClientTest( new SipClientTestReportEvent( ReportEventType.ACTION_FAILED, actionId, sipClientActor
              .getClientActorId(), isLastAction(), "ReceiveSipRequestAction " + method
              ,"Error: SIP Method awaited: " + method + ", SIP Method received: " + eventReceived.getMethod()));
        }
      }
      else
      {
        if (logger.isTraceEnabled())
          logger.trace( "ReceiveSipRequestAction.executeAction: Sip ClientActor: " + sipClientActor.getClientActorId()
              + ", Action Id: " + actionId + ", No Event Received => Action Failed");
        notifySipClientTest( new SipClientTestReportEvent( ReportEventType.ACTION_FAILED, actionId, sipClientActor
            .getClientActorId(), isLastAction(), "ReceiveSipRequestAction " + method ,"Error: No Event Received"));

      }

    }
    catch (Throwable e)
    {
      logger.error( "ReceiveSipResponseAction.executeAction ERROR: Sip ClientActor: "
          + sipClientActor.getClientActorId() + ", Action Id: " + actionId + ", Error:\n " + e.getMessage());

      notifySipClientTest( new SipClientTestReportEvent( ReportEventType.ACTION_FAILED, actionId, sipClientActor
          .getClientActorId(), isLastAction(), "ReceiveSipRequestAction " + method ,"Error: " + e.getMessage()));
    }
  }
}
