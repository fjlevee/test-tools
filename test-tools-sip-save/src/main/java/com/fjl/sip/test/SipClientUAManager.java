/*
 * ---------------------------------------------------------------------------
 * Orange-FT/NSM/RD/MAPS/MMC
 * ---------------------------------------------------------------------------
 * Project    : Basic
 * Subproject : SipClientTester
 * 
 * Copyright Orange - France Telecom, All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Orange -
 * France Telecom. You shall not disclose such confidential information and 
 * shall use it only in accordance with the terms of the license agreement 
 * you entered into with Orange - France Telecom.
 * ---------------------------------------------------------------------------
 * Filename : SipClientUAManager.java
 * Created  : 17 mars 2010
 * Author   : ofli8276
 * ---------------------------------------------------------------------------
 * History :
 * 
 *
 * --------------------------------------------------------------------------- */
package com.fjl.sip.test;

import java.util.Vector;

import org.apache.log4j.Logger;

import com.fjl.sip.SipPhone;
import com.fjl.sip.call.SipClientStackException;
import com.fjl.sip.register.RegistrationTask;
import com.fjl.sip.test.configuration.SipTestProfileConfiguration;

/**
 * This class/interface SipClientUAManager ...
 * 
 * @author ofli8276
 * 
 * @version G0R0C0
 * @since G0R0C0
 */
public class SipClientUAManager
{
  /**
   * Singleton instance
   */
  private static SipClientUAManager instance;

  /**
   * Log4J Logger
   * 
   */
  private Logger logger = Logger.getLogger( com.fjl.sip.test.SipClientUAManager.class);

  /**
   * List of SipClientUA
   */
  private Vector<SipClientUA> sipClientUAs;

  /**
   * 
   * Constructor of SipClientUAManager. ...
   * 
   */
  private SipClientUAManager ()
  {
    sipClientUAs = new Vector<SipClientUA>();

  }

  /**
   * Get the SipClientUAManager
   * 
   * @return the SipClientUAManager
   */
  public static SipClientUAManager getInstance ()
  {
    if (instance == null)
    {
      instance = new SipClientUAManager();
    }
    return instance;
  }

  /**
   * Load the SipClientUA
   * 
   * @param sipTestProfileConfiguration Specifies the SipTestProfileConfiguration to be used for loading the Sip Client
   *          UAs
   * @throws SipClientStackException
   */
  public void loadSipClientUAs ( SipTestProfileConfiguration sipTestProfileConfiguration) throws SipClientStackException
  
  {
    if (logger.isDebugEnabled()) logger.debug( "SipClientUAManager.loadSipClientUAs: Start...");

    // First release the sip client UAs created before
    for (int i = 0; i < sipClientUAs.size(); i++)
    {
      SipClientUA sipClientUA = sipClientUAs.get( i);
      // TODO release the sip client ua
      if (logger.isTraceEnabled()) logger.trace( "SipClientUAManager.loadSipClientUAs: release a Sip UA");
    }

    // now erases the list of sip client UAs from the sipClientUAs vector
    sipClientUAs.clear();
    if (logger.isTraceEnabled()) logger.trace( "SipClientUAManager.loadSipClientUAs: Start Loading the Sip Client UAs");
    for (int i = 0; i < sipTestProfileConfiguration.getSipProfiles().size(); i++)
    {
      SipClientUA sipClientUA = new SipClientUA();
      if (logger.isTraceEnabled()) logger.trace( "SipClientUAManager.loadSipClientUAs: Creating a Sip Client UAs with profile: "+ sipTestProfileConfiguration.getSipProfiles().get( i).getProfileName());
      // Create the Sip Phones with the SipClient UA and the Sip Profiles
      SipPhone phone = new SipPhone( sipClientUA);
      phone.setSipProfile( sipTestProfileConfiguration.getSipProfiles().get( i));
      sipClientUA.setSipPhone( phone);
      sipClientUAs.add( sipClientUA);
    }

  }
  
  /**
  * Register the SipClientUA
  * 
  *
  */
 public boolean  registerSipClientUAs () throws SipClientStackException{
   if (logger.isDebugEnabled()) logger.debug( "SipClientUAManager.registerSipClientUAs: Start...");
   
   Vector<RegistrationTask> registrationTasks = new Vector<RegistrationTask>();
   for(SipClientUA sipClientUA:sipClientUAs){
     if(!sipClientUA.getSipPhone().isRegistered()){
       if (logger.isDebugEnabled()) logger.debug( "SipClientUAManager.registerSipClientUAs: Sip Phone Not Registered with user name: "+sipClientUA.getSipPhone().getSipLayer().getUserName());
       RegistrationTask registrationTask = new RegistrationTask( true, sipClientUA.getSipPhone(), null);
       registrationTasks.add( registrationTask);
     }
   }
   // Check if there is Some Registration Task to be executed
   if(registrationTasks.size()==0){
     if (logger.isDebugEnabled()) logger.debug( "SipClientUAManager.registerSipClientUAs: Not Registration Task to be executed => Return true");
     return true;
   }else{
     if (logger.isDebugEnabled()) logger.debug( "SipClientUAManager.registerSipClientUAs: Registration Task number to be executed: "+registrationTasks.size());
     for (RegistrationTask registrationTask: registrationTasks){
       registrationTask.start();
     }
     if (logger.isDebugEnabled()) logger.debug( "SipClientUAManager.registerSipClientUAs: Registration Tasks started");
     
     for (RegistrationTask registrationTask:registrationTasks){
       try{
       registrationTask.join();
       }catch (InterruptedException e) {
         if (logger.isDebugEnabled()) logger.debug( "SipClientUAManager.registerSipClientUAs InterruptedException:"+e.getMessage());
      }
     }
     
     // all registration task has been done check now if the Registrations have succeeded.
     for(RegistrationTask registrationTask:registrationTasks){
       if (logger.isDebugEnabled()) logger.debug( "SipClientUAManager.registerSipClientUAs: Registration Task status: "+registrationTask.getRegistrationStatusCode());
       if(registrationTask.getRegistrationStatusCode()!=200){
         if (logger.isDebugEnabled()) logger.debug( "SipClientUAManager.registerSipClientUAs: Registration Task status:  is not OK Failed to register sip phones => return false");
       }
     }
     return true;
   }
   
   
   
 }
  
  
  
  /**
   * Get a Sip Client UA in the List of the Current ready Sip UA.
   * 
   * @param index
   * @return a Sip Client UA
   */
  public SipClientUA getSipClientUA ( int index)
  {
    SipClientUA clientUA = sipClientUAs.get(index);
    if(clientUA.isUsed()){
      logger.warn("SipClientUAManager.getSipClientUA: The SipClientUA: "+clientUA.getSipPhone().getSipProfile().getProfileName()+ " is already used");
      return null;
    }
    if(logger.isDebugEnabled()) logger.debug( "SipClientUAManager.getSipClientUA: The SipClientUA returned is: "+clientUA.getSipPhone().getSipProfile().getProfileName());
    clientUA.setUsed( true);
    return clientUA;

  }
}
