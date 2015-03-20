package com.fjl.test.xml.parser.http;

import java.util.Vector;

public class ApplicationObject {

    private String applicationName;

    private Vector<CallObject> calls;

    public ApplicationObject() {
        calls = new Vector<CallObject>();

    }

    /**
     * Parse the Application Element
     * 
     * @param applicationNode
     */
    public void parseElements(org.w3c.dom.Node applicationNode) {
        // Get the ApplicationObject attributes
        org.w3c.dom.NamedNodeMap applicationNodeNodesAttributes = applicationNode
                .getAttributes();

        // Application Name
        String applicationName = null;
        try {
            applicationName = applicationNodeNodesAttributes.getNamedItem(
                    HttpParserUtils.APPLICATION_NAME_ATTRIBUTE).getNodeValue();
            this.applicationName = applicationName;
        } catch (NullPointerException e) {
            this.applicationName = "UNDEFINED";
        }

        // Retrieve the Call Nodes
        // Get the Sip Test Call Flow elements
        org.w3c.dom.NodeList applicationNodeChildren = applicationNode
                .getChildNodes();

        for (int i = 0; i < applicationNodeChildren.getLength(); i++) {
            org.w3c.dom.Node callsNode = applicationNodeChildren.item(i);
            if (callsNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {

                if (HttpParserUtils.CALLS_LIST_ELEMENT.equals(callsNode
                        .getNodeName())) {
                    org.w3c.dom.NodeList callsNodeChildren = callsNode
                            .getChildNodes();
                    for (int j = 0; j < callsNodeChildren.getLength(); j++) {
                        org.w3c.dom.Node callNode = callsNodeChildren.item(j);
                        if (callNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                            CallObject call = new CallObject();
                            call.parseElements(calls, callNode);
                        }
                    }
                }
            }
        }

    }

    /**
     * Get the Application Name
     * 
     * @return the Application Name
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * Get the Calls List
     * 
     * @return the Calls List
     */
    public Vector<CallObject> getCalls() {
        return calls;
    }

    /**
     * Get a CallObject with its key
     * 
     * @param key
     * @return the CallObject if found
     */
    public CallObject getCallWithKey(String key) {
        if (key != null) {
            for (int i = 0; i < calls.size(); i++) {
                if (key.equals(calls.get(i).getKey())) {
                    return calls.get(i);
                }
            }
        }
        return null;
    }

}
