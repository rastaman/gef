// Copyright (c) 1996-99 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

package org.tigris.gef.xml.pgml;

import java.util.*;
import java.awt.*;
import java.io.*;
import java.net.URL;

import org.tigris.gef.base.*;
import org.tigris.gef.presentation.*;
import org.tigris.gef.graph.presentation.*;
import org.tigris.gef.graph.*;

import org.tigris.gef.xml.*;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.xml.sax.*;


public class PGMLParser extends HandlerBase {

  ////////////////////////////////////////////////////////////////
  // static variables

  public static PGMLParser SINGLETON = new PGMLParser();

  ////////////////////////////////////////////////////////////////
  // instance variables

  protected Diagram   _diagram = null;
  protected int       _nestedGroups = 0;
  protected HashMap _figRegistry = null;
  protected Map _ownerRegistry = new HashMap();

  ////////////////////////////////////////////////////////////////
  // constructors

  protected PGMLParser() {
  }

  ////////////////////////////////////////////////////////////////
  // main parsing methods

  public synchronized Diagram readDiagram(URL url) {
    try {
      InputStream is = url.openStream();
      String filename = url.getFile();
      System.out.println("=======================================");
      System.out.println("== READING DIAGRAM: " + url);
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(false);
      factory.setValidating(false);
      initDiagram("org.tigris.gef.base.Diagram");
      _figRegistry = new HashMap();
      SAXParser pc = factory.newSAXParser();
      pc.parse(is,this);
      is.close();
      return _diagram;
    }
    catch (Exception ex) {
      System.out.println("Exception in readDiagram");
      ex.printStackTrace();
    }
    return null;
  }

  ////////////////////////////////////////////////////////////////
  // accessors

  public void setOwnerRegistry(Map owners) {
    _ownerRegistry = owners;
  }

  ////////////////////////////////////////////////////////////////
  // internal methods

  protected void initDiagram(String diagDescr) {
      String clsName = diagDescr;
      String initStr = null;
      int bar = diagDescr.indexOf("|");
      if (bar != -1) {
	  clsName = diagDescr.substring(0, bar);
	  initStr = diagDescr.substring(bar + 1);
      }

      String newClassName = translateClassName(clsName);
      try {
	  Class cls = Class.forName(newClassName);
	  _diagram = (Diagram) cls.newInstance();

	  if (initStr != null && !initStr.equals(""))
	      _diagram.initialize(findOwner(initStr));
      }
      catch (Exception ex) {
	  System.out.println("could not set diagram type to " + newClassName);
	  ex.printStackTrace();
      }
  }


  ////////////////////////////////////////////////////////////////
  // XML element handlers
  private int _elementState = 0;
  private static final int DEFAULT_STATE = 0;
  private static final int TEXT_STATE = 1;
  private static final int LINE_STATE = 2;
  private static final int POLY_STATE = 3;
  private static final int NODE_STATE = 4;
  private static final int PRIVATE_STATE = 5;
  public void startElement(String elementName,AttributeList attrList) {
    switch(_elementState) {
        case DEFAULT_STATE:
        if ("group".equals(elementName)) {
            _nestedGroups++;
            _diagram.add(handleGroup(attrList));
        }
        else if (elementName.equals("pgml")) {
            handlePGML(attrList);
        }
        else if (_nestedGroups == 0) {
	    if (elementName.equals("path")) {
	        _diagram.add(handlePolyLine(attrList));
	    }
	    else if (elementName.equals("ellipse")) {
	        _diagram.add(handleEllipse(attrList));
	    }
	    else if (elementName.equals("rectangle")) {
	        _diagram.add(handleRect(attrList));
	    }
	    else if (elementName.equals("text")) {
	        _diagram.add(handleText(attrList));
	    }
	    else if (elementName.equals("piewedge")) { }
	    else if (elementName.equals("circle")) { }
	    else if (elementName.equals("moveto")) { }
	    else if (elementName.equals("lineto")) { }
	    else if (elementName.equals("curveto")) { }
	    else if (elementName.equals("arc")) { }
	    else if (elementName.equals("closepath")) { }
	    else System.out.println("unknown top-level tag: " + elementName);
        }
        else if (_nestedGroups > 0) {
	//System.out.println("skipping nested " + elementName);
        }
        break;

        case LINE_STATE:
        lineStateStartElement(elementName,attrList);
        break;

        case POLY_STATE:
        polyStateStartElement(elementName,attrList);
        break;

        case NODE_STATE:
        nodeStateStartElement(elementName,attrList);
        break;
    }
  }

