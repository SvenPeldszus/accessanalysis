/**
 * 
 */
package de.uni_hamburg.informatik.swt.accessanalysis.analysis.determinators;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

/**
 * @author christian
 *
 */
public class InvokedMethodAccessDeterminatorForStaticContext extends InvokedMethodAccessDeterminator
{
    private final ITypeBinding _invokingTypeBinding;
    
    /**
     * Initializes an object of InvokedMethodAccessDeterminator. 
     * 
     * @param MethodMap The method map to be used
     */
    public InvokedMethodAccessDeterminatorForStaticContext(ITypeBinding invokingType)
    {
        super(null);
        _invokingTypeBinding = invokingType;
    }
    
    boolean canBeNoUse(IMethodBinding binding, ITypeBinding expressionType)
    {
        return false;
    }
    
    ITypeBinding getCurrentTypeBinding()
    {
        return _invokingTypeBinding;
    }
}
