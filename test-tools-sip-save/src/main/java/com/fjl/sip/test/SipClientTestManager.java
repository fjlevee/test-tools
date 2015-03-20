package com.fjl.sip.test;

import static com.fjl.sip.test.SipClientTesterConstants.LIST_UNIQUE_ID_START;
import static com.fjl.sip.test.SipClientTesterConstants.SIMPLE_TEST_UNIQUE_ID_START;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.fjl.sip.call.SipClientStackException;
import com.fjl.sip.test.configuration.SipProfileConfigurationException;
import com.fjl.sip.test.configuration.SipTestProfileConfiguration;
import com.fjl.sip.test.configuration.SipTestProfilesManager;

import fj_levee.document.generator.DocumentGeneratorListener;
import fj_levee.document.generator.PdfTestPlanDocumentGenerator;
import fj_levee.test.files.XmlCallFlowFile;
import fj_levee.test.files.XmlCallFlowList;
import fj_levee.test.files.XmlCallFlowListParser;
import fj_levee.test.files.XmlCallFlowListParserException;
import fj_levee.test.parser.SipClientParserException;
import fj_levee.test.parser.SipTestCallFlow;
import fj_levee.test.parser.SipTestCallFlowParser;
import fj_levee.test.report.SipClientTestListReport;

/**
 * 
 * This class SipClientTestManager ...
 * 
 * @author FJ. LEVEE (ofli8276)
 * 
 * @version G0R1C0
 * @since G0R1C0
 */
public class SipClientTestManager
{
  /**
   * Log4j logger
   */
  private Logger logger = Logger.getLogger( com.fjl.sip.test.SipClientTestManager.class);

  /**
   * SipClientTestManager instance
   */
  private static SipClientTestManager instance;

  /**
   * SipTestCallFlowParser
   */
  private SipTestCallFlowParser parser;

  /**
   * Unique Id for a Test
   */
  private int uniqueId = 0;

  /**
   * List of Sip Client Test ready to be executed
   */
  private HashMap<String, SipClientTest> sipClientTests;

  /**
   * List of Sip Client Test List ready to be executed
   */
  private HashMap<String, SipClientTestList> sipClientTestsLists;

  /**
   * Current Sip Server Profile Configuration Used
   */
  private String sipTestProfileConfigurationName;

  /**
   * Current Sip Server Profiles Configuration used
   */
  private SipTestProfileConfiguration sipServerProfileConfiguration;

  /**
   * SipServerProfilesManager instance
   */
  SipTestProfilesManager sipServerProfilesManager;

  /**
   * Get the SipClientTestManager singleton
   * 
   * @param sipClientTesterInstallationPath
   * @param sipTestProfileConfigurationName
   * @return the SipClientTestManager singleton
   */
  public static SipClientTestManager getSipClientTestManager (
      String sipTestProfileConfigurationName)
  {
    if (instance == null)
    {
      instance = new SipClientTestManager( sipTestProfileConfigurationName);
    }
    return instance;
  }

  /**
   * Get the SipClientTestManager singleton
   * 
   * @return the SipClientTestManager singleton
   */
  public static SipClientTestManager getInstance ()
  {

    return instance;
  }

