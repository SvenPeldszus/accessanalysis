/**
 * 
 */
package de.uni_hamburg.informatik.swt.accessanalysis.resultsview;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Event;

import de.uni_hamburg.informatik.swt.accessanalysis.results.Result;
import de.uni_hamburg.informatik.swt.accessanalysis.results.ResultFormatter;


/**
 * Provides the labels for the "Actual Access"-column 
 * in the AccessAnalysisView-TreeTable.
 */
class ActualAccessLabelProvider extends StyledCellLabelProvider
{
    private final ColorRegistry _colorRegistry;
    
    /**
     * Initializes an object of MinimalAccessLabelProvider. 
     */
    ActualAccessLabelProvider()
    {
        super(StyledCellLabelProvider.COLORS_ON_SELECTION);
        _colorRegistry = new ColorRegistry();
        _colorRegistry.put("red", new RGB(255, 0, 0));
    }
    
    
    
    @Override
    public void update(ViewerCell cell)
    {
        ResultFormatter formatter = ((Result) cell.getElement()).getFormatter();
        cell.setText(formatter.actualAccess());
        cell.setStyleRanges(createStyle(formatter));
        super.update(cell);
    }
    
    @Override
    protected void paint(Event event, Object element)
    {
        super.paint(event, element);
    }
    
    /**
     * Returns the style for the label. 
     *  
     * @param ResultFormatter formatter The result formatter
     * @return StyleRange[] The style
     */
    private StyleRange[] createStyle(ResultFormatter formatter)
    {
        StyleRange[] styleRanges = new StyleRange[1];
        styleRanges[0] = new StyleRange();

        if (formatter.accessAlert())
        {
            styleRanges[0].foreground = _colorRegistry.get("red");
        }
        else
        {
            styleRanges[0].foreground = _colorRegistry.get("black");
        }
        
        styleRanges[0].start = 0;
        styleRanges[0].length = formatter.actualAccess().length();
        
        return styleRanges;
    }
}