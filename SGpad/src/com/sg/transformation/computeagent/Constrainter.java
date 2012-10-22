package com.sg.transformation.computeagent;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.sg.control.OperationType;
import com.sg.control.UndoRedoSolver;
import com.sg.control.UndoRedoStruct;
import com.sg.logic.common.CommonFunc;
import com.sg.object.graph.Sketch;
import com.sg.object.graph.Graph;
import com.sg.object.graph.LineGraph;
import com.sg.object.graph.RectangleGraph;
import com.sg.object.graph.TriangleGraph;
import com.sg.object.unit.GUnit;
import com.sg.object.unit.LineUnit;
import com.sg.object.unit.PointUnit;
import com.sg.property.common.ThresholdProperty;

public class Constrainter {
	
	private static Constrainter instance = new Constrainter();
	
	//private PointUnit constraintPointUnit;
	
	private UndoRedoSolver URSolver;
	private Constrainter() {
		URSolver = UndoRedoSolver.getInstance();
	}
	
	public static Constrainter getInstance() {
		return instance;
	}
	
	
	public boolean constraint(List<Graph> graphList, Graph curGraph) { 
		Graph constraintGraph = null;
		if(curGraph instanceof LineGraph && isALine(curGraph)) {      //对直线图进行约束识别
			for(Graph graph : graphList) {
				if(graph instanceof LineGraph && !graph.isClosed() && graph != curGraph){ //如果图形是直线 不闭合图形
					constraintGraph = this.lineToLineConstrain(graph, curGraph);
					if(constraintGraph != null)
						break;
				}else{
					if(graph instanceof TriangleGraph || graph instanceof RectangleGraph){
						constraintGraph = new LinearCloseConstraint().linearConstraint(graph, curGraph);
						if(constraintGraph != null)
							break;
					}
				}
			}
		}
		
		if(constraintGraph != null){   //如果有约束
			Graph temp = rebuildLinearClose(graphList, constraintGraph);  //重构三角形，四边形
			if(temp == null)
				temp = constraintGraph;
			if(curGraph.isChecked()){                     //如果curGraph是选中的图形 于其他图形有约束关系，则需删除curGraph
				graphList.remove(curGraph);
				URSolver.EnUndoStack(new UndoRedoStruct(OperationType.MOVEANDCONSTRAIN, temp.clone()));
			}else{
				URSolver.EnUndoStack(new UndoRedoStruct(OperationType.CHANGE, temp.clone()));
			}
			return true;
		}else{                        //如果没有约束关系
			if(!curGraph.isChecked()) {            //如果curGraph没被选中，即是刚画上去的，则在图形链表添加
				graphList.add(curGraph);
				if(curGraph instanceof Sketch){
					URSolver.EnUndoStack(new UndoRedoStruct(OperationType.CREATE, curGraph));
				}else{
					URSolver.EnUndoStack(new UndoRedoStruct(OperationType.CREATE, curGraph.clone()));
				}
			}else{
				URSolver.EnUndoStack(new UndoRedoStruct(OperationType.CHANGE, curGraph.clone()));
			}
			return false;
		}
	}
	/*
	public void constraint(List<Graph> graphList, Graph curGraph) { 

		double minDist, curDist;
		Graph constraintGraph = null;
		PointUnit constraintPointUnit = null;
		boolean hasConstraint = false;
		GUnit curUnit;
		
		minDist = ThresholdProperty.TWO_POINT_IS_CONSTRAINTED;
		if(isALine(curGraph)) {      //只对直线图进行约束识别

			LineGraph tmpLineGraph = (LineGraph) curGraph;
			for(GUnit unit : curGraph.getGraph()) {
				if(unit instanceof LineUnit) {
					curUnit = unit;           //获取当前直线图形里的约束关系，即为直线元
				}
			}
			
			for(Graph graph : graphList) {
				if(graph instanceof LineGraph){
				if(isAllLineUnit(graph) && !graphList.contains(curGraph)) {   //由直线构成的图像，
					Log.v("进入约束识别", "进入约束识别");
					for(GUnit unit : graph.getGraph()) {  
						if(unit instanceof PointUnit) {         //直线之间的约束，其实就是点之间的约束。所以只要寻找图形列表中的点元即可
							curDist = lineToPoint(tmpLineGraph,(PointUnit) unit);  //约束的直线的两端点和点元的最短距离
							Log.v("进入约束识别distance", curDist + " min | " + minDist);
							Log.v("进入约束识别degree", this.constraintPointUnit.getDegree() + "");
							if(curDist < minDist && this.constraintPointUnit.getDegree() == 1) {         //从所有点元中找出离约束直线最近的点元，且该点元只有一条约束关系
								curDist = minDist;
								constraintPointUnit = this.constraintPointUnit;    //一个是局部变量一个是成员变量！！
								constraintGraph = graph;                           //约束的图形
							}
						}
					}
				}
			}
			
			if(constraintGraph != null) {
				Log.v("约束", "约束");
				boolean isLine = isALine(constraintGraph);    //如果约束图形是一条直线直线，只需进行一个点的约束识别，如果识别两点会造成约束图形与被约束图形直线重合
				PointUnit otherPointUnit = getOtherPointUnit(tmpLineGraph, constraintPointUnit);  //被约束图形（直线）的另一个点元
				otherPointUnit.clearDegree();    //成为一个独立的点
				
				minDist = ThresholdProperty.TWO_POINT_IS_CONSTRAINTED;
				PointUnit otherConstraintPointUnit = null;
				if(!isLine) {
					for(GUnit unit : constraintGraph.getGraph()) {
						if(unit instanceof PointUnit) {
							if(((PointUnit)unit).getDegree() == 1) {   //度为1表示只有一条约束关系
								curDist = CommonFunc.distance((PointUnit)unit, otherPointUnit);
								if(curDist < minDist) {
									minDist = curDist;
									otherConstraintPointUnit = (PointUnit)unit;
								}
							}
						}
					}
				}
				
				if(otherConstraintPointUnit != null) {
					constraintGraph.buildGraph(new LineUnit(constraintPointUnit, otherConstraintPointUnit));
				} else {
					constraintGraph.buildGraph(new LineUnit(otherPointUnit, constraintPointUnit));
					constraintGraph.buildGraph(otherPointUnit);
				}
				
				rebuildTriangle(graphList, constraintGraph);
				hasConstraint = true;
				constraintGraph.setConstrainted(hasConstraint);
				URSolver.EnUndoStack(new UndoRedoStruct(OperationType.CHANGE, constraintGraph.clone()));
			}
		}
		
		if(!hasConstraint) {
			if(curGraph != null && !graphList.contains(curGraph)) {
				graphList.add(curGraph);
			}
			if(curGraph instanceof Sketch){
				URSolver.EnUndoStack(new UndoRedoStruct(OperationType.CREATE, curGraph));
			}else{
				URSolver.EnUndoStack(new UndoRedoStruct(OperationType.CREATE, curGraph.clone()));
			}
		}
		
	}
	*/
	
