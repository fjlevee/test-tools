package com.fjl.test.tools.sip.call;

import static com.fjl.test.tools.sip.SipConstants.CALL_ID_HEADER;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Vector;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionUnavailableException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.AuthorizationHeader;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ProxyAuthorizationHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Message;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fjl.test.tools.sip.SipClient;
import com.fjl.test.tools.sip.SipConstants;
import com.fjl.test.tools.sip.SipEventListener;
import com.fjl.test.tools.sip.SipPhone;
import com.fjl.test.tools.sip.SipProfile;
import com.fjl.test.tools.sip.authentication.AuthenticationUtils;
import com.fjl.test.tools.sip.call.content.Content;
import com.fjl.test.tools.sip.call.state.SipDialogState;
import com.fjl.test.tools.sip.call.state.SipDialogState.DialogState;
import com.fjl.test.tools.sip.utils.FromToHeader;
import com.fjl.test.tools.sip.utils.SipHeader;

public class SipDialog implements SipStackEventListener {
    /**
     * Log4j Logger
     */
    private Logger logger = LoggerFactory
            .getLogger(com.fjl.test.tools.sip.call.SipDialog.class);

    /**
     * SIp Dialog
     */
    private Dialog dialog;

    /**
     * SipLayer
     */
    private SipLayer sipLayer;

    /**
     * sipPhone
     */
    private SipPhone sipPhone;

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
     * Current CSeq Header index
     */
    long currentCSeqHeaderIndex;

    /**
     * Sip Dialog State
     */
    private DialogState sipDialogState;

    /**
     * Sip Stack Event List
     */
    private SipStackEventList sipStackEventList;

    /**
     * Sip Event Listener
     */
    private SipEventListener sipEventListener;

    /**
     * Call ID Header
     */
    private CallIdHeader callIdHeader;

    /**
     * Sip Call-ID
     */
    private String callId;

    private String callTag;

    private Address contactAddress;

    private FromToHeader localHeader;

    private SipHeader contactHeader;

    private FromToHeader remoteHeader;

    private SipClient remoteClient;

    private long registerExpires;

    /**
     * Intial Server Transaction (Invited Case => User is not the initiator of
     * the Sip Call)
     */
    private ServerTransaction initialServerTransation;

    /**
     * Intial Client Transaction (Inviting Case => User is the initiator of the
     * Sip Call)
     */
    private ClientTransaction intialClientTransation;

    /**
     * Determines if this Sip Dialog is the Initiator of the Sip Call (INVITE
     * sent)
     */
    public boolean isInitiator;

    /**
     * Client Transactions HashMap => Method,Transaction
     */
    private HashMap<String, ClientTransaction> clientTransactions;

    /**
     * Server Transactions HashMap => Method,Transaction
     */
    private HashMap<String, ServerTransaction> serverTransactions;

    /**
     * Current provisional reponse received
     */
    private Response currentProvisionalResponse;

    /**
     * Constructor used when creating a new Outgoing SipCall
     * 
     * @param sipPhone
     *            specifies the SipPhone containing this new Sip Call
     * @param sipLayer
     *            specifies the SipLayer managing this new Sip Call
     * @throws SipClientStackException
     */
    public SipDialog(SipPhone sipPhone) throws SipClientStackException {

        logger.debug("SipDialog.constructor(SipPhone sipPhone)");
        initSipDialog(sipPhone, null);
    }

    /**
     * Constructor used when creating a new Outgoing SipCall
     * 
     * @param sipPhone
     *            specifies the SipPhone containing this new Sip Call
     * @param sipLayer
     *            specifies the SipLayer managing this new Sip Call
     * @throws SipClientStackException
     */
    public SipDialog(SipPhone sipPhone, SipEventListener sipEventListener)
            throws SipClientStackException {
        logger.debug("SipDialog.constructor(SipPhone sipPhone,SipEventListener sipEventListener)");
        initSipDialog(sipPhone, sipEventListener);
    }

    /**
     * initSipDialog
     * 
     * @param sipPhone
     * @param sipEventListener
     * @throws SipClientStackException
     */
    private void initSipDialog(SipPhone sipPhone,
            SipEventListener sipEventListener) throws SipClientStackException {

        logger.debug("SipDialog.initSipDialog");

        this.sipPhone = sipPhone;
        this.sipLayer = sipPhone.getSipLayer();
        this.sipStackEventList = new SipStackEventList(this);
        if (sipEventListener != null) {
            this.sipEventListener = sipEventListener;
        } else {
            this.sipEventListener = sipPhone.getSipEventListener();
        }

        if (this.sipEventListener == null) {
            throw new SipClientStackException(
                    "SipDialog.constructor Error: can not create a SipDialog with a null sipEventListener");

        }
        this.sipDialogState = DialogState.START;
        this.addressFactory = sipLayer.getAddressFactory();
        this.headerFactory = sipLayer.getHeaderFactory();
        this.messageFactory = sipLayer.getMessageFactory();
        this.currentCSeqHeaderIndex = 1;
        this.isInitiator = false;
        this.serverTransactions = new HashMap<String, ServerTransaction>();
        this.clientTransactions = new HashMap<String, ClientTransaction>();
        this.localHeader = null;
        this.remoteHeader = null;

    }

    /**
     * handleSipStackEvent
     * 
     * @param event
     * @throws SipClientStackException
     */
    public void handleSipStackEvent(EventObject event)
            throws SipClientStackException {

        logger.debug("SipDialog.handleSipStackEvent");
        try {
            if (event.getClass().equals(RequestEvent.class)) {
                handleSipRequestEvent((RequestEvent) event);
            } else if (event.getClass().equals(ResponseEvent.class)) {
                handleSipResponseEvent((ResponseEvent) event);
            } else if (event.getClass().equals(TimeoutEvent.class)) {
                handleTimeoutEvent((TimeoutEvent) event);
            }
            checkIfAlive();
        } catch (SipClientStackException e) {
            logger.error(
                    "SipDialog.handleSipStackEvent SipClientStackException: ",
                    e);
            checkIfAlive();
            throw new SipClientStackException(
                    "SipDialog.handleSipStackEvent SipClientStackException: "
                            + e.getMessage());
        } catch (Throwable e) {
            logger.error("SipDialog.handleSipStackEvent Throwable error: ", e);
            checkIfAlive();
            throw new SipClientStackException(
                    "SipDialog.handleSipStackEvent Throwable error: "
                            + e.getMessage());
        }
    }

    /**
     * handleSipRequestEvent
     * 
     * @param requestEvent
     * @throws SipClientStackException
     */
    private void handleSipRequestEvent(RequestEvent requestEvent)
            throws SipClientStackException {

        Request request = requestEvent.getRequest();
        String method = request.getMethod();
        logger.info("------------- {} RECEIVED ---------------->\n{}", method,
                request.toString());

        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        // Checks if the methods is a RE-INVITE in this case the Invite Server
        // Transaction must be set
        if ((method.equals(SipConstants.INVITE))
                && (requestEvent.getServerTransaction() != null)) {
            setInitialServerTransaction(requestEvent.getServerTransaction());
        }

        // create the server transaction if the request is not an INVITE or a
        // ACK
        if ((!method.equals(SipConstants.INVITE)/*
                                                 * ) && (!method.equals(
                                                 * SipConstants.INVITE))
                                                 */)) {
            logger.trace(
                    "SipDialog.handleSipRequestEvent: add a SipServerTransaction for method: {}",
                    method);

            serverTransactions.put(method, requestEvent.getServerTransaction());
        }

        if (method.equals(SipConstants.INVITE)) {
            state.handleInvite(this, requestEvent, sipEventListener);
        } else if (method.equals(SipConstants.BYE)) {
            state.handleBye(this, requestEvent, sipEventListener);
        } else if (method.equals(SipConstants.MESSAGE)) {
            state.handleMessage(this, requestEvent, sipEventListener);
        } else if (method.equals(SipConstants.OPTIONS)) {
            state.handleOptions(this, requestEvent, sipEventListener);
        } else if (method.equals(SipConstants.UPDATE)) {
            state.handleUpdate(this, requestEvent, sipEventListener);
        } else if (method.equals(SipConstants.NOTIFY)) {
            state.handleNotify(this, requestEvent, sipEventListener);
        } else if (method.equals(SipConstants.PRACK)) {
            state.handlePrack(this, requestEvent, sipEventListener);
        } else if (method.equals(SipConstants.ACK)) {
            state.handleAck(this, requestEvent, sipEventListener);
        } else if (method.equals(SipConstants.CANCEL)) {
            state.handleCancel(this, requestEvent, sipEventListener);
        } else if (method.equals(SipConstants.INFO)) {
            state.handleInfo(this, requestEvent, sipEventListener);
        } else if (method.equals(SipConstants.REFER)) {
            state.handleRefer(this, requestEvent, sipEventListener);
        } else if (method.equals(SipConstants.REGISTER)) {
            state.handleRegister(this, requestEvent, sipEventListener);
        } else {
            throw new SipClientStackException(
                    "SipDialog.handleSipRequestEvent : error undefined method: "
                            + method);
        }

    }

