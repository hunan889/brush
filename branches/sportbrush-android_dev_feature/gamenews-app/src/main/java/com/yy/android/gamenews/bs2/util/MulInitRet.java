package com.yy.android.gamenews.bs2.util;

import java.io.UnsupportedEncodingException;

public class MulInitRet extends CallRet {
	String zoneHost;
	String uploadid;
	String chunk;

	public MulInitRet() {
	}

	public MulInitRet(HttpResponse httpResponse)
			throws UnsupportedEncodingException {
		this.setHttpResponse(httpResponse);
	}

	public void setHttpResponse(HttpResponse httpResponse) {
		super.setHttpResponse(httpResponse);
//		if (this.getCode() == 200) {
//			String json = httpResponse.getRspDataString();
//			System.out.println("json:" + json);
//			JSONObject obj = (JSONObject) JSONValue.parse(json.trim());
//			System.out.println("obj:" + obj);
//			zoneHost = (String) obj.get("zone");
//			uploadid = (String) obj.get("uploadid");
//			chunk = (String) obj.get("chunk");
//		}
	}

	public String getZoneHost() {
		return zoneHost;
	}

	public void setZoneHost(String zoneHost) {
		this.zoneHost = zoneHost;
	}

	public String getUploadid() {
		return uploadid;
	}

	protected void setUploadid(String uploadid) {
		this.uploadid = uploadid;
	}

	public String getChunk() {
		return chunk;
	}

	protected void setChunk(String chunk) {
		this.chunk = chunk;
	}

}
