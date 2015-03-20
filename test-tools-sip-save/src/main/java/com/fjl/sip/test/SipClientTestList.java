package com.fjl.sip.test;

import static com.fjl.sip.test.SipClientTesterConstants.LIST_UNIQUE_ID_START;
import static com.fjl.sip.test.SipClientTesterConstants.SIMPLE_TEST_UNIQUE_ID_START;

import java.util.Vector;

import org.apache.log4j.Logger;

import fj_levee.test.files.XmlCallFlowFile;
import fj_levee.test.files.XmlCallFlowList;
import fj_levee.test.parser.SipClientParserException;
import fj_levee.test.parser.SipTestCallFlow;
import fj_levee.test.report.SipClientTestListReport;
public class SipClientTestList
{
  /**
   * Log4J logger
   */
  private Logger logger = Logger.getLogger( com.fjl.sip.test.SipClientTestList.class);

  /**
   * sip Client Test List unique Id
   */
  private String sipClientTestListId;

  /**
   * SipClientTestList List
   */
  private Vector<SipClientTestList> sipClientTestLists = new Vector<SipClientTestList>();
  
  /**
   * SipClientTest List
   */
  private Vector<SipClientTest> sipClientTests = new Vector<SipClientTest>();

  /**
   * XmlCallFlow List
   */
  private XmlCallFlowList xmlCallFlowList;
  
  /**
   * SipClientTestListReport
   */
  private SipClientTestListReport sipClientTestListReport;
  
  /**
   * Constructor
   * 
   * @param sipClientTestLauncher
   * @param sipClientTestListId
   * @throws SipClientTestException
   */
  public SipClientTestList (SipClientTestListReport sipClientTestListReportParent, XmlCallFlowList xmlCallFlowList, String sipClientTestListId) throws SipClientTestException
  {
    if (logger.isDebugEnabled())
      logger.debug( "SipClientTestList.constructor: for xmlCallFlowList: " + xmlCallFlowList.getName());
    this.sipClientTestListReport = new SipClientTestListReport();
    
    if(sipClientTestListReportParent!=null){
      sipClientTestListReportParent.addSipClientTestListReport(this.sipClientTestListReport);
    }
    
    
    this.sipClientTestListId = sipClientTestListId;
    this.xmlCallFlowList = xmlCallFlowList;
    for (int i = 0; i < xmlCallFlowList.getXmlCallFlowLists().size(); i++)
    {
      SipClientTestList sipClientTestList = new SipClientTestList(this.sipClientTestListReport, xmlCallFlowList.getXmlCallFlowLists().get( i),
          LIST_UNIQUE_ID_START + SipClientTestManager.getInstance().getUniqueId());
      this.sipClientTestLists.add( sipClientTestList);
    }
    for (int i = 0; i < xmlCallFlowList.getXmlCallFlowFiles().size(); i++)
    {
      XmlCallFlowFile xmlCallFlowFile = xmlCallFlowList.getXmlCallFlowFiles().get(i);
      try{
        
        if(xmlCallFlowFile.getFile().exists()){
          SipTestCallFlow sipTestCallFlow = new SipTestCallFlow( xmlCallFlowFile);
          SipClientTest sipClientTest = new SipClientTest(sipClientTestListReport,sipTestCallFlow,SIMPLE_TEST_UNIQUE_ID_START + SipClientTestManager.getInstance().getUniqueId());
          this.sipClientTests.add( sipClientTest);
        }
      }catch (SipClientTestException e) {
        logger.warn( "SipClientTestList.constructor: SipClientTestException for xmlCallFlowList: " + xmlCallFlowList.getName()+", can not create the SipClientTest for XmlCallFlowFile: "+xmlCallFlowFile.getFilename());
      
      }catch (SipClientParserException e) {
        logger.warn( "SipClientTestList.constructor: SipClientParserException for xmlCallFlowList: " + xmlCallFlowList.getName()+", can not create the SipClientTest for XmlCallFlowFile: "+xmlCallFlowFile.getFilename());
      }
    }
    
    if (logger.isDebugEnabled())
      logger.debug( "SipClientTestList.constructor: End for xmlCallFlowList: " + xmlCallFlowList.getName());
  }


  /**
   * startTest
   * 
   */
  public void startTest ()
  {
    if (logger.isDebugEnabled())
      logger.debug( "SipClientTestList.startTest: SipClientTestList name: " + xmlCallFlowList.getName());
    // First execute the lists of SipClientTestLists
    for(int i=0;i<sipClientTestLists.size();i++){
      sipClientTestLists.get(i).startTest();
    }
    // Then execute the lists of SipClientTests
    for(int i=0;i<sipClientTests.size();i++){
      sipClientTests.get(i).startTest();
    }
    if (logger.isDebugEnabled())
      logger.debug( "SipClientTestList.endTest: SipClientTestList name: " + xmlCallFlowList.getName());
  }

  /**
   * get the SipClientTest Id
   * 
   * @return the SipClientTest Id
   */
  public String getSipClientTestListId ()
  {
    return sipClientTestListId;
  }

  /**
   * Set the SipClientTest Id
   * 
   * @param sipClientTestListId Specifies the SipClientTestList Id
   */
  public void setSipClientTestListId ( String sipClientTestListId)
  {
    this.sipClientTestListId = sipClientTestListId;
  }
  

  /**
   * Get the XmlCallFlowList
   * @return the XmlCallFlowList
   */
  public XmlCallFlowList getXmlCallFlowList ()
  {
    return xmlCallFlowList;
  }
  
  /**
   * Get the SipClientTestListReport
   * @return the SipClientTestListReport
   */
  public SipClientTestListReport getSipClientTestListReport(){
    return sipClientTestListReport;
    
  }
}
