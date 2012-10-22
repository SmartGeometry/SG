package com.sg.object.unit;

import java.io.Serializable;
import java.util.List;

import com.sg.logic.common.CommonFunc;
import com.sg.object.Point;
import com.sg.property.common.ThresholdProperty;
import com.sg.property.tools.Painter;

import android.graphics.Canvas;
import android.graphics.Color;

public class LineUnit extends GUnit implements Serializable{

//	private Point startPoint;
//	private Point endPoint;
	
	private PointUnit startPointUnit;
	private PointUnit endPointUnit;

	/*
	 * 约束直线储存结构
	 * Type用于识别约束直线的类型
	 * Type = 0.普通直线
	 *        1.垂线
	 *        2.中位线
	 *        3.角平分线
	 *        4.中线
	 *        5.约束线
	 *Ver用于记录约束线与三角形相交的顶点，数值有0,2,4；
	 *startPoint 约定储存顶点，endPoint约定储存约束线的脚（如垂足） 
	*/	
		
	private int Type = 0;
	private int Ver ;
	private double proportion;    //对于普通约束直线的约束比例
	
	public LineUnit() {

	}

//	public LineUnit(Point startPoint, Point endPoint) {
//		this.startPoint = startPoint;
//		this.endPoint = endPoint;
//	}
	
	public LineUnit(List<Point> pList) {
		this.startPointUnit = new PointUnit(pList.get(0));
		this.endPointUnit = new PointUnit(pList.get(pList.size() - 1));
		increaseDegree();
	}
	
	public LineUnit(PointUnit startPointUnit, PointUnit endPointUnit) {
		this.startPointUnit = startPointUnit;
		this.endPointUnit = endPointUnit;
		increaseDegree();
	}
	
	private void increaseDegree() {
		startPointUnit.increaseDegree();
		endPointUnit.increaseDegree();
	}
	
	public void decreaseDegree() {
		startPointUnit.decreaseDegree();
		endPointUnit.decreaseDegree();
	}
	
	/*
	 * 返回线元的长度
	 * */
	public double getLength() {
		return CommonFunc.distance(startPointUnit, endPointUnit);
	}

	@Override
	public void draw(Canvas canvas, Painter painter) {
		canvas.drawLine(startPointUnit.getPoint().getX(), startPointUnit.getPoint().getY(), 
				endPointUnit.getPoint().getX(), endPointUnit.getPoint().getY(), painter.getPaint());
		Painter specialPainter = new Painter(Color.BLUE, 5);
		switch(Type){
			case 2:{
				canvas.drawPoint(startPointUnit.getPoint().getX(), startPointUnit.getPoint().getY(), specialPainter.getPaint());
				canvas.drawPoint(endPointUnit.getPoint().getX(), endPointUnit.getPoint().getY(), specialPainter.getPaint());
				break;
			}
			case 4:{
				canvas.drawPoint(endPointUnit.getPoint().getX(), endPointUnit.getPoint().getY(), specialPainter.getPaint());
				break;
			}
			case 5:{
				canvas.drawPoint(endPointUnit.getPoint().getX(), endPointUnit.getPoint().getY(), specialPainter.getPaint());
			}
		}
		
	}

	@Override
	public boolean judge(List<Point> pList) {
/*		int n = pList.size();

		double averX = 0.0;
		double averY = 0.0;
		double lxx = 0.0;
		double lxy = 0.0;
		double lyy = 0.0;
		double p = 0.0;

		for (Point point : pList) {
			averX += point.getX();
			averY += point.getY();
		}
		averX /= n;
		averY /= n;

		for (Point point : pList) {
			lxx += (point.getX() - averX) * (point.getX() - averX);
			lyy += (point.getY() - averY) * (point.getY() - averY);
			lxy += (point.getX() - averX) * (point.getY() - averY);
		}

		p = lxy / Math.sqrt(lxx * lyy);
		p = Math.abs(p);
		if (p > ThresholdProperty.JUDGE_LINE_VALUE) {
			return true;
		}
		return false;*/

		int n = pList.size();
		
        //判断是否是直线图元的方法：若首末两点的距离比上所有的两两相邻的点之间的距离之和，比之大于阀值的话，则判断为直线图元
        //阀值暂定为0.95
		double totalLength = 0.0;
		double tmpLength = CommonFunc.distance(pList.get(0), pList.get(n-1));
		
		for(int i = 0; i < n-1; i++) {
			totalLength += CommonFunc.distance(pList.get(i), pList.get(i+1));
		}
		
		if(tmpLength / totalLength >= 0.95) {
			return true;
		}
		
		return false;
	
	}

	public PointUnit getStartPointUnit() {
		return startPointUnit;
	}

	public void setStartPointUnit(PointUnit startPointUnit) {
		this.startPointUnit = startPointUnit;
	}

	public PointUnit getEndPointUnit() {
		return endPointUnit;
	}

	public void setEndPointUnit(PointUnit endPointUnit) {
		this.endPointUnit = endPointUnit;
	}
	
	@Override
	public GUnit clone(){
		GUnit temp = null;  
        temp = (LineUnit) super.clone();
        ((LineUnit)temp).startPointUnit = (PointUnit)startPointUnit.clone();
        ((LineUnit)temp).endPointUnit = (PointUnit)endPointUnit.clone();  
        return temp; 
	}

	@Override
	public boolean isInUnit(Point point) {
		// TODO Auto-generated method stub
		double checkdistance1 = CommonFunc.distance(startPointUnit.getPoint(), point);
		double checkdistance2 = CommonFunc.distance(endPointUnit.getPoint(), point);
		double linedistance = CommonFunc.distance(startPointUnit.getPoint(), endPointUnit.getPoint());
		double curDistance = CommonFunc.lineDistance(startPointUnit.getPoint(), endPointUnit.getPoint(), point);
		if(checkdistance1 < linedistance && checkdistance2 < linedistance && curDistance < ThresholdProperty.GRAPH_CHECKED_DISTANCE){
			return true;
		}else{
			return false;
		}
	}

	public void setType(int i){
		Type = i;
	}
	
	public int getType(){
		return Type;
	}
	
	public void setVer(int i){
		Ver = i;
	}
	
	public int getVer(){
		return Ver;
	}
	
	public void setProportion(double temp){
		proportion = temp;
	}
	
	public double getProportion(){
		return proportion;
	}
	
}
