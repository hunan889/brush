package com.yy.brushswitcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class Log {

	private static BufferedWriter mLogWriter;
	private static final String LOG_FILE_PATH = "log.txt";
	public static void init() {
		try {
			mLogWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(LOG_FILE_PATH)), "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static void release() {
		if(mLogWriter != null) {
			try {
				mLogWriter.flush();
				mLogWriter.close();
				mLogWriter = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void log(String message) {
		if(mLogWriter != null) {
			try {
				mLogWriter.append(message);
				mLogWriter.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
