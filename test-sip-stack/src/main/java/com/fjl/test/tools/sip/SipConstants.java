package com.fjl.test.tools.sip;



public final class SipConstants {
  
  
  /**
   * IfcType
   */
  public enum IfcType{UNDEFINED,ORIGINATING_REGISTERED,TERMINATING_REGISTERED,TERMINATING_UNREGISTERED};
  
  public static final String TERMINATING_REGISTERED = "term_registered";
  public static final String TERMINATING_UNREGISTERED = "term_unregistered";
  public static final String ORIGINATING_REGISTERED = "orig";
  
  public static final String SAVES_FILE = "save/saves.properties";

  public static final String CLIENT_CONFIGURATION_FILE = "client.properties";
  public static final String MEDIA_CONFIGURATION_FILE = "conf/media.properties";
  
  public static final String LAST_PROFILE_USED_PARAMETER = "sip.client.last.profile.used";
  
  public static final long DEFAULT_EXPIRES_TIME = 3600;
  /**
   * Prefix for the branch parameter that identifies 
   * BIS 09 compatible branch strings. This indicates
   * that the branch may be as a global identifier for
   * identifying transactions.
   */
  public static final String BRANCH_MAGIC_COOKIE = "z9hG4bK";
  
  
  public static final String INVITE = "INVITE";
  public static final String BYE = "BYE";
  public static final String MESSAGE = "MESSAGE";
  public static final String OPTIONS = "OPTIONS";
  public static final String UPDATE = "UPDATE";
  public static final String NOTIFY = "NOTIFY";
  public static final String PRACK = "PRACK";
  public static final String ACK = "ACK";
  public static final String CANCEL = "CANCEL";
  public static final String INFO = "INFO";
  public static final String REFER = "REFER";
  public static final String SUBSCRIBE = "SUBSCRIBE";
  public static final String REGISTER = "REGISTER";
  public static final String TIME_OUT_EVENT = "TIME_OUT_EVENT";
  
  

  
  public static final String RESPONSE_100_TRYING = "Trying";
  public static final String RESPONSE_18O_RINGING = "Ringing";
  public static final String RESPONSE_183_SESSION_PROGRESS = "Session Progress";
  
  public static final String RESPONSE_487_REQUEST_TERMINATED = "Request Terminated";
  public static final String RESPONSE_404_NOT_FOUND = "Not Found";
  
  public static final String RESPONSE_200_OK = "OK";
  
