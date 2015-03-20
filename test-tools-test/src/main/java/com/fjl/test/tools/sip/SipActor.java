package com.fjl.test.tools.sip;

import java.util.EventObject;
import java.util.Vector;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fjl.test.tools.Actor;
import com.fjl.test.tools.TheoricRealIdsMap;
import com.fjl.test.tools.sip.CallControl;
import com.fjl.test.tools.sip.SipConstants;
import com.fjl.test.tools.sip.SipEventListener;
import com.fjl.test.tools.sip.SipPhone;
import com.fjl.test.tools.sip.call.SipClientStackException;
import com.fjl.test.tools.sip.call.content.Content;
import com.fjl.test.tools.sip.utils.SipHeader;


public class SipActor extends Actor implements SipEventListener {
    /**
     * 
     */
    private static final long serialVersionUID = -5301475138171713710L;

    /**
     * Log4j Logger
     */
    private Logger logger = LoggerFactory.getLogger(com.fjl.test.tools.sip.SipActor.class);

    /**
     * Sip Phone
     */
    private SipPhone sipPhone;

    /**
     * hashMap between Theoric Call Id and Real Call Id. (call header)
     */
    private TheoricRealIdsMap callsMap;

    /**
     * SipClientUAListener
     */
    private SipClientUAListener listener;

    /**
     * Current CallId on Invite
     */
    private String currentCallID;

    /**
     * Last contact uri received on an Initial INVITE request or in a Initial
     * INVITE final response. Today we use it only to make a convergence between
     * an INVITE session and a Subscribe session.
     */
    private String lastContactUriReceived;

    /**
     * This parameter check if the Client UA is used.
     */
    private boolean used = false;

    /**
     * Concurent HashMap to Stor Event that have not be notified to the
     * SipClientUAListener
     */
    private Vector<SipClientUAEvent> sipUAEvents;

    /**
     * This parameter is helpful to get the Last Sip Call Id removed in this
     * SipClient UA. This is helpful to get the Last Sip Event received before a
     * Sip Dialog has been closed. Example: a reception of an INVITE Error while
     * the Sip Test Client is not listening on event. When this Sip Test Client
     * 
     */
    private String lastSipCallIdRemoved;

    /**
     * Constructor
     * 
     * @param sipPhone
     */
    /**
     * 
     * Constructor of SipClientActor.
     * 
     * @param test
     * @param sipTestCallFlowActor
     * @param sipClientUA
     */
    public SipActor(String id, boolean simulated) {
        super(id, simulated);
        callsMap = new TheoricRealIdsMap();
        

        
    }

     

    /**
     * 
     * @param callId
     *            Specifies the Call ID on which the Event Has occured
     * @param method
     *            Specifes the Method. Can be a Sip Method (for Request or
     *            response method) or a TIME_OUT_EVENT
     * @param event
     *            Specifies the real Event.
     * 
     */
    public void notifyListener(String callId, String method, EventObject event) {

        SipClientUAEvent sipUAEvent = new SipClientUAEvent(callId, method,
                event);
        if (logger.isTraceEnabled())
            logger.trace("SipActor.notifyListener of Sip UA Event: "
                    + sipUAEvent.getEventAsString());

        boolean handled = false;
        if (listener != null) {
            logger.debug("FJL TEST 1.0");
            handled = listener.notifySipClientUAEvent(sipUAEvent);
            logger.debug("FJL TEST 1.1 Handled?: " + handled);
            // now the listener is set to null if handled
            if (handled) {
                listener = null;
            }
        }
        if (!handled) {
            logger.debug("FJL TEST 1.2 !Handled");

            addSipUAEvent(sipUAEvent);

            if (logger.isTraceEnabled())
                logger.trace("SipClientUA: Size of sipUAEvents List: "
                        + sipUAEvents.size());
        }

    }

    public void addSipUAEvent(SipClientUAEvent sipUAEvent) {
        synchronized (sipUAEvents) {
            String codeString = "";
            if (sipUAEvent.getEvent().getClass() == ResponseEvent.class) {
                codeString = " ("
                        + ((ResponseEvent) sipUAEvent.getEvent()).getResponse()
                                .getStatusCode() + ")";
            }
            logger.warn("SipActor.notifyListener: an Event: "
                    + sipUAEvent.getMethod()
                    + codeString
                    + " on call id: "
                    + sipUAEvent.getCallId()
                    + " has not be handled by a SipClientUAListener => Store it in sipUAEvents List");
            sipUAEvents.add(sipUAEvent);
        }

    }

    /**
     * Allows the SipClientUAListener to check if the Event he waits has already
     * been received.
     * 
     * @param clientCallId
     *            Specifies the Client Call ID
     * @param method
     *            Specifies the method of the Event awaited
     * @param eventClass
     *            Specifies the Event Class awaited (can be ResponseEvent.class
     *            or RequestEvent.class)
     * @param statusCode
     *            Specifies the Status Code in case of a Response awaited
     * @return the SipClientUAEvent
     */
    public SipClientUAEvent checkIfEventAlreadyReceived(
            String theoricId, String method, Class eventClass,
            int statusCode) {
        SipClientUAEvent event = null;
        // First determine the SIP Call ID
        String realCallId = null;
        try {
            realCallId = getSipCallIdWiththeoricId(theoricId);
        } catch (SipClientStackException e) {
            // Nothing to do
            logger.warn("SipActor.checkIfEventAlreadyReceived can not find a Sip Call Id with scenario Sip Call Id: "
                    + theoricId
                    + ", the Sip Client UA will search with the Last Sip Call Id removed: "
                    + lastSipCallIdRemoved);

            realCallId = lastSipCallIdRemoved;
        }
        if (realCallId != null) {
            if (logger.isTraceEnabled())
                logger.trace("SipActor.checkIfEventAlreadyReceived: on sip call Id: "
                        + realCallId
                        + ", method awaited: "
                        + method
                        + ", Event Class: " + eventClass.getName());

            synchronized (sipUAEvents) {

                for (int i = 0; i < sipUAEvents.size(); i++) {
                    if (logger.isTraceEnabled())
                        logger.trace("Check With Event: "
                                + sipUAEvents.get(i).getEventAsString());
                    if (sipUAEvents.get(i).checkEventParameters(realCallId,
                            method, eventClass, statusCode)) {

                        event = sipUAEvents.get(i);
                        deleteEventsReceivedBeforeEventIndex(i);
                        if (logger.isTraceEnabled())
                            logger.trace("SipClientUA: Size of sipUAEvents List: "
                                    + sipUAEvents.size());
                        return event;
                    }
                }
            }
        }
        return event;
    }

