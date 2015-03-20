package com.fjl.test.http;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import com.fjl.test.Action;
import com.fjl.test.TestException;
import com.fjl.test.ActionReport;
import com.fjl.test.ActionReport.ActionResult;
import com.fjl.test.configuration.SipTestProfileConfiguration;
import com.fjl.test.sip.SipActor;
import com.fjl.test.xml.parser.elements.HttpRequestElement;
import com.fjl.test.xml.parser.elements.ParamElement;
import com.fjl.test.xml.parser.http.ApplicationObject;
import com.fjl.test.xml.parser.http.HttpParserUtils;
import com.fjl.test.xml.parser.http.HttpResponseParser;

public class HttpRequestAction extends Action implements Runnable {

    /**
     * Http Client Actor
     */
    private HttpActor httpClientActor;

    /**
     * OpenCall Action
     */
    public static final String OPEN_CALL_ACTION = "opencall";

    /**
     * Get Application Encoded Url with key Action
     */
    public static final String GET_APPLICATION_URL_ACTION = "getappurl";

    /**
     * Application Session Key param
     */
    public static final String APPLICATION_KEY_PARAM = "appkey";

    /**
     * Http responses Content Type Param (html or xml)
     */
    public static final String HTTP_RESPONSES_CONTENT_TYPE_PARAM = "contentType=xml";

    /**
     * Http Action Save Parameters
     */
    public static final String HTTP_SAVE_PARAMETERS_ACTION = "saveParameters";

    /**
     * Http Scenario Name parameter
     */
    public static final String HTTP_SCENARIO_NAME_PARAMETER = "startStringDebugger";

    /**
     * Method (get or post)
     */
    private String method;

    /**
     * Action
     */
    private String action;

    /**
     * sipServerConfiguration
     */
    private SipTestProfileConfiguration sipServerConfiguration;

    /**
     * Action
     */
    private Vector<HttpParameter> httpParameters;

    /**
     * 
     * Constructor of HttpRequestAction.
     * 
     * @param httpClientActor
     * @param httpRequestElement
     * @param sipServerConfiguration
     * @throws TestException
     */
    public HttpRequestAction(HttpActor httpClientActor,
            HttpRequestElement httpRequestElement,
            SipTestProfileConfiguration sipServerConfiguration)
            throws TestException {
        super(httpClientActor, httpRequestElement.getSipTestActionId());
        logger.trace("HttpRequestAction.constructor: Http Client Actor: "
                    + httpClientActor.getId() + ", Action Id: " + actionId);
        this.httpClientActor = httpClientActor;
        this.method = httpRequestElement.getMethod();
        this.action = httpRequestElement.getAction();
        this.actionExecuted = false;
        this.httpParameters = new Vector<HttpParameter>();
        for (int i = 0; i < httpRequestElement.getHttpParametersNumber(); i++) {
            ParamElement element = httpRequestElement.getHttpParameter(i);
            if (!element.getName()
                    .equals(SipTestCallFlowUtils.ACTION_ATTRIBUTE)) {
                String elementValue = element.getValue();
                if (HTTP_SCENARIO_NAME_PARAMETER.equals(element.getName())) {
                    // replace the value by the test name
                    elementValue = httpClientActor.getTest()
                            .getSipTestCallFlow().getSipTestCallFlowName();
                }
                httpParameters.add(new HttpParameter(element.getName(),
                        elementValue));
            }

        }
        this.actionExecuted = false;

        this.sipServerConfiguration = sipServerConfiguration;

        // Now add this request has a listener of the Action executed before:
        if (actionId != 0) {

            int oldActionId = actionId - 1;
            httpClientActor.addActionMap("" + oldActionId, this);

        }

    }

