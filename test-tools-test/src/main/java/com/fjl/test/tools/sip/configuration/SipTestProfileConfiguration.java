package com.fjl.test.tools.sip.configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dom4j.DocumentException;
import org.dom4j.io.DOMWriter;
import org.dom4j.io.SAXReader;

import com.fjl.test.tools.configuration.SipTestProfileConstants;
import com.fjl.test.tools.configuration.TestProfileConfigurationException;
import com.fjl.test.tools.sip.SipProfile;

public class SipTestProfileConfiguration {

    /**
     * List of Sip Profiles configured in this Configuration
     */
    private Vector<SipProfile> sipProfiles;

    /**
     * Host Ip Address
     */
    private String localAddress;

    /**
     * Name of this Sip Test ProfileConfiguration
     */
    private String sipTestProfileConfigurationName;

    /**
     * Determine if this Server Configuration is connected to an Ims
     */
    private boolean isImsConnected;

    /**
     * Configured Server or Proxy Sip IP Address
     */
    private String serverSipIpAddress;

    /**
     * Configured Server or Proxy Sip Ip Port
     */
    private int serverSipIpPort;

    /**
     * Configured sip application http url
     */
    private String serverHttpUrl;

    /**
     * Configured server http ip address
     */
    private String serverHttpIpAddress;

    /**
     * Configured sip application http ip port
     */
    private int serverHttpIpPort;

    /**
     * Log4J Logger
     */
    private Logger logger = LoggerFactory
            .getLogger(com.fjl.test.tools.sip.configuration.SipTestProfileConfiguration.class);

    /**
     * 
     * Constructor of SipTestProfileConfiguration.
     * 
     * @param fileName
     *            Specifies the Properties File
     * @throws TestProfileConfigurationException
     */
    public SipTestProfileConfiguration(String fileName)
            throws TestProfileConfigurationException {
        try {
            logger.debug(
                    "SipTestProfileConfiguration.constructor: Configuration File used: {}",
                    fileName);

            // Get the Ip Address
            try {
                localAddress = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                logger.error(
                        "SipTestProfileConfiguration.constructor UnknownHostException: ",
                        e);
                throw new TestProfileConfigurationException(
                        "SipTestProfileConfiguration.constructor UnknownHostException: "
                                + e.getMessage());
            }
            sipProfiles = new Vector<SipProfile>();

            // Instanciate the XML parser
            FileInputStream inputStream = new FileInputStream(fileName);
            SAXReader reader = new SAXReader();
            org.dom4j.Document doc = reader.read(inputStream);
            DOMWriter domWriter = new DOMWriter();
            org.w3c.dom.Document domDoc = domWriter.write(doc);

            // Retrieve the TEST_PROFILE_ELEMENT root Nodes
            org.w3c.dom.NodeList sipTestProfileNodes = domDoc
                    .getElementsByTagName(SipTestProfileConstants.TEST_PROFILE_ELEMENT);
            org.w3c.dom.Node sipTestProfileNode = sipTestProfileNodes.item(0);
            // Get the sipTestCallFlowNode attributes
            org.w3c.dom.NamedNodeMap sipTestProfileNodeAttributes = sipTestProfileNode
                    .getAttributes();

            // Get the name of the SipTestProfileConfiguration
            try {
                sipTestProfileConfigurationName = sipTestProfileNodeAttributes
                        .getNamedItem(SipTestProfileConstants.NAME_ATTRIBUTE)
                        .getNodeValue();
            } catch (NullPointerException e) {
                throw new TestProfileConfigurationException(
                        "SipTestProfileConfiguration.constructor can not find attribute: "
                                + SipTestProfileConstants.NAME_ATTRIBUTE
                                + " in xml node: "
                                + sipTestProfileNode.getNodeName()
                                + e.getMessage());
            }
            // Get if the SipTestProfileConfiguration is IMS
            try {
                logger.debug("FJL TEST 50.0");
                String isImsString = sipTestProfileNodeAttributes.getNamedItem(
                        SipTestProfileConstants.IS_IMS_ATTRIBUTE)
                        .getNodeValue();
                logger.debug("FJL TEST 50.1: isImsString: " + isImsString);
                isImsConnected = false;
                if ("true".equals(isImsString)) {
                    isImsConnected = true;
                }
                logger.debug("FJL TEST 50.2: isImsConnected: " + isImsConnected);
            } catch (NullPointerException e) {
                isImsConnected = false;
            }

            // Get thesipTestProfileNode children elements
            org.w3c.dom.NodeList sipTestProfileNodeChildren = sipTestProfileNode
                    .getChildNodes();

            // Get the Sip Server ProfileChild
            for (int i = 0; i < sipTestProfileNodeChildren.getLength(); i++) {
                org.w3c.dom.Node sipTestProfileNodeChild = sipTestProfileNodeChildren
                        .item(i);
                if (sipTestProfileNodeChild.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    if (SipTestProfileConstants.SIP_SERVER_ELEMENT
                            .equals(sipTestProfileNodeChild.getNodeName())) {
                        parseSipServerElement(sipTestProfileNodeChild);
                    } else if (SipTestProfileConstants.HTTP_SERVER_ELEMENT
                            .equals(sipTestProfileNodeChild.getNodeName())) {
                        parseHttpServerElement(sipTestProfileNodeChild);
                    } else if (SipTestProfileConstants.CLIENT_PROFILES_ELEMENT
                            .equals(sipTestProfileNodeChild.getNodeName())) {
                        parseClientprofilesElement(sipTestProfileNodeChild);
                    }
                }
            }

            traceConfiguration();
            // refreshSipProfiles();

        } catch (FileNotFoundException e) {
            logger.error(
                    "SipTestProfileConfiguration.constructor FileNotFoundException: ",
                    e);
            throw new TestProfileConfigurationException(
                    "SipTestProfileConfiguration.constructor FileNotFoundException: "
                            + e.getMessage());
        } catch (DocumentException e) {
            logger.error(
                    "SipTestProfileConfiguration.constructor FileNotFoundException: ",
                    e);
            throw new TestProfileConfigurationException(
                    "SipTestProfileConfiguration.constructor FileNotFoundException: "
                            + e.getMessage());
        } catch (Throwable e) {
            logger.error(
                    "SipTestProfileConfiguration.constructor Throwable Error: ",
                    e);
            throw new TestProfileConfigurationException(
                    "SipTestProfileConfiguration.constructor Throwable Error: "
                            + e.getMessage());
        }
    }

