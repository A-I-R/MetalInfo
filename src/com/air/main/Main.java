package com.air.main;

import java.util.Scanner;

import com.air.function.AbstractFactory;
import com.air.function.KeywordFactory;
import com.air.function.TFIDFFactory;

public class Main {
	
	public static String root="F:\\�����ļ�\\��ҵ��Ʒ\\��Ŀ���\\ұ����Ϣ�о���\\����\\�����ݻ����µ�ұ��������Ϣ�������ھ�_��Ϣ����_201505191137�����\\";
	public static String target="F:\\�����ļ�\\��ҵ��Ʒ\\��Ŀ���\\ұ����Ϣ�о���\\ʵ�����\\";

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("��ӭ��");
		System.out.println("������ָ���kw�����ؼ��ʴ�Ƶͳ�ƣ���ab����ժҪ�ִʲ�ͳ�ƣ���ti����ͳ��tf-idf");
		Scanner scanner=new Scanner(System.in);
		switch(scanner.nextLine())
		{
		
		case "kw":
		{
			KeywordFactory kf=new KeywordFactory();
			kf.getKeywordFrequency(target+"�ؼ��ʴ�Ƶ.txt");
			
			break;
		}
		case "ab":
		{
			AbstractFactory af=new AbstractFactory();
			af.wordFrequency(target+"ժҪ��Ƶ.txt", target+"userDict.txt");
			break;
		}
		case "ti":
		{
			TFIDFFactory tf=new TFIDFFactory();
			tf.getTFIDF(target+"ժҪ��Ƶ.txt", target+"tf-idf.txt");
			break;
		}
		
		}
		
		scanner.close();
	}

}
