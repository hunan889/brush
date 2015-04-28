package com.yy.brushswitcher;

import java.io.File;

public class Config {
	public static final String TYPE_MODIFY = "modify";
	public static final String TYPE_REPLACE = "replace";
	private String type;
	private String path;
	private KeyMap<String, String> map;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public KeyMap<String, String> getMap() {
		return map;
	}

	public void setMap(KeyMap<String, String> map) {
		this.map = map;
	}

	// 匹配 *\XXX
	private static final String REG_MATCH_PRE = ".*\\*\\\\.*";
	// 匹配 XXX\*
	private static final String REG_MATCH_SUF = ".*\\\\\\*";

	public boolean matchPath(String targetPath) {
		if (path == null) {
			return false;
		}

		if (path.matches(REG_MATCH_PRE)) {
			int index = path.lastIndexOf("*\\");
			String fileName = path.substring(index + 2);
			File file = new File(targetPath);
			if (fileName != null && fileName.equals(file.getName())) {
				return true;
			}
		} else if (path.matches(REG_MATCH_SUF)) {
			int index = path.lastIndexOf("\\*");
			String folderName = path.substring(0, index);

			if (targetPath != null && targetPath.startsWith(folderName)) {
				return true;
			}
		} else if (path.equals(targetPath)) {
			return true;
		}

		return false;
	}

	public static void main(String args[]) {
		String s = "*\\pom.xml";
		System.out.println(s.matches(REG_MATCH_PRE));

		if (s.matches(REG_MATCH_PRE)) {
			int index = s.lastIndexOf("*\\");
			String fileName = s.substring(index + 2);
			System.out.println(fileName);
		}
	}
	
	@Override
	public String toString() {
		String message = "type: " + type + ", KeyMap: " + map + ", path:" + path;
		return message;
	}
}
