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

package org.tigris.gef.swing;

import org.tigris.gef.base.KeyEvent;
import org.tigris.gef.graph.presentation.GraphInternalPane;

/**
 * A class which implements org.tigris.gef.base.KeyEvent interface and wraps
 * the functions of java.awt.event.keyEvent
 * <p>
 * 
 * It defines a private java.awt.event.KeyEvent variable and set 
 * it to the passed-in java.awt.event.KeyEvent parameter in the constructor 
 * method.   
 * <p>
 * 
 * Then for each implemented function, it calls the corresponding function in 
 * the private java.awt.event.KeyEvent variable.
 * 
 * @see java.awt.event.KeyEvent
 * @see orgtigris.gef.base.KeyEvent
 */
public class SwingKeyEventWrapper implements KeyEvent {

    private java.awt.event.KeyEvent event;
    private GraphInternalPane _jComponent = null;

    // set the private event to the awt KeyEvent
    public SwingKeyEventWrapper(java.awt.event.KeyEvent me) {
        this.event = me;
    }

    /*
     * @see org.tigris.gef.base.KeyEvent#getKeyChar()
     */
    public char getKeyChar() {

        return event.getKeyChar();
    }

    /*
     * @see org.tigris.gef.base.KeyEvent#getKeyCode()
     */
    public int getKeyCode() {
        return event.getKeyCode();
    }

    /*
     * @see org.tigris.gef.base.KeyEvent#getKeyLocation()
     */
    public int getKeyLocation() {
        return event.getKeyLocation();
    }

    /*
     * @see org.tigris.gef.base.KeyEvent#isActionKey()
     */
    public boolean isActionKey() {
        return event.isActionKey();
    }

    /*
     * @see org.tigris.gef.base.KeyEvent#paramString()
     */
    public String paramString() {
        return event.paramString();
    }

    /*
     * @see org.tigris.gef.base.KeyEvent#setKeyChar(char)
     */
    public void setKeyChar(char keyChar) {
        event.setKeyChar(keyChar);
    }

    /*
     * @see org.tigris.gef.base.KeyEvent#setKeyCode(int)
     */
    public void setKeyCode(int keyCode) {
        event.setKeyCode(keyCode);
    }

    /*
     * @see org.tigris.gef.base.KeyEvent#setModifiers(int)
     */
    public void setModifiers(int modifiers) {
        event.setModifiers(modifiers);
    }

    /*
     * @see org.tigris.gef.base.InputEvent#consume()
     */
    public void consume() {
        event.consume();
    }

    /*
     * @see org.tigris.gef.base.InputEvent#getID()
     */
    public int getID() {
        return event.getID();
    }

    /*
     * @see org.tigris.gef.base.InputEvent#getModifiers()
     */
    public int getModifiers() {
        return event.getModifiers();
    }

    /*
     * @see org.tigris.gef.base.InputEvent#getModifiersEx()
     */
    public int getModifiersEx() {
        return event.getModifiersEx();
    }

    /*
     * @see org.tigris.gef.base.InputEvent#getWhen()
     */
    public long getWhen() {
        return event.getWhen();
    }

    /*
     * @see org.tigris.gef.base.InputEvent#isAltDown()
     */
    public boolean isAltDown() {
        return event.isAltDown();
    }

    /*
     * @see org.tigris.gef.base.InputEvent#isAltGraphDown()
     */
    public boolean isAltGraphDown() {
        return event.isAltGraphDown();
    }

    /*
     * @see org.tigris.gef.base.InputEvent#isConsumed()
     */
    public boolean isConsumed() {
        return event.isConsumed();
    }

    /*
     * @see org.tigris.gef.base.InputEvent#isControlDown()
     */
    public boolean isControlDown() {
        return event.isControlDown();
    }

    /*
     * @see org.tigris.gef.base.InputEvent#isMetaDown()
     */
    public boolean isMetaDown() {
        return event.isMetaDown();
    }

    /*
     * 
     * @see org.tigris.gef.base.InputEvent#isShiftDown()
     */
    public boolean isShiftDown() {
        return event.isShiftDown();
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
