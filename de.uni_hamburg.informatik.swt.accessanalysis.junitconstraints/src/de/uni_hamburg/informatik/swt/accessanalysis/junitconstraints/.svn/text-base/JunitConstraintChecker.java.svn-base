package de.uni_hamburg.informatik.swt.accessanalysis.junitconstraints;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

import de.uni_hamburg.informatik.swt.accessanalysis.access.Access;
import de.uni_hamburg.informatik.swt.accessanalysis.constraints.MethodAccessConstraintChecker;
import de.uni_hamburg.informatik.swt.accessanalysis.constraints.TypeAccessConstraintChecker;
import de.uni_hamburg.informatik.swt.accessanalysis.extensions.RcpExtension;

@RcpExtension
public class JunitConstraintChecker implements TypeAccessConstraintChecker, MethodAccessConstraintChecker 
{

	private AccessDeterminator _junit3;
	private AccessDeterminator _junit4;

	@RcpExtension
	public JunitConstraintChecker() {
		_junit3 = new Junit3AccessDeterminator();
		_junit4 = new Junit4AccessDeterminator();
	}
	
	@Override
	public Access checkTypeDeclaration(ITypeBinding typeBinding)
			throws JavaModelException {
		
		return Access.max(_junit4.determineAccess(typeBinding), _junit3.determineAccess(typeBinding));
	}
	
	@Override
    public Access checkMethodDeclaration(IMethodBinding methodBinding)
            throws JavaModelException {

        return Access.max(_junit4.determineAccess(methodBinding), _junit3.determineAccess(methodBinding));
    }
}
