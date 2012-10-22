package com.sg.object.graph;

import java.io.Serializable;

import com.sg.logic.strategy.LineStrategy;
import com.sg.object.Point;
import com.sg.object.unit.GUnit;
import com.sg.property.tools.Painter;

import android.graphics.Canvas;

public class PointGraph extends Graph implements Serializable{

	public PointGraph() {
		translationStratery = new LineStrategy();  //选择线变换策略
	}
	
	/*
	@Override
	public void draw(Canvas canvas, Painter painter) {
		for(GUnit unit : graph) {
			unit.draw(canvas, painter);
		}
	}
	*/

	@Override
	public void move(float mx, float my) {
		// TODO Auto-generated method stub
		
	}

	/*
	@Override
	public boolean isInGraph(Point point) {
		// TODO Auto-generated method stub
		return graph.get(0).isInUnit(point);
	}
	*/

}
