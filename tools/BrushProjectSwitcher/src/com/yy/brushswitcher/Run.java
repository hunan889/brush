package com.yy.brushswitcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.w3c.dom.Element;

public class Run {
	private static final String POM_NAME = "pom.xml";

	private ConfigReader mConfigReader = new ConfigReader();

	private static final String PARAMS_TARGET_PATH = "-t";
	private static final String PARAMS_BACKUP_PATH = "-s";

	public static void main(String args[]) throws IOException {
		Run run = new Run();
		Log.init();
		if (run.initArgs(args)) {
			run.run();
		}
		Log.release();
	}

	private boolean initArgs(String args[]) {
		if (args == null) {
			Log.log("请输入参数");
			return false;
		}

		try {
			for (int i = 0; i < args.length; i++) {
				String s = args[i];
				if (i % 2 == 0) {
					if (PARAMS_BACKUP_PATH.equals(s)) {
						backUpBase = args[++i];
					} else if (PARAMS_TARGET_PATH.equals(s)) {
						codeBase = args[++i];
					}
				}
			}
		} catch (Exception e) {
			Log.log("参数输入有误，请检查参数!");
			return false;
		}
		File codeBaseFile = new File(codeBase);
		if (!codeBaseFile.exists()) {
			Log.log("目标工程路径不存在，请检查config.xml文件， codeBase = "
					+ codeBase);
			return false;
		}
		File backupFile = new File(backUpBase);
		if (!backupFile.exists()) {
			Log.log("备份工程路径不存在，请检查config.xml文件, backUpBase = "
					+ backUpBase);
			return false;
		}
		codeBase = codeBaseFile.getAbsolutePath();
		backUpBase = backupFile.getAbsolutePath();
		mConfigReader.init(codeBase, backUpBase);
		initPath();
		return true;
	}

	private void run() {
		Log.log("开始更新...");
		Log.log("目标工程路径：" + codeBase);
		Log.log("备份工程路径：" + backUpBase);

		try {
			opDirectory(new File(codeBase));
		} catch (IOException e) {
			Log.log("运行时发生了错误：");
			e.printStackTrace();
		}
		Log.log("更新结束，总共更新了" + updatedCount + "个文件");
	}

	public String codeBase;
	public String backUpBase;
	public String appBase;
	public String srcBase;
	public String resBase;
	public String layoutBase;
	public String valuesBase;
	public String drbBaseHdpi;
	public String drbBaseXhdpi;
	public String drbBaseMdpi;
	public String drbBaseXxdpi;
	public String constantsBase;
	public String manifestBase;
	public String filterPath;
	public String parentPomPath;

	private void initPath() {

		appBase = codeBase + "\\gamenews-app";
		srcBase = appBase + "\\src";
		resBase = appBase + "\\res";
		layoutBase = resBase + "\\layout";
		valuesBase = resBase + "\\values";
		drbBaseHdpi = resBase + "\\drawable-hdpi";
		drbBaseXhdpi = resBase + "\\drawable-xhdpi";
		drbBaseMdpi = resBase + "\\drawable-mdpi";
		drbBaseXxdpi = resBase + "\\drawable-xxhdpi";
		constantsBase = srcBase
				+ "\\main\\java\\com\\yy\\android\\gamenews\\Constants.java";

		manifestBase = appBase + "\\AndroidManifest.xml";
		filterPath = appBase + "\\filters";
		parentPomPath = codeBase + "\\pom.xml";
	}

	private void opDirectory(File file) throws IOException {
		if (!file.isDirectory()) {
			Log.log("输入文件有误！路径：" + file.getAbsolutePath());
			return;
		}
		File childList[] = file.listFiles();
		if (childList != null) {
			for (File child : childList) {
				if (child.isDirectory()) {
					opDirectory(child);
				} else {
					opFile(child);
				}
			}
		}
		opFile(file);
	}

