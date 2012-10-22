/*
 * 图形绘制控制类
 * 控制图像对象的绘制流程等
 * */

package com.sg.control;

import java.util.Iterator;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.sg.object.graph.Graph;
import com.sg.property.tools.Painter;

public class GraphControl {
	
	private List<Graph> graphList;
	private Painter painter;
	private Painter checkedPainter;
	
	
	public GraphControl(List<Graph> graphList, int color, int width) {
		this.graphList = graphList;
		painter = new Painter(color, width);
		checkedPainter = new Painter(Color.RED, 3);
	}
	
	/*
	 * 在画板canvas上绘制对象列表
	 * */
	public void drawObjList(Canvas canvas) {
		//int size = graphList.size();
		//canvas.drawText("同步", 0, 2, 10, 10, checkedPainter.getPaint());
		//Log.v("size1", graphList.size() + "");
		
		for(int num = 0; num < graphList.size(); num++) {
			/*
			//size = graphList.size();
			if(num >= graphList.size()){
				canvas.drawText("不同步", 0, 3, 10, 10, checkedPainter.getPaint());
				Log.v("不同步", "");
				return;
			}
			*/
			//Log.v("size2", num + "");
			Graph graph = graphList.get(num);
			this.drawObj(graph, canvas);
		}
		/*
		Iterator<Graph> it = graphList.iterator();
		while(it.hasNext())
		{
			Graph graph = it.next();
			this.drawObj(graph, canvas);
		}*/
	}
	
	/*
	 * 在画板canvas上绘制graph对象
	 * */
	public void drawObj(Graph graph, Canvas canvas) {
		if(graph != null) {
			if(graph.isChecked()) {
				graph.draw(canvas, checkedPainter);
			} else {
				graph.draw(canvas, painter);
			}
		}
	}
	
	/*
	 * 添加对象到绘制列表
	 * */
	public void addObj(Graph graph) {
		if(graph != null && !graphList.contains(graph)) {
			graphList.add(graph);
		}
	}
	
	/*
	 * 返回绘制对象列表
	 * */
	public List<Graph> getGraphList() {
		return graphList;
	}

}
