package com.sg.transformation.recognizer;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.sg.logic.common.CommonFunc;
import com.sg.object.graph.Graph;
import com.sg.object.graph.LineGraph;
import com.sg.object.graph.RectangleGraph;
import com.sg.object.graph.TriangleGraph;
import com.sg.object.unit.GUnit;
import com.sg.object.unit.LineUnit;
import com.sg.object.unit.PointUnit;
import com.sg.property.common.ThresholdProperty;

//图形规整
public class Regulariser {
	
	final private double PRIVIOUS = 0.05;	//判断直线是否相等		
	final private double IS_PARALLEL_EDGES = 10 ;  //判断是否平行
	final private double IS_VERTICAL = 5;   //判断是否垂直
	
	private static Regulariser instance = new Regulariser();
	
	public Regulariser() {
		
	}
	
	public static Regulariser getInstance() {
		return instance;
	}
	
	public Graph regularise(List<Graph> graphList, Graph curGraph) {

		double minDist = ThresholdProperty.TWO_POINT_IS_CONSTRAINTED;
		Graph graph = null;
		
		//如果图形选中，非闭合的直线图形,判断是否可以构造闭合图形
		if(curGraph.isChecked() && curGraph instanceof LineGraph && !curGraph.isClosed()){

			Graph temp = curGraph.clone();
			List<GUnit> units = temp.getGraph();
			int num = units.size();
			if(num > 6){
				
				PointUnit firstPoint = (PointUnit) units.get(0);
				PointUnit lastPoint = (PointUnit) units.get(num - 1);
				double distance = CommonFunc.distance(firstPoint, lastPoint);
				
				if(distance < minDist) {

					graphList.remove(curGraph);
					LineUnit lastLine = (LineUnit) units.get(num - 2);  //最后一个线元
					lastLine.setEndPointUnit(firstPoint);
					units.remove(lastPoint);
					//curGraph.setIsClosed(true);
					
					if(units.size() == 6){
						//重新构造成三角形
						//graphList.remove(curGraph);
						graph = new TriangleGraph(units);
						graph.setChecked(true);
						graph.setID(temp.getID());
						graphList.add(graph);
					}else{
						if(units.size() == 8){
							//重新构造四边形
							//graphList.remove(curGraph);
							graph = new RectangleGraph(units);
							graph.setChecked(true);
							graph.setID(temp.getID());
							graphList.add(graph);
						}else{
							graph = temp;
							graph.setIsClosed(true);
							graphList.add(graph);
							/*
							graph = new LineGraph();
							((LineGraph)graph).buildLineGraph(units);
							graph.setChecked(true);
							graph.setIsClosed(true);
							graph.setID(curGraph.getID());
							graphList.add(graph);
							*/
						}
					}
				}
			}
		}
		if(graph == null)
			graph = curGraph;
		if(graph instanceof LineGraph){
			if(graph.isClosed() && isPolygon(graph)){
				regularPolygonHolotactic(graph);  //多边形规整
			}else if(!graph.isClosed()){
				if(graph.getGraph().size() > 3){
					brokenLineHolotactic(graph);  //折线规整
				}
			}else{
				graph.setEqualAngleToF();
		    	graph.setRightAngleToF();}
		}else{
			if(graph instanceof TriangleGraph){
				triangleHolotactic(graph);
			}else if(graph instanceof RectangleGraph && isPolygon(graph)){
				rectangleHolotactic(graph);
			}else{
				graph.setEqualAngleToF();
				graph.setRightAngleToF();
				}
		}
		return graph;
	}
	
	/*三角形规整：
	 * T1：等腰三角形——两条边近似相等(两直线长差绝对值比例约为0.05是近似相等)
     * T2：直角三角形——两条边近似相等
     * T3：等边三角形——三条边近似相等
     * T4：等腰直角三角形——同时满足T1和T3
	 */
	private void triangleHolotactic(Graph graph){
		
		PointUnit[] temp = new PointUnit [ 3 ];
		temp[0] = (PointUnit) graph.getGraph().get(0);
		temp[1] = (PointUnit) graph.getGraph().get(2);
		temp[2] = (PointUnit) graph.getGraph().get(4);
		for(int i = 0 ; i < 3 ; i++){
				temp[i].setEqualAngle(false);
				temp[i].setRightAngle(false);
		}
	
		double distance1 = CommonFunc.distance(temp[0].getPoint(),temp[1].getPoint()); 
		double distance2 = CommonFunc.distance(temp[1].getPoint(),temp[2].getPoint());
		double distance3 = CommonFunc.distance(temp[0].getPoint(),temp[2].getPoint());
			
		boolean T1 = Math.abs(distance1 - distance2 )/distance2 < PRIVIOUS||Math.abs(distance1 - distance3)/distance3 < PRIVIOUS||Math.abs(distance2 - distance3 )/distance3 < PRIVIOUS;
		boolean T2 = Math.abs((CommonFunc.square(distance1)+CommonFunc.square(distance2))-CommonFunc.square(distance3))/CommonFunc.square(distance3) < PRIVIOUS
					||Math.abs((CommonFunc.square(distance1)+CommonFunc.square(distance3)) - CommonFunc.square(distance2))/CommonFunc.square(distance2) < PRIVIOUS
					||Math.abs((CommonFunc.square(distance2)+CommonFunc.square(distance3)) - CommonFunc.square(distance1))/CommonFunc.square(distance1) < PRIVIOUS;
		boolean T3 =Math.abs(distance1 - distance2)/distance2 < PRIVIOUS &&Math.abs( distance1 - distance3) /distance3 < PRIVIOUS && Math.abs(distance2-distance3) /distance3 < PRIVIOUS;
			//等边三角形规整---此处不用此方法
			/*1、求已知线段的斜角：tgα=(y1-y2)/(x1-x2) 
	         *2、求已知线段的长度：L=√((y1-y2)^2+(x1-x2)^2)
	         *3、求第三点的坐标：
	         *x3=x2+L*cos(α+60)；y3=y2+L*sin(α+60)
	         */
		if( T3 ){
			regularPolygonHolotactic(graph);
			Log.v("等边三角形","");
		}
		else{
			//注：如果是等腰直角三角形的话就无需进行等腰或直角的判断
			
			/*
			 * 等腰三角形的规整
			 */
			if(T1&&T2 ){
				if( Math.abs(distance1 - distance2) / distance2 < PRIVIOUS && Math.abs((CommonFunc.square(distance1) + CommonFunc.square(distance2))
						- CommonFunc.square(distance3)) / CommonFunc.square(distance3) < PRIVIOUS ){
					temp[1].setRightAngle(true);
					changeToEquation(temp[0],temp[1],temp[2]);
                    Log.v("等腰直角三角形1","");
				}
				
				if(Math.abs((distance1-distance3)) / distance3 < PRIVIOUS && Math.abs((CommonFunc.square(distance1) + CommonFunc.square(distance3))
						- CommonFunc.square(distance2)) / CommonFunc.square(distance2) < PRIVIOUS ){
					temp[0].setRightAngle(true);
					changeToEquation(temp[1],temp[0],temp[2]);
                    Log.v("等腰直角三角形2","");
				}
				if(Math.abs(distance2 - distance3 ) / distance3 < PRIVIOUS && Math.abs((CommonFunc.square(distance2) + CommonFunc.square(distance3)) 
						- CommonFunc.square(distance1))/CommonFunc.square(distance1) < PRIVIOUS ){
					temp[2].setRightAngle(true);
					changeToEquation(temp[1] , temp[2] , temp[0]);
					Log.v("等腰直角三角形 3","");
				}
			}
			else{
		    /*
		     * 直角三角形的规整
		     *计算方法，从非直角定点应垂线，垂足即为所求的直角点
		     *令k1为一直角边的斜率。--------------为了统一做法，该方法在实际中没用到。而且该方法有个缺点就是要用到斜率，当斜率为0或者无穷大时会出现错误。
		     */
			if(T2){
				if(Math.abs((CommonFunc.square(distance1)+CommonFunc.square(distance2)) - CommonFunc.square(distance3)) / 
						CommonFunc.square(distance3) < PRIVIOUS){
					changeToApeak(temp[0],temp[1],temp[2]);
					Log.v("直角三角形1",",");
				}
				if(Math.abs((CommonFunc.square(distance1) + CommonFunc.square(distance3)) - CommonFunc.square(distance2)) / 
						CommonFunc.square(distance2) < PRIVIOUS){
					changeToApeak(temp[1],temp[0],temp[2]);
					Log.v("直角三角形2",",");
				}
				if(Math.abs((CommonFunc.square(distance2) + CommonFunc.square(distance3)) - CommonFunc.square(distance1)) / 
						CommonFunc.square(distance1) < PRIVIOUS){
					changeToApeak(temp[1],temp[2],temp[0]);
					Log.v("直角三角形3",",");
				}
			}
			
			/*等腰三角形的规整
			 * 利用垂直平分线上的点到直线两端的距离相等这一性质
			 */
			if(T1){
				if(Math.abs(distance1 - distance2) / distance2 < PRIVIOUS){
					changeToEquation(temp[0] , temp[1] , temp[2]);
					Log.v("等腰三角形1","");
				}
				if(Math.abs(distance1 - distance3) / distance3 < PRIVIOUS){
					changeToEquation(temp[1] , temp[0] , temp[2]);
					Log.v("等腰三角形2","");
				}
				
				if(Math.abs(distance2 - distance3) / distance3 < PRIVIOUS){
					changeToEquation(temp[1],temp[2],temp[0]);
					Log.v("等腰三角形3","");
				}
				}
			}
		}
			Log.v("三角形规整","三角形规整");
	}
	
