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
import org.tigris.gef.swing.JGraph;

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