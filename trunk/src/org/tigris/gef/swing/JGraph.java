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

package org.tigris.gef.swing;

import java.awt.AWTEventMulticaster;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentListener;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyListener;
import java.awt.event.InputEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;

import org.tigris.gef.base.NudgeAction;
import org.tigris.gef.base.SelectNextAction;
import org.tigris.gef.base.Diagram;
import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.Layer;
import org.tigris.gef.base.LayerDiagram;
import org.tigris.gef.base.SelectNearAction;
import org.tigris.gef.base.ZoomAction;
import org.tigris.gef.event.GraphSelectionListener;
import org.tigris.gef.event.ModeChangeListener;
import org.tigris.gef.graph.ConnectionConstrainer;
import org.tigris.gef.graph.GraphEdgeRenderer;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.graph.GraphNodeRenderer;
import org.tigris.gef.graph.presentation.DefaultGraphModel;
import org.tigris.gef.graph.presentation.Graph;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigText;
import org.tigris.gef.presentation.FigTextEditor;
import org.tigris.gef.presentation.TextEditor;
import org.tigris.gef.graph.presentation.GraphInternalPane;

/**
 * JGraph is a Swing component that displays a connected graph and allows
 * interactive editing. In many ways this class serves as a simple front-end to
 * class Editor, and other classes which do the real work.
 */

