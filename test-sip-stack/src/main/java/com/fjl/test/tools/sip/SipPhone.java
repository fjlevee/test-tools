package com.fjl.test.tools.sip;

import java.util.Date;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fjl.test.tools.sip.authentication.Nonce;
import com.fjl.test.tools.sip.call.CallControlImp;
import com.fjl.test.tools.sip.call.SipClientStackException;
import com.fjl.test.tools.sip.call.SipDialog;
import com.fjl.test.tools.sip.call.SipDialogList;
import com.fjl.test.tools.sip.call.SipLayer;
import com.fjl.test.tools.sip.call.state.SipDialogState;
import com.fjl.test.tools.sip.register.RegistrationHandler;
import com.fjl.test.tools.sip.register.RegistrationListener;

public class SipPhone {

    /**
     * List of SIP Calls managed by this Sip Phone
     */
    private SipDialogList sipDialogs;

    /**
     * registrationHandler
     */
    private RegistrationHandler registrationHandler;
    /**
   * 
   */
    private RegistrationListener registrationListener;

    /**
     * Slf4J logger
     */
    private Logger logger;

    /**
     * Sip Layer
     */
    private SipLayer sipLayer;

    /**
     * Sip Profile
     */
    private SipProfile sipProfile;

    private Random random = new Random((new Date()).getTime());

    /**
     * sipEventListener
     */
    private SipEventListener sipEventListener;

    /**
     * Call Control object
     */
    private CallControl callControl;

    /**
   * 
   */
    private Nonce registerNonce;

    /**
   * 
   */
    private Nonce nonce;

    /**
     * 
     * Constructor of SipPhone. ...
     * 
     * @param logger
     *            defines the logger used for traces
     * @param sipEventListener
     *            specifes the Sip Event Listener object.
     * @throws SipClientStackException
     */
    public SipPhone(SipEventListener sipEventListener) {

        logger = LoggerFactory.getLogger(com.fjl.test.tools.sip.SipPhone.class);
        logger.debug("SipPhone.constructor");

        sipDialogs = new SipDialogList();
        this.sipEventListener = sipEventListener;
        this.callControl = new CallControlImp(this);
        this.registrationHandler = null;
        try {

            SipDialogState.initCallStates();
        } catch (SipClientStackException e) {
            logger.error("SipPhone.constructor error: ", e);
        }
    }

    /**
     * Try to get a SipDialog with its call-id
     * 
     * @param callId
     *            defines the call-id.
     * @return the SipDialog if found, else null.
     */
    public SipDialog getSipDialog(String callId) {
        logger.trace("SipPhone.getSipDialog with callID: {}", callId);
        return sipDialogs.getSipDialog(callId);
    }

    /**
     * Create a new Sip Dialog. This methods is called by the Sip Layer when
     * receiving an initial INVITE.
     * 
     * @param sipDialog
     *            defines the Sip Dialog to add.
     */
    public void addSipDialog(SipDialog sipDialog) {
        logger.debug("SipPhone.addSipDialog with call ID: {}",
                sipDialog.getCallId());
        sipDialogs.add(sipDialog);
    }

    /**
     * Remove a Sip Dialog.
     * 
     * @param sipDialog
     *            defines the Sip Dialog to remove.
     */
    public void removeSipDialog(SipDialog sipDialog) {
        logger.debug("SipPhone.removeSipDialog with call ID: {}",
                sipDialog.getCallId());
        sipDialogs.remove(sipDialog.getCallId());
    }

    /**
     * This method returns a newly generated unique tag ID.
     * 
     * @return A String tag ID
     */
    public String generateNewTag() {
        int r = random.nextInt();
        r = (r < 0) ? 0 - r : r; // generate a positive number
        return Integer.toString(r);
    }

    public SipProfile getSipProfile() {
        return sipProfile;
    }

