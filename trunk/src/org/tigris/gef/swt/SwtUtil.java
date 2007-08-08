// Copyright (c) 1996-06 The Regents of the University of California. All
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

// File: SwtRectangleWrapper.java
// Classes: SwtRectangleWrapper
// Original Author: johnnycoding@gmail.com

package org.tigris.gef.swt;

import swingwt.awt.*;
import org.jfree.experimental.swt.*;

public class SwtUtil{
    
    private static final int SWINGWT_GRAPHICS = 0;
    private static final int HOLONGATE_GRAPHICS = 1;
    private static final int JFREE_GRAPHICS = 2;
    
    private static final int GRAPHICS_IMPLEMENTATION = JFREE_GRAPHICS;
    
    public static Rectangle translateRectangle(java.awt.Rectangle awt) {
	return new Rectangle(awt.x, awt.y, awt.width, awt.height); 
    }
    
    public static Color translateColor(java.awt.Color awt) {
	return new Color(awt.getRGB()); 
    }
    
    public static Dimension translateDimension(java.awt.Dimension awt) {
	return new Dimension(awt.width, awt.height); 
    }
    
    public static java.awt.Graphics translateGraphics(Graphics swt) {
	// TODO GraphicsTranslator is our own attempt to convert a swingwt Graphics object
	// to an awt Graphics
	if (GRAPHICS_IMPLEMENTATION == SWINGWT_GRAPHICS) {
	    if (swt instanceof Graphics2D) {
		// TODO we need a Graphics2DTranslator also
		return new GraphicsTranslator(swt);
	    } else {
		return new GraphicsTranslator(swt);
	    }
	} else if (GRAPHICS_IMPLEMENTATION == HOLONGATE_GRAPHICS) {
	    return null;
	} else if (GRAPHICS_IMPLEMENTATION == JFREE_GRAPHICS) {
	    if (swt instanceof SWTGraphics2DRenderer) {
		SWTGraphics2DRenderer render = (SWTGraphics2DRenderer)swt;
		SWTGraphics2D swtGraphics2D = new SWTGraphics2D(render.getSWTGC());
		return swtGraphics2D;
	    }
	    return null;
	} else {
	    return null;
	}
    }
}
