package com.sg.object.unit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sg.logic.common.CommonFunc;
import com.sg.logic.common.CurveType;
import com.sg.object.Point;
import com.sg.property.common.ThresholdProperty;
import com.sg.property.tools.Painter;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;

public class CurveUnit extends GUnit implements Serializable {

	// 一般方程：aX^2 + bXY + cY^2 + dX + eY + f = 0
	//长轴倾角:alpha= 1/2 arctan (b/(a - c))
	//几何中心:Xc = (2cd - be) / (b^2 – 4ac)
	//		Yc = (2ae – bd) / (b^2 – 4ac)

	private final int n = 5; 					// 二次曲线一般方程系数个数
	private double f = 1000000;			// 方程中 f的取值
	private double a, b, c, d, e;				// 一般方程系数，方便写表达式
	private double sa, sb, sc, sd, se, sf;		// 变换后标准方程系数

	private double fact[] = new double[n+1]; 	// 一般方程系数	
	private double sfact[] = new double[n+1]; 	// 标准化系数
	private CurveType curveType;
	private List<Point> pList;
	
	private PointUnit center;     //曲线中心
	private double radius;    //圆的半径
	private boolean isClosed;   //曲线是否闭合
	private double alpha;  //旋转角
	private double Cos, Sin;  //cos(alpha), sin(alpha)

	/*
	 * public CurveUnit() { for (int i = 0; i < n; i++) { fact[i] = 0; } }
	 * 
	 * public CurveUnit(float[] f) { for (int i = 0; i < n; i++) { fact[i] =
	 * f[i]; } }
	 */

	public CurveUnit(List<Point> pList) {

		curveType = CurveType.None;
		fact[5] = f;

		if (pList.size() < n)
			return;

//		pList = getTestCircle1();
//		pList = getTestCircle2();
//		pList = getTestOval1();
//		pList = getTestOval2();
		
		//for (int i = 0; i < pList.size(); i++)	Log.v("Curve", "pList.add(new Point(" + pList.get(i).getX() + "f, " + pList.get(i).getY() + "f));");

		//FirstRec_basic(pList);		// 首次识别——直接取点
		FirstRec_LastSquare(pList);		// 首次识别——最小二乘法
		if(curveType != CurveType.Other)
		{
			SecondRec_TypeAndStandard();	// 二次识别——类型与标准化
			this.pList = findCurvePoint(pList);
		} else {
			this.pList = pList;
		}
//		Log.v("Curve", curveType.toString() + " " + a + ", " + b
//				+ ", " + c + ", " + d + ", " + e + " | " + (b*b-4*a*c));

	}
	
	// 初步识别——最小二乘法
	private void FirstRec_LastSquare(List<Point> pList) {

		// aX^2 + bXY + cY^2 + dX + eY + 1 = 0
		// 根据最小二乘法，先构造矩阵
		// | ∑x(4)y(0) ∑x(3)y(1) ∑x(2)y(2) ∑x(3)y(0) ∑x(2)y(1) ∑x(2)y(0) |
		// | ∑x(3)y(1) ∑x(2)y(2) ∑x(1)y(3) ∑x(2)y(1) ∑x(1)y(2) ∑x(1)y(1) |
		// | ∑x(2)y(2) ∑x(1)y(3) ∑x(0)y(4) ∑x(1)y(2) ∑x(0)y(3) ∑x(0)y(2) |
		// | ∑x(3)y(0) ∑x(2)y(1) ∑x(1)y(2) ∑x(2)y(0) ∑x(1)y(1) ∑x(1)y(0) |
		// | ∑x(2)y(1) ∑x(1)y(2) ∑x(0)y(3) ∑x(1)y(1) ∑x(0)y(2) ∑x(0)y(1) |

		final double standard_deviation = 0.1;	// 二次曲线标准差
		double[][] mEle = new double[n][n]; // 取前 n 个点作高斯消元求系数
		double[][] powNum = new double[n][2]; // 每行公共的 x, y 系数次方

		// x
		powNum[0][0] = 2;
		powNum[1][0] = 1;
		powNum[2][0] = 0;
		powNum[3][0] = 1;
		powNum[4][0] = 0;

		// y
		powNum[0][1] = 0;
		powNum[1][1] = 1;
		powNum[2][1] = 2;
		powNum[3][1] = 0;
		powNum[4][1] = 1;
		
		// 构造矩阵
		for (int j = 0; j < n; j++) {	// row
			fact[j] = 0;	// fact init
			for (int k = 0; k < n; k++) {	// col
				mEle[j][k] = 0;	//matrix element init
				for (int i = 0; i < pList.size(); i++) {	// each point
					mEle[j][k] += Math.pow(pList.get(i).getX(), powNum[j][0] + powNum[k][0])
								* Math.pow(pList.get(i).getY(), powNum[j][1] + powNum[k][1]);
					if (k == 0) 
						fact[j] -= Math.pow(pList.get(i).getX(), powNum[j][0])
								* Math.pow(pList.get(i).getY(), powNum[j][1]) * fact[5];
				}
			}
		}

		if (gauss(mEle, fact) == 0) { 	// 高斯消元出现非唯一解
			curveType = CurveType.None;
			return;
		}
		if (calSD(pList) >= standard_deviation) {	//标准差过大，非二次曲线	
			curveType = CurveType.Other;
			return;
		}
		a = fact[0];
		b = fact[1];
		c = fact[2];
		d = fact[3];
		e = fact[4];
	}
	
