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

// File: NetList.java
// Classes: NetList
// Original Author: ics125 spring 1996
// $Id$

package org.tigris.gef.graph.presentation;

import java.util.Collection;
import java.util.Vector;

/** A class that implements the concept of a connected graph. A
 *  NetList is not any one object in the connected graph, it is the
 *  overall graph. A NetList contains a list of nodes and edges.  This
 *  class is used by DefaulGraphModel, if you implement your own
 *  GraphModel, you can use your own application-specific
 *  representation of graphs.
 */

public class NetList extends NetPrimitive implements java.io.Serializable {

    ////////////////////////////////////////////////////////////////
    // instance variables

    /** The nodes in the NetList */
    private Vector nodes = new Vector();

    /** The edges in the NetList */
    private Vector edges = new Vector();

    /** The name of this connected graph. */
    String name;

    ////////////////////////////////////////////////////////////////
    // constructors

    /** Construct a new NetList with no contained nodes. */
    public NetList() { }

    ////////////////////////////////////////////////////////////////
    // accessors

    public String getId() {
        return name;
    }

    /** @deprecated 0.11 use setName(String) */
    public void name(String n) { name = n; }
    /** @deprecated 0.11 use getName() */
    public String name() { return name; }

    public void setName(String n) { name = n; }
    public String getName() { return name; }

    /** Reply the vector of nodes 
     * @deprectaed 0.11 use getNodes(Collection)
     */
    public Vector getNodes() { return nodes; }

    /** Reply the vector of edges
    * @deprectaed 0.11 use getNodes(Collection)
    */
    public Vector getEdges() { return edges; }

    /** Reply the vector of nodes */
    public Collection getNodes(Collection c) { 
        return nodes;
    }

    /** Reply the vector of edges */
    public Collection getEdges(Collection c) {
        return edges;
    }

    /** Add a node to this NetList.  */
    public void addNode(NetNode n) {
        nodes.addElement(n);
    }

    /** Remove a node from this NetList. When a node is deleted a
     *  notification is sent out. */
    public void removeNode(NetNode n) {
        if (n != null && nodes.contains(n)) {
            nodes.removeElement(n);
        }
    }

    /** Add a NetEdge to this NetList. */
    public void addEdge(NetEdge a) {
        edges.addElement(a);
    }

    /** Remove a Edge from this NetList. */
    public void removeEdge(NetEdge a) {
        if (a != null && edges.contains(a)) {
            edges.removeElement(a);
        } 
    }

    static final long serialVersionUID = -238774170084340147L;
} /* end class NetList */

