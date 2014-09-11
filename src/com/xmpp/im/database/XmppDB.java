package com.xmpp.im.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.xmpp.im.database.DBHelper.ContactGroup;
import com.xmpp.im.database.DBHelper.UserMessage;
import com.xmpp.im.model.ContactGroupModel;
import com.xmpp.im.model.MessageModel;

/**
 * 
 * @ClassName: MinaDB
 * @Description: 数据库管理类
 * @author andy.xu
 * @date 2014-2-28 下午9:55:28
 * 
 */
public class XmppDB {

	private DBHelper mDBHelper;
	private Context mContext;

	public XmppDB(final Context context) {
		mContext = context;
		mDBHelper = new DBHelper(context);
	}

	public XmppDB(final Context context, final String DBName, final int dbVersion) {
		mContext = context;
		mDBHelper = new DBHelper(context, DBName, null, dbVersion);
	}

	/**
	 * 取得可读数据库
	 * 
	 * @return
	 */
	private SQLiteDatabase getReadableDatabase() {
		SQLiteDatabase sdb = mDBHelper.getReadableDatabase();
		return sdb;
	}

	/**
	 * 取得可写数据库
	 * 
	 * @return
	 */
	private SQLiteDatabase getWritableDatabase() {
		SQLiteDatabase sdb = mDBHelper.getWritableDatabase();
		return sdb;
	}

	/**
	 * 
	 * @描述:添加一个新的分组
	 * @参数 @param userId
	 * @参数 @param groupName
	 * @返回值 void
	 * @异常
	 */
	public void onAddGroup(final String userId, final String groupName) {
		if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(groupName))
			return;

		if (isGroupExist(userId, groupName))
			return;

		SQLiteDatabase db = getWritableDatabase();
		if (null == db)
			return;

		ContentValues value = new ContentValues();
		value.put(ContactGroup.COLUMN_ID, userId);
		value.put(ContactGroup.COLUMN_GROUP_NAME, groupName);

		db.insert(ContactGroup.TB_NAME, null, value);
		db.close();
	}

	public void onDeleteGroup(final String userId, final String groupName) {
		if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(groupName))
			return;

		SQLiteDatabase db = getWritableDatabase();
		if (null == db)
			return;

		db.delete(ContactGroup.TB_NAME, ContactGroup.COLUMN_ID + " = '" + userId + "' " + " and " + ContactGroup.COLUMN_GROUP_NAME + " = '" + groupName + "' ",
				null);
		db.close();
	}

	public boolean isGroupExist(final String userId, final String groupName) {
		if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(groupName))
			return false;

		SQLiteDatabase db = getReadableDatabase();
		if (null == db)
			return false;

		String whereStr = ContactGroup.COLUMN_ID + " = '" + userId + "' " + " and " + ContactGroup.COLUMN_GROUP_NAME + " = '" + groupName + "' ";
		Cursor cursor = db.query(ContactGroup.TB_NAME, null, whereStr, null, null, null, null);
		if (null != cursor) {
			if (cursor.getCount() > 0) {
				cursor.close();
				db.close();
				return true;
			}
		}

		db.close();
		return false;
	}

	public List<ContactGroupModel> onGetUserGroup(final String userId) {
		if (TextUtils.isEmpty(userId))
			return null;

		SQLiteDatabase db = getReadableDatabase();
		if (null == db)
			return null;

		String whereStr = ContactGroup.COLUMN_ID + " = '" + userId + "' ";
		Cursor cursor = db.query(ContactGroup.TB_NAME, null, whereStr, null, null, null, null);
		if (null != cursor) {
			List<ContactGroupModel> groupList = new ArrayList<ContactGroupModel>(cursor.getCount());
			while (cursor.moveToNext()) {
				String name = cursor.getString(cursor.getColumnIndex(ContactGroup.COLUMN_GROUP_NAME));
				if (!TextUtils.isEmpty(name)) {
					ContactGroupModel model = new ContactGroupModel();
					model.name = name;
					groupList.add(model);
				}
			}
			cursor.close();
			db.close();
			return groupList;
		}
		db.close();
		return null;
	}

	// /////////////////////////////////////////////////////////////////////
	// ////////信息的操作

	public void onAddMessage(final MessageModel msg) {
		if (null == msg)
			return;

		SQLiteDatabase db = getWritableDatabase();
		if (null == db)
			return;

		ContentValues value = msg.onGetDB();
		if (null == value) {
			db.close();
			return;
		}
		db.insert(UserMessage.TB_NAME, null, value);
		db.close();
	}

	public void onDeleteMessage(final MessageModel msg) {
		if (null == msg)
			return;

		SQLiteDatabase db = getWritableDatabase();
		if (null == db)
			return;

		String whereStr = UserMessage.ID + " = " + msg.id;
		db.delete(UserMessage.ID, whereStr, null);
		db.close();
	}

	private SearchModel mMessageSearchKey = null;

	public List<MessageModel> onGetMessageList(final SearchModel key) {

		if (null == key)
			return null;

		mMessageSearchKey = key;

		SQLiteDatabase db = getReadableDatabase();
		if (null == db)
			return null;

		String limiter = key.nPageIndex * key.nPageSize + " , " + key.nPageSize;
		Cursor cursor = db.query(UserMessage.TB_NAME, null, key.whereStr, null, null, null, UserMessage.COLUMN_TIME + " desc ", limiter);

		List<MessageModel> msgList = null;
		if (null != cursor) {
			msgList = new ArrayList<MessageModel>(cursor.getCount());
			while (cursor.moveToNext()) {
				MessageModel msg = new MessageModel().onSetDB(cursor);
				if (null != msg) {
					msgList.add(0, msg);
				}
			}
			cursor.close();
			db.close();
			return msgList;
		}

		db.close();
		return null;
	}

	public List<MessageModel> onGetNextPageMessage() {

		if (null == mMessageSearchKey)
			return null;

		mMessageSearchKey.nPageIndex = mMessageSearchKey.nPageIndex + 1;
		return onGetMessageList(mMessageSearchKey);
	}

}
