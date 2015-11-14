package com.air.function;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class AnalysisTools {

	public AnalysisTools(){
		
	}
	
	/**
	 * 计算TF-IDF权值
	 * @param sourcePath 词频统计文件路径
	 * @param targetPath 目标路径
	 */
	public void getTFIDF(String sourcePath, String targetPath){
		
		//变量
		List<String> headerList=new ArrayList<String>();
		List<List<Double>> wf=new ArrayList<List<Double>>();
		List<String> idList=new ArrayList<String>();
		
		//读取文件
		try {
			FileInputStream fis=new FileInputStream(sourcePath);
			InputStreamReader isr=new InputStreamReader(fis, "utf-8");
			BufferedReader br=new BufferedReader(isr);
			
			System.out.println("开始读取！");
			
			//读取表头
			String header=br.readLine();
			String[] headerParts=header.split(",");
			for(String part: headerParts){
				if(!part.equals("")&&!part.equals("id")){
					headerList.add(part);
				}
			}
			System.out.println("表头读取完成！");
			
			//读取表体
			int count=0;
			String line=br.readLine();
			while(line!=null){
				if(!line.equals("")){
					String[] parts=line.split(",");
					List<Double> lineList=new ArrayList<Double>();
					//分解每一行
					for(int i=0; i<parts.length; i++){
						String part=parts[i];
						if(i==0){
							//id添加到idList中
							idList.add(part);
						}
						else if(!part.equals("")){
							//添加到每一行的列表中
							lineList.add(Double.parseDouble(part));
						}
					}
					//添加至数据集中
					wf.add(lineList);
				}
				
				line=br.readLine();
				count++;
				System.out.println("第"+count+"行读取完成！");
				
			}
			
			br.close();
			isr.close();
			fis.close();
							
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("FileNotFound!");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("UnsupportedEncodingException!");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IOException");
		}
		
		//确定行列数量
		int colNum=headerList.size();//不包括id那列
		int rowNum=wf.size();//不包括表头
		
		//计算逆文档频率，行优先遍历
		System.out.println("计算逆文档频率");
		List<Double> idf=new ArrayList<Double>();
		//倒序开始，到0结束。可以直接删掉出现次数为0的字段
		for(int i=colNum-1; i>=0; i--){
			int appearedNum=0;
			for(int j=0; j<rowNum; j++){
				if(wf.get(j).get(i)>0){
					appearedNum++;
				}
			}
			
			if(appearedNum==0)//说明没有文章出现该关键词，直接删掉
			{
				headerList.remove(i);
				for(int j=0; j<rowNum; j++){
					List<Double> tmpList=wf.get(j);
					tmpList.remove(i);
					wf.set(j, tmpList);
				}
			}
			else{
				double idfValue=0;
				idfValue=Math.log10((double)rowNum/(double)appearedNum);
				idf.add(idfValue);
				System.out.println("idf:"+idfValue);
			}
			
		}
		//将倒序的idf变成正序
		Collections.reverse(idf);
		
		System.out.println("逆文档频率计算完成，共有"+(idf.size())+"个关键词");
		
		
		//计算tf-idf，列优先遍历
		System.out.println("计算tf-idf");
		for(int i=0; i<rowNum; i++){
			List<Double> lineList=wf.get(i);
			for(int j=0; j<idf.size(); j++){
				lineList.set(j, lineList.get(j)*idf.get(j));
			}
			wf.set(i, lineList);
			System.out.println("第"+(i+1)+"行计算完成");
		}
		System.out.println("tf-idf计算完成");
		
		//写入文件
		System.out.println("开始写入");
		try {
			FileOutputStream fos=new FileOutputStream(targetPath, false);
			OutputStreamWriter osw=new OutputStreamWriter(fos, "utf-8");
			BufferedWriter bw=new BufferedWriter(osw);
			//输出表头
			String headerString="id,";
			for(String string: headerList){
				headerString+=string+",";
			}
			bw.write(headerString+"\r\n");
			
			//输出表体
			for(int i=0; i<rowNum; i++){
				String line=idList.get(i)+",";
				for(int j=0; j<idf.size(); j++){
					line+=wf.get(i).get(j)+",";
				}
				
				bw.write(line+"\r\n");
			}
			
			bw.flush();
			bw.close();
			osw.close();
			fos.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("FileNotFound!");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("UnsupportedEncodingException!");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IOException");
		}
		
		System.out.println("写入完成！");
		
	}
	
	
	/**
	 * 计算夹角余弦相似度
	 * @param resoucePath 词频统计文件
	 * @param targetPath 输出结果文件的路径
	 */
	public void getAngleSimilarity(String resourcePath, String targetPath){
		
		//读入词频文件
		Map<String, List<Double>> wordFrequency=new HashMap<String, List<Double>>();
		System.out.println("读取词频");
		try {
			FileInputStream fis=new FileInputStream(resourcePath);
			InputStreamReader isr=new InputStreamReader(fis, "utf-8");
			BufferedReader br=new BufferedReader(isr);
			
			//抛去第一行的标题
			br.readLine();
			
			//逐行读取
			String line= br.readLine();
			int progress=0;
			while(line!=null){
				String[] lineParts=line.split(",");
				//定义行存储列表
				List<Double> lineList= new ArrayList<Double>();			
				for(int i=0; i<lineParts.length; i++){
					String part=lineParts[i];
					if(i!=0&&!part.equals("")){
						lineList.add(Double.parseDouble(part));
					}
				}
				wordFrequency.put(lineParts[0], lineList);
				
				progress++;
				System.out.println("第"+progress+"行读取完成！");
				
				line=br.readLine();
			}
			
			br.close();
			isr.close();
			fis.close();
			System.out.println("词频文件读取完成！");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		
		//计算向量长度
		System.out.println("计算向量长度");
		List<Entry<String, List<Double>>> wordEntry=new ArrayList<Entry<String, List<Double>>>(wordFrequency.entrySet());
		Map<String, Double> lengthList=new HashMap<String, Double>();
		
		for(int i=wordEntry.size()-1; i>=0; i--){
			Entry<String, List<Double>> lineEntry=wordEntry.get(i);
			double sum=0.0d;
			for(Double tmp: lineEntry.getValue()){
				sum+=Math.pow(tmp, 2);//平方和
			}
			sum=Math.sqrt(sum);//开方
			if(sum==0){//删除长度为0的向量
				wordEntry.remove(i);
			}
			else
			{
				lengthList.put(lineEntry.getKey(), sum);
			}
		}
		System.out.println("向量长度计算完成！");
		
		
		//计算夹角余弦并写入文件
		System.out.println("计算夹角余弦并写入文件");
		try{
			FileOutputStream fos=new FileOutputStream(targetPath, false);
			OutputStreamWriter osw=new OutputStreamWriter(fos, "utf-8");
			BufferedWriter bw=new BufferedWriter(osw);
			//输出标题行
			String header="id,";
			for(Entry<String, List<Double>> tmpEntry: wordEntry){
				header+=tmpEntry.getKey()+",";
			}
			bw.write(header+"\r\n");
			
			for(int i=0; i<wordEntry.size(); i++){
				Entry<String, List<Double>> vector1=wordEntry.get(i);
				String line=vector1.getKey()+",";
				for(int j=0; j<wordEntry.size(); j++){
					double cosine=0.0d;
					if(i!=j){
						//计算夹角余弦（我也知道是对称的，但是写出来也没法运行：太吃内存了）
						Entry<String, List<Double>> vector2=wordEntry.get(j);
						//计算向量内积
						double product=0.0d;
						for(int p=0; p<vector1.getValue().size(); p++){
							product+=vector1.getValue().get(p)*vector2.getValue().get(p);
						}
						
						cosine=product/(lengthList.get(vector1.getKey())*lengthList.get(vector2.getKey()));
					}
					else{//主对角线
						cosine=1.0d;
					}
					
					line+=cosine+",";
				}
				
				bw.write(line+"\r\n");
				line=null;
				System.out.println("第"+(i+1)+"行计算完成！");
			}
			
			bw.flush();
			bw.close();
			osw.close();
			fos.close();
			System.out.println("写入完成！");
			
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 将词表与数据库中的词表进行比对，提取在两者中同时存在的词，并输出成新的文件
	 * @param sourcePath 源词表地址 
	 * @param targetPath 输出新词表的地址
	 * @param tableName 数据库中储存词表的表名
	 */
	public void filterKeywords(String sourcePath, String targetPath, String tableName){
		
		Connection conn=new DBConnection().getConnection();
		
		try {
			FileInputStream fis=new FileInputStream(sourcePath);
			InputStreamReader isr=new InputStreamReader(fis, "utf-8");
			BufferedReader br=new BufferedReader(isr);
			
			String line=br.readLine();
			Statement stmt=conn.createStatement();
			
			FileOutputStream fos=new FileOutputStream(targetPath, false);
			OutputStreamWriter osw=new OutputStreamWriter(fos, "utf-8");
			BufferedWriter bw=new BufferedWriter(osw);
			
			//设置步长
			int step=20;
			//每步长个词一起查询和写入
			List<String> tmpList=new ArrayList<String>();//缓存列表
			
			while(true){
				if(line!=null){
					String[] word=line.split(" ");
					if(word[0]!="")
						tmpList.add(word[0]);
				}
				
				if(tmpList.size()==step||(line==null&&tmpList.size()>0)){//如果满足步长或者已经达到词表末尾且缓存中有数据，就查询数据库
					String query="select distinct word from "+tableName+" where word in ('"+tmpList.get(0)+"'";
					for(int i=1; i<tmpList.size(); i++){
						query+=", '"+tmpList.get(i)+"'";
					}
					query+=")";
					System.out.println(query);
					ResultSet results=stmt.executeQuery(query);
					while(results.next()){//查询到的数据就是共有的数据，将它们写入文件中
						bw.write(results.getString("word")+"\r\n");
					}
					tmpList.clear();//清空列表
				}
				//结束循环条件
				if(line==null)
					break;
				else
					line=br.readLine();
				
			}
			bw.flush();
			
			br.close();
			isr.close();
			fis.close();
			bw.close();
			osw.close();
			fos.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("未找到文件！");
			return;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
