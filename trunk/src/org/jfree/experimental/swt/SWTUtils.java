package org.jfree.experimental.swt;

import javax.swing.JPanel;

import org.eclipse.swt.SWT;

import org.eclipse.swt.graphics.Color;

import org.eclipse.swt.graphics.Device;

import org.eclipse.swt.graphics.Font;

import org.eclipse.swt.graphics.FontData;

import org.eclipse.swt.graphics.GC;

public class SWTUtils {

    private static final String Az = "ABCpqr";

    public static FontData toSwtFontData(Device device, java.awt.Font font,
	    boolean ensureSameSize) {

	FontData fontData = new FontData();

	fontData.setName(font.getFamily());

	int style = SWT.NORMAL;

	switch (font.getStyle()) {
	case java.awt.Font.PLAIN:
	    style |= SWT.NORMAL;

	    break;

	case java.awt.Font.BOLD:
	    style |= SWT.BOLD;

	    break;

	case java.awt.Font.ITALIC:
	    style |= SWT.ITALIC;

	    break;

	case (java.awt.Font.ITALIC + java.awt.Font.BOLD):
	    style |= SWT.ITALIC | SWT.BOLD;

	    break;

	}

	fontData.setStyle(style);

	int height = (int) Math
		.round(font.getSize() * 72.0 / device.getDPI().y);

	fontData.setHeight(height);

	if (ensureSameSize) {

	    GC tmpGC = new GC(device);

	    JPanel DUMMY_PANEL = new JPanel();

	    Font tmpFont = new Font(device, fontData);

	    tmpGC.setFont(tmpFont);

	    if (tmpGC.textExtent(Az).x > DUMMY_PANEL.getFontMetrics(font)
		    .stringWidth(Az)) {

		while (tmpGC.textExtent(Az).x > DUMMY_PANEL
			.getFontMetrics(font).stringWidth(Az)) {

		    tmpFont.dispose();

		    height--;

		    fontData.setHeight(height);

		    tmpFont = new Font(device, fontData);

		    tmpGC.setFont(tmpFont);

		}

	    } else if (tmpGC.textExtent(Az).x < DUMMY_PANEL
		    .getFontMetrics(font).stringWidth(Az)) {

		while (tmpGC.textExtent(Az).x < DUMMY_PANEL
			.getFontMetrics(font).stringWidth(Az)) {

		    tmpFont.dispose();

		    height++;

		    fontData.setHeight(height);

		    tmpFont = new Font(device, fontData);

		    tmpGC.setFont(tmpFont);

		}

	    }

	    tmpFont.dispose();

	    tmpGC.dispose();

	}

	return fontData;

    }

    public static java.awt.Font toAwtFont(Device device, FontData fontData,
	    boolean ensureSameSize) {

	int style;

	switch (fontData.getStyle()) {
	case SWT.NORMAL:
	    style = java.awt.Font.PLAIN;

	    break;

	case SWT.ITALIC:
	    style = java.awt.Font.ITALIC;

	    break;

	case SWT.BOLD:
	    style = java.awt.Font.BOLD;

	    break;

	default:
	    style = java.awt.Font.PLAIN;

	    break;

	}

	int height = (int) Math.round(fontData.height * device.getDPI().y
		/ 72.0);

	if (ensureSameSize) {

	    GC tmpGC = new GC(device);

	    Font tmpFont = new Font(device, fontData);

	    tmpGC.setFont(tmpFont);

	    JPanel DUMMY_PANEL = new JPanel();

	    java.awt.Font tmpAwtFont = new java.awt.Font(fontData.getName(),
		    style, height);

	    if (DUMMY_PANEL.getFontMetrics(tmpAwtFont).stringWidth(Az) > tmpGC
		    .textExtent(Az).x) {

		while (DUMMY_PANEL.getFontMetrics(tmpAwtFont).stringWidth(Az) > tmpGC
			.textExtent(Az).x) {

		    height--;

		    tmpAwtFont = new java.awt.Font(fontData.getName(), style,
			    height);

		}

	    } else if (DUMMY_PANEL.getFontMetrics(tmpAwtFont).stringWidth(Az) < tmpGC
		    .textExtent(Az).x) {

		while (DUMMY_PANEL.getFontMetrics(tmpAwtFont).stringWidth(Az) < tmpGC
			.textExtent(Az).x) {

		    height++;

		    tmpAwtFont = new java.awt.Font(fontData.getName(), style,
			    height);

		}

	    }

	    tmpFont.dispose();

	    tmpGC.dispose();

	}

	return new java.awt.Font(fontData.getName(), style, height);

    }

    public static java.awt.Font toAwtFont(Device device, Font font) {

	FontData fontData = font.getFontData()[0];

	return toAwtFont(device, fontData, true);

    }

    public static java.awt.Color toAwtColor(Color color) {

	return new java.awt.Color(color.getRed(), color.getGreen(), color
		.getBlue());

    }

    public static Color toSwtColor(Device device, java.awt.Paint paint) {

	java.awt.Color color;

	if (paint instanceof java.awt.Color) {

	    color = (java.awt.Color) paint;

	} else {

	    try {

		throw new Exception(
			"only color is supported at present... setting paint to uniform black color");

	    } catch (Exception e) {

		e.printStackTrace();

		color = new java.awt.Color(0, 0, 0);

	    }

	}

	return new org.eclipse.swt.graphics.Color(device, color.getRed(), color
		.getGreen(), color.getBlue());

    }

    public static Color toSwtColor(Device device, java.awt.Color color) {

	return new org.eclipse.swt.graphics.Color(device, color.getRed(), color
		.getGreen(), color.getBlue());

    }

}
