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

package org.tigris.gef.plot2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 * A Java2d implementation of the Plotter interface.
 * @author Bob Tarling
 */
public class Java2d implements Plotter {
    
    /** The size of the dashes drawn when the Fig is dashed. */
    private static final String[] DASHED_CHOICES = {"Solid", "Dashed",     "LongDashed",  "Dotted",      "DotDash"};
    private static final float[][] DASH_ARRAYS   = {null,    {5.0f, 5.0f}, {15.0f, 5.0f}, {3.0f, 10.0f}, {3.0f, 6.0f, 10.0f, 6.0f}};  // opaque, transparent, [opaque, transparent]
    private static final int[]     DASH_PERIOD   = {0,        10,           20,            13,            25,                     };  // the sum of each subarray

    /**
     * 
     */
    public Java2d() {
    }
    
    /** Paint this line object. */
    public void drawLine(
            Object graphicsContext, 
            int lineWidth, 
            Color lineColor, 
            int x1, int y1, int x2, int y2, 
            boolean dashed, 
            float[] dashes, int dashPeriod) {
        if (lineWidth <= 0) return;
        
        Graphics g = (Graphics)graphicsContext;
        if (dashed) {
            g.setColor(lineColor);
            drawDashedLine(g, lineWidth, x1, y1, x2, y2, 0, dashes, dashPeriod);
        } else {
            g.setColor(lineColor);
            g.drawLine(x1, y1, x2, y2);
        }
    }
    
    public int drawDashedLine(
            Object graphicsContext, 
            int lineWidth, 
            int x1, int y1, int x2, int y2,
            int phase, 
            float[] dashes, int dashPeriod) {
        if (graphicsContext instanceof Graphics2D) {
            return drawDashedLineG2D(
                    (Graphics2D)graphicsContext, 
                    lineWidth, 
                    phase, 
                    x1, y1, x2, y2, 
                    dashes, dashPeriod);
        }
        Graphics g = (Graphics)graphicsContext;
        // Fall back on the old inefficient method of drawing dashed
        // lines. This is required until SVGWriter is converted to
        // extend Graphics2D
        int segStartX;
        int segStartY;
        int segEndX;
        int segEndY;
        int dxdx = (x2 - x1) * (x2 - x1);
        int dydy = (y2 - y1) * (y2 - y1);
        int length = (int)Math.sqrt(dxdx + dydy);
        int numDashes = dashes.length;
        int d;
        int dashesDist = 0;
        for(d = 0; d < numDashes; d++) {
            dashesDist += dashes[d];
            // find first partial dash?
        }

        d = 0;
        int i = 0;
        while(i < length) {
            segStartX = x1 + ((x2 - x1) * i) / length;
            segStartY = y1 + ((y2 - y1) * i) / length;
            i += dashes[d];
            d = (d + 1) % numDashes;
            if(i >= length) {
                segEndX = x2;
                segEndY = y2;
            }
            else {
                segEndX = x1 + ((x2 - x1) * i) / length;
                segEndY = y1 + ((y2 - y1) * i) / length;
            }

            g.drawLine(segStartX, segStartY, segEndX, segEndY);
            i += dashes[d];
            d = (d + 1) % numDashes;
        }

        // needs-more-work: phase not taken into account
        return (length + phase) % dashesDist;
    }
    
    private int drawDashedLineG2D(Graphics2D g, int lineWidth, int phase, int x1, int y1, int x2, int y2, float[] dashes, int dashPeriod) {
        int dxdx = (x2 - x1) * (x2 - x1);
        int dydy = (y2 - y1) * (y2 - y1);
        int length = (int)(Math.sqrt(dxdx + dydy) + 0.5);       // This causes a smaller rounding error of 0.5pixels max. . Seems acceptable.
        Graphics2D g2D = (Graphics2D)g;
        Stroke  OriginalStroke = g2D.getStroke();               // we need this to restore the original stroke afterwards

        BasicStroke  DashedStroke   = new BasicStroke(lineWidth,   BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, dashes, (float)phase);
        //                                           (float width, int cap,                int join,               float miterlimit, float[] dash, float dash_phase)
        g2D.setStroke(DashedStroke);
        g2D.drawLine(x1, y1, x2, y2);
        g2D.setStroke(OriginalStroke);   // undo the manipulation of g

        return (length + phase) % dashPeriod ;
    }
    
