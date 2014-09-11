package com.xmpp.im;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmpp.im.client.util.XmppTool;
import com.xmpp.im.model.UserModel;
import com.xmpp.im.util.IMCommDefine;

/**
 * 
 * 
 * @类名称: UserInfoShowActivity
 * @描述: 用户个人中心
 * @开发者: andy.xu
 * @时间: 2014-9-2 下午5:34:05
 * 
 */
public class UserInfoShowActivity extends IMBasicActivity {

	private TextView mBackBtn = null;
	private TextView mMoreBtn = null;

	private ImageView mPhotoImgView = null;
	private TextView mUserNameTxt = null;
	private TextView mUserInfoTxt = null;
	private TextView mUserAccountTxt = null;
	private TextView mUserNicknameTxt = null;
	private TextView mUserSignatureTxt = null;
	private UserModel mUserModel = null;
	private UserModel mCurUserModel = null;
	private Drawable mPhotoDrawable = null;
	private TextView mSendMsgBtn = null;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				onSetData();
				if (null != mPhotoDrawable)
					mPhotoImgView.setImageDrawable(mPhotoDrawable);
				break;
			case 1:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.userinfo_show_activity);
		mUserModel = (UserModel) getIntent().getSerializableExtra(IMCommDefine.intent_data);
		initUI();
		initListener();
		oninitData();
	}

	private void initUI() {

		mBackBtn = (TextView) findViewById(R.id.back_btn_id);
		mMoreBtn = (TextView) findViewById(R.id.more_btn_id);

		mPhotoImgView = (ImageView) findViewById(R.id.photo_img_view_id);
		mUserNameTxt = (TextView) findViewById(R.id.user_name_id);
		mUserInfoTxt = (TextView) findViewById(R.id.user_addr_id);

		mUserAccountTxt = (TextView) findViewById(R.id.user_account_view_id);
		mUserNicknameTxt = (TextView) findViewById(R.id.user_nickname_view_id);
		mUserSignatureTxt = (TextView) findViewById(R.id.user_signature_view_id);

		mSendMsgBtn = (TextView) findViewById(R.id.send_msg_btn_id);

		if (null != mUserModel) {
			mUserNameTxt.setText(mUserModel.userName);
			mUserAccountTxt.setText(mUserModel.userId);
		}
	}

	private void initListener() {
		mBackBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		mMoreBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(UserInfoShowActivity.this, UserInfoModifyActivity.class);
				intent.putExtra(IMCommDefine.intent_data, mUserModel);
				startActivity(intent);
			}
		});

		mSendMsgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(UserInfoShowActivity.this, IMClientActivity.class);
				intent.putExtra(IMCommDefine.intent_userId, mUserModel.userId);
				startActivity(intent);
			}
		});
	}

	private void oninitData() {

		new Thread() {
			public void run() {
				if (!TextUtils.isEmpty(mUserModel.userId))
					mCurUserModel = XmppTool.getUserModel(mUserModel.userId);
				else
					mCurUserModel = XmppTool.getOwnerModel();

				if (null != mCurUserModel) {
					mUserModel.nickName = mCurUserModel.nickName;
					mUserModel.addr = mCurUserModel.addr;
					mUserModel.signatrue = mCurUserModel.signatrue;
				}

				mPhotoDrawable = XmppTool.getUserImage(mUserModel.userId);

				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	private void onSetData() {
		if (null == mCurUserModel)
			return;

		// mUserNameTxt.setText(mCurUserModel.userName);
		mUserNicknameTxt.setText(mCurUserModel.nickName);
		// mUserAccountTxt.setText(mCurUserModel.userId);
		mUserSignatureTxt.setText(mCurUserModel.signatrue);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
