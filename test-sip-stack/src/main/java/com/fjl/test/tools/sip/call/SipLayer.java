package com.fjl.test.tools.sip.call;

import static com.fjl.test.tools.sip.SipConstants.CALL_ID_HEADER;

import java.net.InetAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Properties;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.slf4j.Logger;

import com.fjl.test.tools.sip.SipConstants;
import com.fjl.test.tools.sip.SipPhone;
import com.fjl.test.tools.sip.SipProfile;
import com.fjl.test.tools.sip.utils.SipUtils;

public class SipLayer implements SipListener {

    public static String DEFAULT_BRACH_ID = "z9hG4bK";

    private final static String STACK_NAME = "javax.sip.STACK_NAME";

    private final static String OUTBOUND_PROXY = "javax.sip.OUTBOUND_PROXY";

    private final static String PROTOCOL = "javax.sip.PROTOCOL";

    private final static String IP_ADDRESS = "javax.sip.IP_ADDRESS";

    private final static String USER_IDENTITY = "javax.sip.USER_IDENTITY";

    private final static String USER_DOMAIN = "javax.sip.USER_DOMAIN";

    private final static String USER_PASSWORD = "javax.sip.USER_PASSWORD";

    private final static String TRACE_LEVEL = "gov.nist.javax.sip.TRACE_LEVEL";

    private final static String SERVER_LOG = "gov.nist.javax.sip.SERVER_LOG";

    private final static String DEBUG_LOG = "gov.nist.javax.sip.DEBUG_LOG";

    private final static int DEFAULT_TRACE_LEVEL = 32;

    private final static String DEFAULT_SERVER_LOG = "logs/server.log";

    private final static String DEFAULT_DEBUG_LOG = "logs/debug.log";

    private final static int MAX_FORWARDS_DEFAULT = 70;

    private final static int DEFAULT_EXPIRE_TIME = 60;

    private final static String SIP_URI = "sip:";

    private final static String AROBASE = "@";

    private String outboundProxy;

    /**
     * Address Factory of the SIP Stack
     */
    private AddressFactory addressFactory;

    /**
     * Header Factory of the SIP Stack
     */
    private HeaderFactory headerFactory;

    /**
     * Message Factory of the SIP Stack
     */
    private MessageFactory messageFactory;

    /**
     * Sip Factory of the SIP Stack
     */
    private SipFactory sipFactory;

    /**
     * Jain Sip Sip Stack
     */
    private SipStack sipStack;

    /**
     * TCP Sip Provider
     */
    private SipProvider tcpSipProvider;

    /**
     * UDP Sip Provider
     */
    private SipProvider udpSipProvider;

    /**
     * TCP Listening point
     */
    private ListeningPoint tcp;

    /**
     * UDP Listening point
     */
    private ListeningPoint udp;

    /**
     * Host Ip Address
     */
    private String ipAddress;

    /**
     * Sip Profile used by this Sip Stack
     */
    private SipProfile sipProfile;

    /**
     * Registration ID (Helpful to register to a Proxy Registrar Server)
     */
    private String registrationID;

    /**
     * Slf4J Logger
     */
    private Logger logger;

    /**
     * SipPhone using this SipLayer
     */
    private SipPhone sipPhone;

    /**
     * Name of the Sip Stack
     */
    private String stackName;

    /**
     * Contact Header
     */
    private ContactHeader contactHeader;

    /**
     * Contact address
     */
    private Address contactAddress;

    /**
     * MaxForwards Header
     */
    private MaxForwardsHeader maxForwardsHeader;

