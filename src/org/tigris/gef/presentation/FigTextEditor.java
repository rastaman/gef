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



// File: FigTextEditor.java
// Classes: FigTextEditor
// Original Author: jrobbins@ics.uci.edu
// $Id$

package org.tigris.gef.presentation;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentListener;

import java.awt.Color;
import java.awt.event.InputEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeListener;

// needs-more-work: could this be a singleton?

public abstract class FigTextEditor extends JTextPane implements PropertyChangeListener, DocumentListener, KeyListener {

    protected static int _extraSpace = 2;
    protected static Border _border = BorderFactory.createLineBorder(Color.gray);
    protected static boolean _makeBrighter = false;
    protected static Color _backgroundColor = null;

    /** Needs-more-work: does not open if I use tab to select the
     *  FigText. */
    public FigTextEditor() {
    }
    
    private static FigTextEditor _activeTextEditor;

    /**
     * Factory method to create a new instance of a FigTextEditor.
     * @returns a new FigTextEditor instance.
     */
    public static final FigTextEditor newInstance() {
        String version = System.getProperty("java.version");
    
        FigTextEditor fte = null;
        if (version.startsWith("1.2.") || version.startsWith("1.3.")) {
            fte = new FigTextEditor13();
        } else {
            fte = new FigTextEditor14();
        }

        return fte;
    }

    public static void configure(int extraSpace, Border border, boolean makeBrighter, Color backgroundColor) {
        _extraSpace = extraSpace;
        _border = border;
        _makeBrighter = makeBrighter;
        _backgroundColor = backgroundColor;
    }

    public static synchronized FigTextEditor getActiveTextEditor() {
        return _activeTextEditor;
    }

    public static synchronized void setActiveTextEditor(FigTextEditor fte) {
        _activeTextEditor = fte;
    }

    public static synchronized void remove() {
        if(_activeTextEditor != null) {
            FigTextEditor old = _activeTextEditor;
            _activeTextEditor = null;
            old.endEditing();
        }
    }
    
    public abstract void init(FigText ft, InputEvent ie);
    public abstract void endEditing();
}
