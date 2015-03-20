package com.fjl.test.tools.sip.utils;

import java.security.MessageDigest;

import org.slf4j.Logger;

import com.fjl.test.tools.sip.SipConstants;



public class SipUtils
{
  
  private static java.util.Random rand = new java.util.Random();

  private static long counter = 0;
  
  private static SipUtils instance = new SipUtils(); 
    
  /**
   * to hex converter
   */
  private static final char[] toHex = { '0', '1', '2', '3', '4', '5', '6',
      '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

  
  private static MessageDigest digester;
  static {
    try {
      digester = MessageDigest.getInstance("MD5");
    } catch (Exception ex) {
      throw new RuntimeException("Could not intialize Digester ", ex);
    }
  }
  
  
  public static SipUtils getInstance() {
    return instance;    
  }
  
 
  /**
   * convert an array of bytes to an hexadecimal string
   * 
   * @return a string
   * @param b
   *            bytes array to convert to a hexadecimal string
   */

  public static String toHexString(byte b[]) {
    int pos = 0;
    char[] c = new char[b.length * 2];
    for (int i = 0; i < b.length; i++) {
      c[pos++] = toHex[(b[i] >> 4) & 0x0F];
      c[pos++] = toHex[b[i] & 0x0f];
    }
    return new String(c);
  }
  
  /**
   * Generate a cryptographically random identifier that can be used to
   * generate a branch identifier.
   * 
   * @return a cryptographically random gloablly unique string that can be
   *         used as a branch identifier.
   */
  public synchronized String generateBranchId() {

      long num = rand.nextLong() + SipUtils.counter++  + System.currentTimeMillis();

      byte bid[] = digester.digest(Long.toString(num).getBytes());
      // prepend with a magic cookie to indicate we are bis09 compatible.
      return SipConstants.BRANCH_MAGIC_COOKIE + SipUtils.toHexString(bid);
  }
  
  
  
}
