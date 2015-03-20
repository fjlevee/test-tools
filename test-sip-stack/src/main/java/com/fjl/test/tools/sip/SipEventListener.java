package com.fjl.test.tools.sip;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;

public interface SipEventListener {

	/**
	 * Handle an INVTE message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param requestEvent
	 *          specifies the INVITE request event
	 * 
	 */
	public void handleInvite(String sipCallId, RequestEvent requestEvent);

	/**
	 * handle a REINVITE message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param requestEvent
	 *          specifies the INVITE request event
	 */
	public void handleReInvite(String sipCallId, RequestEvent requestEvent);

	/**
	 * Handles a CANCEL message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param requestEvent
	 *          specifies the CANCEL request event
	 */
	public void handleCancel(String sipCallId, RequestEvent requestEvent);

	/**
	 * Handles a BYE message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param requestEvent
	 *          specifies the BYE request event
	 */
	public void handleBye(String sipCallId, RequestEvent requestEvent);

	/**
	 * Handles a Prack message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param requestEvent
	 *          specifies the PRACK request event
	 */
	public void handlePrack(String sipCallId, RequestEvent requestEvent);

	/**
	 * Handles a ACK message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param requestEvent
	 *          specifies the ACK request event
	 */
	public void handleAck(String sipCallId, RequestEvent requestEvent);

	/**
	 * Handles a MESSAGE message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param requestEvent
	 *          specifies the MESSAGE request event
	 */
	public void handleMessage(String sipCallId, RequestEvent requestEvent);

	/**
	 * Handles a OPTIONS message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param requestEvent
	 *          specifies the OPTIONS request event
	 */
	public void handleOptions(String sipCallId, RequestEvent requestEvent);

	/**
	 * Handles a UPDATE message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param requestEvent
	 *          specifies the UPDATE request event
	 */
	public void handleUpdate(String sipCallId, RequestEvent requestEvent);

	/**
	 * Handles a NOTIFY message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param requestEvent
	 *          specifies the NOTIFY request event
	 */
	public void handleNotify(String sipCallId, RequestEvent requestEvent);

	/**
	 * Handles a INFO message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param requestEvent
	 *          specifies the INFO request event
	 */
	public void handleInfo(String sipCallId, RequestEvent requestEvent);

	/**
	 * Handles a REFER message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param requestEvent
	 *          specifies the REFER request event
	 */
	public void handleRefer(String sipCallId, RequestEvent requestEvent);
	
	 /**
   * Handles a REGISTER message
   * 
   * @param sipCallId
   *          specifies the Call ID
   * @param requestEvent
   *          specifies the REGISTER request event
   */
  public void handleRegister ( String sipCallId, RequestEvent requestEvent);


	/**
	 * handle a INVITE success response
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param responseEvent
	 *          specifies the Success Response Evente
	 */
	public void handleInviteSuccessResponse(String sipCallId,
			ResponseEvent responseEvent);

	/**
	 * handle a INVITE success response
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param responseEvent
	 *          specifies the Success Response Event
	 */
	public void handleInviteErrorResponse(String sipCallId,
			ResponseEvent responseEvent);

	/**
	 * handle a INVITE provisional response
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param responseEvent
	 *          specifies the Provisional Response Event
	 */
	public void handleInviteProvisionalResponse(String sipCallId,
			ResponseEvent responseEvent);

	/**
	 * handle a INVITE redirect response
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param responseEvent
	 *          specifies the Redirect Response Event
	 */
	public void handleInviteRedirectResponse(String sipCallId,
			ResponseEvent responseEvent);

	/**
	 * Handles a CANCEL message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param responseEvent
	 *          specifies the Success Response Event
	 */
	public void handleCancelSuccessResponse(String sipCallId,
			ResponseEvent responseEvent);

	/**
	 * Handles a CANCEL message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param responseEvent
	 *          specifies the Error Response Event
	 */
	public void handleCancelErrorResponse(String sipCallId,
			ResponseEvent responseEvent);

	/**
	 * Handles a BYE message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param responseEvent
	 *          specifies the Success Response Event
	 */
	public void handleByeSuccessResponse(String sipCallId,
			ResponseEvent responseEvent);

	/**
	 * Handles a BYE message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param responseEvent
	 *          specifies the Success Response Event
	 */
	public void handleByeErrorResponse(String sipCallId,
			ResponseEvent responseEvent);