  public void endElement(String elementName) {
    switch(_elementState) {
        case 0:
        if ("group".equals(elementName)) {
            _nestedGroups--;
        }
        break;

        case POLY_STATE:
        if(elementName.equals("path")) {
            _elementState = DEFAULT_STATE;
            _currentPoly = null;
        }
        break;

        case LINE_STATE:
        if(elementName.equals("line")) {
            _elementState = DEFAULT_STATE;
            _currentLine = null;
        }
        break;

        case TEXT_STATE:
        if(elementName.equals("text")) {
            _currentText.setText(_textBuf.toString());
            _elementState = DEFAULT_STATE;
            _currentText = null;
            _textBuf = null;
        }
        break;

        case NODE_STATE:
            _elementState = DEFAULT_STATE;
            _currentNode = null;
            _textBuf = null;
        break;

        case PRIVATE_STATE:
            privateStateEndElement(elementName);
            _textBuf = null;
            _elementState = NODE_STATE;
    }
  }

  public void characters(char[] ch,
                       int start,
                       int length) {
    if((_elementState == TEXT_STATE || _elementState == PRIVATE_STATE) &&
        _textBuf != null) {
        _textBuf.append(ch,start,length);
    }
  }

  protected void handlePGML(AttributeList attrList) {
    String name = attrList.getValue("name");
    String clsName = attrList.getValue("description");
    try {
      if (clsName != null && !clsName.equals("")) initDiagram(clsName);
      if (name != null && !name.equals("")) _diagram.setName(name);
    }
    catch (Exception ex) {
        System.out.println("Exception in handlePGML");
    }
  }

  protected Fig handlePolyLine(AttributeList attrList) {
    String clsName = translateClassName(attrList.getValue("description"));
    if (clsName != null && clsName.indexOf("FigLine") != -1) {
      return handleLine(attrList);
    }
    else {
      return handlePath(attrList);
    }
  }

  private FigLine _currentLine = null;
  private int _x1Int = 0;
  private int _y1Int = 0;
  protected FigLine handleLine(AttributeList attrList) {
    _currentLine = new FigLine(0, 0, 100, 100);
    setAttrs(_currentLine, attrList);
    _x1Int = 0;
    _y1Int = 0;
    _elementState = LINE_STATE;
    return _currentLine;
  }

  protected void lineStateStartElement(String tagName,AttributeList attrList) {
      if(_currentLine != null) {
          if(tagName.equals("moveto")) {
              String x1 = attrList.getValue("x");
              String y1 = attrList.getValue("y");
              _x1Int = (x1 == null || x1.equals("")) ? 0 : Integer.parseInt(x1);
              _y1Int = (y1 == null || y1.equals("")) ? 0 : Integer.parseInt(y1);
              _currentLine.setX1(_x1Int);
              _currentLine.setY1(_y1Int);
          }
          else if(tagName.equals("lineto")) {
            String x2 = attrList.getValue("x");
            String y2 = attrList.getValue("y");
            int x2Int = (x2 == null || x2.equals("")) ? _x1Int : Integer.parseInt(x2);
            int y2Int = (y2 == null || y2.equals("")) ? _y1Int : Integer.parseInt(y2);
            _currentLine.setX2(x2Int);
            _currentLine.setY2(y2Int);
          }
      }
  }


  protected FigCircle handleEllipse(AttributeList attrList) {
    FigCircle f = new FigCircle(0, 0, 50, 50);
    setAttrs(f, attrList);
    String rx = attrList.getValue("rx");
    String ry = attrList.getValue("ry");
    int rxInt = (rx == null || rx.equals("")) ? 10 : Integer.parseInt(rx);
    int ryInt = (ry == null || ry.equals("")) ? 10 : Integer.parseInt(ry);
    f.setX(f.getX() - rxInt);
    f.setY(f.getY() - ryInt);
    f.setWidth(rxInt * 2);
    f.setHeight(ryInt * 2);
    return f;
  }

