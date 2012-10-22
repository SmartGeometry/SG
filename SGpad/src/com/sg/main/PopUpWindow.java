package com.sg.main;

import java.util.List;

import com.sg.logic.common.CommonFunc;
import com.sg.object.graph.Graph;
import com.sg.object.graph.RectangleGraph;
import com.sg.object.graph.TriangleGraph;
import com.sg.object.unit.GUnit;
import com.sg.object.unit.PointUnit;
import com.sg.property.R;
import com.sg.transformation.recognizer.Regulariser;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

public class PopUpWindow {
	private LayoutInflater mLayoutInflater;
	private View popView;
	private PopupWindow mPop;
	private Context context;
	private Regulariser regulariser;
	private Graph myGraph;
	
	public PopUpWindow(Context context, Regulariser regulariser){
		this.context = context;
		this.regulariser = regulariser;
		mLayoutInflater = (LayoutInflater) 
				this.context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
	}

	public void showPop(View parent, Graph graph, int x, int y){
		myGraph = graph;
		if(graph instanceof TriangleGraph){
			myGraph.setEqualAngleToF();
			myGraph.setRightAngleToF();
			popView =  mLayoutInflater.inflate(R.layout.popup_for_tri, null);
			mPop = new PopupWindow(popView, 300, 70);
			mPop.setOutsideTouchable(true);
			popView.findViewById(R.id.isoceles_tri).setOnClickListener(
					new View.OnClickListener()
					{
						public void onClick(View v)
						{
							changeToIsocelesTri(myGraph);
							// 关闭Popup窗口
							mPop.dismiss();
						}
					});
			
			popView.findViewById(R.id.right_angled_tri).setOnClickListener(
					new View.OnClickListener()
					{
						public void onClick(View v)
						{
							PointUnit point1 = (PointUnit)(myGraph.getGraph().get(0));
							PointUnit point2 = (PointUnit)(myGraph.getGraph().get(2));
							PointUnit point3 = (PointUnit)(myGraph.getGraph().get(4));
							double distance1 = CommonFunc.distance(point1,point2); 
							double distance2 = CommonFunc.distance(point2,point3);
							double distance3 = CommonFunc.distance(point3,point1);
							double cos1 = (CommonFunc.square(distance1)+CommonFunc.square(distance3)-CommonFunc.square(distance2)) / (2 * distance1 * distance3);
							double cos2 = (CommonFunc.square(distance1)+CommonFunc.square(distance2)-CommonFunc.square(distance3)) / (2 * distance1 * distance2);
							double cos3 = (CommonFunc.square(distance2)+CommonFunc.square(distance3)-CommonFunc.square(distance1)) / (2 * distance2 * distance3);
							if(Math.abs(cos1) <= Math.abs(cos2) && Math.abs(cos1) <= Math.abs(cos3)){
								regulariser.changeToApeak(point3 , point1 , point2);
							}
							if(Math.abs(cos2) <= Math.abs(cos1) && Math.abs(cos2) <= Math.abs(cos3)){
								regulariser.changeToApeak(point1 , point2 , point3);
							}
							if(Math.abs(cos3) <= Math.abs(cos1) && Math.abs(cos3) <= Math.abs(cos2)){
								regulariser.changeToApeak(point2 , point3 , point1);
							}
							Log.v("直角三角形","意图推测");
							// 关闭Popup窗口
							mPop.dismiss();
						}
					});
			popView.findViewById(R.id.right_angled_isosceles_tri).setOnClickListener(
					new View.OnClickListener()
					{
						public void onClick(View v)
						{
							PointUnit point1 = (PointUnit)(myGraph.getGraph().get(0));
							PointUnit point2 = (PointUnit)(myGraph.getGraph().get(2));
							PointUnit point3 = (PointUnit)(myGraph.getGraph().get(4));
							double distance1 = CommonFunc.distance(point1,point2); 
							double distance2 = CommonFunc.distance(point2,point3);
							double distance3 = CommonFunc.distance(point3,point1);
							double cos1 = (CommonFunc.square(distance1)+CommonFunc.square(distance3)-CommonFunc.square(distance2)) / (2 * distance1 * distance3);
							double cos2 = (CommonFunc.square(distance1)+CommonFunc.square(distance2)-CommonFunc.square(distance3)) / (2 * distance1 * distance2);
							double cos3 = (CommonFunc.square(distance2)+CommonFunc.square(distance3)-CommonFunc.square(distance1)) / (2 * distance2 * distance3);
							if(Math.abs(cos1) <= Math.abs(cos2) && Math.abs(cos1) <= Math.abs(cos3)){
								point1.setRightAngle(true);
								regulariser.changeToEquation(point3 , point1 , point2);
							}
							if(Math.abs(cos2) <= Math.abs(cos1) && Math.abs(cos2) <= Math.abs(cos3)){
								point2.setRightAngle(true);
								regulariser.changeToEquation(point1 , point2 , point3);
							}
							if(Math.abs(cos3) <= Math.abs(cos1) && Math.abs(cos3) <= Math.abs(cos2)){
								point3.setRightAngle(true);
								regulariser.changeToEquation(point2 , point3 , point1);
							}
							Log.v("等腰直角三角形","意图推测");
							// 关闭Popup窗口
							mPop.dismiss();
						}
					});
			popView.findViewById(R.id.equilateral_tri).setOnClickListener(
					new View.OnClickListener()
					{
						public void onClick(View v)
						{
							((PointUnit)(myGraph.getGraph().get(2))).setEqualAngle(true);
							regulariser.changeToEquation((PointUnit)(myGraph.getGraph().get(0)) , (PointUnit)(myGraph.getGraph().get(2)) ,
									(PointUnit)(myGraph.getGraph().get(4)));
							Log.v("等边三角形","意图推测");
							// 关闭Popup窗口
							mPop.dismiss();
						}
					});
			mPop.showAtLocation(parent,  Gravity.LEFT | Gravity.TOP, x, y);
		}
		if(graph instanceof RectangleGraph){
			myGraph.setEqualAngleToF();
			myGraph.setRightAngleToF();
			popView =  mLayoutInflater.inflate(R.layout.popup_for_rect, null);
			mPop = new PopupWindow(popView, 510, 70);
			mPop.setOutsideTouchable(true);
			popView.findViewById(R.id.parallelogram).setOnClickListener(
					new View.OnClickListener()
					{
						public void onClick(View v)
						{
							Log.v("普通平行四边形",	1+ ","+1);
							((PointUnit)(myGraph.getGraph().get(6))).setX((int)(Math.round(((PointUnit)(myGraph.getGraph().get(0))).getX()+((PointUnit)(myGraph.getGraph().get(4))).getX()-((PointUnit)(myGraph.getGraph().get(2))).getX())));
							((PointUnit)(myGraph.getGraph().get(6))).setY((int)(Math.round(((PointUnit)(myGraph.getGraph().get(0))).getY()+((PointUnit)(myGraph.getGraph().get(4))).getY()-((PointUnit)(myGraph.getGraph().get(2))).getY())));
							// 关闭Popup窗口
							mPop.dismiss();
						}
					});
			popView.findViewById(R.id.rect).setOnClickListener(
					new View.OnClickListener()
					{
						public void onClick(View v)
						{
							regulariser.changeToApeak((PointUnit)(myGraph.getGraph().get(0)) , (PointUnit)(myGraph.getGraph().get(2)) ,
									(PointUnit)(myGraph.getGraph().get(4)));
							((PointUnit)(myGraph.getGraph().get(6))).setX((int)(Math.round(((PointUnit)(myGraph.getGraph().get(0))).getX()+((PointUnit)(myGraph.getGraph().get(4))).getX()-((PointUnit)(myGraph.getGraph().get(2))).getX())));
							((PointUnit)(myGraph.getGraph().get(6))).setY( (int)(Math.round(((PointUnit)(myGraph.getGraph().get(0))).getY()+((PointUnit)(myGraph.getGraph().get(4))).getY()-((PointUnit)(myGraph.getGraph().get(2))).getY())));
							((PointUnit)(myGraph.getGraph().get(0))).setRightAngle(true);
							((PointUnit)(myGraph.getGraph().get(4))).setRightAngle(true);
							((PointUnit)(myGraph.getGraph().get(6))).setRightAngle(true);
							// 关闭Popup窗口
							mPop.dismiss();
						}
					});
			popView.findViewById(R.id.squre).setOnClickListener(
					new View.OnClickListener()
					{
						public void onClick(View v)
						{
							((PointUnit)(myGraph.getGraph().get(2))).setRightAngle(true);
							regulariser.changeToEquation((PointUnit)(myGraph.getGraph().get(0)) , (PointUnit)(myGraph.getGraph().get(2)) ,
									(PointUnit)(myGraph.getGraph().get(4)));
							((PointUnit)(myGraph.getGraph().get(6))).setX((int)(Math.round(((PointUnit)(myGraph.getGraph().get(0))).getX()+((PointUnit)(myGraph.getGraph().get(4))).getX()-((PointUnit)(myGraph.getGraph().get(2))).getX())));
							((PointUnit)(myGraph.getGraph().get(6))).setY( (int)(Math.round(((PointUnit)(myGraph.getGraph().get(0))).getY()+((PointUnit)(myGraph.getGraph().get(4))).getY()-((PointUnit)(myGraph.getGraph().get(2))).getY())));
							((PointUnit)(myGraph.getGraph().get(0))).setRightAngle(true);
							((PointUnit)(myGraph.getGraph().get(4))).setRightAngle(true);
							((PointUnit)(myGraph.getGraph().get(6))).setRightAngle(true);
							// 关闭Popup窗口
							mPop.dismiss();
						}
					});
			popView.findViewById(R.id.diamond).setOnClickListener(
					new View.OnClickListener()
					{
						public void onClick(View v)
						{
							regulariser.changeToEquation((PointUnit)(myGraph.getGraph().get(0)) , (PointUnit)(myGraph.getGraph().get(2)) ,
									(PointUnit)(myGraph.getGraph().get(4)));
							((PointUnit)(myGraph.getGraph().get(6))).setX((int)(Math.round(((PointUnit)(myGraph.getGraph().get(0))).getX()+
									((PointUnit)(myGraph.getGraph().get(4))).getX()-((PointUnit)(myGraph.getGraph().get(2))).getX())));
							((PointUnit)(myGraph.getGraph().get(6))).setY( (int)(Math.round(((PointUnit)(myGraph.getGraph().get(0))).getY()+
									((PointUnit)(myGraph.getGraph().get(4))).getY()-((PointUnit)(myGraph.getGraph().get(2))).getY())));
							((PointUnit)(myGraph.getGraph().get(0))).setEqualAngle(true);
							((PointUnit)(myGraph.getGraph().get(2))).setEqualAngle(true);
							((PointUnit)(myGraph.getGraph().get(4))).setEqualAngle(true);
							((PointUnit)(myGraph.getGraph().get(6))).setEqualAngle(true);
							Log.v("等边三角形","意图推测");
							// 关闭Popup窗口
							mPop.dismiss();
						}
					});
			popView.findViewById(R.id.trapezoid).setOnClickListener(
					new View.OnClickListener()
					{
						public void onClick(View v)
						{
							PointUnit point1 = (PointUnit)(myGraph.getGraph().get(0));
							PointUnit point2 = (PointUnit)(myGraph.getGraph().get(2));
							PointUnit point3 = (PointUnit)(myGraph.getGraph().get(4));
							PointUnit point4 = (PointUnit)(myGraph.getGraph().get(6));
							double distance1 = CommonFunc.distance(point1,point2); 
							double distance2 = CommonFunc.distance(point2,point3);
							double distance3 = CommonFunc.distance(point3,point4);
							double distance4 = CommonFunc.distance(point4,point1);
							double catercorner1 = CommonFunc.distance(point1,point3);
							double catercorner2 = CommonFunc.distance(point2,point4);
							double cos1 = (CommonFunc.square(distance1)+CommonFunc.square(distance4)-CommonFunc.square(catercorner2))/(2*distance1*distance4);
							double cos2 = (CommonFunc.square(distance1)+CommonFunc.square(distance2)-CommonFunc.square(catercorner1))/(2*distance1*distance2);
							double cos3 = (CommonFunc.square(distance2)+CommonFunc.square(distance3)-CommonFunc.square(catercorner2))/(2*distance2*distance3);
							if(cos1 > 0){
								if(distance1 > distance4){
									regulariser.beTrapezium(point4 , point1 , point2 , point3 , true);
								}else{
									regulariser.beTrapezium(point2 , point1 , point4 , point3 , true);
								}
							}else if(cos2 > 0){
								if(distance1 > distance2){
									regulariser.beTrapezium(point3 , point2 , point1 , point4 , true);
								}else{
									regulariser.beTrapezium(point1 , point2 , point3 , point4 , true);
								}
							}else if(cos3 > 0){
								if(distance2 > distance3){
									regulariser.beTrapezium(point4 , point3, point2 , point1 , true);
								}else{
									regulariser.beTrapezium(point2 , point3 , point4 , point1 , true);
								}
							}else{
								if(distance3 > distance4){
									regulariser.beTrapezium(point1 , point4 , point3 , point2 , true);
								}else{
									regulariser.beTrapezium(point3 , point4 , point1 , point2 , true);
								}
							}
							// 关闭Popup窗口
							mPop.dismiss();
						}
					});
			popView.findViewById(R.id.right_trap).setOnClickListener(
					new View.OnClickListener()
					{
						public void onClick(View v)
						{
							PointUnit point1 = (PointUnit)(myGraph.getGraph().get(0));
							PointUnit point2 = (PointUnit)(myGraph.getGraph().get(2));
							PointUnit point3 = (PointUnit)(myGraph.getGraph().get(4));
							PointUnit point4 = (PointUnit)(myGraph.getGraph().get(6));
							double distance1 = CommonFunc.distance(point1,point2); 
							double distance2 = CommonFunc.distance(point2,point3);
							double distance3 = CommonFunc.distance(point3,point4);
							double distance4 = CommonFunc.distance(point4,point1);
							double catercorner1 = CommonFunc.distance(point1,point3);
							double catercorner2 = CommonFunc.distance(point2,point4);
							double cos1 = (CommonFunc.square(distance1)+CommonFunc.square(distance4)-CommonFunc.square(catercorner2))/(2*distance1*distance4);
							double cos2 = (CommonFunc.square(distance1)+CommonFunc.square(distance2)-CommonFunc.square(catercorner1))/(2*distance1*distance2);
							double cos3 = (CommonFunc.square(distance2)+CommonFunc.square(distance3)-CommonFunc.square(catercorner2))/(2*distance2*distance3);
							if(cos1 > 0){
								if(distance1 > distance4){
									regulariser.changeToApeak(point4 , point1 , point2);
									regulariser.beTrapezium(point4 , point1 , point2 , point3 , true);
								}else{
									regulariser.changeToApeak(point2 , point1 , point4);
									regulariser.beTrapezium(point2 , point1 , point4 , point3 , true);
								}
							}else if(cos2 > 0){
								if(distance1 > distance2){
									regulariser.changeToApeak(point3 , point2 , point1);
									regulariser.beTrapezium(point3 , point2 , point1 , point4 , true);
								}else{
									regulariser.changeToApeak(point1 , point2 , point3);
									regulariser.beTrapezium(point1 , point2 , point3 , point4 , true);
								}
							}else if(cos3 > 0){
								if(distance2 > distance3){
									regulariser.changeToApeak(point4 , point3 , point2);
									regulariser.beTrapezium(point4 , point3, point2 , point1 , true);
								}else{
									regulariser.changeToApeak(point2 , point3 , point4);
									regulariser.beTrapezium(point2 , point3 , point4 , point1 , true);
								}
							}else{
								if(distance3 > distance4){
									regulariser.changeToApeak(point1 , point4 , point3);
									regulariser.beTrapezium(point1 , point4 , point3 , point2 , true);
								}else{
									regulariser.changeToApeak(point3 , point4 , point1);
									regulariser.beTrapezium(point3 , point4 , point1 , point2 , true);
								}
							}
							// 关闭Popup窗口
							mPop.dismiss();
						}
					});
			popView.findViewById(R.id.isosceles_trap).setOnClickListener(
					new View.OnClickListener()
					{
						public void onClick(View v)
						{
							PointUnit point1 = (PointUnit)(myGraph.getGraph().get(0));
							PointUnit point2 = (PointUnit)(myGraph.getGraph().get(2));
							PointUnit point3 = (PointUnit)(myGraph.getGraph().get(4));
							PointUnit point4 = (PointUnit)(myGraph.getGraph().get(6));
							double distance1 = CommonFunc.distance(point1,point2); 
							double distance2 = CommonFunc.distance(point2,point3);
							double distance3 = CommonFunc.distance(point3,point4);
							double distance4 = CommonFunc.distance(point4,point1);
							double catercorner1 = CommonFunc.distance(point1,point3);
							double catercorner2 = CommonFunc.distance(point2,point4);
							double cos1 = (CommonFunc.square(distance1)+CommonFunc.square(distance4)-CommonFunc.square(catercorner2))/(2*distance1*distance4);
							double cos2 = (CommonFunc.square(distance1)+CommonFunc.square(distance2)-CommonFunc.square(catercorner1))/(2*distance1*distance2);
							double cos3 = (CommonFunc.square(distance2)+CommonFunc.square(distance3)-CommonFunc.square(catercorner2))/(2*distance2*distance3);
							if(cos1 > 0){
								if(distance1 > distance4){
									point1.setEqualAngle(true);
									point2.setEqualAngle(true);
									regulariser.beTrapezium(point4 , point1 , point2 , point3 , true);
								}else{
									point1.setEqualAngle(true);
									point4.setEqualAngle(true);
									regulariser.beTrapezium(point2 , point1 , point4 , point3 , true);
								}
							}else if(cos2 > 0){
								if(distance1 > distance2){
									point1.setEqualAngle(true);
									point2.setEqualAngle(true);
									regulariser.beTrapezium(point3 , point2 , point1 , point4 , true);
								}else{
									point2.setEqualAngle(true);
									point3.setEqualAngle(true);
									regulariser.beTrapezium(point1 , point2 , point3 , point4 , true);
								}
							}else if(cos3 > 0){
								if(distance2 > distance3){
									point2.setEqualAngle(true);
									point3.setEqualAngle(true);
									regulariser.beTrapezium(point4 , point3, point2 , point1 , true);
								}else{
									point3.setEqualAngle(true);
									point4.setEqualAngle(true);
									regulariser.beTrapezium(point2 , point3 , point4 , point1 , true);
								}
							}else{
								if(distance3 > distance4){
									point3.setEqualAngle(true);
									point4.setEqualAngle(true);
									regulariser.beTrapezium(point1 , point4 , point3 , point2 , true);
								}else{
									point1.setEqualAngle(true);
									point4.setEqualAngle(true);
									regulariser.beTrapezium(point3 , point4 , point1 , point2 , true);
								}
							}
							mPop.dismiss();
						}
					});
			mPop.showAtLocation(parent,  Gravity.LEFT | Gravity.TOP, x, y);
		}
	} 
	
	public void changeToIsocelesTri(Graph graph){
		double length1 , length2 ,length3;//用来存放两边的长度差的绝对值。
		PointUnit point1 = (PointUnit)(myGraph.getGraph().get(0));
		PointUnit point2 = (PointUnit)(myGraph.getGraph().get(2));
		PointUnit point3 = (PointUnit)(myGraph.getGraph().get(4));
		double distance1 = CommonFunc.distance(point1,point2); 
		double distance2 = CommonFunc.distance(point2,point3);
		double distance3 = CommonFunc.distance(point3,point1);
		length1 = Math.abs(distance1 - distance2);
		length2 = Math.abs(distance2 - distance3);
		length3 = Math.abs(distance3 - distance1);
		if(length1 <= length2 && length1 <= length3)
		regulariser.changeToEquation(point1 , point2 , point3);
		if(length2 <= length1 && length2 <= length3)
			regulariser.changeToEquation(point2 , point3 , point1);
		if(length3 <= length1 && length3 <= length2)
			regulariser.changeToEquation(point3 , point1 , point2);
		Log.v("等腰三角形","意图推测");
	}
	public void dismiss(){
		if(mPop != null){
			mPop.dismiss();
		}
	}
}
