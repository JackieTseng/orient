package com.orient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.constant.Constant;
import com.network.Register;
import com.network.Register;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

public class RegisterActivity extends Activity {
	static final String MALE = "male";
	static final String FEMALE = "female";
	private Button confirmButton;
	private EditText userNameEt, pwEt, pwCheckEt, nickNameEt, phoneNumberEt;
	String userName, pw, pwCheck, nickName, phoneNumber, gender, picid;
	int genderId;
	private ImageButton avatarButton;
	private RadioGroup genderRg;
	private final Context context = this;
	private ProgressDialog dialog;
	AlertDialog imgChooseDialog;
	
	private int[] images = { R.drawable.photo1, R.drawable.photo2,
            R.drawable.photo3, R.drawable.photo4, R.drawable.photo5,
            R.drawable.photo6, R.drawable.photo7, R.drawable.photo8,
            };
	GridView grid;
	private int select = 1;
	private final Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			dialog.cancel();
			Bundle bundle = msg.getData();
			String status = bundle.getString("status", "no status");
			
			switch(msg.what){
			case Constant.NETWORK_SUCCESS_MESSAGE_TAG:
				if (status.equalsIgnoreCase("success")) {
					SharedPreferences sharePreferences = getSharedPreferences(
							Constant.USER_SHAREDPREFERENCE, MODE_PRIVATE);
					Editor editor = sharePreferences.edit();
					editor.putString(Constant.SHAREDPREFERENCE_KEY_USERNAME, userName);
					editor.putString(Constant.SHAREDPREFERENCE_KEY_PASSWORD, pw);
					editor.commit();
					Toast.makeText(context, "注册成功", 
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.setClass(RegisterActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
				}
				else if (status.equalsIgnoreCase("email used")){
					Toast.makeText(context, "用户名已被注册", 
							Toast.LENGTH_LONG).show();
					userNameEt.requestFocus();
				}
				else 
					Toast.makeText(context, "注册失败", 
							Toast.LENGTH_SHORT).show();
				break;
			case Constant.NETWORK_FAILED_MESSAGE_TAG:
				Toast.makeText(context, "网络连接有错，请稍后再试",
						Toast.LENGTH_LONG).show();
				break;
			default:
					break;
			}
		}
	};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        
        dialog = new ProgressDialog(context);
        //设置进度条风格，风格为圆形，旋转的
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //设置ProgressDialog标题
        dialog.setTitle("注册");
        //设置ProgressDialog提示信息
        dialog.setMessage("正在提交注册消息");
        //设置ProgressDialg的进度条是否不明确
        dialog.setIndeterminate(false);
        //设置ProgressDialog是否可以按退回键取消
        dialog.setCancelable(true);
        //设置ProgressDialog的一个Button
        dialog.setButton("返回", new DialogInterface.OnClickListener(){
        	public void onClick(DialogInterface pDialog, int i){
        		//点击，取消对话框
        		dialog.cancel();
        	}
        });
        userNameEt = (EditText) findViewById(R.id.friend_page_searchContent);
        pwEt = (EditText) findViewById(R.id.signUp_password);
        pwCheckEt = (EditText) findViewById(R.id.signUp_passwordChecked);
        nickNameEt = (EditText) findViewById(R.id.signUp_nickname);
        genderRg = (RadioGroup) findViewById(R.id.radioGroup1);
        phoneNumberEt = (EditText) findViewById(R.id.signUp_phoneNumber);
        picid = Integer.toString(select);     
        /*
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.userheadchoose, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        imgChooseDialog = builder.create();
        grid = (GridView)findViewById(R.id.grid);
        */
        grid = new GridView(this);
        grid.setNumColumns(4);
        
        ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();  
        for(int i=0;i<images.length;i++){  
            HashMap<String,Object> map = new HashMap<String,Object>();  
            map.put("img", images[i]);
            list.add(map);
        }
        
        grid.setAdapter(new SimpleAdapter(this, list, R.layout.headimg, new String[]{"img"}, new int[]{R.id.headimg}));
        
        /*
        //设置适配器  
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, R.layout.headimg, new String[]{"img"}, new int[]{R.id.headimg});  
        //适配器  
        grid.setAdapter(simpleAdapter);  
        */
        
        imgChooseDialog=new AlertDialog.Builder(this).setView(grid).create(); 
        imgChooseDialog.setOnKeyListener(new AlertDialog.OnKeyListener() {  
            @Override  
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {  
                if(keyCode==KeyEvent.KEYCODE_MENU||keyCode==KeyEvent.KEYCODE_BACK){//监听按钮  
                	imgChooseDialog.dismiss();  
                }  
                return false;  
            }
        }); 
        //选中事件监听  
        grid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                select = position;
                imgChooseDialog.dismiss();
                avatarButton.setImageDrawable(getResources().getDrawable(images[select]));
            }
        });
        
        avatarButton = (ImageButton)findViewById(R.id.signUp_Avatar);
        avatarButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imgChooseDialog.show();
            }
        });
        
        confirmButton = (Button)findViewById(R.id.submit);
        confirmButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				userName = userNameEt.getText().toString(); 
				pw = pwEt.getText().toString();
				pwCheck = pwCheckEt.getText().toString();
				nickName = nickNameEt.getText().toString();
				phoneNumber = phoneNumberEt.getText().toString();
				int genderId = genderRg.getCheckedRadioButtonId();
				if (genderId == R.id.signUp_gender_1){
					gender = MALE;
				}else{
					gender = FEMALE;
				}
				if (!pw.equals(pwCheck)){
					Toast.makeText(context, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
					pwCheckEt.requestFocus();
					return;
				}
				GlobalVarApplication gva = (GlobalVarApplication)getApplication();
				Register register = new Register(gva.httpClient, mHandler, userName, pw, nickName, phoneNumber, gender);
				new Thread(register).start();
				//显示进度条对话框
				dialog.show();
				
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