	//直线图形curGraph与直线图形graph的约束
	private Graph lineToLineConstrain(Graph constraintGraph, Graph curGraph){
		double minDist = ThresholdProperty.TWO_POINT_IS_CONSTRAINTED;
		PointUnit constraintPointUnit = null;
		PointUnit otherConstraintPointUnit = null;
		//GUnit curUnit;
		
		int num = constraintGraph.getGraph().size();
		PointUnit firstPointUnit = (PointUnit) constraintGraph.getGraph().get(0);    //约束图形的第一个点元
		PointUnit lastPointUnit = (PointUnit) constraintGraph.getGraph().get(num - 1);  //约束图形的最后一个点元

		if(this.lineToPoint((LineGraph) curGraph, firstPointUnit) < minDist){      //发生约束的是第一个点
			constraintPointUnit = firstPointUnit;
		}else{
			if(this.lineToPoint((LineGraph) curGraph, lastPointUnit) < minDist){     //发生约束的是最后一个点
				constraintPointUnit = lastPointUnit;
			}else{
				return null;           //没有约束
			}
		}
		boolean isALine = isALine(constraintGraph);    //如果约束图形是一条直线直线，只需进行一个点的约束识别，如果识别两点会造成约束图形与被约束图形直线重合
		PointUnit otherPointUnit = getOtherPointUnit(curGraph, constraintPointUnit);  //被约束图形（直线）的另一个点元
		otherPointUnit.clearDegree();    //成为一个独立的点
		
		if(!isALine) {
			if(constraintPointUnit == firstPointUnit){          //如果约束点是第一个点
				if(CommonFunc.distance(lastPointUnit, otherPointUnit) < minDist){         //有另一个约束点，则是闭合图形
					otherConstraintPointUnit = lastPointUnit;
					constraintGraph.buildGraph(new LineUnit(constraintPointUnit, otherConstraintPointUnit));
					constraintGraph.setIsClosed(true);
				}else{                                                                    //没有另一个约束点，则图元加在第一个点之前，保持点线点的顺序
					constraintGraph.buildGraph(0, new LineUnit(constraintPointUnit, otherPointUnit));
					constraintGraph.buildGraph(0, otherPointUnit);
				}
			}else{                                             //如果约束点是最后一个点
				if(CommonFunc.distance(firstPointUnit, otherPointUnit) < minDist){
					otherConstraintPointUnit = firstPointUnit;
					constraintGraph.buildGraph(new LineUnit(constraintPointUnit, otherConstraintPointUnit));
					constraintGraph.setIsClosed(true);
				}else{                                                                    //没有另一个约束点，则图元加在最后一个点之后，保持点线点的顺序
					constraintGraph.buildGraph(num, new LineUnit(constraintPointUnit, otherPointUnit));
					constraintGraph.buildGraph(num + 1, otherPointUnit);
				}
			}
		}else{
			if(constraintPointUnit == firstPointUnit){
				constraintGraph.buildGraph(0, new LineUnit(constraintPointUnit, otherPointUnit));
				constraintGraph.buildGraph(0, otherPointUnit);
			}else{
				constraintGraph.buildGraph(num, new LineUnit(constraintPointUnit, otherPointUnit));
				constraintGraph.buildGraph(num + 1, otherPointUnit);
			}
		}
			return constraintGraph;
	}
	