    /**
     * Remove the Event received atr the given index all the event received
     * before the given index, from the List of Event received. <br>
     * Example:<br>
     * in a List of event: event1,event2,event3,event4,event5 => the call of
     * method deleteEventsReceivedBeforeEventIndex(event3) will result to the
     * list: event4,event5
     * 
     * @param eventIndex
     *            Specifies the event index
     */
    private void deleteEventsReceivedBeforeEventIndex(int eventIndex) {
        for (int i = eventIndex; i >= 0; i--) {
            sipUAEvents.remove(i);
        }
    }

    // SipEventListener interface implementation

    /**
     * SipDialog closed
     * 
     * @param realCallId
     *            specifies the Call ID
     */
    public void sipDialogClosed(String realCallId) {
        this.lastSipCallIdRemoved = realCallId;
        
        callsMap.removeMapWithRealId(realCallId);
        if (logger.isTraceEnabled())
            logger.trace("SipClientUA: Sip Dialog Closed: " + realCallId
                    + ", Call Map Size: " + callsMap.size());
    }

    /**
     * Handle an INVTE message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param requestEvent
     *            specifies the INVITE request event
     */
    public void handleInvite(String realCallId, RequestEvent requestEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleInvite: Call ID: " + realCallId);
        checkIfNewSipDialog(realCallId, true);

        notifyListener(realCallId, SipConstants.INVITE, requestEvent);

    }

    /**
     * handle a REINVITE message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param requestEvent
     *            specifies the INVITE request event
     */
    public void handleReInvite(String realCallId, RequestEvent requestEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleReInvite: Call ID: " + realCallId);
        notifyListener(realCallId, SipConstants.INVITE, requestEvent);
    }

    /**
     * Handles a CANCEL message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param requestEvent
     *            specifies the CANCEL request event
     */
    public void handleCancel(String realCallId, RequestEvent requestEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleCancel: Call ID: " + realCallId);
        notifyListener(realCallId, SipConstants.CANCEL, requestEvent);
    }

    /**
     * Handles a BYE message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param requestEvent
     *            specifies the BYE request event
     */
    public void handleBye(String realCallId, RequestEvent requestEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleBye: Call ID: " + realCallId);
        notifyListener(realCallId, SipConstants.BYE, requestEvent);
    }

    /**
     * Handles a Prack message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param requestEvent
     *            specifies the PRACK request event
     */
    public void handlePrack(String realCallId, RequestEvent requestEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handlePrack: Call ID: " + realCallId);
        notifyListener(realCallId, SipConstants.PRACK, requestEvent);
    }

    /**
     * Handles a ACK message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param requestEvent
     *            specifies the ACK request event
     */
    public void handleAck(String realCallId, RequestEvent requestEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleAck: Call ID: " + realCallId);
        notifyListener(realCallId, SipConstants.ACK, requestEvent);
    }

    /**
     * Handles a MESSAGE message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param requestEvent
     *            specifies the MESSAGE request event
     */
    public void handleMessage(String realCallId, RequestEvent requestEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleMessage: Call ID: " + realCallId);
        // Check if this message request creates a new Sip Dialog
        checkIfNewSipDialog(realCallId, false);

        notifyListener(realCallId, SipConstants.MESSAGE, requestEvent);
    }

    /**
     * Handles a OPTIONS message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param requestEvent
     *            specifies the OPTION request event
     */
    public void handleOptions(String realCallId, RequestEvent requestEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleOptions: Call ID: " + realCallId);
        // Check if this options request creates a new Sip Dialog
        checkIfNewSipDialog(realCallId, false);
        notifyListener(realCallId, SipConstants.OPTIONS, requestEvent);
    }

    /**
     * Handles a UPDATE message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param requestEvent
     *            specifies the UPDATE request event
     */
    public void handleUpdate(String realCallId, RequestEvent requestEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleUpdate: Call ID: " + realCallId);
        notifyListener(realCallId, SipConstants.UPDATE, requestEvent);
    }

    /**
     * Handles a NOTIFY message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param requestEvent
     *            specifies the NOTIFY request event
     */
    public void handleNotify(String realCallId, RequestEvent requestEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleNotify: Call ID: " + realCallId);
        notifyListener(realCallId, SipConstants.NOTIFY, requestEvent);
    }

    /**
     * Handles a INFO message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param requestEvent
     *            specifies the INFO request event
     */
    public void handleInfo(String realCallId, RequestEvent requestEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleInfo: Call ID: " + realCallId);
        notifyListener(realCallId, SipConstants.INFO, requestEvent);
    }

