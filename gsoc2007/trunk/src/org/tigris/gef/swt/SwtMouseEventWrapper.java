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

// File: SwingMouseEventWrapper.java
// Classes: SwingMouseEventWrapper
// Original Author: johnnycoding@gmail.com
// $Id: SwingMouseEventWrapper.java 1011 2007-05-29 17:12:49Z johnnycoding $

package org.tigris.gef.swt;

import org.eclipse.swt.SWT;
import org.tigris.gef.base.MouseEvent;
import org.tigris.gef.graph.presentation.GraphInternalPane;

import java.awt.Point;

/**
 * @see java.awt.event.MouseEvent
 * @see orgtigris.gef.base.MouseEvent
 */

public class SwtMouseEventWrapper implements MouseEvent {

    private org.eclipse.swt.events.MouseEvent event;
    protected boolean consumed = false;
    private GraphInternalPane _jComponent = null;

    //set the private event to the swt MouseEvent
    public SwtMouseEventWrapper(org.eclipse.swt.events.MouseEvent me) {
        this.event = me;
    }

    /**
     * Returns which, if any, of the mouse buttons has changed state.
     * 
     * @return one of the following constants: <code>NOBUTTON</code>,
     *         <code>BUTTON1</code>, <code>BUTTON2</code> or
     *         <code>BUTTON3</code>.
     * @since 1.4
     */
    public int getButton() {
        return event.button;
    }

    /**
     * Returns the horizontal x position of the event relative to the source
     * component.
     * 
     * @return x an integer indicating horizontal position relative to the
     *         component
     */
    public int getX() {
        return event.x;
    }

    /**
     * Returns the vertical y position of the event relative to the source
     * component.
     * 
     * @return y an integer indicating vertical position relative to the
     *         component
     */
    public int getY() {
        return event.y;
    }

    /**
     * Returns whether or not this mouse event is the popup menu trigger event
     * for the platform.
     * <p>
     * <b>Note</b>: Popup menus are triggered differently on different systems.
     * Therefore, <code>isPopupTrigger</code> should be checked in both
     * <code>mousePressed</code> and <code>mouseReleased</code> for proper
     * cross-platform functionality.
     * 
     * @return boolean, true if this event is the popup menu trigger for this
     *         platform
     */
    public boolean isPopupTrigger() {
        return event.button == 3;
    }

    /**
     * Translates the event's coordinates to a new position by adding specified
     * <code>x</code> (horizontal) and <code>y</code> (vertical) offsets.
     * 
     * @param x
     *            the horizontal x value to add to the current x coordinate
     *            position
     * @param y
     *            the vertical y value to add to the current y coordinate
     *            position
     */
    public void translatePoint(int x, int y) {
        event.x += x;
        event.y += y;
    }

    /**
     * Returns the timestamp of when this event occurred.
     */
    public long getWhen() {
        return event.time;
    }

    /**
     * Consumes this event so that it will not be processed in the default
     * manner by the source which originated it.
     */
    public void consume() {
        consumed = true;
    }

    /**
     * Returns whether or not this event has been consumed.
     * 
     * @see #consume
     */
    public boolean isConsumed() {
        return consumed;
    }

    /**
     * Returns whether or not the Alt modifier is down on this event.
     */
     public boolean isAltDown() {
        return (event.stateMask & SWT.ALT) != 0;
    }

     /**
      * Returns whether or not the Control modifier is down on this event.
      */
    public boolean isControlDown() {
        return (event.stateMask & SWT.CTRL) != 0;
    }

    /**
     * Returns whether or not the Meta modifier is down on this event.
     */
    public boolean isMetaDown() {
        return (event.stateMask & SWT.COMMAND) != 0;
    }

    /**
     * Returns whether or not the Shift modifier is down on this event.
     */
    public boolean isShiftDown() {
        return (event.stateMask & SWT.SHIFT) != 0;
    }
 
    /**
     * Returns the modifier mask for this event.
     */
    public int getModifiers(){
        return event.stateMask;
    }

    /**
     * Returns id
     */
   public int getID()
    {
        return event.stateMask;
    }
 
   /**
    * Returns whether or not the AltGraph modifier is down on this event.
    */
    public boolean isAltGraphDown()
    {
        return (event.stateMask & SWT.ALT) != 0;
    }

    /**
     * Returns the x,y position of the event relative to the source component.
     * 
     * @return a <code>Point</code> object containing the x and y coordinates
     *         relative to the source component
     * 
     */
    public Point getPoint()
    {
        return new Point(event.x,event.y);
    }

    /*
     * @see org.tigris.gef.base.InputEvent#getComponent()
     */
    public GraphInternalPane getComponent() {
        // TODO Auto-generated method stub
        return _jComponent;
    }

    /*
     * @see org.tigris.gef.base.InputEvent#getComponent()
     */
   public void setComponent(GraphInternalPane gip) {
        _jComponent = gip;        
    }
}
