package com.xmpp.im;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ad.util.ToastUtil;
import com.xmpp.im.adapter.ContactGroupManagerAdapter;
import com.xmpp.im.adapter.ContactGroupManagerAdapter.ContactOperListener;
import com.xmpp.im.client.util.XmppTool;
import com.xmpp.im.database.XmppDB;
import com.xmpp.im.model.ContactGroupModel;
import com.xmpp.im.util.IMCommDefine;
import com.xmpp.im.view.AddContactGroupDlg;
import com.xmpp.im.view.AddContactGroupDlg.AddContactGroupListener;
import com.xmpp.im.view.ReNameContactGroupDlg;
import com.xmpp.im.view.ReNameContactGroupDlg.ReNameContactGroupListener;

/**
 * 
 * 
 * @类名称: ContactGroupManagerActivity
 * @描述: 联系人群组管理
 * @开发者: andy.xu
 * @时间: 2014-8-26 下午4:52:19
 * 
 */
public class ContactGroupManagerActivity extends IMBasicActivity {

	private TextView mFinishBtn = null;
	private TextView mAddBtn = null;
	private ListView mListView = null;
	private ContactGroupManagerAdapter mAdapter = null;
	private List<ContactGroupModel> mGroupList = new ArrayList<ContactGroupModel>();
	private boolean bModifyFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_group_manager_activity);

		@SuppressWarnings("unchecked")
		List<ContactGroupModel> temp = (List<ContactGroupModel>) getIntent().getSerializableExtra(IMCommDefine.intent_data);
		if (null != temp && !temp.isEmpty())
			mGroupList.addAll(temp);

		initUI();
		addListener();
	}

	private void initUI() {
		mFinishBtn = (TextView) findViewById(R.id.finish_btn_id);
		mAddBtn = (TextView) findViewById(R.id.add_btn_id);
		mListView = (ListView) findViewById(R.id.listview_id);

		mAdapter = new ContactGroupManagerAdapter(this, mGroupList);
		mListView.setAdapter(mAdapter);
	}

	private void addListener() {

		mFinishBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (bModifyFlag)
					setResult(RESULT_OK);
				finish();
			}
		});

		mAddBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				addGroup();
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				ContactGroupModel model = mGroupList.get(pos);
				onRename(model);
			}
		});

		mAdapter.onSetGroupOperListener(new ContactOperListener() {
			@Override
			public void onDelete(ContactGroupModel group) {

			}
		});
	}

	private void addGroup() {
		AddContactGroupDlg dlg = new AddContactGroupDlg(ContactGroupManagerActivity.this);
		dlg.onSetAddListener(new AddContactGroupListener() {
			@Override
			public void onAddGroup(String name) {
				if (TextUtils.isEmpty(name)) {
					ToastUtil.onShowToast(getBaseContext(), getString(R.string.im_toast_name_empty_str));
				} else {
					onAddOneGroup(name);
				}
			}
		});
		dlg.show();
	}

	private void onAddOneGroup(final String groupName) {
		if (TextUtils.isEmpty(groupName))
			return;

		String userId = XmppTool.onGetUserId();
		ContactGroupModel groupModel = new ContactGroupModel();
		groupModel.name = groupName;

		if (null != mGroupList && !mGroupList.isEmpty()) {
			if (mGroupList.contains(groupModel)) {
				ToastUtil.onShowToast(getBaseContext(), getString(R.string.im_toast_contact_group_isexist_str));
				return;
			}
		}

		try {
			boolean bSuccessFlag = XmppTool.addGroup(groupName);
			// if (bSuccessFlag) {
			XmppDB db = ImApplication.onGetInstance().onGetDB();
			if (null == db)
				return;
			db.onAddGroup(userId, groupName);

			mGroupList.add(groupModel);
			mAdapter.notifyDataSetChanged();
			String formatStr = String.format(getString(R.string.im_create_group_success_format_str), groupName);
			ToastUtil.onShowToast(getBaseContext(), formatStr);
			bModifyFlag = true;
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void onRename(final ContactGroupModel group) {
		if (null == group)
			return;

		ReNameContactGroupDlg dlg = new ReNameContactGroupDlg(this);
		dlg.onSetGroup(group.name);
		dlg.onSetAddListener(new ReNameContactGroupListener() {
			@Override
			public void onAddGroup(String name) {
				if (!TextUtils.isEmpty(name)) {
					boolean bFlag = XmppTool.reNameGroupName(group.name, name);
					if (bFlag) {
						if (mGroupList.contains(group)) {
							int nIndex = mGroupList.indexOf(group);
							group.name = name;
							mGroupList.set(nIndex, group);
							mAdapter.notifyDataSetChanged();
						}
						ToastUtil.onShowToast(getApplicationContext(), "分组名称修改成功");
						onSendBroadcast();
					}
				}
			}
		});
		dlg.show();
	}

	private void onSendBroadcast() {
		Intent intent = new Intent(IMCommDefine.broadcast_group_change);
		sendBroadcast(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (bModifyFlag)
				setResult(RESULT_OK);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (null != mGroupList)
			mGroupList.clear();
	}
}
