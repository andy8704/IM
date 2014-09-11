package com.xmpp.im;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smackx.muc.HostedRoom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.xmpp.im.adapter.chatroomListAdapter;
import com.xmpp.im.client.util.XmppTool;

/**
 * 
 * @ClassName: ChatRootFragment
 * @Description: 聊天室群组
 * @author andy.xu
 * @date 2014-8-28 下午11:10:45
 * 
 */
public class ChatRootFragment extends Fragment {

	private ListView mListView = null;
	private TextView mCreateRoomBtn = null;
	private chatroomListAdapter mAdapter = null;
	private List<HostedRoom> mChatRoomList = new ArrayList<HostedRoom>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		List<HostedRoom> temp = XmppTool.onGetHostedRoom();
		if (null != temp && !temp.isEmpty())
			mChatRoomList.addAll(temp);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.chatroom_fragment, container, false);
		initView(view);
		return view;
	}

	private void initView(final View view) {
		if (null == view)
			return;

		mCreateRoomBtn = (TextView) view.findViewById(R.id.add_btn_id);
		mListView = (ListView) view.findViewById(R.id.listview_id);
		mAdapter = new chatroomListAdapter(getActivity(), mChatRoomList);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int postion, long arg3) {

			}
		});

		mCreateRoomBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), CreateChatRoomActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	
}
