package org.tigris.gef.presentation;

/**
 * An abstract factory for creating complex Fig objects
 * This should be extended by the application to create
 * Figs for that application or for convenience FigNodeFactory can
 * be used.
 * Any extension to this class MUST call the init method
 * of this class before returning the Fig.
 * @author Bob Tarling
 * @since release 0.11
 */
public abstract class AbstractFigFactory {
    final protected void init(Fig fig) {
        fig.setFactoryConstructed();
    }
}
