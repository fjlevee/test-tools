package com.fjl.test.xml.parser.elements;

import com.fjl.test.sip.SipTestTools;

import fj_levee.callflow.xml.configuration.XmlElementsConfiguration;
import fj_levee.test.parser.SipClientParserException;
import fj_levee.test.parser.SipTestCallFlow;
import fj_levee.test.parser.SipTestCallFlowAction;
import fj_levee.test.parser.SipTestCallFlowActor;
import fj_levee.test.parser.SipTestCallFlowUtils;

public class SipResponseElement extends SipTestCallFlowAction {
    public static final String SIP_RESPONSE_ELEMENT = "response";

    /**
     * SIP_REQUEST_DESCRIPTION_ID
     */
    private static final String SIP_RESPONSE_DESCRIPTION_ID = "xml.call.flow.element.description.sip.response";

    /**
     * Sip Response Direction
     */
    private String directionString;

    /**
     * Sip Response method
     */
    private String method;

    /**
     * Sip Response code
     */
    private int code;

    /**
     * Sip Response CallID
     */
    private String callid;

    /**
     * Sip Response From Sip Uri
     */
    private String fromSipUri;

    /**
     * Sip Response To Sip Uri
     */
    private String toSipUri;

    /**
     * Attribute auto generated
     */
    private boolean auto = false;

    /**
     * Message Element
     */
    private MessageElement message;

    /**
     * 
     * Constructor of SipResponseElement.
     */
    public SipResponseElement() {
        super(SIP_RESPONSE_ELEMENT);
    }

    /**
     * 
     * @see com.fjl.sip.test.parser.elements.SipTestCallFlowAction#parseSipTestCallFlowAction(com.fjl.sip.test.parser.SipTestCallFlow,
     *      org.w3c.dom.Node, int)
     */
    @Override
    public boolean parseSipTestCallFlowAction(SipTestCallFlow sipTestCallFlow,
            org.w3c.dom.Node siptestCallFlowAction, int sipTestActionId)
            throws SipClientParserException {
        if (logger.isTraceEnabled())
            logger.trace("SipResponseElement.parseSipTestCallFlowAction Start");
        this.sipTestActionId = sipTestActionId;
        // Get the siptestCallFlowAction attributes
        org.w3c.dom.NamedNodeMap sipTestCallFlowNodesAttributes = siptestCallFlowAction
                .getAttributes();
        // Auto attribute
        try {
            String autoString = sipTestCallFlowNodesAttributes.getNamedItem(
                    SipTestCallFlowUtils.AUTO_ATTRIBUTE).getNodeValue();
            if (SipTestCallFlowUtils.TRUE.equals(autoString)) {
                auto = true;
            }
        } catch (NullPointerException e) {
            // auto is false
        }

        // Sip Response direction
        try {
            directionString = sipTestCallFlowNodesAttributes.getNamedItem(
                    SipTestCallFlowUtils.DIRECTION_ATTRIBUTE).getNodeValue();

        } catch (NullPointerException e) {
            throw new SipClientParserException(
                    "SipResponseElement.parsesiptestCallFlowAction: direction attribute can not be null");
        }
        // Sip Response method
        try {
            method = sipTestCallFlowNodesAttributes.getNamedItem(
                    SipTestCallFlowUtils.METHOD_ATTRIBUTE).getNodeValue();
        } catch (NullPointerException e) {
            throw new SipClientParserException(
                    "SipResponseElement.parsesiptestCallFlowAction: method attribute can not be null");
        }
        // Sip Response code
        try {
            String codeString = sipTestCallFlowNodesAttributes.getNamedItem(
                    SipTestCallFlowUtils.CODE_ATTRIBUTE).getNodeValue();
            if (codeString != null) {
                this.code = Integer.parseInt(codeString);
            }
        } catch (NullPointerException e) {
            throw new SipClientParserException(
                    "SipResponseElement.parsesiptestCallFlowAction: code attribute can not be null");
        }

        // Sip Response time
        try {
            String timeAsString = sipTestCallFlowNodesAttributes.getNamedItem(
                    SipTestCallFlowUtils.TIME_ATTRIBUTE).getNodeValue();
            if (timeAsString != null) {
                this.time = Long.parseLong(timeAsString);
            }
        } catch (NullPointerException e) {
            // not present
        }

        // Sip Response Call ID
        try {
            callid = sipTestCallFlowNodesAttributes.getNamedItem(
                    SipTestCallFlowUtils.CALL_ID_ATTRIBUTE).getNodeValue();
        } catch (NullPointerException e) {
            throw new SipClientParserException(
                    "SipResponseElement.parsesiptestCallFlowAction: callid attribute can not be null");
        }
        // Sip Response From
        try {
            fromSipUri = sipTestCallFlowNodesAttributes.getNamedItem(
                    SipTestCallFlowUtils.FROM_ATTRIBUTE).getNodeValue();
        } catch (NullPointerException e) {
            throw new SipClientParserException(
                    "SipResponseElement.parsesiptestCallFlowAction: fromSipUri attribute can not be null");
        }
        // Sip Response To
        try {
            toSipUri = sipTestCallFlowNodesAttributes.getNamedItem(
                    SipTestCallFlowUtils.TO_ATTRIBUTE).getNodeValue();
        } catch (NullPointerException e) {
            throw new SipClientParserException(
                    "SipResponseElement.parsesiptestCallFlowAction: toSipUri attribute can not be null");
        }

        // Parse Sip Response Element Children
        // Retrieve the Http request Element Children
        org.w3c.dom.NodeList children = siptestCallFlowAction.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            org.w3c.dom.Node child = children.item(i);
            if (child.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                if (SipTestCallFlowUtils.MESSAGE_ELEMENT == child.getNodeName()) {
                    MessageElement element = new MessageElement();
                    element.parseElement(this, child);
                }

            }
        }
        // Check now the direction to add an actor if necessary
        SipTestCallFlowActor callFlowActor = null;

