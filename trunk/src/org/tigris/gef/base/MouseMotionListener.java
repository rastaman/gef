package org.tigris.gef.base;

import java.util.EventListener;

public interface MouseMotionListener extends EventListener {
	/**
	 * mouseDragged
	 */
	public void mouseDragged(MouseEvent e);

	/**
	 * mouseMoved
	 */
	public void mouseMoved(MouseEvent e);
}