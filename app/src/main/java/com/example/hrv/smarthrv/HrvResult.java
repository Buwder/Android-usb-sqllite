package com.example.hrv.smarthrv;

public class HrvResult {
	public int   id;
	public int   testerid;
	public String testdate;
	public int    HR;
	
	// 8
	public int RRMean;//RR���ھ�ֵ
	public int RMSSD;//����RR���ڵĲ�ľ�����
	public int CV;//����ϵ��
	public int SDSD; //����RR���ڲ�ֵ�ı�׼��
	public int SDNN;//RR���ڱ�׼��
	public int NN50;//����RR���ڵĲ�ֵ����50ms���Ĳ���
	public int PNN50;//����RR���ڵĲ�ֵ����50ms���Ĳ���ռRR�����ܲ����İٷֱ�
	public int DRR;//����
	
	//Ƶ�� 7
	public int TP;//�ܹ���
	public int VLF;//����Ƶ����
	public int LF;//��Ƶ����
	public int HF;//��Ƶ����
	public int LFHF;//�͸�Ƶ��
	public int LFnorm;//��һ���ĵ�Ƶ����
	public int HFnorm;//��һ���ĸ�Ƶ����
		
	// 4
	public int BRPA;//���忹ѹ����
	public int BFS;//����ƣ��״̬
	public int BPS;//����ѹ��״̬
	public int EMS; //  ��������״̬
	
	 
	  
	
	
}
