/*
 * 一些重用的函数
 * */
package com.sg.logic.common;

import android.util.Log;

import com.sg.object.Point;
import com.sg.object.unit.PointUnit;

public class CommonFunc {
	
	private static int DriverWidth;
	private static int DriverHeight;
	public static void setDriverBound(int w, int h) {
		DriverWidth = w;
		DriverHeight = h;
	}
	public static int getDriverWidth() {
		return DriverWidth;
	}
	public static int getDriverHeight() {
		return DriverHeight;
	}
	
	public static double distance(Point info1, Point info2) {
		float X = info1.getX() - info2.getX();
		float Y = info1.getY() - info2.getY();
		return Math.sqrt( X*X + Y*Y );
	}
	
	public static double distance(PointUnit pointUnit1, PointUnit pointUnit2) {
		return distance(pointUnit1.getPoint(), pointUnit2.getPoint());
	}
	
	public static double lineDistance(Point startpoint,Point endpoint,Point curPoint){
		double x1,x2,y1,y2,curX,curY;
		Log.v("yeah","no");
		x1=(double)startpoint.getX();
		x2=(double)endpoint.getX();
		y1=(double)startpoint.getY();
		y2=(double)endpoint.getY();
		curX=(double)curPoint.getX();
		curY=(double)curPoint.getY();
		double distance;
		distance=Math.abs((y1-y2)/(x1-x2)*curX-curY-(y1-y2)/(x1-x2)*x1+y1)/Math.sqrt(((y1-y2)*(y1-y2))/((x1-x2)*(x1-x2))+1);
		Log.v("k",(x1-x2)/(y1-y2)+"");
		return distance;
	}
	
	public static double min(double[] array) {
		double min = array[0];
		for(int i = 1; i < array.length; i++) {
			if(array[i] < min) {
				min = array[i];
			}
		}
		return min;
	}
	
	public static double square( double element){
		return element*element;
	}
	
	//判断放大缩小
	public static boolean NarrowOrEnlarge(Point startpoint1,Point startpoint2,Point endpoint1,Point endpoint2){
		double distance1 = distance(startpoint1,startpoint2);
		double distance2 = distance(endpoint1,endpoint2);
		if(distance1>distance2)
			return true;
		else
			return false;
	}
	
	//求旋转两向量夹角
	public static double rotatecos(Point baseVector, Point vector1, Point vector2){
		Point changeVector1 = new Point(baseVector.getX()+vector1.getX(),baseVector.getY()+vector1.getY());
		Point changeVector2 = new Point(baseVector.getX()+vector2.getX(),baseVector.getY()+vector2.getY());
		double cosA = VectorFunc.direction(changeVector1, changeVector2);
		//double cosA = (changeVector1.getX()*changeVector2.getX()+changeVector1.getY()*changeVector2.getY())/(distance(changeVector1,new Point())*distance(changeVector2,new Point()));
		return cosA;
	}
	
	//判断顺时针 逆时针
	public static boolean isClockWise(Point baseVector, Point vector1, Point vector2){
		Point changeVector1 = new Point(baseVector.getX()+vector1.getX(),baseVector.getY()+vector1.getY());
		Point changeVector2 = new Point(baseVector.getX()+vector2.getX(),baseVector.getY()+vector2.getY());
		Log.v("vector1,vector2",changeVector1.getX()+","+changeVector1.getY()+"  "+changeVector2.getX()+","+changeVector2.getY());
		double z = changeVector1.getY() * changeVector2.getX() - changeVector1.getX() * changeVector2.getY() ;
		Log.v("z",z+"");
		if( z > 0)
			return true;
		else
			return false;
/*
		double k1 = ( 1.0 * changeVector1.getY() ) / ( 1.0 * changeVector1.getX() );
		double k2 = ( 1.0 * changeVector2.getY() ) / ( 1.0 * changeVector2.getX() );
		double tanA = ( k1 - k2 ) / ( 1 - k1 * k2 );
		if( tanA < 0 ){
			return true;
		}else{
			return false;
		}
*/
	}
	
	//求出要画标记的点
	public static Point markPoint(PointUnit other, PointUnit special, double length){
		//int length = 15;
		float x1 = other.getX();
		float y1 = other.getY();
		float x2 = special.getX();
		float y2 = special.getY();
		double n =  distance(other, special) / length; //要画的线占这条线的比例
		float x = (float) ((x1 - x2) / n + x2);
		float y = (float) ((y1 - y2) / n + y2);
		return new Point(x, y);
	}
	
	// 求3个点的曲率半径
		public static double CurvatureRadius(Point p1, Point p2, Point p3) {
			double a = distance(p1, p2);
			double b = distance(p1, p3);
			double c = distance(p2, p3);
			double cosA = (b*b + c*c - a*a) / (2*b*c);
			// 正弦定理 a/sinA = b/sinB = c/sinC = 2R
			return 0.5 * a / Math.sin(Math.acos(cosA));
		}

}
