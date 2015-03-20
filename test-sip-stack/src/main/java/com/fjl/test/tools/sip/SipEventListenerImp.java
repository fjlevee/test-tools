package com.fjl.test.tools.sip;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;

import org.slf4j.Logger;

public class SipEventListenerImp implements SipEventListener {
    protected Logger logger;

    /**
     * 
     * Constructor of SipEventListenerImp.
     * 
     * @param logger
     */
    public SipEventListenerImp(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void handleAck(String sipCallId, RequestEvent requestEvent) {
        logger.debug(
                "SipEventListenerImp.handleAck (Handled by default listener): sipCallId: {}",
                sipCallId);

    }

    @Override
    public void handleBye(String sipCallId, RequestEvent requestEvent) {
        logger.debug(
                "SipEventListenerImp.handleBye (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleByeErrorResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleByeErrorResponse (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleByeSuccessResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleByeSuccessResponse (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleCancel(String sipCallId, RequestEvent requestEvent) {
        logger.debug(
                "SipEventListenerImp.handleCancel (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleCancelErrorResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleCancelErrorResponse (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleCancelSuccessResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleCancelSuccessResponse (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleInfo(String sipCallId, RequestEvent requestEvent) {
        logger.debug(
                "SipEventListenerImp.handleInfo (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleInfoErrorResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleInfo (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleInfoSuccessResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleInfoSuccessResponse (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleInvite(String sipCallId, RequestEvent requestEvent) {
        logger.debug(
                "SipEventListenerImp.handleInvite (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleInviteErrorResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleInviteErrorResponse(Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleInviteProvisionalResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleInviteProvisionalResponse (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleInviteRedirectResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleInviteRedirectResponse (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleInviteSuccessResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleInviteSuccessResponse (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleMessage(String sipCallId, RequestEvent requestEvent) {
        logger.debug(
                "SipEventListenerImp.handleMessage (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleMessageErrorResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleMessageErrorResponse (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleMessageSuccessResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleMessageSuccessResponse (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleNotify(String sipCallId, RequestEvent requestEvent) {
        logger.debug(
                "SipEventListenerImp.handleNotify (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleNotifyErrorResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleNotifyErrorResponse (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleNotifySuccessResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleNotifySuccessResponse (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleOptions(String sipCallId, RequestEvent requestEvent) {
        logger.debug(
                "SipEventListenerImp.handleOptions (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleOptionsErrorResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleOptionsErrorResponse (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleOptionsSuccessResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleOptionsSuccessResponse (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handlePrack(String sipCallId, RequestEvent requestEvent) {
        logger.debug(
                "SipEventListenerImp.handlePrack (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handlePrackErrorResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handlePrackErrorResponse (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handlePrackSuccessResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handlePrackSuccessResponse (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleReInvite(String sipCallId, RequestEvent requestEvent) {
        logger.debug(
                "SipEventListenerImp.handleReInvite (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleRefer(String sipCallId, RequestEvent requestEvent) {
        logger.debug(
                "SipEventListenerImp.handleRefer (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleReferErrorResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleReferErrorResponse (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleReferSuccessResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleReferSuccessResponse (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleSubscribeErrorResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleSubscribeErrorResponse (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleSubscribeSuccessResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleSubscribeSuccessResponse (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleUpdate(String sipCallId, RequestEvent requestEvent) {
        logger.debug(
                "SipEventListenerImp.handleUpdate (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleUpdateErrorResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleUpdateErrorResponse (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleUpdateSuccessResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleUpdateSuccessResponse (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleRegister(String sipCallId, RequestEvent requestEvent) {
        logger.debug(
                "SipEventListenerImp.handleRegister (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleRegisterErrorResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleRegisterErrorResponse (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void handleRegisterSuccessResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleRegisterSuccessResponse (Handled by default listener):  {}",
                sipCallId);

    }

    @Override
    public void sipDialogClosed(String sipCallId) {
        logger.debug(
                "SipEventListenerImp.sipDialogClosed (Handled by default listener): sipCallId: {}",
                sipCallId);

    }

}
