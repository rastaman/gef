package org.tigris.gef.base;

import java.util.EventObject;

public interface LayerListener {

    void figAdded(EventObject event);

    void figRemoved(EventObject event);
    
}
