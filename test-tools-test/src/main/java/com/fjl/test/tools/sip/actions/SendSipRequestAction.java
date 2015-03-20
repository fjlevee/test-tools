package com.fjl.test.tools.sip.actions;

import java.util.Vector;

import com.fjl.test.tools.ActionReport;
import com.fjl.test.tools.ActionReport.ActionResult;
import com.fjl.test.tools.sip.SipActor;
import com.fjl.test.tools.sip.SipConstants;
import com.fjl.test.tools.sip.SipTestTools;
import com.fjl.test.tools.sip.call.content.Content;
import com.fjl.test.tools.sip.utils.SipHeader;
import com.fjl.test.tools.xml.parser.elements.HeaderElement;
import com.fjl.test.tools.xml.parser.elements.MessageElement;
import com.fjl.test.tools.xml.parser.elements.SipRequestElement;

import fj_levee.test.parser.SipTestCallFlowActor;
import fj_levee.test.parser.SipTestCallFlowUtils;

public class SendSipRequestAction extends SipAction {

    /**
     * List of Headers to be Added
     */
    private Vector<SipHeader> headers;

    /**
     * Method of the Sip Request To Send
     */
    private String method;

    /**
     * Request Receiver Sip URI
     */
    private SipTestCallFlowActor requestReceiver;

    /**
     * Refer-To actor
     */
    private SipTestCallFlowActor referToActor;

    /**
     * Content To Send
     */
    private Content content;

    /**
     * Scenario Sip call id to replace if Replaces header is specified in the
     * scenario
     */
    private String replacesHeaderScenariorSipCallId = null;

    /**
     * This parameter is set to true if the request final destination is a
     * server. The last contact uri received in a Initial INVITE request or in a
     * Initial INVITE final response has to be used to send this request. Today
     * we use it only to make a convergence between an INVITE session and a
     * Subscribe session.
     */
    private boolean finalDestinationIsServer;

    private boolean authenticatedRequest;

    /**
     * 
     * Constructor of SendSipRequestAction.
     * 
     * @param sipActor
     * @param requestElement
     * @param timeToWait
     */
    public SendSipRequestAction(SipActor sipActor,
            SipRequestElement requestElement) {
        super(sipActor, requestElement.getCallid(), requestElement
                .getSipTestActionId(), 0);
        finalDestinationIsServer = requestElement.isFinalDestinationIsServer();
        logger.debug("FJL TEST 20.0: SendSipRequestAction.constructor: finalDestinationIsServer: "
                + finalDestinationIsServer);
        authenticatedRequest = requestElement.isAuthenticated();
        headers = SipClientActionFactory.getHeaders(requestElement
                .getMessageElement());

        method = requestElement.getMethod();
        if (logger.isTraceEnabled())
            logger.trace("SendSipRequestAction.constructor method is :"
                    + method);

        this.requestReceiver = requestElement.getSipTestCallFlowActorReceiver();

        MessageElement message = requestElement.getMessageElement();

        if (message != null) {
            content = message.getContent();
            // Check if the Replaces Header is present
            HeaderElement replacesHeader = message
                    .getHeader(SipConstants.REPLACES_HEADER);

            if (replacesHeader != null) {

                if (logger.isTraceEnabled())
                    logger.trace("SendSipRequestAction Replaces header is present");
                if (logger.isTraceEnabled())
                    logger.trace("SendSipRequestAction Replaces header value is: "
                            + replacesHeader.getCData());
                replacesHeaderScenariorSipCallId = SipTestTools
                        .extractCallIdFromReplacesHeader(replacesHeader
                                .getCData());

                if (logger.isTraceEnabled())
                    logger.trace("SendSipRequestAction Replaces header will replace the scenario sip call id: "
                            + replacesHeaderScenariorSipCallId);
            }

            // check if the Refer-To header is present
            HeaderElement referToHeader = message
                    .getHeader(SipConstants.REFER_TO_HEADER);

            if (referToHeader != null) {

                if (logger.isTraceEnabled())
                    logger.trace("SendSipRequestAction Refer-To header is present");
                if (logger.isTraceEnabled())
                    logger.trace("SendSipRequestAction Refer-To header value is: "
                            + referToHeader.getCData());

                String referToActorId = SipTestCallFlowUtils
                        .getUserIdentifier(referToHeader.getCData());
                referToActor = sipActor.getTest().getSipTestCallFlow()
                        .getSipTestCallFlowActor(referToActorId);

                if (logger.isTraceEnabled())
                    logger.trace("SendSipRequestAction Refer-To header value will be replaced by the sip uri of the Actor: "
                            + referToActor.getId());
            }
        }
        if ((logger.isTraceEnabled()) && (headers != null)) {
            String headersString = "";
            if (headers.size() > 0) {
                headersString += headers.get(0).getHeaderName();
                for (int i = 1; i < headers.size(); i++) {
                    headersString += ", " + headers.get(i).getHeaderName();
                }
            }

            logger.trace("SendSipRequestAction.constructor: List of Header that will be copied in this request are: "
                    + headersString);
        }

        if (logger.isTraceEnabled())
            logger.trace("SendSipRequestAction.constructor End");
    }

