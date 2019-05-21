package de.uni_hamburg.informatik.swt.accessanalysis.results;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.swt.graphics.Image;

import de.uni_hamburg.informatik.swt.accessanalysis.access.Access;


/**
 * This class collects the analysis results for a single method.
 */
public class MethodResult implements Result
{
    private final ResultFormatter _formatter;
    
    private final IMethod _method;
    private final TypeResult _parent;
    
    private Access _minimalAccess;
    
    private boolean _recalculateIgam;
    private double _igam;
    
    /**
     * Initializes an object of MethodResult.
     * 
     * @param MethodDeclaration node The declaration node of this method from the AST
     * @param TypeResult parent The result of the parent element
     */
    public MethodResult(MethodDeclaration node, TypeResult parent)
    {
        _formatter = new MethodResultFormatter();
        
        _method = (IMethod) node.resolveBinding().getJavaElement();
        _parent = parent;
        _minimalAccess = Access.NO_USE;
        
        _recalculateIgam = true;
        _igam = GOOD;
    }
    
    @Override
    public boolean hasChildren()
    {
        return false;
    }
    
    @Override
    public List<Result> getChildren()
    {
        return new ArrayList<Result>();
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
     * Returns the actual access modifier of this method
     * 
     * @return Access The actual access modifier  
     */
    private Access getActualAccess() throws JavaModelException
    {
        if (_method.getDeclaringType().isInterface())
        {
            return Access.PUBLIC;
        }

        if (_method.isConstructor() && _method.getDeclaringType().isEnum())
        {
            return Access.PRIVATE;
        }
        
        return Access.fromFlags(_method);
    }

    /**
     * Returns the minimal possible access modifier of this method
     * 
     * @return Access The minimal possible access modifier  
     */
    private Access getMinimalAccess()
    {
        return _minimalAccess;
    }
    
    /**
     * Sets the minimal possible access modifier of this method
     * 
     * @param Access The minimal possible access modifier  
     */
    public void setMinimalAccess(Access access)
    {
        _minimalAccess = access;
        setRecalculateIgam();
    }
    
    private void setRecalculateIgam()
    {
        _recalculateIgam = true;
        _parent.setRecalculateIgam();
    }

    /**
     * Returns the IGAM metric value of this method
     * 
     * @return float IGAM
     */
    double getIgam() throws JavaModelException
    {
        if (_recalculateIgam)
        {
            calculateIgam();
        }
        
        return _igam;
    }
    
    private void calculateIgam() throws JavaModelException
    {
        _igam = GOOD;
        if (getActualAccess().compareTo(getMinimalAccess()) > 0)
        {
            _igam = BAD;
        }
    }
    
    @Override
    public IMethod getJavaElement()
    {
        return _method;
    }
    
    @Override
    public boolean isElementEditable()
    {
        return true;
    }
    
    @Override
    public int compareTo(Result o)
    {
        if (o instanceof MethodResult)
        {
            MethodResult mr = (MethodResult) o;
            return _method.getElementName().compareTo(mr._method.getElementName());
        }
        
        return -1;
    }
    
    @Override
    public String toString()
    {
        return "Method\t" + getFormatter().toString();
    }
    
    /**
     * Generates Image and Strings for a method result element.
     */
    private class MethodResultFormatter extends AbstractResultFormatter implements ResultFormatter
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
            return getLabelProvider().getText(getJavaElement());
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