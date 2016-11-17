package com.github.anrtestapp;

import android.app.Application;
import android.util.Log;

import com.github.anrwatchdog.ANRError;
import com.github.anrwatchdog.ANRWatchDog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class ANRWatchdogTestApplication extends Application {

    ANRWatchDog anrWatchDog = new ANRWatchDog(2000);

    @Override
    public void onCreate() {
        super.onCreate();

        //统一注册异常捕获handler
        UnCrashHandler unCrashHandler = UnCrashHandler.getInstance();
        unCrashHandler.init(getApplicationContext());

        anrWatchDog.setANRListener(new ANRWatchDog.ANRListener() {
            @Override
            public void onAppNotResponding(ANRError error) {
                Log.e("ANR-Watchdog", "Detected Application Not Responding!");

                // Some tools like ACRA are serializing the exception, so we must make sure the exception serializes correctly
                try {
                    new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(error);
                }
                catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                //后期扩展,用于将拿到的异常数据保存,或者上传到服务器
                Log.i("ANR-Watchdog", "Error was successfully serialized");

                //使用PrintWriter
                PrintWriter pw = null;
                Writer writer = null;
                try {
                    writer = new StringWriter();
                    pw = new PrintWriter(writer);
                    error.printStackTrace(pw);
                    Log.i("zq",writer.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (null != pw) {
                        pw.close();
                    }
                }

                //使用PrintStream
                ByteArrayOutputStream baos = null;
                PrintStream ps = null;
                try{
                    baos = new ByteArrayOutputStream();
                    ps = new PrintStream(baos);
                    error.printStackTrace(ps);
                    String msg = baos.toString();
                    Log.i("zq",msg);
                }catch (Exception E){
                    E.printStackTrace();
                }finally {
                    if(ps != null){
                        ps.close();
                    }
                }




                //error.setStackTrace();
                //for(int i = 0; i<error.getStackTrace().length;i++){
                    Log.i("zq",error.getCause()+"");
                Log.i("zq",error.getMessage()+"");
                Log.i("zq",error.getLocalizedMessage()+"");
                Log.i("zq",error.getClass()+"");
                //Log.i("zq",error.getStackTrace()[0]+"");
                //}

                throw error;
            }
        });

        //anrWatchDog.start();
    }
}
