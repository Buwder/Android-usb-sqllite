package com.zskx.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class MySqliteHelper extends SQLiteOpenHelper {

    private String TAG = "MySqliteHelper";
    private final String TABLE_NAME_PERSON = "user";
    private final String VALUE_ID = "_id";
    private final String VALUE_NAME = "name";
    private final String VALUE_ISBOY = "isboy";
    private final String VALUE_AGE = "age";
    private final String VALUE_NUMBER = "number";
    private final String VALUE_ANSWER = "answer";
    private final String VALUE_HRVDATA1 = "hrvdata1";
    private final String VALUE_HRVDATA2 = "hrvdata2";
    private final String VALUE_GENESCORE = "genescore";
    private final String VALUE_DATA = "data";
    private final String VALUE_TITLE = "title";
    private final String VALUE_STATUS = "status";

    /*创建表语句 语句对大小写不敏感 create table 表名(字段名 类型，字段名 类型，…)*/
    private final String CREATE_PERSON = "create table " + TABLE_NAME_PERSON + "(" +
            VALUE_ID + " integer primary key," +
            VALUE_NAME + " text," +
            VALUE_ISBOY + " integer," +
            VALUE_AGE + " ingeter," +
            VALUE_NUMBER + " text,"+
            VALUE_ANSWER + " text,"+
            VALUE_HRVDATA1 + " text,"+
            VALUE_HRVDATA2 + " text,"+
            VALUE_GENESCORE+ " text,"+
            VALUE_DATA + " text,"+
            VALUE_TITLE + " text,"+
            VALUE_STATUS + " text" +
            ")";

    public MySqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.e(TAG, "-------> MySqliteHelper");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表
        db.execSQL(CREATE_PERSON);
        Log.e(TAG, "-------> onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion != newVersion)
        {
            switch (newVersion)
            {
                case 3:
                    //升级数据库，不改变表结构 注意空格
                    //添加列 addcol_goods2 , text 为字符串数据类型 ,person为表名
                    //alter table person add column addcol_goods2 text
                    //添加的列 添加优点 添加缺点
                    String addColGoods = "addcol_goods"+newVersion;
                    String addColBads = "addcol_bads"+newVersion;
                    //添加列的sql语句
                    String upgradeGoods = "alter table "+TABLE_NAME_PERSON + " add column "+ addColGoods+" text";
                    String upgradeBads = "alter table "+TABLE_NAME_PERSON+" add column "+addColBads+" text";
                    //执行sql语句  一次只能添加一个字段
                    db.execSQL(upgradeGoods);
                    db.execSQL(upgradeBads);
                    Log.e(TAG, ""+upgradeGoods );
                    break;
            }
        }
    }

    /**
     * 添加数据
     * @param model 数据模型
     * @return 返回添加数据有木有成功
     */
    public PersonModel addPersonDataReturnID(PersonModel model) {
        //把数据添加到ContentValues
        ContentValues values = new ContentValues();
        values.put(VALUE_NAME, model.getName());
        values.put(VALUE_AGE, model.getAge());
        values.put(VALUE_ISBOY, model.getIsBoy());
        values.put(VALUE_NUMBER,model.getNumber());
        values.put(VALUE_GENESCORE,model.getGenescore());
        values.put(VALUE_ANSWER,model.getAnswer());
        values.put(VALUE_HRVDATA1,model.getHrvdata1());
        values.put(VALUE_HRVDATA2,model.getHrvdata2());
        values.put(VALUE_STATUS,model.getStatus());
        values.put(VALUE_DATA,model.getData());
        values.put(VALUE_TITLE,model.getTitle());
        //添加数据到数据库
        long index = getWritableDatabase().insert(TABLE_NAME_PERSON, null, values);

        if (index != -1) {
            model.setId(index);
            return model;
        } else {
            return null;
        }
    }
    public PersonModel updatePersonDataReturnID(PersonModel model,Integer id) {
        //把数据添加到ContentValues
        ContentValues values = new ContentValues();
        values.put(VALUE_NAME, model.getName());
        values.put(VALUE_AGE, model.getAge());
        values.put(VALUE_ISBOY, model.getIsBoy());
        values.put(VALUE_NUMBER,model.getNumber());
        //添加数据到数据库
        long index = getWritableDatabase().update(TABLE_NAME_PERSON,values,VALUE_ID + "=?", new String[]{"" + id});
        if (index != -1) {
            model.setId(index);
            return model;
        } else {
            return null;
        }
    }
    public Cursor queryPersonData(Integer id) {
        Cursor cursor = null;
        cursor = getWritableDatabase().query(TABLE_NAME_PERSON, null, VALUE_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }
    public void deletePersonData(PersonModel model) {
        getWritableDatabase().delete(TABLE_NAME_PERSON, VALUE_ID + "=?", new String[]{"" + model.getId()});
    }
    public void updateHrv(String sb,Integer once){
        ContentValues values = new ContentValues();
        if(once == 0){
            values.put(VALUE_HRVDATA1, sb);
        }else{
            values.put(VALUE_HRVDATA2, sb);
        }
        getWritableDatabase().update(TABLE_NAME_PERSON,values,VALUE_ID + "=?", new String[]{"" + Config.BAR_CODE});
    }
    public void updateQuestion(String genescore,String data,String answer){
        ContentValues values = new ContentValues();
        values.put(VALUE_GENESCORE, genescore);
        values.put(VALUE_DATA,data);
        values.put(VALUE_ANSWER,answer);
        values.put(VALUE_STATUS,"Y");
        values.put(VALUE_TITLE,"自主神经心身平衡指数评估报告");
        getWritableDatabase().update(TABLE_NAME_PERSON,values,VALUE_ID + "=?", new String[]{"" + Config.BAR_CODE});
    }
    public List<PersonModel> queryAllPersonData() {

        Cursor cursor = getWritableDatabase().query(TABLE_NAME_PERSON, null, null, null, null, null, null, null);
        List<PersonModel> list = new ArrayList<>();
        if (cursor.getCount() > 0) {
            //移动到首位
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {

                int id = cursor.getInt(cursor.getColumnIndex(VALUE_ID));
                String name = cursor.getString(cursor.getColumnIndex(VALUE_NAME));
                int isBoy = cursor.getInt(cursor.getColumnIndex(VALUE_ISBOY));
                int age = cursor.getInt(cursor.getColumnIndex(VALUE_AGE));
                String number = cursor.getString(cursor.getColumnIndex(VALUE_NUMBER));
                String question = cursor.getString(cursor.getColumnIndex(VALUE_TITLE));
                PersonModel model = new PersonModel();
                model.setId(id);
                model.setName(name);
                model.setIsBoy(isBoy);
                model.setAge(age);
                model.setNumber(number);
                model.setTitle(question);
                list.add(model);
                //移动到下一位
                cursor.moveToNext();
            }
        }
        cursor.close();
        getWritableDatabase().close();

        return list;
    }
}