  /**
   * 
   * Constructor of SipClientTestManager.
   * 
   * @param executionAbsolutePath Specifies the absolute execution path in case of Start in other place than exec
   *          folder.
   * @param sipTestProfileConfigurationName Specifies the Sip Test Profile Configuration Name
   */
  private SipClientTestManager ( String sipTestProfileConfigurationName)
  {
    sipClientTests = new HashMap<String, SipClientTest>();
    sipClientTestsLists = new HashMap<String, SipClientTestList>();
    this.sipTestProfileConfigurationName = sipTestProfileConfigurationName;
    if (logger.isDebugEnabled())
    {
      logger.debug( "SipClientTestManager created, name: " + getTestLauncherName() + ", description: "
          + getTestLauncherDescription());
    }
    try
    {
      if ((sipTestProfileConfigurationName == null) || (sipTestProfileConfigurationName.equals( "")))
        this.sipTestProfileConfigurationName = SipClientTestManagerConfiguration
            .getStringParameter( SipClientTestManagerConfiguration.DEFAULT_SIP_SERVER_CONFIGURATION_USED);
      if (logger.isDebugEnabled())
      {
        logger.debug( "SipClientTestManager constructor: sip server configuration used: "
            + this.sipTestProfileConfigurationName);
      }

      this.sipServerProfileConfiguration = SipTestProfilesManager.getSipServerProfileConfiguration(
           this.sipTestProfileConfigurationName);
      
      // Init the SipClient UAs
      initSipClientUAs();
      
      parser = SipTestCallFlowParser.getInstance();

    }
    catch (SipProfileConfigurationException e)
    {
      logger.error( "SipClientTestManager constructor SipProfileConfigurationException Error: " + e.getMessage());

    }
    catch (SipClientStackException e)
    {
      logger.error( "SipClientTestManager constructor SipClientStackException Error: " + e.getMessage());

    }
    catch (SipClientParserException e)
    {
      logger.error( "SipClientTestManager constructor SipTestCallFlowParserException Error: " + e.getMessage());

    }
    catch (Throwable e)
    {
      logger.error( "SipClientTestManager constructor Throwable Error: " + e.getMessage());
    }

  }

  /**
   * Init the Sip Client UAS
   * 
   */
  private void initSipClientUAs () throws SipClientStackException
  {
    SipClientUAManager.getInstance().loadSipClientUAs( sipServerProfileConfiguration);

  }
  
  /**
   * Register the Sip Client UAS
   * 
   */
  public boolean registerSipClientUAs () throws SipClientStackException
  {
    return SipClientUAManager.getInstance().registerSipClientUAs();

  }
  

  /**
   * Get the Name of this implementation.
   * 
   * @return the Name of this implementation
   */
  public String getTestLauncherName ()
  {
    return "SipClientTestManager";
  }

  /**
   * Get the Description of this implementation.
   * 
   * @return the description of this implementation
   */
  public String getTestLauncherDescription ()
  {
    return "FJL Sip Client Test Launcher";
  }

  /**
   * Method use to Launch the TestLauncher.
   * 
   * @param testFile Specifies the Test file or Folder (containing tests files) to be executed.
   * @param recursive Specifies, in case of testFile is a Folder, if the TestLauncher has to search recursively for Test
   *          Files in the Folder.
   * @param sipServerIpAddress specifies the IP Address of the Sip Server.
   * @param sipServerIpPort specifies the IP Port of the Sip Server.
   * @param httpServerUrl specifies the Url of the Http Server.
   * @param httpServerPort specifies the port of the Http Server.
   */
  public void executeTest ( String testFile, boolean recursive, String sipServerIpAddress, int sipServerIpPort,
      String httpServerUrl, int httpServerPort)
  {

    if (logger.isDebugEnabled())
    {
      StringBuffer buffer = new StringBuffer();
      buffer.append( "\n*********** " + getTestLauncherName() + ".executeTest ***********\n");
      buffer.append( "\n > File Name: " + testFile);
      buffer.append( "\n > Rescursive Search: " + recursive);
      buffer.append( "\n > Sip Server IP Address: " + sipServerIpAddress);
      buffer.append( "\n > Sip Server IP Port: " + sipServerIpPort);
      buffer.append( "\n > Http Server Url: " + httpServerUrl);
      buffer.append( "\n > Http Server Port: " + httpServerPort);
      buffer.append( "\n*************************************************************\n");
      logger.debug( buffer);
      try
      {
        Thread.sleep( 2000);
      }
      catch (InterruptedException e)
      {
        logger.error( "TestLauncherImp.executeTest InterruptedException: " + e.getMessage());
      }
      if (logger.isDebugEnabled()) logger.debug( "TestLauncherImp.executeTest: TestLauncher End.");
    }

  }