    /**
     * handleSipResponseEvent
     * 
     * @param responseEvent
     * @throws SipClientStackException
     */
    private void handleSipResponseEvent(ResponseEvent responseEvent)
            throws SipClientStackException {
        Response response = responseEvent.getResponse();
        int status = response.getStatusCode();
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        // String method =
        // responseEvent.getClientTransaction().getRequest().getMethod();
        CSeqHeader cSeqHeader = (CSeqHeader) responseEvent.getResponse()
                .getHeader(SipConstants.CSEQ_HEADER);
        String method = cSeqHeader.getMethod();
        if (logger.isInfoEnabled())
            logger.info("------------- {} {} RECEIVED ---------------->\n{}",
                    status, method, response.toString());
        if (status == 100) {
            logger.trace(
                    "SipDialog.handleSipResponseEvent processing a SIP {} trying response. Status code is: {}",
                    method, status);
            if (method.equals(SipConstants.INVITE)) {
                state.handleInviteTryingResponse(this, responseEvent,
                        sipEventListener);
            } else {
                throw new SipClientStackException(
                        "SipDialog.handleSipResponseEvent : error can not receive provisional response for method: "
                                + method);
            }
        }
        if ((status > 100) && (status < 200)) {
            logger.trace(
                    "SipDialog.handleSipResponseEvent processing a SIP {} provisional response. Status code is: {}",
                    method, status);
            if (method.equals(SipConstants.INVITE)) {
                // Here we should have received the to-tag
                ToHeader toHeader = (ToHeader) responseEvent.getResponse()
                        .getHeader(SipConstants.TO_HEADER);
                updateRemoteHeader(toHeader);
                currentProvisionalResponse = response;
                state.handleInviteProvisionalResponse(this, responseEvent,
                        sipEventListener);
            } else {
                throw new SipClientStackException(
                        "SipDialog.handleSipResponseEvent : error can not receive provisional response for method: "
                                + method);
            }
        } else if ((status >= 200) && (status < 300)) {
            logger.trace(
                    "SipDialog.handleSipResponseEvent processing a SIP {} success response. Status code is: {}",
                    method, status);
            if (method.equals(SipConstants.INVITE)) {
                // Here we should have received the to-tag
                ToHeader toHeader = (ToHeader) responseEvent.getResponse()
                        .getHeader(SipConstants.TO_HEADER);
                updateRemoteHeader(toHeader);
                state.handleInviteSuccessResponse(this, responseEvent,
                        sipEventListener);
            } else if (method.equals(SipConstants.BYE)) {
                state.handleByeSuccessResponse(this, responseEvent,
                        sipEventListener);
            } else if (method.equals(SipConstants.MESSAGE)) {
                state.handleMessageSuccessResponse(this, responseEvent,
                        sipEventListener);
            } else if (method.equals(SipConstants.OPTIONS)) {
                state.handleOptionsSuccessResponse(this, responseEvent,
                        sipEventListener);
            } else if (method.equals(SipConstants.UPDATE)) {
                state.handleUpdateSuccessResponse(this, responseEvent,
                        sipEventListener);
            } else if (method.equals(SipConstants.NOTIFY)) {
                state.handleNotifySuccessResponse(this, responseEvent,
                        sipEventListener);
            } else if (method.equals(SipConstants.PRACK)) {
                state.handlePrackSuccessResponse(this, responseEvent,
                        sipEventListener);
            } else if (method.equals(SipConstants.CANCEL)) {
                state.handleCancelSuccessResponse(this, responseEvent,
                        sipEventListener);
            } else if (method.equals(SipConstants.INFO)) {
                state.handleInfoSuccessResponse(this, responseEvent,
                        sipEventListener);
            } else if (method.equals(SipConstants.REFER)) {
                state.handleReferSuccessResponse(this, responseEvent,
                        sipEventListener);
            } else if (method.equals(SipConstants.REGISTER)) {
                state.handleRegisterSuccessResponse(this, responseEvent,
                        sipEventListener);
            } else if (method.equals(SipConstants.SUBSCRIBE)) {
                state.handleSubscribeSuccessResponse(this, responseEvent,
                        sipEventListener);
            } else {
                throw new SipClientStackException(
                        "SipDialog.handleSipResponseEvent : error undefined method: "
                                + method);
            }
        } else if ((status >= 300) && (status < 400)) {

            logger.trace(
                    "SipDialog.handleSipResponseEvent processing a SIP {} redirect response. Status code is: {}",
                    method, status);
            if (!method.equals(SipConstants.INVITE)) {
                throw new SipClientStackException(
                        "SipDialog.handleSipResponseEvent : error can not receive redirect response for method: "
                                + method);
            }
            // Here we should have received the to-tag
            ToHeader toHeader = (ToHeader) responseEvent.getResponse()
                    .getHeader(SipConstants.TO_HEADER);
            updateRemoteHeader(toHeader);
            state.handleInviteRedirectResponse(this, responseEvent,
                    sipEventListener);
        } else if (status > 400) {
            logger.trace(
                    "SipDialog.handleSipResponseEvent processing a SIP {} error response. Status code is: {}",
                    status);
            if (method.equals(SipConstants.INVITE)) {
                // Here we should have received the to-tag
                ToHeader toHeader = (ToHeader) responseEvent.getResponse()
                        .getHeader(SipConstants.TO_HEADER);
                updateRemoteHeader(toHeader);
                state.handleInviteErrorResponse(this, responseEvent,
                        sipEventListener);
            } else if (method.equals(SipConstants.BYE)) {
                state.handleByeErrorResponse(this, responseEvent,
                        sipEventListener);
            } else if (method.equals(SipConstants.MESSAGE)) {
                state.handleMessageErrorResponse(this, responseEvent,
                        sipEventListener);
            } else if (method.equals(SipConstants.OPTIONS)) {
                state.handleOptionsErrorResponse(this, responseEvent,
                        sipEventListener);
            } else if (method.equals(SipConstants.UPDATE)) {
                state.handleUpdateErrorResponse(this, responseEvent,
                        sipEventListener);
            } else if (method.equals(SipConstants.NOTIFY)) {
                state.handleNotifyErrorResponse(this, responseEvent,
                        sipEventListener);
            } else if (method.equals(SipConstants.PRACK)) {
                state.handlePrackErrorResponse(this, responseEvent,
                        sipEventListener);
            } else if (method.equals(SipConstants.CANCEL)) {
                state.handleCancelErrorResponse(this, responseEvent,
                        sipEventListener);
            } else if (method.equals(SipConstants.INFO)) {
                state.handleInfoErrorResponse(this, responseEvent,
                        sipEventListener);
            } else if (method.equals(SipConstants.REFER)) {
                state.handleReferErrorResponse(this, responseEvent,
                        sipEventListener);
            } else if (method.equals(SipConstants.SUBSCRIBE)) {
                state.handleSubscribeErrorResponse(this, responseEvent,
                        sipEventListener);
            } else if (method.equals(SipConstants.REGISTER)) {
                state.handleRegisterErrorResponse(this, responseEvent,
                        sipEventListener);
            } else {
                throw new SipClientStackException(
                        "SipDialog.handleSipResponseEvent : error undefined method: "
                                + method);
            }
        }
        // remove the Client Transaction
        if (!method.equals(SipConstants.INVITE)) {
            removeClientTransactionWithMethod(method);
        }

    }

    /**
     * 
     * This method is called by the SIP stack when there's no answer to a
     * message. Note that this is treated differently from an error message.
     * handleTimeoutEvent
     * 
     * @param timeoutEvent
     * @throws SipClientStackException
     */
    private void handleTimeoutEvent(TimeoutEvent timeoutEvent)
            throws SipClientStackException {
        logger.warn("SipDialog.handleTimeoutEvent: NOT IMPLEMENTED !!!!!!!!!!!");
    }

