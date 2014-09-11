package com.xmpp.im;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ad.util.ToastUtil;
import com.xmpp.im.client.util.ImUtil;
import com.xmpp.im.client.util.XmppTool;
import com.xmpp.im.model.ContactGroupModel;
import com.xmpp.im.model.UserModel;
import com.xmpp.im.util.IMCommDefine;

/**
 * 
 * 
 * @类名称: UserVerificateActivity
 * @描述: 用户信息验证
 * @开发者: andy.xu
 * @时间: 2014-8-28 上午10:31:21
 * 
 */
public class UserVerificateActivity extends IMBasicActivity implements TextWatcher {

	private TextView mBackBtn = null;
	private TextView mSendBtn = null;
	private EditText mNickNameEditView = null;
	private TextView mGroupTxtView = null;
	private ImageView mDelBtn = null;
	private LinearLayout mSetGroupBtn = null;
	private UserModel mUserModel = null;
	private ContactGroupModel mGroupModel = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.user_verificate_activity);
		mUserModel = (UserModel) getIntent().getSerializableExtra(IMCommDefine.intent_data);
		if (null == mUserModel)
			return;

		initUI();
		initListener();
	}

	private void initUI() {

		mBackBtn = (TextView) findViewById(R.id.back_btn_id);
		mNickNameEditView = (EditText) findViewById(R.id.nickname_view_id);
		mSendBtn = (TextView) findViewById(R.id.send_btn_id);
		mDelBtn = (ImageView) findViewById(R.id.del_btn_id);

		mGroupTxtView = (TextView) findViewById(R.id.group_view_id);
		mSetGroupBtn = (LinearLayout) findViewById(R.id.set_group_view_id);

		mNickNameEditView.setText(mUserModel.userName);
		List<ContactGroupModel> tempGroup = ImUtil.getContactGroup();
		if (null != tempGroup && !tempGroup.isEmpty()) {
			mGroupModel = tempGroup.get(0);
		}

		if (null != mGroupModel)
			mGroupTxtView.setText(mGroupModel.name);
	}

	private void initListener() {
		mBackBtn.setOnClickListener(mClick);
		mSendBtn.setOnClickListener(mClick);
		mDelBtn.setOnClickListener(mClick);
		mSetGroupBtn.setOnClickListener(mClick);

		mNickNameEditView.addTextChangedListener(this);
	}

	private View.OnClickListener mClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			default:
				break;
			case R.id.back_btn_id:
				finish();
				break;
			case R.id.send_btn_id:
				addCurUser();
				break;
			case R.id.del_btn_id:
				mNickNameEditView.setText("");
				mDelBtn.setVisibility(View.INVISIBLE);
				break;
			case R.id.set_group_view_id:
				Intent intent = new Intent(UserVerificateActivity.this, SelectGroupActivity.class);
				intent.putExtra(IMCommDefine.intent_data, mGroupModel);
				startActivityForResult(intent, 0x1001);
				break;
			}
		}
	};

	private void addCurUser() {
		if (null == mUserModel)
			return;

		boolean bFlag = XmppTool.addUser(mUserModel.userId, mNickNameEditView.getText().toString(), mGroupTxtView.getText().toString());
		if (bFlag) {
			ToastUtil.onShowToast(getBaseContext(), getString(R.string.im_toast_add_user_success_str));
			finish();
		} else {
			ToastUtil.onShowToast(getBaseContext(), getString(R.string.im_tost_add_user_fail_str));
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void afterTextChanged(Editable arg0) {

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		if (TextUtils.isEmpty(arg0))
			mDelBtn.setVisibility(View.INVISIBLE);
		else
			mDelBtn.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 0x1001 && resultCode == RESULT_OK) {
			mGroupModel = (ContactGroupModel) data.getSerializableExtra(IMCommDefine.intent_data);
			if (null != mGroupModel)
				mGroupTxtView.setText(mGroupModel.name);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
