package com.xmpp.im.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @ClassName: DBHelper
 * @Description: 数据库初始化句柄
 * @author andy.xu
 * @date 2014-2-28 下午8:56:30
 * 
 */
public class DBHelper extends SQLiteOpenHelper {

	// 数据库名称
	private final static String DB_NAME = "adIM.db";
	private final static int DB_VERSION = 1;
	private Context mContext = null;

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);

		mContext = context;
	}

	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		if (null == db)
			return;

		onCreateContactGroupInfoTb(db);
		onCreateUseMessage(db);
	}

	/**
	 * 数据库更新，进行数据继承 & 迁移
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		if (null == db)
			return;

	}

	public interface ContactGroup {

		String TB_NAME = "contactGroup";
		String ID = "id";
		String COLUMN_ID = "userId";
		String COLUMN_GROUP_NAME = "groupName";
	}

	private void onCreateContactGroupInfoTb(SQLiteDatabase db) {
		if (null == db)
			return;

		StringBuffer sqlStr = new StringBuffer();
		sqlStr.append("CREATE TABLE IF NOT EXISTS ");
		sqlStr.append(ContactGroup.TB_NAME);
		sqlStr.append("(");
		sqlStr.append(ContactGroup.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,");
		sqlStr.append(ContactGroup.COLUMN_ID + " TEXT,");
		sqlStr.append(ContactGroup.COLUMN_GROUP_NAME + " TEXT");
		sqlStr.append(")");
		db.execSQL(sqlStr.toString());

//		String indexStr = "create index userid_index on " + ContactGroup.TB_NAME + "(" + ContactGroup.COLUMN_ID + ")";
//		db.execSQL(indexStr);
	}

	public interface UserMessage {

		String TB_NAME = "userMessage";

		String ID = "id";
		/**
		 * 用户的头像
		 */
		String COLUMN_USER_ICON = "userIcon";
		/**
		 * 发送信息的用户ID
		 */
		String COLUMN_FROM_USER = "fromUser";
		/**
		 * 接受信息的用户ID
		 */
		String COLUMN_TO_USER = "toUser";

		/**
		 * 信息的类型 0: In 1: out
		 */
		String COLUMN_FROM_FALG = "fromFlag";
		
		/**
		 * 消息的内容
		 */
		String COLUMN_MSG_CONTENT = "msgContent";

		/**
		 * 发送的时间
		 */
		String COLUMN_TIME = "time";
	}

	private void onCreateUseMessage(SQLiteDatabase db) {
		if (null == db)
			return;

		StringBuffer sqlStr = new StringBuffer();
		sqlStr.append("CREATE TABLE IF NOT EXISTS ");
		sqlStr.append(UserMessage.TB_NAME);
		sqlStr.append("(");
		
		sqlStr.append(UserMessage.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,");
		sqlStr.append(UserMessage.COLUMN_FROM_USER + " TEXT,");
		sqlStr.append(UserMessage.COLUMN_TO_USER + " TEXT,");
		sqlStr.append(UserMessage.COLUMN_USER_ICON + " TEXT,");
		sqlStr.append(UserMessage.COLUMN_FROM_FALG + " INTEGER,");
		sqlStr.append(UserMessage.COLUMN_MSG_CONTENT + " TEXT,");
		sqlStr.append(UserMessage.COLUMN_TIME + " LONG ");
		sqlStr.append(")");
		db.execSQL(sqlStr.toString());
	}
}
