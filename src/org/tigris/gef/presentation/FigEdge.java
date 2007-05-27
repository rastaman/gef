// %1035290364839:org.tigris.gef.presentation%
package org.tigris.gef.presentation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.util.Enumeration;
import java.util.Vector;

import org.tigris.gef.base.Globals;
import org.tigris.gef.base.PathConv;
import org.tigris.gef.di.GraphEdge;
import org.tigris.gef.graph.GraphEdgeHooks;
import org.tigris.gef.ui.Highlightable;
import org.tigris.gef.undo.Memento;
import org.tigris.gef.undo.UndoManager;

/**
 * Abstract Fig class for representing edges between ports.
 */
public abstract class FigEdge extends Fig implements GraphEdge {
    
    /** Fig presenting the edge's from-port . */
    private Fig _sourcePortFig;

    /** Fig presenting the edge's to-port. */
    private Fig _destPortFig;

    /** FigNode presenting the edge's from-port's parent node. */
    protected FigNode _sourceFigNode;

    /** FigNode presenting the edge's to-port's parent node. */
    protected FigNode _destFigNode;

    /** Fig that presents the edge. */
    private Fig _fig;

    /** True if the FigEdge should be drawn from the nearest point of
     *  each port Fig. */
    protected boolean _useNearest = false;

    /** True when the FigEdgde should be drawn highlighted. */
    protected boolean _highlight = false;

    /** The ArrowHead at the start of the line */
    protected ArrowHead _arrowHeadStart = ArrowHeadNone.TheInstance;

    /** The ArrowHead at the end of the line */
    protected ArrowHead _arrowHeadEnd = ArrowHeadNone.TheInstance;

    /** The items that are accumulated along the path, a vector. */
    protected Vector _pathItems = new Vector();

    private class PathItem implements java.io.Serializable {
	
	private static final long serialVersionUID = -5298572087861993804L;
	final Fig _fig;
        final PathConv _path;

        PathItem(final Fig f, final PathConv pc) {
            _fig = f;
            _path = pc;
        }

        final public PathConv getPath() {
            return _path;
        }

        final public Fig getFig() {
            return _fig;
        }
    }

    /** Contruct a new FigEdge without any underlying edge. */
    public FigEdge() {
        setFig(makeEdgeFig());
    }

    ////////////////////////////////////////////////////////////////
    // constructors

    /** Contruct a new FigEdge with the given source and destination
     *  port figs and FigNodes.  The new FigEdge will represent the
     *  given edge (an object from some underlying model). */
    public FigEdge(Fig s, Fig d, FigNode sfn, FigNode dfn, Object edge) {
        setSourcePortFig(s);
        setDestPortFig(d);
        setSourceFigNode(sfn);
        setDestFigNode(dfn);
        setOwner(edge);
        setFig(makeEdgeFig());
        _fig.setGroup(this);
        _fig.setLayer(getLayer());
    }
    
    /** Add a new path item to this FigEdge. newPath indicates both the
     *  location and the Fig (usually FigText) that should be drawn. */
    final public void addPathItem(Fig newFig, PathConv newPath) {
        _pathItems.addElement(new PathItem(newFig, newPath));
        newFig.setGroup(this);
    }

    /** Update my bounding box */
    public void calcBounds() {
        _fig.calcBounds();
        Rectangle res = _fig.getBounds();
        Point loc = new Point();
        int size = _pathItems.size();
        for(int i = 0; i < size; i++) {
            PathItem element = (PathItem)_pathItems.elementAt(i);
            PathConv pc = element.getPath();
            Fig f = element.getFig();
            int oldX = f.getX();
            int oldY = f.getY();
            int halfWidth = f.getWidth() / 2;
            int halfHeight = f.getHeight() / 2;
            pc.stuffPoint(loc);
            if(oldX != loc.x || oldY != loc.y) {
                f.damage();
                f.setLocation(loc.x - halfWidth, loc.y - halfHeight);
            }

            res.add(f.getBounds());
        }

        _x = res.x;
        _y = res.y;
        _w = res.width;
        _h = res.height;
    }