    /**
     * Execute this Action
     * 
     * @return true if the Execution is a Success.
     */
    public void run() {
        try {
            if (logger.isDebugEnabled())
                logger.debug("HttpRequestAction.run: Http Client Actor: "
                        + httpClientActor.getId() + ", Action Id: " + actionId);

            if (OPEN_CALL_ACTION.equals(action)) {
                if (httpClientActor.getHttpConvergenceSipApplicationUriHost() == null) {
                    getHttpConvergenceParametersFromHttpServer();

                }

            }

            checkIfCookieHasTobeRemoved(action);

            GetMethod getMethod = buildHttpGetMethod();

            // get.setFollowRedirects(true);
            if (logger.isTraceEnabled())
                logger.trace("HttpRequestAction.run Get method: "
                        + getMethod.getURI().toString());
            int iGetResultCode = -1;
            try {
                iGetResultCode = httpClientActor.getHttpClient().executeMethod(
                        getMethod);
                final String strGetResponseBody = getMethod
                        .getResponseBodyAsString();

                if (logger.isTraceEnabled())
                    logger.trace("\n[HTTP][HTTP][HTTP][HTTP]  RESPONSE  [HTTP][HTTP][HTTP][HTTP]\n"
                            + strGetResponseBody
                            + "\n[HTTP][HTTP][HTTP][HTTP][HTTP][HTTP][HTTP][HTTP][HTTP][HTTP]\n");

            } catch (URIException e) {
                logger.error("HttpRequestAction.run URIException:"
                        + e.getMessage());
                logger.error("HttpRequestAction.run URIException:"
                        + e.getStackTrace());
            }

            catch (Exception ex) {
                logger.error("HttpRequestAction.run Exception:"
                        + ex.getMessage());
                ex.printStackTrace();

                logger.error(ex.getStackTrace());

                getMethod.releaseConnection();
                notifySipClientTest(new ActionReport(
                        ActionResult.ACTION_FAILED, actionId,
                        httpClientActor.getId(), isLastAction(),
                        "HttpRequestAction " + action, ex.getMessage()));
                actionExecuted = true;
            }
            if (iGetResultCode == 200) {
                notifySipClientTest(new ActionReport(
                        ActionResult.ACTION_SUCCESS, actionId,
                        httpClientActor.getId(), isLastAction(),
                        "HttpRequestAction " + action, "returned code: "
                                + iGetResultCode));

                actionExecuted = true;

            } else {
                notifySipClientTest(new ActionReport(
                        ActionResult.ACTION_FAILED, actionId,
                        httpClientActor.getId(), isLastAction(),
                        "HttpRequestAction " + action, "returned code: "
                                + iGetResultCode));

                actionExecuted = true;
            }

        } catch (Throwable e) {
            logger.error("HttpRequestAction.executeAction ERROR: Http Client ClientActor: "
                    + httpClientActor.getId()
                    + ", Action Id: "
                    + actionId
                    + ", Error:\n " + e.getMessage());
            notifySipClientTest(new ActionReport(
                    ActionResult.ACTION_FAILED, actionId,
                    httpClientActor.getId(), isLastAction(),
                    "HttpRequestAction " + action, e.getMessage()));
            actionExecuted = true;
        }

    }

