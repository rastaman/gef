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
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;

    /**
     * @see java.swt.Component
     */

public interface GraphInternalPane {

    /**
     * @see java.swt.Component#createMouseDragEvent()
     */
    MouseEvent createMouseDragEvent(int x, int y);

    /**
     * @see java.swt.Component#getViewableRect()
     */
    Rectangle getViewableRect();

    /**
     * @see java.swt.Component#revalidate()
     */
    void revalidate();

    /**
     * @see java.swt.Component#setPreferredSize()
     */
    void setPreferredSize(Dimension d);

    /**
     * @see java.swt.Component#repaint()
     */
    void repaint(int x, int y, int width, int height);

    /**
     * @see java.swt.Component#repaint()
     */
    void repaint(int alpha, int x, int y, int width, int height);

    /**
     * @see java.swt.Component#setCursor()
     */
    void setCursor(Cursor c);

    /**
     * @see java.swt.Component#getFileDialog()
     */
    org.tigris.gef.base.FileDialog getFileDialog();

    /**
     * @see java.swt.Component#getGraphSize()
     */
    Dimension getGraphSize();

    /**
     * @see java.swt.Component#setToolTipText()
     */
    void setToolTipText(String text);

    /**
     * @see java.swt.Component#getGraphBackground()
     */
    Color getGraphBackground();

    /**
     * @see java.swt.Component#createImage()
     */
    Image createImage(int x, int h);

    /**
     * @see java.swt.Component#scrollRectToVisible()
     */
    void scrollRectToVisible(Rectangle bounds);

    /**
     * @see java.swt.Component#getCursor()
     */
    Cursor getCursor();

    /**
     * @see java.swt.Component#getViewPosition()
     */
    Point getViewPosition();

    /**
     * @see java.swt.Component#getExtentSize()
     */
    Dimension getExtentSize();    

    /**
     * @see java.swt.Component#getViewRect()
     */
    Rectangle getViewRect();

    /**
     * @see java.swt.Component#setViewPosition()
     */
    void setViewPosition(Point point);

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
 }