package com.example.hrv.smarthrv;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;


public class DBManager {
	private DataBaseHelper helper;  
    private SQLiteDatabase db; 
    @SuppressWarnings("unused")
	private boolean DbRwFlag = false;
    private Context context;
    public DBManager(Context context) { 
    	this.context = context;
        helper = new DataBaseHelper(context);  
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);  
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里  
        try
        {
        	db = helper.getWritableDatabase(); 
        	DbRwFlag = true;
        }catch(SQLiteException e){
        
        	db = helper.getReadableDatabase(); 
        	DbRwFlag = false;
        }
       
    }
    public int newHrvTester(HrvTester tester) {
    	int strid = 0;     
        db.beginTransaction();  //开始事务  
        try {  
        	db.execSQL("INSERT INTO HrvTester VALUES(null, ?, ?, ?)", new Object[]{tester.getName(), tester.getGender(), tester.getBirthday()});  
        	Cursor cursor = db.rawQuery("select last_insert_rowid() from HrvTester",null);            
        	 
        	if(cursor.moveToFirst())    
              strid = cursor.getInt(0);    
            db.setTransactionSuccessful();  //设置事务成功完成  
        } finally {  
            db.endTransaction();    //结束事务  
        }
        return strid;
    }
    public int addTestResult(HrvResult result){
    	int strid = 0;     
        db.beginTransaction();  //开始事务  
        try {  
        	db.execSQL("INSERT INTO HrvResult VALUES(null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)", new Object[]{result.testerid, 
        			result.testdate, result.HR,result.RRMean,result.RMSSD,result.CV,result.SDSD,result.SDNN,result.NN50,result.PNN50,result.DRR,result.TP,result.VLF,result.LF,result.HF,result.LFHF,result.LFnorm,result.HFnorm,result.BRPA,result.BFS,result.BPS,result.EMS  });  
        	Cursor cursor = db.rawQuery("select last_insert_rowid() from HrvResult",null);            
        	 
        	if(cursor.moveToFirst())    
              strid = cursor.getInt(0);    
            db.setTransactionSuccessful();  //设置事务成功完成  
        } finally {  
            db.endTransaction();    //结束事务  
        }
        return strid;
    }
    public List<HrvResult> getHrvResultList(String testerid){
    	ArrayList<HrvResult> hrvResultLists = new ArrayList<HrvResult>();  
        Cursor c = queryTheHrvResultCursor(testerid);  
        while (c.moveToNext()) {  
        	HrvResult result = new HrvResult();
        	result.id = c.getInt(c.getColumnIndex("id"));
        	result.testerid = c.getInt(c.getColumnIndex("testerid"));
        	result.testdate = c.getString(c.getColumnIndex("testdate"));
        	result.HR = c.getInt(c.getColumnIndex("HR"));
        	result.RRMean = c.getInt(c.getColumnIndex("RRMean"));
        	result.RMSSD = c.getInt(c.getColumnIndex("RMSSD"));
        	result.CV = c.getInt(c.getColumnIndex("CV"));
        	result.SDSD = c.getInt(c.getColumnIndex("SDSD"));
        	result.SDNN = c.getInt(c.getColumnIndex("SDNN"));
        	result.NN50 = c.getInt(c.getColumnIndex("NN50"));
        	result.PNN50 = c.getInt(c.getColumnIndex("PNN50"));
        	result.DRR = c.getInt(c.getColumnIndex("DRR"));
        	result.TP = c.getInt(c.getColumnIndex("TP"));
        	result.VLF = c.getInt(c.getColumnIndex("VLF"));
        	result.LF = c.getInt(c.getColumnIndex("LF"));
        	result.HF = c.getInt(c.getColumnIndex("HF"));
        	result.LFHF = c.getInt(c.getColumnIndex("LFHF"));
        	result.LFnorm = c.getInt(c.getColumnIndex("LFnorm"));
        	result.HFnorm = c.getInt(c.getColumnIndex("HFnorm"));
        	result.BRPA = c.getInt(c.getColumnIndex("BRPA"));
        	result.BFS = c.getInt(c.getColumnIndex("BFS"));
        	result.BPS = c.getInt(c.getColumnIndex("BPS"));
        	result.EMS = c.getInt(c.getColumnIndex("EMS"));
            
        	hrvResultLists.add(result);  
        }  
        c.close();  
        return hrvResultLists;  
    }
    
    public List<HrvTester> getHrvTesterList(){
    	ArrayList<HrvTester> hrvTesterLists = new ArrayList<HrvTester>();  
        Cursor c = queryTheHrvTesterCursor();  
        while (c.moveToNext()) {  
        	HrvTester tester = new HrvTester();  
        	tester.setId(c.getInt(c.getColumnIndex("id")));
        	tester.setName(c.getString(c.getColumnIndex("name")));  
        	tester.setGender( c.getString(c.getColumnIndex("gender")));  
        	tester.setBirthday( c.getString(c.getColumnIndex("birthday")));  
            
        	hrvTesterLists.add(tester);  
        }  
        c.close();  
        return hrvTesterLists;  
    }
    public Cursor queryTheHrvTesterCursor() {  
        Cursor c = db.rawQuery("SELECT * FROM HrvTester", null);  
        return c;  
    } 
    /** 
     * query all hrv tester list, return cursor 
     * @return  Cursor 
     */  
    public Cursor queryTheHrvResultCursor(String id) {  
        Cursor c = db.rawQuery("SELECT * FROM HrvResult where testerid =?", new String[]{id});  
        return c;  
    }  
    public void clearDB(){
    	db.delete("HrvTester", null, null);  
    	db.delete("HrvResult", null, null);  
    }
    /** 
     * close database 
     */  
    public void closeDB() {  
        db.close();  
    }  
    public void deleteDB() {
    	helper.deleteDatabase(context);
    }
}
