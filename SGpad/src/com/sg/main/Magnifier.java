package com.sg.main;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Bitmap.Config;
import android.graphics.Path.Direction;

public class Magnifier {
	
	private Path mPath = new Path();
	private Matrix matrix = new Matrix();
	private Bitmap bitmap;
	//放大镜的半径
	private static final int RADIUS = 80;
	//放大倍数
	private static final int FACTOR = 2;
	private int mCurrentX, mCurrentY;
	private Canvas cacheCanvas = null;
	private boolean start;
	private int width, height;
	
	public Magnifier()
	{		
	}

	public void Init(int width, int height) {
		mPath.addCircle(RADIUS, RADIUS, RADIUS, Direction.CW);
		matrix.setScale(FACTOR, FACTOR);
		this.width = width;
		this.height = height;
		//bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.show);
		bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		cacheCanvas = new Canvas();
		cacheCanvas.setBitmap(bitmap);
		start = false;
	}	
	
	public void CollectPoint(int X, int Y) {
		start = true;
		mCurrentX = X;
		mCurrentY = Y;
	}
	
	public void EndShow()
	{
		start = false;
	}
	
	public Canvas GetCacheCanvas()
	{
		return cacheCanvas;
	}
	
	public void Draw(Canvas canvas) {
		
		//底图
		canvas.drawBitmap(bitmap, 0, 0, null);
		if(start)
		{
			//剪切s
			int X, Y;
			if(mCurrentX < 2 * RADIUS && mCurrentY < 2 * RADIUS)
			{
				X = width - 2 * RADIUS;
				Y = 0;
			}
			else
			{
				X = Y = 0;
			}
			canvas.translate(X, Y);
			canvas.clipPath(mPath);	
			//画放大后的图
			canvas.translate(RADIUS-mCurrentX*FACTOR, RADIUS-mCurrentY*FACTOR);
			canvas.drawBitmap(bitmap, matrix, null);
		}		     

	}
}
