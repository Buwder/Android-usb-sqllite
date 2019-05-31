package com.example.hrv.smarthrv;


import android.content.Context;
import android.content.SharedPreferences;

public class ComMethod {
	COM_METHOD ComMethod ;
	
	
	 //声明一个SharedPreferences对象和一个Editor对象
	 private SharedPreferences sharedPreferences;
	 private SharedPreferences.Editor editor;
     //这里可以使用自定义Application类代替构造函数 很方便
     @SuppressWarnings("static-access")
	public ComMethod(Context context, String file) {
    	 sharedPreferences = context.getSharedPreferences(file, context.MODE_PRIVATE);
         editor = sharedPreferences.edit();
         ComMethod =  getSahrePreference();
     }
	public COM_METHOD GetComMethod(){
		return ComMethod;
	}
	public void SetComMethod(int method){
		ComMethod = COM_METHOD.values()[method];
		setSharedPreference(method);
	}
	
	
	// 
	private void setSharedPreference(int type) {  
	
	editor.putInt("ComType", type);  
	editor.commit();// 提交修改  
	}  
	   
	// 
	@SuppressWarnings("unused")
	private void removeSharedPreference() {  
	
	editor.remove("ComType");  	
	editor.commit();// 提交修改  
	}  
	   
	// 
	private COM_METHOD getSahrePreference() {  
		
		int type = sharedPreferences.getInt("ComType", 0); 
		return COM_METHOD.values()[type];   
	
	}  

	public enum COM_METHOD
	{
		None,USB,BLE
	}
}
