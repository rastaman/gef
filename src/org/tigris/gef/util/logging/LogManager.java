// Copyright (c) 1996-99 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

// File: LogManager.java
// Classes: LogManager
// Original Author: psager@tigris.org Sep 2001
// $Id$

package org.tigris.gef.util.logging;

import org.apache.log4j.*;

/**
 * This class is based on the LogManager for ArgoUML initially provided by Phil Sager
 * and enhanced by Thorsten Sturm for Gentleware. It mainly enables logging on both
 * the console and a log file. The location of the log file as well as the logging 
 * priority can be configured.
 * @deprecated 0.11 we will use standard log4j
 */

public class LogManager {
  
	/** Standard definition of the logging category for the console.
	 */
	public static final String CONSOLE_LOG = "gef.console.log";
	
	/** Standard definition of system variable to add text prefix to console log.
	 */
	public static final String CONSOLE_PREFIX = "gef.console.prefix";
	
	/** Define a static log4j category variable for GEF to log to
	 * the console.
	 */
	public final static Category log;
	
	/** Don't let this be instantiated. */
	private LogManager() {
    }
	
	/** Instance initialization to create
	 *  logging category <code>@product.small@.console.log</code>.
         * @deprecated 0.11 we will use standard log4j
	 */
	static {
            Category newCategory = Category.getInstance("GEF");
            String priority = (String)System.getProperty("gef.log.level","INFO");
            if (priority == null || priority.length() <= 0)
                priority = "INFO";
            newCategory.setPriority(Priority.toPriority(priority));
            newCategory.addAppender(new ConsoleAppender(new PatternLayout(System.getProperty(CONSOLE_PREFIX, "")+ "%m%n"),
            ConsoleAppender.SYSTEM_OUT));
            // comented out to prevent file to be created
            /*
            try {
                RollingFileAppender rolling = new RollingFileAppender(new PatternLayout("%-7r %-5p[%t]: %m%n"),
                                                                      System.getProperty("gef.log.location", 
                                                                      System.getProperty("user.home")+"/GEF.log"),
                                                                      true);
                rolling.setMaxFileSize("2MB");
                newCategory.addAppender(rolling);
            }
            catch(Exception e) {
                System.err.println("[LogManager] log file can't be created");
            }
            */
            log = newCategory;
	}
}


