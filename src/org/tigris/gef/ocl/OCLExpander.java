// %1035450542467:org.tigris.gef.ocl%
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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import java.util.*;

public class OCLExpander {
    ////////////////////////////////////////////////////////////////
    // constants
    public static String OCL_START = "<ocl>";
    public static String OCL_END = "</ocl>";
    ////////////////////////////////////////////////////////////////
    // instance variables
    public Map _templates = new Hashtable();
    public Hashtable _bindings = new Hashtable();
    public boolean _useXMLEscapes = true;

    ////////////////////////////////////////////////////////////////
    // constructor
    public OCLExpander(Map templates) {
        _templates = templates;
    }

    ////////////////////////////////////////////////////////////////
    // template expansion
    public void expand(OutputStream w, Object target, String prefix, String suffix) {
        expandContent(new PrintWriter(w), target, prefix, suffix);
    }

    public void expand(Writer w, Object target, String prefix, String suffix) {
        expandContent(new PrintWriter(w), target, prefix, suffix);
    }

    protected void expandContent(PrintWriter pw, Object target, String prefix, String suffix) {
        if(target == null) {
            return;
        }

        List exprs = findTemplatesFor(target);
        String expr = null;
        int numExpr = (exprs == null) ? 0 : exprs.size();
        for(int i = 0; i < numExpr && expr == null; i++) {
            TemplateRecord tr = (TemplateRecord)exprs.get(i);
            if(tr.guard == null || tr.guard.equals("")) {
                expr = tr.body;
                break;
            }

            _bindings.put("self", target);
            Collection results = evaluate(_bindings, tr.guard);
            if(results.size() > 0 && !Boolean.FALSE.equals(results.iterator().next())) {
                expr = tr.body;
                break;
            }
        }

        if(expr == null) {
            String s = target.toString();
            if(_useXMLEscapes) {
                s = replaceWithXMLEscapes(s);
            }

            //System.out.println("[File content]    " + prefix + s + suffix);
            pw.println(prefix + s + suffix);
            return;
        }

        StringTokenizer st = new StringTokenizer(expr, "\n\r");
        while(st.hasMoreTokens()) {
            String line = st.nextToken();
            expandLine(pw, line, target, prefix, suffix);
        }
        // System.out.println();
    }    // end of expand

    protected void expandLine(PrintWriter pw, String line, Object target, String prefix, String suffix) {
        // if no embedded expression then output line else
        // then loop over all values of expr and call recursively for each resul
        int startPos = line.indexOf(OCL_START, 0);
        int endPos = line.indexOf(OCL_END, 0);
        if(startPos == -1 || endPos == -1) {    // no embedded expr's
            //System.out.println("[File content]    " + prefix + line + suffix);
            pw.println(prefix + line + suffix);
            return;
        }

        // assume one embedded expression on line
        prefix = prefix + line.substring(0, startPos);
        String expr = line.substring(startPos + OCL_START.length(), endPos);
        suffix = line.substring(endPos + OCL_END.length()) + suffix;
        _bindings.put("self", target);
        Collection results = evaluate(_bindings, expr);
        Iterator iter = results.iterator();
        while(iter.hasNext()) {
            expand(pw, iter.next(), prefix, suffix);
        }
    }

    /** Find the List of templates that could apply to this target
     *  object.  That includes the templates for its class and all
     *  superclasses.  Needs-More-Work: should cache.
     * @deprecated 0.10 in favour of findTemplates(Object). Remove in 0.11
     */
    public Vector findTemplatesFor(Object target) {
        Vector res = null;
        boolean shared = true;
        for(Class c = target.getClass(); c != null; c = c.getSuperclass()) {
            Vector temps = (Vector)_templates.get(c);
            if(temps == null) {
                continue;
            }

            if(res == null) {
                // if only one template applies, return it
                res = temps;
            }
            else {
                // if another template also applies, merge the two Lists,
                // but leave the original unchanged
                if(shared) {
                    shared = false;
                    Vector newRes = new Vector();
                    for(int i = 0; i < res.size(); i++) {
                        newRes.add(res.get(i));
                    }

                    res = newRes;
                }

                for(int j = 0; j < temps.size(); j++) {
                    res.add(temps.get(j));
                }
            }
        }

        return res;
    }

    /** Find the List of templates that could apply to this target
     *  object.  That includes the templates for its class and all
     *  superclasses.  Needs-More-Work: should cache. */
    public Collection findTemplates(Object target) {
        List res = null;
        boolean shared = true;
        for(Class c = target.getClass(); c != null; c = c.getSuperclass()) {
            List temps = (List)_templates.get(c);
            if(temps == null) {
                continue;
            }

            if(res == null) {
                // if only one template applies, return it
                res = temps;
            }
            else {
                // if another template also applies, merge the two Lists,
                // but leave the original unchanged
                if(shared) {
                    shared = false;
                    List newRes = new ArrayList();
                    for(int i = 0; i < res.size(); i++) {
                        newRes.add(res.get(i));
                    }

                    res = newRes;
                }

                for(int j = 0; j < temps.size(); j++) {
                    res.add(temps.get(j));
                }
            }
        }

        return res;
    }

    protected String replaceWithXMLEscapes(String s) {
        s = replaceAll(s, "&", "&amp;");
        s = replaceAll(s, "<", "&lt;");
        s = replaceAll(s, ">", "&gt;");
        return s;
    }

    protected String replaceAll(String s, String pat, String rep) {
        int index = s.indexOf(pat);
        int patLen = pat.length();
        int repLen = rep.length();
        while(index != -1) {
            s = s.substring(0, index) + rep + s.substring(index + patLen);
            index = s.indexOf(pat, index + repLen);
        }

        return s;
    }

    protected Collection evaluate(Map bindings, String expr) {
        return org.tigris.gef.ocl.OCLEvaluator.SINGLETON.eval(bindings, expr);
    }
}