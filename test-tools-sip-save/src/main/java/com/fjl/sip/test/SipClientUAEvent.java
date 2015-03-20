package com.fjl.sip.test;

import java.util.EventObject;

import javax.sip.ResponseEvent;

import org.apache.log4j.Logger;

public class SipClientUAEvent {
	/**
	 * Log4j Logger
	 */
	private Logger logger = Logger.getLogger(com.fjl.sip.test.SipClientUAEvent.class);
  /**
   * Call ID
   */
  private String callId;
  /**
   * Method Can be a Sip Method (for Request or response method) or a TIME_OUT_EVENT
   */
  private String method;
  /**
   * The real event received from the Sip Stack.
   */
  private EventObject event;
  
	/**
   * 
   * Constructor of SipClientUAEvent.
   * @param callId Specifies the Call ID on which the Event Has occured
   * @param method Specifes the Method. Can be a Sip Method (for Request or response method) or a TIME_OUT_EVENT
   * @param event Specifies the real Event.
	 */
  public SipClientUAEvent(String callId,String method,EventObject event){
    this.callId = callId;
    this.event = event;
    this.method = method;
    
  }
  /**
   * Checks the Current Event parameters 
   * @param callId
   * @param Specifies the Call ID on which the Event Has occured
   * @param method Specifes the Method. Can be a Sip Method (for Request or response method) or a TIME_OUT_EVENT
   * @param eventClass Specifies the Event Class.
   * @param statusCode Specifies the Status code in case of ResponseEvent awaited => Must be set to -1 in case of the Event awaited is not a ResponseEvent
   * @return
   */
  public boolean checkEventParameters(String callId,String method,Class eventClass,int statusCode){
    if(eventClass!=ResponseEvent.class){
      return ((this.callId.equals( callId))&&(this.method.equals( method))&&(this.event.getClass()== eventClass));
    }else{
      return ((this.callId.equals( callId))&&(this.method.equals( method))&&(this.event.getClass()== eventClass)&&(((ResponseEvent)this.event).getResponse().getStatusCode()==statusCode)); 
    }
    
  }
  /**
   * Get the Call Id of the Sip Dialog on which the event occurred. 
   * @return the Call Id of the Sip Dialog on which the event occurred
   */
  public String getCallId ()
  {
    return callId;
  }
  
  /**
   * Get the Event Object (SipRequestEvent, SipResponse Event or TimeoutEvent)
   * @return the Event Object
   */
  public EventObject getEvent ()
  {
    return event;
  }

  /**
   * Get the Method (Sip Method for SipRequestEvent or SipResponsEvent and TIME_OUT String for TimeOut);
   * @return
   */
  public String getMethod ()
  {
    return method;
  }

  public void setMethod ( String method)
  {
    this.method = method;
  }
	/**
   * Get a String representation of This Event
   * @return A String representation of This Event
	 */
  public String getEventAsString(){
    return "Call Id: "+callId+", Method: "+method+", Class: "+event.getClass().getName();
  }
}
