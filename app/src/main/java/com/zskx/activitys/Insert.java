package com.zskx.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.hrv.startpack.R;
import com.zskx.util.Config;
import com.zskx.util.MySqliteHelper;
import com.zskx.util.PersonModel;
import com.zskx.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.zskx.util.FileUtils.copyDBToSDcrad;


public class Insert extends AppCompatActivity {

    private EditText name;
    private EditText age;
    private EditText number;
    private Spinner sex;
    private Button btn,back;
    private  int Sexint;
    private MySqliteHelper mySqliteHelper;
    private String data;
    private JSONObject json = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        Intent intent = getIntent();
        data = intent.getStringExtra("userInfo");
        mySqliteHelper = new MySqliteHelper(this, Config.DB_NAME, null, Config.DB_VERSION);
        init();
        initView();
        if(("").equals(data) || data == null){
            return;
        }else{
            try {
                setUserInfo(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void init(){
        name = (EditText) findViewById(R.id.name);
        age = (EditText) findViewById(R.id.age);
        number = (EditText) findViewById(R.id.number);
        sex = (Spinner) findViewById(R.id.sex);
        btn  = (Button) findViewById(R.id.btn);
        back = (Button) findViewById(R.id.back);
    }
    public void setUserInfo(String user) throws JSONException {
        json = new JSONObject(user);
        name.setText(json.getString("name"));
        age.setText(json.getString("age"));
        number.setText(json.getString("number"));
        if(json.getInt("isboy") == 0){
            sex.setSelection(1,true);
        }else{
            sex.setSelection(0,true);
        }

    }
    @SuppressLint("WrongViewCast")
    public void initView(){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null == name.getText() || name.getText().toString().equals("")){
                    ToastUtils.show(Insert.this, "姓名不能为空");
                    return;
                }else if(null == age.getText() || age.getText().toString().equals("")){
                    ToastUtils.show(Insert.this, "年龄不能为空");
                    return;
                }else if(null == number.getText() || number.getText().toString().equals("")){
                    ToastUtils.show(Insert.this, "电话不能为空");
                    return;
                }else if(null == sex.getSelectedItem().toString() || sex.getSelectedItem().toString().equals("")){
                    ToastUtils.show(Insert.this, "性别不能为空");
                    return;
                }else{
                    if(sex.getSelectedItem().toString().equals("男")){
                        Sexint = 1;
                    }else{
                        Sexint = 0;
                    }
                    try {
                        addDataReturnID();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Insert.this, MainActivity.class);
                Insert.this.startActivityForResult(intent, Config.REQUEST_CODE);
            }
        });
    }
    private void addDataReturnID() throws JSONException {
        PersonModel model = new PersonModel();

        model.setName(name.getText().toString());
        model.setAge(Integer.parseInt(age.getText().toString()));
        model.setIsBoy(Sexint);
        model.setNumber(number.getText().toString());

        model.setTitle("自主神经心身平衡指数评估报告");
        model.setStatus("N");
        if(("").equals(data) || data == null){
            model = mySqliteHelper.addPersonDataReturnID(model);
            if (model != null) {
                ToastUtils.show(this, "添加数据成功");
                Intent intent = new Intent(Insert.this, MainActivity.class);
                Insert.this.startActivityForResult(intent, Config.REQUEST_CODE);
                copyDBToSDcrad();
            } else {
                ToastUtils.show(this, "添加失败");
            }
        }else{
            model = mySqliteHelper.updatePersonDataReturnID(model,json.getInt("_id"));
            if (model != null) {
                ToastUtils.show(this, "修改数据成功");
                Intent intent = new Intent(Insert.this, MainActivity.class);
                Insert.this.startActivityForResult(intent, Config.REQUEST_CODE);
                copyDBToSDcrad();
            } else {
                ToastUtils.show(this, "修改失败");
            }
        }


    }
}
