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



// File: FigText.java
// Classes: FigText
// Original Author: ics125 spring 1996
// $Id$

package org.tigris.gef.presentation;

import org.apache.commons.logging.*;
import org.tigris.gef.properties.PropCategoryManager;

import java.awt.*;
import java.awt.event.*;
import java.util.StringTokenizer;

/** This class handles painting and editing text Fig's in a
 *  LayerDiagram. Needs-More-Work: should eventually allow styled text
 *  editing, ... someday... */

public class FigText extends Fig implements KeyListener, MouseListener {

    ////////////////////////////////////////////////////////////////
    // constants

    /** Constants to specify text justification. */
    public static final int JUSTIFY_LEFT = 0;
    public static final int JUSTIFY_RIGHT = 1;
    public static final int JUSTIFY_CENTER = 2;

    /** Minimum size of a FigText object. */
    public static final int MIN_TEXT_WIDTH = 30;

    ////////////////////////////////////////////////////////////////
    // instance variables

    /** Font info. */
    private Font _font = new Font("TimesRoman", Font.PLAIN, 10);
    private transient FontMetrics _fm;
    private int _lineHeight;

    /** Color of the actual text characters. */
    private Color _textColor = Color.black;

    /** Color to be drawn behind the actual text characters. Note that
     *  this will be a smaller area than the bounding box which is
     *  filled with FillColor. */
    private Color _textFillColor = Color.white;

    /** True if the area behind individual characters is to be filled
     *  with TextColor. */
    private boolean _textFilled = false;

    /** True if the text should be editable. False for read-only. */
    private boolean _editable = true;

    private Class _textEditorClass = FigTextEditor.class;

    /** True if the text should be underlined. needs-more-work. */
    private boolean _underline = false;

    /** True if more than one line of text is allow. If false, newline
     *  characters will be ignored. True by default. */
    private boolean _multiLine = true;

    private boolean _allowsTab = true;

    /** Extra spacing between lines. Default is 0 pixels. */
    private int _lineSpacing = 0;

    /** Internal margins between the text and the edge of the rectangle. */
    private int _topMargin = 1;
    private int _botMargin = 1;
    private int _leftMargin = 1;
    private int _rightMargin = 1;

    /** True if the FigText can only grow in size, never shrink. */
    private boolean _expandOnly = false;

    private boolean _editMode = false;

    /** Text justification can be JUSTIFY_LEFT, JUSTIFY_RIGHT, or JUSTIFY_CENTER. */
    private int _justification = JUSTIFY_LEFT;

    /** The current string to display. */
    private String _curText;

    ////////////////////////////////////////////////////////////////
    // static initializer

    /** This puts the text properties on the "Text" and "Style" pages of
     * the org.tigris.gef.ui.TabPropFrame. */
    static {
        PropCategoryManager.categorizeProperty("Text", "font");
        PropCategoryManager.categorizeProperty("Text", "underline");
        PropCategoryManager.categorizeProperty("Text", "expandOnly");
        PropCategoryManager.categorizeProperty("Text", "lineSpacing");
        PropCategoryManager.categorizeProperty("Text", "topMargin");
        PropCategoryManager.categorizeProperty("Text", "botMargin");
        PropCategoryManager.categorizeProperty("Text", "leftMargin");
        PropCategoryManager.categorizeProperty("Text", "rightMargin");
        PropCategoryManager.categorizeProperty("Text", "text");
        PropCategoryManager.categorizeProperty("Style", "justification");
        PropCategoryManager.categorizeProperty("Style", "textFilled");
        PropCategoryManager.categorizeProperty("Style", "textFillColor");
        PropCategoryManager.categorizeProperty("Style", "textColor");
    }

    private static final Log LOG = LogFactory.getLog(FigText.class);

    ////////////////////////////////////////////////////////////////
    // constructors

