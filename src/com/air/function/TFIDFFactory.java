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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TFIDFFactory {

	public TFIDFFactory(){
		
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
	
}
