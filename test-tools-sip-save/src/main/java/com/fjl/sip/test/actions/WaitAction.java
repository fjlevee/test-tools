package com.fjl.sip.test.actions;

import com.fjl.sip.test.SipClientTestException;
import com.fjl.sip.test.WaitActor;
import com.fjl.sip.test.parser.elements.WaitElement;

import fj_levee.test.report.SipClientTestReportEvent;
import fj_levee.test.report.SipClientTestReportEvent.ReportEventType;


public class WaitAction extends ClientAction implements Runnable
{

  /**
   * WaitActor Actor
   */
  private WaitActor waitActor;

  /**
   * Time to wait in ms
   */
  private long timeToWait;



  /**
   * 
   * Constructor of WaitAction.
   * 
   * @param httpClientActor
   * @param httpRequestElement
   * @param sipServerConfiguration
   * @throws SipClientTestException
   */
  public WaitAction ( WaitActor waitActor, WaitElement waitElement) throws SipClientTestException
  {
    super( waitActor, waitElement.getSipTestActionId());
    if (logger.isTraceEnabled())
      logger.trace( "WaitAction.constructor: Wait Actor: " + waitActor.getClientActorId()
          + ", Action Id: " + actionId);
    this.waitActor = waitActor;
    this.timeToWait = waitElement.getTimeToWait();
    
    this.actionExecuted = false;
    

    // Now add this request has a listener of the Action executed before:
    if (actionId != 0)
    {
      int oldActionId = actionId-1;      
      waitActor.addActionMap( ""+oldActionId, this);
    }

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
      if (logger.isDebugEnabled())
        logger.debug( "WaitAction.run: Wait Actor: " + waitActor.getClientActorId()
            + ", Action Id: " + actionId);

     try{
       Thread.sleep( timeToWait);
     }catch (InterruptedException e) {
      // TODO: handle exception
     }
     if (logger.isDebugEnabled())
       logger.debug( "WaitAction.executeAction: Sip ClientActor: " + waitActor.getClientActorId()
           + ", Action Id: " + actionId + ", Action done");
     notifySipClientTest( new SipClientTestReportEvent( ReportEventType.ACTION_SUCCESS, actionId, waitActor
         .getClientActorId(), isLastAction(), "WaitAction " + timeToWait+" ms" ,"OK"));
     actionExecuted = true;
    }
    catch (Throwable e)
    {
      logger.error( "WaitAction.executeAction ERROR: Wait Actor ClientActor: "
          + waitActor.getClientActorId() + ", Action Id: " + actionId + ", Error:\n " + e.getMessage());
      notifySipClientTest(
          new SipClientTestReportEvent( ReportEventType.ACTION_FAILED, actionId, waitActor.getClientActorId(),isLastAction(),
              "WaitAction (" + timeToWait+" ms)" , e.getMessage()));
      actionExecuted = true;
    }

  }

  
 

  /**
   * Execute this Action
   * 
   * @return true if the Execution is a Success.
   */
  /*
  public boolean executeAction ()
  {

    logger
        .error( "WaitAction.executeAction: An Http Request Action must be executed with the run method within an independant Thread");
    return false;
  }*/

  /**
   * Checks if this Action has already been executed
   * 
   * @return true if this Action has already been executed
   */
  public boolean isActionExecuted ()
  {
    return actionExecuted;
  }


  
 

}
