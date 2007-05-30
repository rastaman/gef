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

// File: OpenPGMLAction.java
// Classes: OpenPGMLAction
// Original Author: andrea.nironi@gmail.com

package org.tigris.gef.base;

import java.awt.Dimension;

import javax.swing.Icon;

/**
 * Action to Load a previously saved document document. The loaded editor is
 * displayed in a new JGraphFrame.
 * 
 * @deprecated use org.tigris.gef.base.OpenPGMLAction
 * @see org.tigris.gef.swing.OpenPGMLAction
 * @see SaveAction
 */
public class OpenPGMLAction extends org.tigris.gef.swing.OpenPGMLAction {

	/**
     * Creates a new OpenPGMLAction
	 * @param name
	 * @param dimension
	 * @param localize
	 */
	public OpenPGMLAction(String name, Dimension dimension, boolean localize) {
		super(name, dimension, localize);
	}

	/**
     * Creates a new OpenPGMLAction
	 * @param name
	 * @param dimension
	 */
	public OpenPGMLAction(String name, Dimension dimension) {
		super(name, dimension);
	}

	/**
     * Creates a new OpenPGMLAction
	 * @param name
	 * @param icon
	 * @param dimension
	 * @param localize
	 */
	public OpenPGMLAction(String name, Icon icon, Dimension dimension, boolean localize) {
		super(name, icon, dimension, localize);
	}

	/**
     * Creates a new OpenPGMLAction
	 * @param name
	 * @param icon
	 * @param dimension
	 */
	public OpenPGMLAction(String name, Icon icon, Dimension dimension) {
		super(name, icon, dimension);
	}

	/**
     * Creates a new OpenPGMLAction
	 * @param name
	 * @param icon
	 */
	public OpenPGMLAction(String name, Icon icon) {
		super(name, icon);
	}

	/**
     * Creates a new OpenPGMLAction
	 * @param name
	 */
	public OpenPGMLAction(String name) {
		super(name);
	}
}