        this.direction = SipTestTools.getMessageDirection(directionString);
        if (direction == ActionDirection.TO_SERVER) {

            // response received by the B2B Component search if the to Uri actor
            // has been created yet;
            String actorId = SipTestCallFlowUtils.getUserIdentifier(toSipUri);

            callFlowActor = sipTestCallFlow.getSipTestCallFlowActor(actorId);
            if (callFlowActor == null) {
                // Check if the request is not destinated to a network component
                if (XmlElementsConfiguration.getActorForId(actorId) != null) {
                    if (logger.isDebugEnabled())
                        logger.debug("SipResponseElement.parseSipTestCallFlowAction: this response is an internal network request => Don't add it to the list of actions");
                    return false;
                }
                callFlowActor = new SipTestCallFlowActor(actorId,
                        sipTestCallFlow.getNextCurrentUEActorId(), toSipUri,
                        true);
                sipTestCallFlow.addSipTestCallFlowActor(callFlowActor);
            }
            // The SipTestCallFlowActor Actor is the UE with toSipUri
            setSipTestCallFlowActor(callFlowActor);
        } else {

            // response sent search if the from Uri actor has been created yet;
            String actorId = SipTestCallFlowUtils.getUserIdentifier(fromSipUri);
            callFlowActor = sipTestCallFlow.getSipTestCallFlowActor(actorId);
            if (callFlowActor == null) {
                // Check if the request is not destinated to a network component
                if (XmlElementsConfiguration.getActorForId(actorId) != null) {
                    if (logger.isDebugEnabled())
                        logger.debug("SipResponseElement.parseSipTestCallFlowAction: this response is an internal network request => Don't add it to the list of actions");
                    return false;
                }
                callFlowActor = new SipTestCallFlowActor(actorId,
                        sipTestCallFlow.getNextCurrentUEActorId(), fromSipUri,
                        true);
                sipTestCallFlow.addSipTestCallFlowActor(callFlowActor);
            }
            // The SipTestCallFlowActor Actor is the UE with fromSipUri
            setSipTestCallFlowActor(callFlowActor);

        }
        // Add this action in the actor actions' list
        actor.addSipTestCallFlowAction(this);
        if (logger.isTraceEnabled())
            logger.trace("SipResponseElement.parseSipTestCallFlowAction End");
        return true;
    }

    /**
     * Get the Sip Response method
     * 
     * @return the Sip Response method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Set the Sip Response method
     * 
     * @param method
     *            specifies the new Sip Response method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Get the Sip Response code
     * 
     * @return the Sip Response code
     */
    public int getCode() {
        return code;
    }

    /**
     * Set the Sip Response code
     * 
     * @param code
     *            specifies the new Sip Response code
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Get the Sip Response callid
     * 
     * @return the Sip Response callid
     */
    public String getCallid() {
        return callid;
    }

    /**
     * Set the Sip Response callid
     * 
     * @param callid
     *            specifies the new Sip Response callid
     */
    public void setCallid(String callid) {
        this.callid = callid;
    }

    /**
     * Get the Sip Response From sip uri
     * 
     * @return the Sip Response From sip uri
     */
    public String getFromSipUri() {
        return fromSipUri;
    }

    /**
     * Set the Sip Response From sip uri
     * 
     * @param fromSipUri
     *            specifies the new Sip Response From sip uri
     */
    public void setFromSipUri(String fromSipUri) {
        this.fromSipUri = fromSipUri;
    }

    /**
     * Get the Sip Response To sip uri
     * 
     * @return the Sip Response To sip uri
     */
    public String getToSipUri() {
        return toSipUri;
    }

    /**
     * Set the Sip Response To sip uri
     * 
     * @param toSipUri
     *            specifies the new Sip Response To sip uri
     */
    public void setToSipUri(String toSipUri) {
        this.toSipUri = toSipUri;
    }

    /**
     * Get the Sip Message contained in this Element
     * 
     * @return the Sip Message contained in this Element
     */
    public String getSipMessageAsString() {
        StringBuffer sipMessageBuffer = new StringBuffer();
        if (message != null) {

            sipMessageBuffer.append(message.getSipMessage(method));
        }
        return sipMessageBuffer.toString();
    }

    public boolean isAuto() {
        return auto;
    }

    /**
     * 
     * @param message
     */
    public void setMessageElement(MessageElement message) {
        this.message = message;
    }

    /**
     * Get the Message
     * 
     * @return the message
     */
    public MessageElement getMessageElement() {
        return message;
    }

    /**
     * 
     * @see com.fjl.sip.test.parser.elements.SipTestCallFlowAction#getActionAsString()
     */
    @Override
    public String getActionAsString() {
        return "SipResponse, Method: " + method + ", Status: " + code
                + ", Direction: " + direction;
    }

}