    final public void cleanUp() {
        _fig.cleanUp();
    }

    ////////////////////////////////////////////////////////////////
    // Routing related methods

    /** Method to compute the route a FigEdge should follow.  By defualt
     *  this does nothing. Sublcasses, like FigEdgeRectiline override
     *  this method. */
    final public void computeRoute() {
        if (UndoManager.getInstance().isGenerateMementos()) {
            Memento memento = new Memento() {
                Point[] points = getPoints();
                
                public void undo() {
                	UndoManager.getInstance().addMementoLock(this);
                	Point[] newpoints = getPoints();
                    setPoints(points);
                    points = newpoints;
                    damage();
                    UndoManager.getInstance().removeMementoLock(this);
                }
                public void redo() {
                    undo();
                }
                public void dispose() {}
                
                public String toString() {
                    return (isStartChain() ? "*" : " ") + "ComputeRouteMemento " + points;
                }
            };
            UndoManager.getInstance().addMemento(memento);
        }
        computeRouteImpl();
    }
    
    abstract public void computeRouteImpl();
    
    final public boolean contains(int x, int y) {
        if(_fig.contains(x, y)) {
            return true;
        }

        int size = _pathItems.size();
        for(int i = 0; i < size; i++) {
            Fig f = ((PathItem)_pathItems.elementAt(i)).getFig();
            if(f.contains(x, y)) {
                return true;
            }
        }

        return false;
    }

    public void removeFromDiagram() {
        if(_sourceFigNode instanceof FigNode) {
            ((FigNode)_sourceFigNode).removeFigEdge(this);
        }

        if(_destFigNode instanceof FigNode) {
            ((FigNode)_destFigNode).removeFigEdge(this);
        }

        super.removeFromDiagram();
    }

    /** Get and set the flag about using Fig connection points rather
     *  than centers. */
    final public boolean getBetweenNearestPoints() {
        return _useNearest;
    }

    ////////////////////////////////////////////////////////////////
    // Fig API

    /** Reply the bounding box for this FigEdge. */
    final public Rectangle getBounds(Rectangle r) {
        if(r == null) {
            r = new Rectangle();
        }

        _fig.getBounds(r);
        int size = _pathItems.size();
        for(int pathItemIndex = 0; pathItemIndex < size; pathItemIndex++) {
            PathItem pathItem = (PathItem)_pathItems.get(pathItemIndex);
            Fig f = pathItem.getFig();
            r.add(f.getBounds());
        }

        return r;
    }

    public boolean getDashed() {
        return _fig.getDashed();
    }

    /** Get the ArrowHead at the end of this FigEdge. */
    final public ArrowHead getDestArrowHead() {
        return _arrowHeadEnd;
    }

    /**
     * USED BY PGML.tee
     */
    final public FigNode getDestFigNode() {
        return _destFigNode;
    }

    /**
     * USED BY PGML.tee
     */
    public Fig getDestPortFig() {
        return _destPortFig;
    }

    ////////////////////////////////////////////////////////////////
    // accessors

    /**
     * Return the Fig that will be drawn.
     * USED BY PGML.tee
     */
    final public Fig getFig() {
        return _fig;
    }

    /**
     * The first point ion an edge
     * USED BY PGML.tee
     */
    final public Point getFirstPoint() {
        return _fig.getFirstPoint();
    }

    final public boolean getHighlight() {
        return _highlight;
    }

    final public Point getLastPoint() {
        return _fig.getLastPoint();
    }

    /**
     * USED BY PGML.tee
     */
    final public Color getLineColor() {
        return _fig.getLineColor();
    }

    /**
     * An edge cannot be filled with color
     * @return false
     */
    final public boolean hasFillColor() {
        return false;
    }

    /**
     * USED BY PGML.tee
     */
    final public int getLineWidth() {
        return _fig.getLineWidth();
    }

    final public int getNumPoints() {
        return _fig.getNumPoints();
    }

    /** Return the path item on this FigEdge closest to the given
     *  location. needs-more-work: not implemented yet. */
    final public Fig getPathItem(PathConv pointOnPath) {
        // needs-more-work: Find the closest Fig to this point
        return null;
    }

