package org.tigris.gef.presentation;

import java.util.*;
import java.awt.Color;
import org.tigris.gef.base.*;
import java.beans.*;

// jede Klasse, die als AnotationOwner dienen soll, bekommt eine AnotationStrategy,
// die angibt, wie sich die Anotations veraendern sollen (z.B. parallel mitverschieben)
// wenn der AnotationOwner die Position aendert

public abstract class AnotationStrategy{

	// hashtable of all anotations
	Hashtable anotations = new Hashtable();  // anotation | AnotationProperties

	// this method auto-moves the anotations
	public abstract void translateAnotations(Fig owner);
	// calculates and stores the values necessary for correct auto-movement
	public abstract void storeAnotationPosition(Fig anotation);

	// should the line from anotation to owner be visible ?
	protected boolean lineIsVisible(Fig anotation){ return true; };

	// all figs added to an owner fig with this method become anotations of that fig
	public void addAnotation(Fig owner, Fig anotation, AnotationProperties properties){
		// restrictions
		// 1. no double anotations
		if ( (anotations.containsKey(anotation)) || owner == null || anotation == null ) return;
		// tell the anotation its owner
		anotation.setAnotationOwner(owner);
		// store the anotation with its properties in a hashtable
		anotations.put(anotation, properties);
	}

	public AnotationProperties getAnotationProperties(Fig anotation){
		return (AnotationProperties)anotations.get(anotation);
	}

        public Enumeration getAllAnotations(){
                return anotations.keys();
        }

        public Vector getAnotationsVector(){
                Vector v = new Vector();
                Enumeration enum = getAllAnotations();
                while (enum.hasMoreElements()){
                        v.addElement(enum.nextElement());
                }
                return v;
        }


	public void removeAnotation(Fig anotation){
		//if (anotation==null) return;
		// delete the line first
		//AnotationProperties prop = (AnotationProperties)anotations.get(anotation);
		//FigLine line = prop.getConnectingLine();
		//Globals.curEditor().remove(line);
		anotations.remove(anotation);
                //line.delete();
		//System.out.println("+++++++++++removed from hashtable: " + anotation);
		//Globals.curEditor().remove(anotation);
	}

	public void removeAllAnotations(){
		java.util.Enumeration enum = anotations.keys();
		while (enum.hasMoreElements()){
			Fig anotation = (Fig)enum.nextElement();
			//anotation.delete();
			removeAnotation(anotation);
		}
	}


	// delete line from owner to anotation
	public void removeAllConnectingLines(){
		java.util.Enumeration enum = anotations.keys();
		while (enum.hasMoreElements()){
			Fig anotation = (Fig)enum.nextElement();
			AnotationProperties prop = (AnotationProperties)anotations.get(anotation);
			FigLine line = prop.getConnectingLine();
			if (Globals.curEditor().getLayerManager().getContents().contains(line))
				Globals.curEditor().remove(line);
		}
	}

} // end of class
