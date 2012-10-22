package com.sg.object.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sg.logic.common.CommonFunc;
import com.sg.logic.common.VectorFunc;
import com.sg.logic.strategy.TranslationStratery;
import com.sg.object.Point;
import com.sg.object.unit.*;
import com.sg.property.tools.Painter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

public abstract class Graph implements Cloneable, Serializable {
	/*
	 * 图形类型
	 * 1.点图
	 * 2.线图
	 * 3.三角形
	 * 4.四边形
	 * 5.其它图形
	 * */
	protected int type;
	
	/*
	 * 图形是否选中
	 * */
	protected boolean checked;
	
	/*
	 * 每个图形是由一个或多个图元构成
	 * */
	//线性图形按点-线-点按顺序排放
	protected List<GUnit> graph;
	
	/*
	 * 策略模式
	 * 实现变换接口来完成变换
	 * */
	protected boolean isClosed; //图形是否闭合    // protected boolean isConstrainted
	
	protected long id;
	
	protected TranslationStratery translationStratery;      //在具体类中实例化

	protected boolean isGraphConstrainted;  //闭合图形是否与直线有约束
	
	public Graph() {
		id = new Date().getTime();
		checked = false;
		isClosed = false;
		graph = new ArrayList<GUnit>();
		isGraphConstrainted = false;
		//translationStratery = new LineStrategy();
	}
	
	public void draw(Canvas canvas, Painter painter){
		int size = graph.size();
		List<PointUnit> points = new ArrayList<PointUnit>();
		for(int num = 0; num < size; num++) {
			GUnit unit = graph.get(num);
			if(unit instanceof PointUnit){
				points.add((PointUnit) unit);
			}
			unit.draw(canvas, painter);
		}
		//画直角，等腰标记
		size = points.size(); //点元数
		
		Painter specialPainter = new Painter(Color.BLUE, 2);
		
		for(int num = 0; num < size; num++){
			PointUnit pointUnit = points.get(num);			
			if(pointUnit.isRightAngle() || pointUnit.isEqualAngle()){
				PointUnit other1;
				PointUnit other2;
				if((num - 1) < 0){
					other1 = points.get(size - 1);  //最后一个点
				}else{
					other1 = points.get(num - 1);
				}
				if((num + 1) == size){
					other2 = points.get(0);
				}else{
					other2 = points.get(num + 1);
				}
				//标记直角
				if(pointUnit.isRightAngle()){
					drawRightAngle(canvas, pointUnit, other1, other2, specialPainter.getPaint());
				}else{//标记等边
					drawEqualAngle(canvas, pointUnit, other1, other2, specialPainter.getPaint());
				}
			}
		}
		/*
		for(GUnit unit : graph) {
			unit.draw(canvas, painter);
		}
		*/
	}
	
	//标记直角
	protected void drawRightAngle(Canvas canvas, PointUnit special, PointUnit other1, PointUnit other2, Paint paint){
		double distance1 = CommonFunc.distance(special, other1);
		double distance2 = CommonFunc.distance(special, other2);
		double length;
		//辅助线长度取短的那条的1/5
		if(distance1 < distance2){
			length = distance1 / 5;
		}else{
			length = distance2 / 5;
		}
		if(length < 1){
			return;
		}
		Point first = CommonFunc.markPoint(other1, special, length);
		Point second = CommonFunc.markPoint(other2, special, length);
		Point temp = VectorFunc.subtract(special.getPoint(), first);
		float x = temp.getX() + second.getX();        //求出第三点坐标
		float y = temp.getY() + second.getY();
		canvas.drawLine(first.getX(), first.getY(), x, y, paint);
		canvas.drawLine(x, y, second.getX(), second.getY(), paint);
	}
	
	protected void drawEqualAngle(Canvas canvas, PointUnit special, PointUnit other1, PointUnit other2, Paint paint){
		double distance1 = CommonFunc.distance(special, other1);
		double distance2 = CommonFunc.distance(special, other2);
		double length;
		//辅助线长度取短的那条的1/5
		if(distance1 < distance2){
			length = distance1 / 5;
		}else{
			length = distance2 / 5;
		}
		if(length < 1){
			return;
		}
		//Point first = CommonFunc.markPoint(other1, special, length);
		//Point second = CommonFunc.markPoint(other2, special, length);
		Point point = special.getPoint();
		float starX = (float) (point.getX() - length);            //弧线矩形的左上角点和右下角点
		float starY = (float) (point.getY() - length);
		float endX = (float) (point.getX() + length);
		float endY = (float) (point.getY() + length);
		RectF oval=new RectF(starX, starY, endX, endY);
		
		Point vector1 = VectorFunc.subtract(point, other1.getPoint());
		Point vector2 = VectorFunc.subtract(point, other2.getPoint());
		
		float startAngle, sweepAngle;  //起始角，扫过的角度
		double cos = VectorFunc.direction(vector1, vector2);
		double angle = Math.acos(cos);
		sweepAngle = (float) (angle * 180 / Math.PI);
		
		float tempx1 = vector1.getX();
		float tempx2 = vector2.getX();
		float tempy1 = vector1.getY();
		float tempy2 = vector2.getY();
		
		if(tempy1 <= 0 && tempy2 <= 0){   //两条向量都在x轴上方
			double cos1 = VectorFunc.direction(vector1, new Point(1, 0));
			double angle1 = Math.acos(cos1);
			double cos2 = VectorFunc.direction(vector2, new Point(1, 0));
			double angle2 = Math.acos(cos2);
			if(angle1 > angle2){
				startAngle = (float) (360 - angle1 * 180 / Math.PI);
			}else{
				startAngle = (float) (360 - angle2 * 180 / Math.PI);
			}
		}else{
			if(tempy1 >= 0 && tempy2 >= 0){   //两条向量都在x轴下方
				double cos1 = VectorFunc.direction(vector1, new Point(1, 0));
				double angle1 = Math.acos(cos1);
				double cos2 = VectorFunc.direction(vector2, new Point(1, 0));
				double angle2 = Math.acos(cos2);
				if(angle1 > angle2){
					startAngle = (float) (angle2 * 180 / Math.PI);
				}else{
					startAngle = (float) (angle1 * 180 / Math.PI);
				}
			}else{
				double cos1 = VectorFunc.direction(vector1, new Point(1, 0));
				double angle1 = Math.acos(cos1);
				double cos2 = VectorFunc.direction(vector2, new Point(1, 0));
				double angle2 = Math.acos(cos2);
				if(angle1 + angle2 > Math.PI){
					if(tempy1 >= 0){
						startAngle = (float) (angle1 * 180 / Math.PI);
					}else{
						startAngle = (float) (angle2 * 180 / Math.PI);
					}
				}else{
					if(tempy1 <= 0){
						startAngle = (float) (360 - angle1 * 180 / Math.PI);
					}else{
						startAngle = (float) (360 - angle2 * 180 / Math.PI);
					}
				}
			}
		}
		canvas.drawArc(oval, startAngle, sweepAngle, false, paint);
	}
	
