package com.xmpp.im;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ad.util.ToastUtil;
import com.xmpp.im.adapter.UserSearchListAdapter;
import com.xmpp.im.adapter.UserSearchListAdapter.AddUserListener;
import com.xmpp.im.client.util.XmppTool;
import com.xmpp.im.model.UserModel;
import com.xmpp.im.util.IMCommDefine;

/**
 * 
 * 
 * @类名称: ImAddUserActivity
 * @描述: 添加好友
 * @开发者: andy.xu
 * @时间: 2014-8-27 下午4:47:02
 * 
 */
public class ImAddUserActivity extends IMBasicActivity {

	private TextView mBackBtn = null;
	private EditText mInputView = null;
	private Button mSearchBtn = null;
	private ListView mListView = null;
	private List<UserModel> mSearchUserList = null;
	private UserSearchListAdapter mAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.add_friend_activity);

		initUI();
		initListener();
	}

	private void initUI() {

		mBackBtn = (TextView) findViewById(R.id.back_btn_id);
		mInputView = (EditText) findViewById(R.id.editview_id);
		mSearchBtn = (Button) findViewById(R.id.search_btn_id);
		mListView = (ListView) findViewById(R.id.listview_id);

		mSearchUserList = new ArrayList<UserModel>();
		mAdapter = new UserSearchListAdapter(this, mSearchUserList);
		mListView.setAdapter(mAdapter);
	}

	private void initListener() {

		mBackBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		mSearchBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onSearchUserModel(mInputView.getText().toString());
			}
		});

		mAdapter.onSetAddListener(new AddUserListener() {
			@Override
			public void onAdd(UserModel user) {
				addUser(user);
			}
		});
	}

	private void onSearchUserModel(final String searchKey) {
		if (TextUtils.isEmpty(searchKey)) {
			ToastUtil.onShowToast(this, getString(R.string.im_toast_name_empty_str));
			return;
		}

		mSearchUserList.clear();

		onShowProgressDlg();
		List<UserModel> data = XmppTool.searchUsers(searchKey);
		if (null != data && !data.isEmpty()) {
			mSearchUserList.addAll(data);
		}
		mAdapter.notifyDataSetChanged();
		onHideProgressDlg();
	}

	private void addUser(final UserModel user) {
		if (null == user)
			return;

		Intent intent = new Intent(ImAddUserActivity.this, UserVerificateActivity.class);
		intent.putExtra(IMCommDefine.intent_data, user);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private ProgressDialog mProgressDialog = null;
	private String mProgressStr = "努力加载中，请稍后...";

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			default:
				break;
			case 0:
				mProgressDialog = ProgressDialog.show(ImAddUserActivity.this, null, mProgressStr, true);
				break;
			case 1:
				if (null != mProgressDialog)
					mProgressDialog.dismiss();
				break;
			}
		};
	};

	/**
	 * 
	 * @函数名称: 显示进度对话框
	 * @描述:
	 * @参数
	 * @返回值 void
	 * @异常
	 */
	public void onShowProgressDlg() {

		if (null != mHandler)
			mHandler.sendEmptyMessage(0);
	}

	/**
	 * 
	 * @函数名称: 隐藏进度对话框
	 * @描述:
	 * @参数
	 * @返回值 void
	 * @异常
	 */
	public void onHideProgressDlg() {

		if (null != mHandler)
			mHandler.sendEmptyMessage(1);
	}

}
