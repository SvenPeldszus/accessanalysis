/**
 * 
 */
package de.uni_hamburg.informatik.swt.accessanalysis.access;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.graphics.Image;

import de.uni_hamburg.informatik.swt.accessanalysis.internal.AccessAnalysisActivator;

/**
 * All possible access modes of Java elements
 */
public enum Access
{   
    NO_USE("not used", "icons/access_not_used.png"),
    PRIVATE("private", "icons/access_private.png"),
    DEFAULT("default", "icons/access_default.png"),
    PROTECTED("protected", "icons/access_protected.png"),
    PUBLIC("public", "icons/access_public.png");
    
    private final String _name;
    private final Image _image;
    
    /**
     * Initializes an object of Access. 
     *
     * @param String name The name of the access mode
     */
    private Access(String name, String imagePath)
    {
        _name = name;
        _image = AccessAnalysisActivator.imageDescriptorFromPlugin(imagePath).createImage();
    }
    
    /**
     * Returns an icon representing this access level
     * 
     * @return Image The Icon image
     */
    public Image getImage()
    {
        return _image;
    }
    
    /**
     * Returns a string representation
     * 
     * @return String The name of this access mode
     */
    @Override
    public String toString()
    {
        return _name;
    }
    
    /**
     * Returns the access mode of an IMember by examining the modifier flags
     * 
     * @param IMember member The IMember to be examined
     * @return Access The access mode of the given IMember  
     * 
     * @throws JavaModelException if the <code>member</code> does not exist or if an exception 
     *          occurs while accessing its corresponding resource.
     */
    public static Access fromFlags(IMember member) throws JavaModelException
    {
        int flags = member.getFlags();
        
        if (Flags.isPrivate(flags))
        {
            return Access.PRIVATE;
        }
        else if (Flags.isPackageDefault(flags))
        {
            return Access.DEFAULT;
        }
        else if (Flags.isProtected(flags))
        {
            return Access.PROTECTED;
        }
        else if (Flags.isPublic(flags))
        {
            return Access.PUBLIC;
        }
    
        return Access.NO_USE; 
    }
    
    /**
     * Returns the more generous Access of two.
     * 
     * @param access1 The first Access to compare
     * @param access2 The second Access
     * 
     * @return Access <code>access2<code> if it is less restrictive than <code>access1</code>, 
     * else <code>access1</code>. 
     */
    public static Access max(Access access1, Access access2)
    {
    	if (access1.compareTo(access2) < 0)
    	{
    		return access2;
    	}
    	
    	return access1;
    }
    
    /**
     * Returns the Access modes that can really be the actual access level modes
     * of Java elements such as types or methods.
     * Namely these are PUBLIC, PROTECTED, DEFAULT and PRIVATE. The pseudo access
     * modifier NO_USE which can only be the minimal access modifier of an
     * element, is omited.
     * 
     * @return Access[] The named Access modes
     */
    public static Access[] real()
    {
        return new Access[] { Access.PRIVATE, Access.DEFAULT, Access.PROTECTED, Access.PUBLIC };
    }
}
