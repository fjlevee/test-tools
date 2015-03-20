package com.fjl.test.tools.sip.actions;

import javax.sip.ResponseEvent;

import com.fjl.test.tools.ActionReport;
import com.fjl.test.tools.ActionReport.ActionResult;
import com.fjl.test.tools.sip.SipActor;
import com.fjl.test.tools.sip.SipClientUAEvent;
import com.fjl.test.tools.xml.parser.elements.SipResponseElement;

public class ReceiveSipResponseAction extends SipAction {
    /**
     * Method Awaited
     */
    private String method;

    /**
     * status Awaited
     */
    private int statusCode;

    /**
     * 
     * Constructor of ReceiveSipResponseAction.
     * 
     * @param sipActor
     * @param responseElement
     * @param timeToWait
     */
    public ReceiveSipResponseAction(SipActor sipActor,
            SipResponseElement responseElement, long timeToWait) {
        super(sipActor, responseElement.getCallid(), responseElement
                .getSipTestid(), timeToWait);
        this.method = responseElement.getMethod();
        this.statusCode = responseElement.getCode();

    }

    @Override
    public void executeAction() {
        try {
            if (logger.isDebugEnabled())
                logger.debug("ReceiveSipResponseAction.executeAction: Sip ClientActor: "
                        + sipActor.getId()
                        + ", Action Id: "
                        + id
                        + ",Max Time To Wait: "
                        + timeToWait
                        + ", SIP Response Method: "
                        + method
                        + ", SIP response status: " + statusCode);

            // Check if the response awaited is a CANCEL response

            /*
             * if (SipConstants.CANCEL.equals( method)) { // Yes the Sip Layer
             * will never notify the Sip UA of the ACK => No need to wait for.
             * notifySipClientTest( new SipClientTestReportEvent(
             * ReportEventType.ACTION_SUCCESS, id, sipActor
             * .getClientActorId(), isLastAction(), "ReceiveSipResponseAction "
             * + method + " (" + statusCode + ")","OK (auto received)"));
             * return;
             * 
             * }
             */

            SipClientUAEvent eventReceived = null;

            WaitForEventAction waitAction = new WaitForEventAction(
                    sipActor.getSipClientUA());
            if (logger.isTraceEnabled())
                logger.debug("getSipClientActorId.executeAction: Sip ClientActor: "
                        + sipActor.getId()
                        + ", Action Id: "
                        + id
                        + ", Start...");
            waitAction.waitForEvent(timeToWait, ResponseEvent.class, method,
                    scenarioSipCallId, statusCode);
            if (logger.isTraceEnabled())
                logger.debug("ReceiveSipResponseAction.executeAction: Sip ClientActor: "
                        + sipActor.getId()
                        + ", Action Id: "
                        + id
                        + ", waitAction ended.");
            eventReceived = waitAction.getSipClientUAEvent();

            if (eventReceived != null) {
                if (logger.isTraceEnabled())
                    logger.debug("ReceiveSipResponseAction.executeAction: Sip ClientActor: "
                            + sipActor.getId()
                            + ", Action Id: "
                            + id
                            + ", Event Received: " + eventReceived.getMethod());
                // Check the event received is a SipResponseEvent
                try {
                    ResponseEvent responseEvent = (ResponseEvent) eventReceived
                            .getEvent();
                    String methodReceived = eventReceived.getMethod();
                    if (method.equals(methodReceived)) {
                        // Same method as awaited now check the code
                        int statusReceived = responseEvent.getResponse()
                                .getStatusCode();
                        if (statusReceived == statusCode) {

                            notifySipClientTest(new ActionReport(
                                    ActionResult.ACTION_SUCCESS, id,
                                    sipActor.getId(), isLastAction(),
                                    "ReceiveSipResponseAction " + method + " ("
                                            + statusCode + ")", "OK"));

                        } else {
                            if (logger.isTraceEnabled())
                                logger.debug("ReceiveSipResponseAction.executeAction: Sip ClientActor: "
                                        + sipActor.getId()
                                        + ", Action Id: "
                                        + id
                                        + ", Sip Response received status: "
                                        + statusReceived
                                        + ", status awaited: "
                                        + statusCode + " => Action Failed");

                            notifySipClientTest(new ActionReport(
                                    ActionResult.ACTION_FAILED, id,
                                    sipActor.getId(), isLastAction(),
                                    "ReceiveSipResponseAction " + method + " ("
                                            + statusCode + ")",
                                    "Error: Status Code awaited: " + statusCode
                                            + ", Status Code received: "
                                            + statusReceived));

                        }

                    } else {
                        if (logger.isTraceEnabled())
                            logger.debug("ReceiveSipResponseAction.executeAction: Sip ClientActor: "
                                    + sipActor.getId()
                                    + ", Action Id: "
                                    + id
                                    + ", Sip Response received on method: "
                                    + methodReceived
                                    + ", method awaited: "
                                    + method + " => Action Failed");
                        notifySipClientTest(new ActionReport(
                                ActionResult.ACTION_FAILED, id,
                                sipActor.getId(), isLastAction(),
                                "ReceiveSipResponseAction " + method + " ("
                                        + statusCode + ")",
                                "Error: SIP Method awaited: " + method
                                        + ", SIP Method received: "
                                        + eventReceived.getMethod()));

                    }
                } catch (ClassCastException e) {
                    if (logger.isTraceEnabled())
                        logger.debug("ReceiveSipResponseAction.executeAction: ClassCastException Sip ClientActor: "
                                + sipActor.getId()
                                + ", Action Id: "
                                + id
                                + ", Event Received is not a ResponseEvent => Action Failed");
                    notifySipClientTest(new ActionReport(
                            ActionResult.ACTION_FAILED, id,
                            sipActor.getId(), isLastAction(),
                            "ReceiveSipResponseAction " + method + " ("
                                    + statusCode + ")",
                            "Error: SIP Method awaited: " + method
                                    + ", Event Received is not a ResponseEvent"));

                }

            } else {
                if (logger.isTraceEnabled())
                    logger.debug("ReceiveSipResponseAction.executeAction: Sip ClientActor: "
                            + sipActor.getId()
                            + ", Action Id: "
                            + id
                            + ", No Event Received => Action Failed");

                notifySipClientTest(new ActionReport(
                        ActionResult.ACTION_FAILED, id,
                        sipActor.getId(), isLastAction(),
                        "ReceiveSipResponseAction " + method + " ("
                                + statusCode + ")", "Error: No Event Received"));
            }
        } catch (Throwable e) {
            logger.error("ReceiveSipResponseAction.executeAction ERROR: Sip ClientActor: "
                    + sipActor.getId()
                    + ", Action Id: "
                    + id
                    + ", Error:\n " + e.getMessage());

            notifySipClientTest(new ActionReport(
                    ActionResult.ACTION_FAILED, id, sipActor.getId(),
                    isLastAction(), "ReceiveSipResponseAction " + method + " ("
                            + statusCode + ")", "Error: " + e.getMessage()));
        }
    }

    /**
     * Get the Sip Status code awaited.
     * 
     * @return the Status code awaited.
     */
    public int getStatusCode() {
        return statusCode;
    }
}
