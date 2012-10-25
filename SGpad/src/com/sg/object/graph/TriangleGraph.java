package com.sg.object.graph;

import java.io.Serializable;
import java.util.List;

import com.sg.logic.common.CommonFunc;
import com.sg.logic.strategy.LineStrategy;
import com.sg.object.Point;
import com.sg.object.unit.GUnit;
import com.sg.object.unit.PointUnit;
import com.sg.object.unit.LineUnit;
import com.sg.property.tools.Painter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class TriangleGraph extends Graph implements Serializable{
	
	private boolean[] Vertical = new boolean[3];  //对于一个顶点只进行一次高的约束，约束后将bool型变量改为false
	private boolean[] Median = new boolean[3];   //中位线
	private boolean[] Angular = new boolean[3];	//角平分线
	private boolean[] Mid = new boolean[3];		//中线
	
	/*
	 * 3个点，3条约束关系
	 * */
	public TriangleGraph() {
		isClosed = true;
		translationStratery = new LineStrategy();  //选择线变换策略
		for(int i=0; i<3; i++){
			Vertical[i]=true;
			Median[i]=true;
			Angular[i]=true;
			Mid[i]=true;
		}
	}
	
	public TriangleGraph(List<GUnit> units) {
		isClosed = true;
		translationStratery = new LineStrategy();  //选择线变换策略
		
		for(GUnit unit : units) {
			buildGraph(unit);
		} 
		for(int i=0; i<3; i++){
			Vertical[i]=true;
			Median[i]=true;
			Angular[i]=true;
			Mid[i]=true;
		}
	}

	
	@Override
	public void draw(Canvas canvas, Painter painter) {
		//super.draw(canvas, painter);
		if(this.isGraphConstrainted){
			List<GUnit> units = graph;
			int i = 0,j = 2,k = 4;
			for(GUnit unit : units){
				if(unit instanceof LineUnit){
					if(((LineUnit)unit).getType() != 0){
						switch(((LineUnit)unit).getVer()){    //通过索引寻找顶点i，以及底边端点j,k
							case 0 :{
								i = 0;
								j = 2;
								k = 4;
								break;
							}
							case 2:{
								i = 2;
								j = 0;
								k = 4;
								break;
							}
							case 4:{
								i = 4;
								j = 0;
								k = 2;
								break;
							}
						}
						PointUnit point1 = (PointUnit)units.get(i);     //通过索引对变量赋值，point1为三角形顶点，
						PointUnit point2 = (PointUnit)units.get(j);	 //point2,point3分别为底边顶点
						PointUnit point3 = (PointUnit)units.get(k);
						
						Painter specialPainter = new Painter(Color.BLUE, 2);
						
						switch(((LineUnit)unit).getType()){
							case 1:{			//画图结构temp为垂足。
								PointUnit temp = ((LineUnit)unit).getEndPointUnit();
								this.drawRightAngle(canvas, temp, point1, point2, specialPainter.getPaint());
								canvas.drawLine(temp.getX(), temp.getY(), point2.getX(), point2.getY(), specialPainter.getPaint());
								break;
							}
							case 2:{		//中位线只需标示中位线的端点。
								//Point temp1 = new Point((point1.getX()+point2.getX())/2, (point1.getY()+point2.getY())/2);
								//Point temp2 = new Point((point1.getX()+point3.getX())/2, (point1.getY()+point3.getY())/2);
								//paint.setStrokeWidth(5);
								//canvas.drawPoint((point1.getX()+point2.getX())/2, (point1.getY()+point2.getY())/2, paint);
								//canvas.drawPoint((point1.getX()+point3.getX())/2, (point1.getY()+point3.getY())/2, paint);
								//paint.setStrokeWidth(2);
								break;
							}
							case 3:{		//temp为角平分线约束点
								PointUnit temp = ((LineUnit)unit).getEndPointUnit();
								this.drawEqualAngle(canvas, point1, temp, point2, specialPainter.getPaint());
								this.drawEqualAngle(canvas, point1, temp, point3, specialPainter.getPaint());
								break;
							}
							case 4:{		//temp为底边中点，只需标示一点
								//paint.setStrokeWidth(5);
								//canvas.drawPoint((point3.getX()+point2.getX())/2, (point3.getY()+point2.getY())/2, paint);
								//paint.setStrokeWidth(2);
								break;
							}
							case 5:{		//同上，只需标示一点
								//paint.setStrokeWidth(5);
								//Point temp = (((LineUnit)unit).getEndPointUnit()).getPoint();
								//canvas.drawPoint(temp.getX(), temp.getY(), paint);
								//paint.setStrokeWidth(2);
							}
							
							//对于中位线，中线，一般约束直线的表示点建议用不一样的颜色易于区别
						}
					}
				}
			}
		}
		super.draw(canvas, painter);
	}
	

	@Override
	public void move(float mx, float my) {
		// TODO Auto-generated method stub
		
	}

	public void setVer(int i, boolean state){
		Vertical[i]=state;
	}
	
	public boolean getVer(int i){
		return Vertical[i];
	}
	
	public void setMedian(int i, boolean state){
		Median[i]=state;
	}
	
	public boolean getMedian(int i){
		return Median[i];
	}
	
	public void setAngular(int i, boolean state){
		Angular[i]=state;
	}
	
	public boolean getAngular(int i){
		return Angular[i];
	}
	
	public void setMid(int i, boolean state){
		Mid[i]=state;
	}
	
	public boolean getMid(int i){
		return Mid[i];
	}
	
	@Override
	public Graph clone() {
		Graph temp = null; 
		temp = super.clone();
		((TriangleGraph)temp).Vertical = Vertical.clone();
		((TriangleGraph)temp).Angular = Angular.clone();
		((TriangleGraph)temp).Median = Median.clone();
		((TriangleGraph)temp).Mid = Mid.clone();
		return temp;
	}
	/*
	@Override
	public boolean isInGraph(Point point) {
		// TODO Auto-generated method stub
		PointUnit point1=(PointUnit) this.graph.get(3);
		PointUnit point2=(PointUnit) this.graph.get(4);
		PointUnit point3=(PointUnit) this.graph.get(5);
		double distance1 = CommonFunc.distance(point1.getPoint(), point2.getPoint());
		double distance2 = CommonFunc.distance(point1.getPoint(), point3.getPoint());
		double distance3 = CommonFunc.distance(point2.getPoint(), point3.getPoint());
		double height = CommonFunc.lineDistance(point1.getPoint(), point2.getPoint(), point3.getPoint());
		double height1 = CommonFunc.lineDistance(point1.getPoint(), point2.getPoint(), point);
		double height2 = CommonFunc.lineDistance(point1.getPoint(), point3.getPoint(), point);
		double height3 = CommonFunc.lineDistance(point2.getPoint(), point3.getPoint(), point);
		double Area = distance1 * height /2;
		double Area1 = distance1 * height1 / 2;
		double Area2 = distance2 * height2 / 2;
		double Area3 = distance3 * height3 / 2;
		Log.v("Area",Area + "=" +Area1+"+"+Area2+"+"+Area3);
		if(( Area1 + Area2 + Area3 )/Area < 1.01 ){
			return true;
		}else{
			return false;
		}
	}
	*/

}
