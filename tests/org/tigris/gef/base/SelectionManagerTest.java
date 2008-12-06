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

import java.awt.Graphics;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JComponent;

import org.tigris.gef.presentation.Fig;

import junit.framework.TestCase;

public class SelectionManagerTest extends TestCase {
    
    private Editor editor;
    private JComponent b;
    
    public SelectionManagerTest() {
        editor = new Editor();
        // this is needed for preventing NPE in damage(),
        // called by selectFigs
        b = new JButton();
        editor.setJComponent(b);
    }

    public void testAddFigs() {
        // this takes only assures that we are using generics 
        // in the correct way.
        SelectionManager manager = new SelectionManager(editor);        
        Collection<NewFig> figs = new LinkedList<NewFig>();
        manager.addFigs(figs);
        // test pass.        
    }

    public void testSelectFigs() {
        // this takes only assures that we are using generics 
        // in the correct way.
        SelectionManager manager = new SelectionManager(editor);
        Collection<NewFig> figs = new LinkedList<NewFig>();
        manager.selectFigs(figs);
        // test pass.   
    }

}

class NewFig extends Fig {
    @Override
    public void appendSvg(StringBuffer sb) {
        // do nothing        
    }
    @Override
    public void paint(Graphics g) {
        // do nothing        
    }
}
