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



// File: FigNode.java
// Classes: FigNode
// Original Author: ics125 spring 1996
// $Id$

package org.tigris.gef.presentation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tigris.gef.base.Globals;
import org.tigris.gef.graph.GraphNodeHooks;
import org.tigris.gef.graph.GraphPortHooks;
import org.tigris.gef.ui.Highlightable;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Class to present a node (such as a NetNode) in a diagram. */

public class FigNode extends FigGroup implements
        Connecter,
        MouseListener {
    ////////////////////////////////////////////////////////////////
    // constants

    private static final Log LOG = LogFactory.getLog(FigNode.class);
    /** Constants useful for determining what side (north, south, east,
     *  or west) a port is located on.*/
    public static final double ang45 = Math.PI / 4;
    public static final double ang135 = 3 * Math.PI / 4;
    public static final double ang225 = 5 * Math.PI / 4;
    public static final double ang315 = 7 * Math.PI / 4;

    ////////////////////////////////////////////////////////////////
    // instance variables

    /** True if you want ports to show when the mouse moves in and
     *  be invisible otherwise. */
    protected boolean _blinkPorts = false;

    /** True when we want to draw the user's attention to this FigNode. */
    protected boolean _highlight = false;

    /** A list of FigEdges that need to be rerouted when this FigNode
     *  moves.
     */
    private ArrayList figEdges = new ArrayList();

    ////////////////////////////////////////////////////////////////
    // constructors

    public FigNode() {
    }

    /** Constructs a new FigNode on the given node with the given owner.
     * @param node The model item that this node represents
     */
    public FigNode(Object node) {
        setOwner(node);
        // if (node instanceof GraphNodeHooks)
        // ((GraphNodeHooks)node).addPropertyChangeListener(this);
    }

    /** Constructs a new FigNode on the given node with the given owner
     *  and Figs.
     * @param node the model item that this node represents
     * @param figs the figs to be contained as a group by this FigNode 
     */
    public FigNode(Object node, Collection figs) {
        this(node);
        setFigs(figs);
    }

    /**
     * Returns true if dragging from a port on this fig should 
     * automatically go to mode ModeCreateEdge 
     * @return the drag connectable property
     */
    public boolean isDragConnectable() {
        return true;
    }

    public Object clone() {
        FigNode figClone = (FigNode)super.clone();
        figClone.figEdges = (ArrayList)figEdges.clone();
        return figClone;
    }

    ////////////////////////////////////////////////////////////////
    // accessors

    /** Set the property of highlighting ports when the user moves the
     *  mouse over this FigNode. */
    public void setBlinkPorts(boolean b) {
        _blinkPorts = b;
        hidePorts();
    }

    /**
     * @deprecated in favour of isBlinkPorts()
     * @return
     */
    public boolean getBlinkPorts() {
        return _blinkPorts;
    }

    /**
     * Determine if ports are set to appear only on mouseover.
     * @return true if ports are set to appear only on mouseover.
     */
    public boolean isBlinkPorts() {
        return _blinkPorts;
    }


    /** Adds a FigEdge to the list of them that need to be rerouted when
     *  this FigNode moves. */
    public void addFigEdge(FigEdge fe) {
        figEdges.add(fe);
    }

    /** removes a FigEdge from the list of them that need to be rerouted when
     *  this FigNode moves. */
    public void removeFigEdge(FigEdge fe) {
        figEdges.remove(fe);
    }


    public Collection getFigEdges(Collection c) {
        if (c == null) return figEdges;
        c.addAll(figEdges);
        return c;
    }

    public List getFigEdges() {
        return (List)(figEdges.clone());
    }

    /** Sets the owner (a node in some underlying model). If the given
     *  node implements GraphNodeHooks, then the FigNode will register
     *  itself as a listener on the node.
     */
    public void setOwner(Object node) {
        Object oldOwner = getOwner();
        if(oldOwner instanceof GraphNodeHooks)
            ((GraphNodeHooks)oldOwner).removePropertyChangeListener(this);
        else if(oldOwner instanceof Highlightable)
            ((Highlightable)oldOwner).removePropertyChangeListener(this);

        if(node instanceof GraphNodeHooks)
            ((GraphNodeHooks)node).addPropertyChangeListener(this);
        else if(node instanceof Highlightable)
            ((Highlightable)node).addPropertyChangeListener(this);

        super.setOwner(node);
    }

    /** Returns true if any Fig in the group hits the given rect. */
    public boolean hit(Rectangle r) {
        int cornersHit = countCornersContained(r.x, r.y, r.width, r.height);
        if(_filled)
            return cornersHit > 0;
        else
            return cornersHit > 0 && cornersHit < 4;
    }

    public boolean contains(int x, int y) {
        return (_x <= x) && (x <= _x + _w) && (_y <= y) && (y <= _y + _h);
    }

    public void setEnclosingFig(Fig f) {
        if(f != null && f != getEnclosingFig() && getLayer() != null) {
            int edgeCount = figEdges.size();
            for(int i = 0; i < edgeCount; ++i) {
                FigEdge fe = (FigEdge)figEdges.get(i);
                getLayer().bringInFrontOf(fe, f);
            }
        }
        super.setEnclosingFig(f);
        //System.out.println("enclosing fig has been set");
    }

    ////////////////////////////////////////////////////////////////
    // Editor API

    /** When a FigNode is damaged, all of its edges may need repainting. */
    public void endTrans() {
        int edgeCount = figEdges.size();
        for(int i = 0; i < edgeCount; ++i) {
            FigEdge f = (FigEdge)figEdges.get(i);
            f.endTrans();
        }
        super.endTrans();
    }

    /** When a FigNode is deleted, all of its edges are deleted.
     * @deprecated 0.10.7 use removeFromDiagram
     */
    public void delete() {
        removeFromDiagram();
    }

    /** When a FigNode is removed, all of its edges are removed. */
    public void removeFromDiagram() {
        // remove the edges in reverse order because to make sure
        // that other edges in figEdge don't have their position
        // altered as a side effect.
        for(int i = figEdges.size()-1; i >= 0; --i) {
            FigEdge f = (FigEdge)figEdges.get(i);
            f.removeFromDiagram();
        }
        super.removeFromDiagram();
    }

    /** When a FigNode is disposed, all of its edges are disposed. */
    public void deleteFromModel() {
        LOG.debug("Deleting FigNode from model");
        // delete the edges in reverse order because to make sure
        // that other edges in figEdge don't have their position
        // altered as a side effect.
        for(int i = figEdges.size()-1; i >= 0; --i) {
            FigEdge f = (FigEdge)figEdges.get(i);
            f.deleteFromModel();
        }
        super.deleteFromModel();
    }

    /** When a FigNode is disposed, all of its edges are disposed.
     * @depreacted 0.10.7 use deleteFromModel()
     */
    public void dispose() {
        deleteFromModel();
    }


    ////////////////////////////////////////////////////////////////
    // ports

    /** Sets the port (some object in an underlying model) for Fig f.  f
     *  must already be contained in the FigNode. f will now represent
     *  the given port. */
    public void bindPort(Object port, Fig f) {
        Fig oldPortFig = getPortFig(port);
        if(oldPortFig != null)
            oldPortFig.setOwner(null); //?
        f.setOwner(port);
    }

    /** Removes a port from the current FigNode. */
    public void removePort(Fig rep) {
        if(rep.getOwner() != null)
            rep.setOwner(null);
    }

    /** Reply the NetPort associated with the topmost Fig under the mouse, or
     *  null if there is none. */
    public final Object hitPort(Point p) {
        return hitPort(p.x, p.y);
    }

    /** Reply the port that "owns" the topmost Fig under the given point, or
     *  null if none. */
    public Object hitPort(int x, int y) {
        Fig f = hitFig(new Rectangle(x, y, 1, 1));
        if(f != null) {
            Object owner = f.getOwner();
            return owner;
        } else {
            return null;
        }
    }

    /** Reply a port for the topmost Fig that actually has a port. This
     *  allows users to drag edges to or from ports that are hidden by
     *  other Figs. */
    public Object deepHitPort(int x, int y) {
        int figCount = figs.size();
        for (int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig f = (Fig)figs.get(figIndex);
            Object own = f.getOwner();
            // assumes ports are always filled
            if (f.contains(x, y) && own != null) {
                return own;
            }
        }

        Rectangle r = new Rectangle(x - 16, y - 16, 32, 32);
        for (int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig f = (Fig)figs.get(figIndex);
            Object own = f.getOwner();
            // assumes ports are always filled
            if (f.hit(r) && own != null) {
                return own;
            }
        }

        return null;
    }


    /** Reply the Fig that displays the given NetPort. */
    public Fig getPortFig(Object np) {
        int figCount = figs.size();
        for(int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig f = (Fig)figs.get(figIndex);
            if(f.getOwner() == np)
                return f;
        }
        return null;
    }

    /** Get all the figs that have some port as their owner
     * @param figs a collection to which to add the figs or null
     * @return the collection of figs
     */
    public List getPortFigs() {
        ArrayList portFigs = new ArrayList();
		int figCount = figs.size();
		for(int figIndex = 0; figIndex < figCount; ++figIndex) {
			Fig f = (Fig)figs.get(figIndex);
			if(isPortFig(f)) {
                portFigs.add(f);
			}
		}
        return portFigs;
    }

	private boolean isPortFig(Fig f) {
		boolean retVal = (f.getOwner() != null);
		if (retVal && getOwner() instanceof GraphNodeHooks) {
			retVal = f.getOwner() instanceof GraphPortHooks;
		}
		return retVal;
	}
	
    ////////////////////////////////////////////////////////////////
    // diagram-level operations

    /** Reply the port's sector within the current view.  This version
     *  works precisely with square FigNodes the angxx constants
     *  should be removed and calculated by the port if non-square
     *  FigNodes will be used.
     *
     *  <pre>Sectors
     *		      \  1   /
     *		       \    /
     *		        \  /
     *		     2   \/   -2
     *			 /\
     *		        /  \
     *		       /    \
     *		      /  -1  \ </pre>
     **/

    public int getPortSector(Fig portFig) {
        Rectangle nodeBBox = getBounds();
        Rectangle portBBox = portFig.getBounds();
        int nbbCenterX = nodeBBox.x + nodeBBox.width / 2;
        int nbbCenterY = nodeBBox.y + nodeBBox.height / 2;
        int pbbCenterX = portBBox.x + portBBox.width / 2;
        int pbbCenterY = portBBox.y + portBBox.height / 2;
        int dX = pbbCenterX - nbbCenterX;
        int dY = pbbCenterY - nbbCenterY;

        //
        //   the key is the tangent of this rectangle
        //
        //   if you didn't care about divisions by zero,
        //       you could do
        //
        //   tangentBox = nodeBBox.height/nodeBBox.width;
        //   tangentCenters = dY/dX;
        //   if(Math.abs(tangentCenters) > tangentBox) sector 1 or -1
        //
        int sector = -1;
        if(Math.abs(dY * nodeBBox.width) > Math.abs(nodeBBox.height * (dX))) {
            if(dY > 0) {
                sector = 1;
            }
        }
        else {
            sector = 2;
            if(dX > 0) {
                sector = -2;
            }
        }
        return sector;
    }


    ////////////////////////////////////////////////////////////////
    // painting methods

    /** Paints the FigNode to the given Graphics. Calls super.paint to
     *  paint all the Figs contained in the FigNode. Also can draw a
     *  highlighting rectangle around the FigNode. Needs-more-work:
     *  maybe I should implement LayerHighlight instead. */
    public void paint(Graphics g) {
        super.paint(g);
        //System.out.println("[FigNode] paint: owner = " + getOwner());
        if(_highlight) {
            g.setColor(Globals.getPrefs().getHighlightColor()); /* needs-more-work */
            g.drawRect(_x - 5, _y - 5, _w + 9, _h + 8);
            g.drawRect(_x - 4, _y - 4, _w + 7, _h + 6);
            g.drawRect(_x - 3, _y - 3, _w + 5, _h + 4);
        }
    }


    ////////////////////////////////////////////////////////////////
    // Highlightable implementation
    public void setHighlight(boolean b) {
        _highlight = b;
        damage();
    }

    public boolean getHighlight() {
        return _highlight;
    }


    ////////////////////////////////////////////////////////////////
    // notifications and updates

    /** The node object that this FigNode is presenting has changed
     *  state, or been disposed or highlighted. */
    public void propertyChange(PropertyChangeEvent pce) {
        //System.out.println("FigNode got a PropertyChangeEvent");
        String pName = pce.getPropertyName();
        Object src = pce.getSource();
        if(pName.equals("disposed") && src == getOwner()) {
            removeFromDiagram();
        }
        if(pName.equals("highlight") && src == getOwner())
            setHighlight(((Boolean)pce.getNewValue()).booleanValue());
    }


    /** Make the port Figs visible. Used when blinkingPorts is true. */
    public void showPorts() {
        int figCount = this.figs.size();
        for(int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig f = (Fig)this.figs.get(figIndex);
            if(f.getOwner() != null) {
                f.setLineWidth(1);
                f.setFilled(true);
            }
        }
        endTrans();
    }

    /** Make the port Figs invisible. Used when blinkingPorts is true. */
    public void hidePorts() {
        int figCount = this.figs.size();
        for(int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig f = (Fig)this.figs.get(figIndex);
            if(f.getOwner() != null) {
                f.setLineWidth(0);
                f.setFilled(false);
            }
        }
        endTrans();
    }

    ////////////////////////////////////////////////////////////////
    // event handlers

    /** If the mouse enters this FigNode's bbox and the
     *  _blinkPorts flag is set, then show ports. */
    public void mouseEntered(MouseEvent me) {
        if(_blinkPorts)
            showPorts();
    }

    /** If the mouse exits this FigNode's bbox and the
     *  _blinkPorts flag is set, then hide ports. */
    public void mouseExited(MouseEvent me) {
        if(_blinkPorts)
            hidePorts();
    }

    /** Do nothing when mouse is pressed in FigNode. */
    public void mousePressed(MouseEvent me) {
    }

    /** Do nothing when mouse is released in FigNode. */
    public void mouseReleased(MouseEvent me) {
    }

    /** Do nothing when mouse is clicked in FigNode. */
    public void mouseClicked(MouseEvent me) {
    }


    public void translate(int dx, int dy) {
        super.translate(dx, dy);
        updateEdges();
    }

    public void superTranslate(int dx, int dy) {
        super.translate(dx, dy);
    }

    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        updateEdges();
    }

    public void updateEdges() {
        int edgeCount = figEdges.size();
        for(int edgeIndex = 0; edgeIndex < edgeCount; ++edgeIndex) {
            FigEdge fe = (FigEdge)figEdges.get(edgeIndex);
            //System.out.println("[FigNode] update edge " + fe.toString());
            fe.computeRoute();
        }
    }

    /** After the file is loaded, re-establish any connections from the
     * model to the Figs */
    public void postLoad() {
        setOwner(getOwner());
    }

    public void cleanUp() {
        int edgeCount = figEdges.size();
        for(int i = 0; i < edgeCount; ++i) {
            FigEdge fe = (FigEdge)figEdges.get(i);
            fe.cleanUp();
        }
    }
}


