/*
 * 读取properties文件初始化常用阈值
 * 如果有常变量要添加：1，在threshold.properties文件中添加相应格式的信息，2，在该类中定义变量然后读取文件初始化
 * */

package com.sg.property.common;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.sg.property.R;

import android.content.Context;
import android.util.Log;

public class ThresholdProperty {
	
	//点阈值
	public static int POINT_COUNT;
	public static int POINT_DISTANCE;
	public static double POINT_SELECTED_DISTANCE;
	
	//线阈值
	public static double JUDGE_LINE_VALUE;
	
	//时间
	public static int PRESS_TIME_SHORT;
	public static int PRESS_TIME_LONG;
	
	//图形选中
	public static double GRAPH_CHECKED_DISTANCE;
	
	//三角形四边形
	public static double TWO_POINT_IS_CLOSED; //用于判断是否能成为三角形或四边形的阀值
	public static double TWO_POINT_IS_CONSTRAINTED;
	
	
	private static final Map<String, String> thresholdMap = new HashMap<String, String>();;
	
	private ThresholdProperty() {
		
	}
	
	public static void load(Context context) {
		readProperties(context);
		initThreshold();
	}
	
	private static void readProperties(Context context) {
		Properties properties = new Properties();
//		File file = new File(filePath);
		InputStream in = null;
		
//		if(!file.exists()) {
//			Log.v("Failed", filePath + " NOT FOUND");
//			return;
//		}
		
		try {
//			in = new BufferedInputStream(new FileInputStream(file));
			in = context.getResources().openRawResource(R.raw.threshold);
			properties.load(in);
			
			Enumeration<?> en = properties.propertyNames();
			while (en.hasMoreElements()) {
				String name = String.valueOf(en.nextElement());
				String value = properties.getProperty(name);
				thresholdMap.put(name, value);
			}
			
			in.close();
		} catch (Exception e) {
			Log.v("Failed", "读取阈值属性文件出错");
		}
	}
	
	private static String getProperty(String key) {
		return thresholdMap.get(key);
	}

	private static void initThreshold() {
		
		POINT_COUNT = Integer.parseInt(getProperty("point_count"));
		POINT_DISTANCE = Integer.parseInt(getProperty("point_distance"));
		POINT_SELECTED_DISTANCE = Double.parseDouble(getProperty("point_selected_distance"));
		
		JUDGE_LINE_VALUE = Double.parseDouble(getProperty("judge_line_value"));	
		
		PRESS_TIME_SHORT = Integer.parseInt(getProperty("press_time_short"));
		PRESS_TIME_LONG = Integer.parseInt(getProperty("press_time_long"));
		GRAPH_CHECKED_DISTANCE = Double.parseDouble(getProperty("graph_checked_distance"));
		
		TWO_POINT_IS_CLOSED = Double.parseDouble(getProperty("two_point_is_closed"));
		TWO_POINT_IS_CONSTRAINTED = Double.parseDouble(getProperty("two_point_is_constrainted"));
	}

}
