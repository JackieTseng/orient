package com.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.orient.R;
import com.otomod.ad.AdFloatTip;
import com.otomod.ad.AdSize;
import com.otomod.ad.AdView;
import com.otomod.ad.listener.O2OAdListener;

public class AdActivity extends Activity {

private final String APP_KEY = "a07e561cc93811e3a221f8bc123c968c"; // 开发者测试KEY
	
	private Button btnBanner;
	private Button btnPopup;
	private Button btnFullScreen;
	private Button btnFloatTip;
	private Button btnOpenScreen;
	private LinearLayout adLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 广告代码
		setContentView(R.layout.ad);
		
		// 静态广告条
		adLayout = (LinearLayout) findViewById(R.id.ad1);
		
		btnBanner = (Button) findViewById(R.id.btnBanner);
		btnBanner.setOnClickListener(bannerOnClickListener);
		btnOpenScreen = (Button) findViewById(R.id.btnOpenScreen);
		btnOpenScreen.setOnClickListener(openScreenOnClickListener);

		btnPopup = (Button) findViewById(R.id.btnPopup);
		btnPopup.setOnClickListener(popupOnClickListener);
		btnFullScreen = (Button) findViewById(R.id.btnFullScreen);
		btnFullScreen.setOnClickListener(fullScreenOnClickListener);
		btnFloatTip = (Button) findViewById(R.id.btnFloatTip);
		btnFloatTip.setOnClickListener(floatTipOnClickListener);
	}

	OnClickListener bannerOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			AdView adView = AdView.createBanner(AdActivity.this, adLayout, AdSize.AD_SMART_BANNER, APP_KEY);
			adView.setAdListener(new O2OAdListenerImpl());
			adView.request();
		}
	};

	OnClickListener popupOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			AdView adView = AdView.createPopup(AdActivity.this, APP_KEY);
			adView.setAdListener(new O2OAdListenerImpl());
			adView.request();
		}
	};

	OnClickListener fullScreenOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			AdView adView = AdView.createFullScreen(AdActivity.this, APP_KEY);
			adView.setAdListener(new O2OAdListenerImpl());
			adView.request();

		}
	};

	OnClickListener floatTipOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			AdFloatTip adFloatTip = new AdFloatTip(AdActivity.this, APP_KEY);
			adFloatTip.setAdListener(new O2OAdListenerImpl());
		}
	};

	OnClickListener openScreenOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			AdView adView = AdView.createOpenScreen(AdActivity.this, APP_KEY);
			adView.setAdListener(new O2OAdListener() {
				
				@Override
				public void onClose() {
					// 广告关闭回调
				}
				
				@Override
				public void onClick() {
					// 广告点击回调
				}
				
				@Override
				public void onAdSuccess() {
					// 广告展示成功回调
				}
				
				@Override
				public void onAdFailed() {
					// 广告展示失败回调
				}
			});
			adView.request();
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public class O2OAdListenerImpl implements O2OAdListener {

		@Override
		public void onClick() {
			System.out.println("o2o广告点击");
			Toast.makeText(AdActivity.this, "o2o广告点击", 1000).show();
		}

		@Override
		public void onClose() {
			 Toast.makeText(AdActivity.this, "o2o广告关闭", 1000).show();
		}

		@Override
		public void onAdFailed() {
			Toast.makeText(AdActivity.this, "o2o广告展示失败", 1000).show();
		}

		@Override
		public void onAdSuccess() {
			Toast.makeText(AdActivity.this, "o2o广告展示成功", 1000).show();
		}
	}

	
}
