// %1035290364839:org.tigris.gef.presentation%
package org.tigris.gef.presentation;

import java.awt.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Enumeration;
import java.util.Vector;

import org.tigris.gef.base.Globals;
import org.tigris.gef.base.PathConv;

import org.tigris.gef.graph.GraphEdgeHooks;

import org.tigris.gef.ui.Highlightable;

/** Abastract Fig class for representing edges between ports.
 *
 *  @see FigEdgeLine
 *  @see FigEdgeRectiline
 */
public abstract class FigEdge extends Fig implements Connecter {
    ////////////////////////////////////////////////////////////////
    // instance variables

    /** Fig presenting the edge's from-port . */
    private Fig _sourcePortFig;

    /** Fig presenting the edge's to-port. */
    private Fig _destPortFig;

    /** FigNode presenting the edge's from-port's parent node. */
    protected FigNode _sourceFigNode;

    /** FigNode presenting the edge's to-port's parent node. */
    protected FigNode _destFigNode;

    /** Fig that presents the edge. */
    protected Fig _fig;

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

    ////////////////////////////////////////////////////////////////
    // inner classes
    private class PathItem implements java.io.Serializable {
        Fig _fig;
        PathConv _path;

        PathItem(Fig f, PathConv pc) {
            _fig = f;
            _path = pc;
        }

        public PathConv getPath() {
            return _path;
        }

        public Fig getFig() {
            return _fig;
        }
    }

    /** Contruct a new FigEdge without any underlying edge. */
    public FigEdge() {
        _fig = makeEdgeFig();
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
        _fig = makeEdgeFig();
        _fig.setGroup(this);
        _fig.setLayer(getLayer());
    }

    /** Add a new path item to this FigEdge. newPath indicates both the
     *  location and the Fig (usually FigText) that should be drawn. */
    public void addPathItem(Fig newFig, PathConv newPath) {
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
        //System.out.println("Bound calculated x="+_x+" y="+_y+" w="+_w+" h="+_h);
    }

    public void cleanUp() {
        _fig.cleanUp();
    }

    ////////////////////////////////////////////////////////////////
    // Routing related methods

    /** Method to compute the route a FigEdge should follow.  By defualt
     *  this does nothing. Sublcasses, like FigEdgeRectiline override
     *  this method. */
    public void computeRoute() {
    }

    public boolean contains(int x, int y) {
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
        if(_sourceFigNode != null) {
            _sourceFigNode.removeFigEdge(this);
        }

        if(_destFigNode != null) {
            _destFigNode.removeFigEdge(this);
        }

        super.removeFromDiagram();
    }

    /** Get and set the flag about using Fig connection points rather
     *  than centers. */
    public boolean getBetweenNearestPoints() {
        return _useNearest;
    }

    ////////////////////////////////////////////////////////////////
    // Fig API

    /** Reply the bounding box for this FigEdge. */
    public Rectangle getBounds() {
        Rectangle r = null;
        return getBounds(r);
    }

