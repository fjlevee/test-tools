package com.fjl.test.tools.sip.call;

import java.util.Vector;

import com.fjl.test.tools.sip.SipConstants;
import com.fjl.test.tools.sip.SipConstants.IfcType;
import com.fjl.test.tools.sip.utils.SipHeader;

public final class SipTools {
  
  
  
  public static String getIfcAsString(IfcType ifcType){
    switch (ifcType) {
    case ORIGINATING_REGISTERED:
      return SipConstants.ORIGINATING_REGISTERED;
    case TERMINATING_REGISTERED:
      return SipConstants.TERMINATING_REGISTERED;
    case TERMINATING_UNREGISTERED:
      return SipConstants.TERMINATING_UNREGISTERED;
    default:
      return null;
    }
    
  }
  public static IfcType getIfc(String ifcString){
    if(ifcString.equals(SipConstants.ORIGINATING_REGISTERED)){
      return IfcType.ORIGINATING_REGISTERED;
    }
    else if(ifcString.equals(SipConstants.TERMINATING_REGISTERED)){
      return IfcType.TERMINATING_REGISTERED;
    }
    else if(ifcString.equals(SipConstants.TERMINATING_UNREGISTERED)){
      return IfcType.TERMINATING_UNREGISTERED;
    }else {
      return IfcType.UNDEFINED;
    }    
  }

  /**
   * Format the user SIP URI.
   * If sip URI contains a  msisdn (user part of the uri)
   * the MSISDN is returned 
   * 
   * @param uri as string
   * @return the telephone number
   */
  public static String getUserNameOrTelInURI( final String uri ) 
  {
    // first remove "sip:" or tel: if in String
    String telephoneNumber= uri;
    int beginIndex = uri.indexOf(":");
    if (beginIndex==-1) 
    {
      beginIndex = 0;
     
    }
    
    if (beginIndex>=0)
    {
      telephoneNumber = uri.substring(beginIndex+1);
    }
    
    int endIndex = telephoneNumber.indexOf('@');
    if (endIndex==-1) 
    {
      endIndex= telephoneNumber.indexOf('>');
    }
    
    if (endIndex==-1) 
    {
      endIndex= telephoneNumber.length();
    }

    telephoneNumber = telephoneNumber.substring(0,endIndex);

    return telephoneNumber;
    
  }
  

      

