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

public class SwtUtil{
    public static Rectangle translateRectangle(java.awt.Rectangle awt)
    {
	return new Rectangle(awt.x, awt.y, awt.width, awt.height); 
    }
    public static Color translateColor(java.awt.Color awt)
    {
	return new Color(awt.getRGB()); 
    }
    public static Dimension translateDimension(java.awt.Dimension awt)
    {
	return new Dimension(awt.width, awt.height); 
    }
    public static Graphics translateGraphics(java.awt.Graphics awt)
    {
	swingwt.awt.Graphics _g;
	java.awt.Rectangle swtRect = awt.getClipBounds();
	_g = new swingwt.awt.Container().getGraphics();
	_g.setClip(swtRect.x, swtRect.y, swtRect.width, swtRect.height);
	_g.setColor(translateColor(awt.getColor()));
	return _g; 
    }
    
}
