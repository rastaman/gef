// Copyright (c) 1996-2001 The Regents of the University of California. All
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

// File: BaseResourceBundle.java
// Classes: BaseResourceBundle
// Original Author: Luc Maisonobe
// $Id$

package org.tigris.gef.base;

import java.util.ListResourceBundle;

public class BaseResourceBundle extends ListResourceBundle {

	static final Object[][] _contents = {
		{"Adjust Grid", "Adjust Grid"},
		{"Adjust Grid Snap", "Adjust Grid Snap"},
		{"Adjust PageBreaks", "Adjust PageBreaks"},
		{"Align Tops", "Align Tops"},
		{"Align Bottoms", "Align Bottoms"},
		{"Align Lefts", "Align Lefts"},
		{"Align Rights", "Align Rights"},
		{"Align Centers", "Align Centers"},
		{"Align Horizontal Centers", "Align Horizontal Centers"},
		{"Align Vertical Centers", "Align Vertical Centers"},
		{"Align To Grid", "Align To Grid"},
		{"Copy", "Copy"},
		{"Delete from Diagram", "Delete from Diagram"},
		{"Remove From Model", "Remove From Model"},
		{"Distribute Horizontal Spacing", "Distribute Horizontal Spacing"},
		{"Distribute Horizontal Centers", "Distribute Horizontal Centers"},
		{"Distribute Leftward", "Distribute Leftward"},
		{"Distribute Vertical Spacing", "Distribute Vertical Spacing"},
		{"Distribute Vertical Centers", "Distribute Vertical Centers"},
		{"Distribute Upward", "Distribute Upward"},
		{"Exit", "Exit"},
		{"Group", "Group"},
		{"Insert a new point", "Insert a new point"},
		{"Nudge Left", "Nudge Left"},
		{"Nudge Right", "Nudge Right"},
		{"Nudge Up", "Nudge Up"},
		{"Nudge Down", "Nudge Down"},
		{"Do nothing", "Do nothing"},
		{"Open...", "Open..."},
		{"Open PGML...", "Open PGML..."},
		{"Open SVG...", "Open SVG..."},
		{"Paste", "Paste"},
		{"Print...", "Print..."},
		{"Remove Point From Polygon", "Remove Point From Polygon"},
		{"Backward", "Backward"},
		{"To Back", "To Back"},
		{"Forward", "Forward"},
		{"To Front", "To Front"},
		{"Save Encapsulated PostScript...", "Save Encapsulated PostScript..."},
		{"Save GIF...", "Save GIF..."},
		{"Save...", "Save..."},
		{"Save as PGML...", "Save as PGML..."},
		{"Save PostScript...", "Save PostScript..."},
		{"Save Scalable Vector Graphics...", "Save Scalable Vector Graphics..."},
		{"Scroll Left", "Scroll Left"},
		{"Scroll Right", "Scroll Right"},
		{"Scroll Up", "Scroll Up"},
		{"Scroll Down", "Scroll Down"},
		{"Select All", "Select All"},
		{"Invert Selection", "Invert Selection"},
		{"SelectNear Left", "SelectNear Left"},
		{"SelectNear Right", "SelectNear Right"},
		{"SelectNear Up", "SelectNear Up"},
		{"SelectNear Down", "SelectNear Down"},
		{"Select Next", "Select Next"},
		{"Select Previous", "Select Previous"},
		{"Sequence of Commands", "Sequence of Commands"},
		{"Set Editor Mode", "Set Editor Mode"},
		{"Show Properties", "Show Properties"},
		{"Show URL in browser", "Show URL in browser"},
		{"Spawn Editor", "Spawn Editor"},
		{"Ungroup", "Ungroup"},
		{"Use Reshape Handles", "Use Reshape Handles"},
		{"Use Resize Handles", "Use Resize Handles"},
		{"Use Rotation Handles", "Use Rotation Handles"},
		{"Zoom Reset", "Zoom Reset"},
		{"Zoom In", "Zoom In"},
		{"Zoom Out", "Zoom Out"},
		{"Do Nothing", "Do Nothing"}
	};
	
	public Object[][] getContents() {
		return _contents;
	}
	
}
