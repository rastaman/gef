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

package org.tigris.gef.xml.svg;

import java.util.*;
import java.awt.*;
import java.io.*;
import java.net.URL;

//import com.ibm.xml.parser.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

import org.tigris.gef.base.*;
import org.tigris.gef.presentation.*;
import org.tigris.gef.graph.*;
import org.tigris.gef.graph.presentation.*;
import org.tigris.gef.xml.*;
import org.xml.sax.*;

public class SVGParser extends HandlerBase {

  ////////////////////////////////////////////////////////////////
  // static variables

  public static SVGParser SINGLETON = new SVGParser();

  ////////////////////////////////////////////////////////////////
  // instance variables

  protected Diagram _diagram = null;
  protected int _nestedGroups = 0;
  protected Hashtable _figRegistry;
  protected Hashtable _ownerRegistry;

  ////////////////////////////////////////////////////////////////
  // constructors

  protected SVGParser() { }
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
  //needs-more-work: find object in model
  protected Object findOwner(String uri) {
	Object own = _ownerRegistry.get(uri);
	return own;
  }
  //needs-more-work: make an instance of the named class
  protected GraphModel getGraphModelFor(String desc) {
	return new DefaultGraphModel();
  }

  private int _state = 0;
  private static final int DEFAULT_STATE = 0;
  private static final int FIG_STATE = 1;
  private static final int TEXT_STATE = 2;
  private static final int FOREIGN_STATE = 3;

    public void startElement(String elementName,AttributeList attrList) {
        try {
            switch(_state) {
                case DEFAULT_STATE:
                if (elementName.equals("svg")) {
                    handleSVG(attrList);
                }
                else if (elementName.equals("g")) {
                    _nestedGroups++;
                    _diagram.add(handleGroup(attrList));
                }
                else if (_nestedGroups == 0) {
                    if (elementName.equals("path")) {
                        _diagram.add(handlePath(attrList));
                    }
                    else if (elementName.equals("ellipse")) {
                        _diagram.add(handleEllipse(attrList));
                    }
                    else if (elementName.equals("rect")) {
                        _diagram.add(handleRect(attrList));
                    }
                    else if (elementName.equals("text")) {
                        _diagram.add(handleText(attrList));
                    }
                    else if (elementName.equals("line")) {
                        _diagram.add(handleLine(attrList));
                    }
                    else {
                        System.out.println("unknown top-level tag: " + elementName);
                    }
                }
                else if (_nestedGroups > 0) {
                    //System.out.println("skipping nested " + elementName);
                }
                break;

                case FIG_STATE:
                figStartElement(elementName,attrList);
                break;
            }
        }
        catch (Exception ex) {
            System.out.println("Exception in SVGParser handleElement");
            ex.printStackTrace();
        }
    }

    public void characters(char[] chars,int offset,int len) {
        switch(_state) {
            case FOREIGN_STATE:
            foreignCharacters(chars,offset,len);
            break;

            case TEXT_STATE:
            textCharacters(chars,offset,len);
            break;
        }
    }

  protected FigCircle handleEllipse(AttributeList attrList) {
	FigCircle f = new FigCircle(0, 0, 50, 50);
	setAttrs(f, attrList);

	String cx = attrList.getValue("cx");
	String cy = attrList.getValue("cy");
	String rx = attrList.getValue("rx");
	String ry = attrList.getValue("ry");

	int cxInt = (cx == null || cx.equals("")) ? 0  : Integer.parseInt(cx);
	int cyInt = (cy == null || cy.equals("")) ? 0  : Integer.parseInt(cy);
	int rxInt = (rx == null || rx.equals("")) ? 10 : Integer.parseInt(rx);
	int ryInt = (ry == null || ry.equals("")) ? 10 : Integer.parseInt(ry);

	f.setX( cxInt - rxInt );
	f.setY( cyInt - ryInt );
	f.setWidth(rxInt * 2);
	f.setHeight(ryInt * 2);
	return f;
  }

