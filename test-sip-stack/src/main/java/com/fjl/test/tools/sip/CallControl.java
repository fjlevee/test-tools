package com.fjl.test.tools.sip;

import java.util.Vector;

import com.fjl.test.tools.sip.call.SipClientStackException;
import com.fjl.test.tools.sip.call.content.Content;
import com.fjl.test.tools.sip.utils.SipHeader;

public interface CallControl
{

  
  /**
   * Create a New Call by Sending a new INVITE mesage
   * 
   * @param toSipUri specifies the To Sip URI
   * @param content specifies the Content to add in the INVITE.
   * @param headers specifies the headers to be added in the INVITE.
   * @return the Call-ID created
   * @throws SipClientStackException In case of error.
   */
  public String sendInvite ( String toSipUri, Content content, Vector<SipHeader> headers)
  throws SipClientStackException;
  
  /**
   * Create a New Call by Sending a new INVITE mesage
   * 
   * @param toSipUri specifies the To Sip URI
   * @param content specifies the Content to add in the INVITE.
   * @param headers specifies the headers to be added in the INVITE.
   * @return the Call-ID created
   * @throws SipClientStackException In case of error.
   */
  public String sendInvite ( String toSipUri, Content content, Vector<SipHeader> headers,boolean authenticated)
  throws SipClientStackException;
  
  /**
   * Send a REINVITE message
   * @param callId specifies the Call ID
   * @param content specifies the Content to add in the INVITE.
   * @param headers specifies the headers to be added in the INVITE.
   * @return the Call-ID created
   * @throws SipClientStackException In case of error.
   */
  public void sendReInvite (  String callId,Content content, Vector<SipHeader> headers)
      throws SipClientStackException;

  /**
   * Send a Bye message
   * 
   * @param callId specifies the Call ID
   * @param headers specifies the headers to be added in the BYE.
   * @return the Call-ID created
   * @throws SipClientStackException In case of error.
   */
  public void sendBye ( String callId, Vector<SipHeader> headers) throws SipClientStackException;

  /**
   * Send a Message message
   * 
   * @param callId specifies the Call ID
   * @param content specifies the Content to add in the MESSAGE.
   * @param headers specifies the headers to be added in the MESSAGE.
   * @return the Call-ID created
   * @throws SipClientStackException In case of error.
   */
  public void sendMessage ( String callId, Content content, Vector<SipHeader> headers) throws SipClientStackException;
  
  /**
   * Send a Message message that creates a new Sip Dialog
   * 
   * @param toSipUri specifies the To Sip URI
   * @param content specifies the Content to add in the MESSAGE.
   * @param headers specifies the headers to be added in the MESSAGE.
   * @return the Call-ID created
   * @throws SipClientStackException In case of error.
   */
  public String sendInitialMessage ( String toSipUri, Content content, Vector<SipHeader> headers,boolean authenticated) throws SipClientStackException;
  /**
   * Send a Options message
   * 
   * @param callId specifies the Call ID
   * @param content specifies the Content to add in the OPTIONS.
   * @param headers specifies the headers to be added in the OPTIONS.
   * @throws SipClientStackException In case of error.
   */
  public void sendOptions ( String callId, Content content, Vector<SipHeader> headers) throws SipClientStackException;
  
  /**
   * Send a Message message that creates a new Sip Dialog
   * 
   * @param toSipUri specifies the To Sip URI
   * @param content specifies the Content to add in the MESSAGE.
   * @param headers specifies the headers to be added in the MESSAGE.
   * @return the Call-ID created
   * @throws SipClientStackException In case of error.
   */
  public String sendInitialOptions ( String toSipUri, Content content, Vector<SipHeader> headers,boolean authenticated) throws SipClientStackException;

  /**
   * Send a Update message
   * 
   * @param callId specifies the Call ID
   * @param content specifies the Content to add in the UPDATE.
   * @param headers specifies the headers to be added in the UPDATE.
   * @throws SipClientStackException In case of error.
   */
  public void sendUpdate ( String callId, Content content, Vector<SipHeader> headers) throws SipClientStackException;
  
