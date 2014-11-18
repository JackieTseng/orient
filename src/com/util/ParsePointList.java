package com.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;



public class ParsePointList {
	static private String safeNextText(XmlPullParser parser)  
	         throws XmlPullParserException, IOException {  
	     String result = parser.nextText();  
	     if (parser.getEventType() != XmlPullParser.END_TAG) {  
	         parser.nextTag();  
	     }  
	     return result;  
	 }
	
	static public ArrayList<HashMap<String,Integer>> parsePointList(String xml) throws XmlPullParserException, NumberFormatException, IOException{
		
		XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
		XmlPullParser parser = parserFactory.newPullParser();
		parser.setInput(new StringReader(xml));
		int parseEvent = parser.getEventType();
		HashMap<String,Integer> point = new HashMap<String,Integer>();
		ArrayList<HashMap<String,Integer>> pointlist = new ArrayList<HashMap<String,Integer>> ();
		String tag;
		while (parseEvent != XmlPullParser.END_DOCUMENT){
			switch(parseEvent){
			case XmlPullParser.START_TAG:
				tag = parser.getName();
				if (tag.equalsIgnoreCase("Point")){
					point = new HashMap<String,Integer>();
				}else if (tag.equalsIgnoreCase("longitude")){
					if (point == null)
						point = new HashMap<String,Integer>();
					point.put("longitude", Integer.valueOf( safeNextText(parser)));
				}else if (tag.equalsIgnoreCase("latitude")){
					if (point == null)
						point = new HashMap<String,Integer>();
					point.put("latitude", Integer.valueOf( safeNextText(parser)));
				}
				break;
			case XmlPullParser.END_TAG:
				tag = parser.getName();
				if (tag.equalsIgnoreCase("Point")){
					pointlist.add(point);
				}
				break;
			default:
				break;
			}
			parseEvent = parser.next();
			
		}
		return pointlist;
	}
}
