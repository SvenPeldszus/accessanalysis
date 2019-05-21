/**
 * 
 */
package de.uni_hamburg.informatik.swt.accessanalysis.analysis.determinators;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.Modifier;

import de.uni_hamburg.informatik.swt.accessanalysis.access.Access;


/**
 * Determines the lowest access mode needed to override a method.
 * 
 * @author christian
 */
public class OverriddenMethodAccessDeterminator
{
	public static Access determineAccess(IMethodBinding overridingBinding, IMethodBinding overriddenBinding)
    {
        if (overriddenBinding.getDeclaringClass().isInterface())
        {
            return Access.PUBLIC;
        }
        
        if (samePackage(overridingBinding, overriddenBinding))
        {
            return Access.DEFAULT;
        }
        
        return Access.PROTECTED;
    }
       
    private static boolean samePackage(IMethodBinding overridingBinding, IMethodBinding overriddenBinding)
    {
        return overridingBinding.getDeclaringClass().getPackage().equals(overriddenBinding.getDeclaringClass().getPackage());
    }
    
    public static Access getActualAccess(IMethodBinding methodBinding) throws JavaModelException
    {
        int modifiers = methodBinding.getModifiers();
        
        if (Modifier.isPublic(modifiers))
        {
            return Access.PUBLIC;
        }
        else if (Modifier.isProtected(modifiers))
        {
            return Access.PROTECTED;
        }
        else if (Modifier.isPrivate(modifiers))
        {
            return Access.PRIVATE;
        }
        
        return Access.DEFAULT;
    }
}
