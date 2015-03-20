package com.fjl.test.tools;

import java.io.Serializable;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ActorFactory {
    /**
     * Slf4j logger
     */
    protected Logger logger;

    /**
     * 
     * @param type
     */
    public ActorFactory() {
        logger = LoggerFactory.getLogger(com.fjl.test.tools.ActorFactory.class);

    };

    /**
     * Get the Type of this Actor Factory
     * 
     * @return the stype as string.
     */
    public abstract String getType();

    /**
     * 
     * @param actorId
     * @param actorParams
     * @return
     */
    public abstract Actor createActor(Test test,String actorId,
            HashMap<String, Serializable> actorParams) throws TestException;
}