    /**
     * parse a Sip Server Element (sipserver)
     * 
     * @param sipServerElementNode
     *            specifies the node to be parsed
     * @throws TestProfileConfigurationException
     */
    private void parseSipServerElement(org.w3c.dom.Node sipServerElementNode)
            throws TestProfileConfigurationException {
        // Get the sipTestCallFlowNode attributes
        org.w3c.dom.NamedNodeMap sipServerElementNodeAttributes = sipServerElementNode
                .getAttributes();

        // Get the ip address of the sipserver
        try {
            serverSipIpAddress = sipServerElementNodeAttributes.getNamedItem(
                    SipTestProfileConstants.IP_ADDRESS_ATTRIBUTE)
                    .getNodeValue();
        } catch (NullPointerException e) {
            throw new TestProfileConfigurationException(
                    "SipTestProfileConfiguration.parseSipServerElement can not find attribute: "
                            + SipTestProfileConstants.IP_ADDRESS_ATTRIBUTE
                            + " in xml node: "
                            + sipServerElementNode.getNodeName()
                            + e.getMessage());
        }
        // Get the ip port of the sipserver
        try {
            String serverSipIpPortString = sipServerElementNodeAttributes
                    .getNamedItem(SipTestProfileConstants.IP_PORT_ATTRIBUTE)
                    .getNodeValue();
            serverSipIpPort = Integer.parseInt(serverSipIpPortString);
        } catch (NullPointerException e) {
            serverHttpIpPort = 5060;
        }

    }

