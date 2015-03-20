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
import com.fjl.test.tools.sip.call.state.SipDialogState.DialogState;
import com.fjl.test.tools.sip.utils.SipHeader;

public class ReInvitedState extends SipDialogState
{
  
  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendInvite(com.fjl.test.tools.sip.call.SipDialog, java.lang.String,
   *      com.fjl.test.tools.sip.call.content.Content, java.util.Vector)
   */
  @Override
  public void sendInvite ( SipDialog sipDialog, String toSipUri, Content content, Vector<SipHeader> headers,boolean authenticated)
      throws SipClientStackException
  {
    logger.trace( "ReInvitedState.sendInvite");
    sipDialog.createAndSendReInvite( content, headers);
    //sipDialog.setSipDialogState( DialogState.REINVITING);
  }
  
  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleInviteErrorResponse(com.fjl.test.tools.sip.call.SipDialog, javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
   */
  @Override
  public void handleInviteErrorResponse (SipDialog sipDialog,ResponseEvent responseEvent,SipEventListener  sipEventListener)
      throws SipClientStackException
  {
    logger.trace("ReInvitedState.handleInviteErrorResponse");
    boolean isDialogToBeClosed = SipTools.isErrorCodeCloseDialog( responseEvent.getResponse().getStatusCode());
    if(isDialogToBeClosed){
      sipDialog.setSipDialogState(DialogState.CLOSED);
    }else{
      // remains in same state
    }
    
    sipEventListener.handleInviteErrorResponse(sipDialog.getCallId(), responseEvent);
  }
  
  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendInviteProvisionalResponse(com.fjl.test.tools.sip.call.SipDialog,
   *      javax.sip.ServerTransaction, int, com.fjl.test.tools.sip.call.content.Content, java.util.Vector)
   */
  @Override
  public void sendInviteProvisionalResponse ( SipDialog sipDialog, ServerTransaction serverTransaction, int statusCode,
      Content content, Vector<SipHeader> headers) throws SipClientStackException
  {
    logger.trace( "ReInvitedState.sendInviteProvisionalResponse");
    sipDialog.createAndSendResponse( serverTransaction, statusCode, content, headers);
    sipDialog.setSipDialogState( DialogState.REINVITED);

  }

  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendInviteSuccessResponse(com.fjl.test.tools.sip.call.SipDialog,
   *      javax.sip.ServerTransaction, int, com.fjl.test.tools.sip.call.content.Content, java.util.Vector)
   */
  @Override
  public void sendInviteSuccessResponse ( SipDialog sipDialog, ServerTransaction serverTransaction, int statusCode,
      Content content, Vector<SipHeader> headers) throws SipClientStackException
  {
    logger.trace( "ReInvitedState.sendInviteSuccessResponse");
    sipDialog.createAndSendResponse( serverTransaction, statusCode, content, headers);

    sipDialog.setSipDialogState( DialogState.REINVITED);

  }

  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendInviteErrorResponse(com.fjl.test.tools.sip.call.SipDialog,
   *      javax.sip.ServerTransaction, int, java.util.Vector)
   */
  @Override
  public void sendInviteErrorResponse ( SipDialog sipDialog, ServerTransaction serverTransaction, int statusCode,
      Vector<SipHeader> headers) throws SipClientStackException
  {
    logger.trace( "ReInvitedState.sendInviteErrorResponse");
    sipDialog.createAndSendResponse( serverTransaction, statusCode, null, headers);
    boolean toBeClosed = SipTools.isErrorCodeCloseDialog( statusCode);
    if (toBeClosed)
    {
      sipDialog.setSipDialogState( DialogState.CLOSED);
    }
    else
    {
      sipDialog.setSipDialogState( DialogState.IN_CALL);
    }
  }

  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#handlePrack(com.fjl.test.tools.sip.call.SipDialog, javax.sip.RequestEvent,
   *      com.fjl.test.tools.sip.SipEventListener)
   */
  @Override
  public void handlePrack ( SipDialog sipDialog, RequestEvent requestEvent, SipEventListener sipEventListener)
      throws SipClientStackException
  {

    logger.trace( "ReInvitedState.handlePrack");
    sipDialog.setSipDialogState( DialogState.REINVITED);
    sipEventListener.handlePrack( sipDialog.getCallId(), requestEvent);
  }

  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendPrackSuccessResponse(com.fjl.test.tools.sip.call.SipDialog,
   *      javax.sip.ServerTransaction, int, com.fjl.test.tools.sip.call.content.Content, java.util.Vector)
   */
  @Override
  public void sendPrackSuccessResponse ( SipDialog sipDialog, ServerTransaction serverTransaction, int statusCode,
      Content content, Vector<SipHeader> headers) throws SipClientStackException
  {
    logger.trace( "ReInvitedState.sendPrackSuccessResponse");
    sipDialog.createAndSendResponse( serverTransaction, statusCode, null, headers);

  }

  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendPrackErrorResponse(com.fjl.test.tools.sip.call.SipDialog,
   *      javax.sip.ServerTransaction, int, java.util.Vector)
   */
  @Override
  public void sendPrackErrorResponse ( SipDialog sipDialog, ServerTransaction serverTransaction, int statusCode,
      Vector<SipHeader> headers) throws SipClientStackException
  {
    logger.trace( "ReInvitedState.sendPrackErrorResponse");
    sipDialog.createAndSendResponse( serverTransaction, statusCode, null, headers);
  }
  
  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleCancel(com.fjl.test.tools.sip.call.SipDialog, javax.sip.RequestEvent,
   *      com.fjl.test.tools.sip.SipEventListener)
   */
  @Override
  public void handleCancel ( SipDialog sipDialog, RequestEvent requestEvent, SipEventListener sipEventListener)
      throws SipClientStackException
  {
    logger.trace( "ReInvitedState.handleCancel");
    sipDialog.setSipDialogState( DialogState.REINVITED);
    sipEventListener.handleCancel( sipDialog.getCallId(), requestEvent);
  }

  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleAck(com.fjl.test.tools.sip.call.SipDialog, javax.sip.RequestEvent,
   *      com.fjl.test.tools.sip.SipEventListener)
   */
  @Override
  public void handleAck ( SipDialog sipDialog, RequestEvent requestEvent, SipEventListener sipEventListener)
      throws SipClientStackException
  {
    logger.trace( "ReInvitedState.handleAck");
    sipDialog.setSipDialogState( DialogState.IN_CALL);
    sipEventListener.handleAck( sipDialog.getCallId(), requestEvent);
  }
  
  /**
   * Send a Bye message
   * 
   * @param sipDialog specifies the SipDialog
   * @param headers specifies the headers to be added in the BYE.
   * @return the Call-ID created
   * @throws SipClientStackException In case of error.
   */
  public void sendBye ( SipDialog sipDialog, Vector<SipHeader> headers) throws SipClientStackException
  {
    sipDialog.createAndSendRequest(SipConstants.BYE, null, headers);
    sipDialog.setSipDialogState(DialogState.CLOSING);

  }
  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendCancelSuccessResponse(com.fjl.test.tools.sip.call.SipDialog, javax.sip.ServerTransaction, int, java.util.Vector)
   */
  @Override
   public void sendCancelSuccessResponse (SipDialog sipDialog, ServerTransaction serverTransaction, int statusCode,
       Vector<SipHeader> headers) throws SipClientStackException
  {
    logger.trace("ReInvitedState.sendCancelSuccessResponse");
    sipDialog.createAndSendResponse(serverTransaction, statusCode, null, headers);
    sipDialog.setSipDialogState(DialogState.REINVITED);
    
  }
}
