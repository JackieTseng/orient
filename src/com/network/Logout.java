package com.network;

import java.io.IOException;
import java.io.StringReader;

import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.R.string;
import android.os.Bundle;
import android.os.Handler;

public class Logout extends BaseNetwork {
	String userName;
	public Logout(HttpClient pClient, Handler pHandler) {
		super(pClient, pHandler, "logout");
		// TODO Auto-generated constructor stub
		setParamsList();
	}

	@Override
	void setParamsList() {
		// TODO Auto-generated method stub
	}

	@Override
	protected Bundle parseXML(String stream) throws XmlPullParserException,
			IOException {
		// TODO Auto-generated method stub
		Bundle bundle = new Bundle();
		XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
		XmlPullParser parser = parserFactory.newPullParser();
		parser.setInput(new StringReader(stream));
		int parserEvent = parser.getEventType();
		while (parserEvent != XmlPullParser.END_DOCUMENT) {
			switch(parserEvent) {
			case XmlPullParser.START_TAG:
				String tag = parser.getName();
				if (tag.equalsIgnoreCase("status")) {
					bundle.putString("status", parser.nextText());
				} else if (tag.equalsIgnoreCase("info")) {
					bundle.putString("info", parser.nextText());
				}
				break;
			default:
				break;
			}
			parserEvent = parser.next();
		}
		return bundle;
	}

}
