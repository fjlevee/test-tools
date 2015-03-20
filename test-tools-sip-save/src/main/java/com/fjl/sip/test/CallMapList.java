package com.fjl.sip.test;

import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * This Class is a List of Mappings between a Sip Client Test Id and the real Sip Call Id of the Sip Layer
 * @author FJ. LEVEE
 *
 */
public class CallMapList {
	
	/**
	 * List of CallMap
	 */
	private Vector<CallMap> callMaps;
  
  /**
   * Log4J Logger
   */
  private Logger logger = Logger.getLogger( com.fjl.sip.test.CallMapList.class);
	
	/**
	 * Constructor
	 *
	 */
	public CallMapList(){
		callMaps = new Vector<CallMap>();
	}
	/**
	 * Put a CallMap between a Sip Client Test Id and a Real sip Call ID
   * @param sipClientTesterId Specifies the Sip Client Test Id
	 * @param sipCallID Specifies the Real sip Call ID
	 */
	public void put(String sipClientTesterId,String sipCallID){
    
    if(logger.isTraceEnabled()) logger.debug( "CallMapList.put Map Call between: "+sipClientTesterId+" => "+sipCallID);
		CallMap callMap = new CallMap(sipClientTesterId,sipCallID);
		callMaps.add(callMap);
	}
	/**
	 * Get a Real Sip Call ID with a given Sip Client Test Id
	 * @param sipClientTesterId Specifies the Sip Client Test Id
	 * @return the Real Sip Call ID if found
	 */
	public String get(String scenarioSipCallId){
		if(scenarioSipCallId!=null){
			for(int i=0;i<callMaps.size();i++){
				if(callMaps.get(i).isSipClientTesterIdEquals(scenarioSipCallId)){
					return callMaps.get(i).getSipCallID();
				}
			}
		}
		return null;
	}
	
	/**
	 * Get a Sip Client Test Id with a given Real Sip Call Id
	 * @param sipCallID Specifies the Real sip Call ID
	 * @return the Sip Client Test Id if found
	 */
	public String getWithSipCallID(String sipCallID){
		if(sipCallID!=null){
			for(int i=0;i<callMaps.size();i++){
				if(callMaps.get(i).isSipCallIDEquals(sipCallID)){
					return callMaps.get(i).getSipClientTesterId();
				}
			}
		}
		return null;
	}
	
	
	/**
	 * Remove a Call Map with a given Sip Client Test Id
	 * @param sipCallID Specifies the Sip Client Test Id
	 */
	public void remove(String sipClientTesterId){
		
		if(sipClientTesterId!=null){
			int index =-1;
			for(int i=0;i<callMaps.size();i++){
				if(callMaps.get(i).isSipClientTesterIdEquals(sipClientTesterId)){
          if(logger.isTraceEnabled()) logger.debug( "CallMapList.remove Map Call between: "+sipClientTesterId+" => "+callMaps.get(i).getSipCallID());
					index =i;
					break;
				}
			}
			if(index!=-1){
				callMaps.remove(index);
			}
		}
	}
	/**
	 * Remove a Call Map with a given Real Sip Call Id
	 * @param sipCallID Specifies the Real Sip Call Id
	 */
	public void removeWithSipCallID(String sipCallID){
		
		if(sipCallID!=null){
			int index =-1;
			for(int i=0;i<callMaps.size();i++){
				if(callMaps.get(i).isSipCallIDEquals(sipCallID)){
          if(logger.isTraceEnabled()) logger.debug( "CallMapList.remove Map Call between: "+callMaps.get(i).getSipClientTesterId()+" => "+sipCallID);
					index =i;
					break;
				}
			}
			if(index!=-1){
				callMaps.remove(index);
			}
		}
	}
	
	/**
	 * Get the number of CallMap contained in this CallMapList
	 * @return the number of CallMap contained in this CallMapList
	 */
	public int size(){
		return callMaps.size();
	}
	/**
	 * Clear the of CallMap contained in this CallMapList
	 */
	public void clear(){
		callMaps.clear();
	}
}	
