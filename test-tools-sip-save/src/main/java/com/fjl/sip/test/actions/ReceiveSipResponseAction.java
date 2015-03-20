package com.fjl.sip.test.actions;

import javax.sip.ResponseEvent;

import com.fjl.sip.test.SipClientActor;
import com.fjl.sip.test.SipClientUAEvent;
import com.fjl.sip.test.parser.elements.SipResponseElement;

import fj_levee.test.report.SipClientTestReportEvent;
import fj_levee.test.report.SipClientTestReportEvent.ReportEventType;


public class ReceiveSipResponseAction extends SipClientAction
{
  /**
   * Method Awaited
   */
  private String method;

  /**
   * status Awaited
   */
  private int statusCode;

  /**
   * 
   * Constructor of ReceiveSipResponseAction.
   * 
   * @param sipClientActor
   * @param responseElement
   * @param timeToWait
   */
  public ReceiveSipResponseAction ( SipClientActor sipClientActor, SipResponseElement responseElement, long timeToWait)
  {
    super( sipClientActor, responseElement.getCallid(), responseElement.getSipTestActionId(), timeToWait);
    this.method = responseElement.getMethod();
    this.statusCode = responseElement.getCode();

  }

  @Override
  public void executeAction ()
  {
    try
    {
      if (logger.isDebugEnabled())
        logger.debug( "ReceiveSipResponseAction.executeAction: Sip ClientActor: " + sipClientActor.getClientActorId()
            + ", Action Id: " + actionId+ ",Max Time To Wait: " + timeToWait+", SIP Response Method: "+method+", SIP response status: "+statusCode);
      
      // Check if the response awaited is a CANCEL response
      
      /*
      if (SipConstants.CANCEL.equals( method))
      {
        // Yes the Sip Layer will never notify the Sip UA of the ACK => No need to wait for.
        notifySipClientTest( new SipClientTestReportEvent( ReportEventType.ACTION_SUCCESS, actionId, sipClientActor
            .getClientActorId(), isLastAction(), "ReceiveSipResponseAction " + method + " (" + statusCode
            + ")","OK (auto received)"));
        return;

      }*/

      SipClientUAEvent eventReceived = null;

      WaitForEventAction waitAction = new WaitForEventAction(sipClientActor.getSipClientUA());
      if (logger.isTraceEnabled())
        logger.debug( "getSipClientActorId.executeAction: Sip ClientActor: " + sipClientActor.getClientActorId()
            + ", Action Id: " + actionId + ", Start...");
      waitAction.waitForEvent( timeToWait, ResponseEvent.class, method,  scenarioSipCallId,statusCode);
      if (logger.isTraceEnabled())
        logger.debug( "ReceiveSipResponseAction.executeAction: Sip ClientActor: " + sipClientActor.getClientActorId()
            + ", Action Id: " + actionId + ", waitAction ended.");
      eventReceived = waitAction.getSipClientUAEvent();

      if (eventReceived != null)
      {
        if (logger.isTraceEnabled())
          logger.debug( "ReceiveSipResponseAction.executeAction: Sip ClientActor: " + sipClientActor.getClientActorId()
              + ", Action Id: " + actionId + ", Event Received: " + eventReceived.getMethod());
        // Check the event received is a SipResponseEvent
        try
        {
          ResponseEvent responseEvent = (ResponseEvent) eventReceived.getEvent();
          String methodReceived = eventReceived.getMethod();
          if (method.equals( methodReceived))
          {
            // Same method as awaited now check the code
            int statusReceived = responseEvent.getResponse().getStatusCode();
            if (statusReceived == statusCode)
            {

              notifySipClientTest( new SipClientTestReportEvent( ReportEventType.ACTION_SUCCESS, actionId,
                  sipClientActor.getClientActorId(), isLastAction(), "ReceiveSipResponseAction " + method + " ("
                      + statusCode +")", "OK"));

            }
            else
            {
              if (logger.isTraceEnabled())
                logger.debug( "ReceiveSipResponseAction.executeAction: Sip ClientActor: "
                    + sipClientActor.getClientActorId() + ", Action Id: " + actionId
                    + ", Sip Response received status: " + statusReceived + ", status awaited: " + statusCode
                    + " => Action Failed");

              notifySipClientTest( new SipClientTestReportEvent( ReportEventType.ACTION_FAILED, actionId,
                  sipClientActor.getClientActorId(), isLastAction(), "ReceiveSipResponseAction " + method + " ("
                      + statusCode + ")", "Error: Status Code awaited: " + statusCode + ", Status Code received: "
                      + statusReceived));

            }

          }
          else
          {
            if (logger.isTraceEnabled())
              logger.debug( "ReceiveSipResponseAction.executeAction: Sip ClientActor: "
                  + sipClientActor.getClientActorId() + ", Action Id: " + actionId
                  + ", Sip Response received on method: " + methodReceived + ", method awaited: " + method
                  + " => Action Failed");
            notifySipClientTest( new SipClientTestReportEvent( ReportEventType.ACTION_FAILED, actionId, sipClientActor
                .getClientActorId(), isLastAction(), "ReceiveSipResponseAction " + method + " (" + statusCode
                + ")", "Error: SIP Method awaited: " + method + ", SIP Method received: " + eventReceived.getMethod()));

          }
        }
        catch (ClassCastException e)
        {
          if (logger.isTraceEnabled())
            logger.debug( "ReceiveSipResponseAction.executeAction: ClassCastException Sip ClientActor: "
                + sipClientActor.getClientActorId() + ", Action Id: " + actionId
                + ", Event Received is not a ResponseEvent => Action Failed");
          notifySipClientTest( new SipClientTestReportEvent( ReportEventType.ACTION_FAILED, actionId, sipClientActor
              .getClientActorId(), isLastAction(), "ReceiveSipResponseAction " + method + " (" + statusCode
              + ")","Error: SIP Method awaited: " + method + ", Event Received is not a ResponseEvent"));

        }

      }
      else
      {
        if (logger.isTraceEnabled())
          logger.debug( "ReceiveSipResponseAction.executeAction: Sip ClientActor: " + sipClientActor.getClientActorId()
              + ", Action Id: " + actionId + ", No Event Received => Action Failed");

        notifySipClientTest( new SipClientTestReportEvent( ReportEventType.ACTION_FAILED, actionId, sipClientActor
            .getClientActorId(), isLastAction(), "ReceiveSipResponseAction " + method + " (" + statusCode
            + ")" ,"Error: No Event Received"));
      }
    }
    catch (Throwable e)
    {
      logger.error( "ReceiveSipResponseAction.executeAction ERROR: Sip ClientActor: "
          + sipClientActor.getClientActorId() + ", Action Id: " + actionId + ", Error:\n " + e.getMessage());

      notifySipClientTest( new SipClientTestReportEvent( ReportEventType.ACTION_FAILED, actionId, sipClientActor
          .getClientActorId(), isLastAction(), "ReceiveSipResponseAction " + method + " (" + statusCode + ")"
          ,"Error: " + e.getMessage()));
    }
  }

  /**
   * Get the Sip Status code awaited.
   * 
   * @return the Status code awaited.
   */
  public int getStatusCode ()
  {
    return statusCode;
  }
}
