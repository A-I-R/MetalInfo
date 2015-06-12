package com.ICTCLAS;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface CLibrary extends Library {
	// 定义并初始化接口的静态变量
	CLibrary Instance = (CLibrary) Native.loadLibrary("F:\\个人文件\\作业作品\\项目相关\\冶金信息研究所\\workSpace\\MetalInfo\\lib\\NLPIR", CLibrary.class);
	
	//初始化
	public int NLPIR_Init(String sDataPath, int encoding, String sLicenceCode);
	
	//对字符串分词
	public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);

	//提取字符串关键词
	public String NLPIR_GetKeyWords(String sLine, int nMaxKeyLimit, boolean bWeightOut);
	
	//提取TXT文件关键词
	public String NLPIR_GetFileKeyWords(String sLine, int nMaxKeyLimit, boolean bWeightOut);
	
	//添加单条用户词典
	public int NLPIR_AddUserWord(String sWord);
	
	//删除单条用户词典
	public int NLPIR_DelUsrWord(String sWord);
	
	//导入用户词典
	public int NLPIR_ImportUserDict(String filename, boolean bOverwrite);
	
	//报错日志
	public String NLPIR_GetLastErrorMsg();
	
	//退出
	public void NLPIR_Exit();
}
