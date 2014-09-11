package com.xmpp.im.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ad.bitmap.BitmapDecodeUtil;
import com.xmpp.im.R;
import com.xmpp.im.model.MessageModel;
import com.xmpp.im.model.UserModel;
import com.xmpp.im.util.FuncUtil;

/**
 * 
 * 
 * @类名称: UserSearchListAdapter
 * @描述: 查询到的用户的结果显示
 * @开发者: andy.xu
 * @时间: 2014-8-27 下午6:22:03
 * 
 */
public class MessageListAdapter extends BaseAdapter {

	private List<MessageModel> mData = null;
	private Context mContext = null;

	public MessageListAdapter(final Context context, final List<MessageModel> data) {
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
			convertView = LayoutInflater.from(mContext).inflate(com.xmpp.im.R.layout.msg_show_item_adapter, null);
			holder = new Holder();
			holder.imgView = (ImageView) convertView.findViewById(R.id.img_view_id);
			holder.userNameView = (TextView) convertView.findViewById(R.id.userName_view_id);
			holder.msgContentView = (TextView) convertView.findViewById(R.id.msg_view_id);
			holder.timeView = (TextView) convertView.findViewById(R.id.time_view_id);
			holder.countView = (TextView) convertView.findViewById(R.id.count_view_id);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		final MessageModel model = mData.get(pos);
		if (null != model) {
			if (TextUtils.isEmpty(model.userName))
				holder.userNameView.setText(model.fromUser);
			else
				holder.userNameView.setText(model.userName);

			holder.msgContentView.setText(model.msgContent);
			String time = FuncUtil.onGetMsgFormatTime(model.time);
			if(!TextUtils.isEmpty(time))
				holder.timeView.setText(time);
			if (model.count <= 0)
				holder.countView.setVisibility(View.INVISIBLE);
			else {
				holder.countView.setVisibility(View.VISIBLE);
				holder.countView.setText("" + model.count);
			}

			if (!TextUtils.isEmpty(model.userIcon)) {
				new BitmapDecodeUtil().onDisplayBitmap(holder.imgView, model.userIcon);
			}
		}

		return convertView;
	}

	public class Holder {
		public ImageView imgView;
		public TextView userNameView;
		public TextView msgContentView;
		public TextView timeView;
		public TextView countView;
	}

	private AddUserListener mListener = null;

	public void onSetAddListener(final AddUserListener listener) {
		mListener = listener;
	}

	public interface AddUserListener {
		public void onAdd(final UserModel user);
	}
}
