package com.fjl.test.tools;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fjl.test.tools.run.ClassPathScanner;

public class GenericActorFactory extends ActorFactory {

    Logger logger = LoggerFactory
            .getLogger(com.fjl.test.tools.GenericActorFactory.class);

    protected HashMap<String, ActorFactory> actorFactories;

    public GenericActorFactory(ClassPathScanner classPathScanner) {
        // super();
        actorFactories = new HashMap<String, ActorFactory>();
        Set<String> actorFactoryClasses = classPathScanner.getActorFactories();

        Iterator<String> classNames = actorFactoryClasses.iterator();
        while (classNames.hasNext()) {
            String className = classNames.next();
            if (!className.equals(GenericActorFactory.class.getName())) {
                try {
                    Class actorFactoryClass = Class.forName(className);
                    Method getTypeMethod = actorFactoryClass
                            .getMethod("getType");
                    ActorFactory actorFactory = (ActorFactory) actorFactoryClass
                            .newInstance();
                    String type = (String) getTypeMethod.invoke(actorFactory);
                    actorFactories.put(type, actorFactory);
                } catch (ClassNotFoundException e) {
                    logger.error(
                            "GenericActorFactory constructor - ClassNotFoundException: ",
                            e);
                } catch (NoSuchMethodException e) {
                    logger.error(
                            "GenericActorFactory constructor - NoSuchMethodException: ",
                            e);
                } catch (InstantiationException e) {
                    logger.error(
                            "GenericActorFactory constructor - InstantiationException: ",
                            e);
                } catch (IllegalAccessException e) {
                    logger.error(
                            "GenericActorFactory constructor - IllegalAccessException: ",
                            e);
                } catch (IllegalArgumentException e) {
                    logger.error(
                            "GenericActorFactory constructor - IllegalArgumentException: ",
                            e);
                } catch (InvocationTargetException e) {
                    logger.error(
                            "GenericActorFactory constructor - InvocationTargetException: ",
                            e);
                }
            }
        }

        Iterator<String> actorFactoryKeys = actorFactories.keySet().iterator();
        while (actorFactoryKeys.hasNext()) {
            String key = actorFactoryKeys.next();
            logger.debug("{}={}", key, actorFactories.get(key).getClass()
                    .getName());
        }

    }

    /**
     * 
     * @param actorId
     * @param actorParams
     * @return
     */
    public Actor createActor(Test test,String actorId,
            HashMap<String, Serializable> actorParams) throws TestException {
        String actorType = (String) actorParams.get("type");
        logger.debug("Creating actor {} of type: {}", actorId, actorType);
        if (actorType == null) {
            throw new TestException(
                    "Error creating actor, no type specified !!!");
        } else if (actorFactories.get(actorType) == null) {
            throw new TestException(
                    "Error creating actor, can not create actor of type {}: no factory found to create it !!!");
        }
        
        return actorFactories.get(actorType).createActor(test,actorId, actorParams);
    }

    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return "default";
    }
}