    /**
     * parse a Http Server Element (httpserver)
     * 
     * @param httpServerElementNode
     *            specifies the node to be parsed
     * @throws TestProfileConfigurationException
     */
    private void parseHttpServerElement(org.w3c.dom.Node httpServerElementNode)
            throws TestProfileConfigurationException {
        // Get the httpServerElementNode attributes
        org.w3c.dom.NamedNodeMap httpServerElementNodeAttributes = httpServerElementNode
                .getAttributes();

        // Get the ip address of the httpserver
        try {
            serverHttpIpAddress = httpServerElementNodeAttributes.getNamedItem(
                    SipTestProfileConstants.IP_ADDRESS_ATTRIBUTE)
                    .getNodeValue();
        } catch (NullPointerException e) {
            throw new TestProfileConfigurationException(
                    "SipTestProfileConfiguration.parseHttpServerElement can not find attribute: "
                            + SipTestProfileConstants.IP_ADDRESS_ATTRIBUTE
                            + " in xml node: "
                            + httpServerElementNode.getNodeName()
                            + e.getMessage());
        }
        // Get the ip port of the httpserver
        try {
            String serverHttpIpPortString = httpServerElementNodeAttributes
                    .getNamedItem(SipTestProfileConstants.IP_PORT_ATTRIBUTE)
                    .getNodeValue();
            serverHttpIpPort = Integer.parseInt(serverHttpIpPortString);
        } catch (NullPointerException e) {
            serverHttpIpPort = 8001;
        }
        // Get the ip url of the httpserver
        try {
            serverHttpUrl = httpServerElementNodeAttributes.getNamedItem(
                    SipTestProfileConstants.URL_ATTRIBUTE).getNodeValue();
        } catch (NullPointerException e) {
            throw new TestProfileConfigurationException(
                    "SipTestProfileConfiguration.parseHttpServerElement can not find attribute: "
                            + SipTestProfileConstants.URL_ATTRIBUTE
                            + " in xml node: "
                            + httpServerElementNode.getNodeName()
                            + e.getMessage());
        }
    }

    /**
     * parse a ClientProfiles Element (clientsprofile)
     * 
     * @param clientProfilesElementNode
     *            specifies the node to be parsed
     * @throws TestProfileConfigurationException
     */
    private void parseClientprofilesElement(
            org.w3c.dom.Node clientProfilesElementNode)
            throws TestProfileConfigurationException {
        // Get clientProfilesElementNode children elements
        org.w3c.dom.NodeList clientProfilesElementNodeChildren = clientProfilesElementNode
                .getChildNodes();

        // Get the Sip Server ProfileChild
        for (int i = 0; i < clientProfilesElementNodeChildren.getLength(); i++) {

            org.w3c.dom.Node clientProfileElementChild = clientProfilesElementNodeChildren
                    .item(i);
            if (clientProfileElementChild.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                // Create a new Sip Profile
                SipProfile sipProfile = new SipProfile(isImsConnected);

                // Get its name in the attributes
                org.w3c.dom.NamedNodeMap clientProfileElementChildAttributes = clientProfileElementChild
                        .getAttributes();

                // Get the name of the clientprofile
                try {
                    String profileName = clientProfileElementChildAttributes
                            .getNamedItem(
                                    SipTestProfileConstants.NAME_ATTRIBUTE)
                            .getNodeValue();
                    sipProfile.setProfileName(profileName);
                } catch (NullPointerException e) {
                    throw new TestProfileConfigurationException(
                            "SipTestProfileConfiguration.parseClientprofilesElement can not find attribute: "
                                    + SipTestProfileConstants.NAME_ATTRIBUTE
                                    + " in xml node: "
                                    + clientProfileElementChild.getNodeName()
                                    + e.getMessage());
                }

                // Get its configuration by getting clientProfileElementChild
                // children
                org.w3c.dom.NodeList clientProfileChildren = clientProfileElementChild
                        .getChildNodes();
                for (int j = 0; j < clientProfileChildren.getLength(); j++) {
                    org.w3c.dom.Node clientProfileChild = clientProfileChildren
                            .item(j);
                    if (clientProfileChild.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        if (SipTestProfileConstants.PROXY_ELEMENT
                                .equals(clientProfileChild.getNodeName())) {
                            parseProxyElement(sipProfile, clientProfileChild);
                        } else if (SipTestProfileConstants.SIP_STACK_ELEMENT
                                .equals(clientProfileChild.getNodeName())) {
                            parseSipStackElement(sipProfile, clientProfileChild);
                        } else if (SipTestProfileConstants.SIP_PROFILE_ELEMENT
                                .equals(clientProfileChild.getNodeName())) {
                            parseSipProfileElement(sipProfile,
                                    clientProfileChild);
                        }
                    }
                }
                logger.debug(
                        "SipTestProfileConfiguration.parseClientprofilesElement new Sip Profile Added: \n{}",
                        sipProfile.toString());
                sipProfiles.add(sipProfile);
            }
        }
    }

