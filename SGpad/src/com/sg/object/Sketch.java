/*
package com.sg.object;

import java.io.Serializable;

import android.graphics.Canvas;
import android.graphics.Path;

import com.sg.object.graph.Graph;
import com.sg.property.tools.Painter;



public class Sketch extends Graph implements Serializable{

	private Path path;
	
	
	public Sketch(float x, float y) {
		path = new Path();
		path.moveTo(x, y);
	}
	
	public void move(float mx, float my) {
		path.lineTo(mx, my);
	}
	
	@Override
	public void draw(Canvas canvas, Painter painter) {
		canvas.drawPath(path, painter.getPaint());
	}

	@Override
	public boolean isInGraph(Point point) {
		// TODO Auto-generated method stub
		return false;
	}

}
*/