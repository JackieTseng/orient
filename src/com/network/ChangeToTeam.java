package com.network;

import java.io.IOException;
import java.io.StringReader;

import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class ChangeToTeam extends BaseNetwork{
	private int newteamid;
	public ChangeToTeam(HttpClient pClient, Handler pHandler, int pteamid) {
		super(pClient, pHandler, "changetoteam");
		newteamid = pteamid;
		setParamsList();
	}
	@Override
	void setParamsList() {
		Log.i("yuan","newteamid---->"+String.valueOf(newteamid));
		paramsList.add(new BasicNameValuePair("teamid", String.valueOf(newteamid)));
		
	}
	@Override
	protected Bundle parseXML(String stream) throws XmlPullParserException,
			IOException {
		Bundle bundle = new Bundle();
		XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
		XmlPullParser parser = parserFactory.newPullParser();
		parser.setInput(new StringReader(stream));
		int parseEvent = parser.getEventType();
		while (parseEvent != XmlPullParser.END_DOCUMENT){
			switch(parseEvent){
			case XmlPullParser.START_TAG:
				String tag = parser.getName();
				if (tag.equalsIgnoreCase("status")){
					bundle.putString("status", parser.nextText());
				}else if (tag.equalsIgnoreCase("info")){
					bundle.putString("info", parser.nextText());
				}else if (tag.equalsIgnoreCase("TeamId")){
					bundle.putString("TeamId", parser.nextText());
				}
				break;
			}
			parseEvent = parser.next();
		}
		return bundle;
	}

}