    // Taken from FigCircle
    
    
    public void drawOval(Object graphicsContext, boolean filled, Color fillColor, Color lineColor, int lineWidth, boolean dashed, int x, int y, int w, int h) {
        if(dashed && (graphicsContext instanceof Graphics2D)) {
            Graphics2D g2d = (Graphics2D)graphicsContext;
            Stroke oldStroke = g2d.getStroke();
            float[] dash = {10.0f, 10.0f};
            Stroke stroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dash, 0.0f);
            g2d.setStroke(stroke);
            if(filled && fillColor != null) {
                g2d.setColor(fillColor);
                g2d.fillOval(x, y, w, h);
            }

            if(lineWidth > 0 && lineColor != null) {
                g2d.setColor(lineColor);
                g2d.drawOval(x, y, w - lineWidth, h - lineWidth);
            }

            g2d.setStroke(oldStroke);
        } else if (filled && fillColor != null) {
            Graphics g = (Graphics)graphicsContext;
            if(lineWidth > 0 && lineColor != null) {
                g.setColor(lineColor);
                g.fillOval(x, y, w, h);
            }

            if (!fillColor.equals(lineColor)) {
                g.setColor(fillColor);
                g.fillOval(x + lineWidth, y + lineWidth, w - (lineWidth*2), h - (lineWidth*2));
            }
        } else if(lineWidth > 0 && lineColor != null) {
            Graphics g = (Graphics)graphicsContext;
            g.setColor(lineColor);
            g.drawOval(x, y, w, h);
        }
    }
    
    // Taken from FigRect

    public void drawRect(Object graphicsContext, boolean filled, Color fillColor, int lineWidth, Color lineColor, int x, int y, int w, int h, boolean dashed, float dashes[], int dashPeriod) {
        Graphics g = (Graphics)graphicsContext;
        if (filled && fillColor != null) {
            int xx = x;
            int yy = y;
            int ww = w;
            int hh = h;
            if (lineColor != null) {
                if (lineWidth > 1 && !dashed) {
                    int lineWidth2 = lineWidth*2;
                    g.setColor(lineColor);
                    g.fillRect(xx, yy, ww, hh);
                    xx += lineWidth;
                    yy += lineWidth;
                    ww -= lineWidth2;
                    hh -= lineWidth2;
                }
            }
            g.setColor(fillColor);
            g.fillRect(xx, yy, ww, hh);
            if (lineColor != null) {
                if (lineWidth == 1 || dashed) {
                    paintRectLine(g, xx, yy, ww, hh, lineWidth, lineColor, dashed, dashes, dashPeriod);
                }
            }
        } else {
            paintRectLine(g, x, y, w, h, lineWidth, lineColor, dashed, dashes, dashPeriod);
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
    private void paintRectLine(Graphics g, int x, int y, int w, int h, int lineWidth, Color lineColor, boolean dashed, float dashes[], int dashPeriod) {
        if (lineWidth > 0 && lineColor != null) {
            g.setColor(lineColor);
            if (lineWidth == 1) {
                paintRectLine(g, x, y, w, h, dashed, lineWidth, dashes, dashPeriod);
            } else {
                int xx = x;
                int yy = y;
                int hh = h;
                int ww = w;
                
                for (int i=0; i < lineWidth; ++i) {
                    paintRectLine(g, xx++, yy++, ww, hh, dashed, lineWidth, dashes, dashPeriod);
                    ww -= 2;
                    hh -= 2;
                }
            }
        }
    }
    
    private void paintRectLine(Graphics g, int x, int y, int w, int h, boolean dashed, int lineWidth, float dashes[], int dashPeriod) {
        if (!dashed)
            g.drawRect(x, y, w, h);
        else {
            drawDashedRectangle(g, 0, x, y, w, h, lineWidth, dashes, dashPeriod);
        }
    }
    
    private void drawDashedRectangle(Graphics g, int phase, int x, int y, int w, int h, int lineWidth, float dashes[], int dashPeriod) {
        
        phase = drawDashedLine(g, lineWidth, x, y, x + w, y, phase, dashes, dashPeriod);
        phase = drawDashedLine(g, lineWidth, x + w, y, x + w, y + h, phase, dashes, dashPeriod);
        phase = drawDashedLine(g, lineWidth, x + w, y + h, x, y + h, phase, dashes, dashPeriod);
        phase = drawDashedLine(g, lineWidth, x, y + h, x, y, phase, dashes, dashPeriod);
    }
    
    // From FigCube
    
    public void drawCube(Object graphicContext, Color lineColor, Color fillColor, int x, int y, int w, int h, int d){
        Graphics g = (Graphics)graphicContext;

        g.setColor(fillColor);
        g.fillRect(x, y, w, h);
        g.setColor(lineColor);
        g.drawRect(x, y, w, h);

        g.setColor(fillColor);
        g.fillPolygon(new int[]{x, x+d, x+w+d, x+w}, 
                      new int[]{y, y-d, y-d, y}, 4);
        g.setColor(lineColor);
        g.drawPolygon(new int[]{x, x+d, x+w+d, x+w}, 
                      new int[]{y, y-d, y-d, y}, 4);

        g.setColor(fillColor);
        g.fillPolygon(new int[]{x+w+d, x+w+d, x+w, x+w}, 
                          new int[]{y-d, y+h-d, y+h, y}, 4);
        g.setColor(lineColor);
        g.drawPolygon(new int[]{x+w+d, x+w+d, x+w, x+w}, 
                      new int[]{y-d, y+h-d, y+h, y}, 4);
    }

    /** Paint this FigDiamond */
    public void drawDiamond(
            Object graphicContext, 
            boolean filled, Color fillColor, 
            int lineWidth, Color lineColor, 
            int x, int y, int w, int h) {
        Graphics g = (Graphics)graphicContext;
        int xs[] = new int[4];
        int ys[] = new int[4];
        xs[0] = x + w/2;
        ys[0] = y;
        xs[1] = x + w;
        ys[1] = y + h/2;
        xs[2] = x + w/2;
        ys[2] = y + h;
        xs[3] = x;
        ys[3] = y + h/2;
        if (filled && fillColor != null) {
            g.setColor(fillColor);
            g.fillPolygon(xs, ys, 4);
        }
        if (lineWidth > 0 && lineColor != null) {
            g.setColor(lineColor);
            g.drawPolygon(xs, ys, 4);
        }
    }
    
    public void drawPoly(Object graphicsContext, boolean filled, Color fillColor, int lineWidth, Color lineColor, int pointCount, int xPoints[], int yPoints[], boolean dashed, float dashes[], int dashPeriod) {

        Graphics g = (Graphics)graphicsContext;

        if(filled && fillColor != null) {
            g.setColor(fillColor);
            g.fillPolygon(xPoints, yPoints, pointCount);
        }

        if(lineWidth > 0 && lineColor != null) {
            g.setColor(lineColor);

            if(dashed)
                drawDashedPerimeter(g, lineWidth, pointCount, xPoints, yPoints, dashes, dashPeriod);
            else
                g.drawPolyline(xPoints, yPoints, pointCount);
        }
    }
    
    private void drawDashedPerimeter(Graphics g, int lineWidth, int pointCount, int xPoints[], int yPoints[], float dashes[], int dashPeriod) {
        int phase = 0;

        for(int i = 1; i < pointCount; i++) {
            phase = drawDashedLine(
                    g, 
                    lineWidth, 
                    xPoints[i - 1], yPoints[i - 1], 
                    xPoints[i], yPoints[i],
                    phase, dashes, dashPeriod);
        }
    }
}
