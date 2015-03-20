package com.fjl.test.tools;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import org.slf4j.Logger;

/**
 * 
 * @author fjl
 *
 */
public class TestConfiguration implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 640365954352178330L;
    /**
     * Test Params
     */
    public HashMap<String, TestParam> testParams;
    /**
     * Test Actors
     */
    public HashMap<String, Actor> actors;
    
    /**
     * Test Actions
     */
    public HashMap<String, Actor> actions;
    
    /**
     * 
     */
    public TestConfiguration() {
        testParams = new HashMap<String, TestParam>();
        actors = new HashMap<String, Actor>();
        actions = new HashMap<String, Actor>();
    }

    public void addParam(TestParam param) {
        testParams.put(param.getName(), param);
    }

    public TestParam getParam(String paramName) {
        return testParams.get(paramName);
    }

    public void removeParam(String paramName) {
        testParams.remove(paramName);
    }

    

    /**
     * Dump All Param List
     * 
     * @param logger
     */
    public void dumpTestsParams(Logger logger) {
        StringBuilder buffer = new StringBuilder();

        buffer.append("\n============== PARAMS LIST ==============\n\n");
        Iterator<String> iter = testParams.keySet().iterator();
        while (iter.hasNext()) {
            String paramName = iter.next();
            TestParam param = testParams.get(paramName);
            buffer.append(param.getAsString() + "\n");

        }

        buffer.append("\n=========================================\n");
        logger.info("{}", buffer);
    }
    
    
    
}
