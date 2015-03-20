package com.fjl.test.tools.sip.call.state;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fjl.test.tools.sip.SipEventListener;
import com.fjl.test.tools.sip.call.SipClientStackException;
import com.fjl.test.tools.sip.call.SipDialog;
import com.fjl.test.tools.sip.call.content.Content;
import com.fjl.test.tools.sip.utils.SipHeader;

/**
 * This class SipDialogState is used to manage Sip Dialog States
 * 
 * @author FJ LEVEE
 */

public abstract class SipDialogState {
    /**
     * Prefix for Sip Dialog States properties.
     */
    public static final String CLASS_STATES_DEFINITION = "dialog_states.properties";

    /**
     * Prefix for Sip Dialog States properties.
     */
    //public static final String DIALOG_STATES_PROPERTY_PREFIX = "com.fjl.sip.call.states.";

    public enum DialogState {
        START, INVITING, INVITING_ANSWERED, INVITED, INVITED_ANSWERED, REINVITING, REINVITING_ANSWERED, REINVITED, IN_CALL, CLOSING, CLOSED, CANCELLING, NOT_INVITE_DIALOG, REGISTERING, UNREGISTERING, REGISTERED
    }

    /**
     * Table of State.
     */
    private static HashMap<DialogState, SipDialogState> stateMap;

    /**
     * Slf4J Logger
     */
    protected Logger logger = LoggerFactory
            .getLogger(com.fjl.test.tools.sip.call.state.SipDialogState.class);

    /**
     * Initialize stateMap by reading the dialog_states.properties file
     * 
     * @throws SipClientStackException
     *             if any error occurs during initialization.
     */
    public static void initCallStates() throws SipClientStackException {
        Properties statesConfiguration = new Properties();

        try {

            statesConfiguration.load(new FileInputStream(
                    CLASS_STATES_DEFINITION));
        } catch (FileNotFoundException e) {
            throw new SipClientStackException(
                    "SipDialogState.init ConfigurationException: "
                            + e.getMessage());
        } catch (IOException e) {
            throw new SipClientStackException(
                    "SipDialogState.init IOException: " + e.getMessage());
        }

        Iterator iterator = statesConfiguration.keySet().iterator();
        stateMap = new HashMap<DialogState, SipDialogState>();
        for (; iterator.hasNext();) {
            String stateName = (String) iterator.next();
            //String stateName = key.replace(DIALOG_STATES_PROPERTY_PREFIX, "");
            
            String stateClass = (String) statesConfiguration.getProperty(stateName);
            stateClass = stateClass.trim();
            SipDialogState state = null;

            try {
                Class classState = Class.forName(stateClass);
                Constructor constructor = classState.getConstructor();
                state = (SipDialogState) constructor.newInstance();

            } catch (InvocationTargetException e) {
                throw new SipClientStackException(
                        "SipDialogState.init Error getting object for State: "
                                + stateName + ", " + e.getMessage());
            } catch (InstantiationException e) {
                throw new SipClientStackException(
                        "SipDialogState.init Error getting object for State: "
                                + stateName + ", " + e.getMessage());
            } catch (IllegalAccessException e) {
                throw new SipClientStackException(
                        "SipDialogState.init Error getting object for State: "
                                + stateName + ", " + e.getMessage());
            } catch (NoSuchMethodException e) {
                throw new SipClientStackException(
                        "SipDialogState.init Error getting object for State: "
                                + stateName + ", " + e.getMessage());
            } catch (ClassNotFoundException e) {
                throw new SipClientStackException(
                        "SipDialogState.init Error getting object for State: "
                                + stateName + ", " + e.getMessage());
            }

            stateMap.put(DialogState.valueOf(stateName), state);

        } // end
    }

