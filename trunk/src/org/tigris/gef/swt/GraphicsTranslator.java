package org.tigris.gef.swt;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;

public class GraphicsTranslator extends Graphics {
    
    private swingwt.awt.Graphics swtGraphics;
    
    GraphicsTranslator(swingwt.awt.Graphics swtGraphics) {
	this.swtGraphics = swtGraphics;
    }

    @Override
    public void clearRect(int arg0, int arg1, int arg2, int arg3) {
	swtGraphics.clearRect(arg0, arg1, arg2, arg3);
    }

    @Override
    public void clipRect(int arg0, int arg1, int arg2, int arg3) {
	swtGraphics.clearRect(arg0, arg1, arg2, arg3);
    }

    @Override
    public void copyArea(int arg0, int arg1, int arg2, int arg3, int arg4,
	    int arg5) {
	swtGraphics.copyArea(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public Graphics create() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void dispose() {
	swtGraphics.dispose();
    }

    @Override
    public void drawArc(int arg0, int arg1, int arg2, int arg3, int arg4,
	    int arg5) {
	swtGraphics.drawArc(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public boolean drawImage(Image arg0, int arg1, int arg2, ImageObserver arg3) {
	return false;
    }

    @Override
    public boolean drawImage(Image arg0, int arg1, int arg2, Color arg3,
	    ImageObserver arg4) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean drawImage(Image arg0, int arg1, int arg2, int arg3,
	    int arg4, ImageObserver arg5) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean drawImage(Image arg0, int arg1, int arg2, int arg3,
	    int arg4, Color arg5, ImageObserver arg6) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean drawImage(Image arg0, int arg1, int arg2, int arg3,
	    int arg4, int arg5, int arg6, int arg7, int arg8, ImageObserver arg9) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean drawImage(Image arg0, int arg1, int arg2, int arg3,
	    int arg4, int arg5, int arg6, int arg7, int arg8, Color arg9,
	    ImageObserver arg10) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void drawLine(int arg0, int arg1, int arg2, int arg3) {
	swtGraphics.drawLine(arg0, arg1, arg2, arg3);
    }

    @Override
    public void drawOval(int arg0, int arg1, int arg2, int arg3) {
	swtGraphics.drawOval(arg0, arg1, arg2, arg3);
    }

    @Override
    public void drawPolygon(int[] arg0, int[] arg1, int arg2) {
	swtGraphics.drawPolygon(arg0, arg1, arg2);
    }

    @Override
    public void drawPolyline(int[] arg0, int[] arg1, int arg2) {
	swtGraphics.drawPolyline(arg0, arg1, arg2);
    }

    @Override
    public void drawRoundRect(int arg0, int arg1, int arg2, int arg3, int arg4,
	    int arg5) {
	swtGraphics.drawRoundRect(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public void drawString(String arg0, int arg1, int arg2) {
	swtGraphics.drawString(arg0, arg1, arg2);
    }

    @Override
    public void drawString(AttributedCharacterIterator arg0, int arg1, int arg2) {
	swtGraphics.drawString(arg0.toString(), arg1, arg2);
    }

    @Override
    public void fillArc(int arg0, int arg1, int arg2, int arg3, int arg4,
	    int arg5) {
	swtGraphics.fillArc(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public void fillOval(int arg0, int arg1, int arg2, int arg3) {
	swtGraphics.fillOval(arg0, arg1, arg2, arg3);
    }

    @Override
    public void fillPolygon(int[] arg0, int[] arg1, int arg2) {
	swtGraphics.fillPolygon(arg0, arg1, arg2);
    }

    @Override
    public void fillRect(int arg0, int arg1, int arg2, int arg3) {
	swtGraphics.fillRect(arg0, arg1, arg2, arg3);
    }

    @Override
    public void fillRoundRect(int arg0, int arg1, int arg2, int arg3, int arg4,
	    int arg5) {
	swtGraphics.fillRoundRect(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public Shape getClip() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Rectangle getClipBounds() {
	return SwingUtil.translateRectangle(swtGraphics.getClipBounds());
    }

    @Override
    public Color getColor() {
	return SwingUtil.translateColor(swtGraphics.getColor());
    }

    @Override
    public Font getFont() {
	return new Font(swtGraphics.getFont().getAttributes());
    }

    @Override
    public FontMetrics getFontMetrics(Font arg0) {
	swtGraphics.getFontMetrics(new swingwt.awt.Font(arg0.getAttributes()));
	return null;
    }

    @Override
    public void setClip(Shape arg0) {
	// TODO Auto-generated method stub

    }

    @Override
    public void setClip(int arg0, int arg1, int arg2, int arg3) {
	swtGraphics.setClip(arg0, arg1, arg2, arg3);
    }

    @Override
    public void setColor(Color arg0) {
	swtGraphics.setColor(SwtUtil.translateColor(arg0));
    }

    @Override
    public void setFont(Font arg0) {
	swtGraphics.setFont(new swingwt.awt.Font(arg0.getAttributes()));
    }

    @Override
    public void setPaintMode() {
	swtGraphics.setPaintMode();
    }

    @Override
    public void setXORMode(Color arg0) {
	swtGraphics.setXORMode(SwtUtil.translateColor(arg0));
    }

    @Override
    public void translate(int arg0, int arg1) {
	swtGraphics.translate(arg0, arg1);
    }

}