	/*四边形规整
	 * 以下是规整的根据。
	 *S1：梯形——只有一组对边接近平行
     *S2：平行四边形——两组对边接近平行
     *S3：直角梯形——满足S1且有一组邻边近似垂直
     *S4：等腰梯形——满足S1且有一组对边近似相等
     *S5：矩形——满足S2且有一组邻边近似垂直
     *S6：棱形——满足S2且有一组邻边近似相等
     *S7：正方形——满足S5和S6
     */
	private void rectangleHolotactic(Graph graph){
		graph.setEqualAngleToF();
		graph.setRightAngleToF();
		PointUnit[] temp = new PointUnit [4];
		temp[0] = (PointUnit) graph.getGraph().get(0);
		temp[1] = (PointUnit) graph.getGraph().get(2);
		temp[2] = (PointUnit) graph.getGraph().get(4);
		temp[3] = (PointUnit) graph.getGraph().get(6);
		
			//四边形的四条边
			double distance1 = CommonFunc.distance(temp[0].getPoint(),temp[1].getPoint()); 
			double distance2 = CommonFunc.distance(temp[1].getPoint(),temp[2].getPoint());
			double distance3 = CommonFunc.distance(temp[2].getPoint(),temp[3].getPoint());
			double distance4 = CommonFunc.distance(temp[3].getPoint(),temp[0].getPoint());
			
			//四边形的对角线
			double catercorner1 = CommonFunc.distance(temp[0].getPoint(),temp[2].getPoint());
			double catercorner2 = CommonFunc.distance(temp[1].getPoint(),temp[3].getPoint());
			
			//四个角的余弦值
			double cos1 = (CommonFunc.square(distance1)+CommonFunc.square(distance4)-CommonFunc.square(catercorner2))/(2*distance1*distance4);
			double cos2 = (CommonFunc.square(distance1)+CommonFunc.square(distance2)-CommonFunc.square(catercorner1))/(2*distance1*distance2);
			double cos3 = (CommonFunc.square(distance2)+CommonFunc.square(distance3)-CommonFunc.square(catercorner2))/(2*distance2*distance3);
			double cos4 = (CommonFunc.square(distance3)+CommonFunc.square(distance4)-CommonFunc.square(catercorner1))/(2*distance3*distance4);
			
			//四个角的角度
			double angle1 = Math.acos(cos1)/Math.PI * 180;
			double angle2 = Math.acos(cos2)/Math.PI * 180;
			double angle3 = Math.acos(cos3)/Math.PI * 180;
			double angle4 = Math.acos(cos4)/Math.PI * 180;
			
			//final double IS_PARALLEL_EDGES = 20 ;
			//final double IS_VERTICAL = 5;
			//final double PTIVIOUS = 0.05;
			
			Log.v("cos12",	cos1+ ","+cos2);
			Log.v("cos34",	cos3+ ","+cos4);
			Log.v("angle12",	angle1+ ","+angle2);
			Log.v("angle34",	angle3+ ","+angle4);
			Log.v("distance12",	distance1+ ","+distance2);
			Log.v("distance34",	distance3+ ","+distance4);
			
			/*
			 * 判断两组对边是否同时平行如果是就为平四边形
			 * 如果否则判断是否有一组边平行是则进行梯形规整。
			 * 如果以上情况都不符合这判断为普通四边形不经心任何规整
			 */
			if( Math.abs(angle1 - (180.0 - angle2)) < IS_PARALLEL_EDGES && Math.abs(angle2 - (180 - angle3)) < IS_PARALLEL_EDGES ){
				/*
				 * 平行四边形规整：
				 * 矩形:有一个角接近直角，就是矩形,正方形归为矩形的一种,四边接近相等为正方形。
				 * 菱形:没有直角但是四条边接近相等为菱形。
				 * 普通四边形
				 */
				Log.v("平行四边形",	1+ ","+1);
				if(Math.abs(angle1 - 90) < IS_VERTICAL || Math.abs(angle2 - 90) < IS_VERTICAL || Math.abs(angle3 - 90) < IS_VERTICAL || Math.abs(angle4 - 90) < IS_VERTICAL){  //矩形
					/*
					 * 矩形分为正方形和普通矩形。
					 */
					if(Math.abs(distance1 - distance2) / distance2 < PRIVIOUS){//正方形
						//changeToEquation(temp[0] , temp[1] , temp[2] , distance1);
						//temp[3].setX((int)(Math.round(temp[0].getX()+temp[2].getX()-temp[1].getX())));
						//temp[3].setY((int)(Math.round(temp[0].getY()+temp[2].getY()-temp[1].getY())));
						regularPolygonHolotactic(graph);//调用多边形规整。
						Log.v("正方形",	1+ ","+1);
					}else{
						/*
						 * 规整方法：取矩形的三个点进行直角规整然后利用向量相加的方法计算第四点。
						 */
						changeToApeak(temp[0] , temp[1] , temp[2]);
						temp[3].setX((int)(Math.round(temp[0].getX()+temp[2].getX()-temp[1].getX())));
						temp[3].setY((int)(Math.round(temp[0].getY()+temp[2].getY()-temp[1].getY())));
						Log.v("普通矩形",	1+ ","+1);
					}
					temp[0].setRightAngle(true);
					temp[1].setRightAngle(true);
					temp[2].setRightAngle(true);
					temp[3].setRightAngle(true);
				}else if( Math.abs(distance1 - distance2) / distance2 < PRIVIOUS){//菱形
					/*
					 * 调用等边规整然后利用向量相加的方法计算第四点。
					 */
					temp[1].setEqualAngle(true);
					changeToEquation(temp[0] , temp[1] , temp[2]);
					temp[3].setX((int)(Math.round(temp[0].getX()+temp[2].getX()-temp[1].getX())));
					temp[3].setY((int)(Math.round(temp[0].getY()+temp[2].getY()-temp[1].getY())));
					temp[3].setEqualAngle(true);
					Log.v("菱形",	1+ ","+1);
				}else{//普通平行四边形
					/*
					 * 调用等边规整然后利用向量相加的方法计算第四点。
					 */
					Log.v("普通平行四边形",	1+ ","+1);
					temp[3].setX((int)(Math.round(temp[0].getX()+temp[2].getX()-temp[1].getX())));
					temp[3].setY( (int)(Math.round(temp[0].getY()+temp[2].getY()-temp[1].getY())));
				}
				
			}
			else if(Math.abs(angle1 - (180.0 - angle2)) < IS_PARALLEL_EDGES || Math.abs(angle2 - (180 - angle3)) < IS_PARALLEL_EDGES){
				/*
				 * 梯形规整：
				 * 直角梯形：有一个角接近九十度。
				 * 等腰梯形：不平行边接近相等。
				 * 由于梯形结构比较复杂所以分为很多个情况一一规整。
				 * 要考虑的情况：
				 * 1.平行边的长短。
				 * 2.特殊梯形中的特殊角度。
				 * 3.点的顺序。
				 * 大致规整方法如下：
				 * 直角梯形：
				 * 1判断出直角所在并且该直角是出于较长边的直角。
				 * 2调用直角规整函数规整该角。
				 * 3再调用平行处理函数对梯形进行平行处理.
				 * 等腰梯形：
				 * 1.判断出等要梯形各个点的情况
				 * 2.将点按顺序输入平行处理函数中。在函数中对是否等边进行判断并规整
				 * 注:以下的判断中等腰梯形和普通梯形的规整是一样的，但为了保持结构，我们仍然按照不同梯形建起分开来。
				 */
				
				//两种情况。2,4边平行和1,3边平行。
				if(Math.abs(angle1 - (180.0 - angle2)) < IS_PARALLEL_EDGES){//2,4边平行。
					if(distance2 > distance4 ){//distance2>distance4
						
						if(Math.abs(angle3 - 90) < IS_VERTICAL){
							//直角梯形
							changeToApeak(temp[3],temp[2],temp[1]);
					        beTrapezium(temp[3],temp[2],temp[1],temp[0]);
					        Log.v("直角梯形1",temp[3].getX() + ","+temp[3].getY());
						}else if(Math.abs(angle2 - 90) < IS_VERTICAL){
							//直角梯形
							changeToApeak(temp[0],temp[1],temp[2]);
					        beTrapezium(temp[0],temp[1],temp[2],temp[3]);
					        Log.v("直角梯形2",temp[3].getX() + ","+temp[3].getY());

						}else if(Math.abs(distance1 - distance3) / distance3 < PRIVIOUS){
							//等腰梯形
					        beTrapezium(temp[0],temp[1],temp[2],temp[3]);
					        temp[1].setEqualAngle(true);
					        temp[2].setEqualAngle(true);
					        Log.v("等腰梯形1",temp[0].getX() + ","+temp[0].getY());

						}else{
							//普通梯形
					        beTrapezium(temp[0],temp[1],temp[2],temp[3]);
					        Log.v("普通梯形1",temp[0].getX() + ","+temp[0].getY());
						}
					}else{
						
						//distance4>distance2
						if(Math.abs(angle1 - 90) < IS_VERTICAL){
							//直角梯形
							changeToApeak(temp[1],temp[0],temp[3]);
					        beTrapezium(temp[1],temp[0],temp[3],temp[2]);
					        Log.v("直角梯形3",temp[3].getX() + ","+temp[3].getY());

						}else if(Math.abs(angle4 - 90) < IS_VERTICAL){
							//直角梯形
							changeToApeak(temp[2],temp[3],temp[0]);
					        beTrapezium(temp[2],temp[3],temp[0],temp[1]);
					        Log.v("直角梯形4",temp[3].getX() + ","+temp[3].getY());

						}else if(Math.abs(distance1 - distance3) / distance3 < PRIVIOUS){
							//等腰梯形
					        beTrapezium(temp[1],temp[0],temp[3],temp[2]);
					        temp[0].setEqualAngle(true);
					        temp[3].setEqualAngle(true);
					        Log.v("等腰梯形2",temp[0].getX() + ","+temp[0].getY());

						}else{
							//普通梯形
					        beTrapezium(temp[1],temp[0],temp[3],temp[2]);
					        Log.v("普通梯形2",temp[0].getX() + ","+temp[0].getY());
						}
					}
				}else{
					//1,3边平行
					if(distance1 > distance3){
						if(Math.abs(angle1 - 90) < IS_VERTICAL){
							//直角梯形
							changeToApeak(temp[3],temp[0],temp[1]);
						    beTrapezium(temp[3],temp[0],temp[1],temp[2]);
					        Log.v("直角梯形5",temp[0].getX() + ","+temp[0].getY());
						}else if(Math.abs(angle2 - 90)<IS_VERTICAL){
							//直角梯形
							changeToApeak(temp[2],temp[1],temp[0]);
					        beTrapezium(temp[2],temp[1],temp[0],temp[3]);
					        Log.v("直角梯形6",temp[1].getX() + ","+temp[1].getY());
						}else if(Math.abs(distance2 - distance4)/distance4 < PRIVIOUS){
							//等腰梯形
					        beTrapezium(temp[2],temp[1],temp[0],temp[3]);
					        temp[1].setEqualAngle(true);
					        temp[0].setEqualAngle(true);
					        Log.v("等腰梯形3",temp[0].getX() + ","+temp[0].getY());
						}else{
							//普通梯形
					        beTrapezium(temp[2],temp[1],temp[0],temp[3]);
					        Log.v("普通梯形3",temp[0].getX() + ","+temp[0].getY());
						}
					}else{
						//distance3>distance1
						
						if(Math.abs(angle3 - 90) < IS_VERTICAL){
							//直角梯形
							changeToApeak(temp[1],temp[2],temp[3]);
							beTrapezium(temp[1],temp[2],temp[3],temp[0]);
					        Log.v("直角梯形7",temp[2].getX() + ","+temp[2].getY());
						}else if(Math.abs(angle4 - 90) < IS_VERTICAL){
							changeToApeak(temp[0],temp[3],temp[2]);
						    beTrapezium(temp[0],temp[3],temp[2],temp[1]);
					        Log.v("直角梯形8",temp[3].getX() + ","+temp[3].getY());
						}else if(Math.abs(distance2 - distance4) / distance4 < PRIVIOUS){
							//等腰梯形
					        beTrapezium(temp[0],temp[3],temp[2],temp[1]);
					        temp[3].setEqualAngle(true);
					        temp[2].setEqualAngle(true);
					        Log.v("等腰梯形4",temp[0].getX() + ","+temp[0].getY());
						}else{
							//普通梯形
					        beTrapezium(temp[1],temp[2],temp[3],temp[0]);
					        Log.v("普通梯形4",temp[0].getX() + ","+temp[0].getY());
						}
					}
				}
				
				}
			Log.v("四边形规整","四边形规整");
	
	}
	/*
	 * 折线的规整。
	 * 循环判断直线的夹角是否接近九十度，接近则进行直角规整。判断的范围是90+-IS_VERTICAL
	 */
	public void brokenLineHolotactic(Graph graph){
		graph.setEqualAngleToF();
		graph.setRightAngleToF();
		List<PointUnit> point = new ArrayList<PointUnit>();
		double distance1 ,distance2 , distance3;
		double cos1;
		double angle1;
		Log.v("折线规整", "1"+","+point.size());
		for(int i = 0 ; i < point.size() - 2 ; i++){
			distance1 = CommonFunc.distance(point.get(i).getPoint(),point.get(i + 1).getPoint()); 
			distance2 = CommonFunc.distance(point.get(i + 1).getPoint(),point.get(i + 2).getPoint());
			distance3 = CommonFunc.distance(point.get(i).getPoint(),point.get(i + 2).getPoint());
			
			cos1 = (CommonFunc.square(distance1)+CommonFunc.square(distance2)-CommonFunc.square(distance3))/(2*distance1*distance2);
			
			angle1 = Math.acos(cos1)/Math.PI * 180;
			
			Log.v("折线规整", "2");
			if(Math.abs(angle1 - 90) < IS_VERTICAL){Log.v("折线规整", "3");
				changeToApeak(point.get(i) , point.get(i + 1) , point.get(i + 2));
			}
		}
		Log.v("折线规整","折线规整");
	}
	
