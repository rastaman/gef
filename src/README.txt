GEF vers 0.10

Resolved Issues
===============

The following issues have been fixed in release 0.10 of GEF

4    Provide javadocs  
8    org/tigris/gef/demo/EquipmentApplet.html has incorrect CODE  
9    typo in method name FigText::setJustifciaionByName()  
10   FigText.java: typo in fired PropertyChangeEvent 'justifciaio  
18   JGraph jumps allways into front  
19   FigGroup.setBounds()  
25   Multiple registration possibly causes eventual crash  
27   Graph background should use look and feel color  
31   StackOverFlowError due to MouseMoved  
33   merging the gentleware branch to solve many issues  
34   putting log4j and ant into cvs repository  
38   FigEdgeLine does not redraw correctly once dirty  
39   Make CVS API backward compatible to 0.9.6  
42   Limit API changes in release 0.10  
44   infinite loop risk in load process  
48   Scrollbars do not adjust when zooming  
49   Drawing problems while dragging in latest GEF  
54   Popup menu does not support adding separators  
55   Ordering popup menu should have mnemonic  
59   ps writer should extend Graphics2D...  
61   FigGroup.addFig does not remove fig from old group  
63   org.tigris.base.Geometry.intersects doesn't find all interse  

The following issues have been marked as invalid

21   ModeDragScroll related eventlistener exceptions  
23   JDK 14.1. hangs with Font error  
57   area not calculated properly for saving images  

The following issues have been marked as wont-fix

24   Problem with zooming in ArgoUML  
6    log4j missing from CVS repository  


Compatability with previous releases
====================================
1. Instance Variables

Release 0.10 contains various patches kindly supplied to us by Gentleware.

Much of these changes involved changing the types of protected instance variables from Vectors to Lists or to more generic collections.

So any client code attempting to directly access protected variables of GEF classes may now no longer be compatible.

We strongly recommend you convert your products while still using GEF0.9.6 to call accessor methods of our objects rather than directly access any GEF instance variables. This way when you later choose to upgrade your version of GEF we remain compatible across releases.

eg. where you currently refer to

yourDiagram._lay;

Instead call

yourDiagram.getLayer();


2. Collections

Note that on 0.10, getting a collection from a GEF component and adding to that collection cannot be guaranteed to add that element to GEF. Consider returned collections to be read-only.

eg This will no longer work
Vector v = figNode.getFigs();
v.addElement(newFig);

You must use the specific add methods of GEF classes to add to them
eg This will work
FigNode.add(newFig);

The reason this changed is again due to the Gentleware changes and a need to keep a compatable API between releases. Internally most collections have changed from Vector to List. In order to give an interface that is compatible to GEF0.9.6 these lists are copied to Vectors before being returned.



Deprecated methods and attributes
=================================
Once you receive release 0.10 you will notice various methods and attributes have been deprecated. In a future release all protected instance variables will become deprecated and eventually removed (effectivly removed from your perspective as we change visibility). This is being done to prevent a
similar problem occuring again in future. Please stick to accesor methods at all times.