    /**
     * Get the Http Convergence Parameters From the Http Server
     * 
     * @throws Exception
     */
    private void getHttpConvergenceParametersFromHttpServer() throws Exception {
        if (logger.isDebugEnabled())
            logger.debug("HttpRequestAction.getHttpConvergenceUrlFromHttpServer: Start...");

        try {
            String key = httpClientActor.getTest().getSipCallKey();
            if (logger.isDebugEnabled())
                logger.debug("HttpRequestAction.getHttpConvergenceUrlFromHttpServer: The Http Client actor has to get the HttpConvergenceSipApplicationUrl before executing the Open Call Action");

            String url = buildHttpRequestGetApplicationUrlUrl(key);
            GetMethod get = new GetMethod();
            get.setURI(new LaxURI(url, true, "ISO-8859-1"));
            try {
                int iGetResultCode = httpClientActor.getHttpClient()
                        .executeMethod(get);
            } catch (Exception e) {

                logger.error(e.getStackTrace());
            }

            final String strGetResponseBody = get.getResponseBodyAsString();

            if (logger.isDebugEnabled())
                logger.debug("HttpRequestAction.getHttpConvergenceUrlFromHttpServer: getHttpConvergenceSipApplicationUrl body received: \n"
                        + strGetResponseBody);

            ApplicationObject application = HttpResponseParser.getInstance()
                    .parseHttpResponse(strGetResponseBody);

            String urlReceived = application.getCallWithKey(key).getUrl();

            if (logger.isDebugEnabled())
                logger.debug("HttpRequestAction.getHttpConvergenceUrlFromHttpServer: getHttpConvergenceSipApplicationUrl URL received for this Test: \n"
                        + urlReceived);

            // Encode the URL
            /*
             * int indexToStartEncoding = encodedUrlReceived.indexOf( "=");
             * 
             * String startString = encodedUrlReceived.substring( 0,
             * indexToStartEncoding + 1);
             * 
             * String stringToEncode = encodedUrlReceived.substring(
             * indexToStartEncoding + 1);
             * 
             * String stringEncoded = URLEncoder.encode( stringToEncode,
             * "ISO-8859-1");
             * 
             * String encodedUrl = startString + stringEncoded;
             */
            int endIndexOfUriHost = 0;

            String tempString = "";
            if (urlReceived.startsWith("http://")) {
                tempString = urlReceived.substring(7);
                endIndexOfUriHost += 7;
            }

            String uriPath = "/";
            int uriPathIndex = tempString.indexOf("/");

            if (uriPathIndex != -1) {
                endIndexOfUriHost += uriPathIndex;
                uriPath = tempString.substring(uriPathIndex);
            }
            if (endIndexOfUriHost == 0) {
                endIndexOfUriHost = urlReceived.length();
            }

            String uriHost = urlReceived.substring(0, endIndexOfUriHost);

            if (logger.isDebugEnabled())
                logger.debug("HttpRequestAction.getHttpConvergenceUrlFromHttpServer: URI host and port: "
                        + uriHost);
            if (logger.isDebugEnabled())
                logger.debug("HttpRequestAction.getHttpConvergenceUrlFromHttpServer: URI path: "
                        + uriPath);

            if (logger.isDebugEnabled())
                logger.debug("HttpRequestAction.getHttpConvergenceUrlFromHttpServer: getHttpConvergenceSipApplicationUrl Encoded URL set for this Test: \n"
                        + uriHost + uriPath);
            httpClientActor.setHttpConvergenceSipApplicationUriHost(uriHost);
            httpClientActor.setHttpConvergenceSipApplicationUriPath(uriPath);
        } catch (URIException e) {
            logger.error("HttpRequestAction.getHttpConvergenceUrlFromHttpServer URIException:\n"
                    + e.getMessage());
            throw new Exception(
                    "HttpRequestAction.getHttpConvergenceUrlFromHttpServer URIException: "
                            + e.getMessage());
        } catch (IOException e) {
            logger.error("HttpRequestAction.getHttpConvergenceUrlFromHttpServer IOException:\n"
                    + e.getMessage());
            throw new Exception(
                    "HttpRequestAction.getHttpConvergenceUrlFromHttpServer IOException: "
                            + e.getMessage());
        } catch (SipClientParserException e) {
            logger.error("HttpRequestAction.getHttpConvergenceUrlFromHttpServer SipClientParserException:\n"
                    + e.getMessage());
            throw new Exception(
                    "HttpRequestAction.getHttpConvergenceUrlFromHttpServer SipClientParserException: "
                            + e.getMessage());
        }
    }

    private void checkIfCookieHasTobeRemoved(String action) {
        if (logger.isDebugEnabled())
            logger.debug("HttpRequestAction.checkIfCookieHasTobeRemoved for action: "
                    + action);

        if (action.equals(OPEN_CALL_ACTION)) {

            Cookie[] cookies = httpClientActor.getHttpClient().getState()
                    .getCookies();
            if (logger.isDebugEnabled()) {
                StringBuffer buffer = new StringBuffer();
                buffer.append("\n{}{}{}{}{} COOKIES {}{}{}{}{}\n");
                for (int i = 0; i < cookies.length; i++) {
                    Cookie cookie = cookies[i];
                    buffer.append("\n > Cookie Name: " + cookie.getName()
                            + ", Value: " + cookie.getValue());
                    if ("JSESSIONID".equals(cookie.getName())) {
                        cookie.setExpiryDate(new Date(1));
                        httpClientActor.getHttpClient().getState()
                                .purgeExpiredCookies();
                    }
                }

                buffer.append("\n{}{}{}{}{}{}{}{}{}{}{}{}{}{}\n");
                logger.debug(buffer.toString());
            }
        }

    }

    /**
     * Execute this Action
     * 
     * @return true if the Execution is a Success.
     */
    /*
     * public boolean executeAction () {
     * 
     * logger .error(
     * "HttpRequestAction.executeAction: An Http Request Action must be executed with the run method within an independant Thread"
     * ); return false; }
     */

    /**
     * Checks if this Action has already been executed
     * 
     * @return true if this Action has already been executed
     */
    public boolean isActionExecuted() {
        return actionExecuted;
    }