  /**
   * Send a Subscribe message
   * 
   * @param callId specifies the Call ID
   * @param content specifies the Content to add in the SUBSCRIBE.
   * @param headers specifies the headers to be added in the SUBSCRIBE.
   * @throws SipClientStackException In case of error.
   */
  public void sendSubscribe ( String callId, Content content, Vector<SipHeader> headers) throws SipClientStackException;
  /**
   * Send a Subscribe message that creates a new Sip Dialog
   * 
   * @param toSipUri specifies the To Sip URI
   * @param content specifies the Content to add in the SUBSCRIBE.
   * @param headers specifies the headers to be added in the SUBSCRIBE.
   * @return the Call-ID created
   * @throws SipClientStackException In case of error.
   */
  public String sendInitialSubscribe ( String toSipUri, Content content, Vector<SipHeader> headers,boolean authenticated) throws SipClientStackException;


  /**
   * Send a Notify message
   * 
   * @param callId specifies the Call ID
   * @param content specifies the Content to add in the NOTIFY.
   * @param headers specifies the headers to be added in the NOTIFY.
   * @throws SipClientStackException In case of error.
   */
  public void sendNotify ( String callId, Content content, Vector<SipHeader> headers) throws SipClientStackException;

  /**
   * Send a Prack message
   * 
   * @param callId specifies the Call ID
   * @param content specifies the Content to add in the PRACK.
   * @param headers specifies the headers to be added in the PRACK.
   * @throws SipClientStackException In case of error.
   */
  public void sendPrack ( String callId, Content content, Vector<SipHeader> headers) throws SipClientStackException;

  /**
   * Send a Ack message
   * 
   * @param callId specifies the Call ID
   * @param content specifies the Content to add in the ACK.
   * @param headers specifies the headers to be added in the ACK.
   * @throws SipClientStackException In case of error.
   */
  public void sendAck ( String callId, Content content, Vector<SipHeader> headers) throws SipClientStackException;

  /**
   * Send a Cancel message
   * 
   * @param callId specifies the Call ID
   * @param headers specifies the headers to be added in the CANCEL.
   * @throws SipClientStackException In case of error.
   */
  public void sendCancel ( String callId, Vector<SipHeader> headers) throws SipClientStackException;

  /**
   * Send a Info message
   * 
   * @param callId specifies the Call ID
   * @param content specifies the Content to add in the INFO.
   * @param headers specifies the headers to be added in the INFO.
   * @throws SipClientStackException In case of error.
   */
  public void sendInfo ( String callId, Content content, Vector<SipHeader> headers) throws SipClientStackException;

  /**
   * Send a Refer message
   * 
   * @param callId specifies the Call ID
   * @param content specifies the Content to add in the REFER.
   * @param headers specifies the headers to be added in the REFER.
   * @throws SipClientStackException In case of error.
   */
  public void sendRefer ( String callId, Content content, Vector<SipHeader> headers) throws SipClientStackException;

  /**
   * Send a INVITE provisional Response
   * 
   * @param callId specifies the Call ID 
   * @param statusCode Specifies the status code of the response
   * @param content specifies the Content to add in the INVITE response.
   * @param headers specifies the headers to be added in the INVITE response.
   * @throws SipClientStackException In case of error.
   */
  public void sendInviteProvisionalResponse ( String callId, int statusCode,Content content, Vector<SipHeader> headers)
      throws SipClientStackException;

  /**
   * Send a INVITE redirected Response
   * 
   * @param callId specifies the Call ID 
   * @param statusCode Specifies the status code of the response
   * @param content specifies the Content to add in the INVITE response.
   * @param headers specifies the headers to be added in the INVITE response.
   * @throws SipClientStackException In case of error.
   */
  public void sendInviteRedirectedResponse ( String callId, int statusCode,Content content, Vector<SipHeader> headers)
      throws SipClientStackException;

  /**
   * Send a INVITE success Response
   * 
   * @param callId specifies the Call ID 
   * @param statusCode Specifies the status code of the response
   * @param content specifies the Content to add in the INVITE response.
   * @param headers specifies the headers to be added in the INVITE response.
   * @throws SipClientStackException In case of error.
   */
  public void sendInviteSuccessResponse ( String callId, int statusCode,Content content, Vector<SipHeader> headers)
      throws SipClientStackException;

