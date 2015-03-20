package com.fjl.test.xml.parser.http;

import java.util.Vector;

public class CallObject {
    /**
     * Call Key
     */
    private String key;

    /**
     * Call Id
     */
    private String id;

    /**
     * Call Url
     */
    private String url;

    public CallObject() {

    }

    /**
     * Parse the Call Elements
     * 
     * @param calls
     *            Specifies the List of Calls
     * @param callNode
     *            Specifies the Call Node
     */
    public void parseElements(Vector<CallObject> calls,
            org.w3c.dom.Node callNode) {

        // Get the CallObject attributes
        org.w3c.dom.NamedNodeMap callNodeAttributes = callNode.getAttributes();

        // Call Id Attribute
        try {
            String id = callNodeAttributes.getNamedItem(
                    HttpParserUtils.CALL_ID_ATTRIBUTE).getNodeValue();
            this.id = id;
        } catch (NullPointerException e) {
            this.id = "UNDEFINED";
        }
        // Call key Attribute
        try {
            String key = callNodeAttributes.getNamedItem(
                    HttpParserUtils.CALL_KEY_ATTRIBUTE).getNodeValue();
            this.key = key;
        } catch (NullPointerException e) {
            this.key = "UNDEFINED";
        }
        // Call url Attribute
        try {
            String url = callNodeAttributes.getNamedItem(
                    HttpParserUtils.CALL_URL_ATTRIBUTE).getNodeValue();
            this.url = url;
        } catch (NullPointerException e) {
            this.url = null;
        }
        calls.add(this);
    }

    /**
     * Get the Call Id
     * 
     * @return the Call Id
     */
    public String getId() {
        return id;
    }

    /**
     * Get Call Key
     * 
     * @return the Call Key
     */
    public String getKey() {
        return key;
    }

    /**
     * Get the Call Url
     * 
     * @return the Call Url
     */
    public String getUrl() {
        return url;
    }
}
