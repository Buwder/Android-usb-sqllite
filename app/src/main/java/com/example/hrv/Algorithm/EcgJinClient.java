package com.example.hrv.Algorithm;


public class EcgJinClient {
	
	
	 public static final int  kLoPass     = 10000; //低通
	 public static final int  kHiPass     = 10001; //高通
	 public static final int  kBandPass   = 10002; //带通
	 public static final int  kBandStop   = 10003; //带阻
	 public static final int  kLoShelf    = 10004;
     public static final int  kHiShelf    = 10005; // high order EQ
     public static final int  kParametric = 10006;  // high order EQ
    
	
	/**
	 * @param sampleData 采样数据
	 * @param sampleLen  采样数据长度
	 * @param sampleRate 采用率
	 * @return 返回 计算得到的心率值
	 */
	static public native int CalculateHeartRate(long[] sampleData,int sampleLen,int sampleRate);
	
	
	/**
	 * @param sampleData 采样数据
	 * @param sampleLen  采样数据长度
	 * @param sampleRate 采用率
	 * @param sensorType 前端设备类型  参考 SensorType
	 * @param hrvResult  HRV分析结果
	 * @return  1 计算ok 0 失败
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
	 * @param fs 采样率
	 * @param type 滤波类型 
	         低通     = 10000,
                       高通     = 10001,
                       带通   = 10002,
                       带阻   = 10003,
	 * @param filerorder 滤波接数
	 * @param low_cutoff 低通频率
	 * @param high_cutoff 高通频率
	 * @return
	 */
	static public native int InitButtFiler(int fs,int type,int filerorder,float low_cutoff, float high_cutoff);
	
}