    /**
     * Constructor
     * 
     * @param sipPhone
     * @param logger
     * @param stackName
     * @param sipProfile
     * @throws SipClientStackException
     */
    public SipLayer(SipPhone sipPhone, Logger logger, SipProfile sipProfile)
            throws SipClientStackException {

        try {

            this.sipPhone = sipPhone;
            this.logger = logger;
            this.sipProfile = sipProfile;

            // Get the sip Stack Attributes
            ipAddress = InetAddress.getLocalHost().getHostAddress();
            outboundProxy = null;
            if ((sipProfile.getProxyIpAddress() != null)
                    && (!sipProfile.getProxyIpAddress().equals(""))) {

                outboundProxy = sipProfile.getProxyIpAddress() + ":"
                        + sipProfile.getProxyPort();
            } else {
                logger.trace("SipLayer.constructor: no outbound proxy configured");
            }
            // Create the SipFactory
            sipFactory = SipFactory.getInstance();
            sipFactory.setPathName("gov.nist");

            stackName = "FJ_LEVEE_" + sipProfile.getUserIdentity();

            logger.debug("SipLayer.constructor: Try to init SipStack: {}",
                    stackName);
            // create the sip stack
            Properties properties = new Properties();
            // properties.setProperty( IP_ADDRESS, ipAddress);

            if (properties.getProperty("javax.sip.IP_ADDRESS") != null) {
                properties.remove("javax.sip.IP_ADDRESS");
            }
            properties.setProperty(STACK_NAME, stackName);
            if (outboundProxy != null) {
                properties.setProperty(OUTBOUND_PROXY, outboundProxy);
            }
            properties.setProperty(TRACE_LEVEL, "" + DEFAULT_TRACE_LEVEL);
            properties.setProperty(SERVER_LOG, DEFAULT_SERVER_LOG);
            properties.setProperty(DEBUG_LOG, DEFAULT_DEBUG_LOG);

            sipStack = sipFactory.createSipStack(properties);
            headerFactory = sipFactory.createHeaderFactory();
            addressFactory = sipFactory.createAddressFactory();
            messageFactory = sipFactory.createMessageFactory();

            tcp = sipStack.createListeningPoint(ipAddress,
                    sipProfile.getTcpListenPort(), "tcp");
            udp = sipStack.createListeningPoint(ipAddress,
                    sipProfile.getUdpListenPort(), "udp");

            tcpSipProvider = sipStack.createSipProvider(tcp);
            tcpSipProvider.addSipListener(this);

            udpSipProvider = sipStack.createSipProvider(udp);
            udpSipProvider.addSipListener(this);
            if (sipProfile.getProtocol().equals("tcp")) {

            } else {

            }

            // Generate the Contact Header
            String localName = sipProfile.getUserIdentity();
            SipURI contactURI = addressFactory.createSipURI(localName,
                    getHost());
            contactURI.setPort(getPort());
            contactAddress = addressFactory.createAddress(contactURI);
            contactAddress.setDisplayName(sipPhone.getSipProfile()
                    .getUserDisplayName());
            contactHeader = headerFactory.createContactHeader(contactAddress);

            // generate the MaxForward Header
            maxForwardsHeader = headerFactory
                    .createMaxForwardsHeader(MAX_FORWARDS_DEFAULT);

            logger.debug(
                    "SipLayer.constructor: SipStack: {} successfully created.",
                    stackName);
        } catch (Throwable e) {
            logger.error("SipLayer.constructor: ", e);
            throw new SipClientStackException(
                    "SipLayer.constructor: Error occured: " + e.getMessage());
        }
    }

    /**
     * This method is called by the SIP stack when a new request arrives.
     * 
     * @param requestEvent
     *            specifies the Request Event received
     */
    public void processRequest(RequestEvent requestEvent) {
        try {
            logger.trace("SipLayer.processRequest Start");
            Request request = requestEvent.getRequest();

            String method = request.getMethod();
            logger.trace("SipLayer.processRequest type: {}",
                    request.getMethod());

            Dialog dialog = requestEvent.getDialog();
            ServerTransaction serverTransaction = null;
            if (dialog == null) {
                logger.trace("SipLayer.processRequest: dialog is null.");
                serverTransaction = requestEvent.getServerTransaction();
                if (serverTransaction == null) {
                    logger.trace("SipLayer.processRequest: create a Server Transaction.");
                    try {

                        serverTransaction = udpSipProvider
                                .getNewServerTransaction(request);
                        dialog = serverTransaction.getDialog();
                    } catch (Exception e) {
                        logger.error("SipStack.processRequest error: ", e);

                    }
                }
            }
            String callId = ((CallIdHeader) request.getHeader(CALL_ID_HEADER))
                    .getCallId();

            logger.debug("SipLayer.request: Received on callID: {}", callId);
            SipDialog sipDialog = getSipDialog(callId);

            if (sipDialog == null) {
                logger.debug(
                        "SipLayer.request: Initial request Received on callID: {}",
                        callId);
                processInitialRequest(callId, requestEvent, serverTransaction);
            } else {
                sipDialog.addSipStackEvent(requestEvent);
            }
            logger.trace("SipLayer.processRequest End");
        } catch (Throwable e) {
            logger.error("SipStack.processRequest error: ", e);
        }

    }

