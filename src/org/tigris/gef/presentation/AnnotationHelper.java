package org.tigris.gef.presentation;

import java.awt.event.*;
import javax.swing.*;
import java.util.Hashtable;
import java.awt.Point;

public class AnnotationHelper{

        private static AnnotationHelper theInstance = null;

	private float ratio;
	private int offset;
	private float dd;
	private Point a = new Point();
	private Point normpoint = new Point();


        private AnnotationHelper(){
        }

        public static AnnotationHelper instance(){
        	if (theInstance==null) theInstance = new AnnotationHelper();
        	return theInstance;
        }
        public int getNormOffset(Point p0, Point p1, Point p2){
		// p0: annotation's position
	  	// p1: edge's starting point
  		// p2: edge's ending point
		dd = (float)( ( (p2.x-p1.x)*(p0.y-p1.y) - (p0.x-p1.x)*(p2.y-p1.y) ) /
  			    Math.sqrt( (p2.x-p1.x)*(p2.x-p1.x) + (p2.y-p1.y)*(p2.y-p1.y) )  );
  		offset = -1 * (int) Math.round(dd);
	  	//System.out.println("distance: " + d);
  		return offset;
  	}

	public float getRatio(Point r1, Point r0, Point r2){
    		// r1: position of annotation;
	  	// r0: edge's starting point
  		// r2: edge's ending point
  	 	//edge's direction vector
		a.x = r2.x-r0.x; a.y = r2.y-r0.y;
		float t = (float) ( (r1.x-r0.x)*a.x + (r1.y-r0.y)*a.y ) / (a.x*a.x + a.y*a.y);
	        //calculate normpoint
		normpoint.x = (int) (  r1.x + (r0.x - r1.x + t*a.x)    );
		normpoint.y = (int) (  r1.y + (r0.y - r1.y + t*a.y)    );
	   	//calculate ratio
		float ges = (float) (Math.sqrt( (r2.x-r0.x)*(r2.x-r0.x) + (r2.y-r0.y)*(r2.y-r0.y) ));
		float srcToL = (float) (Math.sqrt( (normpoint.x-r0.x)*(normpoint.x-r0.x) + (normpoint.y-r0.y)*(normpoint.y-r0.y) ) );
		float rest = ges-srcToL;
		float proz = (srcToL/ges) *100;
	  	return srcToL/ges;
	}

        // get the closest point to p on an edge from start to end
        public Point getClosestPointOnEdge(Point p, Point start, Point end){
                normpoint = getNormPointOnEdge(p, start, end);
                // check if the normpoint is ON the edge (not only on the edge's beam)
                if (  ( (normpoint.x >= start.x && normpoint.x <= end.x) || (normpoint.x <= start.x && normpoint.x >= end.x) )
                	&& ( (normpoint.y >= start.y && normpoint.y <= end.y) || (normpoint.y <= start.y && normpoint.y >= end.y) )  ){
                	return normpoint;
                }
                else{
                	if (sqr_distance(normpoint, start) <= sqr_distance(normpoint, end)) return start;
                	else return end;
                }
        }

	// sqr distance of two points
        public int sqr_distance(Point p1, Point p2){
                return (int) ( (p2.x-p1.x)*(p2.x-p1.x) + (p2.y-p1.y)*(p2.y-p1.y) );
        }

	public Point getNormPointOnEdge(Point r1, Point r0, Point r2){
    		// r1: position of annotation;
	  	// r0: edge's starting point
  		// r2: edge's ending point
  	 	//edge's direction vector
		a.x = r2.x-r0.x; a.y = r2.y-r0.y;
		//
		float t = (float) ( (r1.x-r0.x)*a.x + (r1.y-r0.y)*a.y ) / (a.x*a.x + a.y*a.y);
	        //calculate NormPoint
		normpoint.x = (int) (  r1.x + (r0.x - r1.x + t*a.x)    );
		normpoint.y = (int) (  r1.y + (r0.y - r1.y + t*a.y)    );
		return normpoint;
	}

}// end of class
