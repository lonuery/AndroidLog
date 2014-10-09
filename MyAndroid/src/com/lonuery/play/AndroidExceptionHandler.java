package com.lonuery.play;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class AndroidExceptionHandler implements UncaughtExceptionHandler{
	
	Application application;
	String PATH_LOGCAT;
	
	public AndroidExceptionHandler(Application application) {
		this.application = application;
		init(application);
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		handleException(ex);
		
		try{  
            Thread.sleep(1000);  
        }catch (InterruptedException e){
            Log.e("ITalkieExceptionHandler", "error : ", e);  
        }
		Intent intent = new Intent(application.getApplicationContext(), MainActivity.class);
        PendingIntent restartIntent = PendingIntent.getActivity(  
            		application.getApplicationContext(), 0, intent,  
                    Intent.FLAG_ACTIVITY_NEW_TASK);
        AlarmManager mgr = (AlarmManager)application.getSystemService(Context.ALARM_SERVICE);  
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,  
                restartIntent); // 1秒钟后重启应用
        
        android.os.Process.killProcess(android.os.Process.myPid());  
        System.exit(1);
	}

	public boolean handleException(Throwable ex){
		if (ex == null) {  
            return false;  
        }
		//使用Toast来显示异常信息  
        new Thread(){  
            @Override  
            public void run() {
                Looper.prepare();  
                Toast.makeText(application.getApplicationContext(), "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_SHORT).show();  
                Looper.loop();  
            } 
        }.start();
        
        if(ex!=null){
        	saveLogs(ex);
        }
        
        return true;
	}
	
    public void init(Context context) {  
        if (Environment.getExternalStorageState().equals(  
                Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中   
            PATH_LOGCAT = Environment.getExternalStorageDirectory()  
                    .getAbsolutePath() + File.separator + "case";  
        }else {
        	// 如果SD卡不存在，就保存到本应用的目录下   
            PATH_LOGCAT = context.getFilesDir().getAbsolutePath()  
                    + File.separator + "case";  
        }
        File file = new File(PATH_LOGCAT);  
        if (!file.exists()){  
            file.mkdirs();  
        }
    }
	
    public void saveLogs(Throwable ex){
    	try{  
        	File file = new File(PATH_LOGCAT,"PLAY-"+ System.currentTimeMillis() + ".log");
            FileOutputStream out = new FileOutputStream(file);
            
            StringBuffer sb = new StringBuffer();
            saveAppInfo(sb);//获取手机信息
                       
            Writer writer = new StringWriter();  
            PrintWriter printWriter = new PrintWriter(writer);  
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            
            while (cause != null) {  
                cause.printStackTrace(printWriter); 
            }
            printWriter.close();
            
            String result = writer.toString();  
            sb.append(result);//获取异常信息
            
			out.write(sb.toString().getBytes());
			out.close();
        }catch (FileNotFoundException e){   
            e.printStackTrace();  
        }catch (IOException e) {
			e.printStackTrace();
		} 
    }
    
    public void saveAppInfo(StringBuffer sb){
        try {
        	PackageManager pm = application.getPackageManager();  
			PackageInfo pi = pm.getPackageInfo(application.getPackageName(), PackageManager.GET_ACTIVITIES);
			
			if(pi!=null){
				 String versionName = pi.versionName == null ? "null" : pi.versionName;
	             String versionCode = pi.versionCode + "";	                       
	             
	             sb.append("versionName"+" = "+versionName+"\n");
	             sb.append("versionCode"+" = "+versionCode+"\n");
			}
			
			Field[] fields = Build.class.getDeclaredFields();  
	        for (Field field : fields) {
                field.setAccessible(true);               
				sb.append(field.getName()+" = "+field.get(null).toString()+"\n");
	        }
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} 
    }
}
