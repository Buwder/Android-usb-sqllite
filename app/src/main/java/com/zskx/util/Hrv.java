package com.zskx.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;

import android.os.Environment;
import android.util.Log;

import com.example.hrv.Algorithm.EcgJinClient;
import com.example.hrv.Algorithm.ResultOfHRV;
import com.example.hrv.smarthrv.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * HRV数据监测处理
 */
public class Hrv {
    private MySqliteHelper mySqliteHelper;
    //保存数据的集合
    private List<Integer> listHrvAnalysisData;
    //USB 通信
    private static UsbSerialDriver sDriver = null;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager mSerialIoManager;
    //HRV数据
    private ResultOfHRV resultOfHRV;
    //第几次调用
    private int next;
    private Context context;

    //加载so
    static {
        System.loadLibrary("EcgAlgorithm");
    }

    /**
     * 构造方法
     */
    public Hrv(Context context,int next) {
        this.context = context;
        this.next = next;
        //初始化HRV
        EcgJinClient.InitButtFiler(500, EcgJinClient.kLoPass, 2, 2.0f, 0.0f);
        //初始化集合
        listHrvAnalysisData = Collections.synchronizedList(new LinkedList<Integer>());
    }

    /**
     * 开始监听
     */
    public void startHrv() {
        try {
            if (Config.mUsbManager == null || Config.usbDevice == null) {
                Log.e("USB设备", "连接失败");
                return;
            }
            UsbDeviceConnection mUsbDeviceConnection = Config.mUsbManager.openDevice(Config.usbDevice);

            sDriver = new Cp2102SerialDriver(Config.usbDevice, mUsbDeviceConnection);
            sDriver.open();
            sDriver.setParameters(115200, 8, UsbSerialDriver.STOPBITS_1, UsbSerialDriver.PARITY_NONE);
            sDriver.purgeHwBuffers(true, true);
            if (sDriver != null) {
                mSerialIoManager = new SerialInputOutputManager(sDriver, mListener);
                mExecutor.submit(mSerialIoManager);
                byte[] startRead = new byte[1];
                startRead[0] = 0x31;
                mSerialIoManager.writeAsync(startRead);
            }
        } catch (IOException e) {
            try {
                sDriver.close();
            } catch (IOException ignored) {
            }
            sDriver = null;
        }
    }
    /**
     * USB回调监听
     */
    private final Listener mListener =
            new Listener() {
                @Override
                public void onRunError(Exception e) {
//                    finish();
                }

                @Override
                public void onNewData(final byte[] data) {
                    //处理返回的数据
                    updateReceivedData(data);
                }
            };


    /**
     * 处理USB返回的数据
     *
     * @param data 数据
     */
    private void updateReceivedData(byte[] data) {
            float[] sampleFilterData = new float[data.length / 2];
        int[] sampleData = new int[data.length / 2];
        //变量
        int dataInt;
        for (int i = 0, j = 0; i < (data.length / 2) * 2; ) {
            dataInt = (0x0FF & data[i]) << 8;
            dataInt = dataInt + (0x0FF & (data[i + 1]));
            sampleData[j++] = dataInt;
            i += 2;
        }
        for (int j = 0; j < sampleData.length; j++) {
            sampleFilterData[j] = sampleData[j];
        }
        int filterRet = EcgJinClient.FilterData(sampleFilterData, sampleFilterData.length);
        for (int j = 0; j < sampleData.length; j++) {
            if (filterRet > 0) {//滤波成功
                dataInt = (int) (sampleFilterData[j]);
            } else {
                dataInt = sampleData[j];
            }
            listHrvAnalysisData.add(dataInt);
        }
    }

    /**
     * 停止监听
     */
    public void stopHrv() {
        try {
            if (mSerialIoManager != null) {
                mSerialIoManager.stop();
                mSerialIoManager = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        CaculteHRVStatus();
    }

    /**
     * 处理数据
     */
    private void CaculteHRVStatus() {
        int ppgDataLen = listHrvAnalysisData.size();
        Integer[] ppgArray = listHrvAnalysisData.toArray(new Integer[ppgDataLen]);
        long[] intPPGArray = new long[ppgDataLen];
        for (int i = 0; i < ppgDataLen; i++) {
            intPPGArray[i] = ppgArray[i];
        }
        resultOfHRV = new ResultOfHRV();
        resultOfHRV = EcgJinClient.CalculateHRVResult(intPPGArray, ppgDataLen, 500, resultOfHRV);

        if (resultOfHRV.caculateFlag == 1) {
            //上传数据
            setData();
        }
    }
    /**
     * 上传数据
     */
    private void setData() {
        mySqliteHelper = new MySqliteHelper(context, Config.DB_NAME, null, Config.DB_VERSION);
        StringBuilder sb = new StringBuilder();
        Log.d("BPS++++",String.format("%d",(int)resultOfHRV.BPS));
        sb.append("[{\"key\":\"h_PFT\",\"score\":");
        sb.append((Math.round(resultOfHRV.getBFS() * 100) / 100.0));//疲劳状态
        sb.append("},{\"key\":\"h_MES\",\"score\":");
        sb.append((Math.round(resultOfHRV.getEMS() * 100) / 100.0));//精神情绪状态
        sb.append("},{\"key\":\"h_PRU\",\"score\":");
        sb.append((Math.round(resultOfHRV.getBPS() * 100) / 100.0));
        sb.append("},{\"key\":\"h_BUP\",\"score\":");
        sb.append((Math.round(resultOfHRV.getBRPA() * 100) / 100.0));
        sb.append("},{\"key\":\"h_VNE\",\"score\":");
        sb.append(Math.round(resultOfHRV.getHFnorm()*100)/100.0);
        sb.append("},{\"key\":\"h_SNE\",\"score\":");
        sb.append(Math.round(resultOfHRV.getLFnorm()*100)/100.0);
        sb.append("},{\"key\":\"h_ANFS\",\"score\":");
        sb.append((Math.round(resultOfHRV.getLFHF() * 100) / 100.0));//ANFS平衡状态
        sb.append("}]");
        if (next == 1) {
            Log.e("第二次录入结束",sb.toString());
            mySqliteHelper.updateHrv(sb.toString(),1);
        } else {
            //第一次上传
            Log.e("第一次录入结束",sb.toString());
            mySqliteHelper.updateHrv(sb.toString(),0);
        }
    }

}
