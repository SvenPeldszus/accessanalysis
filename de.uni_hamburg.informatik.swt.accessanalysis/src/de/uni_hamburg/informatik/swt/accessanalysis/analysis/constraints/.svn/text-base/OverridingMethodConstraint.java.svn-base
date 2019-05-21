package de.uni_hamburg.informatik.swt.accessanalysis.analysis.constraints;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.IMethodBinding;

import de.uni_hamburg.informatik.swt.accessanalysis.access.Access;
import de.uni_hamburg.informatik.swt.accessanalysis.analysis.determinators.OverriddenMethodAccessDeterminator;
import de.uni_hamburg.informatik.swt.accessanalysis.bindings.Hierarchy;
import de.uni_hamburg.informatik.swt.accessanalysis.bindings.Locals;
import de.uni_hamburg.informatik.swt.accessanalysis.constraints.MethodAccessConstraintChecker;

class OverridingMethodConstraint implements MethodAccessConstraintChecker {

	@Override
	public Access checkMethodDeclaration(IMethodBinding methodBinding) throws JavaModelException {
		IMethodBinding overriddenBinding = Hierarchy.findOverriddenMethod(methodBinding, true);
        if (overriddenBinding != null)
        {
            overriddenBinding = overriddenBinding.getMethodDeclaration();
            if (! overriddenBinding.getDeclaringClass().isInterface() &&
            		Hierarchy.findOverriddenMethodInInterfaces(methodBinding) != null)
            {
            	return Access.PUBLIC;
            }               
            else if (! Locals.isLocalMethod(overriddenBinding))
            {               
                return OverriddenMethodAccessDeterminator.getActualAccess(overriddenBinding);
            }
        }
        
        return Access.NO_USE;
	}

}
