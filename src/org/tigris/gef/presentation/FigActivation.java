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




// File: FigActivation.java
// Classes: FigActivation
// Original Author: ics125 spring 1996
// $Id$

package org.tigris.gef.presentation;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.beans.*;
import javax.swing.*;


/** Primitive Fig to paint spezial rectangles for SequenceDiagrams on a LayerDiagram. */

public class FigActivation extends FigRect implements Serializable {

  ////////////////////////////////////////////////////////////////
  // instance variables

  public int _fromPos, _toPos; 
  public boolean _fromTheBeg = false;
  public boolean _end = false;

  public int _dynVectorPos;

  ////////////////////////////////////////////////////////////////
  // constructors

  /** Construct a new FigActivation w/ the given position and size. */
  public FigActivation(int x, int y, int w, int h, int from, int to){
    super(x, y, w, h);
    setFromPosition(from);
    setToPosition(to);
  }

  /** Construct a new FigActivation w/ the given position, size, line color,
   *  and fill color. */
  public FigActivation(int x, int y, int w, int h, Color lColor, Color fColor, int from, int to) {
    super(x, y, w, h, lColor, fColor);
    setFromPosition(from);
    setToPosition(to);
  }

  ////////////////////////////////////////////////////////////////
  // acessors
  
  public int getFromPosition() { return _fromPos; }

  public void setFromPosition(int newFrom) { _fromPos = newFrom; }
 
  public int getToPosition() { return _toPos; }

  public void setToPosition(int newTo) { _toPos = newTo; }
    
  public boolean isEnd() { return _end; }

  public void setEnd(boolean newEnd) { _end = newEnd; }
    
  public boolean isFromTheBeg() { return _fromTheBeg; }

  public void setFromTheBeg(boolean newFromTheBeg) { _fromTheBeg = newFromTheBeg; }
    
  public int getDynVectorPos() { return _dynVectorPos; }

  public void setDynVectorPos(int newPos) { _dynVectorPos = newPos; }
 
 } /* end class FigActivation */

