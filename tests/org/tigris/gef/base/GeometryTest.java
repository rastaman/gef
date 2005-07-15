// $Id$
// Copyright (c) 2005 The Regents of the University of California. All
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

package org.tigris.gef.base;
import java.awt.Point;

import junit.framework.TestCase;

import org.tigris.gef.base.Geometry;




public class GeometryTest extends TestCase {


    public GeometryTest(String arg0) {
        super(arg0);
    }


    final public void testPtClosest1() {
        int x1 = 100, x2 = 300;
        int y1 = 100, y2 = 300;
        Point p = new Point(300, 100);
        Point res = new Point(0,0);
        Geometry.ptClosestTo(x1, y1, x2, y2, p, res);
        assertEquals("Wrong value X", 200, res.x);
        assertEquals("Wrong value Y", 200, res.y);
    }
    
    final public void testPtClosest2() {    
        int x1 = 900, x2 = 0;
        int y1 = 000, y2 = 300;
        Point p = new Point(400, 500);
        Point res = new Point(0,0);
        Geometry.ptClosestTo(x1, y1, x2, y2, p, res);
        assertEquals("Wrong value X", 300, res.x);
        assertEquals("Wrong value Y", 200, res.y);
    }

    // on the segment
    final public void testPtClosest3() {    
        int x1 = 100, x2 = 1000;
        int y1 = 400, y2 = 100;
        Point p = new Point(700, 200);
        Point res = new Point(0,0);
        Geometry.ptClosestTo(x1, y1, x2, y2, p, res);
        assertEquals("Wrong value X", 700, res.x);
        assertEquals("Wrong value Y", 200, res.y);
    }

    // on one line, outside the segment
    final public void testPtClosest4() {    
        int x1 = 100, x2 = 700;
        int y1 = 400, y2 = 200;
        Point p = new Point(1000, 100);
        Point res = new Point(0,0);
        Geometry.ptClosestTo(x1, y1, x2, y2, p, res);
        assertEquals("Wrong value X", 700, res.x);
        assertEquals("Wrong value Y", 200, res.y);
    }

    // on one line, outside the segment
    final public void testPtClosest5() {    
        int x1 = 220, x2 = 200;
        int y1 = 180, y2 = 200;
        Point p = new Point(477, 337);
        Point res = new Point(0,0);
        Geometry.ptClosestTo(x1, y1, x2, y2, p, res);
        assertEquals("Wrong value X", 220, res.x);
        assertEquals("Wrong value Y", 180, res.y);
    }

    final public void testPtClosest6() {
        int x1 = 400, x2 = 100;
        int y1 = 400, y2 = 100;
        Point p = new Point(300, 100);
        Point res = new Point(0,0);
        Geometry.ptClosestTo(x1, y1, x2, y2, p, res);
        assertEquals("Wrong value X", 200, res.x);
        assertEquals("Wrong value Y", 200, res.y);
    }

    final public void testPtClosest7() {
        int x1 = 100, x2 = 300;
        int y1 = 300, y2 = 100;
        Point p = new Point(300, 300);
        Point res = new Point(0,0);
        Geometry.ptClosestTo(x1, y1, x2, y2, p, res);
        assertEquals("Wrong value X", 200, res.x);
        assertEquals("Wrong value Y", 200, res.y);
    }



}
