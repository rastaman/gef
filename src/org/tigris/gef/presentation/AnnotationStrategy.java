package org.tigris.gef.presentation;

import java.util.*;
import java.awt.Color;
import java.awt.Point;
import org.tigris.gef.base.*;
import java.beans.*;

/**
 * Each class serving as AnnotationOwner gets an AnnotationStrategy saying how the
 * Annotations should behave when the AnnotationOwner changes its position (e.g. move
 * in parallel). 
 */

public abstract class AnnotationStrategy{
    // hashtable of all annotations
    Hashtable annotations = new Hashtable();  // annotation | AnnotationProperties
    
    public Point restoreAnnotationPosition(Fig annotation){ return new Point(1,1); }
    // this method auto-moves the annotations
    public abstract void translateAnnotations(Fig owner);
    // calculates and stores the values necessary for correct auto-movement
    public abstract void storeAnnotationPosition(Fig annotation);
    
    // should the line from annotation to owner be visible ?
    protected boolean lineIsVisible(Fig annotation){ return true; };
    
    // all figs added to an owner fig with this method become annotations of that fig
    public void addAnnotation(Fig owner, Fig annotation, AnnotationProperties properties){
	// restrictions
	// 1. no double annotations
	if ( (annotations.containsKey(annotation)) || owner == null || annotation == null ) return;
	// tell the annotation its owner
	annotation.setAnnotationOwner(owner);
	// store the annotation with its properties in a hashtable
	annotations.put(annotation, properties);
    }
    
    public AnnotationProperties getAnnotationProperties(Fig annotation){
	return (AnnotationProperties)annotations.get(annotation);
    }
    
    public Enumeration getAllAnnotations(){
	return annotations.keys();
    }

    public Vector getAnnotationsVector(){
	Vector v = new Vector();
	Enumeration enum = getAllAnnotations();
	while (enum.hasMoreElements()){
	    v.addElement(enum.nextElement());
	}
	return v;
    }

    public void replaceAnnotation(Fig annotation, AnnotationProperties properties){
	Fig owner = annotation.getAnnotationOwner();
	annotations.remove(annotation);
	addAnnotation(owner, annotation, properties);
    }
    
    public void removeAnnotation(Fig annotation){
	if (annotations.get(annotation) != null)
	    annotations.remove(annotation);
    }
    
    public void removeAllAnnotations(){
	java.util.Enumeration enum = annotations.keys();
	while (enum.hasMoreElements()){
	    Fig annotation = (Fig)enum.nextElement();
	    //annotation.delete();
	    removeAnnotation(annotation);
		}
    }
        
    // delete line from owner to annotation
    public void removeAllConnectingLines(){
	java.util.Enumeration enum = annotations.keys();
	while (enum.hasMoreElements()){
	    Fig annotation = (Fig)enum.nextElement();
	    AnnotationProperties prop = (AnnotationProperties)annotations.get(annotation);
	    FigLine line = prop.getConnectingLine();
	    if (Globals.curEditor().getLayerManager().getContents().contains(line))
		Globals.curEditor().remove(line);
	}
    }
    
} // end of class
