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

// File: JPopupMenu.java
// Classes: JPopupMenu
// Original Author: johnnycoding 1996
// $Id: Editor.java 1019 2007-06-13 14:21:52Z johnnycoding $

package org.tigris.gef.swing;

import java.awt.Component;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import org.tigris.gef.base.CmdSave;
import org.tigris.gef.base.PopupMenu;
import org.tigris.gef.graph.presentation.GraphInternalPane;

/**
 * @see javax.swing.JPopupMenu
 * @see orgtigris.gef.base.PopupMenu
 */

public class JPopupMenu extends javax.swing.JPopupMenu implements org.tigris.gef.base.PopupMenu {

    private static final long serialVersionUID = -3989403606881437193L;
    /*
     * @see org.tigris.gef.base.PopupMenu#show()
     */
    public void show(GraphInternalPane popup, int x, int y)
    {
        super.show((Component)popup, x, y);
    }

    /*
     * @see org.tigris.gef.base.PopupMenu#add()
     */
    public void add(Object a)
    {
        if(a instanceof AbstractAction)
            this.add((AbstractAction)a);
        else if(a instanceof JMenu)
            this.add((JMenu)a);
        else if(a instanceof JMenuItem)
            this.add((JMenuItem)a);
        else if(a instanceof JSeparator)
            this.add((JSeparator)a);
    }
}