	//计算标准差
	private double calSD(List<Point> pList) {
		
		// 对所有点求标准差以分析拟合度
        // 二次曲线中心点为
        // Y = (2ae - bd) / (b^2 - 4ac)
        // X = -d/(2a) - bY/(2a)
        // d = f - (aX^2 + bXY + cY^2)
        // Q(x,y) = a(x-x0)^2 + b(x-x0)(y-y0) + c(y-y0)^2 + d;
		
		if(fact[1]*fact[1] > 4*fact[0]*fact[2])	//if (b*b > 4*a*c)
        {
                //此时为双曲线的情况
                curveType = CurveType.Hyperbola;
        }
		
        int number =pList.size();
        double y0 = ( 2*fact[0]*fact[4] - fact[1]*fact[3] ) / ( fact[1]*fact[1] - 4*fact[0]*fact[2] );	// ( 2 * a * e - b * d ) / ( b^2 - 4 * a * c )
        double x0 = ( -(fact[3] + fact[1]*y0) ) / ( 2*fact[0] );	// ( -( d + b * y0 ) ) / ( 2 * a )
        double d = fact[5] - (fact[0] * x0 * x0 + fact[1] * x0 * y0 + fact[2] * y0 * y0); 	// f - ( aX0^2 + bX0*Y0 + cY0^2 )
        double t;
        double sum = 0;//标准差
        
        for (int i = 0; i < number; i++)
        {
        	double tx = pList.get(i).getX();
        	double ty = pList.get(i).getY();
            // t = a(X-X0)^2 + b(X-X0)(Y-Y0) + c(Y-Y0)^2
            t = fact[0] * (tx - x0) * (tx - x0) +
                fact[1] * (tx - x0) * (ty - y0) +
                fact[2] * (ty - y0) * (ty - y0); 
            t /= -d;
            t -= 1;
            sum += Math.signum(t) * t;
        }
        return sum / number;
        
	}

	// 全主元gauss消去解 a[][]x[] = Ans[]
	// 返回是否有唯一解,若有解在 Ans[] 中
	private int gauss(double[][/* MAXN */] a, double Ans[]) {

		final double eps = 1e-10;
		int i, j, k, n = a.length;
		double maxp, t;
		int row = n - 1, col = n - 1, index[] = new int[n];

		for (i = 0; i < n; i++)
			index[i] = i;
		for (k = 0; k < n; k++) {
			for (maxp = 0, i = k; i < n; i++)
				for (j = k; j < n; j++)
					if (fabs(a[i][j]) > fabs(maxp))
						maxp = a[row = i][col = j];
			if (fabs(maxp) < eps)
				return 0;
			if (col != k) {
				for (i = 0; i < n; i++) {
					t = a[i][col];
					a[i][col] = a[i][k];
					a[i][k] = t;
				}
				j = index[col];
				index[col] = index[k];
				index[k] = j;
			}
			if (row != k) {
				for (j = k; j < n; j++) {
					t = a[k][j];
					a[k][j] = a[row][j];
					a[row][j] = t;
				}
				t = Ans[k];
				Ans[k] = Ans[row];
				Ans[row] = t;
			}
			for (j = k + 1; j < n; j++) {
				a[k][j] /= maxp;
				for (i = k + 1; i < n; i++)
					a[i][j] -= a[i][k] * a[k][j];
			}
			Ans[k] /= maxp;
			for (i = k + 1; i < n; i++)
				Ans[i] -= Ans[k] * a[i][k];
		}
		// Log.v("Curve", "step1:");
		// MatOut(a);
		for (i = n - 1; i >= 0; i--)
			for (j = i + 1; j < n; j++)
				Ans[i] -= a[i][j] * Ans[j];
		// Log.v("Curve", "step2:");
		// MatOut(a);
		for (k = 0; k < n; k++)
			a[0][index[k]] = Ans[k];
		for (k = 0; k < n; k++)
			Ans[k] = a[0][k];
		// Log.v("Curve", "step3:");
		// MatOut(a);
		return 1;
	}
	
