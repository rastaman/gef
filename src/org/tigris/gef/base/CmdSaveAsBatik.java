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

// File: CmdSave.java
// Classes: CmdSave
// Original Author: jrobbins@ics.uci.edu
// $Id$

package org.tigris.gef.base;

import java.awt.FileDialog;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.commons.logging.*;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public class CmdSaveAsBatik extends CmdSaveGraphics implements FilenameFilter {

    private static Log LOG = LogFactory.getLog(CmdSaveAsBatik.class);
    
    public CmdSaveAsBatik() {
        super("Save SVG As...");
    }

    /** Only allow the user to select files that match the fiven
     *  filename pattern. Needs-More-Work: this is not used yet. */
    public CmdSaveAsBatik(String filterPattern) {
        this();
        setArg("filterPattern", filterPattern);
    }

    public void doIt() {
        try {
            Editor ce = Globals.curEditor();
            // TODO Should use JFileChooser
            FileDialog fd =
                new FileDialog(ce.findFrame(), "Save Diagram", FileDialog.SAVE);
            fd.setFilenameFilter(this);
            fd.setDirectory(Globals.getLastDirectory());
            fd.show();
            String filename = fd.getFile(); // blocking
            String path = fd.getDirectory(); // blocking
            Globals.setLastDirectory(path);
            if (filename != null) {
                Globals.showStatus("Writing " + path + filename + "...");
                FileOutputStream f = new FileOutputStream(path + filename);
                setStream(f);
                super.doIt();
            }
        } catch (FileNotFoundException ignore) {
            System.out.println("got an FileNotFoundException");
        } catch (IOException ignore) {
            System.out.println("got an IOException");
            ignore.printStackTrace();
        }
    }

    /** Only let the user select files that match the filter. This does
     *  not seem to be called under JDK 1.0.2 on solaris. I have not
     *  finished this method, it currently accepts all filenames. <p>
     *
     *  Needs-More-Work: the source code for this method is duplicated in
     *  CmdOpen#accept.  */
    public boolean accept(File dir, String name) {
        if (containsArg("filterPattern")) {
            // if pattern dosen't match, return false
            return true;
        }
        return true; // no pattern was specified
    }

    public void undoIt() {
        System.out.println("Undo does not make sense for CmdSave");
    }
    
    protected void saveGraphics(OutputStream s, Editor ce,
                                Rectangle drawingArea)
                 throws IOException {
        SVGGraphics2D svgGraphics2D = null;
        try {
            // Get a DOMImplementation
            DOMImplementation domImpl =
                GenericDOMImplementation.getDOMImplementation();

            // Create an instance of org.w3c.dom.Document
            Document document = domImpl.createDocument(null, "svg", null);

            // Create an instance of the SVG Generator
            svgGraphics2D = new SVGGraphics2D(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (svgGraphics2D != null) {   
            // Finally, stream out SVG to the standard output using UTF-8
            // character to byte encoding
            boolean useCSS = true; // we want to use CSS style attribute
            ce.print(svgGraphics2D);
            Writer out = new OutputStreamWriter(s, "UTF-8");
            svgGraphics2D.stream(out, useCSS);
            svgGraphics2D.dispose();
        }
    }
}
