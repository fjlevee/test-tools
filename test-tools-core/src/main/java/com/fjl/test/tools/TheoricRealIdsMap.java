package com.fjl.test.tools;

import java.util.HashMap;

/**
 * This Class is a Mapping between a Sip Client Test Id and the real Sip Call Id
 * of the Sip Layer
 * 
 * @author FJ. LEVEE
 *
 */
public class TheoricRealIdsMap {
    
    protected HashMap<String, String> theoricIds;
    
    protected HashMap<String, String> realIds;
    
    public TheoricRealIdsMap(){
        theoricIds = new HashMap<String, String>();
        realIds = new HashMap<String, String>();
    }
    /**
     * Add map between theoricId and realId
     * @param theoricId
     * @param realId
     */
    public void addMap(String theoricId,String realId){
        theoricIds.put(realId, theoricId);
        realIds.put(theoricId, realId);
     }
    
    
    /**
     * Remove map between theoricId and realId giving the theoric id
     * @param theoricId
     */
    public void removeMapWithTheoricId(String theoricId){
        
        String realId= realIds.remove(theoricId);
        theoricIds.remove(realId);
     }
    /**
     * Remove map between theoricId and realId giving the real id
     * @param realId
     */
    public void removeMapWithRealId(String realId){
        
        String theoricId= theoricIds.remove(realId);
        realIds.remove(theoricId);
     }
   
   

    /**
     * Get the real Id giving the theoric id
     * 
     * @return real Id giving the theoric id
     */
    public String getRealId(String theoricId) {
        return realIds.get(theoricId);
    }
    /**
     * Get the theoric id giving the real id
     * 
     * @return theoric id giving the real id
     */
    public String getTheoric(String realId) {
        return theoricIds.get(realId);
    }
    /**
     * Get the Map Size
     * @return the size
     */
    public int size(){
        
        return theoricIds.size();
     }
    /**
     * clear
     */
    public void clear(){
        theoricIds.clear();
        realIds.clear();
        
    }
}