	private void SecondRec_TypeAndStandard() {
		// TODO Auto-generated method stub
		
		if (b*b - 4*a*c < -0.01)	{	// b^2-4ac < 0，椭圆
			double y0 = (2*a*e-b*d) / (b*b-4*a*c);
		    double x0 = (-(d+b*y0)) / (2*a);	        
			center = new PointUnit((float)x0, (float)y0);	
			alpha = Math.atan(b / (a-c)) / 2;
			Sin = Math.sin(alpha);
			Cos = Math.cos(alpha);
			
			// 移轴，旋转
			sa = a * Cos * Cos
			   + b * Sin * Cos
			   + c * Sin * Sin;
			sb = 0;
//			sb = (c - a) * Sin * Cos
//			   + b / 2 * (Cos*Cos - Sin*Sin);
			sc = a * Sin * Sin
			   - b * Sin * Cos
			   + c * Cos * Cos;
			sd = 0;
			se = 0;
			sf = a*x0*x0 + b*x0*y0 + c*y0*y0
				+ d*x0 + e*y0 + f;
			Log.v("Curve", sa + ", " + sc + ", " + sf);
			
			double circleJude=2.0;
			double kk = sa / sc;
            if (kk > 1.0/circleJude && kk < circleJude)
            {
                    kk = (sa + sc) / 2.0;
                    sa = sc = kk;
                    curveType = CurveType.Circle;
                    radius = Math.sqrt(-sf / sa);
            } else {
            	curveType = CurveType.Ellipse;
            }
//	        double sum = 0;
//			for(int i=0; i<pList.size(); i++) {
//				sum += Math.abs(CommonFunc.distance(center.getPoint(), pList.get(i)) - CommonFunc.distance(center.getPoint(), pList.get(0)));
//			}
//			sum /= pList.size();
////			Log.v("Curve", "Sum: " + sum);
//			if( sum <= 10) {
//				sfact[0] = center.getX();
//				sfact[1] = center.getY();
//				sfact[2] = CommonFunc.distance(center.getPoint(), pList.get(0));
////				Log.v("Curve", sfact[0] + ", " + sfact[1]+ ", " + sfact[2]);	
////				Log.v("Curve", a + ", " + b + ", " + c + ", " + d + ", " + e + ", " + f);			
//				curveType = CurveType.Circle;
//			} else {
//				curveType = CurveType.Ellipse;
//			}
		} else {
			if (b*b - 4*a*c > 0.01) {
				curveType = CurveType.Hyperbola;				
			} else {
				curveType = CurveType.Parabola;
			}
		}
		
		//参数标准化
		switch(curveType) {
		case Circle:
			break;
		case Ellipse:
			break;
		case Hyperbola:
			break;
		case Parabola:
			break;
		case Other:
			break;
		default:
			break;
		}
		Log.v("Curve", curveType.toString());
	}

