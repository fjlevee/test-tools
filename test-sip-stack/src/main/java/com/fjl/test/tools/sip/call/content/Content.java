
package com.fjl.test.tools.sip.call.content;

/**
 * 
 * This class Content specifies the Content exchanges in SIP
 * Messages. 
 *
 * @author Fran�ois-Joseph LEVEE (ofli8276)
 *
 */
public class Content
{


  /**
   * Content Type attribute
   */
  private String contentType;
  /**
   * Content Type attribute
   */
  private String contentSubType;
  
  /**
   * Content attribute
   */
  private Object content;
  
  /**
   * 
   * Constructor of Content.
   * @param contentType specifies the content type
   * @param contentSubType specifies the content subtype
   */  
  public Content(String contentType,String contentSubType){
    this.contentType = contentType;
    this.contentSubType = contentSubType;
    this.content = null;
  }
  /**
   * 
   * Constructor of Content.
   * @param contentType specifies the content type
    * @param contentSubType specifies the content subtype
   * @param content  specifies the content
   */  
  public Content(String contentType,String contentSubType,Object content){
    this.contentType = contentType;
    this.contentSubType = contentSubType;
    this.content = content;
  }
  
  /**
   * Get the content
   * @return the content (Object)
   */
  public Object getContent ()
  {
    return content;
  }
  /**
   * set the content
   * @param content specifies the content (Object)
   */
  public void setContent ( Object content)
  {
    this.content = content;
  }
  /**
   * Get the content type
   * @return the content type
   */
  public String getContentType ()
  {
    return contentType;
  }
  /**
   * set the content type
   * @param contentType specifies the content type
   */
  public void setContentType ( String contentType)
  {
    this.contentType = contentType;
  }
  /**
   * Get the content subtype
   * @return the content subtype
   */
  public String getContentSubType ()
  {
    return contentSubType;
  }
  /**
   * set the content type
   * @param contentSubType specifies the content subtype
   */
  public void setContentSubType ( String contentSubType)
  {
    this.contentSubType = contentSubType;
  }
}
