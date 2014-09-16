package com.xmpp.im.client.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.pubsub.PresenceState;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import com.ad.util.L;
import com.xmpp.im.model.MessageModel;
import com.xmpp.im.model.UserModel;
import com.xmpp.im.util.IMCommDefine;

/**
 * 
 * @ClassName: XmppTool
 * @Description: Xmpp通讯的基本工具类
 * @author andy.xu
 * @date 2014-8-24 下午6:23:22
 * 
 */
public class XmppTool {

	private static XMPPConnection con = null;
	private static String mCurUserId = null;
	private static String SERVICE_HOST = "123.57.9.238";

	{
		try {
			Class.forName("org.jivesoftware.smack.ReconnectionManager");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void openConnection() {
		try {
			// url、端口，也可以设置连接的服务器名字，地址，端口，用户。
			configureConnection(ProviderManager.getInstance());
			ConnectionConfiguration connConfig = new ConnectionConfiguration(SERVICE_HOST, 5222);

			connConfig.setReconnectionAllowed(true);
			connConfig.setSendPresence(false);
			con = new XMPPConnection(connConfig);
			con.connect();
		} catch (XMPPException xe) {
			xe.printStackTrace();
		}
	}

	public static void onOffline() {
		Presence presence = new Presence(Presence.Type.available);
		con.sendPacket(presence);
	}

	public static void onLine() {
		Presence presence = new Presence(Presence.Type.available);
		con.sendPacket(presence);
	}

	public static XMPPConnection getConnection() {
		if (con == null) {
			openConnection();
		}
		return con;
	}

	public static void closeConnection() {
		con.disconnect();
		con = null;
	}

	/**
	 * 获取所有的群组聊天室
	 * 
	 * @return
	 */
	public static List<HostedRoom> onGetHostedRoom() {
		Collection<HostedRoom> rooms = null;
		try {
			new ServiceDiscoveryManager(getConnection());
			// rooms = MultiUserChat.getHostedRooms(XmppTool.getConnection(),
			// "conference.192.168.1.102");
			rooms = MultiUserChat.getHostedRooms(XmppTool.getConnection(), "conference." + getConnection().getServiceName());
			if (null != rooms && !rooms.isEmpty()) {
				List<HostedRoom> roomsList = new ArrayList<HostedRoom>();
				roomsList.addAll(rooms);
				return roomsList;
			}
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void onSetUserId(final String userId) {
		mCurUserId = userId;
	}

	public static String onGetUserId() {
		return mCurUserId;
	}

	/**
	 * 注册一个账号
	 * 
	 * @param accoutName
	 * @param password
	 * @return 1、注册成功 0、服务器没有返回结果2、这个账号已经存在3、注册失败
	 */
	public static int onRegist(final XMPPConnection connect, final String accoutName, final String password) {
		if (null == connect || TextUtils.isEmpty(accoutName) || TextUtils.isEmpty(password))
			return 0;

		Registration reg = new Registration();
		reg.setType(IQ.Type.SET);
		reg.setTo(connect.getServiceName());
		// 注意这里createAccount注册时，参数是UserName，不是jid，是"@"前面的部分。
		reg.setUsername(accoutName);
		reg.setPassword(password);
		// 这边addAttribute不能为空，否则出错。所以做个标志是android手机创建的吧！！！！！
		reg.addAttribute("android", "geolo_createUser_android");
		PacketFilter filter = new AndFilter(new PacketIDFilter(reg.getPacketID()), new PacketTypeFilter(IQ.class));
		PacketCollector collector = connect.createPacketCollector(filter);
		connect.sendPacket(reg);
		IQ result = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
		// Stop queuing results停止请求results（是否成功的结果）
		collector.cancel();
		if (result == null) {
			return 0;
		} else if (result.getType() == IQ.Type.RESULT) {
			return 1;
		} else { // if (result.getType() == IQ.Type.ERROR)
			if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
				return 2;
			} else {
				return 3;
			}
		}
	}

	/**
	 * 
	 * @描述:
	 * @参数 @param pre
	 * @参数 @return
	 * @返回值 int
	 * @异常 0：在线 1： Q我把 2 ： 忙碌 3：离开 4： 隐身 5：离线
	 */
	public static int onGetPresence(final Presence pre) {
		if (null == pre)
			return 1;

		int nState = 5;
		Presence.Type curType = pre.getType();
		if (null != curType) {
			if (curType == Type.available) {
				Mode mode = pre.getMode();
				if (null == mode) {
					nState = 0;
				} else {
					if (Mode.chat == mode) {
						nState = 1;
					} else if (Mode.away == mode) {
						nState = 3;
					} else if (Mode.dnd == mode) {
						nState = 2;
					}
				}

			} else if (curType == Type.unavailable) {
				// 离线
				nState = 5;
				String packId = pre.getPacketID();
				if (!TextUtils.isEmpty(packId)) {
					if (TextUtils.equals(packId, Packet.ID_NOT_AVAILABLE))
						nState = 4;
				}

			}
		}
		return nState;
	}

	/**
	 * 更改用户状态 :0：在线 1： Q我把 2 ： 忙碌 3：离开 4： 隐身 5：离线
	 */
	public static void setPresence(int code) {
		XMPPConnection con = getConnection();
		if (con == null)
			return;
		Presence presence;
		switch (code) {
		case 0:
			presence = new Presence(Presence.Type.available);
			con.sendPacket(presence);
			L.v("state", "设置在线");
			break;
		case 1:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.chat);
			con.sendPacket(presence);
			L.v("state", "设置Q我吧");
			break;
		case 2:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.dnd);
			con.sendPacket(presence);
			L.v("state", "设置忙碌");
			break;
		case 3:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.away);
			con.sendPacket(presence);
			L.v("state", "设置离开");
			break;
		case 4:
			Roster roster = con.getRoster();
			Collection<RosterEntry> entries = roster.getEntries();
			for (RosterEntry entry : entries) {
				presence = new Presence(Presence.Type.unavailable);
				presence.setPacketID(Packet.ID_NOT_AVAILABLE);
				presence.setFrom(con.getUser());
				presence.setTo(entry.getUser());
				con.sendPacket(presence);
				L.v("state", presence.toXML());
			}
			// 向同一用户的其他客户端发送隐身状态
			presence = new Presence(Presence.Type.unavailable);
			presence.setPacketID(Packet.ID_NOT_AVAILABLE);
			presence.setFrom(con.getUser());
			presence.setTo(StringUtils.parseBareAddress(con.getUser()));
			con.sendPacket(presence);
			L.v("state", "设置隐身");
			break;
		case 5:
			presence = new Presence(Presence.Type.unavailable);
			con.sendPacket(presence);
			L.v("state", "设置离线");
			break;
		default:
			break;
		}
	}

