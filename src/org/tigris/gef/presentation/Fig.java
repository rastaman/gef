// %1030438053652:org.tigris.gef.presentation%
package org.tigris.gef.presentation;

import java.awt.*;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tigris.gef.base.*;

import org.tigris.gef.graph.GraphEdgeHooks;
import org.tigris.gef.graph.GraphNodeHooks;
import org.tigris.gef.graph.GraphPortHooks;

import org.tigris.gef.properties.PropCategoryManager;

import org.tigris.gef.ui.PopupGenerator;

import org.tigris.gef.util.Localizer;

/**
 *  This class is the base class for basic drawing objects such as rectangles,
 *  lines, text, circles, etc. Also, class FigGroup implements a composite
 *  figure. Fig's are Diagram elements that can be placed in any LayerDiagram.
 *  Fig's are also used to define the look of FigNodes on NetNodes.
 */
public class Fig implements Cloneable, java.io.Serializable, PropertyChangeListener, PopupGenerator {
    ////////////////////////////////////////////////////////////////
    // constants
    
    /** The smallest size that the user can drag this Fig. */
    public final int MIN_SIZE = 4;

    /** The size of the dashes drawn when the Fig is dashed. */

    // TODO deprecate these arrays. There is no such thing as a constant array.
    // these need hiding behind getters and setters
    public static final String[] DASHED_CHOICES = {"Solid", "Dashed",     "LongDashed",  "Dotted",      "DotDash"};
    //public static final int[][] DASH_ARRAYS   = {null,    {5,    5},    {15,    5},    {3,    10},    {3,    6,    10,    6}};     // opaque, transparent, [opaque, transparent]
    public static final float[][] DASH_ARRAYS   = {null,    {5.0f, 5.0f}, {15.0f, 5.0f}, {3.0f, 10.0f}, {3.0f, 6.0f, 10.0f, 6.0f}};  // opaque, transparent, [opaque, transparent]
    public static final int[]     DASH_PERIOD   = {0,        10,           20,            13,            25,                     };  // the sum of each subarray

    ////////////////////////////////////////////////////////
    // instance variables

    /**
     * Indicates whether this fig can be moved
     */
    boolean movable = true;
    
    /**
     * Indicates whether this fig can be resized
     */
    boolean resizable = true;
    
    /**
     *  The Layer that this Fig is in. Each Fig can be in exactly one Layer, but
     *  there can be multiple Editors on a given Layer.
     *  @deprecated 0.10.5 visibility will change to package
     */
    protected transient Layer _layer = null;

    /** True if this object is locked and cannot be moved by the user.
     *  @deprecated 0.10.5 visibility will change to package
     */
    protected boolean _locked = false;

    /**
     *  Owners are underlying objects that "own" the graphical Fig's that
     *  represent them. For example, a FigNode and FigEdge keep a pointer to the
     *  net-level object that they represent. Also, any Fig can have NetPort as
     *  an owner.
     *
     * @see FigNode#setOwner
     * @see FigNode#bindPort
     */
    private transient Object _owner;

    /**
     *  X coordinate of the Fig's bounding box. It is the responsibility of
     *  subclasses to make sure this value is ALWAYS up-to-date.
     */
    protected int _x;
    
    /**
     *  Y coordinate of the Fig's bounding box. It is the responsibility of
     *  subclasses to make sure this value is ALWAYS up-to-date.
     */
    protected int _y;

    /**
     *  Width of the Fig's bounding box. It is the responsibility of
     *  subclasses to make sure this value is ALWAYS up-to-date.
     */
    protected int _w;

    /**
     *  Height of the Fig's bounding box. It is the responsibility of
     *  subclasses to make sure this value is ALWAYS up-to-date.
     */
    protected int _h;

    /** Name of the resource being basis to this figs localization.
     * @deprecated 0.10.1 will change to package visibility use getters/setters
     */
    protected String _resource = "";

    /** Outline color of fig object.
     * @deprecated 0.10.1 will change to package visibility use getters/setters
     */
    protected Color _lineColor = Color.black;

    /** Fill color of fig object.
     * @deprecated 0.10.1 will change to package visibility use getters/setters
     */
    protected Color _fillColor = Color.white;

    /** Thickness of line around object, for now limited to 0 or 1.
     * @deprecated 0.10.1 will change to package visibility use getters/setters
     */
    protected int _lineWidth = 1;
    
    protected float[] _dashes = null;
    protected int     _dashStyle = 0;
    protected int     _dashPeriod = 0;

    /** True if the object should fill in its area. */
    protected boolean _filled = true;
    
    /**
     * The parent Fig of which this Fig is a child
     * @deprecated 0.10.1 will change to package visibility
     */
    protected Fig _group = null;
    
    protected String _context = "";

    /** True if the Fig is shown
     * @deprecated 0.10.1 use getters/setters
     */
    protected boolean _displayed = true;
    /** 
     * @deprecated 0.10.1 use getters/setters
     */
    public int _shown = 0;
    protected boolean _allowsSaving = true;
    /**
     *  @deprecated 0.10.5 client programmers should not be able to access this
     * they should only have access to isSelected().
     */
    protected transient boolean _selected = false;

    ////////////////////////////////////////////////////////////////
    // static initializer
    static {
        //needs-more-work: get rect editor to work
        //PropCategoryManager.categorizeProperty("Geometry", "bounds");
        PropCategoryManager.categorizeProperty("Geometry", "x");
        PropCategoryManager.categorizeProperty("Geometry", "y");
        PropCategoryManager.categorizeProperty("Geometry", "width");
        PropCategoryManager.categorizeProperty("Geometry", "height");
        PropCategoryManager.categorizeProperty("Geometry", "filled");
        PropCategoryManager.categorizeProperty("Geometry", "locked");
        PropCategoryManager.categorizeProperty("Style", "lineWidth");
        PropCategoryManager.categorizeProperty("Style", "fillColor");
        PropCategoryManager.categorizeProperty("Style", "lineColor");
        PropCategoryManager.categorizeProperty("Style", "filled");
    }

