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









// File: FigDynPort.java

// Classes: FigDynPort

// Original Author: ics125 spring 1996

// $Id$



package org.tigris.gef.presentation;



import java.util.*;
import java.util.Enumeration;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.beans.*;
import javax.swing.*;



/** Primitive Fig to paint spezial rectangles for SequenceDiagrams on a LayerDiagram.
 * @deprecated 0.10.1 This is a UML specific fig not relevant to GEF. Please implement in your
 * own application instead.
 */
public class FigDynPort extends FigRect implements Serializable {



  ////////////////////////////////////////////////////////////////

  // instance variables



  public int _pos; 



  public int _dynVectorPos;



  ////////////////////////////////////////////////////////////////

  // constructors



  /** Construct a new FigDynPort w/ the given position and size. */

  public FigDynPort(int x, int y, int w, int h, int pos){

    super(x, y, w, h);

    setPosition(pos);

  }



  /** Construct a new FigDynPort w/ the given position, size, line color,

   *  and fill color. */

  public FigDynPort(int x, int y, int w, int h, Color lColor, Color fColor, int pos) {

    super(x, y, w, h, lColor, fColor);

    setPosition(pos);

  }



  ////////////////////////////////////////////////////////////////

  // acessors

  

  public int getPosition() { return _pos; }



  public void setPosition(int newPos) { _pos = newPos; }

     

  public int getDynVectorPos() { return _dynVectorPos; }



  public void setDynVectorPos(int newVectorPos) { _dynVectorPos = newVectorPos; }

 

} /* end class FigDynPort */