    /**
     * 
     * Send an INVITE
     * 
     * @param toSipUri
     *            specifies the To Sip URI
     * @param content
     *            specifies the Content to add in the INVITE.
     * @param headers
     *            Specifies the headers to add in the message
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendInvite(String toSipUri, Content content,
            Vector<SipHeader> headers, boolean authenticated)
            throws SipClientStackException {
        logger.debug("SipDialog.sendInvite");

        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendInvite(this, toSipUri, content, headers, authenticated);
        this.isInitiator = true;
        checkIfAlive();
    }

    /**
     * 
     * Send an OPTIONS creating a New SipDialog
     * 
     * @param toSipUri
     *            specifies the To Sip URI
     * @param content
     *            specifies the Content to add in the OPTIONS.
     * @param headers
     *            Specifies the headers to add in the message
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendOptions(String toSipUri, Content content,
            Vector<SipHeader> headers, boolean authenticated)
            throws SipClientStackException {
        logger.debug("SipDialog.sendOptions: Creating new SipDialog");

        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendOptions(this, toSipUri, content, headers, authenticated);
        this.isInitiator = true;
        checkIfAlive();
    }

    /**
     * 
     * Send an MESSAGE creating a New SipDialog
     * 
     * @param toSipUri
     *            specifies the To Sip URI
     * @param content
     *            specifies the Content to add in the MESSAGE.
     * @param headers
     *            Specifies the headers to add in the message
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendMessage(String toSipUri, Content content,
            Vector<SipHeader> headers, boolean authenticated)
            throws SipClientStackException {
        logger.debug("SipDialog.sendMessage: Creating new SipDialog");

        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendMessage(this, toSipUri, content, headers, authenticated);
        this.isInitiator = true;
        checkIfAlive();
    }

    /**
     * 
     * Send an REGISTER creating a New SipDialog
     * 
     * @param content
     *            specifies the Content to add in the REGISTER.
     * @param headers
     *            Specifies the headers to add in the message
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendRegister(Content content, Vector<SipHeader> headers,
            long expires) throws SipClientStackException {
        logger.debug("SipDialog.sendRegister");
        setRegisterExpires(expires);
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        if (headers == null) {
            headers = new Vector<SipHeader>();
        }
        headers.add(new SipHeader(SipConstants.EXPIRES_HEADER, "" + expires));
        state.sendRegister(this, content, headers);
        this.isInitiator = true;
        checkIfAlive();
    }

    /**
     * Send a Bye message
     * 
     * 
     * @param headers
     *            specifies the headers to be added in the BYE.
     * @return the Call-ID created
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendBye(Vector<SipHeader> headers)
            throws SipClientStackException {
        logger.debug("SipDialog.sendBye");
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendBye(this, headers);
        checkIfAlive();

    }

    /**
     * Send a Message message
     * 
     * 
     * @param content
     *            specifies the Content to add in the MESSAGE.
     * @param headers
     *            specifies the headers to be added in the MESSAGE.
     * @return the Call-ID created
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendMessage(Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        logger.debug("SipDialog.sendMessage");
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendMessage(this, content, headers);
        checkIfAlive();
    }

    /**
     * Send an Initial Message message
     * 
     * 
     * @param content
     *            specifies the Content to add in the MESSAGE.
     * @param headers
     *            specifies the headers to be added in the MESSAGE.
     * @return the Call-ID created
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendInitialMessage(Content content, Vector<SipHeader> headers)
            throws SipClientStackException {

        logger.debug("SipDialog.sendMessage");
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendMessage(this, content, headers);
        checkIfAlive();
    }

    /**
     * Send a Options message
     * 
     * 
     * @param content
     *            specifies the Content to add in the OPTIONS.
     * @param headers
     *            specifies the headers to be added in the OPTIONS.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendOptions(Content content, Vector<SipHeader> headers)
            throws SipClientStackException {

        logger.debug("SipDialog.sendOptions");
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendOptions(this, content, headers);
        checkIfAlive();
    }

    /**
     * Send a Update message
     * 
     * 
     * @param content
     *            specifies the Content to add in the UPDATE.
     * @param headers
     *            specifies the headers to be added in the UPDATE.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendUpdate(Content content, Vector<SipHeader> headers)
            throws SipClientStackException {

        logger.debug("SipDialog.sendUpdate");
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendUpdate(this, content, headers);
        checkIfAlive();
    }

    /**
     * Send a Subscribe message
     * 
     * 
     * @param content
     *            specifies the Content to add in the SUBSCRIBE.
     * @param headers
     *            specifies the headers to be added in the SUBSCRIBE.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendSubscribe(Content content, Vector<SipHeader> headers)
            throws SipClientStackException {

        logger.debug("SipDialog.sendSubscribe");
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendSubscribe(this, content, headers);
        checkIfAlive();
    }

    /**
     * 
     * Send an OPTIONS creating a New SipDialog
     * 
     * @param toSipUri
     *            specifies the To Sip URI
     * @param content
     *            specifies the Content to add in the SUBSCRIBE.
     * @param headers
     *            Specifies the headers to add in the message
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendSubscribe(String toSipUri, Content content,
            Vector<SipHeader> headers, boolean authenticated)
            throws SipClientStackException {

        logger.debug(
                "SipDialog.sendSubscribe: Creating new SipDialog with the to uri: {}",
                toSipUri);

        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendSubscribe(this, toSipUri, content, headers, authenticated);
        this.isInitiator = true;
        checkIfAlive();
    }

    /**
     * Send a Notify message
     * 
     * 
     * @param content
     *            specifies the Content to add in the NOTIFY.
     * @param headers
     *            specifies the headers to be added in the NOTIFY.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendNotify(Content content, Vector<SipHeader> headers)
            throws SipClientStackException {

        logger.debug("SipDialog.sendNotify");
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendNotify(this, content, headers);
        checkIfAlive();
    }

    /**
     * Send a Prack message
     * 
     * 
     * @param content
     *            specifies the Content to add in the PRACK.
     * @param headers
     *            specifies the headers to be added in the PRACK.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendPrack(Content content, Vector<SipHeader> headers)
            throws SipClientStackException {

        logger.debug("SipDialog.sendPrack");
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendPrack(this, content, headers);
        checkIfAlive();
    }

    /**
     * Send a Ack message
     * 
     * 
     * @param content
     *            specifies the Content to add in the ACK.
     * @param headers
     *            specifies the headers to be added in the ACK.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendAck(Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        logger.debug("SipDialog.sendAck");

        SipDialogState state = SipDialogState.getStateObject(sipDialogState);

        state.sendAck(this, content, headers);

        checkIfAlive();
    }

    /**
     * Send a Cancel message
     * 
     * 
     * @param headers
     *            specifies the headers to be added in the CANCEL.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendCancel(Vector<SipHeader> headers)
            throws SipClientStackException {
        logger.debug("SipDialog.sendCancel");

        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendCancel(this, headers);
        checkIfAlive();
    }

    /**
     * Send a Info message
     * 
     * 
     * @param content
     *            specifies the Content to add in the INFO.
     * @param headers
     *            specifies the headers to be added in the INFO.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendInfo(Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        logger.debug("SipDialog.sendInfo");
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendInfo(this, content, headers);
        checkIfAlive();
    }

    /**
     * Send a Refer message
     * 
     * 
     * @param content
     *            specifies the Content to add in the REFER.
     * @param headers
     *            specifies the headers to be added in the REFER.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendRefer(Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        logger.debug("SipDialog.sendRefer");
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendRefer(this, content, headers);
        checkIfAlive();
    }

    /**
     * Send a INVITE provisional Response
     * 
     * 
     * @param statusCode
     *            Specifies the status code of the response
     * @param content
     *            specifies the Content to add in the INVITE response.
     * @param headers
     *            specifies the headers to be added in the INVITE response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendInviteProvisionalResponse(int statusCode, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {

        logger.debug("SipDialog.sendInviteProvisionalResponse");
        ServerTransaction inviteTransaction = getInitialServerTransaction();

        if (inviteTransaction == null) {
            throw new SipClientStackException(
                    "SipDialog.sendInviteProvisionalResponse Error: can not find a ServerTransaction to sendInviteProvisionalResponse");
        }
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendInviteProvisionalResponse(this, inviteTransaction,
                statusCode, content, headers);
        checkIfAlive();
    }

    /**
     * Send a INVITE redirected Response
     * 
     * 
     * @param statusCode
     *            Specifies the status code of the response
     * @param content
     *            specifies the Content to add in the INVITE response.
     * @param headers
     *            specifies the headers to be added in the INVITE response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendInviteRedirectedResponse(int statusCode, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {
        logger.debug("SipDialog.sendInviteRedirectedResponse");
        ServerTransaction inviteTransaction = getInitialServerTransaction();
        if (inviteTransaction == null) {
            throw new SipClientStackException(
                    "SipDialog.sendInviteRedirectedResponse Error: can not find a ServerTransaction to sendInviteRedirectedResponse");
        }
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendInviteRedirectedResponse(this, inviteTransaction, statusCode,
                content, headers);
        checkIfAlive();
    }

    /**
     * Send a INVITE success Response
     * 
     * 
     * @param statusCode
     *            Specifies the status code of the response
     * @param content
     *            specifies the Content to add in the INVITE response.
     * @param headers
     *            specifies the headers to be added in the INVITE response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendInviteSuccessResponse(int statusCode, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {
        logger.debug("SipDialog.sendInviteSuccessResponse");
        ServerTransaction inviteTransaction = getInitialServerTransaction();
        if (inviteTransaction == null) {
            throw new SipClientStackException(
                    "SipDialog.sendInviteSuccessResponse Error: can not find a ServerTransaction to sendInviteSuccessResponse");
        }
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendInviteSuccessResponse(this, inviteTransaction, statusCode,
                content, headers);
        checkIfAlive();
    }

    /**
     * Send a INVITE success Response
     * 
     * 
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the INVITE response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendInviteErrorResponse(int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {
        logger.debug("SipDialog.sendInviteErrorResponse");
        ServerTransaction inviteTransaction = getInitialServerTransaction();
        if (inviteTransaction == null) {
            throw new SipClientStackException(
                    "SipDialog.sendInviteSuccessResponse Error: can not find a ServerTransaction to sendInviteErrorResponse");
        }
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendInviteErrorResponse(this, inviteTransaction, statusCode,
                headers);
        checkIfAlive();
    }

    /**
     * Send a Bye success response
     * 
     * 
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the BYE response.
     * @return the Call-ID created
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendByeSuccessResponse(int statusCode, Vector<SipHeader> headers)
            throws SipClientStackException {
        logger.debug("SipDialog.sendByeSuccessResponse");
        ServerTransaction serverTransaction = getServerTransactionWithMethod(SipConstants.BYE);
        if (serverTransaction == null) {
            throw new SipClientStackException(
                    "SipDialog.sendByeSuccessResponse Error: can not find a ServerTransaction to sendByeSuccessResponse");
        }
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendByeSuccessResponse(this, serverTransaction, statusCode,
                headers);
        checkIfAlive();
    }

    /**
     * Send a Bye error response
     * 
     * 
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the BYE response.
     * @return the Call-ID created
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendByeErrorResponse(int statusCode, Vector<SipHeader> headers)
            throws SipClientStackException {
        logger.debug("SipDialog.sendByeErrorResponse");
        ServerTransaction serverTransaction = getServerTransactionWithMethod(SipConstants.BYE);
        if (serverTransaction == null) {
            throw new SipClientStackException(
                    "SipDialog.sendByeErrorResponse Error: can not find a ServerTransaction to sendByeErrorResponse");
        }
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendByeErrorResponse(this, serverTransaction, statusCode, headers);
        checkIfAlive();
    }

    /**
     * Send a Message success response
     * 
     * 
     * @param statusCode
     *            Specifies the status code of the response
     * @param content
     *            specifies the Content to add in the MESSAGE response.
     * @param headers
     *            specifies the headers to be added in the MESSAGE response.
     * @return the Call-ID created
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendMessageSuccessResponse(int statusCode, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {
        logger.debug("SipDialog.sendMessageSuccessResponse");
        ServerTransaction serverTransaction = getServerTransactionWithMethod(SipConstants.MESSAGE);
        if (serverTransaction == null) {
            // Try to get the MESSAGE as initial request
            serverTransaction = getInitialServerTransaction();
            if ((serverTransaction == null)
                    || (!Request.MESSAGE.equals(serverTransaction.getRequest()
                            .getMethod()))) {
                throw new SipClientStackException(
                        "SipDialog.sendMessageSuccessResponse Error: can not find a ServerTransaction to sendMessageSuccessResponse");
            }
        }
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendMessageSuccessResponse(this, serverTransaction, statusCode,
                content, headers);
        checkIfAlive();
    }

    /**
     * Send a Message error response
     * 
     * 
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the MESSAGE response.
     * @return the Call-ID created
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendMessageErrorResponse(int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {
        logger.debug("SipDialog.sendMessageErrorResponse");
        ServerTransaction serverTransaction = getServerTransactionWithMethod(SipConstants.MESSAGE);
        if (serverTransaction == null) {
            // Try to get the MESSAGE as initial request
            serverTransaction = getInitialServerTransaction();
            if ((serverTransaction == null)
                    || (!Request.MESSAGE.equals(serverTransaction.getRequest()
                            .getMethod()))) {
                throw new SipClientStackException(
                        "SipDialog.sendMessageErrorResponse Error: can not find a ServerTransaction to sendMessageErrorResponse");
            }

        }
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendMessageErrorResponse(this, serverTransaction, statusCode,
                headers);
        checkIfAlive();
    }

    /**
     * Send a Options success response
     * 
     * 
     * @param statusCode
     *            Specifies the status code of the response
     * @param content
     *            specifies the Content to add in the OPTIONS response.
     * @param headers
     *            specifies the headers to be added in the OPTIONS response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendOptionsSuccessResponse(int statusCode, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {

        logger.debug("SipDialog.sendOptionsSuccessResponse");

        ServerTransaction serverTransaction = getServerTransactionWithMethod(SipConstants.OPTIONS);
        if (serverTransaction == null) {
            // Try to get the OPTION as initial request
            serverTransaction = getInitialServerTransaction();
            if ((serverTransaction == null)
                    || (!Request.OPTIONS.equals(serverTransaction.getRequest()
                            .getMethod()))) {
                throw new SipClientStackException(
                        "SipDialog.sendOptionsSuccessResponse Error: can not find a ServerTransaction to sendOptionsSuccessResponse");
            }
        }
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendOptionsSuccessResponse(this, serverTransaction, statusCode,
                content, headers);
        checkIfAlive();
    }

    /**
     * Send a Options error response
     * 
     * 
     * @param statusCode
     *            Specifies the status code of the response.
     * @param headers
     *            specifies the headers to be added in the OPTIONS response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendOptionsErrorResponse(int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {

        logger.debug("SipDialog.sendOptionsErrorResponse");
        ServerTransaction serverTransaction = getServerTransactionWithMethod(SipConstants.OPTIONS);
        if (serverTransaction == null) {
            // Try to get the OPTION as initial request
            serverTransaction = getInitialServerTransaction();
            if ((serverTransaction == null)
                    || (!Request.OPTIONS.equals(serverTransaction.getRequest()
                            .getMethod()))) {
                throw new SipClientStackException(
                        "SipDialog.sendOptionsErrorResponse Error: can not find a ServerTransaction to sendOptionsErrorResponse");
            }
        }
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendOptionsErrorResponse(this, serverTransaction, statusCode,
                headers);
        checkIfAlive();
    }

    /**
     * Send a Update success response
     * 
     * 
     * @param statusCode
     *            Specifies the status code of the response
     * @param content
     *            specifies the Content to add in the UPDATE response.
     * @param headers
     *            specifies the headers to be added in the UPDATE response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendUpdateSuccessResponse(int statusCode, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {

        logger.debug("SipDialog.sendUpdateSuccessResponse");
        ServerTransaction serverTransaction = getServerTransactionWithMethod(SipConstants.UPDATE);
        if (serverTransaction == null) {
            throw new SipClientStackException(
                    "SipDialog.sendUpdateSuccessResponse Error: can not find a ServerTransaction to sendUpdateSuccessResponse");
        }
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendUpdateSuccessResponse(this, serverTransaction, statusCode,
                content, headers);
        checkIfAlive();
    }

    /**
     * Send a Update error response
     * 
     * 
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the UPDATE response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendUpdateErrorResponse(int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {

        logger.debug("SipDialog.sendUpdateErrorResponse");
        ServerTransaction serverTransaction = getServerTransactionWithMethod(SipConstants.UPDATE);
        if (serverTransaction == null) {
            throw new SipClientStackException(
                    "SipDialog.sendUpdateErrorResponse Error: can not find a ServerTransaction to sendUpdateErrorResponse");
        }
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendUpdateErrorResponse(this, serverTransaction, statusCode,
                headers);
        checkIfAlive();
    }

    /**
     * Send a Notify success Response
     * 
     * 
     * @param statusCode
     *            Specifies the status code of the response
     * @param content
     *            specifies the Content to add in the NOTIFY response.
     * @param headers
     *            specifies the headers to be added in the NOTIFY response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendNotifySuccessResponse(int statusCode, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {

        logger.debug("SipDialog.sendNotifySuccessResponse");
        ServerTransaction serverTransaction = getServerTransactionWithMethod(SipConstants.NOTIFY);
        if (serverTransaction == null) {
            throw new SipClientStackException(
                    "SipDialog.sendNotifySuccessResponse Error: can not find a ServerTransaction to sendNotifySuccessResponse");
        }
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendNotifySuccessResponse(this, serverTransaction, statusCode,
                content, headers);
        checkIfAlive();
    }

    /**
     * Send a Notify error Response
     * 
     * 
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the NOTIFY response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendNotifyErrorResponse(int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {

        logger.debug("SipDialog.sendNotifyErrorResponse");
        ServerTransaction serverTransaction = getServerTransactionWithMethod(SipConstants.NOTIFY);
        if (serverTransaction == null) {
            throw new SipClientStackException(
                    "SipDialog.sendNotifyErrorResponse Error: can not find a ServerTransaction to sendNotifyErrorResponse");
        }
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendNotifyErrorResponse(this, serverTransaction, statusCode,
                headers);
        checkIfAlive();
    }

    /**
     * Send a Prack success response
     * 
     * 
     * @param statusCode
     *            Specifies the status code of the response
     * @param content
     *            specifies the Content to add in the PRACK response.
     * @param headers
     *            specifies the headers to be added in the PRACK response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendPrackSuccessResponse(int statusCode, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {

        logger.debug("SipDialog.sendPrackSuccessResponse");
        ServerTransaction serverTransaction = getServerTransactionWithMethod(SipConstants.PRACK);
        if (serverTransaction == null) {
            throw new SipClientStackException(
                    "SipDialog.sendPrackSuccessResponse Error: can not find a ServerTransaction to sendPrackSuccessResponse");
        }
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendPrackSuccessResponse(this, serverTransaction, statusCode,
                content, headers);
        checkIfAlive();
    }

    /**
     * Send a Prack error response
     * 
     * 
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the PRACK response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendPrackErrorResponse(int statusCode, Vector<SipHeader> headers)
            throws SipClientStackException {

        logger.debug("SipDialog.sendPrackErrorResponse");
        ServerTransaction serverTransaction = getServerTransactionWithMethod(SipConstants.PRACK);
        if (serverTransaction == null) {
            throw new SipClientStackException(
                    "SipDialog.sendPrackErrorResponse Error: can not find a ServerTransaction to sendPrackErrorResponse");
        }
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendPrackErrorResponse(this, serverTransaction, statusCode,
                headers);
        checkIfAlive();
    }

    /**
     * Send a Cancel success response
     * 
     * 
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the CANCEL response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendCancelSuccessResponse(int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {

        logger.debug("SipDialog.sendCancelSuccessResponse");
        ServerTransaction serverTransaction = getServerTransactionWithMethod(SipConstants.CANCEL);
        if (serverTransaction == null) {
            throw new SipClientStackException(
                    "SipDialog.sendCancelSuccessResponse Error: can not find a ServerTransaction to sendCancelSuccessResponse");
        }
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendCancelSuccessResponse(this, serverTransaction, statusCode,
                headers);
        checkIfAlive();
    }

    /**
     * Send a Cancel error response
     * 
     * 
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the CANCEL response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendCancelErrorResponse(int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {

        logger.debug("SipDialog.sendCancelErrorResponse");
        ServerTransaction serverTransaction = getServerTransactionWithMethod(SipConstants.CANCEL);
        if (serverTransaction == null) {
            throw new SipClientStackException(
                    "SipDialog.sendCancelErrorResponse Error: can not find a ServerTransaction to sendCancelErrorResponse");
        }
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendCancelErrorResponse(this, serverTransaction, statusCode,
                headers);
        checkIfAlive();
    }

    /**
     * Send a Info success response
     * 
     * 
     * @param statusCode
     *            Specifies the status code of the response
     * @param content
     *            specifies the Content to add in the INFO response.
     * @param headers
     *            specifies the headers to be added in the INFO response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendInfoSuccessResponse(int statusCode, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {

        logger.debug("SipDialog.sendInfoSuccessResponse");
        ServerTransaction serverTransaction = getServerTransactionWithMethod(SipConstants.INFO);
        if (serverTransaction == null) {
            throw new SipClientStackException(
                    "SipDialog.sendInfoSuccessResponse Error: can not find a ServerTransaction to sendInfoSuccessResponse");
        }
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendInfoSuccessResponse(this, serverTransaction, statusCode,
                content, headers);
        checkIfAlive();
    }

    /**
     * Send a Info error response
     * 
     * 
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the INFO response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendInfoErrorResponse(int statusCode, Vector<SipHeader> headers)
            throws SipClientStackException {

        logger.debug("SipDialog.sendInfoErrorResponse");
        ServerTransaction serverTransaction = getServerTransactionWithMethod(SipConstants.INFO);
        if (serverTransaction == null) {
            throw new SipClientStackException(
                    "SipDialog.sendInfoErrorResponse Error: can not find a ServerTransaction to sendInfoErrorResponse");
        }
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendInfoErrorResponse(this, serverTransaction, statusCode,
                headers);

        checkIfAlive();
    }

    /**
     * Send a Refer success response
     * 
     * 
     * @param statusCode
     *            Specifies the status code of the response
     * @param content
     *            specifies the Content to add in the REFER response.
     * @param headers
     *            specifies the headers to be added in the REFER response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendReferSuccessResponse(int statusCode, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {

        logger.debug("SipDialog.sendReferSuccessResponse");
        ServerTransaction serverTransaction = getServerTransactionWithMethod(SipConstants.REFER);
        if (serverTransaction == null) {
            throw new SipClientStackException(
                    "SipDialog.sendReferSuccessResponse Error: can not find a ServerTransaction to sendReferSuccessResponse");
        }
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendReferSuccessResponse(this, serverTransaction, statusCode,
                content, headers);
        checkIfAlive();
    }

    /**
     * Send a Refer error response
     * 
     * 
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the REFER response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendReferErrorResponse(int statusCode, Vector<SipHeader> headers)
            throws SipClientStackException {

        logger.debug("SipDialog.sendReferErrorResponse");
        ServerTransaction serverTransaction = getServerTransactionWithMethod(SipConstants.REFER);
        if (serverTransaction == null) {
            throw new SipClientStackException(
                    "SipDialog.sendReferErrorResponse Error: can not find a ServerTransaction to sendReferErrorResponse");
        }
        SipDialogState state = SipDialogState.getStateObject(sipDialogState);
        state.sendReferErrorResponse(this, serverTransaction, statusCode,
                headers);

        checkIfAlive();
    }

    /**
     * Checks if this dialog is still alive (Not Closed)
     * 
     */
    private void checkIfAlive() {
        if (sipDialogState == DialogState.CLOSED) {
            if (logger.isInfoEnabled())
                logger.info("Sip Dialog "
                        + getCallId()
                        + " is closed => Remove from Sip Calls of the Sip Phone");
            sipPhone.removeSipDialog(this);
            sipEventListener.sipDialogClosed(getCallId());
        }
    }

