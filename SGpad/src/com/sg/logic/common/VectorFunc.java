package com.sg.logic.common;

import com.sg.object.Point;

public class VectorFunc {
	
	private static final int SCALE_TRANSLATION = 1;
	private static final int ROTATE_TRANSLATION = 2;
	private static final int NON_TRANSLATION = 0;

	public static Point subtract(Point vector1, Point vector2) {
		float X = vector2.getX() - vector1.getX();
		float Y = vector2.getY() - vector1.getY();
		return new Point(X, Y);
	}
	
	//向量点乘
	public static double multiply(Point vector1, Point vector2) {
		float X = vector1.getX() * vector2.getX();
		float Y = vector1.getY() * vector2.getY();
		return X+Y;
	}
	
	//判断两点手势类型
	public static int translation(Point baseVector, Point vector1, Point vector2) {
		double bvToV1 = 0.0, bvToV2 = 0.0, v1ToV2 = 1.0;
/*	
		if(isNotZero(vector1) && !isNotZero(vector2)) {
			
			bvToV1 = direction(baseVector, vector1);
			if(Math.abs(bvToV1) > Math.cos(3.14 / 5)) {
				return SCALE_TRANSLATION;
			} else if(Math.abs(bvToV1) < Math.cos(3.14 / 4)) {
				return ROTATE_TRANSLATION;
			}
		} else if(isNotZero(vector2) && !isNotZero(vector1)) {
			bvToV2 = direction(baseVector, vector2);
			if(Math.abs(bvToV2) > Math.cos(3.14 / 6)) {
				return SCALE_TRANSLATION;
			} else if(Math.abs(bvToV2) < Math.cos(3.14 / 4)) {
				return ROTATE_TRANSLATION;
			}
		} else if(isNotZero(vector1) && isNotZero(vector2)) {
			bvToV1 = direction(baseVector, vector1);
			bvToV2 = direction(baseVector, vector2);
			v1ToV2 = direction(vector1, vector2);
			
			if(v1ToV2 > Math.cos(3.14 / 5)) {
				
				if((Math.abs(bvToV1) > Math.cos(3.14 / 6)) && (Math.abs(bvToV2) > Math.cos(3.14 / 6))) {
					return SCALE_TRANSLATION;
				} else if((Math.abs(bvToV1) < Math.cos(3.14 / 4)) && (Math.abs(bvToV2) < Math.cos(3.14 / 4))) {
					return ROTATE_TRANSLATION;
				}
			}
		}
		
		return NON_TRANSLATION;			
*/
		if( isNotZero(vector1) && !isNotZero(vector2) ){
			bvToV1 = direction(baseVector,vector1);
			if( Math.abs(bvToV1) > Math.cos(3.14/7) ){
				return SCALE_TRANSLATION;
			}
			if( Math.abs(bvToV1) < Math.cos(3.14/4) ){
				return ROTATE_TRANSLATION;
			}
		}else if( !isNotZero(vector1) && isNotZero(vector2) ){
			bvToV2 = direction(baseVector,vector2);
			if( Math.abs(bvToV2) > Math.cos(3.14/7) ){
				return SCALE_TRANSLATION;
			}
			if( Math.abs(bvToV2) < Math.cos(3.14/4) ){
				return ROTATE_TRANSLATION;
			}
		}else{
			bvToV1 = direction(baseVector,vector1);
			bvToV2 = direction(baseVector,vector2);
			v1ToV2 = direction(vector1,vector2);
			if( Math.abs(bvToV1) > Math.cos(3.14/7) && Math.abs(bvToV2) > Math.cos(3.14/7) && Math.abs(v1ToV2) > Math.cos(3.14/6)){
				return SCALE_TRANSLATION;
			}
			if( Math.abs(bvToV1) < Math.cos(3.14/4) && Math.abs(bvToV2) < Math.cos(3.14/4)){
				return ROTATE_TRANSLATION;
			}
		}
		return NON_TRANSLATION;
	}
	
	/*
	 * 两个向量夹角的余弦值
	 * */
	public static double direction(Point vector1, Point vector2) {
		double cosValue = multiply(vector1, vector2) / (distance(vector1) * distance(vector2));
		return cosValue;
	}
	
	/*
	 * 判断是否非零向量
	 * */
	public static boolean isNotZero(Point vector) {
		return !(vector.getX() == 0 && vector.getY() == 0);
	}
	
	/*
	 * 向量的模
	 * */
	public static double distance(Point vector) {
		return CommonFunc.distance(vector, new Point());
	}
	
	/*
	 * 判断三个向量之间的夹角是否相等
	 * */
	public static boolean equalangle(Point vector1, Point vector2, Point midVector){
		double angle1 = Math.acos(direction(vector1,midVector));
		double angle2 = Math.acos(direction(vector2,midVector));
		if(Math.abs(angle1*180/3.14-angle2*180/3.14)<12.00){
			return true;
		}else{
			return false;
		}
	}
}
