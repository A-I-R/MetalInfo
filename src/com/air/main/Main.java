package com.air.main;

import java.util.Scanner;

import com.air.function.AbstractFactory;
import com.air.function.KeywordFactory;
import com.air.function.TFIDFFactory;

public class Main {
	
	public static String root="F:\\个人文件\\作业作品\\项目相关\\冶金信息研究所\\数据\\大数据环境下的冶金文献信息分析与挖掘_信息所发_201505191137杨宏章\\";
	public static String target="F:\\个人文件\\作业作品\\项目相关\\冶金信息研究所\\实验相关\\";

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("欢迎！");
		System.out.println("请输入指令：‘kw’：关键词词频统计；‘ab’：摘要分词并统计；‘ti’：统计tf-idf");
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
			AbstractFactory af=new AbstractFactory();
			af.wordFrequency(target+"摘要词频.txt", target+"userDict.txt");
			break;
		}
		case "ti":
		{
			TFIDFFactory tf=new TFIDFFactory();
			tf.getTFIDF(target+"摘要词频.txt", target+"tf-idf.txt");
			break;
		}
		
		}
		
		scanner.close();
	}

}
