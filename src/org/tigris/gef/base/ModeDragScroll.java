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

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ActionListener;

/** A Mode that allows the user to scroll the Editor by clicking and dragging
 * with the middle mouse button.
 * 
 * @see Mode
 * @see Editor
 * @author Sean Chen, schen@webex.net
 */
public class ModeDragScroll extends FigModifyingModeImpl implements ActionListener {
    private Dimension _viewportExtent;

    // attributes for autoscrolling...
    private boolean autoscroll = false;
    private javax.swing.Timer autoTimer;
    private int recentX, recentY;
    private final static int AUTOSCROLL_DELAY = 200;
    private static final int SCROLL_INCREMENT = 10;


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

    private boolean _isScrolling = false;
    private JViewport _viewport = null;
    private Cursor _oldCursor = null;
    private JComponent _component = null;
    private Dimension _componentSize = null;
    private Point _viewPosition = new Point();
    private int _deltaX;
    private int _deltaY;
    private int _lastX;
    private int _lastY;

    private boolean _simpleDrag = false;

    /**
     * Grabs component to begin scrolling.  Will turn cursor into a hand.
     * 
     * @param me 
     */
    public void mousePressed(MouseEvent me) {
        boolean isAltDown = (me.isAltDown() || me.isAltGraphDown());
        boolean isOtherDown = me.isMetaDown() || me.isControlDown(); // SHIFT speeds up movement
        boolean button1 = ((me.getModifiers() & me.BUTTON1_MASK) != 0);
        boolean button2 = ((me.getModifiers() & me.BUTTON2_MASK) != 0);

        // Note JDK bug: for middle mouse button isAltDown() always returns true.
        // (JDK 1.4 introduced ALT_DOWN_MASK to fix the bug.)
        boolean buttonCondition =    (button1 && isAltDown && !isOtherDown)
                                  || (button2 && !isOtherDown);

        // if only mouse button1 is pressed, activate the auto scrolling
        _simpleDrag = ! buttonCondition && button1;

        if(!buttonCondition)
            return;

        // get the component ...
        _component = editor.getJComponent();
        if(_component == null)
            return;
        
        // ... and the viewport
        Container parent = _component.getParent();
        if(!(parent instanceof JViewport))
            return;
        
        // ok, ready to scroll
        _isScrolling = true;
        me = editor.retranslateMouseEvent(me);
        _viewport = (JViewport) parent;

        _viewPosition = _viewport.getViewPosition();
        _viewportExtent = _viewport.getExtentSize();

        _componentSize = _component.getSize();
        _deltaX = 0;
        _deltaY = 0;
        _lastX = me.getX();
        _lastY = me.getY();

        _oldCursor = _component.getCursor();
        _component.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

        me.consume();
        editor.translateMouseEvent(me);
        // stop auto timer
        if (_simpleDrag) {
            autoTimer.stop();
            autoscroll = false;
            //_simpleDrag = false;
        }
    }

    /**
     * If mouse is outside the component, begins autoscrolling or speeds it up.
     * Otherwise will just scroll.
     * 
     * @param me 
     */
    public void mouseDragged(MouseEvent me) {

        if (_simpleDrag) {
            // examine viewport for auto scrolling
            me = editor.retranslateMouseEvent(me);
            int mouseX = me.getX();
            int mouseY = me.getY();
            recentX = mouseX;
            recentY = mouseY;
            // scroll if mouse is  outside the component
            JComponent jComponent = editor.getJComponent();
            if(jComponent != null && jComponent.getParent() instanceof JViewport) {
                boolean ok = doScroll(jComponent, mouseX,mouseY);
                if ( ok && !autoscroll) {
                    autoscroll = true;
                    autoTimer.start();
                } else if (!ok) {
                    autoscroll = false;
                    autoTimer.stop();
                }
            }

        } else {

            if(!_isScrolling)
                return;

            me = editor.retranslateMouseEvent(me);
            int x = me.getX();
            int y = me.getY();
            //System.out.println("[MOdeDragScroll] x,y: " + x +"," +y);

            int factor = (me.isShiftDown() ? 4 : 1);

            _deltaX = factor * (_lastX - x);
            _deltaY = factor * (_lastY - y);

           _deltaX = Math.max(-_viewPosition.x, _deltaX);
           _deltaX = Math.min(_componentSize.width - (_viewPosition.x + _viewportExtent.width), _deltaX);

            _deltaY = Math.max(-_viewPosition.y, _deltaY);
            _deltaY = Math.min(_componentSize.height - (_viewPosition.y + _viewportExtent.height), _deltaY);

            _viewPosition.x += _deltaX;
            _viewPosition.y += _deltaY;
            _viewport.setViewPosition(_viewPosition);

            if(_deltaX != 0)
                _lastX = x + _deltaX;
            if(_deltaY != 0)
                _lastY = y + _deltaY;
            me.consume();
            editor.translateMouseEvent(me);
        }
    }

    private final boolean doScroll(JComponent jComponent, int mouseX, int mouseY) {
        if(jComponent != null && jComponent.getParent() instanceof JViewport) {
            Dimension componentSize = jComponent.getSize();
            JViewport view = (JViewport)jComponent.getParent();
            Rectangle viewRect = view.getViewRect();
            int viewRight = viewRect.x + viewRect.width;
            int viewY = viewRect.y + viewRect.height;
            // test, if the mouse moves out of the viewport
            // Then auto scrolling is activated but only within the component boundaries

            if   ( mouseX > viewRight &&  ! (viewRight > (componentSize.width - SCROLL_INCREMENT ))) {
                // mouse moves right out of the view -> scroll to right

                view.setViewPosition(new Point(viewRect.x + SCROLL_INCREMENT, viewRect.y));
                return true;

            } else if ( mouseX < viewRect.x && ! (viewRect.x -  SCROLL_INCREMENT < 0)) {
                // mouse moves left out of the viewport -> scroll to left
                view.setViewPosition(new Point(viewRect.x - SCROLL_INCREMENT, viewRect.y));
                return true;

            } else if (mouseY > viewY &&  ! (viewY > (componentSize.height -SCROLL_INCREMENT))) {
                view.setViewPosition(new Point(viewRect.x,  viewRect.y + SCROLL_INCREMENT));
                return true;
            } else if (mouseY < viewRect.y && ! (viewRect.y -SCROLL_INCREMENT < 0)) {
                view.setViewPosition(new Point(viewRect.x,  viewRect.y - SCROLL_INCREMENT));
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
            _simpleDrag = false;
        }

        if(!_isScrolling)
            return;
        _isScrolling = false;
        _viewPosition = null;
        _component.setCursor(_oldCursor);
        _component = null;
        _componentSize = null;
        _viewport = null;
        _oldCursor = null;
        me.consume();
    }

    /**
     * Interface ActionListener: Simulate mouse dragging
     * @param e
     */
     public void actionPerformed(ActionEvent e) {
         MouseEvent me = new MouseEvent(getEditor().getJComponent(), Event.MOUSE_DRAG, 0, InputEvent.BUTTON1_MASK, recentX, recentY, 0, false);
         getEditor().mouseDragged(me);
     }
}