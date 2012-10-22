/*
 * 收集笔信息，采用单例模式
 * */

package com.sg.transformation.collection;

import java.util.ArrayList;
import java.util.List;

import com.sg.object.Point;

public class PenInfoCollector {
	
	 private List<Point> penInfoList;
	
	 private static PenInfoCollector instance = new PenInfoCollector();
	 
	 private PenInfoCollector() {
		 penInfoList = new ArrayList<Point>();
	 }
	 
	 public static PenInfoCollector getInstance() {
		 return instance;
	 }
	 
	 public void start() {
		 if(penInfoList.size() != 0) {
			 penInfoList.clear();
		 }
	 }
	 
	 public void collect(Point point) {
		 penInfoList.add(point);
	 }
	 
	 public void collect(int x, int y) {
		 penInfoList.add(new Point(x, y));
	 }
	 
	 public void release() {
		 penInfoList.clear();
	 }

	public List<Point> getPenInfoList() {
		return penInfoList;
	}

}
