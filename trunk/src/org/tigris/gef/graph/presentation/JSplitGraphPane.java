package org.tigris.gef.graph.presentation;

import org.tigris.gef.swing.JGraph;

/**
 * A split pane container for showing two instances of the same graph.
 * 
 * @author Bob Tarling
 * @deprecated
 * @see org.tigris.gef.swing.JGraph
 */
public class JSplitGraphPane extends org.tigris.gef.swing.JSplitGraphPane {

	/**
	 * Construct a new split graph pane containing a graph
	 * 
	 * @param graph
	 */
	public JSplitGraphPane(JGraph graph) {
		super(graph);
	}
}
