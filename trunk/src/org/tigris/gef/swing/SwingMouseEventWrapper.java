/**
 * 
 */
package org.tigris.gef.swing;

import java.awt.Point;

/**
 * @author johnny
 * 
 */
public class SwingMouseEventWrapper implements org.tigris.gef.base.MouseEvent {

	private java.awt.event.MouseEvent event;

	//set the private event to the awt MouseEvent
	public SwingMouseEventWrapper(java.awt.event.MouseEvent me) {
		this.event = me;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.MouseEvent#getButton()
	 */
	public int getButton() {
		// TODO Auto-generated method stub
		return event.getButton();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.MouseEvent#getClickCount()
	 */
	public int getClickCount() {
		// TODO Auto-generated method stub
		return event.getClickCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.MouseEvent#getPoint()
	 */
	public Point getPoint() {
		// TODO Auto-generated method stub
		return event.getPoint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.MouseEvent#getX()
	 */
	public int getX() {
		// TODO Auto-generated method stub
		return event.getX();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.MouseEvent#getY()
	 */
	public int getY() {
		// TODO Auto-generated method stub
		return event.getY();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.MouseEvent#isPopupTrigger()
	 */
	public boolean isPopupTrigger() {
		// TODO Auto-generated method stub
		return event.isPopupTrigger();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.MouseEvent#paramString()
	 */
	public String paramString() {
		// TODO Auto-generated method stub
		return event.paramString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.gef.base.MouseEvent#translatePoint(int, int)
	 */
	public void translatePoint(int x, int y) {
		// TODO Auto-generated method stub
		event.translatePoint(x, y);
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
