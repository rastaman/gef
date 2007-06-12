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

import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelListener;

import org.tigris.gef.base.Diagram;
import org.tigris.gef.base.Editor;
import org.tigris.gef.graph.ConnectionConstrainer;
import org.tigris.gef.graph.GraphModel;

/**
 * JGraph is a Swing component that displays a connected graph and allows
 * interactive editing. In many ways this class serves as a simple front-end to
 * class Editor, and other classes which do the real work.
 * 
 * @deprecated use org.tigris.gef.graph.presentation.Graph
 * @see org.tigris.gef.swing.JGraph
 */

public class JGraph extends org.tigris.gef.swing.JGraph  {
    /**
     * Make a new JGraph with a new DefaultGraphModel.
     * 
     * @see org.tigris.gef.graph.presentation.DefaultGraphModel
     */
    public JGraph() {
	super();
    }

    /**
     * Make a new JGraph with a new DefaultGraphModel.
     * 
     * @see org.tigris.gef.graph.presentation.DefaultGraphModel
     */
    public JGraph(ConnectionConstrainer cc) {
	super(cc);
    }

    /**
     * Make a new JGraph with a the GraphModel and Layer from the given Diagram.
     */
    public JGraph(Diagram d) {
	super(d);
    }

    /** Make a new JGraph with the given GraphModel */
    public JGraph(GraphModel gm) {
	super(gm);
    }

    /**
     * Make a new JGraph with the given Editor. All JGraph contructors
     * eventually call this contructor.
     */
    public JGraph(Editor ed) {
	super(ed);
    }
}
