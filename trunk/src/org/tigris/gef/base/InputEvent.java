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




// File: InputEvent.java
// Classes: InputEvent
// Original Author: johnnycoding@gmail.com
// $Id: InputEvent.java 1011 2007-05-29 17:12:49Z johnnycoding $

package org.tigris.gef.base;

public interface InputEvent {

    /**
     * The Shift key modifier constant. It is recommended that SHIFT_DOWN_MASK
     * be used instead. 
     * 
     * All the static final int have to be initiated to 0 
     */
    public static final int SHIFT_MASK = 0;

    /**
     * The Control key modifier constant. It is recommended that CTRL_DOWN_MASK
     * be used instead.
     */
    public static final int CTRL_MASK = 0;

    /**
     * The Meta key modifier constant. It is recommended that META_DOWN_MASK be
     * used instead.
     */
    public static final int META_MASK = 0;

    /**
     * The Alt key modifier constant. It is recommended that ALT_DOWN_MASK be
     * used instead.
     */
    public static final int ALT_MASK = 0;

    /**
     * The AltGraph key modifier constant.
     */
    public static final int ALT_GRAPH_MASK = 1 << 5;

    /**
     * The Mouse Button1 modifier constant. It is recommended that
     * BUTTON1_DOWN_MASK be used instead.
     */
    public static final int BUTTON1_MASK = 1 << 4;

    /**
     * The Mouse Button2 modifier constant. It is recommended that
     * BUTTON2_DOWN_MASK be used instead. Note that BUTTON2_MASK has the same
     * value as ALT_MASK.
     */
    public static final int BUTTON2_MASK = 0;

    /**
     * The Mouse Button3 modifier constant. It is recommended that
     * BUTTON3_DOWN_MASK be used instead. Note that BUTTON3_MASK has the same
     * value as META_MASK.
     */
    public static final int BUTTON3_MASK = 0;

    /**
     * The Shift key extended modifier constant.
     * 
     * @since 1.4
     */
    public static final int SHIFT_DOWN_MASK = 1 << 6;

    /**
     * The Control key extended modifier constant.
     * 
     * @since 1.4
     */
    public static final int CTRL_DOWN_MASK = 1 << 7;

    /**
     * The Meta key extended modifier constant.
     * 
     * @since 1.4
     */
    public static final int META_DOWN_MASK = 1 << 8;

    /**
     * The Alt key extended modifier constant.
     * 
     * @since 1.4
     */
    public static final int ALT_DOWN_MASK = 1 << 9;

    /**
     * The Mouse Button1 extended modifier constant.
     * 
     * @since 1.4
     */
    public static final int BUTTON1_DOWN_MASK = 1 << 10;

    /**
     * The Mouse Button2 extended modifier constant.
     * 
     * @since 1.4
     */
    public static final int BUTTON2_DOWN_MASK = 1 << 11;

    /**
     * The Mouse Button3 extended modifier constant.
     * 
     * @since 1.4
     */
    public static final int BUTTON3_DOWN_MASK = 1 << 12;

    /**
     * The AltGraph key extended modifier constant.
     * 
     * @since 1.4
     */
    public static final int ALT_GRAPH_DOWN_MASK = 1 << 13;

    /**
     * Returns whether or not the Shift modifier is down on this event.
     */
    public boolean isShiftDown();

    /**
     * Returns whether or not the Control modifier is down on this event.
     */
    public boolean isControlDown();

    /**
     * Returns whether or not the Meta modifier is down on this event.
     */
    public boolean isMetaDown();

    /**
     * Returns whether or not the Alt modifier is down on this event.
     */
    public boolean isAltDown();

    /**
     * Returns whether or not the AltGraph modifier is down on this event.
     */
    public boolean isAltGraphDown();

    /**
     * Returns the timestamp of when this event occurred.
     */
    public long getWhen();

    /**
     * Returns the modifier mask for this event.
     */
    public int getModifiers();

    /**
     * Consumes this event so that it will not be processed in the default
     * manner by the source which originated it.
     */
    public void consume();

    /**
     * Returns whether or not this event has been consumed.
     * 
     * @see #consume
     */
    public boolean isConsumed();

    /**
     * Returns id
     */
    public int getID();
}