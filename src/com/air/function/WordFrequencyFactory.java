package com.air.function;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.ICTCLAS.CLibrary;

public class WordFrequencyFactory {
	
	Connection conn;
	
	public WordFrequencyFactory(){
		conn=new DBConnection().getConnection();
	}
	
	/**
	 * 分词后获取词频
	 * @param targetPath 目标路径
	 * @param userDictPath 用户词典路径
	 */
	public void wordFrequency(String targetPath, String userDictPath){
		
		int init_flag = CLibrary.Instance.NLPIR_Init("", 1, "0");
		if (0 == init_flag) {
			String error = CLibrary.Instance.NLPIR_GetLastErrorMsg();
			System.err.println("初始化失败！fail reason is "+error);
			return;
		}
		
		List<List<String>> userList=new ArrayList<List<String>>();
		
		//用户词典，直接导入无效，一条条来，同时添加到List中
		try{
			File userDict=new File(userDictPath);
			FileInputStream fisDict=new FileInputStream(userDict);
			InputStreamReader isrDict=new InputStreamReader(fisDict, "utf-8");
			BufferedReader brDict=new BufferedReader(isrDict);
			
			String line=brDict.readLine();
			
			while(line!=null){
				
				//拆分同义词并添加至词典，词典中的词语不允许出现空格
				List<String> similarWords=new ArrayList<String>();
				String[] words= line.split(",");
				for(String word: words){
					if(word.equals("")){
						continue;
					}
					CLibrary.Instance.NLPIR_AddUserWord(word);
					
					//添加至userList
					String[] wordParts=word.split(" ");
					similarWords.add(wordParts[0]);
				}
				userList.add(similarWords);
				
				line=brDict.readLine();
			}
			
			brDict.close();
			isrDict.close();
			fisDict.close();
			
			System.out.println("用户词典装载完成！");
			
		} catch(FileNotFoundException e){
			e.printStackTrace();
		} catch (UnsupportedEncodingException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		
		//分词并进行统计
		ResultSet rs=null;
		Map<Integer, String>result=new HashMap<Integer, String>();
		try {
			Statement stmt=conn.createStatement();
			rs=stmt.executeQuery("select id, abstract from chinese");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int progress=0;
		try {
			while(rs.next()){
				progress++;
				String content=rs.getString("abstract");
				String splitted=CLibrary.Instance.NLPIR_ParagraphProcess(content, 1);
				
				//统计词频
				double[] counts=new double[userList.size()];
				for(int i=0; i<counts.length; i++){
					counts[i]=0d;
				}
				
				String[] words=splitted.split(" ");
				int wordCount= words.length;
				for(String word: words){
					String[] temp=word.split("/");
					if(temp[0].equals("")){
						continue;
					}
					
					for(int i=0; i<userList.size(); i++){
						if(userList.get(i).contains(temp[0])){
							counts[i]=counts[i]+1;
						}
					}
				}
				
				String resultString="";
				for(double count: counts){
					resultString+=(count/wordCount)+",";
				}
				
				result.put(rs.getInt("id"), resultString);
				System.out.println("第"+progress+"行处理完成！");
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("分词统计完成！共"+progress+"条");
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
			
			//写入页头，也就是用户词典
			String header="id,";
			Iterator<List<String>> headerIt=userList.iterator();
			while(headerIt.hasNext()){
				header+=headerIt.next().get(0)+",";
			}
			bw.write(header+"\r\n");
			
			Set<Entry<Integer, String>> entries=result.entrySet();
			Iterator<Entry<Integer, String>> it=entries.iterator();
			while(it.hasNext()){
				Entry<Integer, String> entry=it.next();
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
