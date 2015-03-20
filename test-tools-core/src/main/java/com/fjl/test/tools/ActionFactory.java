package com.fjl.test.tools;

import java.io.Serializable;
import java.util.HashMap;

public abstract class ActionFactory {

    /**
     * Get the Type of this Action Factory
     * @return the stype as string.
     */
    public abstract String getType();
    /**
     * 
     * @param actorId
     * @param actorParams
     * @return
     */
    public abstract Action createAction(String actionId,HashMap<String,Serializable> actionParams) throws TestException;
}
