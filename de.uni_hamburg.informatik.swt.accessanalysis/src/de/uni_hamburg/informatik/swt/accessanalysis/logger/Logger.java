/**
 * 
 */
package de.uni_hamburg.informatik.swt.accessanalysis.logger;

import java.io.PrintStream;

/**
 * Prints text to the console.
 */
public class Logger
{
    private static final ConsoleDisplayMgr _consoleDisplayMgr = new ConsoleDisplayMgr("AccessAnalysis Logger");
    
    static
    {
        System.setErr(new PrintStream(_consoleDisplayMgr.getNewMessageConsoleStream(LoggerMessageType.ERROR)));
    }
    
    /**
     * Prints one line of a given message type.
     *  
     * @param String msg The text to be printed
     * @param LoggerMessageType type The type of message to be printed
     */
    public static void println(String msg, LoggerMessageType type)
    {
        _consoleDisplayMgr.println(msg, type);
    }
}
