package com.fjl.test.tools.sip.authentication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * 
 * This class DigestTools is helpful for Digest Authentication
 * 
 * <br>
 * HA1 = MD5(A1)= MD5(username:realm:passwd);<br>
 * HA2 = MD5(A2)= MD5(method:digestURI);<br>
 * Reponse = MD5 (HA1:nonce:HA2);<br>
 * 
 * @author FJ. LEVEE (ofli8276)
 * 
 * @version G0R0C0
 * @since G0R0C0
 */
public final class DigestTools
{
  /**
   * Md5 
   */
  private static final MessageDigest md5;

  static
  {
    MessageDigest md;
    try
    {
      md = MessageDigest.getInstance( "MD5");
    }
    catch (NoSuchAlgorithmException e)
    {
      md = null;
      e.printStackTrace();
    }
    md5 = md;
  }
  /**
   * Get the HA1 (HA1= MD5(A1)= MD5(username:realm:passwd);
   * 
   * @param username
   * @param realm
   * @param passwd
   * @return
   */
  public static String getHA1 ( String username, String realm, String passwd)
  {
    String input = (new StringBuilder()).append( username).append( ":").append( realm).append( ":").append( passwd)
        .toString();
    return getHash( input);
  }

  /**
   * Get the Digest<br>
   * HA2 = MD5(A2)= MD5(method:digestURI);<br>
   * Reponse = MD5 (HA1:nonce:HA2);<br>
   * 
   * @param ha1
   * @param nonce
   * @param nonceCount
   * @param clientNonce
   * @param qop
   * @param method
   * @param digestUri
   * @param entityBody
   * @return
   */
  /*
   * public static String getDigest(String ha1, String nonce, String nonceCount, String clientNonce, String qop, String
   * method, String digestUri, String entityBody) { StringBuilder input = new StringBuilder();
   * if("auth-int".equals(qop)) { input.append(method).append(":"); input.append(digestUri).append(":");
   * input.append(getHash(entityBody)); } else { input.append(method).append(":"); input.append(digestUri); } String ha2
   * = getHash(input.toString()); return getDigest(ha1, nonce, nonceCount, clientNonce, qop, ha2); }
   */

  /***
   * Get the Digest<br>
   * HA2 = MD5(A2)= MD5(method:digestURI);<br>
   * Reponse = MD5 (HA1:nonce:HA2);<br>
   * 
   * @param ha1
   * @param nonce
   * @param nonceCount
   * @param clientNonce
   * @param qop
   * @param method
   * @param digestUri
   * @return
   */
  public static String getDigest ( String ha1, String nonce, String nonceCount, String clientNonce, String qop,
      String method, String digestUri)
  {
    StringBuilder input = new StringBuilder();
    input.append( method).append( ":");
    input.append( digestUri);
    String ha2 = getHash( input.toString());
    return getDigest( ha1, nonce, nonceCount, clientNonce, qop, ha2);
  }

  /**
   * getDigest
   * 
   * @param ha1
   * @param nonce
   * @param nonceCount
   * @param clientNonce
   * @param qop
   * @param ha2
   * @return
   */
  public static String getDigest ( String ha1, String nonce, String nonceCount, String clientNonce, String qop,
      String ha2)
  {
    StringBuilder input = new StringBuilder();
    if ("auth".equals( qop) || "auth-int".equals( qop))
    {
      input.append( ha1).append( ":");
      input.append( nonce).append( ":");
      input.append( nonceCount).append( ":");
      input.append( clientNonce).append( ":");
      input.append( qop).append( ":");
      input.append( ha2);
    }
    else
    {
      input.append( ha1).append( ":");
      input.append( nonce).append( ":");
      input.append( ha2);
    }
    return getHash( input.toString());
  }

  /**
   * getHash
   * 
   * @param input
   * @return
   */
  public static String getHash ( String input)
  {
    MessageDigest md5i;
    try
    {
      md5i = (MessageDigest) md5.clone();
    }
    catch (CloneNotSupportedException e)
    {
      throw new AssertionError( e);
    }
    byte digest[] = md5i.digest( getBytes( input));
    char ca[] = new char[2 * digest.length];
    int j = 0;
    byte arr$[] = digest;
    int len$ = arr$.length;
    for (int i$ = 0; i$ < len$; i$++)
    {
      byte b = arr$[i$];
      ca[j++] = toHexChar( b >> 4 & 0xf);
      ca[j++] = toHexChar( b & 0xf);
    }

    return new String( ca);
  }

