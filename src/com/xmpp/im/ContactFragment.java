package com.xmpp.im;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.ad.util.NetWorkMonitor;
import com.ad.util.ToastUtil;
import com.ad.view.IphoneTreeView;
import com.xmpp.im.adapter.ContactExpandableListAdapter;
import com.xmpp.im.client.util.ImUtil;
import com.xmpp.im.client.util.XmppTool;
import com.xmpp.im.database.XmppDB;
import com.xmpp.im.model.ContactGroupModel;
import com.xmpp.im.model.UserModel;
import com.xmpp.im.util.IMCommDefine;

/**
 * 
 * 
 * @类名称: ContactFragment
 * @描述: 联系人的列表
 * @开发者: andy.xu
 * @时间: 2014-8-25 上午11:48:12
 * 
 */
public class ContactFragment extends Fragment {

	private IphoneTreeView mTreeView = null;
	private ContactExpandableListAdapter mExpandAdapter = null;
	private LayoutInflater mInflater;
	private List<ContactGroupModel> mGroupList = new ArrayList<ContactGroupModel>();
	private Map<String, List<UserModel>> mChildMap = new HashMap<String, List<UserModel>>();
	private TextView mAddBtn = null;
	private Map<String, UserModel> mAllUserModel = new HashMap<String, UserModel>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.contact_fragment, container, false);
		initView(view);
		return view;
	}

	private void initView(View view) {
		if (null == view)
			return;

		mInflater = LayoutInflater.from(getActivity());

		mTreeView = (IphoneTreeView) view.findViewById(R.id.friend_xlistview);
		mTreeView.setGroupIndicator(null);
		mTreeView.setHeaderView(mInflater.inflate(R.layout.contact_buddy_list_group, mTreeView, false));
		mExpandAdapter = new ContactExpandableListAdapter(getActivity(), mGroupList, mChildMap, mTreeView);
		mTreeView.setAdapter(mExpandAdapter);

		mAddBtn = (TextView) view.findViewById(R.id.add_btn_id);

		mTreeView.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				UserModel u = (UserModel) mExpandAdapter.getChild(groupPosition, childPosition);
				// 选中的是哪一个用户
				if (null != u) {
					Intent intent = new Intent(getActivity(), UserInfoShowActivity.class);
					intent.putExtra(IMCommDefine.intent_data, u);
					getActivity().startActivity(intent);
				}
				return false;
			}
		});

		mTreeView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				int groupPos = (Integer) view.getTag(R.id.xxx01); // 参数值是在setTag时使用的对应资源id号
				int childPos = (Integer) view.getTag(R.id.xxx02);
				if (childPos == -1) {
					// group被长按
					Intent intent = new Intent(getActivity(), ContactGroupManagerActivity.class);
					intent.putExtra(IMCommDefine.intent_data, (Serializable) mGroupList);
					getActivity().startActivityForResult(intent, 0x0100);
				} else {
					// child被长按
				}
				return false;
			}
		});

		mAddBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), ImAddUserActivity.class);
				getActivity().startActivity(intent);
			}
		});

		onGetData();
		onRegisterBroadcast();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	private void onGetData() {

		if (!NetWorkMonitor.isConnect(getActivity())) {
			ToastUtil.onShowToast(getActivity(), getString(R.string.im_toast_disconnect_str));
			return;
		}

		mGroupList.clear();
		mChildMap.clear();
		mAllUserModel.clear();

		XMPPConnection connect = XmppTool.getConnection();
		Roster roster = connect.getRoster();
		List<RosterGroup> group = XmppTool.getGroups();
		if (null != group && !group.isEmpty()) {
			for (RosterGroup rosterGroup : group) {
				if (null != rosterGroup) {
					String groupName = rosterGroup.getName();
					ContactGroupModel groupModel = new ContactGroupModel();
					groupModel.name = groupName;
					mGroupList.add(groupModel);

					List<RosterEntry> userData = XmppTool.getEntriesByGroup(groupName);
					if (null != userData) {
						List<UserModel> userList = new ArrayList<UserModel>();
						List<UserModel> offLineUser = new ArrayList<UserModel>();
						for (RosterEntry ele : userData) {
							if (null != ele) {
								UserModel model = new UserModel();
								model.userName = ele.getName();
								model.userId = ele.getUser();
								model.groupName = groupName;
								Presence availability = roster.getPresence(ele.getUser());
								model.statue = XmppTool.onGetPresence(availability);
								if (model.statue == 5) {
									offLineUser.add(model);
								} else {
									if (model.statue == 0)
										userList.add(0, model);
									else
										userList.add(model);
								}
								mAllUserModel.put(model.userId, model);
							}
						}
						if (null != offLineUser)
							userList.addAll(offLineUser);
						mChildMap.put(groupName, userList);
					}
				}
			}

			Collection<RosterEntry> unFiledUser = roster.getUnfiledEntries();
			if (null != unFiledUser && !unFiledUser.isEmpty()) {
				ContactGroupModel unFiledGroup = new ContactGroupModel();
				unFiledGroup.name = getString(R.string.im_unfiled_group_name_lable);
				mGroupList.add(unFiledGroup);

				List<UserModel> userList = new ArrayList<UserModel>();
				List<UserModel> offLineUser = new ArrayList<UserModel>();
				for (RosterEntry ele : unFiledUser) {
					if (null != ele) {

						UserModel model = new UserModel();
						model.userName = ele.getName();
						model.userId = ele.getUser();
						model.groupName = unFiledGroup.name;
						Presence availability = roster.getPresence(ele.getUser());
						model.statue = XmppTool.onGetPresence(availability);
						if (model.statue == 5) {
							offLineUser.add(model);
						} else {
							if (model.statue == 0)
								userList.add(0, model);
							else
								userList.add(model);
						}
						mAllUserModel.put(model.userId, model);
						if (null != offLineUser)
							userList.addAll(offLineUser);
						mChildMap.put(unFiledGroup.name, userList);
					}
				}
			}
		} else {
			ContactGroupModel groupModel = new ContactGroupModel();
			groupModel.name = getString(R.string.im_unfiled_group_name_lable);

			XmppDB db = ImApplication.onGetInstance().onGetDB();
			if (!db.isGroupExist(XmppTool.onGetUserId(), groupModel.name))
				ImApplication.onGetInstance().onGetDB().onAddGroup(XmppTool.onGetUserId(), groupModel.name);

			Collection<RosterEntry> userData = roster.getEntries();
			if (null != userData && !userData.isEmpty()) {
				List<UserModel> userList = new ArrayList<UserModel>();
				List<UserModel> offLineUser = new ArrayList<UserModel>();
				for (RosterEntry ele : userData) {
					if (null != ele) {
						UserModel model = new UserModel();
						model.userName = ele.getName();
						model.userId = ele.getUser();
						Presence availability = roster.getPresence(ele.getUser());
						model.statue = XmppTool.onGetPresence(availability);
						if (model.statue == 5) {
							offLineUser.add(model);
						} else {
							if (model.statue == 0)
								userList.add(0, model);
							else
								userList.add(model);
						}
						mAllUserModel.put(model.userId, model);
					}
				}
				if (null != offLineUser)
					userList.addAll(offLineUser);
				mChildMap.put(groupModel.name, userList);
			}
		}

		String userId = XmppTool.onGetUserId();
		List<ContactGroupModel> localGroup = ImApplication.onGetInstance().onGetDB().onGetUserGroup(userId);
		if (null != localGroup && !localGroup.isEmpty()) {
			for (ContactGroupModel ele : localGroup) {
				if (null != ele) {
					if (mGroupList.contains(ele)) {
						// ImApplication.onGetInstance().onGetDB().onDeleteGroup(userId,
						// ele.name);
					} else
						mGroupList.add(ele);
				}
			}
		}

		ImUtil.onSetContactGroup(mGroupList);
		ImUtil.onSetAllUser(mAllUserModel);
		mHander.sendEmptyMessage(0);
	}

	private Handler mHander = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			default:
				break;
			case 0:
				mExpandAdapter.notifyDataSetChanged();
				break;
			case 1:
				break;
			}
		};
	};

	@Override
	public void onDestroy() {
		super.onDestroy();

		unRegisterBroadcast();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 0x0100 && resultCode == Activity.RESULT_OK) {
			onGetData();
		}
	}

	private void onRegisterBroadcast() {
		IntentFilter filter = new IntentFilter(IMCommDefine.broadcast_group_change);
		filter.addAction(IMCommDefine.broadcast_add_roster);
		filter.addAction(IMCommDefine.broadcast_delete_roster);
		filter.addAction(IMCommDefine.broadcast_presence_change);
		filter.addAction(IMCommDefine.broadcast_subscribe);
		getActivity().registerReceiver(mBroadcast, filter);
	}

	private void unRegisterBroadcast() {
		if (null != mBroadcast)
			getActivity().unregisterReceiver(mBroadcast);
	}

	private BroadcastReceiver mBroadcast = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent arg1) {

			String action = arg1.getAction();
			if (TextUtils.equals(action, IMCommDefine.broadcast_add_roster)) {
				onGetData();
			} else if (TextUtils.equals(action, IMCommDefine.broadcast_delete_roster)) {
				onGetData();
			} else if (TextUtils.equals(action, IMCommDefine.broadcast_group_change)) {
				onGetData();
			} else if (TextUtils.equals(action, IMCommDefine.broadcast_presence_change)) {
				onGetData();
			} else if (TextUtils.equals(action, IMCommDefine.broadcast_subscribe)) {
				String from = arg1.getStringExtra(IMCommDefine.intent_data);
				ToastUtil.onShowToast(getActivity(), from + "申请添加为好友");
			}
		}
	};

}