    /** Return the fig of a given path item. */
    final public Fig getPathItemFig(PathItem pathItem) {
        Fig fig = pathItem.getFig();
        return fig;
    }

    /** Return all figs of the path items */
    final public Vector getPathItemFigs() {
        Vector figs = new Vector();
        for(int i = 0; i < _pathItems.size(); i++) {
            figs.add(getPathItemFig((PathItem)_pathItems.elementAt(i)));
        }

        return figs;
    }

    /** Return the vector of path items on this FigEdge. */
    final public Vector getPathItemsRaw() {
        return _pathItems;
    }

    final public int getPerimeterLength() {
        return _fig.getPerimeterLength();
    }

    final public Point[] getPoints() {
        return _fig.getPoints();
    }

    final public Point getPoint(int i) {
        return _fig.getPoint(i);
    }

    /**
     * TODO document
     * Used in SVG.TEE
     */
    public String getPrivateData() {
        Fig spf = getSourcePortFig();
        Fig dpf = getDestPortFig();
        FigNode sfn = getSourceFigNode();
        FigNode dfn = getDestFigNode();
        String data = "";
        if(spf != null) {
            data += "sourcePortFig=\"" + spf.getId() + "\" ";
        }

        if(dpf != null) {
            data += "destPortFig=\"" + dpf.getId() + "\" ";
        }

        if(sfn != null) {
            data += "sourceFigNode=\"" + sfn.getId() + "\" ";
        }

        if(dfn != null) {
            data += "destFigNode=\"" + dfn.getId() + "\" ";
        }

        return data;
    }

    /** Get the ArrowHead at the start of this FigEdge. */
    final public ArrowHead getSourceArrowHead() {
        return _arrowHeadStart;
    }

    /**
     * USED BY PGML.tee
     */
    final public FigNode getSourceFigNode() {
        return _sourceFigNode;
    }

    /**
     * USED BY PGML.tee
     */
    public Fig getSourcePortFig() {
        return _sourcePortFig;
    }

    final public int[] getXs() {
        return _fig.getXs();
    }

    final public int[] getYs() {
        return _fig.getYs();
    }

    public boolean hit(Rectangle r) {
        if(_fig.hit(r)) {
            return true;
        }

        int size = _pathItems.size();
        for(int i = 0; i < size; i++) {
            Fig f = ((PathItem)_pathItems.elementAt(i)).getFig();
            if(f.isAnnotation() && f.hit(r)) {
                return true;
            }
        }

        return false;
    }

    final public Fig hitFig(Rectangle r) {
        Enumeration iter = _pathItems.elements();
        Fig res = null;
        if(_fig.hit(r)) {
            res = _fig;
        }

        while(iter.hasMoreElements()) {
            PathItem pi = (PathItem)iter.nextElement();
            Fig f = pi.getFig();
            if(f.hit(r)) {
                res = f;
            }
        }

        return res;
    }

    final public boolean intersects(Rectangle r) {
        if(_fig.intersectsPerimeter(r)) {
            //System.out.println("Intersects perimeter");
            return true;
        }

        int size = _pathItems.size();
        for(int i = 0; i < size; i++) {
            Fig f = ((PathItem)_pathItems.elementAt(i)).getFig();
            //only pathitems represented in a layer (i.e. being displayed) are of interest
            if(f.getLayer() != null && f.intersects(r)) {
                //System.out.println("Intersects");
                return true;
            }
        }

        //System.out.println("Doesn't intersect");
        return false;
    }

    final public boolean isReshapable() {
        return _fig.isReshapable();
    }

    final public boolean isResizable() {
        return _fig.isResizable();
    }

    final public boolean isRotatable() {
        return _fig.isRotatable();
    }

    /** 
     * Abstract method to make the Fig that will be drawn for this
     * FigEdge. In FigEdgeLine this method constructs a FigLine. In
     * FigEdgeRectiline, this method constructs a FigPoly.
     */
    protected abstract Fig makeEdgeFig();

