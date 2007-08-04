package org.tigris.gef.swing;

import org.tigris.gef.base.Layer;
import org.tigris.gef.graph.presentation.GraphFrame;
import org.tigris.gef.graph.presentation.Presentation;

public class SwingPresentation implements Presentation {

    public Layer createLayerGrid() {
	return new LayerGrid();
    }
    
    public GraphFrame createGraphFrame() {
	return new JGraphFrame();
    }
}
