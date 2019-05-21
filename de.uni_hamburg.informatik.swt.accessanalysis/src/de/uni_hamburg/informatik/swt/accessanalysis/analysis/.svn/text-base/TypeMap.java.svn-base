/**
 * 
 */
package de.uni_hamburg.informatik.swt.accessanalysis.analysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ITypeBinding;

import de.uni_hamburg.informatik.swt.accessanalysis.access.Access;


/**
 * Maps types to their minimal access.
 */
class TypeMap
{
    private final Map<String, Access> _accessMap = new HashMap<String, Access>();
    
    void putAccess(ITypeBinding binding, Access access)
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
    
    Access getAccess(IType type)
    {
        return getAccess(type.getKey());
    }

    private Access getAccess(String key)
    {
        if (_accessMap.containsKey(key))
        {
            return _accessMap.get(key);
        }
        
        return Access.NO_USE;
    }
    
    @Override
    public String toString()
    {
        return _accessMap.toString();
    }
}
