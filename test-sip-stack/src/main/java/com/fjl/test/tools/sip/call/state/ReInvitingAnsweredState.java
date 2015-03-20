package com.fjl.test.tools.sip.call.state;

import java.util.Vector;

import com.fjl.test.tools.sip.call.SipClientStackException;
import com.fjl.test.tools.sip.call.SipDialog;
import com.fjl.test.tools.sip.call.content.Content;
import com.fjl.test.tools.sip.utils.SipHeader;



public class ReInvitingAnsweredState extends SipDialogState
{
  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendAck(com.fjl.test.tools.sip.call.SipDialog, com.fjl.test.tools.sip.call.content.Content, java.util.Vector)
   */
  @Override
  public void sendAck ( SipDialog sipDialog, Content content, Vector<SipHeader> headers) throws SipClientStackException
  {
   logger.trace( "ReInvitingAnsweredState.sendAck");
    sipDialog.createAndSendAck( content, headers);
    sipDialog.setSipDialogState( DialogState.IN_CALL);
  }
}
