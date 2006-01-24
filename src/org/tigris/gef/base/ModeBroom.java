// %1034862399516:org.tigris.gef.base%
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
// File: ModeBroom.java
// Classes: ModeBroom
// Original Author: ics125 spring 1996
// $Id$
package org.tigris.gef.base;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.graph.MutableGraphSupport;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigEdge;
import org.tigris.gef.presentation.FigNode;

public class ModeBroom extends FigModifyingModeImpl {
    ////////////////////////////////////////////////////////////////
    // constants
    private final int DIRECTION_UNDEFINED = 0;
    private final int DIRECTION_UPWARD = 1;
    private final int DIRECTION_DOWNWARD = 2;
    private final int DIRECTION_RIGHTWARD = 3;
    private final int DIRECTION_LEFTWARD = 4;
    private final int DEFINE_DIRECTION_TOLERANCE = 10;
    private final int BROOM_WIDTH_NORMAL = 200;
    private final int BROOM_WIDTH_SMALL = 30;
    private final int MAX_TOUCHED_FIGS = 1000;
    private final int DISTRIBUTE_EVEN_SPACE = 0;
    private final int DISTRIBUTE_PACK = 1;
    private final int DISTRIBUTE_SPREAD = 2;
    private final int DISTRIBUTE_ORIG = 3;
    private final Font HINT_FONT = new Font("Dialog", Font.PLAIN, 9);
    ////////////////////////////////////////////////////////////////
    // instance variables
    private Point _start = new Point(0, 0);
    private Vector _LastTouched = new Vector();
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private int _lastX1;
    private int _lastY1;
    private int _lastX2;
    private int _lastY2;
    private int _lastMX;
    private int _lastMY;
    private int _dir = DIRECTION_UNDEFINED;
    private boolean _magnetic = false;
    private boolean _movable = true;
    private boolean _draw = false;
    private Fig[] _touched = new Fig[MAX_TOUCHED_FIGS];
    private int[] _origX = new int[MAX_TOUCHED_FIGS];
    private int[] _origY = new int[MAX_TOUCHED_FIGS];
    private int[] _offX = new int[MAX_TOUCHED_FIGS];
    private int[] _offY = new int[MAX_TOUCHED_FIGS];
    private int _nTouched = 0;
    private int _broomMargin = 0;
    private int _distributeMode = 0;
    private Rectangle _addRect = new Rectangle();
    private Rectangle _selectRect = new Rectangle();
    private Rectangle _bigDamageRect = new Rectangle(0, 0, 400, 400);
    private Rectangle _origBBox = null;
    private String _hint = null;

    ////////////////////////////////////////////////////////////////
    // constructors and related methods

    /** Construct a new ModeBroom with the given parent. */
    public ModeBroom(Editor par) {
        super(par);
    }

    /** Construct a new ModeBroom instance. Its parent must be set
     *  before this instance can be used.  */
    public ModeBroom() {
    }

    ////////////////////////////////////////////////////////////////
    // event handlers

    /** Handle mouse down events by preparing for a drag. If the mouse
     *  down event happens on a handle or an already selected object, and
     *  the shift key is not down, then go to ModeModify. If the mouse
     *  down event happens on a port, to to ModeCreateEdge.   */
    public void mousePressed(MouseEvent me) {
        if(me.isConsumed()) {
            return;
        }

        _touched = new Fig[MAX_TOUCHED_FIGS];
        _origX = new int[MAX_TOUCHED_FIGS];
        _origY = new int[MAX_TOUCHED_FIGS];
        _offX = new int[MAX_TOUCHED_FIGS];
        _offY = new int[MAX_TOUCHED_FIGS];
        _nTouched = 0;
        _dir = DIRECTION_UNDEFINED;
        _magnetic = false;
        _draw = true;
        x1 = x2 = _start.x = me.getX();
        y1 = y2 = _start.y = me.getY();
        _lastX1 = x1;
        _lastY1 = y1;
        _lastX2 = x2;
        _lastY2 = y2;
        _selectRect.setBounds(x1 - 14, y1 - 14, x2 - x1 + 28, y2 - y1 + 28);
        editor.damaged(_selectRect);
        //        editor.getSelectionManager().deselectAll();
        me.consume();
        _hint = null;
        start();
    }