    /** Construct a new FigText with the given position, size, color,
     *  string, font, and font size. Text string is initially empty and
     *  centered. */
    public FigText(int x, int y, int w, int h, Color textColor, String familyName, int fontSize, boolean expandOnly) {
        super(x, y, w, h);
        _x = x;
        _y = y;
        _w = w;
        _h = h;
        _textColor = textColor;
        _font = new Font(familyName, Font.PLAIN, fontSize);
        _justification = JUSTIFY_CENTER;
        _curText = "";
        _expandOnly = expandOnly;
    }

    public FigText(int x, int y, int w, int h, Color textColor, String familyName, int fontSize) {
        this(x, y, w, h, textColor, familyName, fontSize, false);
    }

    public FigText(int x, int y, int w, int h, Color textColor, Font font) {
        this(x, y, w, h, textColor, font.getName(), font.getSize());
    }

    /** Construct a new FigText with the given position and size */
    public FigText(int x, int y, int w, int h) {
        super(x, y, w, h);
        _x = x;
        _y = y;
        _w = w;
        _h = h;
        _justification = JUSTIFY_CENTER;
        _curText = "";
        _expandOnly = false;
    }

    /** Construct a new FigText with the given position, size, and attributes. */
    public FigText(int x, int y, int w, int h, boolean expandOnly) {
        super(x, y, w, h);
        _x = x;
        _y = y;
        _w = w;
        _h = h;
        _justification = JUSTIFY_CENTER;
        _curText = "";
        _expandOnly = expandOnly;
    }
    ////////////////////////////////////////////////////////////////
    // invariant

    /** Check the class invariant to make sure that this FigText is in a
     *  valid state.  Useful for debugging. */
    public boolean OK() {
        if(!super.OK())
            return false;
        return _font != null && _lineSpacing > -20 && _topMargin >= 0 && _botMargin >= 0 && _leftMargin >= 0 && _rightMargin >= 0 && (_justification == JUSTIFY_LEFT || _justification == JUSTIFY_CENTER || _justification == JUSTIFY_RIGHT) && _textColor != null && _textFillColor != null;
    }

    ////////////////////////////////////////////////////////////////
    // accessors

    /** Reply a string that indicates how the text is justified: Left,
     *  Center, or Right. */
    public String getJustificationByName() {
        if(_justification == JUSTIFY_LEFT)
            return "Left";
        else if(_justification == JUSTIFY_CENTER)
            return "Center";
        else if(_justification == JUSTIFY_RIGHT)
            return "Right";
        LOG.error("internal error, unknown text alignment");
        return "Unknown";
    }

    /** Set the text justification given one of these strings: Left,
     *  Center, or Right. */
    public void setJustificationByName(String justifyString) {
        if(justifyString.equals("Left"))
            _justification = JUSTIFY_LEFT;
        else if(justifyString.equals("Center"))
            _justification = JUSTIFY_CENTER;
        else if(justifyString.equals("Right"))
            _justification = JUSTIFY_RIGHT;
        _fm = null;
    }


    ////////////////////////////////////////////////////////////////
    // accessors and modifiers

    public Color getTextColor() {
        return _textColor;
    }

    public void setTextColor(Color c) {
        firePropChange("textColor", _textColor, c);
        _textColor = c;
    }

    public Color getTextFillColor() {
        return _textFillColor;
    }

    public void setTextFillColor(Color c) {
        firePropChange("textFillColor", _textFillColor, c);
        _textFillColor = c;
    }

    public boolean getTextFilled() {
        return _textFilled;
    }

    public void setTextFilled(boolean b) {
        firePropChange("textFilled", _textFilled, b);
        _textFilled = b;
    }

    public boolean getEditable() {
        return _editable;
    }

    public void setEditable(boolean e) {
        firePropChange("editable", _editable, e);
        _editable = e;
    }

    public boolean getUnderline() {
        return _underline;
    }

    public void setUnderline(boolean b) {
        firePropChange("underline", _underline, b);
        _underline = b;
    }

    public int getJustification() {
        return _justification;
    }

    public void setJustification(int align) {
        firePropChange("justification", getJustification(), align);
        _justification = align;
    }

    public int getLineSpacing() {
        return _lineSpacing;
    }

    public void setLineSpacing(int s) {
        firePropChange("lineSpacing", _lineSpacing, s);
        _lineSpacing = s;
        calcBounds();
    }

