// File: CmdZoom.java
// Classes: CmdZoom
// Original Author: lawley@dstc.edu.au
// $Id$

package org.tigris.gef.base;

import java.util.*;
import java.awt.*;

/** Cmd zoom the view.  Needs-More-Work: */

public class CmdZoom extends Cmd
{
    ////////////////////////////////////////////////////////////////
    // constants

    ////////////////////////////////////////////////////////////////
    // instance variables

    protected double _magnitude;

    ////////////////////////////////////////////////////////////////
    // constructor

    /** Default behaviour is to restore scaling to 1.0 (1 to 1) */
    public CmdZoom()
    {
        this(0);
    }

    /** Each time <code>doIt()</code> is invoked, adjust scaling by a
    * factor of <code>magnitude</code>.
    * @param magnitude the factor by which to adjust the Editor's scaling. Must be greater than or equal to zero.  If zero, resets the Editor's scale factor to 1.
    */
    public CmdZoom(double magnitude)
    {
        super(wordFor(magnitude));
        _magnitude = magnitude;
    }

    /** Convert the zoom magnitude to an English description. */
    protected static String wordFor(double magnitude)
    {
        if (magnitude < 0) throw new IllegalArgumentException("Zoom magnitude cannot be less than 0");
        else if (magnitude == 0.0) return "Zoom Reset";
        else if (magnitude > 1.0) return "Zoom In";
        else if (magnitude < 1.0) return "Zoom Out";
        else return "Do Nothing";       // Not a very useful option
    }

    /** Adjust the scale factor of the current editor. */
    public void doIt()
    {
        Editor ed = (Editor) Globals.curEditor();
        if (ed == null) return;

        if (_magnitude > 0.0) {
            ed.setScaleInt(ed.getScale() * _magnitude);
        } else {
            ed.setScaleInt(1.0);
        }
        ed.damageAll();
    }

    /** Undo the zoom.  Does not yet work for magnitude of 0 (a reset),
    * and is subject to skew due to precision errors since
    * for floats we cannot assume <code>(x * f / f) == x</code> */
    public void undoIt()
    {
        Editor ed = (Editor) Globals.curEditor();
        if (ed == null) return;

        if (_magnitude > 0.0) {
            ed.setScaleInt(ed.getScale() / _magnitude);
        } else {
            System.out.println("Cannot undo CmdZoom reset, yet.");
        }
    }
} /* end class CmdZoom */