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

package org.tigris.gef.swing;

import org.tigris.gef.base.CmdCreateNode;
import org.tigris.gef.base.CmdSetMode;
import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.ui.IToolBar;
import org.tigris.gef.util.ResourceLoader;

import javax.swing.Action;
import javax.swing.Icon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

public class ToolBar extends javax.swing.JToolBar implements MouseListener, IToolBar {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected Vector _lockable = new Vector();
    protected Vector _modeButtons = new Vector();
    private static final Color selectedBack = new Color(153, 153, 153);
    private static final Color buttonBack = new Color(204, 204, 204);

    public ToolBar() {
        setFloatable(false);
        setName("toolBar");
    }

    public JButton add(Action a)
    {        
        String name = (String)a.getValue(Action.NAME);
        Icon icon = (Icon)a.getValue(Action.SMALL_ICON);
        return add(a, name, icon);
    }
    /* (non-Javadoc)
     * @see org.tigris.gef.ui.ToolBar#add(javax.swing.Action, java.lang.String, java.lang.String)
     */
    public JButton add(Action a, String name, String iconResourceStr) {
        Icon icon = ResourceLoader.lookupIconResource(iconResourceStr, name);
        //System.out.println(icon);
        return add(a, name, icon);
    }

    /* (non-Javadoc)
     * @see org.tigris.gef.ui.ToolBar#add(javax.swing.Action, java.lang.String, javax.swing.Icon)
     */
    public JButton add(Action a, String name, Icon icon) {
//     JButton b = new JButton(icon);
//     if (a instanceof CmdSetMode || a instanceof CmdCreateNode)
//       _modeButtons.addElement(b);
//     b.setToolTipText(name + " ");
//     b.setEnabled(a.isEnabled());
//     b.addActionListener(a);
//     add(b);
//     if (a instanceof CmdSetMode || a instanceof CmdCreateNode)
//       _lockable.addElement(b);
//     PropertyChangeListener actionPropertyChangeListener =
//       createActionChangeListener(b);
// 	if ( actionPropertyChangeListener != null ) {
// 		a.addPropertyChangeListener(actionPropertyChangeListener);
// 	}
//     b.addMouseListener(this);
//     // needs-more-work: should buttons appear stuck down while action executes?
//     return b;

        JButton b = super.add(a);
        b.setName(null);
        b.setText(null);
        b.setIcon(icon);
        b.setToolTipText(name + " ");
        if(a instanceof CmdSetMode || a instanceof CmdCreateNode)
            _modeButtons.addElement(b);
        if(a instanceof CmdSetMode || a instanceof CmdCreateNode)
            _lockable.addElement(b);
        b.addMouseListener(this);
        // needs-more-work: should buttons appear stuck down while action executes?
        return b;
    }

    /* (non-Javadoc)
     * @see org.tigris.gef.ui.ToolBar#add(java.awt.Component)
     */
    public Component add(Component comp) {
        if(comp instanceof JButton) {
            JButton button = (JButton)comp;
            Action action = button.getAction();
            if(action instanceof CmdSetMode || action instanceof CmdCreateNode)
                _modeButtons.addElement(button);
            if(action instanceof CmdSetMode || action instanceof CmdCreateNode)
                _lockable.addElement(button);
            button.addMouseListener(this);
        }
        return super.add(comp);
    }

    /* (non-Javadoc)
     * @see org.tigris.gef.ui.ToolBar#addToggle(javax.swing.Action)
     */
    public JToggleButton addToggle(Action a) {
        String name = (String)a.getValue(Action.NAME);
        Icon icon = (Icon)a.getValue(Action.SMALL_ICON);
        return addToggle(a, name, icon);
    }

    /* (non-Javadoc)
     * @see org.tigris.gef.ui.ToolBar#addToggle(javax.swing.Action, java.lang.String, java.lang.String)
     */
    public JToggleButton addToggle(Action a, String name, String iconResourceStr) {
        Icon icon = ResourceLoader.lookupIconResource(iconResourceStr, name);
        //System.out.println(icon);
        return addToggle(a, name, icon);
    }

    /* (non-Javadoc)
     * @see org.tigris.gef.ui.ToolBar#addToggle(javax.swing.Action, java.lang.String, javax.swing.Icon)
     */
    public JToggleButton addToggle(Action a, String name, Icon icon) {
        JToggleButton b = new JToggleButton(icon);
        b.setToolTipText(name + " ");
        b.setEnabled(a.isEnabled());
        b.addActionListener(a);
        add(b);
        PropertyChangeListener actionPropertyChangeListener = createActionToggleListener(b);
        a.addPropertyChangeListener(actionPropertyChangeListener);
        // needs-more-work: should buttons appear stuck down while action executes?
        return b;
    }

    /* (non-Javadoc)
     * @see org.tigris.gef.ui.ToolBar#addToggle(javax.swing.Action, java.lang.String, java.lang.String, java.lang.String)
     */
    public JToggleButton addToggle(Action a, String name, String upRes, String downRes) {
        ImageIcon upIcon = ResourceLoader.lookupIconResource(upRes, name);
        ImageIcon downIcon = ResourceLoader.lookupIconResource(downRes, name);
        JToggleButton b = new JToggleButton(upIcon);
        b.setToolTipText(name + " ");
        b.setEnabled(a.isEnabled());
        b.addActionListener(a);
        b.setMargin(new Insets(0, 0, 0, 0));
        add(b);
        PropertyChangeListener actionPropertyChangeListener = createActionToggleListener(b);
        a.addPropertyChangeListener(actionPropertyChangeListener);
        // needs-more-work: should buttons appear stuck down while action executes?
        return b;
    }


