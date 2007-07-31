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




// File: Graph.java
// Classes: Graph
// Original Author: johnnycoding@gmail.com
// $Id: Graph.java 1011 2007-05-29 17:12:49Z johnnycoding $

package org.tigris.gef.graph.presentation;

import java.awt.Dimension;

import org.tigris.gef.base.Editor;
import org.tigris.gef.event.ModeChangeListener;
import org.tigris.gef.graph.GraphEdgeRenderer;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.graph.GraphNodeRenderer;

public interface Graph  {
    public void setGraphModel(GraphModel gm); 
    public void addModeChangeListener(ModeChangeListener mcl);
    public Editor getEditor();
    public GraphModel getGraphModel();
    public void setGraphNodeRenderer(GraphNodeRenderer gnr); 
    public void setGraphEdgeRenderer(GraphEdgeRenderer ger);
	public void setSize(Dimension drawAreaSize);
	public void setPreferredSize(Dimension drawAreaSize);
}