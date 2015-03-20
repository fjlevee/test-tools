package com.fjl.test.tools;

import java.io.Serializable;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Actor extends TestElement{

    /**
     * Flag used to determine if the Actor has to be simulated or not.
     */
    protected boolean simulated;


    /**
     * 
     * Constructor of Actor.
     * 
     * @param test
     * @param id
     */
    public Actor(String id) {
        super(id);
    }

    

    public boolean isSimulated() {
        return simulated;
    }

    public void setSimulated(boolean simulated) {
        this.simulated = simulated;
    }
    
}
