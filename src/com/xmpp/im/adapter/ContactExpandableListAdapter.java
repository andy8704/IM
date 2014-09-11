package com.xmpp.im.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ad.view.IphoneTreeView;
import com.ad.view.IphoneTreeView.IphoneTreeHeaderAdapter;
import com.xmpp.im.R;
import com.xmpp.im.model.ContactGroupModel;
import com.xmpp.im.model.GroupModel;
import com.xmpp.im.model.UserModel;

/**
 * 
 * 
 * @类名称: ContactExpandableListAdapter
 * @描述: expandlistview 展示联系人的群组
 * @开发者: andy.xu
 * @时间: 2014-8-25 下午3:18:51
 * 
 */
public class ContactExpandableListAdapter extends BaseExpandableListAdapter implements IphoneTreeHeaderAdapter {

	private List<ContactGroupModel> mGroupModel = null;
	private Map<String, List<UserModel>> mChildModel = null;
	private IphoneTreeView mView = null;
	private LayoutInflater mInflater;
	private Context mContext = null;

	/**
	 * 设置参数
	 * 
	 * @param group
	 * @param child
	 */
	public ContactExpandableListAdapter(final Context context, final List<ContactGroupModel> group, final Map<String, List<UserModel>> child,
			final IphoneTreeView view) {
		mContext = context;
		mGroupModel = group;
		mChildModel = child;
		mView = view;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {

		if (groupPosition < 0 || childPosition < 0)
			return null;

		if (null == mChildModel)
			return null;

		if (null != mGroupModel && mGroupModel.size() > groupPosition) {
			ContactGroupModel group = mGroupModel.get(groupPosition);
			if (null != mChildModel && mChildModel.containsKey(group.name)) {
				List<UserModel> child = mChildModel.get(group.name);
				if (null != child && child.size() > childPosition)
					return child.get(childPosition);
			}
		}

		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.contact_list_item_for_buddy, null);
		}
		TextView nick = (TextView) convertView.findViewById(R.id.contact_list_item_name);
		final UserModel u = (UserModel) getChild(groupPosition, childPosition);
		nick.setText(u.userName);
		TextView state = (TextView) convertView.findViewById(R.id.cpntact_list_item_state);
		state.setText(u.onGetState(mContext));
		ImageView head = (ImageView) convertView.findViewById(R.id.icon);
		// head.setImageResource(PushApplication.heads[u.getHeadIcon()]);
		// 必须使用资源Id当key（不是资源id会出现运行时异常），android本意应该是想用tag来保存资源id对应组件。
		// 将groupPosition，childPosition通过setTag保存,在onItemLongClick方法中就可以通过view参数直接拿到了
		// 我这里的xxx01和xxx02是两个长宽都为0的空view，只是用来setTag

		ImageView statusIcon = (ImageView) convertView.findViewById(R.id.stateicon);
		statusIcon.setImageResource(onGetCurStatusIcon(u));
		convertView.setTag(R.id.xxx01, groupPosition);
		convertView.setTag(R.id.xxx02, childPosition);

		return convertView;
	}

	private int onGetCurStatusIcon(final UserModel model) {
		if (null == model)
			return R.drawable.im_status_online;

		switch (model.statue) {
		case 5:
			return R.drawable.im_status_offline;
		case 0:
			return R.drawable.im_status_online;
		case 3:
			return R.drawable.im_status_away;
		case 1:
			return R.drawable.im_status_q;
		case 2:
			return R.drawable.im_status_dnd;
		case 4:
			return R.drawable.im_status_offline;
		}
		return R.drawable.im_status_online;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (groupPosition < 0)
			return 0;

		if (null != mGroupModel && mGroupModel.size() > groupPosition) {
			ContactGroupModel group = mGroupModel.get(groupPosition);
			if (null != group) {
				if (null != mChildModel) {
					if (mChildModel.containsKey(group.name)) {
						List<UserModel> child = mChildModel.get(group.name);
						if (null != child)
							return child.size();
					}
				}
			}
		}
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		if (groupPosition < 0)
			return null;

		if (null != mGroupModel && mGroupModel.size() > groupPosition)
			return mGroupModel.get(groupPosition);
		return null;
	}

	@Override
	public int getGroupCount() {
		return mGroupModel.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.contact_buddy_list_group, null);
		}
		TextView groupName = (TextView) convertView.findViewById(R.id.group_name);
		ContactGroupModel model = mGroupModel.get(groupPosition);
		groupName.setText(model.name);
		TextView onlineNum = (TextView) convertView.findViewById(R.id.online_count);
		onlineNum.setText(getChildrenCount(groupPosition) + "/" + getChildrenCount(groupPosition));
		ImageView indicator = (ImageView) convertView.findViewById(R.id.group_indicator);
		if (isExpanded)
			indicator.setImageResource(R.drawable.indicator_expanded);
		else
			indicator.setImageResource(R.drawable.indicator_unexpanded);
		// 必须使用资源Id当key（不是资源id会出现运行时异常），android本意应该是想用tag来保存资源id对应组件。
		// 将groupPosition，childPosition通过setTag保存,在onItemLongClick方法中就可以通过view参数直接拿到了
		convertView.setTag(R.id.xxx01, groupPosition);
		convertView.setTag(R.id.xxx02, -1);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public int getTreeHeaderState(int groupPosition, int childPosition) {
		final int childCount = getChildrenCount(groupPosition);
		if (childPosition == childCount - 1) {
			return PINNED_HEADER_PUSHED_UP;
		} else if (childPosition == -1 && !mView.isGroupExpanded(groupPosition)) {
			return PINNED_HEADER_GONE;
		} else {
			return PINNED_HEADER_VISIBLE;
		}
	}

	private HashMap<Integer, Integer> groupStatusMap = new HashMap<Integer, Integer>();

	@Override
	public void configureTreeHeader(View header, int groupPosition, int childPosition, int alpha) {
		ContactGroupModel group = mGroupModel.get(groupPosition);
		((TextView) header.findViewById(R.id.group_name)).setText(group.name);
		((TextView) header.findViewById(R.id.online_count)).setText(getChildrenCount(groupPosition) + "/" + getChildrenCount(groupPosition));
	}

	@Override
	public void onHeadViewClick(int groupPosition, int status) {
		groupStatusMap.put(groupPosition, status);
	}

	@Override
	public int getHeadViewClickStatus(int groupPosition) {
		if (groupStatusMap.containsKey(groupPosition)) {
			return groupStatusMap.get(groupPosition);
		} else {
			return 0;
		}
	}

}