  /**
   * Load a test
   * 
   * @param testFileName Specifies the file containing the call flow test
   * @return a Unique identifier for the Test beeing executed.
   */
  public String loadTest ( String testFileName) throws SipClientTestException
  {
    if (logger.isDebugEnabled()) logger.debug( "TestLauncherImp.loadTest: load test with file name: " + testFileName);
    try
    {
            
      SipTestCallFlow sipTestCallFlow = new SipTestCallFlow( testFileName);
      if (logger.isDebugEnabled())
      {
        logger.debug( "SipClientTestManager.loadTest: Test File Tested: " + testFileName);
        logger.debug( sipTestCallFlow.listSipTestActions());

        String testId = SIMPLE_TEST_UNIQUE_ID_START + getUniqueId();
        SipClientTestListReport sipClientTestListReport = new SipClientTestListReport();
        SipClientTest sipClientTest = new SipClientTest(sipClientTestListReport, sipTestCallFlow, testId);
        
        // Create the xmlCallFlowList for pdf doument
        XmlCallFlowFile xmlCallFlowFile = sipClientTest.getSipTestCallFlow().getXmlCallFlowFile();
        XmlCallFlowList xmlCallFlowList = new XmlCallFlowList();
        xmlCallFlowList.addXmlCallFlowFile( xmlCallFlowFile);
        xmlCallFlowFile.setParent( xmlCallFlowList);
        
        sipClientTests.put( testId, sipClientTest);
        return testId;

      }
    }
    catch (SipClientParserException e)
    {
      logger.error( "SipClientTestManager.loadTest SipTestCallFlowParserException: " + e.getMessage());
      throw new SipClientTestException( "SipClientTestManager.loadTest SipTestCallFlowParserException: "
          + e.getMessage());
    }
    catch (SipClientTestException e)
    {
      logger.error( "SipClientTestManager.loadTest SipClientTestException: " + e.getMessage());
      throw new SipClientTestException( "SipClientTestManager.loadTest SipTestCallFlowParserException: "
          + e.getMessage());
    }
    catch (Throwable e)
    {
      logger.error( "SipClientTestManager.loadTest Throwable: " + e.getMessage());
      throw new SipClientTestException( "SipClientTestManager.loadTest Throwable: " + e.getMessage());
    }
    return null;
  }

  /**
   * Load a test
   * 
   * @param listFileName Specifies the file containing the list of call flow to be executed
   * @return a Unique identifier for the Test beeing executed.
   */
  public String loadTestList ( String listFileName) throws SipClientTestException
  {
    if (logger.isDebugEnabled())
      logger.debug( "SipClientTestManager.loadTestList: load test with file name: " + listFileName);
    try
    {

      XmlCallFlowList xmlCallFlowList =XmlCallFlowListParser
          .parseXmlCallFlowListFile( listFileName);
      String listId = LIST_UNIQUE_ID_START + getUniqueId();
      SipClientTestList testList = new SipClientTestList( null,xmlCallFlowList, listId);
      sipClientTestsLists.put( listId, testList);
      return listId;
    }
    catch (XmlCallFlowListParserException e)
    {
      logger.error( "SipClientTestManager.loadTestList XmlCallFlowListParserException: " + e.getMessage());
      throw new SipClientTestException( "SipClientTestManager.loadTestList XmlCallFlowListParserException: "
          + e.getMessage());
    }

    catch (Throwable e)
    {
      logger.error( "SipClientTestManager.loadTestList Throwable: " + e.getMessage());
      throw new SipClientTestException( "SipClientTestManager.loadTestList Throwable: " + e.getMessage());
    }

  }