    public int getTopMargin() {
        return _topMargin;
    }

    public void setTopMargin(int m) {
        firePropChange("topMargin", _topMargin, m);
        _topMargin = m;
        calcBounds();
    }

    public int getBotMargin() {
        return _botMargin;
    }

    public void setBotMargin(int m) {
        firePropChange("botMargin", _botMargin, m);
        _botMargin = m;
        calcBounds();
    }

    public int getLeftMargin() {
        return _leftMargin;
    }

    public void setLeftMargin(int m) {
        firePropChange("leftMargin", _leftMargin, m);
        _leftMargin = m;
        calcBounds();
    }

    public int getRightMargin() {
        return _rightMargin;
    }

    public void setRightMargin(int m) {
        firePropChange("rightMargin", _rightMargin, m);
        _rightMargin = m;
        calcBounds();
    }

    public boolean getExpandOnly() {
        return _expandOnly;
    }

    public void setExpandOnly(boolean b) {
        firePropChange("expandOnly", _expandOnly, b);
        _expandOnly = b;
    }

    public Font getFont() {
        return _font;
    }

    public void setFont(Font f) {
        firePropChange("font", _font, f);
        _font = f;
        _fm = null;
        calcBounds();
    }

    /**
     * USED BY PGML.tee
     */
    public String getFontFamily() {
        return _font.getFamily();
    }

    /**
     * USED BY PGML.tee
     */
    public void setFontFamily(String familyName) {
        Font f = new Font(familyName, _font.getStyle(), _font.getSize());
        setFont(f);
    }

    /**
     * USED BY PGML.tee
     */
    public int getFontSize() {
        return _font.getSize();
    }

    /**
     * USED BY PGML.tee
     */
    public void setFontSize(int size) {
        Font f = new Font(_font.getFamily(), _font.getStyle(), size);
        setFont(f);
    }

    public boolean getItalic() {
        return _font.isItalic();
    }

    public void setItalic(boolean b) {
        int style = (getBold() ? Font.BOLD : 0) + (b ? Font.ITALIC : 0);
        Font f = new Font(_font.getFamily(), style, _font.getSize());
        setFont(f);
    }

    public boolean getBold() {
        return _font.isBold();
    }

    public void setBold(boolean b) {
        int style = (b ? Font.BOLD : 0) + (getItalic() ? Font.ITALIC : 0);
        setFont(new Font(_font.getFamily(), style, _font.getSize()));
    }

    public void setMultiLine(boolean b) {
        _multiLine = b;
    }

    public boolean getMultiLine() {
        return _multiLine;
    }

    public void setAllowsTab(boolean b) {
        _allowsTab = b;
    }

    public boolean getAllowsTab() {
        return _allowsTab;
    }

    /** Remove the last char from the current string line and return the
     *  new string.  Called whenever the user hits the backspace key.
     *  Needs-More-Work: Very slow.  This will eventually be replaced by
     *  full text editing... if there are any volunteers to do that...*/
    public String deleteLastCharFromString(String s) {
        int len = Math.max(s.length() - 1, 0);
        char[] chars = s.toCharArray();
        return new String(chars, 0, len);
    }

    /** Delete the last char from the current string. Called whenever
     *  the user hits the backspace key */
    public void deleteLastChar() {
        _curText = deleteLastCharFromString(_curText);
        calcBounds();
    }

    /** Append a character to the current String .*/
    public void append(char c) {
        setText(_curText + c);
    }

    /** Append the given String to the current String. */
    public void append(String s) {
        setText(_curText + s);
    }

    /**
     * Set the give string to be the current string of this fig.
     * Update the current font and font metrics first.
     *
     * @param str String to be set at this object.
     * @param graphics Graphics context for the operation.
     */
    public void setText(String str, Graphics graphics) {
        if(graphics != null)
            _fm = graphics.getFontMetrics(_font);

        setText(str);
    }

    /**
     * Sets the given string to the current string of this fig.
     *
     * @param s
     */
    public void setText(String s) {
        _curText = s;
        calcBounds();
        _editMode = false;
    }

