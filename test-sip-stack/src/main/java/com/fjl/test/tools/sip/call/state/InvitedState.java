package com.fjl.test.tools.sip.call.state;

import java.util.Vector;

import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;

import com.fjl.test.tools.sip.SipEventListener;
import com.fjl.test.tools.sip.call.SipClientStackException;
import com.fjl.test.tools.sip.call.SipDialog;
import com.fjl.test.tools.sip.call.content.Content;
import com.fjl.test.tools.sip.utils.SipHeader;

public class InvitedState extends SipDialogState
{
  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendInviteProvisionalResponse(com.fjl.test.tools.sip.call.SipDialog,
   *      javax.sip.ServerTransaction, int, com.fjl.test.tools.sip.call.content.Content, java.util.Vector)
   */
  @Override
  public void sendInviteProvisionalResponse ( SipDialog sipDialog, ServerTransaction serverTransaction, int statusCode,
      Content content, Vector<SipHeader> headers) throws SipClientStackException
  {

    sipDialog.createAndSendResponse( serverTransaction, statusCode, content, headers);
    sipDialog.setSipDialogState( DialogState.INVITED);

  }

  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendInviteRedirectedResponse(com.fjl.test.tools.sip.call.SipDialog,
   *      javax.sip.ServerTransaction, int, com.fjl.test.tools.sip.call.content.Content, java.util.Vector)
   */
  @Override
  public void sendInviteRedirectedResponse ( SipDialog sipDialog, ServerTransaction serverTransaction, int statusCode,
      Content content, Vector<SipHeader> headers) throws SipClientStackException
  {

    sipDialog.createAndSendResponse( serverTransaction, statusCode, content, headers);

    sipDialog.setSipDialogState( DialogState.CLOSED);

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
    logger.debug( "InvitedState.sendInviteSuccessResponse");
    sipDialog.createAndSendResponse( serverTransaction, statusCode, content, headers);
    sipDialog.setSipDialogState( DialogState.INVITED_ANSWERED);
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
    sipDialog.createAndSendResponse( serverTransaction, statusCode, null, headers);

    sipDialog.setSipDialogState( DialogState.CLOSED);
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
    logger.trace( "InvitedState.handlePrack");
    sipDialog.setSipDialogState( DialogState.INVITED);
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
    logger.trace( "InvitedState.sendPrackSuccessResponse");
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
    logger.trace( "InvitedState.sendPrackErrorResponse");
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
    logger.trace( "InvitedState.handleCancel");
    sipDialog.setSipDialogState( DialogState.CLOSING);
    sipEventListener.handleCancel( sipDialog.getCallId(), requestEvent);
  }
}
