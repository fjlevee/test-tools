package com.fjl.test.tools.run;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fjl.test.tools.ActorFactory;

public class ClassPathScanner {

    protected Logger logger = LoggerFactory
            .getLogger(com.fjl.test.tools.run.ClassPathScanner.class);

    protected Reflections reflections;

    protected Set<String> actorFactories;

    public Set<String> getActorFactories() {
        return actorFactories;
    }

    public ClassPathScanner(String packageName) {

        reflections = new Reflections(packageName);
        
        actorFactories = reflections.getStore().getSubTypesOf(
                ActorFactory.class.getName());
        Iterator<String> actorFactoriesIter = actorFactories.iterator();
        while (actorFactoriesIter.hasNext()) {
            String c = actorFactoriesIter.next();
            logger.trace("logger TestActorFactory class: {}", c);
            System.err.println("TestActorFactory class: " + c);
        }

    }

}
