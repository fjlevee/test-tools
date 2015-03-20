package com.fjl.sip.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.fjl.sip.test.actions.ClientAction;
import com.fjl.sip.test.actions.ClientActionListener;

import fj_levee.test.parser.SipTestCallFlow;
import fj_levee.test.parser.SipTestCallFlowActor;
import fj_levee.test.report.SipClientTestListReport;
import fj_levee.test.report.SipClientTestReport;
import fj_levee.test.report.SipClientTestReportEvent;
import fj_levee.test.report.SipClientTestReportEventList;
import fj_levee.test.report.SipClientTestReportEvent.ReportEventType;

public class SipClientTest 
{
  /**
   * Log4J logger
   */
  private Logger logger = Logger.getLogger( com.fjl.sip.test.SipClientTest.class);
  
  /**
   * sip Client Test unique Id
   */
  private String sipClientTestId;
  /**
   * SipClient test Tasks
   */
  protected HashMap<String, ClientActorTask> sipClientTestTasks = new HashMap<String, ClientActorTask>();

  /**
   * SipClient Actors List
   */
  protected Vector<SipClientActor> sipClientActors = new Vector<SipClientActor>();

  /**
   * Http Client Actors List
   */
  protected Vector<HttpClientActor> httpClientActors = new Vector<HttpClientActor>();
  /**
   * Wait Actors List
   */
  protected Vector<WaitActor> waitActors = new Vector<WaitActor>();

  /**
   * Sip Client Test Report
   */
  private SipClientTestReport sipClientTestReport;

  /**
   * Sip Client Test Report
   */
  private SipClientTestReportEventList sipClientReportEventList;

  /**
   * Sip Client Test Name
   */
  private String sipClientTestName;

  /**
   * This long parameter specifies the basis time for the Actions Time.
   */
  private long baseTime;
  
  /**
   * Sip Call Key. Helpful to make the Http/Sip convergence
   */
  private String sipCallKey;
  
  /**
   * SipTestCallFlow
   */
  private SipTestCallFlow sipTestCallFlow;
  
  
  /**
   * Client Actions Listeners HashMap
   */
  private HashMap<String,Vector<ClientActionListener>> clientActionsListenersMap;
  /**
   * Sip Client Test List Report
   */
  private SipClientTestListReport sipClientTestListReport;
  

  /**
   * Constructor
   * 
   * @param sipClientTestLauncher
   * @param sipClientTestId
   * @throws SipClientTestException
   */
  public SipClientTest ( SipClientTestListReport sipClientTestListReport,SipTestCallFlow sipTestCallFlow,String sipClientTestId) throws SipClientTestException
  {
    if (logger.isDebugEnabled()) logger.debug( "SipClientTest.constructor: Start...");
    this.sipClientTestId = sipClientTestId;
    this.sipClientTestListReport = sipClientTestListReport;
    clientActionsListenersMap = new HashMap<String, Vector<ClientActionListener>>();
    this.sipTestCallFlow = sipTestCallFlow;
    this.sipClientTestName = sipTestCallFlow.getSipTestCallFlowName();
    
    sipClientActors = new Vector<SipClientActor>();
    baseTime = sipTestCallFlow.getSipTestCallFlowTime();
    // create the Sip Client Actors
    for (int i = 0; i < sipTestCallFlow.getSipTestCallFlowActorsCount(); i++)
    {
      SipTestCallFlowActor callFlowActor = sipTestCallFlow.getSipTestCallFlowActor( i);
      if (callFlowActor.isSipClient())
      {
        SipClientActor actor = new SipClientActor( this, callFlowActor);
        sipClientActors.add( actor);
      }
      else if (callFlowActor.getActorName().contains( SipTestCallFlow.HTTP_CLIENT_ACTOR_NAME))
      {
        HttpClientActor actor = new HttpClientActor( this, callFlowActor, SipClientTestManager.getInstance().getServerConfiguration());
        httpClientActors.add( actor);
      }
      else if (callFlowActor.getActorName().contains( SipTestCallFlow.WAIT_ACTOR_NAME))
      {
        WaitActor actor = new WaitActor( this, callFlowActor);
        waitActors.add( actor);
      }
    }
    if (logger.isDebugEnabled()) logger.debug( "SipClientTest.constructor: Sip and Http Client Actors created");
    
    // Initialize the Actions for Sip Client Actors
    for (int i = 0; i < sipClientActors.size(); i++)
    {
      sipClientActors.get( i).initClientActionsList();
    }
    // Initialize the Actions for Http Client Actors
    for (int i = 0; i < httpClientActors.size(); i++)
    {
      httpClientActors.get( i).initClientActionsList();
    }
    // Init the Actions for wait actors
    for (int i = 0; i < waitActors.size(); i++)
    {
      waitActors.get( i).initClientActionsList();
    }
    initiateSipClientReportTest();
    if (logger.isDebugEnabled()) logger.debug( "SipClientTest.constructor: End.");
  }
  /**
   * Iniatiate the Sip Client Report Test
   */
  private void initiateSipClientReportTest(){
    if (logger.isDebugEnabled()) logger.debug( "SipClientTest.initiateSipClientReportTest");
    sipClientTestReport = new SipClientTestReport( sipClientTestName);
    sipClientReportEventList = new SipClientTestReportEventList( sipClientTestReport);
    this.sipClientTestListReport.addSipClientTestReport(sipClientTestReport);
  }