  /**
   * Send a INVITE error Response
   * 
   * @param callId specifies the Call ID 
   * @param statusCode Specifies the status code of the response
   * @param headers specifies the headers to be added in the INVITE response.
   * @throws SipClientStackException In case of error.
   */
  public void sendInviteErrorResponse ( String callId, int statusCode,Vector<SipHeader> headers)
      throws SipClientStackException;

  /**
   * Send a Bye success response
   * 
   * @param callId specifies the Call ID 
   * @param statusCode Specifies the status code of the response
   * @param headers specifies the headers to be added in the BYE response.
   * @return the Call-ID created
   * @throws SipClientStackException In case of error.
   */
  public void sendByeSuccessResponse ( String callId, int statusCode,Vector<SipHeader> headers) throws SipClientStackException;

  /**
   * Send a Bye error response
   * 
   * @param callId specifies the Call ID 
   * @param statusCode Specifies the status code of the response
   * @param headers specifies the headers to be added in the BYE response.
   * @return the Call-ID created
   * @throws SipClientStackException In case of error.
   */
  public void sendByeErrorResponse ( String callId,int statusCode, Vector<SipHeader> headers) throws SipClientStackException;

  /**
   * Send a Message success response
   * 
   * @param callId specifies the Call ID 
   * @param statusCode Specifies the status code of the response
   * @param content specifies the Content to add in the MESSAGE response.
   * @param headers specifies the headers to be added in the MESSAGE response.
   * @return the Call-ID created
   * @throws SipClientStackException In case of error.
   */
  public void sendMessageSuccessResponse ( String callId, int statusCode,Content content, Vector<SipHeader> headers)
      throws SipClientStackException;

  /**
   * Send a Message error response
   * 
   * @param callId specifies the Call ID 
   * @param statusCode Specifies the status code of the response
   * @param headers specifies the headers to be added in the MESSAGE response.
   * @return the Call-ID created
   * @throws SipClientStackException In case of error.
   */
  public void sendMessageErrorResponse ( String callId, int statusCode, Vector<SipHeader> headers)
      throws SipClientStackException;

  /**
   * Send a Options success response
   * 
   * @param callId specifies the Call ID 
   * @param statusCode Specifies the status code of the response
   * @param content specifies the Content to add in the OPTIONS response.
   * @param headers specifies the headers to be added in the OPTIONS response.
   * @throws SipClientStackException In case of error.
   */
  public void sendOptionsSuccessResponse ( String callId, int statusCode,Content content, Vector<SipHeader> headers)
      throws SipClientStackException;

  /**
   * Send a Options error response
   * 
   * @param callId specifies the Call ID 
   * @param statusCode Specifies the status code of the response
   * @param headers specifies the headers to be added in the OPTIONS response.
   * @throws SipClientStackException In case of error.
   */
  public void sendOptionsErrorResponse ( String callId, int statusCode,Vector<SipHeader> headers)
      throws SipClientStackException;

  /**
   * Send a Update success response
   * 
   * @param callId specifies the Call ID 
   * @param statusCode Specifies the status code of the response
   * @param content specifies the Content to add in the UPDATE response.
   * @param headers specifies the headers to be added in the UPDATE response.
   * @throws SipClientStackException In case of error.
   */
  public void sendUpdateSuccessResponse ( String callId, int statusCode,Content content, Vector<SipHeader> headers)
      throws SipClientStackException;

  /**
   * Send a Update success response
   * 
   * @param callId specifies the Call ID 
   * @param statusCode Specifies the status code of the response
   * @param headers specifies the headers to be added in the UPDATE response.
   * @throws SipClientStackException In case of error.
   */
  public void sendUpdateErrorResponse ( String callId, int statusCode,Vector<SipHeader> headers)
      throws SipClientStackException;

  /**
   * Send a Notify success Response
   * 
   * @param callId specifies the Call ID 
   * @param statusCode Specifies the status code of the response
   * @param content specifies the Content to add in the NOTIFY response.
   * @param headers specifies the headers to be added in the NOTIFY response.
   * @throws SipClientStackException In case of error.
   */
  public void sendNotifySuccessResponse ( String callId, int statusCode,Content content, Vector<SipHeader> headers)
      throws SipClientStackException;

