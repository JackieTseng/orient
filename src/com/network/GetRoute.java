package com.network;

import java.io.IOException;
import java.io.StringReader;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.Bundle;
import android.os.Handler;

public class GetRoute extends BaseNetwork{
	private int routeid;
	public GetRoute(HttpClient pClient, Handler pHandler, int routeid) {
		super(pClient, pHandler, "getroute");
		this.routeid = routeid;
		setParamsList();
	}
	@Override
	void setParamsList() {
		paramsList.add(new BasicNameValuePair("routeid", String.valueOf(routeid)));
	}
	@Override
	protected Bundle parseXML(String stream) throws XmlPullParserException,
			IOException {
		// TODO Auto-generated method stub
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
				}else if (tag.equalsIgnoreCase("Pointlist")){
					bundle.putString("Pointlist", stream);
				}else if (tag.equalsIgnoreCase("info")){
					bundle.putString("info", parser.nextText());
				}
				break;
			}
			parseEvent = parser.next();
		}
		return bundle;
	}

}
