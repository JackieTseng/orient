package com.orient;

import java.util.List;

import com.baidu.location.r;
import com.constant.Constant;
import com.network.GetRoomInfo;
import com.network.Login;
import com.network.Login;
import com.network.Register;
import com.util.TeamParcelable;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private EditText userNameEt, pwEt;
	private String userName, pw;
	private final Context context = this;
	Button loginButton;
	TextView registerButton;
	private long exitTime = 0;
	private ProgressDialog dialog;
	SharedPreferences sharePreferences;
	GlobalVarApplication gva;
	
	private final Handler handler = new Handler(){
		
		@Override
		public void handleMessage(Message msg){
			dialog.cancel();
			gva = (GlobalVarApplication)getApplication();
			Bundle bundle = msg.getData();
			String status = bundle.getString("status", "no status");
			String info = bundle.getString("info", "no info");
			String roomid = bundle.getString("RoomId", "no room id");
			String teamid = bundle.getString("TeamId", "no team id");
			String nickname = bundle.getString("nickname", "no nickname");
			String gender = bundle.getString("gender", "no gender");
			String telephone = bundle.getString("telephone", "no telephone");
			int portrait = Integer.valueOf(bundle.getString("portrait", "0"));
			switch(msg.what){
			case Constant.NETWORK_SUCCESS_MESSAGE_TAG:
				
				if (status.equalsIgnoreCase("succeed")) {
					Editor editor = sharePreferences.edit();
					editor.putString(Constant.SHAREDPREFERENCE_KEY_USERNAME, userName);
					editor.putString(Constant.SHAREDPREFERENCE_KEY_PASSWORD, pw);
					gva.nickname = nickname;
					gva.gender = gender;
					gva.telephone = telephone;
					gva.portrait = portrait;
					editor.commit();
					Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show();
					if (info.equalsIgnoreCase("ok")){
						
						Intent intent = new Intent();
						intent.setClass(LoginActivity.this, HomeActivity.class);
						startActivity(intent);
						finish();
					}else if (info.equalsIgnoreCase("in room")){
						Toast.makeText(context, "正在房间中", 
								Toast.LENGTH_SHORT).show();
						int room_id_num = Integer.parseInt(roomid);
						gva.curRoomId = room_id_num;
						int team_id_num = Integer.parseInt(teamid);
						gva.teamid = team_id_num;
						Intent intent = new Intent();
						intent.putExtra("RoomId", room_id_num);
						intent.setClass(LoginActivity.this, RoomNew.class);
						startActivity(intent);
					}else if (info.equalsIgnoreCase("in game")){
						Toast.makeText(context, "正在游戏中", 
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.setClass(LoginActivity.this, GameMap.class);
						startActivity(intent);
						finish();
					}/*else {
						Intent intent = new Intent();
						intent.setClass(LoginActivity.this, HomeActivity.class);
						startActivity(intent);
						finish();
					}*/
					
				}else if (status.equalsIgnoreCase("not exists")){
					Toast.makeText(context, "用户名不存在", 
							Toast.LENGTH_LONG).show();
					userNameEt.requestFocus();
					userNameEt.selectAll();
				}else if (status.equalsIgnoreCase("failed")){
					Toast.makeText(context, "请重新登录", Toast.LENGTH_SHORT).show();
					Log.i("lin", info);
				}else{
					Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
				}
				break;
			case Constant.NETWORK_FAILED_MESSAGE_TAG:
				Toast.makeText(context, "网络连接有错，请稍后再试",
						Toast.LENGTH_LONG).show();
				break;
			default:
				Log.i("lin", "unknown");
				break;
			}
		
		}
	};
	  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		loginButton = (Button)findViewById(R.id.loginbtn);
		registerButton = (TextView)findViewById(R.id.register);
		userNameEt = (EditText)findViewById(R.id.username);
		pwEt = (EditText)findViewById(R.id.password);
		sharePreferences = getSharedPreferences(Constant.USER_SHAREDPREFERENCE, MODE_PRIVATE);
		userName = sharePreferences.getString(Constant.SHAREDPREFERENCE_KEY_USERNAME, "");
		pw = sharePreferences.getString(Constant.SHAREDPREFERENCE_KEY_PASSWORD, "");
		userNameEt.setText(userName);
		gva = (GlobalVarApplication)getApplication(); 
		pwEt.setText(pw);
		loginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				userName = userNameEt.getText().toString();
				pw = pwEt.getText().toString();
				
				Login login = new Login(gva.httpClient, handler, userName, pw);
				new Thread(login).start();
				dialog.show();
			}
		});
		registerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
				finish();
			}
		});
		dialog = new ProgressDialog(context);
        //设置进度条风格，风格为圆形，旋转的
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //设置ProgressDialog标题
        dialog.setTitle("登录");
        //设置ProgressDialog提示信息
        dialog.setMessage("正在登录");
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){

	        if((System.currentTimeMillis()-exitTime) > 2000){
	          Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
	        	exitTime = System.currentTimeMillis();
	        	
	        }
	        else{
	            finish();
	            System.exit(0);
	        }
	
	        return true;
	    }
        return super.onKeyDown(keyCode, event);
	}
}
