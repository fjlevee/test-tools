package com.fjl.test.tools.sip.call.state;

import java.util.Vector;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;

import com.fjl.test.tools.sip.SipConstants;
import com.fjl.test.tools.sip.SipEventListener;
import com.fjl.test.tools.sip.call.SipClientStackException;
import com.fjl.test.tools.sip.call.SipDialog;
import com.fjl.test.tools.sip.call.SipTools;
import com.fjl.test.tools.sip.call.content.Content;
import com.fjl.test.tools.sip.utils.SipHeader;

public class InCallState extends SipDialogState {

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleBye(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.RequestEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleBye(SipDialog sipDialog, RequestEvent requestEvent,
            SipEventListener sipEventListener) throws SipClientStackException {
        logger.trace("InCallState.handleBye");
        sipDialog.setSipDialogState(DialogState.CLOSING);
        sipEventListener.handleBye(sipDialog.getCallId(), requestEvent);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendBye(com.fjl.test.tools.sip.call.SipDialog,
     *      java.util.Vector)
     */
    @Override
    public void sendBye(SipDialog sipDialog, Vector<SipHeader> headers)
            throws SipClientStackException {
        logger.trace("InCallState.sendBye");
        sipDialog.createAndSendRequest(SipConstants.BYE, null, headers);
        sipDialog.setSipDialogState(DialogState.CLOSING);

    }

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
        logger.trace("InCallState.sendInvite");
        sipDialog.createAndSendReInvite(content, headers);
        sipDialog.setSipDialogState(DialogState.REINVITING);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleInvite(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.RequestEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleInvite(SipDialog sipDialog, RequestEvent requestEvent,
            SipEventListener sipEventListener) throws SipClientStackException {
        logger.trace("InCallState.handleInvite");

        sipDialog.setSipDialogState(DialogState.REINVITED);
        sipEventListener.handleInvite(sipDialog.getCallId(), requestEvent);

    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendMessage(com.fjl.test.tools.sip.call.SipDialog,
     *      com.fjl.test.tools.sip.call.content.Content, java.util.Vector)
     */
    @Override
    public void sendMessage(SipDialog sipDialog, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {
        logger.trace("InCallState.sendMessage");
        sipDialog.createAndSendRequest(SipConstants.MESSAGE, content, headers);
        sipDialog.setSipDialogState(DialogState.IN_CALL);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendOptions(com.fjl.test.tools.sip.call.SipDialog,
     *      com.fjl.test.tools.sip.call.content.Content, java.util.Vector)
     */
    @Override
    public void sendOptions(SipDialog sipDialog, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {
        logger.trace("InCallState.sendOptions");
        sipDialog.createAndSendRequest(SipConstants.OPTIONS, content, headers);
        sipDialog.setSipDialogState(DialogState.IN_CALL);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendInfo(com.fjl.test.tools.sip.call.SipDialog,
     *      com.fjl.test.tools.sip.call.content.Content, java.util.Vector)
     */
    @Override
    public void sendInfo(SipDialog sipDialog, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {
        logger.trace("InCallState.sendInfo");
        sipDialog.createAndSendRequest(SipConstants.INFO, content, headers);
        sipDialog.setSipDialogState(DialogState.IN_CALL);
    }

    @Override
    public void sendSubscribe(SipDialog sipDialog, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {
        logger.trace("InCallState.sendSubscribe");
        sipDialog
                .createAndSendRequest(SipConstants.SUBSCRIBE, content, headers);
        sipDialog.setSipDialogState(DialogState.IN_CALL);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendRefer(com.fjl.test.tools.sip.call.SipDialog,
     *      com.fjl.test.tools.sip.call.content.Content, java.util.Vector)
     */
    @Override
    public void sendRefer(SipDialog sipDialog, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {
        logger.trace("InCallState.sendRefer");
        sipDialog.createAndSendRequest(SipConstants.REFER, content, headers);
        sipDialog.setSipDialogState(DialogState.IN_CALL);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendMessageSuccessResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ServerTransaction, int, com.fjl.test.tools.sip.call.content.Content,
     *      java.util.Vector)
     */
    @Override
    public void sendMessageSuccessResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        logger.trace("InCallState.sendMessageSuccessResponse");
        sipDialog.createAndSendResponse(serverTransaction, statusCode, content,
                headers);
        sipDialog.setSipDialogState(DialogState.IN_CALL);
    }

    /*********************************************************************************************************************
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendMessageErrorResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ServerTransaction, int, java.util.Vector)
     */
    @Override
    public void sendMessageErrorResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {
        logger.trace("InCallState.sendMessageErrorResponse");
        sipDialog.createAndSendResponse(serverTransaction, statusCode, null,
                headers);
        if (SipTools.isErrorCodeCloseDialog(statusCode)) {
            sipDialog.setSipDialogState(DialogState.CLOSED);
        } else {
            sipDialog.setSipDialogState(DialogState.IN_CALL);
        }
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendOptionsSuccessResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ServerTransaction, int, com.fjl.test.tools.sip.call.content.Content,
     *      java.util.Vector)
     */
    @Override
    public void sendOptionsSuccessResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        logger.trace("InCallState.sendOptionsSuccessResponse");
        sipDialog.createAndSendResponse(serverTransaction, statusCode, content,
                headers);
        sipDialog.setSipDialogState(DialogState.IN_CALL);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendOptionsErrorResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ServerTransaction, int, java.util.Vector)
     */
    @Override
    public void sendOptionsErrorResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {
        logger.trace("InCallState.sendOptionsErrorResponse");
        sipDialog.createAndSendResponse(serverTransaction, statusCode, null,
                headers);
        if (SipTools.isErrorCodeCloseDialog(statusCode)) {
            sipDialog.setSipDialogState(DialogState.CLOSED);
        } else {
            sipDialog.setSipDialogState(DialogState.IN_CALL);
        }
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendInfoSuccessResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ServerTransaction, int, com.fjl.test.tools.sip.call.content.Content,
     *      java.util.Vector)
     */
    @Override
    public void sendInfoSuccessResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        logger.trace("InCallState.sendInfoSuccessResponse");
        sipDialog.createAndSendResponse(serverTransaction, statusCode, content,
                headers);
        if (SipTools.isErrorCodeCloseDialog(statusCode)) {
            sipDialog.setSipDialogState(DialogState.CLOSED);
        } else {
            sipDialog.setSipDialogState(DialogState.IN_CALL);
        }
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendInfoErrorResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ServerTransaction, int, java.util.Vector)
     */
    @Override
    public void sendInfoErrorResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {
        logger.trace("InCallState.sendInfoErrorResponse");
        sipDialog.createAndSendResponse(serverTransaction, statusCode, null,
                headers);

        if (SipTools.isErrorCodeCloseDialog(statusCode)) {
            sipDialog.setSipDialogState(DialogState.CLOSED);
        } else {
            sipDialog.setSipDialogState(DialogState.IN_CALL);
        }

    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendReferSuccessResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ServerTransaction, int, com.fjl.test.tools.sip.call.content.Content,
     *      java.util.Vector)
     */
    @Override
    public void sendReferSuccessResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        logger.trace("InCallState.sendReferSuccessResponse");
        sipDialog.createAndSendResponse(serverTransaction, statusCode, content,
                headers);
        sipDialog.setSipDialogState(DialogState.IN_CALL);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendReferErrorResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ServerTransaction, int, java.util.Vector)
     */
    @Override
    public void sendReferErrorResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {
        logger.trace("InCallState.sendReferErrorResponse");
        sipDialog.createAndSendResponse(serverTransaction, statusCode, null,
                headers);
        if (SipTools.isErrorCodeCloseDialog(statusCode)) {
            sipDialog.setSipDialogState(DialogState.CLOSED);
        } else {
            sipDialog.setSipDialogState(DialogState.IN_CALL);
        }
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendNotifySuccessResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ServerTransaction, int, com.fjl.test.tools.sip.call.content.Content,
     *      java.util.Vector)
     */
    @Override
    public void sendNotifySuccessResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        logger.trace("InCallState.sendNotifySuccessResponse");
        sipDialog.createAndSendResponse(serverTransaction, statusCode, content,
                headers);
        sipDialog.setSipDialogState(DialogState.IN_CALL);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendNotifyErrorResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ServerTransaction, int, java.util.Vector)
     */
    @Override
    public void sendNotifyErrorResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {
        logger.trace("InCallState.sendNotifyErrorResponse");
        sipDialog.createAndSendResponse(serverTransaction, statusCode, null,
                headers);
        if (SipTools.isErrorCodeCloseDialog(statusCode)) {
            sipDialog.setSipDialogState(DialogState.CLOSED);
        } else {
            sipDialog.setSipDialogState(DialogState.IN_CALL);
        }
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleInfo(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.RequestEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleInfo(SipDialog sipDialog, RequestEvent requestEvent,
            SipEventListener sipEventListener) throws SipClientStackException {
        logger.trace("InCallState.handleInfo");
        sipDialog.setSipDialogState(DialogState.IN_CALL);
        sipEventListener.handleInfo(sipDialog.getCallId(), requestEvent);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleNotify(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.RequestEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleNotify(SipDialog sipDialog, RequestEvent requestEvent,
            SipEventListener sipEventListener) throws SipClientStackException {
        logger.trace("InCallState.handleNotify");
        sipDialog.setSipDialogState(DialogState.IN_CALL);
        sipEventListener.handleNotify(sipDialog.getCallId(), requestEvent);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleMessage(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.RequestEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleMessage(SipDialog sipDialog, RequestEvent requestEvent,
            SipEventListener sipEventListener) throws SipClientStackException {
        logger.trace("InCallState.handleMessage");
        sipDialog.setSipDialogState(DialogState.IN_CALL);
        sipEventListener.handleMessage(sipDialog.getCallId(), requestEvent);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleRefer(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.RequestEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleRefer(SipDialog sipDialog, RequestEvent requestEvent,
            SipEventListener sipEventListener) throws SipClientStackException {
        logger.trace("InCallState.handleRefer");
        sipDialog.setSipDialogState(DialogState.IN_CALL);
        sipEventListener.handleRefer(sipDialog.getCallId(), requestEvent);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleOptions(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.RequestEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleOptions(SipDialog sipDialog, RequestEvent requestEvent,
            SipEventListener sipEventListener) throws SipClientStackException {
        logger.trace("InCallState.handleOptions");
        sipDialog.setSipDialogState(DialogState.IN_CALL);
        sipEventListener.handleOptions(sipDialog.getCallId(), requestEvent);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleInfoSuccessResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleInfoSuccessResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        logger.trace("InCallState.handleInfoSuccessResponse");
        sipDialog.setSipDialogState(DialogState.IN_CALL);
        sipEventListener.handleInfoSuccessResponse(sipDialog.getCallId(),
                responseEvent);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleInfoErrorResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleInfoErrorResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        logger.trace("InCallState.handleInfoErrorResponse");

        if (SipTools.isErrorCodeCloseDialog(responseEvent.getResponse()
                .getStatusCode())) {
            sipDialog.setSipDialogState(DialogState.CLOSED);
        } else {
            sipDialog.setSipDialogState(DialogState.IN_CALL);
        }
        sipEventListener.handleInfoErrorResponse(sipDialog.getCallId(),
                responseEvent);

    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleSubscribeErrorResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleSubscribeErrorResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        logger.trace("InCallState.handleSubscribeErrorResponse");

        if (SipTools.isErrorCodeCloseDialog(responseEvent.getResponse()
                .getStatusCode())) {
            sipDialog.setSipDialogState(DialogState.CLOSED);
        } else {
            sipDialog.setSipDialogState(DialogState.IN_CALL);
        }
        sipEventListener.handleSubscribeErrorResponse(sipDialog.getCallId(),
                responseEvent);
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
        logger.trace("InCallState.handleSubscribeSuccessResponse");
        sipDialog.setSipDialogState(DialogState.IN_CALL);
        sipEventListener.handleSubscribeSuccessResponse(sipDialog.getCallId(),
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
        logger.trace("InCallState.handleMessageSuccessResponse");
        sipDialog.setSipDialogState(DialogState.IN_CALL);
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
        logger.trace("InCallState.handleMessageErrorResponse");
        if (SipTools.isErrorCodeCloseDialog(responseEvent.getResponse()
                .getStatusCode())) {
            sipDialog.setSipDialogState(DialogState.CLOSED);
        } else {
            sipDialog.setSipDialogState(DialogState.IN_CALL);
        }
        sipEventListener.handleMessageErrorResponse(sipDialog.getCallId(),
                responseEvent);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleReferSuccessResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleReferSuccessResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        logger.trace("InCallState.handleReferSuccessResponse");
        sipDialog.setSipDialogState(DialogState.IN_CALL);
        sipEventListener.handleReferSuccessResponse(sipDialog.getCallId(),
                responseEvent);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleReferErrorResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleReferErrorResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        logger.trace("InCallState.handleReferErrorResponse");
        if (SipTools.isErrorCodeCloseDialog(responseEvent.getResponse()
                .getStatusCode())) {
            sipDialog.setSipDialogState(DialogState.CLOSED);
        } else {
            sipDialog.setSipDialogState(DialogState.IN_CALL);
        }
        sipEventListener.handleReferErrorResponse(sipDialog.getCallId(),
                responseEvent);
    }

    /**
     * 
     * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleOptionsSuccessResponse(com.fjl.test.tools.sip.call.SipDialog,
     *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
     */
    @Override
    public void handleOptionsSuccessResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        logger.trace("InCallState.handleOptionsSuccessResponse");
        sipDialog.setSipDialogState(DialogState.IN_CALL);
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
        logger.trace("InCallState.handleOptionsErrorResponse");
        if (SipTools.isErrorCodeCloseDialog(responseEvent.getResponse()
                .getStatusCode())) {
            sipDialog.setSipDialogState(DialogState.CLOSED);
        } else {
            sipDialog.setSipDialogState(DialogState.IN_CALL);
        }
        sipEventListener.handleOptionsErrorResponse(sipDialog.getCallId(),
                responseEvent);
    }

}