  /*
   * public static HashMap parseDigestChallenge(String challenge) { if(challenge.startsWith("Digest ")) challenge =
   * challenge.substring("Digest ".length()); HashMap map = new HashMap(); for(StringTokenizer st = new
   * StringTokenizer(challenge, ","); st.hasMoreTokens(); parseKeyValue(map, st.nextToken())); return map; }
   */
  /*
   * private static void parseKeyValue(HashMap map, String token) { int end; for(end = token.length() - 1;
   * Character.isWhitespace(token.charAt(end)); end--); int index = token.indexOf("="); if(index != -1) { String key =
   * token.substring(0, index).trim(); int valStart = token.charAt(index + 1) != '"' ? index + 1 : index + 2; int valEnd
   * = token.charAt(end) != '"' ? end : end - 1; String value = token.substring(valStart, valEnd + 1).trim();
   * map.put(key, value); } }
   */

  /*
   * public static String createDigestResponseWithHA1(String username, String ha1, Map challengeMap, String sipMethod,
   * String sipUri, String qop, String nonceCnt, String cnonce, String entityBody) { String realm =
   * (String)challengeMap.get("realm"); String nonce = (String)challengeMap.get("nonce"); String algorithm =
   * (String)challengeMap.get("algorithm"); String clientResponse = getDigest(ha1, nonce, nonceCnt, cnonce, qop,
   * sipMethod, sipUri, entityBody); StringBuilder digestResponse = (new
   * StringBuilder("Digest username=\"")).append(username).append("\",realm=\"").append(realm).append("\","); if(qop !=
   * null)
   * digestResponse.append("cnonce=\"").append(cnonce).append("\",nc=").append(nonceCnt).append(",qop=\"").append(qop
   * ).append("\",");
   * digestResponse.append("uri=\"").append(sipUri).append("\",nonce=\"").append(nonce).append("\",response=\""
   * ).append(clientResponse).append("\",algorithm=\"").append(algorithm).append("\""); return
   * digestResponse.toString(); }
   */

  /*
   * public static String createDigestResponseWithPassword(String username, String password, Map challengeMap, String
   * sipMethod, String sipUri, String qop, String nonceCnt, String cnonce, String entityBody) { String realm =
   * (String)challengeMap.get("realm"); String ha1 = getHA1(username, realm, password); return
   * createDigestResponseWithHA1(username, ha1, challengeMap, sipMethod, sipUri, qop, nonceCnt, cnonce, entityBody); }
   */

  private static byte[] getBytes ( String input)
  {
    int len = input.length();
    byte result[] = new byte[len];
    input.getBytes( 0, len, result, 0);
    return result;
  }

  public static String byteToHex ( byte b)
  {
    return new String( new char[] { toHexChar( b >> 4 & 0xf), toHexChar( b & 0xf) });
  }

  private static char toHexChar ( int n)
  {
    return (char) (n >= 10 ? 87 + n : 48 + n);
  }
  /**
   * Transform a String in Hex
   * @param str Specifies the String to be transformed
   * @return the transformed String
   */
  public static String stringToHex ( String str)
  {
    StringBuilder sb = new StringBuilder();
    byte b[] = getBytes( str);
    byte arr$[] = b;
    int len$ = arr$.length;
    for (int i$ = 0; i$ < len$; i$++)
    {
      byte aB = arr$[i$];
      sb.append( byteToHex( aB));
    }

    return sb.toString();
  }


  /**
   * Unquote a String
   * 
   * @param str Specifies the String to unquote
   * @return the unquoted String
   */
  private static String unquote ( String str)
  {
    if (!isQuoted( str))
      return str;
    else
      return str.substring( 1, str.length() - 1);
  }

  /**
   * Check if the given String is quoted
   * 
   * @param str Specifies the String to be checked
   * @return true if the given String is quoted
   */
  public static boolean isQuoted ( String str)
  {
    return str != null && str.length() > 2 && str.startsWith( "\"") && str.endsWith( "\"");
  }

}