    /**
     * parse a Proxy Element (proxy)
     * 
     * @param sipProfile
     *            Specifies the SipProfile being created
     * @param proxyElementNode
     *            specifies the node to be parsed
     * @throws TestProfileConfigurationException
     */
    private void parseProxyElement(SipProfile sipProfile,
            org.w3c.dom.Node proxyElementNode)
            throws TestProfileConfigurationException {

        // Get the proxyElementNode attributes
        org.w3c.dom.NamedNodeMap proxyElementNodeAttributes = proxyElementNode
                .getAttributes();

        // Get the ip address of the sip proxy
        try {
            String proxyIpAddress = proxyElementNodeAttributes.getNamedItem(
                    SipTestProfileConstants.IP_ADDRESS_ATTRIBUTE)
                    .getNodeValue();
            if (SipTestProfileConstants.SIP_SERVER_IP_ADDRESS_PARAM
                    .equals(proxyIpAddress)) {
                proxyIpAddress = serverSipIpAddress;
            } else if (SipTestProfileConstants.NULL_VALUE
                    .equals(proxyIpAddress)) {
                proxyIpAddress = "";
            }
            sipProfile.setProxyIpAddress(proxyIpAddress);
        } catch (NullPointerException e) {
            throw new TestProfileConfigurationException(
                    "SipTestProfileConfiguration.parseProxyElement can not find attribute: "
                            + SipTestProfileConstants.IP_ADDRESS_ATTRIBUTE
                            + " in xml node: " + proxyElementNode.getNodeName()
                            + e.getMessage());
        }
        // Get the ip port of the sip proxy
        try {
            String proxyIpPortString = proxyElementNodeAttributes.getNamedItem(
                    SipTestProfileConstants.IP_PORT_ATTRIBUTE).getNodeValue();
            int proxyIpPort = 5060;
            if (SipTestProfileConstants.SIP_SERVER_IP_PORT_PARAM
                    .equals(proxyIpPortString)) {
                proxyIpPort = serverSipIpPort;
            } else if (SipTestProfileConstants.NULL_VALUE
                    .equals(proxyIpPortString)) {
                proxyIpPort = 0;
            } else {
                proxyIpPort = Integer.parseInt(proxyIpPortString);
            }

            sipProfile.setProxyPort(proxyIpPort);
        } catch (NullPointerException e) {
            sipProfile.setProxyPort(5060);
        }
        // Get the domain of the sip proxy
        try {
            String proxyDomain = proxyElementNodeAttributes.getNamedItem(
                    SipTestProfileConstants.DOMAIN_ATTRIBUTE).getNodeValue();
            if (SipTestProfileConstants.LOCAL_ADDRESS_PARAM.equals(proxyDomain)) {
                proxyDomain = localAddress;
            }

            sipProfile.setUserDomain(proxyDomain);
        } catch (NullPointerException e) {
            throw new TestProfileConfigurationException(
                    "SipTestProfileConfiguration.parseProxyElement can not find attribute: "
                            + SipTestProfileConstants.DOMAIN_ATTRIBUTE
                            + " in xml node: " + proxyElementNode.getNodeName()
                            + e.getMessage());
        }
        // Get the realm of the sip proxy
        try {
            String proxyRealm = proxyElementNodeAttributes.getNamedItem(
                    SipTestProfileConstants.REALM_ATTRIBUTE).getNodeValue();
            /*
             * if (SipTestProfileConstants.LOCAL_ADDRESS_PARAM.equals(
             * proxyDomain)) { proxyDomain = localAddress; }
             */

            sipProfile.setUserRealm(proxyRealm);
        } catch (NullPointerException e) {

            logger.debug("SipTestProfileConfiguration.parseProxyElement: No Realm found for this Proxy Configuration");
        }

    }

