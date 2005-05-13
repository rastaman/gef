//%1032269233760:org.tigris.gef.xml.pgml%
//Copyright (c) 1996-99 The Regents of the University of California. All
//Rights Reserved. Permission to use, copy, modify, and distribute this
//software and its documentation without fee, and without a written
//agreement is hereby granted, provided that the above copyright notice
//and this paragraph appear in all copies.  This software program and
//documentation are copyrighted by The Regents of the University of
//California. The software program and documentation are supplied "AS
//IS", without any accompanying services from The Regents. The Regents
//does not warrant that the operation of the program will be
//uninterrupted or error-free. The end-user understands that the program
//was developed for research purposes and is advised not to rely
//exclusively on the program for any reason.  IN NO EVENT SHALL THE
//UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
//SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
//ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
//THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
//SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
//WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
//MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
//PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
//CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
//UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

package org.tigris.gef.persistence.pgml;

import java.awt.Color;

import org.tigris.gef.presentation.Fig;
import org.tigris.gef.util.ColorFactory;

/**
 * Utility methods referred to by PGML.tee
 * 
 * @since 0.11.1 12-May-2005
 * @author Bob Tarling
 * @stereotype utility
 */
public class PgmlUtility {

    /**
     * Get the PGML description of a color. If possible this is a text
     * description otherwise it is in red green blue integer format seperated
     * by spaces.
     * 
     * @param color The color to convert to PGML style
     * @return a string representing the color in pgml format
     */
    public static String toString(Color color) {
        String colorDescr = getColorName(color);
        if (colorDescr != null) {
            return colorDescr;
        }
        return color.getRed() + " " + color.getGreen() + " " + color.getBlue();
    }
    
    /**
     * Get a color name for a color or null if this is some custom color.
     * @param color
     * @return the color name or null.
     */
    private static String getColorName(Color color) {
        
        String colorName = null;
        
        if (color.equals(Color.white)) {
            colorName = "white";
        } else if(color.equals(Color.black)) {
            colorName = "black";
        } else if(color.equals(Color.red)) {
            colorName = "red";
        } else if(color.equals(Color.green)) {
            colorName = "green";
        } else if(color.equals(Color.blue)) {
            colorName = "blue";
        }
        
        return colorName;
    }
    
    /**
     * Translate the visibility flag of a Fig to the PGML "shown" attribute
     * value.
     * 
     * @param f The Fig
     * @return 0=hidden, 1=shown
     */
    public static int getVisibility(Fig f) {
        return (f.isVisible()) ?  1 : 0;
    }
    
    /**
     * Translate the dashed flag of a Fig to the PGML "dashed" attribute
     * value.
     * 
     * @param f The Fig
     * @return 0=not dashed, 1=dashed
     */
    public static int getDashed(Fig f) {
        return (f.getDashed()) ?  1 : 0;
    }
    
    /**
     * Translate the filled flag of a Fig to the PGML "filled" attribute
     * value.
     * 
     * @param f The Fig
     * @return 0=not filled, 1=filled
     */
    public static int getFilled(Fig f) {
        return (f.getFilled()) ?  1 : 0;
    }
    
    /**
     * Output the class name and bounds of a Fig.
     * An empty bounds indicates an invisible Fig.
     * 
     * @param f The Fig
     * @return the class name and bounds.
     */
    public static String classNameAndBounds(Fig f) {
        if (f.isVisible()) {
            return f.getClass().getName() + "[" + f.getX() + ", " + f.getY() + ", " + f.getWidth() + ", " + f.getHeight() + "]";
        } else {
            return f.getClass().getName() + "[]";
        }
    }
}