  public static final int RESPONSE_TRYING = 100;
  public static final int RESPONSE_RINGING = 180;
  public static final int RESPONSE_CALL_BEING_FORWARDED = 181;
  public static final int RESPONSE_CALL_QUEUED = 182;
  public static final int RESPONSE_SESSION_PROGRESS = 183;
  public static final int RESPONSE_OK = 200;
  public static final int RESPONSE_ACCEPTED = 202;
  public static final int RESPONSE_MULTIPLE_CHOICES = 300;
  public static final int RESPONSE_MOVED_PERMANENTLY = 301;
  public static final int RESPONSE_MOVED_TEMPORARILY = 302;
  public static final int RESPONSE_USE_PROXY = 305;
  public static final int RESPONSE_ALTERNATIVE_SERVICE = 380;
  public static final int RESPONSE_BAD_REQUEST = 400;
  public static final int RESPONSE_UNAUTHORIZED = 401;
  public static final int RESPONSE_PAYMENT_REQUIRED = 402;
  public static final int RESPONSE_FORBIDDEN = 403;
  public static final int RESPONSE_NOT_FOUND = 404;
  public static final int RESPONSE_METHOD_NOT_ALLOWED = 405;
  public static final int RESPONSE_NOT_ACCEPTABLE = 406;
  public static final int RESPONSE_PROXY_AUTHENTICATION_REQUIRED = 407;
  public static final int RESPONSE_REQUEST_TIMEOUT = 408;
  public static final int RESPONSE_GONE = 410;
  public static final int RESPONSE_REQUEST_ENTITY_TOO_LARGE = 413;
  public static final int RESPONSE_REQUEST_URI_TOO_LONG = 414;
  public static final int RESPONSE_UNSUPPORTED_MEDIA_TYPE = 415;
  public static final int RESPONSE_UNSUPPORTED_URI_RESPONSE_SCHEME = 416;
  public static final int RESPONSE_BAD_EXTENSION = 420;
  public static final int RESPONSE_EXTENSION_REQUIRED = 421;
  public static final int RESPONSE_INTERVAL_TOO_BRIEF = 423;
  public static final int RESPONSE_TEMPORARLY_UNAVAILABLE = 480;
  public static final int RESPONSE_CALL_LEG_DONE = 481;
  public static final int RESPONSE_LOOP_DETECTED = 482;
  public static final int RESPONSE_TOO_MANY_HOPS = 483;
  public static final int RESPONSE_ADDRESS_INCOMPLETE = 484;
  public static final int RESPONSE_AMBIGUOUS = 485;
  public static final int RESPONSE_BUSY_HERE = 486;
  public static final int RESPONSE_REQUEST_TERMINATED = 487;
  public static final int RESPONSE_NOT_ACCEPTABLE_HERE = 488;
  public static final int RESPONSE_REQUEST_PENDING = 491;
  public static final int RESPONSE_UNDECIPHERABLE = 493;
  public static final int RESPONSE_SERVER_INTERNAL_ERROR = 500;
  public static final int RESPONSE_NOT_IMPLEMENTED = 501;
  public static final int RESPONSE_BAD_GATEWAY = 502;
  public static final int RESPONSE_SERVICE_UNAVAILABLE = 503;
  public static final int RESPONSE_SERVER_TIMEOUT = 504;
  public static final int RESPONSE_VERSION_NOT_SUPPORTED = 505;
  public static final int RESPONSE_MESSAGE_TOO_LARGE = 513;
  public static final int RESPONSE_BUSY_EVERYWHERE = 600;
  public static final int RESPONSE_DECLINE = 603;
  public static final int RESPONSE_DOES_NOT_EXIT_ANYWHERE = 604;
  public static final int RESPONSE_NOT_ACCEPTABLE_ANYWHERE = 606;
  
  
  /**
   * Dtmf-Relay sub-content type
   */
  public static String DTMF_RELAY_SUNCONTENT_TYPE="dtmf-relay";
  
  /**
   * Content Type Header
   */
  public static final String CONTENT_TYPE_HEADER= "Content-Type";
  
  /**
   * From Header
   */
  public static final String CALL_ID_HEADER= "Call-ID";
  /**
   * From Header
   */
  public static final String FROM_HEADER= "From";
  /**
   * To Header
   */
  public static final String TO_HEADER= "To";
  /**
   * CSeq Header
   */
  public static final String CSEQ_HEADER= "CSeq";
  /**
   * Require Header
   */
  public static final String REQUIRE_HEADER= "Require";
  /**
   * Replaces Header
   */
  public static final String REPLACES_HEADER= "Replaces";
  
  /**
   * Refer-To Header
   */
  public static final String REFER_TO_HEADER= "Refer-To";
  /**
   * Referred-By Header
   */
  public static final String REFERRED_BY_HEADER= "Referred-By";
  
  /**
   * Contact Header
   */
  public static final String CONTACT_HEADER= "Contact";
  
  /**
   * 100rel parameter
   */
  public static final String PARAM_100REL= "100rel";
  
  /**
   * WWW-Authenticate Header
   */
  public static final String WWW_AUTHENTICATE_HEADER="WWW-Authenticate";
  
  /**
   * Proxy-Authenticate Header
   */
  public static final String PROXY_AUTHENTICATE_HEADER="Proxy-Authenticate";
  
  
  /**
   * Authorization Header
   */
  public static final String AUTHORIZATION_HEADER = "Authorization";
  
  /**
   * Proxy-Authorization Header
   */
  public static final String PROXY_AUTHORIZATION_HEADER = "Proxy-Authorization";
    
  /**
   * Authentication-Info Header
   */
  public static final String AUTHENTICATION_INFO_HEADER = "Authentication-Info";
  /**
   * Expires Header
   */
  public static final String EXPIRES_HEADER="Expires";
  /**
   * Expires Param
   */
  public static final String EXPIRES_PARAM="expires";
  
}
