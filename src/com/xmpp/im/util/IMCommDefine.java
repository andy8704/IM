package com.xmpp.im.util;

public interface IMCommDefine {

	/**
	 * intent传参 -- userId
	 */
	public String intent_userId = "userId";

	/**
	 * intent传参 -- intentData
	 */
	public String intent_data = "intent_data";

	/**
	 * 消息监听广播发出的事件
	 */
	public String broadcast_msg = "broadcastMsg";
	
	/**
	 * 
	 */
	public String broadcast_chatroom_msg = "broadcastChatRoomMsg";

	/**
	 * 群组发生改变的广播事件
	 */
	public String broadcast_chatroom_change = "broadcastChatroomChange";

	/**
	 * 分组发生改变
	 */
	public String broadcast_group_change = "broadcastGroupChange";
	
	/**
	 * 添加好友
	 */
	public String broadcast_add_roster = "roster.add";
	
	/**
	 * 删除删除好友
	 */
	public String broadcast_delete_roster = "roster.del";
	
	/**
	 * 好友状态改变
	 */
	public String broadcast_presence_change = "roster.presence.change";
	
	/**
	 * 好友邀请
	 */
	public String broadcast_subscribe = "roster.subscribe";
	

	/**
	 * 用户签名
	 */
	public String USER_SIGNATURE = "userSignature";

	/**
	 * 用户地址
	 */
	public String USER_ADDR = "userAddr";
}
