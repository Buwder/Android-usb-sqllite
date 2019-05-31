package com.zskx.util;

import org.json.JSONException;
import org.json.JSONObject;

public class MyJSONObject {

    /**
     * 获取字符串数据
     *
     * @param jsonObject 传入的json对象
     * @param key        键名
     * @return 获取到的值
     */
    public static String getString(JSONObject jsonObject, String key) throws JSONException {
        if (jsonObject == null) {
            return "";
        }
        if (jsonObject.has(key)) {
            return jsonObject.getString(key);
        }
        return "";
    }

    /**
     * 获取int数据
     *
     * @param jsonObject 传入的json对象
     * @param key        键名
     * @return 获取到的值
     */
    public static int getInt(JSONObject jsonObject, String key) throws JSONException {
        if (jsonObject == null) {
            return -1;
        }
        if (jsonObject.has(key)) {
            return jsonObject.getInt(key);
        }
        return -1;
    }

    /**
     * 获取long数据
     *
     * @param jsonObject 传入的json对象
     * @param key        键名
     * @return 获取到的值
     */
    public static long getLong(JSONObject jsonObject, String key) throws JSONException {
        if (jsonObject == null) {
            return -1;
        }
        if (jsonObject.has(key)) {
            return jsonObject.getLong(key);
        }
        return -1;
    }

    /**
     * 获取double数据
     *
     * @param jsonObject 传入的json对象
     * @param key        键名
     * @return 获取到的值
     */
    public static double getDouble(JSONObject jsonObject, String key) throws JSONException {
        if (jsonObject == null) {
            return -1;
        }
        if (jsonObject.has(key)) {
            return jsonObject.getDouble(key);
        }
        return -1;
    }

    /**
     * 获取boolean数据
     *
     * @param jsonObject 传入的json对象
     * @param key        键名
     * @return 获取到的值
     */
    public static boolean getBoolean(JSONObject jsonObject, String key) throws JSONException {
        if (jsonObject == null) {
            return false;
        }
        if (jsonObject.has(key)) {
            return jsonObject.getBoolean(key);
        }
        return false;
    }
}
