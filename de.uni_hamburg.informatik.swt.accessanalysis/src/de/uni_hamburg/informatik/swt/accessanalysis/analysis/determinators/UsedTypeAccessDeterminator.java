/**
 * 
 */
package de.uni_hamburg.informatik.swt.accessanalysis.analysis.determinators;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

import de.uni_hamburg.informatik.swt.accessanalysis.bindings.Hierarchy;
import de.uni_hamburg.informatik.swt.accessanalysis.bindings.Nesting;

/**
 * Determines the lowest access mode needed for the usage of a type.
 */
public class UsedTypeAccessDeterminator extends TypeAccessDeterminator
{
    private final ITypeBinding _usingTypeBinding;
    
    /**
     * Initializes an object of UsedTypeAccessDeterminator. 
     * 
     * @param ITypeBinding usingType The Type that uses other types
     */
    public UsedTypeAccessDeterminator(ITypeBinding usingType)
    {
        _usingTypeBinding = usingType;
    }
    
    boolean canBeNoUse(ITypeBinding binding)
    {
        return binding.equals(getCurrentTypeBinding()) ||
               Nesting.isNestedIn(getCurrentTypeBinding(), binding);
    }
    
    boolean canBePrivate(ITypeBinding binding) throws JavaModelException
    {
        if (! binding.isNested())
        {
            return false;
        }
        
        ICompilationUnit compilationUnit = ((IType) binding.getJavaElement()).getCompilationUnit();
        return getCurrentCompilationUnit().equals(compilationUnit);
    }

    boolean canBeProtected(ITypeBinding binding)
    {
        if (! binding.isNested())
        {
            return false;
        }

        ITypeBinding declaringType = binding.getDeclaringClass();
        
        ITypeBinding currentType = getCurrentTypeBinding();
        while (currentType != null)
        {
            if (Hierarchy.isSuperType(declaringType, currentType))
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
    
    private ITypeBinding getCurrentTypeBinding()
    {
        return _usingTypeBinding;
    }
    
    private ICompilationUnit getCurrentCompilationUnit()
    {
        return ((IType) getCurrentTypeBinding().getJavaElement()).getCompilationUnit();
    }
    
    IPackageBinding getCurrentPackageBinding()
    {
        return getCurrentTypeBinding().getPackage();
    }
}
