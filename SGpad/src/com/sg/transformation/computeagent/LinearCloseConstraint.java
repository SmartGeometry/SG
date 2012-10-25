/*
 * 线性闭合约束
 * 传入的是一个闭合线性图形，即是一个n边形，由n个点元和n个线元构成，
 * */
package com.sg.transformation.computeagent;

import java.util.List;

import android.util.Log;

import com.sg.logic.common.CommonFunc;
import com.sg.logic.common.VectorFunc;
import com.sg.object.graph.Graph;
import com.sg.object.graph.LineGraph;
import com.sg.object.graph.RectangleGraph;
import com.sg.object.graph.TriangleGraph;
import com.sg.object.unit.GUnit;
import com.sg.object.unit.LineUnit;
import com.sg.object.unit.PointUnit;
import com.sg.object.Point;

public class LinearCloseConstraint {
	
	private int twoPointDistance = 30;
	private int lineDistance = 15;
	private boolean MedianLineConstraint = false;
	private boolean VerticalLineCostraint = false;
	private boolean AngleBisectorConstraint = false;
	private boolean MidLineConstraint = false;
	private boolean Constraint = false;
	
	public LinearCloseConstraint() {
		
	}
	
	public Graph linearConstraint(Graph graph, Graph curGraph) {
		if(graph instanceof TriangleGraph){
			List<GUnit> units = graph.getGraph();
			List<GUnit> lineUnit = curGraph.getGraph();
			//graph.output();
			
			//获取三角形的第一顶点（x1,y1）；
			float x1 = ((PointUnit)units.get(0)).getX();
			float y1 = ((PointUnit)units.get(0)).getY();
			PointUnit point1 = (PointUnit)units.get(0);
		//	Log.v("x1,y1",x1+","+y1);
			
			//获取三角形的第二个顶点(x2,y2)；
			float x2 = ((PointUnit)units.get(2)).getX();
			float y2 = ((PointUnit)units.get(2)).getY();
			PointUnit point2 = (PointUnit)units.get(2);
		//	Log.v("x2,y2",x2+","+y2);
			
			//获取三角形的第三个顶点(x3,y3)；
			float x3 = ((PointUnit)units.get(4)).getX();
			float y3 = ((PointUnit)units.get(4)).getY();
			PointUnit point3 = (PointUnit)units.get(4);
		//	Log.v("x3,y3",x3+","+y3);
			
			//获取待约束线的第一点(px1,py1);
			float px1 = ((PointUnit)lineUnit.get(0)).getX();
			float py1 = ((PointUnit)lineUnit.get(0)).getY();
			PointUnit Pxpoint1 = (PointUnit)lineUnit.get(0);
		//	Log.v("px1,py1",px1+","+py1);
			
			//获取待约束线的第一点(px2,py2);
			float px2 = ((PointUnit)lineUnit.get(2)).getX();
			float py2 = ((PointUnit)lineUnit.get(2)).getY();
			PointUnit Pxpoint2 = (PointUnit)lineUnit.get(2);
		//	Log.v("px2,py2",px2+","+py2);
			
			//xo,yo用于计算待约束直线规整后与三角形边的交点
			float xo,yo;
			
			//k1,k2,k3为三角形各边的斜率
			float k1 = (y3-y2)/(x3-x2);
			float k2 = (y1-y3)/(x1-x3);
			float k3 = (y1-y2)/(x1-x2);
			
			//待约束直线斜率
			float kp = (py1-py2)/(px1-px2);
			
			//三角形边长向量
			Point vector1 = new Point(x1-x2,y1-y2);
			Point vector2 = new Point(x1-x3,y1-y3);
			Point vector3 = new Point(x2-x3,y2-y3);
			Point vector4 = new Point(x2-x1,y2-y1);
			Point vector5 = new Point(x3-x1,y3-y1);
			Point vector6 = new Point(x3-x2,y3-y2);
			
			//待约束直线的向量
			Point vectorpx = new Point(px2-px1,py2-py1);
			
			/* 识别待约束直线是否中位线
			 * 如果直线的两点与三角形某两边的中点的距离分别小于15，那么认为该直线为三角形的中位线
			 */
			if(CommonFunc.distance(Pxpoint1.getPoint(), new Point((x1+x2)/2,(y1+y2)/2)) < twoPointDistance){
				if(CommonFunc.distance(Pxpoint2.getPoint(), new Point((x1+x3)/2,(y1+y3)/2)) < twoPointDistance
				&& ((TriangleGraph) graph).getMedian(0)){
					LineUnit line = new LineUnit(new PointUnit((x1+x2)/2,(y1+y2)/2),
												 new PointUnit((x1+x3)/2,(y1+y3)/2));
					line.setType(2);
					line.setVer(0);
					graph.buildGraph(line);
					graph.setConstraint();
					MedianLineConstraint = true;
					((TriangleGraph) graph).setMedian(0, false);
				}
				if(CommonFunc.distance(Pxpoint2.getPoint(), new Point((x2+x3)/2,(y2+y3)/2)) < twoPointDistance
				&& ((TriangleGraph) graph).getMedian(1)){
					LineUnit line = new LineUnit(new PointUnit((x1+x2)/2,(y1+y2)/2),
												 new PointUnit((x2+x3)/2,(y2+y3)/2));
					line.setType(2);
					line.setVer(2);
					graph.buildGraph(line);
					graph.setConstraint();
					MedianLineConstraint = true;
					((TriangleGraph) graph).setMedian(1, false);
				}
			}
			
			if(CommonFunc.distance(Pxpoint1.getPoint(), new Point((x1+x3)/2,(y1+y3)/2)) < twoPointDistance){
				if(CommonFunc.distance(Pxpoint2.getPoint(), new Point((x1+x2)/2,(y1+y2)/2)) < twoPointDistance
				&& ((TriangleGraph) graph).getMedian(0)){
					LineUnit line = new LineUnit(new PointUnit((x1+x3)/2,(y1+y3)/2),
												 new PointUnit((x1+x2)/2,(y1+y2)/2));
					line.setType(2);
					line.setVer(0);
					graph.buildGraph(line);
					graph.setConstraint();
					MedianLineConstraint = true;
					((TriangleGraph) graph).setMedian(0, false);
				}
				if(CommonFunc.distance(Pxpoint2.getPoint(), new Point((x2+x3)/2,(y2+y3)/2)) < twoPointDistance
				&& ((TriangleGraph) graph).getMedian(2)){
					LineUnit line = new LineUnit(new PointUnit((x1+x3)/2,(y1+y3)/2),
												 new PointUnit((x2+x3)/2,(y2+y3)/2));
					line.setType(2);
					line.setVer(4);
					graph.buildGraph(line);
					graph.setConstraint();
					MedianLineConstraint = true;
					((TriangleGraph) graph).setMedian(2, false);
				}
			}
			
			if(CommonFunc.distance(Pxpoint1.getPoint(), new Point((x2+x3)/2,(y2+y3)/2)) < twoPointDistance){
				if(CommonFunc.distance(Pxpoint2.getPoint(), new Point((x1+x2)/2,(y1+y2)/2)) < twoPointDistance
				&& ((TriangleGraph) graph).getMedian(1)){
					LineUnit line = new LineUnit(new PointUnit((x2+x3)/2,(y2+y3)/2),
												 new PointUnit((x1+x2)/2,(y1+y2)/2));
					line.setType(2);
					line.setVer(2);
					graph.buildGraph(line);
					graph.setConstraint();
					MedianLineConstraint = true;
					((TriangleGraph) graph).setMedian(1, false);
				}
				if(CommonFunc.distance(Pxpoint2.getPoint(), new Point((x1+x3)/2,(y1+y3)/2)) < twoPointDistance
				&& ((TriangleGraph) graph).getMedian(2)){
					LineUnit line = new LineUnit(new PointUnit((x2+x3)/2,(y2+y3)/2),
												 new PointUnit((x1+x3)/2,(y1+y3)/2));
					line.setType(2);
					line.setVer(4);
					graph.buildGraph(line);
					graph.setConstraint();
					MedianLineConstraint = true;
					((TriangleGraph) graph).setMedian(2, false);
				}
			}
			Log.v("start","oho");
			//识别待约束直线是否垂线、角平分线、中位线、中线、普通约束线。
			/*
			 * 约束方法。
			 * 1.垂直约束：判定待约束直线斜率与底边斜率乘积的大小，如果在(-0.8,-1.2)这个区间范围内，判定为两直线垂直
			 * 			再根据两向量垂直关系a1*a2+b1*b2=0，垂足点在三角形底边直线上可以求出垂足点(xo,yo)。
			 * 2.角平分线约束：判定两条三角形边长与待约束直线的角度之差，如果相差在12°之间，判定为角平分线
			 * 			       根据角平分线的性质，存在 AB/AC = BD/CD,其中D为角平分线与底边的交点，A为被平分角。
			 * 3.中线约束：判定待约束直线与底边的交点距底边两点的距离，如相差在6之间，判定待约束直线为中线
			 * 		           根据中线定理，交点在底边的重点。
			 * 4.普通约束直线：除以上三种直线外与顶点和底边相交的直线。
			 */
			if((CommonFunc.distance(Pxpoint1.getPoint(), point1.getPoint()) < twoPointDistance 
					&& CommonFunc.lineDistance(point2.getPoint(), point3.getPoint(), Pxpoint2.getPoint()) < lineDistance)
					|| (CommonFunc.distance(Pxpoint2.getPoint(), point1.getPoint()) < twoPointDistance
					&& CommonFunc.lineDistance(point2.getPoint(), point3.getPoint(), Pxpoint1.getPoint()) < lineDistance)){
						if(kp*k1<(-0.7) && kp*k1>(-2.5) && ((TriangleGraph) graph).getVer(0)){
						xo = (x1*(x2-x3)+(y2-k1*x2-y1)*(y3-y2))/(k1*(y2-y3)+x2-x3);
						yo = k1*(xo-x2)+y2;
						PointUnit constrainted = new PointUnit(xo,yo);
						constrainted.setInLine(true);
						LineUnit line = new LineUnit(point1,constrainted);
						line.setType(1);
						line.setVer(0);
						graph.buildGraph(line);
						graph.buildGraph(constrainted);
						graph.setConstraint();
						VerticalLineCostraint = true;
						Log.v("VerticalLine","verticalLine");
						((TriangleGraph) graph).setVer(0, false);
						}
						else{
							xo = (kp*x1+y2-y1-k1*x2)/(kp-k1);
							yo = k1*(xo-x2)+y2;
							Log.v("end","");
							if(VectorFunc.equalangle(vector1, vector2, vectorpx) && ((TriangleGraph) graph).getAngular(0)){
							//	xo = (Math.sqrt(1+k3*k3)*(k1-k2)-(k1-k3)*Math.sqrt(1+k2*k2))/((y2-k1*x2-y1+k3*x1)*Math.sqrt(1+k2*k2)-(y2-k1*x2-y1+k2*x1)*Math.sqrt(1+k3*k3));
							//	yo = k1*(xo-x2)+y2;
							/*	double tempX = x1 + VectorFunc.returnX(vector1, vector2);
								double tempY = y1 + VectorFunc.returnY(vector1, vector2);
								Log.v("temp",tempX+","+tempY);
								double tempK = (tempY-y1)/(tempX-x1);
								xo = (tempK*x1+y2-y1-k1*x2)/(tempK-k1);
								yo = k1*(xo-x2)+y2;*/
								double distance1 = CommonFunc.distance(point1.getPoint(), point2.getPoint());
								double distance2 = CommonFunc.distance(point1.getPoint(), point3.getPoint());
								double k = distance1/distance2;
								xo = (float) (x2 - vector3.getX()*k/(k+1));
								yo = (float) (y2 - vector3.getY()*k/(k+1));
								Log.v("xo,yo",xo+","+yo);
								PointUnit constrainted = new PointUnit(xo,yo);
								constrainted.setInLine(true);
								LineUnit line = new LineUnit(point1,constrainted);
								line.setType(3);
								line.setVer(0);
								graph.buildGraph(line);
								graph.buildGraph(constrainted);
								graph.setConstraint();
								AngleBisectorConstraint = true;
								Log.v("AngularBisctorConstraint","a");
								((TriangleGraph) graph).setAngular(0, false);
							}else{
								if(Math.abs(CommonFunc.distance(new Point(xo,yo), point2.getPoint())-
									CommonFunc.distance(new Point(xo,yo), point3.getPoint())) <7
								&& ((TriangleGraph) graph).getMid(0)){
								//	Log.v("distance1",CommonFunc.distance(new Point(xo,yo), point2.getPoint())+"");
								//	Log.v("distance2",CommonFunc.distance(new Point(xo,yo), point3.getPoint())+"");
									xo = (x2+x3)/2;
									yo = (y2+y3)/2;
									PointUnit constrainted = new PointUnit(xo,yo);
									constrainted.setInLine(true);
									LineUnit line = new LineUnit(point1, constrainted);
									line.setType(4);
									line.setVer(0);
									graph.buildGraph(line);
									graph.buildGraph(constrainted);
									graph.setConstraint();
									MidLineConstraint = true;
									Log.v("Mid","Middle Line");
									((TriangleGraph) graph).setMid(0, false);
								}else{
									PointUnit verPoint = new PointUnit(xo,yo);
									verPoint.setMark(0);      //记录一般约束线的顶点
									verPoint.setInLine(true);		//标记
									verPoint.setCommonConstrainted(true);
									LineUnit line = new LineUnit(point1,verPoint);
									line.setType(5);
									line.setVer(0);
									double a = CommonFunc.distance(point2, verPoint) / CommonFunc.distance(point3, verPoint);
									line.setProportion(a);
									graph.buildGraph(line);
									graph.buildGraph(verPoint);
									graph.setConstraint();
									Constraint = true;
									Log.v("Why","no sign");
								}
						}
					}
					Log.v("nani","1");
					return graph;
				}
			
			if((CommonFunc.distance(Pxpoint1.getPoint(), point2.getPoint()) < twoPointDistance 
					&& CommonFunc.lineDistance(point1.getPoint(), point3.getPoint(), Pxpoint2.getPoint()) < lineDistance)
					|| (CommonFunc.distance(Pxpoint2.getPoint(), point2.getPoint()) < twoPointDistance
					&& CommonFunc.lineDistance(point1.getPoint(), point3.getPoint(), Pxpoint1.getPoint()) < lineDistance)){
						if(kp*k2<(-0.7) && kp*k2>(-2.5) && ((TriangleGraph) graph).getVer(1)){
							xo = (x2*(x1-x3)+(y1-k2*x1-y2)*(y3-y1))/(k2*(y1-y3)+x1-x3);
							yo = k2*(xo-x1)+y1;
							PointUnit constrainted = new PointUnit(xo,yo);
							constrainted.setInLine(true);
							LineUnit line = new LineUnit(point2, constrainted);
							line.setType(1);
							line.setVer(2);
							graph.buildGraph(line);
							graph.buildGraph(constrainted);
							graph.setConstraint();
							VerticalLineCostraint = true;
							Log.v("VerticalLine","verticalLine");
							((TriangleGraph) graph).setVer(1, false);
						}
						else{
							xo = (kp*x2+y1-y2-k2*x1)/(kp-k2);
							yo = k2*(xo-x1)+y1;
							Log.v("end","");
							if(VectorFunc.equalangle(vector3, vector4, vectorpx) && ((TriangleGraph) graph).getAngular(1)){
							//	xo = (Math.sqrt(1+k3*k3)*(k1-k2)-(k1-k3)*Math.sqrt(1+k2*k2))/((y2-k1*x2-y1+k3*x1)*Math.sqrt(1+k2*k2)-(y2-k1*x2-y1+k2*x1)*Math.sqrt(1+k3*k3));
							//	yo = k1*(xo-x2)+y2;
							/*	double tempX = x1 + VectorFunc.returnX(vector1, vector2);
								double tempY = y1 + VectorFunc.returnY(vector1, vector2);
								Log.v("temp",tempX+","+tempY);
								double tempK = (tempY-y1)/(tempX-x1);
								xo = (tempK*x1+y2-y1-k1*x2)/(tempK-k1);
								yo = k1*(xo-x2)+y2;*/
								double distance1 = CommonFunc.distance(point2.getPoint(), point1.getPoint());
								double distance2 = CommonFunc.distance(point2.getPoint(), point3.getPoint());
								double k = distance1/distance2;
								xo = (float) (x1 - vector2.getX()*k/(k+1));
								yo = (float) (y1 - vector2.getY()*k/(k+1));
								Log.v("xo,yo",xo+","+yo);
								PointUnit constrainted = new PointUnit(xo,yo);
								constrainted.setInLine(true);
								LineUnit line = new LineUnit(point2, constrainted);
								line.setType(3);
								line.setVer(2);
								graph.buildGraph(line);
								graph.buildGraph(constrainted);
								graph.setConstraint();
								AngleBisectorConstraint = true;
								Log.v("AngularBisctorConstraint","a");
								((TriangleGraph) graph).setAngular(1, false);
							}else{ 
								if(Math.abs(CommonFunc.distance(new Point(xo,yo), point1.getPoint())-
									CommonFunc.distance(new Point(xo,yo), point3.getPoint())) <7
								&& ((TriangleGraph) graph).getMid(1)){
								//	Log.v("distance1",CommonFunc.distance(new Point(xo,yo), point2.getPoint())+"");
								//	Log.v("distance2",CommonFunc.distance(new Point(xo,yo), point3.getPoint())+"");
									xo = (x1+x3)/2;
									yo = (y1+y3)/2;
									PointUnit constrainted = new PointUnit(xo,yo);
									constrainted.setInLine(true);
									LineUnit line = new LineUnit(point2, constrainted);
									line.setType(4);
									line.setVer(2);
									graph.buildGraph(line);
									graph.buildGraph(constrainted);
									graph.setConstraint();
									MidLineConstraint = true;
									Log.v("Mid","Bitch");
									((TriangleGraph) graph).setMid(1, false);
								}else{
									PointUnit verPoint = new PointUnit(xo,yo);
									verPoint.setMark(2);		//记录一般约束线的顶点
									verPoint.setInLine(true);		//标记
									verPoint.setCommonConstrainted(true);
									LineUnit line = new LineUnit(point2,verPoint);
									line.setType(5);
									line.setVer(2);
									double a = CommonFunc.distance(point1.getPoint(), new Point(xo,yo))/CommonFunc.distance(point3.getPoint(), new Point(xo,yo));
									line.setProportion(a);
									graph.buildGraph(line);
									graph.buildGraph(verPoint);
									graph.setConstraint();
									Constraint = true;
									Log.v("Why","no sign");
								}
							}
						}
						Log.v("nani","2");
						return graph;
					}
			
			if((CommonFunc.distance(Pxpoint1.getPoint(), point3.getPoint()) < twoPointDistance 
					&& CommonFunc.lineDistance(point2.getPoint(), point1.getPoint(), Pxpoint2.getPoint()) < lineDistance)
					|| (CommonFunc.distance(Pxpoint2.getPoint(), point3.getPoint()) < twoPointDistance
					&& CommonFunc.lineDistance(point2.getPoint(), point1.getPoint(), Pxpoint1.getPoint()) < lineDistance)){
						if(kp*k3<(-0.7) && kp*k3>(-2.5) && ((TriangleGraph) graph).getVer(2)){
						xo = (x3*(x2-x1)+(y2-k3*x2-y3)*(y1-y2))/(k3*(y2-y1)+x2-x1);
						yo = k3*(xo-x2)+y2;
						PointUnit constrainted = new PointUnit(xo,yo);
						constrainted.setInLine(true);
						LineUnit line = new LineUnit(point3, constrainted);
						line.setType(1);
						line.setVer(4);
						graph.buildGraph(line);
						graph.buildGraph(constrainted);
						graph.setConstraint();
						VerticalLineCostraint = true;
						Log.v("VerticalLine","verticalLine");
						((TriangleGraph) graph).setVer(2, false);
						}
						else{
							xo = (kp*x3+y2-y3-k3*x2)/(kp-k3);
							yo = k3*(xo-x2)+y2;
							Log.v("end","");
							if(VectorFunc.equalangle(vector5, vector6, vectorpx) && ((TriangleGraph) graph).getAngular(2)){
							//	xo = (Math.sqrt(1+k3*k3)*(k1-k2)-(k1-k3)*Math.sqrt(1+k2*k2))/((y2-k1*x2-y1+k3*x1)*Math.sqrt(1+k2*k2)-(y2-k1*x2-y1+k2*x1)*Math.sqrt(1+k3*k3));
							//	yo = k1*(xo-x2)+y2;
							/*	double tempX = x1 + VectorFunc.returnX(vector1, vector2);
								double tempY = y1 + VectorFunc.returnY(vector1, vector2);
								Log.v("temp",tempX+","+tempY);
								double tempK = (tempY-y1)/(tempX-x1);
								xo = (tempK*x1+y2-y1-k1*x2)/(tempK-k1);
								yo = k1*(xo-x2)+y2;*/
								double distance1 = CommonFunc.distance(point3.getPoint(), point1.getPoint());
								double distance2 = CommonFunc.distance(point3.getPoint(), point2.getPoint());
								double k = distance1/distance2;
								xo = (float) (x1 - vector1.getX()*k/(k+1));
								yo = (float) (y1 - vector1.getY()*k/(k+1));
								Log.v("xo,yo",xo+","+yo);
								PointUnit constrainted = new PointUnit(xo,yo);
								constrainted.setInLine(true);
								LineUnit line = new LineUnit(point3, constrainted);
								line.setType(3);
								line.setVer(4);
								graph.buildGraph(line);
								graph.buildGraph(constrainted);
								graph.setConstraint();
								AngleBisectorConstraint = true;
								Log.v("AngularBisctorConstraint","a");
								((TriangleGraph) graph).setAngular(2, false);
							}else{ 
								if(Math.abs(CommonFunc.distance(new Point(xo,yo), point1.getPoint())-
									CommonFunc.distance(new Point(xo,yo), point2.getPoint())) <7
								&& ((TriangleGraph) graph).getMid(2)){
								//	Log.v("distance1",CommonFunc.distance(new Point(xo,yo), point2.getPoint())+"");
								//	Log.v("distance2",CommonFunc.distance(new Point(xo,yo), point3.getPoint())+"");
									xo = (x2+x1)/2;
									yo = (y2+y1)/2;
									PointUnit constrainted = new PointUnit(xo,yo);
									constrainted.setInLine(true);
									LineUnit line = new LineUnit(point3, constrainted);
									line.setType(4);
									line.setVer(4);
									graph.buildGraph(line);
									graph.buildGraph(constrainted);
									graph.setConstraint();
									MidLineConstraint = true;
									Log.v("Mid","Bitch");
									((TriangleGraph) graph).setMid(2, false);
								}else{
									PointUnit verPoint = new PointUnit(xo,yo);
									verPoint.setMark(4);		//记录一般约束线的顶点
									verPoint.setInLine(true);		//标记
									verPoint.setCommonConstrainted(true);
									LineUnit line = new LineUnit(point3,verPoint);
									line.setType(5);
									line.setVer(4);
									double a = CommonFunc.distance(point1.getPoint(), new Point(xo,yo))/CommonFunc.distance(point2.getPoint(), new Point(xo,yo));
									line.setProportion(a);
									graph.buildGraph(line);
									graph.buildGraph(verPoint);
									graph.setConstraint();
									Constraint = true;
									Log.v("Why","no sign");
								}
							}
						}
						Log.v("nani","3");
						return graph;
					}
			
			Log.v("end","");
			if(!MedianLineConstraint && !VerticalLineCostraint && !AngleBisectorConstraint && !MidLineConstraint && !Constraint){
				Log.v("直线与三角形没有约束", "直线与三角形没有约束");
				return null;
			}
			
			Log.v("nani","4");
			return null;
		}
		MedianLineConstraint = false;
		VerticalLineCostraint = false;
		AngleBisectorConstraint = false;
		MidLineConstraint = false;
		Constraint = false;
		Log.v("直线与三角形没有约束", "直线与三角形没有约束");
		return null;
	}

