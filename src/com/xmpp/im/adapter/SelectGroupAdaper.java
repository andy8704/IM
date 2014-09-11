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
import com.xmpp.im.model.ContactGroupModel;

/**
 * 
 * 
 * @类名称: SelectGroupAdaper
 * @描述: 选择分组
 * @开发者: andy.xu
 * @时间: 2014-8-28 上午11:19:22
 * 
 */
public class SelectGroupAdaper extends BaseAdapter {

	private List<ContactGroupModel> mData = null;
	private Context mContext = null;
	private ContactGroupModel mSelectGroup = null;

	public SelectGroupAdaper(final Context context, List<ContactGroupModel> data) {
		mContext = context;
		mData = data;
	}

	public void onSetSelect(ContactGroupModel group) {
		mSelectGroup = group;
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
			convertView = LayoutInflater.from(mContext).inflate(com.xmpp.im.R.layout.select_group_adapter, null);
			holder = new Holder();
			holder.mSelectView = (ImageView) convertView.findViewById(R.id.select_img_id);
			holder.mTextView = (TextView) convertView.findViewById(R.id.text_id);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		ContactGroupModel model = mData.get(pos);
		if (null != model) {
			holder.mTextView.setText(model.name);

			if (model.equals(mSelectGroup))
				holder.mSelectView.setVisibility(View.VISIBLE);
			else
				holder.mSelectView.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

	public class Holder {
		public ImageView mSelectView;
		public TextView mTextView;
	}
}
