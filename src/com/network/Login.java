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

public class Login extends BaseNetwork{
	String userName, pw;
	public Login(HttpClient pClient, Handler pHandler, String pUserName, String pPassWord) {
		super(pClient, pHandler, "login");
		userName = pUserName;
		pw = pPassWord;
		setParamsList();
	}

	@Override
	void setParamsList() {
		paramsList.add(new BasicNameValuePair("username", userName));
        paramsList.add(new BasicNameValuePair("pw", pw));
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
				}else if (tag.equalsIgnoreCase("RoomId")){
					bundle.putString("RoomId", parser.nextText());
				}else if (tag.equalsIgnoreCase("TeamId")){
					bundle.putString("TeamId", parser.nextText());
				}else if (tag.equalsIgnoreCase("userinfo")) {
					parseEvent = parser.next();
					while (!(parseEvent == XmlPullParser.END_TAG && parser.getName().equalsIgnoreCase("userinfo"))){
						switch(parseEvent){
						case XmlPullParser.START_TAG:
							tag = parser.getName();
							if (tag.equalsIgnoreCase("nickname"))
								bundle.putString("nickname", parser.nextText());
							else if (tag.equalsIgnoreCase("gender"))
								bundle.putString("telephone", parser.nextText());
							else if (tag.equalsIgnoreCase("telephone"))
								bundle.putString("telephone", parser.nextText());
							else if (tag.equalsIgnoreCase("portrait"))
								bundle.putString("portrait", parser.nextText());
							break;
						default:
							break;
						}
						parseEvent = parser.next();
					}
				}
				break;
			}
			parseEvent = parser.next();
		}
		return bundle;
	}
}
