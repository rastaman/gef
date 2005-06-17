package org.tigris.gef.base;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A place to register the UndoStack to be used by the application.
 * @author Bob Tarling
 */
public class UndoManager {

    private Collection listeners = new ArrayList();
    
    /**
     * Default to the standard undo manager but applications can set this
     * themseleves by calling setInstance with some extension of UndoManager.
     */
    private static UndoManager instance = new UndoManager();
    
    private UndoManager() {
        super();
    }
    
    public static void setInstance(UndoManager manager) {
        instance = manager;
    }
    
    public static UndoManager getInstance() {
        return instance;
    }
    
    protected ArrayList undoStack = new ArrayList();
    protected ArrayList redoStack = new ArrayList();

    public void addMemento(Memento memento) {
        undoStack.add(memento);
        redoStack.clear();
        fire();
    }
    
    public void undo() {
        Memento memento = pop(undoStack);
        memento.undo();
        redoStack.add(memento);
        fire();
    }
    
    public void redo() {
        Memento memento = pop(redoStack);
        memento.redo();
        undoStack.add(memento);
    }
    
    private Memento pop(ArrayList stack) {
        return (Memento)stack.remove(stack.size()-1);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.listeners.add(listener);
    }
    
    private void fire() {
        Iterator i = listeners.iterator();
        while (i.hasNext()) {
            PropertyChangeListener listener = (PropertyChangeListener) i.next();
            listener.propertyChange(new PropertyChangeEvent(this, "size", "", 
                    Integer.toString(undoStack.size()) 
                    + ";"
                    + Integer.toString(redoStack.size())));
        }
    }
}
