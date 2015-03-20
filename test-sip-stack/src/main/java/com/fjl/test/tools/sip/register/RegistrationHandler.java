package com.fjl.test.tools.sip.register;

import java.util.Vector;

import javax.sip.ResponseEvent;
import javax.sip.header.AuthorizationHeader;
import javax.sip.header.Header;
import javax.sip.header.WWWAuthenticateHeader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fjl.test.tools.sip.SipConstants;
import com.fjl.test.tools.sip.SipEventListenerImp;
import com.fjl.test.tools.sip.SipPhone;
import com.fjl.test.tools.sip.call.SipClientStackException;
import com.fjl.test.tools.sip.call.SipDialog;
import com.fjl.test.tools.sip.call.state.SipDialogState.DialogState;
import com.fjl.test.tools.sip.utils.SipHeader;

public class RegistrationHandler extends SipEventListenerImp {

    /**
     * sipPhone
     */
    private SipPhone sipPhone;

    /**
     * registerDialog
     */
    private SipDialog registerDialog;

    /**
   * 
   */
    private long expiresTime;

    /**
   * 
   */
    private RegistrationListener registrationListener;

    /**
     * 
     * Constructor of RegistrationHandler.
     *
     * @param sipPhone
     */
    public RegistrationHandler(SipPhone sipPhone,
            RegistrationListener registrationListener)
            throws SipClientStackException {
        super(LoggerFactory
                .getLogger(com.fjl.test.tools.sip.register.RegistrationHandler.class));
        this.sipPhone = sipPhone;
        this.registrationListener = registrationListener;
    }

    /**
     * Get if the SipPhone is Registered
     * 
     * @return
     */
    public boolean isRegistered() {
        logger.debug("RegistrationHandler.isRegistered");
        if (registerDialog == null) {
            return false;
        }
        return (registerDialog.getSipDialogState() == DialogState.REGISTERED);

    }

    /**
     * 
     * @throws SipClientStackException
     */
    public void register(long expiresTime) throws SipClientStackException {
        logger.debug("RegistrationHandler.register");
        this.expiresTime = expiresTime;
        boolean add = false;
        if (registerDialog == null) {
            registerDialog = new SipDialog(sipPhone, this);
            add = true;

        }

        registerDialog.sendRegister(null, null, expiresTime);
        if (add) {
            sipPhone.addSipDialog(registerDialog);
        }

    }

    /**
     * 
     * @throws SipClientStackException
     */
    public void unregister() throws SipClientStackException {
        logger.debug("RegistrationHandler.unregister");
        this.expiresTime = 0;
        boolean add = false;
        if (registerDialog == null) {
            registerDialog = new SipDialog(sipPhone, this);
            add = true;
        }
        registerDialog.sendRegister(null, null, 0);
        if (add) {
            sipPhone.addSipDialog(registerDialog);
        }
    }

    @Override
    public void handleRegisterErrorResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "RegistrationHandler.handleRegisterErrorResponse: sipCallId: {}",
                sipCallId);
        // try{

        if (responseEvent.getResponse().getStatusCode() == SipConstants.RESPONSE_UNAUTHORIZED) {
            logger.debug("RegistrationHandler.handleRegisterErrorResponse: Authenticating");
        } else {
            if (registrationListener != null) {
                registrationListener.registrationFailure(responseEvent
                        .getResponse().getStatusCode(), responseEvent
                        .getResponse().getReasonPhrase());

            }

        }
        /*
         * }catch (SipClientStackException e) { if(registrationListener!=null){
         * registrationListener.registrationFailure(
         * responseEvent.getResponse().getStatusCode(), e.getMessage());
         * 
         * } }
         */

    }

    @Override
    public void handleRegisterSuccessResponse(String sipCallId,
            ResponseEvent responseEvent) {
        logger.debug(
                "SipEventListenerImp.handleRegisterSuccessResponse (Handled by default listener): sipCallId: {}",
                sipCallId);
        if (registrationListener != null) {
            registrationListener.registrationSuccess();

        }
    }

}