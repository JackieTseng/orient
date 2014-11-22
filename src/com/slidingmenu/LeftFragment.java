/*
 * Copyright (C) 2012 yueyueniao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.slidingmenu;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.constant.Constant;
import com.network.Logout;
import com.orient.GlobalVarApplication;
import com.orient.LoginActivity;
import com.orient.R;
import com.util.ParseRoomList;

public class LeftFragment extends Fragment {
	private Button exitButton;
	GlobalVarApplication gva;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.left, null);
		gva = (GlobalVarApplication)this.getActivity().getApplication();
		exitButton = (Button)view.findViewById(R.id.exit_btn);
		exitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Logout logout = new Logout(gva.httpClient, logoutHandler);
				new Thread(logout).start();
				}
		});
		return view;
	}
	private Handler logoutHandler = new Handler() {
		@Override
        public void handleMessage(Message msg){
            Bundle bundle = msg.getData();
            String status = bundle.getString("status", "no status");
            switch(msg.what){
            case Constant.NETWORK_SUCCESS_MESSAGE_TAG:
                if (status.equalsIgnoreCase("succeed")){
                	Toast.makeText(getActivity(), "注销成功", Toast.LENGTH_SHORT);
                	gva.curRoomId = -1;
                	gva.teamid = -1;
                	Intent intent = new Intent();
                	intent.setClass(getActivity(), LoginActivity.class);
                	startActivity(intent);
                	getActivity().finish();
                } else if (status.equalsIgnoreCase("failed")){
                	Toast.makeText(getActivity(), "注销失败", Toast.LENGTH_SHORT);
                } else {
                	Toast.makeText(getActivity(), "未知错误", Toast.LENGTH_SHORT);
                }
                break;
            case Constant.NETWORK_FAILED_MESSAGE_TAG:
                Toast.makeText(getActivity(), "网络连接有错，请稍后再试", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
            }
        }
	};
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

}
