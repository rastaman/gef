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

package uci.xml.argo;

import uci.uml.ui.Project;
import uci.xml.SAXParserBase;
import uci.xml.XMLElement;

import java.util.*;
import java.io.*;
import java.net.URL;

public class ArgoParser extends SAXParserBase {

  ////////////////////////////////////////////////////////////////
  // static variables

  public static ArgoParser SINGLETON = new ArgoParser();

  ////////////////////////////////////////////////////////////////
  // instance variables

  protected Project        _proj   = null;

  private   ArgoTokenTable _tokens = new ArgoTokenTable();

  ////////////////////////////////////////////////////////////////
  // constructors

  protected ArgoParser() { super(); }

  ////////////////////////////////////////////////////////////////
  // main parsing methods

  // needs-more-work: should be able to merge an existing project into
  // the current one.

  public synchronized void readProject(URL url) {
    try {
      System.out.println("=======================================");
      System.out.println("== READING PROJECT: " + url);
      _proj = new Project(url);
      parse(url);
    }
    catch (Exception ex) {
      System.out.println("Exception reading project================");
      ex.printStackTrace();
    }
  }

  public Project getProject() { return _proj; }

  public void handleStartElement(XMLElement e) {
    if (_dbg) System.out.println("NOTE: ArgoParser handleStartTag:" + e.getName());
    try {
      switch (_tokens.toToken(e.getName(), true)) {
      case _tokens.TOKEN_argo: handleArgo(e); break;
      case _tokens.TOKEN_documentation: handleDocumentation(e); break;
      default: if (_dbg) 
	System.out.println("WARNING: unknown tag:" + e.getName());  break;
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }


  public void handleEndElement(XMLElement e) {
    if (_dbg) System.out.println("NOTE: ArgoParser handleEndTag:" + e.getName()+".");
    try {
      switch (_tokens.toToken(e.getName(), false)) {
      case _tokens.TOKEN_authorname : handleAuthorname(e); break;
      case _tokens.TOKEN_version : handleVersion(e); break;
      case _tokens.TOKEN_description : handleDescription(e); break;
      case _tokens.TOKEN_searchpath : handleSearchpath(e); break;
      case _tokens.TOKEN_member : handleMember(e); break;
      case _tokens.TOKEN_historyfile : handleHistoryfile(e); break;
      default : if (_dbg) 
	System.out.println("WARNING: unknown end tag:" + e.getName()); break;
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  protected void handleArgo(XMLElement e) {
    /* do nothing */
  }

  protected void handleDocumentation(XMLElement e) {
    /* do nothing */
  }


  protected void handleAuthorname(XMLElement e) {
    String authorname = e.getText().trim();
    _proj._authorname = authorname;
  }

  protected void handleVersion(XMLElement e) {
    String version = e.getText().trim();
    _proj._version = version;
  }

  protected void handleDescription(XMLElement e) {
    String description = e.getText().trim();
    _proj._description = description;
  }

  protected void handleSearchpath(XMLElement e) {
    String searchpath = e.getAttribute("href").trim();
    _proj.addSearchPath(searchpath);
  }

  protected void handleMember(XMLElement e) {
    String name = e.getAttribute("name").trim();
    String type = e.getAttribute("type").trim();
    _proj.addMember(name, type);
  }

  protected void handleHistoryfile(XMLElement e) {
    if (e.getAttribute("name") == null) return;
    String historyfile = e.getAttribute("name").trim();
    _proj._historyFile = historyfile;
  }

} /* end class ArgoParser */