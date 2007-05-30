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

// File: SaveAction.java
// Classes: SaveAction
// Original Author: andrea.nironi@gmail.com

package org.tigris.gef.base;

import javax.swing.Icon;

/**
 * Action to save the current document to a binary file using Sun's
 * ObjectSerialization library. The written file contains the Editor object and
 * all objects reachable through instance variables of the Editor (e.g., the
 * selections, the views, the contents of the views, the net-level description
 * of the graph, etc.). UI objects such as Windows, Frames, Panels, and Images
 * are not stored because I have marked those instance variables as transient in
 * the source code.
 * <p>
 * 
 * One advantage of this approach to saving and loading is that developers using
 * GEF can add subclasses (e.g., to NetNode) which introduce new instance
 * variables, and those will be saved and loaded without the developers having
 * to special load and save methods. However, make sure that you do not point to
 * any AWT objects unless those instance variables are transient because those
 * cannot be saved.
 * <p>
 * 
 * Needs-More-Work: the files produced by a save are not really good for
 * anything other than reloading into this tool, or another Java program that
 * uses ObjectSerialization. At this time GEF provides no support for saving or
 * loading textual representations of documents that could be used in other
 * tools.
 * <p>
 * 
 * @deprecated use org.tigris.gef.base.SaveAction
 * @see org.tigris.gef.swing.SaveAction
 * @see OpenAction
 */
public class SaveAction extends org.tigris.gef.swing.SaveAction {

	/**
	 * Creates a new SaveAction
	 * 
	 */
	public SaveAction() {
		super();
	}

	/**
	 * Creates a new SaveAction
	 * 
	 * @param name
	 * @param localize
	 */
	public SaveAction(String name, boolean localize) {
		super(name, localize);
	}

	/**
	 * Creates a new SaveAction
	 * 
	 * @param name
	 * @param icon
	 * @param localize
	 */
	public SaveAction(String name, Icon icon, boolean localize) {
		super(name, icon, localize);
	}

	/**
	 * Creates a new SaveAction
	 * 
	 * @param name
	 * @param icon
	 */
	public SaveAction(String name, Icon icon) {
		super(name, icon);
	}

	/**
	 * Creates a new SaveAction
	 * 
	 * @param name
	 */
	public SaveAction(String name) {
		super(name);
	}

}
