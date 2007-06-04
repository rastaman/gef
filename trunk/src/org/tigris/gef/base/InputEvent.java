package org.tigris.gef.base;

public interface InputEvent {

	/**
	 * The Shift key modifier constant. It is recommended that SHIFT_DOWN_MASK
	 * be used instead. 
	 * 
	 * All the static final int have to be initiated to 0 
	 */
	public static final int SHIFT_MASK = 0;

	/**
	 * The Control key modifier constant. It is recommended that CTRL_DOWN_MASK
	 * be used instead.
	 */
	public static final int CTRL_MASK = 0;

	/**
	 * The Meta key modifier constant. It is recommended that META_DOWN_MASK be
	 * used instead.
	 */
	public static final int META_MASK = 0;

	/**
	 * The Alt key modifier constant. It is recommended that ALT_DOWN_MASK be
	 * used instead.
	 */
	public static final int ALT_MASK = 0;

	/**
	 * The AltGraph key modifier constant.
	 */
	public static final int ALT_GRAPH_MASK = 1 << 5;

	/**
	 * The Mouse Button1 modifier constant. It is recommended that
	 * BUTTON1_DOWN_MASK be used instead.
	 */
	public static final int BUTTON1_MASK = 1 << 4;

	/**
	 * The Mouse Button2 modifier constant. It is recommended that
	 * BUTTON2_DOWN_MASK be used instead. Note that BUTTON2_MASK has the same
	 * value as ALT_MASK.
	 */
	public static final int BUTTON2_MASK = 0;

	/**
	 * The Mouse Button3 modifier constant. It is recommended that
	 * BUTTON3_DOWN_MASK be used instead. Note that BUTTON3_MASK has the same
	 * value as META_MASK.
	 */
	public static final int BUTTON3_MASK = 0;

	/**
	 * The Shift key extended modifier constant.
	 * 
	 * @since 1.4
	 */
	public static final int SHIFT_DOWN_MASK = 1 << 6;

	/**
	 * The Control key extended modifier constant.
	 * 
	 * @since 1.4
	 */
	public static final int CTRL_DOWN_MASK = 1 << 7;

	/**
	 * The Meta key extended modifier constant.
	 * 
	 * @since 1.4
	 */
	public static final int META_DOWN_MASK = 1 << 8;

	/**
	 * The Alt key extended modifier constant.
	 * 
	 * @since 1.4
	 */
	public static final int ALT_DOWN_MASK = 1 << 9;

	/**
	 * The Mouse Button1 extended modifier constant.
	 * 
	 * @since 1.4
	 */
	public static final int BUTTON1_DOWN_MASK = 1 << 10;

	/**
	 * The Mouse Button2 extended modifier constant.
	 * 
	 * @since 1.4
	 */
	public static final int BUTTON2_DOWN_MASK = 1 << 11;

	/**
	 * The Mouse Button3 extended modifier constant.
	 * 
	 * @since 1.4
	 */
	public static final int BUTTON3_DOWN_MASK = 1 << 12;

	/**
	 * The AltGraph key extended modifier constant.
	 * 
	 * @since 1.4
	 */
	public static final int ALT_GRAPH_DOWN_MASK = 1 << 13;

	/**
	 * Returns whether or not the Shift modifier is down on this event.
	 */
	public boolean isShiftDown();

	/**
	 * Returns whether or not the Control modifier is down on this event.
	 */
	public boolean isControlDown();

	/**
	 * Returns whether or not the Meta modifier is down on this event.
	 */
	public boolean isMetaDown();

	/**
	 * Returns whether or not the Alt modifier is down on this event.
	 */
	public boolean isAltDown();

	/**
	 * Returns whether or not the AltGraph modifier is down on this event.
	 */
	public boolean isAltGraphDown();

	/**
	 * Returns the timestamp of when this event occurred.
	 */
	public long getWhen();

	/**
	 * Returns the modifier mask for this event.
	 */
	public int getModifiers();

	/**
	 * Returns the extended modifier mask for this event. Extended modifiers
	 * represent the state of all modal keys, such as ALT, CTRL, META, and the
	 * mouse buttons just after the event occurred
	 * <P>
	 * For example, if the user presses <b>button 1</b> followed by <b>button 2</b>,
	 * and then releases them in the same order, the following sequence of
	 * events is generated:
	 * 
	 * <PRE>
	 *    <code>MOUSE_PRESSED</code>: <code>BUTTON1_DOWN_MASK</code>
	 *    <code>MOUSE_PRESSED</code>:
	 * <code>BUTTON1_DOWN_MASK | BUTTON2_DOWN_MASK</code>
	 *    <code>MOUSE_RELEASED</code>:
	 * <code>BUTTON2_DOWN_MASK</code>
	 *    <code>MOUSE_CLICKED</code>:
	 * <code>BUTTON2_DOWN_MASK</code>
	 *    <code>MOUSE_RELEASED</code>:
	 * <code>MOUSE_CLICKED</code>:
	 * 
	 * </PRE>
	 * 
	 * <P>
	 * It is not recommended to compare the return value of this method using
	 * <code>==</code> because new modifiers can be added in the future. For
	 * example, the appropriate way to check that SHIFT and BUTTON1 are down,
	 * but CTRL is up is demonstrated by the following code:
	 * 
	 * <PRE>
	 * 
	 * int onmask = SHIFT_DOWN_MASK | BUTTON1_DOWN_MASK; int offmask =
	 * CTRL_DOWN_MASK; if (event.getModifiersEx() & (onmask | offmask) ==
	 * onmask) { ... }
	 * 
	 * </PRE>
	 * 
	 * The above code will work even if new modifiers are added.
	 * 
	 * @since 1.4
	 */
	public int getModifiersEx();

	/**
	 * Consumes this event so that it will not be processed in the default
	 * manner by the source which originated it.
	 */
	public void consume();

	/**
	 * Returns whether or not this event has been consumed.
	 * 
	 * @see #consume
	 */
	public boolean isConsumed();

	/**
	 * Returns id
	 */
	public int getID();
}