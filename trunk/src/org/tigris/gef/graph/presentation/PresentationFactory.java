package org.tigris.gef.graph.presentation;

import org.tigris.gef.swing.SwingPresentation;
import org.tigris.gef.swt.SwtPresentation;
//graphics2dFactory = org.holongate.j2d.J2DRegistry.createGraphics2DFactory(super.getSWTPeer());

public class PresentationFactory {

    private static Presentation presentation = new SwingPresentation();

    static public Presentation getPresentation() {
	return presentation;
    }
    
    static public void setPresentation(Presentation presentation) {
	PresentationFactory.presentation = presentation;
    }
}