public class JGraph extends JPanel implements Graph, Cloneable, AdjustmentListener,
MouseWheelListener {

    /**
     * The Editor object that is being shown in this panel
     */
    private Editor editor;

    private JGraphInternalPane drawingPane;

    private JScrollPane scrollPane;

    private Dimension defaultSize = new Dimension(6000, 6000);

    private Hashtable _viewPortPositions = new Hashtable();

    private String _currentDiagramId = null;

    private ZoomAction zoomOut = new ZoomAction(0.9);

    private ZoomAction zoomIn = new ZoomAction(1.1);

    // //////////////////////////////////////////////////////////////
    // constructor

    /**
     * Make a new JGraph with a new DefaultGraphModel.
     * 
     * @see org.tigris.gef.graph.presentation.DefaultGraphModel
     */
    public JGraph() {
	this(new DefaultGraphModel());
    }

    /**
     * Make a new JGraph with a new DefaultGraphModel.
     * 
     * @see org.tigris.gef.graph.presentation.DefaultGraphModel
     */
    public JGraph(ConnectionConstrainer cc) {
	this(new DefaultGraphModel(cc));
    }

    /**
     * Make a new JGraph with a the GraphModel and Layer from the given Diagram.
     */
    public JGraph(Diagram d) {
	this(new Editor(d));
    }

    /** Make a new JGraph with the given GraphModel */
    public JGraph(GraphModel gm) {
	this(new Editor(gm, null));
    }

    /**
     * Make a new JGraph with the given Editor. All JGraph contructors
     * eventually call this contructor.
     */
    public JGraph(Editor ed) {
	super(false); // not double buffered. I do my own flicker-free redraw.
	editor = ed;
	drawingPane = new JGraphInternalPane(editor);
	setDrawingSize(getDefaultSize());

	scrollPane = new JScrollPane(drawingPane);

	scrollPane.setBorder(null);
	scrollPane.getHorizontalScrollBar().setUnitIncrement(25);
	scrollPane.getVerticalScrollBar().setUnitIncrement(25);

	editor.setJComponent(drawingPane);
	setLayout(new BorderLayout());
	add(scrollPane, BorderLayout.CENTER);
	addMouseListener(editor);
	addMouseMotionListener(editor);
	addKeyListener(editor);
	scrollPane.getHorizontalScrollBar().addAdjustmentListener(this);
	scrollPane.getVerticalScrollBar().addAdjustmentListener(this);

	initKeys();

	validate();

	Collection layerManagerContent = ed.getLayerManager().getContents();
	if (layerManagerContent != null) {
	    updateDrawingSizeToIncludeAllFigs(Collections
		    .enumeration(layerManagerContent));
	} // end if

	int mask = java.awt.event.KeyEvent.ALT_MASK
		| java.awt.event.KeyEvent.CTRL_MASK;
	establishAlternateMouseWheelListener(this, mask);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
	if (o instanceof JGraph) {
	    JGraph other = (JGraph) o;
	    if (((this.getCurrentDiagramId() != null && this
		    .getCurrentDiagramId().equals(other.getCurrentDiagramId())) || (this
		    .getCurrentDiagramId() == null && other
		    .getCurrentDiagramId() == null))
		    && this.getEditor().equals(other.getEditor())) {
		return true;
	    }
	}
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#hashCode()
     */
    public int hashCode() {
	if (getCurrentDiagramId() == null) {
	    return 0;
	} else {
	    return getCurrentDiagramId().hashCode();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#addMouseListener(java.awt.event.MouseListener)
     */
    public void addMouseListener(org.tigris.gef.base.MouseListener listener) {
	drawingPane.addMouseListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#addMouseMotionListener(java.awt.event.MouseMotionListener)
     */
    public void addMouseMotionListener(
	    org.tigris.gef.base.MouseMotionListener listener) {
	drawingPane.addMouseMotionListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#addKeyListener(java.awt.event.KeyListener)
     */
    public void addKeyListener(org.tigris.gef.base.KeyListener listener) {
	drawingPane.addKeyListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#clone()
     */
    public Object clone() {
	Graph newJGraph = new JGraph((Editor) editor.clone());
	return newJGraph;
    }

    /* Set up some standard keystrokes and the Cmds that they invoke. */
    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#initKeys()
     */
    public void initKeys() {
	int shift = KeyEvent.SHIFT_MASK;
	int alt = KeyEvent.ALT_MASK;
	int meta = KeyEvent.META_MASK;

	bindKey(new SelectNextAction("Select Next", true), KeyEvent.VK_TAB, 0);
	bindKey(new SelectNextAction("Select Previous", false),
		KeyEvent.VK_TAB, shift);

	bindKey(new NudgeAction(NudgeAction.LEFT), KeyEvent.VK_LEFT, 0);
	bindKey(new NudgeAction(NudgeAction.RIGHT), KeyEvent.VK_RIGHT, 0);
	bindKey(new NudgeAction(NudgeAction.UP), KeyEvent.VK_UP, 0);
	bindKey(new NudgeAction(NudgeAction.DOWN), KeyEvent.VK_DOWN, 0);

	bindKey(new NudgeAction(NudgeAction.LEFT, 8), KeyEvent.VK_LEFT, shift);
	bindKey(new NudgeAction(NudgeAction.RIGHT, 8), KeyEvent.VK_RIGHT, shift);
	bindKey(new NudgeAction(NudgeAction.UP, 8), KeyEvent.VK_UP, shift);
	bindKey(new NudgeAction(NudgeAction.DOWN, 8), KeyEvent.VK_DOWN, shift);

	bindKey(new NudgeAction(NudgeAction.LEFT, 18), KeyEvent.VK_LEFT, alt);
	bindKey(new NudgeAction(NudgeAction.RIGHT, 18), KeyEvent.VK_RIGHT, alt);
	bindKey(new NudgeAction(NudgeAction.UP, 18), KeyEvent.VK_UP, alt);
	bindKey(new NudgeAction(NudgeAction.DOWN, 18), KeyEvent.VK_DOWN, alt);

	bindKey(new SelectNearAction(SelectNearAction.LEFT), KeyEvent.VK_LEFT,
		meta);
	bindKey(new SelectNearAction(SelectNearAction.RIGHT),
		KeyEvent.VK_RIGHT, meta);
	bindKey(new SelectNearAction(SelectNearAction.UP), KeyEvent.VK_UP, meta);
	bindKey(new SelectNearAction(SelectNearAction.DOWN), KeyEvent.VK_DOWN,
		meta);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#bindKey(java.awt.event.ActionListener,
     *      int, int)
     */
    public void bindKey(ActionListener action, int keyCode, int modifiers) {
	drawingPane.registerKeyboardAction(action, KeyStroke.getKeyStroke(
		keyCode, modifiers), WHEN_FOCUSED);
    }

    // //////////////////////////////////////////////////////////////
    // accessors

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#getEditor()
     */
    public Editor getEditor() {
	return editor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#setDiagram(org.tigris.gef.base.Diagram)
     */
    public void setDiagram(Diagram d) {
	if (d == null)
	    return;
	if (_currentDiagramId != null) {
	    _viewPortPositions.put(_currentDiagramId, scrollPane.getViewport()
		    .getViewRect());
	} // end if
	setDrawingSize(getDefaultSize());
	updateDrawingSizeToIncludeAllFigs(d.elements());
	editor.getLayerManager().replaceActiveLayer(d.getLayer());
	editor.setGraphModel(d.getGraphModel());
	editor.getSelectionManager().deselectAll();
	editor.setScale(d.getScale());
	String newDiagramId = Integer.toString(d.hashCode());
	if (newDiagramId.equals(_currentDiagramId)) {
	    return;
	}
	_currentDiagramId = newDiagramId;
	if (_viewPortPositions.get(_currentDiagramId) != null) {
	    Rectangle rect = (Rectangle) _viewPortPositions
		    .get(_currentDiagramId);
	    scrollPane.getViewport().setViewPosition(new Point(rect.x, rect.y));
	} else {
	    scrollPane.getViewport().setViewPosition(new Point());
	}
    }

    /**
     * Enlarges the JGraphInternalPane dimensions as necessary to insure that
     * all the contained Figs are visible.
     */
    protected void updateDrawingSizeToIncludeAllFigs(Enumeration iter) {
	if (iter == null) {
	    return;
	}
	Dimension drawingSize = new Dimension(defaultSize.width,
		defaultSize.height);
	while (iter.hasMoreElements()) {
	    Fig fig = (Fig) iter.nextElement();
	    Rectangle rect = fig.getBounds();
	    Point point = rect.getLocation();
	    Dimension dim = rect.getSize();
	    if ((point.x + dim.width + 5) > drawingSize.width) {
		drawingSize
			.setSize(point.x + dim.width + 5, drawingSize.height);
	    }
	    if ((point.y + dim.height + 5) > drawingSize.height) {
		drawingSize
			.setSize(drawingSize.width, point.y + dim.height + 5);
	    }
	}
	setDrawingSize(drawingSize.width, drawingSize.height);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#setDrawingSize(int, int)
     */
    public void setDrawingSize(int width, int height) {
	setDrawingSize(new Dimension(width, height));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#setDrawingSize(java.awt.Dimension)
     */
    public void setDrawingSize(Dimension dim) {
	editor.drawingSizeChanged(dim);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#setGraphModel(org.tigris.gef.graph.GraphModel)
     */
    public void setGraphModel(GraphModel gm) {
	editor.setGraphModel(gm);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#getGraphModel()
     */
    public GraphModel getGraphModel() {
	return editor.getGraphModel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#setGraphNodeRenderer(org.tigris.gef.graph.GraphNodeRenderer)
     */
    public void setGraphNodeRenderer(GraphNodeRenderer r) {
	editor.setGraphNodeRenderer(r);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#getGraphNodeRenderer()
     */
    public GraphNodeRenderer getGraphNodeRenderer() {
	return editor.getGraphNodeRenderer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#setGraphEdgeRenderer(org.tigris.gef.graph.GraphEdgeRenderer)
     */
    public void setGraphEdgeRenderer(GraphEdgeRenderer r) {
	editor.setGraphEdgeRenderer(r);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#getGraphEdgeRenderer()
     */
    public GraphEdgeRenderer getGraphEdgeRenderer() {
	return editor.getGraphEdgeRenderer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#setVisible(boolean)
     */
    public void setVisible(boolean visible) {
	super.setVisible(visible);
	drawingPane.setVisible(visible);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#isManagingFocus()
     */
    public boolean isManagingFocus() {
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#isFocusTraversable()
     */
    public boolean isFocusTraversable() {
	return true;
    }

    // //////////////////////////////////////////////////////////////
    // events

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#addGraphSelectionListener(org.tigris.gef.event.GraphSelectionListener)
     */
    public void addGraphSelectionListener(GraphSelectionListener listener) {
	getEditor().addGraphSelectionListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#removeGraphSelectionListener(org.tigris.gef.event.GraphSelectionListener)
     */
    public void removeGraphSelectionListener(GraphSelectionListener listener) {
	getEditor().removeGraphSelectionListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#addModeChangeListener(org.tigris.gef.event.ModeChangeListener)
     */
    public void addModeChangeListener(ModeChangeListener listener) {
	getEditor().addModeChangeListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#removeModeChangeListener(org.tigris.gef.event.ModeChangeListener)
     */
    public void removeModeChangeListener(ModeChangeListener listener) {
	getEditor().removeModeChangeListener(listener);
    }

    // //////////////////////////////////////////////////////////////
    // Editor facade

    /**
     * The JGraph is painted by simply painting its Editor.
     */
    // public void paint(Graphics g) { _editor.paint(getGraphics()); }
    // //////////////////////////////////////////////////////////////
    // selection methods
    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#select(org.tigris.gef.presentation.Fig)
     */
    public void select(Fig f) {
	if (f == null)
	    deselectAll();
	else
	    editor.getSelectionManager().select(f);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#selectByOwner(java.lang.Object)
     */
    public void selectByOwner(Object owner) {
	Layer lay = editor.getLayerManager().getActiveLayer();
	if (lay instanceof LayerDiagram)
	    select(((LayerDiagram) lay).presentationFor(owner));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#selectByOwnerOrFig(java.lang.Object)
     */
    public void selectByOwnerOrFig(Object owner) {
	if (owner instanceof Fig)
	    select((Fig) owner);
	else
	    selectByOwner(owner);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#selectByOwnerOrNoChange(java.lang.Object)
     */
    public void selectByOwnerOrNoChange(Object owner) {
	Layer lay = editor.getLayerManager().getActiveLayer();
	if (lay instanceof LayerDiagram) {
	    Fig f = ((LayerDiagram) lay).presentationFor(owner);
	    if (f != null)
		select(f);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#deselect(org.tigris.gef.presentation.Fig)
     */
    public void deselect(Fig f) {
	editor.getSelectionManager().deselect(f);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#toggleItem(org.tigris.gef.presentation.Fig)
     */
    public void toggleItem(Fig f) {
	editor.getSelectionManager().toggle(f);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#deselectAll()
     */
    public void deselectAll() {
	editor.getSelectionManager().deselectAll();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#select(java.util.Vector)
     */
    public void select(Vector items) {
	editor.getSelectionManager().select(items);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#toggleItems(java.util.Vector)
     */
    public void toggleItems(Vector items) {
	editor.getSelectionManager().toggle(items);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#selectedFigs()
     */
    public Vector selectedFigs() {
	return editor.getSelectionManager().getFigs();
    }

    // public Dimension getPreferredSize() { return new Dimension(1000, 1000); }

    // public Dimension getMinimumSize() { return new Dimension(1000, 1000); }

    // public Dimension getSize() { return new Dimension(1000, 1000); }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#setDefaultSize(int, int)
     */
    public void setDefaultSize(int width, int height) {
	defaultSize = new Dimension(width, height);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#setDefaultSize(java.awt.Dimension)
     */
    public void setDefaultSize(Dimension dim) {
	defaultSize = dim;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#getDefaultSize()
     */
    public Dimension getDefaultSize() {
	return defaultSize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#getViewPosition()
     */
    public Point getViewPosition() {
	return scrollPane.getViewport().getViewPosition();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#setViewPosition(java.awt.Point)
     */
    public void setViewPosition(Point p) {
	if (p != null)
	    scrollPane.getViewport().setViewPosition(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#establishAlternateMouseWheelListener(java.awt.event.MouseWheelListener,
     *      int)
     */
    public void establishAlternateMouseWheelListener(
	    MouseWheelListener listener, int mask) {

	WheelKeyListenerToggleAction keyListener = new WheelKeyListenerToggleAction(
		this.drawingPane, listener, mask);

	this.drawingPane.addKeyListener(keyListener);
    }

    static final long serialVersionUID = -5459241816919316496L;

    /**
     * @return Returns the _currentDiagramId.
     */
    protected String getCurrentDiagramId() {
	return _currentDiagramId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#adjustmentValueChanged(java.awt.event.AdjustmentEvent)
     */
    public void adjustmentValueChanged(AdjustmentEvent e) {
	TextEditor textEditor = FigText.getActiveTextEditor();
	if (textEditor != null) {
	    textEditor.endEditing();
	}
	editor.damageAll();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tigris.gef.graph.presentation.Graph#mouseWheelMoved(java.awt.event.MouseWheelEvent)
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
	if (e.isAltDown() || e.isControlDown()) {

	    if (e.getWheelRotation() < 0)
		this.zoomOut.actionPerformed(null);
	    else if (e.getWheelRotation() > 0)
		this.zoomIn.actionPerformed(null);

	    e.consume();
	}
    }
} /* end class JGraph */

class JGraphInternalPane extends JPanel implements GraphInternalPane,
	MouseListener, MouseMotionListener, KeyListener {

    private Editor _editor;

    private boolean registeredWithTooltip;

    private ArrayList<EventListener> eventListeners;

    public JGraphInternalPane(Editor e) {
	_editor = e;
	setLayout(null);
	setDoubleBuffered(false);

	//make it listening for itself 
	addMouseListener(this);
	addMouseMotionListener(this);
	addKeyListener(this);

	//initiate the eventListeners ArrayList
	eventListeners = new ArrayList<EventListener>();
    }

    public void paintComponent(Graphics g) {
	_editor.paint(g);
    }

    public Graphics getGraphics() {
	Graphics res = super.getGraphics();
	if (res == null) {
	    return res;
	}
	Component parent = getParent();

	if (parent instanceof JViewport) {
	    JViewport view = (JViewport) parent;
	    Rectangle bounds = view.getBounds();
	    Point pos = view.getViewPosition();
	    res.clipRect(bounds.x + pos.x - 1, bounds.y + pos.y - 1,
		    bounds.width + 1, bounds.height + 1);
	}
	return res;
    }

    public Point getToolTipLocation(MouseEvent event) {
        event = retranslateMouseEvent(event);
        return (super.getToolTipLocation(event));
    }

    /** Scales the mouse coordinates (which match the model scale)
     * back to the drawing scale. */
    public java.awt.event.MouseEvent retranslateMouseEvent(
            java.awt.event.MouseEvent me) {
        double xp = me.getX();
        double yp = me.getY();
        int dx = (int) (xp * Globals.curEditor().getScale() - xp);
        int dy = (int) (yp * Globals.curEditor().getScale() - yp);
        me.translatePoint(dx, dy);
        return me;
    }
        
    public void setToolTipText(String text) {
	if ("".equals(text))
	    text = null;
	putClientProperty(TOOL_TIP_TEXT_KEY, text);
	ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
	// if (text != null) {
	if (!registeredWithTooltip) {
	    toolTipManager.registerComponent(this);
	    registeredWithTooltip = true;
	}
    }

    protected void processMouseEvent(MouseEvent e) {
	if (e.getID() == MouseEvent.MOUSE_PRESSED) {
	    requestFocus();
	}

	super.processMouseEvent(e);
    }

    /** Tell Swing/AWT that JGraph handles tab-order itself. */
    public boolean isManagingFocus() {
	return true;
    }

    /** Tell Swing/AWT that JGraph can be tabbed into. */
    public boolean isFocusTraversable() {
	return true;
    }

    /** Overload the addMouseListener to support GEF's own MouseListener 
     * @see java.awt.Component.addMouseListener
     * */
    public void addMouseListener(org.tigris.gef.base.MouseListener listener) {
	eventListeners.add(listener);
    }

    /** Overload the addMouseListener to support GEF's own MouseMotionListener
     * @see java.awt.Component.addMouseMotionListener
     *  */
    public void addMouseMotionListener(
	    org.tigris.gef.base.MouseMotionListener listener) {
	eventListeners.add(listener);
    }

    /** Overload the addMouseListener to support GEF's own KeyListener 
     * @see java.awt.Component.addKeyListener
     *  */
    public void addKeyListener(org.tigris.gef.base.KeyListener listener) {
	eventListeners.add(listener);
    }

    static final long serialVersionUID = -5067026168452437942L;

    /**
     * Invoked when the mouse button has been clicked (pressed and released) 
     * Iterate all the registered MouseListeners and pass a new SwingMouseEventWrapper 
     * which wraps the awt MouseEvent to their mouseClicked methods 
     */
    public void mouseClicked(MouseEvent e) {
	for (EventListener el : eventListeners) {
	    if (el.getClass() == org.tigris.gef.base.MouseListener.class)
		((org.tigris.gef.base.MouseListener) el)
			.mouseClicked(new SwingMouseEventWrapper(e));
	}
	;
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent e) {
	for (EventListener el : eventListeners) {
	    if (el.getClass() == org.tigris.gef.base.MouseListener.class)
		((org.tigris.gef.base.MouseListener) el)
			.mousePressed(new SwingMouseEventWrapper(e));
	}
	;
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {
	for (EventListener el : eventListeners) {
	    if (el.getClass() == org.tigris.gef.base.MouseListener.class)
		((org.tigris.gef.base.MouseListener) el)
			.mouseReleased(new SwingMouseEventWrapper(e));
	}
	;
    }

    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent e) {
	for (EventListener el : eventListeners) {
	    if (el.getClass() == org.tigris.gef.base.MouseListener.class)
		((org.tigris.gef.base.MouseListener) el)
			.mouseEntered(new SwingMouseEventWrapper(e));
	}
	;
    }

    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {
	for (EventListener el : eventListeners) {
	    if (el.getClass() == org.tigris.gef.base.MouseListener.class)
		((org.tigris.gef.base.MouseListener) el)
			.mouseExited(new SwingMouseEventWrapper(e));
	}
	;
    }

    /**
     * Invoked when a mouse button is pressed on a component and then 
     * dragged.  <code>MOUSE_DRAGGED</code> events will continue to be 
     * delivered to the component where the drag originated until the 
     * mouse button is released (regardless of whether the mouse position 
     * is within the bounds of the component).
     * <p> 
     * Due to platform-dependent Drag&Drop implementations, 
     * <code>MOUSE_DRAGGED</code> events may not be delivered during a native 
     * Drag&Drop operation.  
     */
    public void mouseDragged(MouseEvent e) {
	for (EventListener el : eventListeners) {
	    if (el.getClass() == org.tigris.gef.base.MouseMotionListener.class)
		((org.tigris.gef.base.MouseMotionListener) el)
			.mouseDragged(new SwingMouseEventWrapper(e));
	}
	;
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component
     * but no buttons have been pushed.
     */
    public void mouseMoved(MouseEvent e) {
	for (EventListener el : eventListeners) {
	    if (el.getClass() == org.tigris.gef.base.MouseMotionListener.class)
		((org.tigris.gef.base.MouseMotionListener) el)
			.mouseMoved(new SwingMouseEventWrapper(e));
	}
	;
    }

    /**
     * Invoked when a key has been typed.
     * See the class description for {@link KeyEvent} for a definition of 
     * a key typed event.
     */
    public void keyTyped(KeyEvent e) {
	for (EventListener el : eventListeners) {
	    if (el.getClass() == org.tigris.gef.base.KeyListener.class)
		((org.tigris.gef.base.KeyListener) el)
			.keyTyped(new SwingKeyEventWrapper(e));
	}
	;
    }

    /**
     * Invoked when a key has been pressed. 
     * See the class description for {@link KeyEvent} for a definition of 
     * a key pressed event.
     */
    public void keyPressed(KeyEvent e) {
	for (EventListener el : eventListeners) {
	    if (el.getClass() == org.tigris.gef.base.KeyListener.class)
		((org.tigris.gef.base.KeyListener) el)
			.keyPressed(new SwingKeyEventWrapper(e));
	}
	;
    }

    /**
     * Invoked when a key has been released.
     * See the class description for {@link KeyEvent} for a definition of 
     * a key released event.
     */
    public void keyReleased(KeyEvent e) {
	for (EventListener el : eventListeners) {
	    if (el.getClass() == org.tigris.gef.base.KeyListener.class)
		((org.tigris.gef.base.KeyListener) el)
			.keyReleased(new SwingKeyEventWrapper(e));
	}
	;
    }

    public org.tigris.gef.base.MouseEvent createMouseDragEvent(int x, int y) {
	MouseEvent me = new MouseEvent(this, Event.MOUSE_DRAG, 0,
		InputEvent.BUTTON1_MASK, x, y, 0, false);
	return new SwingMouseEventWrapper(me);
    }

    public void showPopupMenu(JPopupMenu popup, int x, int y) {
	popup.show(this, x, y);
    }

}

class WheelKeyListenerToggleAction implements KeyListener {

    private int mask;

    private int down;

    private MouseWheelListener listener;

    private JPanel panel;

    /**
     * Creates KeyListener that adds and removes MouseWheelListener from
     * indicated JPanel so that it's only active when the modifier keys
     * (indicated by modifiersMask) are held down. Otherwise, the scrollbars
     * automatically managed by the JScrollPanel would never see the wheel
     * events.
     * 
     * @param panel
     *            JPanel object that will be listening for MouseWheelEvents on
     *            demand.
     * @param listener
     *            MouseWheelListener that listens for MouseWheelEvents
     * @param modifiersMask
     *            the logical OR of the AWT modifier keys values defined as
     *            constants by the KeyEvent class. This has been tested with
     *            ALT_MASK, CTRL_MASK, and SHIFT_MASK.
     */
    public WheelKeyListenerToggleAction(JPanel panel,
	    MouseWheelListener listener, int modifiersMask) {
	this.panel = panel;
	this.listener = listener;
	this.mask = modifiersMask;
    }

    public synchronized void keyPressed(KeyEvent e) {
	if ((e.getModifiers() | mask) != mask) {
	    return;
	}

	if (down == 0) {
	    panel.addMouseWheelListener(listener);
	}
	down |= e.getModifiers();
    }

    public synchronized void keyReleased(KeyEvent e) {
	if ((e.getModifiers() & mask) == 0) {
	    panel.removeMouseWheelListener(listener);
	}
	down = e.getModifiers();
    }

    public void keyTyped(KeyEvent e) {
    }

}
