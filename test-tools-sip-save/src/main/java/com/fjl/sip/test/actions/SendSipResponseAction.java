package com.fjl.sip.test.actions;

import java.util.Vector;

import com.fjl.sip.SipConstants;
import com.fjl.sip.call.content.Content;
import com.fjl.sip.test.SipClientActor;
import com.fjl.sip.test.parser.elements.HeaderElement;
import com.fjl.sip.test.parser.elements.MessageElement;
import com.fjl.sip.test.parser.elements.SipResponseElement;
import com.fjl.sip.utils.SipHeader;

import fj_levee.test.parser.SipTestCallFlowUtils;
import fj_levee.test.report.SipClientTestReportEvent;
import fj_levee.test.report.SipClientTestReportEvent.ReportEventType;

public class SendSipResponseAction extends SipClientAction
{

  /**
   * Method Awaited
   */
  private String method;

  /**
   * status Awaited
   */
  private int statusCode;

  /**
   * Content To Send
   */
  private Content content;

  /**
   * List of Headers to be Added
   */
  private Vector<SipHeader> headers;
  
  /**
   * Contact Header helpful in case of redirect response
   */
  private SipHeader contactHeader=null;

  /**
   * 
   * Constructor of SendSipRequestAction.
   * 
   * @param sipClientActor
   * @param responseElement
   * @param timeToWait
   */
  public SendSipResponseAction ( SipClientActor sipClientActor, SipResponseElement responseElement)
  {
    super( sipClientActor, responseElement.getCallid(), responseElement.getSipTestActionId(), 0);
    this.method = responseElement.getMethod();
    this.statusCode = responseElement.getCode();
    headers = SipClientActionFactory.getHeaders( responseElement.getMessageElement());
    
    // Check if the Response code is a 3XX
    if((statusCode>=300)&&(statusCode<400)){
      HeaderElement contact = responseElement.getMessageElement().getHeader( SipConstants.CONTACT_HEADER);
      if (contact!=null){
        
        String actorId = SipTestCallFlowUtils.getUserIdentifier( contact.getCData());
        contactHeader = new SipHeader( SipConstants.CONTACT_HEADER, actorId);
      }
    }
    
    MessageElement message = responseElement.getMessageElement();

    if (message != null)
    {
      content = message.getContent();
    }

  }

  /**
   * 
   * @see com.fjl.sip.test.actions.SipClientAction#executeAction()
   */
  @Override
  public void executeAction ()
  {

    if (logger.isDebugEnabled()) logger.debug( "SendSipResponseAction.executeAction: Start");
     // Check if the Contact Header is not null
      if(contactHeader!=null){
        String actorId = contactHeader.getHeaderValue();
        SipClientActor contactActor = this.sipClientActor.getSipClientTest().getSipClientActor( actorId);
        if(contactActor!=null){
          String sipuri = contactActor.getSipClientUA().getSipPhone().getSipUri();
          if (logger.isDebugEnabled()) logger.debug( "SendSipResponseAction.executeAction: contact header will be set with sip uri: "+sipuri);
          contactHeader.setHeaderValue( sipuri);
        }
        else{
          // No sip client value found => Used a default sip uri
          String sipuri = "sip:"+actorId+"@"+"sip.fjl.default.domain:5060";
          if (logger.isDebugEnabled()) logger.debug( "SendSipResponseAction.executeAction: contact header will be set with a default sip uri: "+sipuri);
          contactHeader.setHeaderValue( sipuri);
        }
        headers.add( contactHeader);
      }
    try
    {

      if (logger.isDebugEnabled())
        logger.debug( "SendSipResponseAction.executeAction: Sip ClientActor: " + sipClientActor.getClientActorId()
            + ", Action Id: " + actionId + ", Time to wait:" + timeToWait);

      Thread.sleep( timeToWait);

      sipClientActor.getSipClientUA().sendResponse( scenarioSipCallId, method, statusCode, content, headers);

      if (logger.isDebugEnabled())
        logger.debug( "SendSipResponseAction.executeAction: Sip ClientActor: " + sipClientActor.getClientActorId()
            + ", Action Id: " + actionId + ", Action done");
      notifySipClientTest( new SipClientTestReportEvent( ReportEventType.ACTION_SUCCESS, actionId, sipClientActor
          .getClientActorId(), isLastAction(), "SendSipResponseAction " + method + " (" + statusCode + ")","OK"));

    }
    catch (InterruptedException e)
    {
      logger.warn( "SendSipResponseAction.executeAction: InterruptedException: " + e.getMessage());
      notifySipClientTest( new SipClientTestReportEvent( ReportEventType.ACTION_FAILED, actionId, sipClientActor
          .getClientActorId(), isLastAction(), "SendSipResponseAction " + method + " (" + statusCode + ")"
          ,"ERROR: InterruptedException: " + e.getMessage()));

    }
    catch (Throwable e)
    {
      logger.error( "SendSipResponseAction.executeAction ERROR: Sip ClientActor: " + sipClientActor.getClientActorId()
          + ", Action Id: " + actionId + ", Error:\n " + e.getMessage());

      notifySipClientTest( new SipClientTestReportEvent( ReportEventType.ACTION_FAILED, actionId, sipClientActor
          .getClientActorId(), isLastAction(), "SendSipResponseAction " + method + " (" + statusCode + ")"
          , "Error: " + e.getMessage()));
    }

  }

  /**
   * Get the Sip Status code sent.
   * 
   * @return the Status code sent.
   */
  public int getStatusCode ()
  {
    return statusCode;
  }

}
