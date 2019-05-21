/**
 * 
 */
package de.uni_hamburg.informatik.swt.accessanalysis.analysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.IMethodBinding;

import de.uni_hamburg.informatik.swt.accessanalysis.access.Access;


/**
 * Collects minimal access and overridden methods of methods
 * and maps the methods to their actual minimal access.
 *
 */
class MethodMap
{
    private final Map<String, Access> _accessMap = new HashMap<String, Access>();
    private final Map<String, String> _overrideMap = new HashMap<String, String>();;
    
    
    void putAccess(IMethodBinding binding, Access access)
    {
        putAccess(binding.getKey(), access);
    }
    
    private void putAccess(String key, Access access)
    {
        Access old = _accessMap.get(key);
        if (old == null || access.compareTo(old) > 0)
        {
            _accessMap.put(key, access);
        }
    }

    void putOverride(IMethodBinding overriding, IMethodBinding overridden)
    {
        putOverride(overriding.getKey(), overridden.getKey());
    }
    
    private void putOverride(String overridingKey, String overriddenKey)
    {
        _overrideMap.put(overridingKey, overriddenKey);
    }
    
    Access getAccess(IMethod method)
    {
        return getAccess(method.getKey());
    }

    private Access getAccess(String key)
    {
        Access access = Access.NO_USE;
        
        Access ownAccess = _accessMap.get(key);
        if (ownAccess != null)
        {
            access = ownAccess;
        }
        
        String overrideKey = _overrideMap.get(key);
        if (overrideKey != null)
        {
            Access overrideAccess = getAccess(overrideKey);
            if (overrideAccess.compareTo(access) > 0)
            {
                access = overrideAccess;
            }
        }
        
        return access;
    }
    
    @Override
    public String toString()
    {
        return _accessMap.toString();
    }
}
