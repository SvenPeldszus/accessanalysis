/**
 * 
 */
package de.uni_hamburg.informatik.swt.accessanalysis;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;

import de.uni_hamburg.informatik.swt.accessanalysis.analysis.AccessAnalysis;


/**
 * Generates the right analysis object
 */
public class AnalysisFactory
{   
    /**
     * All possible analysis modes
     */
    public static enum AnalysisMode 
    {
        ACCESS_QUIET,
        ACCESS_VERBOSE,
    }
 
    /**
     * The default analysis mode
     */
    public static final AnalysisMode DEFAULT = AnalysisMode.ACCESS_QUIET;
    
    /**
     * Returns the analysis for a list of projects at a given mode.
     * 
     * @param List<IJavaProject> projects The projects to be analyzed
     * @param AnalysisMode mode The analysis mode of choice
     * @return Analysis The analysis object
     */
    public static Analysis analyzer(List<IJavaProject> projects, AnalysisMode mode)
    {
        switch (mode)
        {
            case ACCESS_QUIET : 
                return new AccessAnalysis(projects, false);
            case ACCESS_VERBOSE : 
                return new AccessAnalysis(projects, true);
        }
        
        return null;
    }
    
    /**
     * Returns the analysis for a single project at a given mode.
     * 
     * @param IJavaProject project The project to be analyzed
     * @param AnalysisMode mode The analysis mode of choice
     * @return Analysis The analysis object
     */
    public static Analysis analyzer(IJavaProject project, AnalysisMode type)
    {
        List<IJavaProject> projects = new LinkedList<IJavaProject>();
        projects.add(project);
        return analyzer(projects, type);
    }
    
    /**
     * Returns the analysis for a list of projects at the default mode.
     * 
     * @param List<IJavaProject> projects The projects to be analyzed
     * @return Analysis The analysis object
     */
    public static Analysis analyzer(List<IJavaProject> projects)
    {
        return analyzer(projects, DEFAULT);
    }
}
