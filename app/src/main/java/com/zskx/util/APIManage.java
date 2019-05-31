package com.zskx.util;

/**
 * 请求接口管理
 */
public class APIManage {
    //请求头
    private static String Head = "http://";

    //用户登录方法
    public String LoginSleep = Head + Config.IP + "/open/loginTestSleep.action";
    //上传HRV数据方法
    static String SaveHrvData = Head + Config.IP + "/open/saveHrvData.action";
    //获取答题web方法
    public String TouchLogin = "file:///android_asset/guide.html";
}