    /**
     * 
     * @see com.fjl.test.tools.Action#executeAction()
     */
    @Override
    public void executeAction() {

        if (logger.isDebugEnabled())
            logger.debug("SendSipRequestAction.executeAction: Start");

        try {

            // Check if the request send is an ACK on an error message
            if ((SipConstants.ACK.equals(method))
                    && (previousMessageWasAnError)) {
                // Yes the Sip Layer will the ACK => No need to send it.
                notifySipClientTest(new ActionReport(
                        ActionResult.ACTION_SUCCESS, actionId,
                        sipActor.getId(), isLastAction(),
                        "SendSipRequestAction " + method, "OK"));
                return;
            }

            if (logger.isDebugEnabled())
                logger.debug("SendSipRequestAction.executeAction: Sip ClientActor: "
                        + sipActor.getId()
                        + ", Action Id: "
                        + actionId
                        + ", Time to wait:"
                        + timeToWait
                        + ", Method: "
                        + method);

            Thread.sleep(timeToWait);
            String requestReceiverSipUri = null;
            if (finalDestinationIsServer) {
                logger.debug("FJL TEST 50.0");

                requestReceiverSipUri = sipActor.getSipClientUA()
                        .getLastContactUriReceived();

                logger.debug("FJL TEST 50.1: requestReceiverSipUri= "
                        + requestReceiverSipUri);
            } else {
                logger.debug("FJL TEST 51.1.0 sipActor: " + sipActor.getId());

                logger.debug("FJL TEST 51.1.1 getSipClientTest: "
                        + sipActor.getTest().getId());

                logger.debug("FJL TEST 51.1.2 requestReceiver: "
                        + requestReceiver.getId());

                requestReceiverSipUri = sipActor.getTest()
                        .getActor(requestReceiver.getId())
                        .getSipClientUA().getSipPhone().getSipUri();
                logger.debug("FJL TEST 51.1");
            }
            // Check the Method
            if (SipConstants.INVITE.equals(method)) {
                // Check if the Replaces Header has to be added
                if (replacesHeaderScenariorSipCallId != null) {
                    if (logger.isTraceEnabled())
                        logger.trace("SendSipRequestAction.executeAction Replaces header has to be Added");

                    String sipCallIdToReplace = sipActor.getSipClientUA()
                            .getSipCallIdWithScenarioSipCallId(
                                    replacesHeaderScenariorSipCallId);

                    if (logger.isTraceEnabled())
                        logger.trace("SendSipRequestAction.executeAction Sip Call Id to be Replaced: "
                                + sipCallIdToReplace);

                    SipHeader replacesHeader = sipActor.getSipClientUA()
                            .getCallControl()
                            .getReplacesHeader(sipCallIdToReplace);

                    headers.add(replacesHeader);
                }

                sipActor.getSipClientUA().sendInvite(scenarioSipCallId,
                        requestReceiverSipUri, content, headers,
                        authenticatedRequest);
                String callIdHeader = sipActor.getSipClientUA()
                        .getSipCallIdWithScenarioSipCallId(scenarioSipCallId);
                sipActor.getTest().setSipCallKey(callIdHeader);

            } else if (SipConstants.BYE.equals(method)) {
                sipActor.getSipClientUA().sendBye(scenarioSipCallId, headers);
            } else if (SipConstants.ACK.equals(method)) {
                sipActor.getSipClientUA().sendAck(scenarioSipCallId, content,
                        headers);

            } else if (SipConstants.MESSAGE.equals(method)) {
                sipActor.getSipClientUA().sendMessage(scenarioSipCallId,
                        requestReceiverSipUri, content, headers,
                        authenticatedRequest);
            } else if (SipConstants.OPTIONS.equals(method)) {
                sipActor.getSipClientUA().sendOptions(scenarioSipCallId,
                        requestReceiverSipUri, content, headers,
                        authenticatedRequest);
            } else if (SipConstants.UPDATE.equals(method)) {
                sipActor.getSipClientUA().sendUpdate(scenarioSipCallId,
                        content, headers);
            } else if (SipConstants.NOTIFY.equals(method)) {
                sipActor.getSipClientUA().sendNotify(scenarioSipCallId,
                        content, headers);
            } else if (SipConstants.SUBSCRIBE.equals(method)) {
                sipActor.getSipClientUA().sendSubscribe(scenarioSipCallId,
                        requestReceiverSipUri, content, headers,
                        authenticatedRequest);
            } else if (SipConstants.PRACK.equals(method)) {
                sipActor.getSipClientUA().sendPrack(scenarioSipCallId, content,
                        headers);
            } else if (SipConstants.CANCEL.equals(method)) {
                sipActor.getSipClientUA()
                        .sendCancel(scenarioSipCallId, headers);
            } else if (SipConstants.INFO.equals(method)) {
                sipActor.getSipClientUA().sendInfo(scenarioSipCallId, content,
                        headers);
            } else if (SipConstants.REFER.equals(method)) {
                if (referToActor == null)
                    logger.debug("FJL TEST 1.0 refer to actor is null");
                if (referToActor != null) {
                    if (referToActor == null)
                        logger.debug("FJL TEST 1.1 refer to actor is not null");
                    String referToActorSipUri = sipActor.getTest()
                            .getActor(referToActor.getId())
                            .getSipClientUA().getSipPhone().getSipUri();

                    headers.add(new SipHeader(SipConstants.REFER_TO_HEADER,
                            referToActorSipUri));
                    headers.add(new SipHeader(SipConstants.REFERRED_BY_HEADER,
                            sipActor.getSipClientUA().getSipPhone().getSipUri()));
                }

                sipActor.getSipClientUA().sendRefer(scenarioSipCallId, content,
                        headers);
            }

            if (logger.isDebugEnabled())
                logger.debug("SendSipRequestAction.executeAction: Sip ClientActor: "
                        + sipActor.getId()
                        + ", Action Id: "
                        + actionId
                        + ", Action done");
            notifySipClientTest(new ActionReport(
                    ActionResult.ACTION_SUCCESS, actionId, sipActor.getId(),
                    isLastAction(), "SendSipRequestAction " + method, "OK"));
        } catch (InterruptedException e) {
            logger.warn("SendSipRequestAction.executeAction: InterruptedException: "
                    + e.getMessage());
            notifySipClientTest(new ActionReport(
                    ActionResult.ACTION_FAILED, actionId, sipActor.getId(),
                    isLastAction(), "SendSipRequestAction " + method,
                    "ERROR: InterruptedException: " + e.getMessage()));

        } catch (Throwable e) {
            logger.error("SendSipRequestAction.executeAction ERROR: Sip ClientActor: "
                    + sipActor.getId()
                    + ", Action Id: "
                    + actionId
                    + ", Error:\n " + e.getMessage());
            notifySipClientTest(new ActionReport(
                    ActionResult.ACTION_FAILED, actionId, sipActor.getId(),
                    isLastAction(), "SendSipRequestAction " + method, "ERROR: "
                            + e.getMessage()));
        }

        actionExecuted = true;

    }

}
