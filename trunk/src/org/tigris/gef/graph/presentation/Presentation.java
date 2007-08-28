package org.tigris.gef.graph.presentation;

import org.tigris.gef.base.Editor;
import org.tigris.gef.base.FigModifyingMode;
import org.tigris.gef.base.Layer;
import org.tigris.gef.ui.IToolBar;

public interface Presentation {

    Layer createLayerGrid();
    
    FigModifyingMode createModeDragScroll(Editor editor);
    
    GraphFrame createGraphFrame();
    
    IToolBar createToolBar();
    
    IToolBar createPaletteFig();
}
