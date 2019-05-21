/**
 * 
 */
package de.uni_hamburg.informatik.swt.accessanalysis.resultsview;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import de.uni_hamburg.informatik.swt.accessanalysis.results.Result;
import de.uni_hamburg.informatik.swt.accessanalysis.results.ResultFormatter;


/**
 * Provides the labels for the "IGAM"-column 
 * in the AccessAnalysisView-TreeTable.
 */
class IGAMLabelProvider extends StyledCellLabelProvider
{
    @Override
    public void update(ViewerCell cell)
    {
        ResultFormatter formatter = ((Result) cell.getElement()).getFormatter();
        cell.setText(formatter.igam());
        super.update(cell);
    }
}