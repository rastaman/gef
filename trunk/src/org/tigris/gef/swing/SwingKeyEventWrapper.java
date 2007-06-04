/**
 * 
 */
package org.tigris.gef.swing;

import org.tigris.gef.base.KeyEvent;

/**
 * @author johnny
 * 
 */
public class SwingKeyEventWrapper implements KeyEvent {

	private java.awt.event.KeyEvent event;

	// set the private event to the awt KeyEvent
	public SwingKeyEventWrapper(java.awt.event.KeyEvent me) {
		this.event = me;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.KeyEvent#getKeyChar()
	 */
	public char getKeyChar() {
		// TODO Auto-generated method stub
		return event.getKeyChar();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.KeyEvent#getKeyCode()
	 */
	public int getKeyCode() {
		// TODO Auto-generated method stub
		return event.getKeyCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.KeyEvent#getKeyLocation()
	 */
	public int getKeyLocation() {
		// TODO Auto-generated method stub
		return event.getKeyLocation();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.KeyEvent#isActionKey()
	 */
	public boolean isActionKey() {
		// TODO Auto-generated method stub
		return event.isActionKey();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.KeyEvent#paramString()
	 */
	public String paramString() {
		// TODO Auto-generated method stub
		return event.paramString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.KeyEvent#setKeyChar(char)
	 */
	public void setKeyChar(char keyChar) {
		// TODO Auto-generated method stub
		event.setKeyChar(keyChar);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.KeyEvent#setKeyCode(int)
	 */
	public void setKeyCode(int keyCode) {
		// TODO Auto-generated method stub
		event.setKeyCode(keyCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.KeyEvent#setModifiers(int)
	 */
	public void setModifiers(int modifiers) {
		// TODO Auto-generated method stub
		event.setModifiers(modifiers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.InputEvent#consume()
	 */
	public void consume() {
		// TODO Auto-generated method stub
		event.consume();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.InputEvent#getID()
	 */
	public int getID() {
		// TODO Auto-generated method stub
		return event.getID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.InputEvent#getModifiers()
	 */
	public int getModifiers() {
		// TODO Auto-generated method stub
		return event.getModifiers();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.InputEvent#getModifiersEx()
	 */
	public int getModifiersEx() {
		// TODO Auto-generated method stub
		return event.getModifiersEx();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.InputEvent#getWhen()
	 */
	public long getWhen() {
		// TODO Auto-generated method stub
		return event.getWhen();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.InputEvent#isAltDown()
	 */
	public boolean isAltDown() {
		// TODO Auto-generated method stub
		return event.isAltDown();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.InputEvent#isAltGraphDown()
	 */
	public boolean isAltGraphDown() {
		// TODO Auto-generated method stub
		return event.isAltGraphDown();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.InputEvent#isConsumed()
	 */
	public boolean isConsumed() {
		// TODO Auto-generated method stub
		return event.isConsumed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.InputEvent#isControlDown()
	 */
	public boolean isControlDown() {
		// TODO Auto-generated method stub
		return event.isControlDown();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.InputEvent#isMetaDown()
	 */
	public boolean isMetaDown() {
		// TODO Auto-generated method stub
		return event.isMetaDown();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.InputEvent#isShiftDown()
	 */
	public boolean isShiftDown() {
		// TODO Auto-generated method stub
		return event.isShiftDown();
	}

}
