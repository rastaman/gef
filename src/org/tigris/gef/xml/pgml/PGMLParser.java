// %1032269233760:org.tigris.gef.xml.pgml%
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

import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.tigris.gef.base.Diagram;

import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.graph.presentation.DefaultGraphModel;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigCircle;
import org.tigris.gef.presentation.FigEdge;
import org.tigris.gef.presentation.FigEdgePoly;
import org.tigris.gef.presentation.FigGroup;
import org.tigris.gef.presentation.FigLine;
import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.presentation.FigPoly;
import org.tigris.gef.presentation.FigRRect;
import org.tigris.gef.presentation.FigRect;
import org.tigris.gef.presentation.FigText;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PGMLParser extends DefaultHandler {
    ////////////////////////////////////////////////////////////////
    // instance variables
    protected Diagram _diagram = null;
    protected int _nestedGroups = 0;
    protected HashMap _figRegistry = null;
    protected Map _ownerRegistry = new HashMap();
    protected boolean _detectedFailure = false;
    protected String systemId = "";

    /**
     * A map of previously created colors mapped by an
     * RGB string description in the for "rrr ggg bbb"
     * where rrr = red value int ggg = green value int
     * and bbb = blue value int.
     */
    private static final Map USED_COLORS = new HashMap();
    
    ////////////////////////////////////////////////////////////////
    // constructors
    public PGMLParser() {
    }

    ////////////////////////////////////////////////////////////////
    // main parsing methods
    public synchronized Diagram readDiagram(URL url) {
        try {
            return readDiagram(url.openStream());
        }
        catch(IOException e) {
            System.out.println("Couldn't open InputStream! " + e);
            e.printStackTrace();
        }

        return null;
    }

    public synchronized Diagram readDiagram(InputStream is) {
        return readDiagram(is, true);
    }

    public synchronized Diagram readDiagram(InputStream is, boolean closeStream) {
        try {
            System.out.println("=======================================");
            System.out.println("== READING DIAGRAM");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setValidating(false);
            initDiagram("org.tigris.gef.base.Diagram");
            _figRegistry = new HashMap();
            SAXParser pc = factory.newSAXParser();
            InputSource source = new InputSource(is);
            source.setSystemId(systemId);
            // what is this for?
            // source.setSystemId(url.toString());
            pc.parse(source, this);
            // source = null;
            if(closeStream) {
                //System.out.println("closing stream now (in PGMLParser.readDiagram)");
                is.close();
            }
            else {
                //System.out.println("leaving stream OPEN!");
            }

            return _diagram;
        }
        catch(SAXException saxEx) {
            System.err.println("Exception in readDiagram");
            //
            //  a SAX exception could have been generated
            //    because of another exception.
            //    Get the initial exception to display the
            //    location of the true error
            Exception ex = saxEx.getException();
            if(ex == null) {
                saxEx.printStackTrace();
            }
            else {
                ex.printStackTrace();
            }
        }
        catch(Exception ex) {
            System.err.println("Exception in readDiagram");
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
        if(bar != -1) {
            clsName = diagDescr.substring(0, bar);
            initStr = diagDescr.substring(bar + 1);
        }

        String newClassName = translateClassName(clsName);
        try {
            Class cls = Class.forName(newClassName);
            _diagram = (Diagram)cls.newInstance();
            if(initStr != null && !initStr.equals("")) {
                _diagram.initialize(findOwner(initStr));
            }
        } catch(Exception ex) {
            //System.out.println("could not set diagram type to " + newClassName);
            ex.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////
    // XML element handlers
    protected int _elementState = 0;
    protected static final int DEFAULT_STATE = 0;
    protected static final int TEXT_STATE = 1;
    protected static final int LINE_STATE = 2;
    protected static final int POLY_STATE = 3;
    protected static final int NODE_STATE = 4;
    protected static final int EDGE_STATE = 5;
    protected static final int PRIVATE_STATE = 6;
    protected static final int ANNOTATION_STATE = 7;
    protected static final int PRIVATE_NODE_STATE = 46;
    protected static final int PRIVATE_EDGE_STATE = 56;
    protected static final int ANNOTATION_EDGE_STATE = 57;
    protected static final int TEXT_NODE_STATE = 41;
    protected static final int TEXT_EDGE_STATE = 51;
    protected static final int TEXT_ANNOTATION_STATE = 71;
    protected static final int POLY_EDGE_STATE = 53;
    protected static final int POLY_NODE_STATE = 43;
    protected static final int DEFAULT_NODE_STATE = 40;
    protected static final int DEFAULT_EDGE_STATE = 50;

    public void startElement(String uri,
                String localname, 
                String elementName, 
                Attributes atts) throws SAXException {
        //System.out.println("[PGMLParser]: startElement " + elementName + " / " + _nestedGroups + " / " + _elementState);
        // moved here to compensate for groups that do not have default state...PJS
        if("group".equals(elementName)) {
            _nestedGroups++;
        }

        switch(_elementState) {

            case DEFAULT_STATE:
                if("group".equals(elementName)) {
                    Fig groupFig = handleGroup(atts);
                    if(groupFig != null && !_detectedFailure) {
                        _diagram.add(groupFig);
                    }
                }
                else if(elementName.equals("pgml")) {
                    handlePGML(atts);
                }
                else if(_nestedGroups == 0) {
                    if(elementName.equals("path")) {
                        _diagram.add(handlePolyLine(atts));
                    }
                    else if(elementName.equals("ellipse")) {
                        _diagram.add(handleEllipse(atts));
                    }
                    else if(elementName.equals("rectangle")) {
                        _diagram.add(handleRect(atts));
                    }
                    else if(elementName.equals("text")) {
                        _elementState = TEXT_STATE;
                        _textBuf = new StringBuffer();
                        _diagram.add(handleText(atts));
                    }
                    else if(elementName.equals("piewedge")) {
                    }
                    else if(elementName.equals("circle")) {
                    }
                    else if(elementName.equals("moveto")) {
                    }
                    else if(elementName.equals("lineto")) {
                    }
                    else if(elementName.equals("curveto")) {
                    }
                    else if(elementName.equals("arc")) {
                    }
                    else if(elementName.equals("closepath")) {
                    }
                    else {
                        System.out.println("unknown top-level tag: " + elementName);
                    }
                }
                else if(_nestedGroups > 0) {
                    //System.out.println("skipping nested " + elementName);
                }

                break;

            case LINE_STATE:
                lineStateStartElement(elementName, atts);
                break;

            case POLY_STATE:
                polyStateStartElement(elementName, atts);
                break;

            case POLY_EDGE_STATE:
                polyStateStartElement(elementName, atts);
                break;

            case NODE_STATE:
                nodeStateStartElement(elementName, atts);
                break;

            case EDGE_STATE:
                edgeStateStartElement(elementName, atts);
                break;

            case ANNOTATION_STATE:
                annotationStateStartElement(elementName, atts);
                break;
        }
    }

    public void endElement(String uri, String localname, String elementName) 
    throws SAXException {
        if("group".equals(elementName)) {
            _nestedGroups--;
        }

        switch(_elementState) {

            case 0:
                break;

            case POLY_STATE:
                if(elementName.equals("path")) {
                    _elementState = DEFAULT_STATE;
                    _currentPoly = null;
                }

                break;

            case LINE_STATE:
                if(elementName.equals("line") || elementName.equals("path")) {
                    _elementState = DEFAULT_STATE;
                    _currentLine = null;
                }

                break;

            case TEXT_STATE:
                //System.out.println("[PGMLParser]: endElement TEXT_STATE: " + elementName);
                if(elementName.equals("text")) {
                    _currentText.setText(_textBuf.toString());
                    _elementState = DEFAULT_STATE;
                    _currentText = null;
                    _textBuf = null;
                }

                break;

            case TEXT_NODE_STATE:
                //System.out.println("[PGMLParser]: endElement TEXT_NODE_STATE: " + _textBuf.toString());
                if(elementName.equals("text")) {
                    if(!_detectedFailure) {
                        _currentText.setText(_textBuf.toString());
                    }

                    _elementState = NODE_STATE;
                    _currentText = null;
                    _textBuf = null;
                }

                break;

            case TEXT_EDGE_STATE:
                //System.out.println("[PGMLParser]: endElement TEXT_EDGE_STATE: " + _textBuf.toString());
                if(elementName.equals("text")) {
                    if(!_detectedFailure) {
                        _currentText.setText(_textBuf.toString());
                    }

                    _elementState = EDGE_STATE;
                    _currentText = null;
                    _textBuf = null;
                }

                break;

            case TEXT_ANNOTATION_STATE:
                //System.out.println("[PGMLParser]: endElement TEXT_ANNOTATION_STATE: " + _textBuf.toString());
                if(elementName.equals("text")) {
                    //System.out.println("[GEF.PGMLParser] text annotation: "+_currentText.getBounds());
                    if(!_detectedFailure) {
                        _currentText.setJustification(FigText.JUSTIFY_LEFT);
                        _currentText.setText(_textBuf.toString());
                        _currentEdge.addAnnotation(_currentText, "text", _currentText.getContext());
                        _currentText.setJustification(FigText.JUSTIFY_CENTER);
                    }

                    _elementState = ANNOTATION_STATE;
                    _currentText = null;
                    _textBuf = null;
                }

                break;

            case POLY_EDGE_STATE:
                if(elementName.equals("path")) {
                    _elementState = EDGE_STATE;
                    _currentPoly = null;
                }

                break;

            case POLY_NODE_STATE:
                if(elementName.equals("path")) {
                    _elementState = DEFAULT_NODE_STATE;
                    _currentPoly = null;
                }

                break;

            case NODE_STATE:
                //System.out.println("[PGMLParser]: endElement NODE_STATE");
                //detect failure here
                if(!_detectedFailure) {
                    _currentNode.updateVisState();
                    if(_currentNode != null && _currentEncloser != null) {
                        _currentNode.setEnclosingFig(_currentEncloser);
                    }
                }
                else {
                    rollbackAdd(_currentNode);
                }

                _elementState = DEFAULT_STATE;
                setDetectedFailure(false);
                _currentNode = null;
                _currentEncloser = null;
                _textBuf = null;
                break;

            case EDGE_STATE:
                //System.out.println("[PGMLParser]: endElement EDGE_STATE");
                _elementState = DEFAULT_STATE;
                //detect failure here
                if(!_detectedFailure) {
                    _currentEdge.computeRoute();
                    _currentEdge.updateAnnotationPositions();
                }
                else {
                    rollbackAdd(_currentEdge);
                }

                setDetectedFailure(false);
                _currentEdge = null;
                _currentPoly = null;
                _textBuf = null;
                break;

            case ANNOTATION_STATE:
                //System.out.println("[PGMLParser]: endElement ANNOTATION_STATE");
                _elementState = EDGE_STATE;
                break;

            case PRIVATE_STATE:
                //System.out.println("[PGMLParser]: endElement PRIVATE_STATE");
                privateStateEndElement(elementName);
                _textBuf = null;
                _elementState = DEFAULT_STATE;
                break;

            case PRIVATE_NODE_STATE:
                //System.out.println("[PGMLParser]: endElement PRIVATE_NODE_STATE");
                if(!_detectedFailure) {
                    privateStateEndElement(elementName);
                }

                _textBuf = null;
                _elementState = NODE_STATE;
                break;

            case PRIVATE_EDGE_STATE:
                //System.out.println("[PGMLParser]: endElement PRIVATE_EDGE_STATE");
                if(!_detectedFailure) {
                    privateStateEndElement(elementName);
                }

                _textBuf = null;
                _elementState = EDGE_STATE;
                break;

            case DEFAULT_NODE_STATE:
                //System.out.println("[PGMLParser]: endElement DEFAULT_NODE_STATE");
                _elementState = NODE_STATE;
                _textBuf = null;
                break;

            case DEFAULT_EDGE_STATE:
                //System.out.println("[PGMLParser]: endElement DEFAULT_EDGE_STATE");
                _elementState = EDGE_STATE;
                _textBuf = null;
                break;
        }
    }

    public void characters(char[] ch, int start, int length) {
        if((_elementState == TEXT_STATE || _elementState == PRIVATE_STATE || _elementState == TEXT_NODE_STATE || _elementState == TEXT_EDGE_STATE || _elementState == TEXT_ANNOTATION_STATE || _elementState == PRIVATE_NODE_STATE || _elementState == PRIVATE_EDGE_STATE) && _textBuf != null) {
            _textBuf.append(ch, start, length);
        }
    }

    protected void handlePGML(Attributes attrList) {
        String name = attrList.getValue("name");
        String scale = attrList.getValue("scale");
        String clsName = attrList.getValue("description");
        String showSingleMultiplicity = attrList.getValue("showSingleMultiplicity");
        //System.out.println("name = " + name);
        //System.out.println("scale = " + scale);
        //System.out.println("despcription = " + clsName);
        //System.out.println("single Mult = " + showSingleMultiplicity);
        try {
            if(clsName != null && !clsName.equals("")) {
                initDiagram(clsName);
            }

            if(name != null && !name.equals("")) {
                _diagram.setName(name);
            }

            if(scale != null && !"".equals(scale)) {
                _diagram.setScale(Double.parseDouble(scale));
            }

            if(showSingleMultiplicity != null && !"".equals(showSingleMultiplicity)) {
                _diagram.setShowSingleMultiplicity(Boolean.valueOf(showSingleMultiplicity).booleanValue());
            }
        }
        catch(Exception ex) {
            System.out.println("Exception in handlePGML");
            //ex.printStackTrace();
        }
    }

    protected Fig handlePolyLine(Attributes attrList) {
        String clsName = translateClassName(attrList.getValue("description"));
        if(clsName != null && clsName.indexOf("FigLine") != -1) {
            return handleLine(attrList);
        }
        else {
            return handlePath(attrList);
        }
    }

    protected FigLine _currentLine = null;
    protected int _x1Int = 0;
    protected int _y1Int = 0;

    protected FigLine handleLine(Attributes attrList) {
        _currentLine = new FigLine(0, 0, 100, 100);
        setAttrs(_currentLine, attrList);
        _x1Int = 0;
        _y1Int = 0;
        _elementState = LINE_STATE;
        return _currentLine;
    }

    protected void lineStateStartElement(String tagName, Attributes attrList) {
        if(_currentLine != null) {
            if(tagName.equals("moveto")) {
                String x1 = attrList.getValue("x");
                String y1 = attrList.getValue("y");
                _x1Int = (x1 == null || x1.equals("")) ? 0 : Integer.parseInt(x1);
                _y1Int = (y1 == null || y1.equals("")) ? 0 : Integer.parseInt(y1);
                _currentLine.setX1(_x1Int);
                _currentLine.setY1(_y1Int);
                //System.out.println("[PGMLParser] lineStateStartElement: x1="+x1+" y1="+y1);
            }
            else if(tagName.equals("lineto")) {
                String x2 = attrList.getValue("x");
                String y2 = attrList.getValue("y");
                int x2Int = (x2 == null || x2.equals("")) ? _x1Int : Integer.parseInt(x2);
                int y2Int = (y2 == null || y2.equals("")) ? _y1Int : Integer.parseInt(y2);
                _currentLine.setX2(x2Int);
                _currentLine.setY2(y2Int);
                //System.out.println("[PGMLParser] lineStateStartElement: x2="+x2+" y2="+y2);
            }
        }
    }

    protected FigCircle handleEllipse(Attributes attrList) {
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

    protected FigRect handleRect(Attributes attrList) {
        FigRect f;
        String cornerRadius = attrList.getValue("rounding");
        if(cornerRadius == null || cornerRadius.equals("")) {
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
    protected StringBuffer _textBuf = null;

    protected FigText handleText(Attributes attrList) {
        FigText f = new FigText(100, 100, 90, 45);
        setAttrs(f, attrList);
        _currentText = f;
        //_elementState = TEXT_STATE;
        //_textBuf = new StringBuffer();
        //String text = e.getText();
        //f.setText(text);
        String font = attrList.getValue("font");
        if(font != null && !font.equals("")) {
            f.setFontFamily(font);
        }

        String textsize = attrList.getValue("textsize");
        if(textsize != null && !textsize.equals("")) {
            int textsizeInt = Integer.parseInt(textsize);
            f.setFontSize(textsizeInt);
        }

        return f;
    }

    protected FigPoly _currentPoly = null;

    protected FigPoly handlePath(Attributes attrList) {
        FigPoly f = new FigPoly();
        _elementState = POLY_STATE;
        setAttrs(f, attrList);
        _currentPoly = f;
        return f;
    }

    protected void polyStateStartElement(String tagName, Attributes attrList) {
        if(_currentPoly != null) {
            if(tagName.equals("moveto")) {
                String x1 = attrList.getValue("x");
                String y1 = attrList.getValue("y");
                _x1Int = (x1 == null || x1.equals("")) ? 0 : Integer.parseInt(x1);
                _y1Int = (y1 == null || y1.equals("")) ? 0 : Integer.parseInt(y1);
                //_currentLine.setX1(_x1Int);
                //_currentLine.setY1(_y1Int);
                _currentPoly.addPoint(_x1Int, _y1Int);
                //System.out.println("[PGMLParser] polyStateStartElement: x1="+x1+" y1="+y1);
            }
            else if(tagName.equals("lineto")) {
                String x2 = attrList.getValue("x");
                String y2 = attrList.getValue("y");
                int x2Int = (x2 == null || x2.equals("")) ? _x1Int : Integer.parseInt(x2);
                int y2Int = (y2 == null || y2.equals("")) ? _y1Int : Integer.parseInt(y2);
                //_currentLine.setX2(x2Int);
                //_currentLine.setY2(y2Int);
                _currentPoly.addPoint(x2Int, y2Int);
                //System.out.println("[PGMLParser] polyStateStartElement: x2="+x2+" y2="+y2);
            }
        }
        //System.out.println("[PGMLParser] polyStateStartElement: numPoints"+_currentPoly.getNumPoints());
    }

    protected FigNode _currentNode = null;

    /* Returns Fig rather than FigGroups because this is also
       used for FigEdges. */
    protected Fig handleGroup(Attributes attrList) throws SAXException {
        //System.out.println("[PGMLParser]: handleGroup: ");
        Fig f = null;
        String clsNameBounds = attrList.getValue("description");
        StringTokenizer st = new StringTokenizer(clsNameBounds, ",;[] ");
        String clsName = translateClassName(st.nextToken());
        String xStr = null;
        String yStr = null;
        String wStr = null;
        String hStr = null;
        if(st.hasMoreElements()) {
            xStr = st.nextToken();
            yStr = st.nextToken();
            wStr = st.nextToken();
            hStr = st.nextToken();
        }

        try {
            Class nodeClass = Class.forName(translateClassName(clsName));
            f = (Fig)nodeClass.newInstance();
            if(xStr != null && !xStr.equals("")) {
                int x = Integer.parseInt(xStr);
                int y = Integer.parseInt(yStr);
                int w = Integer.parseInt(wStr);
                int h = Integer.parseInt(hStr);
                f.setBounds(x, y, w, h);
            }

            if(f instanceof FigNode) {
                FigNode fn = (FigNode)f;
                _currentNode = fn;
                _elementState = NODE_STATE;
                _textBuf = new StringBuffer();
            }

            if(f instanceof FigEdge) {
                _currentEdge = (FigEdge)f;
                _elementState = EDGE_STATE;
            }
        } catch (ClassNotFoundException ex) {
            throw new SAXException(ex);
        } catch (InstantiationException ex) {
            throw new SAXException(ex);
        } catch (IllegalAccessException ex) {
            throw new SAXException(ex);
        }

        setAttrs(f, attrList);
        return f;
    }

    protected Fig _currentEncloser = null;

    protected void privateStateEndElement(String tagName) {
        try {
            if(_currentNode != null) {
                if(_currentEdge != null) {
                    _currentEdge = null;
                }

                String body = _textBuf.toString();
                StringTokenizer st2 = new StringTokenizer(body, "=\"' \t\n");
                while(st2.hasMoreElements()) {
                    String t = st2.nextToken();
                    String v = "no such fig";
                    if(st2.hasMoreElements()) {
                        v = st2.nextToken();
                    }

                    if(t.equals("enclosingFig")) {
                        _currentEncloser = findFig(v);
                    }
                }
            }

            if(_currentEdge != null) {
                Fig spf = null;
                Fig dpf = null;
                FigNode sfn = null;
                FigNode dfn = null;
                String body = _textBuf.toString();
                StringTokenizer st2 = new StringTokenizer(body, "=\"' \t\n");
                while(st2.hasMoreElements()) {
                    String t = st2.nextToken();
                    String v = st2.nextToken();
                    if(t.equals("sourcePortFig")) {
                        spf = findFig(v);
                    }

                    if(t.equals("destPortFig")) {
                        dpf = findFig(v);
                    }

                    if(t.equals("sourceFigNode")) {
                        sfn = (FigNode)findFig(v);
                    }

                    if(t.equals("destFigNode")) {
                        dfn = (FigNode)findFig(v);
                    }
                }

                if(spf == null || dpf == null || sfn == null || dfn == null) {
                    setDetectedFailure(true);
                }
                else {
                    _currentEdge.setSourcePortFig(spf);
                    _currentEdge.setDestPortFig(dpf);
                    _currentEdge.setSourceFigNode(sfn);
                    _currentEdge.setDestFigNode(dfn);
                }
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void nodeStateStartElement(String tagName, Attributes attrList) {
        //System.out.println("[PGMLParser]: nodeStateStartElement: " + tagName);
        if(tagName.equals("private")) {
            _textBuf = new StringBuffer();
            _elementState = PRIVATE_NODE_STATE;
        }
        else if(tagName.equals("text")) {
            _textBuf = new StringBuffer();
            _elementState = TEXT_NODE_STATE;
            Fig p;
            if(!_detectedFailure) {
                p = handleText(attrList);
            }
            //needs-more-work: FigText should be set at distinct position within surrounding
            // Fig, but this is not supported by Fig framework yet!
            //_currentNode.addFig(handleText(attrList));
        }
        else {
            _textBuf = new StringBuffer();
            _elementState = DEFAULT_NODE_STATE;
        }
    }

    protected FigEdge _currentEdge = null;

    protected void edgeStateStartElement(String tagName, Attributes attrList) {
        if(tagName.equals("path")) {
            if(!_detectedFailure) {
                Fig p = handlePath(attrList);
                _elementState = POLY_EDGE_STATE;
                _currentEdge.setFig(p);
                ((FigPoly)p)._isComplete = true;
                _currentEdge.calcBounds();
                //System.out.println("[PGMLParser]: edgeStateStartElement: cur= " + _currentEdge.getNumPoints());
                if(_currentEdge instanceof FigEdgePoly) {
                    ((FigEdgePoly)_currentEdge).setInitiallyLaidOut(true);
                }

                _currentEdge.updateAnnotationPositions();
            }
            else {
                _elementState = POLY_EDGE_STATE;
            }
        }
        else if(tagName.equals("private")) {
            _elementState = PRIVATE_EDGE_STATE;
            _textBuf = new StringBuffer();
        }
        else if(tagName.equals("annotations") || tagName.equals("anotations")) {
            _elementState = ANNOTATION_STATE;
            _textBuf = new StringBuffer();
            if(!_detectedFailure) {
                _currentEdge.initAnnotations();
            }
        }
        else if(tagName.equals("text")) {
            _elementState = TEXT_EDGE_STATE;
            _textBuf = new StringBuffer();
            Fig p;
            if(!_detectedFailure) {
                p = handleText(attrList);
            }
        }
        else {
            _textBuf = new StringBuffer();
            _elementState = DEFAULT_EDGE_STATE;
        }
    }

    public void annotationStateStartElement(String tagName, Attributes attrList) {
        if(tagName.equals("text")) {
            _elementState = TEXT_ANNOTATION_STATE;
            _textBuf = new StringBuffer();
            FigText p = handleText(attrList);
        }
    }

    ////////////////////////////////////////////////////////////////
    // internal parsing methods
    protected void setAttrs(Fig f, Attributes attrList) {
        String name = attrList.getValue("name");
        if(name != null && !name.equals("")) {
            _figRegistry.put(name, f);
        }

        String x = attrList.getValue("x");
        if(x != null && !x.equals("")) {
            String y = attrList.getValue("y");
            String w = attrList.getValue("width");
            String h = attrList.getValue("height");
            int xInt = Integer.parseInt(x);
            int yInt = (y == null || y.equals("")) ? 0 : Integer.parseInt(y);
            int wInt;
            int hInt;
            if(_elementState == TEXT_ANNOTATION_STATE) {
                wInt = (w == null || w.equals("")) ? 30 : Integer.parseInt(w);
                hInt = (h == null || h.equals("")) ? 30 : Integer.parseInt(h);
            }
            else {
                wInt = (w == null || w.equals("")) ? 20 : Integer.parseInt(w);
                hInt = (h == null || h.equals("")) ? 20 : Integer.parseInt(h);
            }

            f.setBounds(xInt, yInt, wInt, hInt);
            //System.out.println("[PGMLParser]: setAttrs: " + name);
            //System.out.println("[PGMLParser]: setAttrs: x="+x+" y="+y);
            //System.out.println("[PGMLParser]: setAttrs: w="+w+" h="+h);
        }

        String linewidth = attrList.getValue("stroke");
        if(linewidth != null && !linewidth.equals("")) {
            f.setLineWidth(Integer.parseInt(linewidth));
        }

        String strokecolor = attrList.getValue("strokecolor");
        if(strokecolor != null && !strokecolor.equals("")) {
            f.setLineColor(colorByName(strokecolor, Color.blue));
        }

        String fill = attrList.getValue("fill");
        if(fill != null && !fill.equals("")) {
            f.setFilled(fill.equals("1") || fill.startsWith("t"));
        }

        String fillcolor = attrList.getValue("fillcolor");
        if(fillcolor != null && !fillcolor.equals("")) {
            f.setFillColor(colorByName(fillcolor, Color.white));
        }

        String dasharray = attrList.getValue("dasharray");
        if(dasharray != null && !dasharray.equals("") && !dasharray.equals("solid")) {
            f.setDashed(true);
        }

        String dynobjs = attrList.getValue("dynobjects");
        if(dynobjs != null && dynobjs.length() != 0) {
            if(f instanceof FigGroup) {
                FigGroup fg = (FigGroup)f;
                fg.parseDynObjects(dynobjs);
            }
        }

        String context = attrList.getValue("context");
        if(context != null && !context.equals("")) {
            f.setContext(context);
        }

        String visState = attrList.getValue("shown");
        //System.out.println("[PGMLParser]: setAttrs: " + visState);
        if(visState != null && !visState.equals("")) {
            int visStateInt = Integer.parseInt(visState);
            //System.out.println("[PGMLParser]: setAttrs: " + visStateInt);
            f.setVisState(visStateInt);
        }

        String single = attrList.getValue("single");
        //System.out.println("[PGMLParser]: setAttrs: " + visState);
        if(single != null && !single.equals("")) {
            //System.out.println("[PGMLParser]: setAttrs: " + visStateInt);
            f.setSingle(single);
        }

        setOwnerAttr(f, attrList);
    }

    protected void setOwnerAttr(Fig f, Attributes attrList) {
        //System.out.println("[GEF.PGMLParser]: setOwnerAttr");
        try {
            String owner = attrList.getValue("href");
            if(owner != null && !owner.equals("")) {
                //System.out.println("[GEF.PGMLParser]: setOwnerAttr");
                f.setOwner(findOwner(owner));
            }
        }
        catch(Exception ex) {
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
        if(uri.indexOf(".") == -1) {
            f = (Fig)_figRegistry.get(uri);
        }
        else {
            StringTokenizer st = new StringTokenizer(uri, ".");
            String figNum = st.nextToken();
            f = (Fig)_figRegistry.get(figNum);
            if(f == null) {
                return null;
            }

            if(f instanceof FigEdge) {
                return ((FigEdge)f).getFig();
            }

            while(st.hasMoreElements()) {
                String subIndex = st.nextToken();
                if(f instanceof FigGroup) {
                    FigGroup figGroup = (FigGroup)f;
                    int i = Integer.parseInt(subIndex);
                    f = (Fig)figGroup.getFigAt(i);
                }
            }
        }

        return f;
    }

    //needs-more-work: make an instance of the named class
    protected GraphModel getGraphModelFor(String desc) {
        //System.out.println("should be: "+desc);
        return new DefaultGraphModel();
    }

    protected Color colorByName(String name, Color defaultColor) {
        if(name.equalsIgnoreCase("white")) {
            return Color.white;
        }

        if(name.equalsIgnoreCase("lightGray")) {
            return Color.lightGray;
        }

        if(name.equalsIgnoreCase("gray")) {
            return Color.gray;
        }

        if(name.equalsIgnoreCase("darkGray")) {
            return Color.darkGray;
        }

        if(name.equalsIgnoreCase("black")) {
            return Color.black;
        }

        if(name.equalsIgnoreCase("red")) {
            return Color.red;
        }

        if(name.equalsIgnoreCase("pink")) {
            return Color.pink;
        }

        if(name.equalsIgnoreCase("orange")) {
            return Color.orange;
        }

        if(name.equalsIgnoreCase("yellow")) {
            return Color.yellow;
        }

        if(name.equalsIgnoreCase("green")) {
            return Color.green;
        }

        if(name.equalsIgnoreCase("magenta")) {
            return Color.magenta;
        }

        if(name.equalsIgnoreCase("cyan")) {
            return Color.cyan;
        }

        if(name.equalsIgnoreCase("blue")) {
            return Color.blue;
        }

        if (name.indexOf(' ') > 0) {
            // The color is in the format "red green blue"
            return getColor(name);
        }
        
        try {
            return Color.decode(name);
        }
        catch(Exception ex) {
            System.out.println("invalid color code string: " + name);
        }

        return defaultColor;
    }

    /**
     * A flyweight factory method for reusing the same Color
     * value multiple times.
     * @param rgb A string of RGB values seperated by space
     * @return the equivilent Color
     */
    private static Color getColor(String rgb) {
        Color color = (Color)USED_COLORS.get(rgb);
        if (color == null) {
            StringTokenizer st = new StringTokenizer(rgb, " ");
            int red = Integer.parseInt(st.nextToken());
            int green = Integer.parseInt(st.nextToken());
            int blue = Integer.parseInt(st.nextToken());
            color = new Color(red, green, blue);
            USED_COLORS.put(rgb, color);
        }
        return color;
    }

    protected String translateClassName(String oldName) {
        return oldName;
    }

    private String[] _entityPaths = {"/org/tigris/gef/xml/dtd/"};

    protected String[] getEntityPaths() {
        return _entityPaths;
    }

    public InputSource resolveEntity(java.lang.String publicId, java.lang.String systemId) {
        InputSource source = null;
        try {
            java.net.URL url = new java.net.URL(systemId);
            source = new InputSource(url.openStream());
            source.setSystemId(systemId);
            if(publicId != null) {
                source.setPublicId(publicId);
            }
        }
        catch(Exception e) {
            if(systemId.endsWith(".dtd")) {
                int i = systemId.lastIndexOf('/');
                i++;    // go past '/' if there, otherwise advance to 0
                String[] entityPaths = getEntityPaths();
                InputStream is = null;
                for(int pathIndex = 0; pathIndex < entityPaths.length && is == null; pathIndex++) {
                    String DTD_DIR = entityPaths[pathIndex];
                    is = getClass().getResourceAsStream(DTD_DIR + systemId.substring(i));
                    if(is == null) {
                        try {
                            is = new FileInputStream(DTD_DIR.substring(1) + systemId.substring(i));
                        }
                        catch(Exception ex) {
                        }
                    }
                }

                if(is != null) {
                    source = new InputSource(is);
                    source.setSystemId(systemId);
                    if(publicId != null) {
                        source.setPublicId(publicId);
                    }
                }
            }
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

    public void setSystemId(String id) {
        systemId = id;
    }

    public String getSystemId() {
        return systemId;
    }

    protected void rollbackAdd(Fig currentFig) {
        currentFig.setOwner(null);
    }

    protected void setDetectedFailure(boolean newValue) {
        //System.out.println("set detected failure to " + newValue);
        _detectedFailure = newValue;
    }
}    /* end class PGMLParser */