    /**
     * Paint this FigEdge.  TODO: take Highlight into account
     */
    public void paint(Object graphicContext) {
        //computeRoute();
        Graphics g = (Graphics)graphicContext;
        _fig.paint(g);
        paintArrowHeads(g);
        paintPathItems(g);
    }

    public void appendSvg(StringBuffer sb) {
        //computeRoute();
        _fig.appendSvg(sb);
        //appendSvgArrowHeads(g);
        appendSvgPathItems(sb);
    }

    ////////////////////////////////////////////////////////////////
    // display methods

    /**
     * Paint ArrowHeads on this FigEdge. Called from paint().
     */
    final protected void paintArrowHeads(Object g) {
        _arrowHeadStart.paintAtHead(g, _fig);
        _arrowHeadEnd.paintAtTail(g, _fig);
    }

    final public void paintHighlightLine(Graphics g, int x1, int y1, int x2, int y2) {
        g.setColor(Globals.getPrefs().getHighlightColor());    /* needs-more-work */
        double dx = (x2 - x1);
        double dy = (y2 - y1);
        double denom = Math.sqrt(dx * dx + dy * dy);
        if(denom == 0) {
            return;
        }

        double orthoX = dy / denom;
        double orthoY = -dx / denom;
        // needs-more-work: should fill poly instead
        for(double i = 2.0; i < 5.0; i += 0.27) {
            int hx1 = (int)(x1 + i * orthoX);
            int hy1 = (int)(y1 + i * orthoY);
            int hx2 = (int)(x2 + i * orthoX);
            int hy2 = (int)(y2 + i * orthoY);
            g.drawLine(hx1, hy1, hx2, hy2);
        }
    }

    /** Paint any labels that are located relative to this FigEdge. */
    final protected void paintPathItems(Graphics g) {
        Vector pathVec = getPathItemsRaw();
        for(int i = 0; i < pathVec.size(); i++) {
            PathItem element = (PathItem)pathVec.elementAt(i);
            //       PathConv path = element.getPath();
            Fig f = element.getFig();
            //       int halfWidth = f.getWidth() / 2;
            //       int halfHeight = f.getHeight() / 2;
            //       Point loc = path.getPoint();
            //       f.setLocation(loc.x - halfWidth, loc.y - halfHeight);
            f.paint(g);
        }
    }

    /** Paint any labels that are located relative to this FigEdge. */
    final protected void appendSvgPathItems(StringBuffer sb) {
        Vector pathVec = getPathItemsRaw();
        for(int i = 0; i < pathVec.size(); i++) {
            PathItem element = (PathItem)pathVec.elementAt(i);
            Fig f = element.getFig();
            f.appendSvg(sb);
        }
    }

    ////////////////////////////////////////////////////////////////
    // notifications and updates
    public void propertyChange(PropertyChangeEvent pce) {
        //System.out.println("FigEdge got a PropertyChangeEvent");
        String pName = pce.getPropertyName();
        Object src = pce.getSource();
        if(pName.equals("disposed") && src == getOwner()) {
            removeFromDiagram();
        }

        if(pName.equals("highlight") && src == getOwner()) {
            _highlight = ((Boolean)pce.getNewValue()).booleanValue();
            damage();
        }
    }

    /** Removes the given path item. */
    final public void removePathItem(PathItem goneItem) {
        _pathItems.removeElement(goneItem);
        goneItem.getFig().setGroup(null);
    }

    final public void removePathItem(Fig goneFig) {
        for(int i = 0; i < _pathItems.size(); i++) {
            PathItem curItem = (PathItem)_pathItems.elementAt(i);
            if(curItem.getFig() == goneFig) {
                removePathItem(curItem);
                return;
            }
        }
    }

    final public void setBetweenNearestPoints(boolean un) {
        _useNearest = un;
    }

    final public void setDashed(boolean d) {
        _fig.setDashed(d);
    }

    /** Set the ArrowHead at the end of this FigEdge. */
    final public void setDestArrowHead(ArrowHead newArrow) {
        _arrowHeadEnd = newArrow;
    }

