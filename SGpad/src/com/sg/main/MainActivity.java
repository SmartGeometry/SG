package com.sg.main;

import com.sg.property.R;
import com.sg.property.common.ThresholdProperty;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TableLayout;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
	
	private MainView mainView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  //设置全屏
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //设置背光灯长亮
		initConfig(this);
		mainView = new MainView(this);
        setContentView(mainView);
        //读取关联文件
        Intent intent = getIntent(); 
        String action = intent.getAction(); 
        if(intent.ACTION_VIEW.equals(action)){
        	Uri uri = (Uri) intent.getData();
        	String path = uri.getPath();
        	mainView.open(path);
        } 
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
    	switch(resultCode){
    	case 0:
    		break;
    	case 1:
    		String path = data.getStringExtra("path");
    		mainView.open(path);
    		break;
    	default:
			break;
    	}
    }
    /*
     * 初始化 阈值配置文件
     * */
    private void initConfig(Context context) {
//    	String filePath = this.getResources().getResourceName(R.raw.threshold);
    	ThresholdProperty.load(context);
    }
    
    //创建menu菜单栏
    private static final int UNDO_ID = Menu.FIRST + 1;
    private static final int REDO_ID = Menu.FIRST + 2;
    private static final int CLEAR_ID = Menu.FIRST + 3;
    private static final int OPEN_ID = Menu.FIRST + 4;
    private static final int SAVE_ID = Menu.FIRST + 5;
    private static final int EXIT_ID = Menu.FIRST + 6;
    
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	/*第一个参数是groupId，如果不需要可以设置为Menu.NONE。将若干个menu item都设置在同一个Group中，可以使用setGroupVisible()，setGroupEnabled()，setGroupCheckable()这样的方法，而不需要对每个item都进行setVisible(), setEnable(), setCheckable()这样的处理，这样对我们进行统一的管理比较方便
         * 第二个参数就是item的ID，我们可以通过menu.findItem(id)来获取具体的item 
         * 第三个参数是item的顺序，一般可采用Menu.NONE，具体看本文最后MenuInflater的部分
         * 第四个参数是显示的内容，可以是String，或者是引用Strings.xml的ID*/
    	menu.add(Menu.NONE, UNDO_ID, Menu.NONE, "撤销").setIcon(R.drawable.undo);
        menu.add(Menu.NONE, REDO_ID, Menu.NONE, "重做").setIcon(R.drawable.redo);
        menu.add(Menu.NONE, CLEAR_ID, Menu.NONE, "清除").setIcon(R.drawable.clear);
        menu.add(Menu.NONE, SAVE_ID, Menu.NONE, "保存").setIcon(R.drawable.save);
        menu.add(Menu.NONE, OPEN_ID, Menu.NONE, "读取").setIcon(R.drawable.open);
        menu.add(Menu.NONE, EXIT_ID, Menu.NONE, "退出").setIcon(R.drawable.exit);
        return super.onCreateOptionsMenu(menu);
    }
    
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	switch (item.getItemId())
    	{
    	case REDO_ID:
    		mainView.Redo();
    		break;
    	case UNDO_ID:
    		mainView.Undo();
    		break;
    	case CLEAR_ID:
    		mainView.clear();
    		break;
    	case SAVE_ID:
    		save();
    		break;
    	case OPEN_ID:
    		Intent intent = new Intent(this,FileExplorerActivity.class); 
    		startActivityForResult(intent, 0);
    		break;
    	case EXIT_ID:
    		mainView.clear();
   		    finish();
    		break;
    	default:
    			break;
    	}
        return super.onOptionsItemSelected(item);
    }
    
    //返回键对话框
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	//builder.setIcon(icon);
        	builder.setTitle("退出软件");
        	builder.setMessage("确认退出？");
        	builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
        		public void onClick(DialogInterface dialog, int whichButton) {
        			mainView.clear();
           		    finish();
        		}
        	});
        	builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
        		public void onClick(DialogInterface dialog, int whichButton) {
        			
        		}
        	});
        	AlertDialog dialog = builder.create();
        	dialog.setCanceledOnTouchOutside(true);
        	dialog.show();
        	return true;
        }else{
        	return super.onKeyDown(keyCode, event);
        }
    }
    
    private void save(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("保存文件");
    	LayoutInflater factory = LayoutInflater.from(this);  
    	final TableLayout saveForm = (TableLayout)factory.inflate(R.layout.save, null);
    	builder.setView(saveForm);
    	builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
    		public void onClick(DialogInterface dialog, int whichButton) {
    			EditText nameText = (EditText) saveForm.findViewById(R.id.editName);
    			String name = nameText.getText().toString();
    			mainView.save(name);
    		}
    	});
    	builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
    		public void onClick(DialogInterface dialog, int whichButton) {
    			
    		}
    	});
    	builder.create().show();
    }
    
    /*
    private void open(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("读取文件");
    	LayoutInflater factory = LayoutInflater.from(this);  
    	final TableLayout saveForm = (TableLayout)factory.inflate(R.layout.save, null);
    	builder.setView(saveForm);
    	builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
    		public void onClick(DialogInterface dialog, int whichButton) {
    			EditText nameText = (EditText) saveForm.findViewById(R.id.editName);
    			String name = nameText.getText().toString();
    			mainView.open(name);
    		}
    	});
    	builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
    		public void onClick(DialogInterface dialog, int whichButton) {
    			
    		}
    	});
    	builder.create().show();
    }
    */
}