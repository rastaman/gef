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




// File: GraphFrame.java
// Classes: GraphFrame
// Original Author: johnnycoding@gmail.com
// $Id: GraphFrame.java 1011 2007-05-29 17:12:49Z johnnycoding $

package org.tigris.gef.graph.presentation;

import java.awt.Dimension;

import javax.swing.JMenuBar;

import org.tigris.gef.event.ModeChangeEvent;
import org.tigris.gef.event.ModeChangeListener;
import org.tigris.gef.graph.GraphEdgeRenderer;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.graph.GraphNodeRenderer;
import org.tigris.gef.ui.IStatusBar;
import org.tigris.gef.ui.ToolBar;
import org.tigris.gef.graph.presentation.JGraph;

public interface GraphFrame {

    public void init();

    public void init(JGraph jg);

    public Object clone();

    ////////////////////////////////////////////////////////////////
    // accessors

    public Graph getGraph();

    public GraphEdgeRenderer getGraphEdgeRenderer();

    public GraphModel getGraphModel();

    public GraphNodeRenderer getGraphNodeRenderer();

    public JMenuBar getJMenuBar();

    public ToolBar getToolBar();

    ////////////////////////////////////////////////////////////////
    // ModeChangeListener implementation
    public void modeChange(ModeChangeEvent mce);

    public void setGraph(JGraph g);

    public void setGraphEdgeRenderer(GraphEdgeRenderer rend);

    public void setGraphModel(GraphModel gm);

    public void setGraphNodeRenderer(GraphNodeRenderer rend);

    public void setJMenuBar(JMenuBar mb);

    public void setToolBar(ToolBar tb);

    public void setVisible(boolean b);

    ////////////////////////////////////////////////////////////////
    // IStatusListener implementation

    /** Show a message in the statusbar. */
    public void showStatus(String msg);

}