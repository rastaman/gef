package org.tigris.gef.presentation;

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

// File: FigGroup.java
// Classes: FigGroup
// Original Author: jrobbins@ics.uci.edu
// $Id$

import java.awt.*;
import java.util.*;
import java.util.List;

import org.apache.commons.logging.*;
import org.apache.commons.logging.impl.*;

/** 
 * A FigGroup is a collection of Figs to all be treated as a single item 
 * @author Jason Robbins
 */

public class FigGroup extends Fig {

    ////////////////////////////////////////////////////////////////
    // instance variables

    /** 
     * The Fig's contained in this FigGroup  
     */
    ArrayList figs;
    
    /**
     * @deprecated visibility in 0.10.5 use getters and setters 
     */
    protected int _extraFrameSpace = 0;
    
    /** Color of the actual text characters. */
    private Color textColor = Color.black;

    /** Color to be drawn behind the actual text characters. Note that
     *  this will be a smaller area than the bounding box which is
     *  filled with FillColor. */
    private Color textFillColor = Color.white;

    /** True if the area behind individual characters is to be filled
     *  with TextColor. */
    private boolean textFilled = false;


    /**
     * The String of figs that are dynamically generated.
     * @deprecated in 0.11 this is not used
     */
    public String _dynObjects;

    /**
     * Normally the bounds of the FigGroup is calculated whenever
     * a Fig is added. Setting this flag to false allows multiple
     * adds without the overhead of the calculation each time.
     */
    private boolean suppressCalcBounds;

    private static Log log = LogFactory.getLog(FigGroup.class);
    
    ////////////////////////////////////////////////////////////////
    // constructors

    /** Construct a new FigGroup that holds no Figs. */
    public FigGroup() {
        super();
        figs = new ArrayList();
    }

    /** Construct a new FigGroup that holds the given Figs. */
   public FigGroup(List figs) {
        super();
        this.figs = new ArrayList(figs);
        calcBounds();
    }

    /**
     * Empty method. Every FigGroup that generates new
     * elements dynamically has to overwrite this method
     * for loading this figure.
     * TODO this needs further explanation. It has some
     * connection with PGMLParser and SVGParser
     */
    public void parseDynObjects(String dynStr) {
    }

    /**
     * Add a Fig to the group.
     * Takes no action if already part of the group.
     * Removes from any other group it may have been in already.
     * New Figs are added on the top.
     * @param fig the Fig to add to this group
     */
    public void addFig(Fig fig) {
        Fig group = fig.getGroup();
        if (group != this) {
            if (group != null) {
                ((FigGroup)group).removeFig(fig);
            }
            this.figs.add(fig);
            fig.setGroup(this);
            calcBounds();
        }
    }

    /**
     * Add a collection of figs to the group.
     * @param figs Collection of figs to be added.
     */
    public void addFigs(Collection figs) {
        Iterator figIter = figs.iterator();
        while(figIter.hasNext()) {
            addFig((Fig)figIter.next());
        }
        calcBounds();
    }

    /**
     * Sets a new collection of figs. The old collection is
     * removed.
     * @param figs Collection of figs to be set.
     */
    public void setFigs(Collection figs) {
        this.figs.clear();
        addFigs(figs);
    }

    /**
     * Accumulate a bounding box for all the Figs in the group.
     * This method is called by many parts of the framework and may
     * cause some performance problems. It is possible to suppress
     * this mthod by calling suppressCalcBounds(true). Be sure to
     * call calcBounds as soon as this suppression is turned off
     * again.
     */
    public void calcBounds() {
        if (suppressCalcBounds) {
            return;
        }
        Rectangle boundingBox = null;

        int figCount = this.figs.size();

        Fig f;
        for(int figIndex = 0; figIndex < figCount; ++figIndex) {
            f = (Fig)this.figs.get(figIndex);
            if(f.isVisible()) {
                if(boundingBox == null) {
                    boundingBox = f.getBounds();
                }
                else {
                    boundingBox.add(getSubFigBounds(f));
                }
            }
        }

        if(boundingBox == null) {
            boundingBox = new Rectangle(0, 0, 0, 0);
        }

        _x = boundingBox.x;
        _y = boundingBox.y;
        _w = boundingBox.width;
        _h = boundingBox.height + _extraFrameSpace;
    }

