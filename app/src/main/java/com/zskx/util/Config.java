package com.zskx.util;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import com.zskx.beans.UserBean;

/**
 * 全局常量
 */
public class Config {
    public static final String ACTION_DEVICE_PERMISSION = "com.linc.USB_PERMISSION";
    public static final int TIME = 5*60;//计时器5分钟
    public static String IP;//Ip地址
    public static UserBean USER_INFO;//用户数据
    public static String BAR_CODE;//条形码
    public static final int REQUEST_CODE = 1;//页面请求参数
    public static UsbDevice usbDevice;//usb设备对象
    public static UsbManager mUsbManager;//usb设备管理器
    public static String VIDEO_PATH = Environment.getExternalStorageDirectory().getPath() + "/Android/data/smmx.mp4";//视频地址
    static int HRV_ID=-1;//记录HRV上传次数
    public static final String DB_NAME = "Zskx_DB"; /*数据库名*/
    public static final int DB_VERSION = 1;/*数据库版本号*/
}
