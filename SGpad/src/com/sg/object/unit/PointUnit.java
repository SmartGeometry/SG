package com.sg.object.unit;

import java.util.List;

import com.sg.logic.common.CommonFunc;
import com.sg.logic.strategy.LineStrategy;
import com.sg.logic.strategy.TranslationStratery;
import com.sg.object.Point;
import com.sg.object.graph.Graph;
import com.sg.property.common.ThresholdProperty;
import com.sg.property.tools.Painter;

import android.graphics.Canvas;

public class PointUnit extends GUnit {
	
//	private Point point;
	private float x;
	private float y;
	private boolean isInLine; //点是否在直线上
	private boolean isCommonConstrainted; //是否是一般约束点
	private int mark;
	
	/*
	 * 约束度，比如一根直线的点元约束度是1
     * 三角形顶点点元的约束度是2
     * 于判断闭合图形
     * 如果所有点元的使用度==2，且点元数量等于线元数量，可判断该图形是闭合多边形
	 * */
	private int degree;
	
	private int id = 0;
	private static int ID = 0;
	
	private boolean isRightAngle;  //是否是直角点
	private boolean isEqualAngle;  //是否是与其他角相等的点
	
	public PointUnit() {
		ID++;
		id = ID;
		this.x = 0;
		this.y = 0;
		isRightAngle = false;
		isEqualAngle = false;
		isInLine = false;
		isCommonConstrainted = false;
	}
	
	public PointUnit(float x, float y) {
		ID++;
		id = ID;
		this.x = x;
		this.y = y;
		isRightAngle = false;
		isEqualAngle = false;
		isInLine = false;
		isCommonConstrainted = false;
	}
	
	public PointUnit(Point point) {
		ID++;
		id = ID;
		this.x = point.getX();
		this.y = point.getY();
		isRightAngle = false;
		isEqualAngle = false;
		isInLine = false;
		isCommonConstrainted = false;
	}
	
	public PointUnit(List<Point> pList) {
		ID++;
		id = ID;
		int n = pList.size();
		this.x = (pList.get(0).getX() + pList.get(n-1).getX()) / 2;
		this.y = (pList.get(0).getY() + pList.get(n-1).getY()) / 2;
		isRightAngle = false;
		isEqualAngle = false;
		isInLine = false;
		isCommonConstrainted = false;
	}

	@Override
	public void draw(Canvas canvas, Painter painter) {
		canvas.drawCircle(this.x, this.y, painter.getWidth()/2, painter.getPaint());
	}

	/*
	 * 既当输入点少于10个，且收尾的距离小于12时为点图元
	 * */
	@Override
	public boolean judge(List<Point> pList) {
		/*
		int n = pList.size();
		if(n == 0) {
			return false;
		}
		
		double distance = CommonFunc.distance(pList.get(0), pList.get(n-1));
		if(n < ThresholdProperty.POINT_COUNT && 
				distance < ThresholdProperty.POINT_DISTANCE) {
			return true;
		}
		return false;
		*/
		//2012-9-23  cai  原方法识别有问题
		int n = pList.size();
		Point cPoint = pList.get(n/2);
		
		int count = 0;     //在圆外的点数量
		for(Point point : pList) {
			if(CommonFunc.distance(cPoint, point) > ThresholdProperty.POINT_DISTANCE) {
				count++;
			}
		}
		
		return (5*count > n) ? false : true;   //圆外的点超过五分之一则不是点
	}
	
	public Point getPoint() {
		return new Point(x, y);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}
	
	public void increaseDegree() {
		this.degree++;
	}
	
	public void decreaseDegree() {
		this.degree--;
	}
	
	public void clearDegree() {
		this.degree = 0;
	}
	
	public long getID(){
		return id;
	}
	
	@Override
	public GUnit clone(){
		GUnit temp = null;  
        temp = (PointUnit) super.clone();  
        return temp; 
	}

	@Override
	public boolean isInUnit(Point point) {
		// TODO Auto-generated method stub
		double curDistance = CommonFunc.distance(this.getPoint(), point);
		if(curDistance < ThresholdProperty.GRAPH_CHECKED_DISTANCE){
			return true;
		}else{
			return false;
		}
	}

	public boolean isRightAngle() {
		return isRightAngle;
	}

	public void setRightAngle(boolean isRightAngle) {
		this.isRightAngle = isRightAngle;
	}

	public boolean isEqualAngle() {
		return isEqualAngle;
	}

	public void setEqualAngle(boolean isEqualAngle) {
		this.isEqualAngle = isEqualAngle;
	}

	public boolean isInLine() {
		return isInLine;
	}

	public void setInLine(boolean isInLine) {
		this.isInLine = isInLine;
	}

	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}

	public boolean isCommonConstrainted() {
		return isCommonConstrainted;
	}

	public void setCommonConstrainted(boolean isCommonConstrainted) {
		this.isCommonConstrainted = isCommonConstrainted;
	}

	@Override
	public void translate(float[][] transMatrix) {
		// TODO Auto-generated method stub
		x += transMatrix[0][2];
		y += transMatrix[1][2];
	}

	@Override
	public void scale(float[][] scaleMatrix, Point translationCenter) {
		// TODO Auto-generated method stub
		float x0 = translationCenter.getX(), y0 = translationCenter.getY(); 
		x = (x - x0) * scaleMatrix[0][0] + x0;
		y = (y - y0) * scaleMatrix[1][1] + y0;
	}

	@Override
	public void rotate(float[][] rotateMatrix, Point translationCenter) {
		// TODO Auto-generated method stub
		float x0 = translationCenter.getX(), y0 = translationCenter.getY();
		float tempX = x - x0;
		float tempY = y - y0;
		x = (tempX * rotateMatrix[0][0] + tempY * rotateMatrix[0][1]) + x0;
		y = (tempX * rotateMatrix[1][0] + tempY * rotateMatrix[1][1]) + y0;
	} 
	
	
}