	public abstract void move(float mx, float my);
	
	//判断点是否在直线上
	public boolean isInGraph(Point point) {
		// TODO Auto-generated method stub
		for(GUnit unit : graph){
			if(unit.isInUnit(point)){
				return true;
			}
		}
		return false;
	}
	
	/*
	 * 平移，通过平移矩阵，对图形中的各个图元进行变换，图元的变换：对关键的平移
	 * */
	public void translate(Graph graph, float[][] transMatrix) {
		translationStratery.translate(graph, transMatrix);
	} 
	
	/*
	 * 伸缩，通过伸缩矩阵操作
	 * */
	public void scale(Graph graph, float[][] scaleMatrix) {;
		translationStratery.scale(graph, scaleMatrix);
	}
	
	/*
	 * 旋转，通过旋转矩阵计算操作
	 * */
	public void rotate(Graph graph, float[][] rotateMatrix) {
		translationStratery.rotate(graph, rotateMatrix);
	}
	
	/*
	 * 添加图元来构建图形
	 * */
	public void buildGraph(GUnit unit) {
		if(!graph.contains(unit)) {
			graph.add(unit);
		}
	}
	
	public void buildGraph(int index, GUnit unit){
		if(!graph.contains(unit)) {
			graph.add(index, unit);
		}
	}
	
	public void destory() {
		graph.clear();
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public long getID(){
		return id;
	}
	
	public void setID(long id){
		this.id = id;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	public boolean isClosed(){
		return isClosed;
	}
	
	public void setIsClosed(boolean isClosed){
		this.isClosed = isClosed;
	}

	public List<GUnit> getGraph() {
		return graph;
	}

	public void setGraph(List<GUnit> graph) {
		this.graph = graph;
	}
	
	public void setConstraint(){
		isGraphConstrainted = true;
	}
	
	public void setEqualAngleToF(){
		for(GUnit unit : graph){
			if(unit instanceof PointUnit){
				((PointUnit)(unit)).setEqualAngle(false);
			}
		}
	}
	
	public void setRightAngleToF(){
		for(GUnit unit : graph){
			if(unit instanceof PointUnit){
				((PointUnit)(unit)).setRightAngle(false);
			}
		}
	}
	
	public boolean getConstraint(){
		return isGraphConstrainted;
	}
	
	public Graph clone(){
		Graph temp = null;  
        try {  
            temp = (Graph) super.clone();
            temp.graph = new ArrayList<GUnit>();
            for(GUnit unit : this.graph){
            	temp.graph.add(unit.clone());
            	/*
            	if(unit instanceof PointUnit){
            		Log.v("id", ((PointUnit) unit).getID() + "");
            		temp.graph.add(((PointUnit)unit).clone());
            	}else{
            		if(unit instanceof LineUnit){
            			 temp.graph.add(((LineUnit)unit).clone());
            		}else{
            			
            		}
            	}
            	*/
            }
            for(GUnit unit : temp.graph){
            	if(unit instanceof LineUnit){
            		for(GUnit gunit : temp.graph){
            			if(gunit instanceof PointUnit){            //保证clone后线元于点元保持约束
            				if(((PointUnit) gunit).getID() == ((LineUnit)unit).getStartPointUnit().getID()){
            					((LineUnit)unit).setStartPointUnit((PointUnit) gunit);
            					Log.v("id", ((PointUnit) gunit).getID() + "");
            				}
            				if(((PointUnit) gunit).getID() == ((LineUnit)unit).getEndPointUnit().getID()){
            					((LineUnit)unit).setEndPointUnit((PointUnit) gunit);
            					Log.v("id", ((PointUnit) gunit).getID() + "");
            				}
            			}
            		}
            	}
            }
        } catch (CloneNotSupportedException e) {  
            e.printStackTrace();  
        }  
        return temp; 
	}
}