    /**
     * Handles a REFER message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param requestEvent
     *            specifies the REFER request event
     */
    public void handleRefer(String realCallId, RequestEvent requestEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleRefer: Call ID: " + realCallId);
        notifyListener(realCallId, SipConstants.REFER, requestEvent);
    }

    /**
     * Handles a REGISTER message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param requestEvent
     *            specifies the REGISTER request event
     */
    public void handleRegister(String realCallId, RequestEvent requestEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleRegister: Call ID: " + realCallId);
        notifyListener(realCallId, SipConstants.REGISTER, requestEvent);
    }

    /**
     * handle a INVITE success response
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Success Response Evente
     */
    public void handleInviteSuccessResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleInviteSuccessResponse: Call ID: "
                    + realCallId);
        try {
            String lastContactUriReceived = sipPhone.getCallControl()
                    .getContactHeaderReceived(realCallId).getHeaderValue();
            if (lastContactUriReceived != null) {
                setLastContactUriReceived(sipPhone.getCallControl().getContactHeaderReceived(
                        realCallId).getHeaderValue());
            }
        } catch (SipClientStackException e) {
            logger.warn("SipActor.handleInviteSuccessResponse SipClientStackException when calling sipPhone.getCallControl().getContactHeaderReceived method\n"
                    + e.getMessage());
        }

        notifyListener(realCallId, SipConstants.INVITE, responseEvent);
    }

    /**
     * handle a INVITE success response
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Success Response Event
     */
    public void handleInviteErrorResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleInviteErrorResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.INVITE, responseEvent);
    }

    /**
     * handle a INVITE provisional response
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Provisional Response Event
     */
    public void handleInviteProvisionalResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleInviteProvisionalResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.INVITE, responseEvent);
    }

    /**
     * handle a INVITE redirect response
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Redirect Response Event
     */
    public void handleInviteRedirectResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleInviteRedirectResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.INVITE, responseEvent);
    }

    /**
     * Handles a CANCEL message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Success Response Event
     */
    public void handleCancelSuccessResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleCancelSuccessResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.CANCEL, responseEvent);
    }

    /**
     * Handles a CANCEL message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Error Response Event
     */
    public void handleCancelErrorResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleCancelErrorResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.CANCEL, responseEvent);
    }

    /**
     * Handles a BYE message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Success Response Event
     */
    public void handleByeSuccessResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleByeSuccessResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.BYE, responseEvent);
    }

    /**
     * Handles a BYE message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Success Response Event
     */
    public void handleByeErrorResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleByeErrorResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.BYE, responseEvent);
    }

    /**
     * Handles a Prack message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Success Response Event
     */
    public void handlePrackSuccessResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handlePrackSuccessResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.PRACK, responseEvent);
    }

    /**
     * Handles a Prack message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Success Response Event
     */
    public void handlePrackErrorResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handlePrackErrorResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.PRACK, responseEvent);
    }

    /**
     * Handles a MESSAGE message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Success Response Event
     */
    public void handleMessageSuccessResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleMessageSuccessResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.MESSAGE, responseEvent);
    }

    /**
     * Handles a MESSAGE message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Success Response Event
     */
    public void handleMessageErrorResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleMessageErrorResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.MESSAGE, responseEvent);
    }

    /**
     * Handles a OPTIONS message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Success Response Event
     */
    public void handleOptionsSuccessResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleOptionsSuccessResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.OPTIONS, responseEvent);
    }

    /**
     * Handles a OPTIONS message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Error Response Event
     */
    public void handleOptionsErrorResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleOptionsErrorResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.OPTIONS, responseEvent);
    }

    /**
     * Handles a UPDATE message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Success Response Event
     */
    public void handleUpdateSuccessResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleUpdateSuccessResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.UPDATE, responseEvent);
    }

    /**
     * Handles a UPDATE message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Errot Response Event
     */
    public void handleUpdateErrorResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleUpdateErrorResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.UPDATE, responseEvent);
    }

    /**
     * Handles a NOTIFY message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Success Response Event
     */
    public void handleNotifySuccessResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleNotifySuccessResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.NOTIFY, responseEvent);
    }

    /**
     * Handles a NOTIFY message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Error Response Event
     */
    public void handleNotifyErrorResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleNotifyErrorResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.NOTIFY, responseEvent);
    }

    /**
     * Handles a INFO message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Success Response Event
     */
    public void handleInfoSuccessResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleInfoSuccessResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.INFO, responseEvent);
    }

    /**
     * Handles a INFO message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Error Response Event
     */
    public void handleInfoErrorResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleInfoErrorResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.INFO, responseEvent);
    }

    /**
     * Handles a SUBSCRIBE message success response
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Success Response Event
     */
    public void handleSubscribeSuccessResponse(String realCallId,
            ResponseEvent responseEvent) {

        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleSubscribeSuccessResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.SUBSCRIBE, responseEvent);
    }

    /**
     * Handles a SUBSCRIBE message error response
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Error Response Event
     */
    public void handleSubscribeErrorResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleSubscribeErrorResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.SUBSCRIBE, responseEvent);

    }

    /**
     * Handles a REFER message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Success Response Event
     */
    public void handleReferSuccessResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleReferSuccessResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.REFER, responseEvent);
    }

    /**
     * Handles a REFER message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Error Response Event
     */
    public void handleReferErrorResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleReferErrorResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.REFER, responseEvent);
    }

    /**
     * Handles a REGISTER success response message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Success Response Event
     */
    public void handleRegisterSuccessResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleRegisterSuccessResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.REGISTER, responseEvent);
    }

    /**
     * Handles a REGISTER error response message
     * 
     * @param realCallId
     *            specifies the Call ID
     * @param responseEvent
     *            specifies the Error Response Event
     */
    public void handleRegisterErrorResponse(String realCallId,
            ResponseEvent responseEvent) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.handleRegisterErrorResponse: Call ID: "
                    + realCallId);
        notifyListener(realCallId, SipConstants.REGISTER, responseEvent);
    }

    /**
     * Get the SipPhone used by this SipCLientUA
     * 
     * @return the SipPhone used by this SipCLientUA
     */
    public SipPhone getSipPhone() {
        return sipPhone;
    }

    /**
     * set the SipPhone used by this SipClient UA
     * 
     * @param sipPhone
     *            specifies the SipPhone used by this SipClient UA
     */
    public void setSipPhone(SipPhone sipPhone) {
        this.sipPhone = sipPhone;
     }

    // Call Control methods calls
    /**
     * Create a New Call by Sending a new INVITE mesage
     * 
     * @param theoricId
     *            specifies the Client Call ID
     * @param toSipUri
     *            specifies the To Sip URI
     * @param content
     *            specifies the Content to add in the INVITE.
     * @param headers
     *            specifies the headers to be added in the INVITE.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendInvite(String theoricId, String toSipUri,
            Content content, Vector<SipHeader> headers, boolean authenticated)
            throws SipClientStackException {
        try {
            if (logger.isTraceEnabled())
                logger.trace("SipActor.sendInvite to :" + toSipUri);
            // Check if the INVITE is a RE-INVITE => It means that the
            // theoricId has already been mapped

            if (callsMap.getRealId(theoricId) != null) {
                if (logger.isTraceEnabled())
                    logger.trace("SipActor.sendInvite is in fact a RE-INVITE");

                sipPhone.getCallControl().sendReInvite(
                        callsMap.getRealId(theoricId), content,
                        headers);

            } else {
                String realCallId = sipPhone.getCallControl().sendInvite(toSipUri, content,
                        headers, authenticated);
                callsMap.addMap(theoricId, realCallId);
            }

        } catch (SipClientStackException e) {
            logger.error("SipActor.sendInvite SipClientStackException: ",e);
            
            throw new SipClientStackException("SipActor.sendInvite error: "
                    + e.getMessage());

        } catch (Throwable e) {
            logger.error("SipActor.sendInvite Throwable: ",e);
            
            throw new SipClientStackException("SipActor.sendInvite error: "
                    + e.getMessage());
        }
    }

    /**
     * Create a New Call by Sending a new INVITE mesage
     * 
     * @param theoricId
     *            specifies the Client Call ID
     * @param headers
     *            specifies the headers to be added in the INVITE.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendBye(String theoricId, Vector<SipHeader> headers)
            throws SipClientStackException {
        try {
            if (logger.isTraceEnabled())
                logger.trace("SipActor.sendBye");
            String callId = callsMap.getRealId(theoricId);
            if (callId == null) {
                throw new SipClientStackException(
                        "SipActor.sendBye error: Can not find a Real Call with client Call Id: "
                                + theoricId);
            }
            sipPhone.getCallControl().sendBye(callId, headers);
        } catch (SipClientStackException e) {
            logger.error("SipActor.sendBye SipClientStackException: "
                    + e.getMessage());
            throw new SipClientStackException("SipActor.sendBye error: "
                    + e.getMessage());

        } catch (Throwable e) {
            logger.error("SipActor.sendBye Throwable: " + e.getMessage());
            throw new SipClientStackException("SipActor.sendBye error: "
                    + e.getMessage());
        }
    }

    /**
     * Send a Message message
     * 
     * @param theoricId
     *            specifies the Client Call ID
     * @param toSipUri
     *            specifies the To Sip URI. Helpful only when creating a intial
     *            Message Request
     * @param content
     *            specifies the Content to add in the MESSAGE.
     * @param headers
     *            specifies the headers to be added in the MESSAGE.
     * @return the Call-ID created
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendMessage(String theoricId, String toSipUri,
            Content content, Vector<SipHeader> headers, boolean authenticated)
            throws SipClientStackException {
        try {
            if (logger.isTraceEnabled())
                logger.trace("SipActor.sendMessage");

            if (callsMap.getRealId(theoricId) != null) {
                if (logger.isTraceEnabled())
                    logger.trace("SipActor.sendMessage in a SIP INVITE DIALOG");

                sipPhone.getCallControl().sendMessage(
                        callsMap.getRealId(theoricId), content,
                        headers);

            } else {
                String callId = sipPhone.getCallControl().sendInitialMessage(toSipUri,
                        content, headers, authenticated);
                callsMap.addMap(theoricId, callId);
            }

        } catch (SipClientStackException e) {
            logger.error("SipActor.sendMessage SipClientStackException: "
                    + e.getMessage());
            throw new SipClientStackException("SipActor.sendMessage error: "
                    + e.getMessage());

        } catch (Throwable e) {
            logger.error("SipActor.sendMessage Throwable: " + e.getMessage());
            throw new SipClientStackException("SipActor.sendMessage error: "
                    + e.getMessage());
        }
    }

    /**
     * Send a Options message
     * 
     * @param theoricId
     *            specifies the Client Call ID
     * @param toSipUri
     *            specifies the To Sip URI. Helpful only when creating a intial
     *            Message Request
     * @param content
     *            specifies the Content to add in the OPTIONS.
     * @param headers
     *            specifies the headers to be added in the OPTIONS.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendOptions(String theoricId, String toSipUri,
            Content content, Vector<SipHeader> headers, boolean authenticated)
            throws SipClientStackException {
        try {
            if (logger.isTraceEnabled())
                logger.trace("SipActor.sendOptions");

            if (callsMap.getRealId(theoricId) != null) {
                if (logger.isTraceEnabled())
                    logger.trace("SipActor.sendOptions in a SIP INVITE DIALOG");

                sipPhone.getCallControl().sendOptions(
                        callsMap.getRealId(theoricId), content,
                        headers);

            } else {
                String callId = sipPhone.getCallControl().sendInitialOptions(toSipUri,
                        content, headers, authenticated);
                callsMap.addMap(theoricId, callId);
            }

        } catch (SipClientStackException e) {
            logger.error("SipActor.sendOptions SipClientStackException: "
                    + e.getMessage());
            throw new SipClientStackException("SipActor.sendOptions error: "
                    + e.getMessage());

        } catch (Throwable e) {
            logger.error("SipActor.sendOptions Throwable: " + e.getMessage());
            throw new SipClientStackException("SipActor.sendOptions error: "
                    + e.getMessage());
        }
    }

    /**
     * Send a Update message
     * 
     * @param theoricId
     *            specifies the Client Call ID
     * @param content
     *            specifies the Content to add in the UPDATE.
     * @param headers
     *            specifies the headers to be added in the UPDATE.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendUpdate(String theoricId, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {
        try {
            if (logger.isTraceEnabled())
                logger.trace("SipActor.sendUpdate");
            String callId = callsMap.getRealId(theoricId);
            if (callId == null) {
                throw new SipClientStackException(
                        "SipActor.sendUpdate error: Can not find a SipClient Call with client Call Id: "
                                + theoricId);
            }
            sipPhone.getCallControl().sendUpdate(callId, content, headers);
        } catch (SipClientStackException e) {
            logger.error("SipActor.sendUpdate SipClientStackException: "
                    + e.getMessage());
            throw new SipClientStackException("SipActor.sendUpdate error: "
                    + e.getMessage());

        } catch (Throwable e) {
            logger.error("SipActor.sendUpdate Throwable: " + e.getMessage());
            throw new SipClientStackException("SipActor.sendUpdate error: "
                    + e.getMessage());
        }
    }

    /**
     * Send a Notify message
     * 
     * @param theoricId
     *            specifies the Client Call ID
     * @param content
     *            specifies the Content to add in the NOTIFY.
     * @param headers
     *            specifies the headers to be added in the NOTIFY.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendNotify(String theoricId, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {
        try {
            if (logger.isTraceEnabled())
                logger.trace("SipActor.sendNotify");
            String callId = callsMap.getRealId(theoricId);
            if (callId == null) {
                throw new SipClientStackException(
                        "SipActor.sendNotify error: Can not find a SipClient Call with client Call Id: "
                                + theoricId);
            }
            sipPhone.getCallControl().sendNotify(callId, content, headers);
        } catch (SipClientStackException e) {
            logger.error("SipActor.sendNotify SipClientStackException: "
                    + e.getMessage());
            throw new SipClientStackException("sendNotify.sendNotify error: "
                    + e.getMessage());

        } catch (Throwable e) {
            logger.error("SipActor.sendNotify Throwable: " + e.getMessage());
            throw new SipClientStackException("SipActor.sendNotify error: "
                    + e.getMessage());
        }
    }

    /**
     * Send a Subscribe message
     * 
     * @param theoricId
     *            specifies the Client Call ID
     * @param toSipUri
     *            specifies the To Sip URI. Helpful only when creating a initial
     *            Message Request
     * @param content
     *            specifies the Content to add in the SUBSCRIBE.
     * @param headers
     *            specifies the headers to be added in the SUBSCRIBE.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendSubscribe(String theoricId, String toSipUri,
            Content content, Vector<SipHeader> headers, boolean authenticated)
            throws SipClientStackException {
        try {
            if (logger.isTraceEnabled())
                logger.trace("SipActor.sendSubscribe");

            if (callsMap.getRealId(theoricId) != null) {
                if (logger.isTraceEnabled())
                    logger.trace("SipActor.sendSubscribe in a SIP INVITE DIALOG");

                sipPhone.getCallControl().sendSubscribe(
                        callsMap.getRealId(theoricId), content,
                        headers);

            } else {
                if (logger.isTraceEnabled())
                    logger.trace("SipActor.sendSubscribe => sendInitialSubscribe");
                String callId = sipPhone.getCallControl().sendInitialSubscribe(toSipUri,
                        content, headers, authenticated);
                callsMap.addMap(theoricId, callId);
            }

        } catch (SipClientStackException e) {
            logger.error("SipActor.sendSubscribe SipClientStackException: "
                    + e.getMessage());
            throw new SipClientStackException(
                    "SipActor.sendSubscribe error: " + e.getMessage());

        } catch (Throwable e) {
            logger.error("SipActor.sendSubscribe Throwable: "
                    + e.getMessage());
            throw new SipClientStackException(
                    "SipActor.sendSubscribe error: " + e.getMessage());
        }
    }

    /**
     * Send a Prack message
     * 
     * @param theoricId
     *            specifies the Client Call ID
     * @param content
     *            specifies the Content to add in the PRACK.
     * @param headers
     *            specifies the headers to be added in the PRACK.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendPrack(String theoricId, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {
        try {
            if (logger.isTraceEnabled())
                logger.trace("SipActor.sendPrack");
            String callId = callsMap.getRealId(theoricId);
            if (callId == null) {
                throw new SipClientStackException(
                        "SipActor.sendPrack error: Can not find a SipClient Call with client Call Id: "
                                + theoricId);
            }
            sipPhone.getCallControl().sendPrack(callId, content, headers);
        } catch (SipClientStackException e) {
            logger.error("SipActor.sendPrack SipClientStackException: "
                    + e.getMessage());
            throw new SipClientStackException("sendNotify.sendPrack error: "
                    + e.getMessage());

        } catch (Throwable e) {
            logger.error("SipActor.sendPrack Throwable: " + e.getMessage());
            throw new SipClientStackException("SipActor.sendPrack error: "
                    + e.getMessage());
        }
    }

    /**
     * Send a Ack message
     * 
     * @param theoricId
     *            specifies the Client Call ID
     * @param content
     *            specifies the Content to add in the ACK.
     * @param headers
     *            specifies the headers to be added in the ACK.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendAck(String theoricId, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {
        try {
            if (logger.isTraceEnabled())
                logger.trace("SipActor.sendAck");
            String callId = callsMap.getRealId(theoricId);
            if (callId == null) {
                throw new SipClientStackException(
                        "SipActor.sendAck error: Can not find a SipClient Call with client Call Id: "
                                + theoricId);
            }
            sipPhone.getCallControl().sendAck(callId, content, headers);
        } catch (SipClientStackException e) {
            logger.error("SipActor.sendAck SipClientStackException: "
                    + e.getMessage());
            throw new SipClientStackException("SipActor.sendAck error: "
                    + e.getMessage());

        } catch (Throwable e) {
            logger.error("SipActor.sendAck Throwable: " + e.getMessage());
            throw new SipClientStackException("SipActor.sendAck error: "
                    + e.getMessage());
        }
    }

    /**
     * Send a Cancel message
     * 
     * @param theoricId
     *            specifies the Client Call ID
     * @param headers
     *            specifies the headers to be added in the CANCEL.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendCancel(String theoricId, Vector<SipHeader> headers)
            throws SipClientStackException {
        try {
            if (logger.isTraceEnabled())
                logger.trace("SipActor.sendCancel");
            String callId = callsMap.getRealId(theoricId);
            if (callId == null) {
                throw new SipClientStackException(
                        "SipActor.sendCancel error: Can not find a SipClient Call with client Call Id: "
                                + theoricId);
            }
            sipPhone.getCallControl().sendCancel(callId, headers);
        } catch (SipClientStackException e) {
            logger.error("SipActor.sendCancel SipClientStackException: "
                    + e.getMessage());
            throw new SipClientStackException("SipActor.sendCancel error: "
                    + e.getMessage());

        } catch (Throwable e) {
            logger.error("SipActor.sendCancel Throwable: " ,e);
            throw new SipClientStackException("SipActor.sendCancel error: "
                    + e.getMessage());
        }
    }

    /**
     * Send a Info message
     * 
     * @param theoricId
     *            specifies the Client Call ID
     * @param content
     *            specifies the Content to add in the INFO.
     * @param headers
     *            specifies the headers to be added in the INFO.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendInfo(String theoricId, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {
        try {
            if (logger.isTraceEnabled())
                logger.trace("SipActor.sendInfo");
            String callId = callsMap.getRealId(theoricId);
            if (callId == null) {
                throw new SipClientStackException(
                        "SipActor.sendInfo error: Can not find a SipClient Call with client Call Id: "
                                + theoricId);
            }
            sipPhone.getCallControl().sendInfo(callId, content, headers);
        } catch (SipClientStackException e) {
            logger.error("SipActor.sendInfo SipClientStackException: "
                    + e.getMessage());
            throw new SipClientStackException("SipActor.sendInfo error: "
                    + e.getMessage());

        } catch (Throwable e) {
            logger.error("SipActor.sendInfo Throwable: " ,e);
            throw new SipClientStackException("SipActor.sendInfo error: "
                    + e.getMessage());
        }
    }

    /**
     * Send a Refer message
     * 
     * @param theoricId
     *            specifies the Client Call ID
     * @param content
     *            specifies the Content to add in the REFER.
     * @param headers
     *            specifies the headers to be added in the REFER.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendRefer(String theoricId, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {
        try {
            if (logger.isTraceEnabled())
                logger.trace("SipActor.sendRefer");
            String callId = callsMap.getRealId(theoricId);
            if (callId == null) {
                throw new SipClientStackException(
                        "SipActor.sendRefer error: Can not find a SipClient Call with client Call Id: "
                                + theoricId);
            }
            sipPhone.getCallControl().sendRefer(callId, content, headers);
        } catch (SipClientStackException e) {
            logger.error("SipActor.sendRefer SipClientStackException: "
                    + e.getMessage());
            throw new SipClientStackException("SipActor.sendRefer error: "
                    + e.getMessage());

        } catch (Throwable e) {
            logger.error("SipActor.sendRefer Throwable: " + e.getMessage());
            throw new SipClientStackException("SipActor.sendRefer error: "
                    + e.getMessage());
        }
    }

    /**
     * Send a Response
     * 
     * @param theoricId
     *            Specifies the Client Call ID
     * @param method
     *            Specifies the SIP Method of the response
     * @param statusCode
     *            Specifies the status code of the response
     * @param content
     *            Specifies the Content to add in the INVITE response.
     * @param headers
     *            Specifies the headers to be added in the INVITE response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendResponse(String theoricId, String method,
            int statusCode, Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        try {
            if (logger.isTraceEnabled())
                logger.trace("SipActor.sendResponse: method: " + method
                        + ", Status code:" + statusCode);
            String realCallId = callsMap.getRealId(theoricId);
            if (realCallId == null) {

                if (currentCallID != null) {
                    // Check if the Response to send is on an Incoming Initial
                    // Invite

                    realCallId = callsMap.getRealId(currentCallID);
                    if (realCallId != null) {
                        // Change the key to the theoricId
                        callsMap.removeMapWithRealId(currentCallID);
                        callsMap.addMap(theoricId, realCallId);
                    } else {
                        throw new SipClientStackException(
                                "SipActor.sendResponse error: Can not find a SipClient Call with client Call Id: "
                                        + theoricId);
                    }
                } else {
                    throw new SipClientStackException(
                            "SipActor.sendResponse error: Can not find a SipClient Call with client Call Id: "
                                    + theoricId);
                }
            }

            if ((statusCode > 100) && (statusCode < 200)) {

                if (method.equals(SipConstants.INVITE)) {
                    sipPhone.getCallControl().sendInviteProvisionalResponse(realCallId,
                            statusCode, content, headers);
                } else {
                    throw new SipClientStackException(
                            "SipActor.sendResponse: error can not sent provisional response for method: "
                                    + method);
                }
            } else if ((statusCode >= 200) && (statusCode < 300)) {

                if (method.equals(SipConstants.INVITE)) {
                    sipPhone.getCallControl().sendInviteSuccessResponse(realCallId, statusCode,
                            content, headers);
                } else if (method.equals(SipConstants.BYE)) {
                    sipPhone.getCallControl().sendByeSuccessResponse(realCallId, statusCode,
                            headers);
                } else if (method.equals(SipConstants.MESSAGE)) {
                    sipPhone.getCallControl().sendMessageSuccessResponse(realCallId, statusCode,
                            content, headers);
                } else if (method.equals(SipConstants.OPTIONS)) {
                    sipPhone.getCallControl().sendOptionsSuccessResponse(realCallId, statusCode,
                            content, headers);
                } else if (method.equals(SipConstants.UPDATE)) {
                    sipPhone.getCallControl().sendUpdateSuccessResponse(realCallId, statusCode,
                            content, headers);
                } else if (method.equals(SipConstants.NOTIFY)) {
                    sipPhone.getCallControl().sendNotifySuccessResponse(realCallId, statusCode,
                            content, headers);
                } else if (method.equals(SipConstants.PRACK)) {
                    sipPhone.getCallControl().sendPrackSuccessResponse(realCallId, statusCode,
                            content, headers);
                } else if (method.equals(SipConstants.CANCEL)) {
                    sipPhone.getCallControl().sendCancelSuccessResponse(realCallId, statusCode,
                            headers);
                } else if (method.equals(SipConstants.INFO)) {
                    sipPhone.getCallControl().sendInfoSuccessResponse(realCallId, statusCode,
                            content, headers);
                } else if (method.equals(SipConstants.REFER)) {
                    sipPhone.getCallControl().sendReferSuccessResponse(realCallId, statusCode,
                            content, headers);
                } else {
                    throw new SipClientStackException(
                            "SipActor.sendResponset : error undefined method: "
                                    + method);
                }
            } else if ((statusCode >= 300) && (statusCode < 400)) {

                if (!method.equals(SipConstants.INVITE)) {
                    throw new SipClientStackException(
                            "SipActor.sendResponse : error can not sent redirect response for method: "
                                    + method);
                }
                sipPhone.getCallControl().sendInviteRedirectedResponse(realCallId, statusCode,
                        content, headers);
            } else if (statusCode > 400) {

                if (method.equals(SipConstants.INVITE)) {
                    sipPhone.getCallControl().sendInviteErrorResponse(realCallId, statusCode,
                            headers);
                } else if (method.equals(SipConstants.BYE)) {
                    sipPhone.getCallControl().sendByeErrorResponse(realCallId, statusCode,
                            headers);
                } else if (method.equals(SipConstants.MESSAGE)) {
                    sipPhone.getCallControl().sendMessageErrorResponse(realCallId, statusCode,
                            headers);
                } else if (method.equals(SipConstants.OPTIONS)) {
                    sipPhone.getCallControl().sendOptionsErrorResponse(realCallId, statusCode,
                            headers);
                } else if (method.equals(SipConstants.UPDATE)) {
                    sipPhone.getCallControl().sendUpdateErrorResponse(realCallId, statusCode,
                            headers);
                } else if (method.equals(SipConstants.NOTIFY)) {
                    sipPhone.getCallControl().sendNotifyErrorResponse(realCallId, statusCode,
                            headers);
                } else if (method.equals(SipConstants.PRACK)) {
                    sipPhone.getCallControl().sendPrackErrorResponse(realCallId, statusCode,
                            headers);
                } else if (method.equals(SipConstants.CANCEL)) {
                    sipPhone.getCallControl().sendCancelErrorResponse(realCallId, statusCode,
                            headers);
                } else if (method.equals(SipConstants.INFO)) {
                    sipPhone.getCallControl().sendInfoErrorResponse(realCallId, statusCode,
                            headers);
                } else if (method.equals(SipConstants.REFER)) {
                    sipPhone.getCallControl().sendReferErrorResponse(realCallId, statusCode,
                            headers);
                } else {
                    throw new SipClientStackException(
                            "SipActor.sendResponse : error undefined method: "
                                    + method);
                }
            }

        } catch (SipClientStackException e) {
            logger.error("SipActor.sendResponse SipClientStackException: ",e);
            throw new SipClientStackException(
                    "SipActor.sendResponse error: ",e);

        } catch (Throwable e) {
            logger.error("SipActor.sendResponse Throwable: ",e);
            throw new SipClientStackException(
                    "SipActor.sendResponse error: ",e);
        }
    }

    /**
     * Get the CallControl
     * 
     * @return
     */
    public CallControl getCallControl() {
        return sipPhone.getCallControl();
    }

    public SipClientUAListener getListener() {
        return listener;
    }

    /**
     * Set the Sip Client UA Listener
     * 
     * @param listener
     *            Specifies the Sip Client UA Listener
     */
    public void setListener(SipClientUAListener listener) {
        this.listener = listener;
    }

    /**
     * Get the Current call ID. Helpful for incoming Invite => the Current call
     * ID will be the Key To get the Call-Id on Incoming Invites message.
     * 
     * @return the Current call ID.
     */
    public String getCurrentCallID() {
        return currentCallID;
    }

    /**
     * Get the Sip Call Id (Header Call-Id value) giving Scenario Sip Call Id of
     * the Sip Client
     * 
     * @param theoricId
     *            Specifies the scenario Sip Call Id.
     * @return the Sip Call Id (Header Call-Id value) giving Call If of the Sip
     *         Client
     * @throws SipClientStackException
     */
    public String getSipCallIdWiththeoricId(String theoricId)
            throws SipClientStackException {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.getSipCallIdWiththeoricId: "
                    + theoricId);
        String realCallId = null;
        if (theoricId != null) {

            realCallId = callsMap.getRealId(theoricId);

            if (realCallId == null) {
                if (currentCallID != null) {
                    // Check if the Response to send is on an Incoming Initial
                    // Invite

                    realCallId = callsMap.getRealId(currentCallID);
                    if (realCallId != null) {
                        // Change the key to the theoricId
                        callsMap.removeMapWithTheoricId(currentCallID);
                        callsMap.addMap(theoricId, realCallId);
                    } else {
                        throw new SipClientStackException(
                                "SipActor.getSipCallIdWiththeoricId error: Can not find a SipClient Call with scenario sip Call Id: "
                                        + theoricId);
                    }
                } else {
                    throw new SipClientStackException(
                            "SipActor.getSipCallIdWiththeoricId error: Can not find a SipClient Call with scenario sip Call Id: "
                                    + theoricId);
                }
            }

        }
        return realCallId;
    }

    /**
     * Check if the Sip Call-ID has been already mapped or not
     * 
     * @param realCallId
     *            Specifies the Sip Call ID
     */
    private void checkIfNewSipDialog(String realCallId, boolean isInviteMessage) {
        if (logger.isTraceEnabled())
            logger.trace("SipActor.checkIfNewSipDialog: Call ID: "
                    + realCallId);
        
        if (callsMap.getTheoric(realCallId) == null) {
            if (logger.isTraceEnabled())
                logger.trace("SipActor.checkIfNewSipDialog: Call ID: "
                        + realCallId + " is a new Sip Dialog");
            this.currentCallID = realCallId;
            if (isInviteMessage) {
                try {
                    String lastContactUriReceived = sipPhone.getCallControl()
                            .getContactHeaderReceived(realCallId)
                            .getHeaderValue();
                    if (lastContactUriReceived != null) {
                        setLastContactUriReceived(sipPhone.getCallControl()
                                .getContactHeaderReceived(realCallId)
                                .getHeaderValue());
                    }
                } catch (SipClientStackException e) {
                    logger.warn("SipActor.checkIfNewSipDialog SipClientStackException when calling sipPhone.getCallControl().getContactHeaderReceived method\n"
                            + e.getMessage());
                }
            }
            callsMap.addMap(this.currentCallID, realCallId);
            
        }

    }

    /**
     * Set the Sip Client UA Listener
     * 
     * @param listener
     *            Specifies the Sip Client UA Listener
     */
    public void removeListener() {
        this.listener = null;
    }

    /**
     * Checks if this client UA is Used
     * 
     * @return true f this client UA is Used
     */
    public boolean isUsed() {
        return used;
    }

    /**
     * Set if this client UA is Used
     * 
     * @param used
     *            true if used
     */
    public void setUsed(boolean used) {
        this.used = used;
        if (used == false) {
            if (logger.isTraceEnabled())
                logger.trace("SipActor.setUsed:SipClientUA: "
                        + sipPhone.getSipProfile().getProfileName()
                        + " has been released by a SipClientTest");
            // Clear all
            removeListener();
            callsMap.clear();
            currentCallID = null;
            sipUAEvents.clear();
            lastContactUriReceived = null;
        } else {
            if (logger.isTraceEnabled())
                logger.trace("SipActor.setUsed:SipClientUA: "
                        + sipPhone.getSipProfile().getProfileName()
                        + " is now used by a SipClientTest");
        }
    }

    /**
     * Get the Last contact uri received on an Initial INVITE request or in a
     * Initial INVITE final response. Today we use it only to make a convergence
     * between an INVITE session and a Subscribe session.
     * 
     * @return the Last contact uri received on an Initial INVITE request or in
     *         a Initial INVITE final response.
     */
    public String getLastContactUriReceived() {
        return lastContactUriReceived;
    }

    /**
     * Set the Last contact uri received on an Initial INVITE request or in a
     * Initial INVITE final response. Today we use it only to make a convergence
     * between an INVITE session and a Subscribe session.
     * 
     * @param lastContactUriReceived
     *            the Last contact uri received on an Initial INVITE request or
     *            in a Initial INVITE final response.
     */
    public void setLastContactUriReceived(String lastContactUriReceived) {
        this.lastContactUriReceived = lastContactUriReceived;
    }
}