    public void endElement(String elementName) {
        switch(_state) {
            case DEFAULT_STATE:
	    if ("g".equals(elementName)) _nestedGroups--;
            break;

            case FIG_STATE:
            figEndElement(elementName);
            break;

            case FOREIGN_STATE:
            foreignEndElement(elementName);
            break;

            case TEXT_STATE:
            textEndElement(elementName);
            break;
        }
    }

  private FigNode _fn = null;
  private FigEdge _fe = null;

  /* Returns Fig rather than FigGroups because this is also
 used for FigEdges. */
    protected Fig handleGroup(AttributeList attrList) {
	Fig f = null;
	String clsNameBounds = attrList.getValue("class");
	StringTokenizer st = new StringTokenizer(clsNameBounds, ",;[] ");
	String clsName = st.nextToken();
	String xStr = null, yStr = null, wStr = null, hStr = null;
	if (st.hasMoreElements()) {
		xStr = st.nextToken();
		yStr = st.nextToken();
		wStr = st.nextToken();
		hStr = st.nextToken();
	}
	try {
            Class nodeClass = Class.forName(clsName);
            f = (Fig) nodeClass.newInstance();
            if (xStr != null && !xStr.equals("")) {
                    int x = Integer.parseInt(xStr);
                    int y = Integer.parseInt(yStr);
                    int w = Integer.parseInt(wStr);
                    int h = Integer.parseInt(hStr);
                    f.setBounds(x, y, w, h);
            }
            if (f instanceof FigNode) {
                _fn = (FigNode) f;
                _fe = null;
                _foreignBuffer = null;
                _foreignCount = 0;
                _state = FIG_STATE;
            }
            if (f instanceof FigEdge) {
                _fn = null;
                _fe = (FigEdge) f;
                _foreignBuffer = null;
                _foreignCount = 0;
                _state = FIG_STATE;
            }
	} catch (Exception ex) {
		System.out.println("Exception in handleGroup");
		ex.printStackTrace();
	} catch (NoSuchMethodError ex) {
		System.out.println("No constructor() in class " + clsName);
		ex.printStackTrace();
	}
	return f;
}

    private StringBuffer _foreignBuffer = null;
    private int _foreignCount = 0;
    private void figStartElement(String tagName,AttributeList attrList) {
        if(tagName.equals("foreignObject")) {
            _state = FOREIGN_STATE;
            _foreignCount++;
            _foreignBuffer = new StringBuffer();
        }
    }

    private void foreignCharacters(char[] chars,int start,int length) {
        _foreignBuffer.append(chars,start,length);
    }

    private void foreignEndElement(String tagName) {
        if(tagName.equals("foreignObject")) {
            _state = FIG_STATE;
        }
    }

    private void figEndElement(String tagName) {
        if(_foreignCount == 1 && _foreignBuffer != null) {
            String data = _foreignBuffer.toString();
            if(_fn != null) {
                _fn.setPrivateData(data);
            }
            else {
                _fe.setPrivateData(data);
            }
        }
        _foreignBuffer = null;
        _fn = null;
        _fe = null;
        _state = DEFAULT_STATE;
    }