    /**
     * Return the SipDialogState object from the given state.
     * 
     * @param state
     *            the state.
     * @return the SipDialogState object.
     * @throws SipClientStackException
     *             if SipDialogState object was not found.
     */
    public static SipDialogState getStateObject(DialogState state)
            throws SipClientStackException {
        SipDialogState stateObject = stateMap.get(state);
        if (stateObject == null) {
            throw new SipClientStackException(
                    "SipDialogState.getStateObject: Error can not find void object for: "
                            + state.toString());
        }
        return stateObject;
    }

    /**
     * Handle an incoming INVITE. By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param requestEvent
     *            specifies the INVITE request
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * @throws SipClientStackException
     *             if any error occurs during void execution.
     */
    public void handleInvite(SipDialog sipDialog, RequestEvent requestEvent,
            SipEventListener sipEventListener) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleInvite not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Process a doAck request. By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param requestEvent
     *            specifies the ACK request
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */
    public void handleAck(SipDialog sipDialog, RequestEvent requestEvent,
            SipEventListener sipEventListener) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleAck not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Process an incoming Bye request. By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param requestEvent
     *            specifies the BYE request
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */
    public void handleBye(SipDialog sipDialog, RequestEvent requestEvent,
            SipEventListener sipEventListener) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleBye not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Process a Cancel request. Note: the response is sent by the container
     * itself and also the response to the invite if necessary. By default this
     * method will raise a SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param requestEvent
     *            specifies the CANCEL request
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */
    public void handleCancel(SipDialog sipDialog, RequestEvent requestEvent,
            SipEventListener sipEventListener) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleCancel not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Manage an incoming INFO request. By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param requestEvent
     *            specifies the INFO request
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs during void execution.
     */
    public void handleInfo(SipDialog sipDialog, RequestEvent requestEvent,
            SipEventListener sipEventListener) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleInfo not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Manage an incoming MESSAGE request. By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param requestEvent
     *            specifies the MESSAGE request
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs during void execution.
     */
    public void handleMessage(SipDialog sipDialog, RequestEvent requestEvent,
            SipEventListener sipEventListener) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleMessage not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Manage an incoming REFER request. By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param requestEvent
     *            specifies the REFER request
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * @throws SipClientStackException
     *             if any error occurs during void execution.
     */
    public void handleRefer(SipDialog sipDialog, RequestEvent requestEvent,
            SipEventListener sipEventListener) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleRefer not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Manage an incoming UPDATE request. By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param requestEvent
     *            specifies the UPDATE request
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs during void execution.
     */
    public void handleUpdate(SipDialog sipDialog, RequestEvent requestEvent,
            SipEventListener sipEventListener) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleUpdate not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Manage an incoming NOTIFY request. By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param requestEvent
     *            specifies the NOTIFY request
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs during void execution.
     */
    public void handleNotify(SipDialog sipDialog, RequestEvent requestEvent,
            SipEventListener sipEventListener) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleNotify not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Manage an incoming PRACK request. By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param requestEvent
     *            specifies the PRACK request
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs during void execution.
     */
    public void handlePrack(SipDialog sipDialog, RequestEvent requestEvent,
            SipEventListener sipEventListener) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handlePrack not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Manage an incoming OPTION request. By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param requestEvent
     *            specifies the OPTION request
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs during void execution.
     */
    public void handleOptions(SipDialog sipDialog, RequestEvent requestEvent,
            SipEventListener sipEventListener) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleOption not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Manage an incoming REGISTER request. By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param requestEvent
     *            specifies the REGISTER request
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * @throws SipClientStackException
     *             if any error occurs during void execution.
     */
    public void handleRegister(SipDialog sipDialog, RequestEvent requestEvent,
            SipEventListener sipEventListener) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleRegister not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Manage an incoming trying response. By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the INVITE Provisional response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs during void execution.
     */
    public void handleInviteTryingResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        /*
         * throw new SipClientStackException(
         * "CallState Error: method handleInviteTryingResponse not implemented in CallState: "
         * + getClass().getName());
         */
    }

    /**
     * Manage an incoming provisional response. By default this method will
     * raise a SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the INVITE Provisional response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs during void execution.
     */
    public void handleInviteProvisionalResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleProvisionalResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Manage an incoming redirect response. By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the INVITE Redirect response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs during void execution.
     */
    public void handleInviteRedirectResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleInviteRedirectResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Manage an incoming invite success response. By default this method will
     * raise a SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the INVITE SUCCESS response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs during void execution.
     */
    public void handleInviteSuccessResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleInviteSuccessResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Manage an incoming invite error response. By default this method will
     * raise a SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the INVITE error response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs during void execution.
     */
    public void handleInviteErrorResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleInviteErrorResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Handle a BYE Success Response By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the Success response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */
    public void handleByeSuccessResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleByeSuccessResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Process a BYE Error response By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the Error response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */

    public void handleByeErrorResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleByeErrorResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Handle a CANCEL Success Response By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the Success response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */
    public void handleCancelSuccessResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleCancelSuccessResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Process a BYE Error response By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the Error response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */

    public void handleCancelErrorResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleCancelErrorResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Handle a INFO Success Response By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the Success response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */
    public void handleInfoSuccessResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleInfoSuccessResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Process a INFO Error response By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the Error response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */

    public void handleInfoErrorResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleInfoErrorResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Handle a MESSAGE Success Response By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the Success response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */
    public void handleMessageSuccessResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleMessageSuccessResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Process a MESSAGE Error response By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the Error response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */

    public void handleMessageErrorResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleMessageErrorResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Handle a REFER Success Response By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the Success response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */
    public void handleReferSuccessResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleReferSuccessResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Handle a REGISTER Success Response By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the Success response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */
    public void handleRegisterSuccessResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleRegisterSuccessResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Process a SUBSCRIBE Error response By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the Error response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */

    public void handleSubscribeErrorResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleSubscribeErrorResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Handle a SUBSCRIBE Success Response By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the Success response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */
    public void handleSubscribeSuccessResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleSubscribeSuccessResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Process a REFER Error response By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the Error response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */

    public void handleReferErrorResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleReferErrorResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Handle a REGISTER Error Response By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the Error response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */
    public void handleRegisterErrorResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleRegisterErrorResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Handle a UPDATE Success Response By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the Success response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */
    public void handleUpdateSuccessResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleUpdateSuccessResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Process a UPDATE Error response By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the Error response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */

    public void handleUpdateErrorResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleUpdateErrorResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Handle a NOTIFY Success Response By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the Success response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */
    public void handleNotifySuccessResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleNotifySuccessResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Process a NOTIFY Error response By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the Error response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */

    public void handleNotifyErrorResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleNotifyErrorResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Handle a PRACK Success Response By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the Success response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */
    public void handlePrackSuccessResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handlePrackSuccessResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Process a PRACK Error response By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the Error response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */

    public void handlePrackErrorResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handlePrackErrorResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Handle a OPTIONS Success Response By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the Success response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */
    public void handleOptionsSuccessResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleOptionSuccessResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Process a OPTIONS Error response By default this method will raise a
     * SipClientStackException.
     * 
     * @param sipDialog
     *            specifies the SipDialog.
     * @param responseEvent
     *            specifies the Error response
     * @param sipEventListener
     *            specifies the Sip Event Listener
     * 
     * @throws SipClientStackException
     *             if any error occurs.
     */

    public void handleOptionsErrorResponse(SipDialog sipDialog,
            ResponseEvent responseEvent, SipEventListener sipEventListener)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method handleOptionErrorResponse not implemented in CallState: "
                        + getClass().getName());
    }

    // ///////////////// SEND SIP MESSAGES METHODS ///////////////////

    /**
     * 
     * Send an INVITE
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param toSipUri
     *            specifies the To Sip URI
     * @param content
     *            specifies the Content to add in the INVITE.
     * @param headers
     *            specifies the Headers to be added.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendInvite(SipDialog sipDialog, String toSipUri,
            Content content, Vector<SipHeader> headers, boolean authenticated)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendInvite not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * 
     * Send an OPTIONS message by creating a new Sip Call
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param toSipUri
     *            specifies the To Sip URI
     * @param content
     *            specifies the Content to add in the OPTIONS.
     * @param headers
     *            specifies the Headers to be added.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendOptions(SipDialog sipDialog, String toSipUri,
            Content content, Vector<SipHeader> headers, boolean authenticated)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendOptions not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * 
     * Send an MESSAGE message by creating a new Sip Call
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param toSipUri
     *            specifies the To Sip URI
     * @param content
     *            specifies the Content to add in the OPTIONS.
     * @param headers
     *            specifies the Headers to be added.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendMessage(SipDialog sipDialog, String toSipUri,
            Content content, Vector<SipHeader> headers, boolean authenticated)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendMessage not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * 
     * Send an REGISTER message by creating a new Sip Call
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param content
     *            specifies the Content to add in the REGISTER.
     * @param headers
     *            specifies the Headers to be added.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendRegister(SipDialog sipDialog, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendRegister not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Bye message
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param headers
     *            specifies the headers to be added in the BYE.
     * @return the Call-ID created
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendBye(SipDialog sipDialog, Vector<SipHeader> headers)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendBye not implemented in CallState: "
                        + getClass().getName());

    }

    /**
     * Send a Message message
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param content
     *            specifies the Content to add in the MESSAGE.
     * @param headers
     *            specifies the headers to be added in the MESSAGE.
     * @return the Call-ID created
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendMessage(SipDialog sipDialog, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendMessage not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Options message
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param content
     *            specifies the Content to add in the OPTIONS.
     * @param headers
     *            specifies the headers to be added in the OPTIONS.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendOptions(SipDialog sipDialog, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendOptions not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Update message
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param content
     *            specifies the Content to add in the UPDATE.
     * @param headers
     *            specifies the headers to be added in the UPDATE.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendUpdate(SipDialog sipDialog, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendUpdate not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Subscribe message
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param content
     *            specifies the Content to add in the SUBSCRIBE.
     * @param headers
     *            specifies the headers to be added in the SUBSCRIBE.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendSubscribe(SipDialog sipDialog, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendSubscribe not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * 
     * Send an SUBSCRIBE message by creating a new Sip Call
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param toSipUri
     *            specifies the To Sip URI
     * @param content
     *            specifies the Content to add in the SUBSCRIBE.
     * @param headers
     *            specifies the Headers to be added.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendSubscribe(SipDialog sipDialog, String toSipUri,
            Content content, Vector<SipHeader> headers, boolean authenticated)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendSubscribe not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Notify message
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param content
     *            specifies the Content to add in the NOTIFY.
     * @param headers
     *            specifies the headers to be added in the NOTIFY.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendNotify(SipDialog sipDialog, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendNotify not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Prack message
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param content
     *            specifies the Content to add in the PRACK.
     * @param headers
     *            specifies the headers to be added in the PRACK.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendPrack(SipDialog sipDialog, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendPrack not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Ack message
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param content
     *            specifies the Content to add in the ACK.
     * @param headers
     *            specifies the headers to be added in the ACK.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendAck(SipDialog sipDialog, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendAck not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Cancel message
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param headers
     *            specifies the headers to be added in the CANCEL.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendCancel(SipDialog sipDialog, Vector<SipHeader> headers)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendCancel not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Info message
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param content
     *            specifies the Content to add in the INFO.
     * @param headers
     *            specifies the headers to be added in the INFO.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendInfo(SipDialog sipDialog, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendInfo not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Refer message
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param content
     *            specifies the Content to add in the REFER.
     * @param headers
     *            specifies the headers to be added in the REFER.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendRefer(SipDialog sipDialog, Content content,
            Vector<SipHeader> headers) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendRefer not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a INVITE provisional Response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param content
     *            specifies the Content to add in the INVITE response.
     * @param headers
     *            specifies the headers to be added in the INVITE response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendInviteProvisionalResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendInviteProvionalResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a INVITE redirected Response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param content
     *            specifies the Content to add in the INVITE response.
     * @param headers
     *            specifies the headers to be added in the INVITE response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendInviteRedirectedResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendInviteRedirectedResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a INVITE success Response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param content
     *            specifies the Content to add in the INVITE response.
     * @param headers
     *            specifies the headers to be added in the INVITE response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendInviteSuccessResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendInviteSuccessResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a INVITE error Response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the INVITE response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendInviteErrorResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendInviteErrorResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Bye success response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the BYE response.
     * @return the Call-ID created
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendByeSuccessResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendByeSuccessResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Bye error response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the BYE response.
     * @return the Call-ID created
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendByeErrorResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendByeSuccessErrorResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Message success response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param content
     *            specifies the Content to add in the MESSAGE response.
     * @param headers
     *            specifies the headers to be added in the MESSAGE response.
     * @return the Call-ID created
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendMessageSuccessResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendMessageSuccessResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Message error response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the MESSAGE response.
     * @return the Call-ID created
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendMessageErrorResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendMessageErrorResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Options success response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param content
     *            specifies the Content to add in the OPTIONS response.
     * @param headers
     *            specifies the headers to be added in the OPTIONS response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendOptionsSuccessResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendOptionsSuccessResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Options error response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the OPTIONS response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendOptionsErrorResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendOptionsErrorResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Update success response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param content
     *            specifies the Content to add in the UPDATE response.
     * @param headers
     *            specifies the headers to be added in the UPDATE response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendUpdateSuccessResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendUpdateSuccessResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Update error response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the UPDATE response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendUpdateErrorResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendUpdateErrorResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Notify success Response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param content
     *            specifies the Content to add in the NOTIFY response.
     * @param headers
     *            specifies the headers to be added in the NOTIFY response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendNotifySuccessResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendNotifySuccessResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Notify error Response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the NOTIFY response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendNotifyErrorResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendNotifyErrorResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Prack success response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param content
     *            specifies the Content to add in the PRACK response.
     * @param headers
     *            specifies the headers to be added in the PRACK response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendPrackSuccessResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendPrackSuccessResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Prack error response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the PRACK response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendPrackErrorResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendPrackErrorResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Cancel success response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the CANCEL response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendCancelSuccessResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendCancelSuccessResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Cancel error response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the CANCEL response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendCancelErrorResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendCancelErrorResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Info success response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param content
     *            specifies the Content to add in the INFO response.
     * @param headers
     *            specifies the headers to be added in the INFO response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendInfoSuccessResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendInfoSuccessResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Info error response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the INFO response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendInfoErrorResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendInfoErrorResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Refer success response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param content
     *            specifies the Content to add in the REFER response.
     * @param headers
     *            specifies the headers to be added in the REFER response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendReferSuccessResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Content content, Vector<SipHeader> headers)
            throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendReferSuccessResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Send a Refer error response
     * 
     * @param sipDialog
     *            specifies the SipDialog
     * @param serverTransaction
     *            specifies the Server Transaction
     * @param statusCode
     *            Specifies the status code of the response
     * @param headers
     *            specifies the headers to be added in the REFER response.
     * @throws SipClientStackException
     *             In case of error.
     */
    public void sendReferErrorResponse(SipDialog sipDialog,
            ServerTransaction serverTransaction, int statusCode,
            Vector<SipHeader> headers) throws SipClientStackException {
        throw new SipClientStackException(
                "CallState Error: method sendReferErrorResponse not implemented in CallState: "
                        + getClass().getName());
    }

    /**
     * Set the logger
     * 
     * @param logger
     *            specifies the logger.
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * Get the Name of This SipDialogState
     * 
     * @return
     */
    public static String getName() {
        return "Undefined";
    }

}
