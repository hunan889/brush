package com.yy.brushswitcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ConfigReader {

	private void addToMap(NodeList mapList, KeyMap<String, String> map) {
		if (map == null || mapList == null) {
			return;
		}
		for (int i = 0; i < mapList.getLength(); i++) {
			Element node = (Element) mapList.item(i);
			Node key = node.getElementsByTagName("key").item(0);
			Node value = node.getElementsByTagName("value").item(0);

			String keyStr = key.getTextContent();
			String valueStr = value.getTextContent();
			map.put(keyStr == null ? "" : keyStr.trim(), valueStr == null ? ""
					: valueStr.trim());
		}
	}

	public static final int TYPE_ANDROID_MANIFEST = 1;
	public static final int TYPE_CHILD_POM = 2;
	public static final int TYPE_PARENT_POM = 3;
	public static final int TYPE_LAYOUT = 4;
	public static final int TYPE_SOURCE = 5;
	private final String CONFIG_FILE = "config.xml";

	private Element mRoot;

	private KeyMap<String, String> mLayoutMap;
	private KeyMap<String, String> mSrcMap;
	private KeyMap<String, String> mManifestMap;
	private KeyMap<String, String> mChildPomMap;
	private KeyMap<String, String> mParentPomMap;

	// public String codeBase;
	// public String backUpBase;
	// public String appBase;
	// public String srcBase;
	// public String resBase;
	// public String layoutBase;
	// public String valuesBase;
	// public String drbBaseHdpi;
	// public String drbBaseXhdpi;
	// public String drbBaseMdpi;
	// public String drbBaseXxdpi;
	// public String constantsBase;
	// public String manifestBase;
	// public String filterPath;
	// public String parentPomPath;
	//
	// private void initPath() {
	// Element codeBaseElement = (Element) selectSingleNode(
	// "/configs/path[@name='code_base']", mRoot);
	// codeBase = codeBaseElement.getTextContent();
	//
	// Element backupBaseElement = (Element) selectSingleNode(
	// "/configs/path[@name='backup_base']", mRoot);
	// backUpBase = backupBaseElement.getTextContent();
	//
	// appBase = codeBase + "\\gamenews-app";
	// srcBase = appBase + "\\src";
	// resBase = appBase + "\\res";
	// layoutBase = resBase + "\\layout";
	// valuesBase = resBase + "\\values";
	// drbBaseHdpi = resBase + "\\drawable-hdpi";
	// drbBaseXhdpi = resBase + "\\drawable-xhdpi";
	// drbBaseMdpi = resBase + "\\drawable-mdpi";
	// drbBaseXxdpi = resBase + "\\drawable-xxhdpi";
	// constantsBase = srcBase
	// + "\\main\\java\\com\\yy\\android\\gamenews\\Constants.java";
	//
	// manifestBase = appBase + "\\AndroidManifest.xml";
	// filterPath = appBase + "\\filters";
	// parentPomPath = codeBase + "\\pom.xml";
	// }

	public boolean init(String path, String backupPath) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringElementContentWhitespace(true);

		DocumentBuilder db;
		try {
			db = factory.newDocumentBuilder();
			File file = new File(backupPath + "\\" + CONFIG_FILE);
			if (file.exists()) {
				Document xmldoc = db.parse(file);
				mRoot = xmldoc.getDocumentElement();
			} else {
				Log.log("配置文件不存在！path = " + file.getAbsolutePath());
			}

			initConfigs(path);
			return true;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	// public static void main(String args[]) {
	// ConfigReader reader = new ConfigReader();
	// reader.init(".");
	// reader.initConfigs();
	// Log.log(reader.mConfigList);
	//
	// for(Config config : reader.mConfigList) {
	// Log.log("path:" + config.getPath());
	// Log.log("type:" + config.getType());
	// }
	// }

	private List<Config> mConfigList = new ArrayList<Config>();

	public List<Config> getConfigList() {
		return mConfigList;
	}

	private void initConfigs(String codebase) {
		NodeList configList = selectNodes("/configs/config", mRoot);
		mConfigList.clear();
		for (int i = 0; i < configList.getLength(); i++) {
			Element node = (Element) configList.item(i);
			Node path = node.getElementsByTagName("path").item(0);
			Node type = node.getElementsByTagName("type").item(0);
			NodeList configMap = node.getElementsByTagName("map");

			if(path == null) {
				Log.log("path 为空，请检查配置文件");
				continue;
			} 
			if(type == null) {
				Log.log("type 为空，请检查配置文件");
				continue;
			}
			String pathStr = path.getTextContent();
			pathStr = codebase + pathStr;

			KeyMap<String, String> map = new KeyMap<String, String>();

			addToMap(configMap, map);

			Config config = new Config();
			config.setPath(pathStr);
			config.setType(type.getTextContent());
			config.setMap(map);
			mConfigList.add(config);
		}
		
		if(mConfigList.size() == 0) {
			Log.log("mConfigList.size == 0");
		}
		
		for(Config config : mConfigList) {
			Log.log(config.toString());
		}
	}

	//
	// public KeyMap<String, String> getConfigMap(int type) {
	// KeyMap<String, String> map = null;
	// NodeList configMap = null;
	// switch (type) {
	// case TYPE_ANDROID_MANIFEST: {
	// if (mManifestMap != null) {
	// return mManifestMap;
	// }
	// map = mManifestMap = new KeyMap<String, String>();
	// configMap = selectNodes(
	// "/configs/config[@name='AndroidManifest']/map", mRoot);
	// break;
	// }
	// case TYPE_CHILD_POM: {
	// if (mChildPomMap != null) {
	// return mChildPomMap;
	// }
	// map = mChildPomMap = new KeyMap<String, String>();
	// configMap = selectNodes("/configs/config[@name='child_pom']/map",
	// mRoot);
	// break;
	// }
	// case TYPE_PARENT_POM: {
	// if (mParentPomMap != null) {
	// return mParentPomMap;
	// }
	// map = mParentPomMap = new KeyMap<String, String>();
	// configMap = selectNodes("/configs/config[@name='parent_pom']/map",
	// mRoot);
	// break;
	// }
	// case TYPE_LAYOUT: {
	// if (mLayoutMap != null) {
	// return mLayoutMap;
	// }
	// map = mLayoutMap = new KeyMap<String, String>();
	// configMap = selectNodes("/configs/config[@name='layout']/map",
	// mRoot);
	// break;
	// }
	// case TYPE_SOURCE: {
	// if (mSrcMap != null) {
	// return mSrcMap;
	// }
	// map = mSrcMap = new KeyMap<String, String>();
	// configMap = selectNodes("/configs/config[@name='source']/map",
	// mRoot);
	// break;
	// }
	// }
	// addToMap(configMap, map);
	//
	// return map;
	// }

	public static void output(Node node) {// 将node的XML字符串输出到控制台
		TransformerFactory transFactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = transFactory.newTransformer();
			transformer.setOutputProperty("encoding", "utf-8");
			transformer.setOutputProperty("indent", "yes");
			DOMSource source = new DOMSource();
			source.setNode(node);
			StreamResult result = new StreamResult();
			result.setOutputStream(System.out);

			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	public static Node selectSingleNode(String express, Object source) {// 查找节点，并返回第一个符合条件节点
		Node result = null;
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		try {
			result = (Node) xpath
					.evaluate(express, source, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return result;
	}

	public NodeList selectNodes(String express, Object source) {// 查找节点，返回符合条件的节点集。
		NodeList result = null;
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		try {
			result = (NodeList) xpath.evaluate(express, source,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static void saveXml(String fileName, Document doc) {// 将Document输出到文件
		TransformerFactory transFactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = transFactory.newTransformer();
			transformer.setOutputProperty("indent", "yes");
			DOMSource source = new DOMSource();
			source.setNode(doc);
			StreamResult result = new StreamResult();
			result.setOutputStream(new FileOutputStream(fileName));

			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
