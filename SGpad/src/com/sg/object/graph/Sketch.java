package com.sg.object.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sg.object.Point;
import com.sg.object.unit.GUnit;
import com.sg.property.common.GType;
import com.sg.property.tools.Painter;

import android.graphics.Canvas;

public class Sketch extends Graph implements Serializable{
	
	private List<Point> pList;
	
	public Sketch(List<Point> pList) {
		this.pList = pList;
	}
	
	public Sketch(float x, float y) {
		pList = new ArrayList<Point>();
		pList.add(new Point(x, y));
	}

	
	@Override
	public void draw(Canvas canvas, Painter painter) {
		int n = pList.size();
		for(int i = 0; i < n-1; i++) {
			canvas.drawLine(pList.get(i).getX(), pList.get(i).getY(), pList.get(i+1).getX(), pList.get(i+1).getY(), painter.getPaint());
		}
	}
	

	@Override
	public void move(float mx, float my) {
		// TODO Auto-generated method stub
		pList.add(new Point(mx, my));
	}

	@Override
	public boolean isInGraph(Point point) {
		// TODO Auto-generated method stub
		return false;
	}

}
