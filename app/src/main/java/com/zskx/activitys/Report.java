package com.zskx.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.hrv.startpack.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Report extends AppCompatActivity {
    private WebView report;
    private String data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        initView();
    }
    public void initView(){
        Intent intent = getIntent();
        data = intent.getStringExtra("report");

        report = (WebView) findViewById(R.id.report);
        report.setBackgroundColor(0);
        report.loadUrl("file:///android_asset/report.html");
        report.getSettings().setJavaScriptEnabled(true);
        report.addJavascriptInterface(new JiaoHu(),"sendData");
        report.getSettings().setDomStorageEnabled(true);
        report.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        report.getSettings().setLoadWithOverviewMode(true);

        report.getSettings().setBuiltInZoomControls(false);
        report.getSettings().setSupportZoom(false);
        report.getSettings().setUseWideViewPort(false);

        report.getSettings().setDefaultTextEncodingName("utf-8");
        report.getSettings().setUseWideViewPort(true);
    }
    public class JiaoHu{
        @JavascriptInterface
        public String getData() throws JSONException {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data",data);
            return jsonObject.toString();
        }
        @JavascriptInterface
        public boolean print(String val) {
            if (val.equals("Y")) {
                doMopriaPrint();
            }
            return false;
        }
    }
    private void doMopriaPrint() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String packageName = "org.mopria.printplugin";
        String className = "org.mopria.printplugin.DocumentRenderingActivity";
        intent.setClassName(packageName, className);

//        Uri data = null;
//
//        data = Uri.fromFile(new File(filePath));
//
//        intent.setData(data);

        startActivity(intent);
    }
}
