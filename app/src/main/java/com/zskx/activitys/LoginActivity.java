package com.zskx.activitys;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.hrv.startpack.R;
import com.zskx.util.FindViewById;
import com.zskx.util.Config;
import com.zskx.util.MySqliteHelper;
import com.zskx.util.ToastUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 登录页面
 */
public class LoginActivity extends AppCompatActivity {

    @FindViewById(R.id.code)
    private EditText code;//条形码输入框
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private final String VALUE_ID = "_id";
    private final String VALUE_STATUS = "status";
    private Context context;
    private MySqliteHelper mySqliteHelper;

    //需要动态注册的权限
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        init();
        dataInit();
    }

    private void dataInit() {
        mySqliteHelper = new MySqliteHelper(context, Config.DB_NAME, null, Config.DB_VERSION);
    }

    private void init() {
        //权限注册
        verifyStoragePermissions(this);
        //初始化组件
        Button btnLogin = findViewById(R.id.btnLogin);
        code = findViewById(R.id.code);
        //添加点击事件
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String text = code.getText().toString();
            Pattern p = Pattern.compile("[0-9]*");
            Matcher m = p.matcher(text);

            if(text.equals("admin")){
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivityForResult(intent, Config.REQUEST_CODE);
            }else if(m.matches() && !text.equals("") && text != null){
                    Cursor userData = mySqliteHelper.queryPersonData(Integer.valueOf(text));
                    userData.moveToFirst();
                    if(userData.getCount() > 0){
                        if(userData.getString(userData.getColumnIndex(VALUE_STATUS)).equals("N")){
                            Config.BAR_CODE = userData.getString(userData.getColumnIndex(VALUE_ID));
                            Intent intent = new Intent(LoginActivity.this, GuideActivity.class);
                            LoginActivity.this.startActivityForResult(intent, Config.REQUEST_CODE);
                        }else{
                            Toast.makeText(LoginActivity.this, "用户已测！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }else{
                        Toast.makeText(LoginActivity.this, "请输入正确编号", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else{
                    ToastUtils.show(context,"请先创建账号!");
                    return;
                }
            }
        });
    }

    /**
     * 双击退出
     */
    private long firstTime = 0;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(LoginActivity.this, R.string.douclick, Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 权限申请
     *
     * @param activity 页面
     */
    private static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }
}
