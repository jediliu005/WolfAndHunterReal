package com.jedi.wolf_and_hunter.utils;

public class StrTools {
	public static String transNULL(String s){
		if(s==null||s.trim().toLowerCase().equals("null"))
			s="";
		return s;
	}
	public static boolean isEmpty(String s){
		if(s==null||s.trim().equals("")||s.trim().toLowerCase().equals("null"))
			return true;
		return false;
	}
	public static String tranWrap(String s){
		if(s==null||s.trim().equals("")||s.trim().toLowerCase().equals("null"))
			return "";
		return s.replace("\r", "\n");
	}
}
