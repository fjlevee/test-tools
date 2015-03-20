package com.fjl.test.tools.sip.call;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fjl.test.tools.sip.CallControl;
import com.fjl.test.tools.sip.SipPhone;
import com.fjl.test.tools.sip.call.content.Content;
import com.fjl.test.tools.sip.utils.SipHeader;

public class CallControlImp implements CallControl
{
  /**
   * Log4j logger
   */
  private Logger logger = LoggerFactory.getLogger( com.fjl.test.tools.sip.call.CallControlImp.class);

  /**
   * SipPhone
   */
  private SipPhone sipPhone;

  public CallControlImp ( SipPhone sipPhone)
  {
    this.sipPhone = sipPhone;
  }

  /**
   * Create a Sip Dialog
   * 
   * @param toSipUri specifies the To Sip URI
   * @param content specifies the Content to add in the INVITE.
   * @param headers specifies the headers to be added in the INVITE.
   * @return the Call-ID created
   * @throws SipClientStackException In case of error.
   */
  public String sendInvite ( String toSipUri, Content content, Vector<SipHeader> headers)
      throws SipClientStackException
  {

   return sendInvite( toSipUri, content, headers,false);

  }
  
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
  throws SipClientStackException{
    logger.debug( "CallControlImp.sendInvite toSipUri: {}",  toSipUri);

    SipDialog sipDialog = new SipDialog( sipPhone);
    sipDialog.sendInvite( toSipUri, content, headers,authenticated);
    sipPhone.addSipDialog( sipDialog);

    return sipDialog.getCallId();
    
  }
  /**
   * Send a REINVITE message
   * @param callId specifies the Call ID
   * @param content specifies the Content to add in the INVITE.
   * @param headers specifies the headers to be added in the INVITE.
   * @return the Call-ID created
   * @throws SipClientStackException In case of error.
   */
  public void sendReInvite (  String callId,Content content, Vector<SipHeader> headers)
      throws SipClientStackException{
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException( "CallControlImp:sendReInvite error: can not find a SipDialog with callId: "
          + callId);
    }
    sipDialog.sendInvite( null, content, headers,false);
    
  }
  /**
   * Send a Bye message
   * 
   * @param callId specifies the Call ID
   * @param headers specifies the headers to be added in the BYE.
   * @return the Call-ID created
   * @throws SipClientStackException In case of error.
   */
  public void sendBye ( String callId, Vector<SipHeader> headers) throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException( "CallControlImp:sendBye error: can not find a SipDialog with callId: "
          + callId);
    }
    sipDialog.sendBye( headers);
  }

  /**
   * Send a Message message
   * 
   * @param callId specifies the Call ID
   * @param content specifies the Content to add in the MESSAGE.
   * @param headers specifies the headers to be added in the MESSAGE.
   * @return the Call-ID created
   * @throws SipClientStackException In case of error.
   */
  public void sendMessage ( String callId, Content content, Vector<SipHeader> headers) throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException( "CallControlImp:sendMessage error: can not find a SipDialog with callId: "
          + callId);
    }
    sipDialog.sendMessage( content, headers);
  }
  /**
   * Send a Message message that creates a new Sip Dialog
   * 
   * @param toSipUri specifies the To Sip URI
   * @param content specifies the Content to add in the MESSAGE.
   * @param headers specifies the headers to be added in the MESSAGE.
   * @return the Call-ID created
   * @throws SipClientStackException In case of error.
   */
  public String sendInitialMessage ( String toSipUri, Content content, Vector<SipHeader> headers,boolean authenticated) throws SipClientStackException{
    logger.debug( "CallControlImp.sendInitialMessage toSipUri: {}",  toSipUri);

    SipDialog sipDialog = new SipDialog( sipPhone);
    sipDialog.sendMessage( toSipUri, content, headers,authenticated);
    sipPhone.addSipDialog( sipDialog);

    return sipDialog.getCallId();
    
  }
  /**
   * Send a Options message
   * 
   * @param callId specifies the Call ID
   * @param content specifies the Content to add in the OPTIONS.
   * @param headers specifies the headers to be added in the OPTIONS.
   * @throws SipClientStackException In case of error.
   */
  public void sendOptions ( String callId, Content content, Vector<SipHeader> headers) throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);

    if (sipDialog == null)
    {
      throw new SipClientStackException( "CallControlImp:sendOptions error: can not find a SipDialog with callId: "
          + callId);
    }
    sipDialog.sendOptions( content, headers);
  }
  
  /**
   * Send a Message message that creates a new Sip Dialog
   * 
   * @param toSipUri specifies the To Sip URI
   * @param content specifies the Content to add in the MESSAGE.
   * @param headers specifies the headers to be added in the MESSAGE.
   * @return the Call-ID created
   * @throws SipClientStackException In case of error.
   */
  public String sendInitialOptions ( String toSipUri, Content content, Vector<SipHeader> headers,boolean authenticated) throws SipClientStackException{
    SipDialog sipDialog = new SipDialog( sipPhone);
    sipDialog.sendOptions( toSipUri, content, headers,authenticated);
    sipPhone.addSipDialog( sipDialog);

    return sipDialog.getCallId();
    
    
    
  }
  /**
   * Create an Options dialog by sending an Options message
   * 
   * 
   * @param toSipUri specifies the To Sip URI
   * @param content specifies the Content to add in the OPTIONS.
   * @param headers specifies the headers to be added in the OPTIONS.
   * @return the Call-ID created
   * @throws SipClientStackException In case of error.
   */
  /*
  public String createOptionsDialog ( String toSipUri,  Content content, Vector<SipHeader> headers) throws SipClientStackException
  {
    SipDialog sipDialog = new SipDialog( sipPhone);
    sipDialog.sendOptions( toSipUri, content, headers);
    sipPhone.addSipDialog( sipDialog);

    return sipDialog.getCallId();
  }*/
  /**
   * Send a Update message
   * 
   * @param callId specifies the Call ID
   * @param content specifies the Content to add in the UPDATE.
   * @param headers specifies the headers to be added in the UPDATE.
   * @throws SipClientStackException In case of error.
   */
  public void sendUpdate ( String callId, Content content, Vector<SipHeader> headers) throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException( "CallControlImp:sendUpdate error: can not find a SipDialog with callId: "
          + callId);
    }
    sipDialog.sendUpdate( content, headers);
  }
  
  
  /**
   * Send a Subscribe message
   * 
   * @param callId specifies the Call ID
   * @param content specifies the Content to add in the SUBSCRIBE.
   * @param headers specifies the headers to be added in the SUBSCRIBE.
   * @throws SipClientStackException In case of error.
   */
  public void sendSubscribe ( String callId, Content content, Vector<SipHeader> headers) throws SipClientStackException{
    
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException( "CallControlImp:sendSubscribe error: can not find a SipDialog with callId: "
          + callId);
    }
    sipDialog.sendSubscribe( content, headers);
  }
  /**
   * Send a Subscribe message that creates a new Sip Dialog
   * 
   * @param toSipUri specifies the To Sip URI
   * @param content specifies the Content to add in the SUBSCRIBE.
   * @param headers specifies the headers to be added in the SUBSCRIBE.
   * @return the Call-ID created
   * @throws SipClientStackException In case of error.
   */
  public String sendInitialSubscribe ( String toSipUri, Content content, Vector<SipHeader> headers,boolean authenticated) throws SipClientStackException{
    
    SipDialog sipDialog = new SipDialog( sipPhone);
    sipDialog.sendSubscribe( toSipUri, content, headers,authenticated);
    sipPhone.addSipDialog( sipDialog);

    return sipDialog.getCallId();
  }


  /**
   * Send a Notify message
   * 
   * @param callId specifies the Call ID
   * @param content specifies the Content to add in the NOTIFY.
   * @param headers specifies the headers to be added in the NOTIFY.
   * @throws SipClientStackException In case of error.
   */
  public void sendNotify ( String callId, Content content, Vector<SipHeader> headers) throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException( "CallControlImp:sendNotify error: can not find a SipDialog with callId: "
          + callId);
    }
    sipDialog.sendNotify( content, headers);
  }

  /**
   * Send a Prack message
   * 
   * @param callId specifies the Call ID
   * @param content specifies the Content to add in the PRACK.
   * @param headers specifies the headers to be added in the PRACK.
   * @throws SipClientStackException In case of error.
   */
  public void sendPrack ( String callId, Content content, Vector<SipHeader> headers) throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException( "CallControlImp:sendPrack error: can not find a SipDialog with callId: "
          + callId);
    }
    sipDialog.sendPrack( content, headers);
  }

  /**
   * Send a Ack message
   * 
   * @param callId specifies the Call ID
   * @param content specifies the Content to add in the ACK.
   * @param headers specifies the headers to be added in the ACK.
   * @throws SipClientStackException In case of error.
   */
  public void sendAck ( String callId, Content content, Vector<SipHeader> headers) throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException( "CallControlImp:sendAck error: can not find a SipDialog with callId: "
          + callId);
    }
    sipDialog.sendAck( content, headers);
  }

  /**
   * Send a Cancel message
   * 
   * @param callId specifies the Call ID
   * @param headers specifies the headers to be added in the CANCEL.
   * @throws SipClientStackException In case of error.
   */
  public void sendCancel ( String callId, Vector<SipHeader> headers) throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException( "CallControlImp:sendCancel error: can not find a SipDialog with callId: "
          + callId);
    }
    sipDialog.sendCancel( headers);
  }

  /**
   * Send a Info message
   * 
   * @param callId specifies the Call ID
   * @param content specifies the Content to add in the INFO.
   * @param headers specifies the headers to be added in the INFO.
   * @throws SipClientStackException In case of error.
   */
  public void sendInfo ( String callId, Content content, Vector<SipHeader> headers) throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException( "CallControlImp:sendInfo error: can not find a SipDialog with callId: "
          + callId);
    }
    sipDialog.sendInfo( content, headers);
  }

  /**
   * Send a Refer message
   * 
   * @param callId specifies the Call ID
   * @param content specifies the Content to add in the REFER.
   * @param headers specifies the headers to be added in the REFER.
   * @throws SipClientStackException In case of error.
   */
  public void sendRefer ( String callId, Content content, Vector<SipHeader> headers) throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException( "CallControlImp:sendRefer error: can not find a SipDialog with callId: "
          + callId);
    }
    sipDialog.sendRefer( content, headers);
  }

  /**
   * Send a INVITE provisional Response
   * 
   * @param callId specifies the Call ID
   * @param statusCode Specifies the status code of the response
   * @param content specifies the Content to add in the INVITE response.
   * @param headers specifies the headers to be added in the INVITE response.
   * @throws SipClientStackException In case of error.
   */
  public void sendInviteProvisionalResponse ( String callId, int statusCode, Content content, Vector<SipHeader> headers)
      throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException(
          "CallControlImp:sendInviteProvionalResponse error: can not find a SipDialog with callId: " + callId);
    }
    sipDialog.sendInviteProvisionalResponse( statusCode, content, headers);
  }

  /**
   * Send a INVITE redirected Response
   * 
   * @param callId specifies the Call ID
   * @param statusCode Specifies the status code of the response
   * @param content specifies the Content to add in the INVITE response.
   * @param headers specifies the headers to be added in the INVITE response.
   * @throws SipClientStackException In case of error.
   */
  public void sendInviteRedirectedResponse ( String callId, int statusCode, Content content, Vector<SipHeader> headers)
      throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException(
          "CallControlImp:sendInviteRedirectedResponse error: can not find a SipDialog with callId: " + callId);
    }
    sipDialog.sendInviteRedirectedResponse( statusCode, content, headers);
  }

  /**
   * Send a INVITE success Response
   * 
   * @param callId specifies the Call ID
   * @param statusCode Specifies the status code of the response
   * @param content specifies the Content to add in the INVITE response.
   * @param headers specifies the headers to be added in the INVITE response.
   * @throws SipClientStackException In case of error.
   */
  public void sendInviteSuccessResponse ( String callId, int statusCode, Content content, Vector<SipHeader> headers)
      throws SipClientStackException
  {
    logger.debug( "CallControlImp:sendInviteSuccessResponse ");
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException(
          "CallControlImp:sendInviteSuccessResponse error: can not find a SipDialog with callId: " + callId);
    }
    sipDialog.sendInviteSuccessResponse( statusCode, content, headers);
  }

  /**
   * Send a INVITE error Response
   * 
   * @param callId specifies the Call ID
   * @param statusCode Specifies the status code of the response
   * @param headers specifies the headers to be added in the INVITE response.
   * @throws SipClientStackException In case of error.
   */
  public void sendInviteErrorResponse ( String callId, int statusCode, Vector<SipHeader> headers)
      throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException(
          "CallControlImp:sendInviteErrorResponse error: can not find a SipDialog with callId: " + callId);
    }
    sipDialog.sendInviteErrorResponse( statusCode, headers);
  }

  /**
   * Send a Bye success response
   * 
   * @param callId specifies the Call ID
   * @param statusCode Specifies the status code of the response
   * @param headers specifies the headers to be added in the BYE response.
   * @return the Call-ID created
   * @throws SipClientStackException In case of error.
   */
  public void sendByeSuccessResponse ( String callId, int statusCode, Vector<SipHeader> headers)
      throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException(
          "CallControlImp:sendByeSuccessResponse error: can not find a SipDialog with callId: " + callId);
    }
    sipDialog.sendByeSuccessResponse( statusCode, headers);
  }

  /**
   * Send a Bye error response
   * 
   * @param callId specifies the Call ID
   * @param statusCode Specifies the status code of the response
   * @param headers specifies the headers to be added in the BYE response.
   * @return the Call-ID created
   * @throws SipClientStackException In case of error.
   */
  public void sendByeErrorResponse ( String callId, int statusCode, Vector<SipHeader> headers)
      throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException(
          "CallControlImp:sendByeErrorResponse error: can not find a SipDialog with callId: " + callId);
    }
    sipDialog.sendByeErrorResponse( statusCode, headers);
  }

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
  public void sendMessageSuccessResponse ( String callId, int statusCode, Content content, Vector<SipHeader> headers)
      throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException(
          "CallControlImp:sendMessageSuccessResponse error: can not find a SipDialog with callId: " + callId);
    }
    sipDialog.sendMessageSuccessResponse( statusCode, content, headers);
  }

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
      throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException(
          "CallControlImp:sendMessageErrorResponse error: can not find a SipDialog with callId: " + callId);
    }
    sipDialog.sendMessageErrorResponse( statusCode, headers);
  }

  /**
   * Send a Options success response
   * 
   * @param callId specifies the Call ID
   * @param statusCode Specifies the status code of the response
   * @param content specifies the Content to add in the OPTIONS response.
   * @param headers specifies the headers to be added in the OPTIONS response.
   * @throws SipClientStackException In case of error.
   */
  public void sendOptionsSuccessResponse ( String callId, int statusCode, Content content, Vector<SipHeader> headers)
      throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException(
          "CallControlImp:sendOptionsSuccessResponse error: can not find a SipDialog with callId: " + callId);
    }
    sipDialog.sendOptionsSuccessResponse( statusCode, content, headers);
  }

  /**
   * Send a Options error response
   * 
   * @param callId specifies the Call ID
   * @param statusCode Specifies the status code of the response
   * @param headers specifies the headers to be added in the OPTIONS response.
   * @throws SipClientStackException In case of error.
   */
  public void sendOptionsErrorResponse ( String callId, int statusCode, Vector<SipHeader> headers)
      throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException(
          "CallControlImp:sendOptionsErrorResponse error: can not find a SipDialog with callId: " + callId);
    }
    sipDialog.sendOptionsErrorResponse( statusCode, headers);
  }

  /**
   * Send a Update success response
   * 
   * @param callId specifies the Call ID
   * @param statusCode Specifies the status code of the response
   * @param content specifies the Content to add in the UPDATE response.
   * @param headers specifies the headers to be added in the UPDATE response.
   * @throws SipClientStackException In case of error.
   */
  public void sendUpdateSuccessResponse ( String callId, int statusCode, Content content, Vector<SipHeader> headers)
      throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException(
          "CallControlImp:sendUpdateSuccessResponse error: can not find a SipDialog with callId: " + callId);
    }
    sipDialog.sendUpdateSuccessResponse( statusCode, content, headers);
  }

  /**
   * Send a Update success response
   * 
   * @param callId specifies the Call ID
   * @param statusCode Specifies the status code of the response
   * @param headers specifies the headers to be added in the UPDATE response.
   * @throws SipClientStackException In case of error.
   */
  public void sendUpdateErrorResponse ( String callId, int statusCode,  Vector<SipHeader> headers)
      throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException(
          "CallControlImp:sendUpdateErrorResponse error: can not find a SipDialog with callId: " + callId);
    }
    sipDialog.sendUpdateErrorResponse( statusCode,  headers);
  }

  /**
   * Send a Notify success Response
   * 
   * @param callId specifies the Call ID
   * @param statusCode Specifies the status code of the response
   * @param content specifies the Content to add in the NOTIFY response.
   * @param headers specifies the headers to be added in the NOTIFY response.
   * @throws SipClientStackException In case of error.
   */
  public void sendNotifySuccessResponse ( String callId, int statusCode, Content content, Vector<SipHeader> headers)
      throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException(
          "CallControlImp:sendNotifySuccessResponse error: can not find a SipDialog with callId: " + callId);
    }
    sipDialog.sendNotifySuccessResponse( statusCode, content, headers);
  }

  /**
   * Send a Notify error Response
   * 
   * @param callId specifies the Call ID
   * @param statusCode Specifies the status code of the response
   * @param headers specifies the headers to be added in the NOTIFY response.
   * @throws SipClientStackException In case of error.
   */
  public void sendNotifyErrorResponse ( String callId, int statusCode,  Vector<SipHeader> headers)
      throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException(
          "CallControlImp:sendNotifyErrorResponse error: can not find a SipDialog with callId: " + callId);
    }
    sipDialog.sendNotifyErrorResponse( statusCode, headers);
  }

  /**
   * Send a Prack success response
   * 
   * @param callId specifies the Call ID
   * @param statusCode Specifies the status code of the response
   * @param content specifies the Content to add in the PRACK response.
   * @param headers specifies the headers to be added in the PRACK response.
   * @throws SipClientStackException In case of error.
   */
  public void sendPrackSuccessResponse ( String callId, int statusCode, Content content, Vector<SipHeader> headers)
      throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException(
          "CallControlImp:sendPrackSuccessResponse error: can not find a SipDialog with callId: " + callId);
    }
    sipDialog.sendPrackSuccessResponse( statusCode, content, headers);
  }

  /**
   * Send a Prack error response
   * 
   * @param callId specifies the Call ID
   * @param statusCode Specifies the status code of the response
   * @param headers specifies the headers to be added in the PRACK response.
   * @throws SipClientStackException In case of error.
   */
  public void sendPrackErrorResponse ( String callId, int statusCode, Vector<SipHeader> headers)
      throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException(
          "CallControlImp:sendPrackErrorResponse error: can not find a SipDialog with callId: " + callId);
    }
    sipDialog.sendPrackErrorResponse( statusCode, headers);
  }

  /**
   * Send a Cancel success response
   * 
   * @param callId specifies the Call ID
   * @param statusCode Specifies the status code of the response
   * @param headers specifies the headers to be added in the CANCEL response.
   * @throws SipClientStackException In case of error.
   */
  public void sendCancelSuccessResponse ( String callId,int statusCode, Vector<SipHeader> headers) throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException(
          "CallControlImp:sendPrackErrorResponse error: can not find a SipDialog with callId: " + callId);
    }
    sipDialog.sendCancelSuccessResponse( statusCode,  headers);
  }

  /**
   * Send a Cancel error response
   * 
   * @param callId specifies the Call ID
   * @param statusCode Specifies the status code of the response
   * @param headers specifies the headers to be added in the CANCEL response.
   * @throws SipClientStackException In case of error.
   */
  public void sendCancelErrorResponse ( String callId,int statusCode, Vector<SipHeader> headers) throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException(
          "CallControlImp:sendCancelErrorResponse error: can not find a SipDialog with callId: " + callId);
    }
    sipDialog.sendCancelErrorResponse( statusCode,  headers);
  }

  /**
   * Send a Info success response
   * 
   * @param callId specifies the Call ID
   * @param statusCode Specifies the status code of the response
   * @param content specifies the Content to add in the INFO response.
   * @param headers specifies the headers to be added in the INFO response.
   * @throws SipClientStackException In case of error.
   */
  public void sendInfoSuccessResponse ( String callId, int statusCode, Content content, Vector<SipHeader> headers)
      throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException(
          "CallControlImp:sendInfoSuccessResponse error: can not find a SipDialog with callId: " + callId);
    }
    sipDialog.sendInfoSuccessResponse( statusCode,content,  headers);
  }

  /**
   * Send a Info error response
   * 
   * @param callId specifies the Call ID
   * @param statusCode Specifies the status code of the response
   * @param headers specifies the headers to be added in the INFO response.
   * @throws SipClientStackException In case of error.
   */
  public void sendInfoErrorResponse ( String callId, int statusCode,Vector<SipHeader> headers)
      throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException(
          "CallControlImp:sendInfoErrorResponse error: can not find a SipDialog with callId: " + callId);
    }
    sipDialog.sendInfoErrorResponse( statusCode, headers);
  }

  /**
   * Send a Refer success response
   * 
   * @param callId specifies the Call ID
   * @param statusCode Specifies the status code of the response
   * @param content specifies the Content to add in the REFER response.
   * @param headers specifies the headers to be added in the REFER response.
   * @throws SipClientStackException In case of error.
   */
  public void sendReferSuccessResponse ( String callId, int statusCode, Content content, Vector<SipHeader> headers)
      throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException(
          "CallControlImp:sendReferSuccessResponse error: can not find a SipDialog with callId: " + callId);
    }
    sipDialog.sendReferSuccessResponse( statusCode,content,  headers);
  }

  /**
   * Send a Refer error response
   * 
   * @param callId specifies the Call ID
   * @param statusCode Specifies the status code of the response
   * @param headers specifies the headers to be added in the REFER response.
   * @throws SipClientStackException In case of error.
   */
  public void sendReferErrorResponse ( String callId, int statusCode, Vector<SipHeader> headers)
      throws SipClientStackException
  {
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException(
          "CallControlImp:sendReferErrorResponse error: can not find a SipDialog with callId: " + callId);
    }
    sipDialog.sendReferErrorResponse( statusCode, headers);
  }
  /**
   * get the Replaces Header for a current call
   * @return the Replaces Header
   * @throws SipClientStackException In case of error.
   */
  public SipHeader getReplacesHeader (  String callId)
      throws SipClientStackException{
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException(
          "CallControlImp:getReplacesHeader error: can not find a SipDialog with callId: " + callId);
    }
    return sipDialog.getReplacesHeader();
    
  }
  
  /**
   * get the Contact header received for a current call
   * @return the Contact Header
   * @throws SipClientStackException In case of error.
   */
  public SipHeader getContactHeaderReceived (  String callId)
      throws SipClientStackException{
    SipDialog sipDialog = sipPhone.getSipDialog( callId);
    if (sipDialog == null)
    {
      throw new SipClientStackException(
          "CallControlImp:getContactHeaderReceived error: can not find a SipDialog with callId: " + callId);
    }
    return sipDialog.getContactHeaderReceived();
    
    
    
  }
}
