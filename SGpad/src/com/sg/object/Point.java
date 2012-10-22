/*
 * 点类，区别于点图类，只是用来定义，不会绘制
 * */

package com.sg.object;

import java.io.Serializable;
import java.util.Date;

public class Point implements Serializable{

	private float x;             //x坐标
	private float y;			 //y坐标
	//private long timestamp;      //记录时间
	
	private double speed;        //速率
	private double direction;    //方向
	private double curvity;     //曲率
	
	private int total;          //特征点权重值，根据速率，方向，曲率来计算
	
	
	public Point() {
		this(0, 0);
	}
	
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
		//this.timestamp = new Date().getTime();
		
		this.speed = 0.0;
		this.direction = 0.0;
		this.curvity = 0.0;
		this.total = 0;
	}


	public float getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	//public long getTimestamp() {
		//return timestamp;
	//}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getDirection() {
		return direction;
	}

	public void setDirection(double direction) {
		this.direction = direction;
	}

	public double getCurvity() {
		return curvity;
	}

	public void setCurvity(double curvity) {
		this.curvity = curvity;
	}

	public int getTotal() {
		return total;
	}

	/*
	 * 自增+1
	 * */
	public void increaseTotal() {      
		this.total++;
	}
	
	/*
	 * 增指定值
	 * */
	public void increaseTotal(int increaseNum) {
		this.total += increaseNum;
	}

}
