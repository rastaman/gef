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




// File: MouseEvent.java
// Classes: MouseEvent
// Original Author: johnnycoding@gmail.com
// $Id: MouseEvent.java 1011 2007-05-29 17:12:49Z johnnycoding $

package org.tigris.gef.base;

import java.awt.Point;

public interface MouseEvent extends InputEvent {
    /**
     * The first number in the range of ids used for mouse events.
     */
    public static final int MOUSE_FIRST = 500;

    /**
     * The last number in the range of ids used for mouse events.
     */
    public static final int MOUSE_LAST = 507;

    /**
     * The "mouse clicked" event. This <code>MouseEvent</code> occurs when a
     * mouse button is pressed and released.
     */
    public static final int MOUSE_CLICKED = MOUSE_FIRST;

    /**
     * The "mouse pressed" event. This <code>MouseEvent</code> occurs when a
     * mouse button is pushed down.
     */
    public static final int MOUSE_PRESSED = 1 + MOUSE_FIRST; //Event.MOUSE_DOWN

    /**
     * The "mouse released" event. This <code>MouseEvent</code> occurs when a
     * mouse button is let up.
     */
    public static final int MOUSE_RELEASED = 2 + MOUSE_FIRST; //Event.MOUSE_UP

    /**
     * The "mouse moved" event. This <code>MouseEvent</code> occurs when the
     * mouse position changes.
     */
    public static final int MOUSE_MOVED = 3 + MOUSE_FIRST; //Event.MOUSE_MOVE

    /**
     * The "mouse entered" event. This <code>MouseEvent</code> occurs when the
     * mouse cursor enters the unobscured part of component's geometry.
     */
    public static final int MOUSE_ENTERED = 4 + MOUSE_FIRST; //Event.MOUSE_ENTER

    /**
     * The "mouse exited" event. This <code>MouseEvent</code> occurs when the
     * mouse cursor exits the unobscured part of component's geometry.
     */
    public static final int MOUSE_EXITED = 5 + MOUSE_FIRST; //Event.MOUSE_EXIT

    /**
     * The "mouse dragged" event. This <code>MouseEvent</code> occurs when the
     * mouse position changes while a mouse button is pressed.
     */
    public static final int MOUSE_DRAGGED = 6 + MOUSE_FIRST; //Event.MOUSE_DRAG

    /**
     * The "mouse wheel" event. This is the only <code>MouseWheelEvent</code>.
     * It occurs when a mouse equipped with a wheel has its wheel rotated.
     * 
     * @since 1.4
     */
    public static final int MOUSE_WHEEL = 7 + MOUSE_FIRST;

    /**
     * Indicates no mouse buttons; used by {@link #getButton}.
     * 
     * @since 1.4
     */
    public static final int NOBUTTON = 0;

    /**
     * Indicates mouse button #1; used by {@link #getButton}.
     * 
     * @since 1.4
     */
    public static final int BUTTON1 = 1;

    /**
     * Indicates mouse button #2; used by {@link #getButton}.
     * 
     * @since 1.4
     */
    public static final int BUTTON2 = 2;

    /**
     * Indicates mouse button #3; used by {@link #getButton}.
     * 
     * @since 1.4
     */
    public static final int BUTTON3 = 3;

    /**
     * Returns the horizontal x position of the event relative to the source
     * component.
     * 
     * @return x an integer indicating horizontal position relative to the
     *         component
     */
    public int getX();

    /**
     * Returns the vertical y position of the event relative to the source
     * component.
     * 
     * @return y an integer indicating vertical position relative to the
     *         component
     */
    public int getY();

    /**
     * Returns the x,y position of the event relative to the source component.
     * 
     * @return a <code>Point</code> object containing the x and y coordinates
     *         relative to the source component
     * 
     */
    public Point getPoint();

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
    public void translatePoint(int x, int y);

    /**
     * Returns which, if any, of the mouse buttons has changed state.
     * 
     * @return one of the following constants: <code>NOBUTTON</code>,
     *         <code>BUTTON1</code>, <code>BUTTON2</code> or
     *         <code>BUTTON3</code>.
     * @since 1.4
     */
    public int getButton();

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
    public boolean isPopupTrigger();

}