package com.zskx.activitys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.hrv.startpack.R;
import com.zskx.util.APIManage;
import com.zskx.util.Hrv;
import com.zskx.util.Config;
import com.zskx.util.MySqliteHelper;

import static com.zskx.util.FileUtils.copyDBToSDcrad;

/**
 * 答题页面
 */
public class WebActivity extends AppCompatActivity {

    private Hrv hrv;//hrv监测类
    private boolean lean = false;//数据是否已经上传
    private AlertDialog alertDialog = null;//提示框
    private SQLiteDatabase db;
    private String answer,genescore,data;
    private MySqliteHelper mySqliteHelper;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_pem);
        context = this;
        //业务实现
        init();
        //开启htv监测
        //initHrv();
        dataInit();
    }

    /**
     * 业务实现
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        //加载web的控件
        WebView sjj = findViewById(R.id.sjj);
        sjj.setBackgroundColor(getResources().getColor(R.color.blackcolor));
        sjj.loadUrl(new APIManage().TouchLogin);
        sjj.getSettings().setJavaScriptEnabled(true);
        sjj.addJavascriptInterface(new JiaoHu(), "fun");
        sjj.getSettings().setDomStorageEnabled(true);
        sjj.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        sjj.getSettings().setLoadWithOverviewMode(true);
        sjj.getSettings().setAppCacheEnabled(false);
        sjj.getSettings().setBuiltInZoomControls(false);
        sjj.getSettings().setSupportZoom(false);
        sjj.getSettings().setUseWideViewPort(false);
        sjj.getSettings().setDefaultTextEncodingName("utf-8");
        sjj.getSettings().setUseWideViewPort(true);
        sjj.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }
    private void dataInit() {
        mySqliteHelper = new MySqliteHelper(context, Config.DB_NAME, null, Config.DB_VERSION);
    }

    /**
     * 与web交互
     */
    public class JiaoHu {
        @JavascriptInterface
        public void sendData(String datas,String answers,String genescores,String val){
            data = datas;
            answer = answers;
            genescore = genescores;

            if (val.equals("Y")) {
                update();
                alertDialog = new AlertDialog.Builder(WebActivity.this)
                        .setMessage(R.string.testfinish)
                        .create();
                alertDialog.show();
                countDownTimer1.start();
            }
        }
    }
    private void update(){
        mySqliteHelper.updateQuestion(genescore,data,answer);
        copyDBToSDcrad();
    }
    /**
     * 定时器
     */
    private CountDownTimer countDownTimer1 = new CountDownTimer(3000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            finish();
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        lean = true;
        if (countDownTimer1 != null) {
            countDownTimer1.cancel();
        }
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }
}
