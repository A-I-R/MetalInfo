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
	 * ����TF-IDFȨֵ
	 * @param sourcePath ��Ƶͳ���ļ�·��
	 * @param targetPath Ŀ��·��
	 */
	public void getTFIDF(String sourcePath, String targetPath){
		
		//����
		List<String> headerList=new ArrayList<String>();
		List<List<Double>> wf=new ArrayList<List<Double>>();
		List<String> idList=new ArrayList<String>();
		
		//��ȡ�ļ�
		try {
			FileInputStream fis=new FileInputStream(sourcePath);
			InputStreamReader isr=new InputStreamReader(fis, "utf-8");
			BufferedReader br=new BufferedReader(isr);
			
			System.out.println("��ʼ��ȡ��");
			
			//��ȡ��ͷ
			String header=br.readLine();
			String[] headerParts=header.split(",");
			for(String part: headerParts){
				if(!part.equals("")&&!part.equals("id")){
					headerList.add(part);
				}
			}
			System.out.println("��ͷ��ȡ��ɣ�");
			
			//��ȡ����
			int count=0;
			String line=br.readLine();
			while(line!=null){
				if(!line.equals("")){
					String[] parts=line.split(",");
					List<Double> lineList=new ArrayList<Double>();
					//�ֽ�ÿһ��
					for(int i=0; i<parts.length; i++){
						String part=parts[i];
						if(i==0){
							//id��ӵ�idList��
							idList.add(part);
						}
						else if(!part.equals("")){
							//��ӵ�ÿһ�е��б���
							lineList.add(Double.parseDouble(part));
						}
					}
					//��������ݼ���
					wf.add(lineList);
				}
				
				line=br.readLine();
				count++;
				System.out.println("��"+count+"�ж�ȡ��ɣ�");
				
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
		
		//ȷ����������
		int colNum=headerList.size();//������id����
		int rowNum=wf.size();//��������ͷ
		
		//�������ĵ�Ƶ�ʣ������ȱ���
		System.out.println("�������ĵ�Ƶ��");
		List<Double> idf=new ArrayList<Double>();
		//����ʼ����0����������ֱ��ɾ�����ִ���Ϊ0���ֶ�
		for(int i=colNum-1; i>=0; i--){
			int appearedNum=0;
			for(int j=0; j<rowNum; j++){
				if(wf.get(j).get(i)>0){
					appearedNum++;
				}
			}
			
			if(appearedNum==0)//˵��û�����³��ָùؼ��ʣ�ֱ��ɾ��
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
		//�������idf�������
		Collections.reverse(idf);
		
		System.out.println("���ĵ�Ƶ�ʼ�����ɣ�����"+(idf.size())+"���ؼ���");
		
		
		//����tf-idf�������ȱ���
		System.out.println("����tf-idf");
		for(int i=0; i<rowNum; i++){
			List<Double> lineList=wf.get(i);
			for(int j=0; j<idf.size(); j++){
				lineList.set(j, lineList.get(j)*idf.get(j));
			}
			wf.set(i, lineList);
			System.out.println("��"+(i+1)+"�м������");
		}
		System.out.println("tf-idf�������");
		
		//д���ļ�
		System.out.println("��ʼд��");
		try {
			FileOutputStream fos=new FileOutputStream(targetPath, false);
			OutputStreamWriter osw=new OutputStreamWriter(fos, "utf-8");
			BufferedWriter bw=new BufferedWriter(osw);
			//�����ͷ
			String headerString="id,";
			for(String string: headerList){
				headerString+=string+",";
			}
			bw.write(headerString+"\r\n");
			
			//�������
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
		
		System.out.println("д����ɣ�");
		
	}
	
}
