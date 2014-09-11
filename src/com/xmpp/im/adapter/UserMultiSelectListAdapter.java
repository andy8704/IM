package com.xmpp.im.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmpp.im.R;
import com.xmpp.im.model.UserModel;

/**
 * 
 * 
 * @类名称: UserMultiSelectListAdapter
 * @描述: 选择多个用户
 * @开发者: andy.xu
 * @时间: 2014-8-27 下午6:22:03
 * 
 */
public class UserMultiSelectListAdapter extends BaseAdapter {

	private List<UserModel> mData = null;
	private Context mContext = null;
	private List<UserModel> mSelectUser = null;

	public UserMultiSelectListAdapter(final Context context, final List<UserModel> data) {
		mContext = context;
		mData = data;
		mSelectUser = new ArrayList<UserModel>();
	}

	public List<UserModel> getSelectUser() {
		return mSelectUser;
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
			convertView = LayoutInflater.from(mContext).inflate(com.xmpp.im.R.layout.select_user_list_adapter, null);
			holder = new Holder();
			holder.imgView = (ImageView) convertView.findViewById(R.id.img_view_id);
			holder.mTextView = (TextView) convertView.findViewById(R.id.text_id);
			holder.checkView = (CheckBox) convertView.findViewById(R.id.check_view_id);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		final UserModel model = mData.get(pos);
		if (null != model) {
			if (TextUtils.isEmpty(model.userName))
				holder.mTextView.setText(model.userId);
			else
				holder.mTextView.setText(model.userName);
		}

		holder.checkView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (mSelectUser.contains(model)) {
					if (!isChecked)
						mSelectUser.remove(model);
				} else if (isChecked) {
					mSelectUser.add(model);
				}
			}
		});

		return convertView;
	}

	public class Holder {
		public ImageView imgView;
		public TextView mTextView;
		public CheckBox checkView;
	}
}
