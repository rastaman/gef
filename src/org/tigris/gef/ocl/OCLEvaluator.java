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

import java.io.Writer;
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
    ////////////////////////////////////////////////////////////////
    // static variables
    protected Map _scratchBindings = new Hashtable();
    protected StringBuffer _strBuf = new StringBuffer(100);

    protected OCLEvaluator() {
    }

    protected synchronized String evalToString(Object self, String expr)
            throws ExpansionException {
        return evalToString(self, expr, ", ");
    }

    protected synchronized String evalToString(Object self, String expr, String sep)
            throws ExpansionException {
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

    protected List eval(Map bindings, String expr) throws ExpansionException {
        int firstPos = expr.indexOf(".");
        Object target = bindings.get(expr.substring(0, firstPos));
        Vector targets;

        if (target instanceof Vector)  {
            targets = (Vector) target;
        } else {
            targets = new Vector();
            targets.addElement(target);
        }
        String prop = expr.substring(firstPos);
        return eval(bindings, prop, targets);
    } // end of eval()
    
    private List eval(Map bindings, String expr, List targets) throws ExpansionException {
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
            } else {    // <expr>::= ".<property>.<expr>"
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

    /**
     * Return the first character of a string converted to upper case
     * @param s The string to convert
     * @return the converted string
     */
    private String toTitleCase(String s) {
        if(s.length() > 1) {
            return s.substring(0, 1).toUpperCase() + s.substring(1, s.length());
        } else {
            return s.toUpperCase();
        }
    }    // end of toTitleCase


    /**
     * Attempt to retrieve a named property from a target
     * object.
     * @param target
     * @param property
     * @return
     */
    private Object evaluateProperty(Object target, String property) throws ExpansionException {
        if(target == null) {
            return null;
        }

        Method m = null;
        
        if (property.endsWith("()")) {
            // Lastly try and find a method in the form property(Writer)
            property = property.substring(0,property.length()-2);
            try {
                Class params[] = new Class[2];
                params[0] = Writer.class;
                params[1] = Integer.class;
                m = target.getClass().getMethod(property, params);
                MethodInfo info = new MethodInfo(target, m);
                return info;
            } catch(NoSuchMethodException e) {
                throw new ExpansionException(e);
            }
        }
        
        // First try and find a getter method in the form getProperty()
        Object o = null;
        try {
            m = target.getClass().getMethod("get" + toTitleCase(property), null);
            o = m.invoke(target, null);    // getter methods take no args =>  null
            //System.out.println("[OCLEvaluator] Trying to get method get" + toTitleCase(property) + " = " + o);
            return convertCollection(o);
        } catch(NoSuchMethodException e) {
        } catch(InvocationTargetException e) {
            if(m != null) {
                e.getTargetException().printStackTrace();
                return null;
            }
        } catch(IllegalAccessException e) {
        }

        // Then try and find a method in the form property()
        try {
            m = target.getClass().getMethod(property, null);
            o = m.invoke(target, null);
            return convertCollection(o);
        } catch(NoSuchMethodException e) {
        } catch(InvocationTargetException e) {
            if(m != null) {
                e.getTargetException().printStackTrace();
                return null;
            }
        } catch(IllegalAccessException e) {
        }

        
        // Next try and find a method in the form Property()
        try {
            m = target.getClass().getMethod(toTitleCase(property), null);
            o = m.invoke(target, null);
            return convertCollection(o);
        } catch(NoSuchMethodException e) {
        } catch(IllegalAccessException e) {
        } catch(InvocationTargetException e) {
        }


        
        // We have tried all method forms so lets now try just getting the property
        Field f = null;
        try {
            f = target.getClass().getField(property);
            o = f.get(target);    // access the field f or object targe
            return convertCollection(o);
        } catch(NoSuchFieldException e) {
            o = getExternalProperty(target, property);
            System.out.println("external property value = " + o);
            if(o != null) {
                return convertCollection(o);
            }
            else {
                e.printStackTrace();
                return null;
            }
        } catch(Exception e) {
            if(f != null) {
                e.printStackTrace();
                return null;
            }
        }

        return null;
    }    // end of evaluateProperty

    /**
     * Copy every item from the given list to a new list.
     * If the item to copy is itslef a list then each item is taken out
     * of that (recursively) so that the end list contains only
     * non-lists.
     */
    private List flatten(List v) {
        List accum = new ArrayList();
        flattenInto(v, accum);
        return accum;
    }

    /**
     * Copy the object o into the given list.
     * If the object is itself a list then each item is taken out
     * of that (recursively) so that the end list contains only
     * non-lists.
     */
    private void flattenInto(Object o, List accum) {
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
    protected Object getExternalProperty(Object target, String property) {
        return null;
    }

    /** 
     * If an object is a collection then return it as an ArrayList otherwise return it unchanged.
     * 
     * @param o the object
     * @return the original object or ArrayList
     */
    private static Object convertCollection(Object o) {
        if(o instanceof Collection && !(o instanceof List)) {
            return new ArrayList((Collection)o);
        }
        else {
            return o;
        }
    }
}
