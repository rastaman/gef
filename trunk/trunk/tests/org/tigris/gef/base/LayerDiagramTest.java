/*
 * Created on 02-May-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tigris.gef.base;

import java.awt.Rectangle;

import org.tigris.gef.presentation.FigRect;

import junit.framework.TestCase;

/**
 * 
 * @author Bob Tarling
 * @since 02-May-2004
 */
public class LayerDiagramTest extends TestCase {

    /**
     * Constructor for LayerTest.
     * @param arg0
     */
    public LayerDiagramTest(String arg0) {
        super(arg0);
    }

    public static void main(String[] args) {
    }

    final public void testCalcDrawingArea() {
        // Test a layer containing two figs
        LayerDiagram lay = new LayerDiagram();
        lay.add(new FigRect(20, 20, 80, 80));
        lay.add(new FigRect(120, 120, 200, 200));
        Rectangle rect = lay.calcDrawingArea();
        assertEquals("Rectangle is the wrong size", new Rectangle(16,16,308,308), rect);
        // Test a layer containing no figs
        lay = new LayerDiagram();
        rect = lay.calcDrawingArea();
        assertEquals("Rectangle is the wrong size", new Rectangle(Integer.MAX_VALUE - 4, Integer.MAX_VALUE - 4, 8 - Integer.MAX_VALUE, 8 - Integer.MAX_VALUE), rect);
    }
}
