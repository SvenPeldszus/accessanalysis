/**
 * 
 */
package de.uni_hamburg.informatik.swt.accessanalysis.resultsview;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.uni_hamburg.informatik.swt.accessanalysis.results.Result;


/**
 * Content provider for the AccessAnalysis-View
 *
 */
class AccessAnalysisViewContentProvider implements IStructuredContentProvider, ITreeContentProvider
{
    private Object[] _results;
    
    /**
     * Initializes an object of ResultViewContentProvider. 
     */
    AccessAnalysisViewContentProvider()
    {
        _results = null;
    }
    
    @Override
    public Object[] getElements(Object inputElement)
    {
        return _results;
    }

    @Override
    public void dispose()
    {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        if (newInput == null)
        {
            _results = null;
        }
        else
        {
            List<Result> list = (List<Result>) newInput;
            Collections.sort(list);
            _results = list.toArray();
        }
    }

    @Override
    public Object[] getChildren(Object parentElement)
    {
        return ((Result) parentElement).getChildren().toArray();
    }

    @Override
    public Object getParent(Object element)
    {
        return ((Result) element).getParent();
    }

    @Override
    public boolean hasChildren(Object element)
    {
        boolean test = ((Result) element).hasChildren();
        return test;
    }
}
