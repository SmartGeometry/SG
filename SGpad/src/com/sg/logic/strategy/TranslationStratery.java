/*
 * 图形变换接口
 * */
package com.sg.logic.strategy;

import com.sg.object.graph.Graph;

public interface TranslationStratery {
	
    /*
     * 平移
     * */
	public void translate(Graph graph, float[][] transMatrix);
	
	/*
	 * 伸缩
	 * */
	public void scale(Graph graph, float[][] scaleMatrix);
	
	/*
	 * 旋转
	 * */
	public void rotate(Graph graph, float[][] rotateMatrix);
}
