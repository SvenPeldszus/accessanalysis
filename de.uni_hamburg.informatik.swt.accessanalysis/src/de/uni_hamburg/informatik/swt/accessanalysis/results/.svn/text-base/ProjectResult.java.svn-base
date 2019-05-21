package de.uni_hamburg.informatik.swt.accessanalysis.results;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.graphics.Image;

/**
 * This class collects the analysis results for a single project and its
 * source folders.
 */
public class ProjectResult implements Result
{
    private final ResultFormatter _formatter;
    private final List<SourceFolderResult> _sourceFolders;
    
    private final IJavaProject _project;
    
    private boolean _recalculateIgat;
    private double _igat;
    private boolean _recalculateIgam;
    private double _igam;

    /**
     * Initialize an object of ProjectResult.
     * 
     * @param IJavaProject project The project, the results are based on
     */
    public ProjectResult(IJavaProject project)
    {
        _formatter = new ProjectResultFormatter();
        _sourceFolders = new LinkedList<SourceFolderResult>();
        
        _project = project;
        
        _recalculateIgat = true;
        _igat = GOOD;
        _recalculateIgam = true;
        _igam = GOOD;
    }

    @Override
    public boolean hasChildren()
    {
        return ! _sourceFolders.isEmpty();
    }

    @Override
    public List<Result> getChildren()
    {
        List<Result> children = new ArrayList<Result>(_sourceFolders);
        Collections.sort(children);
        return children;
    }
    
    @Override
    public Result getParent()
    {
        return null;
    }

    @Override
    public ResultFormatter getFormatter()
    {
        return _formatter;
    }

    /**
     * Adds the results of a source folder to this project
     * 
     * @param SourceFolderResult
     *            sourceFolderResult The results of a source folder that belongs 
     *            to this project
     */
    public void addSourceFolder(SourceFolderResult sourceFolderResult)
    {
        _sourceFolders.add(sourceFolderResult);
        _recalculateIgat = true;
        _recalculateIgam = true;
    }
    
    void setRecalculateIgat()
    {
        _recalculateIgat = true;
    }

    /**
     * Returns the IGAT metric value of this project
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
        _igat = GOOD;
        int numberOfTypes = calculateNumberOfTypes();
        if (numberOfTypes > 0)
        {
            _igat = calculateIGATSum() / numberOfTypes;
        }
        
        _recalculateIgat = false;
    }

    /**
     * Returns the IGAM sum of all methods
     * 
     * @return double The sum
     */
    private double calculateIGATSum() throws JavaModelException
    {
        double sum = 0.0;
        for (SourceFolderResult sourceFolderResult : _sourceFolders)
        {
            if (sourceFolderResult.getNumberOfTypes() > 0)
            {
                sum += sourceFolderResult.getIgatSum();
            }
        }
        
        return sum;
    }

    /**
     * Returns the number of types
     * 
     * @return double The number
     * @throws JavaModelException 
     */
    private int calculateNumberOfTypes() throws JavaModelException
    {
        int n = 0;
        for (SourceFolderResult sourceFolderResult : _sourceFolders)
        {
            n += sourceFolderResult.getNumberOfTypes();
        }
        
        return n;
    }

    void setRecalculateIgam()
    {
        _recalculateIgam = true;
    }

    /**
     * Returns the IGAM metric value of this project
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
        _igam = GOOD;
        int numberOfMethods = calculateNumberOfMethods();
        if (numberOfMethods > 0)
        {
            _igam = calculateIGAMSum() / numberOfMethods;
        }
        
        _recalculateIgam = false;
    }
    
    /**
     * Returns the IGAM sum of all methods
     * 
     * @return double The sum
     */
    private double calculateIGAMSum() throws JavaModelException
    {
        double sum = 0.0;
        for (SourceFolderResult sourceFolderResult : _sourceFolders)
        {
            if (sourceFolderResult.getNumberOfMethods() > 0)
            {
                sum += sourceFolderResult.getIgamSum();
            }
        }
        
        return sum;
    }

    /**
     * Returns the number of methods
     * 
     * @return double The number
     * @throws JavaModelException 
     */
    private int calculateNumberOfMethods() throws JavaModelException
    {
        int n = 0;
        for (SourceFolderResult sourceFolderResult : _sourceFolders)
        {
            n += sourceFolderResult.getNumberOfMethods();
        }
        
        return n;
    }

    @Override
    public IJavaProject getJavaElement()
    {
        return _project;
    }
    
    @Override
    public boolean isElementEditable()
    {
        return false;
    }
    
    @Override
    public String toString()
    {
        return "Project\t" + getFormatter().toString();
    }
    
    @Override
    public int compareTo(Result o)
    {
        if (o instanceof ProjectResult)
        {
            ProjectResult pr = (ProjectResult) o;
            return _project.getElementName().compareTo(pr._project.getElementName());
        }
        
        return 1;
    }

    /**
     * Generates Image and Strings for a project result element.
     */
    private class ProjectResultFormatter extends AbstractResultFormatter implements ResultFormatter
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
