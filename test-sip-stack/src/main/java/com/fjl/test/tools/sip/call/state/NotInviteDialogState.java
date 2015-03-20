package com.fjl.test.tools.sip.call.state;

import java.util.Vector;

import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;

import com.fjl.test.tools.sip.SipEventListener;
import com.fjl.test.tools.sip.call.SipClientStackException;
import com.fjl.test.tools.sip.call.SipDialog;
import com.fjl.test.tools.sip.call.content.Content;
import com.fjl.test.tools.sip.utils.SipHeader;

public class NotInviteDialogState extends SipDialogState {

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleOptionsSuccessResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleOptionsSuccessResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        logger.trace("NotInviteDialogState.handleOptionsSuccessResponse");
        sipDialog.setSipDialogState(DialogState.CLOSED);
        sipEventListener.handleOptionsSuccessResponse(sipDialog.getCallId(),
                responseEvent);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleOptionsErrorResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleOptionsErrorResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        logger.trace("NotInviteDialogState.handleOptionsErrorResponse");
        sipDialog.setSipDialogState(DialogState.CLOSED);
        sipEventListener.handleOptionsErrorResponse(sipDialog.getCallId(),
                responseEvent);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleMessageSuccessResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleMessageSuccessResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        logger.trace("NotInviteDialogState.handleMessageSuccessResponse");
        sipDialog.setSipDialogState(DialogState.CLOSED);
        sipEventListener.handleMessageSuccessResponse(sipDialog.getCallId(),
                responseEvent);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleMessageErrorResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleMessageErrorResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        logger.trace("NotInviteDialogState.handleMessageErrorResponse");
        sipDialog.setSipDialogState(DialogState.CLOSED);
        sipEventListener.handleMessageErrorResponse(sipDialog.getCallId(),
                responseEvent);
    }

    /**
     * Send a Message success response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
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
    public void sendMessageSuccessResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        logger.trace("NotInviteDialogState.sendMessageSuccessResponse");
        sipDialog.createAndSendResponse(serverTransaction, statusCode, content,
                headers);

        sipDialog.setSipDialogState(DialogState.CLOSED);

    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleSubscribeSuccessResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleSubscribeSuccessResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        logger.trace("NotInviteDialogState.handleSubscribeSuccessResponse");
        sipDialog.setSipDialogState(DialogState.CLOSED);
        sipEventListener.handleSubscribeSuccessResponse(sipDialog.getCallId(),
                responseEvent);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleOptionsErrorResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleSubscribeErrorResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        logger.trace("NotInviteDialogState.handleSubscribeErrorResponse");
        sipDialog.setSipDialogState(DialogState.CLOSED);
        sipEventListener.handleSubscribeErrorResponse(sipDialog.getCallId(),
                responseEvent);
    }

    /**
     * Send a Message error response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the MESSAGE response.
     * @return the Call-ID created
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendMessageErrorResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {
        logger.trace("NotInviteDialogState.sendMessageErrorResponse");
        sipDialog.createAndSendResponse(serverTransaction, statusCode, null,
                headers);

        sipDialog.setSipDialogState(DialogState.CLOSED);

    }

    /**
     * Send a Options success response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param content
     *            specifies the Content to add in the OPTIONS response.
     * @param headers
     *            specifies the headers to be added in the OPTIONS response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendOptionsSuccessResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        logger.trace("NotInviteDialogState.sendOptionsSuccessResponse");
        sipDialog.createAndSendResponse(serverTransaction, statusCode, content,
                headers);

        sipDialog.setSipDialogState(DialogState.CLOSED);
    }

    /**
     * Send a Options error response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the OPTIONS response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendOptionsErrorResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {
        logger.trace("NotInviteDialogState.sendOptionsErrorResponse");
        sipDialog.createAndSendResponse(serverTransaction, statusCode, null,
                headers);
        sipDialog.setSipDialogState(DialogState.CLOSED);
    }
}
