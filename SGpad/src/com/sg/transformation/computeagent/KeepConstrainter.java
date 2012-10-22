package com.sg.transformation.computeagent;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.sg.control.OperationType;
import com.sg.control.UndoRedoSolver;
import com.sg.control.UndoRedoStruct;
import com.sg.logic.common.CommonFunc;
import com.sg.object.Point;
import com.sg.object.graph.Sketch;
import com.sg.object.graph.Graph;
import com.sg.object.graph.LineGraph;
import com.sg.object.graph.RectangleGraph;
import com.sg.object.graph.TriangleGraph;
import com.sg.object.unit.GUnit;
import com.sg.object.unit.LineUnit;
import com.sg.object.unit.PointUnit;
import com.sg.property.common.ThresholdProperty;
import com.sg.transformation.recognizer.Regulariser;

public class KeepConstrainter {
	
	private PointUnit point1,point2,point3;
	
	private static KeepConstrainter instance = new KeepConstrainter();
	
	public KeepConstrainter(){}
	
	public static KeepConstrainter getInstance() {
		return instance;
	}
	
	public Graph keepConstraint(Graph graph){
		if(!graph.getConstraint()){
			return null;
		}
		if(graph instanceof TriangleGraph){									//判定是否三角形
			List<GUnit> units = graph.getGraph();		
			for(GUnit unit : units){										//遍历三角形图形链表
				if(unit instanceof LineUnit){			
					if(((LineUnit)unit).getType() != 0){					//判定是否约束直线
						switch(((LineUnit)unit).getVer()){					//根据直线图元所存的顶点索引找出关联点
							case 0:{
									point1 = (PointUnit)units.get(0);
									point2 = (PointUnit)units.get(2);
									point3 = (PointUnit)units.get(4);
									break;
							}
							case 2:{
									point1 = (PointUnit)units.get(2);
									point2 = (PointUnit)units.get(0);
									point3 = (PointUnit)units.get(4);
									break;
							}
							case 4:{
									point1 = (PointUnit)units.get(4);
									point2 = (PointUnit)units.get(0);
									point3 = (PointUnit)units.get(2);
									break;
							}
						}
						
						//获取顶点坐标
						double x1 = (double)point1.getX();
						double y1 = (double)point1.getY();
						
						//获取底边第一点坐标
						double x2 = (double)point2.getX();
						double y2 = (double)point2.getY();
						
						//获取底边第二点坐标
						double x3 = (double)point3.getX();
						double y3 = (double)point3.getY();
						
						//新约束点的坐标
						double xo,yo;
						
						//底边斜率
						double k = (y2-y3)/(x2-x3);
						
						
						
						switch(((LineUnit)unit).getType()){
							case 1:{
								xo = (x1*(x2-x3)+(y2-k*x2-y1)*(y3-y2))/(k*(y2-y3)+x2-x3);
								yo = k*(xo-x2)+y2;
								((LineUnit)unit).setStartPointUnit(point1);
								((LineUnit)unit).setEndPointUnit(new PointUnit((int)xo,(int)yo));
								break;
							}
							case 2:{
								xo = (x1+x2)/2;
								yo = (y1+y2)/2;
								double tempX = (x1+x3)/2;
								double tempY = (y1+y3)/2;
								((LineUnit)unit).setStartPointUnit(new PointUnit((int)tempX,(int)tempY));
								((LineUnit)unit).setEndPointUnit(new PointUnit((int)xo,(int)yo));
								break;
							}
							case 3:{
								//两腰的长度
								double distance1 = CommonFunc.distance(point1.getPoint(), point2.getPoint());
								double distance2 = CommonFunc.distance(point1.getPoint(), point3.getPoint());
								
								//两腰的比值a
								double a = distance1/distance2;
								
								//底边向量，指向为由point2->point3
								Point vector = new Point((int)(x3-x2),(int)(y3-y2));
								
								xo = x2 + vector.getX()*a/(a+1);
								yo = y2 + vector.getY()*a/(a+1);
								((LineUnit)unit).setStartPointUnit(point1);
								((LineUnit)unit).setEndPointUnit(new PointUnit((int)xo,(int)yo));
								break;
							}
							case 4:{
								xo = (x2+x3)/2;
								yo = (y2+y3)/2;
								((LineUnit)unit).setStartPointUnit(point1);
								((LineUnit)unit).setEndPointUnit(new PointUnit((int)xo,(int)yo));
								break;
							}
							case 5:{
								Point vector = new Point((int)(x3-x2),(int)(y3-y2));
								double a = ((LineUnit)unit).getProportion();
								xo = x2 + vector.getX()*a/(a+1);
								yo = y2 + vector.getY()*a/(a+1);
								((LineUnit)unit).setStartPointUnit(point1);
								((LineUnit)unit).setEndPointUnit(new PointUnit((int)xo,(int)yo));
								break;
							}
						}
					}
				}
			}
			return graph;
		}
		return null;
	}
}
