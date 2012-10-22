package com.sg.object.graph;

import java.io.Serializable;
import java.util.List;

import com.sg.logic.strategy.LineStrategy;
import com.sg.object.Point;
import com.sg.object.unit.GUnit;
import com.sg.object.unit.LineUnit;
import com.sg.object.unit.PointUnit;
import com.sg.property.tools.Painter;

import android.graphics.Canvas;

public class LineGraph extends Graph implements Serializable {
	
//	private PointUnit beginUnit;
//	private PointUnit endUnit;
	
	/*
	 * 线图是由两个点元以及他们的约束关系构成的(直线约束关系)
	 * */
	public LineGraph(List<Point> pList) {
		PointUnit beginUnit = new PointUnit(pList.get(0));
		PointUnit endUnit = new PointUnit(pList.get(pList.size() - 1));
		LineUnit lineUnit = new LineUnit(beginUnit, endUnit);
		graph.add(beginUnit);
		graph.add(lineUnit);
		graph.add(endUnit);
		
		translationStratery = new LineStrategy();  //选择线变换策略
	}

	public LineGraph() {
		translationStratery = new LineStrategy();  //选择线变换策略
	}
	
	//构造折线图形
	public void buildLineGraph(List<GUnit> units){
		for(GUnit unit : units) {
			buildGraph(unit);
		} 
	}
	/*
	@Override
	public void draw(Canvas canvas, Painter painter) {
		
		//for(GUnit unit : graph) {
		//	unit.draw(canvas, painter);
		//}
		
		int size = graph.size();
		for(int num = 0; num < size; num++) {
			GUnit unit = graph.get(num);
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
		for(GUnit unit : graph){
			if(unit.isInUnit(point)){
				return true;
			}
		}
		return false;
	}
	*/

//	public PointUnit getBeginUnit() {
//		return beginUnit;
//	}
//
//	public void setBeginUnit(PointUnit beginUnit) {
//		this.beginUnit = beginUnit;
//	}
//
//	public PointUnit getEndUnit() {
//		return endUnit;
//	}
//
//	public void setEndUnit(PointUnit endUnit) {
//		this.endUnit = endUnit;
//	}
	


}
