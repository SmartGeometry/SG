package com.sg.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sg.control.FileService;
import com.sg.control.GraphControl;
import com.sg.control.OperationType;
import com.sg.control.UndoRedoSolver;
import com.sg.control.UndoRedoStruct;
import com.sg.logic.common.CommonFunc;
import com.sg.logic.common.VectorFunc;
import com.sg.logic.strategy.LineStrategy;
import com.sg.object.Point;
import com.sg.object.graph.Sketch;
import com.sg.object.graph.Graph;
import com.sg.object.graph.TriangleGraph;
import com.sg.object.unit.GUnit;
import com.sg.object.unit.PointUnit;
import com.sg.object.unit.CurveUnit;
import com.sg.property.R;
import com.sg.property.common.ThresholdProperty;
import com.sg.property.tools.Painter;
import com.sg.transformation.collection.PenInfo;
import com.sg.transformation.collection.PenInfoCollector;
import com.sg.transformation.computeagent.Constrainter;
import com.sg.transformation.computeagent.KeepConstrainter;
import com.sg.transformation.computeagent.Regulariser;
import com.sg.transformation.computeagent.UserIntentionReasoning;
import com.sg.transformation.recognizer.Recognizer;
import com.sg.transformation.renderfactory.GraphFactory;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainView extends SurfaceView implements SurfaceHolder.Callback,
		Runnable {

	private static final int SINGLE_POINT_TOUCH = 1;
	private static final int DOUBLE_POINT_TOUCH = 2;

	private SurfaceHolder mHolder;
	private boolean mLoop;

	private Graph curGraph;
	private Graph checkedGraph;
	private GUnit curUnit;
	private List<Graph> graphList;
	private List<Point> pointList;

	private PenInfoCollector collector; // 点信息收集器
	private GraphFactory graphFactory; // 图形对象工厂
	private Constrainter constrainter; // 图形约束
	private Regulariser regulariser; // 图形规整

	private long downTime;
	private long upTime;

	private boolean isEidt; // 编辑态 识别态
	//private boolean isRecognize; // 图形是否识别
	private boolean isChecked;
	private Canvas canvas;
	
	private UndoRedoSolver URSolver;
	
	//GraphControl graphControl;
	private int color; // 画笔颜色 宽度
	private int width;
	
    //private boolean isFirstUndoDelete; //是否是第一次undo 撤销删除
    
    private boolean isDoubleTouch;

    private FileService fileServicer;
    
    private KeepConstrainter keepConstrainter; //约束保持求解器
    
    private UserIntentionReasoning userIntentionReasoning; //用户意图推测器

	//2012-8-26 onion
    private Magnifier magnifier;
	
	public MainView(Context context) {
		super(context);
		mHolder = getHolder();
		mHolder.addCallback(this);
		setFocusable(true);
		mLoop = true; // 循环画图
		isEidt = false;
		isChecked = false;
		
		isDoubleTouch = false;

		curUnit = null;
		graphList = new ArrayList<Graph>();
		pointList = new ArrayList<Point>();

		collector = PenInfoCollector.getInstance();
		graphFactory = GraphFactory.getInstance();
		constrainter = Constrainter.getInstance();
		regulariser = Regulariser.getInstance();
		
		pointList = collector.getPenInfoList(); // 收集到的点信息，未处理过的
		
		URSolver = UndoRedoSolver.getInstance();
		
		fileServicer = new FileService(context);
		
		keepConstrainter = KeepConstrainter.getInstance();
		
		userIntentionReasoning = new UserIntentionReasoning(context, regulariser, constrainter, keepConstrainter, URSolver);
		
		color = Color.BLACK; // 画笔颜色
		width = 3; // 画笔宽度
		//graphControl = new GraphControl(graphList, color, width);

		//isFirstUndoDelete = true;
		
		//new Thread(this).start();

		//2012-8-26 onion
		magnifier = new Magnifier();
		
	}
	
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mLoop = true; // 循环画图
		new Thread(this).start();

		//2012-8-26 onion
		CommonFunc.setDriverBound(this.getWidth(), this.getHeight());
		magnifier.Init();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		this.mLoop = false;
	}
	
	@Override
	public void run() {
		while(mLoop) {
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			synchronized(mHolder) {  // 代表这个方法加锁
				onDraw();
			}
		}
	}
	
	protected void onDraw() {
		if(mHolder == null) {
			return;
		}

		//2012-8-26 onion
		//Canvas tCanvas = mHolder.lockCanvas();  // 锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了
		//原：canvas = mHolder.lockCanvas();  // 锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了
		
		canvas = magnifier.GetCacheCanvas();
		if(canvas == null) {
			return;
		}
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE);         //画背景色
		GraphControl graphControl = new GraphControl(graphList, color, width);
		if(curGraph instanceof Sketch){
			graphControl.drawObj(curGraph, canvas); // 绘制当前勾画的草图
		}
		//graphControl.drawObj(curGraph, canvas); // 绘制当前勾画的草图
		//Log.v("draw size", graphList.size() + "");
		graphControl.drawObjList(canvas); // 绘制已有的图像对象列表

		Canvas tCanvas = mHolder.lockCanvas();  // 锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了
		if(tCanvas == null) {
			return;
		}
		//2012-8-26 onion
		magnifier.Draw(tCanvas);

		//2012-8-26 onion
		mHolder.unlockCanvasAndPost(tCanvas); // 结束锁定画图，并提交改变
		//原：mHolder.unlockCanvasAndPost(canvas); // 结束锁定画图，并提交改变
	}
	/*
	public void run() {
		while (mLoop) {
			try {
				Thread.sleep(16);
				synchronized (mHolder) {        // 代表这个方法加锁
					onDraw();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void onDraw() {
		if (mHolder == null) {
			return;
		}
		try{
			canvas = mHolder.lockCanvas();  // 锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了
			if (canvas == null) {
			return;
			}
			super.onDraw(canvas);

			canvas.drawColor(Color.WHITE); // 背景色
			int color = Color.BLACK; // 画笔颜色
			int width = 3; // 画笔宽度


			GraphControl graphControl = new GraphControl(graphList, color, width);

			graphControl.drawObj(curGraph, canvas); // 绘制当前勾画的草图
			graphControl.drawObjList(canvas); // 绘制已有的图像对象列表
		}catch(Exception ex){
			
		}finally{
			if (mHolder == null) {
				mHolder.unlockCanvasAndPost(canvas);  // 结束锁定画图，并提交改变
			}
		}
	}
	*/

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//isFirstUndoDelete = true;
		int action = event.getAction();
		if (MotionEvent.ACTION_CANCEL == action) {
			return false;
		}

		int multiPoint = event.getPointerCount();
		if (multiPoint == SINGLE_POINT_TOUCH) {
			
			//2012-8-26 onion
			magnifier.CollectPoint((int)event.getX(), (int)event.getY());
			if(event.getAction()==MotionEvent.ACTION_UP)
				magnifier.EndShow();				
			//invalidate();	//调用完上面的一定要调用这个
			
			singleTouch(event);
		} else if (multiPoint == DOUBLE_POINT_TOUCH) {
			doubleTouch(event);
		}
		
		return true;
	}

	private void singleTouch(MotionEvent event) {
		int action = event.getAction();
		int touchX = (int) event.getX();
		int touchY = (int) event.getY();
		int num = 0;
		if(isEidt  && curGraph != null && !isDoubleTouch){  //编辑态
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				userIntentionReasoning.dismiss(); //如果没点选图标，则popupwindow消失
				checkedGraph = curGraph;
				isChecked = isEidt;
				URSolver.RedoStackClear(); // 清空redo栈
				downTime = new Date().getTime();
				Point first = new Point(touchX, touchY);
				if(curGraph.isInGraph(first)){
					//如果点位置在图形上,选中图元
					for(GUnit unit : curGraph.getGraph()){
						if(unit instanceof PointUnit){
							if(((PointUnit)unit).isInLine() && !((PointUnit)unit).isCommonConstrainted())
								continue;
							if(unit.isInUnit(first)){
								curUnit = unit;
								break;
							}
						}
					}
				}else{
					for(GUnit unit : curGraph.getGraph()) {
						//点在圆内 则两点手势时放大缩小，旋转以该图形中心做为中心
						if(unit instanceof CurveUnit) {
							Log.v("点在圆内 ", "点在圆内 ");
							if(((CurveUnit)unit).isInCircle(first)) {
								Log.v("点在圆内 ", "点在圆内 ");
								centerCurve = (CurveUnit) unit;
							}
						}
					}
					isEidt = false;  //如果点位置不在图形上 退出编辑态
					curGraph = new Sketch(touchX, touchY);
				}
				collector.start(); // 启动收集器
				collector.collect(touchX, touchY);
				Log.v("onDown", touchX + ", " + touchY);
				break;
			case MotionEvent.ACTION_MOVE:
				collector.collect(touchX, touchY); // 收集点
				num = pointList.size();
				//平移
				float[][] transMatrix = {{1, 0, touchX - pointList.get(num - 2).getX()}, {0, 1, touchY - pointList.get(num - 2).getY()}, {0, 0, 1}};
				if(curUnit != null){
					//拖拽在直线上的点
					if(((PointUnit)curUnit).isInLine()) {
						//如果是一般约束点 则拖动
						//if(((PointUnit)curUnit).isCommonConstrainted()) {
							LineStrategy.translatePointInLine(curUnit, curGraph, new Point(touchX, touchY));
						//} else {
							
						//}
					} else {
						curGraph.setEqualAngleToF();
						curGraph.setRightAngleToF();
						curUnit.translate(transMatrix);
					}
					Log.v("拖拽点", "拖拽点");
				}else{
					curGraph.translate(curGraph, transMatrix);
				}
				keepConstrainter.keepConstraint(curGraph);				
				Log.v("onMove", touchX + ", " + touchY);
				Log.v("translate", (touchX - pointList.get(num - 2).getX()) + ", " + (touchY - pointList.get(num - 2).getY()));
				break;
			case MotionEvent.ACTION_UP:
				centerCurve = null;
				Log.v("onUp", touchX + ", " + touchY);
				//只有拖动顶点才重新进行图形规整,拖动直线上的点重新识别约束
				if(curUnit != null) {
					if(!((PointUnit)curUnit).isInLine()) {
						curGraph = regulariser.regularise(graphList, curGraph);  //图形规整
					} else {
						if(((PointUnit)curUnit).isCommonConstrainted()) {
							curGraph = keepConstrainter.rebuildGraph(curGraph);
						}
					}
				}
				curUnit = null;
				if(constrainter.constraint(graphList, curGraph) != null){   //动态约束
					curGraph = null;
					checkedGraph = null;
					isEidt = false;
					isChecked = false;
				}
				if(curGraph != null){
					keepConstrainter.keepConstraint(curGraph);
				}
				//URSolver.EnUndoStack(new UndoRedoStruct(OperationType.CHANGE, curGraph.clone()));  //改变图形，undo栈添加
				URSolver.RedoStackClear(); //清空redo
				collector.release(); // 释放收集器
				break;
			default:
				break;
			}
			
		}else{  //识别态
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				userIntentionReasoning.dismiss(); //如果没点选图标，则popupwindow消失
				URSolver.RedoStackClear(); // 清空redo栈
				downTime = new Date().getTime();
				collector.start(); // 启动收集器
				collector.collect(touchX, touchY);
				curGraph = new Sketch(touchX, touchY);
				Log.v("onDown", touchX + ", " + touchY);
				break;
			case MotionEvent.ACTION_MOVE:
				//处理两点手势 一点弹起
				if(isDoubleTouch){
					event1Points.clear();
					event2Points.clear();
					collector.release();
					curGraph = null;
					isRotate = true;
					isScale = true;
					//isDoubleTouch = false;
				}
				collector.collect(touchX, touchY); // 收集点
				if(curGraph != null){
					curGraph.move(touchX, touchY);
				}
				break;
			case MotionEvent.ACTION_UP:
				centerCurve = null;
				//处理两点手势 弹起
				if(isDoubleTouch){
					isRotate = true;
					isScale = true;
					event1Points.clear();
					event2Points.clear();
					collector.release();
					Log.v("两点手势", "两点手势");
					isDoubleTouch = false;
					if(isChecked){
						isEidt = true;
						isChecked = true;
						curGraph = checkedGraph;
						if(constrainter.constraint(graphList, curGraph) != null){   //动态约束
							curGraph = null;
							checkedGraph = null;
							isEidt = false;
							isChecked = false;
						}
					}
					//URSolver.EnUndoStack(new UndoRedoStruct(OperationType.CHANGE, checkedGraph.clone())); //改变图形，undo栈添加
					break;
				}
				Log.v("onUp", touchX + ", " + touchY);
				upTime = new Date().getTime();
				PenInfo penInfo = new PenInfo(pointList);
				//选中图形
				if (upTime - downTime > ThresholdProperty.PRESS_TIME_SHORT
						&& penInfo.isFixedPoint(pointList)) { // 选中对象
					Graph graph = graphFactory.getCheckedGraph(graphList, new PointUnit(pointList).getPoint());
					if (graph != null && graph != checkedGraph) {
						graph.setChecked(true); // 选中图形
						if(isChecked){    //如果已有选中图形
							Log.v("已选中图形", "已选中图形");
							checkedGraph.setChecked(false);
							URSolver.EnUndoStack(new UndoRedoStruct(OperationType.CHANGE, checkedGraph.clone())); //改变图形，undo栈添加
						}
						isEidt = true; // 进入编辑态
						isChecked = true;
						curGraph = graph;
						checkedGraph = graph;
						collector.release(); // 释放收集器
						URSolver.EnUndoStack(new UndoRedoStruct(OperationType.CHANGE, curGraph.clone())); //改变图形，undo栈添加
						URSolver.RedoStackClear(); //清空redo
						//选中图形后显示用户意图推测
						userIntentionReasoning.regulariseReasoning(this, graph, touchX, touchY);
						break;
					}
				}
				//图形识别
				Graph graph = graphFactory.create(penInfo.getNewPenInfo()); // 传入预处理后的点信息给图形工厂去识别，然后返回识别后的对象
				if (graph != null) {
					curGraph = graph;
				}
				//删除手势,撤销选中
				if(isChecked){
					isChecked = false;
					//删除手势
					checkedGraph.setChecked(false);
					URSolver.EnUndoStack(new UndoRedoStruct(OperationType.CHANGE, checkedGraph.clone())); //改变图形，undo栈添加
					//如果在选中图形外点一点 则识别为撤销选中
					if(penInfo.isFixedPoint(pointList)){
						URSolver.RedoStackClear(); //清空redo
						curGraph = null;
						checkedGraph = null;
						collector.release(); // 释放收集器
						break;
					}
					//如果是删除手势
					if(Recognizer.isDeleteGesture(pointList)){
						Log.v("删除图形", "删除图形");
						graphList.remove(checkedGraph);
						URSolver.EnUndoStack(new UndoRedoStruct(OperationType.DELETE, checkedGraph.clone())); //改变图形，undo栈添加
						URSolver.RedoStackClear(); //清空redo
						curGraph = null;
						checkedGraph = null;
						collector.release(); // 释放收集器
						break;
					}else{
						//URSolver.EnUndoStack(new UndoRedoStruct(OperationType.CHANGE, checkedGraph.clone())); //改变图形，undo栈添加
					}
				}
				curGraph = regulariser.regularise(graphList, curGraph);  //图形规整
				//图形约束
				curGraph = constrainter.constraint(graphList, curGraph);  // 对新构造的图形进一步约束识别
				if(curGraph != null) { //有约束
					if(curGraph instanceof TriangleGraph && curGraph.getConstraint()) {
						//约束线用户意图推测
						userIntentionReasoning.constraintReasoning(this,curGraph, touchX, touchY);
					}
				}
				URSolver.RedoStackClear(); //清空redo
				curGraph = null;
				checkedGraph = null;
				collector.release(); // 释放收集器
				break;
			default:
				break;
			}
		}
	}
	
	/*
	private void singleTouch(MotionEvent event) {
		int action = event.getAction();
		int touchX = (int) event.getX();
		int touchY = (int) event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			//isRecognize = false;
			downTime = new Date().getTime();
			collector.start(); // 启动收集器
			collector.collect(touchX, touchY);
			if(!isEidt){
				curGraph = new Sketch(touchX, touchY); // 用于勾画当前草图
			}
			Log.v("onDown", touchX + ", " + touchY);
			break;
		case MotionEvent.ACTION_MOVE:
			collector.collect(touchX, touchY); // 收集点
			if(!isEidt){
				curGraph.move(touchX, touchY);
			}else{
				PointUnit temp = (PointUnit)curGraph.getGraph().get(0);
				//List<Point> pList = collector.getPenInfoList();
				double[][] transMatrix = {{1, 0.0, touchX - temp.getX()}, {0, 1, touchY - temp.getY()}, {0, 0, 1}};
				Log.v("translate", (touchX - temp.getX()) / 2 + "|" + (touchY - temp.getY()) / 2);
				curGraph.translate(curGraph, transMatrix);
				Log.v("move", temp.getX() + ","+temp.getY());
			}
			Log.v("onMove", touchX + ", " + touchY);
			break;
		case MotionEvent.ACTION_UP:
			Log.v("onUp", touchX + ", " + touchY);
			upTime = new Date().getTime();
			List<Point> pList = collector.getPenInfoList(); // 收集到的点信息，未处理过的
			Log.v("num", pList.size()+"");
			if(isEidt){
				//curGraph.setChecked(false);
				//isEidt = false;
			}else{
			PenInfo penInfo = new PenInfo(pList);

			if (upTime - downTime > ThresholdProperty.PRESS_TIME_SHORT
					&& penInfo.isFixedPoint(pList)) { // 选中对象
				Graph graph = graphFactory.getCheckedGraph(graphList,
						new PointUnit(pList).getPoint());
				if (graph != null) {
					graph.setChecked(true); // 选中图形
					isEidt = true; // 进入编辑态
					curGraph = graph;
					URSolver.EnUndoStack(new UndoRedoStruct(OperationType.CREATE, curGraph.clone()));
					//Log.v("first", ((PointUnit)curGraph.getGraph().get(0)).getX() + ","+((PointUnit)curGraph.getGraph().get(0)).getY());
				}
			} else {
				Graph graph = graphFactory.create(penInfo.getNewPenInfo()); // 传入预处理后的点信息给图形工厂去识别，然后返回识别后的对象
				if (graph != null) {
					curGraph = graph;
				}
				constrainter.constraint(graphList, curGraph, curUnit); // 对新构造的图形进一步约束识别
				
				//URSolver.EnUndoStack(new UndoRedoStruct(OperationType.CREATE, curGraph));
				URSolver.RedoStackClear();
				
				curGraph = null;
				//isRecognize = true;
			}
			}

			collector.release(); // 释放收集器
			break;
		default:
			break;
		}
	}
	*/

	private List<Point> event1Points = new ArrayList<Point>();
	private List<Point> event2Points = new ArrayList<Point>();
	private static final int SAMPLE_COUNT = 5;   //每5点判断一次
	private int flagCount = 0;  
	//private Point AB; // 向量AB
	private boolean isNarrowOrEnlarge ;   //放大 or 缩小
	private float cosA = 0, sinA = 0;
	private boolean isclockwise;       //顺时针 or 逆时针
	//private boolean isTouchUp = true;
	//private int a = 0, b = 1;

	//cwq
	private CurveUnit centerCurve = null;
	private boolean isRotate = true, isScale = true;
	/*
	 * 双点触摸， 缩放手势，旋转手势，删除手势，还未完成，暂时不用去管
	 */
	private void doubleTouch(MotionEvent event) {
		isDoubleTouch = true;
		if(isChecked){
			isEidt = false;
			//isDoubleTouch = true;
			/*
			//规整输入，以靠近坐标原点的为first点，反之则为second点
			if(isTouchUp){
				int action = event.getAction();
				int firstX = (int) event.getX(0);
				int firstY = (int) event.getY(0);
				int secondX = (int) event.getX(1);
				int secondY = (int) event.getY(1);
				isTouchUp = false;
				if( (CommonFunc.distance(new Point(firstX,firstY), new Point()) ) > CommonFunc.distance(new Point(secondX,secondY), new Point()) ){
					a = 1;
					b = 0;
				}
			}
			*/
			int action = event.getAction();
			int firstX = (int) event.getX(0);
			int firstY = (int) event.getY(0);
			int secondX = (int) event.getX(1);
			int secondY = (int) event.getY(1);
			
			//Log.v("1,2",firstX+","+firstY+"   "+secondX+","+secondY);
			switch (action) {
				case MotionEvent.ACTION_DOWN:
					userIntentionReasoning.dismiss(); //如果没点选图标，则popupwindow消失
					collector.release();
					break;
				case MotionEvent.ACTION_MOVE:
					event1Points.add(new Point(firstX, firstY));
					event2Points.add(new Point(secondX, secondY));
					if (event1Points.size() == SAMPLE_COUNT) {
						if (flagCount != 0) { // 首部5个采样点不处理
							Point baseVector = VectorFunc.subtract(event2Points.get(0),
									event1Points.get(0));
							Point vector1 = VectorFunc.subtract(
									event1Points.get(SAMPLE_COUNT - 1),
									event1Points.get(0));
							Point vector2 = VectorFunc.subtract(
									event2Points.get(SAMPLE_COUNT - 1),
									event2Points.get(0));
							isNarrowOrEnlarge = CommonFunc.NarrowOrEnlarge(event1Points.get(0), event2Points.get(0), event1Points.get(SAMPLE_COUNT - 1), event2Points.get(SAMPLE_COUNT - 1));
							int type = VectorFunc.translation(baseVector, vector1, vector2);		
							if (type == 1 && isScale) {
								isRotate = false;
								if(isNarrowOrEnlarge){
									float[][] transMatrixscalenarrow = {{(float) 0.9, 0, 0}, 
																		{0, (float) 0.9, 0}, 
																		{0, 0, 1}}; //缩小矩阵
									checkedGraph.scale(checkedGraph, transMatrixscalenarrow, centerCurve);
								}else{
									float[][] transMatrixscaleenlarge = {{(float) 1.13, 0, 0}, 
																		  {0, (float) 1.13, 0}, 
																		  {0, 0, 1}};	//放大矩阵
									checkedGraph.scale(checkedGraph, transMatrixscaleenlarge, centerCurve);
								}
									
								Log.v("translate", "scale");
							} else if (type == 2 && isRotate) {
								isScale = false;
								cosA = (float) CommonFunc.rotatecos(baseVector, vector1, vector2);
								sinA = (float) Math.sqrt(1-cosA*cosA);
								isclockwise = CommonFunc.isClockWise(baseVector, vector1, vector2);
								if(!isclockwise){
									float[][] rotateMatrix = {{cosA, -sinA, 0}, 
															   {sinA, cosA, 0}, 
															   {0, 0, 1}};	 //逆时针旋转矩阵
									checkedGraph.rotate(checkedGraph, rotateMatrix, centerCurve);
									Log.v("translate", "alongrotate");
								}else{
									float[][] rotateMatrix = {{cosA, sinA, 0}, 
															   {-sinA, cosA, 0}, 
															   {0, 0, 1}};	//顺时针旋转矩阵
									checkedGraph.rotate(checkedGraph, rotateMatrix, centerCurve);
									Log.v("translate", "wiserrotate");
								}
							} else {
								Log.v("translate", "null");
							}
						}
						keepConstrainter.keepConstraint(checkedGraph);
						flagCount++;
						event1Points.clear();
						event2Points.clear();
						collector.release();
					}
					break;
				case MotionEvent.ACTION_UP:
					event1Points.clear();
					event2Points.clear();
					collector.release();
					centerCurve = null;
					isRotate = true;
					isScale = true;
//					isTouchUp = true;
					break;
				default:
					break;
			}
			}	
		}

	//功能
    public void clear(){
    	userIntentionReasoning.dismiss(); //如果没点选图标，则popupwindow消失
    	isEidt = false;
    	isChecked = false;
    	curGraph = null;
    	checkedGraph = null;
    	//isFirstUndoDelete = true;
    	URSolver.RedoStackClear();  //清空reod undo栈
    	URSolver.UndoStackClear();
    	graphList.clear();
    }
    
	public void Undo() {
		userIntentionReasoning.dismiss(); //如果没点选图标，则popupwindow消失
		curGraph = null;
		if(URSolver.isUndoStackEmpty()) {
			Log.v("Undo", "empty");
			return;	
		}
		UndoRedoStruct temp;
		temp = URSolver.popUndoStack();
		Graph tempGraph;
		tempGraph = temp.getGraph();
		switch(temp.getOperationType())
		{
		case NONE:
			break;
		case CREATE:
			for(Graph graph : graphList){
				if(tempGraph.getID() == graph.getID()){
						/*
						tempGraph = URSolver.getFrontGraph(graph);
						if(tempGraph != null){
							Log.v("恢复改变之前图形", "恢复改变之前图形");
							tempGraph = tempGraph.clone();
							graphList.add(tempGraph); //添加约束,选中前的图形
							if(tempGraph.isChecked()){  //如果添加的图形是选中的图形
								isEidt = true;
								//isChecked = true;
								curGraph = tempGraph;
							}else{
								isEidt = false;  //如果添加的图形不是选中的图形
								//isChecked = false;
								curGraph = null;
							}
						}
						
						*/
					graphList.remove(graph);
					Log.v("undo", graphList.size() + "");
					return;
				}
			}
			break;
		case CHANGE:
			for(Graph graph : graphList){
				if(tempGraph.getID() == graph.getID()){
						tempGraph = URSolver.getFrontGraph(graph);
						if(tempGraph != null){
							Log.v("恢复改变之前图形", "恢复改变之前图形");
							tempGraph = tempGraph.clone();
							graphList.add(tempGraph); //添加约束,选中前的图形
							if(tempGraph.isChecked()){  //如果添加的图形是选中的图形
								isEidt = true;
								isChecked = true;
								curGraph = tempGraph;
								checkedGraph = tempGraph;
							}else{
								isEidt = false;  //如果添加的图形不是选中的图形
								isChecked = false;
								curGraph = null;
								checkedGraph = null;
							}
						}
					graphList.remove(graph);
					Log.v("undo", graphList.size() + "");
					return;
				}
			}
			break;
		case DELETE:
			//删除图形后撤销
			//if(tempGraph != null){
				Log.v("删除图形后撤销", "删除图形后撤销");
				/*
				if(isFirstUndoDelete){   //仅当第一次undo 恢复删除图形时  undo栈添加刚出栈的图形
					URSolver.EnUndoStack(new UndoRedoStruct(OperationType.CHANGE, tempGraph)); //添加被删除的图形，undo栈添加刚出栈的图形
					isFirstUndoDelete = false;
				}
				*/
				tempGraph = tempGraph.clone();
				graphList.add(tempGraph); //添加被删除的图形
				if(tempGraph.isChecked()){  //如果添加的图形是选中的图形
					isEidt = true;
					isChecked = true;
					curGraph = tempGraph;
					checkedGraph = tempGraph;
				}else{
					isEidt = false;  //如果添加的图形不是选中的图形
					isChecked = false;
					curGraph = null;
					checkedGraph = null;
				}
				break;
			//}
		case MOVEANDCONSTRAIN:
			for(Graph graph : graphList){
				if(tempGraph.getID() == graph.getID()){
						tempGraph = URSolver.getFrontGraph(graph);
						if(tempGraph != null){
							Log.v("恢复动态约束前的图形", "恢复动态约束前的图形");
							tempGraph = tempGraph.clone();
							graphList.add(tempGraph); //添加约束前的图形
						}
					graphList.remove(graph);
					tempGraph = URSolver.peekUndoStack().getGraph().clone();  //添加动态约束前选择的图形
					isEidt = true;
					isChecked = true;
					curGraph = tempGraph;
					checkedGraph = tempGraph;
					graphList.add(tempGraph);
					Log.v("undo", graphList.size() + "");
					return;
				}
			}
			break;
		default:
			break;
		}
	}	
	
	public void Redo() {
		if(URSolver.isRedoStackEmpty()) {
			Log.v("Redo", "empty");	
			return;	
		}
		userIntentionReasoning.dismiss(); //如果没点选图标，则popupwindow消失
		UndoRedoStruct temp;
		temp = URSolver.popRedoStack();
		Graph tempGraph;
		tempGraph = temp.getGraph();
		switch(temp.getOperationType())
		{
		case NONE:
			break;
		case CREATE:
			/*
			for(Graph graph : graphList){
				if(tempGraph.getID() == graph.getID()){
					graphList.remove(graph);
					break;
				}
			}
			*/
			tempGraph = tempGraph.clone();
			if(tempGraph.isChecked()){  //如果要恢复的图形是选中的图形
				isEidt = true;
				isChecked = true;
				curGraph = tempGraph;
				checkedGraph = tempGraph;
			}else{
				isEidt = false;  //如果添加的图形不是选中的图形
				isChecked = false;
				curGraph = null;
				checkedGraph = null;
			}
			graphList.add(tempGraph);
			Log.v("redo", graphList.size() + "");
			break;
		case CHANGE:
			for(Graph graph : graphList){
				if(tempGraph.getID() == graph.getID()){
					graphList.remove(graph);
					tempGraph = tempGraph.clone();
					if(tempGraph.isChecked()){  //如果要恢复的图形是选中的图形
						isEidt = true;
						isChecked = true;
						curGraph = tempGraph;
						checkedGraph = tempGraph;
					}else{
						isEidt = false;  //如果添加的图形不是选中的图形
						isChecked = false;
						curGraph = null;
						checkedGraph = null;
					}
					graphList.add(tempGraph);
					Log.v("redo", graphList.size() + "");
					return;
				}
			}
			break;
		case DELETE:
			for(Graph graph : graphList){
				if(tempGraph.getID() == graph.getID()){
					Log.v("恢复删除操作", "恢复删除操作");
					graphList.remove(graph);
					isEidt = false;
					isChecked = false;
					curGraph = null;
					checkedGraph = null;
				}
			}
			break;
		case MOVEANDCONSTRAIN:
			for(Graph graph : graphList){
				if(tempGraph.getID() == graph.getID()){
					graphList.remove(graph);
					graphList.remove(curGraph);  //删除之前移动的约束的图形
					tempGraph = tempGraph.clone();
					isEidt = false;  //如果添加的图形不是选中的图形
					isChecked = false;
					curGraph = null;
					checkedGraph = null;
					graphList.add(tempGraph);
					Log.v("redo", graphList.size() + "");
					return;
				}
			}
			break;
		default:
			break;
		}
	}
	
	public void save(String name){
		userIntentionReasoning.dismiss(); //如果没点选图标，则popupwindow消失
		fileServicer.save(graphList, name);
	}
	
	public void open(String path){
		Object temp = fileServicer.read(path);
		if(temp != null){
			userIntentionReasoning.dismiss(); //如果没点选图标，则popupwindow消失
			graphList = (List<Graph>)temp;
			URSolver.RedoStackClear();  //清空reod undo栈
	    	URSolver.UndoStackClear();
	    	for(Graph graph : graphList){
	    		if(graph.isChecked()){
	    			isEidt = true;
					isChecked = true;
					curGraph = graph;
					checkedGraph = graph;
					return;
	    		}
	    	}
	    	isEidt = false;
			isChecked = false;
			curGraph = null;
			checkedGraph = null;
		}
	}
}
