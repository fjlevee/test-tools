package com.fjl.test.tools.sip;



public class SipProfile {
  
  private final static String PROFILE_NAME = "javax.sip.PROFILE_NAME";
  
  private final static String OUTBOUND_PROXY_ADDRESS = "javax.sip.OUTBOUND_PROXY_ADDRESS";
  
  private final static String OUTBOUND_PROXY_PORT = "javax.sip.OUTBOUND_PROXY_PORT";
  
  private final static String PROTOCOL = "javax.sip.PROTOCOL";
    
  private final static String DISPLAY_NAME = "javax.sip.USER_DISPLAY_NAME";
  
  private final static String USER_IDENTITY = "javax.sip.USER_IDENTITY";
  
  private final static String USER_DOMAIN = "javax.sip.USER_DOMAIN";
  
  private final static String USER_PASSWORD = "javax.sip.USER_PASSWORD";
  
  private final static String UDP_LISTEN_PORT = "javax.sip.UDP_LISTEN_PORT";
  
  private final static String TCP_LISTEN_PORT = "javax.sip.TCP_LISTEN_PORT";
  
  private static final String PROFILES_FOLDER = "profiles";
  
  private static final String PROPERTIES_EXTENSION = ".properties";
  /**
   * udp String constant
   */
  public final static String UDP = "udp";
  /**
   * tcp String constant
   */
  public final static String TCP = "tcp";
  /**
   * Name of this Profile
   */
  private String profileName;
  /**
   * Sip Proxy Ip Address
   */
  private String proxyIpAddress;
  /**
   * Sip Proxy Port
   */
  private int proxyPort;
  /**
   * Sip udp Listen Port
   */
  private int udpListenPort;
  /**
   * Sip tcp Listen Port
   */
  private int tcpListenPort;
  /**
   * Sip User Display Name
   */
  private String userDisplayName;
  /**
   * Sip User Identity
   */
  private String userIdentity;
  /**
   * Sip User Private Identity
   */
  private String privateIdentity;
  /**
   * Sip User Domain
   */
  private String userDomain;
  /**
   * Sip User realm
   */
  private String userRealm;
  /**
   * Sip User Password
   */
  private String password;
  /**
   * Protocol used (Udp or Tcp)
   */
  private String protocol;
  
  
  /**
   * 
   * Constructor of SipProfile.
   * 
   */
  public SipProfile(boolean isImsProfile){
    this.profileName = null;
    this.proxyIpAddress= null;
    this.proxyPort=  5060;
    this.udpListenPort= 5060;
    this.tcpListenPort= 5061;
    this.userDisplayName= null;
    this.userIdentity= null;
    this.privateIdentity = null;
    this.userDomain= null;
    this.userRealm=null;
    this.password= null;
    this.protocol= null;
    
  }
  
  /**
   * 
   * Constructor of SipProfile.
   * 
   * @param profileName specifies the Sip Profile name.
   * @param proxyAddress specifies the Sip Proxy Address
   * @param proxyPort specifies the Sip Proxy Port
   * @param udpListenPort specifies the Udp listen port
   * @param tcpListenPort specifies the Tcp listen port
   * @param userDisplayName specifies the User Display Name
   * @param userIdentity specifies the User identity (User identifier)
   * @param privateIdentity Specifies the User Private identity
   * @param userDomain specifies the the Sip User Domain
   * @param userRealm specifies the the Sip User Realm
   * @param password specifies the the Sip User Password
   * @param protocol specifies the internet Protocol use for Sip (Tcp or Udp)
   * @param isImsProfile specifies if this profile is an IMS profile
   */
  public SipProfile(String profileName,String proxyAddress, int proxyPort, int udpListenPort, int tcpListenPort, 
      String userDisplayName, String userIdentity,String privateIdentity, String userDomain,String userRealm,String password, String protocol){

      this.profileName = profileName;
      this.proxyIpAddress= proxyAddress;
      this.proxyPort=proxyPort;
      this.udpListenPort=udpListenPort;
      this.tcpListenPort= tcpListenPort;
      this.userDisplayName= userDisplayName;
      this.userIdentity= userIdentity;
      this.privateIdentity = privateIdentity;
      this.userDomain= userDomain;
      this.userRealm =userRealm;
      this.password=password;
      this.protocol= protocol;
      
    
  }


  public String toString(){
    StringBuffer buffer = new StringBuffer();
    buffer.append("Sip Configuration: \n"+
        " PROFILE_NAME="+profileName
        +", OUTBOUND_PROXY_ADDRESS="+proxyIpAddress
        +", OUTBOUND_PROXY_PORT="+proxyPort
        +", PROTOCOL="+protocol
        +", UDP_LISTEN_PORT="+udpListenPort
        +", TCP_LISTEN_PORT="+tcpListenPort
        +", DISPLAY_NAME="+userDisplayName
        +", USER_IDENTITY="+userIdentity
        +", USER_PRIVATE_IDENTITY="+privateIdentity
        +", USER_DOMAIN="+userDomain
        +", USER_REALM="+userRealm
        +", USER_PASSWORD="+password
        );
        
    return buffer.toString();
  }
  
  
  
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }


  public String getProtocol() {
    return protocol;
  }

 
  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }


  public String getProxyIpAddress() {
    return proxyIpAddress;
  }
  
  public void setProxyIpAddress(String proxyIpAddress) {
    this.proxyIpAddress=proxyIpAddress;
  }


  public int getProxyPort() {
    return proxyPort;
  }
  
  public void setProxyPort(int proxyPort) {
    this.proxyPort = proxyPort;
  }


  public int getTcpListenPort() {
    return tcpListenPort;
  }
  

  public void setTcpListenPort(int tcpListenPort) {
    this.tcpListenPort= tcpListenPort;
  }


  public int getUdpListenPort() {
    return udpListenPort;
  }
  
  public void setUdpListenPort(int udpListenPort) {
    this.udpListenPort = udpListenPort;
  }


  public String getUserDisplayName() {
    return userDisplayName;
  }
  

  public void setUserDisplayName(String userDisplayName) {
    this.userDisplayName= userDisplayName;
  }


  public String getUserDomain() {
    return userDomain;
  }
 

  public void setUserDomain(String userDomain) {
    this.userDomain=userDomain;
   
  }
  
  public String getUserRealm() {
    return userRealm;
  }
 

  public void setUserRealm(String userRealm) {
    this.userRealm=userRealm;
   
  }
  

  public String getUserIdentity() {
    return userIdentity;
  }
 
  public void setUserIdentity(String userIdentity) {
    this.userIdentity= userIdentity;
  }
  public String getPrivateIdentity() {
    return privateIdentity;
  }
 
  public void setPrivateIdentity(String privateIdentity) {
    this.privateIdentity= privateIdentity;
  }
  
  
  
  
  public String getProfileName ()
  {
    return profileName;
  }

  public void setProfileName ( String profileName)
  {
    this.profileName= profileName;
  }
 
 

}
