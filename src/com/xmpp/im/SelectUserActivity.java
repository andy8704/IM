package com.xmpp.im;

import java.util.List;

import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.ad.util.ToastUtil;
import com.xmpp.im.adapter.UserMultiSelectListAdapter;
import com.xmpp.im.client.util.ImUtil;
import com.xmpp.im.client.util.XmppTool;
import com.xmpp.im.model.UserModel;
import com.xmpp.im.util.IMCommDefine;

/**
 * 
 * 
 * @类名称: SelectUserActivity
 * @描述: 选择用户
 * @开发者: andy.xu
 * @时间: 2014-8-29 下午2:43:13
 * 
 */
public class SelectUserActivity extends IMBasicActivity {

	private TextView mBackBtn = null;
	private TextView mFinishBtn = null;
	private ListView mListView = null;
	private List<UserModel> mUserList = null;
	private UserMultiSelectListAdapter mAdapter = null;
	private String mChatRoomName = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.select_multiuser_activity);

		mChatRoomName = getIntent().getStringExtra(IMCommDefine.intent_data);

		mUserList = XmppTool.getAllUser();
		initUI();
		initListener();
	}

	private void initUI() {
		mBackBtn = (TextView) findViewById(R.id.back_btn_id);
		mListView = (ListView) findViewById(R.id.listview_id);
		mFinishBtn = (TextView) findViewById(R.id.create_btn_id);

		mAdapter = new UserMultiSelectListAdapter(this, mUserList);
		mListView.setAdapter(mAdapter);
	}

	private void initListener() {

		mBackBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		mFinishBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onCreateGroup();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (null != mUserList)
			mUserList.clear();
	}

	private void onCreateGroup() {
		MultiUserChat multiChat = XmppTool.createRoom(mChatRoomName, "");
		if (null != multiChat) {
			List<UserModel> addChatRoomList = mAdapter.getSelectUser();
			if (null != addChatRoomList && !addChatRoomList.isEmpty()) {

				String name = StringUtils.parseName(XmppTool.getConnection().getUser());
				XmppTool.inviteUsersToChatRoom(multiChat, addChatRoomList, name + "邀请您加入" + mChatRoomName);
				ToastUtil.onShowToast(getBaseContext(), getString(R.string.im_toast_create_chatroom_success_str));
				addChatRoomList.clear();
			}

			ImUtil.onSendChatRoomChange(getApplicationContext());
			setResult(RESULT_OK);
			finish();
		} else {
			ToastUtil.onShowToast(getBaseContext(), getString(R.string.im_toast_create_chatroom_fail_str));
		}
	}

}
