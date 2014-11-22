package com.slidingmenu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.baidu.mapapi.map.LocationData;
import com.constant.Constant;
import com.network.GetRoomList;
import com.network.PostPosition;
import com.orient.GlobalVarApplication;
import com.orient.HomeActivity;
import com.orient.R;
import com.orient.RefreshableView;
import com.orient.RoomNew;
import com.orient.Room_Second_1;
import com.orient.RefreshableView.PullToRefreshListener;
import com.util.Location;
import com.util.ParseRoomList;
import com.util.Room;


public class PageFragment1 extends Fragment {
    private ListView listView;
    private List<Room> rooms;
    GlobalVarApplication gva;
    ImageButton createRoomImageButton;
    private TextView addrname;
	//下拉刷新列表
    RefreshableView refreshableView;
    Location location;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addrname = (TextView)this.getActivity().findViewById(R.id.addr_name);
    }
    
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.page1, null);
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		gva = (GlobalVarApplication)getActivity().getApplication();
		rooms = new ArrayList<Room>();
		location = new Location();
        location.positioning(getActivity(), positioningHandler, gva.getMyLocationListener(), false);
        //下拉刷新列表
        refreshableView = (RefreshableView) getActivity().findViewById(R.id.refreshable_view);  
        listView = (ListView) getActivity().findViewById(R.id.home_listView);
        //下拉刷新监听器
        refreshableView.setOnRefreshListener(new PullToRefreshListener() {  
            @Override  
            public void onRefresh() {
            	GetRoomList g = new GetRoomList(gva.httpClient, getRoomListHandler);
                new Thread(g).start();
                try {  
                    Thread.sleep(5000);  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                }  
                refreshableView.finishRefreshing();  
            }  
        }, 0);
        
        createRoomImageButton = (ImageButton)getActivity().findViewById(R.id.create_imageButton);
        createRoomImageButton.setOnClickListener(new OnClickListener() { 
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(getActivity(), Room_Second_1.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        
        
	}

    private Handler getRoomListHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            Bundle bundle = msg.getData();
            String status = bundle.getString("status", "no status");
            String info = bundle.getString("info", "no info");
            String roomlist = bundle.getString("Roomlist", "no room list");
            switch(msg.what){
            case Constant.NETWORK_SUCCESS_MESSAGE_TAG:
                if (status.equalsIgnoreCase("succeed")){
                    try {
                        rooms = ParseRoomList.parseRoomList(roomlist);
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
                    show();
                } else if (status.equalsIgnoreCase("failed")){
                    if (info.equalsIgnoreCase("not login")){
                        Toast.makeText(getActivity(), "尚未登录", 
                                Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getActivity(), "未知错误", 
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case Constant.NETWORK_FAILED_MESSAGE_TAG:
                Toast.makeText(getActivity(), "网络连接有错，请稍后再试",
                        Toast.LENGTH_LONG).show();
                break;
            default:
                break;
            }
        }
    };
    
    private Handler positioningHandler = new Handler(){
        @Override
           public void handleMessage(android.os.Message msg){
                
                Bundle bundle = msg.getData();                      
                if(bundle.getString("result").equals("location")){
                    LocationData locData = new LocationData();  
                      
                    locData.latitude = bundle.getDouble("latitude");  
                    locData.longitude =  bundle.getDouble("longitude");
                    locData.direction = bundle.getFloat("direction");
                    String addr = bundle.getString("address");
                    gva.address = addr;
                    Log.i("lin", "get location succeed");
                    Log.i("lin", "longitude: "+locData.longitude+" latitude: "+locData.latitude);
                    Log.i("lin", "address: "+addr);
                    //View mView = thisinflater.inflate(R.layout.view_pager, null);
                    
                    addrname.setText(gva.address);
                    uploadPosition((int)(locData.longitude*1e6), (int)(locData.latitude*1e6));
                }else {
                    Toast.makeText(getActivity(), "努力定位ing...", 2000).show();
                }
            }
    };
    
    public boolean uploadPosition(int pLongitude, int pLatitude){
        final Handler handler2 = new Handler(){
            
            @Override
            public void handleMessage(Message msg){
                Bundle bundle = msg.getData();
                String status = bundle.getString("status");
                String info = bundle.getString("info", "no info");
                switch(msg.what){
                case Constant.NETWORK_SUCCESS_MESSAGE_TAG:
                    if (status.equalsIgnoreCase("succeed")) {
                        System.out.println("get room list");
                        GetRoomList g = new GetRoomList(gva.httpClient, getRoomListHandler);
                        new Thread(g).start();
                    }else if (status.equalsIgnoreCase("failed")){
                        if (info.equalsIgnoreCase("not login")){
                            Toast.makeText(getActivity(), "尚未登录", 
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity(), "未知错误", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getActivity(), "未知错误", Toast.LENGTH_LONG).show();
                    }
                    break;
                case Constant.NETWORK_FAILED_MESSAGE_TAG:
                    Log.i("lin", "经纬度暂时不能上传");
                    break;
                default:
                    break;
                }
            
            }
        };
        Handler handler3 = new Handler(){

            @Override
            public void handleMessage(Message msg){
                Bundle bundle = msg.getData();                      
                if(bundle.getString("result").equals("succeed")){
                    String addr = bundle.getString("address");
                    Log.i("lin", "addr: "+addr);
                }else {
                    Toast.makeText(getActivity(), "努力转换地址信息ing...", 2000).show();
                }
            
            }
        };
        PostPosition p = new PostPosition(gva.httpClient, handler2, pLongitude, pLatitude);
        new Thread(p).start();
        //Location.reverseGeocode(getApplicationContext(), handler3, pLongitude, pLatitude);
        return true;
    }
    
    private void show() {
        String[] mFrom = new String[]{"image","roomname","point","time","button"};
        int[] mTo = new int[]{R.id.home_item_image,R.id.home_item_roomname,R.id.home_item_point,
                R.id.home_item_time,R.id.home_item_button};
        
        ArrayList<HashMap<String, Object>> mList = new ArrayList<HashMap<String,Object>>();
        for (int i = 0; i < rooms.size(); i++){
            HashMap<String,Object> mMap = new HashMap<String,Object>();
            Room room = rooms.get(i);
            mMap.put("image", R.drawable.group);
            if (gva.curRoomId == room.getRoomid()){
                mMap.put("button", R.drawable.joined);
            }else{
                mMap.put("button", R.drawable.join);
            }
            mMap.put("point", room.getAddress()+" "+room.getDistance());
            mMap.put("roomname", room.getRoomName());
            mMap.put("time", room.getTime());
            mList.add(mMap);
        }
        lvButtonAdapter mAdapter = new lvButtonAdapter(getActivity(),mList,R.layout.home_item,mFrom,mTo);
        listView.setAdapter(mAdapter);
       
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){  
         @Override  
             public void onItemSelected(AdapterView<?> parent, View view,  
                     int position, long id) {  
                     //当此选中的item的子控件需要获得焦点时   
                     listView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);  
                     //else parent.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);   
            }  
             @Override  
             public void onNothingSelected(AdapterView<?> parent) {  
                listView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);  
             }  
        });
        
        listView.setOnItemClickListener(new OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                   long arg3) {
               // TODO Auto-generated method stub

        	   Intent intent = new Intent();
        	   intent.putExtra("RoomId", rooms.get(arg2).getRoomid());
               intent.setClass(getActivity(), RoomNew.class);
               startActivity(intent);
               getActivity().finish();
           }
            
       });
   }

    public class lvButtonAdapter extends BaseAdapter {
        private class buttonViewHolder {
            ImageView appView;
            TextView appRoomName;
            TextView appPoint;
            TextView appTime;
            ImageButton button;
        }
        
        private ArrayList<HashMap<String, Object>> mAppList;
        private LayoutInflater mInflater;
        private Context mContext;
        private String[] keyString;
        private int[] valueViewID;
        private buttonViewHolder holder;
        
        public lvButtonAdapter(Context c, ArrayList<HashMap<String, Object>> appList, int resource,
                String[] from, int[] to) {
            mAppList = appList;
            mContext = c;
            mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            keyString = new String[from.length];
            valueViewID = new int[to.length];
            System.arraycopy(from, 0, keyString, 0, from.length);
            System.arraycopy(to, 0, valueViewID, 0, to.length);
        }
        @Override
        public int getCount() {
            return mAppList.size();
        }

        @Override
        public Object getItem(int position) {
            return mAppList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void removeItem(int position){
            mAppList.remove(position);
            this.notifyDataSetChanged();
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView != null) {
                holder = (buttonViewHolder) convertView.getTag();
            } else {
                convertView = mInflater.inflate(R.layout.home_item, null);
                holder = new buttonViewHolder();
                holder.appView = (ImageView)convertView.findViewById(valueViewID[0]);
                holder.appRoomName = (TextView)convertView.findViewById(valueViewID[1]);
                holder.appPoint = (TextView)convertView.findViewById(valueViewID[2]);
                holder.appTime = (TextView)convertView.findViewById(valueViewID[3]);
                holder.button = (ImageButton)convertView.findViewById(valueViewID[4]);
                convertView.setTag(holder);
            }
            
            HashMap<String, Object> appInfo = mAppList.get(position);
            if (appInfo != null) {
                int viewId = (Integer)appInfo.get(keyString[0]);
                String aRoomName = (String) appInfo.get(keyString[1]);
                String aPoint = (String) appInfo.get(keyString[2]);
                String aTime = (String) appInfo.get(keyString[3]);
                int buttonId = (Integer)appInfo.get(keyString[4]);
                holder.appRoomName.setText(aRoomName);
                holder.appPoint.setText(aPoint);
                holder.appTime.setText(aTime);
                holder.appView.setImageDrawable(holder.appView.getResources().getDrawable(viewId));
                holder.button.setImageDrawable(holder.button.getResources().getDrawable(buttonId));

            }         
            return convertView;
        }
      
    }
}

