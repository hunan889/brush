package com.duowan.android.base.net;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import android.content.Context;

/**
 * @author yy:909012690@lishaoqi
 * @version 创建时间：2014-3-10 下午4:37:05
 */
public class VolleyErrorHelper {

	public static String getMessage(Context context, Object error) {
		if (error instanceof TimeoutError) {
			return "暂时无法连接网络，请检测网络环境"; //"请求网络超时";
		} else if (isServerProblem(error)) {
			return handleServerError(error, context);
		} else if (isNetworkProblem(error)) {
			return "当前网络不可用，请检测网络环境";
		}
		return "请求超时，请检测网络环境";
	}

	public static boolean isTimeoutError(Object error) {
		return error instanceof TimeoutError;
	}

	/**
	 * Determines whether the error is related to network
	 * 
	 * @param error
	 * @return
	 */
	public static boolean isNetworkProblem(Object error) {
		return (error instanceof NetworkError) || (error instanceof NoConnectionError);
	}

	/**
	 * Determines whether the error is related to server
	 * 
	 * @param error
	 * @return
	 */
	public static boolean isServerProblem(Object error) {
		return (error instanceof ServerError) || (error instanceof AuthFailureError);
	}

	/**
	 * Handles the server error, tries to determine whether to show a stock
	 * message or to show a message retrieved from the server.
	 * 
	 * @param err
	 * @param context
	 * @return
	 */
	public static String handleServerError(Object err, Context context) {
		VolleyError error = (VolleyError) err;

		NetworkResponse response = error.networkResponse;

		if (response != null)
			return "连接服务器失败: " + String.valueOf(response.statusCode);

		return "连接服务器失败";
	}
}
