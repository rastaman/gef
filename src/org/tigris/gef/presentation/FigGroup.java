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

/** This class implements a group of Figs. */

public class FigGroup extends Fig {

    ////////////////////////////////////////////////////////////////
    // instance variables

    /** 
     * The Fig's contained in this FigGroup  
     */
    List figs;
    
    /** @deprecated - use getExtraFrameSpace() 
     * will change visibilty in 0.12
     */
    protected int _extraFrameSpace = 0;

    /** The String of figs that are dynamically
     generated. */
    public String _dynObjects;

    ////////////////////////////////////////////////////////////////
    // constructors

    /** Construct a new FigGroup that holds no Figs. */
    public FigGroup() {
        super();
        figs = new ArrayList();
    }

    /** Construct a new FigGroup that holds the given Figs. 
     * @deprecated in favour of FigGroup(List)
     */
    public FigGroup(Vector figs) {
        super();
        this.figs = figs;
        calcBounds();
    }

    /** Construct a new FigGroup that holds the given Figs. */
   public FigGroup(List figs) {
        super();
        this.figs = figs;
        calcBounds();
    }

    /** Empty method. Every figgroup that generates new
     elements dynamically has to overwrite this method
     for loading this figure. */
    public void parseDynObjects(String dynStr) {
    }

    /** Add a Fig to the group. Takes no action if already part of the group.
     * Removes from any other group. New Figs are added on the top.
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
        Iterator figIter = figs.iterator();
        while(figIter.hasNext()) {
            addFig((Fig)figIter.next());
        }
        calcBounds();
    }

    /** Accumulate a bounding box for all the Figs in the group. */
    public void calcBounds() {
        Rectangle bbox = null;

        int figCount = this.figs.size();

        for(int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig f = (Fig)this.figs.get(figIndex);
            if(f.isDisplayed()) {
                if(bbox == null) {
                    bbox = f.getBounds();
                }
                else {
                    bbox.add(getSubFigBounds(f));
                }
            }
        }

        if(bbox == null) {
            bbox = new Rectangle(0, 0, 0, 0);
        }

        _x = bbox.x;
        _y = bbox.y;
        _w = bbox.width;
        _h = bbox.height + _extraFrameSpace;
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
        List figsClone = new ArrayList(figCount);
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
     * @depreacted 0.10 this will be replaced in 0.11 in favour of iterator()
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
     * @deprecated 0.10 this will removed for release 0.11
     */
    public Vector getFigs() {
        Vector v = new Vector();
        v.addAll(this.figs);
        return v;
    }

    public Color getFillColor() {
        if(this.figs.size() == 0)
            return super.getFillColor();
        return ((Fig)this.figs.get(this.figs.size() - 1)).getFillColor();
    }

    public boolean getFilled() {
        if(this.figs.size() == 0)
            return super.getFilled();
        return ((Fig)this.figs.get(this.figs.size() - 1)).getFilled();
    }

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

    public Color getLineColor() {
        if(this.figs.size() == 0)
            return super.getLineColor();
        return ((Fig)this.figs.get(this.figs.size() - 1)).getLineColor();
    }

    public int getLineWidth() {
        if(this.figs.size() == 0)
            return super.getLineWidth();
        return ((Fig)this.figs.get(this.figs.size() - 1)).getLineWidth();
    }

    public String getPrivateData() {
        Fig enc = getEnclosingFig();
        if(enc != null) {
            return "enclosingFig=\"" + enc.getId() + "\"";
        }

        return "";
    }

    public Color getTextColor() {
        int size = this.figs.size();
        for(int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if(ft instanceof FigText)
                return ((FigText)ft).getTextColor();
        }
        return null;
    }

    public Color getTextFillColor() {
        int size = this.figs.size();
        for(int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if(ft instanceof FigText)
                return ((FigText)ft).getTextFillColor();
        }
        return null;
    }

    public boolean getTextFilled() {
        int size = this.figs.size();
        for(int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if(ft instanceof FigText)
                return ((FigText)ft).getTextFilled();
        }
        return false;
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

    /** Retrieve the top-most Fig containing the given point, or null.
     *  Needs-More-Work: just do a linear search.  Later, optimize this
     *  routine using Quad Trees (or other) techniques. */

    public Fig hitFig(Rectangle r) {
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
        int figCount = this.figs.size();
        for(int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig f = (Fig)this.figs.get(figIndex);
            if(f.isDisplayed())
                f.paint(g);
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
            if(f.isDisplayed()) {
                c.add(f);
            }
        }

        return c;
    }

    /** Set the bounding box to the given rect. Figs in the group are
     *  scaled to fit. Fires PropertyChange with "bounds"
     */
    public void setBounds(int x, int y, int w, int h) {
        Rectangle oldBounds = getBounds();
        int figCount = this.figs.size();
        for(int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig f = (Fig)this.figs.get(figIndex);
            if (f.isDisplayed()) {
                int newW = f.getWidth();
                int newH = f.getHeight();
                int newX = f.getX();
                int newY = f.getY();
                if (f.isResizable()) {
                    newW = (_w == 0) ? 0 : (f.getWidth() * w) / _w;
                    newH = (_h == 0) ? 0 : (f.getHeight() * h) / _h;
                }
                if (f.isMovable()) {
                    newX = (_w == 0) ? x : x + ((f.getX() - _x) * w) / _w;
                    newY = (_h == 0) ? y : y + ((f.getY() - _y) * h) / _h;
                }
                if (f.isMovable() || f.isResizable()) {
                    f.setBounds(newX, newY, newW, newH);
                }
            }
        }
        calcBounds(); //_x = x; _y = y; _w = w; _h = h;
        firePropChange("bounds", oldBounds, getBounds());
    }

    /** Set the Vector of Figs in this group. Fires PropertyChange with "bounds". */
    public void setFigs(Vector figs) {
        Rectangle oldBounds = getBounds();
        this.figs = figs;
        calcBounds();
        firePropChange("bounds", oldBounds, getBounds());
    }

    public void setFillColor(Color col) {
        int size = this.figs.size();
        for(int i = 0; i < size; i++)
            ((Fig)this.figs.get(i)).setFillColor(col);
    }

    public void setFilled(boolean f) {
        int size = this.figs.size();
        for(int i = 0; i < size; i++)
            ((Fig)this.figs.get(i)).setFilled(f);
    }

    public void setFont(Font f) {
        int size = this.figs.size();
        for(int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if(ft instanceof FigText)
                ((FigText)ft).setFont(f);
        }
    }

    public void setFontFamily(String s) {
        int size = this.figs.size();
        for(int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if(ft instanceof FigText)
                ((FigText)ft).setFontFamily(s);
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
        for(int i = 0; i < size; i++)
            ((Fig)this.figs.get(i)).setLineColor(col);
    }

    public void setLineWidth(int w) {
        int size = this.figs.size();
        for(int i = 0; i < size; i++)
            ((Fig)this.figs.get(i)).setLineWidth(w);
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
        int size = this.figs.size();
        for(int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if(ft instanceof FigText)
                ((FigText)ft).setTextColor(c);
        }
    }

    public void setTextFillColor(Color c) {
        int size = this.figs.size();
        for(int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if(ft instanceof FigText)
                ((FigText)ft).setTextFillColor(c);
        }
    }

    public void setTextFilled(boolean b) {
        int size = this.figs.size();
        for(int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if(ft instanceof FigText)
                ((FigText)ft).setTextFilled(b);
        }
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

} /* end class FigGroup */
