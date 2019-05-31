package com.example.hrv.Algorithm;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ResultOfHRV implements Serializable{
	public int    caculateFlag;//计算结果
	public int    heartRate;
	public double BRPA;//身体抗压能力
	public double BFS;//身体疲劳状态
	public double BPS;//精神压力状态
	public double EMS; //  精神情绪状态
	public double Attention;//注意力
	public double RRMean;//RR间期均值
	public double RMSSD;//相邻RR间期的差的均方根
	public double CV;//变异系数
	public double SDSD; //相邻RR间期差值的标准差
	public double SDNN;//RR间期标准差
	public  double NN50;//相邻RR间期的差值超过50ms的心搏数
	public double PNN50;//相邻RR间期的差值超过50ms的心搏数占RR间期总搏数的百分比
	public double DRR;//极差
	    //频域
	public double TP;//总功率
	public double VLF;//极低频功率
	public double LF;//低频功率
	public double HF;//高频功率
	public double LFHF;//低高频比
	public double LFnorm;//归一化的低频功率
	public double HFnorm;//归一化的高频功率
    public ResultOfHRV(){
    	caculateFlag = 0;
    	BRPA = 0.0;
    	BFS = 0.0;
    	BPS = 0.0;
    	EMS = 0.0;
    	Attention = 0.0;
    	
    	RRMean = 0.0;
        RMSSD = 0.0;
        CV = 0.0;
        SDSD = 0.0;
        SDNN = 0.0;
        NN50 = 0.0;
        PNN50 = 0.0;
        DRR = 0.0;
      //频域
        TP= 0.0;
        VLF= 0.0;
        LF= 0.0;
        HF= 0.0;
        LFHF= 0.0;
        LFnorm= 0.0;
        HFnorm= 0.0;
    }
	@Override
	public String toString() {
		return "ResultOfHRV [caculateFlag=" + caculateFlag + ", heartRate="
				+ heartRate + ", BRPA=" + BRPA + ", BFS=" + BFS + ", BPS="
				+ BPS + ", EMS=" + EMS + ", Attention=" + Attention
				+ ", RRMean=" + RRMean + ", RMSSD=" + RMSSD + ", CV=" + CV
				+ ", SDSD=" + SDSD + ", SDNN=" + SDNN + ", NN50=" + NN50
				+ ", PNN50=" + PNN50 + ", DRR=" + DRR + ", TP=" + TP + ", VLF="
				+ VLF + ", LF=" + LF + ", HF=" + HF + ", LFHF=" + LFHF
				+ ", LFnorm=" + LFnorm + ", HFnorm=" + HFnorm + "]";
	}
	public int getCaculateFlag() {
		return caculateFlag;
	}
	public void setCaculateFlag(int caculateFlag) {
		this.caculateFlag = caculateFlag;
	}
	public int getHeartRate() {
		return heartRate;
	}
	public void setHeartRate(int heartRate) {
		this.heartRate = heartRate;
	}
	public double getBRPA() {
		return BRPA;
	}
	public void setBRPA(double bRPA) {
		BRPA = bRPA;
	}
	public double getBFS() {
		return BFS;
	}
	public void setBFS(double bFS) {
		BFS = bFS;
	}
	public double getBPS() {
		return BPS;
	}
	public void setBPS(double bPS) {
		BPS = bPS;
	}
	public double getEMS() {
		return EMS;
	}
	public void setEMS(double eMS) {
		EMS = eMS;
	}
	public double getAttention() {
		return Attention;
	}
	public void setAttention(double attention) {
		Attention = attention;
	}
	public double getRRMean() {
		return RRMean;
	}
	public void setRRMean(double rRMean) {
		RRMean = rRMean;
	}
	public double getRMSSD() {
		return RMSSD;
	}
	public void setRMSSD(double rMSSD) {
		RMSSD = rMSSD;
	}
	public double getCV() {
		return CV;
	}
	public void setCV(double cV) {
		CV = cV;
	}
	public double getSDSD() {
		return SDSD;
	}
	public void setSDSD(double sDSD) {
		SDSD = sDSD;
	}
	public double getSDNN() {
		return SDNN;
	}
	public void setSDNN(double sDNN) {
		SDNN = sDNN;
	}
	public double getNN50() {
		return NN50;
	}
	public void setNN50(double nN50) {
		NN50 = nN50;
	}
	public double getPNN50() {
		return PNN50;
	}
	public void setPNN50(double pNN50) {
		PNN50 = pNN50;
	}
	public double getDRR() {
		return DRR;
	}
	public void setDRR(double dRR) {
		DRR = dRR;
	}
	public double getTP() {
		return TP;
	}
	public void setTP(double tP) {
		TP = tP;
	}
	public double getVLF() {
		return VLF;
	}
	public void setVLF(double vLF) {
		VLF = vLF;
	}
	public double getLF() {
		return LF;
	}
	public void setLF(double lF) {
		LF = lF;
	}
	public double getHF() {
		return HF;
	}
	public void setHF(double hF) {
		HF = hF;
	}
	public double getLFHF() {
		return LFHF;
	}
	public void setLFHF(double lFHF) {
		LFHF = lFHF;
	}
	public double getLFnorm() {
		return LFnorm;
	}
	public void setLFnorm(double lFnorm) {
		LFnorm = lFnorm;
	}
	public double getHFnorm() {
		return HFnorm;
	}
	public void setHFnorm(double hFnorm) {
		HFnorm = hFnorm;
	}
	
	
}
