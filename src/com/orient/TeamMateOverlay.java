package com.orient;
import android.R.integer;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class TeamMateOverlay extends ItemizedOverlay<OverlayItem> {  
    //用MapView构造ItemizedOverlay  
    public TeamMateOverlay(Drawable mark,MapView mapView){  
            super(mark,mapView);  
    }  
    protected boolean onTap(final int index) {  
        //在此处理item点击事件  
    	final OverlayItem item = getItem(index);
    	//Toast.makeText(GameMap.context, item.getTitle(), 2000).show();
    	if(item.getTitle()=="teammate"){
    		GeoPoint teammatepos = item.getPoint();
    		//GeoPoint missonpos = GameMap.context.curmissonpos;
    		
    	}
        return true;  
    }  
    
    	//计算距离
  		static double DEF_PI = 3.14159265359; // PI
  		static double DEF_2PI= 6.28318530712; // 2*PI
  		static double DEF_PI180= 0.01745329252; // PI/180.0
  		static double DEF_R =6370693.5; // radius of earth
  		public double GetShortDistance(double lon1, double lat1, double lon2, double lat2)
  		{
  			double ew1, ns1, ew2, ns2;
  			double dx, dy, dew;
  			double distance;
  			// 角度转换为弧度
  			ew1 = lon1 * DEF_PI180;
  			ns1 = lat1 * DEF_PI180;
  			ew2 = lon2 * DEF_PI180;
  			ns2 = lat2 * DEF_PI180;
  			// 经度差
  			dew = ew1 - ew2;
  			// 若跨东经和西经180 度，进行调整
  			if (dew > DEF_PI)
  			dew = DEF_2PI - dew;
  			else if (dew < -DEF_PI)
  			dew = DEF_2PI + dew;
  			dx = DEF_R * Math.cos(ns1) * dew; // 东西方向长度(在纬度圈上的投影长度)
  			dy = DEF_R * (ns1 - ns2); // 南北方向长度(在经度圈上的投影长度)
  			// 勾股定理求斜边长
  			distance = Math.sqrt(dx * dx + dy * dy);
  			return distance;
  		}
  		public double GetLongDistance(double lon1, double lat1, double lon2, double lat2)
  		{
  			double ew1, ns1, ew2, ns2;
  			double distance;
  			// 角度转换为弧度
  			ew1 = lon1 * DEF_PI180;
  			ns1 = lat1 * DEF_PI180;
  			ew2 = lon2 * DEF_PI180;
  			ns2 = lat2 * DEF_PI180;
  			// 求大圆劣弧与球心所夹的角(弧度)
  			distance = Math.sin(ns1) * Math.sin(ns2) + Math.cos(ns1) * Math.cos(ns2) * Math.cos(ew1 - ew2);
  			// 调整到[-1..1]范围内，避免溢出
  			if (distance > 1.0)
  			     distance = 1.0;
  			else if (distance < -1.0)
  			      distance = -1.0;
  			// 求大圆劣弧长度
  			distance = DEF_R * Math.acos(distance);
  			return distance;
  		}
    
}