/**
 * 
 */
package de.uni_hamburg.informatik.swt.accessanalysis.logger;

import org.eclipse.swt.SWT;

/**
 * Defines the color of a message printed to the console.
 */
public enum LoggerMessageType
{
    INFO(SWT.COLOR_BLACK),
    WARNING(SWT.COLOR_BLUE),
    ERROR(SWT.COLOR_RED);
    
    private final int _colorId;
    
    private LoggerMessageType(int colorId)
    {
        _colorId = colorId;
    }
    
    int getColor()
    {
        return _colorId;
    }
}
