package org.tigris.gef.presentation;

import java.util.*;

public class NoAnotationStrategy extends AnotationStrategy{
         
        // Diese Klasse ist nur ein Dummy fuer die Figs, die keine Anotations 
        // haben sollen. Sie wird standardmaessig fuer jede Fig angelegt.   
	public NoAnotationStrategy(){
	}

	public void translateAnotations(Fig owner){
		// do nothing in this case
	}
	
	/*
	protected void restoreAnotationPosition(Fig anotation){
		// do nothing in this case
	}
	*/
	
	public void storeAnotationPosition(Fig anotation){
		// do nothing in this case
	}
}