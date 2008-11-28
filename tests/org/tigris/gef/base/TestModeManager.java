// $Id$
// Copyright (c) 2008 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
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

package org.tigris.gef.base;

import java.awt.Cursor;
import java.awt.Graphics;
import java.util.Hashtable;

import org.tigris.gef.presentation.Fig;

import junit.framework.TestCase;

public class TestModeManager extends TestCase {

    private Editor editor;
    
    @Override
    protected void setUp() throws Exception {
        editor = new Editor();
    }

    public void testTop() {
        MockMode mockMode = new MockMode();  
        MockMode2 mockMode2 = new MockMode2();
        Mode mode = null;
        ModeManager manager = new ModeManager(editor);
        
        // there is not any mode, so it should return null
        mode = manager.top();
        assertNull(mode);

        // we push a mode, and it should return that mode
        manager.push(mockMode);
        mode = manager.top();
        assertNotNull("top() didn't returned a Mode.", 
                mode);
        assertEquals("top() didn't returned the correct Mode.", 
                mockMode, mode);
        
        // we push a mode, and it should behave like a LIFO
        manager.push(mockMode2);
        mode = manager.top();
        assertNotNull("top() didn't returned a Mode.", 
                mode);
        assertEquals("top() didn't returned the last inserted Mode.", 
                mockMode2, mode);
    }

    public void testPush() {        
        MockMode mockMode = new MockMode();  
        MockMode mockMode2 = new MockMode();
        Mode mode = null;
        ModeManager manager = new ModeManager(editor);

        // we push a mode, and it should return that mode
        manager.push(mockMode);
        mode = manager.top();
        assertNotNull("top() didn't returned a Mode.", 
                mode);
        assertEquals("top() didn't returned the correct Mode.", 
                mockMode, mode);

        // we push a different mode of the same type,
        // so push should do nothing and top should return 
        // the same mode again.
        manager.push(mockMode2);
        mode = manager.top();
        assertNotNull("top() didn't returned a Mode.", 
                mode);
        assertEquals("top() didn't returned the correct Mode.", 
                mockMode, mode);
    }
    
    public void testPushNull() {
        ModeManager manager = new ModeManager(editor);
        manager.push(null);
        // no error should happen, we ignore nulls
    }

    public void testPop() {
        MockMode mockMode = new MockMode();  
        MockMode2 mockMode2 = new MockMode2();
        Mode mode = null;
        ModeManager manager = new ModeManager(editor);

        // there is not any mode, so it should return null
        mode = manager.pop();
        assertNull(mode);
        
        // we push a mode, and it should return that mode
        manager.push(mockMode);
        mode = manager.pop();
        assertNotNull("pop() didn't returned a Mode.", 
                mode);
        assertEquals("pop() didn't returned the correct Mode.", 
                mockMode, mode);

        
        // there is not any more modes, so it should return null
        mode = manager.pop();
        assertNull("There shouldn't be any modes in the stack",
                mode);
        
        // we push a mode, and it should return that mode 
        // always because it never exits.
        manager.push(mockMode2);
        mode = manager.pop();
        mode = manager.pop();
        assertNotNull("mode has been exited and it shouldn't!", 
                mode);

    }
/*
    public void testPopAll() {
        fail("Not yet implemented"); // TODO
    }

    public void testIncludes() {
        fail("Not yet implemented"); // TODO
    }

    public void testLeaveAll() {
        fail("Not yet implemented"); // TODO
    }

    public void testKeyTyped() {
        fail("Not yet implemented"); // TODO
    }

    public void testKeyReleased() {
        fail("Not yet implemented"); // TODO
    }

    public void testKeyPressed() {
        fail("Not yet implemented"); // TODO
    }

    public void testMouseMoved() {
        fail("Not yet implemented"); // TODO
    }

    public void testMouseDragged() {
        fail("Not yet implemented"); // TODO
    }

    public void testMouseClicked() {
        fail("Not yet implemented"); // TODO
    }

    public void testMousePressed() {
        fail("Not yet implemented"); // TODO
    }

    public void testMouseReleased() {
        fail("Not yet implemented"); // TODO
    }

    public void testMouseEntered() {
        fail("Not yet implemented"); // TODO
    }

    public void testMouseExited() {
        fail("Not yet implemented"); // TODO
    }

    public void testCheckModeTransitions() {
        fail("Not yet implemented"); // TODO
    }

    public void testAddModeChangeListener() {
        fail("Not yet implemented"); // TODO
    }

    public void testRemoveModeChangeListener() {
        fail("Not yet implemented"); // TODO
    }

    public void testFireModeChanged() {
        fail("Not yet implemented"); // TODO
    }

    public void testPaint() {
        fail("Not yet implemented"); // TODO
    }
*/
}
/**
 * A mock mode that can be exited
 * @author penyaskito
 */
class MockMode implements FigModifyingMode {

    public Editor getEditor() {
        return null;
    }

    public Cursor getInitialCursor() {
        return null;
    }

    public String instructions() {
        return null;
    }

    public boolean isFigEnclosedIn(Fig testedFig, Fig enclosingFig) {
        return false;
    }

    public void paint(Graphics g) {        
    }

    public void print(Graphics g) {        
    }

    public void setCursor(Cursor c) {        
    }

    public void setEditor(Editor w) {        
    }

    public boolean canExit() {
        return true;
    }

    public void done() {
    }

    public Object getArg(String key) {
        return null;
    }

    public Hashtable getArgs() {
        return null;
    }

    public void init(Hashtable parameters) {
    }

    public void setArg(String key, Object value) {
    }

    public void setArgs(Hashtable args) {
    }

    public void start() {
    }    
}
/**
 * A mock mode that cannot be exited
 * @author penyaskito
 */
class MockMode2 extends MockMode {
    @Override
    public boolean canExit() {
        return false;
    }
}