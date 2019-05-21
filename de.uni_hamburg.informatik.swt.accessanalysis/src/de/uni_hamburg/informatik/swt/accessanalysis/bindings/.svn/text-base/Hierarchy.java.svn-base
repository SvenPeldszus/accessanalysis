package de.uni_hamburg.informatik.swt.accessanalysis.bindings;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Modifier;

/**
 * Searches elements in type hierarchies and a lot of other stuff.
 * 
 * Partly copied from org.eclipse.jdt.internal.corext.dom.Bindings due to restrictions
 * on that package.
 */
public class Hierarchy
{
    /**
     * Checks if the two bindings are equals. Also works across binding
     * environments.
     * 
     * @param b1
     *            first binding treated as <code>this</code>. So it must not be
     *            <code>null</code>
     * @param b2
     *            the second binding.
     * @return boolean
     */
    private static boolean equals(IBinding b1, IBinding b2)
    {
        return b1.isEqualTo(b2);
    }

    /**
     * Checks if the two arrays of bindings have the same length and their
     * elements are equal. Uses <code>Bindings.equals(IBinding, IBinding)</code>
     * to compare.
     * 
     * @param b1
     *            the first array of bindings. Must not be <code>null</code>.
     * @param b2
     *            the second array of bindings.
     * @return boolean
     */
    private static boolean equals(IBinding[] b1, IBinding[] b2)
    {
        Assert.isNotNull(b1);
        if (b1 == b2)
            return true;
        if (b2 == null)
            return false;
        if (b1.length != b2.length)
            return false;
        for (int i = 0; i < b1.length; i++)
        {
            if (!Hierarchy.equals(b1[i], b2[i]))
                return false;
        }
        return true;
    }

