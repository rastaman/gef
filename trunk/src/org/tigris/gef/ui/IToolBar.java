package org.tigris.gef.ui;

import javax.swing.Action;
import javax.swing.JButton;

public interface IToolBar {

    /**
     * Add a new JButton which dispatches the action.
     *
     * @param a the Action object to add as a new menu item
     */
    public abstract JButton add(Action a);

    public abstract JButton add(Action a, String name, String iconResourceStr);
    
    public abstract void addSeparator();

}