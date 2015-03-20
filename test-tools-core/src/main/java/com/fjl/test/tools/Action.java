package com.fjl.test.tools;

import java.util.Vector;

public abstract class Action extends TestElement{
    /**
     * 
     */
    private static final long serialVersionUID = -1733950048411968361L;


    /**
     * Actor
     */
    protected String actorId;

    /**
     * 
     */
    protected Vector<ActionListener> actionListeners;
    
    
  

    /**
     * 
     * Constructor of Action.
     *
     * @param id Action Id
     * @param actorId Actor id
     */
    public Action(String id,String actorId ) {
        super(id);
        this.actorId = actorId;     
    }

    /**
     * Execute the Action
     * 
     * @return ActionReport
     */
    public abstract ActionReport execute(Test test);

   
    /**
     * Remove an Action Listener
     * @param actionListener Specifies the Action Listener to be removed
     */
    public void removeActionListener(ActionListener actionListener) {
         actionListeners.remove(actionListener);
    }

    /**
     * Add an Action Listener
     * @param actionListener Specifies the Action Listener to be added
     */
    public void addActionListener(ActionListener actionListener) {
         actionListeners.add(actionListener);
    }
    /**
     * Notify Action Listeners
     * @param actionReport Specifies the Action Report to be notified.
     */
    protected void notifyActionListeners(ActionReport actionReport){
        for (ActionListener actionListener:actionListeners){
            actionListener.actionExecuted(actionReport);
        }
    }
    
    
}