    /**
     * Add a new SipStackEvent in the Event List
     * 
     * @param sipStackEvent
     */
    public void addSipStackEvent(EventObject sipStackEvent) {

        logger.debug("SipDialog.addSipStackEvent type: {}",
                sipStackEvent.getClass());
        sipStackEventList.addEvent(sipStackEvent);

    }

    /**
     * Get the Sip Call-Id
     * 
     * @return the Sip call ID
     */
    public String getCallId() {
        return callId;
    }

    /**
     * Set the Sip Call ID
     * 
     * @param callId
     *            defines the new Call ID
     */
    public void setCallId(String callId) {
        this.callId = callId;
    }

    /*
     * public String getCallTag () { return callTag; }
     * 
     * public void setCallTag ( String callTag) { this.callTag = callTag; }
     */

    public Address getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(Address contactAddress) {
        this.contactAddress = contactAddress;
    }

    /**
     * Get the local FromToHeader to build From or To headers
     * 
     * @return the local FromToHeader to build From or To headers
     */
    public FromToHeader getLocalHeader() {
        return localHeader;
    }

    /**
     * Set the local FromToHeader helpful to build From or To headers
     * 
     * @param localHeader
     *            Specifies the local FromToHeader helpful to build From or To
     *            headers
     */
    public void setLocalHeader(FromToHeader localHeader) {
        this.localHeader = localHeader;
    }