  protected FigRect handleRect(AttributeList attrList) {
    FigRect f;
    String cornerRadius = attrList.getValue("rounding");
    if (cornerRadius == null || cornerRadius.equals("")) {
      f = new FigRect(0, 0, 80, 80);
    }
    else {
      f = new FigRRect(0, 0, 80, 80);
      int rInt = Integer.parseInt(cornerRadius);
      ((FigRRect)f).setCornerRadius(rInt);
    }
    setAttrs(f, attrList);
    return f;
  }

  private FigText _currentText = null;
  private StringBuffer _textBuf = null;
  protected FigText handleText(AttributeList attrList) {
    FigText f = new FigText(100, 100, 90, 45);
    setAttrs(f, attrList);
    _currentText = f;
    _elementState = TEXT_STATE;
    _textBuf = new StringBuffer();
//    String text = e.getText();
//    f.setText(text);
    String font = attrList.getValue("font");
    if (font != null && !font.equals("")) f.setFontFamily(font);
    String textsize = attrList.getValue("textsize");
    if (textsize != null && !textsize.equals("")) {
      int textsizeInt = Integer.parseInt(textsize);
      f.setFontSize(textsizeInt);
    }
    return f;
  }

  private FigPoly _currentPoly = null;
  protected FigPoly handlePath(AttributeList attrList) {
    FigPoly f = new FigPoly();
    setAttrs(f, attrList);
    _currentPoly = f;
    _elementState = POLY_STATE;
    return f;
  }


  private void polyStateStartElement(String tagName,AttributeList attrList) {
    if(_currentPoly != null) {
	int xInt = 0;
	int yInt = 0;
        String x = attrList.getValue("x");
        if (x != null && !x.equals("")) xInt = Integer.parseInt(x);
        String y = attrList.getValue("y");
        if (y != null && !y.equals("")) yInt = Integer.parseInt(y);
        //needs-more-work: dx, dy
        _currentPoly.addPoint(xInt, yInt);
    }
  }

  private FigNode _currentNode = null;
  /* Returns Fig rather than FigGroups because this is also
     used for FigEdges. */
  protected Fig handleGroup(AttributeList attrList) {
    Fig f = null;
    String clsNameBounds = attrList.getValue("description");
    StringTokenizer st = new StringTokenizer(clsNameBounds, ",;[] ");
    String clsName = translateClassName(st.nextToken());
    String xStr = null, yStr = null, wStr = null, hStr = null;
    if (st.hasMoreElements()) {
      xStr = st.nextToken();
      yStr = st.nextToken();
      wStr = st.nextToken();
      hStr = st.nextToken();
    }
    try {
      Class nodeClass = Class.forName(translateClassName(clsName));
      f = (Fig) nodeClass.newInstance();
      if (xStr != null && !xStr.equals("")) {
	int x = Integer.parseInt(xStr);
	int y = Integer.parseInt(yStr);
	int w = Integer.parseInt(wStr);
	int h = Integer.parseInt(hStr);
	f.setBounds(x, y, w, h);
      }
      if (f instanceof FigNode) {
	FigNode fn = (FigNode) f;
        _currentNode = fn;
        _elementState = NODE_STATE;
        _textBuf = new StringBuffer();
      }
      if (f instanceof FigEdge) {
	_currentEdge = (FigEdge) f;
      }
    }
    catch (Exception ex) {
      System.out.println("Exception in handleGroup");
      ex.printStackTrace();
    }
    catch (NoSuchMethodError ex) {
      System.out.println("No constructor() in class " + clsName);
      ex.printStackTrace();
    }
    setAttrs(f, attrList);
    return f;
  }

