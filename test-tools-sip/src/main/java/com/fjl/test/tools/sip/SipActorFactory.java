package com.fjl.test.tools.sip;

import java.io.Serializable;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fjl.test.tools.Actor;
import com.fjl.test.tools.ActorFactory;
import com.fjl.test.tools.TestException;




public class SipActorFactory extends ActorFactory{
    
    
    
    public SipActorFactory() {
        super();
        
    }

    
    public Actor createActor(String actorId,
            HashMap<String, Serializable> actorParams) throws TestException {
        logger.debug("Creating actor {}",actorId);
        return null;
    }


    @Override
    public String getType() {      
        return "SIP";
    }

    
    
    
}
