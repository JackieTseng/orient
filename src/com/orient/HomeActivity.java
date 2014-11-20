package com.orient;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

import com.orient.R;
import com.slidingmenu.LeftFragment;
import com.slidingmenu.RightFragment;
import com.slidingmenu.ViewPageFragment;
import com.slidingmenu.ViewPageFragment.MyPageChangeListener;
import com.slidingmenu.SlidingMenu;

public class HomeActivity extends FragmentActivity {
	SlidingMenu mSlidingMenu;
	LeftFragment leftFragment;
	RightFragment rightFragment;
	ViewPageFragment viewPageFragment;
	
	// 分辨率
    private int disPlayWidth;
    DisplayMetrics dm = new DisplayMetrics();
    GlobalVarApplication gva;

    private long exitTime = 0;
    
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		getWindowManager().getDefaultDisplay().getMetrics(dm);
        disPlayWidth = dm.widthPixels;
		init();
		initListener();

	}

	private void init() {
		mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMenu);
		mSlidingMenu.setLeftView(getLayoutInflater().inflate(
				R.layout.left_frame, null));
		mSlidingMenu.setRightView(getLayoutInflater().inflate(
				R.layout.right_frame, null));
		mSlidingMenu.setCenterView(getLayoutInflater().inflate(
				R.layout.center_frame, null));

		FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();
		leftFragment = new LeftFragment();
		t.replace(R.id.left_frame, leftFragment);

		rightFragment = new RightFragment();
		t.replace(R.id.right_frame, rightFragment);

		viewPageFragment = new ViewPageFragment();
		t.replace(R.id.center_frame, viewPageFragment);
		t.commit();
	}

	private void initListener() {
		viewPageFragment.setMyPageChangeListener(new MyPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				if(viewPageFragment.isFirst()){
					mSlidingMenu.setCanSliding(true,false);
				}else if(viewPageFragment.isEnd()){
					mSlidingMenu.setCanSliding(false,true);
				}else{
					mSlidingMenu.setCanSliding(false,false);
				}
			}
		});
	}

	public void showLeft() {
		mSlidingMenu.showLeftView();
	}

	public void showRight() {
        mSlidingMenu.showRightView();
    }
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){

        if((System.currentTimeMillis()-exitTime) > 2000){
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();                                exitTime = System.currentTimeMillis();
        }
        else{
            finish();
            System.exit(1);
            }

        return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

