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

// File: JPopupMenu.java
// Classes: JPopupMenu
// Original Author: johnnycoding 1996
// $Id: JPopupMenu.java 1019 2007-06-13 14:21:52Z johnnycoding $

package org.tigris.gef.swt;

import swingwt.awt.*;

/**
 * @see swingwtx.swing.JFileDialog
 * @see orgtigris.gef.base.FileDialog
 */

public class JFileDialog extends FileDialog implements org.tigris.gef.base.FileDialog {

    /*
     * @see org.tigris.gef.base.FileDialog#JFileDialog()
     */
    public JFileDialog(Frame parent) {
        super(parent);
    }

    /*
     * @see org.tigris.gef.base.FileDialog#setFilenameFilter()
     */
    public void setFilenameFilter(java.io.FileFilter ff) {
        super.setFilenameFilter(ff);
    }

    /*
     * @see org.tigris.gef.base.FileDialog#setFilenameFilter()
     */
    public void setFilenameFilter(java.io.FilenameFilter ff) {
        java.io.FileFilter f = null;
        super.setFilenameFilter(f);
    }

    /*
     * @see org.tigris.gef.base.FileDialog#setDirectory()
     */
    public void setDirectory(String d) {
        super.setDirectory(d);
    }

    /*
     * @see org.tigris.gef.base.FileDialog#setVisible()
     */
    public void setVisible(boolean v) {
        super.setVisible(v);
    }

    /*
     * @see org.tigris.gef.base.FileDialog#getFile()
     */
    public String getFile() {
        return super.getFile();
    }

    /*
     * @see org.tigris.gef.base.FileDialog#getDirectory()
     */
    public String getDirectory() {
        return super.getDirectory();
    }

    /*
     * @see org.tigris.gef.base.FileDialog#setTitle()
     */
    public void setTitle(String t) {
        super.setTitle(t);
    }

    /*
     * @see org.tigris.gef.base.FileDialog#setMode()
     */
    public void setMode(int mode) {
        super.setMode(mode);
    }
}
