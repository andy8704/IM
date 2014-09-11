package com.xmpp.im;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ad.bitmap.BitmapDecodeUtil;
import com.ad.util.ToastUtil;
import com.ad.view.PullListView;
import com.ad.view.PullListView.IXListViewListener;
import com.xmpp.im.adapter.MessageShowAdapter;
import com.xmpp.im.client.util.ImUtil;
import com.xmpp.im.client.util.XmppTool;
import com.xmpp.im.database.DBHelper.UserMessage;
import com.xmpp.im.database.SearchModel;
import com.xmpp.im.database.XmppDB;
import com.xmpp.im.model.MessageModel;
import com.xmpp.im.model.UserModel;
import com.xmpp.im.util.IMCommDefine;

/**
 * 
 * 
 * @类名称: FormClientActivity
 * @描述: 客户端聊天
 * @开发者: andy.xu
 * @时间: 2014-8-25 上午11:12:33
 * 
 */
public class IMClientActivity extends Activity implements IXListViewListener {

	private String mUserChatId = "";
	private MessageShowAdapter mAdapter;
	private List<MessageModel> listMsg = new ArrayList<MessageModel>();
	private String mCurUserId = null;
	private EditText mInputView;
	private ProgressBar mProgressBar;
	private Chat mCurChat = null;
	private ImageView mAttachBtn = null;
	private Button mSendBtn = null;
	private TextView mBackBtn = null;
	private TextView mTitleView = null;
	private PullListView mListView = null;
	private LinearLayout mMenuView = null;
	private TextView mPicBtn = null, mCameraBtn = null, mFileBtn = null, mPosBtn = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.formclient);

		registerBroadcast();

		// 获取Intent传过来的用户名
		mCurUserId = XmppTool.onGetUserId();
		mUserChatId = getIntent().getStringExtra(IMCommDefine.intent_userId);

		ImUtil.onSetCurChatUserId(mUserChatId);

		initUI();
		initListener();

		onGetLastMessage();
	}

	private void initUI() {
		mListView = (PullListView) findViewById(R.id.formclient_listview);
		mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		mListView.setPullRefreshEnable(true);
		mListView.setPullLoadEnable(false);
		mListView.setXListViewListener(this);

		mAdapter = new MessageShowAdapter(this, listMsg);
		mListView.setAdapter(mAdapter);

		mInputView = (EditText) findViewById(R.id.formclient_text);
		mProgressBar = (ProgressBar) findViewById(R.id.formclient_pb);
		mBackBtn = (TextView) findViewById(R.id.back_btn_id);
		mTitleView = (TextView) findViewById(R.id.title_view_id);

		mMenuView = (LinearLayout) findViewById(R.id.menu_view_id);
		mPicBtn = (TextView) findViewById(R.id.pic_btn_id);
		mCameraBtn = (TextView) findViewById(R.id.camera_btn_id);
		mFileBtn = (TextView) findViewById(R.id.file_btn_id);
		mPosBtn = (TextView) findViewById(R.id.pos_btn_id);

		// 消息监听
		ChatManager cm = XmppTool.getConnection().getChatManager();
		// 发送消息给yinghan-pc服务器的小王（获取自己的服务器，和好友）
		mCurChat = cm.createChat(mUserChatId, null);
		mAttachBtn = (ImageView) findViewById(R.id.formclient_btattach);
		mSendBtn = (Button) findViewById(R.id.formclient_btsend);
		// 接受文件
		FileTransferManager fileTransferManager = new FileTransferManager(XmppTool.getConnection());
		fileTransferManager.addFileTransferListener(new RecFileTransferListener());

		UserModel chatUserModel = ImUtil.onGetUserModel(mUserChatId);
		if (null != chatUserModel) {
			mTitleView.setText(chatUserModel.userName);
		}
	}

	private void initListener() {
		mAttachBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mMenuView.getVisibility() == View.GONE) {
					mInputView.setFocusable(false);
					mInputView.clearFocus();
					onHideSoftStatue();
					mMenuView.setVisibility(View.VISIBLE);
				} else {
					mInputView.requestFocus();
					mInputView.setFocusable(true);
					mMenuView.setVisibility(View.GONE);
					onChangeSoftStatue();
				}
			}
		});
		mSendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onSendMsg();
			}
		});
		mBackBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		mPicBtn.setOnClickListener(mClick);
		mCameraBtn.setOnClickListener(mClick);
		mFileBtn.setOnClickListener(mClick);
		mPosBtn.setOnClickListener(mClick);
	}

	/**
	 * 
	 * @描述:切换输入法的状态
	 * @参数
	 * @返回值 void
	 * @异常
	 */
	private void onChangeSoftStatue() {
		// 隐藏输入法
		InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		// 显示或者隐藏输入法
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private void onHideSoftStatue() {
		InputMethodManager imm = (InputMethodManager) mInputView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (null != imm && imm.isActive()) {
			imm.hideSoftInputFromWindow(mInputView.getApplicationWindowToken(), 0);
		}
	}

	private String mCameraPicPath = null;

	private View.OnClickListener mClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.pic_btn_id:
				BitmapDecodeUtil.onGallery(IMClientActivity.this);
				mMenuView.setVisibility(View.GONE);
				break;
			case R.id.camera_btn_id:
				mCameraPicPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp/camera.jpg";
				BitmapDecodeUtil.onCamera(IMClientActivity.this, mCameraPicPath);
				mMenuView.setVisibility(View.GONE);
				break;
			case R.id.file_btn_id:
				Intent intent = new Intent(IMClientActivity.this, FormFilesActivity.class);
				startActivityForResult(intent, 2);
				mMenuView.setVisibility(View.GONE);
				break;
			case R.id.pos_btn_id:
				mMenuView.setVisibility(View.GONE);
				break;
			}
		}
	};

	private void onSendMsg() {

		String msg = mInputView.getText().toString();

		if (msg.length() > 0) {
			if (XmppTool.getConnection().isConnected()) {
				MessageModel outMsg = new MessageModel();
				outMsg.userName = mCurUserId;
				outMsg.msgContent = msg;
				outMsg.fromFlag = 1;
				outMsg.fromUser = mUserChatId;
				outMsg.toUser = mCurUserId;
				outMsg.time = System.currentTimeMillis();
				listMsg.add(outMsg);
				mAdapter.notifyDataSetChanged();
				try {
					// 发送消息
					mCurChat.sendMessage(msg);
					ImApplication.onGetInstance().onGetDB().onAddMessage(outMsg);
				} catch (XMPPException e) {
					e.printStackTrace();
				}

			}
		} else {
			ToastUtil.onShowToast(getBaseContext(), getString(R.string.im_toast_send_empty_str));
		}
		// 清空text
		mInputView.setText("");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 发送附件
		if (requestCode == 2 && resultCode == 2 && data != null) {

			String filepath = data.getStringExtra("filepath");
			if (filepath.length() > 0) {
				sendFile(filepath);
			}
		} else if (requestCode == BitmapDecodeUtil.RESULT_GALLERY && resultCode == RESULT_OK) {
			// 图库
			Uri uri = data.getData();
			Cursor cursor = getContentResolver().query(uri, null, null, null, null);
			if (null == cursor)
				return;
			cursor.moveToFirst();
			String imgPath = cursor.getString(1); // 图片文件路径
			cursor.close();
			sendFile(imgPath);
		} else if (requestCode == BitmapDecodeUtil.RESULT_CAMERA && resultCode == RESULT_OK) {
			// 拍照
			sendFile(mCameraPicPath);
		}
	}

	private void sendFile(String filepath) {

		final FileTransferManager fileTransferManager = new FileTransferManager(XmppTool.getConnection());
		// 发送给yinghan-pc服务器，xiaowang（获取自己的服务器，和好友）
		final OutgoingFileTransfer fileTransfer = fileTransferManager.createOutgoingFileTransfer(mCurUserId + "/Spark 2.6.3");

		final File file = new File(filepath);
		if (!file.exists())
			return;

		try {
			fileTransfer.sendFile(file, "Sending");
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtil.onShowToast(getBaseContext(), getString(R.string.im_toast_send_fail_str));
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						Thread.sleep(500L);

						Status status = fileTransfer.getStatus();
						if ((status == FileTransfer.Status.error) || (status == FileTransfer.Status.complete) || (status == FileTransfer.Status.cancelled)
								|| (status == FileTransfer.Status.refused)) {
							handler.sendEmptyMessage(4);
							break;
						} else if (status == FileTransfer.Status.negotiating_transfer) {
							// ..
						} else if (status == FileTransfer.Status.negotiated) {
							// ..
						} else if (status == FileTransfer.Status.initial) {
							// ..
						} else if (status == FileTransfer.Status.negotiating_stream) {
							// ..
						} else if (status == FileTransfer.Status.in_progress) {
							handler.sendEmptyMessage(2);

							long p = fileTransfer.getBytesSent() * 100L / fileTransfer.getFileSize();

							android.os.Message message = handler.obtainMessage();
							message.arg1 = Math.round((float) p);
							message.what = 3;
							message.sendToTarget();
							ToastUtil.onShowThreadToast(getBaseContext(), getString(R.string.im_toast_send_success_str));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					ToastUtil.onShowThreadToast(getBaseContext(), getString(R.string.im_toast_send_fail_str));
				}
			}
		}).start();
	}

	private FileTransferRequest request;
	private File file;

	class RecFileTransferListener implements FileTransferListener {
		@Override
		public void fileTransferRequest(FileTransferRequest prequest) {
			// System.out.println("The file received from: " +
			// prequest.getRequestor());

			file = new File("mnt/sdcard/" + prequest.getFileName());
			request = prequest;
			handler.sendEmptyMessage(5);
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1:
				// 获取消息并显示
				MessageModel msgModel = (MessageModel) msg.obj;
				listMsg.add(msgModel);
				mAdapter.notifyDataSetChanged();
				break;
			case 2:
				// 附件进度条
				if (mProgressBar.getVisibility() == View.GONE) {
					mProgressBar.setMax(100);
					mProgressBar.setProgress(1);
					mProgressBar.setVisibility(View.VISIBLE);
				}
				break;
			case 3:
				mProgressBar.setProgress(msg.arg1);
				break;
			case 4:
				mProgressBar.setVisibility(View.GONE);
				break;
			case 5:
				onReciveFile();
				break;
			case 6:
				mAdapter.notifyDataSetChanged();
				break;
			case 7:
				mListView.stopRefresh();
				mAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};
	
	private void onReciveFile(){
		final IncomingFileTransfer infiletransfer = request.accept();

		AlertDialog.Builder builder = new AlertDialog.Builder(IMClientActivity.this);

		builder.setTitle("附件").setCancelable(false).setMessage("是否接收文件：" + file.getName() + "?")
				.setPositiveButton(getString(R.string.im_ok_string), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						try {
							infiletransfer.recieveFile(file);
						} catch (XMPPException e) {
							e.printStackTrace();
							ToastUtil.onShowToast(getBaseContext(), getString(R.string.im_toast_revice_fail_str));
						}

						handler.sendEmptyMessage(2);

						Timer timer = new Timer();
						TimerTask updateProgessBar = new TimerTask() {
							public void run() {
								if ((infiletransfer.getAmountWritten() >= request.getFileSize())
										|| (infiletransfer.getStatus() == FileTransfer.Status.error)
										|| (infiletransfer.getStatus() == FileTransfer.Status.refused)
										|| (infiletransfer.getStatus() == FileTransfer.Status.cancelled)
										|| (infiletransfer.getStatus() == FileTransfer.Status.complete)) {
									cancel();
									handler.sendEmptyMessage(4);
								} else {
									long p = infiletransfer.getAmountWritten() * 100L / infiletransfer.getFileSize();

									android.os.Message message = handler.obtainMessage();
									message.arg1 = Math.round((float) p);
									message.what = 3;
									message.sendToTarget();
									ToastUtil.onShowToast(getBaseContext(), getString(R.string.im_toast_revice_success_str));
								}
							}
						};
						timer.scheduleAtFixedRate(updateProgessBar, 10L, 10L);
						dialog.dismiss();
					}
				}).setNegativeButton(getString(R.string.im_cancel_string), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						request.reject();
						dialog.cancel();
					}
				}).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unRegisterBroadcast();
		ImUtil.onSetCurChatUserId(null);
	}

	private void registerBroadcast() {

		IntentFilter filter = new IntentFilter(IMCommDefine.broadcast_msg);
		registerReceiver(chatBroadcast, filter);
	}

	private void unRegisterBroadcast() {
		if (null != chatBroadcast)
			unregisterReceiver(chatBroadcast);
	}

	private BroadcastReceiver chatBroadcast = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (null != intent) {
				MessageModel msg = (MessageModel) intent.getSerializableExtra(IMCommDefine.intent_data);
				onDealWithMessage(msg);
			}
		}
	};

	private void onDealWithMessage(MessageModel msgData) {
		if (null == msgData)
			return;

		// 获取用户、消息、时间、IN
		// String[] args = new String[] { chatName, msgData.msgContent,
		// TimeRender.getDate(), "IN" };
		msgData.fromFlag = 0;
		android.os.Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.obj = msgData;
		msg.sendToTarget();
	}

	private boolean bNextPage = true;
	private final int PAGE_SIZE = 20;

	private void onGetLastMessage() {

		// new Thread() {
		// public void run() {
		// synchronized (IMClientActivity.class) {
		XmppDB db = ImApplication.onGetInstance().onGetDB();
		if (null == db)
			return;

		SearchModel key = new SearchModel();
		key.nPageIndex = 0;
		key.nPageSize = PAGE_SIZE;
		key.whereStr = UserMessage.COLUMN_FROM_USER + " = '" + mUserChatId + "'" + " and " + UserMessage.COLUMN_TO_USER + " = '" + mCurUserId + "'";

		List<MessageModel> temp = db.onGetMessageList(key);
		if (null != temp && !temp.isEmpty()) {
			listMsg.addAll(temp);
			if (temp.size() >= PAGE_SIZE)
				bNextPage = true;
			else
				bNextPage = false;
		}
		handler.sendEmptyMessage(6);
		// }
		// };
		// }.start();
	}

	private void onNextPage() {

		// new Thread() {
		// public void run() {
		//
		// synchronized (IMClientActivity.class) {
		XmppDB db = ImApplication.onGetInstance().onGetDB();
		if (null == db)
			return;

		List<MessageModel> temp = db.onGetNextPageMessage();
		if (null != temp && !temp.isEmpty()) {
			listMsg.addAll(0, temp);
			if (temp.size() >= PAGE_SIZE)
				bNextPage = true;
			else
				bNextPage = false;
		}

		handler.sendEmptyMessage(7);
		// }
		//
		// };
		// }.start();
	}

	@Override
	public void onRefresh() {
		if (!bNextPage) {
			mListView.stopRefresh();
			return;
		}

		onNextPage();
	}

	@Override
	public void onLoadMore() {

	}
}