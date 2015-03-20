package com.fjl.test.tools;

import com.fjl.test.tools.ActionReport.ActionResult;

public class WaitAction extends Action {

    /**
     * 
     */
    private static final long serialVersionUID = 5935003311465112799L;
    
    /**
     * Time to wait in ms
     */
    protected long timeToWait;

    /**
     * 
     * Constructor of WaitAction.
     * 
     * @param httpClientActor
     * @param httpRequestElement
     * @param sipServerConfiguration
     * @throws TestException
     */
    public WaitAction(String actorId, String id) {

        super(actorId, id);

    }

    /**
     * 
     */
    @Override
    public ActionReport execute(Test test) {
        try {
            Thread.sleep(timeToWait);
            return new ActionReport(actorId, id, ActionResult.ACTION_SUCCESS,
                    "Wait " + timeToWait + " ms success", "");
        } catch (InterruptedException e) {
            return new ActionReport(actorId, id, ActionResult.ACTION_FAILED,
                    "Wait " + timeToWait + " ms failed", e.getMessage());
        }
    }

}
