package org.tigris.gef.presentation;

import java.util.*;
import java.awt.*;
import org.tigris.gef.base.*;



public class EdgeAnnotationStrategy extends AnnotationStrategy{

	AnnotationHelper helper = AnnotationHelper.instance();

	public EdgeAnnotationStrategy(){
	}

	/*
	protected boolean lineIsVisible(Fig annotation){ 
		System.out.println("-----------------------------------------LineIsVisible called !");
		if (annotation instanceof FigText){
			return (!((FigText)annotation).getText().equals(""));
		}
		return true;
	};
	*/

        public Point restoreAnnotationPosition(Fig annotation){
		int d; //offset
		float ratio;
		
		Fig owner = annotation.getAnnotationOwner();
		// in the case: owner is an edge
		if (owner instanceof FigEdge){
			AnnotationProperties prop = (AnnotationProperties)annotations.get(annotation);
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

			if ((start.x == ende.x) && (start.y == ende.y)) return annotation.getLocation();
			
			// calculate
			float xdirection = ende.x - start.x;
			float ydirection = ende.y - start.y;
			double newX = start.x + ratio * xdirection;
			double newY = start.y + ratio * ydirection;
			// restore offset d
			newX = newX + d * ( ydirection / Math.sqrt(xdirection*xdirection + ydirection*ydirection) );
			newY = newY + d * (-1)* (xdirection / Math.sqrt(xdirection*xdirection + ydirection*ydirection) );
			// the annotation's new position
			return new Point( (int)newX- (annotation.getWidth()/2), (int)newY-(annotation.getHeight()/2) );
		}
		return new Point(1,1);
	}


	// calculate offset and ratio of annotation (relative to annotationOwner)
	// store values in a hashtable
	// method is called when the annotation is moved without its owner
	public void storeAnnotationPosition(Fig annotation){
		int d; float ratio;
		Fig owner = annotation.getAnnotationOwner();
		// case: owner ist eine Kante
		if (owner instanceof FigEdge){
			FigEdge edge = (FigEdge)owner;
			Point anPos 	= annotation.center();
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
			AnnotationProperties prop = (AnnotationProperties)annotations.get(annotation);
			prop.setRatio(ratio,prop.hasFixedRatio());
			prop.setOffset(d ,prop.hasFixedOffset());

			//((PathConvPercentPlusConst)edge.getPathConvOfFig(annotation)).setPercentOffset((int)ratio, d);
			//System.out.println("ratio = " +ratio + "   d= " +d);

			//
		}
		drawConnectingLine(annotation);
	}

	// draws a dotted line between this annotation and its owner
	public void drawConnectingLine(Fig annotation) throws NullPointerException{
		if (!(getAnnotationProperties(annotation).lineIsVisible(annotation))) return;
		Fig owner = annotation.getAnnotationOwner();
		AnnotationProperties prop = (AnnotationProperties)annotations.get(annotation);
		FigLine line = prop.getConnectingLine();


		if (  ((FigEdge)owner).getSourcePortFig().center() == null || ((FigEdge)owner).getDestPortFig().center() == null ) return;
                //line from annotation to center of owning edge
		//line.setShape(annotation.center(), owner.center());
                //line from annotation to closest point on owning edge
                try{
                	line.setShape(annotation.center(), helper.getClosestPointOnEdge(annotation.center(),((FigEdge)owner).getFirstPoint(), ((FigEdge)owner).getLastPoint()));
                } catch (ArrayIndexOutOfBoundsException e){
                	//line.setShape(annotation.center(), calculateLotpunkt(annotation.center(), ((FigEdge)owner).getSourcePortFig().center(), ((FigEdge)owner).getDestPortFig().center() ));
                	line.setShape(annotation.center(), helper.getClosestPointOnEdge(annotation.center(), ((FigEdge)owner).getSourcePortFig().center(), ((FigEdge)owner).getDestPortFig().center() ));
                }
                //line.setShape(annotation.center(), getClosestPointOnEdge(annotation.center(), ((FigEdge)owner).getSourcePortFig().center(), ((FigEdge)owner).getDestPortFig().center() ));
                line.setLineColor(getAnnotationProperties(annotation).getLineColor());
		line.setFillColor(getAnnotationProperties(annotation).getLineColor());
		line.setDashed(true);
		// draw the line
		if (!(Globals.curEditor().getLayerManager().getContents().contains(line))) Globals.curEditor().add(line);
		Globals.curEditor().getLayerManager().bringToFront(annotation);
		//
		line.damage();
		annotation.damage();

		// remove line automatically
		AnnotationLineRemover.instance().removeLineIn( getAnnotationProperties(annotation).getLineVisibilityDuration(),annotation );
        }

	// move annotations
	// this method is called, when an annotationOwner is moved without its
	// annotations
	public void translateAnnotations(Fig owner){
		java.util.Enumeration enum = annotations.keys();
		// owner has moved; set annotations to their new positions
		while (enum.hasMoreElements()){
			Fig annotation = (Fig)enum.nextElement();
			annotation.setLocation(restoreAnnotationPosition(annotation));
			//drawConnectingLine(annotation);
			Globals.curEditor().getLayerManager().bringToFront(annotation);
			// call endtrans of annotation
			annotation.endTrans();
			annotation.damage();

		}
		owner.damage();
	}

} // end of class
