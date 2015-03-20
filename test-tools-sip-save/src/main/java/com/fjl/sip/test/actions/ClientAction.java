package com.fjl.sip.test.actions;

import org.apache.log4j.Logger;

import com.fjl.sip.test.ClientActor;

import fj_levee.test.report.SipClientTestReportEvent;




public abstract class ClientAction implements Runnable
{
  /**
   * Action ID
   */
  protected int actionId;
  /**
   * Sip Client Actor
   */
  protected ClientActor clientActor;
  /**
   * Log4J Logger
   */
  protected Logger logger = Logger.getLogger( com.fjl.sip.test.actions.ClientAction.class);

  /**
   * Client Action Listener (Listener of this ClientAction)
   */
  //protected Vector<ClientActionListener> clientActionListeners;
  /**
   * Determines if this action has already been executed
   */
  protected boolean actionExecuted;
  
  /**
   * Determines if this action is the Last of the Action to be executed by an actor
   */
  protected boolean lastAction;
  
  
  /**
   * 
   * Constructor of ClientAction.
   *
   * @param clientActor
   * @param actionId
   */
  public ClientAction(ClientActor clientActor,int actionId){
    this.actionId = actionId;
    this.clientActor = clientActor;
    this.actionExecuted=false;
    this.lastAction =false;
  }
  /**
   * Execute the Action
   * @return true if the execution is a success
   */
  //public abstract boolean executeAction();
  
  
  /**
   * Notify the Listeners of this ClientAction
   * @param reportEvent Specifes the Report Event.
   * @return true if the notifications work
   */
  protected void notifySipClientTest (SipClientTestReportEvent reportEvent)
  {
    clientActor.getSipClientTest().notifyEndOfAction(reportEvent);
   
  }
  /**
   * actionExecuted
   * @param actionId Specifies the Id of the Executed Action
   * @return true if action success
   */
  /*
  public boolean actionExecuted(String actionId){
    if(logger.isTraceEnabled()) logger.debug( "ClientAction:actionExecuted "+actionId);
    return executeAction();
  }
  */
  /**
   * Checks if this Action has already been executed
   * @return true if this Action has already been executed
   */
  public boolean isActionExecuted ()
  {
    return actionExecuted;
  }
  
  /**
   * Get the Action Id of this Action
   * @return the Action Id
   */
  public int getActionId(){
    return actionId;
  }
  /**
   * Set this action as the last action to be executed by an Actor
   * @param lastAction
   */
  public void setLastAction(boolean lastAction){
    this.lastAction = lastAction;
  
  }
  /**
   * Get if this action is the last action to be executed by an Actor
   * @return true if this action is the last action to be executed by an Actor
   */
  public boolean isLastAction(){
    return this.lastAction ;
  
  }
  
}
