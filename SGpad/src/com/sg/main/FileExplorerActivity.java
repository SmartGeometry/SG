package com.sg.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sg.property.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FileExplorerActivity extends Activity
{
	private ListView listView;
	private TextView textView;
	// 记录当前的父文件夹
	private File currentParent;
	// 记录当前路径下的所有文件的文件数组
	private File[] currentFiles;

	private static final String MYFILE = "/SG";
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.explorer);
		//获取列出全部文件的ListView
		listView = (ListView) findViewById(R.id.list);
		textView = (TextView) findViewById(R.id.path);

		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			//获取系统的SD卡的目录
			String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
			File root = new File(sdPath + MYFILE);
			if(!root.exists()){
				root.mkdirs();
			}
			currentParent = root;
			currentFiles = root.listFiles();
			//使用当前目录下的全部文件、文件夹来填充ListView
			inflateListView(currentFiles);
		}else{
			Toast.makeText(this, "sdcard不存在", Toast.LENGTH_SHORT).show();
			finish();
		}
		// 为ListView的列表项的单击事件绑定监听器
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
				int position, long id)
			{
				try
				{
					if (!currentParent.getCanonicalPath().equals("/"))
					{
						if(position == 0){
							// 获取上一级目录
							currentParent = currentParent.getParentFile();
							// 列出当前目录下所有文件
							currentFiles = currentParent.listFiles();
							// 再次更新ListView
							inflateListView(currentFiles);
							return;
						}else{
							position--;
						}
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				// 用户单击了文件
				if (currentFiles[position].isFile()){
					String path = currentFiles[position].getAbsolutePath();
					if(path.endsWith(".jsg")){
						Intent intent = new Intent();
						intent.putExtra("path", path);
						setResult(1, intent);
						finish();
					}else{
						return;
					}
				}else{
					File[] tmp = currentFiles[position].listFiles();
					//获取用户单击的列表项对应的文件夹，设为当前的父文件夹
					currentParent = currentFiles[position];
					//保存当前的父文件夹内的全部文件和文件夹
					currentFiles = tmp;
					// 再次更新ListView
					inflateListView(currentFiles);
				}
			}
		});
	}

	private void inflateListView(File[] files)
	{
		// 创建一个List集合，List集合的元素是Map
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		try
		{
			//设置第一个list作为返回上一级选项
			if (!currentParent.getCanonicalPath().equals("/"))
			{
				Map<String, Object> listItem = new HashMap<String, Object>();
				listItem.put("icon", R.drawable.folder);
				listItem.put("fileName", "...");
				listItems.add(listItem);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		if (files != null && files.length != 0){
			sortFiles(files);
			for (int i = 0; i < files.length; i++)
			{
				Map<String, Object> listItem = new HashMap<String, Object>();
				//如果当前File是文件夹，使用folder图标；否则使用file图标
				if (files[i].isDirectory())
				{
					listItem.put("icon", R.drawable.folder);
				}
				else
				{
					if(files[i].getName().endsWith(".jsg")){
						listItem.put("icon", R.drawable.ic_launcher);
					}else{
						listItem.put("icon", R.drawable.file);
					}
				}
				listItem.put("fileName", files[i].getName());
				//添加List项
				listItems.add(listItem);
			}
		}
		// 创建一个SimpleAdapter
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
			R.layout.line, new String[] { "icon", "fileName" }, new int[] {
				R.id.icon, R.id.file_name });
		// 为ListView设置Adapter
		listView.setAdapter(simpleAdapter);
		try
		{
			textView.setText("当前路径为：" + currentParent.getCanonicalPath());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	//对文件进行排序
	private void sortFiles(File[] files){
		Arrays.sort(files, new Comparator<File>() {
			public int compare(File f1, File f2) {
				return f1.getName().compareToIgnoreCase(f2.getName());
//				if (f1 == null || f2 == null) {// 先比较null
//					if (f1 == null) {
//						return -1;
//					} else {
//						return 1;
//					}
//				} else {
//					if (f1.isDirectory() == true && f2.isDirectory() == true) { // 再比较文件夹
//						return f1.getName().compareToIgnoreCase(f2.getName());
//					} else {
//						if ((f1.isDirectory() && !f2.isDirectory()) == true) {
//							return -1;
//						} else if ((f2.isDirectory() && !f1.isDirectory()) == true) {
//							return 1;
//						} else {
//							return f1.getName().compareToIgnoreCase(f2.getName());// 最后比较文件
//						}
//					}
//				}
			}
		});
	}
}