	/**
	 * 获取所有组
	 * 
	 * @return 所有组集合
	 */
	public static List<RosterGroup> getGroups() {
		if (getConnection() == null)
			return null;
		List<RosterGroup> grouplist = new ArrayList<RosterGroup>();
		Collection<RosterGroup> rosterGroup = getConnection().getRoster().getGroups();
		Iterator<RosterGroup> i = rosterGroup.iterator();
		while (i.hasNext()) {
			grouplist.add(i.next());
		}
		return grouplist;
	}

	/**
	 * 获取某个组里面的所有好友
	 * 
	 * @param roster
	 * @param groupName
	 *            组名
	 * @return
	 */
	public static List<RosterEntry> getEntriesByGroup(String groupName) {
		if (getConnection() == null)
			return null;
		List<RosterEntry> Entrieslist = new ArrayList<RosterEntry>();
		RosterGroup rosterGroup = getConnection().getRoster().getGroup(groupName);
		Collection<RosterEntry> rosterEntry = rosterGroup.getEntries();
		Iterator<RosterEntry> i = rosterEntry.iterator();
		while (i.hasNext()) {
			Entrieslist.add(i.next());
		}
		return Entrieslist;
	}

	/**
	 * 获取所有好友信息
	 * 
	 * @return
	 */
	public static List<RosterEntry> getAllEntries() {
		if (getConnection() == null)
			return null;
		List<RosterEntry> Entrieslist = new ArrayList<RosterEntry>();
		Collection<RosterEntry> rosterEntry = getConnection().getRoster().getEntries();
		Iterator<RosterEntry> i = rosterEntry.iterator();
		while (i.hasNext()) {
			Entrieslist.add(i.next());
		}
		return Entrieslist;
	}