    /**
     * Set the contactHeader received in the initial request or in the final
     * respons of an initial request
     * 
     * @param contactHeader
     */
    public void setContactHeaderReceived(ContactHeader contactHeaderReceived) {

        this.contactHeader = new SipHeader(SipConstants.CONTACT_HEADER,
                contactHeaderReceived.getAddress().getURI().toString());
    }

    /**
     * Get the contactHeader received in the initial request or in the final
     * respons of an initial request
     * 
     * @return the contactHeader received in the initial request or in the final
     *         respons of an initial request
     */
    public SipHeader getContactHeaderReceived() {
        return this.contactHeader;

    }

    /**
     * Get the remote FromToHeader to build From or To headers
     * 
     * @return the remote FromToHeader to build From or To headers
     */
    public FromToHeader getRemoteHeader() {
        return remoteHeader;
    }

    /**
     * Set the remote FromToHeader helpful to build From or To headers
     * 
     * @param remoteHeader
     *            Specifies the remote FromToHeader helpful to build From or To
     *            headers
     */
    public void setRemoteHeader(FromToHeader remoteHeader) {
        this.remoteHeader = remoteHeader;
    }

    /**
     * Get the Remote Display Name
     * 
     * @return the Remote Display Name
     */
    public String getRemoteDisplayName() {
        if (remoteHeader == null)
            return null;
        return remoteHeader.getAddress().getDisplayName();
    }

    /**
     * Get the Remote Number
     * 
     * @return the Remote Number
     */
    public String getRemoteNumber() {
        if (remoteHeader == null)
            return null;
        return remoteHeader.getAddress().getURI().toString();
    }

    /**
     * Get the Dialog
     * 
     * @return the Dialog
     */

    public Dialog getDialog() {
        // Check if the initialServerTransaction is not null
        /*
         * if(initialServerTransation!=null){ return
         * initialServerTransation.getDialog();
         * 
         * }
         */
        return dialog;
    }

    /**
     * Set the Dialog
     * 
     * @param dialog
     *            Specifies the Dialog
     */

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    /**
     * get the SipDialogState
     * 
     * @return the SipDialogState
     */
    public DialogState getSipDialogState() {
        return sipDialogState;
    }

