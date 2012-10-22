package com.sg.object.unit;

import java.io.Serializable;
import java.util.List;

import com.sg.object.Point;
import com.sg.property.tools.Painter;

import android.graphics.Canvas;

public class CurveUnit extends GUnit implements Serializable{
	
	public CurveUnit() {
		
	}

	@Override
	public void draw(Canvas canvas, Painter painter) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean judge(List<Point> pList) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public GUnit clone(){
		GUnit temp = null;  
        temp = (CurveUnit) super.clone();  
        return temp; 
	}

	@Override
	public boolean isInUnit(Point point) {
		// TODO Auto-generated method stub
		return false;
	}

}