    /**
     * parse a SipStack Element (sipstack)
     * 
     * @param sipProfile
     *            Specifies the SipProfile being created
     * @param sipStackElementNode
     *            specifies the node to be parsed
     * @throws TestProfileConfigurationException
     */
    private void parseSipStackElement(SipProfile sipProfile,
            org.w3c.dom.Node sipStackElementNode)
            throws TestProfileConfigurationException {
        // Get the sipStackElementNode attributes
        org.w3c.dom.NamedNodeMap sipStackElementNodeAttributes = sipStackElementNode
                .getAttributes();

        // Get the udp listen port of the Sip Stack
        try {
            String udpListenPortString = sipStackElementNodeAttributes
                    .getNamedItem(
                            SipTestProfileConstants.UDP_LISTEN_PORT_ATTRIBUTE)
                    .getNodeValue();
            int udpListenPort = Integer.parseInt(udpListenPortString);
            sipProfile.setUdpListenPort(udpListenPort);
        } catch (NullPointerException e) {
            throw new TestProfileConfigurationException(
                    "SipTestProfileConfiguration.parseSipStackElement can not find attribute: "
                            + SipTestProfileConstants.UDP_LISTEN_PORT_ATTRIBUTE
                            + " in xml node: "
                            + sipStackElementNode.getNodeName()
                            + e.getMessage());
        }
        // Get the tcp listen port of the Sip Stack
        try {
            String tcpListenPortString = sipStackElementNodeAttributes
                    .getNamedItem(
                            SipTestProfileConstants.TCP_LISTEN_PORT_ATTRIBUTE)
                    .getNodeValue();
            int tcpListenPort = Integer.parseInt(tcpListenPortString);
            sipProfile.setTcpListenPort(tcpListenPort);
        } catch (NullPointerException e) {
            throw new TestProfileConfigurationException(
                    "SipTestProfileConfiguration.parseSipStackElement can not find attribute: "
                            + SipTestProfileConstants.TCP_LISTEN_PORT_ATTRIBUTE
                            + " in xml node: "
                            + sipStackElementNode.getNodeName()
                            + e.getMessage());
        }
        // Get the default protocol used by the Sip Stack
        try {
            String defaultProtocol = sipStackElementNodeAttributes
                    .getNamedItem(SipTestProfileConstants.PROTOCOL_ATTRIBUTE)
                    .getNodeValue();

            sipProfile.setProtocol(defaultProtocol);
        } catch (NullPointerException e) {
            throw new TestProfileConfigurationException(
                    "SipTestProfileConfiguration.parseSipStackElement can not find attribute: "
                            + SipTestProfileConstants.PROTOCOL_ATTRIBUTE
                            + " in xml node: "
                            + sipStackElementNode.getNodeName()
                            + e.getMessage());
        }
    }