    /** On mouse dragging, stretch the selection rectangle. */
    public void mouseDragged(MouseEvent me) {
        if(me.isConsumed()) {
            return;
        }

        me.consume();
        editor.getSelectionManager().deselectAll();
        Point snapPt = me.getPoint();
        snapPt.x = Math.max(0, snapPt.x);
        snapPt.y = Math.max(0, snapPt.y);
        _lastMX = snapPt.x;
        _lastMY = snapPt.y;
        editor.snap(snapPt);
        int x = snapPt.x;
        int y = snapPt.y;
        int i;
        _selectRect.setBounds(x1 - 4, y1 - 4, x2 - x1 + 8, y2 - y1 + 8);
        _bigDamageRect.setLocation(x1 - 200, y1 - 200);
        editor.damaged(_bigDamageRect);
        editor.damaged(_selectRect);
        if(_dir == DIRECTION_UNDEFINED) {
            if(me.isShiftDown()) {
                _broomMargin = BROOM_WIDTH_SMALL;
            }
            else {
                _broomMargin = BROOM_WIDTH_NORMAL;
            }

            int dx = me.getX() - _start.x;
            int dy = me.getY() - _start.y;
            if(Math.abs(dx) < DEFINE_DIRECTION_TOLERANCE && Math.abs(dy) < DEFINE_DIRECTION_TOLERANCE) {
                return;
            }

            if(Math.abs(dx) > Math.abs(dy)) {
                _dir = (dx > 0) ? DIRECTION_RIGHTWARD : DIRECTION_LEFTWARD;
                x1 = x2 = x;
                y1 = y - _broomMargin / 2;
                y2 = y + _broomMargin / 2;
                if(me.isShiftDown()) {
                    y1 = y - _broomMargin / 2;
                    y2 = y + _broomMargin / 2;
                }
            }
            else {
                _dir = (dy > 0) ? DIRECTION_DOWNWARD : DIRECTION_UPWARD;
                y1 = y2 = y;
                x1 = x - _broomMargin / 2;
                x2 = x + _broomMargin / 2;
                if(me.isShiftDown()) {
                    x1 = x - _broomMargin / 2;
                    x2 = x + _broomMargin / 2;
                }
            }
        }

        if(!_magnetic) {
            addNewItems();
        }

        _lastX1 = x1;
        _lastY1 = y1;
        _lastX2 = x2;
        _lastY2 = y2;
        switch(_dir) {

            case DIRECTION_UPWARD:
                if(_movable) {
                    y1 = y2 = Math.min(y, _start.y);
                    if(_magnetic) {
                        y1 = y2 = y;
                    }
                }

                x1 = Math.min(x1, _lastMX - _broomMargin / 2);
                x2 = Math.max(x2, _lastMX + _broomMargin / 2);
                break;

            case DIRECTION_DOWNWARD:
                if(_movable) {
                    y1 = y2 = Math.max(y, _start.y);
                    if(_magnetic) {
                        y1 = y2 = y;
                    }
                }

                x1 = Math.min(x1, _lastMX - _broomMargin / 2);
                x2 = Math.max(x2, _lastMX + _broomMargin / 2);
                break;

            case DIRECTION_RIGHTWARD:
                if(_movable) {
                    x1 = x2 = Math.max(x, _start.x);
                    if(_magnetic) {
                        x1 = x2 = x;
                    }
                }

                y1 = Math.min(y1, _lastMY - _broomMargin / 2);
                y2 = Math.max(y2, _lastMY + _broomMargin / 2);
                break;

            case DIRECTION_LEFTWARD:
                if(_movable) {
                    x1 = x2 = Math.min(x, _start.x);
                    if(_magnetic) {
                        x1 = x2 = x;
                    }
                }

                y1 = Math.min(y1, _lastMY - _broomMargin / 2);
                y2 = Math.max(y2, _lastMY + _broomMargin / 2);
                break;
        }

        if(_movable) {
            Vector nonMovingEdges = new Vector();
            Vector movingEdges = new Vector();
            for(i = 0; i < _nTouched; i++) {
                Fig f = _touched[i];
                int newX = x;
                int newY = y;
                int figX = f.getX();
                int figY = f.getY();
                int dx = 0;
                int dy = 0;
                switch(_dir) {

                    case DIRECTION_UPWARD:
                        if(!_magnetic) {
                            newY = Math.min(y, _origY[i] + _offY[i]);
                        }

                        dy = Math.max(-figY, newY - figY - _offY[i]);
                        break;

                    case DIRECTION_DOWNWARD:
                        if(!_magnetic) {
                            newY = Math.max(y, _origY[i] + _offY[i]);
                        }

                        dy = newY - figY - _offY[i];
                        break;

                    case DIRECTION_RIGHTWARD:
                        if(!_magnetic) {
                            newX = Math.max(x, _origX[i] + _offX[i]);
                        }

                        dx = newX - figX - _offX[i];
                        break;

                    case DIRECTION_LEFTWARD:
                        if(!_magnetic) {
                            newX = Math.min(x, _origX[i] + _offX[i]);
                        }

                        dx = Math.max(-figX, newX - figX - _offX[i]);
                        break;
                }

                if (f instanceof FigNode) {
                    FigNode fn = (FigNode)f;
                    fn.superTranslate(dx, dy);
                    Collection figEdges = fn.getFigEdges(null);
                    Iterator it = figEdges.iterator();
                    while(it.hasNext()) {
                        FigEdge fe = (FigEdge)it.next();
                        if(nonMovingEdges.contains(fe) && !movingEdges.contains(fe)) {
                            movingEdges.addElement(fe);
                            fe.translateEdge(dx, dy);
                        }
                        else {
                            nonMovingEdges.addElement(fe);
                        }
                    }
                } else {
                    f.translate(dx, dy);
                }
                
                if ((dx > 0 || dy > 0) && !(f instanceof FigEdge)) {
                    MutableGraphSupport.setSaveEnabled(true);
                }

                f.endTrans();
            }

            for(i = 0; i < _nTouched; i++) {
                Fig f = _touched[i];
                if(f instanceof FigNode) {
                    ((FigNode)f).updateEdges();
                }
            }
        }

        _selectRect.setBounds(x1 - 4, y1 - 4, x2 - x1 + 8, y2 - y1 + 8);
        editor.damaged(_selectRect);
        _hint = null;
        touching();
    }

