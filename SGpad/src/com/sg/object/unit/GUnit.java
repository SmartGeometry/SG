package com.sg.object.unit;

import java.io.Serializable;
import java.util.List;

import com.sg.object.Point;
import com.sg.object.graph.Graph;
import com.sg.property.tools.Painter;

import android.graphics.Canvas;

public abstract class GUnit implements Cloneable, Serializable{
	
	/*
	 * 图元类型
	 * 1点元
	 * 2直线元
	 * 3曲线元
	 * */
	protected int type; 
	/*
	 * 图元是否被选中
	 * */
	protected boolean checked;
	
	public GUnit(){
		checked = false;
	}

	/*
	 * 绘制图元
	 * */
	public abstract void draw(Canvas canvas, Painter painter);
	
	/*
	 * 判断图元
	 * */
	public abstract boolean judge(List<Point> pList);

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	public abstract boolean isInUnit(Point point);
	
	public abstract void translate(float[][] transMatrix);
	
	public abstract void scale(float[][] scaleMatrix, Point translationCenter);
	
	public abstract void rotate(float[][] rotateMatrix, Point translationCenter);
	
	public GUnit clone(){
		GUnit temp = null;  
        try {
			temp = (GUnit) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        return temp; 
	}

}
