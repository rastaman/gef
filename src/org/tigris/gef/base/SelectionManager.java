// %1035450540230:org.tigris.gef.base%
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
// File: SelectionManager.java
// Classes: SelectionManager
// Original Author: jrobbins@ics.uci.edu
// $Id$
package org.tigris.gef.base;

import java.awt.*;
import java.awt.event.*;

import java.io.Serializable;

import java.util.*;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.tigris.gef.event.GraphSelectionEvent;
import org.tigris.gef.event.GraphSelectionListener;

import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigEdge;
import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.presentation.Handle;

import org.tigris.gef.util.VetoableChangeEventSource;

/** This class handles Manager selections. It is basically a
 *  collection of Selection instances. Most of its operations
 *  just dispatch the same operation to each of the Selection
 *  instances in turn.<p>
 *
 *  The SelectionManager is also responsible for sending out
 *  GraphSelectionEvents to any GraphSelectionListeners that are
 *  registered.
 *
 * @see Selection */
public class SelectionManager implements Serializable, KeyListener, MouseListener, MouseMotionListener {
    ////////////////////////////////////////////////////////////////
    // instance variables

    /** The collection of Selection instances */
    protected Vector _selections = new Vector();
    protected Editor _editor;
    protected EventListenerList _listeners = new EventListenerList();

    ////////////////////////////////////////////////////////////////
    // constructor
    public SelectionManager(Editor ed) {
        _editor = ed;
    }

    /**
     * @deprecated 0.10 no longer required. Removed in ver 0.11
     */
    public void startTrans() {
    }
    
    ////////////////////////////////////////////////////////////////
    // accessors

    /** Add a new selection to the collection of selections */
    protected void addSelection(Selection s) {
        _selections.addElement(s);
    }

    protected void addFig(Fig f) {
        _selections.addElement(makeSelectionFor(f));
    }

    protected void addAllFigs(List v) {
        int count = v.size();
        for(int i = 0; i < count; ++i) {
            Fig fig = (Fig)v.get(i);
            addFig(fig);
        }
    }

    protected void removeAllElements() {
        _selections.removeAllElements();
    }

    protected void removeSelection(Selection s) {
        if(s != null) {
            _selections.removeElement(s);
        }
    }

    protected void removeFig(Fig f) {
        Selection s = findSelectionFor(f);
        if(s != null) {
            _selections.removeElement(s);
        }
    }

    protected void allDamaged() {
        Rectangle bounds = this.getBounds();
        _editor.scaleRect(bounds);
        _editor.damaged(bounds);
    }

    public void select(Fig f) {
        allDamaged();
        removeAllElements();
        addFig(f);
        _editor.damageAll();
        fireSelectionChanged();
    }

    /**
     * Adds an additional fig to the current selection.
     *
     * @param fig Additional fig to select.
     */
    public void addToSelection(Fig fig) {
        addFig(fig);
        _editor.damageAll();
        fireSelectionChanged();
    }

    /** Deselect the given Fig */
    public void deselect(Fig f) {
        if(containsFig(f)) {
            removeFig(f);
            _editor.damageAll();
            fireSelectionChanged();
        }
    }

    public void toggle(Fig f) {
        _editor.damageAll();
        if(containsFig(f)) {
            removeFig(f);
        }
        else {
            addFig(f);
        }

        _editor.damageAll();
        fireSelectionChanged();
    }

    public void deselectAll() {
        Rectangle damagedArea = this.getBounds();    // too much area
        removeAllElements();
        _editor.damaged(damagedArea);
        fireSelectionChanged();
    }

    public void select(List items) {
        allDamaged();
        removeAllElements();
        addAllFigs(items);
        allDamaged();
        fireSelectionChanged();
    }

    public void toggle(Vector items) {
        allDamaged();
        Enumeration figs = ((Vector)items.clone()).elements();
        while(figs.hasMoreElements()) {
            Fig f = (Fig)figs.nextElement();
            if(containsFig(f)) {
                removeFig(f);
            }
            else {
                addFig(f);
            }
        }

        allDamaged();
        fireSelectionChanged();
    }

