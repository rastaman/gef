package org.tigris.gef.graph.presentation;

import org.tigris.gef.base.Layer;

public interface Presentation {

    Layer createLayerGrid();
    
    GraphFrame createGraphFrame();
}
