package org.tigris.gef.base;

import java.awt.event.MouseEvent;

public interface MouseListener extends java.awt.event.MouseListener {

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