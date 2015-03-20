package com.fjl.test.tools.sip.authentication;

import java.text.ParseException;
import java.util.Date;
import java.util.Random;

import javax.sip.address.AddressFactory;
import javax.sip.address.URI;
import javax.sip.header.AuthorizationHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ProxyAuthorizationHeader;

import org.slf4j.Logger;

import com.fjl.test.tools.sip.SipConstants;
import com.fjl.test.tools.sip.SipPhone;
import com.fjl.test.tools.sip.call.SipDialog;

public class AuthenticationUtils
{
  
  
  private static Random random = new Random( (new Date()).getTime());
  
  

    /**
     * This method returns a newly generated unique nonce .
     * 
     * @return A String nonce
     */
    public static String generateNonce ()
    {
      int r = random.nextInt();
      r = (r < 0) ? 0 - r : r; // generate a positive number
      return Integer.toString( r);
    }
  
  
  /**
   * 
   * @param sipPhone
   * @return
   * @throws ParseException
   */
  public static AuthorizationHeader createAuthorizationHeader(Logger logger,SipPhone sipPhone) throws ParseException{
    logger.debug( "AuthenticationUtils.createAuthenticationHeader");
    String headerReturn = null;
    Nonce registerNonce = sipPhone.useRegisterNonce();
    if(registerNonce==null){
      logger.debug( "AuthenticationUtils.createAuthorizationHeader registerNonce is null => No Authorization Header created");
      return null;
    }
    
    HeaderFactory headerFactory = sipPhone.getSipLayer().getHeaderFactory();
    AddressFactory addressFactory = sipPhone.getSipLayer().getAddressFactory();
    AuthorizationHeader authorizationHeader =  headerFactory.createAuthorizationHeader( "Digest");
    authorizationHeader.setAlgorithm( "MD5");
    authorizationHeader.setUsername( sipPhone.getSipProfile().getPrivateIdentity());
    authorizationHeader.setRealm( sipPhone.getSipProfile().getUserRealm());
    URI uri = addressFactory.createURI( "sip:"+sipPhone.getSipProfile().getUserDomain());
    authorizationHeader.setURI(  uri);
    String qop = "auth";
    authorizationHeader.setQop( qop);
    String clientNonce = generateNonce();
    authorizationHeader.setCNonce(clientNonce);
    
    authorizationHeader.setNonce( registerNonce.getNonceValue());
    authorizationHeader.setNonceCount( registerNonce.getNonceCount());
    
    String ha1 = DigestTools.getHA1(sipPhone.getSipProfile().getPrivateIdentity(),sipPhone.getSipProfile().getUserRealm(),sipPhone.getSipProfile().getPassword());
    
    String response = DigestTools.getDigest( ha1, registerNonce.getNonceValue(), getNonceCountAsString(registerNonce.getNonceCount()), clientNonce, qop, "REGISTER", uri.toString());
    
    authorizationHeader.setResponse( response);
    
    
    headerReturn = authorizationHeader.toString().substring( SipConstants.AUTHORIZATION_HEADER.length()+2);
    headerReturn = headerReturn.replace( ",", ", ");
   logger.debug( "AuthenticationUtils.createAuthorizationHeader: Header Value Returned: {}", headerReturn);
    return authorizationHeader;
  }
  /**
   * 
   * @param sipPhone
   * @return
   * @throws ParseException
   */
  public static ProxyAuthorizationHeader createProxyAuthorizationHeader(Logger logger,SipPhone sipPhone,String method) throws ParseException{
    logger.debug( "AuthenticationUtils.createProxyAuthorizationHeader");
    String headerReturn = null;
    Nonce nonce = sipPhone.useNonce();
    if(nonce==null){
      logger.debug( "AuthenticationUtils.createProxyAuthorizationHeader registerNonce is null => No Authorization Header created");
      return null;
    }
    
    HeaderFactory headerFactory = sipPhone.getSipLayer().getHeaderFactory();
    AddressFactory addressFactory = sipPhone.getSipLayer().getAddressFactory();
    ProxyAuthorizationHeader authorizationHeader =  headerFactory.createProxyAuthorizationHeader(  "Digest");
    authorizationHeader.setAlgorithm( "MD5");
    authorizationHeader.setUsername( sipPhone.getSipProfile().getPrivateIdentity());
    authorizationHeader.setRealm( sipPhone.getSipProfile().getUserRealm());
    URI uri = addressFactory.createURI( "sip:"+sipPhone.getSipProfile().getUserDomain());
    authorizationHeader.setURI(  uri);
    String qop = "auth";
    authorizationHeader.setQop( qop);
    String clientNonce = generateNonce();
    authorizationHeader.setCNonce(clientNonce);
    
    authorizationHeader.setNonce( nonce.getNonceValue());
    authorizationHeader.setNonceCount( nonce.getNonceCount());
    
    String ha1 = DigestTools.getHA1(sipPhone.getSipProfile().getPrivateIdentity(),sipPhone.getSipProfile().getUserRealm(),sipPhone.getSipProfile().getPassword());
    
    String response = DigestTools.getDigest( ha1, nonce.getNonceValue(), getNonceCountAsString(nonce.getNonceCount()), clientNonce, qop, method, uri.toString());
    
    authorizationHeader.setResponse( response);
    
    
    headerReturn = authorizationHeader.toString().substring( SipConstants.PROXY_AUTHORIZATION_HEADER.length()+2);
    headerReturn = headerReturn.replace( ",", ", ");
    logger.debug( "AuthenticationUtils.createProxyAuthorizationHeader: Header Value Returned: {}", headerReturn);
    return authorizationHeader;
  }
  
  
  
  /**
   * 
   * @param nounceCount
   * @return
   */
  public static String getNonceCountAsString(int nounceCount){
    
    String nc = ""+nounceCount;
    while (nc.length()<8){
      nc = "0"+nc;
    }
    return nc;
  }
  
}
