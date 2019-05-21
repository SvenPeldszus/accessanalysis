package de.uni_hamburg.informatik.swt.accessanalysis.results;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.swt.graphics.Image;

import de.uni_hamburg.informatik.swt.accessanalysis.access.Access;


/**
 * This class collects the analysis results for a single Type and its methods.
 */
public class TypeResult implements Result
{
    private final ResultFormatter _formatter;
    private final List<MethodResult> _methods;
    
    private final IType _type;
    private final PackageResult _parent;
    
    private Access _minimalAccess;
    
    private boolean _recalculateIgat;
    private double _igat;
    private boolean _recalculateIgam;
    private double _igam;
    private double _igamSum;

    /**
     * Initialize an object of TypeResult.
     * 
     * @param AbstractTypeDeclaration node The declaration node of this method from the AST
     * @param PackageResult parent The result of the parent element
     */
    public TypeResult(AbstractTypeDeclaration node, PackageResult parent)
    {
        _formatter = new TypeResultFormatter();
        _methods = new LinkedList<MethodResult>();
        
        _type = (IType) node.resolveBinding().getJavaElement();
        _parent = parent;
        _minimalAccess = Access.NO_USE;
        
        _recalculateIgat = true;
        _igat = GOOD;
        _recalculateIgam = true;
        _igam = GOOD;
        _igamSum = 0;
    }
    
    @Override
    public boolean hasChildren()
    {
        return ! _methods.isEmpty();
    }
    
    @Override
    public List<Result> getChildren()
    {
        List<Result> children = new ArrayList<Result>(_methods);
        Collections.sort(children);
        return children;
    }
    
    @Override
    public Result getParent()
    {
        return _parent;
    }

    @Override
    public ResultFormatter getFormatter()
    {
        return _formatter;
    }
    
    /**
     * Adds the results of a method to this type
     * 
     * @param MethodResult
     *            method The results of a method that belongs to this type
     */
    public void addMethodResults(MethodResult methodResult)
    {
        _methods.add(methodResult);
        setRecalculateIgam();
    }
    
    /**
     * Returns the actual access modifier of this type
     * 
     * @return Access The actual access modifier  
     */
    private Access getActualAccess() throws JavaModelException
    {
        IType declaringType = _type.getDeclaringType(); 
        if (declaringType != null && declaringType.isInterface())
        {
            return Access.PUBLIC;
        }
        
        return Access.fromFlags(_type);
    }

    /**
     * Returns the minimal possible access modifier of this type
     * 
     * @return Access The minimal possible access modifier  
     */
    private Access getMinimalAccess()
    {
        return _minimalAccess;
    }
    
    /**
     * Sets the minimal possible access modifier of this type
     * 
     * @param Access The minimal possible access modifier  
     */
    public void setMinimalAccess(Access access)
    {
        _minimalAccess = access;
        setRecalculateIgat();
    }
    
    private void setRecalculateIgat()
    {
        _recalculateIgat = true;
        _parent.setRecalculateIgat();
    }

    /**
     * Returns the IGAT metric value of this type
     * 
     * @return float IGAT
     */
    double getIgat() throws JavaModelException
    {
        if (_recalculateIgat)
        {
            calculateIgat();
        }
        
        return _igat;
    }
    
    private void calculateIgat() throws JavaModelException
    {
        _igat = GOOD;
        if (getActualAccess().compareTo(getMinimalAccess()) > 0)
        {
            _igat = BAD;
        }
    }
   
    void setRecalculateIgam()
    {
        _recalculateIgam = true;
        _parent.setRecalculateIgam();
    }

    /**
     * Returns the IGAM metric value of this type
     * 
     * @return float IGAM
     */
    private double getIgam() throws JavaModelException
    {
        if (_recalculateIgam)
        {
            calculateIgam();
        }
        
        return _igam;
    }
    
    private void calculateIgam() throws JavaModelException
    {
        calculateIgamSum();
        int numberOfMethods = _methods.size();
        _igam = GOOD;
        if (numberOfMethods > 0)
        {
            _igam = _igamSum / numberOfMethods;
        }
        _recalculateIgam = false;
    }
    
    private void calculateIgamSum() throws JavaModelException
    {
        double sum = 0.0;
        for (MethodResult methodResult : _methods)
        {
            sum += methodResult.getIgam();
        }
        _igamSum = sum;
    }
    
    /**
     * Returns the IGAM sum of all methods
     * 
     * @return double The sum
     */
    double getIgamSum() throws JavaModelException
    {
        if (_recalculateIgam)
        {
            calculateIgam();
        }
        
        return _igamSum;
    }
    
    /**
     * Returns the number of methods
     * 
     * @return double The number
     */
    int getNumberOfMethods()
    {
        return _methods.size();
    }
    
    @Override
    public IType getJavaElement()
    {
        return _type;
    }
    
    @Override
    public boolean isElementEditable()
    {
        return true;
    }
    
    @Override
    public int compareTo(Result o)
    {
        if (o instanceof TypeResult)
        {
            TypeResult tr = (TypeResult) o;
            _type.getTypeQualifiedName().compareTo(tr._type.getTypeQualifiedName());
        }
        else if (o instanceof MethodResult)
        {
            return 1;
        }
        
        return -1;
    }
    
    @Override
    public String toString()
    {
        String str = "Type\t" + getFormatter().toString(); 
        try
        {
            if (! (_type.isMember() || _type.isLocal() || _type.isAnonymous()))
            {
                str += "\tTOP LEVEL";
            }
        }
        catch (JavaModelException e) { }
        
        return str;
    }
    
    /**
     * Generates Image and Strings for a type result element.
     */
    private class TypeResultFormatter extends AbstractResultFormatter implements ResultFormatter
    {
        
        
        @Override
        public String actualAccess()
        {
            try
            {
                return getActualAccess().toString();
            }
            catch (Exception e)
            {
                return "error";
            }
        }

        @Override
        public Image image()
        {
            return getLabelProvider().getImage(getJavaElement());
        }

        @Override
        public String minimalAccess()
        {
            try
            {
                return getMinimalAccess().toString();
            }
            catch (Exception e)
            {
                return "error";
            }
        }

        @Override
        public String igat()
        {
            try
            {
                return getDecimalFormat().format(getIgat());
            }
            catch (Exception e)
            {
                return "error";
            }
        }
        
        @Override
        public String igam()
        {
            try
            {
                return getDecimalFormat().format(getIgam());
            }
            catch (Exception e)
            {
                return "error";
            }
        }

        @Override
        public String name()
        {
            String typeName = getLabelProvider().getText(getJavaElement());

            String qualifiedName = getJavaElement().getTypeQualifiedName('.');
            if (qualifiedName.contains("."))
            {
                return qualifiedName.substring(0, qualifiedName.lastIndexOf('.') + 1) + typeName;
            }
            
            return typeName;
        }
        
        @Override
        public boolean accessAlert()
        {
            try
            {
                return getActualAccess().compareTo(getMinimalAccess()) > 0;
            }
            catch (Exception e)
            {
                return true;
            }
        }
    }
}