
// File: GraphController.java
// Interfaces: GraphController
// Original Author: thorsten Oct 2000
// $Id$

package org.tigris.gef.graph;

/**
 * This interface is the basis for each class that
 * handles the control of pairs of data and representational objects.
 */
public interface GraphController extends java.io.Serializable {
	/** 
	 * Add a new presentation to the list of known presentations.
	 * Each presentation consists of a data object and an object
	 * for its graphical representation.
	 */
	public void addPresentation(Object representation, Object data);

	/** 
	 * Remove a presentation from the list of known presentations.
	 */
	public void removePresentation(Object element);

	/**
	 * Get the graphical representation of the given object.
	 */
	public Object presentationFor(Object data);

} /* end interface GraphController */
