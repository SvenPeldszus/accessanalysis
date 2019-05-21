package de.uni_hamburg.informatik.swt.accessanalysis.annotations;

import org.eclipse.jdt.core.IType;

import de.uni_hamburg.informatik.swt.accessanalysis.access.Access;

class AnnotationConstraint implements Comparable<AnnotationConstraint> {

    enum ApplyTo { 
        TYPES(true, false, "Types"), 
        METHODS(false, true, "Methods"), 
        BOTH(true, true, "Types/Methods"); 
    
        private boolean _types;
        private boolean _methods;
        private String _printingName;
        
        private ApplyTo(boolean types, boolean methods, String printingName)
        {
            _types = types;
            _methods = methods;
            _printingName = printingName;
        }
        
        boolean types()
        {
            return _types;
        }
        
        boolean methods()
        {
            return _methods;
        }
        
        @Override
        public String toString()
        {
            return _printingName;
        }
    }
    
    private IType _annotationType;
    private Access _access;
    private ApplyTo _applyTo;

    /**
     * @require annotationType != null
     * @require access != null
     * @require access != Access.NO_USE
     * @require applyTo != null
     */
    AnnotationConstraint(IType annotationType, Access access, ApplyTo applyTo)
    {
        assert annotationType != null : "Precondition failed: annotationType != null";
        assert access != null : "Precondition failed: access != null";
        assert applyTo != null : "Precondition failed: applyTo != null";

        _annotationType = annotationType;
        _access = access;
        _applyTo = applyTo;
    }
    
    IType getAnnotationType()
    {
        return _annotationType;
    }
    
    Access getMinimalAccess()
    {
        return _access;
    }
    
    ApplyTo applyTo()
    {
        return _applyTo;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_access == null) ? 0 : _access.hashCode());
        result = prime * result + ((_annotationType == null) ? 0 : _annotationType.hashCode());
        result = prime * result + ((_applyTo == null) ? 0 : _applyTo.hashCode());
        return result;
    }
    
    @Override
    public String toString()
    {
        return "AnnotationConstraint [" + _annotationType.getFullyQualifiedName() + ", " + _access + ", " + _applyTo + "]";
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AnnotationConstraint other = (AnnotationConstraint) obj;
        if (_access != other._access)
            return false;
        if (_annotationType == null)
        {
            if (other._annotationType != null)
                return false;
        } else if (!_annotationType.equals(other._annotationType))
            return false;
        if (_applyTo != other._applyTo)
            return false;
        return true;
    }

    @Override
    public int compareTo(AnnotationConstraint o)
    {
        int result = this._annotationType.getPackageFragment().getElementName().compareTo(o._annotationType.getPackageFragment().getElementName());
        if (result == 0)
        {
            result = this._annotationType.getElementName().compareTo(o._annotationType.getElementName());
        }
        if (result == 0)
        {
            result = - this.getMinimalAccess().compareTo(o.getMinimalAccess());
        }
        if (result == 0)
        {
            result = this.applyTo().compareTo(o.applyTo());
        }
                
        return result;
    }
}
