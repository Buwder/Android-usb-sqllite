package com.example.hrv.smarthrv;


import android.content.Context;
import android.content.SharedPreferences;

public class ComMethod {
	COM_METHOD ComMethod ;
	
	
	 //����һ��SharedPreferences�����һ��Editor����
	 private SharedPreferences sharedPreferences;
	 private SharedPreferences.Editor editor;
     //�������ʹ���Զ���Application����湹�캯�� �ܷ���
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
	editor.commit();// �ύ�޸�  
	}  
	   
	// 
	@SuppressWarnings("unused")
	private void removeSharedPreference() {  
	
	editor.remove("ComType");  	
	editor.commit();// �ύ�޸�  
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
