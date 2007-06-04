package org.tigris.gef.base;

import java.util.EventListener;

public interface MouseListener extends EventListener{

	/**
	 * mouseClicked
	 */
	public void mouseClicked(MouseEvent e);

	/**
	 * mousePressed
	 */
	public void mousePressed(MouseEvent e);

	/**
	 *mouseReleased
	 */
	public void mouseReleased(MouseEvent e);

	/**
	 * mouseEntered
	 */
	public void mouseEntered(MouseEvent e);

	/**
	 * mouseExited
	 */
	public void mouseExited(MouseEvent e);
}