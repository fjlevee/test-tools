package com.fjl.sip.test.run;

import static com.fjl.sip.test.SipClientTesterConstants.CALLFLOW_START_STRING;

import org.apache.log4j.Logger;

import com.fjl.sip.test.SipClientTestException;
import com.fjl.sip.test.SipClientTestManager;

import fj_levee.FJLToolsConfiguration;
import fj_levee.callflow.xml.configuration.XmlElementsConfiguration;
import fj_levee.document.generator.DocumentGeneratorEvent;
import fj_levee.document.generator.DocumentGeneratorListener;
import fj_levee.gui.GuiConfiguration;
/**
 * 
 * This class SipTester is the main Class
 *
 * @author FJ. LEVEE (ofli8276)
 *
 * @version G0R1C0
 * @since G0R1C0
 */
public class SipTester implements DocumentGeneratorListener
{
  /**
   * Log4J Logger
   */
  private Logger logger = Logger.getLogger( com.fjl.sip.test.run.SipTester.class);

  /**
   * 
   */
  private SipClientTestManager testLauncher;

  /**
   * 
   * Constructor of SipTester_Test_01.
   * @param executionAbsolutePath Specifies the absolute execution path in case of Start in other place than exec folder. 
   * @param fileToBeExecuted Specifies the path of the file (list or simple file) to be executed
   */
  public SipTester (String sipClientTesterInstallationPath,String fileToBeExecuted,String testProfile)
  {

    try
    {
      if (logger.isDebugEnabled()) logger.debug( "SipTester.constructor: Start...");
      
      // Init the FJL Tools
      FJLToolsConfiguration.initFJLToolsConfiguration( sipClientTesterInstallationPath);
      
      // Init the XmlElementsConfiguration
      XmlElementsConfiguration.init();
      // Init the GuiConfiguration
      GuiConfiguration.initGuiConfiguration();
      testLauncher = SipClientTestManager.getSipClientTestManager(testProfile);
      
      // Check if the File to be executed is a Simple Test File or a List of test files
      if(fileToBeExecuted.contains( CALLFLOW_START_STRING)){
        if (logger.isDebugEnabled()) logger.debug( "SipTester.constructor: The type of execution is a Simple Test.");
        String testId = testLauncher.loadTest( fileToBeExecuted);
        boolean registerOK = testLauncher.registerSipClientUAs();
        if(!registerOK){
          logger.error( "SipTester.runTest error: the register are KO");
          throw new SipClientTestException( "SipTester.runTest error: the register are KO");
        }
        testLauncher.executeTest( testId,this);
      }
      else{
        if (logger.isDebugEnabled()) logger.debug( "SipTester.constructor: The type of execution is a List of Tests.");
        String testListId = testLauncher.loadTestList(  fileToBeExecuted);
        boolean registerOK = testLauncher.registerSipClientUAs();
        if(!registerOK){
          logger.error( "SipTester.runTest error: the register are KO");
          throw new SipClientTestException( "SipTester.runTest error: the register are KO");
        }
        testLauncher.executeTestList( testListId,this);
      }
      if (logger.isDebugEnabled()) logger.debug( "SipTester.constructor: End");
    }
    catch (Throwable e)
    {
      logger.error( "SipTester.runTest error: " + e.getMessage());
      logger.error( e);
    }
  }

  public void runTest ()
  {
    try
    {
      if (logger.isDebugEnabled()) logger.debug( "SipTester.runTest: Start...");

      if (logger.isDebugEnabled()) logger.debug( "SipTester.runTest: Stop");
    }
    catch (Throwable e)
    {
      logger.error( "SipTester.runTest error: " + e.getMessage());
      logger.error( e);
    }
  }

  /**
   * Main Method
   * 
   * @param args
   */
  public static void main ( String[] args)
  {
    System.out.println( "SipTester.Start args length is: "+ args.length);
    
    if (args.length == 1)
    {
      String callFlowPath = args[0];
      SipTester test = new SipTester("",callFlowPath,"");
      test.runTest();

      System.exit( 0);
      try
      {

      }
      catch (Throwable e)
      {
        System.err.println( "SipTester Error: " + e.getMessage());
        System.exit( -1);
      }
    }
    else if (args.length == 2)
    {
      String executionAbsolutePath = args[0];
      System.out.println( "SipTester.Start executionAbsolutePath: "+ executionAbsolutePath);
      String callFlowPath = args[1];
      System.out.println( "SipTester.Start callFlowPath: "+ callFlowPath);
      SipTester test = new SipTester(executionAbsolutePath,callFlowPath,"");
      test.runTest();

      System.exit( 0);
      try
      {

      }
      catch (Throwable e)
      {
        System.err.println( "SipTester Error: " + e.getMessage());
        System.exit( -1);
      }
    }else if (args.length == 3)
    {
      String executionAbsolutePath = args[0];
      System.out.println( "SipTester.Start executionAbsolutePath: "+ executionAbsolutePath);
      String callFlowPath = args[1];
      System.out.println( "SipTester.Start callFlowPath: "+ callFlowPath);
      String testProfile =  args[2];
      System.out.println( "SipTester.Start test profile: "+ testProfile);
      SipTester test = new SipTester(executionAbsolutePath,callFlowPath,testProfile);
      test.runTest();

      System.exit( 0);
      try
      {

      }
      catch (Throwable e)
      {
        System.err.println( "SipTester Error: " + e.getMessage());
        System.exit( -1);
      }
    }
    else
    {
      System.out.println( "SipTester use: [CallFlow_File_PATH]");
    }
    
  }
  /**
   * 
   * @see fj_levee.document.generator.DocumentGeneratorListener#documentGeneratorEventOccured(fj_levee.document.generator.DocumentGeneratorEvent)
   */
  @Override
  public void documentGeneratorEventOccured ( DocumentGeneratorEvent event)
  {
    if(logger.isDebugEnabled()) logger.debug( "SipTester.documentGeneratorEventOccured: event type: "+event.getEventType());
  }
}
