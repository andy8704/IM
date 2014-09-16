package com.xmpp.im.client.listener;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
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
 * @ClassName: MultiChatListener
 * @Description: chatRoom 监听
 * @author andy.xu
 * @date 2014-9-16 下午11:34:47
 * 
 */
public class MultiChatListener implements PacketListener {

	@Override
	public void processPacket(Packet arg0) {
		Message msg = (Message) arg0;
		String sendUser = msg.getFrom();
		String body = msg.getBody();
		Type chatType = msg.getType();

		if (!TextUtils.isEmpty(sendUser)) {
			if (sendUser.contains("@")) {
				sendUser = StringUtils.parseName(sendUser);
				sendUser += "@" + XmppTool.getConnection().getServiceName();
			}
		}
		
		String childName = StringUtils.parseResource( msg.getFrom());
		
		MessageModel msgModel = new MessageModel();
		msgModel.fromUser = sendUser;
		msgModel.msgContent = body;
		msgModel.chatType = chatType;
		msgModel.time = System.currentTimeMillis();
		msgModel.toUser = StringUtils.parseName(msg.getTo());
		if(TextUtils.equals(childName, msgModel.toUser))
			return;
		
		msgModel.fromFlag = 0;
		UserModel userModel = ImUtil.onGetUserModel(msgModel.fromUser);
		if (null != userModel)
			msgModel.userName = userModel.userName;

//		XmppDB db = ImApplication.onGetInstance().onGetDB();
//		if(null != db){
//			db.onAddMessage(msgModel);
//		}
		// 发出广播
		Intent intent = new Intent(IMCommDefine.broadcast_chatroom_msg);
		intent.putExtra(IMCommDefine.intent_data, msgModel);
		ImApplication.onGetInstance().sendBroadcast(intent);
	}

}