    private void privateStateEndElement(String tagName) {
        if(_currentNode != null) {
            String body = _textBuf.toString();
            StringTokenizer st2 = new StringTokenizer(body, "=\"' \t\n");
            Fig encloser = null;
            while (st2.hasMoreElements()) {
                String t = st2.nextToken();
                String v = "no such fig";
                if (st2.hasMoreElements()) v = st2.nextToken();
                if (t.equals("enclosingFig")) encloser = findFig(v);
            }
            _currentNode.setEnclosingFig(encloser);
        }
        if(_currentEdge != null) {
            Fig spf = null;
            Fig dpf = null;
            FigNode sfn = null;
            FigNode dfn = null;
            String body = _textBuf.toString();
            StringTokenizer st2 = new StringTokenizer(body, "=\"' \t\n");
            while (st2.hasMoreElements()) {
                String t = st2.nextToken();
                String v = st2.nextToken();
                if (t.equals("sourcePortFig")) spf = findFig(v);
                if (t.equals("destPortFig")) dpf = findFig(v);
                if (t.equals("sourceFigNode")) sfn = (FigNode) findFig(v);
                if (t.equals("destFigNode")) dfn = (FigNode) findFig(v);
            }
            _currentEdge.setSourcePortFig(spf);
            _currentEdge.setDestPortFig(dpf);
            _currentEdge.setSourceFigNode(sfn);
            _currentEdge.setDestFigNode(dfn);
        }
    }

  private void nodeStateStartElement(String tagName,AttributeList attrList) {
    if (tagName.equals("private")) {
        _textBuf = new StringBuffer();
        _elementState = PRIVATE_STATE;
    }
  }

    private FigEdge _currentEdge = null;
    private void edgeModeStartElement(String tagName,AttributeList attrList)
    {
        if (tagName.equals("path")) {
            Fig p = handlePath(attrList);
            _currentEdge.setFig(p);
            ((FigPoly)p)._isComplete = true;
            _currentEdge.calcBounds();
            if (_currentEdge instanceof FigEdgePoly) {
                ((FigEdgePoly)_currentEdge).setInitiallyLaidOut(true);
            }
        }
        else if (tagName.equals("private")) {
            _elementState = PRIVATE_STATE;
            _textBuf = new StringBuffer();
        }
    }

  ////////////////////////////////////////////////////////////////
  // internal parsing methods

  protected void setAttrs(Fig f, AttributeList attrList) {
    String name = attrList.getValue("name");
    if (name != null && !name.equals("")) _figRegistry.put(name, f);
    String x = attrList.getValue("x");
    if (x != null && !x.equals("")) {
      String y = attrList.getValue("y");
      String w = attrList.getValue("width");
      String h = attrList.getValue("height");
      int xInt = Integer.parseInt(x);
      int yInt = (y == null || y.equals("")) ? 0 : Integer.parseInt(y);
      int wInt = (w == null || w.equals("")) ? 20 : Integer.parseInt(w);
      int hInt = (h == null || h.equals("")) ? 20 : Integer.parseInt(h);
      f.setBounds(xInt, yInt, wInt, hInt);
    }
    String linewidth = attrList.getValue("stroke");
    if (linewidth != null && !linewidth.equals("")) {
      f.setLineWidth(Integer.parseInt(linewidth));
    }
    String strokecolor = attrList.getValue("strokecolor");
    if (strokecolor != null && !strokecolor.equals(""))
      f.setLineColor(colorByName(strokecolor, Color.blue));

    String fill = attrList.getValue("fill");
    if (fill != null && !fill.equals(""))
      f.setFilled(fill.equals("1") || fill.startsWith("t"));
    String fillcolor = attrList.getValue("fillcolor");
    if (fillcolor != null && !fillcolor.equals(""))
      f.setFillColor(colorByName(fillcolor, Color.white));

    String dasharray = attrList.getValue("dasharray");
    if (dasharray != null && !dasharray.equals("") &&
	!dasharray.equals("solid"))
      f.setDashed(true);

    String dynobjs = attrList.getValue("dynobjects");
    if (dynobjs != null && dynobjs.length() != 0) {
      if (f instanceof FigGroup) {
        FigGroup fg = (FigGroup) f;
        fg.parseDynObjects(dynobjs);
      }
    }

    try {
      String owner = attrList.getValue("href");
      if (owner != null && !owner.equals("")) f.setOwner(findOwner(owner));
    }
    catch (Exception ex) {
      System.out.println("could not set owner");
    }
  }


