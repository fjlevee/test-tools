package com.fjl.sip.test;

import java.util.Vector;

import org.apache.log4j.Logger;

import com.fjl.sip.test.actions.ClientAction;

import fj_levee.test.parser.SipTestCallFlowActor;

public abstract class ClientActor
{

  /**
   * Log4J logger
   */
  protected Logger logger = Logger.getLogger( com.fjl.sip.test.ClientActor.class);

 

  /**
   * Sip Client Test
   */
  protected SipClientTest sipClientTest;

  /**
   * Client Action List
   */
  protected Vector<ClientAction> clientActions;

  /**
   * Client Actor Id
   */
  protected String clientActorId;

  /**
   * Sip Test Call Flow Actor
   */
  protected SipTestCallFlowActor sipTestCallFlowActor;

  /**
   * 
   * Constructor of ClientActor.
   * 
   * @param sipClientTest
   * @param sipTestCallFlowActor
   */
  public ClientActor ( SipClientTest sipClientTest, SipTestCallFlowActor sipTestCallFlowActor)
  {

    this.sipClientTest = sipClientTest;
    this.clientActorId = sipTestCallFlowActor.getActorId();
    this.sipTestCallFlowActor = sipTestCallFlowActor;
    this.clientActions = new Vector<ClientAction>();
    if (logger.isDebugEnabled()) logger.debug( "ClientActor.constructor, Actor id: " + clientActorId);
  }

  /**
   * Init the Sip Client Action List
   * @throws SipClientTestException
   */
  public abstract void initClientActionsList () throws SipClientTestException;
  
  /**
   * Start the excecute of this  ClientActor
   * 
   */
  
  public abstract void start ();
 
  /**
   * Get a Client Action with its id
   * @param actionId Specifies the Action Id to find
   * @return the Client Action if found.
   */
  public ClientAction getClientActionWithId(int actionId){
    
    // Search in the List of Client Actions:
    for (int i=0;i< clientActions.size();i++){
      if(clientActions.get(i).getActionId()==actionId){
        return clientActions.get(i);
      }
    }
   
    return null;
  }
  

  /**
   * Get the Sip Client Actor Id
   * 
   * @return the Sip Client Actor Id
   */
  public String getClientActorId ()
  {
    return clientActorId;
  }

  /**
   * Get the Sip Client Test
   * 
   * @return the Sip Client Test
   */
  public SipClientTest getSipClientTest ()
  {
    return sipClientTest;
  }
  
  /**
   * Get the Number of actions to be executed by this Client
   * @return the Number of actions to be executed by this Client
   */
  public int getActionsNumber(){
    return clientActions.size();
    
  }

}