    ////////////////////////////////////////////////////////////////
    // geometric manipulations

    /** Margin between this Fig and automatically routed arcs. */
    public final int BORDER = 8;

    private static final Log LOG = LogFactory.getLog(Fig.class);

    /**
     * Most subclasses will not use this constructor, it is only useful
     * for subclasses that redefine most of the infrastructure provided
     * by class Fig.
     */
    public Fig() {
        an = NoAnnotationStrategy.getInstance();
    }

    /** Construct a new Fig with the given bounds. */
    public Fig(int x, int y, int w, int h) {
        this(x, y, w, h, Color.black, Color.white, null);
    }

    /** Construct a new Fig with the given bounds and colors. */
    public Fig(int x, int y, int w, int h, Color lineColor, Color fillColor) {
        this(x, y, w, h, lineColor, fillColor, null);
    }

    ////////////////////////////////////////////////////////////////
    // constuctors

    /** Construct a new Fig with the given bounds, colors, and owner. */
    public Fig(int x, int y, int w, int h, Color lineColor, Color fillColor, Object own) {
        this();
        _x = x;
        _y = y;
        _w = w;
        _h = h;
        if(lineColor != null) {
            _lineColor = lineColor;
        } else {
            _lineWidth = 0;
        }

        if(fillColor != null) {
            _fillColor = fillColor;
        } else {
            _filled = false;
        }

        setOwner(own);
        //annotation related
    }

    //------------------------
    // localization related
    public void setResource(String resource) {
        _resource = resource;
    }

    public String getResource() {
        return _resource;
    }

    //--------------------------------
    // annotation related
    protected AnnotationStrategy an = NoAnnotationStrategy.getInstance();
    protected boolean annotationStatus = false;
    protected Fig annotationOwner;

    // specifies the AnnotationOwner
    public void setAnnotationOwner(Fig f) {
        annotationOwner = f;
        setAnnotationStatus(true);
    }

    // fig is not an annotation any longer
    public void unsetAnnotationOwner() {
        annotationOwner = null;
        setAnnotationStatus(false);
    }

    public Fig getAnnotationOwner() {
        return annotationOwner;
    }

    /**
     * USED BY PGML.tee
     */
    public AnnotationStrategy getAnnotationStrategy() {
        return an;
    }

    //** Set the AnnotationStrategy for this fig */
    //** using this method will discard the previous AnnotationStrategy */
    public void setAnnotationStrategy(AnnotationStrategy a) {
        an = a;
    }

    // returns true if this fig is an annotation of any other fig
    public boolean isAnnotation() {
        return annotationStatus;
    }

    public void setAnnotationStatus(boolean newValue) {
        annotationStatus = newValue;
    }

    /**
     * Adds a new Annotation of type "text" to fig.
     */
    public void addAnnotation(Fig annotation, String type, String context) {
    }

    public void removeAnnotation(String context) {
    }

    public void removeAnnotation(Fig annotationFig) {
        if(annotationFig.isAnnotation() && (this == annotationFig.getAnnotationOwner())) {
            Globals.curEditor().remove(annotationFig);
            getAnnotationStrategy().removeAnnotation(annotationFig);
        }
    }

    /**
     * Fig has been moved: Adjust the annotation positions
     * Extracted from endTrans() so that annotation positions can be updated without redrawing everything. 
     */ 
    public void translateAnnotations() {
        // If this Fig is an annotaion itself, simply store the position at the owner.
        if(this.isAnnotation()) {
            SelectionManager selectionManager = Globals.curEditor().getSelectionManager();
            if(!(selectionManager.containsFig(this.getAnnotationOwner())) && selectionManager.containsFig(this)) {
                (getAnnotationOwner().an).storeAnnotationPosition(this);
            }
        }

        // If this Fig is owner of annotations then move the annotations according to the Fig's own position.
        if(!(getAnnotationStrategy() instanceof NoAnnotationStrategy)) {
            getAnnotationStrategy().translateAnnotations(this);
        }
    }

    /**
     * Updates the positions of the connected annotations.
     */
    public void updateAnnotationPositions() {
        //System.out.println("[Fig] updateAnnotationPositions");
        Enumeration annotations = getAnnotationStrategy().getAllAnnotations();
        //System.out.println("[Fig] numOfAnnotations = " + getAnnotationStrategy().numOfAnnotations());
        while(annotations.hasMoreElements()) {
            Fig annotation = (Fig)annotations.nextElement();
            //Rectangle annotRect = annotation.getBounds();
            //System.out.println("[Fig] updateAnnotationPositions: "+annotRect.x+" "+annotRect.y+" "+annotRect.width+" "+annotRect.height);
            getAnnotationStrategy().storeAnnotationPosition(annotation);
            annotation.endTrans();
        }

        endTrans();
    }

    public void initAnnotations() {
    }

    // end annotation related
    //-----------------------------------
    // TODO We really need some javadoc for this. How is this supposed to be
    // used. WHy isn't this extended by FigEdgePoly?
    public void addPoint(int x, int y) {
    }

    ////////////////////////////////////////////////////////////////
    // updates

