package com.xmpp.im.model;

import java.io.Serializable;

import org.jivesoftware.smack.packet.Message.Type;

import com.xmpp.im.database.DBHelper.UserMessage;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

/**
 * 
 * 
 * @类名称: MessageModel
 * @描述: 消息的内容
 * @开发者: andy.xu
 * @时间: 2014-8-28 下午2:20:01
 * 
 */
public class MessageModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4907538562614336176L;

	public int id;
	public String userIcon;
	public String fromUser;
	public String userName;
	public String toUser;
	public String msgContent;
	public Type chatType;
	public long time;
	public int count;

	/**
	 * 0: In 1: out
	 */
	public int fromFlag;

	public MessageModel() {
		fromUser = null;
		msgContent = null;
		fromFlag = 0;
	}

	@Override
	public boolean equals(Object o) {
		if (null == o)
			return false;

		return TextUtils.equals(fromUser, ((MessageModel) o).fromUser);
	}

	public ContentValues onGetDB() {
		ContentValues value = new ContentValues();
		value.put(UserMessage.COLUMN_FROM_USER, fromUser);
		value.put(UserMessage.COLUMN_TO_USER, toUser);
		value.put(UserMessage.COLUMN_USER_ICON, userIcon);
		value.put(UserMessage.COLUMN_FROM_FALG, fromFlag);
		value.put(UserMessage.COLUMN_TIME, time);
		value.put(UserMessage.COLUMN_MSG_CONTENT, msgContent);
		return value;
	}

	public MessageModel onSetDB(final Cursor cursor) {
		if (null == cursor)
			return null;

		id = cursor.getInt(cursor.getColumnIndex(UserMessage.ID));
		fromUser = cursor.getString(cursor.getColumnIndex(UserMessage.COLUMN_FROM_USER));
		toUser = cursor.getString(cursor.getColumnIndex(UserMessage.COLUMN_TO_USER));
		userIcon = cursor.getString(cursor.getColumnIndex(UserMessage.COLUMN_USER_ICON));
		fromFlag = cursor.getInt(cursor.getColumnIndex(UserMessage.COLUMN_FROM_FALG));
		time = cursor.getLong(cursor.getColumnIndex(UserMessage.COLUMN_TIME));
		msgContent = cursor.getString(cursor.getColumnIndex(UserMessage.COLUMN_MSG_CONTENT));
		return this;
	}

}