    public Selection findSelectionFor(Fig f) {
        Enumeration sels = ((Vector)_selections.clone()).elements();
        while(sels.hasMoreElements()) {
            Selection sel = (Selection)sels.nextElement();
            if(sel.contains(f)) {
                return sel;
            }
        }

        return null;
    }

    public Selection findSelectionAt(int x, int y) {
        Enumeration sels = ((Vector)_selections.clone()).elements();
        while(sels.hasMoreElements()) {
            Selection sel = (Selection)sels.nextElement();
            if(sel.contains(x, y)) {
                return sel;
            }
        }

        return null;
    }

    /** Reply true if the given selection instance is part of my
     * collection */
    public boolean contains(Selection s) {
        return _selections.contains(s);
    }

    /** Reply true if the given Fig is selected by any of my
     * selection objects */
    public boolean containsFig(Fig f) {
        return findSelectionFor(f) != null;
    }

    public boolean getLocked() {
        Enumeration sels = _selections.elements();
        while(sels.hasMoreElements()) {
            if(((Selection)sels.nextElement()).getLocked()) {
                return true;
            }
        }

        return false;
    }

    /** Reply the number of selected Fig's. This assumes that
     * this collection holds only Selection instances and each of
     * those holds one Fig */
    public int size() {
        return _selections.size();
    }

    public Vector selections() {
        return _selections;
    }

    /** Reply the collection of all selected Fig's */
    public Vector getFigs() {
        Vector figs = new Vector(_selections.size());
        int selCount = _selections.size();
        for(int i = 0; i < selCount; ++i) {
            figs.addElement(((Selection)_selections.get(i)).getContent());
        }

        return figs;
    }

    /** End a transaction that damages all selected Fig's */
    public void endTrans() {
        int selSize = _selections.size();
        List affected = new ArrayList();
        for(int i = 0; i < selSize; ++i) {
            Selection s = (Selection)_selections.elementAt(i);
            addEnclosed(affected, s.getContent());
        }

        int size = affected.size();
        for(int i = 0; i < size; ++i) {
            Fig f = (Fig)affected.get(i);
            f.endTrans();
        }
    }

    /** Paint all selection objects */
    public void paint(Graphics g) {
        Enumeration sels = _selections.elements();
        while(sels.hasMoreElements()) {
            ((Selection)sels.nextElement()).paint(g);
        }
    }

    /** When the SelectionManager is damageAll, that implies that each
     * Selection should be damageAll. */
    public void damage() {
        Enumeration ss = _selections.elements();
        while(ss.hasMoreElements()) {
            ((Selection)ss.nextElement()).damage();
        }
    }

    /** Reply true iff the given point is inside one of the selected Fig's */
    public boolean contains(int x, int y) {
        Enumeration sels = _selections.elements();
        while(sels.hasMoreElements()) {
            if(((Selection)sels.nextElement()).contains(x, y)) {
                return true;
            }
        }

        return false;
    }

    /** Reply true iff the given point is inside one of the selected
     * Fig's */
    public boolean hit(Rectangle r) {
        Enumeration sels = _selections.elements();
        while(sels.hasMoreElements()) {
            if(((Selection)sels.nextElement()).hit(r)) {
                return true;
            }
        }

        return false;
    }

    public Rectangle getBounds() {
        int size = _selections.size();
        if(size == 0) {
            return new Rectangle(0, 0, 0, 0);
        }

        Rectangle r = ((Selection)_selections.elementAt(0)).getBounds();
        for(int i = 1; i < size; ++i) {
            Selection sel = (Selection)_selections.elementAt(i);
            r.add(sel.getBounds());
        }

        return r;
    }

    public Rectangle getContentBounds() {
        Rectangle r = null;
        Enumeration sels = _selections.elements();
        if(sels.hasMoreElements()) {
            r = ((Selection)sels.nextElement()).getContentBounds();
        }
        else {
            return new Rectangle(0, 0, 0, 0);
        }

        while(sels.hasMoreElements()) {
            Selection sel = (Selection)sels.nextElement();
            r.add(sel.getContentBounds());
        }

        return r;
    }

