package org.tigris.gef.presentation;

import java.util.*;
import java.awt.*;
import org.tigris.gef.base.*;



public class EdgeAnotationStrategy extends AnotationStrategy{

	AnotationHelper helper = AnotationHelper.instance();

	public EdgeAnotationStrategy(){
	}

	/*
	protected boolean lineIsVisible(Fig anotation){ 
		System.out.println("-----------------------------------------LineIsVisible called !");
		if (anotation instanceof FigText){
			return (!((FigText)anotation).getText().equals(""));
		}
		return true;
	};
	*/

        protected Point restoreAnotationPosition(Fig anotation){
		int d; //offset
		float ratio;
		
		Fig owner = anotation.getAnotationOwner();
		// in the case: owner is an edge
		if (owner instanceof FigEdge){
			AnotationProperties prop = (AnotationProperties)anotations.get(anotation);
			d 	= prop.getOffset();
			ratio 	= prop.getRatio();
			FigEdge edge = (FigEdge)owner;
			//Point start 	= edge.getSourcePortFig().center();
			//Point ende  	= edge.getDestPortFig().center();
			Point start;
			Point ende;
			try{
				start = edge.getFirstPoint();
				ende = edge.getLastPoint();
			} catch (ArrayIndexOutOfBoundsException e){
				try{
					start= edge.getSourcePortFig().center();	
					ende = edge.getDestPortFig().center();
				}
				catch(NullPointerException ne){
    					start = new Point(10,10);
    					ende = new Point(100,10);
				}
			} 
			
			if ((start.x == ende.x) && (start.y == ende.y)) return anotation.getLocation();
			
			// calculate
			float xdirection = ende.x - start.x;
			float ydirection = ende.y - start.y;
			double newX = start.x + ratio * xdirection;
			double newY = start.y + ratio * ydirection;
			// restore offset d
			newX = newX + d * ( ydirection / Math.sqrt(xdirection*xdirection + ydirection*ydirection) );
			newY = newY + d * (-1)* (xdirection / Math.sqrt(xdirection*xdirection + ydirection*ydirection) );
			// the anotation's new position
			return new Point( (int)newX- (anotation.getWidth()/2), (int)newY-(anotation.getHeight()/2) );
		}
		return new Point(1,1);
	}


	// calculate offset and ratio of anotation (relative to anotationOwner)
	// store values in a hashtable
	// method is called when the anotation is moved without its owner
	public void storeAnotationPosition(Fig anotation){
		int d; float ratio;
		Fig owner = anotation.getAnotationOwner();
		// case: owner ist eine Kante
		if (owner instanceof FigEdge){
			FigEdge edge = (FigEdge)owner;
			Point anPos 	= anotation.center();
			//Point start= edge.getSourcePortFig().center();	
			//Point ende = edge.getDestPortFig().center();
			Point start; 	
			Point ende;
			try{
				start = edge.getFirstPoint();
				ende = edge.getLastPoint();
			} catch (ArrayIndexOutOfBoundsException e){
				start= edge.getSourcePortFig().center();	
				ende = edge.getDestPortFig().center();
			}
			
			if ((start.x == ende.x) && (start.y == ende.y)) return;
			
			d     = helper.getNormOffset(anPos, start, ende);
			ratio = helper.getRatio(anPos, start, ende);
                        Point normpoint = helper.getNormPointOnEdge(anPos, start, ende);
			// store values
			AnotationProperties prop = (AnotationProperties)anotations.get(anotation);
			prop.setRatio(ratio,prop.hasFixedRatio());
			prop.setOffset(d ,prop.hasFixedOffset());
			
			// so ungefaehr
			//((PathConvPercentPlusConst)edge.getPathConvOfFig(anotation)).setPercentOffset((int)ratio, d);
			System.out.println("ratio = " +ratio + "   d= " +d);
			
			//
		}
		drawConnectingLine(anotation);
	}

	// draws a dotted line between this anotation and its owner
	public void drawConnectingLine(Fig anotation) throws NullPointerException{
		if (!(getAnotationProperties(anotation).lineIsVisible(anotation))) return;
		Fig owner = anotation.getAnotationOwner();
		AnotationProperties prop = (AnotationProperties)anotations.get(anotation);
		FigLine line = prop.getConnectingLine();
		
		
		if (  ((FigEdge)owner).getSourcePortFig().center() == null || ((FigEdge)owner).getDestPortFig().center() == null ) return;
                //line from anotation to center of owning edge
		//line.setShape(anotation.center(), owner.center());
                //line from anotation to closest point on owning edge
                try{
                	line.setShape(anotation.center(), helper.getClosestPointOnEdge(anotation.center(),((FigEdge)owner).getFirstPoint(), ((FigEdge)owner).getLastPoint()));
                } catch (ArrayIndexOutOfBoundsException e){
                	//line.setShape(anotation.center(), calculateLotpunkt(anotation.center(), ((FigEdge)owner).getSourcePortFig().center(), ((FigEdge)owner).getDestPortFig().center() ));
                	line.setShape(anotation.center(), helper.getClosestPointOnEdge(anotation.center(), ((FigEdge)owner).getSourcePortFig().center(), ((FigEdge)owner).getDestPortFig().center() ));
                }
                //line.setShape(anotation.center(), getClosestPointOnEdge(anotation.center(), ((FigEdge)owner).getSourcePortFig().center(), ((FigEdge)owner).getDestPortFig().center() ));
                line.setLineColor(getAnotationProperties(anotation).getLineColor());
		line.setFillColor(getAnotationProperties(anotation).getLineColor());
		line.setDashed(true);
		// draw the line
		if (!(Globals.curEditor().getLayerManager().getContents().contains(line))) Globals.curEditor().add(line);
		Globals.curEditor().getLayerManager().bringToFront(anotation);
		//
		line.damage();
		anotation.damage();

		// remove line automatically		
		AnotationLineRemover.instance().removeLineIn( getAnotationProperties(anotation).getLineVisibilityDuration(),anotation );		
        }

	// move anotations
	// this method is called, when an anotationOwner is moved without its 
	// anotations
	public void translateAnotations(Fig owner){
		java.util.Enumeration enum = anotations.keys();
		// owner has moved; set anotations to their new positions
		while (enum.hasMoreElements()){	
			Fig anotation = (Fig)enum.nextElement();
			anotation.setLocation(restoreAnotationPosition(anotation));
			//drawConnectingLine(anotation);
			Globals.curEditor().getLayerManager().bringToFront(anotation);
			// call endtrans of anotation
			anotation.endTrans();
			anotation.damage();
			
		}
		owner.damage();
	}

} // end of class