	//重新构建闭合图形
	private Graph rebuildLinearClose(List<Graph> graphList, Graph graph) {
		Graph temp = null;
		if(graph instanceof LineGraph && graph.isClosed()){
			List<GUnit> units = graph.getGraph();
			if(units.size() == 6){
				//重新构造成三角形
				graphList.remove(graph);
				temp = new TriangleGraph(units);
				temp.setID(graph.getID());
				graphList.add(temp);
			}
			if(units.size() == 8){
				//重新构造四边形
				graphList.remove(graph);
				temp = new RectangleGraph(units);
				temp.setID(graph.getID());
				graphList.add(temp);
			}
		}
		return temp;
	}
	/*
	 * 判断一个图形是否由直线图元构成（不算点图元）
	 * */
	/*
	private boolean isAllLineUnit(Graph graph) {
		for(GUnit unit : graph.getGraph()) {
			
			if(!(unit instanceof LineUnit) && !(unit instanceof PointUnit)) {
				return false;
			}
		}
		return true;
	}
	
	private void rebuildTriangle(List<Graph> graphList, Graph graph) {
		List<GUnit> units = graph.getGraph();
		int pcount = 0;
		
		if(units.size() == 6) {
			for(GUnit unit : units) {
				if(unit instanceof PointUnit) {
					pcount++;
					if(((PointUnit)unit).getDegree() != 2) {
						return false;
					}
				} 
			}
			if(pcount != 3) {
				return false;
			} else {
				graphList.remove(graph);
				graphList.add(new TriangleGraph(units));     //重新构造成三角形
				//TriangleGraph triangle = new TriangleGraph(units);
				//graph = triangle;
				Log.v("约束为三角形", "约束为三角形");
				return true;
			}
		} else {
			return false;
		}
	}
	*/
	/*
	 * 判断图形是不是一条直线。根据点元的度来判断
	 * */
	private boolean isALine(Graph constraintGraph) {
		for(GUnit unit : constraintGraph.getGraph()) {
			if(unit instanceof PointUnit) {
				if(((PointUnit)unit).getDegree() != 1) {
					return false;
				}
			}
		}
		return true;
	}
	
	/*
	 * 线段的两个端点到点的最小距离
	 * */
	private double lineToPoint(LineGraph lineGraph, PointUnit pUnit) {
		double[] minDist = new double[2];
		/*
		int i = 0;
		for(GUnit unit : lineGraph.getGraph()) {
			if(unit instanceof PointUnit) {
				minDist[i] = CommonFunc.distance((PointUnit)unit, pUnit);
				i++;
			}
		}
		*/
		//this.constraintPointUnit = pUnit;
		int num = lineGraph.getGraph().size();
		minDist[0] = CommonFunc.distance((PointUnit)lineGraph.getGraph().get(0), pUnit);
		minDist[1] = CommonFunc.distance((PointUnit)lineGraph.getGraph().get(num - 1), pUnit);
		return CommonFunc.min(minDist);
	}
	
	private PointUnit getOtherPointUnit(Graph graph, PointUnit constraintPUnit) {
		//找到直线除了约束的另一个点
		//PointUnit theOther = null;
		/*
		double maxDist = Double.MIN_VALUE;
		double curDist = maxDist;
		
		if(graph instanceof LineGraph) {
			for(GUnit unit : graph.getGraph()) {
				if(unit instanceof PointUnit) {
					curDist = CommonFunc.distance((PointUnit)unit, constraintPUnit);
					if(maxDist < curDist) {
						maxDist = curDist;
						theOther = (PointUnit)unit;
					}
				}
			}
			return theOther;
		}
		
		if(graph instanceof LineGraph){
			for(GUnit unit : graph.getGraph()) {
				if(unit instanceof LineUnit){
					PointUnit starPointUnit = ((LineUnit)unit).getStartPointUnit();
					PointUnit endPointUnit = ((LineUnit)unit).getEndPointUnit();
					//距离远的为另一个点
					if(CommonFunc.distance(starPointUnit, constraintPUnit) < CommonFunc.distance(endPointUnit, constraintPUnit)){
						theOther = endPointUnit;
					}
					else{
						theOther = starPointUnit;
					}
					return theOther;
				}
			}
		}
		return null;
		*/
		//理约束点远的为另一点
		int num = graph.getGraph().size();
		PointUnit starPointUnit = (PointUnit) graph.getGraph().get(0);
		PointUnit endPointUnit = (PointUnit) graph.getGraph().get(num - 1);
		if(CommonFunc.distance(starPointUnit, constraintPUnit) < CommonFunc.distance(endPointUnit, constraintPUnit)){
			return endPointUnit;
		}
		else{
			return starPointUnit;
		}
	}
}
