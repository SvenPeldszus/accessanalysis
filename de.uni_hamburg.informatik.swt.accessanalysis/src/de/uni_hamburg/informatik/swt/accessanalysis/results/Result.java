package de.uni_hamburg.informatik.swt.accessanalysis.results;

import java.util.List;

import org.eclipse.jdt.core.IJavaElement;

/**
 * Collects the result data of an analysis entity.
 */
public interface Result extends Comparable<Result>
{
    public static double GOOD = 0.0;
    public static double BAD = 1.0;
    
    /**
     * Returns the result of the parent element.
     * 
     * @return Result The result of the parent element if there is one, else null
     * 
     * @see hasParent()
     */
    public Result getParent();
    
    /**
     * Checks if there are child elements.
     * 
     * @return boolean true, if the element the result is based on has child elements, else false
     */
    public boolean hasChildren();
    
    /**
     * Returns the result of the parent element.
     * 
     * @return List<Result> The results of the childs elements if there is at least one child, else an empty list
     * 
     * @see hasChilds()
     */
    public List<Result> getChildren();
    
    /**
     * Returns the result formatter for this result
     * 
     * @return ResultFormatter The result formatter
     */
    public ResultFormatter getFormatter();
    
    /**
     * Returns the java element the result is based on
     * 
     * @return IJavaElement The java element associated with this result
     */
    public IJavaElement getJavaElement();
    
    /**
     * Checks whether this underlying java element has a source code, that 
     * can be edited.
     * 
     * @return boolean true, if the underlying java element is editable.
     */
    public boolean isElementEditable();
}
