package com.xmpp.im.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmpp.im.R;
import com.xmpp.im.model.ContactGroupModel;

public class ContactGroupManagerAdapter extends BaseAdapter {

	private List<ContactGroupModel> mData = null;
	private Context mContext = null;

	public ContactGroupManagerAdapter(final Context context, final List<ContactGroupModel> data) {
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
			convertView = LayoutInflater.from(mContext).inflate(com.xmpp.im.R.layout.contac_group_adapter, null);
			holder = new Holder();
			holder.mDelBtn = (ImageView) convertView.findViewById(R.id.del_btn_id);
			holder.mTextView = (TextView) convertView.findViewById(R.id.text_id);
			holder.mView = (LinearLayout) convertView.findViewById(R.id.linearlayout_id);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		final ContactGroupModel model = mData.get(pos);
		if (null != model) {
			holder.mTextView.setText(model.name);
		}

		holder.mDelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (null != mListener)
					mListener.onDelete(model);
			}
		});
		return convertView;
	}

	public class Holder {
		public ImageView mDelBtn;
		public LinearLayout mView;
		public TextView mTextView;
	}

	private ContactOperListener mListener = null;

	public void onSetGroupOperListener(final ContactOperListener listener) {
		mListener = listener;
	}

	public interface ContactOperListener {
		public void onDelete(final ContactGroupModel group);
	}

}
