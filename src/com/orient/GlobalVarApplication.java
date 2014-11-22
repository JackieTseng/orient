package com.orient;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.BMapManager;
import com.constant.Constant;
import com.network.GetRoute;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class GlobalVarApplication extends Application{
	public HttpClient httpClient = null;
	public boolean clientLock = false;
	public static int playRouteId = -1;
	public static int curRoomId = -1;
	public static int teamid = -1;
	public String address = "";
	public String nickname = "";
	public String gender = "";
	public String telephone = "";
	public int portrait = 0;
	public static ArrayList<HashMap<String,Integer>> points;
	public Context context = null;
	public Handler getRemoteIdHandler;
	private static MySearchListener msi = null; 
	private BMapManager mBMapMan = null; 
	private static MyLocationListener mll = null;
	//private static LocationClient locationClient= null;
	public setRouteOverlay uploadRoute = null;
	private LocationClient mlc = null;
	@Override
	public void onCreate() {
		context = this;
		httpClient = new DefaultHttpClient();
	    ClientConnectionManager mgr = httpClient.getConnectionManager();
	    HttpParams params = httpClient.getParams();
	    httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params,mgr.getSchemeRegistry()), params);
		msi = new MySearchListener();
		mBMapMan = new BMapManager(getApplicationContext());
		mBMapMan.init("Kbe7fy7M05PhdOboeeRkkibv", null);
		mll = new MyLocationListener();
		mlc = new LocationClient(context);     //声明LocationClient类
		mlc.setAK("90ehkP9tBULpKYG8rbwXffjG");
		super.onCreate();
	}

	public MySearchListener getMKSearchListener(){
		return msi;
	}
	public BMapManager getBMapManager(){
		if(mBMapMan == null){
			mBMapMan = new BMapManager(getApplicationContext());
			mBMapMan.init("Kbe7fy7M05PhdOboeeRkkibv", null);
		}
		return mBMapMan;
	}
	/*public LocationClient getLocationClient(){
		return locationClient;
	}*/
	public MyLocationListener getMyLocationListener(){
		if(mll == null)
			mll = new MyLocationListener();
		return mll;
	}
	
	public LocationClient getMyLocationClient(){
		if(mlc == null){
			mlc = new LocationClient(context);     //声明LocationClient类
			mlc.setAK("90ehkP9tBULpKYG8rbwXffjG");
		}
		return mlc;
	}
	
	@Override
	public void onTerminate() {
		mBMapMan.destroy();
		super.onTerminate();
	}
	
}
