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
// File: ModeModify.java
// Classes: ModeModify
// Original Author: ics125 spring 1996
// $Id$
package org.tigris.gef.base;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.*;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.graph.MutableGraphSupport;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigEdge;
import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.presentation.Handle;

/** A Mode to process events from the Editor when the user is
 *  modifying a Fig.  Right now users can drag one or more
 *  Figs around the drawing area, or they can move a handle
 *  on a single Fig.
 *
 * @see Fig
 * @see Selection
 */
public class ModeModify extends FigModifyingModeImpl {
    ////////////////////////////////////////////////////////////////
    // constants

    /** Minimum amount that the user must move the mouse to indicate that she
     *  really wants to modify something. */
    private static final int MIN_DELTA = 4;
    private double degrees45 = Math.PI / 4;

    private static final int SCROLL_INCREMENT = 10;

    ////////////////////////////////////////////////////////////////
    // instance variables

    /** drag in process */
    private boolean _dragInProcess = false;

    /** The current position of the mouse during a drag operation. */
    private Point newMousePosition = new Point(0, 0);
    
    /** The point at which the mouse started a drag operation. */
    private Point dragStartMousePosition = new Point(0, 0);
    
    /** The location of the selection when the drag was started. */
    private Point dragStartSelectionPosition = null;

    /** The index of the handle that the user is dragging */
    private Handle _curHandle = new Handle(-1);
    private Rectangle _highlightTrap = null;
    private int _deltaMouseX;
    private int _deltaMouseY;
    
    private static Log LOG = LogFactory.getLog(ModeModify.class);

    /** Construct a new ModeModify with the given parent, and set the
     *  Anchor point to a default location (the _anchor's proper position
     *  will be determioned on mouse down). */
    public ModeModify(Editor par) {
        super(par);
    }

    ////////////////////////////////////////////////////////////////
    // user feedback

    /** Reply a string of instructions that should be shown in the
     *  statusbar when this mode starts. */
    public String instructions() {
        return "Modify selected objects";
    }

    ////////////////////////////////////////////////////////////////
    // event handlers

    /** When the user drags the mouse two things can happen:
     *  (1) if the user is dragging the body of one or more Figs then
     *  they are all moved around the drawing area, or
     *  (2) if the user started dragging on a handle of one Fig then the user
     *  can drag the handle around the drawing area and the Fig reacts to that.
     */
    public void mouseDragged(MouseEvent mouseEvent) {
        if (mouseEvent.isConsumed()) {
            if (LOG.isDebugEnabled()) LOG.debug("MouseDragged detected but with wrong button condition for scrolling");
            return;
        }

        mouseEvent.consume();
        int mouseX = mouseEvent.getX();
        int mouseY = mouseEvent.getY();
        newMousePosition.x = mouseX;
        newMousePosition.y = mouseY;
        _deltaMouseX = mouseX - dragStartMousePosition.x;
        _deltaMouseY = mouseY - dragStartMousePosition.y;
        if(!_dragInProcess && Math.abs(_deltaMouseX) < MIN_DELTA && Math.abs(_deltaMouseY) < MIN_DELTA) {
            if (LOG.isDebugEnabled()) LOG.debug("MouseDragged detected but not enough to notice");
            return;
        }

        if (!_dragInProcess) {
            _dragInProcess = true;
            GraphModel gm = editor.getGraphModel();
            if (gm instanceof MutableGraphSupport) {
                ((MutableGraphSupport)gm).fireGraphChanged();
            }
        }

        boolean restrict45 = mouseEvent.isControlDown();
        handleMouseDragged(restrict45);
        if (LOG.isDebugEnabled()) LOG.debug("MouseDragged detected");
    }

    /**
     * Check if a drag operation is in progress and if the key event changes the restriction of horizontal/vertical
     * movement. If so, update the selection's position.
     * @param keyEvent
     */
    private void updateMouseDrag(KeyEvent keyEvent) {
        if(_dragInProcess) {
            boolean restrict45 = keyEvent.isControlDown();
            handleMouseDragged(restrict45);
        }
    }

    public void keyPressed(KeyEvent keyEvent) {
        super.keyPressed(keyEvent);
        updateMouseDrag(keyEvent);
    }

    public void keyReleased(KeyEvent keyEvent) {
        super.keyReleased(keyEvent);
        updateMouseDrag(keyEvent);
    }