    /**
     * Set the SipDialogState
     * 
     * @param sipDialogState
     *            the new SipDialogState
     */
    public void setSipDialogState(DialogState sipDialogState) {
        if (this.sipDialogState != sipDialogState) {
            logger.info(
                    "SipDialog.setState:\n Old State: {}   >>>>>>   New State: {}",
                    this.sipDialogState, sipDialogState);
            this.sipDialogState = sipDialogState;
        }
    }

    // //////// Create requests Methods //////////

    /**
     * Create an Initial Request Transaction
     * 
     * @param method
     *            specifies the initial request type
     * @param toSipUri
     *            specifies the To Sip URI
     * @param content
     *            specifies the Content to add in the initial request.
     * @param headers
     *            specifies the additionnal Headers to be added.
     * @param addOutboundProxy
     *            Specifies if the outbound proxy has to be added.
     * @throws SipClientStackException
     *             In case of error.
     */
    private void createAndSendInitialRequest(String method, String toSipUri,
            Vector<SipHeader> headers, Content content,
            boolean addOutboundProxy, boolean authenticated)
            throws SipClientStackException {
        try {
            callTag = sipPhone.generateNewTag();
            /*
             * String localName = sipPhone.getSipProfile().get(); String
             * localDomain = sipPhone.getSipProfile().getUserDomain();
             */
            String protocol = sipPhone.getSipProfile().getProtocol();
            int port = sipPhone.getSipProfile().getUdpListenPort();
            if (SipProfile.TCP.equals(protocol)) {
                port = sipPhone.getSipProfile().getTcpListenPort();
            }

            // Generate the from Header
            // SipURI sipUri = addressFactory.createSipURI( );
            Address localAddress = addressFactory.createAddress(sipPhone
                    .getSipUri());
            localAddress.setDisplayName(sipPhone.getSipProfile()
                    .getUserDisplayName());
            FromHeader fromHeader = headerFactory.createFromHeader(
                    localAddress, callTag);
            localHeader = new FromToHeader(fromHeader);

            SipClient remoteClient = null;
            if (toSipUri == null) {
                remoteClient = new SipClient(sipPhone.getSipUri());
            } else {
                remoteClient = new SipClient(toSipUri);
            }
            if (headers == null) {
                headers = new Vector<SipHeader>();
            }
            if ((authenticated) && (sipPhone.hasNonce())) {
                headers.add(new SipHeader(
                        SipConstants.PROXY_AUTHORIZATION_HEADER));
            }

            logger.trace(
                    "SipDialog.createAndSendInitialRequest: \n ----- Callee Address -----\n{}\n ------------------------------\n",
                    remoteClient.getString());

            // Generate the to Header
            SipURI toUri = (SipURI) addressFactory.createURI(remoteClient
                    .getFullUri());

            /*
             * SipURI toUri = addressFactory.createSipURI(
             * remoteClient.getUserName(), remoteClient.getUserDomain() + ":" +
             * remoteClient.getUserPort());
             */

            logger.trace(
                    "SipDialog.createAndSendInitialRequest: Sip To Uri created: {}",
                    toUri.toString());

            Address remoteAddress = addressFactory.createAddress(toUri);

            // remoteAddress.setDisplayName( remotePartyName);
            ToHeader toHeader = headerFactory.createToHeader(remoteAddress,
                    null);
            remoteHeader = new FromToHeader(toHeader);

            // Generate the request uri
            /*
             * SipURI requestURI = addressFactory.createSipURI(
             * remoteClient.getUserName(), remoteClient.getUserDomain() + ":" +
             * remoteClient.getUserPort());
             */
            SipURI requestURI = (SipURI) addressFactory.createURI(remoteClient
                    .getFullUri());
            requestURI
                    .setTransportParam(sipPhone.getSipProfile().getProtocol());

            // generate the Call-ID Header
            callIdHeader = sipLayer.getUdpSipProvider().getNewCallId();
            // generate the CSeq Header
            CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(
                    currentCSeqHeaderIndex, method);

            ArrayList<ViaHeader> viaHeaders = sipLayer.getViaHeaders();

            Request request = messageFactory.createRequest(requestURI, method,
                    callIdHeader, cSeqHeader, fromHeader, toHeader, viaHeaders,
                    sipLayer.getMaxForwardsHeader());

            request.addHeader(sipLayer.getContactHeader());

            if ((addOutboundProxy) && (sipLayer.getOutboundProxy() != null)) {
                SipURI routeUri = addressFactory.createSipURI(null,
                        sipLayer.getOutboundProxy());
                routeUri.setTransportParam(protocol);
                routeUri.setLrParam();

                Address route_address = addressFactory.createAddress(routeUri);
                request.addHeader(headerFactory
                        .createRouteHeader(route_address));
            }
            if (content != null) {
                // Set the content
                ContentTypeHeader contentTypeHeader = headerFactory
                        .createContentTypeHeader(content.getContentType(),
                                content.getContentSubType());
                request.setContent(content.getContent(), contentTypeHeader);
            }
            addHeaders(request, headers, method);
            logger.trace(
                    "SipDialog.createAndSendInitialRequest: Request created:  {}",
                    request.toString());

            ClientTransaction clientTransaction = sipLayer.getUdpSipProvider()
                    .getNewClientTransaction(request);

            clientTransaction.sendRequest();

            if (logger.isInfoEnabled())
                logger.info("<------------- {} SENT ----------------\n{}",
                        method, request.toString());

            setInitialClientTransaction(clientTransaction);

            setDialog(clientTransaction.getDialog());

            String callId = ((CallIdHeader) request.getHeader(CALL_ID_HEADER))
                    .getCallId();
            setCallId(callId);

        }

        catch (ParseException e) {
            logger.error(
                    "SipDialog.createAndSendInitialRequest: ParseException: \n",
                    e);
            throw new SipClientStackException(
                    "SipDialog.createAndSendInitialRequest: ParseException error occured: ",
                    e);
        } catch (InvalidArgumentException e) {
            logger.error(
                    "SipDialog.createAndSendInitialRequest: InvalidArgumentException: \n",
                    e);
            throw new SipClientStackException(
                    "SipDialog.createAndSendInitialRequest: InvalidArgumentException error occured: ",
                    e);
        } catch (TransactionUnavailableException e) {
            logger.error(
                    "SipDialog.createAndSendInitialRequest: TransactionUnavailableException: \n",
                    e);
            throw new SipClientStackException(
                    "SipDialog.createAndSendInitialRequest: TransactionUnavailableException error occured: ",
                    e);
        } catch (SipException e) {
            logger.error(
                    "SipDialog.createAndSendInitialRequest: SipException: \n",
                    e);
            throw new SipClientStackException(
                    "SipDialog.createAndSendInitialRequest: SipException error occured: ",
                    e);
        }

    }

