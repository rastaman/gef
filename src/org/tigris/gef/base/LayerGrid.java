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




// File: LayerGrid.java
// Classes: LayerGrid
// Original Author: jrobbins@ics.uci.edu
// $Id$

package org.tigris.gef.base;

import org.tigris.gef.presentation.Fig;

import java.awt.*;
import java.util.List;

/** Paint a background drawing guide consisting of horizontal and
 *  vertical lines in a neutral color. This feature is common to many
 *  drawing applications (e.g., MacDraw).  LayerGrid is in concept a
 *  Layer, just like any other so it can be composed, locked, grayed,
 *  hidden, and reordered. <p>
 */
public class LayerGrid extends Layer {

    ////////////////////////////////////////////////////////////////
    // instance variables

    /** The spacing between the lines. */
    private int _spacing = 16;

    /** True means paint grid lines, false means paint only dots where the
     *  lines would intersect. Painting dots is about as useful as painting
     *  lines and it looks less cluttered. But lines are more familiar to
     *  some people. */
    private boolean _paintLines = false;
    private boolean _paintDots = true;

    /** The color of the grid dots. */
    protected Color _colorDots = new Color(164, 164, 164);

    /** The color of the grid lines. This is lighter, because lines are more prominent, anyway. */
    protected Color _colorLines = new Color(192, 192, 192);

    /** The color of the space between the lines or dots.  */
    protected Color _backgroundColor = new Color(208, 208, 208);

    /** True means to fill in the image stamp or drawing area with the
     *  background color. False means to just paint the lines or dots.
     *  Currently the background has to be painted: There is noone else
     *  painting a background, so disabling it here will leave any garbage
     *  on the screen that was there previously. (Besides when painting dots
     *  part of the background is filled anyway.)
     */
    protected boolean _paintBackground = true;

    /** The size of the dots.  Dots are actually small rectangles. */
    protected int _dotSize = 2;

    /** As an example of different grid styles 5 are
     *  defined. Needs-More-Work: I should use the property sheet to
     *  adjust the grid.
     *
     * @see LayerGrid#adjust */
    private int _style = 2;
    private final int NUM_STYLES = 5;

    private Rectangle _clipBounds = new Rectangle();

    private final BasicStroke _thinStroke = new BasicStroke(0);

    ////////////////////////////////////////////////////////////////
    // constructors

    /** Construct a new LayerGrid and name it 'Grid'. */
    public LayerGrid() {
        super("Grid");
    }

    ////////////////////////////////////////////////////////////////
    // accessors

    public List getContents() {
        return null;
    }

    public Fig presentationFor(Object obj) {
        return null;
    }

    public void setPaintBackground(boolean paintBack) {
        _paintBackground = paintBack;
    }

    public boolean getPaintBackground() {
        return _paintBackground;
    }

    public void setPaintDots(boolean paintDots) {
        _paintDots = paintDots;
    }

    public boolean getPaintDots() {
        return _paintDots;
    }

    public void setPaintLines(boolean paintLines) {
        _paintLines = paintLines;
    }

    public boolean getPaintLines() {
        return _paintLines;
    }

    public void setSpacing(int spacing) {
        _spacing = spacing;
    }

    public int getSpacing() {
        return _spacing;
    }
    
    ////////////////////////////////////////////////////////////////
    // painting methods

    /** Paint the grid lines or dots */
    public synchronized void paintContents(Graphics g) {
        Graphics2D g2 = ((Graphics2D)g);
        g.getClipBounds(_clipBounds);

        boolean printing = (g instanceof PrintGraphics);
        boolean paintBackground = (printing && Globals.getPrefs().getPrintBackground()) || (!printing && _paintBackground);

        Stroke oldStroke = g2.getStroke();
        g2.setStroke(_thinStroke);

        if(paintBackground) {
            g.setColor(_backgroundColor);
            g.fillRect(_clipBounds.x, _clipBounds.y, _clipBounds.width, _clipBounds.height);
        }
        
        // This line is for printing under Java 1.1
        if(printing && !Globals.getPrefs().getPrintGrid()) {
            return;
        }

        if(_paintLines)
            paintLines(g2);
        else if(_paintDots)
            paintDots(g2);

        g2.setStroke(oldStroke);
    }

    /** Paint lines on the given Graphics. */
    private void paintLines(Graphics2D g2) {
        int left = _clipBounds.x / _spacing * _spacing - _spacing;
        int top = _clipBounds.y / _spacing * _spacing - _spacing;
        int stepsX = _clipBounds.width / _spacing + 3;
        int stepsY = _clipBounds.height / _spacing + 3;
        int right = _clipBounds.x + _clipBounds.width;
        int bot = _clipBounds.y + _clipBounds.height;

        g2.setColor(_colorLines);

        int x = left;
        while(stepsX > 0) {
            g2.drawLine(x, top, x, bot);
            x += _spacing;
            --stepsX;
        }

        int y = top;
        while(stepsY > 0) {
            g2.drawLine(left, y, right, y);
            y += _spacing;
            --stepsY;
        }
    }

    /** Paint dots on the given Graphics.
     *  Tried a lot of different things (paintind dashed lines, blitting a precomputed image ...),
     *  but this is fastest: Draw solid horizontal lines, then draw vertical lines in background color in
     *  between the grid.  
     */
    private void paintDots(Graphics2D g2) {
        int top = _clipBounds.y / _spacing * _spacing;
        int bottom = ((_clipBounds.y + _clipBounds.height) / _spacing + 1) * _spacing;
        int left = _clipBounds.x / _spacing * _spacing;
        int right = ((_clipBounds.x + _clipBounds.width) / _spacing + 1) * _spacing;

        g2.setColor(_colorDots);
        for(int y = top; y <= bottom; y += _spacing) {
            g2.fillRect(left, y, right - left + 1, _dotSize);
        }
        g2.setColor(_backgroundColor);
        for(int x = left + _dotSize; x <= right; x += _spacing) {
            g2.fillRect(x, top, _spacing - _dotSize, bottom - top + 1);
        }
    }

    ////////////////////////////////////////////////////////////////
    // user interface

    /** Eventually this will open a dialog box to let the user adjust
     *  the grid line spacing, colors, and whether liens or dots are
     *  shown. For now it just cycles among 5 predefined styles. */
    public void adjust() {
        _style = (_style + 1) % NUM_STYLES;
        setHidden(false);
        switch(_style) {
            case 0:
                _paintLines = true;
                _paintDots = true;
                _spacing = 16;
                break;
            case 1:
                _paintLines = true;
                _paintDots = true;
                _spacing = 8;
                break;
            case 2:
                _paintLines = false;
                _paintDots = true;
                _spacing = 16;
                break;
            case 3:
                _paintLines = false;
                _paintDots = true;
                _spacing = 32;
                break;
            case 4:
                _paintLines = false;
                _paintDots = false;
                break;
        }
        refreshEditors();
    }
}
