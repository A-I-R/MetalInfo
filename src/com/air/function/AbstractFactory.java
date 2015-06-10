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

public class AbstractFactory {
	
	Connection conn;
	
	public AbstractFactory(){
		conn=new DBConnection().getConnection();
	}
	
	/**
	 * �ִʺ��ȡ��Ƶ
	 * @param targetPath Ŀ��·��
	 * @param userDictPath �û��ʵ�·��
	 */
	public void wordFrequency(String targetPath, String userDictPath){
		
		int init_flag = CLibrary.Instance.NLPIR_Init("", 1, "0");
		if (0 == init_flag) {
			String error = CLibrary.Instance.NLPIR_GetLastErrorMsg();
			System.err.println("��ʼ��ʧ�ܣ�fail reason is "+error);
			return;
		}
		
		List<List<String>> userList=new ArrayList<List<String>>();
		
		//�û��ʵ䣬ֱ�ӵ�����Ч��һ��������ͬʱ��ӵ�List��呵呵呵
		try{
			File userDict=new File(userDictPath);
			FileInputStream fisDict=new FileInputStream(userDict);
			InputStreamReader isrDict=new InputStreamReader(fisDict, "utf-8");
			BufferedReader brDict=new BufferedReader(isrDict);
			
			String line=brDict.readLine();
			
			while(line!=null){
				
				//���ͬ��ʲ�������ʵ䣬�ʵ��еĴ��ﲻ������ֿո�
				List<String> similarWords=new ArrayList<String>();
				String[] words= line.split(",");
				for(String word: words){
					if(word.equals("")){
						continue;
					}
					CLibrary.Instance.NLPIR_AddUserWord(word);
					
					//�����userList
					String[] wordParts=word.split(" ");
					similarWords.add(wordParts[0]);
				}
				userList.add(similarWords);
				
				line=brDict.readLine();
			}
			
			brDict.close();
			isrDict.close();
			fisDict.close();
			
			System.out.println("�û��ʵ�װ����ɣ�");
			
		} catch(FileNotFoundException e){
			e.printStackTrace();
		} catch (UnsupportedEncodingException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		
		//�ִʲ�����ͳ��
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
				
				//ͳ�ƴ�Ƶ
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
				System.out.println("��"+progress+"�д�����ɣ�");
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("�ִ�ͳ����ɣ���"+progress+"��");
		System.out.println("��ʼд�룡");
		
		//д���ļ�
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
			
			//д��ҳͷ��Ҳ�����û��ʵ�
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
			
			System.out.println("д����ɣ�");
			
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
}
