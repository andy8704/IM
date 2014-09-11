package com.xmpp.im.view;

import android.content.Context;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
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
 * @类名称: ReNameContactGroupDlg
 * @描述: 修改联系人分组
 * @开发者: andy.xu
 * @时间: 2014-8-26 下午6:10:13
 * 
 */
public class ReNameContactGroupDlg extends TopShowDialog {

	private EditText mInputTextView = null;
	private TextView mOKBtn = null;
	private TextView mCancelBtn = null;
	private TextView mTitleView = null;
	private TextView mSubView = null;
	private Context mContext = null;
	private String mGroupName = null;

	public ReNameContactGroupDlg(Context context) {
		super(context, R.style.transparent);
		mContext = context;
	}

	public ReNameContactGroupDlg(Context context, int theme) {
		super(context, theme);
		mContext = context;
	}

	public void onSetGroup(final String groupName) {

		mGroupName = groupName;
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
		mTitleView = (TextView) findViewById(R.id.dlg_title_view_id);
		mTitleView.setText(mContext.getString(R.string.im_rename_contact_group_str));
		mSubView = (TextView) findViewById(R.id.dlg_sub_view_id);
		mSubView.setText(mContext.getString(R.string.im_rename_contact_group_sub_str));

		mInputTextView.setText(mGroupName);
		Selection.setSelection((Spannable) mInputTextView.getText(), mInputTextView.getText().length());

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

	private ReNameContactGroupListener mListener = null;

	public void onSetAddListener(final ReNameContactGroupListener listener) {
		mListener = listener;
	}

	public interface ReNameContactGroupListener {
		public void onAddGroup(final String name);
	}
}
