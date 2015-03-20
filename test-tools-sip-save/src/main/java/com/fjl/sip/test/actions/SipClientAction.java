package com.fjl.sip.test.actions;

import com.fjl.sip.test.SipClientActor;

import fj_levee.test.report.SipClientTestReportEvent;
import fj_levee.test.report.SipClientTestReportEvent.ReportEventType;


public abstract class SipClientAction extends ClientAction
{

  /**
   * Sip Client Actor
   */
  protected SipClientActor sipClientActor;

  /**
   * Time to wait
   */
  protected long timeToWait;

  /**
   * Client Call ID of this action
   */
  protected String scenarioSipCallId;

  /**
   * This parameter checks if the previous received reponse was an Error Response. This is helpful for Sending ACK
   * messages or not (or receiving ACK or not) => In case of Error response received before , the ACK is automatically
   * sent by the Sip Layer, so the sendAck must not be done.
   */
  protected boolean previousMessageWasAnError = false;

  /**
   * 
   * Constructor of SipClientAction.
   * 
   * @param sipClientActor
   * @param callId
   * @param actionId
   * @param timeTowait
   */
  public SipClientAction ( SipClientActor sipClientActor, String scenarioSipCallId, int actionId, long timeToWait)
  {
    super( sipClientActor, actionId);
    this.sipClientActor = sipClientActor;
    this.timeToWait = timeToWait;
    this.scenarioSipCallId = scenarioSipCallId;

    // Now add this request has a listener of the Action executed before:
    if (actionId != 0)
    {
      int oldActionId = actionId - 1;
      sipClientActor.addActionMap( "" + oldActionId, this);

    }
  }

  /**
   * Execute the Action
   */
  public abstract void executeAction ();

  /**
   * Get a Sip Client Actor with its ID
   * 
   * @param clientActorId Specifies the Sip Client Actor Id.
   * @return the Client Client Actor
   */
  public SipClientActor getSipClientActorWithId ( String clientActorId)
  {
    return sipClientActor.getSipClientTest().getSipClientActor( clientActorId);
  }

  /**
   * Get the CallId
   * 
   * @return the CallId
   */
  public String getScenarioSipCallId ()
  {
    return scenarioSipCallId;
  }

  /**
   * This parameter checks if the previous received reponse was an Error Response. This is helpful for Sending ACK
   * messages or not (or receiving or not)=> In case of Error response received before , the ACK is automatically sent
   * by the Sip Layer, so the sendAck must not be done.
   * 
   * @param previousMessageWasAnError
   */
  public void setPreviousMessageWasAnError ( boolean previousMessageWasAnError)
  {
    this.previousMessageWasAnError = previousMessageWasAnError;
  }

  /**
   * Execute this Action
   * 
   * @return true if the Execution is a Success.
   */
  public void run ()
  {
    try
    {
      executeAction();
    }
    catch (Throwable e)
    {
      logger.error( "SipClientAction.executeAction ERROR: SipClientActor: "
          + sipClientActor.getClientActorId() + ", Action Id: " + actionId + ", Error:\n " + e.getMessage());
      notifySipClientTest( new SipClientTestReportEvent( ReportEventType.ACTION_FAILED, actionId, sipClientActor
          .getClientActorId(), isLastAction(), "Error in Class: " + getClass().getCanonicalName() ,
          e.getMessage()));

    }

  }

}