    /** On mouse up, select or toggle the selection of items under the
     *  mouse or in the selection rectangle. */
    public void mouseReleased(MouseEvent me) {
        if(me.isConsumed()) {
            return;
        }

        _selectRect.setBounds(x1 - 1, y1 - 1, x2 - x1 + 2, y2 - y1 + 20);
        _bigDamageRect.setLocation(x1 - 200, y1 - 200);
        editor.damaged(_bigDamageRect);
        editor.damaged(_selectRect);
        if(_LastTouched.size() > 0) {
            editor.getSelectionManager().select(_LastTouched);
        }

        _draw = false;
        done();
        me.consume();
        _hint = null;
    }

    public void addNewItems() {
        if(_nTouched >= MAX_TOUCHED_FIGS) {
            return;
        }

        int i;
        _addRect.setBounds(_lastX1, _lastY1, _lastX2 - _lastX1, _lastY2 - _lastY1);
        _addRect.add(_selectRect);
        Enumeration figs = editor.figs();
        iterateFigs: 
        while(figs.hasMoreElements()) {
            Fig f = (Fig)figs.nextElement();
            Rectangle figBounds = f.getBounds();
            if(_addRect.intersects(figBounds)) {
                if(_dir == DIRECTION_LEFTWARD && figBounds.x + figBounds.width > _addRect.x + _addRect.width || _dir == DIRECTION_RIGHTWARD && figBounds.x < _addRect.x || _dir == DIRECTION_UPWARD && figBounds.y + figBounds.height > _addRect.y + _addRect.height || _dir == DIRECTION_DOWNWARD && figBounds.y < _addRect.y) {
                    continue iterateFigs;    // ####################
                }

                for(i = 0; i < _nTouched; i++) {
                    Fig ft = _touched[i];
                    if(ft == f) {
                        continue iterateFigs;    // ####################
                    }
                }

                _touched[_nTouched] = f;
                _origX[_nTouched] = f.getX();
                _origY[_nTouched] = f.getY();
                _offX[_nTouched] = (_dir == DIRECTION_LEFTWARD) ? f.getWidth() : 0;
                _offY[_nTouched] = (_dir == DIRECTION_UPWARD) ? f.getHeight() : 0;
                _nTouched++;
                _origBBox = null;
            }
            // use different points depending on _dir
        }
    }

