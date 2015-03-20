package com.fjl.test.tools.sip.call.state;

import java.util.Vector;

import javax.sip.RequestEvent;

import com.fjl.test.tools.sip.SipConstants;
import com.fjl.test.tools.sip.SipEventListener;
import com.fjl.test.tools.sip.call.SipClientStackException;
import com.fjl.test.tools.sip.call.SipDialog;
import com.fjl.test.tools.sip.call.content.Content;
import com.fjl.test.tools.sip.call.state.SipDialogState.DialogState;
import com.fjl.test.tools.sip.utils.SipHeader;



public class InvitingAnsweredState extends SipDialogState
{
  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendAck(com.fjl.test.tools.sip.call.SipDialog, com.fjl.test.tools.sip.call.content.Content, java.util.Vector)
   */
  @Override
  public void sendAck ( SipDialog sipDialog, Content content, Vector<SipHeader> headers) throws SipClientStackException
  {
    sipDialog.createAndSendAck( content, headers);
    sipDialog.setSipDialogState( DialogState.IN_CALL);
  }
  
  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleBye(com.fjl.test.tools.sip.call.SipDialog, javax.sip.RequestEvent, com.fjl.test.tools.sip.SipEventListener)
   */
  @Override
  public void handleBye(SipDialog sipDialog,RequestEvent requestEvent,SipEventListener  sipEventListener)
      throws SipClientStackException
  {
    logger.trace("InvitingAnsweredState.handleBye");
    sipDialog.setSipDialogState(DialogState.CLOSING);
    sipEventListener.handleBye(sipDialog.getCallId(), requestEvent);
  }
 
}