    /**
     * This method will return the upper-left coordinate point
     * of the entire selection by iterating through the figs
     *
     * @return Point - the point for that upper left corner
     *
     */
    public Point getLocation() {
        int size = _selections.size();
        if(size < 1) {
            return new Point(0, 0);
        }

        Selection sel = null;
        int lowestX = Integer.MAX_VALUE;
        int lowestY = Integer.MAX_VALUE;
        Point pt = null;
        for(int i = 0; i < size; i++) {
            sel = (Selection)_selections.elementAt(i);
            pt = sel.getLocation();
            if(pt.getX() < lowestX) {
                lowestX = (int)pt.getX();
            }

            if(pt.getY() < lowestY) {
                lowestY = (int)pt.getY();
            }
        }

        pt = null;
        sel = null;
        return new Point(lowestX, lowestY);
    }

    //   /** Align the selected Fig's relative to each other */
    //   /* needs-more-work: more of this logic should be in ActionAlign */
    //   public void align(int dir) {
    //     Editor ed = Globals.curEditor();
    //     Rectangle bbox = getContentBounds();
    //     Enumeration ss = _selections.elements();
    //     while (ss.hasMoreElements())
    //       ((Selection) ss.nextElement()).align(bbox, dir, ed);
    //   }
    //   public void align(Rectangle r, int dir, Editor ed) {
    //     Enumeration ss = _selections.elements();
    //     while(ss.hasMoreElements()) ((Selection)ss.nextElement()).align(r,dir,ed);
    //   }

    /** When Manager selections are sent to back, each of them is sent
     * to back. */
    public void reorder(int func, Layer lay) {
        Enumeration ss = _selections.elements();
        while(ss.hasMoreElements()) {
            ((Selection)ss.nextElement()).reorder(func, lay);
        }
    }

    /** When Manager selections are moved, each of them is moved */
    public void translate(int dx, int dy) {
        Vector affected = new Vector();
        Vector nonMovingEdges = new Vector();
        Vector movingEdges = new Vector();
        Vector nodes = new Vector();
        int selSize = _selections.size();
        for(int i = 0; i < selSize; ++i) {
            Selection s = (Selection)_selections.elementAt(i);
            addEnclosed(affected, s.getContent());
        }

        int size = affected.size();
        for(int i = 0; i < size; ++i) {
            Fig f = (Fig)affected.elementAt(i);
            int fx = f.getX();
            int fy = f.getY();
            dx = Math.max(-fx, dx);
            dy = Math.max(-fy, dy);
        }

        for(int i = 0; i < size; ++i) {
            Fig f = (Fig)affected.elementAt(i);
            if(!(f instanceof FigNode)) {
                f.translate(dx, dy);    // lost selection.translate() !
            }
            else {
                FigNode fn = (FigNode)f;
                nodes.addElement(fn);
                fn.superTranslate(dx, dy);
                List figEdges = fn.getFigEdges();
                int feSize = figEdges.size();
                for(int j = 0; j < feSize; j++) {
                    Object fe = figEdges.get(j);
                    if(nonMovingEdges.contains(fe) && !movingEdges.contains(fe)) {
                        movingEdges.addElement(fe);
                    }
                    else {
                        nonMovingEdges.addElement(fe);
                    }
                }
            }
        }

        int meSize = movingEdges.size();
        for(int i = 0; i < meSize; i++) {
            FigEdge fe = (FigEdge)movingEdges.elementAt(i);
            fe.translateEdge(dx, dy);
        }

        int fnSize = nodes.size();
        for(int i = 0; i < fnSize; i++) {
            FigNode fn = (FigNode)nodes.elementAt(i);
            fn.updateEdges();
        }
    }

    protected void addEnclosed(List affected, Fig f) {
        if(!affected.contains(f)) {
            affected.add(f);
            List enclosed = f.getEnclosedFigs();
            if(enclosed != null) {
                int size = enclosed.size();
                for(int i = 0; i < size; ++i) {
                    addEnclosed(affected, (Fig)enclosed.get(i));
                }
            }
        }
    }

