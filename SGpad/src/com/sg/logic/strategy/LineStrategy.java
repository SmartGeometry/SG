package com.sg.logic.strategy;

import java.io.Serializable;
import java.util.List;

import com.sg.object.graph.Graph;
import com.sg.object.unit.GUnit;
import com.sg.object.graph.PointGraph;
import com.sg.object.unit.PointUnit;

public class LineStrategy implements TranslationStratery, Serializable{
	/*
	 * 平移操作可以不要用相对中心的坐标
	 */
	
	//平移点元
	public static void translatePoint(GUnit unit, float[][] transMatrix){
		((PointUnit)unit).setX(((PointUnit)unit).getX() + transMatrix[0][2]);
		((PointUnit)unit).setY(((PointUnit)unit).getY() + transMatrix[1][2]);
	}
	
	@Override
	public void translate(Graph graph, float[][] transMatrix) {
		/*平移矩阵
		 * 1, 0, Tx 
		 * 0, 1, Ty
		 * 0, 0, 1
		 * 点坐标为（x,y,1）
		 */
		List<GUnit> gUnit = graph.getGraph();
		for(GUnit unit : gUnit){
			if(unit instanceof PointUnit){
				LineStrategy.translatePoint(unit, transMatrix);
			}
		}
	}

	/*因为伸缩，旋转操作是相对于图形中心的--为方便计算，定义图形中心为点的平均坐标（Sx/n,Sy/n）
	 * 将图形点坐标化为相对于中心的坐标，矩阵变换之后再还原为系统坐标
	 */
	@Override
	public void scale(Graph graph, float[][] scaleMatrix) {
		/*伸缩矩阵
		 * Tx, 0, 0 
		 * 0 ,Ty, 0
		 * 0 , 0, 1
		 * 点坐标为（x,y,1）
		 */
		List<GUnit> gUnit = graph.getGraph();

		//如果图形是一个点,没有伸缩变换
		if(graph instanceof PointGraph){
			return;
		}
		else{
			
			//求中心坐标
			float x = 0, y = 0; 
			int n = 0;  //n记录点元个数
			for(GUnit unit : gUnit){
				if(unit instanceof PointUnit){
					n ++;
					x += ((PointUnit)unit).getX();
					y += ((PointUnit)unit).getY();
				}
			}
			x /= n;          
			y /= n;
			
			//变换
			for(GUnit unit : gUnit){
				if(unit instanceof PointUnit){
					//将图形点坐标化为相对于中心的坐标，矩阵变换之后再还原为系统坐标
					((PointUnit)unit).setX(((((PointUnit)unit).getX() - x) * scaleMatrix[0][0]) + x);
					((PointUnit)unit).setY(((((PointUnit)unit).getY() - y) * scaleMatrix[1][1]) + y);
				}
			}
		}
	}

	@Override
	public void rotate(Graph graph, float[][] rotateMatrix) {
		/*顺时针旋转矩阵
		 *cosQ, -sinQ, 0 
		 * sinQ, cosQ, 0
		 * 0   , 0   , 1
		 * 点坐标为（x,y,1）
		 */
		List<GUnit> gUnit = graph.getGraph();
		
		//如果图形是一个点,可以当做没有旋转变换
		if(graph instanceof PointGraph){
			return;
		}
		else{
			
			//求中心坐标
			float x = 0, y = 0;
			int n = 0;  //n记录点元个数
			for(GUnit unit : gUnit){
				if(unit instanceof PointUnit){
					n ++;
					x += ((PointUnit)unit).getX();
					y += ((PointUnit)unit).getY();
				}
			}
			x /= n;          
			y /= n;
			
			//变换
			float tempX;
			float tempY;
			for(GUnit unit : gUnit){
				if(unit instanceof PointUnit){
					//将图形点坐标化为相对于中心的坐标，矩阵变换之后再还原为系统坐标
					tempX = ((PointUnit)unit).getX() - x;
					tempY = ((PointUnit)unit).getY() - y;
					((PointUnit)unit).setX((tempX * rotateMatrix[0][0] + tempY * rotateMatrix[0][1]) + x);
					((PointUnit)unit).setY((tempX * rotateMatrix[1][0] + tempY * rotateMatrix[1][1]) + y);
				}
			}
		}
	}
}
