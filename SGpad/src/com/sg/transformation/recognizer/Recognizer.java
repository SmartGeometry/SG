package com.sg.transformation.recognizer;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.sg.logic.common.CommonFunc;
import com.sg.object.Point;
import com.sg.object.graph.Graph;
import com.sg.object.graph.LineGraph;
import com.sg.object.graph.PointGraph;
import com.sg.object.graph.RectangleGraph;
import com.sg.object.graph.TriangleGraph;
import com.sg.object.unit.CurveUnit;
import com.sg.object.unit.GUnit;
import com.sg.object.unit.LineUnit;
import com.sg.object.unit.PointUnit;
import com.sg.property.common.ThresholdProperty;
import com.sg.transformation.collection.Stroke;



public class Recognizer {
	
//	private List<Point> pList;
//	
//	public Recognizer(List<Point> pList) {
//		this.pList = pList;
//	}
//	
	private List<GUnit> unitList;
	private int num;
	private List<Integer> specialPointIndexs;
	
	public Recognizer() {
		unitList = new ArrayList<GUnit>();
	}
	
	public Graph recognize(List<Point> pList) {
		Graph graph = null;
		GUnit unit = null;
		Stroke stroke = new Stroke();
		specialPointIndexs = stroke.getSpecialPointIndex(pList);
		num = specialPointIndexs.size();
		Log.v("delete num", num + "");
		
		unit = new PointUnit(pList);   //先判断是不是点元
		if(unit.judge(pList)) {
			graph = new PointGraph();
			graph.buildGraph(unit);
			return graph;
		}
		
		unit = new LineUnit(pList);   //判断是不是线元
		if(unit.judge(pList)) {
			graph = new LineGraph(pList);
			return graph;
		}
		
		
		rebuildUnit(pList);
		if(isLineGraph(unitList)){
			graph = rebuileLineGraph(pList);
		}else{
			
		}
		return graph;
		/*
		boolean isTriangle = rebuildTriangle(pList);
		if(isTriangle) {
			triangleHolotactic(unitList);
			graph = new TriangleGraph(unitList);
			return graph;
		}
		
		boolean isRectangle = rebuileRecangle(pList);
		if(isRectangle){
			rectangleHolotactic(unitList);
			graph = new RectangleGraph(unitList);
			return graph;
		}
		*/
	}
	
	//识别出所有的图元，直线元，曲线元
	private void rebuildUnit(List<Point> pList) {
		unitList.clear();
		for(int i = 0; i < num-1; i++) {
			List<Point> tmpList = new ArrayList<Point>();
			for(int listIndex = specialPointIndexs.get(i); listIndex < specialPointIndexs.get(i+1); listIndex++) {
				tmpList.add(pList.get(listIndex));
			}
			
			unitList.add(recognizeGraphUnit(tmpList));
		}
	}
	
	private GUnit recognizeGraphUnit(List<Point> pList) {
		int n = pList.size();
		GUnit unit = null;
		
        //判断是否是直线图元的方法：若首末两点的距离比上所有的两两相邻的点之间的距离之和，比之大于阀值的话，则判断为直线图元
        //阀值暂定为0.95
		double totalLength = 0.0;
		double tmpLength = CommonFunc.distance(pList.get(0), pList.get(n-1));
		
		for(int i = 0; i < n-1; i++) {
			totalLength += CommonFunc.distance(pList.get(i), pList.get(i+1));
		}
		
		if(tmpLength / totalLength >= 0.95) {
			unit = new LineUnit(pList);
		} else {                 //若也不是直线图元，则判定为曲线图元
			unit = new CurveUnit();
		}
		
		return unit;
	}
	
	//是否与直线构成的图形
	private boolean isLineGraph(List<GUnit> unitList) {
		for(GUnit unit : unitList) {
			if(!(unit instanceof LineUnit)) {
				return false;
			}
		}
		return true;
	}
	
