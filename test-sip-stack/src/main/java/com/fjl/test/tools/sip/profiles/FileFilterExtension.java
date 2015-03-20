package com.fjl.test.tools.sip.profiles;

import java.io.File;
import java.io.FileFilter;
/**
 * 
 * This class FileFilterExtension is useful to filter files which extension 
 * checks a condition. 
 *
 * @author FJ LEVEE
 *
 */
public class FileFilterExtension implements FileFilter
{
  /**
   * Extension of the FileFilter
   */
  private String extension;
  /**
   * Accept Folders value
   */
  private boolean acceptFolders;
  /**
   * Description of this Folder
   */
  private String description;
  /**
   * 
   * Constructor of FileFilterExtension.
   * ...
   *
   * @param description specifies a description for this filter
   * @param extension specifies the extension of accepted file
   * @param acceptFolders defines if the folder or accepted by this filter
   */
  public FileFilterExtension(String description,String extension,boolean acceptFolders){
    this.description = description;
    this.extension = extension;
    this.acceptFolders = acceptFolders;
  }
  /**
   * Return true is this fileter accept this file
   * @see java.io.FileFilter#accept(java.io.File)
   */
  public boolean accept(File file){
    if((acceptFolders)&&(file.isDirectory())){ 
      return true; 
    } 
    String fileName = file.getName();
    if(fileName.endsWith(extension)){
         return true;
    }
    return false;
  }
  /**
   * Get the description of this FileFilter
   * @return the description of this FileFilter
   */
  public String getDescription(){
    return this.description;
  }
}