    public void startDrag() {
        List draggingFigs = new ArrayList();
        _draggingNodes = new ArrayList();
        _draggingMovingEdges = new ArrayList();
        _draggingNonMovingEdges = new ArrayList();
        _draggingOthers = new ArrayList();
        int selectionCount = _selections.size();
        for(int selectionIndex = 0; selectionIndex < selectionCount; ++selectionIndex) {
            Selection selection = (Selection)_selections.get(selectionIndex);
            addEnclosed(draggingFigs, selection.getContent());
        }

        int figCount = draggingFigs.size();
        for(int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig fig = (Fig)draggingFigs.get(figIndex);
            if(fig instanceof FigEdge) {
                FigEdge figEdge = (FigEdge)fig;
                checkDragEdge(figEdge, draggingFigs);
            }
            else if(!(fig instanceof FigNode)) {
                _draggingOthers.add(fig);
            }
            else {
                FigNode figNode = (FigNode)fig;
                _draggingNodes.add(figNode);
                List figEdges = figNode.getFigEdges();
                int figEdgeCount = figEdges.size();
                for(int edgeIndex = 0; edgeIndex < figEdgeCount; edgeIndex++) {
                    FigEdge figEdge = (FigEdge)figEdges.get(edgeIndex);
                    checkDragEdge(figEdge, draggingFigs);
                }
            }
        }

        List topLeftList = (_draggingNodes.size() > 0 ? _draggingNodes : _draggingOthers);
        int s = topLeftList.size();
        for(int i = 0; i < s; ++i) {
            Fig fig = (Fig)topLeftList.get(i);
            if(_dragLeftMostFig == null || fig.getX() < _dragLeftMostFig.getX()) {
                _dragLeftMostFig = fig;
            }

            if(_dragTopMostFig == null || fig.getY() < _dragTopMostFig.getY()) {
                _dragTopMostFig = fig;
            }
        }
    }

    private void checkDragEdge(FigEdge figEdge, List draggingFigs) {
        Fig dest = figEdge.getDestFigNode();
        Fig source = figEdge.getSourceFigNode();
        if(draggingFigs.contains(dest) && draggingFigs.contains(source)) {
            if(!_draggingMovingEdges.contains(figEdge)) {
                _draggingMovingEdges.add(figEdge);
            }
        }
        else {
            if(!_draggingNonMovingEdges.contains(figEdge)) {
                _draggingNonMovingEdges.add(figEdge);
            }
        }
    }

