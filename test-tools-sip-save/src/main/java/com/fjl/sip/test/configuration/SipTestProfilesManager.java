package com.fjl.sip.test.configuration;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;

import com.fjl.sip.test.SipClientTestManagerConfiguration;
import com.fjl.sip.test.files.StartExtensionFileFilter;

import fj_levee.FJLToolsConfiguration;

/**
 * 
 * This class SipTestProfilesManager is reponsible for loading and created all sip server profiles configuration files
 * 
 * @author FJ LEVEE
 * 
 * @version G0R0C1
 * @since G0R0C1
 */
public class SipTestProfilesManager
{
  /**
   * Singleton
   */
  private static SipTestProfilesManager instance;

  /**
   * List of Sip Server Profile Configurations
   */
  private HashMap<String, SipTestProfileConfiguration> sipTestProfileConfigurations;

  /**
   * 
   * Constructor of SipTestProfilesManager.
   * @param executionAbsolutePath Specifies the absolute execution path in case of Start in other place than exec folder.
   * @throws SipProfileConfigurationException
   */
  private SipTestProfilesManager() throws SipProfileConfigurationException{
    
   
    
    sipTestProfileConfigurations = new HashMap<String, SipTestProfileConfiguration>();

    String configFolderPath = SipClientTestManagerConfiguration
        .getStringParameter( SipClientTestManagerConfiguration.SIP_SERVERS_CONFIGURATION_FOLDER);
    
    configFolderPath =  FJLToolsConfiguration.getFJLToolsConfigurationPath()+configFolderPath;
    
    File configFolder =new File(configFolderPath);
    
    if(!configFolder.exists()){
      throw new SipProfileConfigurationException("SipTestProfilesManager constructor error: Can not find a folder with path: "+configFolderPath); 
    }
    FileFilter filter = new StartExtensionFileFilter("TestProfile_",new String[]{".xml"});
    
    File[] serversConfigurationsFiles = configFolder.listFiles( filter);
    if(serversConfigurationsFiles==null){
      throw new SipProfileConfigurationException("SipTestProfilesManager constructor error: Can not find server configuration file in folder: "+configFolderPath); 
    }
    for(int i=0;i<serversConfigurationsFiles.length;i++){
      SipTestProfileConfiguration serverConfiguration = new SipTestProfileConfiguration(serversConfigurationsFiles[i].getPath());
      sipTestProfileConfigurations.put( serverConfiguration.getSipServerConfigurationName(),serverConfiguration);
      
      
    }
    

  }
 
  
  /**
   * get a SipServerProfileConfiguration 
   * @param executionAbsolutePath Specifies the absolute execution path in case of Start in other place than exec folder.
   * @param configurationName Specifies the name of the Configuration to get
   * @return the SipServerProfilesConfiguration if found
   * @throws SipProfileConfigurationException
   */
  public static SipTestProfileConfiguration getSipServerProfileConfiguration (String configurationName) throws SipProfileConfigurationException
  {
    if (instance == null)
    {
      instance = new SipTestProfilesManager();
    }
    return instance.sipTestProfileConfigurations.get( configurationName);

  }

}