    /**
     * Returns the bounds of the given subfig. This method can be overwritten
     * in order to use different strategies on determining the overall bounds
     * of the FigGroup.
     *
     * @param subFig Subfig of this group to calculate the bounds for.
     * @return Rectangle representing the bounds of the subfig.
     */
    protected Rectangle getSubFigBounds(Fig subFig) {
        return subFig.getBounds();
    }

    public Object clone() {
        FigGroup figClone = (FigGroup)super.clone();
        int figCount = this.figs.size();
        ArrayList figsClone = new ArrayList(figCount);
        for(int i = 0; i < figCount; ++i) {
            Fig tempFig = (Fig)this.figs.get(i);
            Fig tempFigClone = (Fig)tempFig.clone();
            figsClone.add(tempFigClone);
            tempFigClone.setGroup(figClone);
        }

        figClone.figs = figsClone;
        return figClone;
    }

    /** Returns true if any Fig in the group contains the given point. */
    public boolean contains(int x, int y) {
        return hitFig(new Rectangle(x, y, 0, 0)) != null;
    }
    ////////////////////////////////////////////////////////////////
    // accessors

    /** Reply an Enumeration of the Figs contained in this FigGroup.
     * @depreacted 0.10.5 use iterator()
     */
    public Enumeration elements() {
        return new Vector(this.figs).elements();
    }

    /** Reply an Iterator of the Figs contained in this FigGroup. */
    public Iterator iterator() {
        return this.figs.iterator();
    }

    /** Get the figs that make up this group
     * @param c a collection to populate with the figs
     * @return the figs of this group added to the given collection
     * @deprecated use getFigs()
     */
    public Collection getFigs(Collection c) {
        if (c == null) return this.figs;
        c.addAll(this.figs);
        return c;
    }

    /**
     * Get the fig within this group with the given index position
     * @param i position of fig within this group
     */
    public Fig getFigAt(int i) {
        return (Fig)this.figs.get(i);
    }

    /** Get the figs that make up this group
     * @return the figs of this group
     * USED BY PGML.tee
     */
    public List getFigs() {
        return (List)this.figs.clone();
    }

//    public Color getFillColor() {
//        if(this.figs.size() == 0)
//            return super.getFillColor();
//        return ((Fig)this.figs.get(this.figs.size() - 1)).getFillColor();
//    }
//
//    public boolean getFilled() {
//        if(this.figs.size() == 0)
//            return super.getFilled();
//        return ((Fig)this.figs.get(this.figs.size() - 1)).getFilled();
//    }
//
    public Font getFont() {
        int size = this.figs.size();
        for(int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if(ft instanceof FigText)
                return ((FigText)ft).getFont();
        }
        return null;
    }

    public String getFontFamily() {
        int size = this.figs.size();
        for(int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if(ft instanceof FigText)
                return ((FigText)ft).getFontFamily();
        }
        return "Serif";
    }

    public int getFontSize() {
        int size = this.figs.size();
        for(int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if(ft instanceof FigText)
                return ((FigText)ft).getFontSize();
        }
        return 10;
    }

//    public Color getLineColor() {
//        if(this.figs.size() == 0)
//            return super.getLineColor();
//        return ((Fig)this.figs.get(this.figs.size() - 1)).getLineColor();
//    }
//
//    public int getLineWidth() {
//        if(this.figs.size() == 0)
//            return super.getLineWidth();
//        return ((Fig)this.figs.get(this.figs.size() - 1)).getLineWidth();
//    }

    /**
     * TODO document
     * Used in SVG.TEE
     */
    public String getPrivateData() {
        Fig enc = getEnclosingFig();
        if(enc != null) {
            return "enclosingFig=\"" + enc.getId() + "\"";
        }

        return "";
    }

    /**
     * Returns the extra space that is added the frame surrounding the elements
     *
     * @return num of pixel used for extra spacing.
     */
    public int getExtraFrameSpace() {
        return _extraFrameSpace;
    }

    /** Returns true if any Fig in the group hits the given rect. */
    public boolean hit(Rectangle r) {
        return hitFig(r) != null;
    }
    ////////////////////////////////////////////////////////////////
    // Fig API

