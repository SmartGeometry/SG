/*
 * 笔信息类
 * 对笔信息去除噪声
 * 转换器的功能
 * */

package com.sg.transformation.collection;

import java.util.List;

import com.sg.logic.common.CommonFunc;
import com.sg.object.Point;
import com.sg.property.common.ThresholdProperty;

public class PenInfo {
	
	private List<Point> penInfos;
	
	public PenInfo(List<Point> penInfos) {
		this.penInfos = penInfos;
	}
	
	/*
	 * a[i] = 0.7*a[i] + 0.3*a[i+1] / a[i] = 0.7*a[i] + 0.3*a[i-1]
	 *高斯处理消噪
	 * */
	public List<Point> getNewPenInfo() {
		int n = penInfos.size();
		
		//从前到后处理一遍
		double x = 0.0f;
		double y = 0.0f;
		for(int i = 0; i < n-1; i++) {
			Point info = penInfos.get(i);
			x = 0.7 * info.getX() + 0.3 * penInfos.get(i+1).getX();
			y = 0.7 * info.getY() + 0.3 * penInfos.get(i+1).getY();
			info.setX((int)x);
			info.setY((int)y);
		}
		
		//从后到前处理一遍
		for(int i = n-1; i > 0; i--) {
			Point info = penInfos.get(i);
			x = 0.7 * info.getX() + 0.3 * penInfos.get(i-1).getX();
			y = 0.7 * info.getY() + 0.3 * penInfos.get(i-1).getY();
			info.setX((int)x);
			info.setY((int)y);
		}
		
		/*
		//去除首尾点
		if(n > 30) {
			int delSize = (int)(n * 0.03);
			for(int i = 0; i < delSize; i++) {   //去除首部
				penInfos.remove(0); 
			}
			
			n = penInfos.size();
			for(int i = n-1; i > n-1-delSize; i--) {
				penInfos.remove(i);               //去除尾部
			}
		}
		*/	
		return penInfos;
	}
	
	/*
	 * 因为手指触在屏幕是不停在晃动的，在这里进行处理
	 * 判断是否是不动点
	 * 以点列表的中间点为圆心，指定半径的圆。超过一定的点在里面则是不动点
	 * */
	public boolean isFixedPoint(List<Point> pList) {
		int n = pList.size();
		Point cPoint = pList.get(n/2);
		
		int count = 0;     //在圆外的点数量
		for(Point point : pList) {
			if(CommonFunc.distance(cPoint, point) > ThresholdProperty.POINT_DISTANCE) {
				count++;
			}
		}
		
		return (5*count > n) ? false : true;   //圆外的点超过五分之一即是动点
	}

}
