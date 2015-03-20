package com.fjl.sip.test;

public interface SipClientUAListener
{
  /**
   * Notifies that the SipClientUA has received an Event
   * 
   * @param event Specifies the Event Received;
   * @return true if the Event has been handled by the listener
   */
  public boolean notifySipClientUAEvent ( SipClientUAEvent event);

}
