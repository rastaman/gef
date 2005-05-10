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

package org.tigris.gef.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * A flyweight factory class used to get color instances. This only creates new
 * instances of a Color if required. Previous instances are cached.
 * 
 * @since 0.11.1 10-May-2005
 * @author Bob Tarling
 * @stereotype utility
 */
public class ColorUtility {

    /**
     * A map of previously created colors mapped by an
     * RGB string description in the form "rrr ggg bbb"
     * where rrr = red value int ggg = green value int
     * and bbb = blue value int.
     */
    private static final Map USED_COLORS_BY_RGB_STRING = new HashMap();
    private static final Map USED_COLORS_BY_RGB_INTEGER = new HashMap();
    
    static {
        cacheColor(Color.white);
        cacheColor(Color.black);
        cacheColor(Color.red);
        cacheColor(Color.green);
        cacheColor(Color.blue);
    }

    /**
     * A utility
     */
    private ColorUtility() {
    }
    
    /**
     * A flyweight factory method for reusing the same Color
     * value multiple times.
     * @param rgb A string of RGB values seperated by space
     * or a color name recognised by PGML (later to include SVG)
     * @param defaultColor a color to return if the color description can't be
     * interpretted.
     * @return the equivilent Color
     */
    public static Color getColor(String colorDescr, Color defaultColor) {
        Color color = getColor(colorDescr);
        
        if (color != null) {
            return color;
        }
        
        return defaultColor;
    }
    
    /**
     * A flyweight factory method for reusing the same Color
     * value multiple times.
     * @param rgb A string of RGB values seperated by space
     * or a color name recognised by PGML (later to include SVG)
     * @return the equivilent Color
     */
    public static Color getColor(String colorDescr) {
        Color color = null;
        if (colorDescr.equalsIgnoreCase("white")) {
            color = Color.white;
        } else if(colorDescr.equalsIgnoreCase("black")) {
            color = Color.black;
        } else if(colorDescr.equalsIgnoreCase("red")) {
            color = Color.red;
        } else if(colorDescr.equalsIgnoreCase("green")) {
            color = Color.green;
        } else if(colorDescr.equalsIgnoreCase("blue")) {
            color = Color.blue;
        } else if (colorDescr.indexOf(' ') > 0) {
            // If there any spaces we assume this is a space
            // seperate string of RGB values
            color = getColorByRgb(colorDescr);
        } else {
            // Otherwise we assume its a single integer value
            color = getColorByRgb(Integer.valueOf(colorDescr));
        }
        return color;
    }

    /**
     * Get a color name for a color or null if this is some custom color.
     * @param color
     * @return the color name or null.
     */
    public static String getColorName(Color color) {
        
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
     * Get the PGML description of a color. If possible this is a text
     * description otherwise it is in red green blue integer format seperated
     * by spaces.
     * 
     * @param color The color to convert to PGML style
     * @return a string representing the color in pgml format
     */
    public static String getPgmlColor(Color color) {
        String colorDescr = getColorName(color);
        if (colorDescr != null) {
            return colorDescr;
        }
        return color.getRed() + " " + color.getGreen() + " " + color.getBlue();
    }

    /**
     * Get the SVG description of a color. If possible this is a text
     * description otherwise it is in hexadecimal red green blue format of
     * either #rrggbb or #rgb.
     * 
     * @param color The color to convert to SVG style
     * @return a string representing the color in SVG format
     */
    public static String getSvgColor(Color color) {
        String colorDescr = getColorName(color);
        if (colorDescr != null) {
            return colorDescr;
        }
        
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        if (red < 15 && green < 15 && blue < 15) {
            return "#" + Integer.toHexString(color.getRed())
                 + " " + Integer.toHexString(color.getGreen())
                 + " " + Integer.toHexString(color.getBlue());
        }
        return "#" + colorToHex(color.getRed())
             + " " + colorToHex(color.getGreen())
             + " " + colorToHex(color.getBlue());
    }

    /**
     * Convert an integer representing a color scale to 2 hex digits.
     * @param integer
     * @return
     */
    private static String colorToHex(int integer) {
        if (integer > 255) {
            throw new IllegalArgumentException(
                    "Color value cannot be great than 255");
        }
        if (integer > 15) {
            return Integer.toHexString(integer);
        }
        
        return "0" + Integer.toHexString(integer);
    }

    /**
     * Get a color based on a space seperated RGB string.
     * @param colorDescr an RGB description of the color as integers seperated
     * by spaces.
     * @return the required Color object.
     */
    private static Color getColorByRgb(String colorDescr) {
        Color color = (Color)USED_COLORS_BY_RGB_STRING.get(colorDescr);
        if (color == null) {
            StringTokenizer st = new StringTokenizer(colorDescr, " ");
            int red = Integer.parseInt(st.nextToken());
            int green = Integer.parseInt(st.nextToken());
            int blue = Integer.parseInt(st.nextToken());
            color = new Color(red, green, blue);
            cacheColor(colorDescr, color);
        }
        
        return color;
    }

    /**
     * Get a color based on a single RGB integer.
     * @param rgbInteger the integer value of the color.
     * @return the required Color object.
     */
    private static Color getColorByRgb(Integer rgbInteger) {
        Color color = (Color)USED_COLORS_BY_RGB_INTEGER.get(rgbInteger);
        if (color == null) {
            color = Color.decode(rgbInteger.toString());
            cacheColor(rgbInteger, color);
        }
        
        return color;
    }
    
    /**
     * Cache a Color the indexes will be deduced.
     * @param stringIndex
     * @param color
     */
    private static void cacheColor(Color color) {
        cacheColor(Integer.valueOf(color.getRGB()), color);
    }
    
    /**
     * Cache a Color providing the RGB string by which it can be retrieved
     * @param stringIndex
     * @param color
     */
    private static void cacheColor(String stringIndex, Color color) {
        cacheColor(stringIndex, Integer.valueOf(color.getRGB()), color);
    }
    
    /**
     * Cache a Color providing the RGB integer by which it can be retrieved
     * @param intIndex
     * @param color
     */
    private static void cacheColor(Integer intIndex, Color color) {
        cacheColor(color.getRed() + " " + color.getGreen() + " " + color.getBlue(), intIndex, color);
    }

    /**
     * Cache a Color providing all the indexes by which it can be retrieved
     * @param stringIndex
     * @param intIndex
     * @param color
     */
    private static void cacheColor(String stringIndex, Integer intIndex, Color color) {
        USED_COLORS_BY_RGB_INTEGER.put(intIndex, color);
        USED_COLORS_BY_RGB_STRING.put(stringIndex, color);
    }
}
