package com.fjl.test.tools;

import java.util.HashMap;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fjl.test.tools.report.TestReport;

public class Test {
    /**
     * Log4J logger
     */
    protected Logger logger = LoggerFactory
            .getLogger(com.fjl.test.tools.Test.class);

    /**
     * Test unique Id
     */
    protected String testId;

    /**
     * Test Actors List
     */
    protected HashMap<String, Actor> actors;
    /**
     * Tests Actions List
     */
    protected HashMap<String, Action> actions;
    /**
     * Test Params
     */
    public HashMap<String, TestParam> testParams;

    /**
     * Test Report
     */
    protected TestReport testReport;

    /**
   * 
   */
    protected String testfilename;

    /**
     * Constructor
     * 
     * @param testLauncher
     * @param testId
     * @throws TestException
     */
    public Test(String testfilename) {

        logger.debug("Test.constructor: Start...");

        this.testId = null;
        this.testfilename = testfilename;
        this.actors = new HashMap<String, Actor>();
        this.actions = new HashMap<String, Action>();
        this.testParams = new HashMap<String, TestParam>();
        this.testReport = new TestReport(testId);

        if (logger.isDebugEnabled())
            logger.debug("Test.constructor: End.");
    }

    /**
     * startTest
     * 
     * @param listener
     */
    public void execute() {
        logger.debug("SipClientTest.startTest Generate UA for all Actors");
        Iterator<String> actionsIds = actions.keySet().iterator();
        String actionId = null;
        Action action = null;
        try {
            while (actionsIds.hasNext()) {
                actionId = actionsIds.next();
                action = actions.get(actionId);

            }
        } catch (Exception e) {
            logger.error(
                    "Test.execute Exception while executing action with Id: {} \n",
                    actionId, e);
            // testReport.traceReportEvent(new A);
        }
    }

    /**
     * Add Parameter
     * 
     * @param name
     * @param value
     */
    public void addTestParam(TestParam testParam) {
        this.testParams.put(testParam.getName(), testParam);
    }

    /**
     * Remove Parameter
     * 
     * @param name
     */
    public void removeTestParam(String name) {
        this.testParams.remove(name);

    }

    /**
     * Get a Parameter with its name
     * 
     * @param name
     *            return the parameter
     */
    public TestParam getTestParam(String name) {
        return this.testParams.get(name);

    }
    
    /**
     * Add Parameter
     * 
     * @param name
     * @param value
     */
    public void addActorParam(String actorId,TestParam testParam) {
        Actor actor = actors.get(actorId);
        if (actor==null){
           
        }
        
    }

    /**
     * Find a Parameter with its name in (by order) test Params, actors params
     * and actions params
     * 
     * @param paramName
     *            return the parameter
     */
    public TestParam getParam(String paramName) {
        TestParam param = this.testParams.get(paramName);
        if (param != null) {
            logger.trace("Test.getParam: {}={}, found in test params", paramName,
                    param.getValue().toString());
            return this.testParams.get(paramName);
        }
        // Ok not a test param search if this is an actor param
        int pointIndex = paramName.indexOf(".");
        if (pointIndex != -1) {
            logger.trace(
                    "Test.getParam: {} : can not extract actor or action id in this param => return null",
                    paramName);
            return null;
        }
        String id = paramName.substring(0, pointIndex);
        String name = paramName.substring( pointIndex+1);
        Actor actor = actors.get(id);
        if(actor!=null){
            logger.trace(
                    "Test.getParam: Actor found with id: {}, looking for {} in his parameters",id,name);
            param = actor.getParam(name);
            if(param!=null){
                if(logger.isTraceEnabled())logger.trace(
                        "Test.getParam: {}={}, found in Actor {} params",paramName,param.getValue().toString(),id);
                return param;
            }
        }
        logger.trace("Test.getParam: Nothing found in actors => Search in Actions");
        Action action = actions.get(id);
        if(action!=null){
            logger.trace(
                    "Test.getParam: Action found with id: {}, looking for {} in his parameters",id,name);
            param = action.getParam(name);
            if(param!=null){
                if(logger.isTraceEnabled())logger.trace(
                        "Test.getParam: {}={}, found in Action {} params",param.getName(),param.getValue().toString(),id);
                return param;
            }
        }
        
        logger.trace(
                "Test.getParam: {} : Nothing found => return null",param);
        return param;
    }

    /**
     * Get the Actor with its Id
     * 
     * @param actorId
     *            Specifies the Actor ID;
     * @return the Actor
     */
    public Actor getActor(String actorId) {

        return actors.get(actorId);
    }

    /**
     * Get a Action with its id
     * 
     * @param actionId
     *            Specifies the Action Id to find
     * @return the Action if found.
     */
    public Action getAction(String actionId) {
        return actions.get(actionId);
    }

    /**
     * get the Test Id If null or empty, it will return the filename
     * 
     * @return the test Id
     */
    public String getId() {
        if ((testId == null) || ("".equals(testId))) {
            return testfilename;
        }
        return testId;
    }

    /**
     * get the Test Filename
     * 
     * @return the test Filename
     */
    public String getTestFilename() {
        if ((testId == null) || ("".equals(testId))) {
            return testfilename;
        }
        return testId;
    }

    /**
     * Set the Test Id
     * 
     * @return the SipClientTest Id
     */
    public void setId(String testId) {
        this.testId = testId;
    }

    /**
     * Set the SipClientTest Id
     * 
     * @param testId
     *            Specifies the SipClientTest Id
     */
    public void setSipClientTestId(String testId) {
        this.testId = testId;
    }

    /**
     * Replace param name idendtifed with its key by its value Replace all
     * ${param_name} strings with the value
     */
    public void replaceParamsNamesWithValues() {
        Iterator<String> iter = testParams.keySet().iterator();
        while (iter.hasNext()) {
            String paramName = iter.next();
            TestParam param = testParams.get(paramName);
            Iterator<String> iter2 = testParams.keySet().iterator();
            String searchString = "${" + paramName + "}";
            while (iter2.hasNext()) {
                String paramName2 = iter2.next();
                TestParam param2 = testParams.get(paramName2);
                param2.replaceParamNameWithValue(searchString, param.getValue());

            }
        }
    }

    /**
     * Dump All Param List
     * 
     * @param logger
     */
    public void dumpTestsParams(Logger logger) {
        StringBuilder buffer = new StringBuilder();

        buffer.append("\n============== PARAMS LIST ==============\n\n");
        buffer.append("Test Id: " + getId() + "\n");
        buffer.append("Test file: " + getTestFilename() + "\n");
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
