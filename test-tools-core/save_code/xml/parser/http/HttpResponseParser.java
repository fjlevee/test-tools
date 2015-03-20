package com.fjl.test.xml.parser.http;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import fj_levee.test.parser.SipClientParserException;

public class HttpResponseParser {

    /**
     * Http Response Parser singleton instance
     */
    private static HttpResponseParser instance;

    /**
     * Log 4J logger
     */
    private Logger logger;

    /**
     * Default constructor
     */
    private HttpResponseParser() {
        this.logger = Logger
                .getLogger(com.fjl.test.xml.parser.http.HttpResponseParser.class);
    }

    /**
     * Get HttpResponseParser instance
     * 
     * @return the HttpResponseParser instance (build it if necessary)
     * @throws SipClientParserException
     */
    public static HttpResponseParser getInstance()
            throws SipClientParserException {
        if (instance == null) {
            instance = new HttpResponseParser();
            instance.init();
        }
        return instance;
    }

    /**
     * Init Method
     * 
     * @param applicationConfig
     *            specifies the application specific configuration
     * @throws SipClientParserException
     */
    public void init() throws SipClientParserException {
        if (logger.isDebugEnabled())
            logger.debug("HttpResponseParser.init: Start...");

        if (logger.isDebugEnabled())
            logger.debug("HttpResponseParser.init done. ");
    }

    /**
     * Parse the Http Response
     * 
     * @param httpResponseContent
     *            specifies the httpResponse body to be parsed
     * 
     * @return the Application Object created
     * @throws SipClientParserException
     * 
     */
    public ApplicationObject parseHttpResponse(String httpResponseContent)
            throws SipClientParserException {
        try {
            if (logger.isDebugEnabled())
                logger.debug("HttpResponseParser.parseHttpResponse: Start...");
            // Instanciate the XML parser
            DocumentBuilder parser = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document document = parser.parse(new InputSource(new StringReader(
                    httpResponseContent)));

            // Retrieve the Sip Test Call Flow root Nodes
            org.w3c.dom.NodeList sipTestCallFlowNodes = document
                    .getElementsByTagName(HttpParserUtils.APPLICATION_ELEMENT);
            org.w3c.dom.Node applicationNode = sipTestCallFlowNodes.item(0);

            if (applicationNode == null) {
                logger.error("APPLICATION NODE IS NULL !!!!!!!!!!!!");
            } else {
                logger.debug("APPLICATION NODE IS NOT NULL");
            }

            ApplicationObject application = new ApplicationObject();
            application.parseElements(applicationNode);

            if (logger.isDebugEnabled())
                logger.debug("HttpResponseParser.parseHttpResponse: End.");
            return application;
        } catch (Throwable e) {
            logger.error("HttpResponseParser.parseHttpResponse: Message,\n"
                    + e.getMessage());
            logger.error("HttpResponseParser.parseHttpResponse: Cause,\n"
                    + e.getCause());
            e.printStackTrace();
            logger.error(e.getStackTrace());
            logger.error(e);
            throw new SipClientParserException(
                    "HttpResponseParser.parseHttpResponse: \n" + e.getMessage());
        }

    }

}