package com.xmpp.im.adapter;

import java.util.List;

import org.jivesoftware.smackx.muc.HostedRoom;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmpp.im.R;

/**
 * 
 * 
 * @类名称: chatroomListAdapter
 * @描述: 聊天室的适配器
 * @开发者: andy.xu
 * @时间: 2014-8-27 下午6:22:03
 * 
 */
public class chatroomListAdapter extends BaseAdapter {

	private List<HostedRoom> mData = null;
	private Context mContext = null;

	public chatroomListAdapter(final Context context, final List<HostedRoom> data) {
		mContext = context;
		mData = data;
	}

	@Override
	public int getCount() {
		return null == mData ? 0 : mData.size();
	}

	@Override
	public Object getItem(int pos) {
		return null == mData ? null : mData.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {

		Holder holder = null;
		if (null == convertView) {
			convertView = LayoutInflater.from(mContext).inflate(com.xmpp.im.R.layout.user_list_adapter, null);
			holder = new Holder();
			holder.imgView = (ImageView) convertView.findViewById(R.id.img_view_id);
			holder.mTextView = (TextView) convertView.findViewById(R.id.text_id);
			holder.addView = (ImageView) convertView.findViewById(R.id.add_view_id);
			holder.addView.setVisibility(View.INVISIBLE);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		final HostedRoom model = mData.get(pos);
		if (null != model) {
			if (TextUtils.isEmpty(model.getName()))
				holder.mTextView.setText(model.getJid());
			else
				holder.mTextView.setText(model.getName());
		}
		return convertView;
	}

	public class Holder {
		public ImageView imgView;
		public TextView mTextView;
		public ImageView addView;
	}
}