    /**
     * parse a SipProfile Element (sipprofile)
     * 
     * @param sipProfile
     *            Specifies the SipProfile being created
     * @param sipProfileElementNode
     *            specifies the node to be parsed
     * @throws TestProfileConfigurationException
     */
    private void parseSipProfileElement(SipProfile sipProfile,
            org.w3c.dom.Node sipProfileElementNode)
            throws TestProfileConfigurationException {
        // Get the sipProfileElementNode attributes
        org.w3c.dom.NamedNodeMap sipProfileElementNodeAttributes = sipProfileElementNode
                .getAttributes();
        // Get the user display name
        try {
            String userDisplayName = sipProfileElementNodeAttributes
                    .getNamedItem(
                            SipTestProfileConstants.DISPLAY_NAME_ATTRIBUTE)
                    .getNodeValue();

            sipProfile.setUserDisplayName(userDisplayName);
        } catch (NullPointerException e) {
            throw new TestProfileConfigurationException(
                    "SipTestProfileConfiguration.parseSipProfileElement can not find attribute: "
                            + SipTestProfileConstants.DISPLAY_NAME_ATTRIBUTE
                            + " in xml node: "
                            + sipProfileElementNode.getNodeName()
                            + e.getMessage());
        }
        // Get the user identity
        try {
            String userIdentity = sipProfileElementNodeAttributes.getNamedItem(
                    SipTestProfileConstants.IDENTITY_ATTRIBUTE).getNodeValue();

            sipProfile.setUserIdentity(userIdentity);
        } catch (NullPointerException e) {
            throw new TestProfileConfigurationException(
                    "SipTestProfileConfiguration.parseSipProfileElement can not find attribute: "
                            + SipTestProfileConstants.IDENTITY_ATTRIBUTE
                            + " in xml node: "
                            + sipProfileElementNode.getNodeName()
                            + e.getMessage());
        }
        // Get the user password
        try {
            String userPassword = sipProfileElementNodeAttributes.getNamedItem(
                    SipTestProfileConstants.PASSWORD_ATTRIBUTE).getNodeValue();

            sipProfile.setPassword(userPassword);
        } catch (NullPointerException e) {
            throw new TestProfileConfigurationException(
                    "SipTestProfileConfiguration.parseSipProfileElement can not find attribute: "
                            + SipTestProfileConstants.PASSWORD_ATTRIBUTE
                            + " in xml node: "
                            + sipProfileElementNode.getNodeName()
                            + e.getMessage());
        }
        // Get the User private identity
        try {
            String privateIdentity = sipProfileElementNodeAttributes
                    .getNamedItem(
                            SipTestProfileConstants.PRIVATE_IDENTITY_ATTRIBUTE)
                    .getNodeValue();

            sipProfile.setPrivateIdentity(privateIdentity);
        } catch (NullPointerException e) {
            logger.debug("SipTestProfileConfiguration.parseSipProfileElement: "
                    + SipTestProfileConstants.PRIVATE_IDENTITY_ATTRIBUTE
                    + " not found");
        }

    }

    /**
     * refreshSipProfiles
     * 
     */
    /*
     * private void refreshSipProfiles () {
     * 
     * sipProfiles.clear(); Configuration profilesConfiguration =
     * serverConfiguration.subset( PROFILE_CONFIGURATION_PREFIX); Iterator
     * iterator = profilesConfiguration.getKeys();
     * 
     * for (; iterator.hasNext();) { String profileName = (String)
     * iterator.next();
     * 
     * List<String> profileParameters = profilesConfiguration.getList(
     * profileName); // Format is //
     * com.fjl.profile.[profileName]=[proxyAddress]
     * ,[proxyPort],[udpListenPort],[tcpListenPort], //
     * [userDisplayName],[userIdentity
     * ],[userDomain],[password],[protocol],[isImsConnected]
     * 
     * // Change the Parameters if necessary (for int and boolean);
     * 
     * String proxyAddress = profileParameters.get( 0); if
     * (SIP_ADDRESS_VALUE.equals( proxyAddress)) { proxyAddress =
     * serverSipIpAddress; } else if (NULL_VALUE.equals( proxyAddress)) {
     * proxyAddress = ""; }
     * 
     * // get the Proxy port as int String proxyPortString =
     * profileParameters.get( 1); int proxyPort; if (SIP_PORT_VALUE.equals(
     * proxyPortString)) { proxyPort = serverSipIpPort; } else if
     * (NULL_VALUE.equals( proxyPortString)) { proxyPort = 0; } else { proxyPort
     * = Integer.parseInt( proxyPortString); }
     * 
     * // get the udp listen port as int String udpListenPortString =
     * profileParameters.get( 2); int udpListenPort = Integer.parseInt(
     * udpListenPortString);
     * 
     * // get the tcp listen port as int String tcpListenPortString =
     * profileParameters.get( 3); int tcpListenPort = Integer.parseInt(
     * tcpListenPortString);
     * 
     * // Check the Profile Sip Domain String domain = profileParameters.get(
     * 6); if (LOCAL_ADDRESS_VALUE.equals( domain)) { // Set the Sip Domain to
     * the Local ip Address domain = localAddress; }
     * 
     * // Build the SipProfile SipProfile sipProfile = new SipProfile(
     * profileName, proxyAddress, proxyPort, udpListenPort, tcpListenPort,
     * profileParameters.get( 4), profileParameters.get( 5), domain,
     * profileParameters.get( 7), profileParameters .get( 8), isImsConnected);
     * if (logger.isDebugEnabled()) logger.debug(
     * "SipTestProfileConfiguration.constructor new Sip Profile Added: \n" +
     * sipProfile.toString()); sipProfiles.add( sipProfile);
     * 
     * } }
     */

