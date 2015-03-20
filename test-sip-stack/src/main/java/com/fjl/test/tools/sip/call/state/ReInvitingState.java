package com.fjl.test.tools.sip.call.state;

import java.util.Vector;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;

import com.fjl.test.tools.sip.SipConstants;
import com.fjl.test.tools.sip.SipEventListener;
import com.fjl.test.tools.sip.call.SipClientStackException;
import com.fjl.test.tools.sip.call.SipDialog;
import com.fjl.test.tools.sip.call.SipTools;
import com.fjl.test.tools.sip.call.content.Content;
import com.fjl.test.tools.sip.utils.SipHeader;



public class ReInvitingState extends SipDialogState
{
  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleInviteTryingResponse(com.fjl.test.tools.sip.call.SipDialog, javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
   */
	@Override
	public void handleInviteTryingResponse (SipDialog sipDialog,ResponseEvent responseEvent,SipEventListener sipEventListener )
      throws SipClientStackException
  {
    logger.trace("ReInvitingState.handleInviteTryingResponse => Nothing to do");
  }
	/**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleInviteProvisionalResponse(com.fjl.test.tools.sip.call.SipDialog, javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
	 */
	@Override
	public void handleInviteProvisionalResponse (SipDialog sipDialog,ResponseEvent responseEvent,SipEventListener  sipEventListener)
      throws SipClientStackException
  {
    logger.trace("ReInvitingState.handleInviteProvisionalResponse");
    sipEventListener.handleInviteProvisionalResponse(sipDialog.getCallId(), responseEvent);
  }
	/**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleInviteErrorResponse(com.fjl.test.tools.sip.call.SipDialog, javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
	 */
	@Override
	public void handleInviteErrorResponse (SipDialog sipDialog,ResponseEvent responseEvent,SipEventListener  sipEventListener)
      throws SipClientStackException
  {
    logger.trace("ReInvitingState.handleInviteErrorResponse");
    boolean isDialogToBeClosed = SipTools.isErrorCodeCloseDialog( responseEvent.getResponse().getStatusCode());
    if(isDialogToBeClosed){
      sipDialog.setSipDialogState(DialogState.CLOSED);
    }else{
      sipDialog.setSipDialogState(DialogState.IN_CALL);
    }
    
    sipEventListener.handleInviteErrorResponse(sipDialog.getCallId(), responseEvent);
  }
  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleInviteSuccessResponse(com.fjl.test.tools.sip.call.SipDialog, javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
   */
  @Override
  public void handleInviteSuccessResponse (SipDialog sipDialog,ResponseEvent responseEvent,SipEventListener  sipEventListener)
      throws SipClientStackException
  {
    logger.trace("ReInvitingState.handleInviteSuccessResponse");
    sipDialog.setSipDialogState(DialogState.REINVITING_ANSWERED);
    sipEventListener.handleInviteSuccessResponse(sipDialog.getCallId(), responseEvent);
  }
  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendPrack(com.fjl.test.tools.sip.call.SipDialog, com.fjl.test.tools.sip.call.content.Content, java.util.Vector)
   */
  @Override
  public void sendPrack ( SipDialog sipDialog, Content content,Vector<SipHeader> headers) throws SipClientStackException
  {
    logger.trace("ReInvitingState.sendPrack");
    sipDialog.createAndSendRequest(SipConstants.PRACK, content, headers);
    sipDialog.setSipDialogState(DialogState.REINVITING);
   
  }
  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#handlePrackSuccessResponse(com.fjl.test.tools.sip.call.SipDialog, javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
   */
  @Override
  public void handlePrackSuccessResponse ( SipDialog sipDialog, ResponseEvent responseEvent,
      SipEventListener sipEventListener) throws SipClientStackException
  {
    logger.trace("ReInvitingState.handlePrackSuccessResponse");
    sipDialog.setSipDialogState(DialogState.REINVITING);
    sipEventListener.handlePrackSuccessResponse(sipDialog.getCallId(), responseEvent);
  }
  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#handlePrackErrorResponse(com.fjl.test.tools.sip.call.SipDialog, javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
   */
  @Override
  public void handlePrackErrorResponse ( SipDialog sipDialog, ResponseEvent responseEvent,
      SipEventListener sipEventListener) throws SipClientStackException
  {
    logger.trace("ReInvitingState.handlePrackErrorResponse");
    sipDialog.setSipDialogState(DialogState.REINVITING);
    sipEventListener.handlePrackSuccessResponse(sipDialog.getCallId(), responseEvent);
  }
  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendPrack(com.fjl.test.tools.sip.call.SipDialog, com.fjl.test.tools.sip.call.content.Content, java.util.Vector)
   */
  @Override
  public void sendCancel ( SipDialog sipDialog, Vector<SipHeader> headers) throws SipClientStackException
  {
    logger.trace("ReInvitingState.sendCancel");
    sipDialog.createAndSendRequest(SipConstants.CANCEL, null, headers);
    sipDialog.setSipDialogState(DialogState.CANCELLING);
   
  }
  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleInviteSuccessResponse(com.fjl.test.tools.sip.call.SipDialog, javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
   */
  @Override
  public void handleBye (SipDialog sipDialog,RequestEvent requestEvent,SipEventListener  sipEventListener)
      throws SipClientStackException
  {
    logger.trace("ReInvitingState.handleBye");
    sipDialog.setSipDialogState(DialogState.CLOSING);
    sipEventListener.handleBye( sipDialog.getCallId(), requestEvent);
  }
}
