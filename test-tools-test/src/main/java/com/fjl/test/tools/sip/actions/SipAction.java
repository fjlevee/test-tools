package com.fjl.test.tools.sip.actions;

import com.fjl.test.tools.Action;
import com.fjl.test.tools.ActionReport;
import com.fjl.test.tools.ActionReport.ActionResult;
import com.fjl.test.tools.sip.SipActor;

public abstract class SipAction extends Action {

    

    /**
     * This parameter checks if the previous received reponse was an Error
     * Response. This is helpful for Sending ACK messages or not (or receiving
     * ACK or not) => In case of Error response received before , the ACK is
     * automatically sent by the Sip Layer, so the sendAck must not be done.
     */
    protected boolean previousMessageWasAnError = false;

    /**
     * 
     * Constructor of SipClientAction.
     * 
     * @param sipActor
     * @param callId
     * @param actionId
     * @param timeTowait
     */
    public SipAction(String actorId, String id) {
        super(actorId, id);
        
    }

    /**
     * Execute the Action
     */
    public abstract void executeAction();

 
    /**
     * This parameter checks if the previous received reponse was an Error
     * Response. This is helpful for Sending ACK messages or not (or receiving
     * or not)=> In case of Error response received before , the ACK is
     * automatically sent by the Sip Layer, so the sendAck must not be done.
     * 
     * @param previousMessageWasAnError
     */
    public void setPreviousMessageWasAnError(boolean previousMessageWasAnError) {
        this.previousMessageWasAnError = previousMessageWasAnError;
    }

    

}
