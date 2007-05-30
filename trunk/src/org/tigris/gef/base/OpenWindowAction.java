// Copyright (c) 1996-06 The Regents of the University of California. All
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

// File: OpenWindowAction.java
// Classes: OpenWindowAction
// Original Author: andrea.nironi@gmail.com

package org.tigris.gef.base;

import javax.swing.Icon;

/**
 * Action to open a user interface dialog window. Given the name of a subclass
 * of Frame, this Action makes a new instance and calls show(). For example,
 * used to open a list of some availible commands.
 * 
 * @deprecated use org.tigris.gef.base.OpenWindowAction
 * @see org.tigris.gef.swing.OpenWindowAction
 * @see org.tigris.gef.graph.presentation.GraphFrame
 */
public class OpenWindowAction extends org.tigris.gef.swing.OpenWindowAction {

	/**
	 * Creates a new OpenWindowAction
	 * 
	 * @param name
	 * @param icon
	 * @param className
	 * @param localize
	 */
	public OpenWindowAction(String name, Icon icon, String className,
			boolean localize) {
		super(name, icon, className, localize);
	}

	/**
	 * Creates a new OpenWindowAction
	 * 
	 * @param name
	 * @param className
	 * @param localize
	 */
	public OpenWindowAction(String name, String className, boolean localize) {
		super(name, className, localize);
	}

	/**
	 * Creates a new OpenWindowAction
	 * 
	 * @param name
	 * @param className
	 * @param icon
	 */
	public OpenWindowAction(String name, String className, Icon icon) {
		super(name, className, icon);
	}

	/**
	 * Creates a new OpenWindowAction
	 * 
	 * @param name
	 * @param className
	 */
	public OpenWindowAction(String name, String className) {
		super(name, className);
	}
}
