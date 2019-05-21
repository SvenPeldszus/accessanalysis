package de.uni_hamburg.informatik.swt.accessanalysis.results;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Returns empty strings as formatted results
 * and holds a static instance of DecimalFormat for the IGAT and IGAM 
 * values and one of JavaElementLabelProvider 
 */
abstract class AbstractResultFormatter
{
    static private DecimalFormat _decimalFormat;
    static private ILabelProvider _labelProvider;
    
    /**
     * Returns an empty string.
     * 
     * @return String <code>""</code>
     */
    public String actualAccess()
    {
        return "";
    }

    /**
     * Returns <code>null</code>.
     * 
     * @return Image null
     */
    public Image image()
    {
        return null;
    }

    /**
     * Returns an empty string.
     * 
     * @return String <code>""</code>
     */
    public String minimalAccess()
    {
        return "";
    }

    /**
     * Returns an empty string.
     * 
     * @return String <code>""</code>
     */
    public String igam()
    {
        return "";
    }

    /**
     * Returns an empty string.
     * 
     * @return String <code>""</code>
     */
    public String igat()
    {
        return "";
    }

    /**
     * Returns an empty string.
     * 
     * @return String <code>""</code>
     */
    public String name()
    {
        return "";
    }
    
    /**
     * Returns <code>false</code>.
     * 
     * @return boolean false
     */
    public boolean accessAlert()
    {
        return false;
    }
    
    @Override
    public String toString()
    {
        String string = name() + "\t" + igat() + "\t" + igam() + "\t" + minimalAccess() + "\t" + actualAccess();
        if (accessAlert())
        {
            string = string + " [!]";
        }
        
        return string;
    }
    
    /**
     * Returns the format for the metrics.
     * 
     * @return DecimalFormat The format
     */
    static DecimalFormat getDecimalFormat()
    {
        if (_decimalFormat == null)
        {
            _decimalFormat = new DecimalFormat("#,##0.00"); 
            _decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        }
        
        return _decimalFormat;
    }
    
    /**
     * Returns the image label provider for the result elements.
     * 
     * @return ILabelProvider The label provider
     */
    static ILabelProvider getLabelProvider()
    {
        if (_labelProvider == null)
        {
            _labelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_OVERLAY_ICONS | JavaElementLabelProvider.SHOW_PARAMETERS); 
        }
        
        return _labelProvider;
    }
}
