package com.air.main;

import java.util.Scanner;

import com.air.function.AbstractFactory;
import com.air.function.KeywordFactory;
import com.air.function.AnalysisTools;

public class Main {
	
	public static String target="F:\\个人文件\\作业作品\\项目相关\\冶金信息研究所\\实验相关\\";

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("欢迎！");
		System.out.println("请输入指令：‘kw’：关键词词频统计；‘fk’：根据数据库筛选关键词；‘ab’：摘要分词并统计；‘ti’：统计tf-idf；‘sm’：计算词频余弦夹角相似度");
		Scanner scanner=new Scanner(System.in);
		switch(scanner.nextLine())
		{
		
		case "kw":
		{
			KeywordFactory kf=new KeywordFactory();
			kf.getKeywordFrequency(target+"关键词词频.txt");
			
			break;
		}
		case "fk":
		{
			AnalysisTools tool=new AnalysisTools();
			tool.filterKeywords(target+"userDict.txt", target+"userDictModfied.txt", "chinese_words");
			
			break;
		}
		case "ab":
		{
			AbstractFactory wff=new AbstractFactory();
			wff.wordFrequency(target+"摘要词频.txt", target+"userDict.txt");
			break;
		}
		case "ti":
		{
			AnalysisTools tool=new AnalysisTools();
			tool.getTFIDF(target+"摘要词频.txt", target+"tf-idf.txt");
			break;
		}
		case "sm":
		{
			AnalysisTools tool=new AnalysisTools();
			tool.getAngleSimilarity(target+"摘要词频.txt", target+"similarity.txt");
			break;
		}
		}
		
		scanner.close();
		
	}

}