	//重构于直线构成的图形
	private Graph rebuileLineGraph(List<Point> pList) {
		Graph graph = null;

		//图元数量为特征点数-1
		int size = num -1;
		for(int n = 1; n < size; n++){
			LineUnit tmp1 = (LineUnit) unitList.get(n - 1);
			LineUnit tmp2 = (LineUnit) unitList.get(n);
			tmp2.setStartPointUnit(tmp1.getEndPointUnit());
			tmp2.getStartPointUnit().increaseDegree();
		}
		//添加点元，除了最后一个点
		for(int n = 0; n < size * 2; n += 2){
			PointUnit temp = ((LineUnit) unitList.get(n)).getStartPointUnit();
			unitList.add(n, temp);
		}
			
		double twoPointDistance = CommonFunc.distance(pList.get(specialPointIndexs.get(0)), pList.get(specialPointIndexs.get(num-1)));
		//如果首位相连
		if(twoPointDistance < ThresholdProperty.TWO_POINT_IS_CLOSED && size > 2) {
			LineUnit tmp1 = (LineUnit) unitList.get(1);  //第一个线元
			LineUnit tmp2 = (LineUnit) unitList.get(size * 2 - 1);  //最后一个线元
			tmp2.setEndPointUnit(tmp1.getStartPointUnit());
			tmp2.getEndPointUnit().increaseDegree();
			if(size == 3){
				//三角形
				graph = new TriangleGraph(unitList);
			}else{
				if(size == 4){
					//四边形
					graph = new RectangleGraph(unitList);
				}else{
					//多边形
					graph = new LineGraph();
					graph.setIsClosed(true);
					((LineGraph)graph).buildLineGraph(unitList);
				}
			}
		}else{
			//添加最后一个点元
			PointUnit temp = ((LineUnit) unitList.get(size * 2 - 1)).getEndPointUnit();
			unitList.add(size * 2, temp);
			graph = new LineGraph();
			((LineGraph)graph).buildLineGraph(unitList);
		}
		return graph;
	}
	
	//识别删除手势
	public static boolean isDeleteGesture(List<Point> pList){
		//List<GUnit> unitList = new ArrayList<GUnit>();
		List<Point> points = new ArrayList<Point>();
		List<Integer> specialPointIndexsForDelete;
		Stroke stroke = new Stroke();
		specialPointIndexsForDelete = stroke.getSpecialPointIndexForDelete(pList);
		int num = specialPointIndexsForDelete.size();
		double distance1,distance2,distance3;
		double cos;
		double angle;
		double k ;
		double b;
		int j = 0;
		
		points.add(pList.get(specialPointIndexsForDelete.get(0)));
		
		for( int i = 0 ; i < num - 2 ; i++){
			distance1 = CommonFunc.distance(pList.get(specialPointIndexsForDelete.get(i)),pList.get(specialPointIndexsForDelete.get(i+1)));
			distance2 = CommonFunc.distance(pList.get(specialPointIndexsForDelete.get(i+1)),pList.get(specialPointIndexsForDelete.get(i+2)));
			distance3 = CommonFunc.distance(pList.get(specialPointIndexsForDelete.get(i)),pList.get(specialPointIndexsForDelete.get(i+2)));
			//求出折线夹角
			cos = ((distance1)*(distance1)+(distance2)*(distance2)-(distance3)*(distance3))/(2*distance1*distance2);
			//暂定直线夹角大于45度时不时删除手势
			angle = Math.acos(cos)/Math.PI * 180;
			if(angle < 100 ){
				points.add((pList.get(specialPointIndexsForDelete.get(i+1))));
			}
		}

		points.add(pList.get(specialPointIndexsForDelete.get(num-1)));
		
		//判断1
		if(points.size() < 4){
			return false;
		}
		
		//判断2
		double twoPointDistance = CommonFunc.distance(pList.get(specialPointIndexsForDelete.get(0)), pList.get(specialPointIndexsForDelete.get(num-1)));
		if(twoPointDistance < ThresholdProperty.TWO_POINT_IS_CLOSED) {   //判断图形非封闭,如果封闭则非封闭。
			return false;
		}
		
		//判断3
		k =(double) (points.get(1).getY()-points.get(2).getY())/
				(double)(points.get(1).getX()-points.get(2).getX());
		
		b = points.get(1).getY()-k*points.get(1).getX();
		
		if(((points.get(0).getY()-k*points.get(0).getX()-b)*
				(points.get(3).getY()-k*points.get(3).getX()-b))>=0){
			return false;
		}
		
		//判断4
		for( int i = 0 ; i <points.size() - 2 ; i++){
			distance1 = CommonFunc.distance(points.get(i),points.get(i+1));
			distance2 = CommonFunc.distance(points.get(i+1),points.get(i+2));
			distance3 = CommonFunc.distance(points.get(i),points.get(i+2));
			//求出折线夹角
			cos = ((distance1)*(distance1)+(distance2)*(distance2)-(distance3)*(distance3))/(2*distance1*distance2);
			//暂定直线夹角大于45度时不时删除手势
			angle = Math.acos(cos)/Math.PI * 180;
			Log.v("delete num55", distance1 + ","+cos);
			Log.v("delete num55", distance2 + ","+cos);
			Log.v("delete num55", distance3 + ","+cos);
			if(angle < 75 ){
				j++;
			}
		}
		if(j < 2){
				return false;
			}
		return true;
	}
}