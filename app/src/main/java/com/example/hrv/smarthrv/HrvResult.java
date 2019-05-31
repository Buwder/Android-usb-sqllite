package com.example.hrv.smarthrv;

public class HrvResult {
	public int   id;
	public int   testerid;
	public String testdate;
	public int    HR;
	
	// 8
	public int RRMean;//RR间期均值
	public int RMSSD;//相邻RR间期的差的均方根
	public int CV;//变异系数
	public int SDSD; //相邻RR间期差值的标准差
	public int SDNN;//RR间期标准差
	public int NN50;//相邻RR间期的差值超过50ms的心搏数
	public int PNN50;//相邻RR间期的差值超过50ms的心搏数占RR间期总搏数的百分比
	public int DRR;//极差
	
	//频域 7
	public int TP;//总功率
	public int VLF;//极低频功率
	public int LF;//低频功率
	public int HF;//高频功率
	public int LFHF;//低高频比
	public int LFnorm;//归一化的低频功率
	public int HFnorm;//归一化的高频功率
		
	// 4
	public int BRPA;//身体抗压能力
	public int BFS;//身体疲劳状态
	public int BPS;//精神压力状态
	public int EMS; //  精神情绪状态
	
	 
	  
	
	
}
