package com.ICTCLAS;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface CLibrary extends Library {
	// ���岢��ʼ���ӿڵľ�̬����
	CLibrary Instance = (CLibrary) Native.loadLibrary("F:\\�����ļ�\\��ҵ��Ʒ\\��������Ϣ����\\���ݷ���\\����\\workSpace\\PudongTextMining\\lib\\NLPIR", CLibrary.class);
	
	//��ʼ��
	public int NLPIR_Init(String sDataPath, int encoding, String sLicenceCode);
	
	//���ַ����ִ�
	public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);

	//��ȡ�ַ����ؼ���
	public String NLPIR_GetKeyWords(String sLine, int nMaxKeyLimit, boolean bWeightOut);
	
	//��ȡTXT�ļ��ؼ���
	public String NLPIR_GetFileKeyWords(String sLine, int nMaxKeyLimit, boolean bWeightOut);
	
	//��ӵ����û��ʵ�
	public int NLPIR_AddUserWord(String sWord);
	
	//ɾ�������û��ʵ�
	public int NLPIR_DelUsrWord(String sWord);
	
	//�����û��ʵ�
	public int NLPIR_ImportUserDict(String filename, boolean bOverwrite);
	
	//������־
	public String NLPIR_GetLastErrorMsg();
	
	//�˳�
	public void NLPIR_Exit();
}
