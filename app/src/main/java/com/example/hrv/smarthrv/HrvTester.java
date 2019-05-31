package com.example.hrv.smarthrv;


import android.os.Parcel;
import android.os.Parcelable;


public class HrvTester implements Parcelable{
	private int _id;
	private String _name;
	private String _gender;
	private String  _birthday;
	
	
	public HrvTester(){
		_id = 0;
		_name = "";
		_gender = "";
		_birthday = "";
	}
	protected HrvTester(Parcel in) {  
		_id = in.readInt();  
		_name = in.readString();  
		_gender = in.readString();
		_birthday = in.readString();
    }  
  
    public static final Creator<HrvTester> CREATOR = new Creator<HrvTester>() {  
        @Override  
        public HrvTester createFromParcel(Parcel in) {  
            return new HrvTester(in);  
        }  
  
        @Override  
        public HrvTester[] newArray(int size) {  
            return new HrvTester[size];  
        }  
    };  
  
    @Override  
    public int describeContents() {  
        return 0;  
    }  
  
    //将对象属性进行序列化  
    @Override  
    public void writeToParcel(Parcel dest, int flags) {  
        dest.writeInt(_id);  
        dest.writeString(_name);  
        dest.writeString(_gender);  
        dest.writeString(_birthday);  
    }  
	public int getId(){
		return _id;
	}
	public String getName(){
		return _name;
	}
	public String getGender(){
		return _gender;
	}
	public String getBirthday(){
		return _birthday;
	}
	public void setId(int id){
		 _id = id;
	}
	public void setName(String name){
		_name = name;
	}
	public void setGender(String gender){
		_gender = gender;
	}
	public void setBirthday(String birthday){
		_birthday = birthday;
	}
public HrvTester(int id,String name,String gender,String birthday){
	this._id = id;
	this._name =  name;
	this._gender =  gender;
	this._birthday = birthday;
		
	}

	
}
