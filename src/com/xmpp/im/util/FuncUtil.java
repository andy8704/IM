package com.xmpp.im.util;

import java.util.Stack;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Pair;

/**
 * 
 * 
 * @类名称: FuncUtil
 * @描述: 功能工具类
 * @开发者: andy.xu
 * @时间: 2014-8-26 上午10:11:13
 * 
 */
public class FuncUtil {

	private static Stack<Pair<String, Activity>> mActivityList = null;

	/**
	 * 添加一个到堆栈
	 * 
	 * @param activity
	 */
	public static void addActivity(final Activity activity) {
		if (null == mActivityList)
			mActivityList = new Stack<Pair<String, Activity>>();
		Pair<String, Activity> data = new Pair<String, Activity>(activity.getClass().getName(), activity);
		mActivityList.add(data);
	}

	public static void popActivity(final Activity activity) {
		if (null != mActivityList && !mActivityList.isEmpty()) {
			for (int i = mActivityList.size() - 1; i >= 0; i--) {
				Pair<String, Activity> ele = mActivityList.get(i);
				if (null != ele) {
					if (TextUtils.equals(ele.first, activity.getClass().getName())) {
						mActivityList.remove(i);
						return;
					}
				}
			}
		}
	}

	/**
	 * 销毁所有存在的activity
	 */
	public static void destoryAllActivity() {
		if (null != mActivityList && mActivityList.isEmpty() == false) {
			for (int i = 0; i < mActivityList.size(); i++) {
				Pair<String, Activity> ele = mActivityList.get(i);
				if (null != ele) {
					ele.second.finish();
				}
			}
			mActivityList.clear();
			// System.exit(0);
		}
	}
	
	public static String onGetMsgFormatTime(final long timeMill){
		return null;
	}
}
