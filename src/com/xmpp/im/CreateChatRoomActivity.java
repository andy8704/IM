package com.xmpp.im;

import com.ad.util.ToastUtil;
import com.xmpp.im.util.IMCommDefine;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * 
 * @类名称: CreateChatRoomActivity
 * @描述: 创建一个聊天室
 * @开发者: andy.xu
 * @时间: 2014-8-29 下午1:51:22
 * 
 */
public class CreateChatRoomActivity extends IMBasicActivity implements TextWatcher {

	private TextView mBackBtn = null;
	private EditText mInputView = null;
	private ImageView mDelBtn = null;

	private LinearLayout mNextPageBtn = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.create_chatroom_activity);
		initUI();
	}

	private void initUI() {
		mBackBtn = (TextView) findViewById(R.id.back_btn_id);
		mInputView = (EditText) findViewById(R.id.chatroom_view_id);
		mDelBtn = (ImageView) findViewById(R.id.del_btn_id);

		mNextPageBtn = (LinearLayout) findViewById(R.id.next_btn_id);

		mBackBtn.setOnClickListener(mClickListener);
		mDelBtn.setOnClickListener(mClickListener);
		mNextPageBtn.setOnClickListener(mClickListener);
	}

	private View.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {

			default:
				break;
			case R.id.back_btn_id:
				break;
			case R.id.del_btn_id:
				mInputView.setText("");
				mDelBtn.setVisibility(View.INVISIBLE);
				break;
			case R.id.next_btn_id:
				onNextStep();
				break;
			}
		}
	};

	private void onNextStep() {
		String chatRoomName = mInputView.getText().toString();
		if (TextUtils.isEmpty(chatRoomName)) {
			ToastUtil.onShowToast(getBaseContext(), getString(R.string.im_toast_chatroom_name_empty_str));
			return;
		}

		Intent intent = new Intent(CreateChatRoomActivity.this, SelectUserActivity.class);
		intent.putExtra(IMCommDefine.intent_data, chatRoomName);
		startActivityForResult(intent, 0x1000);
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

		if (!TextUtils.isEmpty(arg0)) {
			mDelBtn.setVisibility(View.VISIBLE);
		} else
			mDelBtn.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 0x1000 && resultCode == RESULT_OK) {
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
