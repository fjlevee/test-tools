package com.fjl.test.tools.sip.call.state.register;

import java.text.ParseException;
import java.util.Vector;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.header.AuthenticationInfoHeader;
import javax.sip.header.Header;
import javax.sip.header.WWWAuthenticateHeader;

import com.fjl.test.tools.sip.SipConstants;
import com.fjl.test.tools.sip.SipEventListener;
import com.fjl.test.tools.sip.SipPhone;
import com.fjl.test.tools.sip.authentication.AuthenticationUtils;
import com.fjl.test.tools.sip.call.SipClientStackException;
import com.fjl.test.tools.sip.call.SipDialog;
import com.fjl.test.tools.sip.call.SipTools;
import com.fjl.test.tools.sip.call.content.Content;
import com.fjl.test.tools.sip.call.state.SipDialogState;
import com.fjl.test.tools.sip.call.state.SipDialogState.DialogState;
import com.fjl.test.tools.sip.utils.SipHeader;

public class RegisteredState extends SipDialogState
{

  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#sendRegister(com.fjl.test.tools.sip.call.SipDialog, java.lang.String, com.fjl.test.tools.sip.call.content.Content, java.util.Vector)
   */
  @Override
  public void sendRegister ( SipDialog sipDialog, Content content, Vector<SipHeader> headers)
      throws SipClientStackException
  {
    
    
    if(headers ==null){
      headers =  new Vector<SipHeader>();
    }
    
      long expires =sipDialog.getRegisterExpires();
      headers.add( new SipHeader( SipConstants.EXPIRES_HEADER,""+sipDialog.getRegisterExpires()));
      SipPhone sipPhone= sipDialog.getSipPhone();
      // Check if a Register once has been received
      if(sipPhone.hasRegisterNonce()){
        headers.add( new SipHeader( SipConstants.AUTHORIZATION_HEADER,null));
      }
      
      sipDialog.createAndSendRequest( "REGISTER",null, headers);
      if(expires>0){
        sipDialog.setSipDialogState( DialogState.REGISTERING);
      }else{
        sipDialog.setSipDialogState( DialogState.UNREGISTERING);
      }
    
  }

  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleRegisterSuccessResponse(com.fjl.test.tools.sip.call.SipDialog, javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
   */
  @Override
  public void handleRegisterSuccessResponse ( SipDialog sipDialog, ResponseEvent responseEvent,
      SipEventListener sipEventListener) throws SipClientStackException
  {
    logger.trace( "RegisteredState.handleRegisterSuccessResponse");
   try{
    
    Header authInfo = responseEvent.getResponse().getHeader( SipConstants.AUTHENTICATION_INFO_HEADER);
    if(authInfo!=null){
      logger.trace( "RegisteredState.handleRegisterSuccessResponse AuthenticationInfoHeader received: {}",authInfo.toString());
      AuthenticationInfoHeader authenticationInfoHeader = (AuthenticationInfoHeader) authInfo;
      String nextNonce =  authenticationInfoHeader.getNextNonce();
      sipDialog.getSipPhone().updateRegisterNonce( nextNonce);
    }
   
    
    sipDialog.setSipDialogState( DialogState.REGISTERED);
    sipEventListener.handleRegisterSuccessResponse( sipDialog.getCallId(), responseEvent);
   }catch (Exception e) {
     throw new SipClientStackException( "RegisteredState.sendRegister Exception: "+e.getMessage());
   }
  }

  /**
   * 
   * @see com.fjl.test.tools.sip.call.state.SipDialogState#handleInfoErrorResponse(com.fjl.test.tools.sip.call.SipDialog,
   *      javax.sip.ResponseEvent, com.fjl.test.tools.sip.SipEventListener)
   */
  @Override
  public void handleRegisterErrorResponse ( SipDialog sipDialog, ResponseEvent responseEvent,
      SipEventListener sipEventListener) throws SipClientStackException
  {
    logger.trace( "UnregisteringState.handleRegisterErrorResponse: Status code is: {}",responseEvent.getResponse().getStatusCode());
    if(SipConstants.RESPONSE_UNAUTHORIZED==responseEvent.getResponse().getStatusCode()){
      Header wwwAuthenticate = responseEvent.getResponse().getHeader( SipConstants.WWW_AUTHENTICATE_HEADER);
      if(wwwAuthenticate!=null){
        WWWAuthenticateHeader wwwAuthenticateHeader = (WWWAuthenticateHeader) wwwAuthenticate;
        sipDialog.getSipPhone().updateRegisterNonce( wwwAuthenticateHeader.getNonce());
      }
    }
    else{
      sipDialog.setSipDialogState( DialogState.CLOSED);
    }
    sipEventListener.handleRegisterErrorResponse( sipDialog.getCallId(), responseEvent);
    
  }
  

}
