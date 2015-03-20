package com.fjl.test.tools.sip;

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;

public class SipClientTestManagerConfiguration {
    /**
     * Log4j logger
     */
    private Logger logger = Logger
            .getLogger(com.fjl.test.tools.sip.SipClientTestManagerConfiguration.class);

    /**
     * SipClientTestManagerConfiguration instance
     */
    private static SipClientTestManagerConfiguration instance;

    /**
     * SIP_CLIENT_TEST_LAUNCHER_PROPERTIES
     */
    private static String SIP_CLIENT_TEST_LAUNCHER_PROPERTIES = "SipClientTester.properties";

    /**
     * SIP_SERVERS_CONFIGURATION_FOLDER
     */
    public static String SIP_SERVERS_CONFIGURATION_FOLDER = "client.tester.sip.servers.configuration.folder";
    /**
     * SIP_SERVERS_CONFIGURATION_FOLDER
     */
    public static String DEFAULT_SIP_SERVER_CONFIGURATION_USED = "client.tester.default.sip.server.configuration";
    /**
     * ACTION_MAX_WAIT_TIME
     */
    public static String ACTION_MAX_WAIT_TIME = "client.tester.action.max.wait.time";
    /**
     * ACTION_MIN_WAIT_TIME_SEND_MESSAGE
     */
    // public static String ACTION_MIN_WAIT_TIME_SEND_MESSAGE =
    // "client.tester.action.min.wait.time.send.message";
    /**
     * ACTION_MIN_WAIT_TIME_SEND_ACK_MESSAGE
     */
    // public static String ACTION_MIN_WAIT_TIME_SEND_ACK_MESSAGE =
    // "client.tester.action.min.wait.time.send.ack.message";
    /**
     * ACTION_MIN_WAIT_TIME_RECEIVE_MESSAGE
     */
    // public static String ACTION_MIN_WAIT_TIME_RECEIVE_MESSAGE =
    // "client.tester.action.min.wait.time.receive.message";

    /**
     * HEADERS_LIST_COPIED
     */
    public static String HEADERS_LIST_COPIED = "client.tester.sip.header.copied";
    /**
     * Configuration parameters
     */
    private PropertiesConfiguration testClientConfiguration;

    /**
     * 
     * Constructor of SipClientTestManagerConfiguration.
     * 
     */
    private SipClientTestManagerConfiguration() {

        if (logger.isDebugEnabled()) {
            logger.debug("SipClientTestManagerConfiguration.constructor: Start...");
        }
        try {
            testClientConfiguration = new PropertiesConfiguration(
                    SIP_CLIENT_TEST_LAUNCHER_PROPERTIES);
            testClientConfiguration.setAutoSave(true);

        } catch (ConfigurationException e) {
            logger.error("SipClientTestManagerConfiguration constructor ConfigurationException Error: "
                    + e.getMessage());
        }
    }

    /**
     * getStringParameter
     * 
     * @param parameterName
     * @return the parameter
     */
    public static String getStringParameter(String parameterName) {
        if (instance == null) {
            instance = new SipClientTestManagerConfiguration();
        }
        return instance.testClientConfiguration.getString(parameterName);
    }

    /**
     * getStringParameter
     * 
     * @param parameterName
     * @param defaultValue
     * @return the parameter
     */
    public static String getStringParameter(String parameterName,
            String defaultValue) {
        if (instance == null) {
            instance = new SipClientTestManagerConfiguration();
        }
        return instance.testClientConfiguration.getString(parameterName,
                defaultValue);
    }

    /**
     * getLongParameter
     * 
     * @param parameterName
     * @param defaultValue
     * @return the parameter
     */
    public static long getLongParameter(String parameterName, long defaultValue) {
        if (instance == null) {
            instance = new SipClientTestManagerConfiguration();
        }
        return instance.testClientConfiguration.getLong(parameterName,
                defaultValue);
    }

    /**
     * getLongParameter
     * 
     * @param parameterName
     * @return the parameter
     */
    public static long getLongParameter(String parameterName) {
        if (instance == null) {
            instance = new SipClientTestManagerConfiguration();
        }
        return instance.testClientConfiguration.getLong(parameterName);
    }

    /**
     * getIntParameter
     * 
     * @param parameterName
     * @param defaultValue
     * @return the parameter
     */
    public static int getIntParameter(String parameterName, int defaultValue) {
        if (instance == null) {
            instance = new SipClientTestManagerConfiguration();
        }
        return instance.testClientConfiguration.getInt(parameterName,
                defaultValue);
    }

    /**
     * getIntParameter
     * 
     * @param parameterName
     * @return the parameter
     */
    public static long getIntParameter(String parameterName) {
        if (instance == null) {
            instance = new SipClientTestManagerConfiguration();
        }
        return instance.testClientConfiguration.getInt(parameterName);
    }

    /**
     * getListParameter
     * 
     * @param parameterName
     * @return the parameter
     */
    public static List getListParameter(String parameterName) {
        if (instance == null) {
            instance = new SipClientTestManagerConfiguration();
        }
        return instance.testClientConfiguration.getList(parameterName);
    }

}
