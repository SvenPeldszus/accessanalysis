package de.uni_hamburg.informatik.swt.accessanalysis.analysis.constraints;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

import de.uni_hamburg.informatik.swt.accessanalysis.access.Access;
import de.uni_hamburg.informatik.swt.accessanalysis.constraints.MethodAccessConstraintChecker;
import de.uni_hamburg.informatik.swt.accessanalysis.constraints.TypeAccessConstraintChecker;

class InterfaceMemberConstraint implements TypeAccessConstraintChecker, MethodAccessConstraintChecker {
	
	@Override
	public Access checkTypeDeclaration(ITypeBinding typeBinding) {
		ITypeBinding declaringType = typeBinding.getDeclaringClass();
        if (declaringType != null && declaringType.isInterface())
        {
            return Access.PUBLIC;
        }
        
		return Access.NO_USE;
	}

	@Override
	public Access checkMethodDeclaration(IMethodBinding methodBinding){
		ITypeBinding declaringType = methodBinding.getDeclaringClass();
        if (declaringType != null && declaringType.isInterface())
        {
            return Access.PUBLIC;
        }
        
		return Access.NO_USE;
	}
}
