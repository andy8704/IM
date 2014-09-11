package com.xmpp.im.model;

import java.io.Serializable;

import com.xmpp.im.R;

import android.content.Context;

/**
 * 
 * 
 * @类名称: UserModel
 * @描述: 用户信息
 * @开发者: andy.xu
 * @时间: 2014-8-25 下午2:57:02
 * 
 */
public class UserModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8199898514146550842L;

	/**
	 * 用户名称
	 */
	public String userName;
	
	/**
	 * 昵称
	 */
	public String nickName;

	/**
	 * 用户的唯一编号
	 */
	public String userId;

	/**
	 * 0：在线 1： Q我把 2 ： 忙碌 3：离开 4： 隐身 5：离线
	 */
	public int statue;

	/**
	 * 个人签名
	 */
	public String signatrue;
	
	/**
	 * 地址
	 */
	public String addr;
	
	/**
	 * 临时使用，是否被选中
	 */
	public boolean bSelectFlag;
	
	/**
	 * 隶属的群组
	 */
	public String groupName;

	public String onGetState(final Context context) {
		switch (statue) {
		case 5:
			return context.getString(R.string.im_statue_offline_str);
		case 0:
			return context.getString(R.string.im_statue_online_str);
		case 3:
			return context.getString(R.string.im_statue_away_str);
		case 1:
			return context.getString(R.string.im_statue_q_str);
		case 2:
			return context.getString(R.string.im_statue_dnd_str);
		case 4:
			return context.getString(R.string.im_statue_hide_str);
		}

		return context.getString(R.string.im_statue_offline_str);
	}
}
