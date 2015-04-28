package com.yy.android.gamenews;

public class ServerConstants {

	public static interface SUBCODE {

		public static final String KEY = "subcode";

		public static final int GET_USER_INFO_FAIL = 9000; // 无法获取用户信息
		public static final int USER_INVALID = 9001;// 无效用户
		public static final int USER_SIGNED = 9002;// 用户已签到
		public static final int SIGN_RETRY = 9003;// 签到失败，请重试
		public static final int SIGN_TYPE_ERROR = 9004;// 签到类型错误',
		public static final int SIGN_KEY_ERROR = 9005;// 签到验证串错误

		public static final int LOGIN_FAIL = 6506;// 用户未登录
	}

	public static interface MESSAGE {
		public static final String KEY = "msg";
	}
	
	public static interface SERVANT_NAME {
		public static final String GAME = "gamenews";
		public static final String SPORT = "sportnews";
		public static final String AUTO = "autonews";
	}

}