    /**
     * Process an Invite request
     * 
     * @param callId
     *            specifies the Call ID
     * @param requestEvent
     *            specifies the initial Request Event
     * @param serverTransaction
     *            Specifies the Server Transaction
     * @throws SipClientStackException
     */
    private void processInitialRequest(String callId,
            RequestEvent requestEvent, ServerTransaction serverTransaction)
            throws SipClientStackException {

        logger.trace("SipLayer.processInitialRequest: Received on callID: {}",
                callId);

        logger.debug(
                "SipLayer.processInitialRequest: new Incoming Sip Call of type: {}",
                requestEvent.getRequest().getMethod());
        SipDialog sipCall = new SipDialog(sipPhone);
        sipCall.setInitialServerTransaction(serverTransaction);
        sipCall.setDialog(serverTransaction.getDialog());
        sipCall.setCallId(callId);
        sipCall.addSipStackEvent(requestEvent);
        sipPhone.addSipDialog(sipCall);

    }

    /**
     * This method is called by the SIP stack when a response arrives.
     * 
     * @param responseEvent
     *            specifies the Response Event received
     */
    public void processResponse(ResponseEvent responseEvent) {
        try {
            logger.trace("SipLayer.processResponse Start");
            Response response = responseEvent.getResponse();
            int status = response.getStatusCode();
            // Check that the Response exists in a ClientTransaction
            /*
             * ClientTransaction clientTransaction =
             * responseEvent.getClientTransaction();
             * 
             * if (clientTransaction == null) { logger.error(
             * "SipLayer.processResponse Error: can not find a Client
             * Transaction for Reponse: " + response.toString()); return; }
             */
            CSeqHeader cSeqHeader = (CSeqHeader) responseEvent.getResponse()
                    .getHeader(SipConstants.CSEQ_HEADER);
            String method = cSeqHeader.getMethod();
            logger.trace(
                    "SipLayer.processResponse Method: {}, status code: {}",
                    method, status);

            String callId = ((CallIdHeader) response.getHeader(CALL_ID_HEADER))
                    .getCallId();
            SipDialog call = getSipDialog(callId);
            if (call == null)
                throw new SipClientStackException(
                        "SipStack.processRequest: error can not find a SipCall with callId: "
                                + callId);
            call.addSipStackEvent(responseEvent);
            logger.trace("SipLayer.processResponse End");
        } catch (Throwable e) {
            logger.error("SipStack.processResponse error: ", e);
        }
    }

