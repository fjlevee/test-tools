package com.fjl.test.tools;

import java.io.Serializable;
import java.util.HashMap;

public abstract class TestElement implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 6817507317792551729L;


    /**
     * Id
     */
    protected String id;


    /**
     * Actor Parameters
     */
    protected HashMap<String, TestParam> params;
    
    /**
     * Test Element Id
     * @param id
     */
    public TestElement(String id){
        this.id = id;
        this.params = new HashMap<String, TestParam>();
    }
    
    
    /**
     * Get the Id
     * 
     * @return the Id
     */
    public String getId() {
        return id;
    }
    
    
    
    /**
     * Add Parameter
     * @param name
     * @param value
     */
    public void addParam(TestParam testParam){
        this.params.put(testParam.getName(), testParam);
    }
    
    /**
     * Remove Parameter
     * @param name
     */
    public void removeParam(String name){
        this.params.remove(name);
     
    }
    /**
     * Get a Parameter with its name
     * @param name
     * return the parameters
     */
    public TestParam getParam(String name){
        return this.params.get(name);
     
    }

    
}
