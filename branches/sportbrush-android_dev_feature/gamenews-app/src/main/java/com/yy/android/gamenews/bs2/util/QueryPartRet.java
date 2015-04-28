package com.yy.android.gamenews.bs2.util;


public class QueryPartRet extends CallRet {
	String bucket;
	String fileName;
	String uploadid;
	long partNumber;
	long currentSize;
	String zoneUrl;

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUploadid() {
		return uploadid;
	}

	public void setUploadid(String uploadid) {
		this.uploadid = uploadid;
	}

	public long getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(long partNumber) {
		this.partNumber = partNumber;
	}

	public long getCurrentSize() {
		return currentSize;
	}

	public void setCurrentSize(long currentSize) {
		this.currentSize = currentSize;
	}

	public String getZoneUrl() {
		return zoneUrl;
	}

	public void setZoneUrl(String zoneUrl) {
		this.zoneUrl = zoneUrl;
	}

	public QueryPartRet() {
	}

	public QueryPartRet(HttpResponse httpResponse) {
		this.setHttpResponse(httpResponse);
	}

	public void setHttpResponse(HttpResponse httpResponse) {
		super.setHttpResponse(httpResponse);
//		if (this.getCode() == 200) {
//			String json = httpResponse.getRspDataString();
//			JSONObject obj = (JSONObject) JSONValue.parse(json.trim());
//			bucket = (String) obj.get("bucket");
//			fileName = (String) obj.get("filename");
//			zoneUrl = (String) obj.get("zone");
//			uploadid = (String) obj.get("uploadid");
//			partNumber = (Long) obj.get("partnumber");
//			currentSize = (Long) obj.get("currentsize");
//		}
	}
}
