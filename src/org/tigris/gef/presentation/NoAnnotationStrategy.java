package org.tigris.gef.presentation;

import java.util.*;

public class NoAnnotationStrategy extends AnnotationStrategy{
         
        // Diese Klasse ist nur ein Dummy fuer die Figs, die keine Annotations 
        // haben sollen. Sie wird standardmaessig fuer jede Fig angelegt.   
	public NoAnnotationStrategy(){
	}

	public void translateAnnotations(Fig owner){
		// do nothing in this case
	}
	
	/*
	protected void restoreAnnotationPosition(Fig annotation){
		// do nothing in this case
	}
	*/
	
	public void storeAnnotationPosition(Fig annotation){
		// do nothing in this case
	}
}
