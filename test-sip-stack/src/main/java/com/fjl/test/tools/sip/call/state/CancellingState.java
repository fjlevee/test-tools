package com.fjl.test.tools.sip.call.state;

import javax.sip.ResponseEvent;

import com.fjl.test.tools.sip.SipEventListener;
import com.fjl.test.tools.sip.call.SipClientStackException;
import com.fjl.test.tools.sip.call.SipDialog;

public class CancellingState extends SipDialogState {
    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleCancelSuccessResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleCancelSuccessResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        logger.trace("CancellingState.handleCancelSuccessResponse");
        sipDialog.setSipDialogState(DialogState.CANCELLING);
        sipEventListener.handleCancelSuccessResponse(sipDialog.getCallId(),
                responseEvent);

    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleCancelErrorResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleCancelErrorResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        logger.trace("CancellingState.handleCancelErrorResponse");

        sipDialog.setSipDialogState(DialogState.CANCELLING);
        sipEventListener.handleCancelErrorResponse(sipDialog.getCallId(),
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
        logger.trace("CancellingState.handleInviteErrorResponse");
        sipDialog.setSipDialogState(DialogState.CLOSED);
        sipEventListener.handleInviteErrorResponse(sipDialog.getCallId(),
                responseEvent);
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
        logger.trace("CancellingState..handleInviteTryingResponse");
        sipDialog.setSipDialogState(DialogState.CANCELLING);
    }
}