package com.fjl.test.xml.parser.elements;

import fj_levee.test.parser.SipClientParserException;
import fj_levee.test.parser.SipTestCallFlow;
import fj_levee.test.parser.SipTestCallFlowAction;
import fj_levee.test.parser.SipTestCallFlowActor;
import fj_levee.test.parser.SipTestCallFlowUtils;

public class WaitElement extends SipTestCallFlowAction {
    public static final String WAIT_ELEMENT = "wait";

    /**
     * Attribute auto generated
     */
    private long timeToWait = 0;

    /**
     * 
     * Constructor of WaitElement.
     */
    public WaitElement() {
        super(WAIT_ELEMENT);
    }

    /**
     * 
     * @see com.fjl.sip.test.parser.elements.SipTestCallFlowAction#parseSipTestCallFlowAction(com.fjl.sip.test.parser.SipTestCallFlow,
     *      org.w3c.dom.Node, int)
     */
    @Override
    public boolean parseSipTestCallFlowAction(SipTestCallFlow sipTestCallFlow,
            org.w3c.dom.Node sipTestCallFlowElement, int sipTestActionId)
            throws SipClientParserException {
        if (logger.isTraceEnabled())
            logger.trace("WaitElement.parseSipTestCallFlowAction Start");

        this.sipTestActionId = sipTestActionId;
        // Get the SipTestCallFlowElement attributes
        org.w3c.dom.NamedNodeMap sipTestCallFlowNodesAttributes = sipTestCallFlowElement
                .getAttributes();

        // Time to wait attribute
        try {
            String timeToWaitString = sipTestCallFlowNodesAttributes
                    .getNamedItem(SipTestCallFlowUtils.TIME_TO_WAIT_ATTRIBUTE)
                    .getNodeValue();
            this.timeToWait = Long.parseLong(timeToWaitString);
            if (logger.isTraceEnabled())
                logger.trace("WaitElement.parseSipTestCallFlowAction Time To Wait is:"
                        + timeToWait + " ms.");
        } catch (NullPointerException e) {
            // auto is false
        }

        sipTestCallFlow.getSipTestCallFlowActor("waitactor");
        actor = sipTestCallFlow.getWaitSipTestCallFlowActor();
        // Checks if the Http Client has been created
        if (actor == null) {
            // not created => create it
            actor = new SipTestCallFlowActor(SipTestCallFlow.WAIT_ACTOR_NAME,
                    SipTestCallFlow.WAIT_ACTOR_NAME, false);
            sipTestCallFlow.addSipTestCallFlowActor(actor);
        }

        // Add this action in the actor actions' list
        actor.addSipTestCallFlowAction(this);
        if (logger.isTraceEnabled())
            logger.trace("WaitElement.parseSipTestCallFlowAction End");
        return true;
    }

    /**
     * Get the time to wait
     * 
     * @return the the time to wait
     */
    public long getTimeToWait() {
        return this.timeToWait;
    }

    /**
     * 
     * @see com.fjl.sip.test.parser.elements.SipTestCallFlowAction#getActionAsString()
     */
    @Override
    public String getActionAsString() {
        return "Time To Wait: " + timeToWait + " ms.";
    }

}