    /** Get the String held by this FigText. Multi-line text is
     *  represented by newline characters embedded in the String.
     * USED BY PGML.tee
     */
    public String getText() {
        return _curText;
    }

    public Class getTextEditorClass() {
        return _textEditorClass;
    }

    public void setTextEditorClass(Class editorClass) {
        _textEditorClass = editorClass;
    }

    ////////////////////////////////////////////////////////////////
    // painting methods

    /** Paint the FigText.
     *  Distingusih between linewidth=1 and >1
     *  If <linewidth> is equal 1, then paint a single rectangle
     *  Otherwise paint <linewidth> nested rectangles, whereas
     *  every rectangle is painted of 4 connecting lines.
     */

    public void paint(Graphics g) {
        if(!(isVisible()))
            return;
        //System.out.println("FigText.paint: x/y = " + _x + "/" + _y);
        //System.out.println("FigText.paint: top-/bottomMargin = " + _topMargin + "/" + _botMargin);
        int chunkX = _x + _leftMargin;
        int chunkY = _y + _topMargin;
        StringTokenizer lines;

        int lineWidth = getLineWidth();

        if (getFilled()) {
            g.setColor(getFillColor());
            g.fillRect(_x, _y, _w, _h);
        }
        if(lineWidth > 0) {
            g.setColor(getLineColor());
            // test linewidth
            if(lineWidth == 1) {
                // paint single rectangle
                g.drawRect(_x, _y, _w - lineWidth, _h - lineWidth);
            }
            else {
                // paint <linewidth rectangles
                for(int i = 0; i < lineWidth; i++) {
                    // a rectangle is painted as four connecting lines
                    g.drawLine(_x + i, _y + i, _x + _w - i, _y + i);
                    g.drawLine(_x + _w - i, _y + i, _x + _w - i, _y + _h - i);
                    g.drawLine(_x + _w - i, _y + _h - i, _x + i, _y + _h - i);
                    g.drawLine(_x + i, _y + _h - i, _x + i, _y + i);
                }
            }
        }
        if(_font != null)
            g.setFont(_font);
        _fm = g.getFontMetrics(_font);
        //System.out.println("FigText.paint: font height = " + _fm.getHeight());
        int chunkH = _fm.getHeight() + _lineSpacing;
        //System.out.println("FigText.paint: chunkH = " + chunkH);
        chunkY = _y + _topMargin + chunkH;
        if(_textFilled) {
            g.setColor(_textFillColor);
            lines = new StringTokenizer(_curText, "\n\r", true);
            while(lines.hasMoreTokens()) {
                String curLine = lines.nextToken();
                //if (curLine.equals("\r")) continue;
                int chunkW = _fm.stringWidth(curLine);
                switch(_justification) {
                    case JUSTIFY_LEFT:
                        break;
                    case JUSTIFY_CENTER:
                        chunkX = _x + (_w - chunkW) / 2;
                        break;
                    case JUSTIFY_RIGHT:
                        chunkX = _x + _w - chunkW - _rightMargin;
                        break;
                }
                if(curLine.equals("\n") || curLine.equals("\r"))
                    chunkY += chunkH;
                else
                    g.fillRect(chunkX, chunkY - chunkH, chunkW, chunkH);
            }
        }

        g.setColor(_textColor);
        chunkX = _x + _leftMargin;
        chunkY = _y + _topMargin + chunkH;
        //System.out.println("FigText.paint: chunkY = " + chunkY);
        lines = new StringTokenizer(_curText, "\n\r", true);
        while(lines.hasMoreTokens()) {
            String curLine = lines.nextToken();
            //if (curLine.equals("\r")) continue;
            int chunkW = _fm.stringWidth(curLine);
            switch(_justification) {
                case JUSTIFY_LEFT:
                    break;
                case JUSTIFY_CENTER:
                    chunkX = _x + (_w - chunkW) / 2;
                    break;
                case JUSTIFY_RIGHT:
                    chunkX = _x + _w - chunkW;
                    break;
            }
            if(curLine.equals("\n") || curLine.equals("\r"))
                chunkY += chunkH;
            else {
                if(_underline)
                    g.drawLine(chunkX, chunkY + 1, chunkX + chunkW, chunkY + 1);
                drawString(g, curLine, chunkX, chunkY);
            }
        }
    }