	private int updatedCount;
	private void opFile(File file) throws IOException {

		String path = file.getAbsolutePath();
		List<Config> configList = mConfigReader.getConfigList();
		Log.log("[opFile]路径：" + path);
		for(Config config : configList) {
			if(config.matchPath(path)) {

				String type = config.getType();
				Log.log("type = " + type + " path = " + path);
				if(Config.TYPE_MODIFY.equals(type)) {
					modify(file, config.getMap());
				} else if(Config.TYPE_REPLACE.equals(type)) {
					String sourceFilePath = path.replace(codeBase, backUpBase);
					replace(file, new File(sourceFilePath));
				}
			}
		}
	}

	private void replace(File target, File source) throws IOException {
		if (target == null || source == null) {
			return;
		}
		if (target.isFile()) {
			target.delete();
			target.createNewFile();

			copyFile(target, source);
		} else {
			deleteChild(target);
			copyChild(target, source);
		}
	}

	/**
	 * 用source文件里面的内容替换target 里面的内容
	 * 
	 * @param target
	 * @param source
	 * @throws IOException
	 */
	private void copyFile(File target, File source) throws IOException {
		updatedCount++;
		Log.log("拷贝文件：" + target.getAbsolutePath());
		FileInputStream fis = new FileInputStream(source);
		FileOutputStream fos = new FileOutputStream(target);
		int d = -1;
		while ((d = fis.read()) != -1) {
			fos.write(d);
		}

		fos.flush();
		fos.close();
		fis.close();
	}

	private void copyChild(File target, File source) throws IOException {
		String targetPath = target.getAbsolutePath();
		String sourcePath = source.getAbsolutePath();

		File[] childList = source.listFiles();
		if (childList != null) {
			for (File child : childList) {
				if (child.isFile()) {
					String filePath = child.getAbsolutePath();
					filePath = filePath.replace(sourcePath, targetPath);
					File file = new File(filePath);
					file.createNewFile();

					copyFile(file, child);
				}
			}
		}
	}

	private void deleteChild(File file) {
		if (file.isDirectory()) {
			File[] childList = file.listFiles();
			if (childList != null) {
				for (File child : childList) {
					if (child.isDirectory()) {
						deleteChild(child);
					}
					child.delete();
				}
			}
		}
	}

	private void modify(File file, KeyMap<String, String> valueMap)
			throws IOException {
		if (file == null || !file.exists()) {
			return;
		}

		if (file.isFile()) {
			String content = readFileContent(file);
			String contentReplaced = modifyContent(content, valueMap);
			if (!content.equals(contentReplaced)) {

				writeFileContent(file, contentReplaced);
			}
		} else {
			File childList[] = file.listFiles();
			if (childList != null) {
				for (File child : childList) {
					if (child.isFile()) {
						String content = readFileContent(child);
						String contentReplaced = modifyContent(content,
								valueMap);
						if (!content.equals(contentReplaced)) {

							writeFileContent(child, content);
						} else {

							// log("文件：" +
							// child.getAbsolutePath() + "没有变更");
						}
					} else {
						modify(child, valueMap);
					}
				}
			}
		}
	}

	private String modifyContent(String content, KeyMap<String, String> valueMap) {
		List<String> keys = valueMap.keySet();
		for (String key : keys) {
			if (content.contains(key)) {
				String value = valueMap.get(key);
				Log.log("修改 Key = " + key + ", value = " + value);
				content = content.replace(key, value);
			} else {
				Log.log("cannot find the key:" + key);
			}
		}
		return content;
	}

	private String readFileContent(File file) throws IOException {
		if (file == null || !file.exists() || !file.isFile()) {
			return "";
		}

		StringBuilder builder = new StringBuilder();

		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), "UTF-8"));
		String s = null;
		int count = 0;
		while ((s = br.readLine()) != null) {
			if (count == 0) {
				builder.append(s);
			} else {
				builder.append("\n" + s);
			}
			count++;
		}
		br.close();

		return builder.toString();
	}

	private void writeFileContent(File file, String content) throws IOException {
		Log.log("更改文件：" + file.getAbsolutePath());
		updatedCount++;
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8"));
		bw.write(content);
		bw.flush();
		bw.close();
	}
}