    public void drag(int dx, int dy) {
        if(_dragLeftMostFig == null || _dragTopMostFig == null) {
            return;
        }

        Rectangle dirtyRegion = _dragLeftMostFig.getBounds();
        Rectangle figBounds = _dragLeftMostFig.getBounds();
        dx = Math.max(-_dragLeftMostFig.getX(), dx);
        dy = Math.max(-_dragTopMostFig.getY(), dy);
        int nodeCount = _draggingNodes.size();
        for(int i = 0; i < nodeCount; ++i) {
            FigNode figNode = (FigNode)_draggingNodes.get(i);
            figNode.getBounds(figBounds);
            dirtyRegion.add(figBounds.x, figBounds.y);
            dirtyRegion.add(figBounds.x + dx, figBounds.y + dy);
            dirtyRegion.add(figBounds.x + figBounds.width, figBounds.y + figBounds.height);
            dirtyRegion.add(figBounds.x + figBounds.width + dx, figBounds.y + figBounds.height + dy);
            figNode.superTranslate(dx, dy);
            // the next one will confuse everything if elements and annotations are selected and moved
            //figNode.translateAnnotations();
        }

        int otherCount = _draggingOthers.size();
        for(int i = 0; i < otherCount; ++i) {
            Fig fig = (Fig)_draggingOthers.get(i);
            fig.getBounds(figBounds);
            dirtyRegion.add(figBounds.x, figBounds.y);
            dirtyRegion.add(figBounds.x + dx, figBounds.y + dy);
            dirtyRegion.add(figBounds.x + figBounds.width, figBounds.y + figBounds.height);
            dirtyRegion.add(figBounds.x + figBounds.width + dx, figBounds.y + figBounds.height + dy);
            fig.translate(dx, dy);
            fig.translateAnnotations();
        }

        int movingEdgeCount = _draggingMovingEdges.size();
        for(int i = 0; i < movingEdgeCount; i++) {
            FigEdge figEdge = (FigEdge)_draggingMovingEdges.get(i);
            figEdge.getBounds(figBounds);
            dirtyRegion.add(figBounds.x, figBounds.y);
            dirtyRegion.add(figBounds.x + dx, figBounds.y + dy);
            dirtyRegion.add(figBounds.x + figBounds.width, figBounds.y + figBounds.height);
            dirtyRegion.add(figBounds.x + figBounds.width + dx, figBounds.y + figBounds.height + dy);
            figEdge.translateEdge(dx, dy);
            figEdge.translateAnnotations();
        }

        int nonMovingEdgeCount = _draggingNonMovingEdges.size();
        for(int i = 0; i < nonMovingEdgeCount; i++) {
            FigEdge figEdge = (FigEdge)_draggingNonMovingEdges.get(i);
            figEdge.getBounds(figBounds);
            dirtyRegion.add(figBounds);
            figEdge.computeRoute();
            figEdge.getBounds(figBounds);
            dirtyRegion.add(figBounds);
            figEdge.translateAnnotations();
        }

        // GEF is so stinking, absolutly rotten, ...
        // There used to be the whole diagram repainted.
        // Now here's a hack that repaints only the affected region:
        // First I assume that all selected figs are in the same layer, get the editors for this layer
        // rescale the region according to the editor scale, and repaint only the result.
        // first add some extra pixels for the selection handles and arrow heads (which are not included in getBounds())
        int extraDirt = 16;
        dirtyRegion.x -= extraDirt;
        dirtyRegion.y -= extraDirt;
        dirtyRegion.width += 2 * extraDirt;
        dirtyRegion.height += 2 * extraDirt;
        Layer layer = _dragLeftMostFig.getLayer();
        //try to get the layer of the owning fig (if there is one) in case layer is null.
        if(layer == null) {
            if(_dragLeftMostFig.getOwner() instanceof Fig) {
                layer = ((Fig)_dragLeftMostFig.getOwner()).getLayer();
            }
        }

        if(layer != null) {
            List editors = layer.getEditors();
            int editorCount = editors.size();
            Rectangle dirtyRegionScaled = new Rectangle();
            for(int editorIndex = 0; editorIndex < editorCount; ++editorIndex) {
                Editor editor = (Editor)editors.get(editorIndex);
                double editorScale = editor.getScale();
                dirtyRegionScaled.x = (int)Math.floor(dirtyRegion.x * editorScale);
                dirtyRegionScaled.y = (int)Math.floor(dirtyRegion.y * editorScale);
                dirtyRegionScaled.width = (int)Math.floor(dirtyRegion.width * editorScale) + 1;
                dirtyRegionScaled.height = (int)Math.floor(dirtyRegion.height * editorScale) + 1;
                editor.damaged(dirtyRegionScaled);
            }
        }
        else {
            System.out.println("Selection manager: layer is null");
        }
    }

    public void stopDrag() {
        _dragTopMostFig = null;
        _dragLeftMostFig = null;
        _draggingNodes = null;
        _draggingMovingEdges = null;
        _draggingNonMovingEdges = null;
        _draggingOthers = null;
    }

    // The top-left corner of the rectangle enclosing all figs that will move when translated
    // (i.e. not including any non-moving edges).
    public Point getDragLocation() {
        return new Point(_dragLeftMostFig.getX(), _dragTopMostFig.getY());
    }

    private Fig _dragTopMostFig;
    private Fig _dragLeftMostFig;
    private List _draggingNodes;
    private List _draggingMovingEdges;
    private List _draggingNonMovingEdges;
    private List _draggingOthers;

    /** If only one thing is selected, then it is possible to mouse on
     * one of its handles, but if Manager things are selected, users
     * can only drag the objects around */

    /* needs-more-work: should take on more of this responsibility */
    public void hitHandle(Rectangle r, Handle h) {
        if(size() == 1) {
            ((Selection)_selections.firstElement()).hitHandle(r, h);
        }
        else {
            h.index = -1;
        }
    }

    /** If only one thing is selected, then it is possible to mouse on
     * one of its handles, but if Manager things are selected, users
     * can only drag the objects around */
    public void dragHandle(int mx, int my, int an_x, int an_y, Handle h) {
        if(size() != 1) {
            return;
        }

        Selection sel = (Selection)_selections.firstElement();
        sel.dragHandle(mx, my, an_x, an_y, h);
    }

