package com.fjl.sip.test;

import org.apache.log4j.Logger;

public class ClientActorTask extends Thread {
  /**
   * Client Actor
   */
  private ClientActor clientActor;
  
  /**
   * Log4J Logger
   */
  protected Logger logger = Logger.getLogger( com.fjl.sip.test.ClientActorTask.class);

  /**
   * Constructor
   * 
   * @param clientActor
   *          specifies the ClientActor to execute
   */
  public ClientActorTask(ClientActor clientActor) {
    super();
    this.clientActor = clientActor;
  }

  /**
   * Runnable.run() implementation
   */
  public void run() {
    if(logger.isTraceEnabled()) logger.trace( "ClientActorTask.start Actor Id: "+clientActor.getClientActorId());
    
    if(clientActor.getActionsNumber()>0){
      clientActor.start();
      try{
        do {
          Thread.sleep( 1000);
        }while(1==1);
      }catch (InterruptedException e) {
        if(logger.isTraceEnabled()) logger.trace( "ClientActorTask.run: Actor "+clientActor.getClientActorId()+" task finished");
      }
    }else{
      if(logger.isTraceEnabled()) logger.trace( "ClientActorTask.run: Actor "+clientActor.getClientActorId()+" task finished because no actions to be executed");
      
    }
    
  }
  
  
  /**
   * client Actor Finished
   */
  public void clientActorFinished(){
    
    this.interrupt();
  }
  
  /**
   * Stop client Actor
   */
  public void stopClientActor(){
    
    logger.error( "ClientActorTask.stopClientActor: "+clientActor.getClientActorId());
    this.interrupt();
  }

}
