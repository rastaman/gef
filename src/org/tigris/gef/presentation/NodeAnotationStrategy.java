package org.tigris.gef.presentation;

import java.util.*;
import java.awt.*;
import org.tigris.gef.base.*;



public class NodeAnotationStrategy extends AnotationStrategy{
	
	public NodeAnotationStrategy(){
	}

	protected Point restoreAnotationPosition(Fig anotation){
		int delta_x;
		float delta_y;
		Fig owner = anotation.getAnotationOwner();
		// in this case: owner is a node
		if (! ( (owner instanceof FigEdge) || (owner instanceof FigLine) )){
			ArrayList list = (ArrayList)anotations.get(anotation);
			delta_x = ((Integer)list.get(0)).intValue();
			delta_y = ((Float)list.get(1)).floatValue();							
			int own_x = (int) owner.center().x;
			int own_y = (int) owner.center().y;
			int newX = (int) (own_x + delta_x);
			int newY = (int) (own_y + delta_y);
			// neue Position der Anotation einstellen
			return new Point( (int)newX- (anotation.getWidth()/2), (int)newY-(anotation.getHeight()/2) );
		}
		return new Point(1,1);
	}
		
	public void storeAnotationPosition(Fig anotation){
		int delta_x; float delta_y;
		Fig owner = anotation.getAnotationOwner();
		// in this case: owner is a node
		if (! ( (owner instanceof FigEdge) || (owner instanceof FigLine) )){
			Point anPos 	= anotation.center();
			Point ownerPos 	= owner.center();		
			delta_x = anPos.x-ownerPos.x;
			delta_y = anPos.y-ownerPos.y;
			// store values
			ArrayList list = (ArrayList)anotations.get(anotation);
			FigLine line = (FigLine)list.get(2);
			list.add(0, new Integer(delta_x));
			list.add(1, new Float(delta_y));
			list.add(2, line);				
			anotations.put(anotation, list);
		}
		drawConnectingLine(anotation);
	}

	public void drawConnectingLine(Fig anotation){
		Fig owner = anotation.getAnotationOwner();
		AnotationProperties prop = (AnotationProperties)anotations.get(anotation);
		FigLine line = prop.getConnectingLine();
		line.setShape(anotation.center(), owner.center());
		line.setLineColor(Color.red);
		line.setFillColor(Color.red);
		line.setDashed(true);
		if (!(Globals.curEditor().getLayerManager().getContents().contains(line))) Globals.curEditor().add(line);
		Globals.curEditor().getLayerManager().bringToFront(anotation);
		line.damage();
		anotation.damage();
        }
		
	// move anotations to its new position
	public void translateAnotations(Fig owner){
		java.util.Enumeration enum = anotations.keys();
		while (enum.hasMoreElements()){
			Fig anotation = (Fig)enum.nextElement();
			anotation.setLocation(restoreAnotationPosition(anotation));
			drawConnectingLine(anotation);
			anotation.endTrans();
			anotation.damage();
		}
	}
	
} // end of class