// Copyright (c) 2007 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
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

package org.tigris.gef.graph.presentation;

import org.tigris.gef.base.Editor;
import org.tigris.gef.base.MouseEvent;
import org.tigris.gef.base.PopupMenu;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigPainter;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;

    /**
     * @see java.awt.Component
     */

public interface GraphInternalPane {

    /**
     * @see java.awt.Component#createMouseDragEvent()
     */
    MouseEvent createMouseDragEvent(int x, int y);

    /**
     * @see java.awt.Component#getViewableRect()
     */
    Rectangle getViewableRect();

    /**
     * @see java.awt.Component#revalidate()
     */
    void revalidate();

    /**
     * @see java.awt.Component#setPreferredSize()
     */
    void setPreferredSize(Dimension d);

    /**
     * @see java.awt.Component#repaint()
     */
    void repaint(int x, int y, int width, int height);

    /**
     * @see java.awt.Component#getFileDialog()
     */
    org.tigris.gef.base.FileDialog getFileDialog();

    
    /**
     * @see java.awt.Component#setToolTipText()
     */
    void setToolTipText(String text);

    /**
     * @see java.awt.Component#getGraphBackground()
     */
    Color getGraphBackground();

    /**
     * @see java.awt.Component#createImage()
     */
//    Image createImage(int x, int h);

    /**
     * @see java.awt.Component#scrollRectToVisible()
     */
    void scrollRectToVisible(Rectangle bounds);


    /**
     * @return a created PopupMenu
     */
    PopupMenu createPopupMenu();
    
    /**
     * save the current image to Cilpboard
     * @throws IOException 
     */
    void saveGraphics(OutputStream s, Editor ce, Rectangle drawingArea, int scale) throws IOException; 

    /**
     * @return true if its parent is a Viewport
     */
    boolean isParentViewport();
    
    Dimension getGraphSize();
    
    void setCursor(Cursor cursor);
}