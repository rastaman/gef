// %1032800241201:org.tigris.gef.base%
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
package org.tigris.gef.base;

import java.awt.*;

import java.beans.*;

import java.util.*;
import java.util.List;
import java.io.Serializable;

import org.tigris.gef.graph.GraphController;
import org.tigris.gef.graph.GraphEvent;
import org.tigris.gef.graph.GraphListener;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.graph.presentation.DefaultGraphModel;

import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigEdge;
import org.tigris.gef.presentation.FigNode;

import org.tigris.gef.ui.PaletteFig;
import org.tigris.gef.ui.ToolBar;

/** A diagram is just combination of a GraphModel, a Layer, and a
 title. The GraphModel stores the connected graph representation,
 without any graphics. The Layer stores all the Figs. */
public class Diagram implements Serializable, GraphListener {

    ////////////////////////////////////////////////////////////////
    // instance variables
    protected String _name = "no title set";
    protected String _comments = "(no comments given)";
    private LayerPerspective _layer;
    protected transient ToolBar _toolBar;
    private transient Vector vetoListeners;
    private transient PropertyChangeSupport _changeSupport;
    // In JDK < 1.4 there is no way to get all listener from the PropertyChangeSupport, so we keep a list here 
    private Set _propertyChangeListeners = new HashSet();

    /** The bean property name denoting the scale factor. Value is a Double in range [0, 1] */
    public static final String SCALE_KEY = "scale";

    /** The bean property name denoting the diagram's name. Value is a String. */
    public static final String NAME_KEY = "name";

    ////////////////////////////////////////////////////////////////
    // constructors
    public Diagram() {
        this("untitled");
    }

    public Diagram(String name) {
        this(name, new DefaultGraphModel());
    }

    public Diagram(String name, GraphModel graphModel) {
        this(name, graphModel, new LayerPerspective(name, graphModel));
    }

    public Diagram(String name, GraphModel graphModel, LayerPerspective layer) {
        _changeSupport = new PropertyChangeSupport(this);
        _name = name;
        _layer = layer;
        setGraphModel(graphModel);
    }

    protected void initToolBar() {
        _toolBar = new PaletteFig();
    }

    public void initialize(Object owner) {
        /* do nothing by default */
    }

    ////////////////////////////////////////////////////////////////
    // accessors
    public ToolBar getToolBar() {

        if(_toolBar == null)
            initToolBar();

        return _toolBar;
    }

    public void setToolBar(ToolBar tb) {
        _toolBar = tb;
    }

    public String getComments() {
        return _comments;
    }

    public void setComments(String c) throws PropertyVetoException {
        fireVetoableChange("comments", _comments, c);
        _comments = c;
    }

    /**
     * USED BY SVG.tee
     */
    public String getName() {
        return _name;
    }

    public void setName(String name) throws PropertyVetoException {
        fireVetoableChange("name", _name, name);

        String oldName = _name;
        _name = name;
        _changeSupport.firePropertyChange(NAME_KEY, oldName, name);
    }

    public void setShowSingleMultiplicity(boolean enable) {
    }

    public boolean getShowSingleMultiplicity() {
        return false;
    }

    public double getScale() {
        return getLayer().getScale();
    }

    public void setScale(double scale) {
        double oldScale = getScale();
        getLayer().setScale(scale);
        firePropertyChange(SCALE_KEY, new Double(oldScale), new Double(scale));
    }

    /**
     * USED BY SVG.tee
     */
    public String getClassAndModelID() {
        return getClass().getName();
    }

    public GraphModel getGraphModel() {
        return getLayer().getGraphModel();
    }

    public void setGraphModel(GraphModel gm) {

        GraphModel oldGM = getLayer().getGraphModel();

        if(oldGM != null)
            oldGM.removeGraphEventListener(this);

        getLayer().setGraphModel(gm);
        gm.addGraphEventListener(this);
    }

    public GraphController getGraphController() {
        return getLayer().getGraphController();
    }

    public LayerPerspective getLayer() {
        return _layer;
    }

    public void setLayer(LayerPerspective layer) {
        _layer = layer;
    }

