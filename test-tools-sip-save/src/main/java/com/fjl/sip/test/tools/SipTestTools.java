package com.fjl.sip.test.tools;

import fj_levee.test.parser.SipTestCallFlowAction.ActionDirection;

public final class SipTestTools
{
  
  /**
   * Sent direction
   */
  public static final String SENT_DIRECTION = "sent";
  /**
   * To Client direction
   */
  public static final String TO_CLIENT_DIRECTION = "toclient";
  /**
   * Received direction
   */
  public static final String RECEIVED_DIRECTION = "received";
  /**
   * To Server direction
   */
  public static final String TO_SERVER_DIRECTION = "toserver";
  
  /**
   * Extract a Sip Call Id from a Replaces Header.
   * @param replacesHeaderValue Specifies the Replaces Header value.<br>
   * Its format can be Replaces: [CALL_ID] or Replaces: [CALL_ID];to-tag=[TO_TAG];from-tag=[FROM_TAG]
   * 
   * @return the Call Id Found in Replaces Header else null
   */
  public static String extractCallIdFromReplacesHeader(String replacesHeaderValue){
    
    if(replacesHeaderValue==null){
      return null;
    }
    String[] splits =replacesHeaderValue.split(";");
    return splits[0];
  }
  
  /**
   * Get message direction from a String.
   * @param direction Specifies the Direction as String.<br>
   * 
   * @return the message direction (TO_CLIENT or TO_SERVER)
   */
  public static ActionDirection getMessageDirection(String messageDirectionString){
    ActionDirection messageDirection = ActionDirection.TO_CLIENT;
    if((RECEIVED_DIRECTION.equals(messageDirectionString))||(TO_SERVER_DIRECTION.equals(messageDirectionString))){
      messageDirection = ActionDirection.TO_SERVER;
    }
    return messageDirection;
  }
}