  /**
   * startTest
   * @param listener
   */
  public void startTest ()
  {
    if (logger.isDebugEnabled()) logger.debug( "SipClientTest.startTest Generate UA for all Actors");
    
    // Get a Sip Client UA for all of the SipClientActor
    for(int i=0;i<sipClientActors.size();i++){
      sipClientActors.get(i).setSipClientUA( SipClientTestManager.getInstance().getSipClientUA( i));
    }
    
    
    sipCallKey = null;
    sipClientTestTasks.clear();
    

    
    try
    {
      
      // Start each wait actor
      for (int i = 0; i < waitActors.size(); i++)
      {
               
        WaitActor actor = waitActors.get( i);

        ClientActorTask thread = new ClientActorTask( actor);
        thread.start();
        sipClientTestTasks.put( actor.getClientActorId(), thread);
      }
      
      // Start each http client actor
      for (int i = 0; i < httpClientActors.size(); i++)
      {
               
        HttpClientActor actor = httpClientActors.get( i);

        ClientActorTask thread = new ClientActorTask( actor);
        thread.start();
        sipClientTestTasks.put( actor.getClientActorId(), thread);
      }
      // Start each sip actor
      for (int i = 0; i < sipClientActors.size(); i++)
      {
        SipClientActor actor = sipClientActors.get( i);

        ClientActorTask thread = new ClientActorTask( actor);
        thread.start();
        sipClientTestTasks.put( actor.getClientActorId(), thread);
      }
      
      
      // Wait for test threads execution
      Iterator<String> iter = sipClientTestTasks.keySet().iterator();
      
      while (iter.hasNext()){
        ClientActorTask thread = sipClientTestTasks.get( iter.next());
        thread.join();
        
      }
      

      sipClientTestTasks.clear();
      
      // Releases the SipClientUA
      for(int i=0;i<sipClientActors.size();i++){
        sipClientActors.get(i).getSipClientUA().setUsed( false);
      }
      
    }
    catch (Throwable e)
    {
      logger.error( "exception occured during test exection");
      // Releases the SipClientUA
      for(int i=0;i<sipClientActors.size();i++){
        sipClientActors.get(i).getSipClientUA().setUsed( false);
      }
      sipClientTestTasks.clear();
    }

    if (logger.isDebugEnabled())
    {
      logger.debug( "SipClientTest.endOfTest: Report is \n" + sipClientTestReport.getFinalResult());
      try{
        Thread.sleep( 1000);
      }catch (InterruptedException e) {
        // TODO: handle exception
      }
      logger.debug( "SipClientTest.endOfTest: Report is \n" + sipClientTestReport.getFinalResult());
    }
    if (logger.isInfoEnabled()) logger.info( sipClientTestReport.getReportAsString());

  }

  /**
   * Add A Report Event in the Report Event List.
   * 
   * @param reportEvent Specifes the report Event to be added in the Event List.
   */
  /*
  public void addReportEventList ( SipClientTestReportEvent reportEvent)
  {
    sipClientReportEventList.addReportEvent( reportEvent);
  }*/

  /**
   * Get the Base Time For this This Client Test
   * 
   * @return
   */
  public long getBaseTime ()
  {
    return baseTime;
  }

  /**
   * Get the Sip Client Actor by giving the Actor Id
   * 
   * @param actorId Specifies the Actor ID;
   * @return the Actor which ID equals to the given Actor ID
   */
  public SipClientActor getSipClientActor ( String actorId)
  {

    if (actorId != null)
    {
      for (int i = 0; i < sipClientActors.size(); i++)
      {
        if (actorId.equals( sipClientActors.get( i).getClientActorId()))
        {
          return sipClientActors.get( i);
        }
      }
    }
    return null;
  }