    public void cleanUp() {
        Enumeration sels = _selections.elements();
        while(sels.hasMoreElements()) {
            Selection sel = (Selection)sels.nextElement();
            Fig f = sel.getContent();
            f.cleanUp();
        }
    }

    /** When a multiple selection are deleted, each selection is deleted */
    public void delete() {
        Enumeration ss = ((Vector)_selections.clone()).elements();
        while(ss.hasMoreElements()) {
            ((Selection)ss.nextElement()).delete();
        }
    }

    /** When a multiple selection are deleted, each selection is deleted */
    public void dispose() {
        Enumeration ss = ((Vector)_selections.clone()).elements();
        while(ss.hasMoreElements()) {
            Selection s = (Selection)ss.nextElement();
            Fig f = s.getContent();
            Object o = f.getOwner();
            if(o instanceof VetoableChangeEventSource) {
                Vector v = (Vector)((VetoableChangeEventSource)o).getVetoableChangeListeners().clone();
                Enumeration vv = v.elements();
                vv = v.elements();
                Object firstElem = null;
                boolean firstIteration = true;
                while(vv.hasMoreElements()) {
                    Object elem = vv.nextElement();
                    if(elem instanceof Fig) {
                        if(firstIteration) {
                            firstElem = elem;
                            firstIteration = false;
                            continue;
                        }

                        ((Fig)elem).delete();
                    }
                }

                ((Fig)firstElem).dispose();
            }
        }
    }

    ////////////////////////////////////////////////////////////////
    // input events

    /** When an event is passed to a multiple selection, try to pass it
     * off to the first selection that will handle it. */
    public void keyTyped(KeyEvent ke) {
        Enumeration sels = ((Vector)_selections.clone()).elements();
        while(sels.hasMoreElements() && !ke.isConsumed()) {
            ((Selection)sels.nextElement()).keyTyped(ke);
        }
    }

    public void keyReleased(KeyEvent ke) {
        Enumeration sels = ((Vector)_selections.clone()).elements();
        while(sels.hasMoreElements() && !ke.isConsumed()) {
            ((Selection)sels.nextElement()).keyReleased(ke);
        }
    }

    public void keyPressed(KeyEvent ke) {
        Enumeration sels = ((Vector)_selections.clone()).elements();
        while(sels.hasMoreElements() && !ke.isConsumed()) {
            ((Selection)sels.nextElement()).keyPressed(ke);
        }
    }

    public void mouseMoved(MouseEvent me) {
        Enumeration sels = _selections.elements();
        while(sels.hasMoreElements() && !me.isConsumed()) {
            ((Selection)sels.nextElement()).mouseMoved(me);
        }
    }

    public void mouseDragged(MouseEvent me) {
        Enumeration sels = _selections.elements();
        while(sels.hasMoreElements() && !me.isConsumed()) {
            ((Selection)sels.nextElement()).mouseDragged(me);
        }
    }

    public void mouseClicked(MouseEvent me) {
        Enumeration sels = ((Vector)_selections.clone()).elements();
        while(sels.hasMoreElements() && !me.isConsumed()) {
            ((Selection)sels.nextElement()).mouseClicked(me);
        }
    }

    public void mousePressed(MouseEvent me) {
        Enumeration sels = ((Vector)_selections.clone()).elements();
        while(sels.hasMoreElements() && !me.isConsumed()) {
            ((Selection)sels.nextElement()).mousePressed(me);
        }
    }

    public void mouseReleased(MouseEvent me) {
        Enumeration sels = ((Vector)_selections.clone()).elements();
        while(sels.hasMoreElements() && !me.isConsumed()) {
            ((Selection)sels.nextElement()).mouseReleased(me);
        }
    }

    public void mouseExited(MouseEvent me) {
        Enumeration sels = ((Vector)_selections.clone()).elements();
        while(sels.hasMoreElements() && !me.isConsumed()) {
            ((Selection)sels.nextElement()).mouseExited(me);
        }
    }

    public void mouseEntered(MouseEvent me) {
        Enumeration sels = ((Vector)_selections.clone()).elements();
        while(sels.hasMoreElements() && !me.isConsumed()) {
            ((Selection)sels.nextElement()).mouseEntered(me);
        }
    }

