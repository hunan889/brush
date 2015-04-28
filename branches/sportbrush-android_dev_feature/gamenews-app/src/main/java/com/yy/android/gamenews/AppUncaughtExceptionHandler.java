package com.yy.android.gamenews;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;

import android.os.Environment;

import com.duowan.android.base.util.LocalLog;

public class AppUncaughtExceptionHandler implements UncaughtExceptionHandler {

	private Thread arg0;

	// public static final String PACK_RANGTONE_DIR_PATH = Environment
	// .getExternalStorageDirectory().getAbsolutePath()
	// + File.separator
	// + "brush" + File.separator;

	/** 系统默认的UncaughtException处理类 */
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	public AppUncaughtExceptionHandler() {
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread arg0, Throwable arg1) {
		this.arg0 = arg0;
		handleException(arg1);

	}

	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}

		final String crashReport = getCrashReport(ex);
		saveErrorLog(new Exception(crashReport));
		// TODO 上传服务器，退出应用
		return true;
	}

	/**
	 * 获取APP崩溃异常报告
	 * 
	 * @param ex
	 * @return
	 */
	private String getCrashReport(Throwable ex) {
		if (ex == null) {
			return "";
		}
		StringBuffer exceptionStr = new StringBuffer();
		exceptionStr.append("Exception: Thread: id : " + arg0.getId()
				+ "   Priority : " + arg0.getPriority() + "   Name : "
				+ arg0.getName() + "   Class : " + arg0.getClass().toString()
				+ "   State : " + arg0.getState() + "\n");
		exceptionStr.append(ex.getMessage() + "\n");
		StackTraceElement[] elements = ex.getStackTrace();
		for (int i = 0; i < elements.length; i++) {
			exceptionStr.append(elements[i].toString() + "\n");
		}
		exceptionStr.append("\n");
		exceptionStr
				.append("caused by:" + getCrashReport(ex.getCause()) + "\n");
		exceptionStr.append("\n" + "\n" + "\n");
		return exceptionStr.toString();
	}

	public static void saveErrorLog(Exception ex) {
		if (ex == null) {
			return;
		}
		ex.printStackTrace();
		String errorlog = "/error.log";
		String savePath = "";
		String logFilePath = "";
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			// 判断是否挂载了SD卡
			String storageState = Environment.getExternalStorageState();
			if (storageState.equals(Environment.MEDIA_MOUNTED)) {
				savePath = LocalLog.LOG_FILE_FOLDER;
				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdirs();
				}
				logFilePath = savePath + errorlog;
			}
			// 没有挂载SD卡，无法写文件
			if (logFilePath == "") {
				return;
			}
			File logFile = new File(logFilePath);
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
			fw = new FileWriter(logFile, true);
			pw = new PrintWriter(fw);
			pw.println("--------------------" + (new Date().toLocaleString())
					+ "---------------------");
			ex.printStackTrace(pw);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
			if (fw != null) {
				try {
					fw.close();
				} catch (Exception e) {
				}
			}
		}
	}

}