    /**
     * Get the Name of this Sip Server Configuration
     * 
     * @return
     */
    public String getSipServerConfigurationName() {
        return sipTestProfileConfigurationName;
    }

    /**
     * Trace the configuration into the logger
     */
    public void traceConfiguration() {
        if (logger.isDebugEnabled()) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("\n///////// SIP TEST CONFIGURATION /////////\n");
            buffer.append("\n\t -> Configuration Name: "
                    + sipTestProfileConfigurationName);
            buffer.append("\n\t -> Is a Via IMS Configuration (use of Proxy): "
                    + isImsConnected);
            buffer.append("\n\t -> Server or Proxy Sip Ip Address: "
                    + serverSipIpAddress);
            buffer.append("\n\t -> Server or Proxy Sip Ip Port: "
                    + serverSipIpPort);
            if (serverHttpUrl != null) {
                buffer.append("\n\t -> Application Http Url: " + serverHttpUrl);
                buffer.append("\n\t -> Application Http Ip Address: "
                        + serverHttpIpAddress);
                buffer.append("\n\t -> Application Http Port: "
                        + serverHttpIpPort);
            } else {
                buffer.append("\n\t -> Application Http Url: Not defined. Http requests will not be executed");
                buffer.append("\n\t -> Application Http Ip Address: "
                        + serverHttpIpAddress);
                buffer.append("\n\t -> Application Http Port: "
                        + serverHttpIpPort);
            }
            buffer.append("\n\n/////////////////////////////////////////////////////\n");
            logger.debug("{}", buffer.toString());
        }

    }

    /**
     * Get if this Server Configuration is Connected to a Proxy of an IMS
     * 
     * @return
     */
    public boolean isImsConnected() {
        return isImsConnected;
    }

    /**
     * Get the Http Ip Address of the Application Deployed on the Server
     * 
     * @return the HHttp Ip Address of the Application Deployed on the Server
     */
    public String getServerHttpIpAddress() {
        return serverHttpIpAddress;
    }

    /**
     * Get the Http Port of the Application Deployed on the Server
     * 
     * @return the Http Port of the Application Deployed on the Server
     */
    public int getServerHttpIpPort() {
        return serverHttpIpPort;
    }

    /**
     * Get the Http Url of the Application Deployed on the Server
     * 
     * @return the Http Url of the Application Deployed on the Server
     */
    public String getServerHttpUrl() {
        return serverHttpUrl;
    }

    /**
     * Get the Sip Address (IP) of the Server (or Proxy)
     * 
     * @return the Sip Address (IP) of the Server (or Proxy)
     */
    public String getServerSipIpAddress() {
        return serverSipIpAddress;
    }

    /**
     * Get the Sip Port (IP) of the Server (or Proxy)
     * 
     * @return the Sip Port (IP) of the Server (or Proxy)
     */
    public int getServerSipIpPort() {
        return serverSipIpPort;
    }

    /**
     * Get the list of Sip Profiles
     * 
     * @return the list of Sip Profiles
     */
    public Vector<SipProfile> getSipProfiles() {
        return sipProfiles;
    }

}
