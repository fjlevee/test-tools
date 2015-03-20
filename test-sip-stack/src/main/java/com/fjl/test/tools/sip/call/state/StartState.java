package com.fjl.test.tools.sip.call.state;

import java.text.ParseException;
import java.util.Vector;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.header.ContactHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.ProxyAuthenticateHeader;
import javax.sip.header.ToHeader;

import com.fjl.test.tools.sip.SipConstants;
import com.fjl.test.tools.sip.SipEventListener;
import com.fjl.test.tools.sip.SipPhone;
import com.fjl.test.tools.sip.authentication.AuthenticationUtils;
import com.fjl.test.tools.sip.call.SipClientStackException;
import com.fjl.test.tools.sip.call.SipDialog;
import com.fjl.test.tools.sip.call.content.Content;
import com.fjl.test.tools.sip.utils.FromToHeader;
import com.fjl.test.tools.sip.utils.SipHeader;

public class StartState extends SipDialogState {
    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendInvite(com.fjl.test.tools.sip.call.SipDialog,
     *      java.lang.String, com.fjl.test.tools.sip.call.content.Content,
     *      java.util.Vector)
     */
    @Override
    public void sendInvite(SipDialog sipDialog, String toSipUri,
            Content content, Vector<SipHeader> headers, boolean authenticated)
            throws SipClientStackException {
        sipDialog
                .createAndSendInvite(toSipUri, headers, content, authenticated);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendOptions(com.fjl.test.tools.sip.call.SipDialog,
     *      java.lang.String, com.fjl.test.tools.sip.call.content.Content,
     *      java.util.Vector)
     */
    @Override
    public void sendOptions(SipDialog sipDialog, String toSipUri,
            Content content, Vector<SipHeader> headers, boolean authenticated)
            throws SipClientStackException {
        sipDialog.createAndSendInitialOptions(toSipUri, headers, content,
                authenticated);
        sipDialog.setSipDialogState(DialogState.NOT_INVITE_DIALOG);

    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendRegister(com.fjl.test.tools.sip.call.SipDialog,
     *      java.lang.String, com.fjl.test.tools.sip.call.content.Content,
     *      java.util.Vector)
     */
    @Override
    public void sendRegister(SipDialog sipDialog, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {

        if (headers == null) {
            headers = new Vector<SipHeader>();
        }

        long expires = sipDialog.getRegisterExpires();
        headers.add(new SipHeader(SipConstants.EXPIRES_HEADER, ""
                + sipDialog.getRegisterExpires()));
        SipPhone sipPhone = sipDialog.getSipPhone();
        // Check if a Register once has been received
        if (sipPhone.hasRegisterNonce()) {
            headers.add(new SipHeader(SipConstants.AUTHORIZATION_HEADER, null));
        }

        sipDialog.createAndSendInitialRegister(headers, content);
        if (expires > 0) {
            sipDialog.setSipDialogState(DialogState.REGISTERING);
        } else {
            sipDialog.setSipDialogState(DialogState.UNREGISTERING);
        }

    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendMessage(com.fjl.test.tools.sip.call.SipDialog,
     *      java.lang.String, com.fjl.test.tools.sip.call.content.Content,
     *      java.util.Vector)
     */
    @Override
    public void sendMessage(SipDialog sipDialog, String toSipUri,
            Content content, Vector<SipHeader> headers, boolean authenticated)
            throws SipClientStackException {
        sipDialog.createAndSendInitialMessage(toSipUri, headers, content,
                authenticated);
        sipDialog.setSipDialogState(DialogState.NOT_INVITE_DIALOG);
    }

    @Override
    public void sendSubscribe(SipDialog sipDialog, String toSipUri,
            Content content, Vector<SipHeader> headers, boolean authenticated)
            throws SipClientStackException {
        logger.debug("StartState.sendSubscribe to: {}", toSipUri);
        sipDialog.createAndSendInitialSubscribe(toSipUri, headers, content,
                authenticated);
        sipDialog.setSipDialogState(DialogState.NOT_INVITE_DIALOG);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleInviteTryingResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleInviteTryingResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        logger.trace("StartState.handleInviteTryingResponse => Pass to Inviting State");
        sipDialog.setSipDialogState(DialogState.INVITING);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleInviteProvisionalResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleInviteProvisionalResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        logger.trace("StartState.handleInviteProvisionalResponse => Pass to Inviting State");
        sipDialog.setSipDialogState(DialogState.INVITING);
        sipEventListener.handleInviteProvisionalResponse(sipDialog.getCallId(),
                responseEvent);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleInviteSuccessResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleInviteSuccessResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        logger.trace("StartState.handleInviteSuccessResponse");
        // Set the contact header received
        ContactHeader contactHeader = (ContactHeader) responseEvent
                .getResponse().getHeader(SipConstants.CONTACT_HEADER);
        sipDialog.setContactHeaderReceived(contactHeader);
        sipDialog.setSipDialogState(DialogState.INVITING_ANSWERED);
        sipEventListener.handleInviteSuccessResponse(sipDialog.getCallId(),
                responseEvent);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleInvite(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.RequestEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleInvite(SipDialog sipDialog, RequestEvent requestEvent,
            SipEventListener sipEventListener) throws SipClientStackException {
        logger.trace("StartState.handleInvite");

        // Set the local and the remote Address

        // The remote Header is built from the From Header
        FromHeader fromHeader = (FromHeader) requestEvent.getRequest()
                .getHeader(SipConstants.FROM_HEADER);
        sipDialog.setRemoteHeader(new FromToHeader(fromHeader));
        // The local Header is built from the To Header
        ToHeader toHeader = (ToHeader) requestEvent.getRequest().getHeader(
                SipConstants.TO_HEADER);
        FromToHeader localHeader = new FromToHeader(toHeader);
        localHeader.setTag(sipDialog.getSipPhone().generateNewTag());
        sipDialog.setLocalHeader(localHeader);

        // Set the contact header received
        ContactHeader contactHeader = (ContactHeader) requestEvent.getRequest()
                .getHeader(SipConstants.CONTACT_HEADER);
        sipDialog.setContactHeaderReceived(contactHeader);
        sipEventListener.handleInvite(sipDialog.getCallId(), requestEvent);

    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleInviteErrorResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleInviteErrorResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        logger.trace("StartState.handleInviteErrorResponse");

        if (SipConstants.RESPONSE_PROXY_AUTHENTICATION_REQUIRED == responseEvent
                .getResponse().getStatusCode()) {
            Header proxyAuthenticate = responseEvent.getResponse().getHeader(
                    SipConstants.PROXY_AUTHENTICATE_HEADER);
            if (proxyAuthenticate != null) {
                logger.trace(
                        "InvitingState.handleRegisterErrorResponse: proxyAuthenticate header is not null => {}",
                        proxyAuthenticate);
                ProxyAuthenticateHeader wwwAuthenticateHeader = (ProxyAuthenticateHeader) proxyAuthenticate;
                sipDialog.getSipPhone().updateNonce(
                        wwwAuthenticateHeader.getNonce());
            }
        }

        sipDialog.setSipDialogState(DialogState.CLOSED);
        sipEventListener.handleInviteErrorResponse(sipDialog.getCallId(),
                responseEvent);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendInviteProvisionalResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ServerTransaction, int, com.fjl.test.tools.sip.call.content.Content,
     *      java.util.Vector)
     */
    @Override
    public void sendInviteProvisionalResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        logger.trace("StartState.sendInviteProvisionalResponse");
        sipDialog.createAndSendResponse(serverTransaction, statusCode, content,
                headers);

        sipDialog.setSipDialogState(DialogState.INVITED);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendInviteRedirectedResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ServerTransaction, int, com.fjl.test.tools.sip.call.content.Content,
     *      java.util.Vector)
     */
    @Override
    public void sendInviteRedirectedResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Content content, Vector<SipHeader> headers)
            throws SipClientStackException {

        sipDialog.createAndSendResponse(serverTransaction, statusCode, content,
                headers);

        sipDialog.setSipDialogState(DialogState.CLOSED);

    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleCancel(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.RequestEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleCancel(SipDialog sipDialog, RequestEvent requestEvent,
            SipEventListener sipEventListener) throws SipClientStackException {
        logger.trace("InvitedState.handleCancel");
        sipDialog.setSipDialogState(DialogState.CLOSING);
        sipEventListener.handleCancel(sipDialog.getCallId(), requestEvent);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleInvite(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.RequestEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleOptions(SipDialog sipDialog, RequestEvent requestEvent,
            SipEventListener sipEventListener) throws SipClientStackException {
        logger.trace("StartState.handleOptions");

        // Set the local and the remote Address

        // The remote Header is built from the From Header
        FromHeader fromHeader = (FromHeader) requestEvent.getRequest()
                .getHeader(SipConstants.FROM_HEADER);
        sipDialog.setRemoteHeader(new FromToHeader(fromHeader));
        // The local Header is built from the To Header
        ToHeader toHeader = (ToHeader) requestEvent.getRequest().getHeader(
                SipConstants.TO_HEADER);
        FromToHeader localHeader = new FromToHeader(toHeader);
        localHeader.setTag(sipDialog.getSipPhone().generateNewTag());
        sipDialog.setLocalHeader(localHeader);
        sipDialog.setSipDialogState(DialogState.NOT_INVITE_DIALOG);
        sipEventListener.handleOptions(sipDialog.getCallId(), requestEvent);

    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleMessage(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.RequestEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleMessage(SipDialog sipDialog, RequestEvent requestEvent,
            SipEventListener sipEventListener) throws SipClientStackException {
        logger.trace("StartState.handleMessage");

        // Set the local and the remote Address

        // The remote Header is built from the From Header
        FromHeader fromHeader = (FromHeader) requestEvent.getRequest()
                .getHeader(SipConstants.FROM_HEADER);
        sipDialog.setRemoteHeader(new FromToHeader(fromHeader));
        // The local Header is built from the To Header
        ToHeader toHeader = (ToHeader) requestEvent.getRequest().getHeader(
                SipConstants.TO_HEADER);
        FromToHeader localHeader = new FromToHeader(toHeader);
        localHeader.setTag(sipDialog.getSipPhone().generateNewTag());
        sipDialog.setLocalHeader(localHeader);
        sipDialog.setSipDialogState(DialogState.NOT_INVITE_DIALOG);
        sipEventListener.handleMessage(sipDialog.getCallId(), requestEvent);

    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendPrack(com.fjl.test.tools.sip.call.SipDialog,
     *      com.fjl.test.tools.sip.call.content.Content, java.util.Vector)
     */
    @Override
    public void sendCancel(SipDialog sipDialog, Vector<SipHeader> headers)
            throws SipClientStackException {
        logger.trace("StartState.sendCancel");
        sipDialog.createAndSendRequest(SipConstants.CANCEL, null, headers);
        sipDialog.setSipDialogState(DialogState.CANCELLING);

    }
}