    public int countContained(List owners) {

        int count = 0;
        int numOwners = owners.size();

        Iterator nodeIter = getNodes(null).iterator();
        while (nodeIter.hasNext()) {
            Object node = nodeIter.next();

            for (int j = 0; j < numOwners; j++) {

                if (node == owners.get(j)) {
                    count++;
                }
            }
        }

        Iterator edgeIter = getEdges(null).iterator();
        while (edgeIter.hasNext()) {
            Object edge = edgeIter.next();

            for (int j = 0; j < numOwners; j++) {

                if (edge == owners.get(j)) {
                    count++;
                }
            }
        }

        Collection figs = getLayer().getContents(null);

        Iterator it = figs.iterator();
        while(it.hasNext()) {
            Object fig = it.next();
            for(int j = 0; j < numOwners; j++) {
                if(fig == owners.get(j)) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Get all the figs that are a node.
     * @param nodes the collection in which to place the nodes or null
     *              if a new collection should be created
     * @return the nodes
     */
    public Collection getNodes(Collection nodes) {
        // needs-more-work: should just do getGraphModel().getNodes()
        // but that is not updated when the diagram is loaded
        if (nodes == null) {
            nodes = new ArrayList();
        }
        Collection figs = getLayer().getContents(null);
        Iterator it = figs.iterator();
        while (it.hasNext()) {
            Object fig = it.next();
            if (fig instanceof FigNode) {
                nodes.add(((FigNode)fig).getOwner());
            }
        }

        return nodes;
    }

    /**
     * Get all the figs that are edges.
     * @param edges the collection in which to place the edges or null
     *              if a new collection should be created
     * @return the edges
     */
    public Collection getEdges(Collection edges) {
        // needs-more-work: should just do getGraphModel().getEdges()
        // but that is not updated when the diagram is loaded
        if (edges == null) {
            edges = new ArrayList();
        }
        Collection figs = getLayer().getContents(null);

        Iterator it = figs.iterator();
        while (it.hasNext()) {
            Object fig = it.next();
            if((fig instanceof FigEdge) && (null != ((FigEdge)fig).getOwner()))    // Some figs might not have a owner?
                edges.add(((FigEdge)fig).getOwner());
        }
        
        return edges;
    }

    ////////////////////////////////////////////////////////////////
    // accessors on the Layer
    public void add(Fig f) {
        _layer.add(f);
    }

    public void remove(Fig f) {
        _layer.remove(f);
    }

    public void removeAll(Fig f) {
        _layer.removeAll();
    }

    public Enumeration elements() {
        Enumeration result = _layer.elements();

        return result;
    }

    public Fig hit(Rectangle r) {
        return _layer.hit(r);
    }

    public Enumeration elementsIn(Rectangle r) {
        return _layer.elementsIn(r);
    }

    public Fig presentationFor(Object obj) {
        return _layer.presentationFor(obj);
    }

    public void sendToBack(Fig f) {
        _layer.sendToBack(f);
    }

    public void bringForward(Fig f) {
        _layer.bringForward(f);
    }

    public void sendBackward(Fig f) {
        _layer.sendBackward(f);
    }

    public void bringToFront(Fig f) {
        _layer.bringToFront(f);
    }

    public void reorder(Fig f, int function) {
        _layer.reorder(f, function);
    }

    ////////////////////////////////////////////////////////////////
    // graph event handlers
    public void nodeAdded(GraphEvent e) {

        try {
            fireVetoableChange("nodeAdded", null, null);
        }
         catch(PropertyVetoException pve) {
        }
    }

    public void edgeAdded(GraphEvent e) {

        try {
            fireVetoableChange("edgeAdded", null, null);
        }
         catch(PropertyVetoException pve) {
        }
    }

    public void nodeRemoved(GraphEvent e) {

        try {
            fireVetoableChange("nodeRemoved", null, null);
        }
         catch(PropertyVetoException pve) {
        }
    }

    public void edgeRemoved(GraphEvent e) {

        try {
            fireVetoableChange("edgeRemoved", null, null);
        }
         catch(PropertyVetoException pve) {
        }
    }

    public void graphChanged(GraphEvent e) {

        try {
            fireVetoableChange("graphChanged", null, null);
        }
         catch(PropertyVetoException pve) {
        }
    }

    ////////////////////////////////////////////////////////////////
    // VetoableChangeSupport
    public void preSave() {
        _layer.preSave();
    }

    public void postSave() {
        _layer.postSave();
    }

    public void postLoad() {
        _layer.postLoad();
    }

    public synchronized void addVetoableChangeListener(VetoableChangeListener listener) {

        if(vetoListeners == null)
            vetoListeners = new Vector();

        vetoListeners.removeElement(listener);
        vetoListeners.addElement(listener);
    }

    public synchronized void removeVetoableChangeListener(VetoableChangeListener listener) {

        if(vetoListeners == null)
            return;

        vetoListeners.removeElement(listener);
    }

    public void fireVetoableChange(String propertyName, boolean oldValue, boolean newValue) throws PropertyVetoException {
        fireVetoableChange(propertyName, new Boolean(oldValue), new Boolean(newValue));
    }

    public void fireVetoableChange(String propertyName, int oldValue, int newValue) throws PropertyVetoException {
        fireVetoableChange(propertyName, new Integer(oldValue), new Integer(newValue));
    }

    public void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws PropertyVetoException {

        if(vetoListeners == null)
            return;

        if(oldValue != null && oldValue.equals(newValue))
            return;

        PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);

        try {

            for(int i = 0; i < vetoListeners.size(); i++) {

                VetoableChangeListener target = (VetoableChangeListener)vetoListeners.elementAt(i);
                target.vetoableChange(evt);
            }
        }
         catch(PropertyVetoException veto) {

            // Create an event to revert everyone to the old value.
            evt = new PropertyChangeEvent(this, propertyName, newValue, oldValue);

            for(int i = 0; i < vetoListeners.size(); i++) {

                try {

                    VetoableChangeListener target = (VetoableChangeListener)vetoListeners.elementAt(i);
                    target.vetoableChange(evt);
                }
                 catch(PropertyVetoException ex) {

                    // We just ignore exceptions that occur during reversions.
                }
            }

            // And now rethrow the PropertyVetoException.
            throw veto;
        }
    }

    //
    // beans property change event handling
    //
    public void addPropertyChangeListener(PropertyChangeListener l) {
        _changeSupport.addPropertyChangeListener(l);
        _propertyChangeListeners.add(l);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener l) {
        _changeSupport.addPropertyChangeListener(propertyName, l);
        _propertyChangeListeners.add(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        removePropertyChangeListenerInt(l);
        _propertyChangeListeners.remove(l);
    }

    private void removePropertyChangeListenerInt(PropertyChangeListener l) {
        _changeSupport.removePropertyChangeListener(l);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener l) {
        _changeSupport.removePropertyChangeListener(propertyName, l);
        _propertyChangeListeners.remove(l);
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        _changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    // hardcore: called when diagram should be removed - especially when project is removed.  Per.
    public void remove() {
        for(Iterator iterator = _propertyChangeListeners.iterator(); iterator.hasNext();) {
            PropertyChangeListener listener = (PropertyChangeListener)iterator.next();
            removePropertyChangeListenerInt(listener);
        }

        vetoListeners.clear();
    }
}