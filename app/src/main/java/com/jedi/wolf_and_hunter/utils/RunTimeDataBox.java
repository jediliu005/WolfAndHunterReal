package com.jedi.wolf_and_hunter.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public   class RunTimeDataBox {
	private static HashMap<String, Object> box;
	private static void initBox(){
		if(box==null)
			box=new HashMap<String, Object>();
	}
	
	/**
	 * �Ѹñ���ֶ���Ϣ�Ž�����
	 * @param tableName
	 * @param data
	 */
	public static void putColumnAndFieldDataIntoTheBox(String tableName,List<String> data){
		initBox();
		if(box.containsKey("COL_AND_FIELD")==false)
			box.put("COL_AND_FIELD",new  HashMap<String, List<String>>());
		Map<String, List<String>> maps = (Map<String, List<String>>) box.get("COL_AND_FIELD");
		maps.put(tableName, data);
	}
	
	
	/**
	 * �Ѹñ���ֶ���Ϣ�ӻ�����ȡ��
	 * @param tableName
	 * @return
	 */
	public static List<String> getColumnAndFieldDataFromTheBox(String tableName){
		initBox();
		if(box.containsKey("COL_AND_FIELD")==false){
			List<String> newList=new  ArrayList<String>();
			box.put("COL_AND_FIELD	",new  HashMap<String, List<String>>());
			return newList;
		}
		Map<String, List<String>> list = (Map<String, List<String>>) box.get("COL_AND_FIELD");
		return list.get(tableName);
	}
	
	public static void putAllPersonIds(List<String> data){
		initBox();
		box.put("ALL_PERSON_IDS",data);
	}
//	public static String getPersonIdFromAllIdsByIndex(int index) throws Exception{
//		initBox();
//		if(box.containsKey("ALL_PERSON_IDS")==false){
//			throw new Exception("�������ݲ����ڻ��ѱ����");
//		}
//		List<String> list=(List<String>)box.get("ALL_PERSON_IDS");
//		if(list.size()<=index){
//			throw new Exception("��ȡ�������ݳ���");
//		}
//			
//		return list.get(index);
//	}
	public static String getNextOrBeforePersonId(String personId,String nextOrBefore) throws Exception{
		initBox();
		nextOrBefore=nextOrBefore.toLowerCase();
		if((nextOrBefore.equals("next")||nextOrBefore.equals("before"))==false){
			throw new Exception("��������Ϊbefore��next");
		}
		if(box.containsKey("ALL_PERSON_IDS")==false){
			throw new Exception("�������ݲ����ڻ��ѱ����");
		}
		List<String> list=(List<String>)box.get("ALL_PERSON_IDS");
		
		int index = list.indexOf(personId);
		if(index<0){
			throw new Exception("�������ݲ����ڻ��ѱ����");
		}
		
		if(nextOrBefore.equals("next")&&index+1<list.size()){
			return list.get(index+1);
		}else if(nextOrBefore.equals("before")&&index>0){
			return list.get(index-1);
		}
		else 
			return personId;
	}
	
}
