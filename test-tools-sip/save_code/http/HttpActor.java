package com.fjl.test.http;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.fjl.test.ActionListener;
import com.fjl.test.Actor;
import com.fjl.test.Test;
import com.fjl.test.TestException;
import com.fjl.test.configuration.SipTestProfileConfiguration;
import com.fjl.test.parser.elements.HttpRequestElement;

public class HttpActor extends Actor implements ActionListener {

    /**
     * Http Client used to send the requests
     */
    private HttpClient httpClient;
    /**
     * Http Server Sip Configuration
     */
    private SipTestProfileConfiguration sipServerConfiguration;

    /**
     * Map between the Action Id executed and the Action id (Http request
     * action) to be executed by this by this actor
     */
    private ConcurrentHashMap<String, HttpRequestAction> actionsMap;

    /**
     * Determines the Http Client actor's the Http/Sip Convergence url
     */
    private String httpConvergenceSipApplicationUrl = null;

    /**
     * Determines the Http Client actor's the Http/Sip Convergence uri Path
     * 
     */
    private String httpConvergenceSipApplicationUriPath = null;
    /**
     * Determines the Http Client actor's the Http/Sip Convergence uri Host
     * 
     */
    private String httpConvergenceSipApplicationUriHost = null;

    /**
     * 
     * Constructor of HttpClientActor.
     * 
     * @param test
     * @param sipTestCallFlowActor
     * @param serverConfiguration
     */
    public HttpActor ( Test test, SipTestProfileConfiguration serverConfiguration)
  {
    super( test, sipTestCallFlowActor);
    
	this.httpClient = HttpClients.createMinimal(connManager )
    this.sipServerConfiguration = serverConfiguration;
    httpClient.getHostConfiguration().setHost(serverConfiguration.getServerSipIpAddress(), serverConfiguration.getServerHttpIpPort(), "http");
    httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
    this.actionsMap = new ConcurrentHashMap<String, HttpRequestAction>();
    if (logger.isDebugEnabled()) logger.debug( "HttpClientActor.constructor, Actor id: \n{}",id);
  }

    @Override
    public void initActionsList() throws TestException {
        if (logger.isDebugEnabled())
            logger.debug("HttpClientActor.initClientActionsList: Actor Id:"
                    + clientActorId);

        int actionsNumber = sipTestCallFlowActor.getSipTestCallFlowActions()
                .size();

        for (int i = 0; i < actionsNumber; i++) {
            HttpRequestElement httpRequestElement = (HttpRequestElement) sipTestCallFlowActor
                    .getSipTestCallFlowActions().getRealFlowId(i);
            HttpRequestAction action = new HttpRequestAction(this,
                    httpRequestElement, sipServerConfiguration);

            clientActions.add(action);

        }

        // set the last action
        clientActions.getRealFlowId(actionsNumber - 1).setLastAction(true);
    }

    

    /**
     * Get the Http Client
     * 
     * @return the Http Client
     */
    public HttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Add an Action mapping between the Action id executed (notified with the
     * ClientActionListener Interface and the Http request Action to be executed
     * by the Http Actor
     * 
     * @param actionIdToBeListened
     *            Specifies the action to be listened.
     * @param actionToBeExecuted
     *            Specifies the action to be executed.
     */

    public void addActionMap(String actionIdToBeListened,
            HttpRequestAction actionToBeExecuted) {
        getTest().addClientActionListener(actionIdToBeListened, this);
        actionsMap.put(actionIdToBeListened, actionToBeExecuted);
    }

    /**
     * actionExecuted
     * 
     * @param actionId
     *            Specifies the Id of the Executed Action
     */
    public void actionExecuted(String actionId) {
        if (logger.isTraceEnabled())
            logger.debug("HttpClientActor.actionExecuted " + actionId
                    + ", search if there is action to be executed");
        HttpRequestAction action = actionsMap.get(actionId);
        if (action != null) {
            if (logger.isTraceEnabled())
                logger.debug("HttpClientActor.actionExecuted: An action has been found with Id: "
                        + action.getId());
            executeAction(action);

        }

    }

    /**
     * Execute an action
     * 
     * @param action
     *            Specifies the Action to execute
     */
    private void executeAction(HttpRequestAction action) {
        if (logger.isTraceEnabled())
            logger.debug("HttpClientActor.executeAction id:  " + action.getId());
        Thread executeActionThread = new Thread(action);
        executeActionThread.start();

    }

    /**
     * Get the Http Client actor's the Http/Sip Convergence url
     * 
     * @return the Http Client actor's the Http/Sip Convergence url
     */
    /*
     * public String getHttpConvergenceSipApplicationUrl () { return
     * httpConvergenceSipApplicationUrl; }
     */
    /**
     * Set the Http Client actor's the Http/Sip Convergence url
     * 
     * @param httpConvergenceSipApplicationUrl
     *            Specifies the Http Client actor's the Http/Sip Convergence url
     */
    /*
     * public void setHttpConvergenceSipApplicationUrl ( String
     * httpConvergenceSipApplicationUrl) { this.httpConvergenceSipApplicationUrl
     * = httpConvergenceSipApplicationUrl; }
     */

    /**
     * Get the Http Client actor's the Http/Sip Convergence uri host
     * 
     * @return the Http Client actor's the Http/Sip Convergence uri host
     */
    public String getHttpConvergenceSipApplicationUriHost() {
        return httpConvergenceSipApplicationUriHost;
    }

    /**
     * Set the Http Client actor's the Http/Sip Convergence uri host
     * 
     * @param httpConvergenceSipApplicationUriHost
     *            Specifies the Http Client actor's the Http/Sip Convergence uri
     *            host
     */
    public void setHttpConvergenceSipApplicationUriHost(
            String httpConvergenceSipApplicationUriHost) {
        this.httpConvergenceSipApplicationUriHost = httpConvergenceSipApplicationUriHost;
    }

    /**
     * Get the Http Client actor's the Http/Sip Convergence uri path
     * 
     * @return the Http Client actor's the Http/Sip Convergence uri path
     */
    public String getHttpConvergenceSipApplicationUriPath() {
        return httpConvergenceSipApplicationUriPath;
    }

    /**
     * Set the Http Client actor's the Http/Sip Convergence uri path
     * 
     * @param httpConvergenceSipApplicationUriPath
     *            Specifies the Http Client actor's the Http/Sip Convergence uri
     *            path
     */
    public void setHttpConvergenceSipApplicationUriPath(
            String httpConvergenceSipApplicationUriPath) {
        this.httpConvergenceSipApplicationUriPath = httpConvergenceSipApplicationUriPath;
    }

}
