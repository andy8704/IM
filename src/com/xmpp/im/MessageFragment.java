package com.xmpp.im;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.xmpp.im.adapter.MessageListAdapter;
import com.xmpp.im.client.util.ImUtil;
import com.xmpp.im.client.util.XmppTool;
import com.xmpp.im.model.MessageModel;
import com.xmpp.im.model.UserModel;
import com.xmpp.im.util.IMCommDefine;

/**
 * 
 * 
 * @类名称: MessageFragment
 * @描述: 消息显示
 * @开发者: andy.xu
 * @时间: 2014-8-28 下午3:26:30
 * 
 */
public class MessageFragment extends Fragment {

	private ListView mListView = null;
	private List<MessageModel> mMessageList = null;
	private MessageListAdapter mAdaper = null;
	private Map<String, Integer> mMessageCountMap = new HashMap<String, Integer>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		registerBroadcast();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.msg_show_activity, container, false);

		initUI(view);
		return view;
	}

	private void initUI(View view) {
		if (null == view)
			return;

		mListView = (ListView) view.findViewById(R.id.listview_id);
		mMessageList = new ArrayList<MessageModel>();
		mAdaper = new MessageListAdapter(getActivity(), mMessageList);
		mListView.setAdapter(mAdaper);
		initListener();

		onGetOfflineMessage();
	}

	private void initListener() {

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				MessageModel msgModel = mMessageList.get(arg2);
				if (null != msgModel) {

					mMessageCountMap.remove(msgModel.fromUser);
					mMessageList.remove(msgModel);
					mAdaper.notifyDataSetChanged();
					Intent intent = new Intent(getActivity(), IMClientActivity.class);
					intent.putExtra(IMCommDefine.intent_userId, msgModel.fromUser);
					getActivity().startActivity(intent);
				}
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unRegisterBroadcast();
	}

	private void registerBroadcast() {

		IntentFilter filter = new IntentFilter(IMCommDefine.broadcast_msg);
		getActivity().registerReceiver(chatBroadcast, filter);
	}

	private void unRegisterBroadcast() {
		if (null != chatBroadcast)
			getActivity().unregisterReceiver(chatBroadcast);
	}

	private BroadcastReceiver chatBroadcast = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (null != intent) {
				MessageModel msg = (MessageModel) intent.getSerializableExtra(IMCommDefine.intent_data);
				onDealWithMessage(msg);
			}
		}
	};

	private void onDealWithMessage(final MessageModel msg) {
		if (null == msg)
			return;

		if (!TextUtils.isEmpty(msg.fromUser)) {
			if (TextUtils.equals(ImUtil.onGetCurChatUserId(), msg.fromUser))
				return;
		}

		if (null == mMessageList)
			mMessageList = new ArrayList<MessageModel>();
		if (mMessageList.contains(msg)) {
			int nCount = mMessageCountMap.get(msg.fromUser).intValue() + 1;
			msg.count = nCount;
			mMessageCountMap.put(msg.fromUser, nCount);
			mMessageList.set(mMessageList.indexOf(msg), msg);
		} else {
			mMessageCountMap.put(msg.fromUser, 1);
			msg.count = 1;
			mMessageList.add(msg);
		}

		mAdaper.notifyDataSetChanged();
	}

	private void onGetOfflineMessage() {

		Map<String, List<MessageModel>> offlineMessage = ImUtil.onGetOfflineMsg();
		if (null != offlineMessage && !offlineMessage.isEmpty()) {
			for (String key : offlineMessage.keySet()) {
				if (!TextUtils.isEmpty(key)) {
					List<MessageModel> userMsgList = offlineMessage.get(key);
					if (null != userMsgList && !userMsgList.isEmpty()) {
						MessageModel msg = userMsgList.get(userMsgList.size() - 1);
						msg.count = userMsgList.size();
						UserModel userModel = ImUtil.onGetUserModel(msg.fromUser);
						if (null != userModel)
							msg.userName = userModel.userName;

						mMessageCountMap.put(msg.fromUser, msg.count);
						mMessageList.add(msg);
					}
				}
			}
		}
		mAdaper.notifyDataSetChanged();
	}
}