  /**
   * Execute a test
   * 
   * @param sipTestUID Specifies the Sip Test UID
   */
  public void executeTest ( String sipTestUID,DocumentGeneratorListener listener) throws SipClientTestException
  {
    if (logger.isDebugEnabled()) logger.debug( "SipClientTestManager.executeTest: execute test id: " + sipTestUID);
    try
    {
      SipClientTest clientTest = sipClientTests.get( sipTestUID);
      
      if (clientTest == null)
      {
        throw new SipClientTestException( "SipClientTestManager.executeTest Id " + sipTestUID
            + ". Can not find the Test id in the List of Tests");
      }
      clientTest.startTest();
      
      if (logger.isDebugEnabled()) logger.debug( "SipClientTestManager.executeTest: create the test report");
      PdfTestPlanDocumentGenerator generator = new PdfTestPlanDocumentGenerator( clientTest.getSipTestCallFlow().getXmlCallFlowFile().getParent(),
          "B2B Test Plan", "B2B Test Plan.pdf","C:\\temp" , "FJ. LEVEE",
          "AAS Advanced Application Servers", "SIRP/ASF", "For internal use only", listener,clientTest.getSipClientTestListReport());
      //clientTestList.getXmlCallFlowList().getFile().getParent()
      Thread thread = new Thread( generator);
      thread.start();
      thread.join();
      sipClientTests.remove( sipTestUID);
    }

    catch (Throwable e)
    {
      logger.error( "SipClientTestManager.executeTest Throwable: " + e.getMessage());
      throw new SipClientTestException( "SipClientTestManager.executeTest Id " + sipTestUID + " Throwable: "
          + e.getMessage());
    }

  }

  /**
   * Execute a test list
   * 
   * @param sipTestListUID Specifies the Sip Test List UID
   */
  public void executeTestList ( String sipTestListUID, DocumentGeneratorListener listener)
      throws SipClientTestException
  {
    if (logger.isDebugEnabled())
      logger.debug( "SipClientTestManager.executeTestList: execute test id: " + sipTestListUID);
    try
    {
      SipClientTestList clientTestList = sipClientTestsLists.get( sipTestListUID);
      if (clientTestList == null)
      {
        throw new SipClientTestException( "SipClientTestManager.executeTestList Id " + sipTestListUID
            + ". Can not find the Test List id in the List of Tests Lists ");
      }
      clientTestList.startTest();
      /*
       * @param xmlCallFlowList
       * 
       * @param documentName
       * 
       * @param generatedFileName
       * 
       * @param destinationFolderName
       * 
       * @param author
       * 
       * @param projectName
       * 
       * @param team
       * 
       * @param diffusion
       * 
       * @param listener
       */
      // Start the Document Generator

      if (logger.isDebugEnabled()) logger.debug( "SipClientTestManager.executeTestList: create the test report");
      PdfTestPlanDocumentGenerator generator = new PdfTestPlanDocumentGenerator( clientTestList.getXmlCallFlowList(),
          "B2B Test Plan", "B2B Test Plan.pdf","C:\\temp" , "FJ. LEVEE",
          "AAS Advanced Application Servers", "SIRP/ASF", "For internal use only", listener,clientTestList.getSipClientTestListReport());
      //clientTestList.getXmlCallFlowList().getFile().getParent()
      Thread thread = new Thread( generator);
      thread.start();

      thread.join();
      sipClientTestsLists.remove( sipTestListUID);
    }

    catch (Throwable e)
    {
      logger.error( "SipClientTestManager.executeTestList Throwable: " + e.getMessage());
      throw new SipClientTestException( "SipClientTestManager.executeTestList Id " + sipTestListUID + " Throwable: "
          + e.getMessage());
    }

  }

  /**
   * Get a Sip Client UA in the List of the Current ready Sip UA.
   * 
   * @param index
   * @return a Sip Client UA
   */
  public SipClientUA getSipClientUA ( int index)
  {

    return SipClientUAManager.getInstance().getSipClientUA( index);

  }

  /**
   * Get the Servet Configuration
   * 
   * @return
   */
  public SipTestProfileConfiguration getServerConfiguration ()
  {
    return sipServerProfileConfiguration;
  }

  /**
   * Get the Next unique ID return the Next unique ID
   */
  public synchronized int getUniqueId ()
  {
    return uniqueId++;
  }
}
