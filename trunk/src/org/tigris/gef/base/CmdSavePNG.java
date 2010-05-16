//Copyright (c) 2004-2006 The Regents of the University of California. All
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

//File: CmdSavePNG.java
//Classes: CmdSavePNG

package org.tigris.gef.base;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

/**
 * This is a rewrite of CmdSaveGIF to use the JDK 1.4 ImageIO library to write
 * PNG files, with both better performance and memory efficiency. Unfortunately
 * though, this is only available to those with JRE1.4 and above.
 * 
 * @deprecated in 0.12.3 use SavePNGAction
 */
public class CmdSavePNG extends CmdSaveGraphics {

    private static final long serialVersionUID = 2694114560467440132L;

    /**
     * Used as background color in image and set transparent. Chosen because
     * it's unlikely to be selected by the user, and leaves the diagram readable
     * if viewed without transparency.
     */
    public static final int TRANSPARENT_BG_COLOR = 0x00efefef;

    public CmdSavePNG() {
        super("SavePNG");
    }

    /**
     * Write the diagram contained by the current editor into an OutputStream as
     * a PNG image.
     */
    protected void saveGraphics(OutputStream s, Editor ce, Rectangle drawingArea)
            throws IOException {

        // Create an offscreen image and render the diagram into it.
        Image i = new BufferedImage(drawingArea.width * scale,
                drawingArea.height * scale, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) i.getGraphics();
        g.scale(scale, scale);
        g.setColor(new Color(TRANSPARENT_BG_COLOR, true));
        Composite c = g.getComposite();
        g.setComposite(AlphaComposite.Src);
        g.fillRect(0, 0, drawingArea.width * scale, drawingArea.height * scale);
        g.setComposite(c);
        // a little extra won't hurt
        g.translate(-drawingArea.x, -drawingArea.y);
        ce.print(g);

        ImageIO.write((RenderedImage) i, "png", s);

        g.dispose();
        // force garbage collection, to prevent out of memory exceptions
        g = null;
        i = null;
    }

} /* end class CmdSavePNG */