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
// File: LayerDiagram.java
// Classes: LayerDiagram
// Original Author: jrobbins@ics.uci.edu
// $Id$
package org.tigris.gef.base;

import java.awt.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.logging.*;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.presentation.FigPainter;

/** A Layer like found in many drawing applications. It contains a
 *  collection of Fig's, ordered from back to front. Each
 *  LayerDiagram contains part of the overall picture that the user is
 *  drawing.  Needs-More-Work: eventually add a "Layers" menu to the
 *  Editor.
 *  <A HREF="../features.html#graph_visualization">
 *  <TT>FEATURE: graph_visualization</TT></A>
 */
public class LayerDiagram extends Layer {
    ////////////////////////////////////////////////////////////////
    // instance variables

    /** The Fig's that are contained in this layer. */
    private List _contents = new ArrayList();

    /** A counter so that layers have default names like 'One', 'Two', ... */
    protected static int _nextLayerNumbered = 1;

    private static Log LOG = LogFactory.getLog(LayerDiagram.class);
    
    ////////////////////////////////////////////////////////////////
    // constuctors and related methods

    /** Construct a new LayerDiagram with a default name and do not put
     *  it on the Layer's menu. */
    public LayerDiagram() {
        this("Layer" + numberWordFor(_nextLayerNumbered++));
    }

    /** Construct a new LayerDiagram with the given name, and add it to
     *  the menu of layers. Needs-More-Work: I have not implemented a
     *  menu of layers yet. I don't know if that is really the right user
     *  interface. */
    public LayerDiagram(String name) {
        super(name);
        _onMenu = true;
    }

    public Enumeration elements() {
        return Collections.enumeration(_contents);
    }

    /** A utility function to give the spelled-out word for numbers. */
    protected static String numberWordFor(int n) {
        switch(n) {

            case 1:
                return "One";

            case 2:
                return "Two";

            case 3:
                return "Three";

            case 4:
                return "Four";

            case 5:
                return "Five";

            case 6:
                return "Six";

            case 7:
                return "Seven";

            case 8:
                return "Eight";

            case 9:
                return "Nine";

            default:
                return "Layer " + n;
        }
    }

    ////////////////////////////////////////////////////////////////
    // accessors

    /** Add a Fig to the contents of this layer. Items are
     *  added on top of all other items.
     * @param f the fig to add
     * @throws IllegalArgumentException if the fig is null
     */
    public void add(Fig f) {
        if (f == null) {
            throw new IllegalArgumentException("Attempted to add a null fig to a LayerDiagram");
        }
        
        _contents.remove(f);    // act like a set
        _contents.add(f);
        f.setLayer(this);
        f.endTrans();
    }

    /** Add a Fig to the contents of this layer. Items are
     *  added on top of all other items.
     * @param f the fig to insert
     * @throws IllegalArgumentException if the fig is null
     */
    public void insertAt(Fig f, int index) {
        if (f == null) {
            throw new IllegalArgumentException("Attempted to insert a null fig to a LayerDiagram");
        }

        _contents.remove(f);    // act like a set
        _contents.add(index, f);
        f.setLayer(this);
        f.endTrans();
    }

    /** Add a Fig to the contents of this layer. Items are
     *  added on top of all other items.
     * @param f the fig to insert
     * @throws IllegalArgumentException if the fig is null
     */
    public int indexOf(Fig f) {
        if (f == null) {
            throw new IllegalArgumentException("Attempted to find the index of a null fig in a LayerDiagram");
        }

        return _contents.indexOf(f);
    }

    /** Remove the given Fig from this layer. */
    public void remove(Fig f) {
        _contents.remove(f);
        f.endTrans();
        f.setLayer(null);
    }

    /**
     * Test if the given Fig is in this layer.
     * @param f
     * @return
     */
    public boolean contains(Fig f) {
        return _contents.contains(f);
    }

    /** Reply the contents of this layer. Do I really want to do this? 
     */
    public List getContents() {
        return Collections.unmodifiableList(_contents);
    }

    /** Reply the 'top' Fig under the given (mouse)
     *  coordinates. Needs-More-Work: For now, just do a linear search.
     *  Later, optimize this routine using Quad Trees (or other)
     *  techniques.  */
    public Fig hit(Rectangle r) {

        /* search backward so that highest item is found first */
        for(int i = _contents.size() - 1; i >= 0; i--) {
            Fig f = (Fig)_contents.get(i);
            if(f.hit(r)) {
                return f;
            }
        }

        return null;
    }

    /** Delete all Fig's from this layer. */
    public void removeAll() {
        for(int i = _contents.size() - 1; i >= 0; i--) {
            Fig f = (Fig)_contents.get(i);
            f.setLayer(null);
        }

        _contents.clear();
        //notify?
    }

    /** Find the FigNode that is being used to visualize the
     * given NetPort, or null if there is none in this layer. */
    public FigNode getPortFig(Object port) {
        Enumeration figs = elements();
        while(figs.hasMoreElements()) {
            Fig f = (Fig)figs.nextElement();
            if(f instanceof FigNode) {
                FigNode fn = (FigNode)f;
                Fig port_fig = fn.getPortFig(port);
                if(port_fig != null) {
                    return fn;
                }
            }
        }

        return null;
    }

