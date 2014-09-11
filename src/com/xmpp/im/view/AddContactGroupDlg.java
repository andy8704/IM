package com.xmpp.im.view;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.ad.view.dialog.TopShowDialog;
import com.xmpp.im.R;

/**
 * 
 * 
 * @类名称: AddContactGroupDlg
 * @描述: 添加联系人分组对话框
 * @开发者: andy.xu
 * @时间: 2014-8-26 下午6:10:13
 * 
 */
public class AddContactGroupDlg extends TopShowDialog {

	private EditText mInputTextView = null;
	private TextView mOKBtn = null;
	private TextView mCancelBtn = null;

	public AddContactGroupDlg(Context context) {
		super(context, R.style.transparent);
	}

	public AddContactGroupDlg(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_contact_group_dlg);
		Window dlgWindow = getWindow();
		WindowManager.LayoutParams params = dlgWindow.getAttributes();
		DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
		if (null != displayMetrics) {
			params.width = displayMetrics.widthPixels;
		}
		dlgWindow.setAttributes(params);

		initUI();
	}
	

	private void initUI() {
		mInputTextView = (EditText) findViewById(R.id.editview_id);
		mOKBtn = (TextView) findViewById(R.id.ok_btn_id);
		mCancelBtn = (TextView) findViewById(R.id.cancel_btn_id);

		mOKBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (null != mListener)
					mListener.onAddGroup(mInputTextView.getText().toString());
				dismiss();
			}
		});

		mCancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});
	}

	private AddContactGroupListener mListener = null;

	public void onSetAddListener(final AddContactGroupListener listener) {
		mListener = listener;
	}

	public interface AddContactGroupListener {
		public void onAddGroup(final String name);
	}
}
