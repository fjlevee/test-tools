package com.fjl.test.tools.sip.call;

import java.util.EventObject;

public interface SipStackEventListener {
  
  /**
   * Handle one Event occured on the sip Stack
   * @param event specifies the Event.
   * @throws SipClientStackException
   */
  public void handleSipStackEvent(EventObject event)throws SipClientStackException;
}
