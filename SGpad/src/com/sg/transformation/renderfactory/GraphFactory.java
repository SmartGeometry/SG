package com.sg.transformation.renderfactory;

import java.util.List;

import android.util.Log;

import com.sg.logic.common.CommonFunc;
import com.sg.object.Point;
import com.sg.object.graph.Graph;
import com.sg.object.graph.LineGraph;
import com.sg.object.graph.PointGraph;
import com.sg.object.unit.GUnit;
import com.sg.object.unit.LineUnit;
import com.sg.object.unit.PointUnit;
import com.sg.property.common.ThresholdProperty;
import com.sg.transformation.recognizer.Recognizer;

public class GraphFactory {
	
	private static GraphFactory instance = new GraphFactory();
	
	private GraphFactory() {
		
	}
	
	public static GraphFactory getInstance() {
		return instance;
	}
	
	public Graph create(List<Point> pList) {	
		Recognizer recognizer = new Recognizer();
		return recognizer.recognize(pList);
	}
	
	public Graph getCheckedGraph(List<Graph> graphList, Point curPoint) {
		/*
		Graph checkedGraph = null;
		GUnit unit = null;
		double minDistance = ThresholdProperty.GRAPH_CHECKED_DISTANCE;
		double curDistance = minDistance;
		*/
		for(Graph graph: graphList) {
			if(graph.isInGraph(curPoint)){
				return graph;
			}
			/*
			for(GUnit gUnit : graph.getGraph()){
				if(gUnit.isInUnit(curPoint)){
					return graph;
				}
			}
			*/
		}
		return null;
		/*
		for(Graph graph: graphList) {
			if(graph instanceof PointGraph) {
				unit = graph.getGraph().get(0);        //点图只有一个点元对象
				curDistance = CommonFunc.distance(((PointUnit)unit).getPoint(), curPoint);
	
			} 
			if(graph instanceof LineGraph){
				double mDistance = ThresholdProperty.GRAPH_CHECKED_DISTANCE;
				for(GUnit gUnit : graph.getGraph()){
					if(gUnit instanceof LineUnit){
						GUnit startpoint = ((LineUnit)gUnit).getStartPointUnit();
						GUnit endpoint = ((LineUnit)gUnit).getEndPointUnit();
						double checkdistance1 = CommonFunc.distance(((PointUnit)startpoint).getPoint(), curPoint);
						double checkdistance2 = CommonFunc.distance(((PointUnit)endpoint).getPoint(), curPoint);
						double linedistance = CommonFunc.distance(((PointUnit)startpoint).getPoint(), ((PointUnit)endpoint).getPoint());
						if(checkdistance1 < linedistance && checkdistance2 < linedistance){
							curDistance = CommonFunc.lineDistance(((PointUnit)startpoint).getPoint(), ((PointUnit)endpoint).getPoint(), curPoint);
							Log.v("distance1", curDistance + "");
							if(curDistance < mDistance) {
								mDistance = curDistance;
							}
						}
					}
				}
				curDistance = mDistance;
				Log.v("distance", curDistance + "");
			}
			
			if(curDistance < minDistance) {       //选中离点最近的对象
				Log.v("选中", "选中");
				minDistance = curDistance;
				checkedGraph = graph;
				break;
			}
			
		}
		return checkedGraph;
		*/
	}

}