	@Override
	public void draw(Canvas canvas, Painter painter) {
		// TODO Auto-generated method stub
		switch (curveType) {
		case Circle:
			center.draw(canvas, painter);
//			canvas.drawOval(new RectF( (float)(sfact[0]-sfact[2]), (float)(sfact[1]-sfact[2]), 
//					(float)(sfact[0]+sfact[2]), (float)(sfact[1]+sfact[2]) ), painter.getPaint());	
////			Log.v("Curve", (float)(-sfact[0]-sfact[2]) + ", " + (float)(-sfact[1]-sfact[2]) + ", " + 
////							(float)(-sfact[0]+sfact[2]) + ", " + (float)(-sfact[1]+sfact[2]) );			
//			break;
//		case Ellipse:			
//			break;	
//		case Hyperbola:
//			break;
//		case Parabola:
//			break;	
//		case Other:		
//			break;
		default:
			for(int i=0; i<pList.size()-1; i++) {
				canvas.drawLine(pList.get(i).getX(), pList.get(i).getY(), pList.get(i+1).getX(), pList.get(i+1).getY(), painter.getPaint());
			}
			break;
		}
		//Log.v("Curve", curveType.toString() + " " + fact[0] + ", " + fact[1]
		//		+ ", " + fact[2] + ", " + fact[3] + ", " + fact[4]);
	}
	
	private List<Point> findCurvePoint(List<Point> pList) {
		
		List<Point> res = new ArrayList<Point>();
		
		switch(curveType) {
			case Circle:
			case Ellipse:
				double stepAgl = Math.PI / 64;	
				double ta = Math.sqrt(-sf / sa);
				double tb = Math.sqrt(-sf / sc);
				double sAgl = 0, eAgl = 2 * Math.PI;
				boolean ccw = false;	//是否逆时针排列	
				
				Point v1 = new Point(pList.get(0).getX() - center.getX(), pList.get(0).getY() - center.getY());
				Point v2 = new Point(pList.get(4).getX() - center.getX(), pList.get(4).getY() - center.getY());
				if(v1.getX()*v2.getY() - v1.getY()*v2.getX() > 0) ccw = true;
				
				//获取起点和终点
				int i = 0;
				v1 = v2 = null;
				while(v1 == null && i < pList.size()) {
					if (null != calPointY(pList.get(i))) v1 = calPointY(pList.get(i));
					else if (null != calPointX(pList.get(i))) v1 = calPointX(pList.get(i));
					i++;
				}
				i = pList.size()-1;
				while(v2 == null && i >= 0) {
					if (null != calPointY(pList.get(i))) v2 = calPointY(pList.get(i));
					else if (null != calPointX(pList.get(i))) v2 = calPointX(pList.get(i));
					i--;
				}
				v1.setX((float)(v1.getX() - center.getX()));
				v1.setY((float)(v1.getY() - center.getY()));
				v2.setX((float)(v2.getX() - center.getX()));
				v2.setY((float)(v2.getY() - center.getY()));
				
				double sum = 0;
				for(i=0; i<pList.size()-1; i++) {
					sum += Math.abs(AngelOfPoint2(pList.get(i+1)) - AngelOfPoint2(pList.get(i)));
				}
				if (Math.abs(sum) > 3.8*Math.PI) {
					sAgl = 0;
					eAgl = 2 * Math.PI;
					isClosed = true;
				} else {
					if (ccw) {
						sAgl = AngelOfPoint(v1) - alpha;
						eAgl = AngelOfPoint(v2) - alpha;
					} else {
						eAgl = AngelOfPoint(v1) - alpha;
						sAgl = AngelOfPoint(v2) - alpha;	
					}
					if (eAgl < sAgl) eAgl += 2 * Math.PI;
				}
				
		//		Log.v("Curve", (sAgl / Math.PI) + " -> " + (eAgl / Math.PI));
				
				for(double tAgl = sAgl; tAgl < eAgl; tAgl += stepAgl) {
					double tx = ta * Math.cos(tAgl);
					double ty = tb * Math.sin(tAgl);
					double x = tx*Cos - ty*Sin + center.getX();
					double y = tx*Sin + ty*Cos + center.getY();
		//			double x = +tx*Math.cos(2*Math.PI - alpha) + ty*Math.sin(2*Math.PI - alpha) + center.getX();
		//			double y = -tx*Math.sin(2*Math.PI - alpha) + ty*Math.cos(2*Math.PI - alpha) + center.getY();
					res.add(new Point((float)x, (float)y));
				}
				break;
			default:
				res = pList;
				break;
		}
		return res;
	}
	
	private double AngelOfPoint(Point p) {
		double res = Math.atan(Math.abs(p.getY() / p.getX()));
		if(p.getX() < 0 && p.getY() > 0) res = Math.PI - res; 
		if(p.getX() < 0 && p.getY() < 0) res = Math.PI + res; 
		if(p.getX() > 0 && p.getY() < 0) res = 2*Math.PI - res; 
		return res;
	}	
	
