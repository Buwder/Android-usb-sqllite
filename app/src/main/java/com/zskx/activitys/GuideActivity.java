package com.zskx.activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.hrv.startpack.R;
import com.zskx.util.Config;
import com.zskx.util.ToastUtils;

import java.util.HashMap;
import java.util.Objects;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * HRV佩戴引导页面
 */
public class GuideActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_guide);
        Config.usbDevice = null;
        Config.mUsbManager = null;
        init();
        requestPower();
        InitializeComUsb();
    }

    private void init(){
        Button btnLogin = findViewById(R.id.comein);
        //添加点击事件
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Config.mUsbManager != null && Config.usbDevice != null){
                    Intent intent = new Intent(GuideActivity.this, VideoActivity.class);
                    GuideActivity.this.startActivity(intent);
                    finish();
                }else {
                    ToastUtils.show(GuideActivity.this,"设备未挂载！");
                }
            }
        });
    }


    /**
     * 回车键
     *
     * @param keyCode 响应码
     * @param event   事件
     * @return 返回值
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 160) {
            //进入视频播放
            Intent intent = new Intent(GuideActivity.this, VideoActivity.class);
            GuideActivity.this.startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * 动态权限申请
     */
    public void requestPower() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "需要读写权限，请打开设置开启对应的权限", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        }
    }

    /**
     * onRequestPermissionsResult方法重写，Toast显示用户是否授权
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        String requestPermissionsResult = "";
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {
                    requestPermissionsResult += permissions[i] + " 申请成功\n";
                } else {
                    requestPermissionsResult += permissions[i] + " 申请失败\n";
                }
            }
        }
        Toast.makeText(this, requestPermissionsResult, Toast.LENGTH_SHORT).show();
    }

    /**
     * 通过usb连接hrv
     */
    @SuppressLint("NewApi")
    public void InitializeComUsb() {
        //USB
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(Config.ACTION_DEVICE_PERMISSION), 0);
        UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        Config.mUsbManager = mUsbManager;
        HashMap<String, UsbDevice> deviceHashMap = mUsbManager.getDeviceList();
        for (UsbDevice device : deviceHashMap.values()) {
            if (0x10c4 == device.getVendorId() && 0xEa60 == device.getProductId() && Objects.requireNonNull(device.getProductName()).contains("CP2102")) {
                //打开USB
                if (mUsbManager.hasPermission(device)) {
                    Config.usbDevice = device;
                } else {
                    mUsbManager.requestPermission(device, mPermissionIntent);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        InitializeComUsb();
                                    }
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                   return;
                }
                break;
            }
        }
    }
}