    /**
     * This method is called by the SIP stack when there's no answer to a
     * message. Note that this is treated differently from an error message.
     * 
     * @param timeoutEvent
     *            specifies the Timeout Event received
     */
    public void processTimeout(TimeoutEvent timeoutEvent) {
        try {
            logger.trace("SipLayer.processTimeout Start");

            // Check that the TimeoutEvent exists in a ClientTransaction or in a
            // ServerTransaction
            ClientTransaction clientTransaction = timeoutEvent
                    .getClientTransaction();
            ServerTransaction serverTransaction = null;

            if (clientTransaction == null) {
                logger.trace("SipLayer.processTimeout: can not find a ClientTransaction for a TimeOutEvent, try to find a ServerTransaction");
                serverTransaction = timeoutEvent.getServerTransaction();
            }
            Dialog dialog = null;
            if ((clientTransaction == null) && (serverTransaction == null)) {
                logger.error("SipLayer.processTimeout Error: can not find neither a ClientTransaction nor a ServerTransaction for a TimeOutEvent: ");
                return;
            }
            if (clientTransaction != null) {
                String method = clientTransaction.getRequest().getMethod();
                dialog = clientTransaction.getDialog();
                logger.trace(
                        "SipLayer.processTimeout (ClientTransaction) to a request Method : {}",
                        method);
            } else {
                String method = serverTransaction.getRequest().getMethod();
                dialog = serverTransaction.getDialog();
                logger.trace(
                        "SipLayer.processTimeout (ServerTransaction) to a response Method : {}",
                        method);
            }

            if (dialog == null) {
                logger.error("SipLayer.processTimeout Error: can not find a Dialog for a TimeOutEvent");
                return;
            }
            String callId = dialog.getCallId().getCallId();
            SipDialog call = getSipDialog(callId);
            if (call == null)
                throw new SipClientStackException(
                        "SipStack.processTimeout: error can not find a SipCall with callId: "
                                + callId);
            call.addSipStackEvent(timeoutEvent);
            logger.trace("SipLayer.processTimeout End");
        } catch (Throwable e) {
            logger.error("SipStack.processTimeout error: ", e);
        }

    }

    /**
     * This method uses the SIP stack to send a message.
     */
    public void sendMessage(String to, String message) throws ParseException,
            InvalidArgumentException, SipException {
        /*
         * SipURI from = addressFactory.createSipURI( userName, getHost() + ":"
         * + getPort()); Address fromNameAddress = addressFactory.createAddress(
         * from); fromNameAddress.setDisplayName( userDisplayName); FromHeader
         * fromHeader = headerFactory.createFromHeader( fromNameAddress,
         * "textclientv1.0");
         * 
         * String username = to.substring( to.indexOf( ":") + 1, to.indexOf(
         * "@")); String address = to.substring( to.indexOf( "@") + 1);
         * 
         * SipURI toAddress = addressFactory.createSipURI( username, address);
         * Address toNameAddress = addressFactory.createAddress( toAddress);
         * toNameAddress.setDisplayName( username); ToHeader toHeader =
         * headerFactory.createToHeader( toNameAddress, null);
         * 
         * SipURI requestURI = addressFactory.createSipURI( username, address);
         * requestURI.setTransportParam( "udp");
         * 
         * ArrayList viaHeaders = new ArrayList(); ViaHeader viaHeader =
         * headerFactory.createViaHeader( getHost(), getPort(), "udp", null);
         * viaHeaders.add( viaHeader);
         * 
         * CallIdHeader callIdHeader = sipProvider.getNewCallId();
         * 
         * CSeqHeader cSeqHeader = headerFactory.createCSeqHeader( 1,
         * Request.MESSAGE);
         * 
         * MaxForwardsHeader maxForwards =
         * headerFactory.createMaxForwardsHeader( 70);
         * 
         * Request request = messageFactory.createRequest( requestURI,
         * Request.MESSAGE, callIdHeader, cSeqHeader, fromHeader, toHeader,
         * viaHeaders, maxForwards);
         * 
         * SipURI contactURI = addressFactory.createSipURI( username,
         * getHost()); contactURI.setPort( getPort()); Address contactAddress =
         * addressFactory.createAddress( contactURI);
         * contactAddress.setDisplayName( userDisplayName); ContactHeader
         * contactHeader = headerFactory.createContactHeader( contactAddress);
         * request.addHeader( contactHeader);
         * 
         * ContentTypeHeader contentTypeHeader =
         * headerFactory.createContentTypeHeader( "text", "plain");
         * request.setContent( message, contentTypeHeader);
         * 
         * sipProvider.sendRequest( request);
         */
    }

    public String getHost() {
        // String host = sipStack.getIPAddress();
        // return host;
        return ipAddress;
    }

    public int getPort() {
        int port = udpSipProvider.getListeningPoint("udp").getPort();
        return port;
    }