    /**
     * Create a Sip Invite Transaction
     * 
     * @param toSipUri
     *            specifies the To Sip URI
     * @param content
     *            specifies the Content to add in the INVITE.
     * @param headers
     *            specifies the additionnal Headers to be added.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void createAndSendInvite(String toSipUri, Vector<SipHeader> headers,
            Content content, boolean authenticated)
            throws SipClientStackException {
        createAndSendInitialRequest(Request.INVITE, toSipUri, headers, content,
                true, authenticated);
    }

    /**
     * Create a Sip Options Transaction
     * 
     * @param toSipUri
     *            specifies the To Sip URI
     * @param content
     *            specifies the Content to add in the OPTIONS.
     * @param headers
     *            specifies the additionnal Headers to be added.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void createAndSendInitialOptions(String toSipUri,
            Vector<SipHeader> headers, Content content, boolean authenticated)
            throws SipClientStackException {
        createAndSendInitialRequest(Request.OPTIONS, toSipUri, headers,
                content, true, authenticated);
    }

    /**
     * Create a Sip Register Transaction
     * 
     * @param toSipUri
     *            specifies the To Sip URI
     * @param content
     *            specifies the Content to add in the OPTIONS.
     * @param headers
     *            specifies the additionnal Headers to be added.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void createAndSendInitialRegister(Vector<SipHeader> headers,
            Content content) throws SipClientStackException {
        createAndSendInitialRequest(Request.REGISTER, null, headers, content,
                true, false);
    }

    /**
     * Create a Sip Message Transaction
     * 
     * @param toSipUri
     *            specifies the To Sip URI
     * @param content
     *            specifies the Content to add in the MESSAGE.
     * @param headers
     *            specifies the additionnal Headers to be added.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void createAndSendInitialMessage(String toSipUri,
            Vector<SipHeader> headers, Content content, boolean authenticated)
            throws SipClientStackException {
        createAndSendInitialRequest(Request.MESSAGE, toSipUri, headers,
                content, true, authenticated);
    }

    /**
     * Create a Sip Subscribe Transaction
     * 
     * @param toSipUri
     *            specifies the To Sip URI
     * @param content
     *            specifies the Content to add in the SUBSCRIBE.
     * @param headers
     *            specifies the additionnal Headers to be added.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void createAndSendInitialSubscribe(String toSipUri,
            Vector<SipHeader> headers, Content content, boolean authenticated)
            throws SipClientStackException {
        createAndSendInitialRequest(Request.SUBSCRIBE, toSipUri, headers,
                content, true, authenticated);
    }

    /**
     * Create a Response
     * 
     * @param serverTransaction
     * @param statusCode
     * @param content
     * @param headers
     * @return Response
     * @throws SipClientStackException
     */
    public void createAndSendResponse(ServerTransaction serverTransaction,
            int statusCode, Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        try {
            
                logger.debug("SipDialog.createAndSendResponse: Start...");
            Response response = null;
            boolean sendReliably = false;
            if ((SipTools.contain100relParameter(headers))
                    && (statusCode > 100) && (statusCode < 200)) {
                
                    logger.debug("SipDialog.createAndSendResponse: Response must be sent reliably.");
                response = dialog.createReliableProvisionalResponse(statusCode);
                sendReliably = true;
            } else {
                
                    logger.debug("SipDialog.createAndSendResponse: Response must not be sent reliably.");
                
                    logger.debug("SipDialog.createAndSendResponse: serverTransaction is null?!: {}", (serverTransaction == null));
                if (serverTransaction != null) {
                    
                        logger.debug("SipDialog.createAndSendResponse: Request is null?!: {}", (serverTransaction.getRequest() == null));
                }

                response = messageFactory.createResponse(statusCode,
                        serverTransaction.getRequest());
            }

            // add the Contact Header if not contained in the list of Headers
            response.addHeader(sipLayer.getContactHeader());

            // Change the toHeader
            ToHeader toHeader = buildToHeaderForSipResponses();

            response.setHeader(toHeader);

            // add the Via Header
            // response.addHeaders( sipLayer.getViaHeaders());
            if (content != null) {
                // Set the content
                ContentTypeHeader contentTypeHeader = headerFactory
                        .createContentTypeHeader(content.getContentType(),
                                content.getContentSubType());
                response.setContent(content.getContent(), contentTypeHeader);
            }

            addHeaders(response, headers, serverTransaction.getRequest()
                    .getMethod());
            String method = serverTransaction.getRequest().getMethod();

            // Check if it is a provisional response
            if (sendReliably) {

                dialog.sendReliableProvisionalResponse(response);
            } else {

                serverTransaction.sendResponse(response);
            }

            if (logger.isInfoEnabled())
                logger.info("<------------- {} {} SENT ----------------\n{}",statusCode,method,response.toString());
            // If the response is not a INVITE response => Remove the
            // Transaction from the list
            if (!SipConstants.INVITE.equals(method)) {
                removeServerTransactionWithMethod(method);
            }
        } catch (ParseException e) {
            logger.error("SipDialog.createResponse: ParseException: \n",e);
            throw new SipClientStackException(
                    "SipDialog.createAndSendResponse: ParseException error occured: ",
                    e);
        } catch (InvalidArgumentException e) {
            logger.error("SipDialog.createResponse: InvalidArgumentException: \n",e);
            throw new SipClientStackException(
                    "SipDialog.createAndSendResponse: InvalidArgumentException error occured: ",
                    e);
        } catch (SipException e) {
            logger.error("SipDialog.createResponse: SipException: \n",e);
            throw new SipClientStackException(
                    "SipDialog.createAndSendResponse: SipException error occured: ",
                    e);
        }
    }

