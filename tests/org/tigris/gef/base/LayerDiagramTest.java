// $Id$
// Copyright (c) 2004-2008 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
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
        // this test seems bad to me.
        // what should we expect from a layer without figs?
        // - penyaskito 29/11/2008
        // assertEquals("Rectangle is the wrong size", new Rectangle(Integer.MAX_VALUE - 4, Integer.MAX_VALUE - 4, 8 - Integer.MAX_VALUE, 8 - Integer.MAX_VALUE), rect);
    }
}
