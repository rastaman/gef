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

package org.tigris.gef.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class ToolBar extends org.tigris.gef.swing.SwingToolBar{
    public ToolBar() {
        super();
    }

    /* (non-Javadoc)
     * @see org.tigris.gef.ui.IToolBar#add(javax.swing.Action)
     */
    public JButton add(Action a) {
        return super.add(a);
    }

    /* (non-Javadoc)
     * @see org.tigris.gef.ui.IToolBar#add(javax.swing.Action, java.lang.String, java.lang.String)
     */
    public JButton add(Action a, String name, String iconResourceStr) {
        return super.add(a, name, iconResourceStr);
    }

    /* (non-Javadoc)
     * @see org.tigris.gef.ui.IToolBar#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent me) {
        super.mouseEntered(me);
    }

    /* (non-Javadoc)
     * @see org.tigris.gef.ui.IToolBar#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent me) {
        super.mouseExited(me);
    }

    /* (non-Javadoc)
     * @see org.tigris.gef.ui.IToolBar#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent me) {
        super.mousePressed(me);
    }

    /* (non-Javadoc)
     * @see org.tigris.gef.ui.IToolBar#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent me) {
        super.mouseReleased(me);
    }

    /* (non-Javadoc)
     * @see org.tigris.gef.ui.IToolBar#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent me) {
        super.mouseClicked(me);
    }
    /* (non-Javadoc)
     * @see org.tigris.gef.ui.IToolBar#unpressAllButtons()
     */
    public void unpressAllButtons() {
        super.unpressAllButtons();
    }

} /* end class ToolBar */
