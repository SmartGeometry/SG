package com.sg.transformation.computeagent;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.sg.control.OperationType;
import com.sg.control.UndoRedoSolver;
import com.sg.control.UndoRedoStruct;
import com.sg.logic.common.CommonFunc;
import com.sg.logic.common.VectorFunc;
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
						float x1 = point1.getX();
						float y1 = point1.getY();
						
						//获取底边第一点坐标
						float x2 = point2.getX();
						float y2 = point2.getY();
						
						//获取底边第二点坐标
						float x3 = point3.getX();
						float y3 = point3.getY();
						
						//新约束点的坐标
						float xo,yo;
						
						//底边斜率
						float k = (y2-y3)/(x2-x3);
						
						switch(((LineUnit)unit).getType()){
							case 1:{
								xo = (x1*(x2-x3)+(y2-k*x2-y1)*(y3-y2))/(k*(y2-y3)+x2-x3);
								yo = k*(xo-x2)+y2;
								//((LineUnit)unit).setStartPointUnit(point1);
								//((LineUnit)unit).setEndPointUnit(new PointUnit((int)xo,(int)yo));
								((LineUnit)unit).getEndPointUnit().setX(xo);
								((LineUnit)unit).getEndPointUnit().setY(yo);
								break;
							}
							case 2:{
								xo = (x1+x2)/2;
								yo = (y1+y2)/2;
								float tempX = (x1+x3)/2;
								float tempY = (y1+y3)/2;
								((LineUnit)unit).getStartPointUnit().setX(tempX);
								((LineUnit)unit).getStartPointUnit().setY(tempY);
								//((LineUnit)unit).setStartPointUnit(new PointUnit((int)tempX,(int)tempY));
								//((LineUnit)unit).setEndPointUnit(new PointUnit((int)xo,(int)yo));
								((LineUnit)unit).getEndPointUnit().setX(xo);
								((LineUnit)unit).getEndPointUnit().setY(yo);
								break;
							}
							case 3:{
								//两腰的长度
								double distance1 = CommonFunc.distance(point1.getPoint(), point2.getPoint());
								double distance2 = CommonFunc.distance(point1.getPoint(), point3.getPoint());
								
								//两腰的比值a
								double a = distance1/distance2;
								
								//底边向量，指向为由point2->point3
								Point vector = new Point(x3-x2,y3-y2);
								
								xo = (float) (x2 + vector.getX()*a/(a+1));
								yo = (float) (y2 + vector.getY()*a/(a+1));
								//((LineUnit)unit).setStartPointUnit(point1);
								//((LineUnit)unit).setEndPointUnit(new PointUnit((int)xo,(int)yo));
								((LineUnit)unit).getEndPointUnit().setX(xo);
								((LineUnit)unit).getEndPointUnit().setY(yo);
								break;
							}
							case 4:{
								xo = (x2+x3)/2;
								yo = (y2+y3)/2;
								//((LineUnit)unit).setStartPointUnit(point1);
								//((LineUnit)unit).setEndPointUnit(new PointUnit((int)xo,(int)yo));
								((LineUnit)unit).getEndPointUnit().setX(xo);
								((LineUnit)unit).getEndPointUnit().setY(yo);
								break;
							}
							case 5:{
								Point vector = new Point(x3-x2,y3-y2);
								double a = ((LineUnit)unit).getProportion();
								xo = (float) (x2 + vector.getX()*a/(a+1));
								yo = (float) (y2 + vector.getY()*a/(a+1));
								double kp = (yo-y1)/(xo-x1);
							/*	if(kp*k<(-0.7) && kp*k>(-2.5) && ((TriangleGraph) graph).getVer(((LineUnit)unit).getVer())){
									xo = (x1*(x2-x3)+(y2-k*x2-y1)*(y3-y2))/(k*(y2-y3)+x2-x3);
									yo = k*(xo-x2)+y2;
									((LineUnit)unit).setType(1);
									((TriangleGraph) graph).setVer(((LineUnit)unit).getVer()/2);
									}
									else{
										Point vector1 = new Point((int)(x1-x2),(int)(y1-y2));
										Point vector2 = new Point((int)(x1-x3),(int)(y1-y3));
										Point vectorpx = new Point((int)(x1-xo),(int)(y1-yo));
										if(VectorFunc.equalangle(vector1, vector2, vectorpx) && ((TriangleGraph) graph).getAngular(((LineUnit)unit).getVer())){
											double distance1 = CommonFunc.distance(point1.getPoint(), point2.getPoint());
											double distance2 = CommonFunc.distance(point1.getPoint(), point3.getPoint());
											double x = distance1/distance2;
											xo = x2 - vector.getX()*x/(x+1);
											yo = y2 - vector.getY()*x/(x+1);
											((LineUnit)unit).setType(3);
											((TriangleGraph) graph).setAngular(((LineUnit)unit).getVer()/2);
										}else{
											if(Math.abs(CommonFunc.distance(new Point((int)xo,(int)yo), point2.getPoint())-
												CommonFunc.distance(new Point((int)xo,(int)yo), point3.getPoint())) <7
											&& ((TriangleGraph) graph).getMid(((LineUnit)unit).getVer())){
												xo = (x2+x3)/2;
												yo = (y2+y3)/2;
												((LineUnit)unit).setType(4);
												((TriangleGraph) graph).setMid(((LineUnit)unit).getVer()/2);
											}
										}
									}*/
								//((LineUnit)unit).setStartPointUnit(point1);
								((LineUnit)unit).getEndPointUnit().setX(xo);
								((LineUnit)unit).getEndPointUnit().setY(yo);
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
	
	public Graph rebuildGraph(Graph graph){
		if(graph instanceof TriangleGraph){
			List<GUnit> units = graph.getGraph();
			for(int i = 0;i < units.size();i++){
				GUnit unit = units.get(i);
				if(unit instanceof LineUnit){
					if(((LineUnit)unit).getType() == 5 && ((LineUnit)unit).isDrag()){
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
						float x1 = point1.getX();
						float y1 = point1.getY();
						
						//获取底边第一点坐标
						float x2 = point2.getX();
						float y2 = point2.getY();
						
						//获取底边第二点坐标
						float x3 = point3.getX();
						float y3 = point3.getY();
						
						//新约束点的坐标
						float xo,yo;
						
						//底边斜率
						float k = (y2-y3)/(x2-x3);
						
						Point vector = new Point(x3-x2,y3-y2);
						double a = ((LineUnit)unit).getProportion();
						xo = (float) (x2 + vector.getX()*a/(a+1));
						yo = (float) (y2 + vector.getY()*a/(a+1));
						double kp = (yo-y1)/(xo-x1);
						if(kp*k<(-0.7) && kp*k>(-2.5) && ((TriangleGraph) graph).getVer(((LineUnit)unit).getVer()/2)){
								xo = (x1*(x2-x3)+(y2-k*x2-y1)*(y3-y2))/(k*(y2-y3)+x2-x3);
								yo = k*(xo-x2)+y2;
								((LineUnit)unit).setType(1);
								((TriangleGraph) graph).setVer(((LineUnit)unit).getVer()/2, false);
								int j = i+1;
								((PointUnit)units.get(j)).setCommonConstrainted(false);
							}
							else{
								Point vector1 = new Point(x1-x2,y1-y2);
								Point vector2 = new Point(x1-x3,y1-y3);
								Point vectorpx = new Point(x1-xo,y1-yo);
								if(VectorFunc.equalangle(vector1, vector2, vectorpx) && ((TriangleGraph) graph).getAngular(((LineUnit)unit).getVer()/2)){
									double distance1 = CommonFunc.distance(point1.getPoint(), point2.getPoint());
									double distance2 = CommonFunc.distance(point1.getPoint(), point3.getPoint());
									double x = distance1/distance2;
									xo = (float) (x2 - vector.getX()*x/(x+1));
									yo = (float) (y2 - vector.getY()*x/(x+1));
									((LineUnit)unit).setType(3);
									((TriangleGraph) graph).setAngular(((LineUnit)unit).getVer()/2, false);
									int j = i+1;
									((PointUnit)units.get(j)).setCommonConstrainted(false);
								}else{
									if(Math.abs(CommonFunc.distance(new Point(xo,yo), point2.getPoint())-
										CommonFunc.distance(new Point(xo,yo), point3.getPoint())) < 7
									&& ((TriangleGraph) graph).getMid(((LineUnit)unit).getVer()/2)){
										xo = (x2+x3)/2;
										yo = (y2+y3)/2;
										((LineUnit)unit).setType(4);
										((TriangleGraph) graph).setMid(((LineUnit)unit).getVer()/2, false);
										int j = i+1;
										((PointUnit)units.get(j)).setCommonConstrainted(false);
									}
								}
							}
						((LineUnit)unit).setStartPointUnit(point1);
						((LineUnit)unit).getEndPointUnit().setX(xo);
						((LineUnit)unit).getEndPointUnit().setY(yo);
						((LineUnit)unit).setDrag(false);
					}
				}
			}
		}
		return graph;
	}
}
