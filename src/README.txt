GEF vers 0.10.14 - 12 April 2005

Resolved Issues
===============

207  Fig.removeFromDiagram() should remove from any diagram
208  Postscript eps doesn't draw dashed lines
209  First character of text edit is lost on JRE1.5
210  Deprecate Enum



=========================================================================
=========================================================================

GEF vers 0.10.13 - 7 March 2005

Resolved Issues
===============

170  Unclear dialog title "Printing Selection"
206  Can't drag primitive figs



=========================================================================
=========================================================================

GEF vers 0.10.12 - 16 February 2005

Resolved Issues
===============

74   PGMLParser should not be singleton
123  Use batik for SVG
194  VectorSet should implement List interface
199  restrict figs being placed and moved into other figs
200  SVGWriter should extend Graphic2D, not Graphics
201  saving graphics into ps/eps doesn't work with guillemets
202  Saving graphics as ps/eps doesn't handle dashed lines
203  Three arg connect should be all Object
204  PGMLParser is hiding exceptions
205  Allow range expression for collections in tee files



=========================================================================
=========================================================================

GEF vers 0.10.11 - 23 January 2005

Resolved Issues
===============

The following issues have been fixed in release 0.10.11 of GEF



192  JGraph should provide a hashCode method
193  Save action for portable network graphics
196  Add an attribute list to the renderers to pass style attribu
197  Take SAXParserBase from ArgoUML
198  Enclosed figs can't be placed directly



=========================================================================
=========================================================================

GEF vers 0.10.10 - 1 January 2005

Resolved Issues
===============

The following issues have been fixed in release 0.10.10 of GEF



185  getPoints to getPoint conversion not completed  
186  MutableGraphSupport must take on some features of DefaultGra  
187  ModeModify should fire an event  
188  Creating and deleting primitive figs should fire an event  
190  Introduce Connecter interface  


Make sure that you replace any calls to the getPoints(int) with getPoint(int)


=========================================================================
=========================================================================

GEF vers 0.10.9 - 30 December 2004

Resolved Issues
===============

The following issues have been fixed in release 0.10.9 of GEF



100  Rename getPreferedSize()  
149  Colors are not saved correctly  
154  FigGroup should remember its own style.  
178  Allow multiple ocl tags on a single line in tee file  
181  Add a FigDiamond  
182  Allow registration of save action  
183  Add accessors to extend ModeCreatePolyEdge  


There have been some other minor changes converting Vector to List if
you have any trouble compiling simply change your return or argument type.



=========================================================================
=========================================================================

GEF vers 0.10.8 - 9 November 2004

Resolved Issues
===============

The following issues have been fixed in release 0.10.8 of GEF



168  Resetting NetList in DefaultGraphModel
169  New method to support mousewheel in JGraph's JScrollPane's J
172  ModeCreateEdge: getter for private variable
173  Improve error reporting from TemplateReader
174  VectorSet should implement the Set interface
175  Specifiy an equals method for JGraph




=========================================================================
=========================================================================

GEF vers 0.10.7 - 27 September 2004

Resolved Issues
===============

The following issues have been fixed in release 0.10.7 of GEF



46   FigTextEditor should stop editing when it loses focus  
120  PGMLParser uses deprecated API  
147  Make keyPressed more robust against deleted modes  
163  typo in FigText.setJustification  
164  NetEdge.dispose() Does not delete the edge from the model  
165  Fig writes debug output with System.out.println()  
166  Change GraphModel to return Lists  




=========================================================================
=========================================================================

GEF vers 0.10.6 - 5 September 2004

Resolved Issues
===============

The following issues have been fixed in release 0.10.6 of GEF


156  Get/Set scroll view position  
157  NPE when debug logging through ModeCreateEdge  
160  OCLEvaluator should not be singleton  
161  Dont allow null for port settings of edge  
162  Allow OCL expression to delegate wring to to some method  




