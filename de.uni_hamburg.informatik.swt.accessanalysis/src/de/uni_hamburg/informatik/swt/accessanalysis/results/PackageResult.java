package de.uni_hamburg.informatik.swt.accessanalysis.results;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.graphics.Image;

/**
 * This class collects the analysis results for a single package and its types.
 */
public class PackageResult implements Result
{
    private final ResultFormatter _formatter;
    private final List<TypeResult> _types;
    
    private final IPackageFragment _package;
    private final SourceFolderResult _parent;
    
    private boolean _recalculateIgat;
    private double _igat;
    private double _igatSum;
    private boolean _recalculateIgam;
    private double _igam;
    private double _igamSum;
    private int _numberOfMethods;

    /**
     * Initialize an object of PackageResult.
     * 
     * @param IPackageFragment packageFragment The package, the results are based on
     * @param Result parent The result of the parent element
     */
    public PackageResult(IPackageFragment packageFragment, SourceFolderResult parent)
    {
        _formatter = new PackageResultFormatter();
        _types = new LinkedList<TypeResult>();
        
        _package = packageFragment;
        _parent = parent;
        
        _recalculateIgat = true;
        _igat = GOOD;
        _igatSum = 0;
        _recalculateIgam = true;
        _igam = GOOD;
        _igamSum = 0;
        _numberOfMethods = 0;
    }

    @Override
    public boolean hasChildren()
    {
        return !_types.isEmpty();
    }

    @Override
    public List<Result> getChildren()
    {
        List<Result> children = new ArrayList<Result>(_types);
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
     * Adds the results of a type to this package
     * 
     * @param TypeResult typeResult The results of a type that belongs to this package
     */
    public void addType(TypeResult typeResult)
    {
        _types.add(typeResult);
        setRecalculateIgat();
        setRecalculateIgam();
    }

    void setRecalculateIgat()
    {
        _recalculateIgat = true;
        _parent.setRecalculateIgat();
    }

    /**
     * Returns the IGAT metric value of this package
     * 
     * @return float IGAT
     */
    private double getIgat() throws JavaModelException
    {
        if (_recalculateIgat)
        {
            calculateIgat();
        }
        
        return _igat;
    }
    
    private void calculateIgat() throws JavaModelException
    {
        calculateIgatSum();
        _igat = GOOD;
        int numberOfTypes = _types.size();
        if (numberOfTypes > 0)
        {
            _igat = _igatSum / numberOfTypes;
        }
        _recalculateIgat = false;
    }

    private void calculateIgatSum() throws JavaModelException
    {
        double sum = 0.0;
        for (TypeResult typeResult : _types)
        {
            sum += typeResult.getIgat();
        }
        _igatSum = sum;
    }

    /**
     * Returns the IGAT sum of all types
     * 
     * @return double The sum
     */
    double getIgatSum() throws JavaModelException
    {
        if (_recalculateIgat)
        {
            calculateIgat();
        }
        
        return _igatSum;
    }

    /**
     * Returns the number of types in this package
     * 
     * @return double The number
     */
    int getNumberOfTypes()
    {
        return _types.size();
    }
    
    void setRecalculateIgam()
    {
        _recalculateIgam = true;
        _parent.setRecalculateIgam();
    }

    /**
     * Returns the IGAM metric value of this package
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
        calculateNumberOfMethods();
        _igam = GOOD;
        if (_numberOfMethods > 0)
        {
            _igam = _igamSum / _numberOfMethods;
        }
        _recalculateIgam = false;
    }
    
    private void calculateIgamSum() throws JavaModelException
    {
        double sum = 0.0;
        for (TypeResult typeResult : _types)
        {
            sum += typeResult.getIgamSum();
        }
        _igamSum = sum;
    }
    
    private void calculateNumberOfMethods() throws JavaModelException
    {
        int n = 0;
        for (TypeResult typeResult : _types)
        {
            n += typeResult.getNumberOfMethods();
        }
        _numberOfMethods = n;
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
     * Returns the number of methods int this package
     * 
     * @return double The number
     * @throws JavaModelException 
     */
    int getNumberOfMethods() throws JavaModelException
    {
        if (_recalculateIgam)
        {
            calculateIgam();
        }
        
        return _numberOfMethods;
    }

    @Override
    public IPackageFragment getJavaElement()
    {
        return _package;
    }
    
    @Override
    public boolean isElementEditable()
    {
        return false;
    }
    
    @Override
    public int compareTo(Result o)
    {
        if (o instanceof PackageResult)
        {
            PackageResult pr = (PackageResult) o;
            return _package.getElementName().compareTo(pr._package.getElementName());
        }
        else if (o instanceof ProjectResult || o instanceof SourceFolderResult)
        {
            return -1;
        }
        
        return 1;
    }
    
    @Override
    public String toString()
    {
        return "Package\t" + getFormatter().toString();
    }

    /**
     * Generates Image and Strings for a package result element.
     */
    private class PackageResultFormatter extends AbstractResultFormatter implements ResultFormatter
    {
        @Override
        public Image image()
        {
            return getLabelProvider().getImage(getJavaElement());
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
            return getLabelProvider().getText(getJavaElement());
        }
    }
}