    /**
     * Create and Send a Request
     * 
     * @param method
     * @param content
     * @param headers
     * @throws SipClientStackException
     */
    public void createAndSendRequest(String method, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {

        try {

            logger.debug("SipDialog.createAndSendRequest: method: {} Start...",
                    method);

            logger.debug(
                    "SipDialog.createAndSendRequest: dialog is null? : {}",
                    (dialog == null));

            Request request = null;
            if (SipConstants.PRACK.equals(method)) {
                // The request to be created is a PRACK => Not the same method
                // than other requests
                // Get the current provisionnal response
                if (currentProvisionalResponse == null)
                    throw new SipClientStackException(
                            "SipDialog.createAndSendRequest: error, can not find the provisional response to send a PRACK");
                request = dialog.createPrack(currentProvisionalResponse);

            } else if (SipConstants.CANCEL.equals(method)) {
                // The request to be created is a CANCEL => Not the same method
                // than other requests
                // Get the current invite Client Transaction

                if (intialClientTransation == null)
                    throw new SipClientStackException(
                            "SipDialog.createAndSendRequest: error, can not find the invite Client Transation to send a CANCEL");
                request = intialClientTransation.createCancel();
                addHeaders(request, headers, method);
                // Change the toHeader
                /*
                 * ToHeader toHeader = buildToHeaderForSipResponses();
                 * request.setHeader( toHeader);
                 */

                ClientTransaction clientTransaction = sipLayer
                        .getUdpSipProvider().getNewClientTransaction(request);
                clientTransaction.sendRequest();

                logger.info("<------------- {} SENT ----------------\n{}",
                        method, request.toString());
                putClientTransactionWithMethod(method, clientTransaction);
                return;
            } else {

                request = dialog.createRequest(method);

                // add the Contact Header
                // TODO Check if the Contact header is necesary for the other
                // requests

                // Here we remove the adding of Contact header => Does not work
                // for REFER requests
                // request.addHeader( sipLayer.getContactHeader());
                logger.warn("SipDialog.createAndSendRequest Add of Contact Header has been removed since the REFER methods does not work. Maybe it can cause some bugs for other methods");
            }

            if (content != null) {
                // Set the content
                ContentTypeHeader contentTypeHeader = headerFactory
                        .createContentTypeHeader(content.getContentType(),
                                content.getContentSubType());
                if ("dtmf-relay".equals(content.getContentSubType())) {

                    ((String) content.getContent()).replace("\n", "\r\n");
                    logger.debug(
                            "Content Subtype is dtmf-relay: new modified content is: {}",
                            content.getContent());
                }

                request.setContent(content.getContent(), contentTypeHeader);
            }
            addHeaders(request, headers, method);

            ClientTransaction clientTransaction = sipLayer.getUdpSipProvider()
                    .getNewClientTransaction(request);
            if (!SipConstants.INVITE.equals(method)) {
                putClientTransactionWithMethod(method, clientTransaction);
            }

            dialog.sendRequest(clientTransaction);
            logger.info("<------------- {} SENT ----------------\n{}",method, request.toString());
            // If the response is not a INVITE response => Remove the
            // Transaction from the list

        } catch (ParseException e) {
            logger.error("SipDialog.createAndSendRequest: ParseException: \n",e);
            throw new SipClientStackException(
                    "SipDialog.createAndSendRequest: ParseException error occured: ",
                    e);
        }

        catch (SipException e) {
            logger.error("SipDialog.createAndSendRequest: SipException: \n",e);
            throw new SipClientStackException(
                    "SipDialog.createAndSendRequest: SipException error occured: ",
                    e);
        }
    }

    /**
     * Create and Send a ReInvite
     * 
     * @param content
     *            Specifies the Content to add in the REINVITE message
     * @param headers
     *            Specifies the Headers to add in the REINVITE message
     * @throws SipClientStackException
     */
    public void createAndSendReInvite(Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        try {

            logger.debug("SipDialog.createAndSendReInvite: Start...");
            Request request = null;

            request = dialog.createRequest(SipConstants.INVITE);
            // add the Contact Header
            // TODO Check if the Contact header is necesary for the other
            // requests
            // request.addHeader( sipLayer.getContactHeader());

            if (content != null) {
                // Set the content
                ContentTypeHeader contentTypeHeader = headerFactory
                        .createContentTypeHeader(content.getContentType(),
                                content.getContentSubType());
                request.setContent(content.getContent(), contentTypeHeader);
            }
            addHeaders(request, headers, "INVITE");

            ClientTransaction clientTransaction = sipLayer.getUdpSipProvider()
                    .getNewClientTransaction(request);

            setInitialClientTransaction(clientTransaction);
            dialog.sendRequest(clientTransaction);
            if (logger.isInfoEnabled())
                logger.info("<------------- RE-INVITE SENT ----------------\n{}", request.toString());

        } catch (ParseException e) {
            logger.error("SipDialog.createAndSendReInvite: ParseException: \n",e);
            throw new SipClientStackException(
                    "SipDialog.createAndSendReInvite: ParseException error occured: ",
                    e);
        }

        catch (SipException e) {
            logger.error("SipDialog.createAndSendReInvite: SipException: \n",e);
            throw new SipClientStackException(
                    "SipDialog.createAndSendReInvite: SipException error occured: ",
                    e);
        }
    }

    /**
     * Create a ACK
     * 
     * @param serverTransaction
     * @param statusCode
     * @param content
     * @param headers
     * @return Response
     * @throws SipClientStackException
     */
    public void createAndSendAck(Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        try {
            // first get the Invite Transaction
            ClientTransaction inviteTransaction = getInitialClientTransaction();
            if (inviteTransaction == null)
                throw new SipClientStackException(
                        "SipDialog.createAck error: no Invite Client Transaction Found to send the ACK request");
            CSeqHeader inviteCSeqHeader = (CSeqHeader) inviteTransaction
                    .getRequest().getHeader(SipConstants.CSEQ_HEADER);
            Request ackRequest = dialog.createAck(inviteCSeqHeader
                    .getSeqNumber());
            if (content != null) {
                // Set the content
                ContentTypeHeader contentTypeHeader = headerFactory
                        .createContentTypeHeader(content.getContentType(),
                                content.getContentSubType());
                ackRequest.setContent(content.getContent(), contentTypeHeader);
            }
            addHeaders(ackRequest, headers, "ACK");

            dialog.sendAck(ackRequest);
            logger.info("<------------- ACK REQUEST SENT ----------------\n{}", ackRequest.toString());
        } catch (ParseException e) {
            logger.error("SipDialog.createAck: ParseException: \n",e);
            throw new SipClientStackException(
                    "SipDialog.createAck: ParseException error occured: ", e);
        } catch (InvalidArgumentException e) {
            logger.error("SipDialog.createAck: InvalidArgumentException: \n",e);
            throw new SipClientStackException(
                    "SipDialog.createAck: InvalidArgumentException error occured: ",
                    e);
        } catch (SipException e) {
            logger.error("SipDialog.createAck: SipException: \n",e);
            throw new SipClientStackException(
                    "SipDialog.createAck: SipException error occured: ", e);
        }
    }

    /**
     * Add the Headers in the Message
     * 
     * @param message
     *            Specifies the Messages in which the Headers are to be added
     * @param headers
     *            Specifies the Headers to be added
     * @throws ParseException
     */
    private void addHeaders(Message message, Vector<SipHeader> headers,
            String method) throws ParseException {
        logger.trace("SipDialog.addHeaders: start...");

        if (headers != null) {
            if (logger.isTraceEnabled())
                logger.trace("SipDialog.addHeaders: headers size: {}", headers.size());
            for (int i = 0; i < headers.size(); i++) {

                String headerName = headers.get(i).getHeaderName();

                String headerValue = headers.get(i).getHeaderValue();
                
                    logger.trace("SipDialog.addHeaders: header name: {}", headerName);

                if (SipConstants.CONTACT_HEADER.equals(headerName)) {
                    
                        logger.trace("SipDialog.addHeaders: Contact Header changed to: {}", headerValue);
                    Header contactHeader = headerFactory.createHeader(
                            headerName, headerValue);
                    message.setHeader(contactHeader);

                } else if (SipConstants.AUTHORIZATION_HEADER.equals(headerName)) {
                    
                        logger.trace("SipDialog.addHeaders: AUTHORIZATION_HEADER");
                    AuthorizationHeader authorizationHeader = AuthenticationUtils
                            .createAuthorizationHeader(logger, sipPhone);
                    message.setHeader(authorizationHeader);

                } else if (SipConstants.PROXY_AUTHORIZATION_HEADER
                        .equals(headerName)) {
                    
                        logger.trace("SipDialog.addHeaders: PROXY_AUTHORIZATION_HEADER {}", headerValue);
                    ProxyAuthorizationHeader proxyAuthorizationHeader = AuthenticationUtils
                            .createProxyAuthorizationHeader(logger, sipPhone,
                                    method);
                    message.setHeader(proxyAuthorizationHeader);

                } else {
                    String[] values = headerValue.split(",");
                    for (int j = 0; j < values.length; j++) {
                        if ((SipConstants.REQUIRE_HEADER.equals(headerName))
                                && (SipConstants.PARAM_100REL.equals(values[j]))) {

                        }

                        else {

                            Header header = headerFactory.createHeader(
                                    headerName, values[j]);
                            message.addHeader(header);

                        }

                    }
                }
            }

        } else {
            logger.trace("SipDialog.addHeaders: headers is null.");

        }
        logger.trace("SipDialog.addHeaders: End.");
    }

    /**
     * Get if the SipDialog is the Initiator of the Sip Call.
     * 
     * @return true if the SipDialog is the Initiator of the Sip Call.
     */
    public boolean isInitiator() {
        return isInitiator;
    }

    /**
     * Set if the SipDialog is the Initiator of the Sip Call.
     * 
     * @param isInitiator
     *            specifies the new value
     */
    public void setInitiator(boolean isInitiator) {
        this.isInitiator = isInitiator;
    }

    /**
     * Set the current initial Client Transaction beeing processed
     * 
     * @param intialClientTransation
     *            specifies the invite Client Transation
     */
    public void setInitialClientTransaction(
            ClientTransaction intialClientTransation) {
        this.intialClientTransation = intialClientTransation;
    }

    /**
     * Set the current invite Server Transaction beeing processed
     * 
     * @param initialServerTransation
     *            specifies the initial Server Transation
     */
    public void setInitialServerTransaction(
            ServerTransaction initialServerTransation) {
        this.initialServerTransation = initialServerTransation;
    }

    /**
     * Get the current initial Client Transaction beeing processed
     * 
     * @return the initial Client Transation
     */
    public ClientTransaction getInitialClientTransaction() {
        return intialClientTransation;
    }

    /**
     * Get the current initial Server Transaction beeing processed
     * 
     * @return the initial Server Transation
     */
    public ServerTransaction getInitialServerTransaction() {
        return initialServerTransation;
    }

    /**
     * Get the Server Transaction with the Method
     * 
     * @param method
     *            Specifies the Method
     * @return the Server Transation if found
     */
    public ServerTransaction getServerTransactionWithMethod(String method) {
        return serverTransactions.get(method);
    }

    /**
     * Get the Client Transaction with the Method
     * 
     * @param method
     *            Specifies the Method
     * @return the Client Transation if found
     */
    public ClientTransaction getClientTransactionWithMethod(String method) {
        return clientTransactions.get(method);
    }

    /**
     * Remove the Server Transaction with the Method
     * 
     * @param method
     *            Specifies the Method
     */
    public void removeServerTransactionWithMethod(String method) {
        serverTransactions.remove(method);
    }

    /**
     * Remove the Client Transaction with the Method
     * 
     * @param method
     *            Specifies the Method
     */
    public void removeClientTransactionWithMethod(String method) {
        clientTransactions.remove(method);
    }

    /**
     * Put a Server Transaction with its Method as identifier
     * 
     * @param method
     *            Specifies the Method
     * @param serverTransaction
     *            Specifies the Server Transaction to put
     * @return the Server Transation if found
     */
    public void putServerTransactionWithMethod(String method,
            ServerTransaction serverTransaction) {
        serverTransactions.put(method, serverTransaction);
    }

    /**
     * Put a Client Transaction with its Method as identifier
     * 
     * @param method
     *            Specifies the Method
     * @param clientTransaction
     *            Specifies the Client Transaction to put
     * @return the Client Transation if found
     */
    public void putClientTransactionWithMethod(String method,
            ClientTransaction clientTransaction) {
        clientTransactions.put(method, clientTransaction);
    }

    /**
     * Get the Sip Phone containing this SipDialog
     * 
     * @return the Sip Phone containing this SipDialog
     */
    public SipPhone getSipPhone() {
        return sipPhone;
    }

    /**
     * Update The Remte Header with the to tag
     * 
     * @param toHeader
     *            Specifies the toHeader
     * @throws SipClientStackException
     */
    private void updateRemoteHeader(ToHeader toHeader)
            throws SipClientStackException {
        if (remoteHeader == null)
            throw new SipClientStackException(
                    "SipDialog.updateRemoteHeader error: Remote Header is null");
        remoteHeader.setTag(toHeader.getTag());

    }

    /**
     * Build the ToHeader for Sip Responses
     * 
     * @return the ToHeader
     * @throws ParseException
     */
    private ToHeader buildToHeaderForSipResponses() throws ParseException {
        ToHeader toHeader = headerFactory.createToHeader(
                localHeader.getAddress(), localHeader.getTag());
        for (int i = 0; i < localHeader.getParameters().size(); i++) {
            toHeader.setParameter(localHeader.getParameters().get(i).getName(),
                    localHeader.getParameters().get(i).getValue());
        }
        return toHeader;
    }

    /**
     * Build and get a Replaces Header
     * 
     * @return a Replaces Header
     */
    public SipHeader getReplacesHeader() {

        SipHeader replacesHeader = new SipHeader(SipConstants.REPLACES_HEADER);
        String replacesHeaderValue = callId + ";to-tag="
                + remoteHeader.getTag() + ";from-tag=" + localHeader.getTag();
        replacesHeader.setHeaderValue(replacesHeaderValue);
        return replacesHeader;

    }

    public long getRegisterExpires() {
        return registerExpires;
    }

    public void setRegisterExpires(long registerExpires) {
        this.registerExpires = registerExpires;
    }

}