	/**
	 * 
	 * @描述: 获取所有的好友列表
	 * @参数 @return
	 * @返回值 List<UserModel>
	 * @异常
	 */
	public static List<UserModel> getAllUser() {
		if (getConnection() == null)
			return null;

		List<UserModel> userList = new ArrayList<UserModel>();
		Collection<RosterEntry> rosterEntry = getConnection().getRoster().getEntries();
		Iterator<RosterEntry> i = rosterEntry.iterator();
		while (i.hasNext()) {
			RosterEntry roster = i.next();
			if (null != roster) {
				UserModel model = new UserModel();
				model.userName = roster.getName();
				model.userId = roster.getUser();
				userList.add(model);
			}
		}
		return userList;
	}

	/**
	 * 获取用户VCard信息
	 * 
	 * @param connection
	 * @param user
	 * @return
	 * @throws XMPPException
	 */
	public static VCard getUserVCard(String user) {
		if (getConnection() == null)
			return null;

		ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new org.jivesoftware.smackx.provider.VCardProvider());
		VCard vcard = new VCard();
		try {
			vcard.load(getConnection(), user);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return vcard;
	}

	public static UserModel getUserModel(String user) {
		if (TextUtils.isEmpty(user))
			return null;

		VCard card = getUserVCard(user);
		if (null == card)
			return null;

		UserModel userModel = new UserModel();
		userModel.addr = card.getField(IMCommDefine.USER_ADDR);
		userModel.signatrue = card.getField(IMCommDefine.USER_SIGNATURE);
		userModel.nickName = card.getNickName();

		return userModel;
	}

	public static UserModel getOwnerModel() {
		if (getConnection() == null)
			return null;
		VCard vcard = new VCard();
		try {
			vcard.load(getConnection());
			UserModel userModel = new UserModel();
			userModel.addr = vcard.getField(IMCommDefine.USER_ADDR);
			userModel.signatrue = vcard.getField(IMCommDefine.USER_SIGNATURE);
			userModel.nickName = vcard.getNickName();
			return userModel;
		} catch (XMPPException e) {
			e.printStackTrace();
			L.e("im", e.toString());
		}
		return null;
	}

