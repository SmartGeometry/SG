package com.sg.property.tools;

import android.graphics.Paint;

public class Painter {
	
	private Paint paint;
	private int color;
	private int width;
	
	public Painter(int color, int width) {
		this.paint = new Paint();
		this.color = color;
		this.width = width;
		initPainter();
	}
	
	
	public Painter(Paint paint) {
		this.paint = paint;
	}
	
	private void initPainter() {
		paint.setAntiAlias(true);
		paint.setColor(color);            
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(width);
	}
	
	public void setColor(int color) {
		this.color = color;
		paint.setColor(color);
	}
	
	public void setWidth(int width) {
		this.width = width;
		paint.setStrokeWidth(width);
	}
	
	public int getWidth() {
		return width;
	}
	
	public void setPaint(Paint paint) {
		this.paint = paint;
	}
	
	public Paint getPaint() {
		return paint;
	}

}
