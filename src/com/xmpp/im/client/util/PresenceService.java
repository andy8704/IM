package com.xmpp.im.client.util;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.ad.util.L;
import com.ad.util.ToastUtil;
import com.xmpp.im.util.IMCommDefine;

/**
 * 
 * 
 * @类名称: PresenceService
 * @描述: 好友状态 &添加好友反馈
 * @开发者: andy.xu
 * @时间: 2014-8-29 下午6:11:39
 * 
 */
public class PresenceService extends Service {

	private XMPPConnection con;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		con = XmppTool.getConnection();
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		addPresenceListener();
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void addPresenceListener(){
		if (null != con && con.isConnected()) {
			PacketFilter filter = new AndFilter(new PacketTypeFilter(Presence.class)/*, new MessageTypeFilter(Message.Type.chat)*/);
			PacketListener listener = new PacketListener() {
				@Override
				public void processPacket(Packet packet) {
					L.i("Presence", "PresenceService------" + packet.toXML());
					// 看API可知道 Presence是Packet的子类
					if (packet instanceof Presence) {
						Presence presence = (Presence) packet;
						// Presence还有很多方法，可查看API
						String from = presence.getFrom();// 发送方
						String to = presence.getTo();// 接收方
						// Presence.Type有7中状态
						if (presence.getType().equals(Presence.Type.subscribe)) {// 好友申请
							onSubscribe(packet);
						} else if (presence.getType().equals(Presence.Type.subscribed)) {// 同意添加好友
							ToastUtil.onShowToast(getApplicationContext(), from + "已经同意添加你为好友");
						} else if (presence.getType().equals(Presence.Type.unsubscribe)) {// 拒绝添加好友
																							// 和
																							// 删除好友
							ToastUtil.onShowToast(getApplicationContext(), from + "拒绝添加你为好友");
						} else if (presence.getType().equals(Presence.Type.unsubscribed)) {// 这个我没用到
						} else if (presence.getType().equals(Presence.Type.unavailable)) {// 好友下线
																							// 要更新好友列表，可以在这收到包后，发广播到指定页面
																							// 更新列表

							Intent intent = new Intent(IMCommDefine.broadcast_presence_change);
							sendBroadcast(intent);
						} else if (presence.getType().equals(Presence.Type.available)) {// 好友上线
							Intent intent = new Intent(IMCommDefine.broadcast_presence_change);
							sendBroadcast(intent);
						}
					} else if (packet instanceof Message) {
						Message msg = (Message) packet;
						Toast.makeText(getApplicationContext(), msg.getFrom() + " 说：" + msg.getBody(), Toast.LENGTH_SHORT).show();
					}
				}
			};
			con.addPacketListener(listener, filter);
		}
	}

	private void onSubscribe(Packet packet) {
		if (null == packet)
			return;

		if (Roster.getDefaultSubscriptionMode().equals(SubscriptionMode.accept_all)) {
			Presence subscription = new Presence(Presence.Type.subscribe);
			subscription.setTo(packet.getFrom());
			XmppTool.getConnection().sendPacket(subscription);
			ToastUtil.onShowToast(this, packet.getFrom() + "申请添加为好友");
		} else {
			Intent intent = new Intent(IMCommDefine.broadcast_subscribe);
			intent.putExtra(IMCommDefine.intent_data, packet.getFrom());
			sendBroadcast(intent);
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

}
