package com.yy.android.gamenews.exception;

public class UserException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1350822718856893223L;
	
	
	private int code;
	private int subcode;
	
	public UserException(int code, int subCode, String errMsg){
		super(errMsg);
		this.code = code;
		this.subcode = subCode;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public int getSubcode() {
		return subcode;
	}
	public void setSubcode(int subcode) {
		this.subcode = subcode;
	}


}
