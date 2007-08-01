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

// File: ModeDragScroll.java
// Classes: ModeDragScroll
// Original Author: Sean Chen, schen@webex.net
package org.tigris.gef.base;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JViewport;

import org.tigris.gef.graph.presentation.GraphInternalPane;

/** A Mode that allows the user to scroll the Editor by clicking and dragging
 * with the middle mouse button.
 * 
 * @see Mode
 * @see Editor
 * @author Sean Chen, schen@webex.net
 */
public class ModeDragScroll extends FigModifyingModeImpl implements ActionListener {
    
    private static final long serialVersionUID = -3744868964626889125L;

    private Dimension _viewportExtent;

    // attributes for autoscrolling...
    private boolean autoscroll = false;
    private javax.swing.Timer autoTimer;
    private int recentX, recentY;
    private final static int AUTOSCROLL_DELAY = 200;
    private static final int SCROLL_INCREMENT = 10;

    private boolean _isScrolling = false;
    private JViewport _viewport = null;
    private Cursor _oldCursor = null;
    private GraphInternalPane _component = null;
    private Dimension componentSize = null;
    private Point viewPosition = new Point();
    private int deltaX;
    private int deltaY;
    private int lastX;
    private int lastY;

    private boolean simpleDrag = false;

    ////////////////////////////////////////////////////////////////
    // constructors and related methods

    /**
     * Construct a new ModeDragScroll with the given parent.
     * 
     * @param editor The Editor this Mode will drag
     */
    public ModeDragScroll(Editor editor) {
        super(editor);
        autoTimer = new javax.swing.Timer(AUTOSCROLL_DELAY, this);
    }

    /**
     * Construct a new ModeDragScroll instance. Its parent must be set
     * before this instance can be used.
     * 
     */
    public ModeDragScroll() {
        this(null);
    }

    /**
     * Always false since this mode can never be exited.
     */
    public boolean canExit() {
        return false;
    }

    /**
     * Instructions for the user.
     */
    public String instructions() {
        return "Drag with mouse to scroll, hold down SHIFT to speed up movement";
    }

    /**
     * Grabs component to begin scrolling.  Will turn cursor into a hand.
     * 
     * @param me 
     */
    public void mousePressed(MouseEvent me) {
        boolean isAltDown = (me.isAltDown() || me.isAltGraphDown());
        boolean isOtherDown = me.isMetaDown() || me.isControlDown(); // SHIFT speeds up movement
        boolean button1 = ((me.getModifiers() & MouseEvent.BUTTON1_MASK) != 0);
        boolean button2 = ((me.getModifiers() & MouseEvent.BUTTON2_MASK) != 0);

        // Note JDK bug: for middle mouse button isAltDown() always returns true.
        // (JDK 1.4 introduced ALT_DOWN_MASK to fix the bug.)
        boolean buttonCondition =    (button1 && isAltDown && !isOtherDown)
                                  || (button2 && !isOtherDown);

        // if only mouse button1 is pressed, activate the auto scrolling
        simpleDrag = ! buttonCondition && button1;

        if(!buttonCondition) {
            //if (LOG.isDebugEnabled()) LOG.debug("MousePressed detected but with wrong button condition for scrolling");
            return;
        }

        // get the component ...
        _component = editor.getGraphInternalPane();
        if(_component == null) {
            //if (LOG.isDebugEnabled()) LOG.debug("MousePressed detected but no component to scrolling");
            return;
        }
        
        // ... and the viewport
//        Container parent = _component.getParent();
//        if(!(parent instanceof JViewport)) {
//            //if (LOG.isDebugEnabled()) LOG.debug("MousePressed detected but no viewport to scrolling");
//            return;
//        }
        
        // ok, ready to scroll
        _isScrolling = true;
        me = editor.retranslateMouseEvent(me);
//        _viewport = (JViewport) parent;

        viewPosition = _component.getViewPosition();
        _viewportExtent = _component.getExtentSize();

        componentSize = _component.getSize();
        deltaX = 0;
        deltaY = 0;
        lastX = me.getX();
        lastY = me.getY();

        _oldCursor = _component.getCursor();
        _component.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

        me.consume();
        editor.translateMouseEvent(me);
        // stop auto timer
        if (simpleDrag) {
            autoTimer.stop();
            autoscroll = false;
            //_simpleDrag = false;
        }
        //if (LOG.isDebugEnabled()) LOG.debug("MousePressed detected scrolling started and event consumed");
    }

