package de.uni_hamburg.informatik.swt.accessanalysis.bindings;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class Locals {

	/**
	 * Checks whether a method is included in the analyzed source code
	 * 
	 * @param IMethodBinding methodBinding The method to be inspected
	 */
	public static boolean isLocalMethod(IMethodBinding methodBinding)
	{ 
	    if (methodBinding == null || methodBinding.getJavaElement() == null)
	    {
	        return false;
	    }
	    
	    try
	    {
	        return ((IPackageFragment) methodBinding.getDeclaringClass().getPackage().getJavaElement()).getKind() == IPackageFragmentRoot.K_SOURCE;
	    }
	    catch (JavaModelException e)
	    {
	        return false;
	    }
	}

	/**
	 * Checks whether a type is included in the analyzed source code
	 * 
	 * @param ITypeBinding typeBinding The type to be inspected
	 */
	public static boolean isLocalType(ITypeBinding typeBinding)
	{ 
	    if (typeBinding == null || typeBinding.isTypeVariable() || typeBinding.getJavaElement() == null)
	    {
	        return false;
	    }
	    
	    try
	    {
	        return ((IPackageFragment) typeBinding.getPackage().getJavaElement()).getKind() == IPackageFragmentRoot.K_SOURCE;
	    }
	    catch (JavaModelException e)
	    {
	        return false;
	    }
	}

}
