package com.fjl.sip.test;

/**
 * This Class is a Mapping between a Sip Client Test Id and the real Sip Call Id of the Sip Layer
 * @author FJ. LEVEE
 *
 */
public class CallMap {

	/**
	 * Sip Client Test Id
	 */
	public String sipClientTesterId;
	/**
	 * Real sip Call ID
	 */
	public String sipCallID;
	/**
	 * Constructor
	 * @param sipClientTesterId Specifies the Sip Client Test Id
	 * @param sipCallID Specifies the Real sip Call ID
	 */
	public CallMap(String sipClientTesterId,String sipCallID){
		this.sipClientTesterId = sipClientTesterId;
		this.sipCallID = sipCallID;
	}
	/**
	 * Get the Sip Call ID
	 * @return the Sip Call ID
	 */
	public String getSipCallID() {
		return sipCallID;
	}
	/**
	 * Set the Sip Call ID
	 * @param sipCallID Specifies the Sip Call ID
	 */
	public void setSipCallID(String sipCallID) {
		this.sipCallID = sipCallID;
	}
	/**
	 * Get the Sip Client Test Id
	 * @return the Sip Client Test Id
	 */
	public String getSipClientTesterId() {
		return sipClientTesterId;
	}
	/**
	 * Set the Sip Client Test Id
	 * @param sipClientTesterId Specifies the Sip Client Test Id.
	 */
	public void setSipClientTesterId(String sipClientTesterId) {
		this.sipClientTesterId = sipClientTesterId;
	}
	/**
	 * Checks if the Sip Call Id equals the Sip Call Id given
	 * @param sipCallIDTested Specifies the Sip Call Id tested
	 * @return true if the Sip Call Id tested equals the Sip Call Id
	 */
	public boolean isSipCallIDEquals(String sipCallIDTested){
		if (sipCallIDTested!=null){
			if(sipCallIDTested.equals(sipCallID)){
				return true;
			}
		}
	  //ssdmùm
		return false;
	}
	/**
	 * Checks if the Sthe Sip Client Tester Id equals the the Sip Client Tester Id given
	 * @param sipClientTesterIdTested Specifies the Sip Client Tester Id tested
	 * @return true if the Sip Call Id tested equals the Sip Call Id
	 */
	public boolean isSipClientTesterIdEquals(String sipClientTesterIdTested){
		if (sipClientTesterIdTested!=null){
			if(sipClientTesterIdTested.equals(sipClientTesterId)){
				return true;
			}
		}
		return false;
	}
	
	
}
