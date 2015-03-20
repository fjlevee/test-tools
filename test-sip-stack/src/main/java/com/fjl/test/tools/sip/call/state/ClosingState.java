package com.fjl.test.tools.sip.call.state;

import java.util.Vector;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;

import com.fjl.test.tools.sip.SipConstants;
import com.fjl.test.tools.sip.SipEventListener;
import com.fjl.test.tools.sip.call.SipClientStackException;
import com.fjl.test.tools.sip.call.SipDialog;
import com.fjl.test.tools.sip.call.content.Content;
import com.fjl.test.tools.sip.call.state.SipDialogState.DialogState;
import com.fjl.test.tools.sip.utils.SipHeader;

public class ClosingState extends SipDialogState {
    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendByeSuccessResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ServerTransaction, int, java.util.Vector)
     */
    @Override
    public void sendByeSuccessResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {

        logger.trace("ClosingState.sendByeSuccessResponse");
        sipDialog.createAndSendResponse(serverTransaction, statusCode, null,
                headers);
        sipDialog.setSipDialogState(DialogState.CLOSED);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleByeSuccessResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleByeSuccessResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        logger.trace("ClosingState.handleByeSuccessResponse");
        // Check if there is no BYE messages to be answered.
        if (sipDialog.getServerTransactionWithMethod("BYE") != null) {
            sipDialog.setSipDialogState(DialogState.CLOSING);
        } else {
            sipDialog.setSipDialogState(DialogState.CLOSED);
        }

        sipEventListener.handleByeSuccessResponse(sipDialog.getCallId(),
                responseEvent);

    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendCancelSuccessResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ServerTransaction, int, java.util.Vector)
     */
    @Override
    public void sendCancelSuccessResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {
        logger.trace("ClosingState.sendCancelSuccessResponse");
        sipDialog.createAndSendResponse(serverTransaction, statusCode, null,
                headers);
        sipDialog.setSipDialogState(DialogState.CLOSING);

    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendCancelErrorResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ServerTransaction, int, java.util.Vector)
     */
    @Override
    public void sendCancelErrorResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {
        sipDialog.createAndSendResponse(serverTransaction, statusCode, null,
                headers);
        sipDialog.setSipDialogState(DialogState.CLOSING);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendInviteErrorResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ServerTransaction, int, java.util.Vector)
     */
    @Override
    public void sendInviteErrorResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {
        sipDialog.createAndSendResponse(serverTransaction, statusCode, null,
                headers);

        sipDialog.setSipDialogState(DialogState.CLOSED);
    }

    /**
     * This method has been added in case of receiving the BYE message before
     * the INVITE ERROR Message.s
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleInviteErrorResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleInviteErrorResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        logger.trace("ClosingState.handleInviteErrorResponse");
        sipDialog.setSipDialogState(DialogState.CLOSING);
        sipEventListener.handleInviteErrorResponse(sipDialog.getCallId(),
                responseEvent);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendBye(com.fjl.test.tools.sip.call.SipDialog,
     *      java.util.Vector)
     */
    @Override
    public void sendBye(SipDialog sipDialog, Vector<SipHeader> headers)
            throws SipClientStackException {
        logger.trace("ClosingState.sendBye");
        sipDialog.createAndSendRequest(SipConstants.BYE, null, headers);
        sipDialog.setSipDialogState(DialogState.CLOSING);

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

        sipDialog.createAndSendResponse(serverTransaction, statusCode, content,
                headers);
        sipDialog.setSipDialogState(DialogState.CLOSING);

    }

    @Override
    public void handleAck(SipDialog sipDialog, RequestEvent requestEvent,
            SipEventListener sipEventListener) throws SipClientStackException {
        logger.trace("ClosingState.handleAck");
        sipEventListener.handleAck(sipDialog.getCallId(), requestEvent);
    }

}