  //needs-more-work: find object in model
  protected Object findOwner(String uri) {
    Object own = _ownerRegistry.get(uri);
    return own;
  }

  protected Fig findFig(String uri) {
    Fig f = null;
    if (uri.indexOf(".") == -1) {
      f = (Fig) _figRegistry.get(uri);
    }
    else {
      StringTokenizer st = new StringTokenizer(uri, ".");
      String figNum = st.nextToken();
      f = (Fig) _figRegistry.get(figNum);
      if (f instanceof FigEdge) return ((FigEdge)f).getFig();
      while (st.hasMoreElements()) {
	if (f instanceof FigGroup) {
	  String subIndex = st.nextToken();
	  int i = Integer.parseInt(subIndex);
	  f = (Fig)((FigGroup)f).getFigs().elementAt(i);
	}
      }
    }
    return f;
  }

  //needs-more-work: make an instance of the named class
  protected GraphModel getGraphModelFor(String desc) {
	 System.out.println("should be: "+desc);
    return new DefaultGraphModel();
  }

  protected Color colorByName(String name, Color defaultColor) {
    if (name.equalsIgnoreCase("white")) return Color.white;
    if (name.equalsIgnoreCase("lightGray")) return Color.lightGray;
    if (name.equalsIgnoreCase("gray")) return Color.gray;
    if (name.equalsIgnoreCase("darkGray")) return Color.darkGray;
    if (name.equalsIgnoreCase("black")) return Color.black;
    if (name.equalsIgnoreCase("red")) return Color.red;
    if (name.equalsIgnoreCase("pink")) return Color.pink;
    if (name.equalsIgnoreCase("orange")) return Color.orange;
    if (name.equalsIgnoreCase("yellow")) return Color.yellow;
    if (name.equalsIgnoreCase("green")) return Color.green;
    if (name.equalsIgnoreCase("magenta")) return Color.magenta;
    if (name.equalsIgnoreCase("cyan")) return Color.cyan;
    if (name.equalsIgnoreCase("blue")) return Color.blue;
    try { return Color.decode(name); }
    catch (Exception ex) {
      System.out.println("invalid color code string: " + name);
    }
    return defaultColor;
  }

  protected String translateClassName(String oldName) {
    return oldName;
  }

  private String[] _entityPaths = { "/org/tigris/gef/xml/dtd/" };
  protected String[] getEntityPaths() {
    return _entityPaths;
  }


   public InputSource resolveEntity(java.lang.String publicId,
                                 java.lang.String systemId) {
        InputSource source = null;
        try {
            java.net.URL url = new java.net.URL(systemId);
            try {
	        source = new InputSource(url.openStream());
            source.setSystemId(systemId);
	        if (publicId != null) source.setPublicId(publicId);
            }
            catch (java.io.IOException e) {
	        if (systemId.endsWith(".dtd")) {
                    int i = systemId.lastIndexOf('/');
                    i++;	// go past '/' if there, otherwise advance to 0
                    String[] entityPaths = getEntityPaths();
                    InputStream is = null;
                    for(int pathIndex = 0; pathIndex < entityPaths.length && is == null; pathIndex++) {
                        String DTD_DIR = entityPaths[pathIndex];
                        is = getClass().getResourceAsStream(DTD_DIR + systemId.substring(i));
                        if(is == null) {
                            try {
                                is = new FileInputStream(DTD_DIR.substring(1) + systemId.substring(i));
                            }
                            catch(Exception ex) {}
                        }
                    }
                    if(is != null) {
                        source = new InputSource(is);
                        source.setSystemId(systemId);
                        if(publicId != null) source.setPublicId(publicId);
                    }
                }
            }
	}
        catch(Exception ex) {
        }

        //
        //   returning an "empty" source is better than failing
        //
        if(source == null) {
            source = new InputSource();
            source.setSystemId(systemId);
        }
        return source;
   }

} /* end class PGMLParser */