    /**
     * If mouse is outside the component, begins autoscrolling or speeds it up.
     * Otherwise will just scroll.
     * 
     * @param me 
     */
    public void mouseDragged(MouseEvent me) {

        if (simpleDrag) {
            // examine viewport for auto scrolling
            me = editor.retranslateMouseEvent(me);
            int mouseX = me.getX();
            int mouseY = me.getY();
            recentX = mouseX;
            recentY = mouseY;
            // scroll if mouse is  outside the component
            GraphInternalPane jComponent = editor.getGraphInternalPane();
            if(jComponent != null && jComponent.isParentViewport()) {
                boolean ok = doScroll(jComponent, mouseX,mouseY);
                if ( ok && !autoscroll) {
                    autoscroll = true;
                    autoTimer.start();
                } else if (!ok) {
                    autoscroll = false;
                    autoTimer.stop();
                }
            }

            //if (LOG.isDebugEnabled()) LOG.debug("MouseDragged detected and simple drag took place");
        } else {

            if(!_isScrolling) {
                //if (LOG.isDebugEnabled()) LOG.debug("MouseDragged detected bu not in scrolling mode");
                return;
            }

            me = editor.retranslateMouseEvent(me);
            int x = me.getX();
            int y = me.getY();
            //System.out.println("[MOdeDragScroll] x,y: " + x +"," +y);

            int factor = (me.isShiftDown() ? 4 : 1);

            deltaX = factor * (lastX - x);
            deltaY = factor * (lastY - y);

            deltaX = Math.max(-viewPosition.x, deltaX);
            deltaX = Math.min(componentSize.width - (viewPosition.x + _viewportExtent.width), deltaX);

            deltaY = Math.max(-viewPosition.y, deltaY);
            deltaY = Math.min(componentSize.height - (viewPosition.y + _viewportExtent.height), deltaY);

            viewPosition.x += deltaX;
            viewPosition.y += deltaY;
            _viewport.setViewPosition(viewPosition);

            if (deltaX != 0) {
                lastX = x + deltaX;
            }
            if (deltaY != 0) {
                lastY = y + deltaY;
            }
            me.consume();
            editor.translateMouseEvent(me);
            //if (LOG.isDebugEnabled()) LOG.debug("MouseDragged detected, viewport moved and event consumed");
        }
    }

    private final boolean doScroll(GraphInternalPane jComponent, int mouseX, int mouseY) {
        if(jComponent != null && jComponent.isParentViewport()) {
            Dimension componentSize = jComponent.getSize();
            //JViewport view = (JViewport)jComponent.getParent();
            Rectangle viewRect = jComponent.getViewRect();
            int viewRight = viewRect.x + viewRect.width;
            int viewY = viewRect.y + viewRect.height;
            // test, if the mouse moves out of the viewport
            // Then auto scrolling is activated but only within the component boundaries

            if   ( mouseX > viewRight &&  ! (viewRight > (componentSize.width - SCROLL_INCREMENT ))) {
                // mouse moves right out of the view -> scroll to right
                jComponent.setViewPosition(new Point(viewRect.x + SCROLL_INCREMENT, viewRect.y));
                return true;
            } else if ( mouseX < viewRect.x && ! (viewRect.x -  SCROLL_INCREMENT < 0)) {
                // mouse moves left out of the viewport -> scroll to left
                jComponent.setViewPosition(new Point(viewRect.x - SCROLL_INCREMENT, viewRect.y));
                return true;
            } else if (mouseY > viewY &&  ! (viewY > (componentSize.height -SCROLL_INCREMENT))) {
                jComponent.setViewPosition(new Point(viewRect.x,  viewRect.y + SCROLL_INCREMENT));
                return true;
            } else if (mouseY < viewRect.y && ! (viewRect.y -SCROLL_INCREMENT < 0)) {
                jComponent.setViewPosition(new Point(viewRect.x,  viewRect.y - SCROLL_INCREMENT));
                return true;
            }
        }
        return false;
    }

    /**
     * Stops scrolling, clears all references
     * @param me 
     */
    public void mouseReleased(MouseEvent me) {
        // stop autoscrolling
        if(autoscroll) {
            autoTimer.stop();
            autoscroll = false;
            simpleDrag = false;
        }

        if(!_isScrolling) {
            //if (LOG.isDebugEnabled()) LOG.debug("MouseReleased detected but not in scrolling mode");
            return;
        }
        _isScrolling = false;
        viewPosition = null;
        _component.setCursor(_oldCursor);
        _component = null;
        componentSize = null;
        _viewport = null;
        _oldCursor = null;
        me.consume();
        //if (LOG.isDebugEnabled()) LOG.debug("MouseReleased detected so ending scroll and event consumed");
    }

    /**
     * Interface ActionListener: Simulate mouse dragging
     * @param e
     */
     public void actionPerformed(ActionEvent e) {
	 getEditor().mouseDragged(recentX, recentY);
     }
     
}