package com.xmpp.im.client.listener;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.util.StringUtils;

import android.content.Intent;
import android.text.TextUtils;

import com.xmpp.im.ImApplication;
import com.xmpp.im.client.util.ImUtil;
import com.xmpp.im.client.util.XmppTool;
import com.xmpp.im.database.XmppDB;
import com.xmpp.im.model.MessageModel;
import com.xmpp.im.model.UserModel;
import com.xmpp.im.util.IMCommDefine;

/**
 * 
 * 
 * @类名称: singnalChatManagerListener
 * @描述:
 * @开发者: andy.xu
 * @时间: 2014-8-28 下午2:04:06
 * 
 */
public class SingnalChatManagerListener implements ChatManagerListener {

	@Override
	public void chatCreated(Chat chat, boolean arg1) {
		if (null != chat)
			chat.addMessageListener(new MessageListener() {
				@Override
				public void processMessage(Chat arg0, Message msg) {
					// 用户名称

					// 获取发送消息的用户
					String sendUser = msg.getFrom();
					String body = msg.getBody();
					Type chatType = msg.getType();

					if (!TextUtils.isEmpty(sendUser)) {
						if (sendUser.contains("@")) {
							sendUser = StringUtils.parseName(sendUser);
							sendUser += "@" + XmppTool.getConnection().getServiceName();
						}
					}

					MessageModel msgModel = new MessageModel();
					msgModel.fromUser = sendUser;
					msgModel.msgContent = body;
					msgModel.chatType = chatType;
					msgModel.time = System.currentTimeMillis();
					msgModel.toUser = StringUtils.parseName(msg.getTo());
					msgModel.fromFlag = 0;
					UserModel userModel = ImUtil.onGetUserModel(msgModel.fromUser);
					if (null != userModel)
						msgModel.userName = userModel.userName;

					XmppDB db = ImApplication.onGetInstance().onGetDB();
					if(null != db){
						db.onAddMessage(msgModel);
					}
					// 发出广播
					Intent intent = new Intent(IMCommDefine.broadcast_msg);
					intent.putExtra(IMCommDefine.intent_data, msgModel);
					ImApplication.onGetInstance().sendBroadcast(intent);

//					boolean left = body.substring(0, 1).equals("{");
//					boolean right = body.substring(body.length() - 1, body.length()).equals("}");
//					if (left && right) {
//						try {
//							JSONObject obj = new JSONObject(body);
//							String type = obj.getString("messageType");
//							String chanId = obj.getString("chanId");
//							String chanName = obj.getString("chanName");
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//					}
				}
			});
	}

}
