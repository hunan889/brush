package com.duowan.android.base.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

public class LocalLog {

	public static final String LOG_FILE_FOLDER = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/brush/log";
	private static final String LOG_FILE_PATH = LOG_FILE_FOLDER + "/log.txt";
	private static File mLogFile;

	private static void init() {
		if (mLogFile != null) {
			return;
		}

		File folder = new File(LOG_FILE_FOLDER);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		mLogFile = new File(LOG_FILE_PATH);
		checkFileSize();
	}

	public static boolean FLAG_WRITE_TO_FILE = false;

	public static void d(String TAG, Object msg) {
		// android.util.Log.d(TAG, msg);
		writeToFile(msg);
	}

	public static void e(String TAG, Object msg) {
		// android.util.Log.e(TAG, msg);
		writeToFile(msg);
	}

	public static void v(String TAG, Object msg) {
		// android.util.Log.v(TAG, msg);
		writeToFile(msg);
	}

	public static void i(String TAG, Object msg) {
		// android.util.Log.i(TAG, msg);
		writeToFile(msg);
	}

	public static void w(String TAG, Object msg) {
		// android.util.Log.w(TAG, msg);
		writeToFile(msg);
	}

	public static void writeToFile(Object message) {
		if (!FLAG_WRITE_TO_FILE) {
			return;
		}
		init();
		checkFileSize();
		try {
			FileWriter out = new FileWriter(mLogFile, true);
			out.append("\n=*=*=*=*=*=*=*=*=*==*=*=*=*=*=*=*=*=*=*\n");
			printMessageWithTrace(out, message);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void checkFileSize() {
		while (mLogFile.exists() && mLogFile.length() > SIZE) {
			String deleteFileName = LOG_FILE_PATH;
			if (FILE_INDEX > 1) {
				deleteFileName += ("_" + (FILE_INDEX - 1));
			}
			File deleteFile = new File(deleteFileName);
			if (deleteFile.exists()) {
				deleteFile.delete();
			}

			FILE_INDEX++;
			mLogFile = new File(LOG_FILE_PATH + "_" + FILE_INDEX);
		}

		if (!mLogFile.exists()) {
			try {
				mLogFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static int FILE_INDEX = 0;
	private static final int SIZE = 128 * 1024 * 1024;
	private static final int MAX_STACK_COUNT = 5;

	private static void printStackTrace(FileWriter out) {
		Throwable ex = new Throwable();
		StackTraceElement[] stackElements = ex.getStackTrace();
		if (stackElements != null) {
			int printedStackCount = 0;
			for (int i = 0; i < stackElements.length; i++) {
				if (i == 0) {
					continue;
				}

				StackTraceElement element = stackElements[i];
				String className = element.getClassName();
				if (LocalLog.class.getName().equals(className)) {
					continue;
				}
				printedStackCount++;
				if (printedStackCount > MAX_STACK_COUNT) {
					break;
				}
				String fileName = element.getFileName();
				int lineNumber = element.getLineNumber();
				String methodName = element.getMethodName();

				try {
					out.append("at: " + className + "." + methodName + "("
							+ fileName + ":" + lineNumber + ")\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static void printMessageWithTrace(FileWriter out, Object message)
			throws IOException {
		if (message == null) {
			message = "";
		}
		String time = format.format(new Date());
		out.append(message + "(time:" + time + ")\n");
		printStackTrace(out);
	}
}
