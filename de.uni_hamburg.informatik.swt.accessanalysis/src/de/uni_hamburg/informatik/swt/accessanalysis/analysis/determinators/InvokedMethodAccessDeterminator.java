/**
 * 
 */
package de.uni_hamburg.informatik.swt.accessanalysis.analysis.determinators;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

import de.uni_hamburg.informatik.swt.accessanalysis.access.Access;
import de.uni_hamburg.informatik.swt.accessanalysis.bindings.Hierarchy;
import de.uni_hamburg.informatik.swt.accessanalysis.bindings.Nesting;


/**
 * Determines the lowest access mode needed for the invocation of a method.
 * 
 * @author christian
 * 
 */
public class InvokedMethodAccessDeterminator
{
    private final IMethodBinding _invokingMethodBinding;

    /**
     * Initializes an object of InvokedMethodAccessDeterminator.
     * 
     * @param IMethodBinding
     *            invokingMethod The method that invokes other methods
     */
    public InvokedMethodAccessDeterminator(IMethodBinding invokingMethod)
    {
        _invokingMethodBinding = invokingMethod;
    }

    /**
     * Determines the minimal access of an invoked method needed for the
     * invocation Assumes that the invoking method is the current one.
     * 
     * @param IMethodBinding
     *            binding The binding of the invoked method
     * @param ITypeBinding
     *            expressionType The binding of the type of the expression the
     *            method is called on
     * @return The minimal access needed for the invocation
     */
    public Access determineAccess(IMethodBinding binding, ITypeBinding expressionType) throws JavaModelException
    {
        if (binding.getDeclaringClass().isInterface())
        {
            return Access.PUBLIC;
        }

        if (canBeNoUse(binding, expressionType))
        {
            return Access.NO_USE;
        }

        if (canBePrivate(binding, expressionType))
        {
            return Access.PRIVATE;
        }

        if (canBeDefault(binding, expressionType))
        {
            return Access.DEFAULT;
        }

        if (canBeProtected(binding, expressionType))
        {
            return Access.PROTECTED;
        }

        return Access.PUBLIC;
    }

    boolean canBeNoUse(IMethodBinding binding, ITypeBinding expressionType)
    {
        return binding.equals(getCurrentMethodBinding()) && (expressionType == null || expressionType.equals(getCurrentTypeBinding()));
    }

    private boolean canBePrivate(IMethodBinding binding, ITypeBinding expressionType) throws JavaModelException
    {
        if (Flags.isAbstract(((IMethod) binding.getJavaElement()).getFlags()))
        {
            return false;
        }

        ITypeBinding currentTopLevelTyp = Nesting.getTopLevelType(getCurrentTypeBinding());
        
        return Nesting.getTopLevelType(binding.getDeclaringClass()).equals(currentTopLevelTyp) && (expressionType == null || Nesting.getTopLevelType(expressionType).equals(currentTopLevelTyp));
    }

    private boolean canBeDefault(IMethodBinding binding, ITypeBinding expressionType) throws JavaModelException
    {
        ITypeBinding declaringType = binding.getDeclaringClass();

        if (!declaringType.getPackage().equals(getCurrentPackageBinding()))
        {
            return false;
        }

        ITypeBinding intermediateType = null;
        if (expressionType != null)
        {
            intermediateType = expressionType.getTypeDeclaration();
        }
        if (intermediateType == null)
        {
            intermediateType = getCurrentTypeBinding();
        }

        while (intermediateType != null && !intermediateType.getName().equals("void") && !intermediateType.equals(declaringType))
        {
            ITypeBinding typeBinding = intermediateType;
            
            while (typeBinding.getPackage() == null)
            {
                typeBinding = typeBinding.getDeclaringClass();
            }
            
            if (! typeBinding.getPackage().equals(getCurrentPackageBinding()))
            {
                return false;
            }

            intermediateType = intermediateType.getSuperclass().getTypeDeclaration();
        }

        return true;
    }

    private boolean canBeProtected(IMethodBinding binding, ITypeBinding expressionType)
    {
        ITypeBinding declaringType = binding.getDeclaringClass();

        ITypeBinding currentType = getCurrentTypeBinding();
        while (currentType != null)
        {
            if (Hierarchy.isSuperType(declaringType, currentType) && 
                (expressionType == null || Flags.isStatic(binding.getModifiers()) || Hierarchy.isSuperType(currentType, expressionType)))
            {
                return true;
            }
            currentType = currentType.getDeclaringClass();
        }

        if (declaringType.getPackage().equals(getCurrentPackageBinding()))
        {
            return true;
        }
        
        return false;
    }

    private IMethodBinding getCurrentMethodBinding()
    {
        return _invokingMethodBinding;
    }

    ITypeBinding getCurrentTypeBinding()
    {
        return getCurrentMethodBinding().getDeclaringClass().getTypeDeclaration();
    }

    private IPackageBinding getCurrentPackageBinding()
    {
        return getCurrentTypeBinding().getPackage();
    }
}