	private double AngelOfPoint2(Point p) {
		double x = p.getX() - center.getX();
		double y = p.getY() - center.getY();
		double res = Math.atan(Math.abs(y / x));
		if(x < 0 && y > 0) res = Math.PI - res; 
		if(x < 0 && y < 0) res = Math.PI + res; 
		if(x > 0 && y < 0) res = 2*Math.PI - res; 
		return res;
	}
	
//	public List<Point> findCurvePoint(List<Point> pList) {
//		List<Point> res = new ArrayList<Point>();
//		Point temp, pre = pList.get(0);
//		final double eps = 5;
//		final int pStep = 3;
//		for(int i = 0; i < pList.size(); i++) {
//			temp = calPointY(pList.get(i));
//			if(temp == null)
//				temp = calPointX(pList.get(i));
//			if(Math.abs(temp.getX()-pre.getX()) < eps) {
//				for (int j = (int)pre.getY() + pStep; j < temp.getY(); j += pStep) {
//					res.add(calPointX(new Point(temp.getX(), j)));
//				}
//			}
//			res.add(temp);
//			pre = temp;
//		}
//		return res;
//	}
	
	private Point calPointX(Point x) {
		
		Point res;
		double y = x.getY();
		double xa = (b*y + d) / (2*a);
		double rs = ( -f - e*y - c*y*y ) / a + xa*xa;
		
		if(rs < 0) return null;		
		if (Math.abs(Math.sqrt(rs) - xa - x.getX()) < Math.abs(-Math.sqrt(rs) - xa - x.getX()))
			res = new Point((float)(+Math.sqrt(rs) - xa), (float)y);
		else
			res = new Point((float)(-Math.sqrt(rs) - xa), (float)y);
		
		return res;
	}	
	
	private List<Point> calPointX(double y) {
		
		List<Point> res = new ArrayList<Point>();
		double xa = (b*y + d) / (2*a);
		double rs = ( -f - e*y - c*y*y ) / a + xa*xa;
		
		if(rs < 0) return null;		
		res.add(new Point((float)(+Math.sqrt(rs) - xa), (float)y));
		res.add(new Point((float)(-Math.sqrt(rs) - xa), (float)y));
		
		return res;
	}	
	
	private Point calPointY(Point y) {
		
		Point res;
		double x = y.getX();
		double ya = (b*x + e) / (2*c);
		double rs = ( -f - d*x - a*x*x ) / c + ya*ya;
		
		if (rs < 0) return null;
		if (Math.abs(Math.sqrt(rs) - ya - y.getY()) < Math.abs(-Math.sqrt(rs) - ya - y.getY()))
			res = new Point((float)x, (float)(+Math.sqrt(rs) - ya));
		else
			res = new Point((float)x, (float)(-Math.sqrt(rs) - ya));
		
		return res;
	}	
	
	private List<Point> calPointY(double x) {
		
		List<Point> res = new ArrayList<Point>();
		double ya = (b*x + e) / (2*c);
		double rs = ( -f - d*x - a*x*x ) / c + ya*ya;
		
		if (rs < 0) return null;
		res.add(new Point((float)x, (float)(+Math.sqrt(rs) - ya)));
		res.add(new Point((float)x, (float)(-Math.sqrt(rs) - ya)));
		
		return res;
	}
	
	private double fabs(double x) {
		return ((x) > 0 ? (x) : -(x));
	}
	
	@Override
	public boolean judge(List<Point> pList) {
		// TODO Auto-generated method stub
		if (curveType != CurveType.None && curveType != CurveType.Other)
			return true;
		return false;
	}

	private double[] SecEquSolve(double a, double b, double c) {
		double[] res = new double[2];
		double t = Math.sqrt(b*b - 4*a*c);
		res[0] = (-b + t) / (2*a);
		res[1] = (-b - t) / (2*a);
		return res;
	}
	
	private void MatOut(double[][] a) {
		String temp = new String("");
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (fabs(a[i][j]) < 1e-10)
					temp += "0 | ";
				else
					temp += a[i][j] + " | ";
			}
			temp += "| " + fact[i];
			Log.v("Curve", temp);
			temp = "";
		}
	}
	
