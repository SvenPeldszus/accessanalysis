package de.uni_hamburg.informatik.swt.accessanalysis.results;

import org.eclipse.swt.graphics.Image;

/**
 * Generates Image and Strings for a single analysis result element.
 */
public interface ResultFormatter
{
    /**
     * Returns the image of the element
     * 
     * @return Image An image that represents the java element the result is based on.
     */
    public Image image();
    
    /**
     * Returns the name of the element
     * 
     * @return String The name of the java element the result is based on.
     */
    public String name();
    
    /**
     * Returns the IGAT value
     * 
     * @return String The IGAT result, if the element can have one, else ""
     */
    public String igat();
    
    /**
     * Returns the IGAM value
     * 
     * @return String The IGAM result
     */
    public String igam();
    
    /**
     * Returns the minimal access modifier
     * 
     * @return String The minimal access result, if the element can have an access provider, else ""
     */
    public String minimalAccess();
    
    /**
     * Returns the actual access modifier
     * 
     * @return String The actual access modifier, if the element can have one, else ""
     */
    public String actualAccess();
    
    /**
     * Checks if the access should be highlighted
     * 
     * @return boolean true, if the actual is to high, else false
     */
    public boolean accessAlert();
}