    /**
     * Draws the given string starting at the given position. The position indicates
     * the baseline of the text. This method enables subclasses of FigText to either
     * change the displayed text or the starting position.
     *
     * @param graphics Graphic context for drawing the string.
     * @param curLine The current text to be drawn.
     * @param xPos X-Coordinate of the starting point.
     * @param yPos Y-Coordinate of the starting point.
     */
    protected void drawString(Graphics graphics, String curLine, int xPos, int yPos) {
        graphics.drawString(curLine, xPos, yPos);
    }

    /** Muse clicks are handled differentlty that the defi]ault Fig
     *  behavior so that it is easier to select text that is not
     *  filled.  Needs-More-Work: should actually check the individual
     *  text rectangles. */
    public boolean hit(Rectangle r) {
        int cornersHit = countCornersContained(r.x, r.y, r.width, r.height);
        return cornersHit > 0;
    }

    public int getMinimumHeight() {
        if(_fm != null)
            return _fm.getHeight();
        if(_font != null)
            return _font.getSize();
        return 0;
    }

    public int getTextBounds(Graphics graphics) {
        if(_font != null) {
            FontMetrics fontMetrics = graphics.getFontMetrics();
            return fontMetrics.stringWidth(getText());
        }
        else
            return 0;
    }

    public Dimension getMinimumSize() {
        Dimension d = new Dimension(0, 0);
        stuffMinimumSize(d);
        return d;
    }

    public void stuffMinimumSize(Dimension d) {
        if(_font == null)
            return;
        // usage of getFontMetrics() is deprecated
        //if (_fm == null) _fm = Toolkit.getDefaultToolkit().getFontMetrics(_font);
        int overallW = 0;
        int numLines = 1;
        StringTokenizer lines = new StringTokenizer(_curText, "\n\r", true);
        while(lines.hasMoreTokens()) {
            String curLine = lines.nextToken();
            int chunkW = _fm.stringWidth(curLine);
            if(curLine.equals("\n") || curLine.equals("\r"))
                numLines++;
            else
                overallW = Math.max(chunkW, overallW);
        }
        //_lineHeight = _fm.getHeight();
        //int maxDescent = _fm.getMaxDescent();
        if(_fm == null)
            _lineHeight = _font.getSize();
        else
            _lineHeight = _fm.getHeight();
        int maxDescent = 0;
        int overallH = (_lineHeight + _lineSpacing) * numLines + _topMargin + _botMargin + maxDescent;
        overallH = Math.max(overallH, getMinimumHeight());
        overallW = Math.max(MIN_TEXT_WIDTH, overallW + _leftMargin + _rightMargin);
        d.width = overallW;
        d.height = overallH;
        //System.out.println("FigText.minimumSize: " + getText() + " = " + overallW + " / " + overallH);
    }

    ////////////////////////////////////////////////////////////////
    // event handlers: KeyListener implemtation

    /** When the user presses a key when a FigText is selected, that key
     *  should be added to the current string, or if the key was
     *  backspace, the last character is removed.  Needs-More-Work: Should
     *  also catch arrow keys and mouse clicks for full text
     *  editing... someday... */
    public void keyTyped(KeyEvent ke) {
        if(!_editable)
            return;
//     int mods = ke.getModifiers();
//     if (mods != 0 && mods != KeyEvent.SHIFT_MASK) return;
//     char c = ke.getKeyChar();
//     if (!Character.isISOControl(c)) {
//       FigTextEditor te = startTextEditor(ke);
//       te.keyTyped(ke);
// //       append(c);
// //       endTrans();
//       ke.consume();
//     }
    }

    /** This method handles backspace and enter. */
    public void keyPressed(KeyEvent ke) {
        if(!ke.isActionKey() && !isNonStartEditingKey(ke)) {
            if(!_editable)
                return;
            FigTextEditor te = startTextEditor(ke);
            if(System.getProperty("java.version").startsWith("1.4"))
                te.setText(te.getText() + ke.getKeyChar());
            ke.consume();
        }
    }