	/**
	 * Handles a Prack message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param responseEvent
	 *          specifies the Success Response Event
	 */
	public void handlePrackSuccessResponse(String sipCallId,
			ResponseEvent responseEvent);

	/**
	 * Handles a Prack message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param responseEvent
	 *          specifies the Success Response Event
	 */
	public void handlePrackErrorResponse(String sipCallId,
			ResponseEvent responseEvent);

	/**
	 * Handles a MESSAGE message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param responseEvent
	 *          specifies the Success Response Event
	 */
	public void handleMessageSuccessResponse(String sipCallId,
			ResponseEvent responseEvent);

	/**
	 * Handles a MESSAGE message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param responseEvent
	 *          specifies the Success Response Event
	 */
	public void handleMessageErrorResponse(String sipCallId,
			ResponseEvent responseEvent);

	/**
	 * Handles a OPTIONS message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param responseEvent
	 *          specifies the Success Response Event
	 */
	public void handleOptionsSuccessResponse(String sipCallId,
			ResponseEvent responseEvent);

	/**
	 * Handles a OPTIONS message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param responseEvent
	 *          specifies the Error Response Event
	 */
	public void handleOptionsErrorResponse(String sipCallId,
			ResponseEvent responseEvent);

	/**
	 * Handles a UPDATE message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param responseEvent
	 *          specifies the Success Response Event
	 */
	public void handleUpdateSuccessResponse(String sipCallId,
			ResponseEvent responseEvent);

	/**
	 * Handles a UPDATE message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param responseEvent
	 *          specifies the Errot Response Event
	 */
	public void handleUpdateErrorResponse(String sipCallId,
			ResponseEvent responseEvent);

	/**
	 * Handles a NOTIFY message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param responseEvent
	 *          specifies the Success Response Event
	 */
	public void handleNotifySuccessResponse(String sipCallId,
			ResponseEvent responseEvent);

	/**
	 * Handles a NOTIFY message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param responseEvent
	 *          specifies the Error Response Event
	 */
	public void handleNotifyErrorResponse(String sipCallId,
			ResponseEvent responseEvent);

	/**
	 * Handles a INFO message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param responseEvent
	 *          specifies the Success Response Event
	 */
	public void handleInfoSuccessResponse(String sipCallId,
			ResponseEvent responseEvent);

	/**
	 * Handles a INFO message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param responseEvent
	 *          specifies the Error Response Event
	 */
	public void handleInfoErrorResponse(String sipCallId,
			ResponseEvent responseEvent);
	
	
	 /**
   * Handles a SUBSCRIBE message success response
   * 
   * @param sipCallId
   *          specifies the Call ID
   * @param responseEvent
   *          specifies the Success Response Event
   */
  public void handleSubscribeSuccessResponse(String sipCallId,
      ResponseEvent responseEvent);

  /**
   * Handles a SUBSCRIBE message error response
   * 
   * @param sipCallId
   *          specifies the Call ID
   * @param responseEvent
   *          specifies the Error Response Event
   */
  public void handleSubscribeErrorResponse(String sipCallId,
      ResponseEvent responseEvent);

	/**
	 * Handles a REFER message
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param responseEvent
	 *          specifies the Success Response Event
	 */
	public void handleReferSuccessResponse(String sipCallId,
			ResponseEvent responseEvent);

	/**
	 * Handles a REFER message error response
	 * 
	 * @param sipCallId
	 *          specifies the Call ID
	 * @param responseEvent
	 *          specifies the Error Response Event
	 */
	public void handleReferErrorResponse(String sipCallId,
			ResponseEvent responseEvent);
	
	
	/**
   * Handles a REGISTER message success response
   * 
   * @param sipCallId
   *          specifies the Call ID
   * @param responseEvent
   *          specifies the Success Response Event
   */
  public void handleRegisterSuccessResponse(String sipCallId,
      ResponseEvent responseEvent);

  /**
   * Handles a REGISTER message error response
   * 
   * @param sipCallId
   *          specifies the Call ID
   * @param responseEvent
   *          specifies the Error Response Event
   */
  public void handleRegisterErrorResponse(String sipCallId,
      ResponseEvent responseEvent);

  

	/**
	 * SipDialog closed
	 * @param sipCallId
	 *          specifies the Call ID
	 */
	public void sipDialogClosed(String sipCallId);

}