    /**
     * Finds the method in the given <code>type</code> that is overridden by the
     * specified <code>method<code>.
     * Returns <code>null</code> if no such method exits.
     * 
     * @param type
     *            The type to search the method in
     * @param method
     *            The specified method that would override the result
     * @return the method binding of the method that is overridden by the
     *         specified <code>method<code>, or <code>null</code>
     */
    private static IMethodBinding findOverriddenMethodInType(ITypeBinding type, IMethodBinding method)
    {
        IMethodBinding[] methods = type.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++)
        {
            if (isSubsignature(method, methods[i]))
                return methods[i];
        }
        return null;
    }

    /**
     * Finds the method in the given <code>type</code> that is overriding the
     * specified <code>method<code>.
     * Returns <code>null</code> if no such method exits.
     * 
     * @param type
     *            The type to search the method in
     * @param method
     *            The specified method that would be overridden by the result
     * @return the method binding of the method that is overriding the specified
     *         <code>method<code>, or <code>null</code>
     */
    public static IMethodBinding findOveridingMethodInType(ITypeBinding type, IMethodBinding method)
    {
        IMethodBinding[] methods = type.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++)
        {
            if (isSubsignature(methods[i], method))
                return methods[i];
        }
        return null;
    }

    /**
     * Finds a method in the hierarchy of <code>type</code> that is overridden
     * by </code>binding</code>. Returns <code>null</code> if no such method
     * exists. If the method is defined in more than one super type only the
     * first match is returned. First the super class is examined and than the
     * implemented interfaces.
     * 
     * @param type
     *            The type to search the method in
     * @param binding
     *            The method that overrides
     * @return the method binding overridden the method
     */
    public static IMethodBinding findOverriddenMethodInHierarchy(ITypeBinding type, IMethodBinding binding)
    {
        IMethodBinding method = findOverriddenMethodInType(type, binding);
        if (method != null)
            return method;
        ITypeBinding superClass = type.getSuperclass();
        if (superClass != null)
        {
            method = findOverriddenMethodInHierarchy(superClass, binding);
            if (method != null)
                return method;
        }
        ITypeBinding[] interfaces = type.getInterfaces();
        for (int i = 0; i < interfaces.length; i++)
        {
            method = findOverriddenMethodInHierarchy(interfaces[i], binding);
            if (method != null)
                return method;
        }

        return null;
    }

    /**
     * Finds the method that is overridden by the given method. The search is
     * bottom-up, so this returns the nearest defining/declaring method.
     * 
     * @param overriding
     *            overriding method
     * @param testVisibility
     *            If true the result is tested on visibility. Null is returned
     *            if the method is not visible.
     * @return the method binding representing the method
     */
    public static IMethodBinding findOverriddenMethod(IMethodBinding overriding, boolean testVisibility)
    {
        int modifiers = overriding.getModifiers();
        if (Modifier.isPrivate(modifiers) || overriding.isConstructor())
        {
            return null;
        }

        ITypeBinding type = overriding.getDeclaringClass();
        if (type.getSuperclass() != null)
        {
            IMethodBinding res = findOverriddenMethodInHierarchy(type.getSuperclass(), overriding);
            if (res != null && !Modifier.isPrivate(res.getModifiers()))
            {
                if (!testVisibility || isVisibleInHierarchy(res, overriding.getDeclaringClass().getPackage()))
                {
                    return res;
                }
            }
        }

        return findOverriddenMethodInInterfaces(overriding);
    }

    /**
     * Finds the method that is overridden by the given method. Searches only in
     * interfaces!!! The search is bottom-up, so this returns the nearest
     * defining/declaring method.
     * 
     * @param overriding
     *            overriding method
     * @return the method binding representing the method, or null, if no found
     */
    public static IMethodBinding findOverriddenMethodInInterfaces(IMethodBinding overriding)
    {
        int modifiers = overriding.getModifiers();
        if (Modifier.isPrivate(modifiers) || Modifier.isStatic(modifiers) || overriding.isConstructor())
        {
            return null;
        }

        ITypeBinding type = overriding.getDeclaringClass();
        ITypeBinding[] interfaces = type.getInterfaces();
        for (int i = 0; i < interfaces.length; i++)
        {
            IMethodBinding res = findOverriddenMethodInHierarchy(interfaces[i], overriding);
            if (res != null)
            {
                return res; // methods from interfaces are always and
                // therefore visible
            }
        }
        return null;
    }

    /**
     * Finds the method that is overridden by the given method. Searches only in
     * classes! The search is bottom-up, so this returns the nearest
     * defining/declaring method.
     * 
     * @param method
     *            overriding method
     * @param testVisibility
     *            If true the result is tested on visibility. Null is returned
     *            if the method is not visible.
     * @return the method binding representing the method
     */
    public static IMethodBinding findMethodInSuperclasses(ITypeBinding type, IMethodBinding method, boolean testVisibility)
    {
        int modifiers = method.getModifiers();
        if (Modifier.isPrivate(modifiers) || Modifier.isStatic(modifiers) || method.isConstructor())
        {
            return null;
        }

        ITypeBinding superclass = type.getSuperclass();
        if (superclass != null)
        {
            IMethodBinding res = findOverriddenMethodInType(superclass, method);
            if (res != null)
            {
                if (Modifier.isPrivate(res.getModifiers()))
                {
                    return null;
                }
                if (!testVisibility || isVisibleInHierarchy(res, method.getDeclaringClass().getPackage()))
                {
                    return res;
                }
            }

            return findMethodInSuperclasses(superclass, method, testVisibility);
        }

        return null;
    }

    /**
     * Finds the <code>field</code> in the given <code>type</code>. Returns
     * <code>null</code> if no such field exits.
     * 
     * @param type
     *            the type to search the field in
     * @param the
     *            binding representing the field
     * @return the binding again or <code>null</code>
     */
    private static IVariableBinding findFieldInType(ITypeBinding type, IVariableBinding field)
    {
        if (type.isPrimitive())
            return null;
        IVariableBinding[] fields = type.getDeclaredFields();
        for (int i = 0; i < fields.length; i++)
        {
            if (fields[i].equals(field))
                return field;
        }
        return null;
    }

    /**
     * Finds the <code>field</code> in the type hierarchy denoted by the given
     * type. Returns <code>null</code> if no such field exists. If the field is
     * defined in more than one super type only the first match is returned.
     * First the type itself is examined, than the super class and than the
     * implemented interfaces.
     * 
     * @param type
     *            The type to search the field in
     * @param field
     *            The variable binding of the field to find
     * @return the variable binding again or null, if no such field was find
     */
    public static IVariableBinding findFieldInHierarchy(ITypeBinding type, IVariableBinding field)
    {
        field = findFieldInType(type, field);
        if (field != null)
            return field;
        ITypeBinding superClass = type.getSuperclass();
        if (superClass != null)
        {
            field = findFieldInHierarchy(superClass, field);
            if (field != null)
                return field;
        }
        ITypeBinding[] interfaces = type.getInterfaces();
        for (int i = 0; i < interfaces.length; i++)
        {
            field = findFieldInHierarchy(interfaces[i], field);
            if (field != null) // no private fields in interfaces
                return field;
        }
        return null;
    }

    /**
     * Checks whether a method would be inherited by any subtype in a given package.   
     * 
     * @param IMethodBinding method The method that was found in a supertype 
     * @param IPackageBinding pack The package that contains the subtype
     * @return boolean true, if the method is visible for the subtype, else false
     */
    private static boolean isVisibleInHierarchy(IMethodBinding method, IPackageBinding pack)
    {
        int otherflags = method.getModifiers();
        ITypeBinding declaringType = method.getDeclaringClass();
        if (Modifier.isPublic(otherflags) || Modifier.isProtected(otherflags) || (declaringType != null && declaringType.isInterface()))
        {
            return true;
        }
        else if (Modifier.isPrivate(otherflags))
        {
            return false;
        }
        return declaringType != null && pack == declaringType.getPackage();
    }

    /**
     * Checks whether one method is a subsignature of another.
     * 
     * @param overriding
     *            overriding method (m1)
     * @param overridden
     *            overridden method (m2)
     * @return <code>true</code> if the method <code>m1</code> is a subsignature
     *         of the method <code>m2</code>. This is one of the requirements
     *         for m1 to override m2. Accessibility and return types are not
     *         taken into account. Note that subsignature is <em>not</em>
     *         symmetric!
     */
    private static boolean isSubsignature(IMethodBinding overriding, IMethodBinding overridden)
    {
        // TODO: use IMethodBinding#isSubsignature(..) once it is tested and
        // fixed (only erasure of m1's parameter types, considering type
        // variable counts, doing type variable substitution
        if (!overriding.getName().equals(overridden.getName()))
            return false;

        ITypeBinding[] m1Params = overriding.getParameterTypes();
        ITypeBinding[] m2Params = overridden.getParameterTypes();
        if (m1Params.length != m2Params.length)
            return false;

        ITypeBinding[] m1TypeParams = overriding.getTypeParameters();
        ITypeBinding[] m2TypeParams = overridden.getTypeParameters();
        if (m1TypeParams.length != m2TypeParams.length && m1TypeParams.length != 0) // non-generic
            // can
            // override
            // a
            // generic
            // m2
            return false;

        // m1TypeParameters.length == (m2TypeParameters.length || 0)
        if (m2TypeParams.length != 0)
        {
            // Note: this branch does not 100% adhere to the spec and may report
            // some false positives.
            // Full compliance would require major duplication of compiler code.

            // Compare type parameter bounds:
            for (int i = 0; i < m1TypeParams.length; i++)
            {
                // loop over m1TypeParams, which is either empty, or equally
                // long as m2TypeParams
                Set<ITypeBinding> m1Bounds = getTypeBoundsForSubsignature(m1TypeParams[i]);
                Set<ITypeBinding> m2Bounds = getTypeBoundsForSubsignature(m2TypeParams[i]);
                if (!m1Bounds.equals(m2Bounds))
                    return false;
            }
            // Compare parameter types:
            if (equals(m2Params, m1Params))
                return true;
            for (int i = 0; i < m1Params.length; i++)
            {
                ITypeBinding m1Param = m1Params[i];
                if (containsTypeVariables(m1Param))
                    m1Param = m1Param.getErasure(); // try to achieve effect of
                // "rename type variables"
                else if (m1Param.isRawType())
                    m1Param = m1Param.getTypeDeclaration();
                if (!(equals(m1Param, m2Params[i].getErasure()))) // can erase
                    // m2
                    return false;
            }
            return true;

        }
        else
        {
            // m1TypeParams.length == m2TypeParams.length == 0
            if (equals(m1Params, m2Params))
                return true;
            for (int i = 0; i < m1Params.length; i++)
            {
                ITypeBinding m1Param = m1Params[i];
                if (m1Param.isRawType())
                    m1Param = m1Param.getTypeDeclaration();
                if (!(equals(m1Param, m2Params[i].getErasure()))) // can erase
                    // m2
                    return false;
            }
            return true;
        }
    }

    /**
     * Checks whether at least one type contains a type variable
     * 
     * @param type The types to search in
     * @return boolean true, if one type contains a type variable
     */
    private static boolean containsTypeVariables(ITypeBinding[] types)
    {
        for (int i = 0; i < types.length; i++)
            if (containsTypeVariables(types[i]))
                return true;
        return false;
    }
    
    /**
     * Checks whether a single type contains a type variable
     * 
     * @param type The type to search in
     * @return boolean true, if the type contains a type variable
     */
    private static boolean containsTypeVariables(ITypeBinding type)
    {
        if (type.isTypeVariable())
            return true;
        if (type.isArray())
            return containsTypeVariables(type.getElementType());
        if (type.isCapture())
            return containsTypeVariables(type.getWildcard());
        if (type.isParameterizedType())
            return containsTypeVariables(type.getTypeArguments());
        if (type.isTypeVariable())
            return containsTypeVariables(type.getTypeBounds());
        if (type.isWildcardType() && type.getBound() != null)
            return containsTypeVariables(type.getBound());
        return false;
    }

    @SuppressWarnings("unchecked")
    private static Set<ITypeBinding> getTypeBoundsForSubsignature(ITypeBinding typeParameter)
    {
        ITypeBinding[] typeBounds = typeParameter.getTypeBounds();
        int count = typeBounds.length;
        if (count == 0)
            return Collections.EMPTY_SET;

        Set<ITypeBinding> result = new HashSet<ITypeBinding>(typeBounds.length);
        for (int i = 0; i < typeBounds.length; i++)
        {
            ITypeBinding bound = typeBounds[i];
            if ("java.lang.Object".equals(typeBounds[0].getQualifiedName())) //$NON-NLS-1$
                continue;
            else if (containsTypeVariables(bound))
                result.add(bound.getErasure()); // try to achieve effect of
            // "rename type variables"
            else if (bound.isRawType())
                result.add(bound.getTypeDeclaration());
            else
                result.add(bound);
        }
        return result;
    }

    /**
     * Returns <code>true</code> if the given type is a super type of a
     * candidate. <code>true</code> is returned if the two type bindings are
     * identical (TODO)
     * 
     * @param possibleSuperType
     *            the type to inspect
     * @param type
     *            the type whose super types are looked at
     * @return <code>true</code> iff <code>possibleSuperType</code> is a super
     *         type of <code>type</code> or is equal to it
     */
    public static boolean isSuperType(ITypeBinding possibleSuperType, ITypeBinding type)
    {
        if (type.isArray() || type.isPrimitive())
        {
            return false;
        }
        if (Hierarchy.equals(type.getTypeDeclaration(), possibleSuperType.getTypeDeclaration()))
        {
            return true;
        }
        ITypeBinding superClass = type.getSuperclass();
        if (superClass != null)
        {
            if (isSuperType(possibleSuperType, superClass))
            {
                return true;
            }
        }

        if (possibleSuperType.isInterface())
        {
            ITypeBinding[] superInterfaces = type.getInterfaces();
            for (int i = 0; i < superInterfaces.length; i++)
            {
                if (isSuperType(possibleSuperType, superInterfaces[i]))
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Returns <code>true</code> if the given type is a super type of a
     * candidate. <code>true</code> is returned if the two type bindings are
     * identical (TODO)
     * 
     * @param possibleSuperType
     *            the qualified name of the type to inspect
     * @param type
     *            the type whose super types are looked at
     * @return <code>true</code> if <code>possibleSuperType</code> is a super
     *         type of <code>type</code> or is equal to it
     */
    public static boolean isSuperType(String possibleSuperType, ITypeBinding type)
    {
    	if (type.isArray() || type.isPrimitive())
    	{
    		return false;
    	}
    	
    	if (type.getQualifiedName().equals(possibleSuperType))
    	{
    		return true;
    	}
    	
    	ITypeBinding superClass = type.getSuperclass();
    	if (superClass != null)
    	{
    		if (isSuperType(possibleSuperType, superClass))
    		{
    			return true;
    		}
    	}
    	
		ITypeBinding[] superInterfaces = type.getInterfaces();
		for (int i = 0; i < superInterfaces.length; i++)
		{
			if (isSuperType(possibleSuperType, superInterfaces[i]))
			{
				return true;
			}
		}
		
    	return false;
    }

	/**
	 * Returns all methods that are declared in implemented interfaces of a
	 * type.
	 * 
	 * @param ITypeBinding
	 *            type The type which interfaces should be examined.
	 * @return Set<IMethodBinding> A set of all methods
	 * 
	 */
	public static Set<IMethodBinding> findAllInterfaceMethods(ITypeBinding type)
	{
	    Set<IMethodBinding> methods = new HashSet<IMethodBinding>();
	
	    putAllInterfaceMethodsInASet(methods, type);
	
	    return methods;
	}

	/**
	 * Puts all methods that are declared in implemented interfaces of a type in
	 * a given Set.
	 * 
	 * @param Set<IMethodBinding> methods The set to be filled
	 * @param ITypeBinding
	 *            type The type which interfaces should be examined.
	 */
	private static void putAllInterfaceMethodsInASet(Set<IMethodBinding> methods, ITypeBinding type)
	{
	    for (ITypeBinding interf : type.getInterfaces())
	    {
	        methods.addAll(Arrays.asList(interf.getDeclaredMethods()));
	        putAllInterfaceMethodsInASet(methods, interf);
	    }
	}
}