    /** Not used, does nothing. */
    public void keyReleased(KeyEvent ke) {
        if(!_editable)
            return;
    }

    protected boolean isNonStartEditingKey(KeyEvent ke) {
        int keyCode = ke.getKeyCode();
        switch(keyCode) {
            case KeyEvent.VK_TAB:
            case KeyEvent.VK_CANCEL:
            case KeyEvent.VK_CLEAR:
            case KeyEvent.VK_SHIFT:
            case KeyEvent.VK_CONTROL:
            case KeyEvent.VK_ALT:
            case KeyEvent.VK_META:
            case KeyEvent.VK_HELP:
            case KeyEvent.VK_PAUSE:
            case KeyEvent.VK_DELETE:
            case KeyEvent.VK_ESCAPE:
                return true;
        }
        if(ke.isControlDown())
            return true;
        if(ke.isAltDown())
            return true;
        if(ke.isMetaDown())
            return true;
        return false;
    }
    ////////////////////////////////////////////////////////////////
    // event handlers: KeyListener implemtation

    public void mouseClicked(MouseEvent me) {
        //System.out.println("[FigText] mouseClicked");
        if(me.isConsumed())
            return;
        if(me.getClickCount() >= 2) {
            if(!_editable)
                return;
            startTextEditor(me);
            me.consume();
        }
    }

    public void mousePressed(MouseEvent me) {
    }

    public void mouseReleased(MouseEvent me) {
    }

    public void mouseEntered(MouseEvent me) {
    }

    public void mouseExited(MouseEvent me) {
    }

    public FigTextEditor startTextEditor(InputEvent ie) {
        //System.out.println("[FigText] startTextEditor");
        FigTextEditor te;
        try {
            Object editor = _textEditorClass.newInstance();
            if(!(editor instanceof FigTextEditor))
                te = new FigTextEditor();
            else
                te = (FigTextEditor)editor;
        }
        catch(Exception e) {
            te = new FigTextEditor();
        }
        te.init(this, ie);
        //System.out.println("[FigText] TextEditor started");
        _editMode = true;
        return te;
    }


    ////////////////////////////////////////////////////////////////
    // internal utility functions

    /** Compute the overall width and height of the FigText object based
     *  on the font, font size, and current text. Needs-More-Work: Right
     *  now text objects can get larger when you type more, but they
     *  do not get smaller when you backspace.  */
    public void calcBounds() {
        Rectangle bounds = getBounds();
        if(_font == null) {
            return;
        }
        if (_fm == null) {
            _fm = Toolkit.getDefaultToolkit().getFontMetrics(_font);
        }
        int overallW = 0;
        int numLines = 1;
        StringTokenizer lines = new StringTokenizer(_curText, "\n\r", true);
        while(lines.hasMoreTokens()) {
            String curLine = lines.nextToken();
            int chunkW = _fm.stringWidth(curLine);
            if(curLine.equals("\n") || curLine.equals("\r"))
                numLines++;
            else
                overallW = Math.max(chunkW, overallW);
        }
        _lineHeight = _fm.getHeight();
        int maxDescent = _fm.getMaxDescent();
        int overallH = (_lineHeight + _lineSpacing) * numLines + _topMargin + _botMargin + maxDescent;
        overallW = Math.max(MIN_TEXT_WIDTH, overallW + _leftMargin + _rightMargin);
        if(_editMode) {
            switch(_justification) {
                case JUSTIFY_LEFT:
                    break;

                case JUSTIFY_CENTER:
                    if(_w < overallW)
                        _x -= (overallW - _w) / 2;
                    break;

                case JUSTIFY_RIGHT:
                    if(_w < overallW)
                        _x -= (overallW - _w);
                    break;
            }
        }
        _w = _expandOnly ? Math.max(_w, overallW) : overallW;
        _h = _expandOnly ? Math.max(_h, overallH) : overallH;
    }

} /* end class FigText */