    public void setSipProfile(SipProfile sipProfile)
            throws SipClientStackException {
        // if(!this.sipConfiguration.getProfileName().equals(sipConfiguration.getProfileName())){

        if (sipLayer != null) {
            sipLayer.dispose();
        }
        this.sipProfile = sipProfile;
        try {
            sipLayer = new SipLayer(this, logger, sipProfile);
        } catch (SipClientStackException e) {
            throw new SipClientStackException("SipPhone.setSipProfile: error: "
                    + e.getMessage());
        }

        // }
    }

    /**
     * Get the Sip Layer used by this Sip Phone
     * 
     * @return the Sip Layer used by the Sip Phone
     */
    public SipLayer getSipLayer() {
        return sipLayer;
    }

    /**
     * getRegisterNonce Increment the nonce count
     * 
     * @return the Register Nonce
     */
    public Nonce useRegisterNonce() {
        if (registerNonce == null)
            return null;
        registerNonce.incrementNonceCount();
        return registerNonce;
    }

    /**
     * updateRegisterNonce
     * 
     * @param nonceValue
     */
    public void updateRegisterNonce(String nonceValue) {
        if ((registerNonce != null)
                && (nonceValue.equals(registerNonce.getNonceValue()))) {
            // same nonce
            logger.debug("SipPhone.updateRegisterNonce: same nonce value => Nothing changed");
        } else {
            registerNonce = new Nonce(nonceValue);
        }
    }

    /**
     * getRegisterNonce Increment the nonce count
     * 
     * @return the Register Nonce
     */
    public boolean hasRegisterNonce() {
        return (registerNonce != null);
    }

    /**
     * updateNonce
     * 
     * @param nonceValue
     */
    public void updateNonce(String nonceValue) {
        if ((nonce != null) && (nonceValue.equals(nonce.getNonceValue()))) {
            // same nonce
            logger.debug("SipPhone.updateNonce: same nonce value => Nothing changed");
        } else {
            nonce = new Nonce(nonceValue);
        }

    }

    /**
     * getNonce Increment the nonce count
     * 
     * @return the Register Nonce
     */
    public boolean hasNonce() {
        return (nonce != null);
    }

    /**
     * getNonce Increment the nonce count
     * 
     * @return the Register Nonce
     */
    public Nonce useNonce() {
        if (nonce == null)
            return null;
        nonce.incrementNonceCount();
        return nonce;
    }

    /**
     * Get the SipDialog List
     * 
     * @returnc the SipDialog List
     */
    public SipDialogList getSipDialogList() {
        return sipDialogs;
    }

    /**
     * Get the SipPhone Sip Uri
     * 
     * @return
     */
    public String getSipUri() {
        return sipLayer.getContactAddress();

    }

    /**
     * Get if the SipPhone is Registered
     * 
     * @return
     */
    public boolean isRegistered() {
        logger.debug("SipPhone.isRegistered");
        if (registrationHandler == null) {
            return false;
        }
        return registrationHandler.isRegistered();
    }

    public void setRegistrationListener(
            RegistrationListener registrationListener) {
        this.registrationListener = registrationListener;
    }

    /**
     * Try to register or Unregister this SipPhone.
     * 
     */
    public void register() throws SipClientStackException {
        logger.debug("SipPhone.register");

        if (registrationHandler == null) {
            registrationHandler = new RegistrationHandler(this,
                    registrationListener);
        }

        registrationHandler.register(SipConstants.DEFAULT_EXPIRES_TIME);

    }

    /**
     * Try to register or Unregister this SipPhone.
     * 
     */
    public void unregister() throws SipClientStackException {
        logger.debug("SipPhone.unregister");

        if (registrationHandler == null) {
            registrationHandler = new RegistrationHandler(this,
                    registrationListener);

        }
        registrationHandler.unregister();

    }

    /**
     * Get the Sip Event Listener of this Sip Phone
     * 
     * @return the Sip Event Listener
     */
    public SipEventListener getSipEventListener() {
        return sipEventListener;
    }

    /**
     * Get the CallControl Object.
     * 
     * @return the CallControl.
     */
    public CallControl getCallControl() {
        return callControl;
    }

    public RegistrationHandler getRegistrationHandler() {
        return registrationHandler;
    }
}
