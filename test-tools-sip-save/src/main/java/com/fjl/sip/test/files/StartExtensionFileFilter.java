package com.fjl.sip.test.files;

import java.io.File;
import java.io.FileFilter;

import org.apache.log4j.Logger;

/**
 * 
 * This StartExtensionFileFilter filters Files with a Starts String and and extension.
 * 
 * @author François-Joseph LEVEE
 * 
 * @version G0R0C0
 * @since G0R0C0
 */
public class StartExtensionFileFilter implements FileFilter
{
  private Logger logger = Logger
      .getLogger( com.fjl.sip.test.files.StartExtensionFileFilter.class);

  /**
   * Start with filter String
   */
  String startWithString;

  /**
   * Extensions file filter
   */
  String[] extensions;

  /**
   * 
   * Constructor of StartExtensionFileFilter.
   * 
   * @param description
   * @param startWithString
   * @param extensions
   */
  public StartExtensionFileFilter ( String startWithString, String[] extensions)
  {
    this.startWithString = startWithString;
    this.extensions = extensions;
  }

  /**
   * 
   * @see java.io.FileFilter#accept(java.io.File)
   */
  public boolean accept ( File file)
  {
    String fileName = file.getName();
    if(file.isDirectory()) return true;
    if ((startWithString != null) && (!startWithString.isEmpty())&&(!fileName.startsWith( startWithString)))
    {
      return false;
    }
    if (extensions != null)
    {
      for (int i = 0; i < extensions.length; i++)
      {
        if (fileName.endsWith( extensions[i]))
        {
          return true;
        }
      }
    }

    return false;
  }


}
