package com.example.hrv.Algorithm;


public class EcgJinClient {
	
	
	 public static final int  kLoPass     = 10000; //��ͨ
	 public static final int  kHiPass     = 10001; //��ͨ
	 public static final int  kBandPass   = 10002; //��ͨ
	 public static final int  kBandStop   = 10003; //����
	 public static final int  kLoShelf    = 10004;
     public static final int  kHiShelf    = 10005; // high order EQ
     public static final int  kParametric = 10006;  // high order EQ
    
	
	/**
	 * @param sampleData ��������
	 * @param sampleLen  �������ݳ���
	 * @param sampleRate ������
	 * @return ���� ����õ�������ֵ
	 */
	static public native int CalculateHeartRate(long[] sampleData,int sampleLen,int sampleRate);
	
	
	/**
	 * @param sampleData ��������
	 * @param sampleLen  �������ݳ���
	 * @param sampleRate ������
	 * @param sensorType ǰ���豸����  �ο� SensorType
	 * @param hrvResult  HRV�������
	 * @return  1 ����ok 0 ʧ��
	 */
	static public native ResultOfHRV CalculateHRVResult(long[] sampleData,int sampleLen,int sampleRate,ResultOfHRV hrvResult);
	
	/**
	 * 
	 * @param sampleData
	 * @param sampleLen
	 * @return
	 */
	static public native int FilterData(float[] sampleData,int sampleLen);
	/**
	 * 
	 * @param fs ������
	 * @param type �˲����� 
	         ��ͨ     = 10000,
                       ��ͨ     = 10001,
                       ��ͨ   = 10002,
                       ����   = 10003,
	 * @param filerorder �˲�����
	 * @param low_cutoff ��ͨƵ��
	 * @param high_cutoff ��ͨƵ��
	 * @return
	 */
	static public native int InitButtFiler(int fs,int type,int filerorder,float low_cutoff, float high_cutoff);
	
}