    ////////////////////////////////////////////////////////////////
    // graph events
    public void addGraphSelectionListener(GraphSelectionListener listener) {
        _listeners.add(GraphSelectionListener.class, listener);
    }

    public void removeGraphSelectionListener(GraphSelectionListener listener) {
        _listeners.remove(GraphSelectionListener.class, listener);
    }

    protected void fireSelectionChanged() {
        stopDrag();    // just to be paranoid
        Object[] listeners = _listeners.getListenerList();
        GraphSelectionEvent e = null;
        for(int i = listeners.length - 2; i >= 0; i -= 2) {
            if(listeners[i] == GraphSelectionListener.class) {
                if(e == null) {
                    e = new GraphSelectionEvent(_editor, getFigs());
                }

                //needs-more-work: should copy vector, use JGraph as src?
                ((GraphSelectionListener)listeners[i + 1]).selectionChanged(e);
            }
        }

        updatePropertySheet();
    }

    ////////////////////////////////////////////////////////////////
    // property sheet methods
    public void updatePropertySheet() {
        //     if (_selections.size() != 1) Globals.propertySheetSubject(null);
        //     else {
        //       Fig f = (Fig) getFigs().elementAt(0);
        //       Globals.propertySheetSubject(f);
        //     }
    }

    /**
     * Determines and returns the first common superclass of all selected
     * items.
     */
    public Class findCommonSuperClass() {
        Iterator selectionIter = _selections.iterator();
        Map superclasses = new HashMap();
        int maxCount = 0;
        Class maxClass = null;
        while(selectionIter.hasNext()) {
            Class figClass = ((Selection)selectionIter.next()).getContent().getClass();
            int count = 0;
            if(superclasses.containsKey(figClass.getName())) {
                count = ((Integer)superclasses.get(figClass.getName())).intValue();
                superclasses.put(figClass.getName(), new Integer(++count));
            }
            else {
                count = 1;
                superclasses.put(figClass.getName(), new Integer(count));
            }

            if(count > maxCount) {
                maxCount = count;
                maxClass = figClass;
            }

            Class superClass = figClass.getSuperclass();
            while(!(superClass == null || superClass.equals(Fig.class))) {
                if(superclasses.containsKey(superClass.getName())) {
                    count = ((Integer)superclasses.get(superClass.getName())).intValue();
                    superclasses.put(superClass.getName(), new Integer(++count));
                }
                else {
                    count = 1;
                    superclasses.put(superClass.getName(), new Integer(count));
                }

                if(count > maxCount) {
                    maxCount = count;
                    maxClass = superClass;
                }

                superClass = superClass.getSuperclass();
            }
        }

        if(maxCount == _selections.size()) {
            return maxClass;
        }
        else {
            return Fig.class;
        }
    }

    /**
     * Searches for the first appearance of an object of the designated type
     * in the current selection.
     *
     * @param type Type of selection class to look for.
     * @return The first selected object being instance of the designated type.
     */
    public Object findFirstSelectionOfType(Class type) {
        Iterator selectionIter = _selections.iterator();
        while(selectionIter.hasNext()) {
            Object selectionObj = ((Selection)selectionIter.next()).getContent();
            if(selectionObj.getClass().equals(type)) {
                return selectionObj;
            }
        }

        return null;
    }

    ////////////////////////////////////////////////////////////////
    // static methods
    //protected static Hashtable _SelectionRegistry = new Hashtable();
    // needs-more-work: cache a pool of selection objects?
    public static Selection makeSelectionFor(Fig f) {
        Selection customSelection = f.makeSelection();
        if(customSelection != null) {
            return customSelection;
        }

        //if (f.isRotatable()) return new SelectionRotate(f);
        if(f.isReshapable()) {
            return new SelectionReshape(f);
        }
        else if(f.isLowerRightResizable()) {
            return new SelectionLowerRight(f);
        }
        else if(f.isResizable()) {
            return new SelectionResize(f);
        }
        else if(f.isMovable()) {
            return new SelectionMove(f);
        }
        else {
            return new SelectionNoop(f);
        }
    }
}    /* end class SelectionManager */