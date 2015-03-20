package com.fjl.test.tools.sip.call;

import java.util.EventObject;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SipStackEventList {
	/**
	 * List of Sip events received.
	 */
	private ConcurrentLinkedQueue<EventObject> eventList;
	/**
	 * Sip Event consumer thread.
	 */
	private SipStackEventConsumer eventConsumer;
  /**
   * Sip Event consumer thread.
   */
  private SipStackEventListener eventListener;
	
	/**
	 * Default constructor.
	 * @param eventListener Specifies the SipStackEventListener
	 */
	public SipStackEventList(SipStackEventListener eventListener){
		eventList = new ConcurrentLinkedQueue<EventObject>();
    this.eventListener = eventListener;
		eventConsumer = null;
	}
	/**
	 * Add an Event in the eventList
	 * @param event the new event to be added.
	 */
	public void addEvent(EventObject event){
		eventList.add(event);
    if((eventConsumer==null)||(!eventConsumer.isAlive())){
      eventConsumer = new SipStackEventConsumer(this,eventListener);
			eventConsumer.start();
		}
	}
	/**
	 * hasEvents determines if the List contains Events.
	 * @return true if the List has events.
	 */
	public boolean hasEvents(){
		return (eventList.size()!=0);
	}
	/**
	 * Get the next Event to be Hanlded
	 * @return the next event to be handled.
	 */
	public EventObject getNextEvent(){
		return eventList.poll();
		
	}
}
