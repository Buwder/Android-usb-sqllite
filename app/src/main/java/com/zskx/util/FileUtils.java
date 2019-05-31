package com.zskx.util;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtils {
    public static boolean fileIsExists(String strFile) {
        try {
            File f=new File(strFile);
            if(!f.exists()) {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
    //复制数据库去根目录
    public static void copyDBToSDcrad() {

        String oldPath = "data/data/com.example.hrv.startpackUserAdmin/databases/" + Config.DB_NAME;
        String newPath = Environment.getExternalStorageDirectory() + File.separator + "Android/data/" + Config.DB_NAME;

        copyFile(oldPath, newPath);
    }
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            File newfile = new File(newPath);
            if (!newfile.exists()) {
                newfile.createNewFile();
            }
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    fs.write(buffer,0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("系统错误!");
            e.printStackTrace();
        }
    }
}
