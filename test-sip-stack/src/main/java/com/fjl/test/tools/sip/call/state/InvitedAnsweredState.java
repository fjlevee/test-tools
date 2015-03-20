package com.fjl.test.tools.sip.call.state;

import java.util.Vector;

import javax.sip.RequestEvent;

import com.fjl.test.tools.sip.SipConstants;
import com.fjl.test.tools.sip.SipEventListener;
import com.fjl.test.tools.sip.call.SipClientStackException;
import com.fjl.test.tools.sip.call.SipDialog;
import com.fjl.test.tools.sip.call.state.SipDialogState.DialogState;
import com.fjl.test.tools.sip.utils.SipHeader;

public class InvitedAnsweredState extends SipDialogState {

	@Override
	public void handleAck(SipDialog sipDialog, RequestEvent requestEvent,
			SipEventListener sipEventListener) throws SipClientStackException {
		logger.trace( "InvitedAnsweredState.handleAck");
    sipDialog.setSipDialogState( DialogState.IN_CALL);
    sipEventListener.handleAck( sipDialog.getCallId(), requestEvent);
	}

	
	 
  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendBye(com.fjl.test.tools.sip.call.SipDialog, java.util.Vector)
   */
  @Override
  public void sendBye ( SipDialog sipDialog, Vector<SipHeader> headers) throws SipClientStackException
  {
    logger.trace( "InvitedAnsweredState.sendBye");
    sipDialog.createAndSendRequest( SipConstants.BYE, null, headers);
    sipDialog.setSipDialogState( DialogState.CLOSING);

  }
}
