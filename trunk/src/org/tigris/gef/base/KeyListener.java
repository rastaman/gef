package org.tigris.gef.base;

import java.util.EventListener;

public interface KeyListener extends EventListener {

	/**
	 * keyTyped
	 */
	public void keyTyped(KeyEvent e);

	/**
	 * keyPressed
	 */
	public void keyPressed(KeyEvent e);

	/**
	 * keyReleased
	 */
	public void keyReleased(KeyEvent e);
}