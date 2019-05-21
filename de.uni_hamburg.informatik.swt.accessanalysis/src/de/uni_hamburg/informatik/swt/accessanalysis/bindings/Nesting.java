package de.uni_hamburg.informatik.swt.accessanalysis.bindings;

import java.util.Arrays;

import org.eclipse.jdt.core.dom.ITypeBinding;

public class Nesting {

	/**
	 * Returns the declaring top level type of a type
	 * 
	 * @param ITypeBinding
	 *            type The type, which top level type should be returned
	 * @return ITypeBinding For a member type the top level type under which it
	 *         is declared, for a top level type the type itself
	 */
	public static ITypeBinding getTopLevelType(ITypeBinding binding)
	{
	    if (binding.isNested())
	    {
	        return getTopLevelType(binding.getDeclaringClass()).getTypeDeclaration();
	    }
	
	    return binding;
	}

	/**
	 * Checks if one type is nested in another
	 * 
	 * @param ITypeBinding
	 *            possibleNestedType The type to be inspect
	 * @param ITypeBinding
	 *            possibleContainingType The type whose nested types are looked
	 *            at
	 */
	public static boolean isNestedIn(ITypeBinding possibleNestedType, ITypeBinding possibleContainingType)
	{
	    return Arrays.asList(possibleContainingType.getDeclaredTypes()).contains(possibleNestedType);
	}

}