	/*
	 * 正多边形规整1：
	 * 将输入的多边形进行规整，规整为正的多边形。
	 * 规整方法：
	 * 1.判断传入的闭合多边形时候为正多边形，如果是则进行下一步。
	 * 2.取第一边的长为正多边形的长。通过向量旋转的方法求出第二边。
	 */
	
	/*
	 * 正多边形规整方法2----由于以上方法因为java本身计算存在的为小误差而导致在正四边形规整是出现巨大的误差所以我们用以下方法解决
	 * 1.判断传入的闭合多边形时候为正多边形，如果是则进行下一步。
	 * 2.取第一边的长为正多边形的长。
	 * 3.从第一条边开始规整，以每两条为一个组规整。通过计算第二条边不在第一条边上的点到第一条边的两个点的距离来计算求出
	 * 4.由第三步计算出的点有两个。其中有一个是多余的。我们通过向量旋转的知识来排除多余的那个点。
	 */
	public void regularPolygonHolotactic(Graph graph){
		List<GUnit> unitList = graph.getGraph();
		List<PointUnit> point = new ArrayList<PointUnit>();
		LineUnit ln1 = new LineUnit();
		LineUnit ln2 = new LineUnit();
		int j = 0;
		
		graph.setEqualAngleToF();
		graph.setRightAngleToF();
		//判断是否是正多边形。
		for(GUnit unit : unitList)	{
			if (unit instanceof LineUnit) {
				if(j == 0){
					ln1 = (LineUnit)unit;
					j = 1;
				}
				else{
					ln2 = (LineUnit)unit;
					if(Math.abs(ln1.getLength() - ln2.getLength())/ln2.getLength() > PRIVIOUS){
						return;
					}
				}
			} 	
		}
		
		for(GUnit unit : unitList)	{
			if (unit instanceof PointUnit) {
				point.add((PointUnit)unit);
			}
		}
	    //进行正多边形规整
		//计算每个角的大小。注：因为边的数目等于点的数目。所以此处通过point.size()间接获得边的数目。
		double angle = (double)(point.size() - 2) * 180 / point.size();
		double len = CommonFunc.distance(point.get(0).getPoint(),point.get(1).getPoint());//暂且取第一条边的长为边长。
		double tempLen = Math.sqrt(2 * len * len -2 * len * len * Math.cos((angle / 180) * Math.PI));
		//double sin = Math.sin((angle / 180.0) * Math.PI );
		double x1 , x2 , y1 , y2;//用来存放已知边的两个点信息。
		double a , b , c , d , k;//用来存放计算中的中间值；
		Log.v("angle ,sin,len",angle+ ","+","+len+","+tempLen);
		/*
		 * 通过所求点到已知点的距离来求解。
		 */
		for(int i = 0 ; i < point.size() - 2 ; i++){
		x1 = point.get(i).getX();
		y1 = point.get(i).getY();
		x2 = point.get(i + 1).getX();
		y2 = point.get(i + 1).getY();
    	Log.v("x1 ,y1",x1+ ","+y1);
    	Log.v("x2 ,y2",x2+ ","+y2);
    	Log.v("templen",tempLen+","+Math.cos((angle / 180) * Math.PI));
		Log.v("templen",tempLen+","+Math.cos((angle / 180) * Math.PI)+","+Math.cos((60.0 / 180)* Math.PI)+","+(60.0 / 180) * Math.PI);
    	Log.v("x3 ,y3,i",point.get(i+2).getX()+ ","+point.get(i+2).getY()+","+i+2);
		//通过已知边的两个点信息可求得所求边未知点的方程y = k * x + d;
		k = (double)(x1 - x2) / (y2 - y1);
		d = (tempLen * tempLen - len * len + (x2 * x2 + y2 * y2) - (x1 * x1 + y1 * y1)) / (2 * ( y2 - y1));
		Log.v("k ,d,len,tempLen",k + ","+d+","+len+","+tempLen);
		//以上方程结合未知点和所求边的另一个已知点之间的距离为len可获得方程a*x*x + b * x + c = 0;解方程可获得x值。
		a = 1 + k * k;
    	b = 2 * (k * (d - y2) - x2);
    	c = x2 * x2 + (d - y2) * (d - y2) - len * len;
    	Log.v("a ,b,c",a+ ","+b+","+c);
    	//由于x值会有两个，有一个是多余的，我们通过差乘的方法找出正确值
    	
    	double tempX1 = Math.round((-b - Math.sqrt(b * b - 4 * a * c )) / (2 * a));
    	double tempY1 = k * tempX1 + d; 
    	
    	Log.v("(-b - Math.sqrt(b * b - 4 * a * c )) / (2 * a)",(-b - Math.sqrt(b * b - 4 * a * c )) / (2 * a)+"");
    	Log.v("tempX1 ,tempY1,ROUND()",tempX1+ ","+tempY1+","+Math.sqrt(b * b - 4 * a * c )+","+(b * b - 4 * a * c));
    	double tempx2 = Math.round((-b + Math.sqrt(b * b - 4 * a * c )) / (2 * a));
    	double tempy2 = k * tempx2 + d;
    	
    	double distance1 = Math.sqrt((tempX1 - point.get(i + 2).getX()) * (tempX1 - point.get(i + 2).getX()) + 
    			(tempY1 - point.get(i + 2).getY()) * (tempY1 - point.get(i + 2).getY()));
        double distance2 =  Math.sqrt((tempx2 - point.get(i + 2).getX()) * (tempx2 - point.get(i + 2).getX()) + 
    			(tempy2 - point.get(i + 2).getY()) * (tempy2 - point.get(i + 2).getY()));
    	Log.v("tempX2 ,tempY2",tempx2+ ","+tempy2+","+Math.sqrt(b * b - 4 * a * c ));
    	if(((x1 - x2) * (tempY1 - y2) - (tempX1 - x2) * (y1 - y2) > 0 &&
    			(x1 - x2) * (point.get(i + 2).getY() - y2) - (point.get(i + 2).getX() - x2) * (y1 - y2) > 0) ||
    			((x1 - x2) * (tempY1 - y2) - (tempX1 - x2) * (y1 - y2) < 0 &&
    			(x1 - x2) * (point.get(i + 2).getY() - y2) - (point.get(i + 2).getX() - x2) * (y1 - y2) < 0)){
    		if(distance1 > ThresholdProperty.TWO_POINT_IS_CLOSED){
    			return;
    		}
    		point.get(i + 2).setX((int)tempX1);
    		point.get(i + 2).setY((int)tempY1);
    	}else{
    		if(distance2 > ThresholdProperty.TWO_POINT_IS_CLOSED){
    			return;
    		}
    		point.get(i + 2).setX((int)Math.round((-b + Math.sqrt(b * b - 4 * a * c )) / (2 * a)));
    		point.get(i + 2).setY((int)(k * Math.round((-b + Math.sqrt(b * b - 4 * a * c )) / (2 * a)) + d));
    	}
		}
		if(angle == 90){
			for(int i = 0 ; i < point.size(); i++){
				point.get(i).setRightAngle(true);
			}
		}else{
			for(int i = 0 ; i < point.size(); i++){
				point.get(i).setEqualAngle(true);
			}
		}
		Log.v("正多边形规整","正多边形规整");
	}
	
