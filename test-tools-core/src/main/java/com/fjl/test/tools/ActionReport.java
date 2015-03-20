package com.fjl.test.tools;

public class ActionReport {

    public enum ActionResult {
        ACTION_SUCCESS, ACTION_FAILED
    }

    /**
     * Report Event Type
     */
    protected ActionResult actionResult;

    /**
     * Action ID
     */
    protected String actionId;

    /**
     * Result Description
     */
    private String resultDescription;

    /**
     * Addtional Information
     */
    private String additionalInformation;

    /**
     * ClientActor Id in which the event occured
     */
    private String actorId;


    /**
     * 
     * Constructor of SipClientTestReportEvent. ...
     *
     * @param eventType
     * @param actionId
     * @param actorId
     * @param lastAction
     * @param eventDescription
     * @param additionalInformation
     */

    public ActionReport(String actorId,String actionId,ActionResult actionResult, String resultDescription,
            String additionalInformation) {
        this.actorId = actorId;
        this.actionId = actionId;
        this.actionResult = actionResult;
        this.resultDescription = resultDescription;
        this.additionalInformation = additionalInformation;
       
    }

   

    /**
     * get the Event Type
     * 
     * @return
     */
    public ActionResult getResult() {
        return actionResult;
        
    }

    /**
     * Set the Event Type
     * 
     * @param eventType
     *            Specifies the Event Type
     */
    public void setResult(ActionResult eventType) {
        this.actionResult = eventType;
    }

    /**
     * Get the Action Id
     * 
     * @return the Action Id
     */
    public String getActionId() {
        return actionId;
    }

    /**
     * Set the Action Id
     * 
     * @param actionId
     *            Specifies the Action ID
     */
    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    /**
     * Get the Additional information
     * 
     * @return the Additional Information
     */
    public String getAdditionalInformation() {
        return additionalInformation;
    }

    /**
     * Set the additional Information
     * 
     * @param additionalInformation
     *            Specifies the additional Information
     */
    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    /**
     * Get the Event Description
     * 
     * @return the Event Description
     */
    public String getEventDescription() {
        return resultDescription;
    }

    /**
     * Set the Event Description
     * 
     * @param eventDescription
     *            Specifies the new Event Description
     */
    public void setEventDescription(String eventDescription) {
        this.resultDescription = eventDescription;
    }

    /**
     * Get the Actor Id
     * 
     * @return the Actor Id
     */
    public String getActorId() {
        return actorId;
    }

    /**
     * Set the Actor Id
     * 
     * @param actorId
     *            Specifies the Actor Id
     */
    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    /**
     * getReportEventAsString
     * 
     * @return the Report Event As String
     */
    public String getActionReportAsString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("-> Report on Action Id: " + actionId);
        buffer.append(" is " + actionResult);
        buffer.append(", Actor Id: " + actorId);
        buffer.append(", Description: " + resultDescription);
        if (additionalInformation != null) {
            buffer.append(", Additional Information: " + additionalInformation);

        }
        return buffer.toString();
    }


}
