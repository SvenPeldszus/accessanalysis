package de.uni_hamburg.informatik.swt.accessanalysis.junitconstraints;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

import de.uni_hamburg.informatik.swt.accessanalysis.access.Access;

interface AccessDeterminator {
	
	public Access determineAccess(IMethodBinding methodBinding) throws JavaModelException;
	
	public Access determineAccess(ITypeBinding typeBinding) throws JavaModelException;
}