	//垂线
	public void changeToVerticalLine(Graph graph){
		List<GUnit> units = graph.getGraph();
		int size = units.size();
		//获取刚约束好的约束线
		LineUnit constraintLine;
		constraintLine = (LineUnit) units.get(size - 2);
		
		PointUnit point1 = null,point2 = null,point3 = null;
		switch(constraintLine.getVer()){					//根据直线图元所存的顶点索引找出关联点
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
		
		xo = (x1*(x2-x3)+(y2-k*x2-y1)*(y3-y2))/(k*(y2-y3)+x2-x3);
		yo = k*(xo-x2)+y2;
		
		//对当前图形约束状态进行清空
		switch(constraintLine.getType()) {
		case 1:
			((TriangleGraph) graph).setVer(constraintLine.getVer()/2, true);
			break;
		case 3:
			((TriangleGraph) graph).setAngular(constraintLine.getVer()/2, true);
			break;
		case 4:
			((TriangleGraph) graph).setMid(constraintLine.getVer()/2, true);
			break;
		default:
				break;
		}
		//如果没有垂线
		if(((TriangleGraph) graph).getVer(constraintLine.getVer()/2)) {
			constraintLine.getEndPointUnit().setX(xo);
			constraintLine.getEndPointUnit().setY(yo);
			constraintLine.setType(1);
			((TriangleGraph) graph).setVer(constraintLine.getVer()/2, false);
		} else {
			
		}
	}
	
	//角平分线
	public void changeToAngleBisector(Graph graph){
		List<GUnit> units = graph.getGraph();
		int size = units.size();
		//获取刚约束好的约束线
		LineUnit constraintLine;
		constraintLine = (LineUnit) units.get(size - 2);
		
		PointUnit point1 = null,point2 = null,point3 = null;
		switch(constraintLine.getVer()){					//根据直线图元所存的顶点索引找出关联点
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
		
		//两腰的长度
		double distance1 = CommonFunc.distance(point1.getPoint(), point2.getPoint());
		double distance2 = CommonFunc.distance(point1.getPoint(), point3.getPoint());
		
		//两腰的比值a
		double a = distance1/distance2;
		
		//底边向量，指向为由point2->point3
		Point vector = new Point(x3-x2,y3-y2);
		
		xo = (float) (x2 + vector.getX()*a/(a+1));
		yo = (float) (y2 + vector.getY()*a/(a+1));
		
		//对当前图形约束状态进行清空
		switch(constraintLine.getType()) {
		case 1:
			((TriangleGraph) graph).setVer(constraintLine.getVer()/2, true);
			break;
		case 3:
			((TriangleGraph) graph).setAngular(constraintLine.getVer()/2, true);
			break;
		case 4:
			((TriangleGraph) graph).setMid(constraintLine.getVer()/2, true);
			break;
		default:
				break;
		}
		
		//如果没有角平分线
		if(((TriangleGraph) graph).getAngular(constraintLine.getVer()/2)) {
			constraintLine.getEndPointUnit().setX(xo);
			constraintLine.getEndPointUnit().setY(yo);
			constraintLine.setType(3);
			((TriangleGraph) graph).setAngular(constraintLine.getVer()/2, false);
		} else {
			
		}
	}
	
	//中线
	public void changeToMidLine(Graph graph){
		List<GUnit> units = graph.getGraph();
		int size = units.size();
		//获取刚约束好的约束线
		LineUnit constraintLine;
		constraintLine = (LineUnit) units.get(size - 2);
		
		PointUnit point1 = null,point2 = null,point3 = null;
		switch(constraintLine.getVer()){					//根据直线图元所存的顶点索引找出关联点
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

		xo = (x2+x3)/2;
		yo = (y2+y3)/2;
		
		//对当前图形约束状态进行清空
		switch(constraintLine.getType()) {
		case 1:
			((TriangleGraph) graph).setVer(constraintLine.getVer()/2, true);
			break;
		case 3:
			((TriangleGraph) graph).setAngular(constraintLine.getVer()/2, true);
			break;
		case 4:
			((TriangleGraph) graph).setMid(constraintLine.getVer()/2, true);
			break;
		default:
				break;
		}
		
		//如果没有中线
		if(((TriangleGraph) graph).getMid(constraintLine.getVer()/2)) {
			constraintLine.getEndPointUnit().setX(xo);
			constraintLine.getEndPointUnit().setY(yo);
			constraintLine.setType(4);
			((TriangleGraph) graph).setMid(constraintLine.getVer()/2, false);
		} else {
			
		}
	}

	public void changeToCommonLine(Graph graph){
		List<GUnit> units = graph.getGraph();
		int size = units.size();
		//获取刚约束好的约束线
		LineUnit constraintLine;
		constraintLine = (LineUnit) units.get(size - 2);
		PointUnit verPoint = (PointUnit) units.get(size - 1);
		//对当前图形约束状态进行清空
		switch(constraintLine.getType()) {
		case 1:
			((TriangleGraph) graph).setVer(constraintLine.getVer()/2, true);
			break;
		case 3:
			((TriangleGraph) graph).setAngular(constraintLine.getVer()/2, true);
			break;
		case 4:
			((TriangleGraph) graph).setMid(constraintLine.getVer()/2, true);
			break;
		default:
				break;
		}
		
		PointUnit point1 = null,point2 = null,point3 = null;
		switch(constraintLine.getVer()){					//根据直线图元所存的顶点索引找出关联点
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
		
		double a = CommonFunc.distance(point2, verPoint) / CommonFunc.distance(point3, verPoint);
		constraintLine.setProportion(a);
		verPoint.setCommonConstrainted(true);
		constraintLine.setType(5);
	}
}