    /**
     * Retrieve the top-most Fig containing the given point, or null.
     * Needs-More-Work: just do a linear search.  Later, optimize this
     * routine using Quad Trees (or other) techniques.
     * Always returns false if this FigGroup is invisible.
     */
    public Fig hitFig(Rectangle r) {
        if (!isVisible()) return null;
        Fig res = null;
        int figCount = this.figs.size();
        for(int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig f = (Fig)this.figs.get(figIndex);
            if(f.hit(r))
                res = f;
        }

        return res;
    }

    /** Groups are resizable by default (see super class),
     *  but not reshapable, and not rotatable (for now).
     */
    public boolean isReshapable() {
        return false;
    }

    /** Groups are resizable by default (see super class),
     *  but not reshapable, and not rotatable (for now).
     */
    public boolean isRotatable() {
        return false;
    }
    ////////////////////////////////////////////////////////////////
    // display methods

    /** Paint all the Figs in this group. */
    public void paint(Graphics g) {
    	if (isVisible()) {
			int figCount = this.figs.size();
			for (int figIndex = 0; figIndex < figCount; ++figIndex) {
				Fig f = (Fig)this.figs.get(figIndex);
				if (f.isVisible()) {
					f.paint(g);
				}
			}
    	}
    }

    /** Delete all Fig's from the group. Fires PropertyChange with "bounds".*/
    public void removeAll() {
        Rectangle oldBounds = getBounds();
        int figCount = this.figs.size();
        for(int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig f = (Fig)this.figs.get(figIndex);
            f.setGroup(null);
        }
        this.figs.clear();
        calcBounds();
        firePropChange("bounds", oldBounds, getBounds());
    }

    /** Remove a Fig from the group. Fires PropertyChange with "bounds". */
    public void removeFig(Fig f) {
        if(!this.figs.contains(f))
            return;
        Rectangle oldBounds = getBounds();
        this.figs.remove(f);
        f.setGroup(null);
        calcBounds();
        firePropChange("bounds", oldBounds, getBounds());
    }

    /** Returns a list of the displayable Figs enclosed.
     *  e.g. returns the list of enclosed Figs, without
     *  the Compartments that should not be displayed.
     */
    public Collection getDisplayedFigs(Collection c) {
        if (c == null) c = new ArrayList();

        int figCount = this.figs.size();
        for(int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig f = (Fig)this.figs.get(figIndex);
            if(f.isVisible()) {
                c.add(f);
            }
        }

        return c;
    }

    /**
     * Set the bounding box to the given rect. Figs in the group are
     * scaled to fit. Fires PropertyChange with "bounds"
     * @param x new X co ordinate for fig
     * @param y new Y co ordinate for fig
     * @param w new width for fig
     * @param h new height for fig
     */
    public void setBounds(int x, int y, int w, int h) {
        Rectangle oldBounds = getBounds();
        int figCount = this.figs.size();
        for(int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig f = (Fig)this.figs.get(figIndex);
            if (f.isVisible()) {
                int newW = (_w == 0) ? 0 : (f.getWidth() * w) / _w;
                int newH = (_h == 0) ? 0 : (f.getHeight() * h) / _h;
                int newX = (_w == 0) ? x : x + ((f.getX() - _x) * w) / _w;
                int newY = (_h == 0) ? y : y + ((f.getY() - _y) * h) / _h;
                f.setBounds(newX, newY, newW, newH);
            }
        }
        calcBounds(); //_x = x; _y = y; _w = w; _h = h;
        firePropChange("bounds", oldBounds, getBounds());
    }
    
    /** Set the Figs in this group. Fires PropertyChange with "bounds". */
    public void setFigs(List figs) {
        Rectangle oldBounds = getBounds();
        this.figs = new ArrayList(figs);
        calcBounds();
        firePropChange("bounds", oldBounds, getBounds());
    }

    public void setFillColor(Color col) {
        int size = this.figs.size();
        for(int i = 0; i < size; i++) {
            ((Fig)this.figs.get(i)).setFillColor(col);
        }
        super.setFillColor(col);
    }

    public void setFilled(boolean f) {
        int size = this.figs.size();
        for(int i = 0; i < size; i++) {
            ((Fig)this.figs.get(i)).setFilled(f);
        }
        super.setFilled(f);
    }