    /** The specified PropertyChangeListeners <b>propertyChange</b>
     *  method will be called each time the value of any bound property
     *  is changed.  Note: the JavaBeans specification does not require
     *  PropertyChangeListeners to run in any particular order. <p>
     *
     *  Since most Fig's will never have any listeners, and I want Figs
     *  to be fairly light-weight objects, listeners are kept in a
     *  global Hashtable, keyed by Fig.  NOTE: It is important that all
     *  listeners eventually remove themselves, otherwise this will
     *  prevent garbage collection. */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        Globals.addPropertyChangeListener(this, l);
    }

    /** Align this Fig with the given rectangle. Some subclasses may
     *  need to know the editor that initiated this action.  */
    public void align(Rectangle r, int direction, Editor ed) {
        Rectangle bbox = getBounds();
        int dx = 0;
        int dy = 0;
        switch(direction) {

            case CmdAlign.ALIGN_TOPS:
                dy = r.y - bbox.y;
                break;

            case CmdAlign.ALIGN_BOTTOMS:
                dy = r.y + r.height - (bbox.y + bbox.height);
                break;

            case CmdAlign.ALIGN_LEFTS:
                dx = r.x - bbox.x;
                break;

            case CmdAlign.ALIGN_RIGHTS:
                dx = r.x + r.width - (bbox.x + bbox.width);
                break;

            case CmdAlign.ALIGN_CENTERS:
                dx = r.x + r.width / 2 - (bbox.x + bbox.width / 2);
                dy = r.y + r.height / 2 - (bbox.y + bbox.height / 2);
                break;

            case CmdAlign.ALIGN_H_CENTERS:
                dx = r.x + r.width / 2 - (bbox.x + bbox.width / 2);
                break;

            case CmdAlign.ALIGN_V_CENTERS:
                dy = r.y + r.height / 2 - (bbox.y + bbox.height / 2);
                break;

            case CmdAlign.ALIGN_TO_GRID:
                Point loc = getLocation();
                Point snapPt = new Point(loc.x, loc.y);
                ed.snap(snapPt);
                dx = snapPt.x - loc.x;
                dy = snapPt.y - loc.y;
                break;
        }

        translate(dx, dy);
    }

    /** Update the bounds of this Fig.  By default it is assumed that
     *  the bounds have already been updated, so this does nothing.
     *
     * @see FigText#calcBounds */
    public void calcBounds() {
    }

    // note: computing non-intersection is faster on average.  Maybe I
    // should structure the API to allow clients to take advantage of that?

    /** Return the center of the given Fig. By default the center is the
     *  center of its bounding box. Subclasses may want to define
     *  something else. 
     * USED BY PGML.tee
     */
    public Point center() {
        Rectangle bbox = getBounds();
        return new Point(bbox.x + bbox.width / 2, bbox.y + bbox.height / 2);
    }

    /**
     * USED BY PGML.tee
     */
    public String classNameAndBounds() {
        return getClass().getName() + "[" + getX() + ", " + getY() + ", " + getWidth() + ", " + getHeight() + "]";
    }

    public void cleanUp() {
    }

    public Object clone() {
        try {
            return super.clone();
        } catch(CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Can the fig can be copied and pasted
     * @deprecated 0.11 badly spelt method use is Copyable
     */
    public boolean isCopieable() {
        return true;
    }

    /**
     * Can the fig can be copied and pasted
     */
    public boolean isCopyable() {
        return true;
    }

    /**
     * Can the fig can be cut and pasted
     */
    public boolean isCutable() {
        return true;
    }

    /** Return a point that should be used for arcs that to toward the
     *  given point. By default, this makes arcs end on the edge that is
     *  nearest the given point.
     *
     * needs-more-work: define gravity points, berths
     */
    public Point connectionPoint(Point anotherPt) {
        Vector grav = getGravityPoints();
        if(grav != null && grav.size() > 0) {
            int ax = anotherPt.x;
            int ay = anotherPt.y;
            Point bestPoint = (Point)grav.elementAt(0);
            int bestDist = Integer.MAX_VALUE;
            int size = grav.size();
            for(int i = 0; i < size; i++) {
                Point gp = (Point)grav.elementAt(i);
                int dx = gp.x - ax;
                int dy = gp.y - ay;
                int dist = dx * dx + dy * dy;
                if(dist < bestDist) {
                    bestDist = dist;
                    bestPoint = gp;
                }
            }

            return new Point(bestPoint.x, bestPoint.y);
        }

        return getClosestPoint(anotherPt);
    }

    /** Reply true if the given point is inside the given Fig. By
     *  default reply true if the point is in my bounding
     *  box. Subclasses like FigCircle and FigEdge do more specific
     *  checks.
     *
     * @see FigCircle
     * @see FigEdge */
    public boolean contains(int x, int y) {
        return (_x <= x) && (x <= _x + _w) && (_y <= y) && (y <= _y + _h);
    }

    /** Reply true if the given point is inside this Fig by
     *  calling contains(int x, int y). */
    public final boolean contains(Point p) {
        return contains(p.x, p.y);
    }

    /** Reply true if the all four corners of the given rectangle are
     *  inside this Fig, as determined by contains(int x, int y). */
    public boolean contains(Rectangle r) {
        return countCornersContained(r.x, r.y, r.width, r.height) == 4;
    }

    /** Reply the number of corners of the given rectangle that are
     *  inside this Fig, as determined by contains(int x, int y). */
    protected int countCornersContained(int x, int y, int w, int h) {
        int cornersHit = 0;
        if(contains(x, y)) {
            cornersHit++;
        }

        if(contains(x + w, y)) {
            cornersHit++;
        }

        if(contains(x, y + h)) {
            cornersHit++;
        }

        if(contains(x + w, y + h)) {
            cornersHit++;
        }

        return cornersHit;
    }

    /** Resize the object for drag on creation. It bypasses the things
     *  done in resize so that the position of the object can be kept as
     *  the anchor point. Needs-More-Work: do I really need this
     *  function?
     *
     * @see FigLine#createDrag */
    public void createDrag(int anchorX, int anchorY, int x, int y, int snapX, int snapY) {
        int newX = Math.min(anchorX, snapX);
        int newY = Math.min(anchorY, snapY);
        int newW = Math.max(anchorX, snapX) - newX;
        int newH = Math.max(anchorY, snapY) - newY;
        setBounds(newX, newY, newW, newH);
    }

    /** This is called after an Cmd modifies a Fig and the Fig needs to
     * be redrawn in its new position. */
    public void endTrans() {
        translateAnnotations();
        damage();
    }

    /** This Fig has changed in some way, tell its Layer to record my
     *  bounding box as a damageAll region so that I will eventualy be
     *  redrawn. */
    public void damage() {
        if(_layer != null) {
            _layer.damageAll();
        }
    }

    /** Get the rectangle on whose corners the dragging handles are to be drawn.
     * Should be overwritten by Figures with Bounds larger than the HandleBox.
     * Normally these should be identical.
     */
    public Rectangle getHandleBox() {
        return getBounds();
    }

    /** Set the HandleBox.
     * Normally this should not be used. It is intended for figures where the
     * Handlebox is different from the Bounds.
     * Overide this method if HandleBox and bounds differ
     */
    public void setHandleBox(int x, int y, int w, int h) {
        if (LOG.isDebugEnabled()) LOG.debug("Height = " + h);
        setBounds(x, y, w, h);
    }

    ////////////////////////////////////////////////////////////////
    // Editor API

    /** Remove this Fig from the Layer being edited by the
     *  given editor.
     * @depreacted 0.10.7 use removeFromDiagram
     */
    public void delete() {
        removeFromDiagram();
    }
    
    /** Remove this Fig from the Layer being edited by the
     *  given editor. */
    public void removeFromDiagram() {
        _displayed = false;
        // annotation related
        // delete all annotations first
        java.util.Enumeration iter = getAnnotationStrategy().getAllAnnotations();
        while(iter.hasMoreElements()) {
            Fig annotation = (Fig)iter.nextElement();
            getAnnotationStrategy().getAnnotationProperties(annotation).removeLine();
            removeAnnotation(annotation);
            annotation.removeFromDiagram();
        }

        // end annotation related
        if(_layer != null) {
            _layer.deleted(this);
        }

        // ak: remove this figure from the enclosed figures of the encloser
        setEnclosingFig(null);
        setOwner(null);
    }

    /** Delete whatever application object this Fig is representing, the
     *  Fig itself should automatically be deleted as a side-effect. Simple
     *  Figs have no underlying model, so they are just deleted. Figs
     *  that graphically present some part of an underlying model should
     *  NOT delete themselves, instead they should ask the model to
     *  dispose, and IF it does then the figs will be notified.
     * @deprecated 0.10.7 use deleteFromModel.
     */
    public void dispose() {
        deleteFromModel();
    }
    
    /** Delete whatever application object this Fig is representing, the
     *  Fig itself should automatically be deleted as a side-effect. Simple
     *  Figs have no underlying model, so they are just deleted. Figs
     *  that graphically present some part of an underlying model should
     *  NOT delete themselves, instead they should ask the model to
     *  dispose, and IF it does then the figs will be notified. */
    public void deleteFromModel() {
        LOG.debug("Deleting Fig from model");
        Object own = getOwner();
        if(own instanceof GraphNodeHooks) {
            ((GraphNodeHooks)own).deleteFromModel();
        }
        else if(own instanceof GraphEdgeHooks) {
            ((GraphEdgeHooks)own).deleteFromModel();
        }
        else if(own instanceof GraphPortHooks) {
            ((GraphPortHooks)own).deleteFromModel();
        }
        else {
            removeFromDiagram();
        }
    }

    protected int drawDashedLine(Graphics g, int phase, int x1, int y1, int x2, int y2) {             // float phase?
        int dxdx = (x2 - x1) * (x2 - x1);
        int dydy = (y2 - y1) * (y2 - y1);
        int length = (int)(Math.sqrt(dxdx + dydy) + 0.5);       // This causes a smaller rounding error of 0.5pixels max. . Seems acceptable.
        float lineWidth = _lineWidth;
        Graphics2D g2D = (Graphics2D)g;
        Stroke  OriginalStroke = g2D.getStroke();               // we need this to restore the original stroke afterwards

        BasicStroke  DashedStroke   = new BasicStroke(lineWidth,   BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f,            _dashes,    (float)phase);
        //                                           (float width, int cap,                int join,               float miterlimit, float[] dash, float dash_phase)
        g2D.setStroke(DashedStroke);
        g2D.drawLine(x1, y1, x2, y2);
        g2D.setStroke(OriginalStroke);   // undo the manipulation of g

        return (length + phase) % _dashPeriod ;
    }
    
    protected void drawDashedPerimeter(Graphics g) {
        Point segStart = new Point();
        Point segEnd = new Point();
        int numDashes = _dashes.length;
        int length = getPerimeterLength();
        int i = 0;
        int d = 0;
        while(i < length) {
            stuffPointAlongPerimeter(i, segStart);
            i += _dashes[d];
            d = (d + 1) % numDashes;
            stuffPointAlongPerimeter(i, segEnd);
            g.drawLine(segStart.x, segStart.y, segEnd.x, segEnd.y);
            i += _dashes[d];
            d = (d + 1) % numDashes;
        }
    }

    public void firePropChange(String propName, int oldV, int newV) {
        firePropChange(propName, new Integer(oldV), new Integer(newV));
    }

    /** Creates a PropertyChangeEvent and calls all registered listeners
     *  propertyChanged() method. */
    public void firePropChange(String propName, Object oldV, Object newV) {
        Globals.firePropChange(this, propName, oldV, newV);
        if(_group != null) {
            PropertyChangeEvent pce = new PropertyChangeEvent(this, propName, oldV, newV);
            _group.propertyChange(pce);
        }
    }

    public void firePropChange(String propName, boolean oldV, boolean newV) {
        firePropChange(propName, new Boolean(oldV), new Boolean(newV));
    }

    /** Return a Rectangle that completely encloses this Fig.
     * Subclasses may override getBounds(Rectangle).
     * USED BY PGML.tee
     */
    public Rectangle getBounds() {
        return getBounds(null);
    }

    /** 
     * Stores the Rectangle that completely encloses this Fig into "return value" <b>r</b> and 
     * return <b>r</b>.  If r is <code>null</code> a new
     * <code>Rectangle</code> is allocated.
     * This version of <code>getBounds</code> is useful if the caller
     * wants to avoid allocating a new <code>Rectangle</code> object
     * on the heap.
     * 
     * @param r the return value, modified to the components bounds
     * @return r
     */
    public Rectangle getBounds(Rectangle r) {
        if(r == null) {
            return new Rectangle(_x, _y, _w, _h);
        }
        else {
            r.setBounds(_x, _y, _w, _h);
        }

        return r;
    }

    public Point getClosestPoint(Point anotherPt) {
        return Geometry.ptClosestTo(getBounds(), anotherPt);
    }

    /** Get the dashed attribute **/
    public boolean getDashed() {
        return (_dashes != null);
    }

    public int getDashed01() {
        return getDashed() ? 1 : 0;
    }

    public String getDashedString() {
        return (_dashes == null) ? DASHED_CHOICES[0] : DASHED_CHOICES[1];
    }

    public Vector getEnclosedFigs() {
        return null;
    }

    /**
     * USED BY PGML.tee
     */
    public Fig getEnclosingFig() {
        return null;
    }

    /**
     * Does this Fig support the concept of "fill color" in principle
     * @return
     */
    public boolean hasFillColor() {
        return true;
    }

    public Color getFillColor() {
        return _fillColor;
    }

    public boolean getFilled() {
        return _filled;
    }

    public int getFilled01() {
        return _filled ? 1 : 0;
    }

    /**
     * Does this Fig support the concept of "line color" in principle
     * @return
     */
    public boolean hasLineColor() {
        return true;
    }

    /**
     * USED BY SVG.tee
     */
    public Color getLineColor() {
        return _lineColor;
    }

    /**
     * USED BY SVG.tee
     */
    public int getLineWidth() {
        return _lineWidth;
    }

    public Point getFirstPoint() {
        return new Point();
    }

    public Vector getGravityPoints() {
        return null;
    }

    public Fig getGroup() {
        return _group;
    }

    /**
     * USED BY PGML.tee
     * @return
     */
    public String getContext() {
        return _context;
    }

    /*
     * USED BY PGML.tee
     */
    public int getHalfHeight() {
        return _h / 2;
    }

    /*
     * USED BY PGML.tee
     */
    public int getHalfWidth() {
        return _w / 2;
    }

    /**
     * USED BY PGML.tee
     */
    public int getHeight() {
        return _h;
    }

    /*
     * USED BY PGML.tee
     */
    public String getId() {
        if(getGroup() != null) {
            String gID = getGroup().getId();
            if(getGroup() instanceof FigGroup) {
                return gID + "." + ((List)((FigGroup)getGroup()).getFigs(null)).indexOf(this);
            }
            else {
                return gID + ".1";
            }
        }

        Layer layer = getLayer();
        if(layer == null) {
            return "LAYER_NULL";
        }

        List c = (List)layer.getContents(null);
        int index = c.indexOf(this);
        return "Fig" + index;
    }

    public Point getLastPoint() {
        return new Point();
    }

    public Layer getLayer() {
        return _layer;
    }

    /** Returns a point that is the upper left corner of the Fig's
     *  bounding box. */
    public Point getLocation() {
        return new Point(_x, _y);
    }

    public boolean getLocked() {
        return _locked;
    }

    /** Returns the minimum size of the Fig.  This is the smallest size
     *  that the user can make this Fig by dragging. You can ignore this
     *  and make Figs smaller programmitically if you must. */
    public Dimension getMinimumSize() {
        return new Dimension(MIN_SIZE, MIN_SIZE);
    }

    public int getNumPoints() {
        return 0;
    }

    /**
     * USED BY PGML.tee
     */
    public Object getOwner() {
        return _owner;
    }

    /** Return the length of the path around this Fig. By default,
     *  returns the perimeter of the Fig's bounding box.  Subclasses
     *  like FigPoly have more specific logic. */
    public int getPerimeterLength() {
        return _w + _w + _h + _h;
    }

    public Point[] getPoints() {
        return new Point[0];
    }

    public Point getPoints(int i) {
        return null;
    }

    public Vector getPopUpActions(MouseEvent me) {
        Vector popUpActions = new Vector();
        JMenu orderMenu = new JMenu(Localizer.localize("PresentationGef", "Ordering"));
        orderMenu.setMnemonic('O');
        orderMenu.add(CmdReorder.BringForward);
        orderMenu.add(CmdReorder.SendBackward);
        orderMenu.add(CmdReorder.BringToFront);
        orderMenu.add(CmdReorder.SendToBack);
        popUpActions.addElement(orderMenu);
        return popUpActions;
    }

    /** Returns the prefered size of the Fig. This will be useful for
     *  automated layout. By default just uses the current
     *  size. Subclasses must override to return something useful. */
	public Dimension getPreferredSize() {
		return new Dimension(_w, _h);
	}
	/**
	 * @deprecated 0.10.5 in favour of getPreferredSize()
	 * @return
	 */
    public Dimension getPreferedSize() {
        return getPreferredSize();
    }

    /**
     * TODO document
     * Used in SVG.TEE
     */
    public String getPrivateData() {
        return "";
    }

    /** Returns the single flag of the Fig */
    public boolean getSingle() {
        return false;
    }

    /** Returns the size of the Fig. */
    public Dimension getSize() {
        return new Dimension(_w, _h);
    }

    public String getTipString(MouseEvent me) {
        return toString();
    }

    public Rectangle getTrapRect() {
        return getBounds();
    }

    public boolean getUseTrapRect() {
        return false;
    }

    /**
     * USED BY PGML.tee
     */
    public int getVisState() {
        return _shown;
    }

    /**
     * USED BY PGML.tee
     */
    public int getWidth() {
        return _w;
    }

    /**
     * USED BY PGML.tee
     */
    public int getX() {
        return _x;
    }

    public int[] getXs() {
        return new int[0];
    }

    /**
     * USED BY PGML.tee
     */
    public int getY() {
        return _y;
    }

    public int[] getYs() {
        return new int[0];
    }

    /** 
     * Determine if the given rectangle contains some pixels of the
     * Fig. This is used to determine if the user is trying to select
     * this Fig. Rather than ask if the mouse point is in the Fig, I
     * use a small rectangle around the mouse point so that small
     * objects and lines are easier to select.
     * If the fig is invisible this method always returns false.
     * @param r the rectangular hit area
     * @return true if the hit rectangle strikes this fig
     */
    public boolean hit(Rectangle r) {
    	if (!isVisible()) return false;
        int cornersHit = countCornersContained(r.x, r.y, r.width, r.height);
        if(_filled) {
            return cornersHit > 0;
        }
        else {
            return cornersHit > 0 && cornersHit < 4;
        }
    }

    public void insertPoint(int i, int x, int y) {
    }

    /** Reply true if the object intersects the given rectangle. Used
     *  for selective redrawing and by ModeSelect to select all Figs
     *  that are partly within the selection rectangle.
     *  Note: comparisons are strict (e.g. '<' instead of '<='), so that
     *  figs with zero height or width are handled correctly.
     */
    public boolean intersects(Rectangle r) {
        return !((r.x + r.width < _x) || (r.y + r.height < _y) || (r.x > _x + _w) || (r.y > _y + _h));
    }

    /** Reply true if the object's perimeter intersects the given rectangle. Used
     *  for selective redrawing and by ModeSelect to select all Figs
     *  that are partly within the selection rectangle.
     *  Note: comparisons are strict (e.g. '<' instead of '<='), so that
     *  figs with zero height or width are handled correctly.
     */
    public boolean intersectsPerimeter(Rectangle r) {
        return (r.intersectsLine(_x, _y, _x, _y + _h) && r.intersectsLine(_x, _y + _h, _x + _w, _y + _h) && r.intersectsLine(_x + _w, _y + _h, _x + _w, _y) && r.intersectsLine(_x + _w, _y, _x, _y));
    }

    /** Returns true if this Fig can be resized by the user. */
    public boolean isLowerRightResizable() {
        return false;
    }

    /** Returns true if this Fig can be moved around by the user. */
    public boolean isMovable() {
        return movable;
    }

    /** Returns true if this Fig can be reshaped by the user. */
    public boolean isReshapable() {
        return false;
    }

    /**
     * Determine if this Fig can be resized 
     * @return true if this Fig can be resized by the user.
     */
    public boolean isResizable() {
        return resizable;
    }

    /** Returns true if this Fig can be rotated by the user. */
    public boolean isRotatable() {
        return false;
    }

    /**
     * Returns the current selection state for this item
     *
     * @return True, if the item is currently selected, otherwise false.
     */
    public boolean isSelected() {
        return _selected;
    }

    /** 
     * SelectionManager calls this to attempt to create a custom Selection object
     * when selecting a Fig. Override this only if you have specialist requirements
     * For a selected Fig.
     * SelectionManger uses its own rules if this method returns null.
     * @return a specialist Selection class or null to delegate creation to the
     *         Selection Manager.
     */
    public Selection makeSelection() {
        return null;
    }

    /**
     * Sets the selection state for this item.
     * @param selectionState The new selection state.
     * @deprecated 0.10.5 client programmers should not be able to access this
     * they should only have access to isSelected().
     * This method is not currently called from anywhere in GEF itself.
     */
    public void setSelected(boolean selectionState) {
        _selected = selectionState;
    }

    ////////////////////////////////////////////////////////////////
    // invariant

    /** Check class invariants to make sure the Fig is in a valid state.
     *  This is useful for debugging. needs-more-work. */
    public boolean OK() {
        // super.OK() /
        return _lineWidth >= 0 && _lineColor != null && _fillColor != null;
    }

    /** Method to paint this Fig.  By default it paints an "empty"
     *  space, subclasses should override this method. */
    public void paint(Graphics g) {
        if(_displayed) {
            g.setColor(Color.pink);
            g.fillRect(_x, _y, _w, _h);
            g.setColor(Color.black);
            g.drawString("(undefined)", _x + _w / 2, _y + _h / 2);
        }
    }

    /** Return a point at the given distance along the path around this
     *  Fig. By default, uses perimeter of the Fig's bounding
     *  box. Subclasses like FigPoly have more specific logic. */
    public Point pointAlongPerimeter(int dist) {
        Point res = new Point();
        stuffPointAlongPerimeter(dist, res);
        return res;
    }

    public void postLoad() {
    }

    public void postSave() {
    }

    public void preSave() {
    }

    //   public void assignShadowColor(Color c) { _shadowColor = c; } //?
    //   public void setShadowColor(Color c) { _shadowColor = c; }
    //   public Color getShadowColor() { return _shadowColor; }
    //   public void assignShadowOffset(Point p) { _shadowOffset = p; } //?
    //   public void setShadowOffset(Point p) { _shadowOffset = p; }
    //   public Point getShadowOffset() { return _shadowOffset; }

    /** Draw the Fig on a PrintGraphics. This just calls paint. */
    public void print(Graphics g) {
        paint(g);
    }


    ////////////////////////////////////////////////////////////////
    // property change handling

    /** By default just pass it up to enclosing groups.  Subclasses of
     *  FigNode may want to override this method. */
    public void propertyChange(PropertyChangeEvent pce) {
        if(_group != null) {
            _group.propertyChange(pce);
        }
    }


    /**  Force recalculating of bounds and redraw of fig. */
    public void redraw() {
        Rectangle rect = getBounds();
        setBounds(rect.x, rect.y, rect.width, rect.height);
        damage();
    }


    public void removePoint(int i) {
    }


    /**
     *  Remove this PropertyChangeListener from the JellyBeans internal list. If
     *  the PropertyChangeListener isn't on the list, silently do nothing.
     *
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        Globals.removePropertyChangeListener(this, l);
    }


    /**
     *  Change the back-to-front ordering of a Fig in LayerDiagram. Should the
     *  Fig have any say in it?
     *
     * @see LayerDiagram#reorder
     * @see          CmdReorder
     */
    public void reorder(int func, Layer lay) {
        lay.reorder(this, func);
    }


    /**
     *  Reply a rectangle that arcs should not route through. Basically this is
     *  the bounding box plus some margin around all egdes.
     */
    public Rectangle routingRect() {
        return new Rectangle(_x - BORDER, _y - BORDER, _w + BORDER * 2, _h + BORDER * 2);
    }


    public boolean savingAllowed() {
        return _allowsSaving;
    }


    public void setSavingAllowed(boolean newValue) {
        _allowsSaving = newValue;
    }


    /** Set the bounds of this Fig. Fires PropertyChangeEvent "bounds". */
    public void setBounds(int x, int y, int w, int h) {
        Rectangle oldBounds = getBounds();
        _x = x;
        _y = y;
        _w = w;
        _h = h;
        firePropChange("bounds", oldBounds, getBounds());
    }

    /** Change my bounding box to the given Rectangle. Just calls
     *  setBounds(x, y, w, h). */
    public final void setBounds(Rectangle r) {
        setBounds(r.x, r.y, r.width, r.height);
    }

    public final void setCenter(Point p) {
        int newX = p.x - (_w / 2);
        int newY = p.y - (_h / 2);
        setLocation(newX, newY);
    }

    /**
     * USED BY PGML.tee
     */
    public void setEnclosingFig(Fig f) {
        if(f != null && f != getEnclosingFig() && _layer != null) {
            _layer.bringInFrontOf(this, f);
            damage();
        }
    }

    /** Sets the enclosing FigGroup of this Fig.  The enclosing group is
     * always notified of property changes, without need to add a listener. */
    public void setGroup(Fig f) {
        _group = f;
    }

    public void setContext(String context) {
        _context = context;
    }

    public void setHeight(int h) {
        setBounds(_x, _y, _w, h);
    }

    ////////////////////////////////////////////////////////////////
    // accessors

    /** Sets the Layer that this Fig belongs to. Fires PropertyChangeEvent
     *  "layer". */
    public void setLayer(Layer lay) {
        firePropChange("layer", _layer, lay);
        _layer = lay;
    }

    /** Sets the color that will be used if the Fig is filled.  If col
     *  is null, turns off filling. Fires PropertyChangeEvent
     *  "fillColor", or "filled".*/
    public void setFillColor(Color col) {
        if(col != null) {
            firePropChange("fillColor", _fillColor, col);
            _fillColor = col;
        }
        else {
            firePropChange("filled", _filled, false);
            _filled = false;
        }
    }

    /** Sets a flag to either fill the Fig with its fillColor or
     *  not. Fires PropertyChangeEvent "filled". */
    public void setFilled(boolean f) {
        firePropChange("filled", _filled, f);
        _filled = f;
    }

    /** Sets the color to be used if the lineWidth is > 0. If col is
     *  null, sets the lineWidth to 0.  Fires PropertyChangeEvent
     *  "lineColor", or "lineWidth".*/
    public void setLineColor(Color col) {
        if(col != null) {
            firePropChange("lineColor", _lineColor, col);
            _lineColor = col;
        }
        else {
            firePropChange("lineWidth", _lineWidth, 0);
            _lineWidth = 0;
        }
    }

    /**
     *  Set the line width. Zero means lines are not drawn. One draws them one
     *  pixel wide. Larger widths are in experimental support stadium
     *  (hendrik@freiheit.com, 2003-02-05). Fires PropertyChangeEvent
     *  "lineWidth".
     *
     * @param w The new lineWidth value
     */
    public void setLineWidth(int w) {
        int newLW = Math.max( 0, w );
        firePropChange("lineWidth", _lineWidth, newLW);
        _lineWidth = newLW;
    }

    /** Set line to be dashed or not **/
    public void setDashed(boolean now_dashed) {
        if(now_dashed) {
            _dashes   = DASH_ARRAYS[1];
            _dashPeriod = DASH_PERIOD[1];
        }
        else {
            _dashes = null;
        }
    }

    public void setDashedString( String dashString ) {
        setDashed( dashString.equalsIgnoreCase( "solid" ) );
    }

    /** Move the Fig to the given position. By default translates the
     *  Fig so that the upper left corner of its bounding box is at the
     *  location. Fires property "bounds".*/
    public void setLocation(int x, int y) {
        translate(x - _x, y - _y);
    }

    /** Move the Fig to the given position. */
    public final void setLocation(Point p) {
        setLocation(p.x, p.y);
    }

    //public void assignLocked(boolean b) { _locked = b; } //?

    /** Sets whether this Fig is locked or not.  Most Cmds check to see
     *  if Figs are locked and will not request modifications to locked
     *  Figs. Fires PropertyChangeEvent
     *  "locked". */
    public void setLocked(boolean b) {
        firePropChange("locked", _locked, b);
        _locked = b;
    }

    public void setNumPoints(int npoints) {
    }

    /** Sets the owner object of this Fig. Fires PropertyChangeEvent
     *  "owner"
     *
     * USED BY PGML.tee
     */
    public void setOwner(Object own) {
        firePropChange("owner", _owner, own);
        _owner = own;
    }

    /** Get and set the points along a path for Figs that are path-like. */
    public void setPoints(Point[] ps) {
    }

    /** deprecated 0.10.5 by Bob Tarling use setPoint(int, int, int) */
    public void setPoints(int i, int x, int y) {
        setPoint(i, x, y);
    }

    public void setPoint(int i, int x, int y) {
    }

    /** deprecated 0.10.5 by Bob Tarling use setPoint(int, Point) */
    public final void setPoints(int i, Point p) {
        setPoint(i, p);
    }

    public final void setPoint(int i, Point p) {
        setPoints(i, p.x, p.y);
    }

    /** deprecated 0.10.5 by Bob Tarling use setPoint(Handle, int, int) */
    public void setPoints(Handle h, int x, int y) {
        setPoint(h, x, y);
    }

    public void setPoint(Handle h, int x, int y) {
        setPoints(h.index, x, y);
    }

    /** deprecated 0.10.5 by Bob Tarling use setPoint(Handle, int, int) */
    public final void setPoints(Handle h, Point p) {
        setPoint(h, p);
    }

    public final void setPoint(Handle h, Point p) {
        setPoints(h, p.x, p.y);
    }

    /**
     *  Derived classes should implement this method
     */
    public void setPrivateData(String data) {
    }

    /** Sets the single flag of the Fig. Has to be overwritten in
     * subclasses interested in this flag.
     */
    public void setSingle(String single) {
    }

    /** Sets the size of the Fig. Fires property "bounds". */
    public void setSize(int w, int h) {
        setBounds(_x, _y, w, h);
    }

    /** Sets the size of the Fig. Fires property "bounds". */
    public final void setSize(Dimension d) {
        setSize(d.width, d.height);
    }

    /**
     * USED BY PGML.tee
     */
    public void setVisState(int visState) {
        _shown = visState;
    }

    public void setWidth(int w) {
        setBounds(_x, _y, w, _h);
    }

    // needs-more-work: property change events?
    public void setX(int x) {
        setBounds(x, _y, _w, _h);
    }

    public void setXs(int[] xs) {
    }

    public void setY(int y) {
        setBounds(_x, y, _w, _h);
    }

    public void setYs(int[] ys) {
    }

    /** Reshape the given rectangle to be my bounding box. */
    public void stuffBounds(Rectangle r) {
        r.setBounds(_x, _y, _w, _h);
    }

    public void stuffPointAlongPerimeter(int dist, Point res) {
        if(dist < _w && dist >= 0) {
            res.x = _x + (dist);
            res.y = _y;
        }
        else if(dist < _w + _h) {
            res.x = _x + _w;
            res.y = _y + (dist - _w);
        }
        else if(dist < _w + _h + _w) {
            res.x = _x + _w - (dist - _w - _h);
            res.y = _y + _h;
        }
        else if(dist < _w + _h + _w + _h) {
            res.x = _x;
            res.y = _y + (_w + _h + _w + _h - dist);
        }
        else {
            res.x = _x;
            res.y = _y;
        }
    }

    /** Change the position of the object from were it is to were it is
     *  plus dx and dy. Often called when an object is dragged. This
     *  could be very useful if local-coordinate systems are used
     *  because deltas need less transforming... maybe. Fires property
     *  "bounds". */
    public void translate(int dx, int dy) {
        Rectangle oldBounds = getBounds();
        _x += dx;
        _y += dy;
        firePropChange("bounds", oldBounds, getBounds());
    }

    public void updateVisState() {
    }

    /** Reply true if the entire Fig is contained within the given
     *  Rectangle. This can be used by ModeSelect to select Figs that
     *  are totally within the selection rectangle. */
    public boolean within(Rectangle r) {
        return r.contains(_x, _y) && r.contains(_x + _w, _y + _h);
    }

    /** Returns true if it is to be displayed.
     * @deprecated 0.10.5 in favour of isVisible()
     */
    public boolean isDisplayed() {
        return isVisible();
    }

    /** Determine if it is to be displayed.
     * @deprecated 0.10.5 in favour of setVisible(boolean)
     */
    public void setDisplayed(boolean isDisplayed) {
        setVisible(isDisplayed);
    }
    
    /** Returns true if the fig is visible */
    public boolean isVisible() {
        return _displayed;
    }

    /** 
     * Set the visible status of the fig
     */
    public void setVisible(boolean isDisplayed) {
        _displayed = isDisplayed;
    }
    
    /**
     * Set whether this Fig can be resized
     * @param true to make this Fig resizable
     */
    public void setResizable(boolean b) {
        resizable = b;
    }

    /**
     * Set whether this Fig can be moved
     * @param true to make this Fig resizable
     */
    public void setMovable(boolean b) {
        movable = b;
    }

}    /* end class Fig */
