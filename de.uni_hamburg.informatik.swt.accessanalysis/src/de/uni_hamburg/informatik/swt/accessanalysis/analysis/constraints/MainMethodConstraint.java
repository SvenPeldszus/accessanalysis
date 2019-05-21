package de.uni_hamburg.informatik.swt.accessanalysis.analysis.constraints;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.IMethodBinding;

import de.uni_hamburg.informatik.swt.accessanalysis.access.Access;
import de.uni_hamburg.informatik.swt.accessanalysis.constraints.MethodAccessConstraintChecker;

class MainMethodConstraint implements MethodAccessConstraintChecker {

	@Override
	public Access checkMethodDeclaration(IMethodBinding methodBinding) throws JavaModelException {
		if ((methodBinding.getJavaElement() != null && ((IMethod) methodBinding.getJavaElement()).isMainMethod()))
        {
            return Access.PUBLIC;
        }

		return Access.NO_USE; 
	}

}
