package org.tigris.gef.graph.presentation;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Vector;

import org.tigris.gef.base.Diagram;
import org.tigris.gef.base.Editor;
import org.tigris.gef.event.GraphSelectionListener;
import org.tigris.gef.event.ModeChangeListener;
import org.tigris.gef.graph.GraphEdgeRenderer;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.graph.GraphNodeRenderer;
import org.tigris.gef.presentation.Fig;

public interface Graph extends Cloneable, AdjustmentListener, MouseWheelListener {

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o);

	/**
	 * @see Object#hashCode()
	 *
	 * TODO: Investigate further:<p>
	 *
	 * According to a mail from GZ (6th November 2004) on the ArgoUML dev list,
	 * {@link javax.swing.RepaintManager} puts these objects in
	 * some kind of data structure that uses this function.
	 * Assuming that there is a reason for this we dare not sabotage
	 * this by short-circuiting this to 0. Instead we rely on that
	 * {@link org.tigris.gef.graph.presentation.Graph#setDiagram(
	 * org.tigris.gef.base.Diagram)} actually removes this object from
	 * the {@link javax.swing.RepaintManager} and registers it again
	 * when resetting the diagram id.<p>
	 *
	 * This is based on the assumption that the function
	 * {@link #equals(Object)} must work as it does. I (Linus) have not
	 * understood why it must. Could someone please explain that in the
	 * javadoc.
	 */
	public int hashCode();

	public void addMouseListener(MouseListener listener);

	public void addMouseMotionListener(MouseMotionListener listener);

	public void addKeyListener(KeyListener listener);

	/** Make a copy of this JGraph so that it can be shown in another window. */
	public Object clone();

	/* Set up some standard keystrokes and the Cmds that they invoke. */
	public void initKeys();

	/**
	 * Utility function to bind a keystroke to a Swing Action. Note that GEF
	 * Cmds are subclasses of Swing's Actions.
	 */
	public void bindKey(ActionListener action, int keyCode, int modifiers);

	/** Get the Editor that is being displayed */
	public Editor getEditor();

	/**
	 * Set the Diagram that should be displayed by setting the GraphModel and
	 * Layer that the Editor is using.
	 */
	public void setDiagram(Diagram d);

	public void setDrawingSize(int width, int height);

	public void setDrawingSize(Dimension dim);

	/**
	 * Set the GraphModel the Editor is using.
	 */
	public void setGraphModel(GraphModel gm);

	/**
	 * Get the GraphModel the Editor is using.
	 */
	public GraphModel getGraphModel();

	/**
	 * Get and set the Renderer used to make FigNodes for nodes in the
	 * GraphModel.
	 */
	public void setGraphNodeRenderer(GraphNodeRenderer r);

	public GraphNodeRenderer getGraphNodeRenderer();

	/**
	 * Get and set the Renderer used to make FigEdges for edges in the
	 * GraphModel.
	 */
	public void setGraphEdgeRenderer(GraphEdgeRenderer r);

	public GraphEdgeRenderer getGraphEdgeRenderer();

	/**
	 * When the JGraph is hidden, hide its internal pane
	 */
	public void setVisible(boolean visible);

	/**
	 * Tell Swing/AWT that JGraph handles tab-order itself.
	 */
	public boolean isManagingFocus();

	/**
	 * Tell Swing/AWT that JGraph can be tabbed into.
	 */
	public boolean isFocusTraversable();

	/**
	 * Add listener to the objects to notify whenever the Editor changes its
	 * current selection.
	 */
	public void addGraphSelectionListener(GraphSelectionListener listener);

	public void removeGraphSelectionListener(GraphSelectionListener listener);

	public void addModeChangeListener(ModeChangeListener listener);

	public void removeModeChangeListener(ModeChangeListener listener);

	////////////////////////////////////////////////////////////////
	// selection methods
	/**
	 * Add the given item to this Editor's selections.
	 */
	public void select(Fig f);

	/**
	 * Find the Fig that owns the given item and select it.
	 */
	public void selectByOwner(Object owner);

	/**
	 * Find Fig that owns the given item, or the item if it is a Fig, and select
	 * it.
	 */
	public void selectByOwnerOrFig(Object owner);

	/**
	 * Add the Fig that owns the given item to this Editor's selections.
	 */
	public void selectByOwnerOrNoChange(Object owner);

	/**
	 * Remove the given item from this editors selections.
	 */
	public void deselect(Fig f);

	/**
	 * Select the given item if it was not already selected, and vis-a-versa.
	 */
	public void toggleItem(Fig f);

	/** Deslect everything that is currently selected. */
	public void deselectAll();

	/** Select a collection of Figs. */
	public void select(Vector items);

	/** Toggle the selection of a collection of Figs. */
	public void toggleItems(Vector items);

	/** reply a Vector of all selected Figs. Used in many Cmds. */
	public Vector selectedFigs();

	public void setDefaultSize(int width, int height);

	public void setDefaultSize(Dimension dim);

	public Dimension getDefaultSize();

	/** Get the position of the editor's scrollpane. */
	public Point getViewPosition();

	/** Set the position of the editor's scrollpane. */
	public void setViewPosition(Point p);

	/**
	 * Establishes alternate MouseWheelListener object that's only active
	 * when the alt/shift/ctrl keys are held down.
	 *	
	 * @param listener MouseWheelListener that will receive MouseWheelEvents
	 *                 generated by this JGraph.
	 * @param mask logical OR of key modifier values as defined by
	 *             java.awt.event.KeyEvent constants. This has been tested with
	 *             ALT_MASK, SHIFT_MASK, and CTRL_MASK.
	 */
	public void establishAlternateMouseWheelListener(
			MouseWheelListener listener, int mask);

	public void adjustmentValueChanged(AdjustmentEvent e);

	/**
	 *  Zooms diagram in and out when mousewheel is rolled while holding down
	 *  ctrl and/or alt key.
	 *  Alt, because alt + mouse motion pans the diagram & zooming while panning 
	 *      makes more sense than scrolling while panning.
	 *  Ctrl, because Ctrl/+ and Ctrl/- are used to zoom using the keyboard.
	 */
	public void mouseWheelMoved(MouseWheelEvent e);

}