    /** Find the Fig that visualized the given NetNode in
     * this layer, or null if there is none. */
    public Fig presentationFor(Object obj) {
        int figCount = _contents.size();
        for(int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig fig = (Fig)_contents.get(figIndex);
            if(fig.getOwner() == obj) {
                return fig;
            }
        }

        return null;
    }

    public int presentationCountFor(Object obj) {
        int count = 0;
        int figCount = _contents.size();
        for(int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig fig = (Fig)_contents.get(figIndex);
            if(fig.getOwner() == obj) {
                count++;
            }
        }

        return count;
    }

    ////////////////////////////////////////////////////////////////
    // painting methods

    /** Paint all the Fig's that belong to this layer. */
    public void paintContents(Graphics g) {    // kept for backwards compatibility
        paintContents(g, null);
    }

    /** Paint all the Fig's that belong to this layer using a given FigPainter.
     *  If painter is null, the Fig's are painted directly.
     */
    public void paintContents(Graphics g, FigPainter painter) {
        Rectangle clipBounds = g.getClipBounds();
        Iterator figsIter;
        synchronized(_contents) {
            figsIter = (new ArrayList(_contents)).iterator();
        }
        while(figsIter.hasNext()) {
            Fig fig = (Fig)figsIter.next();
            if(clipBounds == null || fig.intersects(clipBounds)) {
                if(painter == null) {
                    fig.paint(g);
                }
                else {
                    painter.paint(g, fig);
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////
    // ordering of Figs

    /** Reorder the given Fig in this layer. */
    public void sendToBack(Fig f) {
        _contents.remove(f);
        _contents.add(0, f);
    }

    /** Reorder the given Fig in this layer. */
    public void bringToFront(Fig f) {
        _contents.remove(f);
        _contents.add(f);
    }

    /** Reorder the given Fig in this layer. Needs-more-work:
     * Should come backward/forward until they change positions with an
     * object they overlap. Maybe... */
    public void sendBackward(Fig f) {
        int i = _contents.indexOf(f);
        if(i == -1 || i == 0) {
            return;
        }

        Object prevFig = _contents.get(i - 1);
        _contents.set(i, prevFig);
        _contents.set(i - 1, f);
    }

    /** Reorder the given Fig in this layer. */
    public void bringForward(Fig f) {
        int i = _contents.indexOf(f);
        if(i == -1 || i == _contents.size() - 1) {
            return;
        }

        Object nextFig = _contents.get(i + 1);
        _contents.set(i, nextFig);
        _contents.set(i + 1, f);
    }

    /** Reorder the given Fig in this layer. */
    public void bringInFrontOf(Fig f1, Fig f2) {
        int i1 = _contents.indexOf(f1);
        int i2 = _contents.indexOf(f2);
        if(i1 == -1) {
            return;
        }

        if(i2 == -1) {
            return;
        }

        if(i1 >= i2) {
            return;
        }

        _contents.remove(f1);
        _contents.add(i2, f1);
        //     Object frontFig = _contents.elementAt(i1);
        //     Object backFig = _contents.elementAt(i2);
        //     _contents.setElementAt(frontFig, i2);
        //     _contents.setElementAt(backFig, i1);
    }

    /** Reorder the given Fig in this layer. */
    public void reorder(Fig f, int function) {
        switch(function) {

            case CmdReorder.SEND_TO_BACK:
                sendToBack(f);
                break;

            case CmdReorder.BRING_TO_FRONT:
                bringToFront(f);
                break;

            case CmdReorder.SEND_BACKWARD:
                sendBackward(f);
                break;

            case CmdReorder.BRING_FORWARD:
                bringForward(f);
                break;
        }
    }
    
    public void preSave() {
        validate();
        for(int i = 0; i < _contents.size(); i++) {
            Fig f = (Fig)_contents.get(i);
            f.preSave();
        }
    }

    /**
     * Scan the contents of the layer before a save takes place to
     * validate its state is legal.
     */
    private void validate() {
        for(int i = _contents.size() - 1; i >= 0; --i) {
            Fig f = (Fig)_contents.get(i);
            if (f.isRemoveStarted()) {
                //TODO: Once JRE1.4 is minimum support we should use assertions
                LOG.error("A fig has been found that should have been removed " + f.toString());
                _contents.remove(i);
            } else if (f.getLayer() != this) {
                //TODO: Once JRE1.4 is minimum support we should use assertions
                LOG.error("A fig has been found that doesn't refer back to the correct layer " + f.toString() + " - " + f.getLayer());
                f.setLayer(this);
            }
        }
    }

    public void postSave() {
        for(int i = 0; i < _contents.size(); i++) {
            ((Fig)_contents.get(i)).postSave();
        }
    }

    public void postLoad() {
        for(int i = 0; i < _contents.size(); i++) {
            ((Fig)_contents.get(i)).postLoad();
        }
    }
}