  /**
   * Send a Notify error Response
   * 
   * @param callId specifies the Call ID 
   * @param statusCode Specifies the status code of the response
   * @param headers specifies the headers to be added in the NOTIFY response.
   * @throws SipClientStackException In case of error.
   */
  public void sendNotifyErrorResponse ( String callId, int statusCode, Vector<SipHeader> headers)
      throws SipClientStackException;

  /**
   * Send a Prack success response
   * 
   * @param callId specifies the Call ID 
   * @param statusCode Specifies the status code of the response
   * @param content specifies the Content to add in the PRACK response.
   * @param headers specifies the headers to be added in the PRACK response.
   * @throws SipClientStackException In case of error.
   */
  public void sendPrackSuccessResponse ( String callId, int statusCode,Content content, Vector<SipHeader> headers) throws SipClientStackException;


  /**
   * Send a Prack error response
   * 
   * @param callId specifies the Call ID 
   * @param statusCode Specifies the status code of the response
   * @param headers specifies the headers to be added in the PRACK response.
   * @throws SipClientStackException In case of error.
   */
  public void sendPrackErrorResponse ( String callId, int statusCode, Vector<SipHeader> headers) throws SipClientStackException;
  
  

  /**
   * Send a Cancel success response
   * 
   * @param callId specifies the Call ID 
   * @param statusCode Specifies the status code of the response
   * @param headers specifies the headers to be added in the CANCEL response.
   * @throws SipClientStackException In case of error.
   */
  public void sendCancelSuccessResponse ( String callId,int statusCode, Vector<SipHeader> headers) throws SipClientStackException;
  /**
   * Send a Cancel error response
   * 
   * @param callId specifies the Call ID 
   * @param statusCode Specifies the status code of the response
   * @param headers specifies the headers to be added in the CANCEL response.
   * @throws SipClientStackException In case of error.
   */
  public void sendCancelErrorResponse ( String callId,int statusCode, Vector<SipHeader> headers) throws SipClientStackException;


 
  /**
   * Send a Info success response
   * 
   * @param callId specifies the Call ID 
   * @param statusCode Specifies the status code of the response
   * @param content specifies the Content to add in the INFO response.
   * @param headers specifies the headers to be added in the INFO response.
   * @throws SipClientStackException In case of error.
   */
  public void sendInfoSuccessResponse ( String callId, int statusCode,Content content, Vector<SipHeader> headers) throws SipClientStackException;
  
  /**
   * Send a Info error response
   * 
   * @param callId specifies the Call ID 
   * @param statusCode Specifies the status code of the response
   * @param headers specifies the headers to be added in the INFO response.
   * @throws SipClientStackException In case of error.
   */
  public void sendInfoErrorResponse ( String callId, int statusCode, Vector<SipHeader> headers) throws SipClientStackException;

  
  /**
   * Send a Refer success response
   * 
   * @param callId specifies the Call ID 
   * @param statusCode Specifies the status code of the response
   * @param content specifies the Content to add in the REFER response.
   * @param headers specifies the headers to be added in the REFER response.
   * @throws SipClientStackException In case of error.
   */
  public void sendReferSuccessResponse ( String callId, int statusCode,Content content, Vector<SipHeader> headers) throws SipClientStackException;
  /**
   * Send a Refer error response
   * 
   * @param callId specifies the Call ID 
   * @param statusCode Specifies the status code of the response
   * @param headers specifies the headers to be added in the REFER response.
   * @throws SipClientStackException In case of error.
   */
  public void sendReferErrorResponse ( String callId, int statusCode, Vector<SipHeader> headers) throws SipClientStackException;

  
  
  /**
   * get the Replaces Header for a current call
   * @return the Replaces Header
   * @throws SipClientStackException In case of error.
   */
  public SipHeader getReplacesHeader (  String callId)
      throws SipClientStackException;
  
  /**
   * get the Contact header received for a current call
   * @return the Contact Header
   * @throws SipClientStackException In case of error.
   */
  public SipHeader getContactHeaderReceived (  String callId)
      throws SipClientStackException;
  
}
