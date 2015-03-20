package com.fjl.test.xml.parser.elements;

import fj_levee.test.parser.SipClientParserException;
import fj_levee.test.parser.SipTestCallFlowUtils;

public class RequestUriElement {
    public static final String REQUEST_URI_ELEMENT = "requesturi";

    /**
     * Param Element value
     */
    private String value;

    /**
     * 
     * Constructor of RequestUriElement.
     */
    public RequestUriElement() {
        value = null;
    }

    public void parseElement(MessageElement messageElement,
            org.w3c.dom.Node element) throws SipClientParserException {

        // Get the xmlCallFlowElement attributes
        org.w3c.dom.NamedNodeMap xmlCallFlowNodesAttributes = element
                .getAttributes();

        // Param name.
        try {
            String value = xmlCallFlowNodesAttributes.getNamedItem(
                    SipTestCallFlowUtils.VALUE_ATTRIBUTE).getNodeValue();
            this.value = value;
        } catch (NullPointerException e) {
            throw new SipClientParserException(
                    "RequestUriElement.parseXmlCallFlowElement: name attribute can not be null");
        }

        messageElement.setRequestUri(this);

    }

    /**
     * Get the request uri value
     * 
     * @return the request uri value
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the request uri value
     * 
     * @param value
     *            specifies the new value
     */
    public void setValue(String value) {
        this.value = value;
    }

}