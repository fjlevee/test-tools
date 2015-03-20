package com.fjl.test.xml.parser.elements;

import fj_levee.test.parser.SipClientParserException;
import fj_levee.test.parser.SipTestCallFlowUtils;

public class ResponseUriElement {
    public static final String RESPONSE_URI_ELEMENT = "responseuri";

    /**
     * ResponseUriElement value
     */
    private String value;

    /**
     * 
     * Constructor of ResponseUriElement.
     */
    public ResponseUriElement() {
        value = null;
    }

    /**
     * parseElement
     * 
     * @param messageElement
     * @param responseUriElement
     * @throws SipClientParserException
     */
    public void parseElement(MessageElement messageElement,
            org.w3c.dom.Node responseUriElement)
            throws SipClientParserException {

        // Get the xmlCallFlowElement attributes
        org.w3c.dom.NamedNodeMap xmlCallFlowNodesAttributes = responseUriElement
                .getAttributes();

        // Param name.
        try {
            String value = xmlCallFlowNodesAttributes.getNamedItem(
                    SipTestCallFlowUtils.VALUE_ATTRIBUTE).getNodeValue();
            this.value = value;
        } catch (NullPointerException e) {
            throw new SipClientParserException(
                    "ResponseUriElement.parseXmlCallFlowElement: name attribute can not be null");
        }
        messageElement.setResponseUri(this);
    }

    /**
     * Get the response uri value
     * 
     * @return the response uri value
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the response uri value
     * 
     * @param value
     *            specifies the new value
     */
    public void setValue(String value) {
        this.value = value;
    }
}