package org.tigris.gef.base;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A place to register the UndoStack to be used by the application.
 * @author Bob Tarling
 */
public class UndoManager {

    private int undoMax = 100;
    private int undoChainCount = 0;
    private int redoChainCount = 0;
    
    private Collection listeners = new ArrayList();
    
    private boolean newChain = true;
    
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

    /**
     * Adds a new memento to the undo stack.
     * @param memento the memento.
     */
    public void addMemento(Memento memento) {
        // Flag the memento as to whether it is first in a chain
        memento.startChain = newChain;
        if (newChain) {
            // If the memento is the first then consider that
            // there is a new chain received and clear the redos
            redoStack.clear();
            if (++undoChainCount > undoMax) {
                // dispose of the oldest chain.
            }
            newChain = false;
        }
        undoStack.add(memento);
        fire();
    }
    
    public void setUndoMax(int max) {
        undoMax = max;
    }

    /**
     * Undo the most recent chain of mementos received by the undo stack
     */
    public void undo() {
        Memento memento;
        do {
            memento = pop(undoStack);
            memento.undo();
            redoStack.add(memento);
        } while (!memento.startChain);
        fire();
    }
    
    /**
     * Redo the most recent chain of mementos received by the undo stack
     */
    public void redo() {
        do {
            Memento memento = pop(redoStack);
            memento.redo();
            undoStack.add(memento);
        } while(redoStack.size() > 0 &&
                !((Memento)redoStack.get(redoStack.size()-1)).startChain);
    }

    /**
     * Empty all undoable items from the UndoManager
     */
    public void emptyUndo() {
        emptyStack(undoStack);
        fire();
    }
    
    /**
     * Empty all undoable and redoable items from the UndoManager
     */
    public void empty() {
        emptyStack(redoStack);
        emptyUndo();
        fire();
    }
    
    /**
     * Instructs the UndoManager that the sequence of mementos recieved up
     * until the next call to newChain all represent one chain of mementos
     * (ie one undoable user interaction).
     */
    public void startChain() {
        newChain = true;
    }
 
    /**
     * Empty a list stack disposing of all mementos.
     * @param list the list of mementos
     */
    private void emptyStack(List list) {
        for (int i=0; i < list.size(); ++i) {
            ((Memento)list.get(i)).dispose();
        }
        list.clear();
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
                    Integer.toString(undoChainCount) 
                    + ";"
                    + Integer.toString(redoChainCount)));
        }
    }
}
