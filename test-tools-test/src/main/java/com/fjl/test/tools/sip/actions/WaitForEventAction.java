package com.fjl.test.tools.sip.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fjl.test.tools.sip.SipActor;
import com.fjl.test.tools.sip.SipClientUAEvent;
import com.fjl.test.tools.sip.SipClientUAListener;

public class WaitForEventAction implements SipClientUAListener {
    /**
     * SipClientUAEvent
     */
    private SipClientUAEvent sipClientUAEvent;

    /**
     * Event Type awaited
     */
    private Class eventType;

    /**
     * Method awaited
     */
    private String method;

    /**
     * Sip Client UA
     */
    private SipActor sipClientUA;

    /**
     * Log4J Logger
     */
    private Logger logger = LoggerFactory
            .getLogger(com.fjl.test.tools.sip.actions.WaitForEventAction.class);

    /**
     * WaitForEventAction
     */
    public WaitForEventAction(SipActor sipClientUA) {
        super();
        if (logger.isTraceEnabled())
            logger.trace("WaitForEventAction.constructor: Pointer of WaitForEventAction is: "
                    + this.toString() + ", Hash code is: " + this.hashCode());
        sipClientUAEvent = null;
        this.sipClientUA = sipClientUA;
    }

    /**
     * waitForEvent
     * 
     * @param timeout
     *            specifies the Time Out in ms
     * @param eventType
     *            Specifies the EventType awaited
     * @param method
     *            specifies the SIP method awaited
     * @param sipClientUA
     *            Specifies the Sip Client UA
     * @param callId
     *            Specifies the CallID
     * @param statusCode
     *            Specifies the status code in case of ResponseEvent awaited.
     *            Set to -1 if not a RequestEvent awaited.
     * @throws Exception
     */
    public void waitForEvent(long timeout, Class eventType, String method,
            String callId, int statusCode) throws Exception {
        if (logger.isTraceEnabled())
            logger.trace("WaitForEventAction.waitForEvent: Start, Pointer of WaitForEventAction is: "
                    + this.toString()
                    + ", Hash code is: "
                    + this.hashCode()
                    + "timeout is: " + timeout);

        synchronized (this) {
            // Set this object as the Listener
            sipClientUA.setListener(this);
            this.method = method;
            this.eventType = eventType;
            if (sipClientUAEvent == null) {
                // Check if the Event Has already been received
                sipClientUAEvent = sipClientUA.checkIfEventAlreadyReceived(
                        callId, method, eventType, statusCode);
            }
            if (sipClientUAEvent == null) {
                // just wait for 100 ms
                this.wait(100);
                // Check if the Event Has already been received
                if (sipClientUAEvent == null)
                    sipClientUAEvent = sipClientUA.checkIfEventAlreadyReceived(
                            callId, method, eventType, statusCode);
                if (sipClientUAEvent != null) {
                    if (logger.isTraceEnabled())
                        logger.trace("WaitForEventAction.waitForEvent: Event received during the first waiting of 100 ms");
                }
            }
            if (sipClientUAEvent == null) {
                // just wait for 1000 ms
                this.wait(1000);
                // Check if the Event Has already been received
                if (sipClientUAEvent == null)
                    sipClientUAEvent = sipClientUA.checkIfEventAlreadyReceived(
                            callId, method, eventType, statusCode);
                if (sipClientUAEvent != null) {
                    if (logger.isTraceEnabled())
                        logger.trace("WaitForEventAction.waitForEvent: Event received during the second waiting of 1000 ms");
                }
            }
            if (sipClientUAEvent == null) {
                // just wait for 1000 ms
                this.wait(1000);
                // Check if the Event Has already been received
                if (sipClientUAEvent == null)
                    sipClientUAEvent = sipClientUA.checkIfEventAlreadyReceived(
                            callId, method, eventType, statusCode);
                if (sipClientUAEvent != null) {
                    if (logger.isTraceEnabled())
                        logger.trace("WaitForEventAction.waitForEvent: Event received during the third waiting of 2000 ms");
                }
            }
            if (sipClientUAEvent == null) {
                if (logger.isTraceEnabled())
                    logger.trace("WaitForEventAction.waitForEvent: Start waiting: timeout is: "
                            + timeout);
                this.wait(timeout);
                if (logger.isTraceEnabled())
                    logger.trace("WaitForEventAction.waitForEvent: Stop waiting");
            }
            // Remove this object from the Listener
            sipClientUA.setListener(null);
        }
    }

    private void notifyWaitForEventAction() {

    }

    /**
     * SipClientUAListener implementation
     * 
     * @see com.fjl.test.tools.sip.SipClientUAListener#notifySipClientUAEvent(com.fjl.test.tools.sip.SipClientUAEvent)
     */
    public boolean notifySipClientUAEvent(SipClientUAEvent sipClientUAEvent) {

        if (logger.isTraceEnabled())
            logger.trace("WaitForEventAction.notifySipClientUAEvent: "
                    + sipClientUAEvent.getEventAsString());
        if (logger.isTraceEnabled())
            logger.trace("WaitForEventAction.notifySipClientUAEvent: Wait For Event Type: "
                    + eventType + ", and method: " + method);

        synchronized (this) {

            if (logger.isTraceEnabled())
                logger.trace("WaitForEventAction.notifySipClientUAEvent: Start Notifying, Pointer of WaitForEventAction is: "
                        + this.toString()
                        + ", Hash code is: "
                        + this.hashCode());

            if (eventType == sipClientUAEvent.getEvent().getClass()) {
                // Same Class of event awaited
                if (method.equals(sipClientUAEvent.getMethod())) {
                    this.sipClientUAEvent = sipClientUAEvent;
                    this.notify();
                    if (logger.isTraceEnabled())
                        logger.trace("WaitForEventAction.notifySipClientUAEvent: Notified => Return True, Pointer of WaitForEventAction is: "
                                + this.toString()
                                + ", Hash code is: "
                                + this.hashCode());
                    // Remove this object from the Listener
                    sipClientUA.setListener(null);
                    return true;
                } else {
                    logger.warn("WaitForEventAction.notifySipClientUAEvent: Event ignored: Sip Event Method received: "
                            + sipClientUAEvent.getMethod()
                            + ", Sip Event Method awaited: " + method);
                }
            } else {
                logger.warn("WaitForEventAction.notifySipClientUAEvent: Event ignored: Event Class received: "
                        + sipClientUAEvent.getEvent().getClass()
                        + ", Event Class awaited: " + eventType);

            }

            return false;

        }
    }

    /**
     * Get the SipClientUAEvent receveid. Null if Time Out.
     * 
     * @return the sipClientUAEvent.
     */
    public SipClientUAEvent getSipClientUAEvent() {
        return sipClientUAEvent;
    }
}
