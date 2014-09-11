package com.xmpp.im.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xmpp.im.model.ContactGroupModel;
import com.xmpp.im.model.MessageModel;
import com.xmpp.im.model.UserModel;
import com.xmpp.im.util.IMCommDefine;

/**
 * 
 * 
 * @类名称: ImUtil
 * @描述: Im基本工具
 * @开发者: andy.xu
 * @时间: 2014-8-28 上午11:45:29
 * 
 */
public class ImUtil {

	/**
	 * 群组存数
	 */
	private static List<ContactGroupModel> mContactGroupList = null;

	/**
	 * 
	 * @描述:获取所有的群组
	 * @参数 @return
	 * @返回值 List<ContactGroupModel>
	 * @异常
	 */
	public static List<ContactGroupModel> getContactGroup() {
		return mContactGroupList;
	}

	/**
	 * 
	 * @描述:设置当前的群组
	 * @参数 @param data
	 * @返回值 void
	 * @异常
	 */
	public static void onSetContactGroup(final List<ContactGroupModel> data) {
		if (null == mContactGroupList)
			mContactGroupList = new ArrayList<ContactGroupModel>();
		mContactGroupList.clear();
		mContactGroupList.addAll(data);
	}

	/**
	 * 存储所有的用户
	 */
	private static Map<String, UserModel> mAllUserMap = new HashMap<String, UserModel>();

	public static void onSetAllUser(final Map<String, UserModel> data) {
		mAllUserMap.clear();
		mAllUserMap.putAll(data);
	}

	public static UserModel onGetUserModel(final String userId) {
		if (TextUtils.isEmpty(userId))
			return null;

		if (mAllUserMap.containsKey(userId))
			return mAllUserMap.get(userId);
		return null;
	}

	/**
	 * 当前正在回话的用户的Id
	 */
	private static String mLookUserId = null;

	/**
	 * 
	 * @描述:设置当前会话用户的id
	 * @参数 @param userId
	 * @返回值 void
	 * @异常
	 */
	public static void onSetCurChatUserId(final String userId) {
		mLookUserId = userId;
	}

	/**
	 * 
	 * @描述: 返回当前会话的用户的Id
	 * @参数 @return
	 * @返回值 String
	 * @异常
	 */
	public static String onGetCurChatUserId() {
		return mLookUserId;
	}

	/**
	 * 
	 * @描述:发送群组发生改变的广播
	 * @参数 @param context
	 * @返回值 void
	 * @异常
	 */
	public static void onSendChatRoomChange(final Context context) {
		if (null == context)
			return;

		Intent intent = new Intent(IMCommDefine.broadcast_chatroom_change);
		context.sendBroadcast(intent);
	}

	/**
	 * 存储离线消息
	 */
	private static Map<String, List<MessageModel>> mOffLineMessageMap = new HashMap<String, List<MessageModel>>();

	/**
	 * 
	 * @描述:设置离线消息
	 * @参数 @param data
	 * @返回值 void
	 * @异常
	 */
	public static void onSetOfflineMsg(final Map<String, List<MessageModel>> data) {
		if (null == data || data.isEmpty())
			return;

		mOffLineMessageMap.clear();
		mOffLineMessageMap.putAll(data);
	}

	/**
	 * 
	 * @描述: 获取离线消息
	 * @参数 @return
	 * @返回值 Map<String,List<MessageModel>>
	 * @异常
	 */
	public static Map<String, List<MessageModel>> onGetOfflineMsg() {
		return mOffLineMessageMap;
	}

	public static List<MessageModel> onGetUserOfflineMsg(final String userId) {
		if (TextUtils.isEmpty(userId))
			return null;

		if (null == mOffLineMessageMap || mOffLineMessageMap.isEmpty())
			return null;

		return mOffLineMessageMap.get(userId);
	}

	/**
	 * 存储未读信息
	 */
	private static Map<String, List<MessageModel>> mUnReadMsg = new HashMap<String, List<MessageModel>>();

	/**
	 * 
	 * @描述: 添加一条未读的消息
	 * @参数 @param msg
	 * @返回值 void
	 * @异常
	 */
	public static void addUnReadMsg(final MessageModel msg) {
		if (null == msg)
			return;

		if (mUnReadMsg.containsKey(msg.fromUser))
			mUnReadMsg.get(msg.fromUser).add(msg);
		else {
			List<MessageModel> msgList = new ArrayList<MessageModel>();
			msgList.add(msg);
			mUnReadMsg.put(msg.fromUser, msgList);
		}
	}

	/**
	 * 
	 * @描述: 获取该人的所有未读信息
	 * @参数 @param userId
	 * @参数 @return
	 * @返回值 List<MessageModel>
	 * @异常
	 */
	public static List<MessageModel> getUnReadMsg(final String userId) {
		if (null == userId)
			return null;

		return mUnReadMsg.get(userId);
	}
}