    /**
     * Like handleMouseDragged(MouseEvent) but takes only delta mouse position as arguments. Is also called when
     * control is pressed or released during the drag.
     */
    private void handleMouseDragged(boolean restrict45) {
        if (LOG.isDebugEnabled()) LOG.debug("Original position was " + dragStartMousePosition);
        int deltaMouseX = _deltaMouseX;
        int deltaMouseY = _deltaMouseY;
        if(restrict45 && deltaMouseY != 0) {
            double degrees = Math.atan2(deltaMouseY, deltaMouseX);
            degrees = degrees45 * Math.round(degrees / degrees45);
            double r = Math.sqrt(deltaMouseX * deltaMouseX + deltaMouseY * deltaMouseY);
            deltaMouseX = (int)(r * Math.cos(degrees));
            deltaMouseY = (int)(r * Math.sin(degrees));
        }

        if (LOG.isDebugEnabled()) LOG.debug("deltaMouseX = " + deltaMouseX);
        SelectionManager selectionManager = getEditor().getSelectionManager();
        if(selectionManager.getLocked()) {
            Globals.showStatus("Cannot Modify Locked Objects");
            return;
        }

        if(dragStartSelectionPosition == null) {
            selectionManager.startDrag();
        }

        Point selectionCurrentPosition = null;
        if(selectionManager.size() == 1 && ((selectionManager.getFigs().get(0) instanceof FigEdge) || _curHandle.index > 0)) {
            selectionCurrentPosition = new Point(dragStartMousePosition);
        }
        else {
            selectionCurrentPosition = selectionManager.getDragLocation();
        }

        if(dragStartSelectionPosition == null) {
            dragStartSelectionPosition = selectionCurrentPosition;
        }

        Point selectionNewPosition = new Point(dragStartSelectionPosition);
        selectionNewPosition.translate(deltaMouseX, deltaMouseY);
        if (LOG.isDebugEnabled()) LOG.debug("selectionNewPosition = " + selectionNewPosition);
        getEditor().snap(selectionNewPosition);
        selectionNewPosition.x = Math.max(0, selectionNewPosition.x);
        selectionNewPosition.y = Math.max(0, selectionNewPosition.y);

        int deltaSelectionX = selectionNewPosition.x - selectionCurrentPosition.x;
        int deltaSelectionY = selectionNewPosition.y - selectionCurrentPosition.y;
        if(deltaSelectionX != 0 || deltaSelectionY != 0) {
            if (LOG.isDebugEnabled()) LOG.debug("handle index = " + _curHandle.index);
            if(_curHandle.index == -1) {
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                if(legal(deltaSelectionX, deltaSelectionY, selectionManager)) {
                    selectionManager.drag(deltaSelectionX, deltaSelectionY);
                }
            }
            else {
                if(_curHandle.index >= 0) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    if (LOG.isDebugEnabled()) LOG.debug("selectionNewPosition = " + selectionNewPosition);
                    selectionManager.dragHandle(newMousePosition.x, newMousePosition.y, dragStartMousePosition.x, dragStartMousePosition.y, _curHandle);
                    selectionManager.endTrans();
                }
            }
            // Note: if _curHandle.index == -2 then do nothing
        }
    }

    /** When the user presses the mouse button on a Fig, this Mode
     *  starts preparing for future drag events by finding if a handle
     *  was clicked on.  This event is passed from ModeSelect. */
    public void mousePressed(MouseEvent me) {
        if (me.isConsumed()) {
            if (LOG.isDebugEnabled()) LOG.debug("MousePressed detected but already consumed");
            return;
        }

        int x = me.getX();
        int y = me.getY();
        start();
        SelectionManager selectionManager = getEditor().getSelectionManager();
        if(selectionManager.size() == 0) {
            done();
        }

        if(selectionManager.getLocked()) {
            Globals.showStatus("Cannot Modify Locked Objects");
            me.consume();
            return;
        }

        dragStartMousePosition = me.getPoint();
        if (LOG.isDebugEnabled()) LOG.debug("MousePressed at " + dragStartMousePosition);
        dragStartSelectionPosition = null;
        selectionManager.hitHandle(new Rectangle(x - 4, y - 4, 8, 8), _curHandle);
        Globals.showStatus(_curHandle.instructions);
        selectionManager.endTrans();
    }

    /** On mouse up the modification interaction is done. */
    public void mouseReleased(MouseEvent me) {
        _dragInProcess = false;
        if(me.isConsumed()) {
            return;
        }

        done();
        me.consume();
        SelectionManager sm = editor.getSelectionManager();
        sm.stopDrag();
        List figs = sm.getFigs();
        int figCount = figs.size();
        for(int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig selectedFig = (Fig)figs.get(figIndex);
            if((selectedFig instanceof FigNode)) {
                Rectangle bbox = selectedFig.getBounds();
                Layer lay = selectedFig.getLayer();
                Collection otherFigs = lay.getContents(null);
                Fig encloser = null;
                Iterator it = otherFigs.iterator();
                while(it.hasNext()) {
                    Fig otherFig = (Fig)it.next();
                    if(!(otherFig instanceof FigNode)) {
                        continue;
                    }

                    if(!(otherFig.getUseTrapRect())) {
                        continue;
                    }

                    //if (figs.contains(otherFig)) continue;
                    Rectangle trap = otherFig.getTrapRect();
                    if(trap == null) {
                        continue;
                    }

                    // now bbox is where the fig _will_ be
                    if((trap.contains(bbox.x, bbox.y) && trap.contains(bbox.x + bbox.width, bbox.y + bbox.height))) {
                        encloser = otherFig;
                    }
                }

                selectedFig.setEnclosingFig(encloser);
                
            } else if(selectedFig instanceof FigEdge) {
                ((FigEdge)selectedFig).computeRoute();
                selectedFig.endTrans();
            }

            selectedFig.endTrans();
        }
    }

    public void done() {
        super.done();
        SelectionManager sm = getEditor().getSelectionManager();
        sm.cleanUp();
        if(_highlightTrap != null) {
            editor.damaged(_highlightTrap);
            _highlightTrap = null;
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        if(_highlightTrap != null) {
            Color selectRectColor = Globals.getPrefs().getRubberbandColor();
            g.setColor(selectRectColor);
            g.drawRect(_highlightTrap.x - 1, _highlightTrap.y - 1, _highlightTrap.width + 1, _highlightTrap.height + 1);
            g.drawRect(_highlightTrap.x - 2, _highlightTrap.y - 2, _highlightTrap.width + 3, _highlightTrap.height + 3);
        }
    }

    private void damageHighlightTrap() {
        if(_highlightTrap == null) {
            return;
        }
        Rectangle r = new Rectangle(_highlightTrap);
        r.x -= 2;
        r.y -= 2;
        r.width += 4;
        r.height += 4;
        editor.damaged(r);
    }

    private boolean legal(int dx, int dy, SelectionManager selectionManager) {
        damageHighlightTrap();

        _highlightTrap = null;
        List figs = selectionManager.getFigs();
        int figCount = figs.size();
        Rectangle figBounds = new Rectangle();
        boolean draggedOntoCanvas = true;
        Fig encloser = null;
        Fig selectedFig = null;
        for(int figIndex = 0; figIndex < figCount; ++figIndex) {
            selectedFig = (Fig)figs.get(figIndex);
            boolean selectedUseTrap = selectedFig.getUseTrapRect();
            if(!(selectedFig instanceof FigNode)) {
                continue;
            }

            selectedFig.getBounds(figBounds);
            figBounds.x += dx;
            figBounds.y += dy;
            Layer lay = selectedFig.getLayer();
            Collection otherFigs = lay.getContents(null);
            Iterator it = otherFigs.iterator();
            while(it.hasNext()) {
                Fig otherFig = (Fig)it.next();
                if(!(otherFig instanceof FigNode)) {
                    continue;
                }

                if(!selectedUseTrap && !otherFig.getUseTrapRect()) {
                    continue;
                }

                if(figs.contains(otherFig)) {
                    continue;
                }

                if(otherFig.getEnclosingFig() == selectedFig) {
                    continue;
                }

                if(!otherFig.isVisible()) {
                    continue;
                }
                
                Rectangle trap = otherFig.getTrapRect();
                if(trap == null) {
                    continue;
                }

                if(!trap.intersects(figBounds)) {
                    continue;
                }

                if((trap.contains(figBounds.x, figBounds.y) && trap.contains(figBounds.x + figBounds.width, figBounds.y + figBounds.height))) {
                    draggedOntoCanvas = false;
                    encloser = otherFig;
                    continue;
                }

                if((figBounds.contains(trap.x, trap.y) && figBounds.contains(trap.x + trap.width, trap.y + trap.height))) {
                    continue;
                }

                _highlightTrap = trap;
                damageHighlightTrap();
                return false;
            }
        }
        
        if (!(selectedFig instanceof FigNode)) {
            return true;
        }
        
        GraphModel gm = editor.getGraphModel();
        if (draggedOntoCanvas) {
            //If it isn't dragged into any fig but into diagram canvas (null encloser).
            return (((MutableGraphSupport)gm)
                    .isEnclosable(((FigNode) selectedFig).getOwner(),
                                    null));
        } else {
            //If it is dragged into any fig.
            return (((MutableGraphSupport)gm)
                    .isEnclosable(((FigNode) selectedFig).getOwner(),
                                    ((FigNode) encloser).getOwner()));
        }
    }
}