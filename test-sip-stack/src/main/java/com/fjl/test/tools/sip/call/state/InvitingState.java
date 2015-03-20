package com.fjl.test.tools.sip.call.state;

import java.lang.reflect.Proxy;
import java.util.Vector;

import javax.sip.ResponseEvent;
import javax.sip.header.ContactHeader;
import javax.sip.header.Header;
import javax.sip.header.ProxyAuthenticateHeader;
import javax.sip.header.WWWAuthenticateHeader;

import com.fjl.test.tools.sip.SipConstants;
import com.fjl.test.tools.sip.SipEventListener;
import com.fjl.test.tools.sip.call.SipClientStackException;
import com.fjl.test.tools.sip.call.SipDialog;
import com.fjl.test.tools.sip.call.content.Content;
import com.fjl.test.tools.sip.utils.SipHeader;

public class InvitingState extends SipDialogState {
    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleInviteTryingResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleInviteTryingResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        logger.trace("InvitingState.handleInviteTryingResponse => Nothing to do");
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
        logger.trace("InvitingState.handleInviteProvisionalResponse");
        sipEventListener.handleInviteProvisionalResponse(sipDialog.getCallId(),
                responseEvent);
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
        logger.trace("InvitingState.handleInviteErrorResponse");
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
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleInviteRedirectResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleInviteRedirectResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {

        logger.trace("InvitingState.handleInviteRedirectResponse");

        sipDialog.setSipDialogState(DialogState.CLOSED);
        sipEventListener.handleInviteErrorResponse(sipDialog.getCallId(),
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

        logger.trace("InvitingState.handleInviteSuccessResponse");
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
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendPrack(com.fjl.test.tools.sip.call.SipDialog,
     *      com.fjl.test.tools.sip.call.content.Content, java.util.Vector)
     */
    @Override
    public void sendPrack(SipDialog sipDialog, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {

        logger.trace("InvitingState.sendPrack");
        sipDialog.createAndSendRequest(SipConstants.PRACK, content, headers);
        sipDialog.setSipDialogState(DialogState.INVITING);

    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handlePrackSuccessResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handlePrackSuccessResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {

        logger.trace("InvitingState.handlePrackSuccessResponse");
        sipDialog.setSipDialogState(DialogState.INVITING);
        sipEventListener.handlePrackSuccessResponse(sipDialog.getCallId(),
                responseEvent);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handlePrackErrorResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handlePrackErrorResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {

        logger.trace("InvitingState.handlePrackErrorResponse");
        sipDialog.setSipDialogState(DialogState.INVITING);
        sipEventListener.handlePrackSuccessResponse(sipDialog.getCallId(),
                responseEvent);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendPrack(com.fjl.test.tools.sip.call.SipDialog,
     *      com.fjl.test.tools.sip.call.content.Content, java.util.Vector)
     */
    @Override
    public void sendCancel(SipDialog sipDialog, Vector<SipHeader> headers)
            throws SipClientStackException {

        logger.trace("InvitingState.sendCancel");
        sipDialog.createAndSendRequest(SipConstants.CANCEL, null, headers);
        sipDialog.setSipDialogState(DialogState.CANCELLING);

    }
}
