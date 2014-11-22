package com.orient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import com.constant.Constant;
import com.network.ChangeTeamName;
import com.network.ChangeToTeam;
import com.network.CreateTeam;
import com.network.EnterRoom;
import com.network.ExitRoom;
import com.network.GetRoomInfo;
import com.network.GetRoute;
import com.util.ParsePointList;
import com.util.ParseRoomList;
import com.util.Portrait;
import com.util.Room;
import com.util.TeamMemberParcelable;
import com.util.TeamParcelable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class RoomNew extends Activity {
	private TextView roomNameTextView;
	private TextView routeidTextView;
	private TextView startPointTextView;
	private TextView startTimeTextView;
	private String roomNameString;
	private int routeid;
	private String startPointString;
	private String startTimeString;
	private String maxMem;
	private ImageButton backHomeImageButton;
	private Button startGameButton;
	private RelativeLayout teamInfoLayout;
	private Button exitRoomBtn, createTeamButton, readyButton;
	private ImageView joinTeamImageView;
	private Room room;
	private ProgressDialog dialog;
	GlobalVarApplication gva;
	//private ArrayList<HashMap<String,Integer>> points ;

	GridView myteam_gv;
	GridView notchosen_gv;
	ListView otherteams_lv;
	LinearLayout myteam_ll;
	LinearLayout notchosen_ll;
	TeamParcelable myteam;
	TeamParcelable notchosen;
	TextView myteam_tv, maxMemTv;
	EditText myteam_et;
	List<TeamParcelable> otherTeams;
	ImageView edit_iv;
	boolean isEditTeamName;
	InputMethodManager imm;
	ImageView backImageView;
	List<TeamParcelable> teamList;
	Intent intent;
	private int newTeamId;
	private Portrait portrait;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intent = getIntent();
		portrait = new Portrait();
        init();
        enterRoom();       	
    }
	//设置返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
			Intent intent = new Intent();
			intent.setClass(RoomNew.this, HomeActivity.class);
			startActivity(intent);
			finish();
		}

		return super.onKeyDown(keyCode, event);
	}
	private void init(){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.room_new);
        dialog = new ProgressDialog(this);
      //设置进度条风格，风格为圆形，旋转的
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //设置ProgressDialog标题
        dialog.setTitle("房间");
        //设置ProgressDialog提示信息
        dialog.setMessage("正在处理");
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
        gva = (GlobalVarApplication)getApplication(); 
        exitRoomBtn = (Button)findViewById(R.id.exit_room_button);
        createTeamButton = (Button)findViewById(R.id.create_team);
        startGameButton = (Button)findViewById(R.id.start);
        edit_iv = (ImageView) findViewById(R.id.edit_teamName);
		myteam_ll = (LinearLayout) findViewById(R.id.myteam_linearLayout);
		notchosen_ll = (LinearLayout) findViewById(R.id.notchose_linearLayout);
		otherteams_lv = (ListView) findViewById(R.id.other_teams_listView);
		myteam_tv = (TextView) myteam_ll.findViewById(R.id.myteam_name_tv);
		myteam_et = (EditText) myteam_ll.findViewById(R.id.myteam_name_et);
		myteam_gv = (GridView) myteam_ll.findViewById(R.id.myteam_gridView);
	    notchosen_gv = (GridView) findViewById(R.id.notchosen_gridview);
		
		maxMemTv = (TextView) findViewById(R.id.max_member_tv);
		joinTeamImageView = (ImageView)  findViewById(R.id.joinTeam);
		otherTeams = new ArrayList<TeamParcelable>();
		isEditTeamName = false;
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		Log.i("yuan", "roomid ---> " + gva.curRoomId);
     
	}
	private boolean enterRoom(){
		dialog.setMessage("正在获取房间信息...");
		dialog.show();
		if (gva.curRoomId == -1){ //加入新房间
			int newroomid = intent.getIntExtra("RoomId",-1);
			GetRoomInfo gri = new GetRoomInfo(gva.httpClient, getRoomInfoHandler,newroomid);
			new Thread(gri).start();
			
		}else if (gva.curRoomId != -1 && gva.curRoomId !=  intent.getIntExtra("RoomId",-1)){//房间号不对应（已经在A 加入B）
			int newroomid = intent.getIntExtra("RoomId",-1);
			GetRoomInfo gri = new GetRoomInfo(gva.httpClient, getRoomInfoHandler,newroomid);
			new Thread(gri).start();
		}
		else if(gva.curRoomId == intent.getIntExtra("RoomId",-1)){//进入已经加入的房间
			GetRoomInfo gri = new GetRoomInfo(gva.httpClient, getRoomInfoHandler, gva.curRoomId);
			new Thread(gri).start();
			//GetRoute gr = new GetRoute(gva.httpClient, getRouteHandler, routeid);
    		//new Thread(gr).start();
		}
		return true;
	}
	private void setData(){
		otherTeams.clear();
		if(gva.curRoomId == room.getRoomid()){
			for (int i = 0; i < teamList.size(); i++) {
				if (teamList.get(i).getTeamid() == 0) {
					notchosen = teamList.get(i);
				} else if (teamList.get(i).getTeamid()!=gva.teamid)
					otherTeams.add(teamList.get(i));
				else{
					myteam = teamList.get(i);
				}
			}
		}else{
			for (int i = 0; i < teamList.size(); i++) {
				if (teamList.get(i).getTeamid() == 0) {
					notchosen = teamList.get(i);
				} else {
					otherTeams.add(teamList.get(i));
				}
			}
		}

	}
	private void updateUI(){
		roomNameString = room.getRoomName();
		roomNameTextView = (TextView)findViewById(R.id.room_new_room_name);
       	roomNameTextView.setText(roomNameString);
       	String distance = "距离未知";
       	if (room.getDistance() != null){
       		distance = room.getDistance();
       	}
       	startPointString = room.getAddress()+" "+distance;
       	startPointTextView = (TextView)findViewById(R.id.room_new_start_point);
       	startPointTextView.setText(startPointString);
       	startTimeString = room.getTime(); 
       	startTimeTextView = (TextView)findViewById(R.id.room_new_start_time);
       	startTimeTextView.setText(startTimeString);
       	routeid = room.getRouteid();
       	
       	//返回主页面
       	backHomeImageButton = (ImageButton)findViewById(R.id.room_new_back_button);
       	backHomeImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(RoomNew.this, HomeActivity.class);
				startActivity(intent);
				finish();
			}
		});
       	
       	
       	exitRoomBtn.requestFocus();
       	if (room != null && room.getRoomid() == gva.curRoomId){
       		exitRoomBtn.setText("退出房间");
       		exitRoomBtn.setBackgroundColor(Color.rgb(253, 82, 73));
       	}else {
       		exitRoomBtn.setText("加入房间");
       		exitRoomBtn.setBackgroundColor(Color.rgb(106, 167, 27));
       	}
       	exitRoomBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Button b = (Button)v;
				String buttonText = b.getText().toString();
				if (buttonText == "加入房间") {
					if (gva.curRoomId == -1){
						dialog.setMessage("正在加入房间...");
						dialog.show();
						EnterRoom enterroom = new EnterRoom(gva.httpClient, enterRoomHandler,room.getRoomid());
						new Thread(enterroom).start();
					}else if(gva.curRoomId != -1){ // 在A房间，想加入B房间
						dialog.setMessage("正在加入房间...");
						dialog.show();
						ExitRoom er = new ExitRoom(gva.httpClient, exitEnterRoomHandler);
						new Thread(er).start();
					}
					
				} else {
					if (gva.curRoomId != -1){
						dialog.show();
						dialog.setMessage("正在退出房间...");
						ExitRoom exitroom = new ExitRoom(gva.httpClient, exitRoomHandler);
						new Thread(exitroom).start();
					}
				}
			}
		});
       	
       	
       	
       	//进入游戏开始的界面
       	
       	startGameButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (gva.curRoomId != room.getRoomid()) {
					Toast.makeText(getApplicationContext(), "你尚未加入该房间", Toast.LENGTH_SHORT);
				} else if (gva.teamid == 0) {
					Toast.makeText(getApplicationContext(), "你尚未加入队伍", Toast.LENGTH_SHORT);
				} else {
					Intent intent = new Intent();
					intent.setClass(RoomNew.this, GameMap.class);
					Bundle bundle = new Bundle();
					bundle.putParcelable("Room", room);
					for (TeamParcelable team : teamList) {
						if (team.getTeamid() == gva.teamid) {
							bundle.putParcelable("Team", team);
						}
					}
					intent.putExtras(bundle);
					startActivity(intent);
					finish();
				}
			}
		});
       	
       	createTeamButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(gva.curRoomId != room.getRoomid()){
					Toast.makeText(getApplicationContext(), "不在当前房间不能创建", Toast.LENGTH_SHORT).show();
					return;
				}
				dialog.setMessage("正在创建队伍");
	    		dialog.show();
				CreateTeam ct = new CreateTeam(gva.httpClient, createTeamHandler);
				new Thread(ct).start();
				refresh();
			}
		});
       	
	}
	
	private Handler exitRoomHandler = new Handler(){
		@Override
    	public void handleMessage(Message msg){
			dialog.cancel();
			Bundle bundle = msg.getData();
			String status = bundle.getString("status", "no status");
			String info = bundle.getString("info", "no info");
			switch(msg.what){
			case Constant.NETWORK_SUCCESS_MESSAGE_TAG:
				
				if (status.equalsIgnoreCase("succeed")){
					exitRoomBtn.setText("加入房间");
					exitRoomBtn.setBackgroundColor(Color.rgb(0, 187, 51));
					gva.curRoomId = -1;
					gva.teamid = -1;
					Intent intent = new Intent();
					intent.setClass(RoomNew.this, HomeActivity.class);
					startActivity(intent);
					finish();
				} else if (status.equalsIgnoreCase("failed")){
					Log.i("lin", "exit room info: "+info);
					if (info.equalsIgnoreCase("not login")){
						Toast.makeText(getApplicationContext(), "尚未登录", Toast.LENGTH_SHORT).show();
					}else if (info.equalsIgnoreCase("user not in this room")){
						Toast.makeText(getApplicationContext(), "您不在此房间中", Toast.LENGTH_SHORT).show();
					}else if (info.equalsIgnoreCase("room not found")){
						Toast.makeText(getApplicationContext(), "没找到房间", Toast.LENGTH_SHORT).show();
					}
				}
				break;
			case Constant.NETWORK_FAILED_MESSAGE_TAG:
				Toast.makeText(getApplicationContext(), "网络连接有错", Toast.LENGTH_SHORT).show();
				break;
			}
		}
		
	};
	private Handler exitEnterRoomHandler = new Handler(){
		@Override
    	public void handleMessage(Message msg){
			dialog.cancel();
			Bundle bundle = msg.getData();
			String status = bundle.getString("status", "no status");
			String info = bundle.getString("info", "no info");
			switch(msg.what){
			case Constant.NETWORK_SUCCESS_MESSAGE_TAG:
				
				if (status.equalsIgnoreCase("succeed")){
					exitRoomBtn.setText("加入房间");
					exitRoomBtn.setBackgroundColor(Color.rgb(0, 187, 51));
					gva.curRoomId = -1;
					gva.teamid = -1;
					EnterRoom er = new EnterRoom(gva.httpClient, enterRoomHandler, room.getRoomid());
					new Thread(er).start();
				} else if (status.equalsIgnoreCase("failed")){
					Log.i("lin", "exit room info: "+info);
					if (info.equalsIgnoreCase("not login")){
						Toast.makeText(getApplicationContext(), "尚未登录", Toast.LENGTH_SHORT).show();
					}else if (info.equalsIgnoreCase("user not in this room")){
						Toast.makeText(getApplicationContext(), "您不在此房间中", Toast.LENGTH_SHORT).show();
					}else if (info.equalsIgnoreCase("room not found")){
						Toast.makeText(getApplicationContext(), "没找到房间", Toast.LENGTH_SHORT).show();
					}
				}
				break;
			case Constant.NETWORK_FAILED_MESSAGE_TAG:
				Toast.makeText(getApplicationContext(), "网络连接有错", Toast.LENGTH_SHORT).show();
				break;
			}
		}
		
	};
	private Handler enterRoomHandler = new Handler(){
		@Override
    	public void handleMessage(Message msg){
			dialog.cancel();
			Bundle bundle = msg.getData();
			String status = bundle.getString("status", "no status");
			String info = bundle.getString("info", "no info");
			switch(msg.what){
			case Constant.NETWORK_SUCCESS_MESSAGE_TAG:
				
				if (status.equalsIgnoreCase("succeed")){
					exitRoomBtn.setText("退出房间");
					exitRoomBtn.setBackgroundColor(Color.rgb(253, 82, 73));
					gva.curRoomId = room.getRoomid();
					gva.teamid = 0;
					exitRoomBtn.setText("退出房间");
					exitRoomBtn.setBackgroundColor(Color.rgb(253, 82, 73));
					GetRoomInfo gri = new GetRoomInfo(gva.httpClient, getRoomInfoHandler, gva.curRoomId);
					new Thread(gri).start();
				} else if (status.equalsIgnoreCase("failed")){
					Log.i("lin", "enter room info: "+info);
					if (info.equalsIgnoreCase("not login")){
						Toast.makeText(getApplicationContext(), "尚未登录", Toast.LENGTH_SHORT).show();
					}else if (info.equalsIgnoreCase("team not found")){
						Toast.makeText(getApplicationContext(), "没找到队伍", Toast.LENGTH_SHORT).show();
					}else if (info.equalsIgnoreCase("room not found")){
						Toast.makeText(getApplicationContext(), "没找到房间", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.setClass(RoomNew.this, HomeActivity.class);
						startActivity(intent);
						finish();
					}else if (info.equalsIgnoreCase("in game")){
						Toast.makeText(getApplicationContext(), "正在游戏中", Toast.LENGTH_SHORT).show();
					}
				}
				break;
			case Constant.NETWORK_FAILED_MESSAGE_TAG:
				Toast.makeText(getApplicationContext(), "网络连接有错", Toast.LENGTH_SHORT).show();
				break;
			}
		}
		
	};
	private Handler getRoomInfoHandler = new Handler(){
		@Override
    	public void handleMessage(Message msg){
    		dialog.cancel();
    		Bundle bundle = msg.getData();
    		if(bundle.getString("status").equals("succeed")){
	    		room = bundle.getParcelable("Room");
	    		teamList = bundle.getParcelableArrayList("TeamList");
	    		routeid = room.getRouteid();
	    		setData();
	            updateUI();
	            updateMyTeam();
	            updateNotChosen();
	    		updateOtherTeam();
	    		setOnClicks();
	    		GetRoute gr = new GetRoute(gva.httpClient, getRouteHandler, routeid);
	    		new Thread(gr).start();
    		}else if(bundle.getString("status").equals("failed")){
    			Toast.makeText(getApplicationContext(), bundle.getString("info"), 1000).show();
    			Intent i = new Intent();
    			i.setClass(RoomNew.this, HomeActivity.class);
    			startActivity(i);
    			finish();
    		}
    		
		}
	};
	private Handler getRouteHandler = new Handler() {
		@Override
	 	public void handleMessage(android.os.Message msg){
			Bundle bundle = msg.getData();
    		String status = bundle.getString("status", "no status");
    		String info = bundle.getString("info", "no info");
    		String pointlist = bundle.getString("Pointlist", "no point list");
    		gva.points = new ArrayList<HashMap<String,Integer>> ();
    		switch(msg.what){
			case Constant.NETWORK_SUCCESS_MESSAGE_TAG:
				if (status.equalsIgnoreCase("succeed")){
					try {
						gva.points = ParsePointList.parsePointList(pointlist);
						Log.i("cab", "pointlist parsed! --> gva"+gva.points.size());
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (XmlPullParserException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (status.equalsIgnoreCase("failed")){
					if (info.equalsIgnoreCase("not login")){
						Toast.makeText(getApplicationContext(), "尚未登录", 
								Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getApplicationContext(), "未知错误", 
								Toast.LENGTH_SHORT).show();
					}
				}
				break;
			case Constant.NETWORK_FAILED_MESSAGE_TAG:
				Toast.makeText(getApplicationContext(), "网络连接有错，请稍后再试",
						Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}   
	    		
 	}
	};
	private Handler changeTeamNameHandler = new Handler(){
    	@Override
	 	public void handleMessage(android.os.Message msg){
	    		
    		Bundle bundle = msg.getData();
	    	String status = bundle.getString("status", "no status");
	    	String info = bundle.getString("info", "no info");
	    	if (status.equalsIgnoreCase("succeed")){
	    		Toast.makeText(getApplicationContext(), "修改队名成功", Toast.LENGTH_SHORT).show();
	    	}else if (status.equalsIgnoreCase("failed")){
	    		if (info.equalsIgnoreCase("not login")){
	    			Toast.makeText(getApplicationContext(), "尚未登录", Toast.LENGTH_SHORT).show();
	    		}else {
	    			Toast.makeText(getApplicationContext(), "修改失败", Toast.LENGTH_SHORT).show();
	    			Log.i("lin", "change team name: "+info);
	    		}
	    	}
    	}
    };
	private Handler createTeamHandler = new Handler() {
		@Override
	 	public void handleMessage(android.os.Message msg){
	    		
    		Bundle bundle = msg.getData();
	    	String status = bundle.getString("status", "no status");
	    	String info = bundle.getString("info", "no info");
	    	
	    	if (status.equalsIgnoreCase("succeed")){
	    		gva.teamid = Integer.valueOf(bundle.getString("TeamId"));
	    		Toast.makeText(getApplicationContext(), "创建新队伍成功", Toast.LENGTH_SHORT).show();
	    		refresh();
	    	}else if (status.equalsIgnoreCase("failed")){
	    		if (info.equalsIgnoreCase("not login")){
	    			Toast.makeText(getApplicationContext(), "尚未登录", Toast.LENGTH_SHORT).show();
	    		}else if (info.equalsIgnoreCase("user not in room")){
	    			Toast.makeText(getApplicationContext(), "用户不在任何房间内", Toast.LENGTH_SHORT).show();
	    		}else if (info.equalsIgnoreCase("not in any team")){
	    			Toast.makeText(getApplicationContext(), "用户不在任何一个队伍内", Toast.LENGTH_SHORT).show();
	    		}else if (info.equalsIgnoreCase("not found")){
	    			Toast.makeText(getApplicationContext(), "找不到该房间", Toast.LENGTH_SHORT).show();
	    		}else if (info.equalsIgnoreCase("team not found")){
	    			Toast.makeText(getApplicationContext(), "找不到该队伍", Toast.LENGTH_SHORT).show();
	    		}else if (info.equalsIgnoreCase("user not in this room")){
	    			Toast.makeText(getApplicationContext(), "用户不在此房间内", Toast.LENGTH_SHORT).show();
	    		}else {
	    			Toast.makeText(getApplicationContext(), "未知错误", Toast.LENGTH_SHORT).show();
	    		}
	    	}
    	}
	};
	private Handler changeToTeamHandler = new Handler(){
    	@Override
	 	public void handleMessage(android.os.Message msg){
    		Bundle bundle = msg.getData();
	    	String status = bundle.getString("status", "no status");
	    	String info = bundle.getString("info", "no info");
	    	if (status.equalsIgnoreCase("succeed")){
	    		gva.teamid = Integer.parseInt(bundle.getString("TeamId", String.valueOf(-1)));
	    		Toast.makeText(getApplicationContext(), "成功选择队伍", Toast.LENGTH_SHORT).show();
	    		refresh();
	    	}else if (status.equalsIgnoreCase("failed")){
	    		if (info.equalsIgnoreCase("not login")){
	    			Toast.makeText(getApplicationContext(), "尚未登录", Toast.LENGTH_SHORT).show();
	    		} else if (info.equalsIgnoreCase("not in any room")) {
	    			Toast.makeText(getApplicationContext(), "不在任何房间中", Toast.LENGTH_SHORT).show();
	    		} else if (info.equalsIgnoreCase("not in any team")) {
	    			Toast.makeText(getApplicationContext(), "不在队伍中", Toast.LENGTH_SHORT).show();
	    		} else if (info.equalsIgnoreCase("already in team")) {
	    			Toast.makeText(getApplicationContext(), "已经在这个队伍中", Toast.LENGTH_SHORT).show();
	    		} else if (info.equalsIgnoreCase("room not found")) {
	    			Toast.makeText(getApplicationContext(), "找不到该房间", Toast.LENGTH_SHORT).show();
	    		} else if (info.equalsIgnoreCase("team not found")) {
	    			Toast.makeText(getApplicationContext(), "找不到该队伍", Toast.LENGTH_SHORT).show();
	    		} else if (info.equalsIgnoreCase("user not in this room")) {
	    			Toast.makeText(getApplicationContext(), "不在该房间中", Toast.LENGTH_SHORT).show();
	    		} else {
	    			Toast.makeText(getApplicationContext(), "修改失败", Toast.LENGTH_SHORT).show();
	    		}
	    	} else {
	    		Toast.makeText(getApplicationContext(), "未知错误", Toast.LENGTH_SHORT).show();
	    	}
    	}
    };
	private void updateMyTeam(){
		if (gva.curRoomId != room.getRoomid() || gva.teamid == 0){ //不在这个房间里面 或者 还没有选择队伍
			myteam_ll.setVisibility(View.GONE);
			return;
		}else {
			myteam_ll.setVisibility(View.VISIBLE);
		}
		myteam_tv.setText(myteam.getTeamName());
		myteam_et.setText(myteam.getTeamName());
		ArrayList<HashMap<String, Object>> memItem = new ArrayList<HashMap<String, Object>>();
		ArrayList<TeamMemberParcelable> myteam_mems = (ArrayList<TeamMemberParcelable>) myteam.getTeamMemberList();
		for (int i = 0; i < myteam_mems.size(); i++){
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("MemImage", portrait.getPortraitResource(myteam_mems.get(i).getPortrait()));
			map.put("Name", myteam_mems.get(i).getName());
			memItem.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, memItem, R.layout.team_member_item,
				new String[]{"MemImage", "Name"},
				new int[]{R.id.userphoto, R.id.username});
		myteam_gv.setAdapter(adapter);
	}
	private void updateNotChosen(){
		ArrayList<HashMap<String, Object>> memItem = new ArrayList<HashMap<String, Object>>();
		ArrayList<TeamMemberParcelable> team_mems = (ArrayList<TeamMemberParcelable>) notchosen.getTeamMemberList();
		for (int i = 0; i < team_mems.size(); i++){
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("MemImage", portrait.getPortraitResource(team_mems.get(i).getPortrait()));
			Log.i("yuan", "portrait ---> " + team_mems.get(i).getPortrait());
			map.put("Name", team_mems.get(i).getName());
			memItem.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, memItem, R.layout.team_member_item,
				new String[]{"MemImage", "Name"},
				new int[]{R.id.userphoto, R.id.username});
		notchosen_gv.setAdapter(adapter);
	}
	private void updateOtherTeam(){
		otherteams_lv.setAdapter(new ListViewAdapter(otherTeams));
	}
	
	private class ListViewAdapter extends BaseAdapter{
		View[] itemViews;
		
		public ListViewAdapter(List<TeamParcelable> teams) {
			super();
			itemViews = new View[teams.size()];
			for (int i = 0; i < itemViews.length; i++){
				itemViews[i] = makeItemView(teams.get(i).getTeamName(), teams.get(i).getTeamMemberList(), teams.get(i).getTeamid());
			}
		}
		public ListViewAdapter(){}

		@Override
		public int getCount() {
			return itemViews.length;
		}

		@Override
		public Object getItem(int arg0) {
			return itemViews[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg1 == null)
                return itemViews[arg0];  
            return arg1; 
		}
		
		private View makeItemView(String pTeamName, List<TeamMemberParcelable> pMembers, int newteamid){
			LayoutInflater inflater = getLayoutInflater();
			View itemView = inflater.inflate(R.layout.otherteam_listitem, null);
			TextView teamName_tv = (TextView) itemView.findViewById(R.id.teamName);
			teamName_tv.setText(pTeamName);
			ImageView joinTeam_im = (ImageView) itemView.findViewById(R.id.joinTeam);
			GridView gv = (GridView) itemView.findViewById(R.id.gridView1);
			ArrayList<HashMap<String, Object>> memItem = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; i < pMembers.size(); i++){
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("MemImage", portrait.getPortraitResource(pMembers.get(i).getPortrait()));
				map.put("Name", pMembers.get(i).getName());
				memItem.add(map);
			}
			SimpleAdapter adapter = new SimpleAdapter(RoomNew.this, memItem, R.layout.team_member_item,
					new String[]{"MemImage", "Name"},
					new int[]{R.id.userphoto, R.id.username});
			gv.setAdapter(adapter);
			newTeamId = newteamid;
			joinTeam_im.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(gva.curRoomId != room.getRoomid()){
						Toast.makeText(getApplicationContext(), "不在当前房间不能选择", Toast.LENGTH_SHORT).show();
						return;
					}
					Toast.makeText(getApplicationContext(), "你选择了队伍：" + newTeamId, Toast.LENGTH_SHORT).show();
					ChangeToTeam ctt = new ChangeToTeam(gva.httpClient, changeToTeamHandler, newTeamId);
					new Thread(ctt).start();
				}
			});
			return itemView;
		}
	}
	
	private void setOnClicks(){
		edit_iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(gva.curRoomId != room.getRoomid()){
					Toast.makeText(getApplicationContext(), "不在当前房间不能选择", Toast.LENGTH_SHORT).show();
					return;
				}
				if (!isEditTeamName){
					isEditTeamName = !isEditTeamName;
					myteam_tv.setVisibility(View.GONE);
					myteam_et.setVisibility(View.VISIBLE);
					myteam_et.setFocusable(true);
					myteam_et.setFocusableInTouchMode(true);
					myteam_et.requestFocus();
					myteam_et.setSelection(myteam_et.getText().length());
					imm = (InputMethodManager)myteam_et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

			        imm.showSoftInput(myteam_et, 0); //显示软键盘  
			        edit_iv.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_save));
				}
				else {
					isEditTeamName = !isEditTeamName;
					myteam_tv.setVisibility(View.VISIBLE);
					myteam_et.setVisibility(View.GONE);
					myteam_tv.setText(myteam_et.getText());
					imm.hideSoftInputFromWindow(edit_iv.getWindowToken(), 0);//隐藏软键盘 
			        edit_iv.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_edit));
			        ChangeTeamName ctn = new ChangeTeamName(gva.httpClient, changeTeamNameHandler, 
			        		myteam_et.getText().toString());
			        new Thread(ctn).start();
				}
			}
		});
	}
	private void setListViewHeightBasedOnChildren(ListView listView){
		ListAdapter listAdapter = listView.getAdapter();   
        if (listAdapter == null) {  
            // pre-condition  
            return;  
        }  
  
        int totalHeight = 0;  
        for (int i = 0; i < listAdapter.getCount(); i++) {  
            View listItem = listAdapter.getView(i, null, listView);  
            listItem.measure(0, 0);  
            totalHeight += listItem.getMeasuredHeight();  
        }  
  
        ViewGroup.LayoutParams params = listView.getLayoutParams();  
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));  
        listView.setLayoutParams(params);  
	}
	private void refresh() {
		GetRoomInfo gr = new GetRoomInfo(gva.httpClient, getRoomInfoHandler, gva.curRoomId);
		new Thread(gr).start();
	}
}