    public void keyPressed(KeyEvent ke) {
        super.keyPressed(ke);
        if(ke.isConsumed()) {
            return;
        }

        if(KeyEvent.VK_ENTER == ke.getKeyCode() || KeyEvent.VK_TAB == ke.getKeyCode()) {
            _magnetic = !_magnetic;
        }
        else if(KeyEvent.VK_SPACE == ke.getKeyCode()) {
            doDistibute(false, ke.isShiftDown());
            ke.consume();
        }
        else {
            //System.out.println("key code is " + ke.getKeyCode());
            return;
        }

        _bigDamageRect.setLocation(x1 - 200, y1 - 200);
        editor.damaged(_bigDamageRect);
        editor.damaged(_selectRect);
    }

    ////////////////////////////////////////////////////////////////
    // actions
    public void doDistibute(boolean alignToGrid, boolean doCentering) {
        _movable = false;
        Vector figs = _LastTouched;
        if(figs == null) {
            figs = touching();
        }

        int request = 0;
        int size = figs.size();
        if(_distributeMode == DISTRIBUTE_EVEN_SPACE || _distributeMode == DISTRIBUTE_SPREAD) {
            request = CmdDistribute.V_SPACING;
            if(_dir == DIRECTION_UPWARD || _dir == DIRECTION_DOWNWARD) {
                request = CmdDistribute.H_SPACING;
            }
        }
        else if(_distributeMode == DISTRIBUTE_PACK) {
            request = CmdDistribute.V_PACK;
            if(_dir == DIRECTION_UPWARD || _dir == DIRECTION_DOWNWARD) {
                request = CmdDistribute.H_PACK;
            }
        }

        //     if (_distributeMode == DISTRIBUTE_EVEN_SPACE && _origBBox == null) {
        //       for (int i = 0; i < size; i++) {
        //    Fig f = (Fig) figs.elementAt(i);
        //    _origLocation[i] = f.getLocation();
        //       }
        //     }
        if(_distributeMode == DISTRIBUTE_ORIG) {
            for(int i = 0; i < size; i++) {
                Fig f = (Fig)figs.elementAt(i);
                if(_dir == DIRECTION_UPWARD || _dir == DIRECTION_DOWNWARD) {
                    f.setLocation(_origX[i], f.getY());
                }
                else {
                    f.setLocation(f.getX(), _origY[i]);
                }
            }
        }
        else {
            CmdDistribute d = new CmdDistribute(request);
            d.setArg("figs", figs);
            if(_distributeMode == DISTRIBUTE_SPREAD) {
                d.setArg("bbox", _selectRect);
            }
            else if(_distributeMode == DISTRIBUTE_EVEN_SPACE && _origBBox != null) {
                d.setArg("bbox", _origBBox);
            }

            d.doIt();
            if(doCentering) {
                int centerRequest = CmdAlign.ALIGN_H_CENTERS;
                if(_dir == DIRECTION_UPWARD || _dir == DIRECTION_DOWNWARD) {
                    centerRequest = CmdAlign.ALIGN_V_CENTERS;
                }

                CmdAlign a = new CmdAlign(centerRequest);
                a.setArg("figs", figs);
                a.doIt();
            }

            if(alignToGrid) {
                CmdAlign a = new CmdAlign(CmdAlign.ALIGN_TO_GRID);
                a.setArg("figs", figs);
                a.doIt();
            }

            if(_distributeMode == DISTRIBUTE_EVEN_SPACE && _origBBox == null) {
                _origBBox = d.getLastBBox();
            }
        }

        if(_distributeMode == DISTRIBUTE_EVEN_SPACE) {
            _hint = "Space evenly";
        }
        else if(_distributeMode == DISTRIBUTE_PACK) {
            _hint = "Pack tightly";
        }
        else if(_distributeMode == DISTRIBUTE_SPREAD) {
            _hint = "Spread out";
        }
        else if(_distributeMode == DISTRIBUTE_ORIG) {
            _hint = "Original";
        }
        else {
            _hint = "(internal prog error)";
        }

        if(doCentering) {
            _hint += " + center";
        }

        if(alignToGrid) {
            _hint += " + snap";
        }

        _distributeMode = (_distributeMode + 1) % 4;
    }

