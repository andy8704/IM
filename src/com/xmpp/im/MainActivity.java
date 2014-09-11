package com.xmpp.im;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.xmpp.im.client.listener.SingnalChatManagerListener;
import com.xmpp.im.client.util.PresenceService;
import com.xmpp.im.client.util.XmppTool;
import com.xmpp.im.util.FuncUtil;

/**
 * 
 * 
 * @类名称: MainActivity
 * @描述: 主界面
 * @开发者: andy.xu
 * @时间: 2014-8-26 上午10:38:20
 * 
 */
public class MainActivity extends FragmentActivity {

	private TextView mMsgBtn = null;
	private TextView mContactBtn = null;
	private TextView mGroupBtn = null;
	private ContactFragment mContactFragment = null;
	private SingnalChatManagerListener mSignalChatManagerListener = null;
	private MessageFragment mMsgFragment = null;
	private ChatRootFragment mChatRoomFragment = null;

	private enum STATE {
		MSG, CONTACT, GROUP
	}

	private STATE mCurState = STATE.CONTACT;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.main_activity);

		initUI();
		addListener();
		onContact();
		onSetState();

		addChatListener();
		startService(new Intent(this, PresenceService.class));
	}

	private void initUI() {
		mMsgBtn = (TextView) findViewById(R.id.msg_btn_id);
		mContactBtn = (TextView) findViewById(R.id.contact_btn_id);
		mGroupBtn = (TextView) findViewById(R.id.group_btn_id);
	}

	private void addListener() {

		mMsgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (mCurState == STATE.MSG)
					return;
				else {
					mCurState = STATE.MSG;
					onMsg();
					onSetState();
				}
			}
		});

		mContactBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mCurState == STATE.CONTACT)
					return;
				else {
					mCurState = STATE.CONTACT;
					onContact();
					onSetState();
				}
			}
		});

		mGroupBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mCurState == STATE.GROUP)
					return;
				else {
					mCurState = STATE.GROUP;
					onChatRoom();
					onSetState();
				}
			}
		});
	}

	/**
	 * 
	 * @Description: 热门
	 * @param
	 * @return void
	 * @throws
	 */
	private void onContact() {

		FragmentManager fragmentManager = getSupportFragmentManager();

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		if (null == mContactFragment) {
			mContactFragment = new ContactFragment();
			fragmentTransaction.add(R.id.group_view_id, mContactFragment);
			if (null != mMsgFragment)
				fragmentTransaction.hide(mMsgFragment);
			if (null != mChatRoomFragment)
				fragmentTransaction.hide(mChatRoomFragment);
		} else {
			fragmentTransaction.show(mContactFragment);
			if (null != mMsgFragment)
				fragmentTransaction.hide(mMsgFragment);
			if (null != mChatRoomFragment)
				fragmentTransaction.hide(mChatRoomFragment);
		}

		fragmentTransaction.commit();
		mCurState = STATE.CONTACT;
	}

	private void onMsg() {

		FragmentManager fragmentManager = getSupportFragmentManager();

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		if (null == mMsgFragment) {
			mMsgFragment = new MessageFragment();
			fragmentTransaction.add(R.id.group_view_id, mMsgFragment);
			if (null != mContactFragment)
				fragmentTransaction.hide(mContactFragment);
			if (null != mChatRoomFragment)
				fragmentTransaction.hide(mChatRoomFragment);
		} else {
			fragmentTransaction.show(mMsgFragment);
			if (null != mContactFragment)
				fragmentTransaction.hide(mContactFragment);
			if (null != mChatRoomFragment)
				fragmentTransaction.hide(mChatRoomFragment);
		}

		fragmentTransaction.commit();
		mCurState = STATE.MSG;
	}

	private void onChatRoom() {

		FragmentManager fragmentManager = getSupportFragmentManager();

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		if (null == mChatRoomFragment) {
			mChatRoomFragment = new ChatRootFragment();
			fragmentTransaction.add(R.id.group_view_id, mChatRoomFragment);
			if (null != mContactFragment)
				fragmentTransaction.hide(mContactFragment);
			if (null != mMsgFragment)
				fragmentTransaction.hide(mMsgFragment);
		} else {
			fragmentTransaction.show(mChatRoomFragment);
			if (null != mContactFragment)
				fragmentTransaction.hide(mContactFragment);
			if (null != mMsgFragment)
				fragmentTransaction.hide(mMsgFragment);
		}

		fragmentTransaction.commit();
		mCurState = STATE.GROUP;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (null != mContactFragment)
			mContactFragment.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void onSetState() {

		mMsgBtn.setTextColor(getResources().getColor(R.color.color_676767));
		mContactBtn.setTextColor(getResources().getColor(R.color.color_676767));
		mGroupBtn.setTextColor(getResources().getColor(R.color.color_676767));

		if (STATE.CONTACT == mCurState) {
			mContactBtn.setTextColor(getResources().getColor(R.color.color_2cb1f4));
		} else if (STATE.MSG == mCurState) {
			mMsgBtn.setTextColor(getResources().getColor(R.color.color_2cb1f4));
		} else if (STATE.GROUP == mCurState) {
			mGroupBtn.setTextColor(getResources().getColor(R.color.color_2cb1f4));
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		stopService(new Intent(MainActivity.this, PresenceService.class));

		removeChatListener();
		XmppTool.closeConnection();
		FuncUtil.destoryAllActivity();
	}

	/**
	 * 
	 * @描述: 添加消息监听
	 * @参数
	 * @返回值 void
	 * @异常
	 */
	private void addChatListener() {
		mSignalChatManagerListener = new SingnalChatManagerListener();
		XmppTool.getConnection().getChatManager().addChatListener(mSignalChatManagerListener);
	}

	/**
	 * 
	 * @描述: 移除消息监听
	 * @参数
	 * @返回值 void
	 * @异常
	 */
	private void removeChatListener() {
		if (null != mSignalChatManagerListener) {
			XmppTool.getConnection().getChatManager().removeChatListener(mSignalChatManagerListener);
		}
	}
}