  protected FigLine handleLine(AttributeList e) {
	FigLine f = new FigLine(0, 0, 100, 100);
	setAttrs(f, e);

	// Get the start and end
	String x1 = e.getValue("x1");
	String y1 = e.getValue("y1");
	String x2 = e.getValue("x2");
	String y2 = e.getValue("y2");

	// Convert to integers
	int x1Int = (x1 == null || x1.equals("")) ? 0 : Integer.parseInt(x1);
	int y1Int = (y1 == null || y1.equals("")) ? 0 : Integer.parseInt(y1);
	int x2Int = (x2 == null || x2.equals("")) ? x1Int : Integer.parseInt(x2);
	int y2Int = (y2 == null || y2.equals("")) ? y1Int : Integer.parseInt(y2);

	// Set the values
	f.setX1(x1Int);
	f.setY1(y1Int);
	f.setX2(x2Int);
	f.setY2(y2Int);

	return f;
  }
  protected FigPoly handlePath(AttributeList e) {
	String type = e.getValue("class");

	FigPoly f = null;
	if (type.equals("org.tigris.gef.presentation.FigPoly"))
	{
		f = new FigPoly();
	}
	else if (type.equals("org.tigris.gef.presentation.FigSpline"))
	{
		f = new FigSpline();
	}
	else if (type.equals("org.tigris.gef.presentation.FigInk"))
	{
		f = new FigInk();
	}
	if (f != null)
	{
		// Set the default attributes
		setAttrs(f, e);

		// Set the path data
		String path = e.getValue("d");
		int	x = -1;
		int	y = -1;

		try	{
			StringReader reader = new StringReader(path);
			StreamTokenizer tokenizer = new StreamTokenizer(reader);

			int tok = tokenizer.nextToken();
			while (tok != StreamTokenizer.TT_EOF)
			{
				if (tok == StreamTokenizer.TT_NUMBER)
				{
					if (x == -1)
					{
						x = (int)tokenizer.nval;
					}
					else
					{
						y = (int)tokenizer.nval;
					}

					// Add the point
					if (x != -1 && y != -1)
					{
						f.addPoint(x,y);
						x = -1;
						y = -1;
					}
				}

				tok = tokenizer.nextToken();
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	return f;
  }
  protected FigRect handleRect(AttributeList e) {
	FigRect f;
	String cornerRadius = e.getValue("rx");
	if (cornerRadius == null || cornerRadius.equals("")) {
	  f = new FigRect(0, 0, 80, 80);
	}
	else {
	  f = new FigRRect(0, 0, 80, 80);
	  int rInt = Integer.parseInt(cornerRadius);
	  ((FigRRect)f).setCornerRadius(rInt);
	}
	setAttrs(f, e);
	return f;
  }
  ////////////////////////////////////////////////////////////////
  // XML element handlers

  protected void handleSVG(AttributeList e) {
	String name = e.getValue("gef:name");
	String clsName = e.getValue("class");
	try {
	  if (clsName != null && !clsName.equals("")) initDiagram(clsName);
	  if (name != null && !name.equals("")) _diagram.setName(name);
	}
	catch (Exception ex) { System.out.println("Exception in handleSVG"); }
  }

  private FigText _ft = null;
  private StringBuffer _textBuffer = null;
  protected FigText handleText(AttributeList e) {
	FigText f = new FigText(100, 100, 90, 45);
	setAttrs(f, e);
        _state = TEXT_STATE;
        _textBuffer = new StringBuffer();
        _ft = f;
	String style = e.getValue("style");
	if (style != null)
	{
		String font = parseStyle("font",style);
		if (font != null)
		{
                    f.setFontFamily(font);
		}
		String size = parseStyle("font-size",style);
		if (size != null)
		{
			int s = Integer.parseInt( size );
			f.setFontSize( s );
		}
	}
	return f;
  }

  void textCharacters(char[] chars,int offset, int len) {
    _textBuffer.append(chars,offset,len);
  }

  void textEndElement(String tagName) {
    _ft.setText(_textBuffer.toString());
    _ft = null;
    _textBuffer = null;
    _state = DEFAULT_STATE;
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
	try {
	  Class cls = Class.forName(clsName);
	  _diagram = (Diagram) cls.newInstance();
	  if (initStr != null && !initStr.equals(""))
	_diagram.initialize(findOwner(initStr));
	}
	catch (Exception ex) {
	  System.out.println("could not set diagram type to " + clsName);
	  ex.printStackTrace();
	}
  }
  protected Color parseColor(String name, Color defaultColor) {
	try {
		int start = name.indexOf("rgb",0);
		if (start != -1)
		{
			start = name.indexOf("(",start);
			int end = name.indexOf( ",", start);
			if (start != -1)
			{
				start++;
				int red		= Integer.parseInt(name.substring( start, end ).trim());
				start 		= end+1;
				end 		= name.indexOf( ",", start);
				int green	= Integer.parseInt(name.substring( start, end ).trim());
				start 		= end+1;
				end 		= name.indexOf( ")", start);
				int blue	= Integer.parseInt(name.substring( start, end ).trim());

				return new Color(red, green, blue);
			}
			return defaultColor;
		}
	} catch (Exception ex) {
		System.out.println("invalid rgb() sequence: " + name);
		return defaultColor;
	}

	if (name.equalsIgnoreCase("none")) return null;
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

	try {
		return Color.decode(name);
	} catch (Exception ex) {
	  System.out.println("invalid color code string: " + name);
	}
	return defaultColor;
  }
/**
 * This method parses the 'style' attribute for a particular field
 * @return java.lang.String
 * @param field java.lang.String
 * @param style java.lang.String
 */
protected String parseStyle(String field, String style) {
	field += ":";
	int start = style.indexOf(field,0);
	if ( start != -1 )
	{
		int end = style.indexOf(";",start);
		if ( end != -1 )
		{
			return style.substring( start+field.length(), end ).trim();
		}
		else
		{
			return style.substring( start+field.length(), style.length()-1 ).trim();
		}
	}

	return null;
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
        SAXParser pc = factory.newSAXParser();
        initDiagram("org.tigris.gef.base.Diagram");
        _figRegistry = new Hashtable();
        InputSource source = new InputSource(is);
        source.setSystemId(url.toString());
        pc.parse(source,this);

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
  // internal parsing methods

  protected void setAttrs(Fig f, AttributeList e) {
	String name = e.getValue("name");
	if (name != null && !name.equals("")) _figRegistry.put(name, f);
	String x = e.getValue("x");
	if (x != null && !x.equals("")) {
	  String y = e.getValue("y");
	  String w = e.getValue("width");
	  String h = e.getValue("height");
	  int xInt = Integer.parseInt(x);
	  int yInt = (y == null || y.equals("")) ? 0 : Integer.parseInt(y);
	  int wInt = (w == null || w.equals("")) ? 20 : Integer.parseInt(w);
	  int hInt = (h == null || h.equals("")) ? 20 : Integer.parseInt(h);
	  f.setBounds(xInt, yInt, wInt, hInt);
	}

	// Parse Style
	String style = e.getValue("style");
	if (style != null)
	{
		String linewidth = parseStyle("stroke-width",style);
		if (linewidth != null && !linewidth.equals("")) {
		  f.setLineWidth(Integer.parseInt(linewidth));
		}
		String strokecolor = parseStyle("stroke",style);
		if (strokecolor != null && !strokecolor.equals(""))
		  f.setLineColor( parseColor(strokecolor, Color.blue ) );

//		String fill = e.getAttribute("fill-texture");
//		if (fill != null && !fill.equals(""))
//		  f.setFilled(fill.equals("1") || fill.startsWith("t"));

		String fillcolor = parseStyle("fill",style);
		if (fillcolor != null && !fillcolor.equals(""))
		  f.setFillColor( parseColor(fillcolor, Color.blue ) );

		String dasharray = parseStyle("stroke-dash-array",style);
		if (dasharray != null && !dasharray.equals("") &&
		!dasharray.equals("1"))
		  f.setDashed(true);
	}

	try {
	  String owner = e.getValue("gef:href");
	  if (owner != null && !owner.equals("")) f.setOwner(findOwner(owner));
	}
	catch (Exception ex) {
	  System.out.println("could not set owner");
	}
  }
  ////////////////////////////////////////////////////////////////
  // accessors

  public void setOwnerRegistery(Hashtable owners) {
	_ownerRegistry = owners;
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

} /* end class SVGParser */
