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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.*;

// stereotype <<utility>>
public class OCLEvaluator {
    ////////////////////////////////////////////////////////////////
    // constants
    public static String OCL_START = "<ocl>";
    public static String OCL_END = "</ocl>";
    public static String GET_NAME_EXPR_1 = "self";
    public static String GET_NAME_EXPR_2 = "self.name.body";
    public static String GET_OWNER_EXPR = "self.owner";
    public static OCLEvaluator SINGLETON = new OCLEvaluator();
    ////////////////////////////////////////////////////////////////
    // static variables
    protected Map _scratchBindings = new Hashtable();
    protected StringBuffer _strBuf = new StringBuffer(100);

    protected OCLEvaluator() {
    }

    public synchronized String evalToString(Object self, String expr) {
        return evalToString(self, expr, ", ");
    }

    public synchronized String evalToString(Object self, String expr, String sep) {
        _scratchBindings.put("self", self);
        java.util.List values = eval(_scratchBindings, expr);
        _strBuf.setLength(0);
        Iterator iter = values.iterator();
        while(iter.hasNext()) {
            String v = iter.next().toString();
            if(v.length() > 0) {
                _strBuf.append(v);
                if(iter.hasNext()) {
                    _strBuf.append(sep);
                }
            }
        }

        return _strBuf.toString();
    }

    public java.util.List eval(Map bindings, String expr) {
        int firstPos = expr.indexOf(".");
        Object target = bindings.get(expr.substring(0, firstPos));
        Vector targets;

        if (target instanceof Vector)  {
            targets = (Vector) target;
        }
        else {
            targets = new Vector();
            targets.addElement(target);
        }
        String prop = expr.substring(firstPos);
        return eval(bindings, prop, targets);
    } // end of eval()
    
    public Vector eval(Map bindings, String expr, Vector targets) {
        int firstPos;
        int secPos;
        int numElements;
        String property;
        while(expr.length() > 0) {
            List v = new ArrayList();
            firstPos = expr.indexOf(".");
            secPos = expr.indexOf(".", firstPos + 1);
            if(secPos == -1) {    // <expr>::= ".<property>"
                property = expr.substring(firstPos + 1);
                expr = "";
            }
            else    // <expr>::= ".<property>.<expr>"
             {
                property = expr.substring(firstPos + 1, secPos);
                expr = expr.substring(secPos);    //+1
            }

            numElements = targets.size();
            for(int i = 0; i < numElements; i++) {
                v.add(evaluateProperty(targets.get(i), property));
            }

            targets = new Vector(flatten(v));
            // the results of evaluating a property may result in a List
        }

        return targets;
    }    // end of eval()

    public String toTitleCase(String s) {
        if(s.length() > 1) {
            return s.substring(0, 1).toUpperCase() + s.substring(1, s.length());
        }
        else {
            return s.toUpperCase();
        }
    }    // end of toTitleCase

    public Object evaluateProperty(Object target, String property) {
        if(target == null) {
            return null;
        }

        Method m = null;
        Field f = null;
        Object o = null;
        try {
            m = target.getClass().getMethod("get" + toTitleCase(property), null);
            o = m.invoke(target, null);    // getter methods take no args =>  null
            //System.out.println("[OCLEvaluator] Trying to get method get" + toTitleCase(property) + " = " + o);
            return convertCollection(o);
        }
        catch(NoSuchMethodException e) {
        }
        catch(InvocationTargetException e) {
            if(m != null) {
                System.out.println("On Class: " + target.getClass().getName());
                System.out.println("error in evaluating " + "get" + toTitleCase(property) + "()");
                e.getTargetException().printStackTrace();
                return null;
            }
        }
        catch(Exception e) {
        }

        try {
            m = target.getClass().getMethod(property, null);
            o = m.invoke(target, null);
            //System.out.println("Trying to get method " + toTitleCase(property));
            return convertCollection(o);
        }
        catch(NoSuchMethodException e) {
        }
        catch(InvocationTargetException e) {
            if(m != null) {
                System.out.println("On Class: " + target.getClass().getName());
                System.out.println("error in evaluating " + property + "()");
                e.getTargetException().printStackTrace();
                return null;
            }
        }
        catch(Exception e) {
        }

        try {
            m = target.getClass().getMethod(toTitleCase(property), null);
            o = m.invoke(target, null);
            //System.out.println("Trying to get method" + property);
            return convertCollection(o);
        }
        catch(Exception e) {
        }

        try {
            f = target.getClass().getField(property);
            o = f.get(target);    // access the field f or object targe
            return convertCollection(o);
        }
        catch(NoSuchFieldException e) {
            o = getExternalProperty(target, property);
            System.out.println("external property value = " + o);
            if(o != null) {
                return convertCollection(o);
            }
            else {
                System.out.println("On Class: " + target.getClass().getName());
                System.out.println("Trying to get field " + property);
                e.printStackTrace();
                return null;
            }
        }
        catch(Exception e) {
            if(f != null) {
                System.out.println("On Class: " + target.getClass().getName());
                System.out.println("error in evaluating field " + property);
                e.printStackTrace();
                return null;
            }
        }

        return null;
    }    // end of evaluateProperty

    public List flatten(List v) {
        List accum = new ArrayList();
        flattenInto(v, accum);
        return accum;
    }

    public void flattenInto(Object o, List accum) {
        if(!(o instanceof List)) {
            accum.add(o);
        }
        else {
            List oList = (List)o;
            int count = oList.size();
            for(int i = 0; i < count; ++i) {
                Object p = oList.get(i);
                flattenInto(p, accum);
            }
        }
    }

    /**
     * Returns the value of a property that is not a field of the target.
     * This method should be overwritten in a derived class.
     *
     * @param target The object to be examined.
     * @param property The property to look after.
     * @return null
     */
    public Object getExternalProperty(Object target, String property) {
        //System.out.println("called default implementation");
        return null;
    }

    // added this method 02/08/00 (JH) - if an instance of Collection
    // is encountered, convert it to a List so the rest of the
    // OCL code still works; there may be a more efficient way,
    // but this was the least intrusive fix
    public static Object convertCollection(Object o) {
        if(o instanceof Collection && !(o instanceof List)) {
            return new ArrayList((Collection)o);
        }
        else {
            return o;
        }
    }
}    // end of OCLEvaluator