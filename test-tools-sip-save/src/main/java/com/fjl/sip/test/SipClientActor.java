package com.fjl.sip.test;


import java.util.concurrent.ConcurrentHashMap;

import com.fjl.sip.test.actions.ClientAction;
import com.fjl.sip.test.actions.ClientActionListener;
import com.fjl.sip.test.actions.SipClientAction;
import com.fjl.sip.test.actions.SipClientActionFactory;

import fj_levee.test.parser.SipTestCallFlowAction;
import fj_levee.test.parser.SipTestCallFlowActor;

public class SipClientActor extends ClientActor implements ClientActionListener
{
  /**
   * Map between the Action Id executed and the Action id (Http request action) to be executed by this by this actor
   */
  private ConcurrentHashMap<String, SipClientAction> actionsMap;

  /**
   * Sip Client UA
   */
  private SipClientUA sipClientUA;

  /**
   * 
   * Constructor of SipClientActor.
   * 
   * @param sipClientTest
   * @param sipTestCallFlowActor
   * @param sipClientUA
   */
  public SipClientActor ( SipClientTest sipClientTest, SipTestCallFlowActor sipTestCallFlowActor)
  {
    super( sipClientTest, sipTestCallFlowActor);
    actionsMap = new ConcurrentHashMap<String, SipClientAction>();
    this.sipClientUA = null;

    if (logger.isDebugEnabled()) logger.debug( "SipClientActor.constructor, Actor id: " + clientActorId);
  }

  /**
   * 
   * @see com.fjl.sip.test.ClientActor#initClientActionsList()
   */
  @Override
  public void initClientActionsList () throws SipClientTestException
  {
    if (logger.isDebugEnabled()) logger.debug( "SipClientActor.initSipClientActionsList: Actor Id:" + clientActorId);

    ClientAction lastSipClientActionCreated = null;
    
   
    
    for (int i = 0; i < sipTestCallFlowActor.getSipTestCallFlowActions().size(); i++)
    {
      SipTestCallFlowAction callFlowAction = sipTestCallFlowActor.getSipTestCallFlowActions().get( i);
      ClientAction action = SipClientActionFactory.createSipClientAction( this, callFlowAction,
          lastSipClientActionCreated);

      lastSipClientActionCreated = action;
      this.clientActions.add( action);
    }
    
    //  set the last action
    if(lastSipClientActionCreated!=null){
      lastSipClientActionCreated.setLastAction(true);
    }
  }

  /**
   * Start the excecute of this Sip ClientActor
   * 
   */
  @Override
  public void start ()
  {

    if ((clientActions.get( 0) != null) && (clientActions.get( 0).getActionId() == 0))
    {
      executeAction( (SipClientAction) clientActions.get( 0));
    }
  }

  /**
   * Get the SipClient UA.
   * 
   * @return the Sip Client UA.
   */
  public SipClientUA getSipClientUA ()
  {
    return sipClientUA;
  }
  /**
   * Set the SipClient UA
   * @param sipClientUA Specifies the SipClientUA
   */
  public void setSipClientUA ( SipClientUA sipClientUA)
  {
    this.sipClientUA = sipClientUA;
  }

  /**
   * Add an Action mapping between the Action id executed (notified with the ClientActionListener Interface and the Http
   * request Action to be executed by the Http Actor
   * 
   * @param actionIdToBeListened Specifies the action to be listened.
   * @param actionToBeExecuted Specifies the action to be executed.
   */

  public void addActionMap ( String actionIdToBeListened, SipClientAction actionToBeExecuted)
  {
    getSipClientTest().addClientActionListener( actionIdToBeListened, this);
    actionsMap.put( "" + actionIdToBeListened, actionToBeExecuted);
  }

  /**
   * actionExecuted
   * 
   * @param actionId Specifies the Id of the Executed Action
   */
  public void actionExecuted ( String actionId)
  {
    if (logger.isTraceEnabled())
      logger.debug( "SipClientActor.actionExecuted " + actionId + ", search if there is action to be executed");
    SipClientAction action = actionsMap.get( actionId);
    if (action != null)
    {
      if (logger.isTraceEnabled())
        logger.debug( "SipClientActor.actionExecuted: An action has been found with Id: " + action.getActionId());
      executeAction( action);

    }

  }

  /**
   * Execute an action
   * 
   * @param action Specifies the Action to execute
   */
  private void executeAction ( SipClientAction action)
  {
    if (logger.isTraceEnabled()) logger.debug( "SipClientActor.executeAction id:  " + action.getActionId());
    Thread executeActionThread = new Thread( action);
    executeActionThread.start();

  }

}