//	// 初步识别——简单取点
//	private void FirstRec_basic(List<Point> pList) {
//
//		// aX^2 + bXY + cY^2 + dX + eY + 1 = 0
//		// 取前 n 个点作高斯消元求系数
//		double[][] calp = new double[n][n];
//
//		int step = pList.size() / n;
//		Log.v("Curve", "step: " + step);
//		for (int i = 0; i < n; i++) {
//			double tx = pList.get(i * step ).getX();
//			double ty = pList.get(i * step ).getY();
//			calp[i][0] = tx * tx;
//			calp[i][1] = tx * ty;
//			calp[i][2] = ty * ty;
//			calp[i][3] = tx;
//			calp[i][4] = ty;
//			fact[i] = -fact[5];
//			Log.v("Curve", "Point" + i + ": " + tx + ", " + ty);
//		}
//		if (gauss(calp) != 1) { // 高斯消元出现非唯一解
//			curveType = CurveType.None;
//			return;
//		}
//	}
	
	@Override
	public GUnit clone() {
		GUnit temp = null;
		temp = (CurveUnit) super.clone();
		((CurveUnit)temp).pList = new ArrayList<Point>();
		for(Point point : pList) {
			((CurveUnit)temp).pList.add(point.clone());
		}
		((CurveUnit)temp).fact = fact.clone();
		((CurveUnit)temp).sfact = sfact.clone();
		if(curveType == CurveType.Circle || curveType == CurveType.Ellipse)
			((CurveUnit)temp).center = (PointUnit) center.clone();
		return temp;
	}

	@Override
	public boolean isInUnit(Point point) {
		// TODO Auto-generated method stub
		double curDistance;
		//暂时
//		if(curveType == CurveType.Circle) {
//			curDistance = CommonFunc.distance(point, center.getPoint());
//			if(Math.abs(curDistance - radius) < ThresholdProperty.GRAPH_CHECKED_DISTANCE) {
//				return true;
//			}
//		} else {
			for(Point pt : pList) {
				curDistance = CommonFunc.distance(pt, point);
				if(curDistance < ThresholdProperty.GRAPH_CHECKED_DISTANCE) {
					return true;
				}
			}
//		}
		
		return false;
	}

	public List<Point> getPList() {
		return pList;
	}
	
	public double[] getFactor() {
		return fact;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	public PointUnit getCenter() {
		return center;
	}
	
	public void setCenter(PointUnit center) {
		this.center = center;
	}

	public CurveType getCurveType() {
		return curveType;
	}
	
	public boolean isInCircle(Point point) {
		if(curveType == CurveType.Circle) {
			double distance = CommonFunc.distance(point, center.getPoint());
			if(distance <= radius)
				return true;
		}
		
		return false;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}
	
	@Override
	public void translate(float[][] transMatrix) {
		// TODO Auto-generated method stub
		if(center != null) {
			center.translate(transMatrix);
		}
		for(Point point : pList) {
			point.setX(point.getX() + transMatrix[0][2]);
			point.setY(point.getY() + transMatrix[1][2]);
		}
	}

	@Override
	public void scale(float[][] scaleMatrix, Point translationCenter) {
		// TODO Auto-generated method stub
		float x0 = translationCenter.getX(), y0 = translationCenter.getY();
		if(center != null) {
			center.scale(scaleMatrix, translationCenter);
		}
		if(curveType == CurveType.Circle) {
			radius *= scaleMatrix[0][0];
		}
		sf *= scaleMatrix[0][0];
		for(Point point : pList) {
			point.setX((point.getX() - x0) * scaleMatrix[0][0] + x0);
			point.setY((point.getY() - y0) * scaleMatrix[1][1] + y0);
		}
	}

	@Override
	public void rotate(float[][] rotateMatrix, Point translationCenter) {
		// TODO Auto-generated method stub
		float x0 = translationCenter.getX(), y0 = translationCenter.getY();
		if(center != null) {
			center.rotate(rotateMatrix, translationCenter);
		}
		float tempX, tempY;
		for(Point point : pList) {
			tempX = point.getX() - x0;
			tempY = point.getY() - y0;
			point.setX((tempX * rotateMatrix[0][0] + tempY * rotateMatrix[0][1]) + x0);
			point.setY((tempX * rotateMatrix[1][0] + tempY * rotateMatrix[1][1]) + y0);
		}
	}

}
