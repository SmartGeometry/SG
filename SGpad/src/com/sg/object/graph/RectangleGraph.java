package com.sg.object.graph;

import java.io.Serializable;
import java.util.List;

import com.sg.logic.strategy.LineStrategy;
import com.sg.object.Point;
import com.sg.object.unit.GUnit;
import com.sg.property.tools.Painter;

import android.graphics.Canvas;

public class RectangleGraph extends Graph implements Serializable{
	/*
	 * 4个点，4条约束关系
	 * */
	public RectangleGraph() {
		isClosed = true;
		translationStratery = new LineStrategy();  //选择线变换策略
	}
	
	public RectangleGraph(List<GUnit> units) {
		isClosed = true;
		translationStratery = new LineStrategy();  //选择线变换策略
		for(GUnit unit : units) {
			buildGraph(unit);
		} 
	}
	
	@Override
	public void draw(Canvas canvas, Painter painter) {
		// TODO Auto-generated method stub
		super.draw(canvas, painter);
	}
	

	@Override
	public void move(float mx, float my) {
		// TODO Auto-generated method stub
		
	}

	/*
	@Override
	public boolean isInGraph(Point point) {
		// TODO Auto-generated method stub
		return true;
	}
	*/

}
