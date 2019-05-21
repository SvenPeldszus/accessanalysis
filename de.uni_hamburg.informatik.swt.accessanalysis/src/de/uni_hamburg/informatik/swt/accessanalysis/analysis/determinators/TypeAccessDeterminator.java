/**
 * 
 */
package de.uni_hamburg.informatik.swt.accessanalysis.analysis.determinators;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

import de.uni_hamburg.informatik.swt.accessanalysis.access.Access;


/**
 * Determines the lowest access mode needed for the usage of a type.
 */
public abstract class TypeAccessDeterminator
{
    /**
     * Determines the minimal access of an used type needed for the usage.
     * Assumes that the using type is the current one.
     * 
     * @param ITypeBinding expressionType The binding of the type that is to be used.
     * @return The minimal access needed for the usage
     */
    public Access determineAccess(ITypeBinding binding) throws JavaModelException
    {
        if (binding.isArray())
        {
            return determineAccess(binding.getElementType());
        }

        if (canBeNoUse(binding))
        {
            return Access.NO_USE;
        }
        
        if (canBePrivate(binding))
        {
            return Access.PRIVATE;
        }
        
        if (canBeDefault(binding))
        {
            return Access.DEFAULT;
        }

        if (canBeProtected(binding))
        {
            return Access.PROTECTED;
        }

        return Access.PUBLIC;
    }
    
    abstract boolean canBeNoUse(ITypeBinding binding) throws JavaModelException;
    
    abstract boolean canBePrivate(ITypeBinding binding) throws JavaModelException;
    
    private boolean canBeDefault(ITypeBinding binding) throws JavaModelException
    {
        return binding.getPackage().equals(getCurrentPackageBinding());
    }
    
    abstract boolean canBeProtected(ITypeBinding binding);
    
    abstract IPackageBinding getCurrentPackageBinding();
}
