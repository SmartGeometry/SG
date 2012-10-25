/*
 * 图形变换接口
 * */
package com.sg.logic.strategy;

import com.sg.object.Point;
import com.sg.object.graph.Graph;
import com.sg.object.unit.CurveUnit;
import com.sg.object.unit.PointUnit;

public interface TranslationStratery {
	
    /*
     * 平移
     * */
	public void translate(Graph graph, float[][] transMatrix);
	
	/*
	 * 伸缩
	 * */
	public void scale(Graph graph, float[][] scaleMatrix, CurveUnit centerCurve);
	
	/*
	 * 旋转
	 * */
	public void rotate(Graph graph, float[][] rotateMatrix, CurveUnit centerCurve);
}
