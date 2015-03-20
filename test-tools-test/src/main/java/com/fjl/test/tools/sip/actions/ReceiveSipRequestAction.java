package com.fjl.test.tools.sip.actions;

import javax.sip.RequestEvent;

import com.fjl.test.tools.ActionReport;
import com.fjl.test.tools.Test;
import com.fjl.test.tools.ActionReport.ActionResult;
import com.fjl.test.tools.sip.SipActor;
import com.fjl.test.tools.sip.SipClientUAEvent;
import com.fjl.test.tools.sip.SipConstants;
import com.fjl.test.tools.xml.parser.elements.SipRequestElement;

public class ReceiveSipRequestAction extends SipAction {
    /**
     * Method Awaited
     */
    private String method;

    /**
     * 
     * Constructor of ReceiveSipRequestAction.
     * 
     * @param sipActor
     * @param requestElement
     * @param timeToWait
     */
    public ReceiveSipRequestAction(String actorId, String id,
            SipRequestElement requestElement) {
        super(actorId, id);
        this.method = requestElement.getMethod();

    }

    /**
     * 
     */
    @Override
    public ActionReport execute(Test test) {

        try {
            if (logger.isTraceEnabled())
                logger.debug("ReceiveSipRequestAction.executeAction: Actor Id: "
                        + actorId
                        + ", Action Id: "
                        + id
                        + ",Max Time To Wait: "
                        + timeToWait
                        + ", SIP Request Method: " + method);
            // Check if the request awaited is an ACK on an error message
            if ((SipConstants.ACK.equals(method))
                    && (previousMessageWasAnError)) {
                // Yes the Sip Layer will never notify the Sip UA of the ACK =>
                // No need to wait for.
                notifySipClientTest(new ActionReport(
                        ActionResult.ACTION_SUCCESS, id,
                        sipActor.getId(), isLastAction(),
                        "ReceiveSipRequestAction " + method, "OK"));
                return;
            }
            SipClientUAEvent eventReceived = null;
            WaitForEventAction waitAction = new WaitForEventAction(
                    sipActor.getSipClientUA());

            if (logger.isTraceEnabled())
                logger.debug("ReceiveSipRequestAction.executeAction: Sip ClientActor: "
                        + sipActor.getId()
                        + ", Action Id: "
                        + actionId
                        + ", Start...");

            waitAction.waitForEvent(timeToWait, RequestEvent.class, method,
                    scenarioSipCallId, -1);

            if (logger.isTraceEnabled())
                logger.debug("ReceiveSipRequestAction.executeAction: Sip ClientActor: "
                        + sipActor.getId()
                        + ", Action Id: "
                        + actionId
                        + ", waitAction ended.");
            eventReceived = waitAction.getSipClientUAEvent();

            if (eventReceived != null) {
                if (logger.isTraceEnabled())
                    logger.debug("ReceiveSipRequestAction.executeAction: Sip ClientActor: "
                            + sipActor.getId()
                            + ", Action Id: "
                            + actionId
                            + ", Event Received: " + eventReceived.getMethod());
                if (method.equals(eventReceived.getMethod())) {
                    if (SipConstants.INVITE.equals(eventReceived.getMethod())) {
                        sipActor.getTest().setSipCallKey(
                                eventReceived.getCallId());
                    }

                    // Action Success

                    notifySipClientTest(new ActionReport(
                            ActionResult.ACTION_SUCCESS, actionId,
                            sipActor.getId(), isLastAction(),
                            "ReceiveSipRequestAction " + method, "OK"));

                } else {
                    notifySipClientTest(new ActionReport(
                            ActionResult.ACTION_FAILED, actionId,
                            sipActor.getId(), isLastAction(),
                            "ReceiveSipRequestAction " + method,
                            "Error: SIP Method awaited: " + method
                                    + ", SIP Method received: "
                                    + eventReceived.getMethod()));
                }
            } else {
                if (logger.isTraceEnabled())
                    logger.trace("ReceiveSipRequestAction.executeAction: Sip ClientActor: "
                            + sipActor.getId()
                            + ", Action Id: "
                            + actionId
                            + ", No Event Received => Action Failed");
                notifySipClientTest(new ActionReport(
                        ActionResult.ACTION_FAILED, actionId,
                        sipActor.getId(), isLastAction(),
                        "ReceiveSipRequestAction " + method,
                        "Error: No Event Received"));

            }

        } catch (Throwable e) {
            logger.error("ReceiveSipResponseAction.executeAction ERROR: Sip ClientActor: "
                    + sipActor.getId()
                    + ", Action Id: "
                    + actionId
                    + ", Error:\n " + e.getMessage());

            notifySipClientTest(new ActionReport(
                    ActionResult.ACTION_FAILED, actionId, sipActor.getId(),
                    isLastAction(), "ReceiveSipRequestAction " + method,
                    "Error: " + e.getMessage()));
        }
    }

}
