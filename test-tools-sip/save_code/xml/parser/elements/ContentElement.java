package com.fjl.test.xml.parser.elements;

/**
 * 
 * This class ContentElement
 *
 * @author FJ. LEVEE.
 *
 * @version G0R0C1
 * @since G0R0C1
 */
public class ContentElement {

    /**
     * CDATA Section :<![CDATA[ SOME_DATA ]]>
     */
    private String cData;

    /**
     * 
     * Constructor of Content Element.
     * 
     * @param cdData
     */
    public ContentElement(String cdData) {
        this.cData = cdData;
    }

    /**
     * Get the CDATA content.
     * 
     * @return the CDATA content
     */
    public String getCData() {
        return cData;
    }

    /**
     * Set the CDATA content
     * 
     * @param cData
     *            specifies the new CDATA
     */
    public void setCData(String cData) {
        this.cData = cData;
    }

}