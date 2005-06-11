package org.tigris.gef.base;

/**
 * @author Bob Tarling
 */
public interface Memento {
    void undo();
    void redo();
}
