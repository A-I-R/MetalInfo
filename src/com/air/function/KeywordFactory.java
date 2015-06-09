package com.air.function;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class KeywordFactory {

	Connection conn;
	Map<String, Integer> result;
	String[] spliters;
	
	public KeywordFactory(){
		conn=new DBConnection().getConnection();
		spliters=new String[]{" ", ";"};
		result=new HashMap<String, Integer>();
	}
	
	/**
	 * 获得关键词词频
	 * @param targetPath 关键词词频文件保存路径
	 */
	public void getKeywordFrequency(String targetPath){
		
		ResultSet rs=null;
		
		//读取数据库
		try {
			Statement stmt=conn.createStatement();
			rs=stmt.executeQuery("select keyword from chinese");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//取出数据并分析
		int progress=0;
		try {
			while(rs.next()){
				
				progress++;
				
				//分割词语
				String keyword=rs.getString("keyword");
				String[] keywordParts=null;
				for(String spliter: spliters){
					
					if(keyword.contains(spliter)){
						keywordParts=keyword.split(spliter);
						break;
					}
				}
				if(keywordParts==null){
					keywordParts=new String[]{keyword};
				}
				
				//储存结果
				for(String word: keywordParts){
					//判断有效字符，且字符长度要大于1
					if(word.equals("")||word.equals(",")||word.equals(".")||word.equals(":")||word.length()<=1)
					{
						continue;
					}
					
					if(result.containsKey(word)){
						result.put(word, result.get(word)+1);
					}
					else
					{
						result.put(word, 1);
					}
				}
				
				System.out.println("第"+progress+"条处理完成！");
				
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("统计完成！共"+progress+"条");
		System.out.println("开始写入！");
		
		
		//写入文件
		File target=new File(targetPath);
		if(!target.exists()){
			try {
				target.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try{
			FileOutputStream fos=new FileOutputStream(target, false);
			OutputStreamWriter osw=new OutputStreamWriter(fos, "utf-8");
			BufferedWriter bw=new BufferedWriter(osw);
			
			Set<Entry<String, Integer>> entries=result.entrySet();
			Iterator<Entry<String, Integer>> it=entries.iterator();
			while(it.hasNext()){
				Entry<String, Integer> entry=it.next();
				bw.write(entry.getKey()+","+entry.getValue()+"\r\n");
			}
			
			bw.flush();
			bw.close();
			osw.close();
			fos.close();
			
			System.out.println("写入完成！");
			
		}catch (IOException e){
			e.printStackTrace();
		}
		
	}
	
}