    public void processIOException(IOExceptionEvent event) {

    }

    public void processTransactionTerminated(TransactionTerminatedEvent event) {

    }

    public void processDialogTerminated(DialogTerminatedEvent event) {

    }

    public SipDialog getSipDialog(String callID) {
        return sipPhone.getSipDialog(callID);
    }

    public String getUserName() {
        return sipProfile.getUserIdentity();
    }

    /**
     * Get the Sip Address Factory
     * 
     * @return the Sip Address Factory
     */
    public AddressFactory getAddressFactory() {
        return addressFactory;
    }

    /**
     * Get the Sip Factory
     * 
     * @return the Sip Header Factory
     */
    public HeaderFactory getHeaderFactory() {
        return headerFactory;
    }

    /**
     * Get the Sip Messsage Factory
     * 
     * @return the Sip Messsage Factory
     */
    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

    public void dispose() throws SipClientStackException {

        logger.debug("SipLayer.dispose");
        try {
            if (sipStack != null) {
                logger.debug("SipLayer.dispose: SipStack is not null: remove ListeningPoints");
                sipStack.deleteListeningPoint(tcp);
                sipStack.deleteListeningPoint(tcp);
                tcpSipProvider.removeListeningPoint(tcp);
                tcpSipProvider.removeSipListener(this);
                udpSipProvider.removeListeningPoint(udp);
                udpSipProvider.removeSipListener(this);
                sipStack.deleteSipProvider(tcpSipProvider);
                sipStack.deleteSipProvider(udpSipProvider);
            }
        } catch (ObjectInUseException e) {
            throw new SipClientStackException(
                    "SipLayer.dispose error occurred: " + e.getMessage());
        }
    }

    public String getImsAddress() {
        String sipUri = SIP_URI + sipProfile.getUserIdentity() + AROBASE
                + sipProfile.getUserDomain();

        logger.debug("SipLayer.getImsAddress: {}", sipUri);
        return sipUri;
    }

    /**
     * Get the Via Headers to add in the SIP Messages
     * 
     * @return the List of Via Headers
     * @throws SipClientStackException
     */

    public ArrayList getViaHeaders() throws SipClientStackException {
        /**
         * Via Headers to use in ths Sip Messages
         */
        ArrayList viaHeaders = new ArrayList<ViaHeader>();
        try {
            ViaHeader viaHeader = headerFactory.createViaHeader(getHost(),
                    getPort(), sipPhone.getSipProfile().getProtocol(), SipUtils
                            .getInstance().generateBranchId());
            viaHeaders.add(viaHeader);
        } catch (InvalidArgumentException e) {
            throw new SipClientStackException(
                    "SipLayer.getViaHeaders InvalidArgumentException Error:"
                            + e.getMessage());

        } catch (ParseException e) {
            throw new SipClientStackException(
                    "SipLayer.getViaHeaders ParseException Error:"
                            + e.getMessage());
        }

        return viaHeaders;

    }

    /**
     * Get the Contact Header to add in the SIP Messages
     * 
     * @return the Contact Header
     */
    public ContactHeader getContactHeader() {
        return contactHeader;
    }

    /**
     * Get the Contact Address as String
     * 
     * @return the Contact Address as String
     */
    public String getContactAddress() {
        return contactAddress.getURI().toString();
    }

    /**
     * Get the MaxForwards Header to add in the new SIP Requests created
     * 
     * @return the MaxForwards Header
     */
    public MaxForwardsHeader getMaxForwardsHeader() {
        return maxForwardsHeader;
    }

    /**
     * Get the TCP Sip Provider
     * 
     * @return the TCP Sip Provider
     */
    public SipProvider getTcpSipProvider() {
        return tcpSipProvider;
    }

    /**
     * Get the UDP Sip Provider
     * 
     * @return the UDP Sip Provider
     */
    public SipProvider getUdpSipProvider() {
        return udpSipProvider;
    }

    /**
     * Get the Sip Stack Outbound proxy
     * 
     * @return
     */
    public String getOutboundProxy() {
        return outboundProxy;
    }

}