	/*
	 * 以下是直角规整，平行规整和等边规整。
	 * 需要强调的是之前用过很多不同的方法来规整但是出现了不同的问题。比如算中用到斜率就无法避免的会有k=0和k为无穷大的情况。
	 * 最后我们采取了一下方法，并且发想该方法对以上三种规整都适用
	 * 方法如下：
	 * 1.计算所要规整的点到另外两个点的距离，该过程中可能要计算角度什么的
	 * 2.通过点到直线的距离列方程。解方程组的出x
	 * 3.利用向量旋转的方法排除多余的x点。
	 * 4.计算出相应的y。并更新所要规整点的信息。
	 */
	
    /*
     * 对梯形接近平行的两边进行规整。
     * 梯形的四个点a ,b ,c , d .其中ad为短平行边。cb为长平行边。四点按逆时针排序。
     * 计算方法如下：
     * 1。根据b点，和c点的数据求出bc边的方程: y = k * x + d;
     * 过a点作与dc等长的线，交bc于点f,结合步骤1中方程求得方程aa *　x * x + bb * x + cc = 0。解方程，求出点f
     * 再根据平行四边形的三点（a ,f ,c）通过向量的方法求出求出第四点b
     * 将原来b点的数据进行更新
     */
	/*public void beTrapezium(PointUnit a, PointUnit b ,PointUnit c , PointUnit d ,double len){
    	
		double k ,aa ,bb, cc , dd;//用于承载数据   目地在于简化计算的变量。其中aa,bb,cc 为方程 aa * x ^ 2 + bb * x+ cc = 0;中的常量aa,bb,cc;
    	PointUnit f = new PointUnit();
    	k = (double)(c.getY() - b.getY()) / (double)(c.getX() - b.getX());
    	Log.v("k",k+ "");
    	aa = 1 + k * k;
    	bb = 2 * (k * (b.getY() - k * b.getX() - a.getY()) - a.getX());
    	cc = a.getX() * a.getX()+(b.getY() - k * b.getX() - a.getY()) * (b.getY() - k * b.getX() - a.getY()) - len * len;
		Log.v("len * len", len+",");

    	dd = Math.sqrt(Math.abs(bb * bb - 4 * aa * cc));
    	/*if(dd < 0){
    		return;
    	}*
    	Log.v("aa ,bb,cc",aa+ ","+bb+","+cc);
    	if((CommonFunc.square((-bb - Math.sqrt(dd)) / (2 * aa)-c.getX()) + CommonFunc.square(k * ((-bb - Math.sqrt(dd))/ (2 * aa)) +b.getY()-k * b.getX()-c.getY()))>
    	(CommonFunc.square((-bb + Math.sqrt(dd)) / (2 * aa)-c.getX()) + CommonFunc.square(k*((-bb + Math.sqrt(dd)) / (2 * aa))+b.getY()-k * b.getX()-c.getY()))){
    		f.setX((int) (Math.round((-bb + Math.sqrt(dd )) / (2 * aa))));
    		f.setY((int) (Math.round(k*((-bb + Math.sqrt(dd )) / (2 * aa)) + b.getY() - k*b.getX())));
    		Log.v("Math.sqrt(bb * bb - 4 * aa * cc )", Math.sqrt(bb * bb - 4 * aa * cc )+",");
    		Log.v("bb * bb - 4 * aa * cc )", (bb * bb - 4 * aa * cc )+",");

    		Log.v("f.setX",(Math.round((-bb + Math.sqrt(dd)) / (2 * aa)))+",");
    		Log.v("f.setY",(Math.round(k*((-bb + Math.sqrt(dd)) / (2 * aa)) + b.getY() - k*b.getX()))+",");
    	}else{
    		f.setX((int) (Math.round((-bb - Math.sqrt(dd)) / (2 * aa))));
    		f.setY((int) (Math.round(k*((-bb - Math.sqrt(dd)) / (2 * aa)) + b.getY()-k * b.getX())));
    		Log.v("f.setX",(Math.round((-bb - Math.sqrt(dd)) / (2 * aa)))+",");
    		Log.v("f.setY",(Math.round(k*((-bb -
    				Math.sqrt(dd)) / (2 * aa)) + b.getY() - k*b.getX()))+",");
    		Log.v("bb * bb - 4 * aa * cc )", (bb * bb - 4 * aa * cc )+",");

    		Log.v("Math.sqrt(bb * bb - 4 * aa * cc )", Math.sqrt(bb * bb - 4 * aa * cc )+",");

    	}
    	Log.v("d.getX(),d.getY()", d.getX() +","+ d.getY());
		Log.v("f.getX(),f.getY()", f.getX() +","+ f.getY());
		Log.v("b.getX(),b.getY()", b.getX() +","+ b.getY());

    	d.setX((int)(Math.round(a.getX() + c.getX() - f.getX())));
		d.setY((int)(Math.round(a.getY() + c.getY() - f.getY())));
    }*/
	public void beTrapezium(PointUnit point1, PointUnit point2 ,PointUnit point3 , PointUnit point4 , boolean isHolotactic){//如果isHolotactic为真则不是梯形规整而是用户意图推测中的图形变换。
		double distance1 = CommonFunc.distance(point1.getPoint(),point2.getPoint()); 
		double distance2 = CommonFunc.distance(point2.getPoint(),point3.getPoint());
		double distance3 = CommonFunc.distance(point3.getPoint(),point4.getPoint());
		double distance4 = CommonFunc.distance(point1.getPoint(),point4.getPoint());
		double catercorner1 = CommonFunc.distance(point1.getPoint(),point3.getPoint());
		double len = 0;
		double cos = (CommonFunc.square(distance1)+CommonFunc.square(distance2)-CommonFunc.square(catercorner1))/(2*distance1*distance2);
		Log.v("cos",cos+",");

		if(Math.abs(distance1 - distance3) / distance3 < PRIVIOUS||
				((point2.isEqualAngle() == true&&point3.isEqualAngle() == true && isHolotactic))){//判断是否是等腰梯形并规整；判断“(point2.isEqualAngle() == true&&point3.isEqualAngle() == true)”是用户意图推测所需的。
			/*
			 * 注：如果是直角三角形而且被判断等腰则将梯形规整成为矩形。
			 */
			Log.v("变为等腰梯形",",");

			len = distance2 - 2 * distance1 * cos;
			if(Math.abs(cos - 0) < 0.005){
				point1.setRightAngle(true);
				point2.setRightAngle(true);
				point3.setRightAngle(true);
				point4.setRightAngle(true);
			}else{
			point2.setEqualAngle(true);
			point3.setEqualAngle(true);
			}
		}else if(isHolotactic){
			len = distance2 / 2;
		}else{
			len = distance4;
		}
		double tempAngle = Math.acos(cos)/Math.PI * 180;
		double angle = 180 - tempAngle;
		Log.v("tempAngle , angle",tempAngle+","+angle);
		double tempLen = Math.sqrt(distance1 * distance1 + len * len - 2 * distance1 * len * Math.cos((angle / 180) * Math.PI));
		Log.v("templen",tempLen+","+Math.cos((angle / 180) * Math.PI));
		Log.v("templen",tempLen+","+Math.cos((angle / 180) * Math.PI)+","+Math.cos((60.0 / 180)* Math.PI)+","+(60.0 / 180) * Math.PI);
		
		double x1 , x2 , y1 , y2;//用来存放已知边的两个点信息。
		double a , b , c , d , k;//用来存放计算中的中间值；
		
		x1 = point2.getX();
		y1 = point2.getY();
		x2 = point1.getX();
		y2 = point1.getY();
		
		k = (double)(x1 - x2) / (y2 - y1);
		d = (tempLen * tempLen - len * len + (x2 * x2 + y2 * y2) - (x1 * x1 + y1 * y1)) / (2 * ( y2 - y1));
		Log.v("k ,d",k + ","+d+","+len+","+tempLen);
		//以上方程结合未知点和所求边的另一个已知点之间的距离为len可获得方程a*x*x + b * x + c = 0;解方程可获得x值。
		a = 1 + k * k;
		b = 2 * (k * (d - y2) - x2);
		c = x2 * x2 + (d - y2) * (d - y2) - len * len;
		if(b * b - 4 * a * c < 0 || k > 100000000){
			return;
		}
		Log.v("a ,b,c",a+ ","+b+","+c);
		//由于x值会有两个，有一个是多余的，我们通过差乘的方法找出正确值
		double tempX1 = Math.round((-b - Math.sqrt(b * b - 4 * a * c)) / (2 * a));
		double tempY1 = k * tempX1 + d; 
		Log.v("(-b - Math.sqrt(b * b - 4 * a * c )) / (2 * a)",(-b - Math.sqrt(b * b - 4 * a * c )) / (2 * a)+"");
		Log.v("tempX1 ,tempY1,ROUND()",tempX1+ ","+tempY1+","+Math.sqrt(b * b - 4 * a * c )+","+(b * b - 4 * a * c));
		double tempx2 = Math.round((-b + Math.sqrt(b * b - 4 * a * c )) / (2 * a));
		double tempy2 = k * tempx2 + d;
		Log.v("tempX2 ,tempY2",tempx2+ ","+tempy2+","+Math.sqrt(b * b - 4 * a * c ));
		if(((x1 - x2) * (tempY1 - y2) - (tempX1 - x2) * (y1 - y2) > 0 &&
		    (x1 - x2) * (point3.getY() - y2) - (point3.getX() - x2) * (y1 - y2) > 0) ||
		    ((x1 - x2) * (tempY1 - y2) - (tempX1 - x2) * (y1 - y2) < 0 &&
		    (x1 - x2) * (point3.getY() - y2) - (point3.getX() - x2) * (y1 - y2) < 0)){
		    point4.setX((int)tempX1);
		    point4.setY((int)tempY1);
		    Log.v("梯形规整", "1");
		}else{
			point4.setX((int)Math.round((-b + Math.sqrt(b * b - 4 * a * c )) / (2 * a)));
			point4.setY((int)(k * Math.round((-b + Math.sqrt(b * b - 4 * a * c )) / (2 * a)) + d));
			Log.v("梯形规整", "2");
		}
		if(point2.isRightAngle() == true){
			point1.setRightAngle(true);
		}
	}
	
