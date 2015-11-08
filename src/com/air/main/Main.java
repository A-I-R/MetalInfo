package com.air.main;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import com.air.function.SimilarityFactory;
import com.air.function.WordFrequencyFactory;
import com.air.function.KeywordFactory;
import com.air.function.TFIDFFactory;

public class Main {
	
	public static String target="F:\\个人文件\\作业作品\\项目相关\\冶金信息研究所\\实验相关\\";

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("欢迎！");
		System.out.println("请输入指令：‘kw’：关键词词频统计；‘ab’：摘要分词并统计；‘ti’：统计tf-idf；‘sm’：计算词频余弦夹角相似度");
		Scanner scanner=new Scanner(System.in);
		switch(scanner.nextLine())
		{
		
		case "kw":
		{
			KeywordFactory kf=new KeywordFactory();
			kf.getKeywordFrequency(target+"关键词词频.txt");
			
			break;
		}
		case "ab":
		{
			WordFrequencyFactory wff=new WordFrequencyFactory();
			wff.wordFrequency(target+"摘要词频.txt", target+"userDict.txt");
			break;
		}
		case "ti":
		{
			TFIDFFactory tf=new TFIDFFactory();
			tf.getTFIDF(target+"摘要词频.txt", target+"tf-idf.txt");
			break;
		}
		case "sm":
		{
			SimilarityFactory sf=new SimilarityFactory();
			sf.getAngleSimilarity(target+"摘要词频.txt", target+"similarity.txt");
			break;
		}
		}
		
		scanner.close();
		/*
		try {
			FileOutputStream fos=new FileOutputStream(target+"xxx.txt");
			OutputStreamWriter osw=new OutputStreamWriter(fos, "utf-8");
			BufferedWriter bw=new BufferedWriter(osw);
			
			for(int i=0; i<10; i++){
				bw.write("PT\tJ");
			}
			bw.flush();
			bw.close();
			osw.close();
			fos.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}

}
