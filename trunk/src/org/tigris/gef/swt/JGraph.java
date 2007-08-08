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

package org.tigris.gef.swt;

import swingwt.awt.BorderLayout;
import swingwt.awt.Color;
import swingwt.awt.Component;
import swingwt.awt.Cursor;
import swingwt.awt.Dimension;
import swingwt.awt.AWTEvent;
import swingwt.awt.Frame;
import swingwt.awt.Graphics;
import swingwt.awt.Graphics2D;
import swingwt.awt.Image;
import swingwt.awt.Point;
import swingwt.awt.Rectangle;
import swingwt.awt.event.ActionListener;
import swingwt.awt.event.AdjustmentEvent;
import swingwt.awt.event.AdjustmentListener;
import swingwt.awt.event.InputEvent;
import swingwt.awt.event.KeyEvent;
import swingwt.awt.event.KeyListener;
import swingwt.awt.event.MouseEvent;
import swingwt.awt.event.MouseListener;
import swingwt.awt.event.MouseMotionListener;
import swingwt.awt.event.MouseWheelEvent;
import swingwt.awt.event.MouseWheelListener;
import swingwt.awt.image.FilteredImageSource;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Vector;

import swingwtx.swing.JPanel;
import swingwtx.swing.JScrollPane;
import swingwtx.swing.JViewport;
import swingwtx.swing.KeyStroke;
import swingwtx.swing.ToolTipManager;

import org.tigris.gef.base.NudgeAction;
import org.tigris.gef.base.PopupMenu;
import org.tigris.gef.base.SelectNextAction;
import org.tigris.gef.base.Diagram;
import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.Layer;
import org.tigris.gef.base.LayerDiagram;
import org.tigris.gef.base.SelectNearAction;
import org.tigris.gef.base.TransFilter;
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
import org.tigris.gef.presentation.TextEditor;
import org.tigris.gef.graph.presentation.GraphInternalPane;

