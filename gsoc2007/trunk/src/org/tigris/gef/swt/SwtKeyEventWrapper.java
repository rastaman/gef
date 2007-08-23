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

// File: SwingKeyEventWrapper.java
// Classes: SwingKeyEventWrapper
// Original Author: johnnycoding@gmail.com
// $Id: SwingKeyEventWrapper.java 1011 2007-05-29 17:12:49Z johnnycoding $

package org.tigris.gef.swt;

import org.eclipse.swt.SWT;
import org.tigris.gef.base.KeyEvent;
import org.tigris.gef.graph.presentation.GraphInternalPane;

/**
 * @see java.awt.event.KeyEvent
 * @see orgtigris.gef.base.KeyEvent
 */
public class SwtKeyEventWrapper implements KeyEvent {

    private org.eclipse.swt.events.KeyEvent event;
    protected boolean consumed = false;
    private GraphInternalPane _jComponent = null;

    // set the private event to the awt KeyEvent
    public SwtKeyEventWrapper(org.eclipse.swt.events.KeyEvent me) {
        this.event = me;
    }

    /*
     * @see org.tigris.gef.base.KeyEvent#getKeyChar()
     */
    public char getKeyChar() {

        return event.character;
    }

    /*
     * @see org.tigris.gef.base.KeyEvent#getKeyCode()
     */
    public int getKeyCode() {
        return event.keyCode;
    }

 
    /*
     * @see org.tigris.gef.base.KeyEvent#setKeyChar(char)
     */
    public void setKeyChar(char keyChar) {
        event.character=keyChar;
    }

    /*
     * @see org.tigris.gef.base.KeyEvent#setKeyCode(int)
     */
    public void setKeyCode(int keyCode) {
        event.keyCode=keyCode;
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
        return 0;
    }
 
   /**
    * Returns whether or not the AltGraph modifier is down on this event.
    */
    public boolean isAltGraphDown()
    {
        return (event.stateMask & SWT.ALT) != 0;
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
