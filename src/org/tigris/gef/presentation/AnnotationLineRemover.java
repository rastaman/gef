package org.tigris.gef.presentation;

import java.awt.event.*;
import javax.swing.*;
import java.util.Hashtable;

public class AnnotationLineRemover implements ActionListener {           
         
        private Hashtable timers; // FIG | Timer
        private Hashtable figs;   // Timer | Fig (nur der bequemlichkeit wegen ;-)
        private static AnnotationLineRemover theInstance = null; 

        private AnnotationLineRemover(){
    		timers = new Hashtable();
    		figs = new Hashtable();
        }
        
        public static AnnotationLineRemover instance(){
        	if (theInstance==null) theInstance = new AnnotationLineRemover();
        	return theInstance;
        }
        	
        public void removeLineIn(int millis, Fig f){
        	// falls diese Fig schon einen Timer hat, dann starte ihn einfach nur neu,
        	// um zu verhindern, dass die Linie des Elements, dass gerade verschoben wird
        	// unsichtbar wird
        	if (timers.containsKey(f)){
        		((Timer)timers.get(f)).restart();
        	}
        	// sonst lege einen neuen Timer an
        	else{	
        		Timer t = new  Timer(millis, this);
        		timers.put(f,t);
        		figs.put(t,f);
        		t.start();
        	}
        }



        public void actionPerformed(ActionEvent e){
        	//System.out.println("Event from Timer !  " + e.getSource());
        	// die Zeit ist abgelaufen - d.h. die Linie wurde 'millis'-Millisekunden
        	// nicht bewegt und kann geloescht werden.
        	// Gleichzeitig kann der Timer angehalten werden.
        	Timer t = (Timer)e.getSource();
        	t.stop();
        	//((Fig)figs.get(t)).getAnnotationStrategy().removeAllConnectingLines();
        	Fig annotation = ((Fig)figs.get(t));
        	System.out.println("*************************************" +annotation);
                try{
        	        annotation.getAnnotationOwner().getAnnotationStrategy().getAnnotationProperties(annotation).removeLine();
                }
                catch (Exception ex){}
        }	
}