=========================================================================
=========================================================================

GEF vers 0.10.5a - 10 August 2004

Resolved Issues
===============

The following issues have been fixed in release 0.10.5a of GEF


155 Bug in FigNode.dispose




=========================================================================
=========================================================================

GEF vers 0.10.5 - 9 August 2004

Resolved Issues
===============

The following issues have been fixed in release 0.10.5 of GEF

3   Improve usability of Issuezilla  
32  IZ subcomponent request  
46  FigTextEditor should stop editing when it loses focus  
61  FigGroup.addFig does not remove fig from old group  
92  postPlacement event  
98  Methods referred to in tee files should be clearly javadoced  
109 Defect on FigNode.getPortFigs()  
110 hitting invisible Figs  
113 Use Graphics2D to draw good dashed lines  
124 Document logging  
139 DefaultGraphModel not using Collection arguments  
142 DefaultGraphModel disposing element fix  
145 Rename any enum variables  
148 Honour the Visible flag while dragging FigNodes  
153 OCLEvaluator bad end of line render unbuildable under Debian  




=========================================================================
=========================================================================

GEF vers 0.10.4 - 11 June 2004

Resolved Issues
===============

The following issues have been fixed in release 0.10.4 of GEF


7    DELETE: FigActivation & FigDynPort - Argo specific classes  
13   assert keyword and Dbg.java
17   Starting editing of figtext requires extra key input
105  Special rules for connecting figs  
111  JGraphInternalPane doesn't acquire focus when it should  
122  paintAtHead should not assume Graphics2D  
126  Update build.xml for batik  
127  Delete LogManager class  
130  Arrow heads not appearing  
131  DefaultGraphModel edge connection fix  
132  SplitGraphPane  
134  Add executable to release  
135  Line end jumps on primitive figs  
137  Can't select diagonal  
138  Net level objects are never disposed of  



=========================================================================
=========================================================================

GEF vers 0.10.3

Resolved Issues
===============

The following issues have been fixed in release 0.10.3 of GEF


108  Bad hit area  
112  Tee file template mechanism can corrupt PGML save  
113  Use Graphics2D to draw good dashed lines  
114  Ugly arrowheads can be avoided by passing the complete line  
115  CmdSaveGraphics prints trace information on stdout  
116  base.LayerDiagram.paintContents(Graphics, FigPainter) doesn'  
119  FigText constructors use wrong defaults  




=========================================================================
=========================================================================

GEF vers 0.10.2

The version has been released in order to fix 2 major bugs introduced in vers 0.10.1

Resolved Issues
===============

The following issues have been fixed in release 0.10.2 of GEF

95   Decouple CmdCreateNode from the model  
96   Methods referred to in tee file should be reinstated  
97   getContents() should be reinstated returning a List  




=========================================================================
=========================================================================

GEF vers 0.10.1

Resolved Issues
===============

The following issues have been fixed in release 0.10.1 of GEF

47   Patches from freiheit.com  
60   selection fails with zoom != 100% 
65   NPE in VectorSet.toString
68   Scroll to view patch  
69   static final attributes should be public  
70   text on system out in PGML parser  
72   Use of ctrl-click goes against sun standards  
84   build.sh run does not work on linux  


The following issues have been marked as already works

5    FigTextEditor does not comply to zooming/scaling  


The following issues have been marked as invalid

21   ModeDragScroll related eventlistener exceptions  
57   area not calculated properly for saving images  


The following issues have been marked as wont-fix

6    log4j missing from CVS repository  
24   Problem with zooming in ArgoUML  
76   Update website with reqd JDK  


Deprecated methods and attributes
=================================
Version 0.10.1 continues the deprecation of attributes. There is also some deprecation of methods in favour of alternatives.

You would be recommended to adjust your software to use non-deprecated methods as soon aspossible as these deprecated attributes and methods may now be removed without further warning.




=========================================================================
=========================================================================

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
