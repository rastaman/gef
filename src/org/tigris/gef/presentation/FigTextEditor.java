// $Id$
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

package org.tigris.gef.presentation;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.undo.UndoManager;

/**
 * A text pane for on screen editing of a FigText.
 * @author jrobbins
 */
public class FigTextEditor extends JTextPane implements PropertyChangeListener, DocumentListener, KeyListener, FocusListener {

    private FigText figText;
    /** @deprecated will become private */
    JPanel _drawingPanel;
    /** @deprecated will become private */
    JLayeredPane _layeredPane;

    private static int _extraSpace = 2;
    private static Border _border = BorderFactory.createLineBorder(Color.gray);
    private static boolean _makeBrighter = false;
    private static Color _backgroundColor = null;

    private static Log LOG = LogFactory.getLog(FigTextEditor.class);

    private static final FigTextEditor INSTANCE = new FigTextEditor();
    
    public static FigTextEditor getInstance() {
        return INSTANCE;
    }
    
    private FigTextEditor() {
    }

    public static void configure(int extraSpace, Border b, boolean makeBrighter, Color backgroundColor) {
        _extraSpace = extraSpace;
        _border = b;
        _makeBrighter = makeBrighter;
        _backgroundColor = backgroundColor;
    }

    public void init(FigText ft, InputEvent ie) {
        setVisible(true);
        figText = ft;
        Editor ce = Globals.curEditor();
        
        _drawingPanel = (JPanel)ce.getJComponent();
        UndoManager.getInstance().startChain(); 
        figText.firePropChange("editing", false, true);
        figText.addPropertyChangeListener(this);
        // walk up and add to glass pane
        Component awtComp = _drawingPanel;
        while (!(awtComp instanceof JFrame) && awtComp != null) {
            awtComp = awtComp.getParent();
        }
        if (!(awtComp instanceof JFrame)) {
            LOG.warn("no JFrame");
            return;
        }
        _layeredPane = ((JFrame)awtComp).getLayeredPane();

        ft.calcBounds();
        Rectangle bbox = ft.getBounds();

        Color figTextBackgroundColor = ft.getFillColor();
        Color myBackground;
        if(_makeBrighter && !figTextBackgroundColor.equals(Color.white)) {
            myBackground = figTextBackgroundColor.brighter();
        }
        else if(_backgroundColor != null) {
            myBackground = _backgroundColor;
        }
        else {
            myBackground = figTextBackgroundColor;
        }

        setBackground(myBackground);

        setBorder(_border);

        double scale = ce.getScale();
        bbox.x = (int)Math.round(bbox.x * scale);
        bbox.y = (int)Math.round(bbox.y * scale);

        if(scale > 1) {
            bbox.width = (int)Math.round(bbox.width * scale);
            bbox.height = (int)Math.round(bbox.height * scale);
        }

        bbox = SwingUtilities.convertRectangle(_drawingPanel, bbox, _layeredPane);

        // bounds will be overwritten later in updateFigText anyway...
        setBounds(bbox.x - _extraSpace, bbox.y - _extraSpace, bbox.width + _extraSpace * 2, bbox.height + _extraSpace * 2);
        _layeredPane.add(this, JLayeredPane.POPUP_LAYER, 0);
        String text = ft.getTextFriend();
        
        remove();
        _activeTextEditor = this;

        setText(text);

        addKeyListener(this);
        requestFocus();
        getDocument().addDocumentListener(this);

        setSelectionStart(0);
        setSelectionEnd(getDocument().getLength());
        MutableAttributeSet attr = new SimpleAttributeSet();
        if(ft.getJustification() == FigText.JUSTIFY_CENTER)
            StyleConstants.setAlignment(attr, StyleConstants.ALIGN_CENTER);
        if(ft.getJustification() == FigText.JUSTIFY_RIGHT)
            StyleConstants.setAlignment(attr, StyleConstants.ALIGN_RIGHT);
        Font font = ft.getFont();
        StyleConstants.setFontFamily(attr, font.getFamily());
        StyleConstants.setFontSize(attr, font.getSize());
        setParagraphAttributes(attr, true);
        if(ie instanceof KeyEvent) {
            setSelectionStart(getDocument().getLength());
            setSelectionEnd(getDocument().getLength());
        }
        addFocusListener(this);
    }

    public void propertyChange(PropertyChangeEvent pve) {
        updateFigText();
    }

    public void endEditing() {
        removeFocusListener(this);
        updateFigText();
        setVisible(false);
        if (figText == null) {
            return;
        }
        figText.endTrans();
        Container parent = getParent();
        if(parent != null) {
            parent.remove(this);
        }
        figText.removePropertyChangeListener(this);
        figText.firePropChange("editing", true, false);
        removeKeyListener(this);
        _layeredPane.remove(this);
        _drawingPanel.requestFocus();
        _activeTextEditor = null;
    }
    
    private static FigTextEditor _activeTextEditor;

    public static synchronized FigTextEditor getActiveTextEditor() {
        return _activeTextEditor;
    }

    public static synchronized void remove() {
        if(_activeTextEditor != null) {
            FigTextEditor old = _activeTextEditor;
            _activeTextEditor = null;
            old.endEditing();
        }
    }

    ////////////////////////////////////////////////////////////////
    // event handlers for KeyListener implementaion
    public void keyTyped(KeyEvent ke) {
        if(ke.getKeyChar() == KeyEvent.VK_ENTER) {
            if (figText.getReturnAction() == FigText.END_EDITING) {
                endEditing();
                ke.consume();
            }
        } else if(ke.getKeyChar() == KeyEvent.VK_TAB) {
            if (figText.getTabAction() == FigText.END_EDITING) {
                endEditing();
                ke.consume();
            }
        } else if(ke.getKeyChar() == KeyEvent.VK_ESCAPE) {
            // needs-more-work: should revert to orig text, or simply don't commit
            endEditing();
            ke.consume();
        }
    }

    public void keyReleased(KeyEvent ke) {
    }

    public void keyPressed(KeyEvent ke) {
    }


    ////////////////////////////////////////////////////////////////
    // event handlers for DocumentListener implementaion

    public void insertUpdate(DocumentEvent e) {
        updateFigText();
    }

    public void removeUpdate(DocumentEvent e) {
        updateFigText();
    }

    public void changedUpdate(DocumentEvent e) {
        updateFigText();
    }


    ////////////////////////////////////////////////////////////////
    // internal utility methods

    protected void updateFigText() {
        if(figText == null)
            return;
        
        String text = getText();
        
        figText.setTextFriend(text, getGraphics());

        if (figText.getReturnAction() == FigText.INSERT && figText.isWordWrap()) {
            return;
        };

        Rectangle bbox = figText.getBounds();
        Editor ce = Globals.curEditor();
        double scale = ce.getScale();
        bbox.x = (int)Math.round(bbox.x * scale);
        bbox.y = (int)Math.round(bbox.y * scale);

        if(scale > 1) {
            bbox.width = (int)Math.round(bbox.width * scale);
            bbox.height = (int)Math.round(bbox.height * scale);
        }

        bbox = SwingUtilities.convertRectangle(_drawingPanel, bbox, _layeredPane);
        
        setBounds(bbox.x - _extraSpace, bbox.y - _extraSpace, bbox.width + _extraSpace * 2, bbox.height + _extraSpace * 2);
        setFont(figText.getFont());
    }

	/**
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	public void focusGained(FocusEvent e) {
	}

	/**
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	public void focusLost(FocusEvent e) {
        endEditing();
	}
}
