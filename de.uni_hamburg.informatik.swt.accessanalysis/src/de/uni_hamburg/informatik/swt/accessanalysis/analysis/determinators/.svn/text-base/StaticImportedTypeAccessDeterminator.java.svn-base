/**
 * 
 */
package de.uni_hamburg.informatik.swt.accessanalysis.analysis.determinators;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.PackageDeclaration;

/**
 * Determines the lowest access mode needed for the static import of a type.
 */
public class StaticImportedTypeAccessDeterminator extends TypeAccessDeterminator
{
	private final CompilationUnit _currentCompilationUnit;
    
    /**
     * Initializes an object of StaticImportedTypeAccessDeterminator. 
     * 
     * @param CompilationUnit compilationUnit The current compilation unit.
     */
	public StaticImportedTypeAccessDeterminator(CompilationUnit compilationUnit)
    {
        _currentCompilationUnit = compilationUnit;
    }
    
    boolean canBeNoUse(ITypeBinding binding)
    {
        return false;
    }
    
    boolean canBePrivate(ITypeBinding binding)
    {
        return false;
    }

    boolean canBeProtected(ITypeBinding binding)
    {
        return false;
    }
         
    IPackageBinding getCurrentPackageBinding()
    {
        PackageDeclaration packageDecl = _currentCompilationUnit.getPackage();
        if (packageDecl == null)
        {
            return null;
        }
        
        return packageDecl.resolveBinding();
    }
}