    /** Set the FigNode reprenting this FigEdge's to-node. */
    public void setDestFigNode(FigNode fn) {
        // assert fn != null
        try {
            if(_destFigNode instanceof FigNode) {
                ((FigNode)_destFigNode).removeFigEdge(this);
            }

            _destFigNode = fn;
            fn.addFigEdge(this);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Set the Fig reprenting this FigEdge's to-port. */
    final public void setDestPortFig(Fig fig) {
        if (fig == null) {
            throw new IllegalArgumentException("A destination port must be supplied");
        }
        _destPortFig = fig;
    }

    public void setFig(Fig f) {
        if(_fig != null && _fig.getGroup() == this) {
            _fig.setGroup(null);
        }

        _fig = f;
        _fig.setGroup(this);
        _fig.setLayer(getLayer());
    }

    ////////////////////////////////////////////////////////////////
    // Highlightable implementation
    final public void setHighlight(boolean b) {
        _highlight = b;
        damage();
    }

    /**
     * Sets the line color of the edge and of soure/destination arrows.
     * @param c
     */
    public void setLineColor(Color c) {
        _fig.setLineColor(c);
        getSourceArrowHead().setLineColor(c);
        getDestArrowHead().setLineColor(c);
    }

    final public void setLineWidth(int w) {
        _fig.setLineWidth(w);
    }

    final public void setNumPoints(int npoints) {
        _fig.setNumPoints(npoints);
        calcBounds();
    }

    /** Set the edge (some object in an underlying model) that this
     *  FigEdge should represent. */
    public void setOwner(Object own) {
        //System.out.println("Setting owner of " + this + " to " + own);
        Object oldOwner = getOwner();
        if(oldOwner instanceof GraphEdgeHooks) {
            ((GraphEdgeHooks)oldOwner).removePropertyChangeListener(this);
        }
        else if(oldOwner instanceof Highlightable) {
            ((Highlightable)oldOwner).removePropertyChangeListener(this);
        }

        if(own instanceof GraphEdgeHooks) {
            ((GraphEdgeHooks)own).addPropertyChangeListener(this);
        }
        else if(own instanceof Highlightable) {
            ((Highlightable)own).addPropertyChangeListener(this);
        }

        super.setOwner(own);
    }

    final public void setPoints(Point[] ps) {
        _fig.setPoints(ps);
        calcBounds();
    }

    final public void setPoint(int i, int x, int y) {
        _fig.setPoint(i, x, y);
        calcBounds();
    }

    public void setPoint(Handle h, int x, int y) {
        _fig.setPoint(h, x, y);
        calcBounds();
    }

    /** Set the ArrowHead at the start of this FigEdge. */
    final public void setSourceArrowHead(ArrowHead newArrow) {
        _arrowHeadStart = newArrow;
    }

    /** Set the FigNode reprenting this FigEdge's from-node. */
    public void setSourceFigNode(FigNode fn) {
        // assert fn != null
        try {
            if(_sourceFigNode instanceof FigNode) {
                ((FigNode)_sourceFigNode).removeFigEdge(this);
            }

            _sourceFigNode = fn;
            fn.addFigEdge(this);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Get the Fig reprenting this FigEdge's from-port. */
    final public void setSourcePortFig(Fig fig) {
        if (fig == null) {
            throw new IllegalArgumentException("A source port must be supplied");
        }
        _sourcePortFig = fig;
    }

    final public void setXs(int[] xs) {
        _fig.setXs(xs);
        calcBounds();
    }

    final public void setYs(int[] ys) {
        _fig.setYs(ys);
        calcBounds();
    }

    final public void stuffPointAlongPerimeter(int dist, Point res) {
        _fig.stuffPointAlongPerimeter(dist, res);
    }

    final public void translateEdge(final int dx, final int dy) {
        _fig.translate(dx, dy);
        calcBounds();
    }

    public void translateImpl(final int dx, final int dy) {
        _fig.translate(dx, dy);
        calcBounds();
    }

    final public void updatePathItemLocations() {
        calcBounds();
    }
}    /* end class FigEdge */