  /**
   * Get the Sip Response code String reason/
   * @param code the Sip Response code
   * @return the Sip Response code reason
   */
  public static String getSipCodeStringReason(int code){
    switch (code)
    {
      case 100:
        return "Trying";
      case 180:
        return "Ringing";
      case 181:
        return "Call Is Being Forwarded";
      case 182:
        return "182 Queued";
      case 183:
        return "Session Progress";
      case 200:
        return "OK";
      case 202:
        return "Accepted"; 
      case 300:
        return "Multiple Choices";
      case 301:
        return "Moved Permanently";
      case 302:
        return "Moved Temporarily";
      case 305:
        return "Use Proxy";
      case 380:
        return "Alternative Service";
      case 400:
        return "Bad Request";
      case 401:
        return "Unauthorized";// (Used only by registrars or user agents. Proxies should use proxy authorization 407)
      case 402:
        return "Payment Required";// (Reserved for future use)
      case 403:
        return "Forbidden";
      case 404:
        return "Not Found";// (User not found)
      case 405:
        return "Method Not Allowed";
      case 406:
        return "Not Acceptable";
      case 407:
        return "Proxy Authentication Required";
      case 408:
        return "Request Timeout";// (Couldn't find the user in time)
      case 410:
        return "Gone";// (The user existed once, but is not available here any more.)
      case 412:
        return "Conditional Request Failed";
      case 413:
        return "Request Entity Too Large";
      case 414:
        return "Request-URI Too Long";
      case 415:
        return "Unsupported Media Type";
      case 416:
        return "Unsupported URI Scheme";
      case 417:
        return "Unknown Resource-Priority";
      case 420:
        return "Bad Extension";// (Bad SIP Protocol Extension used, not understood by the server)
      case 421:
        return "Extension Required";
      case 422:
        return "Session Interval Too Small";
      case 423:
        return "Interval Too Brief";
      case 424:
        return "Bad Location Information";
      case 428:
        return "Use Identity Header";
      case 429:
        return "Provide Referrer Identity";
      case 433:
        return "Anonymity Disallowed";
      case 436:
        return "Bad Identity-Info";
      case 437:
        return "Unsupported Certificate";
      case 438:
        return "Invalid Identity Header";
      case 480:
        return "Temporarily Unavailable";
      case 481:
        return "Call/Transaction Does Not Exist";
      case 482:
        return "Loop Detected";
      case 483:
        return "Too Many Hops";
      case 484:
        return "Address Incomplete";
      case 485:
        return "Ambiguous";
      case 486:
        return "Busy Here";
      case 487:
        return "Request Terminated";
      case 488:
        return "Not Acceptable Here";
      case 489:
        return "Bad Event";
      case 491:
        return "Request Pending";
      case 493:
        return "Undecipherable (Could not decrypt S/MIME body part)";
      case 494:
        return "Security Agreement Required";
      case 500:
        return "Server Internal Error";
      case 501:
        return "Not Implemented: The SIP request method is not implemented here";
      case 502:
        return "Bad Gateway";
      case 503:
        return "Service Unavailable";
      case 504:
        return "Server Time-out";
      case 505:
        return "Version Not Supported: The server does not support this version of the SIP protocol";
      case 513:
        return "Message Too Large";
      case 580:
        return "Precondition Failure";
      case 600:
        return "Busy Everywhere";
      case 603:
        return "Decline";
      case 604:
        return "Does Not Exist Anywhere";
      case 606:
        return "Not Acceptable";
      default:
        return "";
    }
    
  }
 /**
  * Format a request to html code
  * @param request
  * @return the request formatted
  */
  public static String formatSipRequestToHtmlCode(String request){
    
    // '<' replace by &#60;
    request = request.replace( "<", "&#60");
    // '>' replace by &#62;
    request = request.replace( ">", "&#62");
    return request;
  }
  /**
   * Check if the given list of sip headers contains the Require Header, then check that
   * this Require Header Contains the 100rel Value
   * @param headers Specifies the list of Sip Headers
   * @return true if the given list of Sip Headers contains the Require Header with the 100rel value
   */
  public static boolean contain100relParameter(Vector<SipHeader> headers){
    if(headers!=null){
      SipHeader header = null;
      for(int i=0;i<headers.size();i++){
        header = headers.get(i);
        if(SipConstants.REQUIRE_HEADER.equals( header.getHeaderName())){
          if (header.getHeaderValue()!=null){
            if(header.getHeaderValue().contains( SipConstants.PARAM_100REL)) return true;
          }
        }
      }
    }
    int toto = 0;
    switch (toto)
    {
      case 0:
      case 1: 
        break;

      default:
        break;
    }
    return false;

  }

  
  /**
   * Check if the Error code returned in a Sip ReInvite Response causes the dialog to be closed
   * @param errorCode Specifis the Error Code returned
   * @return true if the Dialog is to be closed.
   */
  public static boolean isErrorCodeCloseDialog(int errorCode){
    switch (errorCode)
    {
      case SipConstants.RESPONSE_BAD_REQUEST /*400*/:
      case SipConstants.RESPONSE_UNAUTHORIZED /*401*/:
      case SipConstants.RESPONSE_PAYMENT_REQUIRED /*402*/:
      case SipConstants.RESPONSE_FORBIDDEN /*403*/:
      case SipConstants.RESPONSE_NOT_FOUND /*404*/:
      case SipConstants.RESPONSE_METHOD_NOT_ALLOWED /*405*/:
      case SipConstants.RESPONSE_NOT_ACCEPTABLE /*406*/:
      case SipConstants.RESPONSE_PROXY_AUTHENTICATION_REQUIRED /*407*/:
      case SipConstants.RESPONSE_GONE /*410*/:
      case SipConstants.RESPONSE_REQUEST_ENTITY_TOO_LARGE /*413*/:
      case SipConstants.RESPONSE_REQUEST_URI_TOO_LONG /*414*/:
      case SipConstants.RESPONSE_UNSUPPORTED_MEDIA_TYPE/*415*/:
      case SipConstants.RESPONSE_UNSUPPORTED_URI_RESPONSE_SCHEME /*416*/:
      case SipConstants.RESPONSE_BAD_EXTENSION /*420*/:
      case SipConstants.RESPONSE_EXTENSION_REQUIRED /*421*/:
      case SipConstants.RESPONSE_INTERVAL_TOO_BRIEF /*423*/:
      case SipConstants.RESPONSE_TEMPORARLY_UNAVAILABLE/*480*/ :
      
      case SipConstants.RESPONSE_LOOP_DETECTED /*482*/:
      case SipConstants.RESPONSE_TOO_MANY_HOPS /*483*/:
      case SipConstants.RESPONSE_ADDRESS_INCOMPLETE /*484*/:
      case SipConstants.RESPONSE_AMBIGUOUS  /*485*/:
      case SipConstants.RESPONSE_BUSY_HERE /*486*/:
      case SipConstants.RESPONSE_REQUEST_TERMINATED /*487*/:
      case SipConstants.RESPONSE_NOT_ACCEPTABLE_HERE /*488*/:
      case SipConstants.RESPONSE_REQUEST_PENDING /*491*/:
      case SipConstants.RESPONSE_UNDECIPHERABLE /*493*/:
      case SipConstants.RESPONSE_SERVER_INTERNAL_ERROR /*500*/:
      case SipConstants.RESPONSE_NOT_IMPLEMENTED /*501*/:
      case SipConstants.RESPONSE_BAD_GATEWAY /*502*/:
      case SipConstants.RESPONSE_SERVICE_UNAVAILABLE /*503*/:
      case SipConstants.RESPONSE_SERVER_TIMEOUT /*504*/:
      case SipConstants.RESPONSE_VERSION_NOT_SUPPORTED /*505*/:
      case SipConstants.RESPONSE_MESSAGE_TOO_LARGE /*513*/:
      case SipConstants.RESPONSE_BUSY_EVERYWHERE /*600*/:
      case SipConstants.RESPONSE_DECLINE /*603*/:
      case SipConstants.RESPONSE_DOES_NOT_EXIT_ANYWHERE /*604*/:
      case SipConstants.RESPONSE_NOT_ACCEPTABLE_ANYWHERE /*606*/:
          return false;
        
      case SipConstants.RESPONSE_REQUEST_TIMEOUT /*408*/:
      case SipConstants.RESPONSE_CALL_LEG_DONE /*481*/:
        return true;
        
      default:
        return false;
        
    }
  }
}
