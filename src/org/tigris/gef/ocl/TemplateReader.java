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

package org.tigris.gef.ocl;

import java.util.*;
import java.util.Enumeration;
import java.util.StringTokenizer;
//import java.util.*;
import java.io.*;
//import com.ibm.xml.parser.*;
import org.xml.sax.*;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

public class TemplateReader extends org.xml.sax.HandlerBase {
  ////////////////////////////////////////////////////////////////
  // static variables
  public final static TemplateReader SINGLETON = new TemplateReader();

  Hashtable _templates;  /* Class -> Vector of TemplateRecord */
  Vector _macros;

  private java.util.List _stack = new ArrayList();
  private int _stackPointer = 0;
  private TemplateRecord _stackTopTemplate = null;
  private MacroRecord _stackTopMacro = null;

  ////////////////////////////////////////////////////////////////
  // constructors
  protected TemplateReader() { }

  ////////////////////////////////////////////////////////////////
  // static methods
  public static Hashtable readFile(String fileName) {
    return SINGLETON.read(fileName);
  }

  ////////////////////////////////////////////////////////////////
  // reading methods
  public Hashtable read(String fileName) {
    InputStream in = null;
    try {
        in = TemplateReader.class.getResourceAsStream(fileName);
    }
    catch (Exception ex) {}
    if (in == null) {
      String relativePath = fileName;
      if(relativePath.startsWith("/")) {
        relativePath = relativePath.substring(1);
      }
      try {
        in = new FileInputStream(relativePath);
      }
      catch(Exception ex) {}
    }
    if(in == null) return null;

    _templates = new Hashtable();
    _macros = new Vector();
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setNamespaceAware(false);
    factory.setValidating(false);
    try {
        SAXParser pc = factory.newSAXParser();
        InputSource source = new InputSource(in);
        source.setSystemId(new java.net.URL("file",null,fileName).toString());
        pc.parse(source,this);
    }
    catch (Exception ex) {
        ex.printStackTrace();
    }
    return _templates;
  }

    public void setDocumentLocator(Locator locator) {
    }

    public void startDocument() {
        _stackPointer = 0;
        _stackTopTemplate = null;
        _stackTopMacro = null;
    }

    public void endDocument() {
        _stackTopTemplate = null;
        _stackTopMacro = null;
    }

    public void ignorableWhitespace(char[] ch,
                                int start,
                                int length) {
    }

    public void processingInstruction(java.lang.String target,
                                  java.lang.String data) {
    }

  ////////////////////////////////////////////////////////////////
  // ElementHandler implementation
  public void startElement(String tagName,AttributeList attrList) {
    if (tagName.equals("template")) {
//      String body = e.getText().trim();
      String guard = attrList.getValue("guard");
      String className = attrList.getValue("class");
      java.lang.Class classObj = null;
      Object objToStack = null;
      try { classObj = Class.forName(className); }
      catch (Exception ex) {
	System.out.println("TemplateReader: Class " + className + " not found");
      }

      _stackTopTemplate = new TemplateRecord(classObj,guard,null);
      _stackTopMacro = null;
      _stack.add(_stackPointer++,_stackTopTemplate);

    }
    else if (tagName.equals("macro")) {
//      String body = e.getText().trim();
      String name = attrList.getValue("name");
      _stackTopMacro = new MacroRecord(name, null);
      _stackTopTemplate = null;
      _stack.add(_stackPointer++,_stackTopMacro);
    }
    else {
      _stackTopMacro = null;
      _stackTopTemplate = null;
      _stack.add(_stackPointer++,null);
      System.out.println("unknown tag: " + tagName);
    }
  }

  public void characters(char[] ch,
                       int start,
                       int length) {
      if(_stackTopMacro != null) {
        _stackTopMacro.characters(ch,start,length);
      }
      else {
        if(_stackTopTemplate != null) {
            _stackTopTemplate.characters(ch,start,length);
        }
      }
  }

  public void endElement(java.lang.String name) {
    if(_stackTopTemplate != null) {
      String body = _stackTopTemplate.getBody().trim();
      body = expandMacros(body);
      _stackTopTemplate.setBody(body);
      Class classObj = _stackTopTemplate.getClass();
      Vector existing = (Vector) _templates.get(classObj);
      if (existing == null) existing = new Vector();
      existing.addElement(_stackTopTemplate);
      _templates.put(classObj, existing);
    }
    else {
        if(_stackTopMacro != null) {
            String body = _stackTopMacro.getBody().trim();
            body = expandMacros(body);
            _stackTopMacro.setBody(body);
            boolean inserted = false;
            int newNameLength = _stackTopMacro.getName().length();
            int size = _macros.size();
            for (int i = 0; i < size && !inserted; i++) {
	        String n = ((MacroRecord)_macros.elementAt(i)).name;
	        if (n.length() < newNameLength) {
	            _macros.insertElementAt(_stackTopMacro, i);
	            inserted = true;
	        }
            }
            if (!inserted) {
                _macros.addElement(_stackTopMacro);
            }
        }
    }
    _stackTopTemplate = null;
    _stackTopMacro = null;
    Object top = _stack.get(--_stackPointer);
    if(top instanceof TemplateRecord) {
        _stackTopTemplate = (TemplateRecord) top;
    }
    else {
        if(top instanceof MacroRecord) {
            _stackTopMacro = (MacroRecord) top;
        }
    }
  }


  public String expandMacros(String body) {
    StringBuffer resultBuffer = new StringBuffer(body.length()*2);
    StringTokenizer st = new StringTokenizer(body, "\n\r");
    while (st.hasMoreElements()) {
      String line = st.nextToken();
      String expanded = expandMacrosOnOneLine(line);
      resultBuffer.append(expanded);
      resultBuffer.append("\n");
    }
    return resultBuffer.toString();
  }

  /** each line can have at most one macro */
  public String expandMacrosOnOneLine(String body) {
    int numMacros = _macros.size();
    for (int i=0; i < numMacros; i++) {
      String k = ((MacroRecord)_macros.elementAt(i)).name;
      int findIndex = body.indexOf(k);
      if (findIndex != -1) {
	String mac = ((MacroRecord)_macros.elementAt(i)).body;
	StringBuffer resultBuffer;
	String prefix = body.substring(0, findIndex);
	String suffix = body.substring(findIndex + k.length());
	resultBuffer = new StringBuffer(mac.length() +
					(prefix.length() + suffix.length())*10);
	StringTokenizer st = new StringTokenizer(mac, "\n\r");
	while (st.hasMoreElements()) {
	  resultBuffer.append(prefix);
	  resultBuffer.append(st.nextToken());
	  resultBuffer.append(suffix);
	  if (st.hasMoreElements()) resultBuffer.append("\n");
	}
	return resultBuffer.toString();
      }
    }
    return body;
  }

} /* end class TemplateReader */


class MacroRecord {
  String name;
  String body;
  private StringBuffer _buf = null;
  MacroRecord(String n, String b) {
    name = n;
    body = b;
  }

  public String getName() {
    return name;
  }

  public String getBody() {
    if(_buf != null) {
        body = _buf.toString();
    }
    return body;
  }

  public void setBody(String b) {
    body = b;
    _buf = null;
  }

  public void characters(char[] ch,
                       int start,
                       int length) {
    if(_buf == null) {
        _buf = new StringBuffer();
        if(body != null) {
            _buf.append(body);
        }
    }
    _buf.append(ch,start,length);
  }


} /* end class MacroRecord */