    /** Reply the bounding box for this FigEdge. */
    public Rectangle getBounds(Rectangle r) {
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
    public ArrowHead getDestArrowHead() {
        return _arrowHeadEnd;
    }

    /**
     * USED BY PGML.tee
     */
    public Fig getDestFigNode() {
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

    /** Return the Fig that will be drawn.
     * USED BY PGML.tee
     */
    public Fig getFig() {
        return _fig;
    }

    /**
     * The first point ion an edge
     * USED BY PGML.tee
     */
    public Point getFirstPoint() {
        return _fig.getFirstPoint();
    }

    public boolean getHighlight() {
        return _highlight;
    }

    public Point getLastPoint() {
        return _fig.getLastPoint();
    }

    /**
     * USED BY PGML.tee
     */
    public Color getLineColor() {
        return _fig.getLineColor();
    }

    /**
     * An edge cannot be filled with color
     * @return false
     */
    public boolean hasFillColor() {
        return false;
    }

    /**
     * USED BY PGML.tee
     */
    public int getLineWidth() {
        return _fig.getLineWidth();
    }

    public int getNumPoints() {
        return _fig.getNumPoints();
    }

    /** Return the path item on this FigEdge closest to the given
     *  location. needs-more-work: not implemented yet. */
    public Fig getPathItem(PathConv pointOnPath) {
        // needs-more-work: Find the closest Fig to this point
        return null;
    }

    /** Return the fig of a given path item. */
    public Fig getPathItemFig(PathItem pathItem) {
        Fig fig = pathItem.getFig();
        return fig;
    }

    /** Return all figs of the path items */
    public Vector getPathItemFigs() {
        Vector figs = new Vector();
        for(int i = 0; i < _pathItems.size(); i++) {
            figs.add(getPathItemFig((PathItem)_pathItems.elementAt(i)));
        }

        return figs;
    }

    /** Return the vector of path items on this FigEdge. */
    public Vector getPathItemsRaw() {
        return _pathItems;
    }

    public int getPerimeterLength() {
        return _fig.getPerimeterLength();
    }

    public Point[] getPoints() {
        return _fig.getPoints();
    }

    /** @deprecated use getPoint(int) */
    public Point getPoints(int i) {
        return _fig.getPoint(i);
    }

    public Point getPoint(int i) {
        return _fig.getPoint(i);
    }

    /**
     * TODO document
     * Used in SVG.TEE
     */
    public String getPrivateData() {
        Fig spf = getSourcePortFig();
        Fig dpf = getDestPortFig();
        Fig sfn = getSourceFigNode();
        Fig dfn = getDestFigNode();
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
    public ArrowHead getSourceArrowHead() {
        return _arrowHeadStart;
    }

    /**
     * USED BY PGML.tee
     */
    public Fig getSourceFigNode() {
        return _sourceFigNode;
    }

    /**
     * USED BY PGML.tee
     */
    public Fig getSourcePortFig() {
        return _sourcePortFig;
    }

    public int[] getXs() {
        return _fig.getXs();
    }

    public int[] getYs() {
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

    public Fig hitFig(Rectangle r) {
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

    public boolean intersects(Rectangle r) {
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

    public boolean isReshapable() {
        return _fig.isReshapable();
    }

    public boolean isResizable() {
        return _fig.isResizable();
    }

    public boolean isRotatable() {
        return _fig.isRotatable();
    }

    /** Abstract method to make the Fig that will be drawn for this
     *  FigEdge. In FigEdgeLine this method constructs a FigLine. In
     *  FigEdgeRectiline, this method constructs a FigPoly. */
    protected abstract Fig makeEdgeFig();

    /** Paint this FigEdge.  Needs-more-work: take Highlight into account */
    public void paint(Graphics g) {
        //computeRoute();
        _fig.paint(g);
        paintArrowHeads(g);
        paintPathItems(g);
    }

    ////////////////////////////////////////////////////////////////
    // display methods

    /** Paint ArrowHeads on this FigEdge. Called from paint().
     *  Determines placement and orientation by using
     *  pointAlongPerimeter(). */
    protected void paintArrowHeads(Graphics g) {
        _arrowHeadStart.paintAtHead(g, _fig);
        _arrowHeadEnd.paintAtTail(g, _fig);
        //     _arrowHeadStart.paint(g, pointAlongPerimeter(5), pointAlongPerimeter(0));
        //     _arrowHeadEnd.paint(g, pointAlongPerimeter(getPerimeterLength() - 6),
        //          pointAlongPerimeter(getPerimeterLength() - 1));
    }

    public void paintHighlightLine(Graphics g, int x1, int y1, int x2, int y2) {
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
    protected void paintPathItems(Graphics g) {
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

    /** After the file is loaded, re-establish any connections from the
     * model to the Figs */
    public void postLoad() {
        setOwner(getOwner());
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
    public void removePathItem(PathItem goneItem) {
        _pathItems.removeElement(goneItem);
        goneItem.getFig().setGroup(null);
    }

    public void removePathItem(Fig goneFig) {
        for(int i = 0; i < _pathItems.size(); i++) {
            PathItem curItem = (PathItem)_pathItems.elementAt(i);
            if(curItem.getFig() == goneFig) {
                removePathItem(curItem);
                return;
            }
        }
    }

    public void setBetweenNearestPoints(boolean un) {
        _useNearest = un;
    }

    public void setDashed(boolean d) {
        _fig.setDashed(d);
    }

    /** Set the ArrowHead at the end of this FigEdge. */
    public void setDestArrowHead(ArrowHead newArrow) {
        _arrowHeadEnd = newArrow;
    }

    /** Set the FigNode reprenting this FigEdge's to-node. */
    public void setDestFigNode(FigNode fn) {
        // assert fn != null
        try {
            if(_destFigNode != null) {
                _destFigNode.removeFigEdge(this);
            }

            _destFigNode = fn;
            fn.addFigEdge(this);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Set the Fig reprenting this FigEdge's to-port. */
    public void setDestPortFig(Fig fig) {
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
    public void setHighlight(boolean b) {
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

    public void setLineWidth(int w) {
        _fig.setLineWidth(w);
    }

    public void setNumPoints(int npoints) {
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

    public void setPoints(Point[] ps) {
        _fig.setPoints(ps);
        calcBounds();
    }

    public void setPoint(int i, int x, int y) {
        _fig.setPoints(i, x, y);
        calcBounds();
    }

    /** @deprecated 0.10.2 in favour of setPoint(int,int,int) */
    public void setPoints(int i, int x, int y) {
        _fig.setPoints(i, x, y);
        calcBounds();
    }

    public void setPoint(Handle h, int x, int y) {
        _fig.setPoint(h, x, y);
        calcBounds();
    }

    /** @deprecated 0.10.5 in favour of setPoint(Handle,int,int) */
    public void setPoints(Handle h, int x, int y) {
        setPoint(h, x, y);
    }

    /** @deprecated 0.10.2 this method does nothing so lets get rid */
    public void setPrivateData(String data) {
        // this method did nothing, so I commented out this Exception throwing code. Toby
        //    StringTokenizer tokenizer = new StringTokenizer(data,"=\"' ");
        //    while (tokenizer.hasMoreTokens()) {
        //       String tok = tokenizer.nextToken();
        //       if (tok.equals("sourcePortFig")) {
        //          String s = tokenizer.nextToken();
        //          int value = Integer.parseInt( s );
        //       }
        //       else if (tok.equals("destPortFig")) {
        //          String s = tokenizer.nextToken();
        //          int value = Integer.parseInt( s );
        //       }
        //       else if (tok.equals("sourceFigNode")) {
        //          String s = tokenizer.nextToken();
        //          int value = Integer.parseInt( s );
        //       }
        //       else if (tok.equals("destFigNode")) {
        //          String s = tokenizer.nextToken();
        //          int value = Integer.parseInt( s );
        //       }
        //       else {
        //          /* Unknown value */
        //       }
        //    }
    }

    /** Set the ArrowHead at the start of this FigEdge. */
    public void setSourceArrowHead(ArrowHead newArrow) {
        _arrowHeadStart = newArrow;
    }

    /** Set the FigNode reprenting this FigEdge's from-node. */
    public void setSourceFigNode(FigNode fn) {
        // assert fn != null
        try {
            if(_sourceFigNode != null) {
                _sourceFigNode.removeFigEdge(this);
            }

            _sourceFigNode = fn;
            fn.addFigEdge(this);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Get the Fig reprenting this FigEdge's from-port. */
    public void setSourcePortFig(Fig fig) {
        if (fig == null) {
            throw new IllegalArgumentException("A source port must be supplied");
        }
        _sourcePortFig = fig;
    }

    public void setXs(int[] xs) {
        _fig.setXs(xs);
        calcBounds();
    }

    public void setYs(int[] ys) {
        _fig.setYs(ys);
        calcBounds();
    }

    public void stuffPointAlongPerimeter(int dist, Point res) {
        _fig.stuffPointAlongPerimeter(dist, res);
    }

    public void translateEdge(int dx, int dy) {
        _fig.translate(dx, dy);
        calcBounds();
    }

    public void updatePathItemLocations() {
        calcBounds();
    }
}    /* end class FigEdge */
