package org.tigris.gef.graph.presentation;

import org.tigris.gef.swing.SwingPresentation;

public class PresentationFactory {

    private static Presentation presentation = new SwingPresentation();

    static public Presentation getPresentation() {
	return presentation;
    }
    
    static public void setPresentation(Presentation presentation) {
	PresentationFactory.presentation = presentation;
    }
}