	public static void onSetUserModel(final String user, final UserModel userInfo) {
		if (TextUtils.isEmpty(user) || null == userInfo)
			return;

		VCard card = getUserVCard(user);
		if (null == card)
			return;

		if (!TextUtils.isEmpty(userInfo.nickName))
			card.setNickName(userInfo.nickName);

		if (!TextUtils.isEmpty(userInfo.addr))
			card.setField(IMCommDefine.USER_ADDR, userInfo.addr);

		if (!TextUtils.isEmpty(userInfo.signatrue))
			card.setField(IMCommDefine.USER_SIGNATURE, userInfo.signatrue);

		try {
			card.save(getConnection());
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	public static void onSetNickName(final String user, final String nickName) {
		if (TextUtils.isEmpty(user) || TextUtils.isEmpty(nickName))
			return;

		RosterEntry entry = getConnection().getRoster().getEntry(user);
		if (null != entry)
			entry.setName(nickName);
	}

	/**
	 * 获取用户头像信息
	 * 
	 * @param connection
	 * @param user
	 * @return
	 */
	public static Drawable getUserImage(String user) {
		if (getConnection() == null)
			return null;
		ByteArrayInputStream bais = null;
		try {
			VCard vcard = new VCard();
			// 加入这句代码，解决No VCard for
			ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new org.jivesoftware.smackx.provider.VCardProvider());
			if (user == "" || user == null || user.trim().length() <= 0) {
				return null;
			}
			vcard.load(getConnection(), user /*
											 * + "@" +
											 * getConnection().getServiceName()
											 */);

			if (vcard == null || vcard.getAvatar() == null)
				return null;
			bais = new ByteArrayInputStream(vcard.getAvatar());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return Drawable.createFromStream(bais, "drawableName");
	}

	/**
	 * 添加一个分组
	 * 
	 * @param groupName
	 * @return
	 */
	public static boolean addGroup(final String groupName) {
		if (getConnection() == null)
			return false;

		new Thread() {
			public void run() {
				try {
					RosterGroup group = getConnection().getRoster().createGroup(groupName);
					if (null != group) {
						L.v("addGroup", groupName + "創建成功");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			};
		}.start();
		return false;
	}

	/**
	 * 
	 * @描述: 修改组的名称
	 * @参数 @param oldGroupName
	 * @参数 @param newGroupName
	 * @参数 @return
	 * @返回值 boolean
	 * @异常
	 */
	public static boolean reNameGroupName(final String oldGroupName, final String newGroupName) {
		if (TextUtils.isEmpty(oldGroupName) || TextUtils.isEmpty(newGroupName))
			return false;

		RosterGroup group = getConnection().getRoster().getGroup(oldGroupName);
		if (null != group) {
			group.setName(newGroupName);
			return true;
		}
		return false;
	}

	/**
	 * 删除分组
	 * 
	 * @param groupName
	 * @return
	 */
	public static boolean removeGroup(String groupName) {
		// 需要删除该分组下的所有的好友， 这样分组就不存在了
		return true;
	}

	/**
	 * 添加好友 无分组
	 * 
	 * @param userName
	 * @param name
	 * @return
	 */
	public static boolean addUser(String userName, String name) {
		if (getConnection() == null)
			return false;
		try {
			getConnection().getRoster().createEntry(userName, name, null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 添加好友 有分组
	 * 
	 * @param userName
	 * @param name
	 * @param groupName
	 * @return
	 */
	public static boolean addUser(String userName, String name, String groupName) {
		if (getConnection() == null)
			return false;
		try {
			Presence subscription = new Presence(Presence.Type.subscribed);
			subscription.setTo(userName);
			userName += "@" + getConnection().getServiceName();
			getConnection().sendPacket(subscription);
			getConnection().getRoster().createEntry(userName, name, new String[] { groupName });
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 
	 * @描述: 修改用户的所在群组
	 * @参数 @param user
	 * @参数 @param groupName
	 * @参数 @return
	 * @返回值 boolean
	 * @异常
	 */
	public static boolean onChangeUserGroup(final String user, final String groupName) {
		if (TextUtils.isEmpty(user) || TextUtils.isEmpty(groupName))
			return false;

		RosterGroup group = getConnection().getRoster().getGroup(groupName);
		RosterEntry userEntry = getConnection().getRoster().getEntry(user);

		try {
			if (null != userEntry) {
				if (null != group) {
					group.addEntry(userEntry);
				} else {
					group = getConnection().getRoster().createGroup(groupName);
					if (null != group)
						group.addEntry(userEntry);
				}
			}
		} catch (XMPPException e) {
			L.v(e.toString());
		}
		return false;
	}

	/**
	 * 删除好友
	 * 
	 * @param userName
	 * @return
	 */
	public static boolean removeUser(String userName) {
		if (getConnection() == null)
			return false;
		try {
			RosterEntry entry = null;
			if (userName.contains("@"))
				entry = getConnection().getRoster().getEntry(userName);
			else
				entry = getConnection().getRoster().getEntry(userName + "@" + getConnection().getServiceName());
			if (entry == null)
				entry = getConnection().getRoster().getEntry(userName);
			getConnection().getRoster().removeEntry(entry);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 查询用户
	 * 
	 * @param userName
	 * @return
	 * @throws XMPPException
	 */
	public static List<UserModel> searchUsers(String userName) {
		if (getConnection() == null)
			return null;
		List<UserModel> userList = new ArrayList<UserModel>();
		try {
			new ServiceDiscoveryManager(getConnection());
			UserSearchManager usm = new UserSearchManager(getConnection());
			String searchStr = "search." + getConnection().getServiceName();
			Form searchForm = usm.getSearchForm(searchStr);

			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("Username", true);
			answerForm.setAnswer("Name", true);
			answerForm.setAnswer("search", userName);
			ReportedData data = usm.getSearchResults(answerForm, searchStr);

			Iterator<Row> it = data.getRows();
			Row row = null;
			while (it.hasNext()) {
				row = it.next();
				String userId = row.getValues("Username").next().toString();
				String nikeName = row.getValues("Name").next().toString();
				UserModel user = new UserModel();
				user.userId = userId;
				user.userName = nikeName;
				userList.add(user);
				// 若存在，则有返回,UserName一定非空，其他两个若是有设，一定非空
			}
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return userList;
	}

	/**
	 * 修改心情
	 * 
	 * @param connection
	 * @param status
	 */
	public static void changeStateMessage(String status) {
		if (getConnection() == null)
			return;
		Presence presence = new Presence(Presence.Type.available);
		presence.setStatus(status);
		getConnection().sendPacket(presence);
	}

	/**
	 * 修改用户头像
	 * 
	 * @param file
	 */
	public static boolean changeImage(File file) {
		if (getConnection() == null)
			return false;
		try {
			VCard vcard = new VCard();
			vcard.load(getConnection());

			byte[] bytes;

			bytes = getFileBytes(file);
			String encodedImage = StringUtils.encodeBase64(bytes);
			vcard.setAvatar(bytes, encodedImage);
			vcard.setEncodedImage(encodedImage);
			vcard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>" + encodedImage + "</BINVAL>", true);

			ByteArrayInputStream bais = new ByteArrayInputStream(vcard.getAvatar());

			// FormatTools.getInstance().InputStream2Bitmap(bais);

			vcard.save(getConnection());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 文件转字节
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private static byte[] getFileBytes(File file) throws IOException {
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			int bytes = (int) file.length();
			byte[] buffer = new byte[bytes];
			int readBytes = bis.read(buffer);
			if (readBytes != buffer.length) {
				throw new IOException("Entire file not read");
			}
			return buffer;
		} finally {
			if (bis != null) {
				bis.close();
			}
		}
	}

	/**
	 * 注销当前用户
	 * 
	 * @return
	 */
	public static boolean deleteAccount() {
		if (getConnection() == null)
			return false;
		try {
			getConnection().getAccountManager().deleteAccount();
			return true;
		} catch (XMPPException e) {
			return false;
		}
	}

	/**
	 * 修改密码
	 * 
	 * @return
	 */
	public static boolean changePassword(String pwd) {
		if (getConnection() == null)
			return false;
		try {
			getConnection().getAccountManager().changePassword(pwd);
			return true;
		} catch (XMPPException e) {
			return false;
		}
	}

	/**
	 * 创建房间
	 * 
	 * @param roomName
	 *            房间名称
	 */
	public static MultiUserChat createRoom(String roomName, String password) {
		if (getConnection() == null)
			return null;

		MultiUserChat muc = null;
		try {
			// 创建一个MultiUserChat
			muc = new MultiUserChat(getConnection(), roomName + "@conference." + getConnection().getServiceName());
			// 创建聊天室
			muc.create(roomName);
			// 获得聊天室的配置表单
			Form form = muc.getConfigurationForm();
			// 根据原始表单创建一个要提交的新表单。
			Form submitForm = form.createAnswerForm();
			// 向要提交的表单添加默认答复
			for (Iterator<FormField> fields = form.getFields(); fields.hasNext();) {
				FormField field = (FormField) fields.next();
				if (!FormField.TYPE_HIDDEN.equals(field.getType()) && field.getVariable() != null) {
					// 设置默认值作为答复
					submitForm.setDefaultAnswer(field.getVariable());
				}
			}
			// 设置聊天室的新拥有者
			List<String> owners = new ArrayList<String>();
			owners.add(getConnection().getUser());// 用户JID
			submitForm.setAnswer("muc#roomconfig_roomowners", owners);
			// 设置聊天室是持久聊天室，即将要被保存下来
			submitForm.setAnswer("muc#roomconfig_persistentroom", true);
			// 房间仅对成员开放
			submitForm.setAnswer("muc#roomconfig_membersonly", false);
			// 允许占有者邀请其他人
			submitForm.setAnswer("muc#roomconfig_allowinvites", true);
			if (!password.equals("")) {
				// 进入是否需要密码
				submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);
				// 设置进入密码
				submitForm.setAnswer("muc#roomconfig_roomsecret", password);
			}
			// 能够发现占有者真实 JID 的角色
			// submitForm.setAnswer("muc#roomconfig_whois", "anyone");
			// 登录房间对话
			submitForm.setAnswer("muc#roomconfig_enablelogging", true);
			// 仅允许注册的昵称登录
			submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
			// 允许使用者修改昵称
			submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
			// 允许用户注册房间
			submitForm.setAnswer("x-muc#roomconfig_registration", false);
			// 发送已完成的表单（有默认值）到服务器来配置聊天室
			muc.sendConfigurationForm(submitForm);
		} catch (XMPPException e) {
			e.printStackTrace();
			return null;
		}
		return muc;
	}

	/**
	 * 加入会议室
	 * 
	 * @param user
	 *            昵称
	 * @param password
	 *            会议室密码
	 * @param roomsName
	 *            会议室名
	 */
	public static MultiUserChat joinMultiUserChat(String user, String roomsName, String password) {
		if (getConnection() == null)
			return null;
		try {
			// 使用XMPPConnection创建一个MultiUserChat窗口
			MultiUserChat muc = new MultiUserChat(getConnection(), roomsName + "@conference." + getConnection().getServiceName());
			// 聊天室服务将会决定要接受的历史记录数量
			DiscussionHistory history = new DiscussionHistory();
			history.setMaxChars(0);
			// history.setSince(new Date());
			// 用户加入聊天室
			muc.join(user, password, history, SmackConfiguration.getPacketReplyTimeout());
			L.i("MultiUserChat", "会议室【" + roomsName + "】加入成功........");
			return muc;
		} catch (XMPPException e) {
			e.printStackTrace();
			L.i("MultiUserChat", "会议室【" + roomsName + "】加入失败........");
			return null;
		}
	}

	/**
	 * 
	 * @描述: 邀请用户加入会议室
	 * @参数 @param chat
	 * @参数 @param userId
	 * @参数 @param despReason
	 * @返回值 void
	 * @异常
	 */
	public static void inviteUserToChatRoom(final MultiUserChat chat, final String userId, final String despReason) {
		if (null == chat || TextUtils.isEmpty(userId))
			return;

		chat.invite(userId, despReason);
	}

	public static void inviteUserToChatRoom(final MultiUserChat chat, final UserModel user, final String despReason) {
		if (null == chat || null == user)
			return;

		inviteUserToChatRoom(chat, user.userId, despReason);
	}

	/**
	 * 
	 * @描述: 邀请多人进入聊天室
	 * @参数 @param chat
	 * @参数 @param userList
	 * @参数 @param despReason
	 * @返回值 void
	 * @异常
	 */
	public static void inviteUsersToChatRoom(final MultiUserChat chat, final List<UserModel> userList, final String despReason) {
		if (null == chat || null == userList || userList.isEmpty())
			return;

		for (UserModel ele : userList) {
			if (null != ele)
				inviteUserToChatRoom(chat, ele, despReason);
		}
	}

	/**
	 * 
	 * @描述:离开房间
	 * @参数 @param chat
	 * @返回值 void
	 * @异常
	 */
	public static void onLeaveCharRoom(final MultiUserChat chat) {
		if (null == chat)
			return;

		chat.leave();
	}

	/**
	 * 查询会议室成员名字
	 * 
	 * @param muc
	 */
	public static List<String> findMulitUser(MultiUserChat muc) {
		if (getConnection() == null)
			return null;

		List<String> listUser = new ArrayList<String>();
		Iterator<String> it = muc.getOccupants();
		// 遍历出聊天室人员名称
		while (it.hasNext()) {
			// 聊天室成员名字
			String name = StringUtils.parseResource(it.next());
			listUser.add(name);
		}
		return listUser;
	}

	/**
	 * 发送文件
	 * 
	 * @param user
	 * @param filePath
	 */
	public static void sendFile(String user, String filePath) {
		if (getConnection() == null)
			return;
		// 创建文件传输管理器
		FileTransferManager manager = new FileTransferManager(getConnection());

		// 创建输出的文件传输
		OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(user);

		// 发送文件
		try {
			transfer.sendFile(new File(filePath), "You won't believe this!");
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取离线消息
	 * 
	 * @return
	 */
	public static Map<String, List<MessageModel>> getOffLineMessage() {
		if (getConnection() == null)
			return null;
		Map<String, List<MessageModel>> offlineMsgs = null;
		try {
			OfflineMessageManager offlineManager = new OfflineMessageManager(getConnection());
			Iterator<Message> it = offlineManager.getMessages();

			int count = offlineManager.getMessageCount();
			if (count <= 0)
				return null;
			offlineMsgs = new HashMap<String, List<MessageModel>>();

			while (it.hasNext()) {
				Message message = it.next();
				String fromUser = StringUtils.parseName(message.getFrom());
				// HashMap<String, String> histrory = new HashMap<String,
				// String>();
				// histrory.put("useraccount",
				// StringUtils.parseName(getConnection().getUser()));
				// histrory.put("friendaccount", fromUser);
				// histrory.put("info", message.getBody());
				// histrory.put("type", "left");

				// 获取发送消息的用户
				String sendUser = message.getFrom();
				String body = message.getBody();
				org.jivesoftware.smack.packet.Message.Type chatType = message.getType();

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
				UserModel userModel = ImUtil.onGetUserModel(msgModel.fromUser);
				if (null != userModel)
					msgModel.userName = userModel.userName;

				if (offlineMsgs.containsKey(msgModel.fromUser)) {
					offlineMsgs.get(msgModel.fromUser).add(msgModel);
				} else {
					List<MessageModel> temp = new ArrayList<MessageModel>();
					temp.add(msgModel);
					offlineMsgs.put(msgModel.fromUser, temp);
				}
			}
			offlineManager.deleteMessages();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return offlineMsgs;
	}

	/**
	 * 判断OpenFire用户的状态 strUrl : url格式 -
	 * http://my.openfire.com:9090/plugins/presence
	 * /status?jid=user1@SERVER_NAME&type=xml 返回值 : 0 - 用户不存在; 1 - 用户在线; 2 -
	 * 用户离线 说明 ：必须要求 OpenFire加载 presence 插件，同时设置任何人都可以访问
	 */
	// public int IsUserOnLine(String user) {
	// String url = "http://"+SERVICE_HOST+":9090/plugins/presence/status?" +
	// "jid="+ user +"@"+ SERVER_NAME +"&type=xml";
	// int shOnLineState = 0; // 不存在
	// try {
	// URL oUrl = new URL(url);
	// URLConnection oConn = oUrl.openConnection();
	// if (oConn != null) {
	// BufferedReader oIn = new BufferedReader(new InputStreamReader(
	// oConn.getInputStream()));
	// if (null != oIn) {
	// String strFlag = oIn.readLine();
	// oIn.close();
	// System.out.println("strFlag"+strFlag);
	// if (strFlag.indexOf("type=\"unavailable\"") >= 0) {
	// shOnLineState = 2;
	// }
	// if (strFlag.indexOf("type=\"error\"") >= 0) {
	// shOnLineState = 0;
	// } else if (strFlag.indexOf("priority") >= 0
	// || strFlag.indexOf("id=\"") >= 0) {
	// shOnLineState = 1;
	// }
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// return shOnLineState;
	// }

	/**
	 * 加入providers的函数 ASmack在/META-INF缺少一个smack.providers 文件
	 * 
	 * @param pm
	 */
	public static void configureConnection(ProviderManager pm) {

		// Private Data Storage
		pm.addIQProvider("query", "jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());

		// Time
		try {
			pm.addIQProvider("query", "jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (ClassNotFoundException e) {
			Log.w("TestClient", "Can't load class for org.jivesoftware.smackx.packet.Time");
		}

		// Roster Exchange
		pm.addExtensionProvider("x", "jabber:x:roster", new RosterExchangeProvider());

		// Message Events
		pm.addExtensionProvider("x", "jabber:x:event", new MessageEventProvider());

		// Chat State
		pm.addExtensionProvider("active", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		pm.addExtensionProvider("composing", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		pm.addExtensionProvider("paused", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		pm.addExtensionProvider("inactive", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		pm.addExtensionProvider("gone", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

		// XHTML
		pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());

		// Group Chat Invitations
		pm.addExtensionProvider("x", "jabber:x:conference", new GroupChatInvitation.Provider());

		// Service Discovery # Items
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());

		// Service Discovery # Info
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());

		// Data Forms
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

		// MUC User
		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user", new MUCUserProvider());

		// MUC Admin
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin", new MUCAdminProvider());

		// MUC Owner
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());

		// Delayed Delivery
		pm.addExtensionProvider("x", "jabber:x:delay", new DelayInformationProvider());

		// Version
		try {
			pm.addIQProvider("query", "jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
			// Not sure what's happening here.
		}

		// VCard
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());

		// Offline Message Requests
		pm.addIQProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());

		// Offline Message Indicator
		pm.addExtensionProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());

		// Last Activity
		pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());

		// User Search
		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());

		// SharedGroupsInfo
		pm.addIQProvider("sharedgroup", "http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());

		// JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses", "http://jabber.org/protocol/address", new MultipleAddressesProvider());

		// FileTransfer
		pm.addIQProvider("si", "http://jabber.org/protocol/si", new StreamInitiationProvider());

		pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());

		// Privacy
		pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
		pm.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
		pm.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.MalformedActionError());
		pm.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadLocaleError());
		pm.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadPayloadError());
		pm.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadSessionIDError());
		pm.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.SessionExpiredError());
	}

	/**
	 * 
	* @描述: 获取离线添加好友请求
	* @参数 @return
	* @返回值 Presence
	* @异常
	 */
	public static Presence getOfflineRoseterRequest() {
		Presence presence = new Presence(Presence.Type.available, null, 0, Presence.Mode.available) {
			public String getExtensionsXML() {
				return "＜c node=\"http://exodus.jabberstudio.org/caps\" ver=\"0.9.1.0\" xmlns=\"http://jabber.org/protocol/caps\"/＞";
			}
		};
		return presence;
	}
}