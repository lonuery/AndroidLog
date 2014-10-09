package com.lonuery.play;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class Logcat {

	static Logcat logcat;
	private static boolean isReadLog;
	private String logPath;
	private Context context;
	private int pID;//��ǰ���̵�ID
	private LogThread logThread;
	
	public Logcat(Context context){
		this.context = context;
		pID = android.os.Process.myPid();
		init();
	}
	
	public static Logcat getInstance(Context context){
		logcat = new Logcat(context);
		return logcat;
	}
	
	public void init(){
		 if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			 // ���ȱ��浽SD����   
			 logPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "lonuery";  
         }else {
        	// ���SD�������ڣ��ͱ��浽��Ӧ�õ�Ŀ¼��   
        	logPath = context.getFilesDir().getAbsolutePath() + File.separator + "lonuery";  
         }
		 Log.i("logPath", logPath);
         File file = new File(logPath);  
         if (!file.exists()){  
            file.mkdirs();
         }
	}
	
	public void start(){
		isReadLog = true;
		logThread = new LogThread();
		new Thread(logThread).start();
	}
	
	public void stop(){
		isReadLog = false;
	}
	
	class LogThread implements Runnable{
		FileOutputStream out=null;
		String logFilter;
		Process logProcess;
		BufferedReader bReader;
		
		public LogThread() {
			try {
				Log.i("LogThread",logPath);
				
				File file = new File(logPath, "lonuery-"+System.currentTimeMillis()+".log");				
				
				out = new FileOutputStream(file);
				
			} catch (FileNotFoundException e) {
				Log.i("LogThread", "FileNotFoundException");
				e.printStackTrace();
			}
			/** 
             *  
             * ��־�ȼ���*:v , *:d , *:w , *:e , *:f , *:s 
             *  
             * ��ʾ��ǰmPID����� E��W�ȼ�����־. 
             *  
             **/               
            // logFilter = "logcat *:e *:w | grep \"(" + mPID + ")\"";   
            // logFilter = "logcat  | grep \"(" + mPID + ")\"";//��ӡ������־��Ϣ   
            // logFilter = "logcat -s way";//��ӡ��ǩ������Ϣ   
			logFilter = "logcat *:e *:i -v time | grep \"(" + pID + ")\""; 
            //logFilter = "logcat *:i | grep \"(" + mPID + ")\"";
		}
		
		@Override
		public void run() {
			try {
				logProcess = Runtime.getRuntime().exec(logFilter);
				InputStreamReader input = new InputStreamReader(logProcess.getInputStream());
				bReader = new BufferedReader(input, 1024);
				String buffer =null;
				
				while(isReadLog && (buffer = bReader.readLine())!=null){				
					 if (buffer.length() == 0){
	                        continue;  
	                 }
                    String content =  buffer + "\n";
                    if (out != null && buffer.contains(String.valueOf(pID))) {  
                        out.write(content.getBytes());
                    }
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				if(logProcess!=null){
					logProcess.destroy();
					logProcess=null;
				}
				if(out!=null){
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					out=null;
				}
				if(bReader!=null){
					try {
						bReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					bReader=null;
				}
			}
		}		
	}
}