    public void setFont(Font f) {
        int size = this.figs.size();
        for(int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if(ft instanceof FigText) {
                ((FigText)ft).setFont(f);
            }
        }
    }

    public void setFontFamily(String s) {
        int size = this.figs.size();
        for(int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if(ft instanceof FigText) {
                ((FigText)ft).setFontFamily(s);
            }
        }
    }

    public void setFontSize(int s) {
        int size = this.figs.size();
        for(int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if(ft instanceof FigText)
                ((FigText)ft).setFontSize(s);
        }
    }
    ////////////////////////////////////////////////////////////////
    // Fig Accessors

    public void setLineColor(Color col) {
        int size = this.figs.size();
        for(int i = 0; i < size; i++) {
            ((Fig)this.figs.get(i)).setLineColor(col);
        }
        super.setLineColor(col);
    }

    public void setLineWidth(int w) {
        int size = this.figs.size();
        for(int i = 0; i < size; i++) {
            ((Fig)this.figs.get(i)).setLineWidth(w);
        }
        super.setLineWidth(w);
    }

    public void setPrivateData(String data) {
        StringTokenizer tokenizer = new StringTokenizer(data, "=\"' ");

        while(tokenizer.hasMoreTokens()) {
            String tok = tokenizer.nextToken();
            if(tok.equals("enclosingFig")) {
                String s = tokenizer.nextToken();
                Integer.parseInt(s);
            }
            else {
                /* Unknown value */
            }
        }
    }
    ////////////////////////////////////////////////////////////////
    // FigText Accessors

    public void setTextColor(Color c) {
        firePropChange("textColor", textColor, c);
        int size = this.figs.size();
        for(int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if(ft instanceof FigText) {
                ((FigText)ft).setTextColor(c);
            } else if(ft instanceof FigGroup) {
                ((FigGroup)ft).setTextColor(c);
            }
        }
        textColor = c;
    }

    public void setTextFillColor(Color c) {
        firePropChange("textFillColor", textFillColor, c);
        int size = this.figs.size();
        for(int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if(ft instanceof FigText) {
                ((FigText)ft).setTextFillColor(c);
            } else if(ft instanceof FigGroup) {
                ((FigGroup)ft).setTextFillColor(c);
            }
        }
        textFillColor = c;
    }

    public void setTextFilled(boolean b) {
        firePropChange("textFilled", textFilled, b);
        int size = this.figs.size();
        for(int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if (ft instanceof FigText) {
                ((FigText)ft).setTextFilled(b);
            } else if (ft instanceof FigGroup) {
                ((FigGroup)ft).setTextFilled(b);
            }
        }
        textFilled = b;
    }

    public Color getTextColor() {
        return textColor;
    }

    public Color getTextFillColor() {
        return textFillColor;
    }

    public boolean getTextFilled() {
        return textFilled;
    }

    /**
     * Sets the extra spacing for the frame around the elements
     *
     * @param extraSpace Num of pixels added as additional spacing
     */
    public void setExtraFrameSpace(int extraSpace) {
        _extraFrameSpace = extraSpace;
    }

    /** Translate all the Fig in the list by the given offset. */
    public void translate(int dx, int dy) {
        Rectangle oldBounds = getBounds();
        int figCount = this.figs.size();
        for(int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig f = (Fig)this.figs.get(figIndex);
            f.translate(dx, dy);
        }
        _x += dx;
        _y += dy; // no need to call calcBounds();
        firePropChange("bounds", oldBounds, getBounds());
    }

    /**
     * Let the group decide if it should be selected itself or the hitten
     * grouped fig instead.
     * @param hitRect Rectangle surrounding the current mouse position.
     * @return The fig that should be selected.
     */
    public Fig deepSelect(Rectangle hitRect) {
        return this;
    }

    /**
     * Returns the size of the group containing all entries.
     * @return Dimension representing the size of the group.
     */
    public Dimension getSize() {
        return new Dimension(_w, _h);
    }
    
    private FigGroup getTopGroup() {
        FigGroup topGroup = this;
        while (topGroup.getGroup() != null) {
            topGroup = (FigGroup) topGroup.getGroup();
        }
        return topGroup;
    }
} /* end class FigGroup */
