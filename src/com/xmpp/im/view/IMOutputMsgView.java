package com.xmpp.im.view;

import com.ad.bitmap.BitmapDecodeUtil;
import com.xmpp.im.R;
import com.xmpp.im.model.MessageModel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * 
* @ClassName: IMOutputMsgView 
* @Description: 输出信息
* @author andy.xu 
* @date 2014-9-13 上午11:24:50 
*
 */
public class IMOutputMsgView extends LinearLayout {

	private Context mContext = null;
	private ImageView mUserIconView = null;
	private TextView mTimeView = null;
	private TextView mMsgContentView = null;

	public IMOutputMsgView(Context context) {
		super(context);

		initUI(context);
	}

	public IMOutputMsgView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initUI(context);
	}

	@SuppressLint("NewApi")
	public IMOutputMsgView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initUI(context);
	}

	private void initUI(final Context context) {
		mContext = context;
		inflate(context, R.layout.formclient_chat_out, this);
		mUserIconView = (ImageView) findViewById(R.id.formclient_row_userid);
		mTimeView = (TextView) findViewById(R.id.formclient_row_date);
		mMsgContentView = (TextView) findViewById(R.id.formclient_row_msg);
	}

	public void onSetMsgData(final MessageModel msg) {
		if (null == msg)
			return;

		if (!TextUtils.isEmpty(msg.userIcon)) {
			new BitmapDecodeUtil().onDisplayBitmap(mUserIconView, msg.userIcon);
		}

		mTimeView.setText(onGetFormatTime(msg.time));
		mMsgContentView.setText(msg.msgContent);
	}

	private String onGetFormatTime(final long time) {
		return null;
	}

}
