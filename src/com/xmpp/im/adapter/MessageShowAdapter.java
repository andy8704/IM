package com.xmpp.im.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmpp.im.R;
import com.xmpp.im.model.MessageModel;

/**
 * 
 * 
 * @类名称: MessageShowAdapter
 * @描述: 聊天记录显示适配器
 * @开发者: andy.xu
 * @时间: 2014-8-30 下午5:12:25
 * 
 */
public class MessageShowAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater inflater;
	private List<MessageModel> mData = null;

	public MessageShowAdapter(Context context, final List<MessageModel> data) {
		mContext = context;
		mData = data;
	}

	@Override
	public int getCount() {
		return null == mData ? 0 : mData.size();
	}

	@Override
	public Object getItem(int position) {
		return null == mData ? null : mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		MessageModel msgModel = mData.get(position);
		if (null == msgModel)
			return null;

		Holder holder = null;
		// if (null == convertView) {
		// IN,OUT的图片
		if (msgModel.fromFlag == 0) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.formclient_chat_in, null);
		} else {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.formclient_chat_out, null);
		}

		holder = new Holder();

		holder.iconView = (ImageView) convertView.findViewById(R.id.formclient_row_userid);
		holder.timeText = (TextView) convertView.findViewById(R.id.formclient_row_date);
		holder.msgContent = (TextView) convertView.findViewById(R.id.formclient_row_msg);
		convertView.setTag(holder);
		// } else {
		// holder = (Holder) convertView.getTag();
		// }

		holder.timeText.setText(onGetFormatTime(msgModel.time));
		holder.msgContent.setText(msgModel.msgContent);

		return convertView;
	}

	private String onGetFormatTime(final long time) {
		return null;
	}

	public class Holder {
		ImageView iconView;
		TextView msgContent;
		TextView timeText;
	}
}
