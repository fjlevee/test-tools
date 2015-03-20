package com.fjl.test.xml.parser.elements;

import com.fjl.test.xml.parser.http.HttpParserUtils;

import fj_levee.test.parser.SipClientParserException;
import fj_levee.test.parser.SipTestCallFlowUtils;

public class ParamElement {
    private static final String PARAM_ELEMENT = "param";

    /**
     * Param Element name
     */
    private String name;

    /**
     * Param Element value
     */
    private String value;

    /**
     * 
     * Constructor of HttpRequestElement.
     */
    public ParamElement() {
        name = null;
        value = null;
    }

    /**
     * 
     * @param sipTestCallFlowAction
     * @param xmlCallFlowSubElement
     * @throws SipClientParserException
     */
    public void parseElement(HttpRequestElement sipTestCallFlowAction,
            org.w3c.dom.Node xmlCallFlowSubElement)
            throws SipClientParserException {

        // Get the sipTestCallFlowAction attributes
        org.w3c.dom.NamedNodeMap xmlCallFlowNodesAttributes = xmlCallFlowSubElement
                .getAttributes();

        // Param name.
        try {
            String name = xmlCallFlowNodesAttributes.getNamedItem(
                    SipTestCallFlowUtils.NAME_ATTRIBUTE).getNodeValue();
            this.name = name;
        } catch (NullPointerException e) {
            throw new SipClientParserException(
                    "ParamElement.parseElement: name attribute can not be null");
        }
        // Param value.
        try {
            String value = xmlCallFlowNodesAttributes.getNamedItem(
                    SipTestCallFlowUtils.VALUE_ATTRIBUTE).getNodeValue();
            this.value = value;
            // Check if the value is a SIP URI
            if ((value.startsWith(HttpParserUtils.SIP_URI_START_STRING) || (value
                    .startsWith(HttpParserUtils.TEL_URI_START_STRING)))) {

                String actorId = SipTestCallFlowUtils.getUserIdentifier(value);
                this.value = HttpParserUtils.SIP_URI_CLIENT_START_STRING
                        + actorId;

            }

        } catch (NullPointerException e) {
            throw new SipClientParserException(
                    "ParamElement.parseElement: value attribute can not be null");
        }
        sipTestCallFlowAction.addHttpParameter(this);

    }

    /**
     * Get the param name
     * 
     * @return the param name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the param name
     * 
     * @param name
     *            specifies the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the param value
     * 
     * @return the param value
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the param value
     * 
     * @param value
     *            specifies the new value
     */
    public void setValue(String value) {
        this.value = value;
    }
}