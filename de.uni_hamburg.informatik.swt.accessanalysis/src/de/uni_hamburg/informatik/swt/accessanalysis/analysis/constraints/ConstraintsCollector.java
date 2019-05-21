package de.uni_hamburg.informatik.swt.accessanalysis.analysis.constraints;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import de.uni_hamburg.informatik.swt.accessanalysis.constraints.MethodAccessConstraintChecker;
import de.uni_hamburg.informatik.swt.accessanalysis.constraints.TypeAccessConstraintChecker;

public class ConstraintsCollector {
	private static final String EXTENSION_POINT_ID = "de.uni_hamburg.informatik.swt.accessanalysis.constraints";
	
	private static final InterfaceMemberConstraint INTERFACE_MEMBER_CONSTRAINT = new InterfaceMemberConstraint();
    private static final MainMethodConstraint MAIN_METHOD_CONSTRAINT = new MainMethodConstraint();
    private static final OverridingMethodConstraint OVERRIDING_METHOD_CONSTRAINT = new OverridingMethodConstraint();
    
    public static Set<TypeAccessConstraintChecker> getTypeConstraints() throws CoreException {
		HashSet<TypeAccessConstraintChecker> constraints = new HashSet<TypeAccessConstraintChecker>(1);
		constraints.add(INTERFACE_MEMBER_CONSTRAINT);
		
		for (IConfigurationElement e : getConfigurationElements()) {
			final Object extension = e.createExecutableExtension("class");
			if (extension instanceof TypeAccessConstraintChecker) {
				TypeAccessConstraintChecker constraint = (TypeAccessConstraintChecker) extension;
				constraints.add(constraint);
			}
		}
		
		return constraints;
	}
    
    public static Set<MethodAccessConstraintChecker> getMethodConstraints() throws CoreException {
		HashSet<MethodAccessConstraintChecker> constraints = new HashSet<MethodAccessConstraintChecker>(1);
		constraints.add(MAIN_METHOD_CONSTRAINT);
		constraints.add(INTERFACE_MEMBER_CONSTRAINT);
		constraints.add(OVERRIDING_METHOD_CONSTRAINT);
		
		for (IConfigurationElement e : getConfigurationElements()) {
			final Object extension = e.createExecutableExtension("class");
			if (extension instanceof MethodAccessConstraintChecker) {
				MethodAccessConstraintChecker constraint = (MethodAccessConstraintChecker) extension;
				constraints.add(constraint);
			}
		}
		
		return constraints;
	}

	private static IConfigurationElement[] getConfigurationElements() {
		return Platform.getExtensionRegistry()
				.getConfigurationElementsFor(EXTENSION_POINT_ID);
	}
}