    public Vector touching() {
        Vector figs = new Vector(_nTouched);
        for(int i = 0; i < _nTouched; i++) {
            if(_touched[i].getBounds().intersects(_selectRect)) {
                if(!(_touched[i] instanceof FigEdge)) {
                    figs.addElement(_touched[i]);
                }
            }
        }

        _LastTouched = figs;
        return figs;
    }

    ////////////////////////////////////////////////////////////////
    // user feedback methods

    /** Reply a string of instructions that should be shown in the
     *  statusbar when this mode starts. */
    public String instructions() {
        return "Push objects around. Return toggles pulling. Space key distributes.";
    }

    ////////////////////////////////////////////////////////////////
    // painting methods

    /** Paint this mode by painting the selection rectangle if appropriate. */
    public void paint(Graphics g) {
        if (!_draw) {
            return;
        }

//        Graphics g = (Graphics)graphicsContext;
        Color selectRectColor = Globals.getPrefs().getRubberbandColor();
        if(_magnetic) {
            g.setColor(Color.red);
        }
        else {
            g.setColor(selectRectColor);
        }

        if(_hint != null) {
            g.setFont(HINT_FONT);
        }

        int bm = _broomMargin / 2;
        switch(_dir) {

            case DIRECTION_UNDEFINED:
                g.fillRect(x1 - 10, (y1 + y2) / 2 - 2, 20, 4);
                g.fillRect((x1 + x2) / 2 - 2, y1 - 10, 4, 20);
                break;

            case DIRECTION_UPWARD:
                g.fillRect(x1, y1, x2 - x1, y2 - y1 + 4);
                g.drawLine(_lastMX - bm, y2 + 4, _lastMX - bm, y2 + 8);
                g.drawLine(_lastMX + bm - 1, y2 + 4, _lastMX + bm - 1, y2 + 8);
                if(_movable) {
                    g.fillRect((x1 + x2) / 2 - 2, y1, 4, 14);
                }

                if(_hint != null) {
                    g.drawString(_hint, (x1 + x2) / 2 + 5, y1 + 15);
                }

                break;

            case DIRECTION_DOWNWARD:
                g.fillRect(x1, y1 - 4, x2 - x1, y2 - y1 + 4);
                if(_movable) {
                    g.fillRect((x1 + x2) / 2 - 2, y1 - 14, 4, 14);
                }

                g.drawLine(_lastMX - bm, y1 - 4, _lastMX - bm, y1 - 8);
                g.drawLine(_lastMX + bm - 1, y1 - 4, _lastMX + bm - 1, y1 - 8);
                if(_hint != null) {
                    g.drawString(_hint, (x1 + x2) / 2 + 5, y1 - 8);
                }

                break;

            case DIRECTION_RIGHTWARD:
                g.fillRect(x1 - 4, y1, x2 - x1 + 4, y2 - y1);
                g.drawLine(x1 - 4, _lastMY - bm, x1 - 8, _lastMY - bm);
                g.drawLine(x1 - 4, _lastMY + bm - 1, x1 - 8, _lastMY + bm - 1);
                if(_movable) {
                    g.fillRect(x1 - 14, (y1 + y2) / 2 - 2, 14, 4);
                }

                if(_hint != null) {
                    g.drawString(_hint, x1 - 70, (y1 + y2) / 2 - 10);
                }

                break;

            case DIRECTION_LEFTWARD:
                g.fillRect(x1, y1, x2 - x1 + 4, y2 - y1);
                g.drawLine(x2 + 4, _lastMY - bm, x2 + 8, _lastMY - bm);
                g.drawLine(x2 + 4, _lastMY + bm - 1, x2 + 8, _lastMY + bm - 1);
                if(_movable) {
                    g.fillRect(x1, (y1 + y2) / 2 - 2, 14, 4);
                }

                if(_hint != null) {
                    g.drawString(_hint, x2 + 5, (y1 + y2) / 2 - 10);
                }

                break;
        }
    }
}    /* end class ModeBroom */