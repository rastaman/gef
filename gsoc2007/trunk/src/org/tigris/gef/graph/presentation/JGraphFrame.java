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

package org.tigris.gef.graph.presentation;

import org.tigris.gef.base.*;
import org.tigris.gef.graph.GraphModel;

/**
 * A window that displays a toolbar, a connected graph editing pane, and a
 * status bar.
 * 
 * @deprecated
 * @see org.tigris.gef.swing.JGraph
 */

public class JGraphFrame extends org.tigris.gef.swing.JGraphFrame {

    /**
     * Contruct a new JGraphFrame with the title "untitled" and a new
     * DefaultGraphModel.
     */
    public JGraphFrame() {
        super();
    }

    public JGraphFrame(boolean init_later) {
        super(init_later);
    }

    /**
     * Contruct a new JGraphFrame with the given title and a new
     * DefaultGraphModel.
     */
    public JGraphFrame(String title) {
        super(title);
    }

    public JGraphFrame(String title, Editor ed) {
        super(title, ed);
    }

    /**
     * Contruct a new JGraphFrame with the given title and given JGraph. All
     * JGraphFrame contructors call this one.
     */
    public JGraphFrame(String title, JGraph jg) {
        super(title, jg);
    }

    /**
     * Contruct a new JGraphFrame with the title "untitled" and the given
     * GraphModel.
     */
    public JGraphFrame(GraphModel gm) {
        super(gm);
    }
} /* end class JGraphFrame */
