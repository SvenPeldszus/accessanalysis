package de.uni_hamburg.informatik.swt.accessanalysis.results;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.graphics.Image;

/**
 * This class collects the analysis results for a single source folder and its packages.
 */
public class SourceFolderResult implements Result
{
    private final ResultFormatter _formatter;
    private final List<PackageResult> _packages;
    
    private final IPackageFragmentRoot _folder;
    private final ProjectResult _parent;
    
    private boolean _recalculateIgat;
    private double _igat;
    private double _igatSum;
    private int _numberOfTypes;
    private boolean _recalculateIgam;
    private double _igam;
    private double _igamSum;
    private int _numberOfMethods;

    /**
     * Initialize an object of PackageResult.
     * 
     * @param IPackageFragmentRoot packageFragmentRoot The source folder, the results are based on
     * @param ProjectResult parent The result of the parent element
     */
    public SourceFolderResult(IPackageFragmentRoot folder, ProjectResult parent)
    {
        _formatter = new SourceFolderResultFormatter();
        _packages = new LinkedList<PackageResult>();
        
        _folder = folder;
        _parent = parent;
        
        _recalculateIgat = true;
        _igat = GOOD;
        _igatSum = 0;
        _numberOfTypes = 0;
        _recalculateIgam = true;
        _igam = GOOD;
        _igamSum = 0;
        _numberOfMethods = 0;
    }

    @Override
    public boolean hasChildren()
    {
        return !_packages.isEmpty();
    }

    @Override
    public List<Result> getChildren()
    {
        List<Result> children = new ArrayList<Result>(_packages);
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
     * Adds the results of a package to this source folder result
     * 
     * @param PackageResult packageResult The results of a package that belongs to this package
     */
    public void addPackage(PackageResult packageResult)
    {
        _packages.add(packageResult);
        setRecalculateIgat();
        setRecalculateIgam();
    }
    
    void setRecalculateIgat()
    {
        _recalculateIgat = true;
        _parent.setRecalculateIgat();
    }

    /**
     * Returns the IGAT metric value of this source folder
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
        calculateNumberOfTypes();
        _igat = GOOD;
        if (_numberOfTypes > 0)
        {
            _igat = _igatSum / _numberOfTypes;
        }
        _recalculateIgat = false;
    }

    private void calculateIgatSum() throws JavaModelException
    {
        double sum = 0.0;
        for (PackageResult packageResult : _packages)
        {
            sum += packageResult.getIgatSum();
        }
        _igatSum = sum;
    }
    
    private void calculateNumberOfTypes() throws JavaModelException
    {
        int n = 0;
        for (PackageResult packageResult : _packages)
        {
            n += packageResult.getNumberOfTypes();
        }
        _numberOfTypes = n;
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
     * @throws JavaModelException 
     */
    int getNumberOfTypes() throws JavaModelException
    {
        if (_recalculateIgat)
        {
            calculateIgat();
        }
        
        return _numberOfTypes;
    }
    
    void setRecalculateIgam()
    {
        _recalculateIgam = true;
        _parent.setRecalculateIgam();
    }

    /**
     * Returns the IGAM metric value of this source folder
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
        for (PackageResult packageResult : _packages)
        {
            sum += packageResult.getIgamSum();
        }
        _igamSum = sum;
    }
    
    private void calculateNumberOfMethods() throws JavaModelException
    {
        int n = 0;
        for (PackageResult packageResult : _packages)
        {
            n += packageResult.getNumberOfMethods();
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
    public IPackageFragmentRoot getJavaElement()
    {
        return _folder;
    }
    
    @Override
    public boolean isElementEditable()
    {
        return false;
    }
    
    @Override
    public int compareTo(Result o)
    {
        if (o instanceof SourceFolderResult)
        {
            SourceFolderResult sfr = (SourceFolderResult) o;
            return _folder.getElementName().compareTo(sfr._folder.getElementName());
        }
        else if (o instanceof ProjectResult)
        {
            return -1;
        }
        
        return 1;
    }
    
    @Override
    public String toString()
    {
        return "SourceFolder\t" + getFormatter().toString();
    }

    /**
     * Generates Image and Strings for a source folder result element.
     */
    private class SourceFolderResultFormatter extends AbstractResultFormatter implements ResultFormatter
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
