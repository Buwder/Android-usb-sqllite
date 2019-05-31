package com.example.hrv.smarthrv;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DataBaseHelper extends SQLiteOpenHelper {

	private static final String name = "HrvTestDb"; //���ݿ�����  
	  
    private static final int version = 2; //���ݿ�汾  
    
	public DataBaseHelper(Context context) {
		super(context, name, null, version);
		// TODO Auto-generated constructor stub
	}
	public boolean deleteDatabase(Context context) {
		return context.deleteDatabase(name);
		}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//HRV ��������Ϣ		
		db.execSQL("CREATE TABLE IF NOT EXISTS HrvTester (id integer primary key autoincrement, name varchar(20),gender varchar(20),birthday varchar(20))"); 
		//���Խ��		
		db.execSQL("CREATE TABLE IF NOT EXISTS HrvResult (id integer primary key autoincrement, testerid integer,testdate Date,HR integer,RRMean integer,"
				+ "RMSSD integer,CV integer,SDSD integer,SDNN integer,NN50 integer,PNN50 integer, DRR integer,TP integer,VLF integer,LF integer,HF integer,LFHF integer,LFnorm integer,HFnorm integer,BRPA integer,"
				+ "BFS integer,BPS double,EMS integer)");     
		//�γ���Ϣ
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if(oldVersion != newVersion && newVersion == 2 ){
		
		
		}

	}


}
