package com.xmpp.im.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ad.bitmap.BitmapDecodeUtil;
import com.xmpp.im.R;
import com.xmpp.im.model.MessageModel;

/**
 * 
 * @ClassName: IMInputMsgView
 * @Description: 输入信息
 * @author andy.xu
 * @date 2014-9-13 上午11:13:29
 * 
 */
public class IMInputMsgPicView extends LinearLayout {

	private Context mContext = null;
	private ImageView mUserIconView = null;
	private TextView mTimeView = null;
	private ImageView mReceivePicView = null;

	public IMInputMsgPicView(Context context) {
		super(context);

		initUI(context);
	}

	public IMInputMsgPicView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initUI(context);
	}

	@SuppressLint("NewApi")
	public IMInputMsgPicView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initUI(context);
	}

	private void initUI(final Context context) {
		mContext = context;
		inflate(context, R.layout.formclient_chat_in_pic, this);
		mUserIconView = (ImageView) findViewById(R.id.formclient_row_userid);
		mTimeView = (TextView) findViewById(R.id.formclient_row_date);
		mReceivePicView = (ImageView) findViewById(R.id.formclient_row_pic);
	}
	
	public void onSetMsgData(final MessageModel msg){
		if(null == msg)
			return;
		
		if(!TextUtils.isEmpty(msg.userIcon)){
			new BitmapDecodeUtil().onDisplayBitmap(mUserIconView, msg.userIcon);
		}
		
		mTimeView.setText(onGetFormatTime(msg.time));
		onSetPic(msg.filePath);
	}
	
	private void onSetPic(final String filePath){
		if(TextUtils.isEmpty(filePath))
			return;
		
		Bitmap bmp = BitmapDecodeUtil.decodeFile(filePath, 360, 640);
		if(null != bmp)
			mReceivePicView.setImageBitmap(bmp);
	}
	
	private String onGetFormatTime(final long time) {
		return null;
	}

}
