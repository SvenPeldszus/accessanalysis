package de.uni_hamburg.informatik.swt.accessanalysis.constraints;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ITypeBinding;

import de.uni_hamburg.informatik.swt.accessanalysis.access.Access;

public interface TypeAccessConstraintChecker {

	public Access checkTypeDeclaration(ITypeBinding typeBinding) throws JavaModelException;
}
