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

import org.tigris.gef.presentation.Fig;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/** Primitive Fig to paint rectangles on a LayerDiagram. */

public class FigDiamond extends Fig {

    int gravityPointsPerSide = 10;
    ////////////////////////////////////////////////////////////////
    // constructors

    /** Construct a new resizable FigRect with the given position and size. */
    public FigDiamond(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    /** Construct a new resizable FigRect with the given position, size, line color,
     *  and fill color. */
    public FigDiamond(int x, int y, int w, int h, Color lColor, Color fColor) {
        super(x, y, w, h, lColor, fColor);
    }

    /** Construct a new FigRect w/ the given position and size. */
    public FigDiamond(int x, int y, int w, int h, boolean resizable) {
        super(x, y, w, h);
        setResizable(resizable);
    }

    /** Construct a new FigRect w/ the given position, size, line color,
     *  and fill color. */
    public FigDiamond(int x, int y, int w, int h, boolean resizable, Color lColor, Color fColor) {
        super(x, y, w, h, lColor, fColor);
        setResizable(resizable);
    }

    /**
     * Makes sure that the edges stick to the outline of the fig.
     * @see org.tigris.gef.presentation.Fig#getGravityPoints()
     */
    public List getGravityPoints() {
        ArrayList ret = new ArrayList(gravityPointsPerSide * 4);
        int divisor = gravityPointsPerSide + 1;
        for (int i=0; i < gravityPointsPerSide; ++i) {
            ret.add(new Point(_x + (_w / divisor)*i, _y));
            ret.add(new Point(_x + _w, _y + (_h / divisor)*i));
            ret.add(new Point(_x + (_w / divisor)*i, _y + _h));
            ret.add(new Point(_x, _y + (_h / divisor)*i));
        }
        return ret;
    }

    ////////////////////////////////////////////////////////////////
    // painting methods

    /** Paint this FigRect */
    public void paint(Graphics g) {
        int xs[] = new int[4];
        int ys[] = new int[4];
        xs[0] = _x + _w/2;
        ys[0] = _y;
        xs[1] = _x + _w;
        ys[1] = _y + _h/2;
        xs[2] = _x + _w/2;
        ys[2] = _y + _h;
        xs[3] = _x;
        ys[3] = _y + _h/2;
        if (_filled && getFillColor() != null) {
            g.setColor(getFillColor());
            g.fillPolygon(xs, ys, 4);
        }
        if (getLineWidth() > 0 && getLineColor() != null) {
            g.setColor(getLineColor());
            g.drawPolygon(xs, ys, 4);
        }
    }
} /* end class FigRect */
