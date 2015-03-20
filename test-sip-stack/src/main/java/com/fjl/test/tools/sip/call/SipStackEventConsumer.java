package com.fjl.test.tools.sip.call;

import java.util.EventObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SipStackEventConsumer extends Thread {
    /**
     * Sip Event Lists to be consumed.
     */
    private SipStackEventList eventsList;
    /**
     * Sip Stack Event Listener => Sip Stack Event Listener Implementation
     * Object.
     */
    private SipStackEventListener eventListener;
    /**
     * Determines if the Sip Event Consumer is running or not.
     */
    private boolean isRunning;
    /**
   * 
   */
    private boolean isFirstStart;

    private Logger logger = LoggerFactory
            .getLogger(com.fjl.test.tools.sip.call.SipStackEventConsumer.class);

    /**
     * Constructor.
     * 
     * @param eventsList
     * @param eventListener
     */
    public SipStackEventConsumer(SipStackEventList eventsList,
            SipStackEventListener eventListener) {
        this.eventsList = eventsList;
        this.eventListener = eventListener;
        this.isRunning = false;
    }

    public void run() {
        isRunning = true;

        do {
            EventObject event = eventsList.getNextEvent();
            // sipEventListener.handleNewCall(sipCall);
            try {
                eventListener.handleSipStackEvent(event);
            } catch (SipClientStackException e) {
                logger.error(
                        "SipStackEventConsumer.run: SipClientStackException\n",
                        e);

            } catch (Throwable e) {
                logger.error("SipStackEventConsumer.run: Throwable Error\n", e);
            }
        } while (eventsList.hasEvents());

        isRunning = false;
    }

    /**
     * Check if the consumer is running
     * 
     * @return true if the Consumer is running
     */
    public boolean isRunning() {
        return isRunning;
    }

}
