package com.fjl.test.tools.sip;

public class SipClient
{
 

  /**
   * Default Sip Client Port (5060)
   */
  private static int SIP_CLIENT_DEFAULT_PORT = 5060;

  /**
   * Sip Client User Name
   */
  String userName;

  /**
   * Sip Client User Doamin
   */
  String userDomain;

  /**
   * Sip Client User port
   */
  int userPort;

  /**
   * Sip Client Uri
   */
  String uri;

  /**
   * Sip Client Sip Uri
   */
  String sipUri;

  /**
   * Sip Client Tel Uri
   */
  String telUri;

  /**
   * isSipUri parameter
   */
  boolean isSipUri;

  /**
   * Constructor
   * 
   * @param uri
   */
  public SipClient ( String uri)
  {
    this.uri = uri;
    this.userPort = 0;
    setParametersFromUri();
  }

  /**
   * set the Client Parameters From a Sip Uri
   * 
   */
  private void setParametersFromUri ()
  {
    if (uri != null)
    {
      if (uri.startsWith( "sip:"))
      {
        setParametersFromSipUri();
      }
      else
      {
        setParametersFromTelUri();
      }
    }
  }

  /**
   * set the Client Parameters From a Sip Uri
   * 
   */
  private void setParametersFromSipUri ()
  {
    isSipUri = true;
    sipUri = uri;
    userPort = SIP_CLIENT_DEFAULT_PORT;
    // First remove the sip: subString
    String subSipUri = sipUri.substring( sipUri.indexOf( ":") + 1);
    int indexOfArobase = subSipUri.indexOf( "@");
    int indexOfDots = subSipUri.indexOf( ":");
    String portString = null;
    if (indexOfArobase == -1)
    {
      userName = null;
      if (indexOfDots == -1)
      {
        userDomain = subSipUri;
      }
      else
      {
        userDomain = subSipUri.substring( 0, indexOfDots);
        portString = subSipUri.substring( indexOfDots + 1);

      }

    }
    else
    {
      // there is a user name
      userName = subSipUri.substring( 0, indexOfArobase);
      if (indexOfDots == -1)
      {
        userDomain = subSipUri.substring( indexOfArobase + 1);
      }
      else
      {
        userDomain = subSipUri.substring( indexOfArobase + 1, indexOfDots);
        portString = subSipUri.substring( indexOfDots + 1);

      }
    }
    if (portString != null)
    {
      int index = portString.indexOf( ";");
      if (index != -1)
      {
        portString = portString.substring( 0, index - 1);
      }

      userPort = Integer.parseInt( portString);
    }

  }

  /**
   * set the Client Parameters From a Tel Uri
   * 
   */
  private void setParametersFromTelUri ()
  {
    isSipUri = false;
    telUri = uri;
    userName = telUri.substring( telUri.indexOf( ":"));
  }

  /**
   * Get the Sip Client Sip Uri
   * 
   * @return the Sip Client Sip Uri
   */
  public String getUri ()
  {
    StringBuffer buffer = new StringBuffer();
    if (isSipUri)
    {
      buffer.append( "sip:");
      if (userName != null)
      {
        buffer.append( userName);
        buffer.append( "@");
      }
      buffer.append( userDomain);
      if (userPort != 0)
      {
        buffer.append( ":");
        buffer.append( userPort);
      }
    }
    else
    {
      buffer.append( "tel:");
      buffer.append( userName);
    }
    return buffer.toString();

  }

  /**
   * Set the Sip Client uri
   * 
   * @param uri specifies the new Uri
   */
  public void setUri ( String uri)
  {
    this.sipUri = null;
    this.telUri = null;
    this.uri = uri;
    this.userDomain = null;
    this.userName = null;
    this.userPort = SIP_CLIENT_DEFAULT_PORT;
    setParametersFromUri();
  }

  /**
   * Get the Full Uri
   * 
   * @return the Full Uri
   */
  public String getFullUri ()
  {
    return uri;
  }

  /**
   * Get the Sip Client Domain
   * 
   * @return the Sip Client Domain
   */
  public String getUserDomain ()
  {

    return userDomain;
  }

  /**
   * Set the Sip Client Domain
   * 
   * @param userDomain specifies the new Sip Client Domain
   */
  public void setUserDomain ( String userDomain)
  {
    this.userDomain = userDomain;
  }

  /**
   * Get the Sip Client name
   * 
   * @return the Sip Client name
   */
  public String getUserName ()
  {
    return userName;
  }

  /**
   * Set the Sip Client name
   * 
   * @param userName specifies the new Sip Client name
   */
  public void setUserName ( String userName)
  {
    this.userName = userName;
  }

  /**
   * Get the Sip Client Port
   * 
   * @return the Sip Client Port
   */
  public int getUserPort ()
  {
    return userPort;
  }

  /**
   * Set the Sip Client Port
   * 
   * @param userPort specifies the new Sip Client Port
   */
  public void setUserPort ( int userPort)
  {
    this.userPort = userPort;
  }

  /**
   * Get a String representation of this SipClient
   * 
   * @return a String representation of this SipClient
   */
  public String getString ()
  {
    StringBuffer buffer = new StringBuffer();
    if (isSipUri)
    {
      if (userName != null)
      {
        buffer.append( " User Name: " + userName);
      }
      buffer.append( " Domain: ");
      buffer.append( userDomain);
      buffer.append( ", Sip Port: ");
      buffer.append( userPort);
    }
    else
    {

      buffer.append( "User number: ");
      buffer.append( userName);
    }
    return buffer.toString();
  }

  /**
   * Chech if the Sip Client is a Sip Uri.
   * 
   * @return true if the Sip Client is a Sip Uri else false if it is a tel uri
   */
  public boolean isSipUri ()
  {
    return isSipUri;
  }
}
