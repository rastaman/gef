package org.tigris.gef.presentation;

import org.tigris.gef.base.*;


// each Anotation has one associated AnotationProperties object
public class FigTextAnotationProperties extends AnotationProperties{
	
        public FigTextAnotationProperties(boolean fixedOffset, int offset, boolean fixedRatio, float ratio){
        	super(fixedOffset, offset, fixedRatio, ratio);
        }

	public FigTextAnotationProperties(int offset, float ratio){
		super(false, offset, false, ratio);
	}
	
	// anotation is visible if it contains some text
	protected boolean anotationIsVisible(Fig anotation){
		if (!(anotation instanceof FigText)) return true;
		FigText f = (FigText)anotation;
		return !(f.getText().equals(""));
	}
			
		

} // end of class