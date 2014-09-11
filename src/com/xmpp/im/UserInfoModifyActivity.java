package com.xmpp.im;

import org.jivesoftware.smack.XMPPConnection;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ad.util.ToastUtil;
import com.xmpp.im.client.util.XmppTool;
import com.xmpp.im.database.XmppDB;
import com.xmpp.im.model.ContactGroupModel;
import com.xmpp.im.model.UserModel;
import com.xmpp.im.util.IMCommDefine;

/**
 * 
 * 
 * @类名称: UserInfoModifyActivity
 * @描述: 用户的信息修改
 * @开发者: andy.xu
 * @时间: 2014-9-3 下午12:15:12
 * 
 */
public class UserInfoModifyActivity extends IMBasicActivity implements OnClickListener {

	private TextView mBackBtn = null;
	private TextView mOkBtn = null;
	private EditText mNickNameView = null;
	private TextView mGroupView = null;
	private TextView mDeleteBtn = null;
	private LinearLayout mGroupSetBtn = null;
	private UserModel mUserModel = null;
	private ContactGroupModel mUserGroupModel = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userinfo_modify_activity);

		mUserModel = (UserModel) getIntent().getSerializableExtra(IMCommDefine.intent_data);

		if (null == mUserGroupModel) {
			mUserGroupModel = new ContactGroupModel();
			mUserGroupModel.name = mUserModel.groupName;
		}

		initUI();
		initListener();
	}

	private void initUI() {

		mBackBtn = (TextView) findViewById(R.id.back_btn_id);
		mOkBtn = (TextView) findViewById(R.id.send_btn_id);

		mNickNameView = (EditText) findViewById(R.id.nickname_view_id);
		mGroupView = (TextView) findViewById(R.id.group_view_id);
		mGroupSetBtn = (LinearLayout) findViewById(R.id.set_group_view_id);
		mDeleteBtn = (TextView) findViewById(R.id.delete_btn_id);

		if (null != mUserModel) {
			mNickNameView.setText(mUserModel.nickName);
			mGroupView.setText(mUserModel.groupName);
		}
	}

	private void initListener() {

		mBackBtn.setOnClickListener(this);
		mOkBtn.setOnClickListener(this);
		mGroupSetBtn.setOnClickListener(this);
		mDeleteBtn.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn_id:
			finish();
			break;
		case R.id.send_btn_id:
			onModifyCardInfo();
			break;
		case R.id.set_group_view_id:
			Intent intent = new Intent(UserInfoModifyActivity.this, SelectGroupActivity.class);
			intent.putExtra(IMCommDefine.intent_data, mUserGroupModel);
			startActivityForResult(intent, 0x1000);
			break;
		case R.id.delete_btn_id:
			break;
		}
	}

	private void onModifyCardInfo() {
		String nickName = mNickNameView.getText().toString();

		if (!TextUtils.isEmpty(nickName)) {
			// UserModel user = new UserModel();
			// user.nickName = nickName;
			// XmppTool.onSetUserModel(mUserModel.userId, user);
			XmppTool.onSetNickName(mUserModel.userId, nickName);
		}

		String curGroupName = mGroupView.getText().toString();
		if (!TextUtils.isEmpty(curGroupName) && !TextUtils.equals(curGroupName, mUserModel.groupName)) {
			XMPPConnection connect = XmppTool.getConnection();
			if (null != connect) {
				XmppTool.onChangeUserGroup(mUserModel.userId, curGroupName);
				XmppDB db = ImApplication.onGetInstance().onGetDB();
				if (null != db) {
					db.onAddGroup(XmppTool.onGetUserId(), mUserModel.groupName);
				}
			}
		}

		onSendBroadcast();
		ToastUtil.onShowToast(getApplicationContext(), "已保存");
		finish();
	}

	private void onSendBroadcast() {

		Intent intent = new Intent(IMCommDefine.broadcast_group_change);
		sendBroadcast(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 0x1000 && resultCode == RESULT_OK) {
			mUserGroupModel = (ContactGroupModel) data.getSerializableExtra(IMCommDefine.intent_data);
			if (null != mUserGroupModel)
				mGroupView.setText(mUserGroupModel.name);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
