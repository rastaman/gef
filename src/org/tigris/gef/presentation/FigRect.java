// Copyright (c) 1996-99 The Regents of the University of California. All
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

// File: FigRect.java
// Classes: FigRect
// Original Author: ics125 spring 1996
// $Id$

package org.tigris.gef.presentation;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

/** Primitive Fig to paint rectangles on a LayerDiagram. */

public class FigRect extends Fig implements Serializable {

    ////////////////////////////////////////////////////////////////
    // constructors

    /** Construct a new resizable FigRect with the given position and size. */
    public FigRect(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    /** Construct a new resizable FigRect with the given position, size, line color,
     *  and fill color. */
    public FigRect(int x, int y, int w, int h, Color lColor, Color fColor) {
        super(x, y, w, h, lColor, fColor);
    }

    /** Construct a new FigRect w/ the given position and size. */
    public FigRect(int x, int y, int w, int h, boolean resizable) {
        super(x, y, w, h);
        this.resizable = resizable;
    }

    /** Construct a new FigRect w/ the given position, size, line color,
     *  and fill color. */
    public FigRect(int x, int y, int w, int h, boolean resizable, Color lColor, Color fColor) {
        super(x, y, w, h, lColor, fColor);
        this.resizable = resizable;
    }

    ////////////////////////////////////////////////////////////////
    // painting methods

    /** Paint this FigRect */
    public void paint(Graphics g) {
        int xx = _x;
        int yy = _y;
        int ww = _w;
        int hh = _h;
        if (_filled && _fillColor != null) {
            if (_lineColor != null) {
                if (_lineWidth > 1 && !getDashed()) {
                    int lineWidth2 = _lineWidth*2;
                    g.setColor(_lineColor);
                    g.fillRect(xx, yy, ww, hh);
                    xx += _lineWidth;
                    yy += _lineWidth;
                    ww -= lineWidth2;
                    hh -= lineWidth2;
                }
            }
            g.setColor(_fillColor);
            g.fillRect(xx, yy, ww, hh);
            if (_lineColor != null) {
                if (_lineWidth == 1 || getDashed()) {
                    paintRectLine(g, xx, yy, ww, hh, _lineWidth);
                }
            }
        } else {
            paintRectLine(g, xx, yy, ww, hh, _lineWidth);
        }
    }

    /**
     * Paint the line of a rectangle without any fill.
     * Manages line width and dashed lines.
     * @param g The Graphics object
     * @param x The x co-ordinate of the rectangle
     * @param y The y co-ordinate of the rectangle
     * @param w The width of the rectangle
     * @param h The height of the rectangle
     * @param lwidth The linewidth of the rectangle
     */
    private void paintRectLine(Graphics g, int x, int y, int w, int h, int lWidth) {
        if (lWidth > 0 && _lineColor != null) {
            g.setColor(_lineColor);
            if (lWidth == 1) {
                paintRectLine(g, x, y, w, h);
            } else {
                int xx = x;
                int yy = y;
                int hh = h;
                int ww = w;
                
                for (int i=0; i < lWidth; ++i) {
                    paintRectLine(g, xx++, yy++, ww, hh);
                    ww -= 2;
                    hh -= 2;
                }
            }
        }
    }
    
    private void paintRectLine(Graphics g, int x, int y, int w, int h) {
        if (!getDashed())
            g.drawRect(x, y, w, h);
        else {
            drawDashedRectangle(g, 0, x, y, w, h);
        }
    }
    
    protected void drawDashedRectangle(Graphics g, int phase, int x, int y, int w, int h) {
        phase = drawDashedLine(g, phase, _x, _y, _x + _w, _y);
        phase = drawDashedLine(g, phase, _x + _w, _y, _x + _w, _y + _h);
        phase = drawDashedLine(g, phase, _x + _w, _y + _h, _x, _y + _h);
        phase = drawDashedLine(g, phase, _x, _y + _h, _x, _y);
    }

    
} /* end class FigRect */
