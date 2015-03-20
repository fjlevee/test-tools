package com.fjl.test.tools.sip.register;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fjl.test.tools.sip.SipConstants;
import com.fjl.test.tools.sip.SipPhone;
import com.fjl.test.tools.sip.call.SipClientStackException;

/**
 * 
 * This class RegistrationTask ...
 *
 * @author FJ. LEVEE (ofli8276)
 *
 * @version G0R0C0
 * @since G0R0C0
 */
public class RegistrationTask extends Thread implements RegistrationListener {
    /**
     * REGISTRATION_TASK_TIMER timeout
     */
    public static final long REGISTRATION_TASK_TIMER = 20000;
    /**
     * Sip Phone
     */
    private SipPhone sipPhone;
    /**
     * Logger
     */
    private Logger logger;
    /**
     * Specifies if the Sip Phone has to be registered (true) or unregistered
     * (false)
     */
    private boolean register;

    /**
     * registration Result Status Code
     */
    private int registrationStatusCode;

    /**
     * registrationListener
     */
    private RegistrationListener registrationListener;

    /**
     * 
     * Constructor of RegistrationTask.
     * 
     * @param register
     *            Specifies if the Sip Phone has to be registered (true) or
     *            unregistered (false)
     * @param sipPhone
     *            Specifies the Sip Phone to register or unregister
     */
    public RegistrationTask(boolean register, SipPhone sipPhone,
            RegistrationListener registrationListener) {

        this.logger = LoggerFactory
                .getLogger(com.fjl.test.tools.sip.register.RegistrationTask.class);
        this.sipPhone = sipPhone;
        this.registrationListener = registrationListener;
        this.register = register;
        this.registrationStatusCode = 0;

    }

    @Override
    public void run() {
        logger.debug("RegistrationTask.run start: register: {}", register);
        try {
            if (register) {
                sipPhone.register();
            } else {
                sipPhone.unregister();
            }
            try {
                sleep(REGISTRATION_TASK_TIMER);
            } catch (InterruptedException e) {
                logger.debug("RegistrationTask.run: InterruptedException");
            }
        } catch (SipClientStackException e) {
            logger.debug(
                    "RegistrationTask.run start: SipClientStackException: ", e);
        } catch (Exception e) {
            logger.debug("RegistrationTask.run start: Exception ({}): ", e
                    .getClass().getName(), e);
        }

    }

    public void waitForRegistration() {

    }

    @Override
    public void registrationFailure(int statusCode, String reason) {
        if (logger.isDebugEnabled())
            logger.debug(
                    "RegistrationTask.registrationFailure (event): statusCode: {}, reason: {}",
                    statusCode, reason);
        registrationStatusCode = statusCode;
        if (registrationListener != null) {
            registrationListener.registrationFailure(statusCode, reason);

        }
        this.notify();
    }

    @Override
    public void registrationSuccess() {
        logger.debug("RegistrationTask.registrationSuccess (event)");
        registrationStatusCode = SipConstants.RESPONSE_OK;
        if (registrationListener != null) {
            registrationListener.registrationSuccess();

        }
        this.notify();

    }

    public int getRegistrationStatusCode() {
        return registrationStatusCode;
    }

}
