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


/**
 * LayerPerspectiveMutable.java
 *
 * A LayerPerspective with an underlying MutableGraphModel.
 * As figures are added and removed the underlying MutableGraphModel is
 * updated. 
 * 
 * @author Eugenio Alvarez
 * Data Access Technologies
 *
 */

package org.tigris.gef.base;

import java.util.*;
import java.awt.*;

import org.tigris.gef.graph.*;
import org.tigris.gef.presentation.*;

public class LayerPerspectiveMutable extends LayerPerspective {

    ////////////////////////////////////////////////////////////////
    // instance variables

    /** The underlying connected graph to be visualized. */
    MutableGraphModel _mgm;

    ////////////////////////////////////////////////////////////////
    // constructors

    public LayerPerspectiveMutable(String name, MutableGraphModel mgm) {
        super(name,(GraphModel)mgm);
        _mgm = mgm;
    }


    ////////////////////////////////////////////////////////////////
    // accessors

    public GraphModel getGraphModel() {
        return(GraphModel)getMutableGraphModel(); 
    }
    
    public void setGraphModel(GraphModel gm) {
        setMutableGraphModel((MutableGraphModel)gm);
    }

    public MutableGraphModel getMutableGraphModel() { return _mgm; }
    public void setMutableGraphModel(MutableGraphModel mgm) {
        super.setGraphModel((GraphModel)mgm);
        _mgm = mgm;
    }    

    ////////////////////////////////////////////////////////////////
    // Layer API

    public void add(Fig f) { 
        Object owner = f.getOwner();
        // prevents duplicate nodes. 
        // To allow multiple views in one diagram, remove the following two lines.
        if (owner != null && f instanceof FigNode &&
            _mgm.containsNode(owner))
            return; 
        super.add(f);
        //if ( owner != null && _mgm.canAddNode(owner))
          //  _mgm.addNode(owner);
        // FigEdges are added by the underlying MutableGraphModel.
    }

    public void remove(Fig f) { 
        super.remove(f);
        Object owner = f.getOwner();
        if (owner != null) {
            if (f instanceof FigEdge && _mgm.containsEdge(owner)) 
                _mgm.removeEdge(owner); 
            else if (_mgm.containsNode(owner)) 
                _mgm.removeNode(owner);
        }
    }
} /* end class LayerPerspectiveMutable */
