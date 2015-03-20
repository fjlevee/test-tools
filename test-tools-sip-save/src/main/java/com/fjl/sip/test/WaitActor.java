package com.fjl.sip.test;

import java.util.concurrent.ConcurrentHashMap;

import com.fjl.sip.test.actions.ClientActionListener;
import com.fjl.sip.test.actions.WaitAction;
import com.fjl.sip.test.parser.elements.WaitElement;

import fj_levee.test.parser.SipTestCallFlowActor;

public class WaitActor extends ClientActor implements ClientActionListener
{

  /**
   * Map between the Action Id executed and the Action id (Http request action) to be executed by this by this actor
   */
  private ConcurrentHashMap<String, WaitAction> actionsMap;

  /**
   * 
   * Constructor of WaitActor.
   * 
   * @param sipClientTest
   * @param sipTestCallFlowActor
   * @param serverConfiguration
   */
  public WaitActor ( SipClientTest sipClientTest, SipTestCallFlowActor sipTestCallFlowActor)
  {
    super( sipClientTest, sipTestCallFlowActor);

    this.actionsMap = new ConcurrentHashMap<String, WaitAction>();
    if (logger.isDebugEnabled()) logger.debug( "WaitActor.constructor, Actor id: " + clientActorId);
  }

  @Override
  public void initClientActionsList () throws SipClientTestException
  {
    if (logger.isDebugEnabled()) logger.debug( "WaitActor.initClientActionsList: Actor Id:" + clientActorId);

    int actionsNumber = sipTestCallFlowActor.getSipTestCallFlowActions().size();

    for (int i = 0; i < actionsNumber; i++)
    {
      WaitElement waitElement = (WaitElement) sipTestCallFlowActor.getSipTestCallFlowActions().get( i);
      WaitAction action = new WaitAction( this, waitElement);

      clientActions.add( action);

    }

    // set the last action
    clientActions.get( actionsNumber - 1).setLastAction( true);
  }

  /**
   * Start the excecute of this Sip ClientActor
   * 
   */
  public void start ()
  {

    if ((clientActions.get( 0) != null) && (clientActions.get( 0).getActionId() == 0))
    {
      executeAction( (WaitAction) clientActions.get( 0));
    }
  }

  /**
   * Add an Action mapping between the Action id executed (notified with the ClientActionListener Interface and the Http
   * request Action to be executed by the Http Actor
   * 
   * @param actionIdToBeListened Specifies the action to be listened.
   * @param actionToBeExecuted Specifies the action to be executed.
   */

  public void addActionMap ( String actionIdToBeListened, WaitAction actionToBeExecuted)
  {
    getSipClientTest().addClientActionListener( actionIdToBeListened, this);
    actionsMap.put( actionIdToBeListened, actionToBeExecuted);
  }

  /**
   * actionExecuted
   * 
   * @param actionId Specifies the Id of the Executed Action
   */
  public void actionExecuted ( String actionId)
  {
    if (logger.isTraceEnabled())
      logger.debug( "WaitActor.actionExecuted " + actionId + ", search if there is action to be executed");
    WaitAction action = actionsMap.get( actionId);
    if (action != null)
    {
      if (logger.isTraceEnabled())
        logger.debug( "WaitActor.actionExecuted: An action has been found with Id: " + action.getActionId());
      executeAction( action);

    }

  }

  /**
   * Execute an action
   * 
   * @param action Specifies the Action to execute
   */
  private void executeAction ( WaitAction action)
  {
    if (logger.isTraceEnabled()) logger.debug( "WaitActor.executeAction id:  " + action.getActionId());
    Thread executeActionThread = new Thread( action);
    executeActionThread.start();

  }

}