    /**
     * Build the Get Method for this Http Request Action
     * 
     * @return the Get method URL
     */
    private GetMethod buildHttpGetMethod() {
        String request = "http://"
                + sipServerConfiguration.getServerHttpIpAddress() + ":"
                + sipServerConfiguration.getServerHttpIpPort()
                + sipServerConfiguration.getServerHttpUrl();
        GetMethod method = null;
        if (OPEN_CALL_ACTION.equals(action)) {
            if (logger.isTraceEnabled())
                logger.trace("HttpRequestAction.buildHttpGetMethod: request to be created is an open call Action");
            String uriHost = httpClientActor
                    .getHttpConvergenceSipApplicationUriHost();
            String uriPath = httpClientActor
                    .getHttpConvergenceSipApplicationUriPath();

            method = new GetMethod();
            String queryString = "";
            if (uriPath.contains("?")) {
                queryString = "&action=" + action + "&"
                        + HTTP_RESPONSES_CONTENT_TYPE_PARAM;
            } else {
                queryString = "?action=" + action + "&"
                        + HTTP_RESPONSES_CONTENT_TYPE_PARAM;
            }

            request = uriHost + uriPath + queryString;

            try {
                org.apache.commons.httpclient.LaxURI uri = new LaxURI(request,
                        true, "UTF-8");
                method.setURI(uri);
            } catch (URIException e) {
                logger.error("HttpRequestAction.buildHttpGetMethod URIException: LaxURI does not work: \n"
                        + e.getMessage());

            }
            /*
             * method.setPath( uriPast); method.setQueryString( queryString);
             */
            /*
             * request += "?" + request += "&" +
             * HTTP_RESPONSES_CONTENT_TYPE_PARAM;
             */
        } else {
            if (HTTP_SAVE_PARAMETERS_ACTION.equals(action)) {
                request += "?" + "action=" + action;
            } else {
                request += "?action=" + action + "&"
                        + HTTP_RESPONSES_CONTENT_TYPE_PARAM;
            }

            for (int i = 0; i < httpParameters.size(); i++) {
                HttpParameter parameter = httpParameters.get(i);
                String parameterValue = parameter.getValue();
                if (parameterValue
                        .startsWith(HttpParserUtils.SIP_URI_CLIENT_START_STRING)) {
                    String actorId = parameterValue
                            .substring(HttpParserUtils.SIP_URI_CLIENT_START_STRING
                                    .length());
                    SipActor actor = httpClientActor.getTest()
                            .getActor(actorId);
                    if (actor != null) {
                        parameterValue = actor.getSipClientUA().getSipPhone()
                                .getSipUri();
                    } else {
                        // No sip client value found => Used a default sip uri
                        parameterValue = "sip:" + actorId + "@"
                                + "sip.fjl.default.domain:5060";
                    }
                    parameter.setValue(parameterValue);
                }
                /*
                 * if( !"action".equals( parameter.getName())){ request += "&" +
                 * parameter.getName() + "=" + parameter.getValue(); }
                 */
                request += "&" + parameter.getName() + "="
                        + parameter.getValue();

            }

            method = new GetMethod(request);
        }
        try {
            if (logger.isDebugEnabled())
                logger.debug("HttpRequestAction.buildHttpGetMethod: request created: "
                        + method.getURI().toString());
        } catch (URIException e) {
            logger.error("HttpRequestAction.buildHttpGetMethod URIException: "
                    + e.getMessage());
            logger.error(e);
            logger.error("HttpRequestAction.buildHttpGetMethod URIException: "
                    + e.getCause());

            logger.error("HttpRequestAction.buildHttpGetMethod URIException: "
                    + e.getStackTrace());

        }
        return method;

    }

    /**
     * Build the request URL to Get the Application Url
     * 
     * @param key
     *            Specifies the Sip Application key
     * @return the request URL
     */
    private String buildHttpRequestGetApplicationUrlUrl(String key) {
        String request = "http://"
                + sipServerConfiguration.getServerHttpIpAddress() + ":"
                + sipServerConfiguration.getServerHttpIpPort()
                + sipServerConfiguration.getServerHttpUrl();
        request += "?" + "action=" + GET_APPLICATION_URL_ACTION;
        request += "&" + HTTP_RESPONSES_CONTENT_TYPE_PARAM;
        request += "&" + APPLICATION_KEY_PARAM + "=" + key;
        if (logger.isDebugEnabled())
            logger.debug("HttpRequestAction.buildHttpRequestGetApplicationUrlUrl: request created: "
                    + request);
        return request;
    }

}