  /**
   * Get a Client Action with its id
   * 
   * @param actionId Specifies the Action Id to find
   * @return the Client Action if found.
   */
  public ClientAction getClientActionWithId ( int actionId)
  {
    ClientAction clientAction = null;
    // Search in the List of Sip Client Actors first:
    for (int i = 0; i < sipClientActors.size(); i++)
    {
      clientAction = sipClientActors.get( i).getClientActionWithId( actionId);
      if (clientAction != null)
      {
        return clientAction;
      }
    }

    // Then Search in the List of Http Client Actors:
    for (int i = 0; i < httpClientActors.size(); i++)
    {
      clientAction = httpClientActors.get( i).getClientActionWithId( actionId);
      if (clientAction != null)
      {
        return clientAction;
      }
    }
    // Then Search in the List of Wait Actors:
    for (int i = 0; i < waitActors.size(); i++)
    {
      clientAction = waitActors.get( i).getClientActionWithId( actionId);
      if (clientAction != null)
      {
        return clientAction;
      }
    }
    return clientAction;
  }
  /**
   * Get the Sip Call Key
   * @return the Sip Call Key (based on the Call-id header of the first SipDialog created)
   */
  public String getSipCallKey ()
  {
    return sipCallKey;
  }
  /**
   * Set the Sip Call Key
   * @param sipCallKey the Sip Call Key (based on the Call-id header of the first SipDialog created)
   */
  public void setSipCallKey ( String sipCallKey)
  {
    
    if(this.sipCallKey==null){
      if(logger.isTraceEnabled()) logger.trace("SipClientTest.setSipCallKey: "+sipCallKey);
      
      this.sipCallKey = sipCallKey;
    }
  }
  
  /**
   * Get an actor sip URI with a Actor ID build with  !!!!! XML CALL FLOW FILE !!!!!
   */
  public String getSipClientActorUri ( String sipActorId)
  {
    SipTestCallFlowActor  sipTestCallFlowActor = sipTestCallFlow.getSipTestCallFlowActor( sipActorId);
    if(sipTestCallFlowActor==null){
      return null;
    }
    return sipTestCallFlowActor.getSipUriActor();
  }
  
  /**
   * Get the Listener of the ClientAction Id
   * @param actionId Specifies the Client Action Id.
   * @return the Listener of this ClientAction
   */
  
  public Vector<ClientActionListener> getSipClientActionListeners (String actionId)
  {
    return clientActionsListenersMap.get( actionId);
  }
  /**
   * Add a Listener to the Given Client Action id
   * @param actionId Specifies the Client Action Id.
   * @param clientActionListener Specifies the Listeners to add.
   */
  
  public void addClientActionListener (String actionId, ClientActionListener clientActionListener)
  {
    if(logger.isTraceEnabled()) logger.trace( "clientActionsListenersMap.addClientActionListener: New Listener Added on Action "+actionId);
    
    Vector<ClientActionListener> clientActionListeners = clientActionsListenersMap.get( actionId);
    
    if(clientActionListeners==null){
      
      clientActionListeners = new Vector<ClientActionListener>();
      clientActionListeners.add( clientActionListener);
      clientActionsListenersMap.put( actionId, clientActionListeners);
    }
    else{
      clientActionListeners.add( clientActionListener);
      
    }
  }
  /**
   * Notify the Listeners that the ClientAction Id has been executed
   * @param reportEvent Specifes the Report Event.
   */
  public void notifyEndOfAction (SipClientTestReportEvent reportEvent )
  {
    int actionId = reportEvent.getActionId();
    
    sipClientReportEventList.addReportEvent( reportEvent);
    
    if(reportEvent.getEventType()==ReportEventType.ACTION_SUCCESS){
      Vector<ClientActionListener> clientActionListeners = clientActionsListenersMap.get( ""+actionId);
      if(clientActionListeners!=null){
        
        for(int i=0;i<clientActionListeners.size();i++){
          clientActionListeners.get(i).actionExecuted( ""+actionId);
        }
      }
      
      // Checks if the action is the last action of an actor:
      if( reportEvent.isLastAction()){
        // Yes => We have to Stop the Thread
        String actorId = reportEvent.getActorId();
        ClientActorTask taskToBeStopped = sipClientTestTasks.get( actorId);
        if(logger.isDebugEnabled()) logger.debug("SipClientTest End of Actor: "+actorId);
        taskToBeStopped.clientActorFinished();
      }
    }else{
      
      stopAllActors();
      
    }
  }
  /**
   * Stop All Actors of this test
   *
   */
  private void stopAllActors(){
    
    logger.error("\n\n---------------  SipClientTest Force Stop All Actors  ---------------\n\n");
    //  Wait for test threads execution
    Iterator<String> iter = sipClientTestTasks.keySet().iterator();
    
    while (iter.hasNext()){
      ClientActorTask actorTask = sipClientTestTasks.get( iter.next());
      actorTask.stopClientActor();
    }
  }
  /**
   * get the SipClientTest Id
   * @return the SipClientTest Id
   */
  public String getSipClientTestId ()
  {
    return sipClientTestId;
  }
  /**
   * Set the SipClientTest Id
   * @param sipClientTestId Specifies the SipClientTest Id
   */
  public void setSipClientTestId ( String sipClientTestId)
  {
    this.sipClientTestId = sipClientTestId;
  }
  /**
   * Get the sip Client Test List Report
   * @return the sip Client Test List Report
   */
  public SipClientTestListReport getSipClientTestListReport(){
    return sipClientTestListReport;
  }
  
  /**
   * Get the sip Test Call Flow
   * @return the sip Test Call Flow
   */
  public SipTestCallFlow getSipTestCallFlow(){
    return sipTestCallFlow;
  }
}