    /* (non-Javadoc)
     * @see org.tigris.gef.ui.ToolBar#addRadioGroup(java.lang.String, javax.swing.ImageIcon, javax.swing.ImageIcon, java.lang.String, javax.swing.ImageIcon, javax.swing.ImageIcon)
     */
    public ButtonGroup addRadioGroup(String name1, ImageIcon oneUp, ImageIcon oneDown, String name2, ImageIcon twoUp, ImageIcon twoDown) {
        JRadioButton b1 = new JRadioButton(oneUp, true);
        b1.setSelectedIcon(oneDown);
        b1.setToolTipText(name1 + " ");
        b1.setMargin(new Insets(0, 0, 0, 0));
        b1.getAccessibleContext().setAccessibleName(name1);

        JRadioButton b2 = new JRadioButton(twoUp, false);
        b2.setSelectedIcon(twoDown);
        b2.setToolTipText(name2 + " ");
        b2.setMargin(new Insets(0, 0, 0, 0));
        b2.getAccessibleContext().setAccessibleName(name2);

        add(b1);
        add(b2);

        //     JPanel p = new JPanel();
        //     p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        //     p.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        //     p.add(b1);
        //     p.add(b2);
        //     add(p);

        ButtonGroup bg = new ButtonGroup();
        bg.add(b1);
        bg.add(b2);
        return bg;
    }

    protected PropertyChangeListener createActionToggleListener(JToggleButton b) {
        return new ActionToggleChangedListener(b);
    }

    private class ActionToggleChangedListener implements PropertyChangeListener {
        JToggleButton button;

        ActionToggleChangedListener(JToggleButton b) {
            super();
            this.button = b;
        }

        public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
            if(e.getPropertyName().equals(Action.NAME)) {
                String text = (String)e.getNewValue();
                button.setText(text);
                button.repaint();
            }
            else if(propertyName.equals("enabled")) {
                Boolean enabledState = (Boolean)e.getNewValue();
                button.setEnabled(enabledState.booleanValue());
                button.repaint();
            }
            else if(e.getPropertyName().equals(Action.SMALL_ICON)) {
                Icon icon = (Icon)e.getNewValue();
                button.setIcon(icon);
                button.invalidate();
                button.repaint();
            }
        }
    }



    ////////////////////////////////////////////////////////////////
    // MouseListener implementation

    /* (non-Javadoc)
     * @see org.tigris.gef.ui.ToolBar#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent me) {
    }

    /* (non-Javadoc)
     * @see org.tigris.gef.ui.ToolBar#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent me) {
    }

    /* (non-Javadoc)
     * @see org.tigris.gef.ui.ToolBar#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent me) {
    }

    /* (non-Javadoc)
     * @see org.tigris.gef.ui.ToolBar#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent me) {
    }

    /* (non-Javadoc)
     * @see org.tigris.gef.ui.ToolBar#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent me) {
        Object src = me.getSource();
        if(isModeButton(src)) {
            unpressAllButtonsExcept(src);
            Editor ce = Globals.curEditor();
            if(ce != null)
                ce.finishMode();
            Globals.setSticky(false);
        }
        if(me.getClickCount() >= 2) {
            if(!(src instanceof JButton))
                return;
            JButton b = (JButton)src;
            if(canLock(b)) {
                b.getModel().setPressed(true);
                b.getModel().setArmed(true);
                b.setBackground(selectedBack);
                Globals.setSticky(true);
            }
        }
        else if(me.getClickCount() == 1) {
            if(src instanceof JButton && isModeButton(src)) {
                JButton b = (JButton)src;
                b.setFocusPainted(false);
                b.getModel().setPressed(true);
                b.setBackground(selectedBack);
            }
        }
    }


    protected boolean canLock(Object b) {
        return _lockable.contains(b);
    }

    protected boolean isModeButton(Object b) {
        return _modeButtons.contains(b);
    }

    protected void unpressAllButtonsExcept(Object src) {
        int size = getComponentCount();
        for(int i = 0; i < size; i++) {
            Component c = getComponent(i);
            if(!(c instanceof JButton))
                continue;
            if(c == src)
                continue;
            ((JButton)c).getModel().setArmed(false);
            ((JButton)c).getModel().setPressed(false);
            ((JButton)c).setBackground(buttonBack);
        }
    }

    /* (non-Javadoc)
     * @see org.tigris.gef.ui.ToolBar#unpressAllButtons()
     */
    public void unpressAllButtons() {
        int size = getComponentCount();
        for(int i = 0; i < size; i++) {
            Component c = getComponent(i);
            if(!(c instanceof JButton))
                continue;
            ((JButton)c).getModel().setArmed(false);
            ((JButton)c).getModel().setPressed(false);
            ((JButton)c).setBackground(buttonBack);
        }
        // press the first button (usually ModeSelect)
        for(int i = 0; i < size; i++) {
            Component c = getComponent(i);
            if(!(c instanceof JButton))
                continue;
            JButton select = (JButton)c;
            select.getModel().setArmed(true);
            select.getModel().setPressed(true);
            select.setBackground(selectedBack);
            return;
        }
    }

    public void mouseClicked(org.tigris.gef.base.MouseEvent me) {
        
    }

    public void mouseEntered(org.tigris.gef.base.MouseEvent me) {
        // TODO Auto-generated method stub
        
    }

    public void mouseExited(org.tigris.gef.base.MouseEvent me) {
        // TODO Auto-generated method stub
        
    }

    public void mousePressed(org.tigris.gef.base.MouseEvent me) {
        // TODO Auto-generated method stub
        
    }

    public void mouseReleased(org.tigris.gef.base.MouseEvent me) {
        // TODO Auto-generated method stub
        
    }

} /* end class ToolBar */
