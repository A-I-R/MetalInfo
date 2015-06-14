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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SimilarityFactory {
	
	public SimilarityFactory(){
		
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
	
}
