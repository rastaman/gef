package org.tigris.gef.swt;

import org.tigris.gef.base.Editor;
import org.tigris.gef.base.FigModifyingMode;
import org.tigris.gef.base.Layer;
import org.tigris.gef.graph.presentation.GraphFrame;
import org.tigris.gef.graph.presentation.Presentation;
import org.tigris.gef.ui.IToolBar;

public class SwtPresentation implements Presentation {

    public Layer createLayerGrid() {
	return new LayerGrid();
    }
    
    public FigModifyingMode createModeDragScroll(Editor editor) {
	return new ModeDragScroll(editor);
    }
    
    public GraphFrame createGraphFrame() {
	return new JGraphFrame();
    }
    public IToolBar createToolBar(IToolBar toolBar)
    {
        return (ToolBar)toolBar;
    }

    public IToolBar createToolBar()
    {
        return new ToolBar();
    }
    
    public IToolBar createPaletteFig() {
        return new PaletteFig();
    }
}