import Acme.JPM.Encoders.GifEncoder;

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
	this(new Editor(gm, (GraphInternalPane) null));
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

	int mask = swingwt.awt.event.KeyEvent.ALT_MASK
		| swingwt.awt.event.KeyEvent.CTRL_MASK;
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

    public void addMouseListener(org.tigris.gef.base.MouseListener listener) {
	drawingPane.addMouseListener(listener);
    }

    public void addMouseMotionListener(
	    org.tigris.gef.base.MouseMotionListener listener) {
	drawingPane.addMouseMotionListener(listener);
    }

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
    public void initKeys() {
	int shift = KeyEvent.SHIFT_MASK;
	int alt = KeyEvent.ALT_MASK;
	int meta = KeyEvent.META_MASK;

//	bindKey(new SelectNextAction("Select Next", true), KeyEvent.VK_TAB, 0);
//	bindKey(new SelectNextAction("Select Previous", false),
//		KeyEvent.VK_TAB, shift);
//
//	bindKey(new NudgeAction(NudgeAction.LEFT), KeyEvent.VK_LEFT, 0);
//	bindKey(new NudgeAction(NudgeAction.RIGHT), KeyEvent.VK_RIGHT, 0);
//	bindKey(new NudgeAction(NudgeAction.UP), KeyEvent.VK_UP, 0);
//	bindKey(new NudgeAction(NudgeAction.DOWN), KeyEvent.VK_DOWN, 0);
//
//	bindKey(new NudgeAction(NudgeAction.LEFT, 8), KeyEvent.VK_LEFT, shift);
//	bindKey(new NudgeAction(NudgeAction.RIGHT, 8), KeyEvent.VK_RIGHT, shift);
//	bindKey(new NudgeAction(NudgeAction.UP, 8), KeyEvent.VK_UP, shift);
//	bindKey(new NudgeAction(NudgeAction.DOWN, 8), KeyEvent.VK_DOWN, shift);
//
//	bindKey(new NudgeAction(NudgeAction.LEFT, 18), KeyEvent.VK_LEFT, alt);
//	bindKey(new NudgeAction(NudgeAction.RIGHT, 18), KeyEvent.VK_RIGHT, alt);
//	bindKey(new NudgeAction(NudgeAction.UP, 18), KeyEvent.VK_UP, alt);
//	bindKey(new NudgeAction(NudgeAction.DOWN, 18), KeyEvent.VK_DOWN, alt);
//
//	bindKey(new SelectNearAction(SelectNearAction.LEFT), KeyEvent.VK_LEFT,
//		meta);
//	bindKey(new SelectNearAction(SelectNearAction.RIGHT),
//		KeyEvent.VK_RIGHT, meta);
//	bindKey(new SelectNearAction(SelectNearAction.UP), KeyEvent.VK_UP, meta);
//	bindKey(new SelectNearAction(SelectNearAction.DOWN), KeyEvent.VK_DOWN,
//		meta);
    }

    public void bindKey(ActionListener action, int keyCode, int modifiers) {
//	drawingPane.registerKeyboardAction(action, KeyStroke.getKeyStroke(
//		keyCode, modifiers), WHEN_FOCUSED);
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

    public void setDiagram(Diagram d) {
	if (d == null) {
	    return;
	}
	if (_currentDiagramId != null) {
	    _viewPortPositions.put(_currentDiagramId, scrollPane.getViewport().getViewRect());
	}
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
	    Rectangle rect = SwtUtil.translateRectangle(fig.getBounds());
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

    public void setDrawingSize(int width, int height) {
	setDrawingSize(new Dimension(width, height));
    }

    public void setDrawingSize(Dimension dim) {
	editor.drawingSizeChanged(SwingUtil.translateDimension(dim));
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

    public void addGraphSelectionListener(GraphSelectionListener listener) {
	getEditor().addGraphSelectionListener(listener);
    }

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
    public void select(Fig f) {
	if (f == null)
	    deselectAll();
	else
	    editor.getSelectionManager().select(f);
    }

    public void selectByOwner(Object owner) {
	Layer lay = editor.getLayerManager().getActiveLayer();
	if (lay instanceof LayerDiagram)
	    select(((LayerDiagram) lay).presentationFor(owner));
    }

    public void selectByOwnerOrFig(Object owner) {
	if (owner instanceof Fig)
	    select((Fig) owner);
	else
	    selectByOwner(owner);
    }

    public void selectByOwnerOrNoChange(Object owner) {
	Layer lay = editor.getLayerManager().getActiveLayer();
	if (lay instanceof LayerDiagram) {
	    Fig f = ((LayerDiagram) lay).presentationFor(owner);
	    if (f != null)
		select(f);
	}
    }

    public void deselect(Fig f) {
	editor.getSelectionManager().deselect(f);
    }

    public void toggleItem(Fig f) {
	editor.getSelectionManager().toggle(f);
    }

    public void deselectAll() {
	editor.getSelectionManager().deselectAll();
    }

    public void select(Vector items) {
	editor.getSelectionManager().select(items);
    }

    public void toggleItems(Vector items) {
	editor.getSelectionManager().toggle(items);
    }

    public Vector selectedFigs() {
	return editor.getSelectionManager().getFigs();
    }

    // public Dimension getPreferredSize() { return new Dimension(1000, 1000); }

    // public Dimension getMinimumSize() { return new Dimension(1000, 1000); }

    // public Dimension getSize() { return new Dimension(1000, 1000); }
    public void setDefaultSize(int width, int height) {
	defaultSize = new Dimension(width, height);
    }

    public void setDefaultSize(Dimension dim) {
	defaultSize = dim;
    }

    public Dimension getDefaultSize() {
	return defaultSize;
    }

    public Point getViewPosition() {
	return scrollPane.getViewport().getViewPosition();
    }

    public void setViewPosition(Point p) {
	if (p != null)
	    scrollPane.getViewport().setViewPosition(p);
    }

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
     * @see org.tigris.gef.graph.presentation.Graph#adjustmentValueChanged(swingwt.awt.event.AdjustmentEvent)
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
     * @see org.tigris.gef.graph.presentation.Graph#mouseWheelMoved(swingwt.awt.event.MouseWheelEvent)
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

    protected org.tigris.gef.base.FileDialog fileDialog;
    
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
	_editor.paint(SwtUtil.translateGraphics(g));
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
    public swingwt.awt.event.MouseEvent retranslateMouseEvent(
            swingwt.awt.event.MouseEvent me) {
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
	//putClientProperty(swingwtx.swing.JComponent.TOOL_TIP_TEXT_KEY, text);
	ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
	// if (text != null) {
	if (!registeredWithTooltip) {
	    toolTipManager.registerComponent(this);
	    registeredWithTooltip = true;
	}
    }

    public void processMouseEvent(MouseEvent e) {
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
     * @see swingwt.awt.Component.addMouseListener
     * */
    public void addMouseListener(org.tigris.gef.base.MouseListener listener) {
	eventListeners.add(listener);
    }

    /** Overload the addMouseListener to support GEF's own MouseMotionListener
     * @see swingwt.awt.Component.addMouseMotionListener
     *  */
    public void addMouseMotionListener(
	    org.tigris.gef.base.MouseMotionListener listener) {
	eventListeners.add(listener);
    }

    /** Overload the addMouseListener to support GEF's own KeyListener 
     * @see swingwt.awt.Component.addKeyListener
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
	    if (el instanceof org.tigris.gef.base.MouseListener)
		((org.tigris.gef.base.MouseListener) el)
			.mouseClicked(new SwingWTMouseEventWrapper(e));
	}
	;
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent e) {
	for (EventListener el : eventListeners) {
	    if (el instanceof org.tigris.gef.base.MouseListener)
		((org.tigris.gef.base.MouseListener) el)
			.mousePressed(new SwingWTMouseEventWrapper(e));
	}
	;
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {
	for (EventListener el : eventListeners) {
	    if (el instanceof org.tigris.gef.base.MouseListener)
		((org.tigris.gef.base.MouseListener) el)
			.mouseReleased(new SwingWTMouseEventWrapper(e));
	}
	;
    }

    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent e) {
	for (EventListener el : eventListeners) {
	    if (el instanceof org.tigris.gef.base.MouseListener)
		((org.tigris.gef.base.MouseListener) el)
			.mouseEntered(new SwingWTMouseEventWrapper(e));
	}
	;
    }

    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {
	for (EventListener el : eventListeners) {
	    if (el instanceof org.tigris.gef.base.MouseListener)
		((org.tigris.gef.base.MouseListener) el)
			.mouseExited(new SwingWTMouseEventWrapper(e));
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
	    if (el instanceof org.tigris.gef.base.MouseMotionListener)
		((org.tigris.gef.base.MouseMotionListener) el)
			.mouseDragged(new SwingWTMouseEventWrapper(e));
	}
	;
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component
     * but no buttons have been pushed.
     */
    public void mouseMoved(MouseEvent e) {
	for (EventListener el : eventListeners) {
	    if (el instanceof org.tigris.gef.base.MouseMotionListener)
		((org.tigris.gef.base.MouseMotionListener) el)
			.mouseMoved(new SwingWTMouseEventWrapper(e));
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
	    if (el instanceof org.tigris.gef.base.KeyListener)
		((org.tigris.gef.base.KeyListener) el)
			.keyTyped(new SwingWTKeyEventWrapper(e));
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
	    if (el instanceof org.tigris.gef.base.KeyListener)
		((org.tigris.gef.base.KeyListener) el)
			.keyPressed(new SwingWTKeyEventWrapper(e));
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
	    if (el instanceof org.tigris.gef.base.KeyListener)
		((org.tigris.gef.base.KeyListener) el)
			.keyReleased(new SwingWTKeyEventWrapper(e));
	}
	;
    }

    public org.tigris.gef.base.MouseEvent createMouseDragEvent(int x, int y) {
	MouseEvent me = new MouseEvent(this, java.awt.Event.MOUSE_DRAG, 0,
		InputEvent.BUTTON1_MASK, x, y, 0, false);
	return new SwingWTMouseEventWrapper(me);
    }

    public java.awt.Rectangle getViewableRect() {
        return SwingUtil.translateRectangle(super.getVisibleRect());
    }
    
    public void revalidate() {
        super.revalidate();
    }
    
    public void setPreferredSize(java.awt.Dimension d) {
        super.setPreferredSize(SwtUtil.translateDimension(d));
    }
    
    public void repaint() {
        super.repaint();
    }
    
    public void repaint(int x, int y, int width, int height) {
        super.repaint(x,y,width,height);
    }
    
    public void repaint(int alpha, int x, int y, int width, int height) {
        super.repaint(alpha,x,y,width,height);
    }
    
    public void setCursor(java.awt.Cursor c) {
	// NOT YET IMPLEMENTED - can we have a translateCursor method?
        //super.setCursor(c);
    }
    
    private Frame findFrame() {
        Component c = this;
        while(c != null && !(c instanceof Frame))
            c = c.getParent();
        return (Frame)c;
    }
    
    public org.tigris.gef.base.FileDialog getFileDialog() {
        fileDialog= new JFileDialog(findFrame()); 
        return fileDialog;
    }
    
    public java.awt.Dimension getGraphSize() {
        return SwingUtil.translateDimension(super.getSize()); 
    }
    
    public java.awt.Color getGraphBackground() {
        return SwingUtil.translateColor(super.getBackground());
    }
    
//    public java.awt.Image createImage(int x, int h) {
//	return SwingUtil.translateImage(super.createImage(x, h));       
//    }
    
    public void scrollRectToVisible(java.awt.Rectangle bounds) {
        super.scrollRectToVisible(SwtUtil.translateRectangle(bounds));
    }
    
    public Point getViewPosition() {
        Component parent = this.getParent();
        if(!(parent instanceof JViewport)) {
            return new Point(0,0);
        }
        return ((JViewport) parent).getViewPosition();
    }
    
    public Dimension getExtentSize() {
        Component parent = this.getParent();
        if(!(parent instanceof JViewport)) {
            return new Dimension(0,0);
        }
        return ((JViewport) parent).getExtentSize();
    }
    
    public Rectangle getViewRect() {
        Component parent = this.getParent();
        if(!(parent instanceof JViewport)) {
            return new Rectangle(0,0);
        }
        return ((JViewport)parent).getViewRect();
    }
    
    public void setViewPosition(Point p) {
        Component parent = this.getParent();
        if(!(parent instanceof JViewport)) {
            return;
        }
        ((JViewport)parent).setViewPosition(p);
    }
    
    public java.awt.Rectangle getInternalBounds() {
        return SwingUtil.translateRectangle(super.getBounds());
    }
    
    public PopupMenu createPopupMenu() {
        return new org.tigris.gef.swing.JPopupMenu();
    }
    
    public boolean isParentViewport() {
        return (this.getParent() instanceof JViewport);
    }
    /**
     * Write the diagram contained by the current editor into an OutputStream as
     * a GIF image.
     */
    public void saveGraphics(OutputStream s, Editor ce, java.awt.Rectangle drawingArea, int scale)
            throws IOException {

//        final int TRANSPARENT_BG_COLOR = 0x00efefef;
//        // Create an offscreen image and render the diagram into it.
//
//        Image i = new swingwt.awt.Container().createImage(drawingArea.width * scale, drawingArea.height
//                * scale);
//        Graphics g = i.getGraphics();
//        if (g instanceof Graphics2D) {
//            ((Graphics2D) g).scale(scale, scale);
//        }
//        g.setColor(new Color(TRANSPARENT_BG_COLOR));
//        g.fillRect(0, 0, drawingArea.width * scale, drawingArea.height * scale);
//        // a little extra won't hurt
//        g.translate(-drawingArea.x, -drawingArea.y);
//        ce.print(SwingUtil.translateGraphics(g));
//
//        // Tell the Acme GIF encoder to save the image as a GIF into the
//        // output stream. Use the TransFilter to make the background
//        // color transparent.
//
//        try {
//            java.awt.image.FilteredImageSource fis = new swingwt.awt.image.FilteredImageSource(i.getSource(),
//                    new org.tigris.gef.base.TransFilter(TRANSPARENT_BG_COLOR));
//            GifEncoder ge = new GifEncoder(fis, s);
//            // GifEncoder ge = new GifEncoder( i, s );
//            ge.encode();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        g.dispose();
//        // force garbage collection, to prevent out of memory exceptions
//        g = null;
//        i = null;
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