	public void beTrapezium(PointUnit point1, PointUnit point2 ,PointUnit point3 , PointUnit point4){		
		beTrapezium(point1 , point2 , point3 , point4 , false);	
	}
	
	/*
	 * 直角规整：
	 * 将接近于90读的角规整为九十度角。误差范围为85～95.即阀值为5
	 * 规整方法：
	 * 1.求出某一直角边的方程 y = k1 * x + b;
	 * 2.由该方程的斜率和另一直角边不处于直角上的点求得另一直角边的方程:y = k2 * x + b;
	 * 3.连立，解方程组可求出规整后的直角点
	 */
/*	public void changeToApeak(PointUnit point1, PointUnit point2 ,PointUnit point3){
		
		double k1 = (double)( point2.getY() - point1.getY() ) / (double)( point2.getX() - point1.getX());
		double k2 = -1/ k1;
		point2.setX((int) Math.round(((point3.getY() - point1.getY() - k2 * point3.getX() + k1 *point1.getX()) / ( k1 - k2 )) ));
		point2.setY((int) (k1 * (Math.round(((point3.getY() - point1.getY() - k2 * point3.getX() + k1 *point1.getX()) / ( k1 - k2 )))) + point1.getY()-k1*point1.getX()));
		Log.v("k1 ,k2",k1+ ","+k2);
		Log.v("直角规整",point2.getX() + ","+point2.getY());
	}*/
	public void changeToApeak(PointUnit point1, PointUnit point2 ,PointUnit point3){
		double len = CommonFunc.distance(point2,point3); 
		double tempLen = Math.sqrt(len * len + CommonFunc.distance(point1,point2) * CommonFunc.distance(point1,point2));
		double x1 , x2 , y1 , y2;//用来存放已知边的两个点信息。
		double a , b , c , d , k;//用来存放计算中的中间值；
		
		x1 = point1.getX();
		y1 = point1.getY();
		x2 = point2.getX();
		y2 = point2.getY();
		
		k = (double)(x1 - x2) / (y2 - y1);
		d = (tempLen * tempLen - len * len + (x2 * x2 + y2 * y2) - (x1 * x1 + y1 * y1)) / (2 * ( y2 - y1));
		//以上方程结合未知点和所求边的另一个已知点之间的距离为len可获得方程a*x*x + b * x + c = 0;解方程可获得x值。
		a = 1 + k * k;
		b = 2 * (k * (d - y2) - x2);
		c = x2 * x2 + (d - y2) * (d - y2) - len * len;
		if(b * b - 4 * a * c < 0 || k > 100000000){//k为无穷大时无法计算。
			return;
		}
		Log.v("a ,b,c",a+ ","+b+","+c);
		//由于x值会有两个，有一个是多余的，我们通过差乘的方法找出正确值
		double tempX1 = Math.round((-b - Math.sqrt(b * b - 4 * a * c )) / (2 * a));
		double tempY1 = k * tempX1 + d; 
		Log.v("(-b - Math.sqrt(b * b - 4 * a * c )) / (2 * a)",(-b - Math.sqrt(b * b - 4 * a * c )) / (2 * a)+"");
		Log.v("tempX1 ,tempY1,ROUND()",tempX1+ ","+tempY1+","+Math.sqrt(b * b - 4 * a * c )+","+(b * b - 4 * a * c));
		double tempx2 = Math.round((-b + Math.sqrt(b * b - 4 * a * c )) / (2 * a));
		double tempy2 = k * tempx2 + d;
		Log.v("tempX2 ,tempY2",tempx2+ ","+tempy2+","+Math.sqrt(b * b - 4 * a * c ));
		if(((x1 - x2) * (tempY1 - y2) - (tempX1 - x2) * (y1 - y2) > 0 &&
		    (x1 - x2) * (point3.getY() - y2) - (point3.getX() - x2) * (y1 - y2) > 0) ||
		    ((x1 - x2) * (tempY1 - y2) - (tempX1 - x2) * (y1 - y2) < 0 &&
		    (x1 - x2) * (point3.getY() - y2) - (point3.getX() - x2) * (y1 - y2) < 0)){
		    point3.setX((int)tempX1);
		    point3.setY((int)tempY1);
		    Log.v("直角规整","1");
		}else{
			point3.setX((int)Math.round((-b + Math.sqrt(b * b - 4 * a * c )) / (2 * a)));
			point3.setY((int)(k * Math.round((-b + Math.sqrt(b * b - 4 * a * c )) / (2 * a)) + d));
			Log.v("直角规整","2");
		}
		point2.setRightAngle(true);
	}
	
	
	/*
	 * 等边和等边直角规整：
	 * 将接近相等的相邻两边规整为相等的两边。
	 * 规整过程：
	 * 1.利用垂直平分线上的点到直线两端点的距离相等，结合已知的三个点所形成的等腰三角形。重新计算相等边的公共点。可获得一个方程y = k1 * x + k2。
	 * 2.利用点到直线的距离可获得第二个方程。
	 * 3.连立可的方程aa * x * x - bb * x + cc = 0，解方程组。
	 * 4.由于解方程后会获得一个伪x值。通过判断所求点在非相等边的哪一边来去除伪x值。
	 * 5.对于特殊的等边三角形，我们根据点的标记来进行特殊的角
	 */
	public void changeToEquation(PointUnit point1, PointUnit point2 ,PointUnit point3){
		
/*		double k1 = (double)(point1.getX() - point3.getX()) / (double) (point3.getY() - point1.getY());
		double k2 = (double)(point3.getX() * point3.getX() + point3.getY() * point3.getY() - point1.getX() * point1.getX()
				-point1.getY() * point1.getY()) /(double)( 2 * (point3.getY() - point1.getY()));
		double aa = 1 + k1 * k1 ;
		double bb = 2 * (k1 * (k2- point3.getY())-point3.getX() );
		double cc = point3.getX() * point3.getX() + (k2 - point3.getY()) * (k2- point3.getY()) - len * len ;
		int tempX1 = (int) ((-bb - Math.sqrt(bb * bb - 4 * aa * cc )) / (2 * aa));
		int tempY1 = (int)(Math.round(( k1 * ((-bb - Math.sqrt(bb * bb - 4 * aa * cc )) / (2 * aa)) + k2 )));
		if(((point1.getX() - point3.getX()) * (tempY1 - point3.getY()) - (tempX1 - point3.getX()) * (point1.getY() - point3.getY()) > 0 &&
				(point1.getX() - point3.getX()) * (point2.getY() - point3.getY()) - (point2.getX() - point3.getX()) * (point1.getY() - point3.getY()) > 0) || 
				((point1.getX() - point3.getX()) * (tempY1 - point3.getY()) - (tempX1 - point3.getX()) * (point1.getY() - point3.getY()) < 0 &&
				(point1.getX() - point3.getX()) * (point2.getY() - point3.getY()) - (point2.getX() - point3.getX()) * (point1.getY() - point3.getY()) < 0)){
			point2.setX(tempX1);
			point2.setY(tempY1);
			Log.v("a ,b",aa+ ","+bb);
			Log.v("c ,len",cc+ ","+len);
			Log.v("k1 ,k2",k1+ ","+k2);
			Log.v("等边规整1",(int)(-bb - Math.sqrt(bb * bb - 4 * aa * cc )) / (2 * aa)+ ","+ (int)(-1)*( k1 * ((-bb - Math.sqrt(bb * bb - 4 * aa * cc )) / (2 * aa)) + k2 ));
		}
		else{
			point2.setX((int)(Math.round( ((-bb + Math.sqrt(bb * bb - 4 * aa * cc )) / (2 * aa)))));
			point2.setY( (int)(Math.round(( k1 * ((-bb + Math.sqrt(bb * bb - 4 * aa * cc )) / (2 * aa)) + k2 ))));
			Log.v("a ,b",aa+ ","+bb);
			Log.v("c ,len",cc+ ","+len);
			Log.v("k1 ,k2",k1+ ","+k2);
			Log.v("等边规整2",(int)(-bb + Math.sqrt(bb * bb - 4 * aa * cc )) / (2 * aa)+ ","+ (int)(-1)*( k1 * ((-bb + Math.sqrt(bb * bb - 4 * aa * cc )) / (2 * aa)) + k2 ));
		}*/ 
		
		double distance1 = CommonFunc.distance(point1.getPoint(),point2.getPoint()); 
		double distance2 = CommonFunc.distance(point2.getPoint(),point3.getPoint());
		double distance3 = CommonFunc.distance(point1.getPoint(),point3.getPoint());
		double len = (distance1 + distance2) / 2;
		lengthenALine(point2,point1,len);
		double cos1 = (CommonFunc.square(distance1)+CommonFunc.square(distance2)-CommonFunc.square(distance3))/(2*distance1*distance2);
		
		double angle = Math.acos(cos1)/Math.PI * 180;
		if(point2.isEqualAngle() == true ){
			angle = 60;
		}else if(point2.isRightAngle() ==true){
			angle = 90;
			point2.setRightAngle(true);
		}
		double tempLen = Math.sqrt(2 * len * len -2 * len * len * Math.cos((angle / 180) * Math.PI));
		double x1 , x2 , y1 , y2;//用来存放已知边的两个点信息。
		double a , b , c , d , k;//用来存放计算中的中间值；
		
		x1 = point1.getX();
		y1 = point1.getY();
		x2 = point2.getX();
		y2 = point2.getY();
		
		k = (double)(x1 - x2) / (y2 - y1);
		d = (tempLen * tempLen - len * len + (x2 * x2 + y2 * y2) - (x1 * x1 + y1 * y1)) / (2 * ( y2 - y1));
		Log.v("k ,d",k + ","+d+","+len+","+tempLen);
		//以上方程结合未知点和所求边的另一个已知点之间的距离为len可获得方程a*x*x + b * x + c = 0;解方程可获得x值。
		a = 1 + k * k;
		b = 2 * (k * (d - y2) - x2);
		c = x2 * x2 + (d - y2) * (d - y2) - len * len;
		if(b * b - 4 * a * c < 0 || k > 100000000){
			return;
		}
		Log.v("a ,b,c",a+ ","+b+","+c);
		//由于x值会有两个，有一个是多余的，我们通过差乘的方法找出正确值
		double tempX1 = Math.round((-b - Math.sqrt(b * b - 4 * a * c )) / (2 * a));
		double tempY1 = k * tempX1 + d; 
		Log.v("(-b - Math.sqrt(b * b - 4 * a * c )) / (2 * a)",(-b - Math.sqrt(b * b - 4 * a * c )) / (2 * a)+"");
		Log.v("tempX1 ,tempY1,ROUND()",tempX1+ ","+tempY1+","+Math.sqrt(b * b - 4 * a * c )+","+(b * b - 4 * a * c));
		double tempx2 = Math.round((-b + Math.sqrt(b * b - 4 * a * c )) / (2 * a));
		double tempy2 = k * tempx2 + d;
		Log.v("tempX2 ,tempY2",tempx2+ ","+tempy2+","+Math.sqrt(b * b - 4 * a * c ));
		if(((x1 - x2) * (tempY1 - y2) - (tempX1 - x2) * (y1 - y2) > 0 &&
		    (x1 - x2) * (point3.getY() - y2) - (point3.getX() - x2) * (y1 - y2) > 0) ||
		    ((x1 - x2) * (tempY1 - y2) - (tempX1 - x2) * (y1 - y2) < 0 &&
		    (x1 - x2) * (point3.getY() - y2) - (point3.getX() - x2) * (y1 - y2) < 0)){
		    point3.setX((int)tempX1);
		    point3.setY((int)tempY1);
		    Log.v("等边规整", "1");
		}else{
			point3.setX((int)Math.round((-b + Math.sqrt(b * b - 4 * a * c )) / (2 * a)));
			point3.setY((int)(k * Math.round((-b + Math.sqrt(b * b - 4 * a * c )) / (2 * a)) + d));
			Log.v("等边规整", "2");
		}
		//标记特殊角
		point1.setEqualAngle(true);
		point3.setEqualAngle(true);
	}
	/*
	 * 判断某一闭合图形是否是多边形。
	 * 判断依据：由在同一平面且不在同一直线上的三条或三条以上的线段首尾顺
	 * 次连结且不相交所组成的封闭图形叫做多边形。在不同平面上的多条线段首
	 * 尾顺次连结且不相交所组成的图形也被称为多边形，是广义的多边形。
	 */
	public boolean isPolygon(Graph graph){
		Log.v("多边形判断", "，");
		List<GUnit> unitList = graph.getGraph();
		List<LineUnit> temp = new ArrayList<LineUnit>();
		LineUnit temp1;
		LineUnit temp2;
		double k , b;
		
		for(GUnit unit : unitList) {
			if(unit instanceof LineUnit){
				temp.add((LineUnit)unit);
			}
		}
		for(int i = 0 ; i < temp.size() - 1 ; i++){
			temp1 = temp.get(i);
			k = (double)(temp1.getStartPointUnit().getY() - temp1.getEndPointUnit().getY()) / 
					(temp1.getStartPointUnit().getX() - temp1.getEndPointUnit().getX());
			b = temp1.getStartPointUnit().getY() - k * temp1.getStartPointUnit().getX();
			for(int j = i + 1; j < temp.size() ; j++){
				temp2 = (LineUnit) temp.get(j);
				if((temp2.getStartPointUnit().getY() - k * temp2.getStartPointUnit().getX() - b) * 
						(temp2.getEndPointUnit().getY() - k * temp2.getEndPointUnit().getX() - b) < 0 || 
						((temp2.getStartPointUnit().getY() - k * temp2.getStartPointUnit().getX() - b) == 0 && 
						(temp2.getEndPointUnit().getY() - k * temp2.getEndPointUnit().getX() - b) == 0)){
					k = (double)(temp2.getStartPointUnit().getY() - temp2.getEndPointUnit().getY()) / 
							(temp2.getStartPointUnit().getX() - temp2.getEndPointUnit().getX());
					b = temp2.getStartPointUnit().getY() - k * temp2.getStartPointUnit().getX();
					if((temp1.getStartPointUnit().getY() - k * temp1.getStartPointUnit().getX() - b) * 
							(temp1.getEndPointUnit().getY() - k * temp1.getEndPointUnit().getX() - b) < 0 || 
							((temp1.getStartPointUnit().getY() - k * temp1.getStartPointUnit().getX() - b) == 0 && 
							(temp1.getEndPointUnit().getY() - k * temp1.getEndPointUnit().getX() - b) == 0)){
						return false;
					}
				}
			}
		}Log.v("多边形判断", "，");
		return true;
	}	
	public void lengthenALine(PointUnit point1 , PointUnit point2 , double length){
		 double k , d , a , b , c;Log.v("asdasdasd直角规整","啊1");
		 k = ((double)(point1.getY() - point2.getY())) / (point1.getX() - point2.getX());
		 d = point1.getY() - k * point1.getX();
		 a = 1 + k * k;
		 b = 2 * k * (d - point1.getY()) - 2 * point1.getX();
		 c = CommonFunc.square(d - point1.getY()) + CommonFunc.square(point1.getX()) -
				 CommonFunc.square(length);
		 Log.v("asdasdasd直角规整",k+","+(b * b - 4 * a * c));
		 if(b * b - 4 * a * c < 0 || k > 100000000){
				return;
			}Log.v("asdasdasd直角规整","1啊");
		 double tempX1 = Math.round((-b - Math.sqrt(b * b - 4 * a * c )) / (2 * a));
		 double tempY1 = k * tempX1 + d; 
			Log.v("(-b - Math.sqrt(b * b - 4 * a * c )) / (2 * a)",(-b - Math.sqrt(b * b - 4 * a * c )) / (2 * a)+"");
			Log.v("tempX1 ,tempY1,ROUND()",tempX1+ ","+tempY1+","+Math.sqrt(b * b - 4 * a * c )+","+(b * b - 4 * a * c));
		 double tempX2 = Math.round((-b + Math.sqrt(b * b - 4 * a * c )) / (2 * a));
		 double tempY2 = k * tempX2 + d;
			Log.v("tempX2 ,tempY2",tempX2+ ","+tempY2+","+Math.sqrt(b * b - 4 * a * c ));
			if((CommonFunc.square(point2.getX() - tempX1) + CommonFunc.square(point2.getY() - tempY1)) < 
					(CommonFunc.square(point2.getX() - tempX2) + CommonFunc.square(point2.getY() - tempY2))){
			    point2.setX((int)tempX1);
			    point2.setY((int)tempY1);
			    Log.v("asdasdasd直角规整","1");
			}else{
				point2.setX((int)tempX2);
				point2.setY((int)tempY2);
				Log.v("asdasdasd直角规整","2");
			}
	}
}
